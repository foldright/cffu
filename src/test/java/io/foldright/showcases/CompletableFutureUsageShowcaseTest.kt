package io.foldright.showcases

import io.foldright.cffu.*
import io.foldright.testutils.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.longs.shouldBeBetween
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

class CompletableFutureUsageShowcaseTest : FunSpec({
    val n = 42
    val anotherN = 424242

    test("execution thread/executor behavior: then*(non-Async) operations/methods of completed CF with immediate value, run in place SEQUENTIALLY").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()
        var seq = 0

        sequenceChecker.assertSeq("\nstart", seq++)

        // a new CompletableFuture with value that is already COMPLETED
        val f0 = CompletableFuture.completedFuture(n)

        val f1 = f0.thenApply {
            sequenceChecker.assertSeq("thenApply", seq++)
            currentThread() shouldBe mainThread
            it * 2
        }
        sequenceChecker.assertSeq("after thenApply", seq++)

        val f2 = f1.thenAccept {
            sleep()
            sequenceChecker.assertSeq("thenAccept", seq++)
            currentThread() shouldBe mainThread
        }
        sequenceChecker.assertSeq("after thenAccept", seq++)

        val f3 = f2.thenRun {
            sequenceChecker.assertSeq("thenRun", seq++)
            currentThread() shouldBe mainThread
        }
        sequenceChecker.assertSeq("after thenRun", seq++)

        @Suppress("BlockingMethodInNonBlockingContext")
        f3.get().shouldBeNull()
        sequenceChecker.assertSeq("after get", seq++)
    }

    test("execution thread/executor behavior: then*(non-Async) operations/methods of completed CF by get, run in place SEQUENTIALLY").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()
        var seq = 0

        sequenceChecker.assertSeq("\nstart", seq++)

        val f0 = CompletableFuture.supplyAsync({
            sequenceChecker.assertSeq("supplyAsync completed CF", seq++)

            n
        }, testThreadPoolExecutor)
        // ensure f0 is already COMPLETED
        @Suppress("BlockingMethodInNonBlockingContext")
        f0.get() shouldBe n // wait f0 COMPLETED
        sequenceChecker.assertSeq("after f0 get", seq++)

        val f1 = f0.thenApply {
            sleep()
            sequenceChecker.assertSeq("thenApply", seq++)
            currentThread() shouldBe mainThread
            it * 2
        }
        sequenceChecker.assertSeq("after thenApply", seq++)

        val f2 = f1.thenAccept {
            sequenceChecker.assertSeq("thenAccept", seq++)
            currentThread() shouldBe mainThread
        }
        sequenceChecker.assertSeq("after thenAccept", seq++)

        val f3 = f2.thenRun {
            sequenceChecker.assertSeq("thenRun", seq++)
            currentThread() shouldBe mainThread
        }
        sequenceChecker.assertSeq("after thenRun", seq++)

        @Suppress("BlockingMethodInNonBlockingContext")
        f3.get().shouldBeNull()
        sequenceChecker.assertSeq("after f3 get", seq++)
    }

    /**
     * - `Async` methods(e.g. `runAsync`/`thenRunAsync`, etc.) can set `executor`;
     *   if executor argument is absent, use default executor of [CompletableFuture.ASYNC_POOL] (normally is a [ForkJoinPool]).
     * - non-`Async` methods(`thenRun/thenApply`) use the thread of notification;
     *   if there is single previous [CompletableFuture], use the same thread of previous CF.
     *   CAUTION: restrict the concurrency!!
     */
    test("execution thread/executor behavior: then*(non-Async) operations/methods of completed CF, run in previous CF executor").config(
        invocations = 100
    ) {
        val mainThread = currentThread()

        lateinit var thenNonAsyncOpThread: Thread
        val buildFinish = CountDownLatch(1)

        val f = CompletableFuture
            .runAsync {
                buildFinish.await() // make sure build CF chain is finished before run

                currentThread() shouldNotBe mainThread
                assertRunNotInTestThreadPoolExecutor()
            }
            .thenRunAsync({
                thenNonAsyncOpThread = currentThread()

                assertRunInTestThreadPoolExecutor()
            }, testThreadPoolExecutor) // !! switch executor !!
            .thenApply {
                // when NOT async,
                // use same thread of single previous CF
                currentThread() shouldBe thenNonAsyncOpThread

                "apply"
            }
            .thenAccept {
                // when NOT async,
                // use same thread of single previous CF
                currentThread() shouldBe thenNonAsyncOpThread
            }
            .thenRun {
                // when NOT async,
                // use same thread of single previous CF
                currentThread() shouldBe thenNonAsyncOpThread
            }
            .thenRunAsync {
                // when run ASYNC,
                //
                // - executor is NOT inherited after switch!!
                // - use the DEFAULT EXECUTOR of CompletableFuture, if no executor specified.
                assertRunNotInExecutor(testThreadPoolExecutor)
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
                assertRunInTestThreadPoolExecutor()

                it.shouldBeTypeOf<CompletionException>()
                it.cause shouldBeSameInstanceAs rte
                "HERE"
            }, testThreadPoolExecutor)

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
                    sleep()
                    addCurrentThreadName(it)
                }, executor)
            }
        }

        val threadNameList = forks.reduce { acc, f -> acc.thenCombine(f, ::merge) }.get()

        threadNameList.shouldHaveSize(forkCount * times)
        threadNameList.toSet().also { println("\n$it") }.shouldHaveSize(THREAD_COUNT_OF_POOL)
    }

    test("thread switch behavior of CF.thenApplyAsync") {
        checkThreadSwitchBehaviorThenApplyAsync(testThreadPoolExecutor)

        checkThreadSwitchBehaviorThenApplyAsync(testForkJoinPoolExecutor)
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
            assertRunInTestThreadPoolExecutor()
            "done"
        }, testThreadPoolExecutor)

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get() shouldBe n
    }

    test("timeout control: replacement value") {
        val f = CompletableFuture.supplyAsync {
            sleep(10)
            n
        }.completeOnTimeout(anotherN, 1, TimeUnit.MILLISECONDS)

        @Suppress("BlockingMethodInNonBlockingContext")
        f.get() shouldBe anotherN
    }

    test("timeout control: exceptionally completed with java.util.concurrent.TimeoutException") {
        val f = CompletableFuture
            .supplyAsync {
                sleep(10)
                n
            }
            .orTimeout(1, TimeUnit.MILLISECONDS)
            .exceptionally {
                it.shouldBeTypeOf<TimeoutException>()
                anotherN
            }

        @Suppress("BlockingMethodInNonBlockingContext")
        f.get() shouldBe anotherN
    }

    test("delay execution") {
        val tick = currentTimeMillis()
        val delay = 5L

        val delayer = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS)

        @Suppress("BlockingMethodInNonBlockingContext")
        val duration = CompletableFuture.supplyAsync({
            currentTimeMillis() - tick
        }, delayer).get()

        duration.shouldBeBetween(delay, delay + 2)
    }

    xtest("performance CF then*").config(invocations = 10) {
        val times = 1_000_000

        var f = CompletableFuture.supplyAsync(
            { sleep(100); currentTimeMillis() },
            testThreadPoolExecutor
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
