package io.foldright.cffu.test

import io.foldright.cffu.CffuState
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.kotlin.*
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import io.foldright.test_utils.sleep
import io.foldright.test_utils.testThreadPoolExecutor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.BiConsumer
import java.util.function.Consumer
import java.util.function.Function

class CompletableFutureExtensionsTest : FunSpec({
    test("allOf*") {
        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allResultsOfCompletableFuture().await() shouldBe listOf(42, 43, 44)
        listOf<CompletableFuture<Int>>().allResultsOfCompletableFuture().await() shouldBe emptyList()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allResultsOfCompletableFuture().await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allResultsOfCompletableFuture().await() shouldBe listOf(42, 43, 44)
        arrayOf<CompletableFuture<Int>>().allResultsOfCompletableFuture().await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFuture().await().shouldBeNull()
        listOf<CompletableFuture<Int>>().allOfCompletableFuture().await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFuture().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFuture().await().shouldBeNull()
        arrayOf<CompletableFuture<*>>().allOfCompletableFuture().await().shouldBeNull()

        ////////////////////////////////////////

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allResultsOfFastFailCompletableFuture().await() shouldBe listOf(42, 43, 44)
        listOf<CompletableFuture<Int>>().allResultsOfFastFailCompletableFuture().await() shouldBe emptyList()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allResultsOfFastFailCompletableFuture().await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allResultsOfFastFailCompletableFuture().await() shouldBe listOf(42, 43, 44)
        arrayOf<CompletableFuture<Int>>().allResultsOfFastFailCompletableFuture().await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfFastFailCompletableFuture().await().shouldBeNull()
        listOf<CompletableFuture<Int>>().allOfFastFailCompletableFuture().await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfFastFailCompletableFuture().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfFastFailCompletableFuture().await().shouldBeNull()
        arrayOf<CompletableFuture<*>>().allOfFastFailCompletableFuture().await().shouldBeNull()

    }

    test("anyOf*") {
        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42
        listOf<CompletableFuture<Int>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        setOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42

        arrayOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42
        arrayOf<CompletableFuture<Int>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        ////////////////////////////////////////

        listOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42
        listOf<CompletableFuture<*>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFuture().await() shouldBe 42
        arrayOf<CompletableFuture<*>>().anyOfCompletableFuture().isDone.shouldBeFalse()

        ////////////////////////////////////////

        listOf(
            CompletableFuture(),
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42
        shouldThrow<NoCfsProvidedException> {
            listOf<CompletableFuture<Int>>().anyOfSuccessCompletableFuture().await()
        }

        setOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42

        arrayOf(
            CompletableFuture(),
            CompletableFuture.completedFuture(42),
            CompletableFuture(),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42
        shouldThrow<NoCfsProvidedException> {
            arrayOf<CompletableFuture<Int>>().anyOfSuccessCompletableFuture().await()
        }

        ////////////////////////////////////////

        listOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42
        shouldThrow<NoCfsProvidedException> {
            listOf<CompletableFuture<*>>().anyOfSuccessCompletableFuture().await()
        }

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFuture().await() shouldBe 42
        shouldThrow<NoCfsProvidedException> {
            arrayOf<CompletableFuture<*>>().anyOfSuccessCompletableFuture().await()
        }
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

    test("combineFastFail - CompletableFuture") {
        CompletableFuture.completedFuture(n).combineFastFail(
            CompletableFuture.completedFuture(s)
        ).get() shouldBe Tuple2.of(n, s)

        CompletableFuture.completedFuture(n).combineFastFail(
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d)
        ).get() shouldBe Tuple3.of(n, s, d)

        CompletableFuture.completedFuture(n).combineFastFail(
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d),
            CompletableFuture.completedFuture(anotherN)
        ).get() shouldBe Tuple4.of(n, s, d, anotherN)

        CompletableFuture.completedFuture(n).combineFastFail(
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d),
            CompletableFuture.completedFuture(anotherN),
            CompletableFuture.completedFuture(n + n)
        ).get() shouldBe Tuple5.of(n, s, d, anotherN, n + n)
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# both methods
    ////////////////////////////////////////////////////////////////////////////////

    test("both fastFail") {
        val cf = CompletableFuture.supplyAsync {
            sleep(2_000)
            n
        }
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)

        val runnable = Runnable {}
        shouldThrow<ExecutionException> {
            cf.runAfterBothFastFail(failed, runnable)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrow<ExecutionException> {
            cf.runAfterBothFastFailAsync(failed, runnable)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrow<ExecutionException> {
            cf.runAfterBothFastFailAsync(failed, runnable, testThreadPoolExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte

        val bc = BiConsumer { _: Int, _: Int -> }
        shouldThrow<ExecutionException> {
            cf.thenAcceptBothFastFail(failed, bc)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrow<ExecutionException> {
            cf.thenAcceptBothFastFailAsync(failed, bc)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrow<ExecutionException> {
            cf.thenAcceptBothFastFailAsync(failed, bc, testThreadPoolExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte

        shouldThrow<ExecutionException> {
            cf.thenCombineFastFail(failed, Integer::sum)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrow<ExecutionException> {
            cf.thenCombineFastFailAsync(failed, Integer::sum)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrow<ExecutionException> {
            cf.thenCombineFastFailAsync(failed, Integer::sum, testThreadPoolExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# either methods
    ////////////////////////////////////////////////////////////////////////////////

    test("either success") {
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)
        val cf = CompletableFuture.completedFuture(n)

        val runnable = Runnable {}
        failed.runAfterEitherSuccess(cf, runnable).get().shouldBeNull()
        failed.runAfterEitherSuccessAsync(cf, runnable).get().shouldBeNull()
        failed.runAfterEitherSuccessAsync(cf, runnable, testThreadPoolExecutor).get().shouldBeNull()

        val c = Consumer<Int> {}
        failed.acceptEitherSuccess(cf, c).get().shouldBeNull()
        failed.acceptEitherSuccessAsync(cf, c).get().shouldBeNull()
        failed.acceptEitherSuccessAsync(cf, c, testThreadPoolExecutor).get().shouldBeNull()

        failed.applyToEitherSuccess(cf, Function.identity()).get() shouldBe n
        failed.applyToEitherSuccessAsync(cf, Function.identity()).get() shouldBe n
        failed.applyToEitherSuccessAsync(cf, Function.identity(), testThreadPoolExecutor).get() shouldBe n
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF instance methods
    //  compatibility for low Java version
    ////////////////////////////////////////////////////////////////////////////////

    test("exceptionallyAsync") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.exceptionallyAsync { n }.get() shouldBe n
        cf.exceptionallyAsync({ n }, testThreadPoolExecutor).get() shouldBe n
    }

    test("test_timeout") {
        var cf = CompletableFuture<Int>()
        shouldThrow<ExecutionException> {
            cf.orTimeout(1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()

        cf = CompletableFuture()
        cf.completeOnTimeout(n, 1, TimeUnit.MILLISECONDS).get() shouldBe n
    }

    test("exceptionallyCompose") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.exceptionallyCompose { CompletableFuture.completedFuture(n) }.get() shouldBe n
        cf.exceptionallyComposeAsync { CompletableFuture.completedFuture(n) }.get() shouldBe n
        cf.exceptionallyComposeAsync({ CompletableFuture.completedFuture(n) }, testThreadPoolExecutor)
            .get() shouldBe n
    }

    test("read methods") {
        val cf = CompletableFuture.completedFuture(n)
        val ff = CompletableFutureUtils.failedFuture<Int>(rte)

        cf.join(1, TimeUnit.MILLISECONDS) shouldBe n
        cf.resultNow() shouldBe n
        ff.exceptionNow() shouldBeSameInstanceAs rte

        cf.cffuState() shouldBe CffuState.SUCCESS
        ff.cffuState() shouldBe CffuState.FAILED
    }

    test("write methods") {
        val cf = CompletableFuture<Int>()
        cf.completeAsync { n }.get() shouldBe n
        cf.completeAsync({ n }, testThreadPoolExecutor).get() shouldBe n
    }

    test("re_config") {
        CompletableFuture.completedFuture(n).minimalCompletionStage()
            .toCompletableFuture().get() shouldBe n

        val cf = CompletableFuture<Int>()
        cf.copy().complete(n)
        cf.isDone.shouldBeFalse()

        val incomplete: CompletableFuture<Int> = cf.newIncompleteFuture()
        incomplete.isDone.shouldBeFalse()
        incomplete.complete(n)
        cf.isDone.shouldBeFalse()
    }
})
