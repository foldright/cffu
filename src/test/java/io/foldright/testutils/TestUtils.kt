@file:JvmName("TestUtils")

package io.foldright.testutils

import io.kotest.core.test.TestCase
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.CompletableFuture


@JvmOverloads
fun sleep(millis: Long = 2) {
    Thread.sleep(millis)
}

////////////////////////////////////////////////////////////////////////////////
// CF creator utils
////////////////////////////////////////////////////////////////////////////////

fun <T> createNormallyCompletedFutureWithSleep(value: T): CompletableFuture<T> = CompletableFuture.supplyAsync {
    sleep(10)
    value
}

fun <T, E : Throwable> createExceptionallyCompletedFutureWithSleep(ex: E): CompletableFuture<T> =
    CompletableFuture.supplyAsync {
        sleep(10)
        throw ex
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

val java9Plus: (TestCase) -> Boolean = { isJavaVersionAtLeast(JavaVersion.JAVA_9) }

val java12Plus: (TestCase) -> Boolean = { isJavaVersionAtLeast(JavaVersion.JAVA_12) }
