package io.foldright.cffu

import io.foldright.cffu.CompletableFutureUtils.completedStage
import io.foldright.cffu.CompletableFutureUtils.failedFuture
import io.foldright.test_utils.*
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.future.shouldBeCompleted
import io.kotest.matchers.future.shouldCompleteExceptionallyWith
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.lang.Thread.currentThread
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.CountDownLatch

class LLCFTest : FunSpec({
    val testExecutor = createThreadPool("CheckMinStageRuntimeTypeTests", queueCapacity = 1000_000)
    val testCffuFac = CffuFactory.builder(testExecutor).build()

    test("f_toCf0") {
        val incomplete = incompleteCf<Int>()
        LLCF.f_toCf0(incomplete) shouldBeSameInstanceAs incomplete
        LLCF.f_toCf0(FooCs(incomplete)) shouldBeSameInstanceAs incomplete
    }

    test("toNonMinCfArray0") {
        val minStages = arrayOf(
            completedStage(n),
            testCffuFac.completedStage(n),
        )
        LLCF.toNonMinCfArray0(minStages).forEachIndexed { idx, f ->
            if (isJava9Plus()) {
                f.shouldNotBeSameInstanceAs(minStages[idx])
            } else {
                if (idx == 0)
                    f.shouldBeSameInstanceAs(minStages[idx])
                else {
                    f.shouldNotBeSameInstanceAs(minStages[idx])
                    f.shouldBeSameInstanceAs((minStages[idx] as Cffu<Int>).cffuUnwrap())
                }
            }
            f.shouldBeCompleted()
            f.join() shouldBe n
            LLCF.isMinStageCf(f).shouldBeFalse()
            f.shouldNotBeMinimalStage()
        }

        val cfs = arrayOf<CompletionStage<Int>>(
            completedFuture(n),
            testCffuFac.completedFuture(n),
        )
        LLCF.toNonMinCfArray0(cfs).forEachIndexed { idx, f ->
            if (idx == 0)
                f.shouldBeSameInstanceAs(cfs[idx])
            else {
                f.shouldNotBeSameInstanceAs(cfs[idx])
                f.shouldBeSameInstanceAs((cfs[idx] as Cffu<Int>).cffuUnwrap())
            }
            f.shouldBeCompleted()
            f.join() shouldBe n
            LLCF.isMinStageCf(f).shouldBeFalse()
            f.shouldNotBeMinimalStage()
        }
    }

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
        val minStages = arrayOf(
            completedStage(n),
            testCffuFac.completedStage(n),
        )
        LLCF.f_toCfCopyArray0(minStages).forEachIndexed { idx, f ->
            f.shouldNotBeSameInstanceAs(minStages[idx])
            if (isJava9Plus()) {
                LLCF.isMinStageCf(f).shouldBeTrue()

                f.shouldBeMinimalStage()
            } else {
                f.shouldBeCompleted()
                f.join() shouldBe n
            }
        }

        val cfs = arrayOf<CompletionStage<Int>>(
            completedFuture(n),
            testCffuFac.completedFuture(n),
        )
        LLCF.f_toCfCopyArray0(cfs).forEachIndexed { idx, f ->
            f.shouldNotBeSameInstanceAs(cfs[idx])
            f.shouldBeCompleted()
            f.join() shouldBe n

            f.shouldNotBeMinimalStage()
        }
    }

    test("toNonMinCfCopyArray0") {
        val stages = arrayOf(
            completedStage(n),
            testCffuFac.completedStage(n),
            completedFuture(n),
            testCffuFac.completedFuture(n),
        )
        LLCF.toNonMinCfCopyArray0(stages).forEachIndexed { idx, f ->
            f.shouldNotBeSameInstanceAs(stages[idx])
            f.shouldBeCompleted()
            f.join() shouldBe n

            f.shouldNotBeMinimalStage()
        }
    }

    test("peek0*") {
        val rte = RuntimeException("peekAsync exception")
        val testThread = currentThread()

        val f = failedFuture<String>(rte)
        var args: Pair<String, Throwable>? = null
        var inRightThread: Boolean? = null

        LLCF.peek0(f, { v, ex ->
            args = v to ex
            inRightThread = currentThread() == testThread
            throw RuntimeException("Another")
        }, "peek0 test")
        args.shouldBe(null to rte)
        inRightThread!!.shouldBeTrue()

        args = null
        inRightThread = null
        LLCF.peek0(f, { v, ex ->
            args = v to ex
            inRightThread = currentThread() == testThread
            throw rte
        }, "peek0 test")
        args.shouldBe(null to rte)
        inRightThread!!.shouldBeTrue()

        args = null
        inRightThread = null
        val latch = CountDownLatch(1)
        LLCF.peekAsync0(f, { v, ex ->
            args = v to ex
            inRightThread = currentThread().belongsTo(testExecutor)

            latch.countDown()
            throw RuntimeException("Another")
        }, "peek0 test", testExecutor)
        latch.await()
        args.shouldBe(null to rte)
        inRightThread!!.shouldBeTrue()

        args = null
        inRightThread = null
        val latch2 = CountDownLatch(1)
        LLCF.peekAsync0(f, { v, ex ->
            args = v to ex
            inRightThread = currentThread().belongsTo(testExecutor)

            latch2.countDown()
            throw rte
        }, "peek0 test", testExecutor)
        latch2.await()
        args.shouldBe(null to rte)
        inRightThread!!.shouldBeTrue()
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
