package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
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

class CffuExtensionsTest : FunSpec({
    val n = 42
    val anotherN = 4242
    val s = "43"
    val d = 44.0

    ////////////////////////////////////////////////////////////////////////////////
    //# Extension methods for CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    // - allOfCompletableFuture
    // - anyOfCompletableFuture
    ////////////////////////////////////////

    test("allOfCompletableFuture / anyOfCompletableFuture - collection") {
        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCompletableFuture().await() shouldBe listOf(42, 43, 44)

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCompletableFuture().await() shouldBe listOf(42, 43, 44)

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfCompletableFutureAny().await() shouldBe 42
    }

    test("allOfCompletableFuture / anyOfCompletableFuture - array") {
        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCompletableFuture().await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42
    }

    ////////////////////////////////////////
    // - allOfCompletableFutureVoid
    // - anyOfCompletableFutureAny
    ////////////////////////////////////////

    test("allOfCompletableFutureVoid / anyOfCompletableFutureAny - collection") {
        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFutureVoid().await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFutureVoid().await().shouldBeNull()

        listOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFutureAny().await() shouldBe 42

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFutureAny().await() shouldBe 42
    }

    test("allOfCompletableFutureVoid / anyOfCompletableFutureAny - array") {
        // FIXME: Need Type arguments for array...

        arrayOf<CompletableFuture<*>>(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFutureVoid().await().shouldBeNull()

        arrayOf<CompletableFuture<*>>(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFutureAny().await() shouldBe 42
    }

    ////////////////////////////////////////
    // - anyOfSuccessCompletableFuture
    ////////////////////////////////////////

    test("anyOfSuccessCompletableFuture - collection") {
        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42

        shouldThrow<RuntimeException> {
            listOf<CompletableFuture<Int>>().anyOfSuccessCompletableFuture().await()
        }.shouldBeTypeOf<NoCfsProvidedException>()
    }

    test("anyOfSuccessCompletableFuture - array") {
        arrayOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42

        shouldThrow<RuntimeException> {
            arrayOf<CompletableFuture<Int>>().anyOfSuccessCompletableFuture().await()
        }.shouldBeTypeOf<NoCfsProvidedException>()
    }

    ////////////////////////////////////////
    // combine
    ////////////////////////////////////////

    test("combine - CompletableFuture") {
        CompletableFuture.completedFuture(n).combine(
            CompletableFuture.completedFuture(s)
        ).get() shouldBe Tuple2.of(n, s)

        CompletableFuture.completedFuture(n).combine(
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d)
        ).get() shouldBe Tuple3.of(n, s, d)

        CompletableFuture.completedFuture(n).combine(
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d),
            CompletableFuture.completedFuture(anotherN)
        ).get() shouldBe Tuple4.of(n, s, d, anotherN)

        CompletableFuture.completedFuture(n).combine(
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d),
            CompletableFuture.completedFuture(anotherN),
            CompletableFuture.completedFuture(n + n)
        ).get() shouldBe Tuple5.of(n, s, d, anotherN, n + n)
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Extension methods for Cffu
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    // asCffu
    ////////////////////////////////////////

    suspend fun checkAsCffu(cffu: Cffu<Int>, n: Int) {
        cffu.await() shouldBe n

        cffu.defaultExecutor() shouldBeSameInstanceAs testThreadPoolExecutor
        cffu.cffuFactory() shouldBeSameInstanceAs testCffuFactory

        val fac2 = newCffuFactoryBuilder(testForkJoinPoolExecutor).build()
        cffu.resetCffuFactory(fac2).let {
            it.defaultExecutor() shouldBeSameInstanceAs testForkJoinPoolExecutor
            it.cffuFactory() shouldBeSameInstanceAs fac2
        }
    }

    test("asCffu for CompletableFuture") {
        val cf = CompletableFuture.completedFuture(n)
        checkAsCffu(cf.asCffu(testCffuFactory), n)
    }

    test("asCffu for CompletableFuture collection") {
        val range = 0 until 10
        val cfs: List<CompletableFuture<Int>> = range.map {
            CompletableFuture.completedFuture(it)
        }

        cfs.asCffu(testCffuFactory).forEachIndexed { index, cffu ->
            checkAsCffu(cffu, index)
        }
        cfs.toSet().asCffu(testCffuFactory).forEachIndexed { index, cffu ->
            checkAsCffu(cffu, index)
        }
    }

    test("asCffu for CompletableFuture array") {
        val cfArray: Array<CompletableFuture<Int>> = Array(10) { CompletableFuture.completedFuture(it) }
        cfArray.asCffu(testCffuFactory).forEachIndexed { index, cffu ->
            checkAsCffu(cffu, index)
        }

        val csArray: Array<CompletionStage<Int>> = Array(10) { CompletableFuture.completedFuture(it) }
        csArray.asCffu(testCffuFactory).forEachIndexed { index, cffu ->
            checkAsCffu(cffu, index)
        }
    }

    ////////////////////////////////////////
    // - allOfCffu
    // - anyOfCffu
    ////////////////////////////////////////

    test("allOfCffu/anyOfCffu for collection") {
        listOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).allOfCffu(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        setOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).allOfCffu(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCffu(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCffu(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).anyOfCffu(testCffuFactory).await() shouldBe 42

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
            testCffuFactory.newIncompleteCffu(),
        ).anyOfCffu(testCffuFactory).await() shouldBe 42

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCffu(testCffuFactory).await() shouldBe 42

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfCffu(testCffuFactory).await() shouldBe 42
    }

    test("allOfCffu/anyOfCffu for array") {
        arrayOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).allOfCffu(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCffu(testCffuFactory).await() shouldBe listOf(42, 43, 44)

        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).anyOfCffu(testCffuFactory).await() shouldBe 42

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCffu(testCffuFactory).await() shouldBe 42
    }

    ////////////////////////////////////////
    // - allOfCffuVoid
    // - anyOfCffuAny
    ////////////////////////////////////////

    test("allOfCffuVoid/anyOfCffuAny for collection") {
        listOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).allOfCffuVoid(testCffuFactory).await().shouldBeNull()

        setOf(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).allOfCffuVoid(testCffuFactory).await().shouldBeNull()

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCffuVoid(testCffuFactory).await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCffuVoid(testCffuFactory).await().shouldBeNull()

        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).anyOfCffuAny(testCffuFactory).await() shouldBe 42

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
            testCffuFactory.newIncompleteCffu(),
        ).anyOfCffuAny(testCffuFactory).await() shouldBe 42

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCffuAny(testCffuFactory).await() shouldBe 42

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfCffuAny(testCffuFactory).await() shouldBe 42
    }

    test("allOfCffuVoid/anyOfCffuAny for array") {
        // FIXME: Need Type arguments for array...

        arrayOf<Cffu<*>>(
            testCffuFactory.completedFuture(42),
            testCffuFactory.completedFuture(43),
            testCffuFactory.completedFuture(44),
        ).allOfCffuVoid(testCffuFactory).await().shouldBeNull()

        arrayOf<CompletableFuture<*>>(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCffuVoid(testCffuFactory).await().shouldBeNull()

        arrayOf<Cffu<*>>(
            testCffuFactory.newIncompleteCffu<String>(),
            testCffuFactory.newIncompleteCffu<Double>(),
            testCffuFactory.completedFuture(42),
        ).anyOfCffuAny(testCffuFactory).await() shouldBe 42

        arrayOf<CompletableFuture<*>>(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCffuAny(testCffuFactory).await() shouldBe 42
    }

    ////////////////////////////////////////
    // - anyOfSuccessCffu
    ////////////////////////////////////////

    test("anyOfSuccessCffu for collection") {
        listOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).anyOfSuccessCffu(testCffuFactory).await() shouldBe 42

        setOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
            testCffuFactory.newIncompleteCffu(),
        ).anyOfSuccessCffu(testCffuFactory).await() shouldBe 42

        shouldThrow<RuntimeException> {
            listOf<Cffu<Int>>().anyOfSuccessCffu(testCffuFactory).await()
        }.shouldBeTypeOf<NoCfsProvidedException>()

        //////////////////////////////////////////////////////////////////////////////

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCffu(testCffuFactory).await() shouldBe 42

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfSuccessCffu(testCffuFactory).await() shouldBe 42

        shouldThrow<RuntimeException> {
            listOf<CompletableFuture<Int>>().anyOfSuccessCffu(testCffuFactory).await()
        }.shouldBeTypeOf<NoCfsProvidedException>()
    }

    test("anyOfSuccessCffu for array") {
        arrayOf(
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.newIncompleteCffu(),
            testCffuFactory.completedFuture(42),
        ).anyOfSuccessCffu(testCffuFactory).await() shouldBe 42

        shouldThrow<RuntimeException> {
            arrayOf<Cffu<Int>>().anyOfSuccessCffu(testCffuFactory).await()
        }.shouldBeTypeOf<NoCfsProvidedException>()

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCffu(testCffuFactory).await() shouldBe 42

        shouldThrow<RuntimeException> {
            arrayOf<CompletableFuture<Int>>().anyOfSuccessCffu(testCffuFactory).await()
        }.shouldBeTypeOf<NoCfsProvidedException>()
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

        val cffus: List<Cffu<Int>> = cfs.asCffu(testCffuFactory)
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

    ////////////////////////////////////////
    // cffuUnwrap
    ////////////////////////////////////////

    test("cffuUnwrap for Cffu collection/array") {
        val range = 0 until 10
        val cfs: List<CompletableFuture<Int>> = range.map {
            CompletableFuture.completedFuture(it)
        }
        val cfArray = cfs.toTypedArray()

        val cffus: List<Cffu<Int>> = cfs.asCffu(testCffuFactory)
        cffus.cffuUnwrap() shouldBe cfs
        cffus.toSet().cffuUnwrap() shouldBe cfs

        val cffuArray: Array<Cffu<Int>> = cffus.toTypedArray()
        cffuArray.cffuUnwrap() shouldBe cfArray
    }
})
