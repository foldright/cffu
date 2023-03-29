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

        CompletableFuture.completedFuture(n).asCffu(factory).let {
            it.defaultExecutor() shouldBeSameInstanceAs testThreadPoolExecutor
            it.cffuFactory() shouldBeSameInstanceAs factory
        }
        val fac2 = newCffuFactoryBuilder(testForkJoinPoolExecutor).build()
        CompletableFuture.completedFuture(n).asCffu(factory)
            .resetCffuFactory(fac2).let {
                it.defaultExecutor() shouldBeSameInstanceAs testForkJoinPoolExecutor
                it.cffuFactory() shouldBeSameInstanceAs fac2
            }
    }

    test("allOf/AnyOf - collection") {
        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOf().await()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOf().await()

        listOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOf().await() shouldBe 42

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOf().await() shouldBe 42
    }

    test("allOf/AnyOf - array") {
        arrayOf<CompletableFuture<*>>(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOf().await()

        arrayOf<CompletableFuture<*>>(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOf().await() shouldBe 42
    }
})
