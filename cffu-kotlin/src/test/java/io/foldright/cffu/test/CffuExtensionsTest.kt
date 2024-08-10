package io.foldright.cffu.test

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.kotlin.*
import io.foldright.cffu.unwrapMadeExecutor
import io.foldright.test_utils.n
import io.foldright.test_utils.testCffuFac
import io.foldright.test_utils.testFjExecutor
import io.foldright.test_utils.testExecutor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeTypeOf
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class CffuExtensionsTest : FunSpec({
    ////////////////////////////////////////
    // toCffu
    ////////////////////////////////////////

    suspend fun checkToCffu(cffu: Cffu<Int>, n: Int) {
        cffu.await() shouldBe n

        cffu.unwrapMadeExecutor() shouldBeSameInstanceAs testExecutor
        cffu.cffuFactory() shouldBeSameInstanceAs testCffuFac

        val fac2 = CffuFactory.builder(testFjExecutor).build()
        cffu.resetCffuFactory(fac2).let {
            it.unwrapMadeExecutor() shouldBeSameInstanceAs testFjExecutor
            it.cffuFactory() shouldBeSameInstanceAs fac2
        }
    }

    test("toCffu for CompletableFuture") {
        val cf = CompletableFuture.completedFuture(n)
        checkToCffu(cf.toCffu(testCffuFac), n)
    }

    test("toCffu for CompletableFuture collection") {
        val range = 0 until 10
        val cfs: List<CompletableFuture<Int>> = range.map {
            CompletableFuture.completedFuture(it)
        }

        cfs.toCffu(testCffuFac).forEachIndexed { index, cffu ->
            checkToCffu(cffu, index)
        }
        cfs.toSet().toCffu(testCffuFac).forEachIndexed { index, cffu ->
            checkToCffu(cffu, index)
        }
    }

    test("toCffu for CompletableFuture array") {
        val cfArray: Array<CompletableFuture<Int>> = Array(10) { CompletableFuture.completedFuture(it) }
        cfArray.toCffu(testCffuFac).forEachIndexed { index, cffu -> checkToCffu(cffu, index) }

        val csArray: Array<CompletionStage<Int>> = Array(10) { CompletableFuture.completedFuture(it) }
        csArray.toCffu(testCffuFac).forEachIndexed { index, cffu -> checkToCffu(cffu, index) }
    }

    test("allOf*") {
        // collection

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        // FIXME: java.lang.ClassCastException if not providing the type parameter explicitly:
        //  class [Ljava.lang.Object; cannot be cast to class [Ljava.util.concurrent.CompletionStage;
        arrayOf<CompletionStage<Int>>(
            CompletableFuture.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allOfCffu(testCffuFac).await().shouldBeNull()

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allOfCffu().await().shouldBeNull()

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allOfCffu(testCffuFac).await().shouldBeNull()

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allOfCffu(testCffuFac).await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allOfCffu(testCffuFac).await().shouldBeNull()

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allOfCffu(testCffuFac).await().shouldBeNull()

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allOfCffu().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allOfCffu(testCffuFac).await().shouldBeNull()

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFastFailOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFastFailOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFac).await().shouldBeNull()

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFastFailOfCffu().await().shouldBeNull()

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFac).await().shouldBeNull()

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFac).await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFac).await().shouldBeNull()

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFac).await().shouldBeNull()

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFastFailOfCffu().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFac).await().shouldBeNull()
    }

    test("mostSuccessResultsOfCffu") {
        // collection

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFac).await() shouldBe listOf(-1, n)

        setOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(-1, n)

        listOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFac).await() shouldBe listOf(-1, n)

        // Array

        arrayOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFac).await() shouldBe listOf(-1, n)

        arrayOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(-1, n)

        arrayOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFac).await() shouldBe listOf(-1, n)

        // FIXME: java.lang.ClassCastException if not providing the type parameter explicitly:
        //  class [Ljava.lang.Object; cannot be cast to class [Ljava.util.concurrent.CompletionStage;
        arrayOf<CompletionStage<Int>>(CompletableFuture(), testCffuFac.completedFuture(n))
            .mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFac).await() shouldBe listOf(-1, n)
    }

    test("anyOf*") {
        // collection

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        setOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
            testCffuFac.newIncompleteCffu(),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        // Array

        arrayOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        arrayOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        setOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
            testCffuFac.newIncompleteCffu(),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        // Array

        arrayOf(
            testCffuFac.newIncompleteCffu<String>(),
            testCffuFac.newIncompleteCffu<Double>(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        arrayOf(
            testCffuFac.newIncompleteCffu<String>(),
            testCffuFac.newIncompleteCffu<Double>(),
            testCffuFac.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFac).await() shouldBe n

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        setOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
            testCffuFac.newIncompleteCffu(),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        // Array

        arrayOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        arrayOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        shouldThrow<RuntimeException> {
            arrayOf<CompletableFuture<Int>>().anySuccessOfCffu(testCffuFac).await()
        }.shouldBeTypeOf<NoCfsProvidedException>()

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        listOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        setOf(
            testCffuFac.newIncompleteCffu(),
            testCffuFac.completedFuture(n),
            testCffuFac.newIncompleteCffu(),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        // Array

        arrayOf(
            testCffuFac.newIncompleteCffu<String>(),
            testCffuFac.newIncompleteCffu<Double>(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n

        arrayOf(
            testCffuFac.newIncompleteCffu<String>(),
            testCffuFac.newIncompleteCffu<Double>(),
            testCffuFac.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFac).await() shouldBe n
    }

    val cffuFactoryForOptional = CffuFactory.builder(Executors.newCachedThreadPool()).build()

    fun assertCffuFactoryForOptional(cffu: Cffu<*>) {
        cffu.cffuFactory() shouldBeSameInstanceAs cffuFactoryForOptional
    }

    fun assertEmptyCollection(block: () -> Any?) {
        shouldThrow<IllegalArgumentException> { block() }
            .message shouldBe "no cffuFactory argument provided when this collection is empty"
    }

    fun assertEmptyArray(block: () -> Any?) {
        shouldThrow<IllegalArgumentException> { block() }
            .message shouldBe "no cffuFactory argument provided when this array is empty"
    }

    test("optional `cffuFactory`") {
        val cf1 = cffuFactoryForOptional.completedFuture(n)
        val cf2 = testCffuFac.completedFuture(n + 1)

        val emptyList: List<Cffu<Int>> = listOf()
        val list = listOf(cf1, cf2)
        val emptyArray: Array<Cffu<Int>> = arrayOf()
        val array = arrayOf(cf1, cf2)

        assertEmptyCollection { emptyList.allResultsOfCffu() }
        assertCffuFactoryForOptional(list.allResultsOfCffu())
        assertEmptyArray { emptyArray.allResultsOfCffu() }
        assertCffuFactoryForOptional(array.allResultsOfCffu())

        assertEmptyCollection { emptyList.allOfCffu() }
        assertCffuFactoryForOptional(list.allOfCffu())
        assertEmptyArray { emptyArray.allOfCffu() }
        assertCffuFactoryForOptional(array.allOfCffu())

        assertEmptyCollection { emptyList.allResultsFastFailOfCffu() }
        assertCffuFactoryForOptional(list.allResultsFastFailOfCffu())
        assertEmptyArray { emptyArray.allResultsFastFailOfCffu() }
        assertCffuFactoryForOptional(array.allResultsFastFailOfCffu())

        assertEmptyCollection { emptyList.allFastFailOfCffu() }
        assertCffuFactoryForOptional(list.allFastFailOfCffu())
        assertEmptyArray { emptyArray.allFastFailOfCffu() }
        assertCffuFactoryForOptional(array.allFastFailOfCffu())

        assertEmptyCollection { emptyList.mostSuccessResultsOfCffu(4, 1, TimeUnit.MILLISECONDS) }
        assertCffuFactoryForOptional(list.mostSuccessResultsOfCffu(4, 1, TimeUnit.MILLISECONDS))
        assertEmptyArray { emptyArray.mostSuccessResultsOfCffu(4, 1, TimeUnit.MILLISECONDS) }
        assertCffuFactoryForOptional(array.mostSuccessResultsOfCffu(4, 1, TimeUnit.MILLISECONDS))

        assertEmptyCollection { emptyList.anyOfCffu() }
        assertCffuFactoryForOptional(list.anyOfCffu())
        assertEmptyArray { emptyArray.anyOfCffu() }
        assertCffuFactoryForOptional(array.anyOfCffu())

        assertEmptyCollection { emptyList.anySuccessOfCffu() }
        assertCffuFactoryForOptional(list.anySuccessOfCffu())
        assertEmptyArray { emptyArray.anySuccessOfCffu() }
        assertCffuFactoryForOptional(array.anySuccessOfCffu())
    }

    ////////////////////////////////////////
    // - toCompletableFuture
    ////////////////////////////////////////

    test("toCompletableFuture for Cffu collection/array") {
        val range = 0 until 10
        val cfs: List<CompletableFuture<Int>> = range.map {
            CompletableFuture.completedFuture(it)
        }
        val cfArray = cfs.toTypedArray()
        val csArray: Array<CompletionStage<Int>> = Array(cfArray.size) { cfArray[it] }
        cfArray.javaClass shouldNotBe csArray.javaClass
        cfArray::class shouldNotBe csArray::class
        cfArray shouldBe csArray // shouldBe ignore the array type!

        val cffus: List<Cffu<Int>> = cfs.toCffu(testCffuFac)
        cffus.toCompletableFuture() shouldBe cfs
        cffus.toSet().toCompletableFuture() shouldBe cfs

        val cffuArray: Array<Cffu<Int>> = cffus.toTypedArray()
        cffuArray.toCompletableFuture().let {
            it shouldBe cfArray
            it shouldNotBeSameInstanceAs cfArray

            it.shouldBeTypeOf<Array<CompletableFuture<*>>>()
            it.shouldNotBeTypeOf<Array<CompletionStage<*>>>()
        }

        csArray.toCompletableFuture().let {
            it shouldBe cfArray
            it shouldNotBeSameInstanceAs cfArray

            it.shouldBeTypeOf<Array<CompletableFuture<*>>>()
            it.shouldNotBeTypeOf<Array<CompletionStage<*>>>()
        }
    }
})
