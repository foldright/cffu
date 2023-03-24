package io.foldright.cffu.kotlin

import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.foldright.test_utils.testForkJoinPoolExecutor
import io.foldright.test_utils.testThreadPoolExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

class CffuExtensionsTest : FunSpec({
    val n = 42

    test("asCffu") {
        val factory = newCffuFactoryBuilder(testThreadPoolExecutor).build()
        CompletableFuture.completedFuture(n).asCffu(factory).await() shouldBe n

        CompletableFuture.completedFuture(n).asCffu(factory)
            .defaultExecutor() shouldBeSameInstanceAs testThreadPoolExecutor

        CompletableFuture.completedFuture(n).asCffu(factory)
            .resetCffuFactory(newCffuFactoryBuilder(testForkJoinPoolExecutor).build())
            .defaultExecutor() shouldBeSameInstanceAs testForkJoinPoolExecutor
    }
})
