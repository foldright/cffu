package io.foldright.cffu.test

import io.foldright.cffu.CffuState
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.assertIsCfDefaultExecutor
import io.foldright.cffu.kotlin.*
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import io.foldright.test_utils.*
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainOnlyNulls
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Function.identity
import java.util.function.Supplier

@Suppress("LocalVariableName")
class CompletableFutureExtensionsTest : FunSpec({
    test("M*") {
        val supplier = Supplier {
            snoreZzz()
            n
        }

        // mSupply

        listOf(
            listOf(supplier, supplier).mSupplyFastFailAsyncCompletableFuture(),
            listOf(supplier, supplier).mSupplyFastFailAsyncCompletableFuture(testExecutor),
            listOf(supplier, supplier).mSupplyAllSuccessAsyncCompletableFuture(anotherN),
            listOf(supplier, supplier).mSupplyAllSuccessAsyncCompletableFuture(anotherN, testExecutor),
            listOf(supplier, supplier).mSupplyMostSuccessAsyncCompletableFuture(
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS
            ),
            listOf(supplier, supplier).mSupplyMostSuccessAsyncCompletableFuture(
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS, testExecutor
            ),
            listOf(supplier, supplier).mSupplyAsyncCompletableFuture(),
            listOf(supplier, supplier).mSupplyAsyncCompletableFuture(testExecutor),
        ).map { it.await() }.shouldForAll { it.shouldContainExactly(n, n) }

        arrayOf(
            arrayOf(supplier, supplier).mSupplyFastFailAsyncCompletableFuture(),
            arrayOf(supplier, supplier).mSupplyFastFailAsyncCompletableFuture(testExecutor),
            arrayOf(supplier, supplier).mSupplyAllSuccessAsyncCompletableFuture(anotherN),
            arrayOf(supplier, supplier).mSupplyAllSuccessAsyncCompletableFuture(anotherN, testExecutor),
            arrayOf(supplier, supplier).mSupplyMostSuccessAsyncCompletableFuture(
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS
            ),
            arrayOf(supplier, supplier).mSupplyMostSuccessAsyncCompletableFuture(
                anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS, testExecutor
            ),
            arrayOf(supplier, supplier).mSupplyAsyncCompletableFuture(),
            arrayOf(supplier, supplier).mSupplyAsyncCompletableFuture(testExecutor),
        ).map { it.await() }.shouldForAll { it.shouldContainExactly(n, n) }

        listOf(
            listOf(supplier, supplier).mSupplyAnySuccessAsyncCompletableFuture(),
            listOf(supplier, supplier).mSupplyAnySuccessAsyncCompletableFuture(testExecutor),
            listOf(supplier, supplier).mSupplyAnyAsyncCompletableFuture(),
            listOf(supplier, supplier).mSupplyAnyAsyncCompletableFuture(testExecutor),
        ).map { it.await() }.shouldForAll { it shouldBe n }

        arrayOf(
            arrayOf(supplier, supplier).mSupplyAnySuccessAsyncCompletableFuture(),
            arrayOf(supplier, supplier).mSupplyAnySuccessAsyncCompletableFuture(testExecutor),
            arrayOf(supplier, supplier).mSupplyAnyAsyncCompletableFuture(),
            arrayOf(supplier, supplier).mSupplyAnyAsyncCompletableFuture(testExecutor),
        ).map { it.await() }.shouldForAll { it shouldBe n }

        // mRun

        val runnable = Runnable { snoreZzz() }

        listOf(
            listOf(runnable, runnable).mRunFastFailAsyncCompletableFuture(),
            listOf(runnable, runnable).mRunFastFailAsyncCompletableFuture(testExecutor),
            listOf(runnable, runnable).mRunAsyncCompletableFuture(),
            listOf(runnable, runnable).mRunAsyncCompletableFuture(testExecutor),
            listOf(runnable, runnable).mRunAnySuccessAsyncCompletableFuture(),
            listOf(runnable, runnable).mRunAnySuccessAsyncCompletableFuture(testExecutor),
            listOf(runnable, runnable).mRunAnyAsyncCompletableFuture(),
            listOf(runnable, runnable).mRunAnyAsyncCompletableFuture(testExecutor),
        ).map { it.await() }.shouldContainOnlyNulls()

        arrayOf(
            arrayOf(runnable, runnable).mRunFastFailAsyncCompletableFuture(),
            arrayOf(runnable, runnable).mRunFastFailAsyncCompletableFuture(testExecutor),
            arrayOf(runnable, runnable).mRunAsyncCompletableFuture(),
            arrayOf(runnable, runnable).mRunAsyncCompletableFuture(testExecutor),
            arrayOf(runnable, runnable).mRunAnySuccessAsyncCompletableFuture(),
            arrayOf(runnable, runnable).mRunAnySuccessAsyncCompletableFuture(testExecutor),
            arrayOf(runnable, runnable).mRunAnyAsyncCompletableFuture(),
            arrayOf(runnable, runnable).mRunAnyAsyncCompletableFuture(testExecutor),
        ).map { it.await() }.shouldContainOnlyNulls()
    }

    test("allOf*") {
        listOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allResultsOfCompletableFuture().await() shouldBe listOf(n, n + 1, n + 2)
        listOf<CompletableFuture<Int>>().allResultsOfCompletableFuture().await() shouldBe emptyList()

        setOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allResultsOfCompletableFuture().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allResultsOfCompletableFuture().await() shouldBe listOf(n, n + 1, n + 2)
        arrayOf<CompletableFuture<Int>>().allResultsOfCompletableFuture().await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            completedFuture(n),
            completedFuture(s),
            completedFuture(d),
        ).allOfCompletableFuture().await().shouldBeNull()
        listOf<CompletableFuture<Int>>().allOfCompletableFuture().await().shouldBeNull()

        setOf(
            completedFuture(n),
            completedFuture(s),
            completedFuture(d),
        ).allOfCompletableFuture().await().shouldBeNull()

        arrayOf(
            completedFuture(n),
            completedFuture(s),
            completedFuture(d),
        ).allOfCompletableFuture().await().shouldBeNull()
        arrayOf<CompletableFuture<*>>().allOfCompletableFuture().await().shouldBeNull()

        ////////////////////////////////////////

        listOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allResultsFastFailOfCompletableFuture().await() shouldBe listOf(n, n + 1, n + 2)
        listOf<CompletableFuture<Int>>().allResultsFastFailOfCompletableFuture().await() shouldBe emptyList()

        setOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allResultsFastFailOfCompletableFuture().await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allResultsFastFailOfCompletableFuture().await() shouldBe listOf(n, n + 1, n + 2)
        arrayOf<CompletableFuture<Int>>().allResultsFastFailOfCompletableFuture().await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allSuccessResultsOfCompletableFuture(anotherN).await() shouldBe listOf(n, n + 1, n + 2)
        listOf<CompletableFuture<Int>>().allSuccessResultsOfCompletableFuture(anotherN).await() shouldBe emptyList()

        setOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allSuccessResultsOfCompletableFuture(anotherN).await() shouldBe listOf(n, n + 1, n + 2)

        arrayOf(
            completedFuture(n),
            completedFuture(n + 1),
            completedFuture(n + 2),
        ).allSuccessResultsOfCompletableFuture(anotherN).await() shouldBe listOf(n, n + 1, n + 2)
        arrayOf<CompletableFuture<Int>>().allSuccessResultsOfCompletableFuture(anotherN).await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            completedFuture(n),
            completedFuture(s),
            completedFuture(d),
        ).allFastFailOfCompletableFuture().await().shouldBeNull()
        listOf<CompletableFuture<Int>>().allFastFailOfCompletableFuture().await().shouldBeNull()

        setOf(
            completedFuture(n),
            completedFuture(s),
            completedFuture(d),
        ).allFastFailOfCompletableFuture().await().shouldBeNull()

        arrayOf(
            completedFuture(n),
            completedFuture(s),
            completedFuture(d),
        ).allFastFailOfCompletableFuture().await().shouldBeNull()
        arrayOf<CompletableFuture<*>>().allFastFailOfCompletableFuture().await().shouldBeNull()
    }

    test("mostSuccessResultsOfCompletableFuture") {
        listOf(CompletableFuture(), completedFuture(n))
            .mostSuccessResultsOfCompletableFuture(null, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(null, n)
        listOf(CompletableFuture(), completedFuture(n))
            .mostSuccessResultsOfCompletableFuture(null, testExecutor, 10, TimeUnit.MILLISECONDS)
            .await() shouldBe listOf(null, n)

        arrayOf(CompletableFuture(), completedFuture(n))
            .mostSuccessResultsOfCompletableFuture(null, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(null, n)
        arrayOf(CompletableFuture(), completedFuture(n))
            .mostSuccessResultsOfCompletableFuture(null, testExecutor, 10, TimeUnit.MILLISECONDS)
            .await() shouldBe listOf(null, n)
    }

    test("anyOf*") {
        listOf(
            CompletableFuture(),
            CompletableFuture(),
            completedFuture(n),
        ).anyOfCompletableFuture().await() shouldBe n
        listOf<CompletableFuture<Int>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        setOf(
            CompletableFuture(),
            CompletableFuture(),
            completedFuture(n),
        ).anyOfCompletableFuture().await() shouldBe n

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            completedFuture(n),
        ).anyOfCompletableFuture().await() shouldBe n
        arrayOf<CompletableFuture<Int>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        ////////////////////////////////////////

        listOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            completedFuture(n),
        ).anyOfCompletableFuture().await() shouldBe n
        listOf<CompletableFuture<*>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            completedFuture(n),
        ).anyOfCompletableFuture().await() shouldBe n

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            completedFuture(n),
        ).anyOfCompletableFuture().await() shouldBe n
        arrayOf<CompletableFuture<*>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        ////////////////////////////////////////

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            completedFuture(n),
        ).anySuccessOfCompletableFuture().await() shouldBe n
        shouldThrowExactly<NoCfsProvidedException> {
            listOf<CompletableFuture<Int>>().anySuccessOfCompletableFuture().await()
        }

        setOf(
            CompletableFuture(),
            completedFuture(n),
            CompletableFuture(),
        ).anySuccessOfCompletableFuture().await() shouldBe n

        arrayOf(
            CompletableFuture(),
            completedFuture(n),
            CompletableFuture(),
        ).anySuccessOfCompletableFuture().await() shouldBe n
        shouldThrowExactly<NoCfsProvidedException> {
            arrayOf<CompletableFuture<Int>>().anySuccessOfCompletableFuture().await()
        }

        ////////////////////////////////////////

        listOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            completedFuture(n),
        ).anySuccessOfCompletableFuture().await() shouldBe n
        shouldThrowExactly<NoCfsProvidedException> {
            listOf<CompletableFuture<*>>().anySuccessOfCompletableFuture().await()
        }

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            completedFuture(n),
        ).anySuccessOfCompletableFuture().await() shouldBe n

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            completedFuture(n),
        ).anySuccessOfCompletableFuture().await() shouldBe n
        shouldThrowExactly<NoCfsProvidedException> {
            arrayOf<CompletableFuture<*>>().anySuccessOfCompletableFuture().await()
        }
    }

    ////////////////////////////////////////////////////////////
    // Then-Multi-Actions(thenM*) Methods
    ////////////////////////////////////////////////////////////

    test("thenM*") {
        val completed = completedFuture(n)

        val runnable = Runnable { snoreZzz() }
        arrayOf(
            completed.thenMRunAsync(runnable, runnable),
            completed.thenMRunAsync(testExecutor, runnable, runnable),
            completed.thenMRunFastFailAsync(runnable, runnable),
            completed.thenMRunFastFailAsync(testExecutor, runnable, runnable),
            completed.thenMRunAnySuccessAsync(runnable, runnable),
            completed.thenMRunAnySuccessAsync(testExecutor, runnable, runnable),
            completed.thenMRunAnyAsync(runnable, runnable),
            completed.thenMRunAnyAsync(testExecutor, runnable, runnable),
        ).map { it.await() }.shouldContainOnlyNulls()

        val consumer = Consumer { x: Int ->
            x shouldBe n
            snoreZzz()
        }
        arrayOf(
            completed.thenMAcceptAsync(consumer, consumer),
            completed.thenMAcceptAsync(testExecutor, consumer, consumer),
            completed.thenMAcceptFastFailAsync(consumer, consumer),
            completed.thenMAcceptFastFailAsync(testExecutor, consumer, consumer),
            completed.thenMAcceptAnySuccessAsync(consumer, consumer),
            completed.thenMAcceptAnySuccessAsync(testExecutor, consumer, consumer),
            completed.thenMAcceptAnyAsync(consumer, consumer),
            completed.thenMAcceptAnyAsync(testExecutor, consumer, consumer),
        ).map { it.await() }.shouldContainOnlyNulls()

        val supplier = Function { x: Int ->
            x shouldBe n
            snoreZzz()
            x + n
        }
        arrayOf(
            completed.thenMApplyFastFailAsync(supplier, supplier),
            completed.thenMApplyFastFailAsync(testExecutor, supplier, supplier),
            completed.thenMApplyAllSuccessAsync(anotherN, supplier, supplier),
            completed.thenMApplyAllSuccessAsync(anotherN, testExecutor, supplier, supplier),
            completed.thenMApplyMostSuccessAsync(anotherN, LONG_WAIT_MS, TimeUnit.MILLISECONDS, supplier, supplier),
            completed.thenMApplyMostSuccessAsync(
                anotherN, testExecutor, LONG_WAIT_MS, TimeUnit.MILLISECONDS, supplier, supplier
            ),
            completed.thenMApplyAsync(supplier, supplier),
            completed.thenMApplyAsync(testExecutor, supplier, supplier),
        ).map { it.await() }.shouldForAll { it.shouldContainExactly(n + n, n + n) }

        arrayOf(
            completed.thenMApplyAnySuccessAsync(supplier, supplier),
            completed.thenMApplyAnySuccessAsync(testExecutor, supplier, supplier),
            completed.thenMApplyAnyAsync(supplier, supplier),
            completed.thenMApplyAnyAsync(testExecutor, supplier, supplier),
        ).map { it.await() }.shouldForAll { it shouldBe n + n }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // Then-Tuple-Multi-Actions(thenTupleM*) Methods
    ////////////////////////////////////////////////////////////

    test("thenTupleM*") {
        val completed = completedFuture(n)
        val function_n = Function { x: Int ->
            snoreZzz()
            n + x
        }

        val function_s = Function { x: Int ->
            snoreZzz()
            s + x
        }

        val function_d = Function { x: Int ->
            snoreZzz()
            d + x
        }
        val function_an = Function { x: Int ->
            snoreZzz()
            anotherN + x
        }
        val function_nn = Function { x: Int ->
            snoreZzz()
            n + x
        }

        // thenTupleMApplyFastFailAsync

        completed.thenTupleMApplyFastFailAsync(function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)
        completed.thenTupleMApplyFastFailAsync(testExecutor, function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)

        completed.thenTupleMApplyFastFailAsync(function_n, function_s, function_d)
            .await() shouldBe Tuple3.of(n + n, s + n, d + n)
        completed.thenTupleMApplyFastFailAsync(testExecutor, function_n, function_s, function_d)
            .await() shouldBe Tuple3.of(n + n, s + n, d + n)

        completed.thenTupleMApplyFastFailAsync(function_n, function_s, function_d, function_an)
            .await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)
        completed.thenTupleMApplyFastFailAsync(testExecutor, function_n, function_s, function_d, function_an)
            .await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)

        completed.thenTupleMApplyFastFailAsync(function_n, function_s, function_d, function_an, function_nn)
            .await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)
        completed.thenTupleMApplyFastFailAsync(
            testExecutor, function_n, function_s, function_d, function_an, function_nn
        ).await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)

        // thenTupleMApplyAllSuccessAsync

        completed.thenTupleMApplyAllSuccessAsync(function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)
        completed.thenTupleMApplyAllSuccessAsync(testExecutor, function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)

        completed.thenTupleMApplyAllSuccessAsync(function_n, function_s, function_d)
            .await() shouldBe Tuple3.of(n + n, s + n, d + n)
        completed.thenTupleMApplyAllSuccessAsync(testExecutor, function_n, function_s, function_d)
            .await() shouldBe Tuple3.of(n + n, s + n, d + n)

        completed.thenTupleMApplyAllSuccessAsync(function_n, function_s, function_d, function_an)
            .await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)
        completed.thenTupleMApplyAllSuccessAsync(testExecutor, function_n, function_s, function_d, function_an)
            .await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)

        completed.thenTupleMApplyAllSuccessAsync(function_n, function_s, function_d, function_an, function_nn)
            .await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)
        completed.thenTupleMApplyAllSuccessAsync(
            testExecutor, function_n, function_s, function_d, function_an, function_nn
        ).await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)

        // thenTupleMApplyMostSuccessAsync

        completed.thenTupleMApplyMostSuccessAsync(LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)
        completed.thenTupleMApplyMostSuccessAsync(
            testExecutor, LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s
        ).await() shouldBe Tuple2.of(n + n, s + n)

        completed.thenTupleMApplyMostSuccessAsync(
            LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s, function_d
        ).await() shouldBe Tuple3.of(n + n, s + n, d + n)
        completed.thenTupleMApplyMostSuccessAsync(
            testExecutor, LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s, function_d
        ).await() shouldBe Tuple3.of(n + n, s + n, d + n)

        completed.thenTupleMApplyMostSuccessAsync(
            LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an
        ).await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)
        completed.thenTupleMApplyMostSuccessAsync(
            testExecutor, LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an
        ).await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)

        completed.thenTupleMApplyMostSuccessAsync(
            LONG_WAIT_MS, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an, function_nn
        ).await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)
        completed.thenTupleMApplyMostSuccessAsync(
            testExecutor, LONG_WAIT_MS, TimeUnit.MILLISECONDS,
            function_n, function_s, function_d, function_an, function_nn
        ).await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)

        // thenTupleMApplyAsync

        completed.thenTupleMApplyAsync(function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)
        completed.thenTupleMApplyAsync(testExecutor, function_n, function_s)
            .await() shouldBe Tuple2.of(n + n, s + n)

        completed.thenTupleMApplyAsync(function_n, function_s, function_d)
            .await() shouldBe Tuple3.of(n + n, s + n, d + n)
        completed.thenTupleMApplyAsync(testExecutor, function_n, function_s, function_d)
            .await() shouldBe Tuple3.of(n + n, s + n, d + n)

        completed.thenTupleMApplyAsync(function_n, function_s, function_d, function_an)
            .await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)
        completed.thenTupleMApplyAsync(testExecutor, function_n, function_s, function_d, function_an)
            .await() shouldBe Tuple4.of(n + n, s + n, d + n, anotherN + n)

        completed.thenTupleMApplyAsync(function_n, function_s, function_d, function_an, function_nn)
            .await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)
        completed.thenTupleMApplyAsync(
            testExecutor, function_n, function_s, function_d, function_an, function_nn
        ).await() shouldBe Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n)
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# both methods
    ////////////////////////////////////////////////////////////////////////////////

    test("both fastFail") {
        val cf = CompletableFuture.supplyAsync {
            snoreZzz(2_000)
            n
        }
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)

        val runnable = Runnable {}
        shouldThrowExactly<ExecutionException> {
            cf.runAfterBothFastFail(failed, runnable)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.runAfterBothFastFailAsync(failed, runnable)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.runAfterBothFastFailAsync(failed, runnable, testExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte

        val bc = BiConsumer { _: Int, _: Int -> }
        shouldThrowExactly<ExecutionException> {
            cf.thenAcceptBothFastFail(failed, bc)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenAcceptBothFastFailAsync(failed, bc)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenAcceptBothFastFailAsync(failed, bc, testExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte

        shouldThrowExactly<ExecutionException> {
            cf.thenCombineFastFail(failed, Integer::sum)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenCombineFastFailAsync(failed, Integer::sum)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenCombineFastFailAsync(failed, Integer::sum, testExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# either methods
    ////////////////////////////////////////////////////////////////////////////////

    test("either success") {
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)
        val cf = completedFuture(n)

        val runnable = Runnable {}
        failed.runAfterEitherSuccess(cf, runnable).await().shouldBeNull()
        failed.runAfterEitherSuccessAsync(cf, runnable).await().shouldBeNull()
        failed.runAfterEitherSuccessAsync(cf, runnable, testExecutor).await().shouldBeNull()

        val c = Consumer<Int> {}
        failed.acceptEitherSuccess(cf, c).await().shouldBeNull()
        failed.acceptEitherSuccessAsync(cf, c).await().shouldBeNull()
        failed.acceptEitherSuccessAsync(cf, c, testExecutor).await().shouldBeNull()

        failed.applyToEitherSuccess(cf, identity()).await() shouldBe n
        failed.applyToEitherSuccessAsync(cf, identity()).await() shouldBe n
        failed.applyToEitherSuccessAsync(cf, identity(), testExecutor).await() shouldBe n
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New enhanced methods
    ////////////////////////////////////////////////////////////////////////////////

    test("peek") {
        val c = BiConsumer<Any, Throwable> { _: Any, _: Throwable -> }
        val ec = BiConsumer<Any, Throwable> { _: Any, _: Throwable -> throw rte }

        val failed = CompletableFutureUtils.failedFuture<Any>(rte)
        failed.peek(c) shouldBeSameInstanceAs failed
        failed.peekAsync(c) shouldBeSameInstanceAs failed
        failed.peekAsync(c, testExecutor) shouldBeSameInstanceAs failed
        failed.peek(ec) shouldBeSameInstanceAs failed
        failed.peekAsync(ec) shouldBeSameInstanceAs failed
        failed.peekAsync(ec, testExecutor) shouldBeSameInstanceAs failed

        val success = completedFuture(n)
        success.peek(c).await() shouldBe n
        success.peekAsync(c).await() shouldBe n
        success.peekAsync(c).await() shouldBe n
        success.peek(ec).await() shouldBe n
        success.peekAsync(ec).await() shouldBe n
        success.peekAsync(ec).await() shouldBe n
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF instance methods
    //  compatibility for low Java version
    ////////////////////////////////////////////////////////////////////////////////

    test("catching") {
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)

        failed.catching(RuntimeException::class.java) { n }.await() shouldBe n
        shouldThrowExactly<RuntimeException> {
            failed.catching(IndexOutOfBoundsException::class.java) { n }.await()
        } shouldBeSameInstanceAs rte

        failed.catchingAsync(RuntimeException::class.java) { n }.await() shouldBe n
        shouldThrowExactly<RuntimeException> {
            failed.catchingAsync(IndexOutOfBoundsException::class.java) { n }.await()
        } shouldBeSameInstanceAs rte
        shouldThrowExactly<RuntimeException> {
            failed.catchingAsync(IndexOutOfBoundsException::class.java, { n }, testExecutor).await()
        } shouldBeSameInstanceAs rte

        val success = completedFuture(n)

        success.catching(RuntimeException::class.java) { anotherN }.await() shouldBe n
        success.catching(IndexOutOfBoundsException::class.java) { anotherN }.await() shouldBe n

        success.catchingAsync(RuntimeException::class.java) { anotherN }.await() shouldBe n
        success.catchingAsync(IndexOutOfBoundsException::class.java) { anotherN }.await() shouldBe n
        success.catchingAsync(IndexOutOfBoundsException::class.java, { anotherN }, testExecutor)
            .await() shouldBe n
    }

    test("exceptionallyAsync") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.exceptionallyAsync { n }.await() shouldBe n
        cf.exceptionallyAsync({ n }, testExecutor).await() shouldBe n
    }

    test("timeout") {
        shouldThrowExactly<ExecutionException> {
            CompletableFuture<Int>().orTimeout(1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()
        shouldThrowExactly<ExecutionException> {
            CompletableFuture<Int>().cffuOrTimeout(1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()
        shouldThrowExactly<ExecutionException> {
            CompletableFuture<Int>().cffuOrTimeout(testExecutor, 1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()

        CompletableFuture<Int>().completeOnTimeout(n, 1, TimeUnit.MILLISECONDS).await() shouldBe n
        CompletableFuture<Int>().cffuCompleteOnTimeout(n, 1, TimeUnit.MILLISECONDS).await() shouldBe n
        CompletableFuture<Int>().cffuCompleteOnTimeout(n, testExecutor, 1, TimeUnit.MILLISECONDS)
            .await() shouldBe n
    }

    test("catchingCompose") {
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)

        failed.catchingCompose(RuntimeException::class.java) { completedFuture(n) }.await() shouldBe n
        shouldThrowExactly<RuntimeException> {
            failed.catchingCompose(IndexOutOfBoundsException::class.java) { completedFuture(n) }.await()
        } shouldBeSameInstanceAs rte

        failed.catchingComposeAsync(RuntimeException::class.java) { completedFuture(n) }.await() shouldBe n
        shouldThrowExactly<RuntimeException> {
            failed.catchingComposeAsync(IndexOutOfBoundsException::class.java) { completedFuture(n) }.await()
        } shouldBeSameInstanceAs rte
        shouldThrowExactly<RuntimeException> {
            failed.catchingComposeAsync(
                IndexOutOfBoundsException::class.java,
                { completedFuture(n) },
                testExecutor
            ).await()
        } shouldBeSameInstanceAs rte

        val success = completedFuture(n)

        success.catchingCompose(RuntimeException::class.java) { completedFuture(anotherN) }.await() shouldBe n
        success.catchingCompose(IndexOutOfBoundsException::class.java) { completedFuture(anotherN) }.await() shouldBe n

        success.catchingComposeAsync(RuntimeException::class.java) { completedFuture(anotherN) }.await() shouldBe n
        success.catchingComposeAsync(IndexOutOfBoundsException::class.java) { completedFuture(anotherN) }
            .await() shouldBe n
        success.catchingComposeAsync(
            IndexOutOfBoundsException::class.java,
            { completedFuture(anotherN) },
            testExecutor
        ).await() shouldBe n
    }

    test("exceptionallyCompose") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.exceptionallyCompose { completedFuture(n) }.await() shouldBe n
        cf.exceptionallyComposeAsync { completedFuture(n) }.await() shouldBe n
        cf.exceptionallyComposeAsync({ completedFuture(n) }, testExecutor).await() shouldBe n
    }

    test("read methods") {
        val cf = completedFuture(n)
        val ff = CompletableFutureUtils.failedFuture<Int>(rte)

        cf.join(1, TimeUnit.MILLISECONDS) shouldBe n
        cf.getSuccessNow(null) shouldBe n
        cf.resultNow() shouldBe n
        ff.exceptionNow() shouldBeSameInstanceAs rte

        cf.cffuState() shouldBe CffuState.SUCCESS
        ff.cffuState() shouldBe CffuState.FAILED
    }

    test("write methods") {
        incompleteCf<Int>().completeAsync { n }.await() shouldBe n
        incompleteCf<Int>().completeAsync({ n }, testExecutor).await() shouldBe n

        shouldThrowExactly<ExecutionException> {
            incompleteCf<Int>().completeExceptionallyAsync { rte }.get()
        }.cause shouldBeSameInstanceAs rte

        shouldThrowExactly<ExecutionException> {
            incompleteCf<Int>().completeExceptionallyAsync({ rte }, testExecutor).get()
        }.cause shouldBeSameInstanceAs rte
    }

    test("re_config") {
        completedFuture(n).minimalCompletionStage()
            .toCompletableFuture().await() shouldBe n

        val cf = CompletableFuture<Int>()
        cf.copy().complete(n)
        cf.isDone.shouldBeFalse()

        assertIsCfDefaultExecutor(cf.defaultExecutor())

        val incomplete: CompletableFuture<Int> = cf.newIncompleteFuture()
        incomplete.isDone.shouldBeFalse()
        incomplete.complete(n)
        cf.isDone.shouldBeFalse()
    }
})
