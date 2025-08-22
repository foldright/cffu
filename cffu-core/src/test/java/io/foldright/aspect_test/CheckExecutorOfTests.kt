package io.foldright.aspect_test

import io.foldright.cffu2.CfIterableUtils
import io.foldright.cffu2.CfParallelUtils
import io.foldright.cffu2.CfTupleUtils.*
import io.foldright.cffu2.CompletableFutureUtils
import io.foldright.cffu2.LLCF
import io.foldright.test_utils.*
import io.kotest.assertions.fail
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.concurrent.TimeUnit.*
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
                testExecutor, null, *am.createSuppliers(count, testExecutor)
            )

            CompletableFutureUtils.mSupplyMostSuccessAsync(
                null, LONG_WAIT_MS, MILLISECONDS, *am.createSuppliers(count)
            )
            CompletableFutureUtils.mSupplyMostSuccessAsync(
                testExecutor, null, LONG_WAIT_MS, MILLISECONDS, *am.createSuppliers(count, testExecutor)
            )

            CompletableFutureUtils.mSupplyAsync(*am.createSuppliers(count))
            CompletableFutureUtils.mSupplyAsync(testExecutor, *am.createSuppliers(count, testExecutor))

            CompletableFutureUtils.mSupplyAnySuccessAsync(*am.createSuppliers(count))
            CompletableFutureUtils.mSupplyAnySuccessAsync(
                testExecutor, *am.createSuppliers(count, testExecutor)
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

    test("mostSuccessResultsOf method") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.mostSuccessResultsOf(-1, SHORT_WAIT_MS, MILLISECONDS, incompleteCf())
            .thenRun(am.createRunnable())
        CompletableFutureUtils.mostSuccessResultsOf(testExecutor, -1, SHORT_WAIT_MS, MILLISECONDS, incompleteCf())
            .thenRun(am.createRunnable(testExecutor))

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
                cfThis, testExecutor, *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, null, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAllSuccessAsync<Int, Int>(
                cfThis, testExecutor, null, *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyMostSuccessAsync(
                cfThis, null, LONG_WAIT_MS, MILLISECONDS, *am.createFunctions(count)
            )
            CompletableFutureUtils.thenMApplyMostSuccessAsync(
                cfThis, testExecutor, null, LONG_WAIT_MS, MILLISECONDS, *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAsync(cfThis, testExecutor, *am.createFunctions(count, testExecutor))

            CompletableFutureUtils.thenMApplyAnySuccessAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAnySuccessAsync(
                cfThis, testExecutor, *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMApplyAnyAsync(cfThis, *am.createFunctions(count))
            CompletableFutureUtils.thenMApplyAnyAsync(
                cfThis, testExecutor, *am.createFunctions(count, testExecutor)
            )

            CompletableFutureUtils.thenMAcceptFailFastAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptFailFastAsync(
                cfThis, testExecutor, *am.createConsumers(count, testExecutor)
            )

            CompletableFutureUtils.thenMAcceptAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptAsync(cfThis, testExecutor, *am.createConsumers(count, testExecutor))

            CompletableFutureUtils.thenMAcceptAnySuccessAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptAnySuccessAsync(
                cfThis, testExecutor, *am.createConsumers(count, testExecutor)
            )

            CompletableFutureUtils.thenMAcceptAnyAsync(cfThis, *am.createConsumers(count))
            CompletableFutureUtils.thenMAcceptAnyAsync(
                cfThis, testExecutor, *am.createConsumers(count, testExecutor)
            )

            CompletableFutureUtils.thenMRunFailFastAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunFailFastAsync(
                cfThis, testExecutor, *am.createRunnables(count, testExecutor)
            )

            CompletableFutureUtils.thenMRunAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunAsync(cfThis, testExecutor, *am.createRunnables(count, testExecutor))

            CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunAnySuccessAsync(
                cfThis, testExecutor, *am.createRunnables(count, testExecutor)
            )

            CompletableFutureUtils.thenMRunAnyAsync(cfThis, *am.createRunnables(count))
            CompletableFutureUtils.thenMRunAnyAsync(cfThis, testExecutor, *am.createRunnables(count, testExecutor))

            am.checkRunningExecutor()
        }
    }

    val other = CompletableFuture.completedFuture(anotherN)

    test("thenBoth* Methods(binary input) with fail-fast support") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.thenCombineFailFastAsync(cfThis, other, am.createBiFunction())
        CompletableFutureUtils.thenCombineFailFastAsync(cfThis, other, am.createBiFunction(testExecutor), testExecutor)

        CompletableFutureUtils.thenAcceptBothFailFastAsync(cfThis, other, am.createBiConsumer())
        CompletableFutureUtils.thenAcceptBothFailFastAsync(
            cfThis, other, am.createBiConsumer(testExecutor), testExecutor
        )

        CompletableFutureUtils.runAfterBothFailFastAsync(cfThis, other, am.createFutureTask())
        CompletableFutureUtils.runAfterBothFailFastAsync(cfThis, other, am.createFutureTask(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }

    test("thenEither* Methods(binary input) with either(any)-success support") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.applyToEitherSuccessAsync(cfThis, other, am.createFunctions(1)[0])
        CompletableFutureUtils.applyToEitherSuccessAsync(
            cfThis, other, am.createFunctions(1, testExecutor)[0], testExecutor
        )

        CompletableFutureUtils.acceptEitherSuccessAsync(cfThis, other, am.createConsumers(1)[0])
        CompletableFutureUtils.acceptEitherSuccessAsync(
            cfThis, other, am.createConsumers(1, testExecutor)[0], testExecutor
        )

        CompletableFutureUtils.runAfterEitherSuccessAsync(cfThis, other, am.createFutureTask())
        CompletableFutureUtils.runAfterEitherSuccessAsync(
            cfThis, other, am.createFutureTask(testExecutor), testExecutor
        )

        am.checkRunningExecutor()
    }

    test("Error Handling Methods of CompletionStage") {
        val am = ExTracingActionMaker()

        val failedCf = CompletableFutureUtils.failedFuture<Int>(RuntimeException("Failed"))

        CompletableFutureUtils.catchingAsync(failedCf, RuntimeException::class.java, am.createExFunction())
        CompletableFutureUtils.catchingAsync(
            failedCf, RuntimeException::class.java, am.createExFunction(testExecutor), testExecutor
        )

        CompletableFutureUtils.exceptionallyAsync(failedCf, am.createExFunction())
        CompletableFutureUtils.exceptionallyAsync(failedCf, am.createExFunction(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }

    test("Timeout Control Methods of CompletableFuture") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.cffuOrTimeout(incompleteCf<Int>(), SHORT_WAIT_MS, MILLISECONDS)
            .exceptionally(am.createExHandleFunction())
        CompletableFutureUtils.cffuOrTimeout(incompleteCf<Int>(), SHORT_WAIT_MS, MILLISECONDS, testExecutor)
            .exceptionally(am.createExHandleFunction(testExecutor))

        CompletableFutureUtils.cffuCompleteOnTimeout(incompleteCf<Int>(), n, SHORT_WAIT_MS, MILLISECONDS)
            .thenRun(am.createRunnable())
        CompletableFutureUtils.cffuCompleteOnTimeout(incompleteCf<Int>(), n, SHORT_WAIT_MS, MILLISECONDS, testExecutor)
            .thenRun(am.createRunnable(testExecutor))

        am.checkRunningExecutor()
    }

    test("Advanced Methods of CompletionStage") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.catchingComposeAsync(
            CompletableFutureUtils.failedFuture<Int>(RuntimeException("Failed")),
            RuntimeException::class.java,
            am.createExHandleComposeFunction()
        )
        CompletableFutureUtils.catchingComposeAsync(
            CompletableFutureUtils.failedFuture<Int>(RuntimeException("Failed")),
            RuntimeException::class.java,
            am.createExHandleComposeFunction(testExecutor),
            testExecutor
        )

        CompletableFutureUtils.exceptionallyComposeAsync(
            CompletableFutureUtils.failedFuture<Int>(RuntimeException("Failed")),
            am.createExHandleComposeFunction()
        )
        CompletableFutureUtils.exceptionallyComposeAsync(
            CompletableFutureUtils.failedFuture<Int>(RuntimeException("Failed")),
            am.createExHandleComposeFunction(testExecutor),
            testExecutor
        )

        CompletableFutureUtils.peekAsync(cfThis, am.createExHandleBiConsumer())
        CompletableFutureUtils.peekAsync(cfThis, am.createExHandleBiConsumer(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }

    test("Write Methods of CompletableFuture") {
        val am = ExTracingActionMaker()

        CompletableFutureUtils.completeAsync(incompleteCf<Int>(), am.createSupplier())
        CompletableFutureUtils.completeAsync(incompleteCf<Int>(), am.createSupplier(testExecutor), testExecutor)

        CompletableFutureUtils.completeExceptionallyAsync(incompleteCf<Int>(), am.createExSupplier())
        CompletableFutureUtils.completeExceptionallyAsync(
            incompleteCf<Int>(), am.createExSupplier(testExecutor), testExecutor
        )

        am.checkRunningExecutor()
    }

    (1..3).forEach { count ->
        test("CfIterableUtils - Multi-Actions(M*) Methods with $count actions") {
            val am = ExTracingActionMaker()

            CfIterableUtils.mSupplyFailFastAsync(am.createSuppliers(count).asList())
            CfIterableUtils.mSupplyFailFastAsync(am.createSuppliers(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mSupplyAllSuccessAsync(null, am.createSuppliers(count).asList())
            CfIterableUtils.mSupplyAllSuccessAsync(
                null, am.createSuppliers(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.mSupplyMostSuccessAsync(
                null, LONG_WAIT_MS, MILLISECONDS, am.createSuppliers(count).asList()
            )
            CfIterableUtils.mSupplyMostSuccessAsync(
                null, LONG_WAIT_MS, MILLISECONDS, am.createSuppliers(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.mSupplyAsync(am.createSuppliers(count).asList())
            CfIterableUtils.mSupplyAsync(am.createSuppliers(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mSupplyAnySuccessAsync(am.createSuppliers(count).asList())
            CfIterableUtils.mSupplyAnySuccessAsync(am.createSuppliers(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mSupplyAnyAsync(am.createSuppliers(count).asList())
            CfIterableUtils.mSupplyAnyAsync(am.createSuppliers(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mRunFailFastAsync(am.createRunnables(count).asList())
            CfIterableUtils.mRunFailFastAsync(am.createRunnables(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mRunAsync(am.createRunnables(count).asList())
            CfIterableUtils.mRunAsync(am.createRunnables(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mRunAnySuccessAsync(am.createRunnables(count).asList())
            CfIterableUtils.mRunAnySuccessAsync(am.createRunnables(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.mRunAnyAsync(am.createRunnables(count).asList())
            CfIterableUtils.mRunAnyAsync(am.createRunnables(count, testExecutor).asList(), testExecutor)

            am.checkRunningExecutor()
        }
    }

    test("CfIterableUtils - mostSuccessResultsOf method") {
        val am = ExTracingActionMaker()

        CfIterableUtils.mostSuccessResultsOf(-1, SHORT_WAIT_MS, MILLISECONDS, listOf(incompleteCf()))
            .thenRun(am.createRunnable())
        CfIterableUtils.mostSuccessResultsOf(-1, SHORT_WAIT_MS, MILLISECONDS, listOf(incompleteCf()), testExecutor)
            .thenRun(am.createRunnable(testExecutor))

        am.checkRunningExecutor()
    }

    (1..3).forEach { count ->
        test("CfIterableUtils - Then-Multi-Actions(thenM*) Methods with $count actions") {
            val am = ExTracingActionMaker()

            CfIterableUtils.thenMApplyFailFastAsync(cfThis, am.createFunctions(count).asList())
            CfIterableUtils.thenMApplyFailFastAsync(
                cfThis, am.createFunctions(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMApplyAllSuccessAsync(cfThis, null, am.createFunctions(count).asList())
            CfIterableUtils.thenMApplyAllSuccessAsync<Int, Int>(
                cfThis, null, am.createFunctions(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMApplyMostSuccessAsync(
                cfThis, null, LONG_WAIT_MS, MILLISECONDS, am.createFunctions(count).asList()
            )
            CfIterableUtils.thenMApplyMostSuccessAsync(
                cfThis, null, LONG_WAIT_MS, MILLISECONDS, am.createFunctions(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMApplyAsync(cfThis, am.createFunctions(count).asList())
            CfIterableUtils.thenMApplyAsync(cfThis, am.createFunctions(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.thenMApplyAnySuccessAsync(cfThis, am.createFunctions(count).asList())
            CfIterableUtils.thenMApplyAnySuccessAsync(
                cfThis, am.createFunctions(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMApplyAnyAsync(cfThis, am.createFunctions(count).asList())
            CfIterableUtils.thenMApplyAnyAsync(
                cfThis, am.createFunctions(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMAcceptFailFastAsync(cfThis, am.createConsumers(count).asList())
            CfIterableUtils.thenMAcceptFailFastAsync(
                cfThis, am.createConsumers(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMAcceptAsync(cfThis, am.createConsumers(count).asList())
            CfIterableUtils.thenMAcceptAsync(cfThis, am.createConsumers(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.thenMAcceptAnySuccessAsync(cfThis, am.createConsumers(count).asList())
            CfIterableUtils.thenMAcceptAnySuccessAsync(
                cfThis, am.createConsumers(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMAcceptAnyAsync(cfThis, am.createConsumers(count).asList())
            CfIterableUtils.thenMAcceptAnyAsync(
                cfThis, am.createConsumers(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMRunFailFastAsync(cfThis, am.createRunnables(count).asList())
            CfIterableUtils.thenMRunFailFastAsync(
                cfThis, am.createRunnables(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMRunAsync(cfThis, am.createRunnables(count).asList())
            CfIterableUtils.thenMRunAsync(cfThis, am.createRunnables(count, testExecutor).asList(), testExecutor)

            CfIterableUtils.thenMRunAnySuccessAsync(cfThis, am.createRunnables(count).asList())
            CfIterableUtils.thenMRunAnySuccessAsync(
                cfThis, am.createRunnables(count, testExecutor).asList(), testExecutor
            )

            CfIterableUtils.thenMRunAnyAsync(cfThis, am.createRunnables(count).asList())
            CfIterableUtils.thenMRunAnyAsync(cfThis, am.createRunnables(count, testExecutor).asList(), testExecutor)

            am.checkRunningExecutor()
        }
    }

    test("CfParallelUtils - CF Factory Methods") {
        val am = ExTracingActionMaker()

        CfParallelUtils.parApplyFailFastAsync(listOf(1), am.createFunction())
        CfParallelUtils.parApplyFailFastAsync(listOf(1), am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.parApplyAllSuccessAsync(listOf(1), -1, am.createFunction())
        CfParallelUtils.parApplyAllSuccessAsync(listOf(1), -1, am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.parApplyMostSuccessAsync(listOf(1), -1, LONG_WAIT_MS, MILLISECONDS, am.createFunction())
        CfParallelUtils.parApplyMostSuccessAsync(
            listOf(1), -1, LONG_WAIT_MS, MILLISECONDS, am.createFunction(testExecutor), testExecutor
        )

        CfParallelUtils.parApplyAsync(listOf(1), am.createFunction())
        CfParallelUtils.parApplyAsync(listOf(1), am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.parApplyAnySuccessAsync(listOf(1), am.createFunction())
        CfParallelUtils.parApplyAnySuccessAsync(listOf(1), am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.parApplyAnyAsync(listOf(1), am.createFunction())
        CfParallelUtils.parApplyAnyAsync(listOf(1), am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.parAcceptFailFastAsync(listOf(1), am.createConsumer())
        CfParallelUtils.parAcceptFailFastAsync(listOf(1), am.createConsumer(testExecutor), testExecutor)

        CfParallelUtils.parAcceptAsync(listOf(1), am.createConsumer())
        CfParallelUtils.parAcceptAsync(listOf(1), am.createConsumer(testExecutor), testExecutor)

        CfParallelUtils.parAcceptAnySuccessAsync(listOf(1), am.createConsumer())
        CfParallelUtils.parAcceptAnySuccessAsync(listOf(1), am.createConsumer(testExecutor), testExecutor)

        CfParallelUtils.parAcceptAnyAsync(listOf(1), am.createConsumer())
        CfParallelUtils.parAcceptAnyAsync(listOf(1), am.createConsumer(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }

    val listCfThis = CompletableFuture.completedFuture(listOf(n))

    test("CfParallelUtils - CF Instance Methods") {
        val am = ExTracingActionMaker()

        CfParallelUtils.thenParApplyFailFastAsync(listCfThis, am.createFunction())
        CfParallelUtils.thenParApplyFailFastAsync(listCfThis, am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.thenParApplyAllSuccessAsync(listCfThis, -1, am.createFunction())
        CfParallelUtils.thenParApplyAllSuccessAsync(listCfThis, -1, am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.thenParApplyMostSuccessAsync(listCfThis, -1, LONG_WAIT_MS, MILLISECONDS, am.createFunction())
        CfParallelUtils.thenParApplyMostSuccessAsync(
            listCfThis, -1, LONG_WAIT_MS, MILLISECONDS, am.createFunction(testExecutor), testExecutor
        )

        CfParallelUtils.thenParApplyAsync(listCfThis, am.createFunction())
        CfParallelUtils.thenParApplyAsync(listCfThis, am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.thenParApplyAnySuccessAsync(listCfThis, am.createFunction())
        CfParallelUtils.thenParApplyAnySuccessAsync(listCfThis, am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.thenParApplyAnyAsync(listCfThis, am.createFunction())
        CfParallelUtils.thenParApplyAnyAsync(listCfThis, am.createFunction(testExecutor), testExecutor)

        CfParallelUtils.thenParAcceptFailFastAsync(listCfThis, am.createConsumer())
        CfParallelUtils.thenParAcceptFailFastAsync(listCfThis, am.createConsumer(testExecutor), testExecutor)

        CfParallelUtils.thenParAcceptAsync(listCfThis, am.createConsumer())
        CfParallelUtils.thenParAcceptAsync(listCfThis, am.createConsumer(testExecutor), testExecutor)

        CfParallelUtils.thenParAcceptAnySuccessAsync(listCfThis, am.createConsumer())
        CfParallelUtils.thenParAcceptAnySuccessAsync(listCfThis, am.createConsumer(testExecutor), testExecutor)

        CfParallelUtils.thenParAcceptAnyAsync(listCfThis, am.createConsumer())
        CfParallelUtils.thenParAcceptAnyAsync(listCfThis, am.createConsumer(testExecutor), testExecutor)

        am.checkRunningExecutor()
    }


    test("Multi-Actions-Tuple(MTuple*) Methods(create by actions)") {
        val am = ExTracingActionMaker()

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            mSupplyTupleFailFastAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            mSupplyTupleFailFastAsync(sInTE1, sInTE2, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            mSupplyTupleFailFastAsync(sInTE1, sInTE2, sInTE3, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            mSupplyTupleFailFastAsync(sInTE1, sInTE2, sInTE3, sInTE4, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            mSupplyTupleFailFastAsync(sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, testExecutor)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            mSupplyAllSuccessTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, sInTE3, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, testExecutor)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }

        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, sInTE3, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, sInTE3, sInTE4, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, testExecutor
            )
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2)
            mSupplyTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3)
            mSupplyTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4)
            mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5)
            mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, testExecutor)
            mSupplyTupleAsync(sInTE1, sInTE2, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, testExecutor)
            mSupplyTupleAsync(sInTE1, sInTE2, sInTE3, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, testExecutor)
            mSupplyTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, testExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, testExecutor)
            mSupplyTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, testExecutor)
        }

        am.checkRunningExecutor()
    }

    test("mostSuccessTupleOf method") {
        val am = ExTracingActionMaker()

        mostSuccessTupleOf(SHORT_WAIT_MS, MILLISECONDS, incompleteCf<Int>(), incompleteCf<Char>())
            .thenRun(am.createRunnable())
        mostSuccessTupleOf(testExecutor, SHORT_WAIT_MS, MILLISECONDS, incompleteCf<Int>(), incompleteCf<Char>())
            .thenRun(am.createRunnable(testExecutor))

        mostSuccessTupleOf(SHORT_WAIT_MS, MILLISECONDS, incompleteCf<Int>(), incompleteCf<Char>(), incompleteCf<Int>())
            .thenRun(am.createRunnable())
        mostSuccessTupleOf(
            testExecutor, SHORT_WAIT_MS, MILLISECONDS, incompleteCf<Int>(), incompleteCf<Char>(), incompleteCf<Int>()
        ).thenRun(am.createRunnable(testExecutor))

        mostSuccessTupleOf(
            SHORT_WAIT_MS,
            MILLISECONDS,
            incompleteCf<Int>(),
            incompleteCf<Char>(),
            incompleteCf<Int>(),
            incompleteCf<Int>()
        ).thenRun(am.createRunnable())
        mostSuccessTupleOf(
            testExecutor,
            SHORT_WAIT_MS,
            MILLISECONDS,
            incompleteCf<Int>(),
            incompleteCf<Char>(),
            incompleteCf<Int>(),
            incompleteCf<Int>()
        ).thenRun(am.createRunnable(testExecutor))

        mostSuccessTupleOf(
            SHORT_WAIT_MS,
            MILLISECONDS,
            incompleteCf<Int>(),
            incompleteCf<Char>(),
            incompleteCf<Int>(),
            incompleteCf<Int>(),
            incompleteCf<Int>()
        ).thenRun(am.createRunnable())
        mostSuccessTupleOf(
            testExecutor,
            SHORT_WAIT_MS,
            MILLISECONDS,
            incompleteCf<Int>(),
            incompleteCf<Char>(),
            incompleteCf<Int>(),
            incompleteCf<Int>(),
            incompleteCf<Int>()
        ).thenRun(am.createRunnable(testExecutor))

        am.checkRunningExecutor()
    }

    test("Then-Multi-Actions-Tuple(thenMTuple*) Methods") {
        val am = ExTracingActionMaker()

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            thenMApplyTupleFailFastAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            thenMApplyTupleFailFastAsync(cfThis, fInTE1, fInTE2, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            thenMApplyTupleFailFastAsync(cfThis, fInTE1, fInTE2, fInTE3, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            thenMApplyTupleFailFastAsync(cfThis, fInTE1, fInTE2, fInTE3, fInTE4, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            thenMApplyTupleFailFastAsync(cfThis, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, testExecutor)
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            thenMApplyAllSuccessTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            thenMApplyAllSuccessTupleAsync(cfThis, fInTE1, fInTE2, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            thenMApplyAllSuccessTupleAsync(cfThis, fInTE1, fInTE2, fInTE3, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            thenMApplyAllSuccessTupleAsync(cfThis, fInTE1, fInTE2, fInTE3, fInTE4, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            thenMApplyAllSuccessTupleAsync(cfThis, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, testExecutor)
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            thenMApplyMostSuccessTupleAsync(cfThis, LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, fInTE3, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            thenMApplyMostSuccessTupleAsync(
                cfThis, LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, fInTE3, fInTE4, testExecutor
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            thenMApplyMostSuccessTupleAsync(
                cfThis, LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, testExecutor
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2)
            thenMApplyTupleAsync(cfThis, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3)
            thenMApplyTupleAsync(cfThis, fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4)
            thenMApplyTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5)
            thenMApplyTupleAsync(cfThis, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, testExecutor)
            thenMApplyTupleAsync(cfThis, fInTE1, fInTE2, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, testExecutor)
            thenMApplyTupleAsync(cfThis, fInTE1, fInTE2, fInTE3, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, testExecutor)
            thenMApplyTupleAsync(cfThis, fInTE1, fInTE2, fInTE3, fInTE4, testExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, testExecutor)
            thenMApplyTupleAsync(cfThis, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, testExecutor)
        }

        am.checkRunningExecutor()
    }

    beforeEach {
        ForkJoinPool.commonPool().awaitQuiescence(1, MINUTES).shouldBeTrue()
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
                anotherExecutor, null, *am.createSuppliers(count, anotherExecutor)
            )

            testCffuFac.mSupplyMostSuccessAsync(
                null, LONG_WAIT_MS, MILLISECONDS, *am.createSuppliers(count, testExecutor)
            )
            testCffuFac.mSupplyMostSuccessAsync(
                anotherExecutor, null, LONG_WAIT_MS, MILLISECONDS, *am.createSuppliers(count, anotherExecutor)
            )

            testCffuFac.mSupplyAsync(*am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyAsync(anotherExecutor, *am.createSuppliers(count, anotherExecutor))

            testCffuFac.mSupplyAnySuccessAsync(*am.createSuppliers(count, testExecutor))
            testCffuFac.mSupplyAnySuccessAsync(
                anotherExecutor, *am.createSuppliers(count, anotherExecutor)
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
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInTE1, sInTE2, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInTE1, sInTE2, sInTE3, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInTE1, sInTE2, sInTE3, sInTE4, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleFailFastAsync(sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, anotherExecutor)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, sInTE3, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, anotherExecutor)
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3, sInCP4
            )
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInCP1, sInCP2, sInCP3, sInCP4, sInCP5
            )
        }

        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, anotherExecutor
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, sInTE3, anotherExecutor
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, sInTE3, sInTE4, anotherExecutor
            )
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, anotherExecutor
            )
        }

        run {
            val (sInCP1, sInCP2) = am.createSuppliers(2, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInCP1, sInCP2)
        }
        run {
            val (sInCP1, sInCP2, sInCP3) = am.createSuppliers(3, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInCP1, sInCP2, sInCP3)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4) = am.createSuppliers(4, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4)
        }
        run {
            val (sInCP1, sInCP2, sInCP3, sInCP4, sInCP5) = am.createSuppliers(5, testExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInCP1, sInCP2, sInCP3, sInCP4, sInCP5)
        }
        run {
            val (sInTE1, sInTE2) = am.createSuppliers(2, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInTE1, sInTE2, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3) = am.createSuppliers(3, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInTE1, sInTE2, sInTE3, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4) = am.createSuppliers(4, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, anotherExecutor)
        }
        run {
            val (sInTE1, sInTE2, sInTE3, sInTE4, sInTE5) = am.createSuppliers(5, anotherExecutor)
            testCffuFac.tupleOps().mSupplyTupleAsync(sInTE1, sInTE2, sInTE3, sInTE4, sInTE5, anotherExecutor)
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
                anotherExecutor, *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyAllSuccessAsync(null, *am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAllSuccessAsync<Int>(
                anotherExecutor, null, *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyMostSuccessAsync(
                null, LONG_WAIT_MS, MILLISECONDS, *am.createFunctions(count, testExecutor)
            )
            cfThis.thenMApplyMostSuccessAsync(
                anotherExecutor, null, LONG_WAIT_MS, MILLISECONDS, *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAsync(anotherExecutor, *am.createFunctions(count, anotherExecutor))

            cfThis.thenMApplyAnySuccessAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAnySuccessAsync(
                anotherExecutor, *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMApplyAnyAsync(*am.createFunctions(count, testExecutor))
            cfThis.thenMApplyAnyAsync(
                anotherExecutor, *am.createFunctions(count, anotherExecutor)
            )

            cfThis.thenMAcceptFailFastAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptFailFastAsync(
                anotherExecutor, *am.createConsumers(count, anotherExecutor)
            )

            cfThis.thenMAcceptAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptAsync(anotherExecutor, *am.createConsumers(count, anotherExecutor))

            cfThis.thenMAcceptAnySuccessAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptAnySuccessAsync(
                anotherExecutor, *am.createConsumers(count, anotherExecutor)
            )

            cfThis.thenMAcceptAnyAsync(*am.createConsumers(count, testExecutor))
            cfThis.thenMAcceptAnyAsync(
                anotherExecutor, *am.createConsumers(count, anotherExecutor)
            )

            cfThis.thenMRunFailFastAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunFailFastAsync(
                anotherExecutor, *am.createRunnables(count, anotherExecutor)
            )

            cfThis.thenMRunAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunAsync(anotherExecutor, *am.createRunnables(count, anotherExecutor))

            cfThis.thenMRunAnySuccessAsync(*am.createRunnables(count, testExecutor))
            cfThis.thenMRunAnySuccessAsync(
                anotherExecutor, *am.createRunnables(count, anotherExecutor)
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
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInTE1, fInTE2, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInTE1, fInTE2, fInTE3, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(fInTE1, fInTE2, fInTE3, fInTE4, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleFailFastAsync(
                fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, anotherExecutor
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInTE1, fInTE2, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInTE1, fInTE2, fInTE3, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(fInTE1, fInTE2, fInTE3, fInTE4, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.tupleOps().thenMApplyAllSuccessTupleAsync(
                fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, anotherExecutor
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2, fInCP3
            )
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2, fInCP3, fInCP4
            )
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInCP1, fInCP2, fInCP3, fInCP4, fInCP5
            )
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, anotherExecutor
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, fInTE3, anotherExecutor
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, fInTE3, fInTE4, anotherExecutor
            )
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.tupleOps().thenMApplyMostSuccessTupleAsync(
                LONG_WAIT_MS, MILLISECONDS, fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, anotherExecutor
            )
        }

        run {
            val (fInCP1, fInCP2) = am.createFunctions(2, testExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInCP1, fInCP2)
        }
        run {
            val (fInCP1, fInCP2, fInCP3) = am.createFunctions(3, testExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInCP1, fInCP2, fInCP3)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4) = am.createFunctions(4, testExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4)
        }
        run {
            val (fInCP1, fInCP2, fInCP3, fInCP4, fInCP5) = am.createFunctions(5, testExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInCP1, fInCP2, fInCP3, fInCP4, fInCP5)
        }
        run {
            val (fInTE1, fInTE2) = am.createFunctions(2, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInTE1, fInTE2, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3) = am.createFunctions(3, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInTE1, fInTE2, fInTE3, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4) = am.createFunctions(4, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInTE1, fInTE2, fInTE3, fInTE4, anotherExecutor)
        }
        run {
            val (fInTE1, fInTE2, fInTE3, fInTE4, fInTE5) = am.createFunctions(5, anotherExecutor)
            cfThis.tupleOps().thenMApplyTupleAsync(fInTE1, fInTE2, fInTE3, fInTE4, fInTE5, anotherExecutor)
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
            other, am.createBiConsumer(anotherExecutor), anotherExecutor
        )

        cfThis.runAfterBothFailFastAsync(other, am.createFutureTask(testExecutor))
        cfThis.runAfterBothFailFastAsync(other, am.createFutureTask(anotherExecutor), anotherExecutor)

        am.checkRunningExecutor()
    }

    test("thenEither* Methods(binary input) with either(any)-success support") {
        val am = ExTracingActionMaker()

        cfThis.applyToEitherSuccessAsync(other, am.createFunctions(1, testExecutor)[0])
        cfThis.applyToEitherSuccessAsync(
            other, am.createFunctions(1, anotherExecutor)[0], anotherExecutor
        )

        cfThis.acceptEitherSuccessAsync(other, am.createConsumers(1, testExecutor)[0])
        cfThis.acceptEitherSuccessAsync(
            other, am.createConsumers(1, anotherExecutor)[0], anotherExecutor
        )

        cfThis.runAfterEitherSuccessAsync(other, am.createFutureTask(testExecutor))
        cfThis.runAfterEitherSuccessAsync(
            other, am.createFutureTask(anotherExecutor), anotherExecutor
        )

        am.checkRunningExecutor()
    }

    test("Error Handling Methods of CompletionStage") {
        val am = ExTracingActionMaker()

        val failedCf = testCffuFac.failedFuture<Int>(RuntimeException("Failed"))

        failedCf.catchingAsync(RuntimeException::class.java, am.createExFunction(testExecutor))
        failedCf.catchingAsync(
            RuntimeException::class.java, am.createExFunction(anotherExecutor), anotherExecutor
        )

        failedCf.exceptionallyAsync(am.createExFunction(testExecutor))
        failedCf.exceptionallyAsync(am.createExFunction(anotherExecutor), anotherExecutor)

        am.checkRunningExecutor()
    }
})

class ExTracingActionMaker(private val testingThread: Thread = currentThread()) {
    private var exceptionsHolder: CompletableFuture<List<Throwable>> = CompletableFuture.completedFuture(emptyList())

    fun createSuppliers(size: Int, executor: Executor = LLCF.ASYNC_POOL): Array<Supplier<Int>> = Array(size) { idx ->
        val exCf = createExCfMergedToHolder()
        Supplier {
            checkRunningAndRecordEx(executor, exCf)
            idx
        }
    }

    fun createSupplier(executor: Executor = LLCF.ASYNC_POOL): Supplier<Int> {
        val exCf = createExCfMergedToHolder()
        return Supplier {
            checkRunningAndRecordEx(executor, exCf)
            n
        }
    }

    fun createExSupplier(executor: Executor = LLCF.ASYNC_POOL): Supplier<Throwable> {
        val exCf = createExCfMergedToHolder()
        return Supplier {
            checkRunningAndRecordEx(executor, exCf)
            RuntimeException("createExSupplier")
        }
    }

    fun createRunnables(size: Int, executor: Executor = LLCF.ASYNC_POOL): Array<Runnable> = Array(size) { _ ->
        val exCf = createExCfMergedToHolder()
        Runnable {
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createRunnable(executor: Executor = LLCF.ASYNC_POOL): Runnable {
        val exCf = createExCfMergedToHolder()
        return Runnable {
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createFunctions(size: Int, executor: Executor = LLCF.ASYNC_POOL): Array<Function<Int, Int>> =
        Array(size) { idx ->
            val exCf = createExCfMergedToHolder()
            Function {
                checkRunningAndRecordEx(executor, exCf)
                idx + it
            }
        }

    fun createFunction(executor: Executor = LLCF.ASYNC_POOL): Function<Int, Int> {
        val exCf = createExCfMergedToHolder()
        return Function {
            checkRunningAndRecordEx(executor, exCf)
            n
        }
    }

    fun createExHandleFunction(executor: Executor = LLCF.ASYNC_POOL): Function<Throwable, Int> {
        val exCf = createExCfMergedToHolder()
        return Function {
            checkRunningAndRecordEx(executor, exCf)
            n
        }
    }

    fun createExHandleComposeFunction(executor: Executor = LLCF.ASYNC_POOL): Function<Throwable, CompletionStage<Int>> {
        val exCf = createExCfMergedToHolder()
        return Function {
            checkRunningAndRecordEx(executor, exCf)
            CompletableFuture()
        }
    }

    fun createConsumers(size: Int, executor: Executor = LLCF.ASYNC_POOL): Array<Consumer<Int>> = Array(size) { _ ->
        val exCf = createExCfMergedToHolder()
        Consumer {
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun <T> createConsumer(executor: Executor = LLCF.ASYNC_POOL): Consumer<T> {
        val exCf = createExCfMergedToHolder()
        return Consumer {
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createFutureTask(executor: Executor = LLCF.ASYNC_POOL): FutureTask<Int> {
        val exCf = createExCfMergedToHolder()
        return FutureTask {
            checkRunningAndRecordEx(executor, exCf)
            n
        }
    }

    fun createBiFunction(executor: Executor = LLCF.ASYNC_POOL): BiFunction<Int, Int, Int> {
        val exCf = createExCfMergedToHolder()
        return BiFunction { x, y ->
            checkRunningAndRecordEx(executor, exCf)
            x + y
        }
    }

    fun createBiConsumer(executor: Executor = LLCF.ASYNC_POOL): BiConsumer<Int, Int> {
        val exCf = createExCfMergedToHolder()
        return BiConsumer { _, _ ->
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createExHandleBiConsumer(executor: Executor = LLCF.ASYNC_POOL): BiConsumer<Int, Throwable?> {
        val exCf = createExCfMergedToHolder()
        return BiConsumer { _, _ ->
            checkRunningAndRecordEx(executor, exCf)
        }
    }

    fun createExFunction(executor: Executor = LLCF.ASYNC_POOL): Function<Throwable, Int> {
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
            if (executor === LLCF.ASYNC_POOL) assertRunningInCfAsyncPool(testingThread)
            else assertRunningInExecutor(executor)
            exCf.complete(null)
        } catch (e: Throwable) {
            exCf.complete(e)
            // rethrow to caller
            throw e
        }
    }
}
