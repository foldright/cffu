@file:JvmName("Utils")

package io.foldright.cffu

import io.kotest.core.test.TestCase
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils

infix fun <T, R> (() -> Unit).andThen(f: (T) -> R): (T) -> R = {
    this()
    f(it)
}

infix fun <R> (() -> Unit).andThen(f: () -> R): () -> R = {
    this()
    f()
}

fun <T> merge(list1: List<T>, list2: List<T>) = list1.merge(list2)

@JvmName("mergeExt")
infix fun <T> List<T>.merge(other: List<T>): List<T> = mutableListOf<T>().apply {
    addAll(this@merge)
    addAll(other)
}

@Suppress("NOTHING_TO_INLINE")
inline fun sleep(millis: Long) {
    Thread.sleep(millis)
}

val java9Plus: (TestCase) -> Boolean = { SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9) }
val java12Plus: (TestCase) -> Boolean = { SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_12) }
