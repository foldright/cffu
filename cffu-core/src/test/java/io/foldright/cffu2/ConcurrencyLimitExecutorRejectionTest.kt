package io.foldright.cffu2

import io.foldright.test_utils.logWithTimeAndThread
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.lang.Thread.sleep
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

/**
 * Where the exception, the queue and the counters go when a submission does not start a worker.
 * Shallow cases first.
 *
 * The base executor rejects every time, rejects once, or drops the worker without throwing.
 * Then this executor rejects on its own: all slots are held by synchronous runners, the rejected
 * task is equal to one already queued, and a synchronous task queues itself again before throwing.
 *
 * @see ConcurrencyLimitExecutorTest
 * @see ConcurrencyLimitExecutorBulkSubmissionTest
 */
class ConcurrencyLimitExecutorRejectionTest : FunSpec({
    val threadCount = 8
    val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)

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


    test("base executor throws exception - exception propagation") {
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

    test("base executor silently drops the worker - slot leaks and queued tasks stay queued") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1, Executor {})
        val ran = AtomicInteger()

        // execute() returns without running the worker, but the concurrency slot is still taken
        concurrencyLimitExecutor.execute { ran.incrementAndGet() }
        concurrencyLimitExecutor.execute { ran.incrementAndGet() }

        ran.get() shouldBe 0
        val stuck = concurrencyLimitExecutor.toString()
        stuck shouldContain "worker count: 1"
        stuck shouldContain "queue size: 2"
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

    beforeSpec {
        // warmup executor
        List(threadCount) {
            executor.submit { sleep(50) }
        }.forEach { it.get() }
    }

    afterSpec {
        executor.shutdownNow()
    }
})
