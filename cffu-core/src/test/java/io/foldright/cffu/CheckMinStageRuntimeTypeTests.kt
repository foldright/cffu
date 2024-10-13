package io.foldright.cffu

import io.foldright.test_utils.*
import org.junit.jupiter.api.Test
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.function.Function
import java.util.function.Supplier

@Suppress("MoveLambdaOutsideParentheses")
private class CheckMinStageRuntimeTypeTests {
    val cfThis: CompletableFuture<String> = completedFuture("cf this")
    val csThis: CompletionStage<String> = CompletableFutureUtils.completedStage("cs this")

    val cfN: CompletableFuture<Int> = completedFuture(n)
    val cfAn: CompletableFuture<Int> = completedFuture(anotherN)
    val cfS: CompletableFuture<String> = completedFuture(s)
    val cfD: CompletableFuture<Double> = completedFuture(d)

    @Test
    fun test_CompletableFutureUtils_methods() {
        // Multi-Actions(M*) Methods(create by actions)

        CompletableFutureUtils.mSupplyFailFastAsync<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyFailFastAsync(Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyFailFastAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyFailFastAsync<String>(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyFailFastAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyFailFastAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mSupplyAllSuccessAsync<String>("").shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync("", Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync("", Supplier { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync<String>("", testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync("", testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync("", testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mSupplyMostSuccessAsync<String>("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync("", 1, MILLISECONDS, Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync("", 1, MILLISECONDS, Supplier { s }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync<String>("", testExecutor, 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync("", testExecutor, 1, MILLISECONDS, Supplier { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync("", testExecutor, 1, MILLISECONDS, Supplier { s }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.mSupplyAsync<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAsync(Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAsync<String>(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mSupplyAnySuccessAsync<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnySuccessAsync(Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnySuccessAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnySuccessAsync<String>(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnySuccessAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnySuccessAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mSupplyAnyAsync<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnyAsync(Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnyAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnyAsync<String>(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnyAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAnyAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mRunFailFastAsync().shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunFailFastAsync(Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunFailFastAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunFailFastAsync(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunFailFastAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunFailFastAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mRunAsync().shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAsync(Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAsync(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mRunAnySuccessAsync().shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnySuccessAsync(Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnySuccessAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnySuccessAsync(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnySuccessAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnySuccessAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mRunAnyAsync().shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnyAsync(Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnyAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnyAsync(testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnyAsync(testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mRunAnyAsync(testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        // Tuple-Multi-Actions(tupleM*) Methods(create by actions)

        CompletableFutureUtils.tupleMSupplyFailFastAsync({ n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(Supplier { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyFailFastAsync(testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.tupleMSupplyAllSuccessAsync({ n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(Supplier { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(Supplier { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAllSuccessAsync(testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(1, MILLISECONDS, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(testExecutor, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(testExecutor, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(1, MILLISECONDS, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyMostSuccessAsync(
            testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.tupleMSupplyAsync({ n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(testExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.tupleMSupplyAsync(testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        // allOf* Methods(including mostSuccessResultsOf)

        CompletableFutureUtils.allResultsFailFastOf<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.allResultsFailFastOf(cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allResultsFailFastOf(cfN, cfAn).shouldNotBeMinimalStage()

        CompletableFutureUtils.allSuccessResultsOf("").shouldNotBeMinimalStage()
        CompletableFutureUtils.allSuccessResultsOf("", cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allSuccessResultsOf("", cfN, cfAn).shouldNotBeMinimalStage()

        CompletableFutureUtils.mostSuccessResultsOf("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf("", 1, MILLISECONDS, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf("", 1, MILLISECONDS, cfN, cfAn).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf("", testExecutor, 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf("", testExecutor, 1, MILLISECONDS, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf("", testExecutor, 1, MILLISECONDS, cfN, cfAn)
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.allResultsOf<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.allResultsOf(cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allResultsOf(cfN, cfAn).shouldNotBeMinimalStage()

        CompletableFutureUtils.allFailFastOf().shouldNotBeMinimalStage()
        CompletableFutureUtils.allFailFastOf(cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allFailFastOf(cfN, cfAn).shouldNotBeMinimalStage()

        CompletableFutureUtils.allOf().shouldNotBeMinimalStage()
        CompletableFutureUtils.allOf(cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allOf(cfN, cfAn).shouldNotBeMinimalStage()

        CompletableFutureUtils.anySuccessOf<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.anySuccessOf(cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.anySuccessOf(cfN, cfAn).shouldNotBeMinimalStage()

        CompletableFutureUtils.anyOf<String>().shouldNotBeMinimalStage()
        CompletableFutureUtils.anyOf(cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.anyOf(cfN, cfAn).shouldNotBeMinimalStage()

        // allTupleOf*/mostSuccessTupleOf Methods

        CompletableFutureUtils.allTupleFailFastOf(cfN, cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.allTupleFailFastOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        CompletableFutureUtils.allTupleFailFastOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allTupleFailFastOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        CompletableFutureUtils.allSuccessTupleOf(cfN, cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.allSuccessTupleOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        CompletableFutureUtils.allSuccessTupleOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allSuccessTupleOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        CompletableFutureUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS, cfD)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS, cfD, cfN)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS, cfD, cfN, cfS)
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.allTupleOf(cfN, cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.allTupleOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        CompletableFutureUtils.allTupleOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.allTupleOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        // Immediate Value Argument Factory Methods(backport methods)

        CompletableFutureUtils.failedFuture<String>(rte).shouldNotBeMinimalStage()

        CompletableFutureUtils.completedStage("").shouldBeMinimalStageAsCF()
        CompletableFutureUtils.failedStage<String>(rte).shouldBeMinimalStageAsCF()

        // Then-Multi-Actions(thenM*) Methods

        CompletableFutureUtils.thenMApplyFailFastAsync<String, String>(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, Function { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, Function { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync<String, String>(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, "").shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, "", Function { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, "", Function { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, "", testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, "", testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, "", testExecutor, { s }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMApplyMostSuccessAsync<String, String>(cfThis, "", 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, "", 1, MILLISECONDS, Function { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, "", 1, MILLISECONDS, Function { s }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, "", testExecutor, 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, "", testExecutor, 1, MILLISECONDS, Function { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(
            cfThis,
            "",
            testExecutor,
            1,
            MILLISECONDS,
            Function { s },
            { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMApplyAsync<String, String>(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAsync(cfThis, Function { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAsync(cfThis, Function { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAsync<String, String>(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAsync(cfThis, testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAsync(cfThis, testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMApplyAnySuccessAsync<String, String>(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnySuccessAsync(cfThis, Function { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnySuccessAsync(cfThis, Function { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnySuccessAsync<String, String>(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnySuccessAsync(cfThis, testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnySuccessAsync(cfThis, testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMApplyAnyAsync<String, String>(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnyAsync(cfThis, Function { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnyAsync(cfThis, Function { s }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnyAsync<String, String>(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnyAsync(cfThis, testExecutor, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAnyAsync(cfThis, testExecutor, { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMRunFailFastAsync(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunFailFastAsync(cfThis, Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunFailFastAsync(cfThis, Runnable { }, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunFailFastAsync(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunFailFastAsync(cfThis, testExecutor, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunFailFastAsync(cfThis, testExecutor, { }, { }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMRunAsync(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAsync(cfThis, Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAsync(cfThis, Runnable { }, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAsync(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAsync(cfThis, testExecutor, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAsync(cfThis, testExecutor, { }, { }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, Runnable { }, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, testExecutor, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnySuccessAsync(cfThis, testExecutor, { }, { }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMRunAnyAsync(cfThis).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnyAsync(cfThis, Runnable { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnyAsync(cfThis, Runnable { }, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnyAsync(cfThis, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnyAsync(cfThis, testExecutor, { }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMRunAnyAsync(cfThis, testExecutor, { }, { }).shouldNotBeMinimalStage()

        // Then-Tuple-Multi-Actions(thenTupleM*) Methods

        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, Function { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, testExecutor, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, Function { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, Function { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyFailFastAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, testExecutor, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, Function { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, testExecutor, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, Function { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, Function { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(cfThis, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(cfThis, testExecutor, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(cfThis, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(
            cfThis, testExecutor, 1, MILLISECONDS, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(cfThis, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(
            cfThis, testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(
            cfThis, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(
            cfThis, testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, Function { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, Function { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenTupleMApplyAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        // thenBoth* Methods(binary input) with fail-fast support

        CompletableFutureUtils.thenCombineFailFast(cfThis, cfD, { t, u -> t + u }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenCombineFailFastAsync(cfThis, cfD, { t, u -> t + u }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenCombineFailFastAsync(cfThis, cfD, { t, u -> t + u }, testExecutor)
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.thenAcceptBothFailFast(cfThis, cfD, { _, _ -> }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenAcceptBothFailFastAsync(cfThis, cfD, { _, _ -> }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenAcceptBothFailFastAsync(cfThis, cfD, { _, _ -> }, testExecutor)
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.runAfterBothFailFast(cfThis, cfD, {}).shouldNotBeMinimalStage()
        CompletableFutureUtils.runAfterBothFailFastAsync(cfThis, cfD, {}).shouldNotBeMinimalStage()
        CompletableFutureUtils.runAfterBothFailFastAsync(cfThis, cfD, {}, testExecutor)
            .shouldNotBeMinimalStage()

        // thenEither* Methods(binary input) with either(any)-success support

        CompletableFutureUtils.applyToEitherSuccess(cfThis, cfS, { s + it }).shouldNotBeMinimalStage()
        CompletableFutureUtils.applyToEitherSuccessAsync(cfThis, cfS, { s + it }).shouldNotBeMinimalStage()
        CompletableFutureUtils.applyToEitherSuccessAsync(cfThis, cfS, { s + it }, testExecutor)
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.acceptEitherSuccess(cfThis, cfS, { s + it }).shouldNotBeMinimalStage()
        CompletableFutureUtils.acceptEitherSuccessAsync(cfThis, cfS, { s + it }).shouldNotBeMinimalStage()
        CompletableFutureUtils.acceptEitherSuccessAsync(cfThis, cfS, { s + it }, testExecutor)
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.runAfterEitherSuccess(cfThis, cfS, {}).shouldNotBeMinimalStage()
        CompletableFutureUtils.runAfterEitherSuccessAsync(cfThis, cfS, {}).shouldNotBeMinimalStage()
        CompletableFutureUtils.runAfterEitherSuccessAsync(cfThis, cfS, {}, testExecutor)
            .shouldNotBeMinimalStage()

        // Error Handling Methods of CompletionStage

        CompletableFutureUtils.catching(cfThis, RuntimeException::class.java, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.catchingAsync(cfThis, RuntimeException::class.java, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.catchingAsync(cfThis, RuntimeException::class.java, { s }, testExecutor)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.catching(csThis, RuntimeException::class.java, { s }).shouldBeMinimalStageAsCF()
        CompletableFutureUtils.catchingAsync(csThis, RuntimeException::class.java, { s }).shouldBeMinimalStageAsCF()
        CompletableFutureUtils.catchingAsync(csThis, RuntimeException::class.java, { s }, testExecutor)
            .shouldBeMinimalStageAsCF()

        CompletableFutureUtils.exceptionallyAsync(cfThis, { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.exceptionallyAsync(cfThis, { s }, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.exceptionallyAsync(csThis, { s }).shouldBeMinimalStageAsCF()
        CompletableFutureUtils.exceptionallyAsync(csThis, { s }, testExecutor).shouldBeMinimalStageAsCF()

        // Timeout Control Methods of CompletableFuture

        CompletableFutureUtils.cffuOrTimeout(cfThis, 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.cffuOrTimeout(cfThis, testExecutor, 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.orTimeout(cfThis, 1, MILLISECONDS).shouldNotBeMinimalStage()

        CompletableFutureUtils.cffuCompleteOnTimeout(cfThis, "", 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.cffuCompleteOnTimeout(cfThis, "", testExecutor, 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.completeOnTimeout(cfThis, "", 1, MILLISECONDS).shouldNotBeMinimalStage()

        // Advanced Methods of CompletionStage(compose* and handle-like methods)

        CompletableFutureUtils.catchingCompose(cfThis, RuntimeException::class.java, { csThis })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.catchingComposeAsync(cfThis, RuntimeException::class.java, { csThis })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.catchingComposeAsync(cfThis, RuntimeException::class.java, { csThis }, testExecutor)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.catchingCompose(csThis, RuntimeException::class.java, { cfThis })
            .shouldBeMinimalStageAsCF()
        CompletableFutureUtils.catchingComposeAsync(csThis, RuntimeException::class.java, { cfThis })
            .shouldBeMinimalStageAsCF()
        CompletableFutureUtils.catchingComposeAsync(csThis, RuntimeException::class.java, { cfThis }, testExecutor)
            .shouldBeMinimalStageAsCF()

        CompletableFutureUtils.exceptionallyCompose(cfThis, { csThis }).shouldNotBeMinimalStage()
        CompletableFutureUtils.exceptionallyComposeAsync(cfThis, { csThis }).shouldNotBeMinimalStage()
        CompletableFutureUtils.exceptionallyComposeAsync(cfThis, { csThis }, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.exceptionallyCompose(csThis, { cfThis }).shouldBeMinimalStageAsCF()
        CompletableFutureUtils.exceptionallyComposeAsync(csThis, { cfThis }).shouldBeMinimalStageAsCF()
        CompletableFutureUtils.exceptionallyComposeAsync(csThis, { cfThis }, testExecutor).shouldBeMinimalStageAsCF()


        // skip peek methods testing.
        // tested cases in "test_peek": return cfThis

        // skip completeAsync/completeExceptionallyAsync methods testing.
        // tested cases in "test_write": return cfThis


        // Re-Config Methods of CompletableFuture

        CompletableFutureUtils.minimalCompletionStage(cfS).shouldBeMinimalStageAsCF()

        CompletableFutureUtils.copy(cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.copy(csThis as CompletableFuture<String>).shouldBeMinimalStage()

        CompletableFutureUtils.newIncompleteFuture<Int>(cfS).shouldNotBeMinimalStage()
        CompletableFutureUtils.newIncompleteFuture<Int>(csThis).shouldBeMinimalStage()
    }

    fun <T> CompletionStage<T>.shouldBeMinimalStageAsCF() {
        (this as CompletableFuture<T>).shouldBeMinimalStage()
    }

    @Test
    fun test_Cffu_methods() {
        // TODO
    }
}
