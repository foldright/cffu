package io.foldright.cffu2

import io.foldright.test_utils.ConcurrencyChecker
import io.foldright.test_utils.rangeList
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import java.lang.Thread.sleep
import java.util.concurrent.*
import kotlin.random.Random


/**
 * Large submissions. Both tests run for seconds, so they are not part of [ConcurrencyLimitExecutorTest].
 *
 * One thread submits 1500 short tasks, deep enough to fill the queue. Ten threads submit 1000 tasks
 * at the same time.
 *
 * @see ConcurrencyLimitExecutorTest
 * @see ConcurrencyLimitExecutorRejectionTest
 */
class ConcurrencyLimitExecutorBulkSubmissionTest : FunSpec({
    val threadCount = 8
    val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)

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
