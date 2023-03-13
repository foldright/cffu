@file:JvmName("TestThreadPoolManager")

package io.foldright.testutils

import io.kotest.assertions.fail
import io.kotest.core.annotation.AutoScan
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong


const val THREAD_COUNT_OF_POOL = 10

private val threadNamePrefixOfThreadPoolExecutor = "CompletableFutureUseTest_ThreadPool_${Random.nextULong()}-"
private val threadNamePrefixOfForkJoinPoolExecutor = "CompletableFutureUseTest_ForkJoinPool_${Random.nextULong()}-"

val testThreadPoolExecutor: ExecutorService by lazy {
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

val testForkJoinPoolExecutor: ExecutorService by lazy {
    val counter = AtomicLong()
    ForkJoinPool(
        THREAD_COUNT_OF_POOL, { fjPool ->
            ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(fjPool).apply {
                name = "$threadNamePrefixOfForkJoinPoolExecutor${counter.getAndIncrement()}"
            }
        },
        null, false
    )
}

fun assertRunInTestThreadPoolExecutor() {
    currentThread().name shouldStartWith threadNamePrefixOfThreadPoolExecutor
}

fun assertRunInTestForkJoinPoolExecutor() {
    currentThread().name shouldStartWith threadNamePrefixOfForkJoinPoolExecutor
}

fun assertRunNotInTestThreadPoolExecutor() {
    currentThread().name shouldNotStartWith threadNamePrefixOfThreadPoolExecutor
}

fun assertRunNotInTestForkJoinPoolExecutor() {
    currentThread().name shouldNotStartWith threadNamePrefixOfForkJoinPoolExecutor
}

fun assertRunInExecutor(executor: Executor) {
    when (executor) {
        testThreadPoolExecutor -> assertRunInTestThreadPoolExecutor()
        testForkJoinPoolExecutor -> assertRunInTestForkJoinPoolExecutor()
        else -> fail("should NOT happen")
    }
}

fun assertRunNotInExecutor(executor: Executor) {
    when (executor) {
        testThreadPoolExecutor -> assertRunNotInTestThreadPoolExecutor()
        testForkJoinPoolExecutor -> assertRunNotInTestForkJoinPoolExecutor()
        else -> fail("should NOT happen")
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
        // warmup
        val fs1: List<Future<*>> = (0 until THREAD_COUNT_OF_POOL * 2).map {
            testThreadPoolExecutor.submit { sleep() }
        }
        val fs2 = (0 until THREAD_COUNT_OF_POOL * 2).map {
            testForkJoinPoolExecutor.submit { sleep() }
        }
        fs1.forEach { it.get() }
        fs2.forEach { it.get() }
    }

    override suspend fun afterProject() {
        testThreadPoolExecutor.shutdown()
        testForkJoinPoolExecutor.shutdown()

        @Suppress("BlockingMethodInNonBlockingContext")
        testThreadPoolExecutor.awaitTermination(3, TimeUnit.SECONDS).shouldBeTrue()
        @Suppress("BlockingMethodInNonBlockingContext")
        testForkJoinPoolExecutor.awaitTermination(3, TimeUnit.SECONDS).shouldBeTrue()
    }
}

