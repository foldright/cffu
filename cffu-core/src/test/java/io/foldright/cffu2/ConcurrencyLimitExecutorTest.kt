package io.foldright.cffu2

import com.google.common.util.concurrent.MoreExecutors
import io.foldright.test_utils.testExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import io.kotest.matchers.nulls.shouldBeNull
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

private const val THREAD_COUNT = 8
private val executor: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

class ConcurrencyLimitExecutorTest : FunSpec({

    test("smoke run: common case") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = THREAD_COUNT * 3
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()

                val millis: Long = Random.nextLong(100, 200)
                logWithTimeAndThread("task %2d begin, then sleep %s ms", it, millis)
                sleep(millis)
                logWithTimeAndThread("task %2d end", it)

                concurrencyChecker.leave()
                latch.countDown()
            }
        }
        latch.await()

        concurrencyChecker.check()
    }

    test("smoke run: sync execution at MoreExecutors.directExecutor(), single-threaded submission") {
        val maxConcurrency = 3
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, MoreExecutors.directExecutor())
        val concurrencyChecker = ConcurrencyChecker(1)

        val taskCount = 32
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()

                sleep(Random.nextLong(10, 20))
                logWithTimeAndThread("running task %2d", it + 1)

                concurrencyChecker.leave()
                latch.countDown()
            }
        }
        latch.await()

        concurrencyChecker.check()
    }

    test("smoke run: sync execution at ThreadPoolExecutor/CallerRunsPolicy, single-threaded submission") {
        val executor = ThreadPoolExecutor(
            0, 1, 3, TimeUnit.SECONDS, SynchronousQueue(), ThreadPoolExecutor.CallerRunsPolicy()
        )
        val latchForBlockerTask = CountDownLatch(1)
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(3, executor)
        val f: Future<*> = executor.submit {
            latchForBlockerTask.await()
        }

        val concurrencyChecker = ConcurrencyChecker(1)

        val taskCount = 32
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()

                sleep(Random.nextLong(10, 20))
                logWithTimeAndThread("running task %2d", it + 1)

                concurrencyChecker.leave()
                latch.countDown()
            }
        }
        latch.await()

        concurrencyChecker.check()

        latchForBlockerTask.countDown()
        f.get().shouldBeNull()
    }

    /**
     * ❗❗ TODO: Due to the limitation in the current ConcurrencyLimitExecutor implementation,
     * if all tasks execute synchronously,
     *  - the remaining tasks in the work queue cannot be executed!
     *  - the task execution is only triggered by task submission.
     */
    test("smoke run: sync execution at MoreExecutors.directExecutor(), multi-threaded submission").config(enabled = false) {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(3, MoreExecutors.directExecutor())

        val concurrencyChecker = ConcurrencyChecker(3)

        val taskCount = 32
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            testExecutor.submit {
                logWithTimeAndThread("submit task %2d", it + 1)
                concurrencyLimitExecutor.execute {
                    concurrencyChecker.enter()

                    sleep(Random.nextLong(10, 20))
                    logWithTimeAndThread("running task %2d", it + 1)

                    concurrencyChecker.leave()
                    latch.countDown()
                }
            }
        }
        latch.await()

        concurrencyChecker.check()
    }

    beforeSpec {
        // warmup executor
        (0..THREAD_COUNT).map {
            executor.submit { sleep(50) }
        }.forEach { it.get() }
    }

    afterSpec {
        executor.shutdownNow()
    }
})

private fun logWithTimeAndThread(format: String = "", vararg args: Any?) {
    val msg = String.format(format, *args)
    System.out.printf("%tF %<tT.%<tL |%s| %s%n", System.currentTimeMillis(), currentThread().name, msg)
}

class ConcurrencyChecker(val maxConcurrency: Int) {
    val concurrencyCount = AtomicInteger()
    val max = AtomicInteger()

    fun enter() {
        concurrencyCount.incrementAndGet()
        max.updateAndGet { it.coerceAtLeast(concurrencyCount.get()) }
    }

    fun leave() = concurrencyCount.decrementAndGet()


    fun check() {
        max.get() shouldBeLessThanOrEqualTo maxConcurrency
    }
}
