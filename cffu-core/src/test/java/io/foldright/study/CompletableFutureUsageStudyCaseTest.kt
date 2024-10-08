package io.foldright.study

import io.foldright.test_utils.*
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
import kotlinx.coroutines.future.await
import java.lang.System.currentTimeMillis
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Study CompletableFuture usage.
 */
class CompletableFutureUsageStudyCaseTest : FunSpec({

    test("execution thread/executor behavior: then*(non-Async) operations chained after COMPLETED CF(completed by immediate value), trigger by then* invocation and run in place SEQUENTIALLY").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()
        var seq = 0

        sequenceChecker.assertSeq("\nstart", seq++)

        // a new CompletableFuture with value that is already COMPLETED
        val f0 = CompletableFuture.completedFuture(n)
        sequenceChecker.assertSeq("create f0", seq++)

        val f1 = f0.thenApply {
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

        f3.await().shouldBeNull()
        sequenceChecker.assertSeq("after f3 get", seq++)
    }

    test("execution thread/executor behavior: then*(non-Async) operations chained after *Async COMPLETED CF(completed by get), trigger by then* invocation and run in place SEQUENTIALLY").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()
        var seq = 0

        sequenceChecker.assertSeq("\nstart", seq++)

        val f0 = CompletableFuture.supplyAsync({
            sequenceChecker.assertSeq("supplyAsync completed CF", seq++)

            currentThread() shouldNotBe mainThread
            assertRunningInExecutor(testExecutor)

            n
        }, testExecutor)
        // ensure f0 is already COMPLETED
        f0.await() shouldBe n // wait f0 COMPLETED
        sequenceChecker.assertSeq("after f0 get", seq++)

        val f1 = f0.thenApply {
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

        f3.await().shouldBeNull()
        sequenceChecker.assertSeq("after f3 get", seq++)
    }

    /**
     * - `Async` methods(e.g. `runAsync`/`thenRunAsync`, etc.) can set `executor`;
     *   if executor argument is absent, use default executor of [CompletableFuture.ASYNC_POOL] (normally is a [ForkJoinPool]).
     * - non-`Async` methods(`thenRun/thenApply`) use the thread of notification;
     *   if there is single previous [CompletableFuture], use the same thread of previous CF.
     *   CAUTION: restrict the concurrency!
     */
    test("execution thread/executor behavior: then*(non-Async) operations chained after *Async UNCOMPLETED CF, trigger by previous *Async CF complete and run in previous Async CF executor").config(
        invocations = 100
    ) {
        val mainThread = currentThread()
        lateinit var thenNonAsyncOpThread: Thread

        val f = Blocker().use { blocker: Blocker ->
            CompletableFuture
                .runAsync {
                    // make sure build CF chain is finished before this `runAsync`
                    // aka. this CF is not completed
                    blocker.block()

                    currentThread() shouldNotBe mainThread
                    assertNotRunningInExecutor(testExecutor)
                }
                .thenRunAsync({
                    thenNonAsyncOpThread = currentThread()

                    assertRunningInExecutor(testExecutor)
                }, testExecutor) // ! switch executor !
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
                    // - executor is NOT inherited after switch!
                    // - use the DEFAULT EXECUTOR of CompletableFuture, if no executor specified.
                    assertNotRunningInExecutor(testExecutor)
                }
        }
        f.join()
    }

    fun checkThreadSwitchBehaviorThenApplyAsync(executor: ExecutorService) {
        val f0 = CompletableFuture.supplyAsync({
            assertRunningInExecutor(executor)
            emptyList<String>()
        }, executor)

        val forks = Array(THREAD_COUNT_OF_POOL) { f0 }

        val times = 10
        repeat(times) {
            for ((index, f) in forks.withIndex()) {
                forks[index] = f.thenApplyAsync({
                    sleep()
                    addCurrentThreadName(it)
                }, executor)
            }
        }

        val threadNameList = forks.reduce { acc, f -> acc.thenCombine(f, ::merge) }.get()

        threadNameList.shouldHaveSize(THREAD_COUNT_OF_POOL * times)
        threadNameList.toSet().shouldHaveSize(THREAD_COUNT_OF_POOL)
    }

    test("execution thread/executor behavior: then*Async operations of CF, run in switched thread(re-submit task into Executor)") {
        checkThreadSwitchBehaviorThenApplyAsync(testExecutor)

        checkThreadSwitchBehaviorThenApplyAsync(testFjExecutor)
    }

    /**
     * this normal process CF will be skipped, if previous CF is exceptional.
     */
    test("exceptionally behavior: exceptionally").config(enabledIf = java9Plus, invocations = 100) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()

        val mark = AtomicBoolean()

        val exceptionalCf = CompletableFuture<String>().apply { completeExceptionally(rte) }
        sequenceChecker.assertSeq("create exceptionalCf", 0)

        val f1 = exceptionalCf
            .thenApply { // skip normal process since previous CF is exceptional
                mark.set(true)
                currentThread() shouldBe mainThread
                "error"
            }
            .exceptionally {
                sequenceChecker.assertSeq("exceptionally", 1)

                currentThread() shouldBe mainThread

                it.shouldBeTypeOf<CompletionException>()
                it.cause shouldBeSameInstanceAs rte
                "HERE"
            }
        sequenceChecker.assertSeq("after exceptionally", 2)

        f1.await().also {
            mark.get().shouldBeFalse()
            it shouldEndWith "HERE"
        }
        sequenceChecker.assertSeq("after get", 3)
    }

    test("exceptionally behavior: exceptionallyAsync").config(enabledIf = java12Plus, invocations = 100) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()

        val mark = AtomicBoolean()

        val exceptionalCf = CompletableFuture<String>().apply { completeExceptionally(rte) }
        sequenceChecker.assertSeq("create exceptionalCf", 0)

        @Suppress("Since15") // exceptionallyAsync api is since java 12
        val f1 = exceptionalCf
            .thenApply { // skip normal process since previous CF is exceptional
                mark.set(true)
                currentThread() shouldBe mainThread
                "error"
            }
            .exceptionallyAsync({
                sequenceChecker.assertSeq("create exceptionallyAsync", 1)

                currentThread() shouldNotBe mainThread
                assertRunningInExecutor(testExecutor)

                it.shouldBeTypeOf<CompletionException>()
                it.cause shouldBeSameInstanceAs rte
                "HERE"
            }, testExecutor)

        @Suppress("BlockingMethodInNonBlockingContext")
        f1.get().also {
            mark.get().shouldBeFalse()
            it shouldEndWith "HERE"
        }
        sequenceChecker.assertSeq("after get", 2)
    }

    test("trigger task by CF.complete").config(invocations = 100) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()

        // create a CF to be completed later
        val f0 = CompletableFuture<String>()
        sequenceChecker.assertSeq("create f0", 0)

        val f1 = f0.thenApply {
            sequenceChecker.assertSeq("thenApply", 2)
            currentThread() shouldBe mainThread

            n
        }
        sequenceChecker.assertSeq("after thenApply", 1)

        // `complete` invocation will trigger above task of `thenApply` f1
        //
        // since *non-async*,
        // run task of `thenApply` f1 in-place(in mainThread), aka. immediately run in the `complete` invocation without submit task to executor
        f0.complete("done")
        sequenceChecker.assertSeq("after complete", 3)

        f1.await() shouldBe n
        sequenceChecker.assertSeq("after get", 4)
    }

    test("trigger task by CF.completeAsync").config(enabledIf = java9Plus, invocations = 100) {
        val mainThread = currentThread()
        val sequenceChecker = SequenceChecker()

        // create a CF to be completed later
        val f0 = CompletableFuture<String>()
        sequenceChecker.assertSeq("create f0", 0)

        val f1 = f0.thenApply {
            sequenceChecker.assertSeq("thenApply", 3)

            currentThread() shouldNotBe mainThread

            n
        }
        sequenceChecker.assertSeq("after thenApply", 1)

        // `complete` invocation will trigger above `thenApply` f1
        //
        // since *non-async*,
        // run task in-place(in mainThread), aka. immediately run in the `complete` invocation without submit task to executor
        @Suppress("Since15") // completeAsync api is since java 9
        f0.completeAsync({
            sequenceChecker.assertSeq("in completeAsync", 2)

            assertRunningInExecutor(testExecutor)

            "done"
        }, testExecutor)

        f1.await() shouldBe n
        sequenceChecker.assertSeq("in completeAsync", 4)
    }

    test("timeout control: normally completed with replacement value").config(enabledIf = java9Plus) {
        val f = completeLaterCf(n)
            .completeOnTimeout(anotherN, 1, TimeUnit.MILLISECONDS)

        f.await() shouldBe anotherN
    }

    test("timeout control: exceptionally completed with java.util.concurrent.TimeoutException").config(enabledIf = java9Plus) {
        val f = completeLaterCf(n)
            .orTimeout(1, TimeUnit.MILLISECONDS)
            .exceptionally {
                it.shouldBeTypeOf<TimeoutException>()
                anotherN
            }

        f.await() shouldBe anotherN
    }

    test("delay execution").config(enabledIf = java9Plus) {
        val tick = currentTimeMillis()
        val delay = 100L

        val delayer = CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS, testExecutor)

        val duration = CompletableFuture.supplyAsync({
            currentTimeMillis() - tick
        }, delayer).await()

        // if run in CI environment, use large tolerance 50ms, because CI environment is more unstable.
        // if run in CI mac environment, use huge tolerance 200ms, because CI mac environment is more more unstable.
        val tolerance = if (isCiMacEnv()) 200 else if (isCiEnv()) 50 else 20
        duration.shouldBeBetween(delay, delay + tolerance)
    }

    xtest("performance CF then*").config(invocations = 10) {
        val times = 1_000_000

        var f = completeLaterCf({ currentTimeMillis() }, executor = testFjExecutor)

        val tick = currentTimeMillis()
        repeat(times) {
            f = f.thenApply { it }
        }

        val buildTime = currentTimeMillis() - tick

        val runTime = f.thenApply { currentTimeMillis() - it }.await()

        String.format(
            "%s tasks: build time: %5sms, run time: %4sms",
            times, buildTime, runTime
        ).run(::println)
    }

    test("ex").config(enabledIf = java9Plus) {
        val cf = CompletableFuture.failedFuture<Int>(rte)

        cf.minimalCompletionStage().handle { _, t ->
            t.shouldBeTypeOf<CompletionException>()
            t.cause shouldBeSameInstanceAs rte
            n
        }.toCompletableFuture().await() shouldBe n

        cf.thenApply { it }.handle { _, t ->
            t.shouldBeTypeOf<CompletionException>()
            t.cause shouldBeSameInstanceAs rte
            n
        }.toCompletableFuture().await() shouldBe n
    }
})
