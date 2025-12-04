package io.foldright.study.concurrency_limit_executor

import io.kotest.core.spec.style.FunSpec
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.random.Random

private const val THREAD_COUNT = 8
private val executor: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

class ConcurrencyLimitExecutorTest : FunSpec({

    test("ConcurrencyLimitExecutorByLock") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutorByLock(4, executor)

        val taskCount = THREAD_COUNT * 3
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                val millis: Long = Random.nextLong(1000, 3000)
                logWithTimeAndThread("task %2d begin, then sleep %s ms", it, millis)
                sleep(millis)
                logWithTimeAndThread("task %2d end", it)

                latch.countDown()
            }
        }

        latch.await()
    }

    beforeSpec {
        // warmup executor
        (0..THREAD_COUNT).map {
            executor.submit { Thread.sleep(100) }
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
