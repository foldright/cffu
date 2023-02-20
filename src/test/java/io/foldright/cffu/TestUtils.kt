@file:JvmName("TestUtils")

package io.foldright.cffu

import io.kotest.core.test.TestCase
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import java.util.concurrent.CompletableFuture


@Suppress("NOTHING_TO_INLINE")
inline fun sleep(millis: Long) {
    Thread.sleep(millis)
}

////////////////////////////////////////////////////////////////////////////////
// CF creator utils
////////////////////////////////////////////////////////////////////////////////

fun <T> createNormallyCompletedFutureWithSleep(value: T): CompletableFuture<T> = CompletableFuture.supplyAsync {
    sleep(10)
    value
}

fun <T, E : Throwable> createExceptionallyCompletedFutureWithSleep(exception: E): CompletableFuture<T> =
    CompletableFuture.supplyAsync {
        sleep(10)
        throw exception
    }

////////////////////////////////////////////////////////////////////////////////
// list utils
////////////////////////////////////////////////////////////////////////////////

fun <T> merge(list1: List<T>, list2: List<T>) = list1.merge(list2)

@JvmName("mergeExt")
infix fun <T> List<T>.merge(other: List<T>): List<T> = mutableListOf<T>().apply {
    addAll(this@merge)
    addAll(other)
}

fun addCurrentThreadName(names: List<String>) = names + Thread.currentThread().name

////////////////////////////////////////////////////////////////////////////////
// Kotest conditions
////////////////////////////////////////////////////////////////////////////////

val java9Plus: (TestCase) -> Boolean = { SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9) }

val java12Plus: (TestCase) -> Boolean = { SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_12) }
