package io.foldright.aspect_test

import io.foldright.cffu.CompletableFutureUtils
import io.foldright.test_utils.*
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import java.lang.Thread.currentThread
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import java.util.function.*
import java.util.function.Function

/**
 * Check the executor parameter usage of all CompletableFutureUtils methods that has Executor parameter.
 */
class CheckExecutorOfCompletableFutureUtilsMethodsTests : FunSpec({
    (1..3).forEach { count ->
        test("Multi-Actions(M*) Methods with $count actions") {
            val am = ExTracingActionMaker()

            CompletableFutureUtils.mSupplyFailFastAsync(*am.createSuppliers(count))
            CompletableFutureUtils.mSupplyFailFastAsync(testExecutor, *am.createSuppliers(count, testExecutor))

            CompletableFutureUtils.mSupplyAllSuccessAsync(null, *am.createSuppliers(count))
            CompletableFutureUtils.mSupplyAllSuccessAsync<Int>(
                testExecutor,
                null,
                *am.createSuppliers(count, testExecutor)
            )

            CompletableFutureUtils.mSupplyMostSuccessAsync(
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createSuppliers(count)
            )
            CompletableFutureUtils.mSupplyMostSuccessAsync(
                testExecutor,
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createSuppliers(count, testExecutor)
            )

            CompletableFutureUtils.mSupplyAsync(*am.createSuppliers(count))
            CompletableFutureUtils.mSupplyAsync(testExecutor, *am.createSuppliers(count, testExecutor))

            CompletableFutureUtils.mSupplyAnySuccessAsync(*am.createSuppliers(count))
            CompletableFutureUtils.mSupplyAnySuccessAsync(
                testExecutor,
                *am.createSuppliers(count, testExecutor)
            )

            CompletableFutureUtils.mSupplyAnyAsync(*am.createSuppliers(count))
            CompletableFutureUtils.mSupplyAnyAsync(testExecutor, *am.createSuppliers(count, testExecutor))

            CompletableFutureUtils.mRunFailFastAsync(*am.createRunnables(count))
            CompletableFutureUtils.mRunFailFastAsync(testExecutor, *am.createRunnables(count, testExecutor))

            CompletableFutureUtils.mRunAsync(*am.createRunnables(count))
            CompletableFutureUtils.mRunAsync(testExecutor, *am.createRunnables(count, testExecutor))

            CompletableFutureUtils.mRunAnySuccessAsync(*am.createRunnables(count))
            CompletableFutureUtils.mRunAnySuccessAsync(testExecutor, *am.createRunnables(count, testExecutor))

            CompletableFutureUtils.mRunAnyAsync(*am.createRunnables(count))
            CompletableFutureUtils.mRunAnyAsync(testExecutor, *am.createRunnables(count, testExecutor))

            am.checkRunningExecutor()
        }
    }

    test("Multi-Actions-Tuple(MTuple*) Methods(create by actions)") {
        val am = ExTracingActionMaker()

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(testExecutor, sInTE1, sInTE2)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(testExecutor, sInTE1, sInTE2, sInTE3)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(testExecutor, sInTE1, sInTE2, sInTE3, sInTE4)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            CompletableFutureUtils.mSupplyTupleFailFastAsync(testExecutor, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(testExecutor, sInTE1, sInTE2)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(testExecutor, sInTE1, sInTE2, sInTE3)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(testExecutor, sInTE1, sInTE2, sInTE3, sInTE4)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            CompletableFutureUtils.mSupplyAllSuccessTupleAsync(testExecutor, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                sInCP1,
                sInCP2,
                sInCP3,
                sInCP4
            )
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                sInCP1,
                sInCP2,
                sInCP3,
                sInCP4,
                sInCP5
            )
        }

        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2,
                sInTE3
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2,
                sInTE3,
                sInTE4
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2,
                sInTE3,
                sInTE4,
                sInTE5
            )
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            CompletableFutureUtils.mSupplyTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            CompletableFutureUtils.mSupplyTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            CompletableFutureUtils.mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            CompletableFutureUtils.mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            CompletableFutureUtils.mSupplyTupleAsync(testExecutor, sInTE1, sInTE2)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            CompletableFutureUtils.mSupplyTupleAsync(testExecutor, sInTE1, sInTE2, sInTE3)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            CompletableFutureUtils.mSupplyTupleAsync(testExecutor, sInTE1, sInTE2, sInTE3, sInTE4)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            CompletableFutureUtils.mSupplyTupleAsync(testExecutor, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5)
        }

        am.checkRunningExecutor()
    }

    test("Delay Execution") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.delayedExecutor(1, MILLISECONDS).execute(am.createFutureTask())
        CompletableFutureUtils.delayedExecutor(1, MILLISECONDS, testExecutor).execute(am.createFutureTask(testExecutor))

        am.checkRunningExecutor()
    }

    val cfThis = CompletableFuture.completedFuture(n)

    (1..3).forEach { count ->
        test("Then-Multi-Actions(thenM*) Methods with $count actions") {
            val am = ExTracingActionMaker()

            CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyFailFastAsync(
                cfThis,
                testExecutor,
                *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, null, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAllSuccessAsync<Int, Int>(
                cfThis,
                testExecutor,
                null,
                *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyMostSuccessAsync(
                cfThis,
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createFunctions(count)
            )
            CompletableFutureUtils.thenMApplyMostSuccessAsync(
                cfThis,
                testExecutor,
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAsync(cfThis, testExecutor, *am.createFunctions(count, testExecutor))

            CompletableFutureUtils.thenMApplyAnySuccessAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAnySuccessAsync(
                cfThis,
                testExecutor,
                *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyAnyAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAnyAsync(
                cfThis,
                testExecutor,
                *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMAcceptFailFastAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptFailFastAsync(
                cfThis,
                testExecutor,
                *am.createConsumers(count, testExecutor)
            )

            CompletableFutureUtils.thenMAcceptAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptAsync(cfThis, testExecutor, *am.createConsumers(count, testExecutor))

            CompletableFutureUtils.thenMAcceptAnySuccessAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptAnySuccessAsync(
                cfThis,
                testExecutor,
                *am.createConsumers(count, testExecutor)
            )

            CompletableFutureUtils.thenMAcceptAnyAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptAnyAsync(
                cfThis,
                testExecutor,
                *am.createConsumers(count, testExecutor)
            )

            CompletableFutureUtils.thenMRunFailFastAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunFailFastAsync(
                cfThis,
                testExecutor,
                *am.createRunnables(count, testExecutor)
            )

            CompletableFutureUtils.thenMRunAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunAsync(cfThis, testExecutor, *am.createRunnables(count, testExecutor))

            CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunAnySuccessAsync(
                cfThis,
                testExecutor,
                *am.createRunnables(count, testExecutor)
            )

            CompletableFutureUtils.thenMRunAnyAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunAnyAsync(cfThis, testExecutor, *am.createRunnables(count, testExecutor))

            am.checkRunningExecutor()
        }
    }

    test("Then-Multi-Actions-Tuple(thenMTuple*) Methods") {
        val am = ExTracingActionMaker()

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, fInTE1, fInTE2)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3, fInTE4)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            CompletableFutureUtils.thenMApplyTupleFailFastAsync(
                cfThis,
                testExecutor,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4,
                fInTE5
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, fInTE1, fInTE2)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3, fInTE4)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(
                cfThis,
                testExecutor,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4,
                fInTE5
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInCP1,
                fInCP2,
                fInCP3
            )
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInCP1,
                fInCP2,
                fInCP3,
                fInCP4
            )
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInCP1,
                fInCP2,
                fInCP3,
                fInCP4,
                fInCP5
            )
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2,
                fInTE3
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cfThis,
                testExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4,
                fInTE5
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, testExecutor, fInTE1, fInTE2)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3, fInTE4)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            CompletableFutureUtils.thenMApplyTupleAsync(cfThis, testExecutor, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5)
        }

        am.checkRunningExecutor()
    }

    val other = CompletableFuture.completedFuture(anotherN)

    test("thenBoth* Methods(binary input) with fail-fast support") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.thenCombineFailFastAsync(cfThis, other, am.createBiFunction())
        CompletableFutureUtils.thenCombineFailFastAsync(cfThis, other, am.createBiFunction(testExecutor), testExecutor)

        CompletableFutureUtils.thenAcceptBothFailFastAsync(cfThis, other, am.createBiConsumer())
        CompletableFutureUtils.thenAcceptBothFailFastAsync(
            cfThis,
            other,
            am.createBiConsumer(testExecutor),
            testExecutor
        )

        CompletableFutureUtils.runAfterBothFailFastAsync(cfThis, other, am.createFutureTask())
        CompletableFutureUtils.runAfterBothFailFastAsync(cfThis, other, am.createFutureTask(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }

    test("thenEither* Methods(binary input) with either(any)-success support") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.applyToEitherSuccessAsync(cfThis, other, am.createFunctions(1)[0])
        CompletableFutureUtils.applyToEitherSuccessAsync(
            cfThis,
            other,
            am.createFunctions(1, testExecutor)[0],
            testExecutor
        )

        CompletableFutureUtils.acceptEitherSuccessAsync(cfThis, other, am.createConsumers(1)[0])
        CompletableFutureUtils.acceptEitherSuccessAsync(
            cfThis,
            other,
            am.createConsumers(1, testExecutor)[0],
            testExecutor
        )

        CompletableFutureUtils.runAfterEitherSuccessAsync(cfThis, other, am.createFutureTask())
        CompletableFutureUtils.runAfterEitherSuccessAsync(
            cfThis,
            other,
            am.createFutureTask(testExecutor),
            testExecutor
        )

        am.checkRunningExecutor()
    }

    test("Error Handling Methods of CompletionStage") {
        val am = ExTracingActionMaker()

        val failedCf = CompletableFutureUtils.failedFuture<Int>(RuntimeException("Failed"))

        CompletableFutureUtils.catchingAsync(failedCf, RuntimeException::class.java, am.createExFunction())
        CompletableFutureUtils.catchingAsync(
            failedCf,
            RuntimeException::class.java,
            am.createExFunction(testExecutor),
            testExecutor
        )

        CompletableFutureUtils.exceptionallyAsync(failedCf, am.createExFunction())
        CompletableFutureUtils.exceptionallyAsync(failedCf, am.createExFunction(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }
})

/**
 * Check the executor parameter usage of all Cffu/CffuFactory methods that has Executor parameter.
 */
class CheckExecutorOfCffuMethodsTests : FunSpec({
    val anotherExecutor = createThreadPool("CheckExecutorOfCffuMethodsTests")

    (1..3).forEach { count ->
        test("Multi-Actions(M*) Methods with $count actions") {
            val am = ExTracingActionMaker()

            testCffuFac.mSupplyFailFastAsync(*am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyFailFastAsync(anotherExecutor, *am.createSuppliers(count, anotherExecutor))

            testCffuFac.mSupplyAllSuccessAsync(null, *am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyAllSuccessAsync<Int>(
                anotherExecutor,
                null,
                *am.createSuppliers(count, anotherExecutor)
            )

            testCffuFac.mSupplyMostSuccessAsync(
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createSuppliers(count, testExecutor)
            )
            testCffuFac.mSupplyMostSuccessAsync(
                anotherExecutor,
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createSuppliers(count, anotherExecutor)
            )

            testCffuFac.mSupplyAsync(*am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyAsync(anotherExecutor, *am.createSuppliers(count, anotherExecutor))

            testCffuFac.mSupplyAnySuccessAsync(*am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyAnySuccessAsync(
                anotherExecutor,
                *am.createSuppliers(count, anotherExecutor)
            )

            testCffuFac.mSupplyAnyAsync(*am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyAnyAsync(anotherExecutor, *am.createSuppliers(count, anotherExecutor))

            testCffuFac.mRunFailFastAsync(*am.createRunnables(count, testExecutor))
            testCffuFac.mRunFailFastAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            testCffuFac.mRunAsync(*am.createRunnables(count, testExecutor))
            testCffuFac.mRunAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            testCffuFac.mRunAnySuccessAsync(*am.createRunnables(count, testExecutor))
            testCffuFac.mRunAnySuccessAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            testCffuFac.mRunAnyAsync(*am.createRunnables(count, testExecutor))
            testCffuFac.mRunAnyAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            am.checkRunningExecutor()
        }
    }

    test("Multi-Actions-Tuple(MTuple*) Methods(create by actions)") {
        val am = ExTracingActionMaker()

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(anotherExecutor, sInTE1, sInTE2)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(anotherExecutor, sInTE1, sInTE2, sInTE3)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(anotherExecutor, sInTE1, sInTE2, sInTE3, sInTE4)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.mSupplyTupleFailFastAsync(anotherExecutor, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(anotherExecutor, sInTE1, sInTE2)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(anotherExecutor, sInTE1, sInTE2, sInTE3)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(anotherExecutor, sInTE1, sInTE2, sInTE3, sInTE4)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.mSupplyAllSuccessTupleAsync(anotherExecutor, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                sInCP1,
                sInCP2,
                sInCP3,
                sInCP4
            )
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                sInCP1,
                sInCP2,
                sInCP3,
                sInCP4,
                sInCP5
            )
        }

        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2,
                sInTE3
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2,
                sInTE3,
                sInTE4
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.mSupplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                sInTE1,
                sInTE2,
                sInTE3,
                sInTE4,
                sInTE5
            )
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.mSupplyTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.mSupplyTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.mSupplyTupleAsync(anotherExecutor, sInTE1, sInTE2)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.mSupplyTupleAsync(anotherExecutor, sInTE1, sInTE2, sInTE3)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.mSupplyTupleAsync(anotherExecutor, sInTE1, sInTE2, sInTE3, sInTE4)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.mSupplyTupleAsync(anotherExecutor, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5)
        }

        am.checkRunningExecutor()
    }

    test("Delay Execution") {
        val am = ExTracingActionMaker()

        testCffuFac.delayedExecutor(1, MILLISECONDS).execute(am.createFutureTask(testExecutor))
        testCffuFac.delayedExecutor(1, MILLISECONDS, anotherExecutor).execute(am.createFutureTask(anotherExecutor))

        am.checkRunningExecutor()
    }

    val cfThis = testCffuFac.completedFuture(n)

    (1..3).forEach { count ->
        test("Then-Multi-Actions(thenM*) Methods with $count actions") {
            val am = ExTracingActionMaker()

            cfThis.thenMApplyFailFastAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyFailFastAsync(
                anotherExecutor,
                *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyAllSuccessAsync(null, *am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAllSuccessAsync<Int>(
                anotherExecutor, null, *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyMostSuccessAsync(
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createFunctions(count, testExecutor)
            )
            cfThis.thenMApplyMostSuccessAsync(
                anotherExecutor,
                null,
                LONG_WAIT_MS,
                MILLISECONDS,
                *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAsync(anotherExecutor, *am.createFunctions(count, anotherExecutor))

            cfThis.thenMApplyAnySuccessAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAnySuccessAsync(
                anotherExecutor,
                *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyAnyAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAnyAsync(
                anotherExecutor,
                *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMAcceptFailFastAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptFailFastAsync(
                anotherExecutor,
                *am.createConsumers(count, anotherExecutor)
            )

            cfThis.thenMAcceptAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptAsync(anotherExecutor, *am.createConsumers(count, anotherExecutor))

            cfThis.thenMAcceptAnySuccessAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptAnySuccessAsync(
                anotherExecutor,
                *am.createConsumers(count, anotherExecutor)
            )

            cfThis.thenMAcceptAnyAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptAnyAsync(
                anotherExecutor,
                *am.createConsumers(count, anotherExecutor)
            )

            cfThis.thenMRunFailFastAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunFailFastAsync(
                anotherExecutor,
                *am.createRunnables(count, anotherExecutor)
            )

            cfThis.thenMRunAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            cfThis.thenMRunAnySuccessAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunAnySuccessAsync(
                anotherExecutor,
                *am.createRunnables(count, anotherExecutor)
            )

            cfThis.thenMRunAnyAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunAnyAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            am.checkRunningExecutor()
        }
    }

    test("Then-Multi-Actions-Tuple(thenMTuple*) Methods") {
        val am = ExTracingActionMaker()

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.thenMApplyTupleFailFastAsync(fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.thenMApplyTupleFailFastAsync(fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.thenMApplyTupleFailFastAsync(fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.thenMApplyTupleFailFastAsync(fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.thenMApplyTupleFailFastAsync(anotherExecutor, fInTE1, fInTE2)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.thenMApplyTupleFailFastAsync(anotherExecutor, fInTE1, fInTE2, fInTE3)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.thenMApplyTupleFailFastAsync(anotherExecutor, fInTE1, fInTE2, fInTE3, fInTE4)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.thenMApplyTupleFailFastAsync(
                anotherExecutor,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4,
                fInTE5
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(anotherExecutor, fInTE1, fInTE2)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(anotherExecutor, fInTE1, fInTE2, fInTE3)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(anotherExecutor, fInTE1, fInTE2, fInTE3, fInTE4)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.thenMApplyAllSuccessTupleAsync(
                anotherExecutor,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4,
                fInTE5
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                fInCP1,
                fInCP2,
                fInCP3
            )
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                fInCP1,
                fInCP2,
                fInCP3,
                fInCP4
            )
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS,
                MILLISECONDS,
                fInCP1,
                fInCP2,
                fInCP3,
                fInCP4,
                fInCP5
            )
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2,
                fInTE3
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.thenMApplyMostSuccessTupleAsync(
                anotherExecutor,
                LONG_WAIT_MS,
                MILLISECONDS,
                fInTE1,
                fInTE2,
                fInTE3,
                fInTE4,
                fInTE5
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.thenMApplyTupleAsync(fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.thenMApplyTupleAsync(fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.thenMApplyTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.thenMApplyTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.thenMApplyTupleAsync(anotherExecutor, fInTE1, fInTE2)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.thenMApplyTupleAsync(anotherExecutor, fInTE1, fInTE2, fInTE3)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.thenMApplyTupleAsync(anotherExecutor, fInTE1, fInTE2, fInTE3, fInTE4)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.thenMApplyTupleAsync(anotherExecutor, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5)
        }

        am.checkRunningExecutor()
    }

    val other = CompletableFuture.completedFuture(anotherN)

    test("thenBoth* Methods(binary input) with fail-fast support") {
        val am = ExTracingActionMaker()

        cfThis.thenCombineFailFastAsync(other, am.createBiFunction(testExecutor))
        cfThis.thenCombineFailFastAsync(other, am.createBiFunction(anotherExecutor), anotherExecutor)

        cfThis.thenAcceptBothFailFastAsync(other, am.createBiConsumer(testExecutor))
        cfThis.thenAcceptBothFailFastAsync(
            other,
            am.createBiConsumer(anotherExecutor),
            anotherExecutor
        )

        cfThis.runAfterBothFailFastAsync(other, am.createFutureTask(testExecutor))
        cfThis.runAfterBothFailFastAsync(other, am.createFutureTask(anotherExecutor), anotherExecutor)

        am.checkRunningExecutor()
    }

    test("thenEither* Methods(binary input) with either(any)-success support") {
        val am = ExTracingActionMaker()

        cfThis.applyToEitherSuccessAsync(other, am.createFunctions(1, testExecutor)[0])
        cfThis.applyToEitherSuccessAsync(
            other,
            am.createFunctions(1, anotherExecutor)[0],
            anotherExecutor
        )

        cfThis.acceptEitherSuccessAsync(other, am.createConsumers(1, testExecutor)[0])
        cfThis.acceptEitherSuccessAsync(
            other,
            am.createConsumers(1, anotherExecutor)[0],
            anotherExecutor
        )

        cfThis.runAfterEitherSuccessAsync(other, am.createFutureTask(testExecutor))
        cfThis.runAfterEitherSuccessAsync(
            other,
            am.createFutureTask(anotherExecutor),
            anotherExecutor
        )

        am.checkRunningExecutor()
    }

    test("Error Handling Methods of CompletionStage") {
        val am = ExTracingActionMaker()

        val failedCf = testCffuFac.failedFuture<Int>(RuntimeException("Failed"))

        failedCf.catchingAsync(RuntimeException::class.java, am.createExFunction(testExecutor))
        failedCf.catchingAsync(
            RuntimeException::class.java,
            am.createExFunction(anotherExecutor),
            anotherExecutor
        )

        failedCf.exceptionallyAsync(am.createExFunction(testExecutor))
        failedCf.exceptionallyAsync(am.createExFunction(anotherExecutor), anotherExecutor)

        am.checkRunningExecutor()
    }
})

private val commonPool = ForkJoinPool.commonPool()

class ExTracingActionMaker(private val testingThread: Thread = currentThread()) {
    private var exceptionsHolder: CompletableFuture<List<Throwable>> = CompletableFuture.completedFuture(emptyList())

    fun createSuppliers(size: Int, executor: Executor = commonPool): Array<Supplier<Int>> = Array(size) { idx ->
        val exCf = createExCfMergedToHolder()
        Supplier {
            checkRunningAndRecordEx(executor, exCf)
            idx
        }
    }

    fun createRunnables(size: Int, executor: Executor = commonPool): Array<Runnable> = Array(size) { _ ->
        val exCf = createExCfMergedToHolder()
        Runnable {
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createFunctions(size: Int, executor: Executor = commonPool): Array<Function<Int, Int>> = Array(size) { idx ->
        val exCf = createExCfMergedToHolder()
        Function {
            checkRunningAndRecordEx(executor, exCf)
            idx + it
        }
    }

    fun createConsumers(size: Int, executor: Executor = commonPool): Array<Consumer<Int>> = Array(size) { _ ->
        val exCf = createExCfMergedToHolder()
        Consumer {
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createFutureTask(executor: Executor = commonPool): FutureTask<Int> {
        val exCf = createExCfMergedToHolder()
        return FutureTask {
            checkRunningAndRecordEx(executor, exCf)
            n
        }
    }

    fun createBiFunction(executor: Executor = commonPool): BiFunction<Int, Int, Int> {
        val exCf = createExCfMergedToHolder()
        return BiFunction { x, y ->
            checkRunningAndRecordEx(executor, exCf)
            x + y
        }
    }

    fun createBiConsumer(executor: Executor = commonPool): BiConsumer<Int, Int> {
        val exCf = createExCfMergedToHolder()
        return BiConsumer { _, _ ->
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createExFunction(executor: Executor = commonPool): Function<Throwable, Int> {
        val exCf = createExCfMergedToHolder()
        return Function {
            checkRunningAndRecordEx(executor, exCf)
            n
        }
    }

    fun checkRunningExecutor() {
        val exs = exceptionsHolder.get(3, SECONDS)
        if (exs.isNotEmpty()) {
            val msg = exs.joinToString("") { it.stackTraceToString() }
            fail("assertRunningExecutor failed. exceptions:\n$msg")
        }
    }

    private fun createExCfMergedToHolder(): CompletableFuture<Throwable> {
        val f = CompletableFuture<Throwable>()
        exceptionsHolder = exceptionsHolder.thenCombine(f) { es, e ->
            if (e == null) es else es + e
        }
        return f
    }

    private fun checkRunningAndRecordEx(executor: Executor, exCf: CompletableFuture<Throwable>) {
        try {
            if (executor === commonPool) assertRunningByFjCommonPool(testingThread)
            else assertRunningInExecutor(executor)
            exCf.complete(null)
        } catch (e: Throwable) {
            exCf.complete(e)
            // rethrow to caller
            throw e
        }
    }
}
