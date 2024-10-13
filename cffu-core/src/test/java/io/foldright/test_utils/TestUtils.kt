@file:JvmName("TestUtils")

package io.foldright.test_utils

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.kotest.core.test.TestCase
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.*
import java.util.function.Supplier


////////////////////////////////////////////////////////////////////////////////
// region# util methods for testing
////////////////////////////////////////////////////////////////////////////////

fun <T> incompleteCf(): CompletableFuture<T> = CompletableFuture()

@JvmOverloads
fun <T> completeLaterCf(value: T, millis: Long = MEDIAN_WAIT_MS): CompletableFuture<T> = CompletableFuture.supplyAsync {
    sleep(millis)
    value
}

@JvmOverloads
fun <T> completeLaterCf(
    value: () -> T, millis: Long = MEDIAN_WAIT_MS, executor: Executor = DEFAULT_EXECUTOR
): CompletableFuture<T> {
    val action = Supplier {
        sleep(millis)
        value()
    }
    return if (executor === DEFAULT_EXECUTOR) CompletableFuture.supplyAsync(action)
    else CompletableFuture.supplyAsync(action, executor)
}

@JvmOverloads
fun <T> cancelledFuture(mayInterruptIfRunning: Boolean = false): CompletableFuture<T> = CompletableFuture<T>().apply {
    cancel(mayInterruptIfRunning)
}

private val DEFAULT_EXECUTOR: Executor = Executor { /* do nothing */ }

@JvmOverloads
fun <T> supplyLater(value: T, millis: Long = MEDIAN_WAIT_MS) = Supplier<T> {
    sleep(millis)
    value
}

@JvmOverloads
fun <T> supplyLater(ex: Throwable, millis: Long = MEDIAN_WAIT_MS) = Supplier<T> {
    sleep(millis)
    throw ex
}

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Helper functions for API compatibility test
//  - CompletableFutureApiCompatibilityTest
//  - CffuApiCompatibilityTest
////////////////////////////////////////////////////////////////////////////////

/**
 * safe means works under java 8
 */
@Suppress("UNUSED_PARAMETER")
fun <T> safeNewFailedCompletableFuture(executorService: ExecutorService, t: Throwable): CompletableFuture<T> {
    val failed: CompletableFuture<T> = CompletableFuture<T>()
    failed.completeExceptionally(t)
    return failed
}

/**
 * safe means works under java 8
 */
fun <T> safeNewFailedCffu(executorService: ExecutorService, t: Throwable): Cffu<T> {
    return CffuFactory.builder(executorService).build().failedFuture(t)
}

fun assertCompletableFutureRunInDefaultThread(executorService: ExecutorService) {
    assertNotRunningInExecutor(executorService)
}

fun assertCompletableFutureRunInThreadOf(executorService: ExecutorService) {
    assertRunningInExecutor(executorService)
}

fun assertCffuRunInDefaultThread(executorService: ExecutorService) {
    assertRunningInExecutor(executorService)
}

fun assertCffuRunInThreadOf(executorService: ExecutorService) {
    assertRunningInExecutor(executorService)
}

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# some simple util functions
////////////////////////////////////////////////////////////////////////////////

/**
 * sleep without throwing checked exception
 */
@JvmOverloads
fun sleep(millis: Long = SHORT_WAIT_MS) {
    Thread.sleep(millis)
}

/**
 * sleep short time
 */
@JvmOverloads
fun nap(millis: Long = SHORT_WAIT_MS) {
    Thread.sleep(millis)
}

/**
 * sleep long time
 */
@JvmOverloads
fun snoreZzz(millis: Long = MEDIAN_WAIT_MS) {
    Thread.sleep(millis)
}

fun sneakyThrow(ex: Throwable): Unit = throw ex

fun <T> merge(list1: List<T>, list2: List<T>) = list1.merge(list2)

@JvmName("mergeExt")
infix fun <T> List<T>.merge(other: List<T>): List<T> = mutableListOf<T>().apply {
    addAll(this@merge)
    addAll(other)
}

fun addCurrentThreadName(names: List<String>) = names + Thread.currentThread().name

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Java Version Checker
////////////////////////////////////////////////////////////////////////////////

fun isJava9Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_9)

fun isJava12Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_12)

fun isJava19Plus(): Boolean = isJavaVersionAtLeast(JavaVersion.JAVA_19)

// about CI env var
// https://docs.github.com/en/actions/learn-github-actions/variables#default-environment-variables
fun isCiEnv(): Boolean = System.getenv("CI")?.lowercase() == "true"

fun isCiMacEnv(): Boolean = isCiEnv() && SystemUtils.IS_OS_MAC

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Kotest conditions
////////////////////////////////////////////////////////////////////////////////

val java9Plus: (TestCase) -> Boolean = { isJava9Plus() }

val java12Plus: (TestCase) -> Boolean = { isJava12Plus() }
