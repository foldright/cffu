package io.foldright.cffu.test

import com.google.common.util.concurrent.Futures
import io.foldright.cffu.CompletableFutureUtils.failedFuture
import io.foldright.cffu.kotlin.toCffu
import io.foldright.cffu.kotlin.toCompletableFuture
import io.foldright.cffu.kotlin.toListenableFuture
import io.foldright.test_utils.testCffuFactory
import io.foldright.test_utils.testThreadPoolExecutor
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ExecutionException

class ListenableFutureExtensionsKtTest : FunSpec({
    test("toCompletableFuture") {
        val lf = Futures.immediateFuture(n)
        lf.toCompletableFuture(testThreadPoolExecutor).get() shouldBe n

        val failed = Futures.immediateFailedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> { failed.toCompletableFuture(testThreadPoolExecutor).get() }.cause shouldBeSameInstanceAs rte
    }

    test("toCffu") {
        val lf = Futures.immediateFuture(n)
        lf.toCffu(testCffuFactory).get() shouldBe n

        val failed = Futures.immediateFailedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> { failed.toCffu(testCffuFactory).get() }.cause shouldBeSameInstanceAs rte
    }

    test("toListenableFuture") {
        val cf = completedFuture(n)
        cf.toListenableFuture().get() shouldBe n

        val failed = failedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> { failed.toListenableFuture().get() }.cause shouldBeSameInstanceAs rte

        val cffu = testCffuFactory.completedFuture(n)
        cffu.toListenableFuture().get() shouldBe n

        val failedCffu = testCffuFactory.failedFuture<Int>(rte)
        shouldThrowExactly<ExecutionException> {
            failedCffu.toListenableFuture().get()
        }.cause shouldBeSameInstanceAs rte
    }
})
