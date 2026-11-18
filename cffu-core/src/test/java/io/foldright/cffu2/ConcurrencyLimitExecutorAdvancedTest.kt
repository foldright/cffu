package io.foldright.cffu2

import io.foldright.test_utils.ConcurrencyChecker
import io.foldright.test_utils.logWithTimeAndThread
import io.foldright.test_utils.rangeList
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import kotlin.random.Random

private const val THREAD_COUNT = 8
private val executor: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

/**
 * Advanced tests for ConcurrencyLimitExecutor covering missing test scenarios
 * identified in the test analysis document.
 */
class ConcurrencyLimitExecutorAdvancedTest : FunSpec({

    // ========================================
    // Critical Priority Tests
    // ========================================

    test("RejectedExecutionException when all concurrency slots occupied by synchronous runners") {
        val maxConcurrency = 3
        val blockingLatch = CountDownLatch(1)
        val rejectedLatch = CountDownLatch(1)

        // Create a custom executor that runs tasks synchronously (like DirectExecutor)
        val syncExecutor = Executor { r -> r.run() }
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, syncExecutor)

        // Submit maxConcurrency tasks that will block, occupying all slots with synchronous runners
        val submissionThreads = List(maxConcurrency) { index ->
            Thread {
                concurrencyLimitExecutor.execute {
                    logWithTimeAndThread("blocking task %d waiting", index)
                    blockingLatch.await()
                    logWithTimeAndThread("blocking task %d done", index)
                }
            }
        }

        // Start all blocking tasks
        submissionThreads.forEach { it.start() }
        sleep(100) // Give them time to start and block

        // Now try to submit a new task - should be rejected
        val rejectionThread = Thread {
            try {
                val exception = shouldThrowExactly<RejectedExecutionException> {
                    concurrencyLimitExecutor.execute {
                        logWithTimeAndThread("this task should never run")
                    }
                }
                // Verify exception message content
                exception.message shouldContain "Task rejected"
                exception.message shouldContain "all $maxConcurrency concurrency slot(s) are occupied by synchronous runner(s)"
                exception.message shouldContain "CallerRunsPolicy/DirectExecutor"
                logWithTimeAndThread("correctly rejected with: ${exception.message}")
            } finally {
                rejectedLatch.countDown()
            }
        }

        rejectionThread.start()
        rejectedLatch.await(5, TimeUnit.SECONDS) shouldBe true

        // Cleanup: unblock the blocking tasks
        blockingLatch.countDown()
        submissionThreads.forEach { it.join(1000) }
    }

    test("multi-threaded concurrent submission stress test") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val submitterThreadCount = 10
        val tasksPerThread = 100
        val totalTasks = submitterThreadCount * tasksPerThread
        val completedTasks = CopyOnWriteArrayList<Int>()
        val latch = CountDownLatch(totalTasks)

        // Launch multiple threads that concurrently submit tasks
        val submitterThreads = List(submitterThreadCount) { threadIndex ->
            Thread {
                repeat(tasksPerThread) { taskIndex ->
                    val taskId = threadIndex * tasksPerThread + taskIndex
                    concurrencyLimitExecutor.execute {
                        concurrencyChecker.enter()
                        try {
                            completedTasks.add(taskId)
                            val millis = Random.nextLong(10, 50)
                            sleep(millis)
                        } finally {
                            concurrencyChecker.leave()
                            latch.countDown()
                        }
                    }
                    // Small random delay between submissions
                    if (Random.nextInt(100) < 10) sleep(Random.nextLong(1, 5))
                }
            }
        }

        // Start all submitter threads
        submitterThreads.forEach { it.start() }
        submitterThreads.forEach { it.join() }

        // Wait for all tasks to complete
        latch.await(30, TimeUnit.SECONDS) shouldBe true

        // Verify all tasks executed exactly once
        concurrencyChecker.check()
        completedTasks shouldHaveSize totalTasks
        completedTasks shouldContainExactlyInAnyOrder rangeList(totalTasks)
    }

    test("executor.execute() throws exception - exception propagation") {
        val maxConcurrency = 3
        val attemptCount = AtomicInteger(0)

        // Create a mock executor that always rejects
        val alwaysRejectExecutor = Executor { r ->
            attemptCount.incrementAndGet()
            throw RejectedExecutionException("Always reject")
        }

        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, alwaysRejectExecutor)
        val rejectedCount = AtomicInteger(0)

        // Submit tasks - all should be rejected and exception should propagate
        repeat(5) { index ->
            val exception = shouldThrowExactly<RejectedExecutionException> {
                concurrencyLimitExecutor.execute {
                    logWithTimeAndThread("task $index - should never run")
                }
            }
            rejectedCount.incrementAndGet()
            exception.message shouldBe "Always reject"
            logWithTimeAndThread("task $index rejected as expected")
        }

        // All 5 submissions should be rejected, and each one reached the base executor.
        // The same instance accepting a later task is covered by
        // "base executor rejects once - the rejected task never runs".
        rejectedCount.get() shouldBeExactly 5
        attemptCount.get() shouldBeExactly 5
    }

    test("base executor rejects once - the rejected task never runs") {
        val ran = CopyOnWriteArrayList<String>()
        val rejectNext = AtomicBoolean(true)
        val base = Executor { r ->
            if (rejectNext.getAndSet(false)) throw RejectedExecutionException("reject once")
            else executor.execute(r)
        }
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1, base)

        shouldThrowExactly<RejectedExecutionException> {
            concurrencyLimitExecutor.execute { ran.add("rejected") }
        }

        val latch = CountDownLatch(1)
        concurrencyLimitExecutor.execute {
            ran.add("accepted")
            latch.countDown()
        }
        latch.await(2, TimeUnit.SECONDS) shouldBe true

        ran shouldBe listOf("accepted")
    }

    test("synchronous task throws after re-queuing itself - queued task still runs") {
        val basePool = ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, SynchronousQueue(), ThreadPoolExecutor.CallerRunsPolicy()
        )
        try {
            val concurrencyLimitExecutor = ConcurrencyLimitExecutor(2, basePool)
            val workerStarted = CountDownLatch(1)
            val unblockWorker = CountDownLatch(1)

            // Occupy the single pool thread with an async worker (workerCount = 1, syncRunnerCount = 0)
            concurrencyLimitExecutor.execute {
                workerStarted.countDown()
                unblockWorker.await()
            }
            workerStarted.await(2, TimeUnit.SECONDS) shouldBe true

            val runCount = AtomicInteger(0)
            val drained = CountDownLatch(1)
            val rte = RuntimeException("sync boom")
            lateinit var selfReschedulingTask: Runnable
            selfReschedulingTask = Runnable {
                if (runCount.incrementAndGet() == 1) {
                    // Runs synchronously via CallerRunsPolicy (workerCount = 2, syncRunnerCount = 1);
                    // both submissions below hit `workerCount >= maxConcurrency` and queue in FIFO order.
                    concurrencyLimitExecutor.execute(selfReschedulingTask)
                    concurrencyLimitExecutor.execute { drained.countDown() }
                    throw rte
                }
            }

            shouldThrowExactly<RuntimeException> {
                concurrencyLimitExecutor.execute(selfReschedulingTask)
            } shouldBeSameInstanceAs rte

            unblockWorker.countDown()
            drained.await(2, TimeUnit.SECONDS) shouldBe true
            runCount.get() shouldBeExactly 2
        } finally {
            basePool.shutdownNow()
        }
    }

    test("base executor rejects while active worker has equal task queued - drops rejected tail task only") {
        class KeyedTask(val key: Int, val tag: String, val onRun: (String) -> Unit) : Runnable {
            override fun run() = onRun(tag)
            override fun equals(other: Any?): Boolean = other is KeyedTask && key == other.key
            override fun hashCode(): Int = key
        }

        val basePool = ThreadPoolExecutor(
            1, 1, 0, TimeUnit.SECONDS, ArrayBlockingQueue(1), ThreadPoolExecutor.AbortPolicy()
        )
        try {
            val concurrencyLimitExecutor = ConcurrencyLimitExecutor(3, basePool)
            val workerStarted = CountDownLatch(1)
            val unblockWorker = CountDownLatch(1)

            // 1st submission: starts worker #1 on basePool's single thread (workerCount = 1)
            concurrencyLimitExecutor.execute {
                workerStarted.countDown()
                unblockWorker.await()
            }
            workerStarted.await(2, TimeUnit.SECONDS) shouldBe true

            val ranTags = CopyOnWriteArrayList<String>()
            val acceptedTask = KeyedTask(1, "accepted") { ranTags.add(it) }
            val rejectedTask = KeyedTask(1, "rejected") { ranTags.add(it) }

            // 2nd submission: queues `acceptedTask` in CLE and parks worker #2 in basePool's queue(1) (workerCount = 2)
            concurrencyLimitExecutor.execute(acceptedTask)

            // 3rd submission: queues `rejectedTask` at CLE tail, then basePool's AbortPolicy rejects worker #3
            shouldThrowExactly<RejectedExecutionException> {
                concurrencyLimitExecutor.execute(rejectedTask)
            }
            concurrencyLimitExecutor.toString() shouldContain "worker count: 2"
            concurrencyLimitExecutor.toString() shouldContain "queue size: 1"

            // Release worker #1 with no further submissions; worker #1 and worker #2 drain CLE queue and exit
            unblockWorker.countDown()
            basePool.shutdown()
            basePool.awaitTermination(2, TimeUnit.SECONDS) shouldBe true

            ranTags shouldBe listOf("accepted")
            concurrencyLimitExecutor.toString() shouldContain "worker count: 0"
            concurrencyLimitExecutor.toString() shouldContain "queue size: 0"
        } finally {
            basePool.shutdownNow()
        }
    }

    test("queue boundary - large queue (1000+ tasks)") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = 1500
        val executedTasks = CopyOnWriteArrayList<Int>()
        val latch = CountDownLatch(taskCount)

        repeat(taskCount) { index ->
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()
                try {
                    executedTasks.add(index)
                    // Very short tasks to stress the queue
                    sleep(Random.nextLong(1, 5))
                } finally {
                    concurrencyChecker.leave()
                    latch.countDown()
                }
            }
        }

        latch.await(60, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.check()
        executedTasks shouldHaveSize taskCount
        executedTasks shouldContainExactlyInAnyOrder rangeList(taskCount)
    }

    test("interrupt status is restored to the base executor thread") {
        // Record the flag immediately after the worker runnable returns. A follow-up task on a
        // ThreadPoolExecutor would miss it: the pool clears the flag while taking the next task.
        fun flagAfterWorkerReturns(submit: (Executor) -> Unit): Boolean? {
            val carrier = Executors.newSingleThreadExecutor()
            val seen = AtomicReference<Boolean>()
            val done = CountDownLatch(1)
            try {
                val base = Executor { command ->
                    carrier.execute {
                        try {
                            command.run()
                            seen.set(currentThread().isInterrupted())
                        } finally {
                            Thread.interrupted()
                            done.countDown()
                        }
                    }
                }
                submit(ConcurrencyLimitExecutor(1, base))
                if (!done.await(5, TimeUnit.SECONDS)) return null
                return seen.get()
            } finally {
                carrier.shutdownNow()
            }
        }

        // InterruptedException does not set the flag; asyncWork must restore it when the worker returns.
        flagAfterWorkerReturns { cle ->
            cle.execute { throw InterruptedException("intentional") }
        } shouldBe true

        // The flag set by the first task is cleared before the second task and must be set again
        // when that same worker finally returns.
        val secondSawInterrupt = AtomicBoolean(true)
        flagAfterWorkerReturns { cle ->
            val running = CountDownLatch(1)
            val release = CountDownLatch(1)
            cle.execute {
                running.countDown()
                release.await()
                currentThread().interrupt()
            }
            running.await(5, TimeUnit.SECONDS) shouldBe true
            cle.execute { secondSawInterrupt.set(currentThread().isInterrupted()) }
            release.countDown()
        } shouldBe true
        secondSawInterrupt.get() shouldBe false

        flagAfterWorkerReturns { cle ->
            cle.execute { }
        } shouldBe false
    }

    test("maxConcurrency boundary - maxConcurrency = 1 (serial execution)") {
        val maxConcurrency = 1
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = 20
        val executionOrder = CopyOnWriteArrayList<Int>()
        val latch = CountDownLatch(taskCount)

        repeat(taskCount) { index ->
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()
                try {
                    executionOrder.add(index)
                    sleep(20)
                } finally {
                    concurrencyChecker.leave()
                    latch.countDown()
                }
            }
        }

        latch.await(10, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.check()

        // With maxConcurrency=1, execution should be strictly serial
        executionOrder shouldHaveSize taskCount
        // Verify order is preserved (since it's serial)
        executionOrder shouldBe rangeList(taskCount)
    }

    test("maxConcurrency above the pool size lets concurrency reach the pool size") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1000, executor)
        val concurrencyChecker = ConcurrencyChecker(THREAD_COUNT)

        val entered = CountDownLatch(THREAD_COUNT)
        val release = CountDownLatch(1)
        val extraTasks = 4
        val finished = CountDownLatch(THREAD_COUNT + extraTasks)

        repeat(THREAD_COUNT) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()
                try {
                    entered.countDown()
                    release.await()
                } finally {
                    concurrencyChecker.leave()
                    finished.countDown()
                }
            }
        }

        entered.await(5, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.max.get() shouldBeExactly THREAD_COUNT

        repeat(extraTasks) {
            concurrencyLimitExecutor.execute { finished.countDown() }
        }
        release.countDown()
        finished.await(5, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.check()
    }

    test("toString contains all key information in different states") {
        val maxConcurrency = 3
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)

        // State 1: Idle (no tasks)
        val idleStr = concurrencyLimitExecutor.toString()
        idleStr shouldContain "worker count: 0"
        idleStr shouldContain "synchronous runner count: 0"
        idleStr shouldContain "queue size: 0"
        idleStr shouldContain "max concurrency: $maxConcurrency"

        // State 2: With running tasks. State 3: after they finish, the queue is empty and workers are gone.
        val taskCount = maxConcurrency + 5
        val blockingLatch = CountDownLatch(1)
        val submittedLatch = CountDownLatch(maxConcurrency)
        val finished = CountDownLatch(taskCount)

        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                submittedLatch.countDown()
                blockingLatch.await()
                finished.countDown()
            }
        }

        submittedLatch.await(2, TimeUnit.SECONDS) shouldBe true

        val activeStr = concurrencyLimitExecutor.toString()
        logWithTimeAndThread("Active state: $activeStr")
        activeStr shouldContain "worker count: $maxConcurrency"
        activeStr shouldContain "queue size: 5"
        activeStr shouldContain "max concurrency: $maxConcurrency"

        blockingLatch.countDown()
        finished.await(5, TimeUnit.SECONDS) shouldBe true

        // workerCount is decremented after the task returns, so poll briefly for the idle state
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2)
        var drained = concurrencyLimitExecutor.toString()
        while (System.nanoTime() < deadline &&
            (!drained.contains("worker count: 0") || !drained.contains("queue size: 0"))
        ) {
            sleep(10)
            drained = concurrencyLimitExecutor.toString()
        }
        drained shouldContain "worker count: 0"
        drained shouldContain "synchronous runner count: 0"
        drained shouldContain "queue size: 0"
    }

    beforeSpec {
        // warmup executor
        List(THREAD_COUNT) {
            executor.submit { sleep(50) }
        }.forEach { it.get() }
    }

    afterSpec {
        executor.shutdownNow()
    }
})
