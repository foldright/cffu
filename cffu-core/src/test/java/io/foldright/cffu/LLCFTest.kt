package io.foldright.cffu

import io.foldright.cffu.CompletableFutureUtils.completedStage
import io.foldright.test_utils.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.future.shouldBeCompleted
import io.kotest.matchers.future.shouldCompleteExceptionallyWith
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CompletionStage

class LLCFTest : FunSpec({
    val testExecutor = createThreadPool("CheckMinStageRuntimeTypeTests", queueCapacity = 1000_000)
    val testCffuFac = CffuFactory.builder(testExecutor).build()

    test("f_toCfCopy0") {
        arrayOf(
            completedStage(n),
            testCffuFac.completedStage(n),
        ).forEach { s ->
            LLCF.f_toCfCopy0(s).apply {
                shouldNotBeSameInstanceAs(s)
                if (isJava9Plus()) {
                    LLCF.isMinStageCf(this).shouldBeTrue()
                    shouldBeMinimalStage()
                } else {
                    shouldBeCompleted()
                    join() shouldBe n
                }
            }
        }

        arrayOf(
            completedFuture(n),
            testCffuFac.completedFuture(n),
        ).forEach { s ->
            LLCF.f_toCfCopy0(s).apply {
                shouldNotBeSameInstanceAs(s)
                shouldBeCompleted()
                join() shouldBe n

                shouldNotBeMinimalStage()
            }
        }
    }

    test("f_toCfCopyArray0") {
        LLCF.f_toCfCopyArray0(
            arrayOf(
                completedStage(n),
                testCffuFac.completedStage(n),
            )
        ).forEach { s ->
            LLCF.f_toCfCopy0(s).apply {
                shouldNotBeSameInstanceAs(s)
                if (isJava9Plus()) {
                    LLCF.isMinStageCf(this).shouldBeTrue()
                    shouldBeMinimalStage()
                } else {
                    shouldBeCompleted()
                    join() shouldBe n
                }
            }
        }
        LLCF.f_toCfCopyArray0(
            arrayOf<CompletionStage<Int>>(
                completedFuture(n),
                testCffuFac.completedFuture(n),
            )
        ).forEach { s ->
            LLCF.f_toCfCopy0(s).apply {
                shouldNotBeSameInstanceAs(s)
                shouldBeCompleted()
                join() shouldBe n

                shouldNotBeMinimalStage()
            }
        }
    }

    test("completeCf0 - success") {
        val cf = CompletableFuture<Int>()
        LLCF.completeCf0(cf, n, null).shouldBeTrue()
        cf.shouldBeCompleted()
        cf.get() shouldBeExactly n

        LLCF.completeCf0(cf, n, null).shouldBeFalse()
    }

    test("completeCf0 - exceptionally") {
        val cf = CompletableFuture<Int>()
        val ex = RuntimeException("foo")
        LLCF.completeCf0(cf, n, ex).shouldBeTrue()
        cf.shouldCompleteExceptionallyWith(ex)

        LLCF.completeCf0(cf, n, ex).shouldBeFalse()
    }
})
