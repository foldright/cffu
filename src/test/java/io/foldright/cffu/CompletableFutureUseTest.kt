package io.foldright.cffu

import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong

class CompletableFutureUseTest : FunSpec({

    lateinit var threadPoolExecutor: ExecutorService
    lateinit var forkJoinPoolExecutor: ExecutorService

    val threadNamePrefixOfThreadPoolExecutor = "CompletableFutureUseTest_ThreadPool_${Random.nextULong()}-"
    val threadNamePrefixOfForkJoinPoolExecutor = "CompletableFutureUseTest_ForkJoinPool_${Random.nextULong()}-"

    val threadCountOfPool = 10

    val assertInExecutor: (ExecutorService) -> Unit = {
        when (it) {
            threadPoolExecutor -> Thread.currentThread().name shouldStartWith threadNamePrefixOfThreadPoolExecutor
            forkJoinPoolExecutor -> Thread.currentThread().name shouldStartWith threadNamePrefixOfForkJoinPoolExecutor
            else -> fail("should be happened")
        }
    }

    val assertNotInExecutor: (ExecutorService) -> Unit = {
        when (it) {
            threadPoolExecutor -> Thread.currentThread().name shouldNotStartWith threadNamePrefixOfThreadPoolExecutor
            forkJoinPoolExecutor -> Thread.currentThread().name shouldNotStartWith threadNamePrefixOfForkJoinPoolExecutor
            else -> fail("should be happened")
        }
    }

    val addCurrentThreadName: (List<String>) -> List<String> = { it + Thread.currentThread().name }

    //val randSleep: () -> Unit = { sleep(Random.nextLong(1, 5)) }
    val sleep: () -> Unit = { sleep(3) }

    ////////////////////////////////////////////////////////////////////////////////
    // test logic
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * - `Async` methods(e.g. `runAsync`/`thenRunAsync`, etc.) can set `executor`;
     * if executor argument is absent use default executor of [CompletableFuture.ASYNC_POOL] (normally is a [ForkJoinPool]).
     * - non-`Async` methods(`thenRun/thenApply`) use the thread of notification;
     *   if there is single previous [CompletableFuture], use the same thread of previous CF.
     *   CAUTION: restrict the concurrency!!
     */
    test("executor_thread_inheritance_behavior").config(invocations = 100) {
        val testThread = Thread.currentThread()
        lateinit var thenNonAsyncThread: Thread

        CompletableFuture
            .runAsync {
                Thread.currentThread() shouldNotBe testThread

                assertNotInExecutor(threadPoolExecutor)
            }
            .thenRunAsync({
                thenNonAsyncThread = Thread.currentThread()

                assertInExecutor(threadPoolExecutor)
            }, threadPoolExecutor) // !! switched executor !!
            .thenApply {
                // when NOT async,
                // use same thread of single previous CF
                Thread.currentThread() shouldBe thenNonAsyncThread

                "apply"
            }
            .thenAccept {
                // when NOT async,
                // use same thread of single previous CF
                Thread.currentThread() shouldBe thenNonAsyncThread
            }
            .thenRun {
                // when NOT async,
                // use same thread of single previous CF
                Thread.currentThread() shouldBe thenNonAsyncThread
            }
            .thenRunAsync {
                // when run ASYNC,
                //
                // - executor is NOT inherited after switch!!
                // - use the DEFAULT EXECUTOR of CompletableFuture, if no executor specified.
                assertNotInExecutor(threadPoolExecutor)
            }
            .join()
    }

    /**
     * this normal process CF will be skipped, if previous CF is exceptional.
     */
    @Suppress("BlockingMethodInNonBlockingContext")
    test("exceptionally_behavior") {
        val mark = AtomicBoolean()

        CompletableFuture<String>()
            .apply { completeExceptionally(RuntimeException("Bang")) }
            .thenApply { // skip normal process since previous CF is exceptional
                mark.set(true)
                "error"
            }
            .exceptionally { it.message!! + "!!" }
            .get()
            .also {
                mark.get().shouldBeFalse()
                it shouldEndWith "Bang!!"
            }
    }

    fun checkThreadSwitchBehaviorThenApplyAsync(executor: ExecutorService) {
        val f0 = CompletableFuture.supplyAsync(
            { assertInExecutor(executor) } andThen { -> emptyList<String>() },
            executor,
        )

        val forkCount = 10
        val forks = Array(forkCount) { f0 }

        val times = 100
        repeat(times) {
            for ((index, f) in forks.withIndex()) {
                forks[index] = f.thenApplyAsync(sleep andThen addCurrentThreadName, executor)
            }
        }

        val threadNameList = forks.reduce { acc, f -> acc.thenCombine(f, ::merge) }.get()

        threadNameList.shouldHaveSize(forkCount * times)
        threadNameList.toSet().also { println("\n$it") }.shouldHaveSize(threadCountOfPool)
    }

    /**
     *
     */
    test("thread_switch_behavior_thenApplyAsync") {
        checkThreadSwitchBehaviorThenApplyAsync(threadPoolExecutor)

        checkThreadSwitchBehaviorThenApplyAsync(forkJoinPoolExecutor)
    }


    ////////////////////////////////////////////////////////////////////////////////
    // before/after Spec
    ////////////////////////////////////////////////////////////////////////////////

    beforeSpec {
        threadPoolExecutor = run {
            val counter = AtomicLong()
            ThreadPoolExecutor(
                threadCountOfPool, threadCountOfPool, 1, TimeUnit.DAYS,
                ArrayBlockingQueue(threadCountOfPool * 2)
            ) { r ->
                Thread(r).apply {
                    name = "$threadNamePrefixOfThreadPoolExecutor${counter.getAndIncrement()}"
                    isDaemon = true
                }
            }
        }
        forkJoinPoolExecutor = run {
            val counter = AtomicLong()
            ForkJoinPool(
                threadCountOfPool, {
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(it).apply {
                        name = "$threadNamePrefixOfForkJoinPoolExecutor${counter.getAndIncrement()}"
                    }
                },
                null, false
            )
        }

        // warmup
        val fs1: List<Future<*>> = (0 until threadCountOfPool * 2).map {
            threadPoolExecutor.submit { sleep(2) }
        }
        val fs2 = (0 until threadCountOfPool * 2).map {
            forkJoinPoolExecutor.submit { sleep(2) }
        }
        fs1.map { it.get() }
        fs2.map { it.get() }
    }

    afterSpec {
        threadPoolExecutor.shutdown()
        forkJoinPoolExecutor.shutdown()

        @Suppress("BlockingMethodInNonBlockingContext")
        threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS) shouldBe true
        @Suppress("BlockingMethodInNonBlockingContext")
        forkJoinPoolExecutor.awaitTermination(1, TimeUnit.SECONDS) shouldBe true
    }
})
