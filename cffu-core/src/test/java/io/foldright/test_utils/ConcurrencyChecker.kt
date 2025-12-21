package io.foldright.test_utils

import io.kotest.assertions.withClue
import io.kotest.matchers.comparables.shouldBeLessThanOrEqualTo
import java.util.concurrent.atomic.AtomicInteger

class ConcurrencyChecker(val maxConcurrency: Int) {
    val concurrencyCount = AtomicInteger()
    val max = AtomicInteger()

    fun enter() {
        concurrencyCount.incrementAndGet()
        max.updateAndGet { it.coerceAtLeast(concurrencyCount.get()) }
    }

    fun leave() = concurrencyCount.decrementAndGet()

    fun check() {
        val m: Int = max.get()
        withClue("Concurrency check failed: actual concurrency $m > max concurrency $maxConcurrency") {
            m shouldBeLessThanOrEqualTo maxConcurrency
        }
    }
}
