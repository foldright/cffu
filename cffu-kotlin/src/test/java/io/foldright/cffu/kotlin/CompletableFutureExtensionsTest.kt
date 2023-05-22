package io.foldright.cffu.kotlin

import io.foldright.cffu.CffuState
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
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

class CompletableFutureExtensionsTest : FunSpec({
    test("allOf*") {
        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCompletableFuture().await() shouldBe listOf(42, 43, 44)
        listOf<CompletableFuture<Int>>().allOfCompletableFuture().await() shouldBe emptyList()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCompletableFuture().await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfCompletableFuture().await() shouldBe listOf(42, 43, 44)
        arrayOf<CompletableFuture<Int>>().allOfCompletableFuture().await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFutureVoid().await().shouldBeNull()
        listOf<CompletableFuture<Int>>().allOfCompletableFutureVoid().await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFutureVoid().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfCompletableFutureVoid().await().shouldBeNull()
        arrayOf<CompletableFuture<*>>().allOfCompletableFutureVoid().await().shouldBeNull()

        ////////////////////////////////////////

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfFastFailCompletableFuture().await() shouldBe listOf(42, 43, 44)
        listOf<CompletableFuture<Int>>().allOfFastFailCompletableFuture().await() shouldBe emptyList()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfFastFailCompletableFuture().await() shouldBe listOf(42, 43, 44)

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture(43),
            CompletableFuture.completedFuture(44),
        ).allOfFastFailCompletableFuture().await() shouldBe listOf(42, 43, 44)
        arrayOf<CompletableFuture<Int>>().allOfFastFailCompletableFuture().await() shouldBe emptyList()

        ////////////////////////////////////////

        listOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfFastFailCompletableFutureVoid().await().shouldBeNull()
        listOf<CompletableFuture<Int>>().allOfFastFailCompletableFutureVoid().await().shouldBeNull()

        setOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfFastFailCompletableFutureVoid().await().shouldBeNull()

        arrayOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("42"),
            CompletableFuture.completedFuture(42.0),
        ).allOfFastFailCompletableFutureVoid().await().shouldBeNull()
        arrayOf<CompletableFuture<*>>().allOfFastFailCompletableFutureVoid().await().shouldBeNull()

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
        ).anyOfCompletableFutureAny().await() shouldBe 42
        listOf<CompletableFuture<*>>().anyOfCompletableFutureAny().isDone.shouldBeFalse()

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFutureAny().await() shouldBe 42

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfCompletableFutureAny().await() shouldBe 42
        arrayOf<CompletableFuture<*>>().anyOfCompletableFutureAny().isDone.shouldBeFalse()

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
        ).anyOfSuccessCompletableFutureAny().await() shouldBe 42
        shouldThrow<NoCfsProvidedException> {
            listOf<CompletableFuture<*>>().anyOfSuccessCompletableFutureAny().await()
        }

        setOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFutureAny().await() shouldBe 42

        arrayOf(
            CompletableFuture<String>(),
            CompletableFuture<Double>(),
            CompletableFuture.completedFuture(42),
        ).anyOfSuccessCompletableFutureAny().await() shouldBe 42
        shouldThrow<NoCfsProvidedException> {
            arrayOf<CompletableFuture<*>>().anyOfSuccessCompletableFutureAny().await()
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

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF instance methods
    //  compatibility for low Java version
    //
    //  all methods name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    test("exceptionallyAsync") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.cffuExceptionallyAsync { n }.get() shouldBe n
        cf.cffuExceptionallyAsync({ n }, testThreadPoolExecutor).get() shouldBe n
    }

    test(" test_timeout") {
        var cf = CompletableFuture<Int>()
        shouldThrow<ExecutionException> {
            cf.cffuOrTimeout(1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()

        cf = CompletableFuture()
        cf.cffuCompleteOnTimeout(n, 1, TimeUnit.MILLISECONDS).get() shouldBe n
    }

    test("exceptionallyCompose") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.cffuExceptionallyCompose { CompletableFuture.completedFuture(n) }.get() shouldBe n
        cf.cffuExceptionallyComposeAsync { CompletableFuture.completedFuture(n) }.get() shouldBe n
        cf.cffuExceptionallyComposeAsync({ CompletableFuture.completedFuture(n) }, testThreadPoolExecutor)
            .get() shouldBe n
    }

    test("read") {
        val cf = CompletableFuture.completedFuture(n)
        val ff = CompletableFutureUtils.failedFuture<Int>(rte)

        cf.cffuJoin(1, TimeUnit.MILLISECONDS) shouldBe n
        cf.cffuResultNow() shouldBe n
        ff.cffuExceptionNow() shouldBeSameInstanceAs rte

        cf.cffuState() shouldBe CffuState.SUCCESS
        ff.cffuState() shouldBe CffuState.FAILED
    }

    test("write") {
        val cf = CompletableFuture<Int>()
        cf.cffuCompleteAsync { n }.get() shouldBe n
        cf.cffuCompleteAsync({ n }, testThreadPoolExecutor).get() shouldBe n
    }

    test("re_config") {
        CompletableFuture.completedFuture(n).cffuMinimalCompletionStage()
            .toCompletableFuture().get() shouldBe n

        val cf = CompletableFuture<Int>()
        cf.cffuCopy().complete(n)
        cf.isDone.shouldBeFalse()

        val incomplete = cf.cffuNewIncompleteFuture<Int, Any>()
        incomplete.isDone.shouldBeFalse()
        incomplete.complete(n)
        cf.isDone.shouldBeFalse()
    }
})
