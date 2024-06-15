package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;

import static io.foldright.cffu.CompletableFutureUtils.failedFuture;
import static io.foldright.cffu.CompletableFutureUtils.toCompletableFutureArray;
import static io.foldright.test_utils.TestUtils.*;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


/**
 * see io.foldright.compatibility_test.CffuApiCompatibilityTest
 */
@SuppressWarnings("RedundantThrows")
class CffuFactoryTest {
    ///////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  Create by immediate value
    //    - completedFuture/completedStage
    //    - failedFuture/failedStage
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_completedFuture() throws Exception {
        assertEquals(n, cffuFactory.completedFuture(n).get());
    }

    @Test
    void test_completedStage() throws Exception {
        CompletionStage<Integer> stage = cffuFactory.completedStage(n);
        CompletionStage<Integer> sa = stage.thenApply(identity());

        assertEquals(n, stage.toCompletableFuture().get());
        assertEquals(n, sa.toCompletableFuture().get());

        // CAUTION: Last check minimal stage, may rewrite the CF by obtrude* methods
        shouldBeMinimalStage((Cffu<?>) stage);
        shouldBeMinimalStage((Cffu<?>) sa);
    }

    @Test
    void test_failedFuture() throws Exception {
        Cffu<Integer> cf = cffuFactory.failedFuture(rte);

        assertSame(rte, assertThrowsExactly(CompletionException.class, cf::join).getCause());
        assertEquals(n, cf.exceptionally(throwable -> n).get());

        shouldNotBeMinimalStage(cf);
    }

    @Test
    void test_failedStage() throws Exception {
        CompletionStage<Integer> stage = cffuFactory.failedStage(rte);
        CompletionStage<Integer> sa = stage.thenApply(identity());
        CompletionStage<Integer> se = stage.exceptionally(throwable -> n);

        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                failedFuture(rte).toCompletableFuture().join()
        ).getCause());
        assertEquals(n, se.toCompletableFuture().get());

        // CAUTION: Last check minimal stage, may rewrite the CF by obtrude* methods
        shouldBeMinimalStage((Cffu<Integer>) stage);
        shouldBeMinimalStage((Cffu<Integer>) sa);
        shouldBeMinimalStage((Cffu<Integer>) se);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  create by logic/lambda
    //    - runAsync*
    //    - supplyAsync*
    ////////////////////////////////////////////////////////////////////////////////

    // ...

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods
    //
    //    - newIncompleteCffu: equivalent to CompletableFuture constructor
    //
    //    - toCffu:      CF/CompletionStage -> Cffu
    //    - toCffuArray: CF/CompletionStage[] -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_newIncompleteCffu() {
        Cffu<Integer> cf = cffuFactory.newIncompleteCffu();
        assertFalse(cf.isDone());
    }

    @Test
    void test_toCffu() throws Exception {
        Cffu<Integer> cf = cffuFactory.toCffu(completedFuture(n));

        assertEquals(n, cf.get());
        shouldNotBeMinimalStage(cf);

        CffuFactory fac = CffuFactory.builder(anotherExecutorService).forbidObtrudeMethods(true).build();
        Cffu<Integer> cffu = fac.toCffu(cffuFactory.completedFuture(42));
        assertSame(anotherExecutorService, cffu.defaultExecutor());
        assertSame(fac, cffu.cffuFactory());

        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                cffu.obtrudeValue(44)
        ).getMessage());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_toCffu__for_factoryMethods_of_Java9() {
        CompletableFuture<Object> cf1 = CompletableFuture.failedFuture(rte);
        assertFalse(cffuFactory.toCffu(cf1).isMinimalStage());
        shouldNotBeMinimalStage(cf1);

        Cffu<Integer> cf2 = cffuFactory.toCffu(CompletableFuture.completedStage(n));
        assertFalse(cf2.isMinimalStage());
        shouldNotBeMinimalStage(cf2);

        Cffu<Object> cf3 = cffuFactory.toCffu(CompletableFuture.failedStage(rte));
        assertFalse(cf3.isMinimalStage());
        shouldNotBeMinimalStage(cf3);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_toCffuArray() throws Exception {
        Cffu<Integer>[] cffus = cffuFactory.toCffuArray(CompletableFuture.completedStage(n), completedFuture(n));
        assertEquals(n, cffus[1].get());

        shouldNotBeMinimalStage(cffus[0]);
        shouldNotBeMinimalStage(cffus[1]);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf / anyOf methods, equivalent to same name static methods of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allOf_CompletableFuture() throws Exception {
        cffuFactory.allOf(completedFuture(n), completedFuture(anotherN)).get();
        cffuFactory.allOf(completedFuture(anotherN)).get();

        assertNull(cffuFactory.allOf().get());

        ////////////////////////////////////////

        cffuFactory.allOfFastFail(completedFuture(n), completedFuture(anotherN)).get();
        cffuFactory.allOfFastFail(completedFuture(anotherN)).get();

        assertNull(cffuFactory.allOfFastFail().get());

        cffuFactory.allOfFastFail(cffuFactory.completedFuture(n), cffuFactory.completedFuture(anotherN)).get();
        cffuFactory.allOfFastFail(cffuFactory.completedFuture(anotherN)).get();
    }

    @Test
    void test_anyOf_CompletableFuture() throws Exception {
        cffuFactory.anyOf(completedFuture(n), completedFuture(anotherN)).get();
        assertEquals(anotherN, cffuFactory.anyOf(completedFuture(anotherN)).get());

        assertFalse(cffuFactory.anyOf().isDone());

        ////////////////////////////////////////

        cffuFactory.anyOfSuccess(completedFuture(n), completedFuture(anotherN)).get();
        assertEquals(anotherN, cffuFactory.anyOfSuccess(completedFuture(anotherN)).get());

        assertInstanceOf(NoCfsProvidedException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.anyOfSuccess().get()
        ).getCause());

        cffuFactory.anyOfSuccess(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(anotherN)
        ).get();
        assertEquals(anotherN, cffuFactory.anyOfSuccess(
                cffuFactory.completedFuture(anotherN)
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Delay Execution, equivalent to same name static methods of CompletableFuture
    //
    //    - delayedExecutor
    ////////////////////////////////////////////////////////////////////////////////

    // ...

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe allOf/anyOf Factory Methods
    //    method name prefix with `cffu`
    //
    //    - allResultsOf
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allResultsOf() throws Exception {
        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allResultsOf(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allResultsOf(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                cffuFactory.allResultsOf().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allResultsOf(cffuFactory.completedFuture(n), cffuFactory.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allResultsOf(cffuFactory.completedFuture(n)).get()
        );

        ////////////////////////////////////////

        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allResultsOfFastFail(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allResultsOfFastFail(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                cffuFactory.allResultsOfFastFail().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allResultsOfFastFail(cffuFactory.completedFuture(n), cffuFactory.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allResultsOfFastFail(cffuFactory.completedFuture(n)).get()
        );
    }

    @Test
    void test_allResultsOf_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allResultsOf(
                        cffuFactory.completedFuture(n),
                        cffuFactory.failedFuture(rte),
                        cffuFactory.completedFuture(s)
                ).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allResultsOfFastFail(
                        cffuFactory.completedFuture(n),
                        cffuFactory.failedFuture(rte),
                        cffuFactory.completedFuture(s)
                ).get()
        ).getCause());
    }

    @Test
    void test_mostOf() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(n);
        final CompletionStage<Integer> completedStage = cffuFactory.completedStage(n);
        final Cffu<Integer> failed = cffuFactory.failedFuture(rte);
        final Cffu<Integer> cancelled = cffuFactory.toCffu(createCancelledFuture());
        final Cffu<Integer> incomplete = cffuFactory.toCffu(createIncompleteFuture());

        assertEquals(0, cffuFactory.mostResultsOfSuccess(null, 10, TimeUnit.MILLISECONDS).get().size());

        assertEquals(Arrays.asList(n, null, null, null), cffuFactory.mostResultsOfSuccess(
                null, 10, TimeUnit.MILLISECONDS, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(Arrays.asList(n, anotherN, anotherN, anotherN), cffuFactory.mostResultsOfSuccess(
                anotherN, 10, TimeUnit.MILLISECONDS, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(Arrays.asList(anotherN, anotherN, anotherN), cffuFactory.mostResultsOfSuccess(
                anotherN, 10, TimeUnit.MILLISECONDS, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(Arrays.asList(anotherN, anotherN), cffuFactory.mostResultsOfSuccess(
                anotherN, 10, TimeUnit.DAYS, failed, cancelled
        ).get());
    }

    @Test
    void test_anyOf() throws Exception {
        assertEquals(n, cffuFactory.anyOf(
                createIncompleteFuture(),
                completedFuture(n)
        ).get());
        assertEquals(n, cffuFactory.anyOf(
                completedFuture(n)
        ).get());

        assertFalse(cffuFactory.anyOf().isDone());

        assertEquals(n, cffuFactory.anyOf(
                cffuFactory.completedFuture(n),
                cffuFactory.newIncompleteCffu()
        ).get());
        assertEquals(n, cffuFactory.anyOf(
                cffuFactory.completedFuture(n)
        ).get());

        ////////////////////////////////////////

        assertEquals(n, cffuFactory.anyOfSuccess(createIncompleteFuture(), completedFuture(n)).get());
        assertEquals(n, cffuFactory.anyOfSuccess(completedFuture(n)).get());

        assertSame(NoCfsProvidedException.class, cffuFactory.anyOfSuccess().exceptionNow().getClass());

        assertEquals(n, cffuFactory.anyOfSuccess(
                cffuFactory.completedFuture(n),
                cffuFactory.newIncompleteCffu()
        ).get());
        assertEquals(n, cffuFactory.anyOfSuccess(cffuFactory.completedFuture(n)).get());
    }

    @Test
    void test_anyOf_exceptionally() throws Exception {
        // first exceptionally completed anyOf cf win,
        // even later cfs normally completed!

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.anyOf(createIncompleteFuture(), failedFuture(rte), createIncompleteFuture()).get()
        ).getCause());

        // first normally completed anyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, cffuFactory.anyOf(
                createIncompleteFuture(),
                completedFuture(n),
                createIncompleteFuture()
        ).get());
    }

    @Test
    void test_anyOfSuccess__trivial_case() throws Exception {
        // success then success
        assertEquals(n, cffuFactory.anyOfSuccess(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.newIncompleteCffu(),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                cffuFactory.completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, cffuFactory.anyOfSuccess(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.newIncompleteCffu(),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                cffuFactory.completedFuture(n)
        ).get());

        // all success
        assertEquals(n, cffuFactory.anyOfSuccess(
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                cffuFactory.completedFuture(n)
        ).get());

        //////////////////////////////////////////////////////////////////////////////

        // success then success
        assertEquals(n, cffuFactory.anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, cffuFactory.anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // all success
        assertEquals(n, cffuFactory.anyOfSuccess(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        //////////////////////////////////////////////////////////////////////////////

        assertSame(NoCfsProvidedException.class, cffuFactory.anyOfSuccess().exceptionNow().getClass());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe allTupleOf Factory Methods
    //  support 2~5 input arguments, method name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allTupleOf() throws Exception {
        assertEquals(Tuple2.of(n, s), cffuFactory.allTupleOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.allTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN),
                cffuFactory.completedFuture(n + n)
        ).get());
    }

    @Test
    void test_allTupleOf_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleOf(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleOf(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleOf(
                        completedFuture(n),
                        completedFuture(d),
                        failedFuture(rte),
                        completedFuture(s),
                        completedFuture(anotherN)
                ).get()
        ).getCause());
    }

    @Test
    void test_allTupleOfFastFail() throws Exception {
        assertEquals(Tuple2.of(n, s), cffuFactory.allTupleOfFastFail(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allTupleOfFastFail(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allTupleOfFastFail(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allTupleOfFastFail(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.allTupleOfFastFail(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allTupleOfFastFail(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allTupleOfFastFail(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allTupleOfFastFail(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN),
                cffuFactory.completedFuture(n + n)
        ).get());
    }

    @Test
    void test_allTupleOfFastFail_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleOfFastFail(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleOfFastFail(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleOfFastFail(
                        completedFuture(n),
                        completedFuture(d),
                        failedFuture(rte),
                        completedFuture(s),
                        completedFuture(anotherN)
                ).get()
        ).getCause());
    }

    @Test
    void test_mostTupleOfSuccess() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(n);
        final CompletionStage<String> anotherCompleted = cffuFactory.completedStage(s);
        final Cffu<Integer> failed = cffuFactory.failedFuture(rte);
        final Cffu<Integer> cancelled = cffuFactory.toCffu(createCancelledFuture());
        final Cffu<Integer> incomplete = cffuFactory.toCffu(createIncompleteFuture());

        assertEquals(Tuple2.of(n, s), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), cffuFactory.mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion (Static) Methods
    //
    //    - cffuArrayUnwrap:              Cffu -> CF
    //    - cffuListToArray:              List<Cffu> -> Cffu[]
    //    - toCompletableFutureArray:     CompletionStage[](including Cffu) -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuListToArray() {
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] input = new Cffu[]{
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(anotherN),
                cffuFactory.newIncompleteCffu()
        };

        assertArrayEquals(input, CffuFactory.cffuListToArray(Arrays.asList(input)));
    }

    @Test
    void test_cffuArrayUnwrap() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfArray = new CompletableFuture[]{
                completedFuture(n),
                completedFuture(anotherN)
        };
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] input = new Cffu[]{
                cffuFactory.toCffu(cfArray[0]),
                cffuFactory.toCffu(cfArray[1]),
        };
        assertArrayEquals(cfArray, CffuFactory.cffuArrayUnwrap(input));
    }

    @Test
    void test_toCompletableFutureArray() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfArray = new CompletableFuture[]{
                completedFuture(n),
                completedFuture(anotherN)
        };
        @SuppressWarnings("unchecked")
        CompletionStage<Integer>[] csArray = new CompletableFuture[]{
                cfArray[0],
                cfArray[1],
        };
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] cffuArray = new Cffu[]{
                cffuFactory.toCffu(cfArray[0]),
                cffuFactory.toCffu(cfArray[1]),
        };

        assertArrayEquals(cfArray, toCompletableFutureArray(cfArray));
        assertArrayEquals(cfArray, toCompletableFutureArray(csArray));
        assertArrayEquals(cfArray, toCompletableFutureArray(cffuArray));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Getter methods of CffuFactory properties
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_getter() {
        assertSame(executorService, cffuFactory.defaultExecutor());
        assertFalse(cffuFactory.forbidObtrudeMethods());

        CffuFactory fac = CffuFactory.builder(anotherExecutorService).forbidObtrudeMethods(true).build();
        assertSame(anotherExecutorService, fac.defaultExecutor());
        assertTrue(fac.forbidObtrudeMethods());
    }

    @Test
    void test_forbidObtrudeMethods_property() {
        CffuFactory fac2 = CffuFactory.builder(executorService).forbidObtrudeMethods(true).build();

        Cffu<Object> cf = fac2.newIncompleteCffu();

        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                cf.obtrudeValue(42)).getMessage()
        );
        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                cf.obtrudeException(rte)).getMessage()
        );
    }

    @Test
    void test_executorSetting_MayBe_ThreadPerTaskExecutor() throws Exception {
        final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;

        CffuFactory fac = CffuFactory.builder(commonPool()).build();
        if (USE_COMMON_POOL) {
            assertSame(commonPool(), fac.defaultExecutor());
        } else {
            String executorClassName = fac.defaultExecutor().getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }

        assertEquals(42, fac.supplyAsync(() -> 42).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Test helper methods
    ////////////////////////////////////////////////////////////////////////////////

    private static CffuFactory cffuFactory;

    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("CffuFactoryTest");
        anotherExecutorService = TestThreadPoolManager.createThreadPool("CffuFactoryTest-Another", true);

        cffuFactory = CffuFactory.builder(executorService).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService, anotherExecutorService);
    }
}
