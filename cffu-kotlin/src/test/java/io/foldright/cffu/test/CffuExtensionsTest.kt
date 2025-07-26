package io.foldright.cffu.test

import io.foldright.cffu.BaseCffu
import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.kotlin.*
import io.foldright.test_utils.*
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainOnlyNulls
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


class CffuExtensionsTest : FunSpec({
    ////////////////////////////////////////
    // toCffu
    ////////////////////////////////////////

    suspend fun checkToCffu(cffu: Cffu<Int>, n: Int) {
        cffu.await() shouldBe n

        cffu.defaultExecutor() shouldBeSameInstanceAs testExecutor
        cffu.cffuFactory() shouldBeSameInstanceAs testCffuFac

        val fac2 = CffuFactory.builder(testFjExecutor).build()
        cffu.withCffuFactory(fac2).let {
            it.defaultExecutor() shouldBeSameInstanceAs testFjExecutor
            it.cffuFactory() shouldBeSameInstanceAs fac2
        }
    }

    test("toCffu for CompletableFuture") {
        val cf = CompletableFuture.completedFuture(n)
        checkToCffu(cf.toCffu(testCffuFac), n)
    }

    test("M*") {
        val supplier = Supplier {
            snoreZzz()
            n
        }

        // mSupply

        listOf(
            listOf(supplier, supplier).mSupplyFailFastAsyncCffu(testCffuFac),
            listOf(supplier, supplier).mSupplyFailFastAsyncCffu(testCffuFac, testExecutor),
            listOf(supplier, supplier).mSupplyAllSuccessAsyncCffu(testCffuFac, anotherN),
            listOf(supplier, supplier).mSupplyAllSuccessAsyncCffu(testCffuFac, anotherN, testExecutor),
            listOf(supplier, supplier).mSupplyMostSuccessAsyncCffu(
                testCffuFac,
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS
            ),
            listOf(supplier, supplier).mSupplyMostSuccessAsyncCffu(
                testCffuFac,
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS, testExecutor
            ),
            listOf(supplier, supplier).mSupplyAsyncCffu(testCffuFac),
            listOf(supplier, supplier).mSupplyAsyncCffu(testCffuFac, testExecutor),
        ).map { it.await() }.shouldForAll { it.shouldContainExactly(n, n) }

        arrayOf(
            arrayOf(supplier, supplier).mSupplyFailFastAsyncCffu(testCffuFac),
            arrayOf(supplier, supplier).mSupplyFailFastAsyncCffu(testCffuFac, testExecutor),
            arrayOf(supplier, supplier).mSupplyAllSuccessAsyncCffu(testCffuFac, anotherN),
            arrayOf(supplier, supplier).mSupplyAllSuccessAsyncCffu(testCffuFac, anotherN, testExecutor),
            arrayOf(supplier, supplier).mSupplyMostSuccessAsyncCffu(
                testCffuFac,
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS
            ),
            arrayOf(supplier, supplier).mSupplyMostSuccessAsyncCffu(
                testCffuFac,
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS, testExecutor
            ),
            arrayOf(supplier, supplier).mSupplyAsyncCffu(testCffuFac),
            arrayOf(supplier, supplier).mSupplyAsyncCffu(testCffuFac, testExecutor),
        ).map { it.await() }.shouldForAll { it.shouldContainExactly(n, n) }

        listOf(
            listOf(supplier, supplier).mSupplyAnySuccessAsyncCffu(testCffuFac),
            listOf(supplier, supplier).mSupplyAnySuccessAsyncCffu(testCffuFac, testExecutor),
            listOf(supplier, supplier).mSupplyAnyAsyncCffu(testCffuFac),
            listOf(supplier, supplier).mSupplyAnyAsyncCffu(testCffuFac, testExecutor),
        ).map { it.await() }.shouldForAll { it shouldBe n }

        arrayOf(
            arrayOf(supplier, supplier).mSupplyAnySuccessAsyncCffu(testCffuFac),
            arrayOf(supplier, supplier).mSupplyAnySuccessAsyncCffu(testCffuFac, testExecutor),
            arrayOf(supplier, supplier).mSupplyAnyAsyncCffu(testCffuFac),
            arrayOf(supplier, supplier).mSupplyAnyAsyncCffu(testCffuFac, testExecutor),
        ).map { it.await() }.shouldForAll { it shouldBe n }

        // mRun

        val runnable = Runnable { snoreZzz() }

        listOf(
            listOf(runnable, runnable).mRunFailFastAsyncCffu(testCffuFac),
            listOf(runnable, runnable).mRunFailFastAsyncCffu(testCffuFac, testExecutor),
            listOf(runnable, runnable).mRunAsyncCffu(testCffuFac),
            listOf(runnable, runnable).mRunAsyncCffu(testCffuFac, testExecutor),
            listOf(runnable, runnable).mRunAnySuccessAsyncCffu(testCffuFac),
            listOf(runnable, runnable).mRunAnySuccessAsyncCffu(testCffuFac, testExecutor),
            listOf(runnable, runnable).mRunAnyAsyncCffu(testCffuFac),
            listOf(runnable, runnable).mRunAnyAsyncCffu(testCffuFac, testExecutor),
        ).map { it.await() }.shouldContainOnlyNulls()

        arrayOf(
            arrayOf(runnable, runnable).mRunFailFastAsyncCffu(testCffuFac),
            arrayOf(runnable, runnable).mRunFailFastAsyncCffu(testCffuFac, testExecutor),
            arrayOf(runnable, runnable).mRunAsyncCffu(testCffuFac),
            arrayOf(runnable, runnable).mRunAsyncCffu(testCffuFac, testExecutor),
            arrayOf(runnable, runnable).mRunAnySuccessAsyncCffu(testCffuFac),
            arrayOf(runnable, runnable).mRunAnySuccessAsyncCffu(testCffuFac, testExecutor),
            arrayOf(runnable, runnable).mRunAnyAsyncCffu(testCffuFac),
            arrayOf(runnable, runnable).mRunAnyAsyncCffu(testCffuFac, testExecutor),
        ).map { it.await() }.shouldContainOnlyNulls()
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
        shouldThrowExactly<IllegalArgumentException> { listOf<Cffu<Int>>().allResultsOfCffu() }
            .message shouldBe "no cffuFactory argument provided when this collection is empty"

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
        shouldThrowExactly<IllegalArgumentException> { arrayOf<Cffu<Int>>().allResultsOfCffu() }
            .message shouldBe "no cffuFactory argument provided when this array is empty"

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
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        // FIXME: java.lang.ClassCastException if not providing the type parameter explicitly:
        //  class [Ljava.lang.Object; cannot be cast to class [Ljava.util.concurrent.CompletionStage;
        arrayOf<CompletionStage<Int>>(
            CompletableFuture.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allSuccessResultsOfCffu(anotherN, testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

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
        ).allResultsFailFastOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFailFastOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFailFastOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFailFastOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFailFastOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFailFastOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allResultsFailFastOfCffu().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allResultsFailFastOfCffu(testCffuFac).await() shouldBe listOf(n, n + 1, n + 2)

        ////////////////////////////////////////

        // collection

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFailFastOfCffu(testCffuFac).await().shouldBeNull()

        listOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFailFastOfCffu().await().shouldBeNull()

        setOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFailFastOfCffu(testCffuFac).await().shouldBeNull()

        listOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFailFastOfCffu(testCffuFac).await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFailFastOfCffu(testCffuFac).await().shouldBeNull()

        // Array

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFailFastOfCffu(testCffuFac).await().shouldBeNull()

        arrayOf(
            testCffuFac.completedFuture(n),
            testCffuFac.completedFuture(n + 1),
            testCffuFac.completedFuture(n + 2),
        ).allFailFastOfCffu().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(n + 1),
            CompletableFuture.completedFuture(n + 2),
        ).allFailFastOfCffu(testCffuFac).await().shouldBeNull()
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

        shouldThrowExactly<NoCfsProvidedException> {
            arrayOf<CompletableFuture<Int>>().anySuccessOfCffu(testCffuFac).await()
        }

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

    fun assertCffuFactoryForOptional(cffu: BaseCffu<*, *>) {
        cffu.cffuFactory() shouldBeSameInstanceAs cffuFactoryForOptional
    }

    fun assertEmptyCollection(block: () -> Any?) {
        shouldThrowExactly<IllegalArgumentException> { block() }
            .message shouldBe "no cffuFactory argument provided when this collection is empty"
    }

    fun assertEmptyArray(block: () -> Any?) {
        shouldThrowExactly<IllegalArgumentException> { block() }
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

        assertEmptyCollection { emptyList.allResultsFailFastOfCffu() }
        assertCffuFactoryForOptional(list.allResultsFailFastOfCffu())
        assertEmptyArray { emptyArray.allResultsFailFastOfCffu() }
        assertCffuFactoryForOptional(array.allResultsFailFastOfCffu())

        assertEmptyCollection { emptyList.allFailFastOfCffu() }
        assertCffuFactoryForOptional(list.allFailFastOfCffu())
        assertEmptyArray { emptyArray.allFailFastOfCffu() }
        assertCffuFactoryForOptional(array.allFailFastOfCffu())

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
})
