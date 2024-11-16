package io.foldright.cffu.lf.study

import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import io.foldright.test_utils.n
import io.foldright.test_utils.sleep
import io.foldright.test_utils.testExecutor
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.longs.shouldBeGreaterThanOrEqual
import io.kotest.matchers.longs.shouldBeLessThan
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import kotlin.system.measureTimeMillis

/**
 * Study ListenableFuture usage.
 */
class ListenableFutureUsageStudyCaseTest : FunSpec({
    val sleepMs = 100L

    test("warming up") {
        val futures = Array(10) { Futures.submit(Callable { it }, testExecutor) }
        Futures.allAsList(*futures).get()
        Futures.successfulAsList(*futures).get()
    }

    test("`allAsList` methods of ListenableFuture is fail-fast") {
        measureTimeMillis {
            val longTask: ListenableFuture<Int> = Futures.submit(Callable {
                sleep(sleepMs)
                n
            }, testExecutor)

            shouldThrowExactly<CancellationException> {
                Futures.allAsList(Futures.immediateCancelledFuture<Int>(), longTask).get()
            }
        }.shouldBeLessThan(sleepMs / 2)
    }

    test("`successfulAsList` methods of ListenableFuture is NOT fail-fast") {
        measureTimeMillis {
            val longTask: ListenableFuture<Int> = Futures.submit(Callable {
                sleep(sleepMs)
                n
            }, testExecutor)

            Futures.successfulAsList(Futures.immediateCancelledFuture<Int>(), longTask)
                .get().shouldContainExactly(null, n)
        }.shouldBeGreaterThanOrEqual(sleepMs)
    }
})
