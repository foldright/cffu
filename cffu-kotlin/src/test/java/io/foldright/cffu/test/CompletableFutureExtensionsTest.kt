package io.foldright.cffu.test

import io.foldright.cffu.CffuState
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.NoCfsProvidedException
import io.foldright.cffu.kotlin.*
import io.foldright.test_utils.*
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
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
import java.util.function.Function.identity

class CompletableFutureExtensionsTest : FunSpec({
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
            .mostSuccessResultsOfCompletableFuture(null, testThreadPoolExecutor, 10, TimeUnit.MILLISECONDS)
            .await() shouldBe listOf(null, n)

        arrayOf(CompletableFuture(), completedFuture(n))
            .mostSuccessResultsOfCompletableFuture(null, 10, TimeUnit.MILLISECONDS).await() shouldBe listOf(null, n)
        arrayOf(CompletableFuture(), completedFuture(n))
            .mostSuccessResultsOfCompletableFuture(null, testThreadPoolExecutor, 10, TimeUnit.MILLISECONDS)
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
            cf.runAfterBothFastFailAsync(failed, runnable, testThreadPoolExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte

        val bc = BiConsumer { _: Int, _: Int -> }
        shouldThrowExactly<ExecutionException> {
            cf.thenAcceptBothFastFail(failed, bc)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenAcceptBothFastFailAsync(failed, bc)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenAcceptBothFastFailAsync(failed, bc, testThreadPoolExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte

        shouldThrowExactly<ExecutionException> {
            cf.thenCombineFastFail(failed, Integer::sum)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenCombineFastFailAsync(failed, Integer::sum)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
        shouldThrowExactly<ExecutionException> {
            cf.thenCombineFastFailAsync(failed, Integer::sum, testThreadPoolExecutor)[1, TimeUnit.MILLISECONDS]
        }.cause shouldBeSameInstanceAs rte
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# either methods
    ////////////////////////////////////////////////////////////////////////////////

    test("either success") {
        val failed = CompletableFutureUtils.failedFuture<Int>(rte)
        val cf = completedFuture(n)

        val runnable = Runnable {}
        failed.runAfterEitherSuccess(cf, runnable).get().shouldBeNull()
        failed.runAfterEitherSuccessAsync(cf, runnable).get().shouldBeNull()
        failed.runAfterEitherSuccessAsync(cf, runnable, testThreadPoolExecutor).get().shouldBeNull()

        val c = Consumer<Int> {}
        failed.acceptEitherSuccess(cf, c).get().shouldBeNull()
        failed.acceptEitherSuccessAsync(cf, c).get().shouldBeNull()
        failed.acceptEitherSuccessAsync(cf, c, testThreadPoolExecutor).get().shouldBeNull()

        failed.applyToEitherSuccess(cf, identity()).get() shouldBe n
        failed.applyToEitherSuccessAsync(cf, identity()).get() shouldBe n
        failed.applyToEitherSuccessAsync(cf, identity(), testThreadPoolExecutor).get() shouldBe n
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
        failed.peekAsync(c, testThreadPoolExecutor) shouldBeSameInstanceAs failed
        failed.peek(ec) shouldBeSameInstanceAs failed
        failed.peekAsync(ec) shouldBeSameInstanceAs failed
        failed.peekAsync(ec, testThreadPoolExecutor) shouldBeSameInstanceAs failed

        val success = completedFuture(n)
        success.peek(c).get() shouldBe n
        success.peekAsync(c).get() shouldBe n
        success.peekAsync(c).get() shouldBe n
        success.peek(ec).get() shouldBe n
        success.peekAsync(ec).get() shouldBe n
        success.peekAsync(ec).get() shouldBe n
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
            failed.catchingAsync(IndexOutOfBoundsException::class.java, { n }, testThreadPoolExecutor).await()
        } shouldBeSameInstanceAs rte

        val success = completedFuture(n)

        success.catching(RuntimeException::class.java) { anotherN }.await() shouldBe n
        success.catching(IndexOutOfBoundsException::class.java) { anotherN }.await() shouldBe n

        success.catchingAsync(RuntimeException::class.java) { anotherN }.await() shouldBe n
        success.catchingAsync(IndexOutOfBoundsException::class.java) { anotherN }.await() shouldBe n
        success.catchingAsync(IndexOutOfBoundsException::class.java, { anotherN }, testThreadPoolExecutor)
            .await() shouldBe n
    }

    test("exceptionallyAsync") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.exceptionallyAsync { n }.get() shouldBe n
        cf.exceptionallyAsync({ n }, testThreadPoolExecutor).get() shouldBe n
    }

    test("timeout") {
        shouldThrowExactly<ExecutionException> {
            CompletableFuture<Int>().orTimeout(1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()
        shouldThrowExactly<ExecutionException> {
            CompletableFuture<Int>().cffuOrTimeout(1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()
        shouldThrowExactly<ExecutionException> {
            CompletableFuture<Int>().cffuOrTimeout(testThreadPoolExecutor, 1, TimeUnit.MILLISECONDS).get()
        }.cause.shouldBeTypeOf<TimeoutException>()

        CompletableFuture<Int>().completeOnTimeout(n, 1, TimeUnit.MILLISECONDS).get() shouldBe n
        CompletableFuture<Int>().cffuCompleteOnTimeout(n, 1, TimeUnit.MILLISECONDS).get() shouldBe n
        CompletableFuture<Int>().cffuCompleteOnTimeout(n, testThreadPoolExecutor, 1, TimeUnit.MILLISECONDS)
            .get() shouldBe n
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
                testThreadPoolExecutor
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
            testThreadPoolExecutor
        ).await() shouldBe n
    }

    test("exceptionallyCompose") {
        val cf = CompletableFutureUtils.failedFuture<Int>(rte)
        cf.exceptionallyCompose { completedFuture(n) }.get() shouldBe n
        cf.exceptionallyComposeAsync { completedFuture(n) }.get() shouldBe n
        cf.exceptionallyComposeAsync({ completedFuture(n) }, testThreadPoolExecutor)
            .get() shouldBe n
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
        incompleteCf<Int>().completeAsync { n }.get() shouldBe n
        incompleteCf<Int>().completeAsync({ n }, testThreadPoolExecutor).get() shouldBe n

        shouldThrowExactly<ExecutionException> {
            incompleteCf<Int>().completeExceptionallyAsync { rte }.get()
        }.cause shouldBeSameInstanceAs rte

        shouldThrowExactly<ExecutionException> {
            incompleteCf<Int>().completeExceptionallyAsync({ rte }, testThreadPoolExecutor).get()
        }.cause shouldBeSameInstanceAs rte
    }

    test("re_config") {
        completedFuture(n).minimalCompletionStage()
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
