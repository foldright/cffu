package io.foldright.cffu.kotlin

import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

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

        // FIXME: Need Type arguments for array...
        arrayOf<CompletableFuture<*>>(
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
        listOf<CompletableFuture<Int>>().allOfFastFailCompletableFuture().await() shouldBe emptyList<Int>()

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
        arrayOf<CompletableFuture<Int>>().allOfFastFailCompletableFuture().await() shouldBe emptyList<Int>()

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

        // FIXME: Need Type arguments for array...
        arrayOf<CompletableFuture<*>>(
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

        // FIXME: Need Type arguments for array...
        arrayOf<CompletableFuture<*>>(
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

        // FIXME: Need Type arguments for array...
        arrayOf<CompletableFuture<*>>(
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
})
