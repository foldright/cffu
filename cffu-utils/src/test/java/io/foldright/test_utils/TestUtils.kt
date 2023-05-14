@file:JvmName("TestUtils")

package io.foldright.test_utils

import java.util.concurrent.CompletableFuture


////////////////////////////////////////////////////////////////////////////////
// constants for testing
////////////////////////////////////////////////////////////////////////////////

const val n = 42
const val anotherN = 424242
const val s = "S42"
const val d = 42.1

@JvmField
val rte = RuntimeException("Bang")

@JvmField
val anotherRte = RuntimeException("AnotherBang")

////////////////////////////////////////////////////////////////////////////////
// util methods for testing
////////////////////////////////////////////////////////////////////////////////

fun <T> createIncompleteFuture(): CompletableFuture<T> = CompletableFuture()

@JvmOverloads
fun <T> createFutureCompleteLater(value: T, millis: Long = 100): CompletableFuture<T> = CompletableFuture.supplyAsync {
    sleep(millis)
    value
}

fun <T> createCancelledFuture(): CompletableFuture<T> = CompletableFuture<T>().apply {
    cancel(false)
}

/**
 * sleep without throwing checked exception
 */
@JvmOverloads
fun sleep(millis: Long = 10) {
    Thread.sleep(millis)
}
