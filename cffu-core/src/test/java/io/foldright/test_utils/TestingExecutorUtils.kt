@file:JvmName("TestingExecutorUtils")

package io.foldright.test_utils

import io.foldright.cffu.CffuFactory
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.listeners.BeforeProjectListener
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong

////////////////////////////////////////////////////////////////////////////////
// region# test executors for kotest
////////////////////////////////////////////////////////////////////////////////

@JvmField
val THREAD_COUNT_OF_POOL: Int = (Runtime.getRuntime().availableProcessors() * 2).coerceAtLeast(4).coerceAtMost(15)

@JvmField
val testExecutor = createThreadPool("ThreadPoolForCffuTesting")

@JvmField
val testCffuFac: CffuFactory = CffuFactory.builder(testExecutor).build()

@JvmField
val testFjExecutor = createThreadPool("ForkJoinPoolForCffuTesting", true)

@JvmOverloads
fun createThreadPool(threadNamePrefix: String, isForkJoin: Boolean = false): ExecutorService {
    val counter = AtomicLong()
    val prefix = "${threadNamePrefix}_${Random.nextULong()}_"

    val executorService = if (!isForkJoin)
        ThreadPoolExecutor(
            /* corePoolSize = */ THREAD_COUNT_OF_POOL, /* maximumPoolSize = */ THREAD_COUNT_OF_POOL,
            /* keepAliveTime = */ 2, TimeUnit.MINUTES,
            /* workQueue = */ ArrayBlockingQueue(5000)
        ) { r ->
            Thread(r).apply {
                name = "${prefix}${counter.getAndIncrement()}"
                isDaemon = true
            }
        }
    else
        ForkJoinPool(
            /* parallelism = */ THREAD_COUNT_OF_POOL, /* factory = */ { fjPool ->
                ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(fjPool).apply {
                    name = "${prefix}${counter.getAndIncrement()}"
                }
            }, /* handler = */ null, /* asyncMode = */ false
        )

    return object : ExecutorService by executorService, ThreadPoolAcquaintance {
        override fun own(thread: Thread): Boolean = thread.name.startsWith(prefix)

        override fun unwrap(): ExecutorService = executorService

        override fun toString(): String =
            "test ${if (isForkJoin) "ForkJoinPool" else "ThreadPoolExecutor"} with thread name prefix `$prefix`"
    }
}

private interface ThreadPoolAcquaintance {
    fun own(thread: Thread): Boolean

    fun unwrap(): ExecutorService
}

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# check methods for thread/executor relationship
////////////////////////////////////////////////////////////////////////////////

fun assertRunningInExecutor(executor: Executor) {
    isRunningInExecutor(executor).shouldBeTrue()
}

fun assertNotRunningInExecutor(executor: Executor) {
    isRunningInExecutor(executor).shouldBeFalse()
}

private fun isRunningInExecutor(executor: Executor): Boolean =
    currentThread().belongsTo(executor)

private fun Thread.belongsTo(executor: Executor): Boolean =
    (executor as ThreadPoolAcquaintance).own(this)

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# util method for executors
////////////////////////////////////////////////////////////////////////////////

fun warmupExecutorService(vararg executors: ExecutorService) {
    executors.flatMap { executor ->
        (0 until THREAD_COUNT_OF_POOL * 2).map {
            executor.submit { nap() }
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

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Kotest Listener
////////////////////////////////////////////////////////////////////////////////

/**
 * https://kotest.io/docs/framework/project-config.html
 */
object CffuKotestProjectConfig : AbstractProjectConfig(), BeforeProjectListener {
    override suspend fun beforeProject() {
        println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        println("call beforeProject")
        println("Env Infos:")
        println("Parallelism of ForkJoinPool:     ${ForkJoinPool.getCommonPoolParallelism()}")
        println("Available Processors of Runtime: ${Runtime.getRuntime().availableProcessors()}")
        println("Java Home:                       ${System.getProperty("java.home")}")
        println("Java Version:                    ${System.getProperty("java.version")}")
        println("CI env var:                      ${System.getenv("CI")} (is ci env: ${isCiEnv()})")
        println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")

        warmupExecutorService(testExecutor, testFjExecutor)
    }
}
