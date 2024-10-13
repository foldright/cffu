package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.MinStageTestUtils;
import io.foldright.test_utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static io.foldright.cffu.CffuTestHelper.unwrapMadeExecutor;
import static io.foldright.cffu.CompletableFutureUtils.failedFuture;
import static io.foldright.cffu.CompletableFutureUtils.toCompletableFutureArray;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.testCffuFac;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @see io.foldright.compatibility_test.CffuApiCompatibilityTest
 */
@SuppressWarnings({"RedundantThrows", "DataFlowIssue", "JavadocReference"})
class CffuFactoryTest {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Constructor Method
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_newIncompleteCffu() {
        Cffu<Integer> cf = testCffuFac.newIncompleteCffu();
        assertFalse(cf.isDone());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // region## supplyAsync*/runAsync* Methods(create by action) + Multi-Actions(M*) Methods(create by actions)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_mRun() throws Exception {
        final Runnable runnable = TestUtils::snoreZzz;

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Void>[] cfs = new Cffu[]{
                testCffuFac.mRunAsync(runnable, runnable),
                testCffuFac.mRunFailFastAsync(runnable, runnable),
                testCffuFac.mRunAnySuccessAsync(runnable, runnable),
                testCffuFac.mRunAnyAsync(runnable, runnable),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (Cffu<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_mSupply() throws Exception {
        final Supplier<Integer> supplier = supplyLater(n);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<List<Integer>>[] cfs = new Cffu[]{
                testCffuFac.mSupplyFailFastAsync(supplier, supplier),
                testCffuFac.mSupplyFailFastAsync(testExecutor, supplier, supplier),
                testCffuFac.mSupplyAllSuccessAsync(anotherN, supplier, supplier),
                testCffuFac.mSupplyAllSuccessAsync(anotherN, testExecutor, supplier, supplier),
                testCffuFac.mSupplyMostSuccessAsync(anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                testCffuFac.mSupplyMostSuccessAsync(anotherN, testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                testCffuFac.mSupplyAsync(supplier, supplier),
                testCffuFac.mSupplyAsync(testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (Cffu<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }

        final long tick1 = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] cfs1 = new Cffu[]{
                testCffuFac.mSupplyAnySuccessAsync(supplier, supplier),
                testCffuFac.mSupplyAnySuccessAsync(testExecutor, supplier, supplier),
                testCffuFac.mSupplyAnyAsync(supplier, supplier),
                testCffuFac.mSupplyAnyAsync(testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick1 < 50);

        for (Cffu<Integer> cf : cfs1) {
            assertEquals(n, cf.get());
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Tuple-Multi-Actions(tupleM*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    @Test
    void test_tupleMSupplyAsync() throws Exception {
        final Supplier<Integer> supplier_n = supplyLater(n);
        final Supplier<String> supplier_s = supplyLater(s);
        final Supplier<Double> supplier_d = supplyLater(d);
        final Supplier<Integer> supplier_an = supplyLater(anotherN);
        final Supplier<Integer> supplier_nnn = supplyLater(nnn);

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyFailFastAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyFailFastAsync(testExecutor, supplier_n, supplier_s).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyFailFastAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyFailFastAsync(testExecutor, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyFailFastAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyFailFastAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyAsync(testExecutor, supplier_n, supplier_s).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyAsync(testExecutor, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
    }

    @Test
    void test_tupleMSupplyMostSuccessAsync() throws Exception {
        final Supplier<Integer> supplier_n = () -> {
            nap();
            return n;
        };
        final Supplier<String> supplier_s = () -> {
            nap();
            return s;
        };

        final Supplier<Double> supplier_d = () -> {
            nap();
            return d;
        };
        final Supplier<Integer> supplier_an = () -> {
            nap();
            return anotherN;
        };
        final Supplier<Integer> supplier_nn = () -> {
            nap();
            return nnn;
        };
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostSuccessResultsOf)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allOf() throws Exception {
        assertEquals(Arrays.asList(n, n + 1),
                testCffuFac.allResultsFailFastOf(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                testCffuFac.allResultsFailFastOf(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                testCffuFac.allResultsFailFastOf().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                testCffuFac.allResultsFailFastOf(testCffuFac.completedFuture(n), testCffuFac.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                testCffuFac.allResultsFailFastOf(testCffuFac.completedFuture(n)).get()
        );

        ////////////////////////////////////////

        assertEquals(Arrays.asList(n, n + 1),
                testCffuFac.allSuccessResultsOf(anotherN, completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                testCffuFac.allSuccessResultsOf(anotherN, completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                testCffuFac.allResultsFailFastOf().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                testCffuFac.allSuccessResultsOf(anotherN, testCffuFac.completedFuture(n), testCffuFac.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                testCffuFac.allSuccessResultsOf(anotherN, testCffuFac.completedFuture(n)).get()
        );

        ////////////////////////////////////////
        assertEquals(Arrays.asList(n, n + 1),
                testCffuFac.allResultsOf(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                testCffuFac.allResultsOf(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                testCffuFac.allResultsOf().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                testCffuFac.allResultsOf(testCffuFac.completedFuture(n), testCffuFac.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                testCffuFac.allResultsOf(testCffuFac.completedFuture(n)).get()
        );
    }

    @Test
    void test_allOf_CompletableFuture() throws Exception {
        testCffuFac.allOf(completedFuture(n), completedFuture(anotherN)).get();
        testCffuFac.allOf(completedFuture(anotherN)).get();

        assertNull(testCffuFac.allOf().get());

        ////////////////////////////////////////

        testCffuFac.allFailFastOf(completedFuture(n), completedFuture(anotherN)).get();
        testCffuFac.allFailFastOf(completedFuture(anotherN)).get();

        assertNull(testCffuFac.allFailFastOf().get());

        testCffuFac.allFailFastOf(testCffuFac.completedFuture(n), testCffuFac.completedFuture(anotherN)).get();
        testCffuFac.allFailFastOf(testCffuFac.completedFuture(anotherN)).get();
    }

    @Test
    void test_allOf_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allResultsOf(
                        testCffuFac.completedFuture(n),
                        testCffuFac.failedFuture(rte),
                        testCffuFac.completedFuture(s)
                ).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allResultsFailFastOf(
                        testCffuFac.completedFuture(n),
                        testCffuFac.failedFuture(rte),
                        testCffuFac.completedFuture(s)
                ).get()
        ).getCause());
    }

    @Test
    void test_mostOf() throws Exception {
        final Cffu<Integer> completed = testCffuFac.completedFuture(n);
        final CompletionStage<Integer> completedStage = testCffuFac.completedStage(n);
        final Cffu<Integer> failed = testCffuFac.failedFuture(rte);
        final Cffu<Integer> cancelled = testCffuFac.toCffu(cancelledFuture());
        final Cffu<Integer> incomplete = testCffuFac.toCffu(incompleteCf());

        assertEquals(0, testCffuFac.mostSuccessResultsOf(null, SHORT_WAIT_MS, MILLISECONDS).get().size());

        assertEquals(Arrays.asList(n, null, null, null), testCffuFac.mostSuccessResultsOf(
                null, SHORT_WAIT_MS, MILLISECONDS, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(Arrays.asList(n, anotherN, anotherN, anotherN), testCffuFac.mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(Arrays.asList(anotherN, anotherN, anotherN), testCffuFac.mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(Arrays.asList(anotherN, anotherN), testCffuFac.mostSuccessResultsOf(
                anotherN, Long.MAX_VALUE, MILLISECONDS, failed, cancelled
        ).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_anyOf_anySuccessOf() throws Exception {
        assertEquals(n, testCffuFac.anyOf(
                incompleteCf(),
                completedFuture(n)
        ).get());
        assertEquals(n, testCffuFac.anyOf(
                completedFuture(n)
        ).get());

        assertFalse(testCffuFac.anyOf().isDone());

        assertEquals(n, testCffuFac.anyOf(
                testCffuFac.completedFuture(n),
                testCffuFac.newIncompleteCffu()
        ).get());
        assertEquals(n, testCffuFac.anyOf(
                testCffuFac.completedFuture(n)
        ).get());

        ////////////////////////////////////////

        assertEquals(n, testCffuFac.anySuccessOf(incompleteCf(), completedFuture(n)).get());
        assertEquals(n, testCffuFac.anySuccessOf(completedFuture(n)).get());

        assertSame(NoCfsProvidedException.class, testCffuFac.anySuccessOf().exceptionNow().getClass());

        assertEquals(n, testCffuFac.anySuccessOf(
                testCffuFac.completedFuture(n),
                testCffuFac.newIncompleteCffu()
        ).get());
        assertEquals(n, testCffuFac.anySuccessOf(testCffuFac.completedFuture(n)).get());
    }

    @Test
    void test_anyOf_CompletableFuture() throws Exception {
        testCffuFac.anyOf(completedFuture(n), completedFuture(anotherN)).get();
        assertEquals(anotherN, testCffuFac.anyOf(completedFuture(anotherN)).get());

        assertFalse(testCffuFac.anyOf().isDone());

        ////////////////////////////////////////

        testCffuFac.anySuccessOf(completedFuture(n), completedFuture(anotherN)).get();
        assertEquals(anotherN, testCffuFac.anySuccessOf(completedFuture(anotherN)).get());

        assertInstanceOf(NoCfsProvidedException.class, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.anySuccessOf().get()
        ).getCause());

        testCffuFac.anySuccessOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(anotherN)
        ).get();
        assertEquals(anotherN, testCffuFac.anySuccessOf(
                testCffuFac.completedFuture(anotherN)
        ).get());
    }

    @Test
    void test_anyOf_exceptionally() throws Exception {
        // first exceptionally completed anyOf cf win,
        // even later cfs normally completed!

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.anyOf(incompleteCf(), failedFuture(rte), incompleteCf()).get()
        ).getCause());

        // first normally completed anyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, testCffuFac.anyOf(
                incompleteCf(),
                completedFuture(n),
                incompleteCf()
        ).get());
    }

    @Test
    void test_anySuccessOf__trivial_case() throws Exception {
        // success then success
        assertEquals(n, testCffuFac.anySuccessOf(
                testCffuFac.newIncompleteCffu(),
                testCffuFac.newIncompleteCffu(),
                testCffuFac.supplyAsync(supplyLater(anotherN)),
                testCffuFac.completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, testCffuFac.anySuccessOf(
                testCffuFac.newIncompleteCffu(),
                testCffuFac.newIncompleteCffu(),
                testCffuFac.supplyAsync(supplyLater(rte)),
                testCffuFac.completedFuture(n)
        ).get());

        // all success
        assertEquals(n, testCffuFac.anySuccessOf(
                testCffuFac.supplyAsync(supplyLater(anotherN)),
                testCffuFac.supplyAsync(supplyLater(anotherN)),
                testCffuFac.completedFuture(n)
        ).get());

        //////////////////////////////////////////////////////////////////////////////

        // success then success
        assertEquals(n, testCffuFac.anySuccessOf(
                incompleteCf(),
                incompleteCf(),
                CompletableFuture.supplyAsync(supplyLater(anotherN)),
                completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, testCffuFac.anySuccessOf(
                incompleteCf(),
                incompleteCf(),
                CompletableFuture.supplyAsync(supplyLater(rte)),
                completedFuture(n)
        ).get());

        // all success
        assertEquals(n, testCffuFac.anySuccessOf(
                CompletableFuture.supplyAsync(supplyLater(anotherN)),
                CompletableFuture.supplyAsync(supplyLater(anotherN)),
                completedFuture(n)
        ).get());

        //////////////////////////////////////////////////////////////////////////////

        assertSame(NoCfsProvidedException.class, testCffuFac.anySuccessOf().exceptionNow().getClass());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostSuccessTupleOf Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allTupleFailFastOf() throws Exception {
        assertEquals(Tuple2.of(n, s), testCffuFac.allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(nnn)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), testCffuFac.allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN),
                testCffuFac.completedFuture(nnn)
        ).get());
    }

    @Test
    void test_allTupleFailFast_Of_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allTupleFailFastOf(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allTupleFailFastOf(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allTupleFailFastOf(
                        completedFuture(n),
                        completedFuture(d),
                        failedFuture(rte),
                        completedFuture(s),
                        completedFuture(anotherN)
                ).get()
        ).getCause());
    }

    @Test
    void test_allSuccessTupleOf() throws Exception {
        assertEquals(Tuple2.of(n, s), testCffuFac.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(nnn)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), testCffuFac.allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN),
                testCffuFac.completedFuture(nnn)
        ).get());
    }

    @Test
    void test_mostSuccessTupleOf() throws Exception {
        final Cffu<Integer> completed = testCffuFac.completedFuture(n);
        final CompletionStage<String> anotherCompleted = testCffuFac.completedStage(s);
        final Cffu<Integer> failed = testCffuFac.failedFuture(rte);
        final Cffu<Integer> cancelled = testCffuFac.toCffu(cancelledFuture());
        final Cffu<Integer> incomplete = testCffuFac.toCffu(incompleteCf());

        assertEquals(Tuple2.of(n, s), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), testCffuFac.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    @Test
    void test_allTupleOf() throws Exception {
        assertEquals(Tuple2.of(n, s), testCffuFac.allTupleOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(nnn)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), testCffuFac.allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN),
                testCffuFac.completedFuture(nnn)
        ).get());
    }

    @Test
    void test_allTupleOf_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allTupleOf(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allTupleOf(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.allTupleOf(
                        completedFuture(n),
                        completedFuture(d),
                        failedFuture(rte),
                        completedFuture(s),
                        completedFuture(anotherN)
                ).get()
        ).getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## Immediate Value Argument Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_completedFuture() throws Exception {
        assertEquals(n, testCffuFac.completedFuture(n).get());
    }

    @Test
    void test_completedStage() throws Exception {
        CompletionStage<Integer> stage = testCffuFac.completedStage(n);
        CompletionStage<Integer> sa = stage.thenApply(identity());

        assertEquals(n, stage.toCompletableFuture().get());
        assertEquals(n, sa.toCompletableFuture().get());

        // CAUTION: Last check minimal stage, may rewrite the CF by obtrude* methods
        MinStageTestUtils.shouldBeMinimalStage((Cffu<?>) stage);
        MinStageTestUtils.shouldBeMinimalStage((Cffu<?>) sa);
    }

    @Test
    void test_failedFuture() throws Exception {
        Cffu<Integer> cf = testCffuFac.failedFuture(rte);

        assertSame(rte, assertThrowsExactly(CompletionException.class, cf::join).getCause());
        assertEquals(n, cf.exceptionally(throwable -> n).get());

        MinStageTestUtils.shouldNotBeMinimalStage(cf);
    }

    @Test
    void test_failedStage() throws Exception {
        CompletionStage<Integer> stage = testCffuFac.failedStage(rte);
        CompletionStage<Integer> sa = stage.thenApply(identity());
        CompletionStage<Integer> se = stage.exceptionally(throwable -> n);

        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                failedFuture(rte).toCompletableFuture().join()
        ).getCause());
        assertEquals(n, se.toCompletableFuture().get());

        // CAUTION: Last check minimal stage, may rewrite the CF by obtrude* methods
        MinStageTestUtils.shouldBeMinimalStage((Cffu<Integer>) stage);
        MinStageTestUtils.shouldBeMinimalStage((Cffu<Integer>) sa);
        MinStageTestUtils.shouldBeMinimalStage((Cffu<Integer>) se);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## CompletionStage Argument Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_toCffu() throws Exception {
        Cffu<Integer> cf = testCffuFac.toCffu(completedFuture(n));
        assertEquals(n, cf.get());
        MinStageTestUtils.shouldNotBeMinimalStage(cf);

        final Cffu<Integer> cffu_in = testCffuFac.completedFuture(n);

        CffuFactory fac = CffuFactory.builder(dummyExecutor).forbidObtrudeMethods(true).build();
        Cffu<Integer> cffu = fac.toCffu(cffu_in);
        assertNotSame(cffu_in, cffu);
        assertSame(dummyExecutor, unwrapMadeExecutor(cffu));
        assertSame(fac, cffu.cffuFactory());
        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                cffu.obtrudeValue(anotherN)
        ).getMessage());

        assertSame(cffu_in, testCffuFac.toCffu(cffu_in));
        final CompletionStage<Integer> minCffu = testCffuFac.completedStage(n);
        assertNotSame(minCffu, testCffuFac.toCffu(minCffu));
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_toCffu__for_factoryMethods_of_Java9() {
        CompletableFuture<Object> cf1 = CompletableFuture.failedFuture(rte);
        assertFalse(testCffuFac.toCffu(cf1).isMinimalStage());
        MinStageTestUtils.shouldNotBeMinimalStage(cf1);

        Cffu<Integer> cf2 = testCffuFac.toCffu(CompletableFuture.completedStage(n));
        assertFalse(cf2.isMinimalStage());
        MinStageTestUtils.shouldNotBeMinimalStage(cf2);

        Cffu<Object> cf3 = testCffuFac.toCffu(CompletableFuture.failedStage(rte));
        assertFalse(cf3.isMinimalStage());
        MinStageTestUtils.shouldNotBeMinimalStage(cf3);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_toCffuArray() throws Exception {
        Cffu<Integer>[] cffus = testCffuFac.toCffuArray(CompletableFuture.completedStage(n), completedFuture(n));
        assertEquals(n, cffus[1].get());

        MinStageTestUtils.shouldNotBeMinimalStage(cffus[0]);
        MinStageTestUtils.shouldNotBeMinimalStage(cffus[1]);
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Delay Execution
    ////////////////////////////////////////////////////////////////////////////////

    // tested in CffuApiCompatibilityTest#staticMethods_delayedExecutor

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Conversion Methods(static methods)
    //
    //    - cffuListToArray: List<Cffu> -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

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
                testCffuFac.toCffu(cfArray[0]),
                testCffuFac.toCffu(cfArray[1]),
        };

        assertArrayEquals(cfArray, toCompletableFutureArray(cfArray));
        assertArrayEquals(cfArray, toCompletableFutureArray(csArray));
        assertArrayEquals(cfArray, toCompletableFutureArray(cffuArray));
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
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(anotherN),
                testCffuFac.newIncompleteCffu()
        };

        assertArrayEquals(input, CffuFactory.cffuListToArray(Arrays.asList(input)));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Getter Methods of CffuFactory properties
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_getter() {
        assertEquals("CffuMadeExecutor of executor(" + testExecutor + ")",
                testCffuFac.defaultExecutor().toString());

        assertSame(testExecutor, unwrapMadeExecutor(testCffuFac));
        assertFalse(testCffuFac.forbidObtrudeMethods());

        CffuFactory fac = CffuFactory.builder(dummyExecutor).forbidObtrudeMethods(true).build();
        assertSame(dummyExecutor, unwrapMadeExecutor(fac));
        assertTrue(fac.forbidObtrudeMethods());

        final CffuFactory fac2 = testCffuFac.resetDefaultExecutor(dummyExecutor);
        assertSame(dummyExecutor, unwrapMadeExecutor(fac2));
        assertEquals(testCffuFac.forbidObtrudeMethods(), fac2.forbidObtrudeMethods());

        final CffuFactory fac3 = testCffuFac.resetDefaultExecutor(fac2.defaultExecutor());
        assertSame(fac2.defaultExecutor(), fac3.defaultExecutor());
        assertEquals(fac2.forbidObtrudeMethods(), fac3.forbidObtrudeMethods());
    }

    @Test
    void test_forbidObtrudeMethods_property() {
        CffuFactory fac2 = CffuFactory.builder(testExecutor).forbidObtrudeMethods(true).build();

        Cffu<Object> cf = fac2.newIncompleteCffu();

        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                cf.obtrudeValue(n)).getMessage()
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
            assertSame(commonPool(), unwrapMadeExecutor(fac));
        } else {
            String executorClassName = unwrapMadeExecutor(fac).getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }

        assertEquals(n, fac.supplyAsync(() -> n).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private final Executor dummyExecutor = Runnable::run;
}
