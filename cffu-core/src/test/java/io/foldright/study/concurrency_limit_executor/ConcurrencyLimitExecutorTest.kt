package io.foldright.study.concurrency_limit_executor

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.*
import kotlin.random.Random

private const val THREAD_COUNT = 8
private val executor: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

class ConcurrencyLimitExecutorTest : FunSpec({

    test("ConcurrencyLimitExecutorByLock mock run") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutorByLock(4, executor)

        val taskCount = THREAD_COUNT * 3
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                val millis: Long = Random.nextLong(100, 200)
                logWithTimeAndThread("task %2d begin, then sleep %s ms", it, millis)
                sleep(millis)
                logWithTimeAndThread("task %2d end", it)

                latch.countDown()
            }
        }

        latch.await()
    }

    test("sync execution mock run") {
        val executor = ThreadPoolExecutor(
            0, 1, 3, TimeUnit.SECONDS, SynchronousQueue(), ThreadPoolExecutor.CallerRunsPolicy()
        )
        val latch = CountDownLatch(1)
        val concurrencyLimitExecutor = ConcurrencyLimitExecutorByLock(3, executor)
        val f: Future<*> = executor.submit {
            latch.await()
        }

        repeat(33) {
            concurrencyLimitExecutor.execute {
                println(currentThread().name)
            }
        }

        latch.countDown()
        f.get().shouldBeNull()
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
