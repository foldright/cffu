@file:JvmName("TestThreadPoolManager")

package io.foldright.test_utils

import io.kotest.core.annotation.AutoScan
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong


const val THREAD_COUNT_OF_POOL = 5

@JvmOverloads
fun createThreadPool(threadNamePrefix: String, isForkJoin: Boolean = false): ExecutorService {
    val counter = AtomicLong()
    val prefix = "${threadNamePrefix}_${Random.nextULong()}-"

    val executorService = if (!isForkJoin)
        ThreadPoolExecutor(
            THREAD_COUNT_OF_POOL, THREAD_COUNT_OF_POOL, 1, TimeUnit.DAYS,
            ArrayBlockingQueue(5000)
        ) { r ->
            Thread(r).apply {
                name = "$prefix${counter.getAndIncrement()}"
                isDaemon = true
            }
        }
    else
        ForkJoinPool(
            THREAD_COUNT_OF_POOL, { fjPool ->
                ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(fjPool).apply {
                    name = "$prefix${counter.getAndIncrement()}"
                }
            },
            null, false
        )

    return object : ExecutorService by executorService, ThreadPoolAcquaintance {
        override fun isMyThread(thread: Thread): Boolean = thread.name.startsWith(prefix)

        override fun wrappedThreadPool(): ExecutorService = executorService
    }
}

@JvmName("isExecutorOwnThread")
fun Executor.doesOwnThread(thread: Thread): Boolean = (this as ThreadPoolAcquaintance).isMyThread(thread)

private interface ThreadPoolAcquaintance {
    fun isMyThread(thread: Thread): Boolean

    fun wrappedThreadPool(): ExecutorService
}

fun assertRunInExecutor(executor: Executor) {
    executor.doesOwnThread(currentThread()).shouldBeTrue()
}

fun assertNotRunInExecutor(executor: Executor) {
    executor.doesOwnThread(currentThread()).shouldBeFalse()
}

fun warmupExecutorService(vararg executors: ExecutorService) {
    executors.flatMap { executor ->
        (0 until THREAD_COUNT_OF_POOL * 2).map {
            executor.submit { sleep() }
        }
    }.forEach { f: Future<*> -> f.get() }
}

fun shutdownExecutorService(vararg executors: ExecutorService) {
    executors.forEach {
        it.shutdown()
    }
    executors.forEach {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println((it as ThreadPoolAcquaintance).wrappedThreadPool())
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        it.awaitTermination(3, TimeUnit.SECONDS).shouldBeTrue()
    }
}


////////////////////////////////////////////////////////////////////////////////
// executors for kotest
////////////////////////////////////////////////////////////////////////////////


val testThreadPoolExecutor: ExecutorService =
    createThreadPool("CompletableFutureUseTest_ThreadPool")

val testForkJoinPoolExecutor: ExecutorService =
    createThreadPool("CompletableFutureUseTest_ForkJoinPool", true)

/**
 * Introduction to Extensions of kotest
 * https://kotest.io/docs/framework/extensions/extensions-introduction.html
 *
 * To run an extension for every spec in the entire project:
 * mark the listener with @AutoScan
 */
@AutoScan
object InitTestThreadPoolsProjectListener : BeforeProjectListener, AfterProjectListener {
    override suspend fun beforeProject() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println("Env Infos:")
        println("Parallelism of ForkJoinPool:     ${ForkJoinPool.getCommonPoolParallelism()}")
        println("Available Processors of Runtime: ${Runtime.getRuntime().availableProcessors()}")
        println("Java Home:                       ${System.getProperty("java.home")}")
        println("Java Version:                    ${System.getProperty("java.version")}")
        println("CI env var:                      ${System.getenv("CI")} (${isCiEnv()})")
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")

        warmupExecutorService(testThreadPoolExecutor, testForkJoinPoolExecutor)
    }

    override suspend fun afterProject() {
        shutdownExecutorService(testThreadPoolExecutor, testForkJoinPoolExecutor)
    }
}
