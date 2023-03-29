package io.foldright.cffu.kotlin

import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.foldright.test_utils.testCffuFactory
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
        CompletableFuture.completedFuture(n).asCffu(testCffuFactory).await() shouldBe n

        CompletableFuture.completedFuture(n).asCffu(testCffuFactory).let {
            it.defaultExecutor() shouldBeSameInstanceAs testThreadPoolExecutor
            it.cffuFactory() shouldBeSameInstanceAs testCffuFactory
        }

        val fac2 = newCffuFactoryBuilder(testForkJoinPoolExecutor).build()
        CompletableFuture.completedFuture(n).asCffu(testCffuFactory)
            .resetCffuFactory(fac2).let {
                it.defaultExecutor() shouldBeSameInstanceAs testForkJoinPoolExecutor
                it.cffuFactory() shouldBeSameInstanceAs fac2
            }
    }

    test("allOf/anyOf - collection") {
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

    test("allOf/anyOf - array") {
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

    test("cffuAllOf/cffuAnyOf - collection") {
        listOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).cffuAllOf(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        setOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).cffuAllOf(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).cffuAllOf(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).cffuAllOf(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).cffuAnyOf(testCffuFactory).await() shouldBe 42

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
            testCffuFactory.newIncompleteCffu(),
        ).cffuAnyOf(testCffuFactory).await() shouldBe 42

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).cffuAnyOf(testCffuFactory).await() shouldBe 42

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).cffuAnyOf(testCffuFactory).await() shouldBe 42
    }

    test("cffuAllOf/cffuAnyOf - array") {
        arrayOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).cffuAllOf(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).cffuAllOf(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).cffuAnyOf(testCffuFactory).await() shouldBe 42

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).cffuAnyOf(testCffuFactory).await() shouldBe 42
    }
})
