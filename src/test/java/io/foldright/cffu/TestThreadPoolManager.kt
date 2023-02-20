@file:JvmName("TestThreadPoolManager")


package io.foldright.cffu

import io.kotest.assertions.fail
import io.kotest.core.annotation.AutoScan
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong

lateinit var threadPoolExecutor: ExecutorService
lateinit var forkJoinPoolExecutor: ExecutorService

const val THREAD_COUNT_OF_POOL = 10

private val threadNamePrefixOfThreadPoolExecutor = "CompletableFutureUseTest_ThreadPool_${Random.nextULong()}-"
private val threadNamePrefixOfForkJoinPoolExecutor = "CompletableFutureUseTest_ForkJoinPool_${Random.nextULong()}-"

fun assertRunInExecutor(executor: Executor) {
    when (executor) {
        threadPoolExecutor -> Thread.currentThread().name shouldStartWith threadNamePrefixOfThreadPoolExecutor
        forkJoinPoolExecutor -> Thread.currentThread().name shouldStartWith threadNamePrefixOfForkJoinPoolExecutor
        else -> fail("should be happened")
    }
}

fun assertRunInTestThreadPoolExecutor() {
    Thread.currentThread().name shouldStartWith threadNamePrefixOfThreadPoolExecutor
}

fun assertRunInTestForkJoinPoolExecutor() {
    Thread.currentThread().name shouldStartWith threadNamePrefixOfForkJoinPoolExecutor
}

fun assertRunNotInTestThreadPoolExecutor() {
    Thread.currentThread().name shouldNotStartWith threadNamePrefixOfThreadPoolExecutor
}

fun assertRunNotInTestForkJoinPoolExecutor() {
    Thread.currentThread().name shouldNotStartWith threadNamePrefixOfForkJoinPoolExecutor
}

fun assertRunNotInExecutor(executor: Executor) {
    when (executor) {
        threadPoolExecutor -> Thread.currentThread().name shouldNotStartWith threadNamePrefixOfThreadPoolExecutor
        forkJoinPoolExecutor -> Thread.currentThread().name shouldNotStartWith threadNamePrefixOfForkJoinPoolExecutor
        else -> fail("should be happened")
    }
}

/**
 * Introduction to Extensions
 * https://kotest.io/docs/framework/extensions/extensions-introduction.html
 *
 * To run an extension for every spec in the entire project:
 * mark the listener with @AutoScan
 */
@AutoScan
object InitTestThreadPoolsProjectListener : BeforeProjectListener, AfterProjectListener {
    override suspend fun beforeProject() {
        threadPoolExecutor = run {
            val counter = AtomicLong()
            ThreadPoolExecutor(
                THREAD_COUNT_OF_POOL, THREAD_COUNT_OF_POOL, 1, TimeUnit.DAYS,
                ArrayBlockingQueue(THREAD_COUNT_OF_POOL * 2)
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
                THREAD_COUNT_OF_POOL, {
                    ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(it).apply {
                        name = "$threadNamePrefixOfForkJoinPoolExecutor${counter.getAndIncrement()}"
                    }
                },
                null, false
            )
        }

        // warmup
        val fs1: List<Future<*>> = (0 until THREAD_COUNT_OF_POOL * 2).map {
            threadPoolExecutor.submit { sleep(2) }
        }
        val fs2 = (0 until THREAD_COUNT_OF_POOL * 2).map {
            forkJoinPoolExecutor.submit { sleep(2) }
        }
        fs1.forEach { it.get() }
        fs2.forEach { it.get() }
    }

    override suspend fun afterProject() {
        threadPoolExecutor.shutdown()
        forkJoinPoolExecutor.shutdown()

        @Suppress("BlockingMethodInNonBlockingContext")
        threadPoolExecutor.awaitTermination(1, TimeUnit.SECONDS) shouldBe true
        @Suppress("BlockingMethodInNonBlockingContext")
        forkJoinPoolExecutor.awaitTermination(1, TimeUnit.SECONDS) shouldBe true
    }
}

