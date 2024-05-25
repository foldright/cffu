@file:JvmName("TestThreadPoolManager")

package io.foldright.test_utils

import io.foldright.cffu.CffuFactory
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.AfterProjectListener
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong


val THREAD_COUNT_OF_POOL: Int = (Runtime.getRuntime().availableProcessors() * 2).coerceAtLeast(4).coerceAtMost(15)

@JvmOverloads
fun createThreadPool(threadNamePrefix: String, isForkJoin: Boolean = false): ExecutorService {
    val counter = AtomicLong()
    val prefix = "${threadNamePrefix}_${Random.nextULong()}"

    val executorService = if (!isForkJoin)
        ThreadPoolExecutor(
            /* corePoolSize = */ THREAD_COUNT_OF_POOL, /* maximumPoolSize = */ THREAD_COUNT_OF_POOL,
            /* keepAliveTime = */ 1, /* unit = */ TimeUnit.DAYS,
            /* workQueue = */ ArrayBlockingQueue(5000)
        ) { r ->
            Thread(r).apply {
                name = "${prefix}_${counter.getAndIncrement()}"
                isDaemon = true
            }
        }
    else
        ForkJoinPool(
            /* parallelism = */ THREAD_COUNT_OF_POOL,/* factory = */ { fjPool ->
                ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(fjPool).apply {
                    name = "${prefix}_${counter.getAndIncrement()}"
                }
            }, /* handler = */ null, /* asyncMode = */ false
        )

    return object : ExecutorService by executorService, ThreadPoolAcquaintance {
        override fun isMyThread(thread: Thread): Boolean = thread.name.startsWith(prefix)

        override fun unwrap(): ExecutorService = executorService

        override fun toString(): String = "test ${if (isForkJoin) "ForkJoinPool" else "ThreadPoolExecutor"} $prefix"
    }
}

private fun Executor.doesOwnThread(thread: Thread): Boolean = (this as ThreadPoolAcquaintance).isMyThread(thread)

private interface ThreadPoolAcquaintance {
    fun isMyThread(thread: Thread): Boolean

    fun unwrap(): ExecutorService
}

fun isRunInExecutor(executor: Executor): Boolean =
    executor.doesOwnThread(currentThread())

fun assertRunInExecutor(executor: Executor) {
    isRunInExecutor(executor).shouldBeTrue()
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
        println((it as ThreadPoolAcquaintance).unwrap())
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
        it.awaitTermination(3, TimeUnit.SECONDS).shouldBeTrue()
    }
}

////////////////////////////////////////////////////////////////////////////////
// executors for kotest
////////////////////////////////////////////////////////////////////////////////

val testThreadPoolExecutor: ExecutorService =
    createThreadPool("CompletableFutureUseTest_ThreadPool")

val testCffuFactory: CffuFactory = CffuFactory.builder(testThreadPoolExecutor).build()

val testForkJoinPoolExecutor: ExecutorService =
    createThreadPool("CompletableFutureUseTest_ForkJoinPool", true)

/**
 * https://kotest.io/docs/framework/project-config.html
 */
object CffuKotestProjectConfig : AbstractProjectConfig(), BeforeProjectListener, AfterProjectListener {
    override suspend fun beforeProject() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println("Env Infos:")
        println("Parallelism of ForkJoinPool:     ${ForkJoinPool.getCommonPoolParallelism()}")
        println("Available Processors of Runtime: ${Runtime.getRuntime().availableProcessors()}")
        println("Java Home:                       ${System.getProperty("java.home")}")
        println("Java Version:                    ${System.getProperty("java.version")}")
        println("CI env var:                      ${System.getenv("CI")} (is ci env: ${isCiEnv()})")
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")

        warmupExecutorService(testThreadPoolExecutor, testForkJoinPoolExecutor)
    }

    override suspend fun afterProject() {
        shutdownExecutorService(testThreadPoolExecutor, testForkJoinPoolExecutor)
    }
}
