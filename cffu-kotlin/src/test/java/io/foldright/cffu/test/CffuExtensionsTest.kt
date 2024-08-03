package io.foldright.cffu.test

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.kotlin.*
import io.foldright.cffu.unwrapMadeExecutor
import io.foldright.test_utils.n
import io.foldright.test_utils.testCffuFactory
import io.foldright.test_utils.testForkJoinPoolExecutor
import io.foldright.test_utils.testThreadPoolExecutor
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

        cffu.unwrapMadeExecutor() shouldBeSameInstanceAs testThreadPoolExecutor
        cffu.cffuFactory() shouldBeSameInstanceAs testCffuFactory

        val fac2 = CffuFactory.builder(testForkJoinPoolExecutor).build()
        cffu.resetCffuFactory(fac2).let {
            it.unwrapMadeExecutor() shouldBeSameInstanceAs testForkJoinPoolExecutor
            it.cffuFactory() shouldBeSameInstanceAs fac2
        }
    }

    test("toCffu for CompletableFuture") {
        val cf = CompletableFuture.completedFuture(n)
        checkToCffu(cf.toCffu(testCffuFactory), n)
    }

    test("toCffu for CompletableFuture collection") {
        val range = 0 until 10
        val cfs: List<CompletableFuture<Int>> = range.map {
            CompletableFuture.completedFuture(it)
        }

        cfs.toCffu(testCffuFactory).forEachIndexed { index, cffu ->
            checkToCffu(cffu, index)
        }
        cfs.toSet().toCffu(testCffuFactory).forEachIndexed { index, cffu ->
            checkToCffu(cffu, index)
        }
    }

    test("toCffu for CompletableFuture array") {
        val cfArray: Array<CompletableFuture<Int>> = Array(10) { CompletableFuture.completedFuture(it) }
        cfArray.toCffu(testCffuFactory).forEachIndexed { index, cffu -> checkToCffu(cffu, index) }

        val csArray: Array<CompletionStage<Int>> = Array(10) { CompletableFuture.completedFuture(it) }
        csArray.toCffu(testCffuFactory).forEachIndexed { index, cffu -> checkToCffu(cffu, index) }
    }

    test("allOf*") {
        // collection

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        // Array

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        // FIXME: java.lang.ClassCastException if not providing the type parameter explicitly:
        //  class [Ljava.lang.Object; cannot be cast to class [Ljava.util.concurrent.CompletionStage;
        arrayOf<CompletionStage<Int>>(
            CompletableFuture.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allOfCffu(testCffuFactory).await().shouldBeNull()

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allOfCffu().await().shouldBeNull()

        setOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allOfCffu(testCffuFactory).await().shouldBeNull()

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allOfCffu(testCffuFactory).await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allOfCffu(testCffuFactory).await().shouldBeNull()

        // Array

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allOfCffu(testCffuFactory).await().shouldBeNull()

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allOfCffu().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allOfCffu(testCffuFactory).await().shouldBeNull()

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsFastFailOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        // Array

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allResultsFastFailOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFastFailOfCffu(testCffuFactory).await() shouldBe listOf(n, n + 1, n + 2)

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFactory).await().shouldBeNull()

        listOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allFastFailOfCffu().await().shouldBeNull()

        setOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFactory).await().shouldBeNull()

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFactory).await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFactory).await().shouldBeNull()

        // Array

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFactory).await().shouldBeNull()

        arrayOf(
            testCffuFactory.completedFuture(n),
            testCffuFactory.completedFuture(n + 1),
            testCffuFactory.completedFuture(n + 2),
        ).allFastFailOfCffu().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFastFailOfCffu(testCffuFactory).await().shouldBeNull()
    }

    test("mostSuccessResultsOfCffu") {
        // collection

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFactory).await() shouldBe listOf(-1, n)

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(-1, n)

        listOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFactory).await() shouldBe listOf(-1, n)

        // Array

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFactory).await() shouldBe listOf(-1, n)

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(-1, n)

        arrayOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFactory).await() shouldBe listOf(-1, n)

        // FIXME: java.lang.ClassCastException if not providing the type parameter explicitly:
        //  class [Ljava.lang.Object; cannot be cast to class [Ljava.util.concurrent.CompletionStage;
        arrayOf<CompletionStage<Int>>(CompletableFuture(), testCffuFactory.completedFuture(n))
            .mostSuccessResultsOfCffu(-1, 10, TimeUnit.MILLISECONDS, testCffuFactory).await() shouldBe listOf(-1, n)
    }

    test("anyOf*") {
        // collection

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
            testCffuFactory.newIncompleteCffu(),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        // Array

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
            testCffuFactory.newIncompleteCffu(),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        // Array

        arrayOf(
            testCffuFactory.newIncompleteCffu<String>(),
            testCffuFactory.newIncompleteCffu<Double>(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        arrayOf(
            testCffuFactory.newIncompleteCffu<String>(),
            testCffuFactory.newIncompleteCffu<Double>(),
            testCffuFactory.completedFuture(n),
        ).anyOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(n),
        ).anyOfCffu(testCffuFactory).await() shouldBe n

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
            testCffuFactory.newIncompleteCffu(),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        // Array

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        shouldThrow<RuntimeException> {
            arrayOf<CompletableFuture<Int>>().anySuccessOfCffu(testCffuFactory).await()
        }.shouldBeTypeOf<NoCfsProvidedException>()

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(n),
            testCffuFactory.newIncompleteCffu(),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(n),
            CompletableFuture(),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        // Array

        arrayOf(
            testCffuFactory.newIncompleteCffu<String>(),
            testCffuFactory.newIncompleteCffu<Double>(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n

        arrayOf(
            testCffuFactory.newIncompleteCffu<String>(),
            testCffuFactory.newIncompleteCffu<Double>(),
            testCffuFactory.completedFuture(n),
        ).anySuccessOfCffu().await() shouldBe n

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(n),
        ).anySuccessOfCffu(testCffuFactory).await() shouldBe n
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
        val cf2 = testCffuFactory.completedFuture(n + 1)

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

        val cffus: List<Cffu<Int>> = cfs.toCffu(testCffuFactory)
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
