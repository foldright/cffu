package io.foldright.lf.kotlin

import com.google.common.util.concurrent.Futures
import io.foldright.cffu.CompletableFutureUtils.failedFuture
import io.foldright.lf.kotlin.toCffu
import io.foldright.lf.kotlin.toCompletableFuture
import io.foldright.lf.kotlin.toListenableFuture
import io.foldright.test_utils.n
import io.foldright.test_utils.rte
import io.foldright.test_utils.testCffuFac
import io.foldright.test_utils.testExecutor
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ExecutionException

class ListenableFutureExtensionsTest : FunSpec({
    test("toCompletableFuture") {
        val lf = Futures.immediateFuture(n)
        lf.toCompletableFuture(testExecutor, true).get() shouldBe n

        val failed = Futures.immediateFailedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> {
            failed.toCompletableFuture(testExecutor, true).get()
        }.cause shouldBeSameInstanceAs rte
    }

    test("toCffu") {
        val lf = Futures.immediateFuture(n)
        lf.toCffu(testCffuFac, true).get() shouldBe n

        val failed = Futures.immediateFailedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> {
            failed.toCffu(testCffuFac, true).get()
        }.cause shouldBeSameInstanceAs rte
    }

    test("toListenableFuture") {
        val cf = completedFuture(n)
        cf.toListenableFuture().get() shouldBe n

        val failed = failedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> { failed.toListenableFuture().get() }.cause shouldBeSameInstanceAs rte

        val cffu = testCffuFac.completedFuture(n)
        cffu.toListenableFuture().get() shouldBe n

        val failedCffu = testCffuFac.failedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> {
            failedCffu.toListenableFuture().get()
        }.cause shouldBeSameInstanceAs rte
    }
})
