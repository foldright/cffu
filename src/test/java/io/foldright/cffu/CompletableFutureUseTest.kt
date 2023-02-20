package io.foldright.cffu

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import java.lang.System.currentTimeMillis
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

class CompletableFutureUseTest : FunSpec({
    ////////////////////////////////////////////////////////////////////////////////
    // test constants
    ////////////////////////////////////////////////////////////////////////////////
    val n = 42

    ////////////////////////////////////////////////////////////////////////////////
    // test logic
    ////////////////////////////////////////////////////////////////////////////////

    test("execution thread/executor behavior: then*(non-Async) operations/methods of completed CF(Immediate value) run in place").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()

        sequenceChecker.assertSeq("\nstart", 0)

        val f1 = CompletableFuture.completedFuture(n) // a new CompletableFuture that is already COMPLETED
            .thenApply {
                sequenceChecker.assertSeq("thenApply", 1)
                currentThread() shouldBe mainThread
                it * 2
            }
            .thenAccept {
                sleep(1)
                sequenceChecker.assertSeq("thenAccept", 2)
                currentThread() shouldBe mainThread
            }
            .thenRun {
                sequenceChecker.assertSeq("thenRun", 3)
                currentThread() shouldBe mainThread
            }

        sequenceChecker.assertSeq("beforeGet", 4)

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get().shouldBeNull()
        sequenceChecker.assertSeq("afterGet", 5)
    }

    test("execution thread/executor behavior: then*(non-Async) operations/methods of completed CF run in place").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()

        sequenceChecker.assertSeq("\nstart", 0)

        val f1 = CompletableFuture.supplyAsync({
            sequenceChecker.assertSeq("supplyAsync completed CF", 1)

            n
        }, threadPoolExecutor)
        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get() shouldBe n // wait f1 COMPLETED
        sequenceChecker.assertSeq("got", 2)

        val f2 = f1.thenApply {
            sleep(1)
            sequenceChecker.assertSeq("thenApply", 3)
            currentThread() shouldBe mainThread
            it * 2
        }.thenAccept {
            sequenceChecker.assertSeq("thenAccept", 4)
            currentThread() shouldBe mainThread
        }.thenRun {
            sequenceChecker.assertSeq("thenRun", 5)
            currentThread() shouldBe mainThread
        }

        sequenceChecker.assertSeq("beforeGet", 6)

        @Suppress("BlockingMethodInNonBlockingContext")
        f2.get().shouldBeNull()
        sequenceChecker.assertSeq("afterGet", 7)
    }

    /**
     * - `Async` methods(e.g. `runAsync`/`thenRunAsync`, etc.) can set `executor`;
     *   if executor argument is absent, use default executor of [CompletableFuture.ASYNC_POOL] (normally is a [ForkJoinPool]).
     * - non-`Async` methods(`thenRun/thenApply`) use the thread of notification;
     *   if there is single previous [CompletableFuture], use the same thread of previous CF.
     *   CAUTION: restrict the concurrency!!
     */
    test("execution thread/executor behavior: run in uncompleted previous CF executor").config(invocations = 100) {
        val mainThread = currentThread()
        lateinit var thenAsyncThread: Thread
        val buildFinish = CountDownLatch(1)

        val f = CompletableFuture
            .runAsync {
                buildFinish.await() // make sure build CF chain is finished before run

                currentThread() shouldNotBe mainThread
                assertRunNotInExecutor(threadPoolExecutor)
            }
            .thenRunAsync({
                thenAsyncThread = currentThread()

                assertRunInExecutor(threadPoolExecutor)
            }, threadPoolExecutor) // !! switch executor !!
            .thenApply {
                // when NOT async,
                // use same thread of single previous CF
                currentThread() shouldBe thenAsyncThread

                "apply"
            }
            .thenAccept {
                // when NOT async,
                // use same thread of single previous CF
                currentThread() shouldBe thenAsyncThread
            }
            .thenRun {
                // when NOT async,
                // use same thread of single previous CF
                currentThread() shouldBe thenAsyncThread
            }
            .thenRunAsync {
                // when run ASYNC,
                //
                // - executor is NOT inherited after switch!!
                // - use the DEFAULT EXECUTOR of CompletableFuture, if no executor specified.
                assertRunNotInExecutor(threadPoolExecutor)
            }

        buildFinish.countDown()
        f.join()
    }

    /**
     * this normal process CF will be skipped, if previous CF is exceptional.
     */
    test("exceptionally behavior: exceptionally").config(enabledIf = java9Plus, invocations = 100) {
        val mainThread = currentThread()

        val mark = AtomicBoolean()
        val rte = RuntimeException("Bang")

        val exceptionalCf = CompletableFuture<String>().apply { completeExceptionally(rte) }

        val f1 = exceptionalCf
            .thenApply { // skip normal process since previous CF is exceptional
                mark.set(true)
                currentThread() shouldBe mainThread
                "error"
            }
            .exceptionally {
                currentThread() shouldBe mainThread

                it.shouldBeTypeOf<CompletionException>()
                it.cause shouldBeSameInstanceAs rte
                "HERE"
            }

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get().also {
            mark.get().shouldBeFalse()
            it shouldEndWith "HERE"
        }
    }

    test("exceptionally behavior: exceptionallyAsync").config(enabledIf = java12Plus, invocations = 100) {
        val mainThread = currentThread()

        val mark = AtomicBoolean()
        val rte = RuntimeException("Bang")

        val exceptionalCf = CompletableFuture<String>().apply { completeExceptionally(rte) }

        @Suppress("Since15") // exceptionallyAsync api is since java 12
        val f1 = exceptionalCf
            .thenApply { // skip normal process since previous CF is exceptional
                mark.set(true)
                currentThread() shouldBe mainThread
                "error"
            }
            .exceptionallyAsync({
                currentThread() shouldNotBe mainThread
                assertRunInExecutor(threadPoolExecutor)

                it.shouldBeTypeOf<CompletionException>()
                it.cause shouldBeSameInstanceAs rte
                "HERE"
            }, threadPoolExecutor)

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get().also {
            mark.get().shouldBeFalse()
            it shouldEndWith "HERE"
        }
    }

    fun checkThreadSwitchBehaviorThenApplyAsync(executor: ExecutorService) {
        val f0 = CompletableFuture.supplyAsync({
            assertRunInExecutor(executor)
            emptyList<String>()
        }, executor)

        val forkCount = 10
        val forks = Array(forkCount) { f0 }

        val times = 100
        repeat(times) {
            for ((index, f) in forks.withIndex()) {
                forks[index] = f.thenApplyAsync({
                    sleep(2)
                    addCurrentThreadName(it)
                }, executor)
            }
        }

        val threadNameList = forks.reduce { acc, f -> acc.thenCombine(f, ::merge) }.get()

        threadNameList.shouldHaveSize(forkCount * times)
        threadNameList.toSet().also { println("\n$it") }.shouldHaveSize(THREAD_COUNT_OF_POOL)
    }

    test("thread switch behavior of CF.thenApplyAsync") {
        checkThreadSwitchBehaviorThenApplyAsync(threadPoolExecutor)

        checkThreadSwitchBehaviorThenApplyAsync(forkJoinPoolExecutor)
    }

    test("trigger computation by CF.complete").config(invocations = 100) {
        val mainThread = currentThread()

        // create a CF to be completed later
        val f0 = CompletableFuture<String>()

        val f1 = f0.thenApply {
            currentThread() shouldBe mainThread

            n
        }
        // `complete` will trigger above `thenApply` computation
        // since *non-async* run computation
        // in-place, aka. immediately after previous CF without submit task to executor
        f0.complete("done")

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get() shouldBe n
    }

    test("trigger computation by CF.completeAsync").config(enabledIf = java9Plus, invocations = 100) {
        val mainThread = currentThread()

        // create a CF to be completed later
        val f0 = CompletableFuture<String>()

        val f1 = f0.thenApply {
            currentThread() shouldNotBe mainThread

            n
        }
        // `complete` will trigger above `thenApply` computation
        // since *non-async* run computation in-place(aka. mainThread)
        @Suppress("Since15") // completeAsync api is since java 9
        f0.completeAsync({
            assertRunInExecutor(threadPoolExecutor)
            "done"
        }, threadPoolExecutor)

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get() shouldBe n
    }

    xtest("performance CF then*").config(invocations = 10) {
        val times = 1_000_000

        var f = CompletableFuture.supplyAsync(
            { sleep(100); currentTimeMillis() },
            threadPoolExecutor
        )

        val tick = currentTimeMillis()
        repeat(times) {
            f = f.thenApply { it }
        }

        val buildTime = currentTimeMillis() - tick

        @Suppress("BlockingMethodInNonBlockingContext")
        val runTime = f.thenApply { currentTimeMillis() - it }.get()

        String.format(
            "%s tasks: build time: %5sms, run time: %4sms",
            times, buildTime, runTime
        ).run(::println)
    }
})
