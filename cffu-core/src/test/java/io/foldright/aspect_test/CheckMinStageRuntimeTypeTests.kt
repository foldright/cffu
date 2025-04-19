package io.foldright.aspect_test

import io.foldright.cffu.CfTupleUtils
import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.test_utils.*
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.inspectors.forAll
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.function.Function
import java.util.function.Supplier

/**
 * Check the runtime type of return value of all method that returned CompletableFuture/Cffu.
 */
@Suppress("MoveLambdaOutsideParentheses")
class CheckMinStageRuntimeTypeTests : AnnotationSpec() {
    private val testExecutor = createThreadPool("CheckMinStageRuntimeTypeTests", queueCapacity = 1000_000)
    private val testCffuFac = CffuFactory.builder(testExecutor).build();

    private val cfThis: CompletableFuture<String> = completedFuture("cf this")
    private val csThis: CompletionStage<String> = CompletableFutureUtils.completedStage("cs this")
    private val cffuThis: Cffu<String> = testCffuFac.completedFuture("cffu this")
    private val minCffuThis: CompletionStage<String> = testCffuFac.completedStage("minCffu this")

    private val cfN: CompletableFuture<Int> = completedFuture(n)
    private val cfAn: CompletableFuture<Int> = completedFuture(anotherN)
    private val cfS: CompletableFuture<String> = completedFuture(s)
    private val cfD: CompletableFuture<Double> = completedFuture(d)

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
        CompletableFutureUtils.mSupplyAllSuccessAsync<String>(testExecutor, "").shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync(testExecutor, "", { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyAllSuccessAsync(testExecutor, "", { s }, { s }).shouldNotBeMinimalStage()

        CompletableFutureUtils.mSupplyMostSuccessAsync<String>("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync("", 1, MILLISECONDS, Supplier { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync("", 1, MILLISECONDS, Supplier { s }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync<String>(testExecutor, "", 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync(testExecutor, "", 1, MILLISECONDS, Supplier { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.mSupplyMostSuccessAsync(testExecutor, "", 1, MILLISECONDS, Supplier { s }, { s })
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

        // Multi-Actions-Tuple(MTuple*) Methods(create by actions)

        CfTupleUtils.mSupplyTupleFailFastAsync({ n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(testExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleFailFastAsync(testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CfTupleUtils.mSupplyAllSuccessTupleAsync({ n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(testExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyAllSuccessTupleAsync(testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CfTupleUtils.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(testExecutor, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(testExecutor, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyMostSuccessTupleAsync(
            testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()

        CfTupleUtils.mSupplyTupleAsync({ n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(testExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.mSupplyTupleAsync(testExecutor, { n }, { s }, { d }, { n }, { s })
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
        CompletableFutureUtils.mostSuccessResultsOf(testExecutor, "", 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf(testExecutor, "", 1, MILLISECONDS, cfN).shouldNotBeMinimalStage()
        CompletableFutureUtils.mostSuccessResultsOf(testExecutor, "", 1, MILLISECONDS, cfN, cfAn)
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

        CfTupleUtils.allTupleFailFastOf(cfN, cfS).shouldNotBeMinimalStage()
        CfTupleUtils.allTupleFailFastOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        CfTupleUtils.allTupleFailFastOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CfTupleUtils.allTupleFailFastOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        CfTupleUtils.allSuccessTupleOf(cfN, cfS).shouldNotBeMinimalStage()
        CfTupleUtils.allSuccessTupleOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        CfTupleUtils.allSuccessTupleOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CfTupleUtils.allSuccessTupleOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        CfTupleUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS, cfD).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()
        CfTupleUtils.mostSuccessTupleOf(testExecutor, 1, MILLISECONDS, cfN, cfS, cfD, cfN, cfS)
            .shouldNotBeMinimalStage()

        CfTupleUtils.allTupleOf(cfN, cfS).shouldNotBeMinimalStage()
        CfTupleUtils.allTupleOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        CfTupleUtils.allTupleOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        CfTupleUtils.allTupleOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

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
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, testExecutor, "").shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, testExecutor, "", { s }).shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyAllSuccessAsync(cfThis, testExecutor, "", { s }, { s })
            .shouldNotBeMinimalStage()

        CompletableFutureUtils.thenMApplyMostSuccessAsync<String, String>(cfThis, "", 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, "", 1, MILLISECONDS, Function { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, "", 1, MILLISECONDS, Function { s }, { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, testExecutor, "", 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(cfThis, testExecutor, "", 1, MILLISECONDS, Function { s })
            .shouldNotBeMinimalStage()
        CompletableFutureUtils.thenMApplyMostSuccessAsync(
            cfThis,
            testExecutor,
            "",
            1,
            MILLISECONDS,
            Function { s },
            { s }
        ).shouldNotBeMinimalStage()

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

        // Then-Multi-Actions-Tuple(thenMTuple*) Methods

        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, Function { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, Function { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleFailFastAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, Function { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, Function { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyAllSuccessTupleAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, 1, MILLISECONDS, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, testExecutor, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, testExecutor, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(cfThis, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyMostSuccessTupleAsync(
            cfThis, testExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()

        CfTupleUtils.thenMApplyTupleAsync(cfThis, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, testExecutor, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, testExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, Function { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, Function { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        CfTupleUtils.thenMApplyTupleAsync(cfThis, testExecutor, { n }, { s }, { d }, { n }, { s })
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
        CompletableFutureUtils.cffuOrTimeout(cfThis, 1, MILLISECONDS, testExecutor).shouldNotBeMinimalStage()
        CompletableFutureUtils.orTimeout(cfThis, 1, MILLISECONDS).shouldNotBeMinimalStage()

        CompletableFutureUtils.cffuCompleteOnTimeout(cfThis, "", 1, MILLISECONDS).shouldNotBeMinimalStage()
        CompletableFutureUtils.cffuCompleteOnTimeout(cfThis, "", 1, MILLISECONDS, testExecutor)
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

    private fun <T> CompletionStage<T>.shouldBeMinimalStageAsCF() {
        (this as CompletableFuture<T>).shouldBeMinimalStage()
    }

    @Test
    fun test_CffuFactory_methods() {
        testCffuFac.newIncompleteCffu<Int>().shouldNotBeMinimalStage()

        // supplyAsync*/runAsync* Methods(create by action)

        testCffuFac.supplyAsync { n }.shouldNotBeMinimalStage()
        testCffuFac.supplyAsync({ n }, testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.runAsync { }.shouldNotBeMinimalStage()
        testCffuFac.runAsync({ }, testFjExecutor).shouldNotBeMinimalStage()

        // Multi-Actions(M*) Methods(create by actions)

        testCffuFac.mSupplyFailFastAsync<String>().shouldNotBeMinimalStage()
        testCffuFac.mSupplyFailFastAsync(Supplier { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyFailFastAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyFailFastAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mSupplyFailFastAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyFailFastAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mSupplyAllSuccessAsync<String>("").shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessAsync("", Supplier { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessAsync("", Supplier { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessAsync<String>(testFjExecutor, "").shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessAsync(testFjExecutor, "", { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessAsync(testFjExecutor, "", { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mSupplyMostSuccessAsync<String>("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessAsync("", 1, MILLISECONDS, Supplier { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessAsync("", 1, MILLISECONDS, Supplier { s }, { s })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessAsync<String>(testFjExecutor, "", 1, MILLISECONDS)
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessAsync(testFjExecutor, "", 1, MILLISECONDS, Supplier { s })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessAsync(testFjExecutor, "", 1, MILLISECONDS, Supplier { s }, { s })
            .shouldNotBeMinimalStage()

        testCffuFac.mSupplyAsync<String>().shouldNotBeMinimalStage()
        testCffuFac.mSupplyAsync(Supplier { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mSupplyAnySuccessAsync<String>().shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnySuccessAsync(Supplier { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnySuccessAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnySuccessAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnySuccessAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnySuccessAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mSupplyAnyAsync<String>().shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnyAsync(Supplier { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnyAsync(Supplier { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnyAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnyAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAnyAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mRunFailFastAsync().shouldNotBeMinimalStage()
        testCffuFac.mRunFailFastAsync(Runnable { }).shouldNotBeMinimalStage()
        testCffuFac.mRunFailFastAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunFailFastAsync(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mRunFailFastAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunFailFastAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mRunAsync().shouldNotBeMinimalStage()
        testCffuFac.mRunAsync(Runnable { }).shouldNotBeMinimalStage()
        testCffuFac.mRunAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunAsync(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mRunAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mRunAnySuccessAsync().shouldNotBeMinimalStage()
        testCffuFac.mRunAnySuccessAsync(Runnable { }).shouldNotBeMinimalStage()
        testCffuFac.mRunAnySuccessAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunAnySuccessAsync(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mRunAnySuccessAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunAnySuccessAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mRunAnyAsync().shouldNotBeMinimalStage()
        testCffuFac.mRunAnyAsync(Runnable { }).shouldNotBeMinimalStage()
        testCffuFac.mRunAnyAsync(Runnable { s }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunAnyAsync(testFjExecutor).shouldNotBeMinimalStage()
        testCffuFac.mRunAnyAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        testCffuFac.mRunAnyAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        // Multi-Actions-Tuple(MTuple*) Methods(create by actions)

        testCffuFac.mSupplyTupleFailFastAsync({ n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(testFjExecutor, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(testFjExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(testFjExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleFailFastAsync(testFjExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        testCffuFac.mSupplyAllSuccessTupleAsync({ n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        testCffuFac.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        testCffuFac.mSupplyMostSuccessTupleAsync(
            testFjExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()

        testCffuFac.mSupplyTupleAsync({ n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(testFjExecutor, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(Supplier { n }, { s }, { d }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(testFjExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(Supplier { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(Supplier { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        testCffuFac.mSupplyTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        // allOf* Methods(including mostSuccessResultsOf)

        testCffuFac.allResultsFailFastOf<String>().shouldNotBeMinimalStage()
        testCffuFac.allResultsFailFastOf(cfN).shouldNotBeMinimalStage()
        testCffuFac.allResultsFailFastOf(cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.allSuccessResultsOf("").shouldNotBeMinimalStage()
        testCffuFac.allSuccessResultsOf("", cfN).shouldNotBeMinimalStage()
        testCffuFac.allSuccessResultsOf("", cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.mostSuccessResultsOf("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        testCffuFac.mostSuccessResultsOf("", 1, MILLISECONDS, cfN).shouldNotBeMinimalStage()
        testCffuFac.mostSuccessResultsOf("", 1, MILLISECONDS, cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.allResultsOf<String>().shouldNotBeMinimalStage()
        testCffuFac.allResultsOf(cfN).shouldNotBeMinimalStage()
        testCffuFac.allResultsOf(cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.allFailFastOf().shouldNotBeMinimalStage()
        testCffuFac.allFailFastOf(cfN).shouldNotBeMinimalStage()
        testCffuFac.allFailFastOf(cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.allOf().shouldNotBeMinimalStage()
        testCffuFac.allOf(cfN).shouldNotBeMinimalStage()
        testCffuFac.allOf(cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.anySuccessOf<String>().shouldNotBeMinimalStage()
        testCffuFac.anySuccessOf(cfN).shouldNotBeMinimalStage()
        testCffuFac.anySuccessOf(cfN, cfAn).shouldNotBeMinimalStage()

        testCffuFac.anyOf<String>().shouldNotBeMinimalStage()
        testCffuFac.anyOf(cfN).shouldNotBeMinimalStage()
        testCffuFac.anyOf(cfN, cfAn).shouldNotBeMinimalStage()

        // allTupleOf*/mostSuccessTupleOf Methods

        testCffuFac.allTupleFailFastOf(cfN, cfS).shouldNotBeMinimalStage()
        testCffuFac.allTupleFailFastOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        testCffuFac.allTupleFailFastOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        testCffuFac.allTupleFailFastOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        testCffuFac.allSuccessTupleOf(cfN, cfS).shouldNotBeMinimalStage()
        testCffuFac.allSuccessTupleOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        testCffuFac.allSuccessTupleOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        testCffuFac.allSuccessTupleOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        testCffuFac.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS).shouldNotBeMinimalStage()
        testCffuFac.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD).shouldNotBeMinimalStage()
        testCffuFac.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        testCffuFac.mostSuccessTupleOf(1, MILLISECONDS, cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        testCffuFac.allTupleOf(cfN, cfS).shouldNotBeMinimalStage()
        testCffuFac.allTupleOf(cfN, cfS, cfD).shouldNotBeMinimalStage()
        testCffuFac.allTupleOf(cfN, cfS, cfD, cfN).shouldNotBeMinimalStage()
        testCffuFac.allTupleOf(cfN, cfS, cfD, cfN, cfS).shouldNotBeMinimalStage()

        // Immediate Value Argument Factory Methods

        testCffuFac.completedFuture<String>("").shouldNotBeMinimalStage()
        testCffuFac.failedFuture<String>(rte).shouldNotBeMinimalStage()

        testCffuFac.completedStage("").shouldBeMinimalStageAsCffu()
        testCffuFac.failedStage<String>(rte).shouldBeMinimalStageAsCffu()

        // CompletionStage Argument Factory Methods

        testCffuFac.toCffu(csThis).shouldNotBeMinimalStage()
        testCffuFac.toCffu(cfThis).shouldNotBeMinimalStage()
        testCffuFac.toCffuArray(csThis, cfThis).forAll { it.shouldNotBeMinimalStage() }
    }

    private fun <T> CompletionStage<T>.shouldBeMinimalStageAsCffu() {
        (this as Cffu<T>).shouldBeMinimalStage()
    }

    @Test
    fun test_Cffu_methods() {
        // simple then* Methods of CompletionStage
        cffuThis.thenApply({ s }).shouldNotBeMinimalStage()
        cffuThis.thenApplyAsync({ s }).shouldNotBeMinimalStage()
        cffuThis.thenApplyAsync({ s }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.thenAccept({ s }).shouldNotBeMinimalStage()
        cffuThis.thenAcceptAsync({ s }).shouldNotBeMinimalStage()
        cffuThis.thenAcceptAsync({ s }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.thenRun({ }).shouldNotBeMinimalStage()
        cffuThis.thenRunAsync({ }).shouldNotBeMinimalStage()
        cffuThis.thenRunAsync({ }, testFjExecutor).shouldNotBeMinimalStage()

        // Then-Multi-Actions(thenM*) Methods

        cffuThis.thenMApplyFailFastAsync<String>().shouldNotBeMinimalStage()
        cffuThis.thenMApplyFailFastAsync(Function { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyFailFastAsync(Function { s }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyFailFastAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMApplyFailFastAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyFailFastAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        cffuThis.thenMApplyAllSuccessAsync("").shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessAsync("", Function { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessAsync("", Function { s }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessAsync(testFjExecutor, "").shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessAsync(testFjExecutor, "", { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessAsync(testFjExecutor, "", { s }, { s }).shouldNotBeMinimalStage()

        cffuThis.thenMApplyMostSuccessAsync<String>("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessAsync("", 1, MILLISECONDS, Function { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessAsync("", 1, MILLISECONDS, Function { s }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessAsync(testFjExecutor, "", 1, MILLISECONDS).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessAsync(testFjExecutor, "", 1, MILLISECONDS, Function { s })
            .shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessAsync(testFjExecutor, "", 1, MILLISECONDS, Function { s }, { s })
            .shouldNotBeMinimalStage()

        cffuThis.thenMApplyAsync<String>().shouldNotBeMinimalStage()
        cffuThis.thenMApplyAsync(Function { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAsync(Function { s }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        cffuThis.thenMApplyAnySuccessAsync<String>().shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnySuccessAsync(Function { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnySuccessAsync(Function { s }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnySuccessAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnySuccessAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnySuccessAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        cffuThis.thenMApplyAnyAsync<String>().shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnyAsync(Function { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnyAsync(Function { s }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnyAsync<String>(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnyAsync(testFjExecutor, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAnyAsync(testFjExecutor, { s }, { s }).shouldNotBeMinimalStage()

        cffuThis.thenMRunFailFastAsync().shouldNotBeMinimalStage()
        cffuThis.thenMRunFailFastAsync(Runnable { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunFailFastAsync(Runnable { }, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunFailFastAsync(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMRunFailFastAsync(testFjExecutor, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunFailFastAsync(testFjExecutor, { }, { }).shouldNotBeMinimalStage()

        cffuThis.thenMRunAsync().shouldNotBeMinimalStage()
        cffuThis.thenMRunAsync(Runnable { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAsync(Runnable { }, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAsync(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMRunAsync(testFjExecutor, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAsync(testFjExecutor, { }, { }).shouldNotBeMinimalStage()

        cffuThis.thenMRunAnySuccessAsync().shouldNotBeMinimalStage()
        cffuThis.thenMRunAnySuccessAsync(Runnable { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnySuccessAsync(Runnable { }, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnySuccessAsync(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnySuccessAsync(testFjExecutor, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnySuccessAsync(testFjExecutor, { }, { }).shouldNotBeMinimalStage()

        cffuThis.thenMRunAnyAsync().shouldNotBeMinimalStage()
        cffuThis.thenMRunAnyAsync(Runnable { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnyAsync(Runnable { }, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnyAsync(testFjExecutor).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnyAsync(testFjExecutor, { }).shouldNotBeMinimalStage()
        cffuThis.thenMRunAnyAsync(testFjExecutor, { }, { }).shouldNotBeMinimalStage()

        // Then-Multi-Actions-Tuple(thenMTuple*) Methods

        cffuThis.thenMApplyTupleFailFastAsync({ n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(testFjExecutor, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(testFjExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(Function { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(testFjExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(Function { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleFailFastAsync(testFjExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        cffuThis.thenMApplyAllSuccessTupleAsync({ n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(Function { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(Function { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyAllSuccessTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        cffuThis.thenMApplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s })
            .shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s }, { d })
            .shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n })
            .shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(1, MILLISECONDS, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()
        cffuThis.thenMApplyMostSuccessTupleAsync(testFjExecutor, 1, MILLISECONDS, { n }, { s }, { d }, { n }, { s })
            .shouldNotBeMinimalStage()

        cffuThis.thenMApplyTupleAsync({ n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(testFjExecutor, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(Function { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(testFjExecutor, { n }, { s }, { d }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(Function { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(Function { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()
        cffuThis.thenMApplyTupleAsync(testFjExecutor, { n }, { s }, { d }, { n }, { s }).shouldNotBeMinimalStage()

        // thenBoth* Methods(binary input)

        cffuThis.thenCombineFailFast(cfD, { t, u -> t + u }).shouldNotBeMinimalStage()
        cffuThis.thenCombineFailFastAsync(cfD, { t, u -> t + u }).shouldNotBeMinimalStage()
        cffuThis.thenCombineFailFastAsync(cfD, { t, u -> t + u }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.thenAcceptBothFailFast(cfD, { _, _ -> }).shouldNotBeMinimalStage()
        cffuThis.thenAcceptBothFailFastAsync(cfD, { _, _ -> }).shouldNotBeMinimalStage()
        cffuThis.thenAcceptBothFailFastAsync(cfD, { _, _ -> }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.runAfterBothFailFast(cfD, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterBothFailFastAsync(cfD, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterBothFailFastAsync(cfD, {}, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.thenCombine(cfD, { t, u -> t + u }).shouldNotBeMinimalStage()
        cffuThis.thenCombineAsync(cfD, { t, u -> t + u }).shouldNotBeMinimalStage()
        cffuThis.thenCombineAsync(cfD, { t, u -> t + u }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.thenAcceptBoth(cfD, { _, _ -> }).shouldNotBeMinimalStage()
        cffuThis.thenAcceptBothAsync(cfD, { _, _ -> }).shouldNotBeMinimalStage()
        cffuThis.thenAcceptBothAsync(cfD, { _, _ -> }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.runAfterBoth(cfD, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterBothAsync(cfD, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterBothAsync(cfD, {}, testFjExecutor).shouldNotBeMinimalStage()

        // thenEither* Methods(binary input) with either(any)-success support

        cffuThis.applyToEitherSuccess(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.applyToEitherSuccessAsync(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.applyToEitherSuccessAsync(cfS, { s + it }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.acceptEitherSuccess(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.acceptEitherSuccessAsync(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.acceptEitherSuccessAsync(cfS, { s + it }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.runAfterEitherSuccess(cfS, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterEitherSuccessAsync(cfS, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterEitherSuccessAsync(cfS, {}, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.applyToEither(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.applyToEitherAsync(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.applyToEitherAsync(cfS, { s + it }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.acceptEither(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.acceptEitherAsync(cfS, { s + it }).shouldNotBeMinimalStage()
        cffuThis.acceptEitherAsync(cfS, { s + it }, testFjExecutor).shouldNotBeMinimalStage()

        cffuThis.runAfterEither(cfS, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterEitherAsync(cfS, {}).shouldNotBeMinimalStage()
        cffuThis.runAfterEitherAsync(cfS, {}, testFjExecutor).shouldNotBeMinimalStage()

        // Error Handling Methods of CompletionStage

        cffuThis.catching(RuntimeException::class.java, { s }).shouldNotBeMinimalStage()
        cffuThis.catchingAsync(RuntimeException::class.java, { s }).shouldNotBeMinimalStage()
        cffuThis.catchingAsync(RuntimeException::class.java, { s }, testFjExecutor).shouldNotBeMinimalStage()
        (minCffuThis as Cffu<String>).catching(RuntimeException::class.java, { s }).shouldBeMinimalStage()
        minCffuThis.catchingAsync(RuntimeException::class.java, { s }).shouldBeMinimalStage()
        minCffuThis.catchingAsync(RuntimeException::class.java, { s }, testFjExecutor).shouldBeMinimalStage()

        cffuThis.exceptionallyAsync({ s }).shouldNotBeMinimalStage()
        cffuThis.exceptionallyAsync({ s }, testFjExecutor).shouldNotBeMinimalStage()
        minCffuThis.exceptionallyAsync({ s }).shouldBeMinimalStage()
        minCffuThis.exceptionallyAsync({ s }, testFjExecutor).shouldBeMinimalStage()

        // Timeout Control Methods of CompletableFuture

        cffuThis.orTimeout(1, MILLISECONDS).shouldNotBeMinimalStage()
        cffuThis.unsafeOrTimeout(1, MILLISECONDS).shouldNotBeMinimalStage()

        cffuThis.completeOnTimeout("", 1, MILLISECONDS).shouldNotBeMinimalStage()
        cffuThis.unsafeCompleteOnTimeout("", 1, MILLISECONDS).shouldNotBeMinimalStage()

        // Advanced Methods of CompletionStage(compose* and handle-like methods)

        cffuThis.catchingCompose(RuntimeException::class.java, { csThis }).shouldNotBeMinimalStage()
        cffuThis.catchingComposeAsync(RuntimeException::class.java, { csThis }).shouldNotBeMinimalStage()
        cffuThis.catchingComposeAsync(RuntimeException::class.java, { csThis }, testFjExecutor)
            .shouldNotBeMinimalStage()
        minCffuThis.catchingCompose(RuntimeException::class.java, { csThis }).shouldBeMinimalStage()
        minCffuThis.catchingComposeAsync(RuntimeException::class.java, { csThis }).shouldBeMinimalStage()
        minCffuThis.catchingComposeAsync(RuntimeException::class.java, { csThis }, testFjExecutor)
            .shouldBeMinimalStage()

        cffuThis.exceptionallyCompose({ csThis }).shouldNotBeMinimalStage()
        cffuThis.exceptionallyComposeAsync({ csThis }).shouldNotBeMinimalStage()
        cffuThis.exceptionallyComposeAsync({ csThis }, testFjExecutor).shouldNotBeMinimalStage()
        minCffuThis.exceptionallyCompose({ csThis }).shouldBeMinimalStage()
        minCffuThis.exceptionallyComposeAsync({ csThis }).shouldBeMinimalStage()
        minCffuThis.exceptionallyComposeAsync({ csThis }, testFjExecutor).shouldBeMinimalStage()


        // skip peek methods testing.
        // tested cases in "test_peek": return

        // skip completeAsync/completeExceptionallyAsync methods testing.
        // tested cases in "test_write": return

        cffuThis.minimalCompletionStage().shouldBeMinimalStageAsCffu()

        cffuThis.toCompletableFuture().shouldNotBeMinimalStage()
        minCffuThis.toCompletableFuture().shouldNotBeMinimalStage()

        cffuThis.copy().shouldNotBeMinimalStage()
        minCffuThis.copy().shouldBeMinimalStage()

        cffuThis.newIncompleteFuture<Int>().shouldNotBeMinimalStage()
        // NOTE: do NOT keep the mini like CompletableFuture, it's OK.
        //       casting CompletionStage instances to Cffu is not normal/public API usage!!
        minCffuThis.newIncompleteFuture<Int>().shouldNotBeMinimalStage()
    }

    @AfterEach
    fun afterEach() {
        println("executor info of CheckMinStageRuntimeTypeTests:")
        println("testExecutor: $testExecutor")
        println("testFjExecutor: $testFjExecutor")
    }
}
