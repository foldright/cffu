package io.foldright.cffu2;

import io.foldright.cffu2.tuple.Tuple2;
import io.foldright.cffu2.tuple.Tuple3;
import io.foldright.cffu2.tuple.Tuple4;
import io.foldright.cffu2.tuple.Tuple5;
import io.foldright.test_utils.MinStageTestUtils;
import io.foldright.test_utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static io.foldright.cffu2.CompletableFutureUtils.failedFuture;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.testCffuFac;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.lang.Thread.currentThread;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.*;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


/**
 * @see io.foldright.compatibility_test.CffuApiCompatibilityTest
 */
@SuppressWarnings({"RedundantThrows", "DataFlowIssue", "JavadocReference", "resource"})
class CffuFactoryTest {
    // region# Factory Methods
    // region## supplyAsync*/runAsync* Methods(create by action) + Multi-Actions(M*) Methods(create by actions)

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
        MCffu<Integer, List<Integer>>[] cfs = new MCffu[]{
                testCffuFac.mSupplyFailFastAsync(supplier, supplier),
                testCffuFac.mSupplyFailFastAsync(testExecutor, supplier, supplier),
                testCffuFac.mSupplyAllSuccessAsync(anotherN, supplier, supplier),
                testCffuFac.mSupplyAllSuccessAsync(testExecutor, anotherN, supplier, supplier),
                testCffuFac.mSupplyMostSuccessAsync(anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                testCffuFac.mSupplyMostSuccessAsync(testExecutor, anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                testCffuFac.mSupplyAsync(supplier, supplier),
                testCffuFac.mSupplyAsync(testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (MCffu<Integer, List<Integer>> cf : cfs) {
            assertEquals(asList(n, n), cf.get());
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
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)

    @Test
    void test_mSupplyTupleAsync() throws Exception {
        final Supplier<Integer> supplier_n = supplyLater(n);
        final Supplier<String> supplier_s = supplyLater(s);
        final Supplier<Double> supplier_d = supplyLater(d);
        final Supplier<Integer> supplier_an = supplyLater(anotherN);
        final Supplier<Integer> supplier_nnn = supplyLater(nnn);

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, testExecutor).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, testExecutor).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an, testExecutor).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn, testExecutor).get());

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, testExecutor).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, testExecutor).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, testExecutor).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn, testExecutor).get());

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, testExecutor).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, testExecutor).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, testExecutor).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nnn, testExecutor).get());
    }

    @Test
    void test_mSupplyMostSuccessTupleAsync() throws Exception {
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
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, testExecutor).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, testExecutor).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, testExecutor).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn, testExecutor).get());
    }

    // endregion
    // region## allOf* Methods(including mostSuccessResultsOf)

    @Test
    void test_allOf() throws Exception {
        assertEquals(asList(n, n + 1),
                testCffuFac.allResultsFailFastOf(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(singletonList(n),
                testCffuFac.allResultsFailFastOf(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                testCffuFac.allResultsFailFastOf().get()
        );

        assertEquals(asList(n, n + 1),
                testCffuFac.allResultsFailFastOf(testCffuFac.completedFuture(n), testCffuFac.completedFuture(n + 1)).get()
        );
        assertEquals(singletonList(n),
                testCffuFac.allResultsFailFastOf(testCffuFac.completedFuture(n)).get()
        );

        ////////////////////////////////////////

        assertEquals(asList(n, n + 1),
                testCffuFac.allSuccessResultsOf(anotherN, completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(singletonList(n),
                testCffuFac.allSuccessResultsOf(anotherN, completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                testCffuFac.allResultsFailFastOf().get()
        );

        assertEquals(asList(n, n + 1),
                testCffuFac.allSuccessResultsOf(anotherN, testCffuFac.completedFuture(n), testCffuFac.completedFuture(n + 1)).get()
        );
        assertEquals(singletonList(n),
                testCffuFac.allSuccessResultsOf(anotherN, testCffuFac.completedFuture(n)).get()
        );

        ////////////////////////////////////////
        assertEquals(asList(n, n + 1),
                testCffuFac.allResultsOf(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(singletonList(n),
                testCffuFac.allResultsOf(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                testCffuFac.allResultsOf().get()
        );

        assertEquals(asList(n, n + 1),
                testCffuFac.allResultsOf(testCffuFac.completedFuture(n), testCffuFac.completedFuture(n + 1)).get()
        );
        assertEquals(singletonList(n),
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

        assertEquals(asList(n, null, null, null), testCffuFac.mostSuccessResultsOf(
                null, SHORT_WAIT_MS, MILLISECONDS, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(asList(n, anotherN, anotherN, anotherN), testCffuFac.mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(asList(anotherN, anotherN, anotherN), testCffuFac.mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(asList(anotherN, anotherN), testCffuFac.mostSuccessResultsOf(
                anotherN, Long.MAX_VALUE, MILLISECONDS, failed, cancelled
        ).get());
    }

    // endregion
    // region## anyOf* Methods

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
    // region## allTupleOf*/mostSuccessTupleOf Methods

    @Test
    void test_allTupleFailFastOf() throws Exception {
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().allTupleFailFastOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(nnn)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().allTupleFailFastOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().allTupleFailFastOf(
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
                testCffuFac.tupleOps().allTupleFailFastOf(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.tupleOps().allTupleFailFastOf(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.tupleOps().allTupleFailFastOf(
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
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(nnn)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().allSuccessTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().allSuccessTupleOf(
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

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), testCffuFac.tupleOps().mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    @Test
    void test_allTupleOf() throws Exception {
        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().allTupleOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().allTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(nnn)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), testCffuFac.tupleOps().allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), testCffuFac.tupleOps().allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), testCffuFac.tupleOps().allTupleOf(
                testCffuFac.completedFuture(n),
                testCffuFac.completedFuture(s),
                testCffuFac.completedFuture(d),
                testCffuFac.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, nnn), testCffuFac.tupleOps().allTupleOf(
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
                testCffuFac.tupleOps().allTupleOf(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.tupleOps().allTupleOf(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.tupleOps().allTupleOf(
                        completedFuture(n),
                        completedFuture(d),
                        failedFuture(rte),
                        completedFuture(s),
                        completedFuture(anotherN)
                ).get()
        ).getCause());
    }

    // endregion
    // region## Immediate Value Argument Factory Methods

    @Test
    void test_completedFuture() throws Exception {
        final Cffu<Integer> cf = testCffuFac.completedFuture(n);
        assertEquals(n, cf.get());

        MinStageTestUtils.shouldNotBeMinimalStage(cf);
    }

    @Test
    void test_failedFuture() throws Exception {
        Cffu<Integer> cf = testCffuFac.failedFuture(rte);

        assertSame(rte, assertThrowsExactly(CompletionException.class, cf::join).getCause());
        assertEquals(n, cf.exceptionally(throwable -> n).get());

        MinStageTestUtils.shouldNotBeMinimalStage(cf);
    }

    @Test
    void test_completedMCffu() throws Exception {
        {
            final List<Integer> empty = singletonList(n);
            final MCffu<Integer, List<Integer>> cf = testCffuFac.completedMCffu(empty);
            assertEquals(empty, cf.get());

            MinStageTestUtils.shouldNotBeMinimalStage(cf);
        }

        {
            final List<Integer> singletonList = singletonList(n);
            final MCffu<Integer, List<Integer>> cf = testCffuFac.completedMCffu(singletonList);
            assertEquals(singletonList, (cf.get()));

            MinStageTestUtils.shouldNotBeMinimalStage(cf);
        }

        {
            final List<String> list = asList("a", "b");
            final MCffu<String, List<String>> cf = testCffuFac.completedMCffu(list);
            assertEquals(list, cf.get());

            MinStageTestUtils.shouldNotBeMinimalStage(cf);
        }
    }

    @Test
    void test_failedMCffu() throws Exception {
        MCffu<Integer, List<Integer>> cf = testCffuFac.failedMCffu(rte);

        assertSame(rte, assertThrowsExactly(CompletionException.class, cf::join).getCause());
        final List<Integer> singletonList = singletonList(n);
        assertEquals(singletonList, cf.exceptionally(throwable -> singletonList).get());

        MinStageTestUtils.shouldNotBeMinimalStage(cf);
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

    @Test
    void test_fromSyncCall() throws Exception {
        final Cffu<Integer> cf = testCffuFac.fromSyncCall(() -> 42);
        assertTrue(cf.isDone());
        assertEquals(42, cf.get());

        final RuntimeException rte = new RuntimeException("foo");
        final ExecutionException ee = assertThrowsExactly(ExecutionException.class,
                () -> testCffuFac.fromSyncCall(() -> {throw rte;}).get());
        assertSame(rte, ee.getCause());
    }

    // endregion
    // region## CompletionStage Argument Factory Methods

    @Test
    void test_toCffu() throws Exception {
        Cffu<Integer> cffu0 = testCffuFac.toCffu(completedFuture(n));
        assertEquals(n, cffu0.get());
        MinStageTestUtils.shouldNotBeMinimalStage(cffu0);

        final Cffu<Integer> cffu_in = testCffuFac.completedFuture(n);

        CffuFactory fac = CffuFactory.builder(dummyExecutor).forbidObtrudeMethods(true).build();
        Cffu<Integer> cffu1 = fac.toCffu(cffu_in);
        assertNotSame(cffu_in, cffu1);
        assertSame(dummyExecutor, cffu1.defaultExecutor());
        assertSame(fac, cffu1.cffuFactory());
        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                cffu1.obtrudeValue(anotherN)
        ).getMessage());
        assertSame(cffu_in, testCffuFac.toCffu(cffu_in));

        final CompletionStage<Integer> minCffu = testCffuFac.completedStage(n);
        final Cffu<Integer> cffu2 = testCffuFac.toCffu(minCffu);
        assertNotSame(minCffu, cffu2);
        MinStageTestUtils.shouldNotBeMinimalStage(cffu2);
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
    void test_toMCffu() throws Exception {
        final List<Integer> singletonList = singletonList(n);
        MCffu<Integer, List<Integer>> mCffu0 = testCffuFac.toMCffu(completedFuture(singletonList));
        assertEquals(singletonList, mCffu0.get());
        MinStageTestUtils.shouldNotBeMinimalStage(mCffu0);

        final MCffu<Integer, List<Integer>> cffu_in = testCffuFac.completedMCffu(singletonList);

        CffuFactory fac = CffuFactory.builder(dummyExecutor).forbidObtrudeMethods(true).build();
        MCffu<Integer, List<Integer>> mCffu1 = fac.toMCffu(cffu_in);
        assertNotSame(cffu_in, mCffu1);
        assertSame(dummyExecutor, mCffu1.defaultExecutor());
        assertSame(fac, mCffu1.cffuFactory());
        assertEquals("obtrude methods is forbidden by cffu", assertThrowsExactly(UnsupportedOperationException.class, () ->
                mCffu1.obtrudeValue(singletonList(anotherN))
        ).getMessage());
        assertSame(cffu_in, testCffuFac.toMCffu(cffu_in));

        final CompletionStage<List<Integer>> minCffu = testCffuFac.completedStage(singletonList);

        final MCffu<Integer, List<Integer>> mCffu2 = testCffuFac.toMCffu(minCffu);
        assertNotSame(minCffu, mCffu2);
        MinStageTestUtils.shouldNotBeMinimalStage(mCffu2);

        MCffu<Integer, List<Integer>> minMCffu = Cffu.asMCffu((Cffu<List<Integer>>) minCffu);
        final MCffu<Integer, List<Integer>> mCffu3 = testCffuFac.toMCffu(minMCffu);
        assertNotSame(minMCffu, mCffu3);
        MinStageTestUtils.shouldNotBeMinimalStage(mCffu3);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_toMCffu__for_factoryMethods_of_Java9() {
        CompletableFuture<List<Object>> cf1 = CompletableFuture.failedFuture(rte);
        assertFalse(testCffuFac.toMCffu(cf1).isMinimalStage());
        MinStageTestUtils.shouldNotBeMinimalStage(cf1);

        final List<Integer> singletonList = singletonList(n);
        MCffu<Integer, List<Integer>> cf2 = testCffuFac.toMCffu(CompletableFuture.completedStage(singletonList));
        assertFalse(cf2.isMinimalStage());
        MinStageTestUtils.shouldNotBeMinimalStage(cf2);

        MCffu<Object, List<Object>> cf3 = testCffuFac.toMCffu(CompletableFuture.failedStage(rte));
        assertFalse(cf3.isMinimalStage());
        MinStageTestUtils.shouldNotBeMinimalStage(cf3);
    }

    // endregion
    // region## Incomplete Cffu/MCffu Constructor

    @Test
    void test_newIncomplete() {
        Cffu<Integer> cf = testCffuFac.newIncompleteCffu();
        assertFalse(cf.isDone());

        MCffu<Integer, List<Integer>> mCffu = testCffuFac.newIncompleteMCffu();
        assertFalse(mCffu.isDone());
    }

    // endregion
    // endregion
    // region# Delay Execution

    // tested in CffuApiCompatibilityTest#staticMethods_delayedExecutor

    // endregion
    // region# Getter Methods of CffuFactory properties

    @Test
    void test_getter() {
        assertSame(testExecutor, testCffuFac.defaultExecutor());
        assertThat(testCffuFac.defaultExecutor.toString()).startsWith("CffuDefaultExecutor, original: ");

        assertSame(testExecutor, testCffuFac.defaultExecutor());
        assertFalse(testCffuFac.forbidObtrudeMethods());

        CffuFactory fac = CffuFactory.builder(dummyExecutor).forbidObtrudeMethods(true).build();
        assertSame(dummyExecutor, fac.defaultExecutor());
        assertTrue(fac.forbidObtrudeMethods());

        final CffuFactory fac2 = testCffuFac.withDefaultExecutor(dummyExecutor);
        assertSame(dummyExecutor, fac2.defaultExecutor());
        assertEquals(testCffuFac.forbidObtrudeMethods(), fac2.forbidObtrudeMethods());

        assertSame(fac2, fac2.withDefaultExecutor(fac2.defaultExecutor()));

        assertEquals("input defaultExecutor should never be a CffuDefaultExecutor", assertThrowsExactly(
                IllegalArgumentException.class, () -> fac2.withDefaultExecutor(fac2.defaultExecutor)).getMessage());
        assertEquals("input defaultExecutor should never be a CffuMadeExecutor", assertThrowsExactly(
                IllegalArgumentException.class, () -> fac2.withDefaultExecutor(fac2.defaultExecutor.screened)).getMessage());
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
    void test_executorSetting_commonPool() throws Exception {
        CffuFactory fac = CffuFactory.builder(commonPool()).build();
        assertSame(commonPool(), (fac).defaultExecutor());

        assertEquals(n, fac.supplyAsync(() -> n).get());
    }

    @Test
    void test_executorSetting_MayRunIn_ThreadPerTaskExecutor() throws Exception {
        final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;
        final String threadNamePrefixOfCommonPool = "ForkJoinPool.commonPool-worker-";

        CffuFactory fac = CffuFactory.builder(commonPool()).build();
        if (USE_COMMON_POOL) {
            fac.runAsync(() -> assertThat(currentThread().getName()).startsWith(threadNamePrefixOfCommonPool)).join();
        } else {
            final int COUNT = 100;

            List<Cffu<Void>> cffuList = new ArrayList<>(COUNT);
            CopyOnWriteArraySet<Thread> runThreads = new CopyOnWriteArraySet<>();
            for (int i = 0; i < COUNT; i++) {
                final Cffu<Void> cffu = fac.runAsync(() -> {
                    assertThat(currentThread().getName()).doesNotStartWith(threadNamePrefixOfCommonPool);
                    runThreads.add(currentThread());
                });
                cffuList.add(cffu);
            }
            CfIterableUtils.allOf(cffuList).join();

            assertThat(runThreads).hasSize(COUNT);
        }
    }

    // endregion
    // region# More Ops

    @Test
    void test_mSupply_iterable() throws Exception {
        assertThat(testCffuFac.iterableOps().mSupplyFailFastAsync(emptyList()).get()).isEmpty();
        assertThat(testCffuFac.iterableOps().mSupplyFailFastAsync(asList(() -> 1, () -> 2)).get()).containsExactly(1, 2);
        assertThat(testCffuFac.iterableOps().mSupplyFailFastAsync(asList(() -> 1, () -> 2), testExecutor).get()).containsExactly(1, 2);

        assertThat(testCffuFac.iterableOps().mSupplyAllSuccessAsync(-1, emptyList()).get()).isEmpty();
        assertThat(testCffuFac.iterableOps().mSupplyAllSuccessAsync(-1, asList(() -> {
            throw new RuntimeException();
        }, () -> 2)).get()).containsExactly(-1, 2);
        assertThat(testCffuFac.iterableOps().mSupplyAllSuccessAsync(-1, asList(() -> {
            throw new RuntimeException();
        }, () -> 2), testExecutor).get()).containsExactly(-1, 2);

        assertThat(testCffuFac.iterableOps().mSupplyMostSuccessAsync(-1, 1, SECONDS, emptyList()).get()).isEmpty();
        assertThat(testCffuFac.iterableOps().mSupplyMostSuccessAsync(-1, 1, SECONDS,
                asList(() -> {throw new RuntimeException();}, () -> 2)).get()).containsExactly(-1, 2);
        assertThat(testCffuFac.iterableOps().mSupplyMostSuccessAsync(-1, 1, SECONDS,
                asList(() -> {throw new RuntimeException();}, () -> 2), testExecutor).get()).containsExactly(-1, 2);

        assertThat(testCffuFac.iterableOps().mSupplyAsync(emptyList()).get()).isEmpty();
        assertThat(testCffuFac.iterableOps().mSupplyAsync(asList(() -> 1, () -> 2)).get()).containsExactly(1, 2);
        assertThat(testCffuFac.iterableOps().mSupplyAsync(asList(() -> 1, () -> 2), testExecutor).get()).containsExactly(1, 2);

        assertCfWithExType(testCffuFac.iterableOps().mSupplyAnySuccessAsync(emptyList()), NoCfsProvidedException.class);
        assertEquals(2, testCffuFac.iterableOps().mSupplyAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> 2)).get());
        assertEquals(2, testCffuFac.iterableOps().mSupplyAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> 2), testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(testCffuFac.iterableOps().mSupplyAnyAsync(emptyList()));
        assertEquals(2, testCffuFac.iterableOps().mSupplyAnyAsync(asList(() -> {
            sleep(MEDIAN_WAIT_MS);
            return 1;
        }, () -> 2)).get());
        assertEquals(2, testCffuFac.iterableOps().mSupplyAnyAsync(asList(() -> {
            sleep(MEDIAN_WAIT_MS);
            return 1;
        }, () -> 2), testExecutor).get());
    }

    @Test
    void test_mRun_iterable() throws Exception {
        assertNull(testCffuFac.iterableOps().mRunFailFastAsync(emptyList()).get());
        assertNull(testCffuFac.iterableOps().mRunFailFastAsync(asList(() -> {}, () -> {})).get());
        assertNull(testCffuFac.iterableOps().mRunFailFastAsync(asList(() -> {}, () -> {}), testExecutor).get());

        assertNull(testCffuFac.iterableOps().mRunAsync(emptyList()).get());
        assertNull(testCffuFac.iterableOps().mRunAsync(asList(() -> {}, () -> {})).get());
        assertNull(testCffuFac.iterableOps().mRunAsync(asList(() -> {}, () -> {}), testExecutor).get());

        assertCfWithExType(testCffuFac.iterableOps().mRunAnySuccessAsync(emptyList()), NoCfsProvidedException.class);
        assertNull(testCffuFac.iterableOps().mRunAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> {})).get());
        assertNull(testCffuFac.iterableOps().mRunAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> {}), testExecutor).get());

        assertCfStillIncompleteIn(testCffuFac.iterableOps().mRunAnyAsync(emptyList()));
        assertNull(testCffuFac.iterableOps().mRunAnyAsync(asList(() -> {}, () -> {})).get());
        assertNull(testCffuFac.iterableOps().mRunAnyAsync(asList(() -> {}, () -> {}), testExecutor).get());
    }

    @Test
    void test_allResultsFailFastOf_iterable() {
        // Test with an empty iterable
        assertThat(testCffuFac.iterableOps().allResultsFailFastOf(emptyList()).join()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(testCffuFac.iterableOps().allResultsFailFastOf(singletonList(future1)).join()).containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(testCffuFac.iterableOps().allResultsFailFastOf(asList(future1, future2, future3)).join()).containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_allSuccessResultsOf_iterable() {
        // Test with an empty iterable
        assertThat(testCffuFac.iterableOps().allSuccessResultsOf(null, emptyList()).join()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(testCffuFac.iterableOps().allSuccessResultsOf(null, singletonList(future1)).join()).containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(testCffuFac.iterableOps().allSuccessResultsOf(null, asList(future1, future2, future3)).join()).containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_mostSuccessResultsOf_iterable() throws Exception {
        assertThat(testCffuFac.iterableOps().mostSuccessResultsOf(null, 10, MILLISECONDS, emptyList()).get()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(testCffuFac.iterableOps().mostSuccessResultsOf(null, 1, TimeUnit.MILLISECONDS, singletonList(future1)).join())
                .containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(testCffuFac.iterableOps().mostSuccessResultsOf(null, 1, TimeUnit.MILLISECONDS, asList(future1, future2, future3)).join())
                .containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_allResultsOf_iterable() {
        // Test with an empty iterable
        assertThat(testCffuFac.iterableOps().allResultsOf(emptyList()).join()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(testCffuFac.iterableOps().allResultsOf(singletonList(future1)).join()).containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(testCffuFac.iterableOps().allResultsOf(asList(future1, future2, future3)).join()).containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_allFailFastOf_iterable() {
        // Test with an empty iterable
        assertNull(testCffuFac.iterableOps().allFailFastOf(emptyList()).join());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertNull(testCffuFac.iterableOps().allFailFastOf(singletonList(future1)).join());

        // Test with multiple completed futures
        RuntimeException rte = new RuntimeException();
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {throw rte;});
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> sleep(LONG_WAIT_MS));
        assertCfWithEx(testCffuFac.iterableOps().allFailFastOf(asList(future1, future2, future3)), rte);
    }

    @Test
    void test_allOf_iterable() {
        // Test with an empty iterable
        assertNull(testCffuFac.iterableOps().allOf(emptyList()).join());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertNull(testCffuFac.iterableOps().allOf(singletonList(future1)).join());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        // Test with multiple completed futures
        RuntimeException rte = new RuntimeException();
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {throw rte;});
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> sleep(LONG_WAIT_MS));
        assertCfStillIncompleteIn(testCffuFac.iterableOps().allOf(asList(future1, future2, future3)), MEDIAN_WAIT_MS, MILLISECONDS);
    }

    @Test
    void test_anySuccessOf_iterable() {
        // Test with an empty iterable
        assertCfWithExType(testCffuFac.iterableOps().anySuccessOf(emptyList()), NoCfsProvidedException.class);

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", testCffuFac.iterableOps().anySuccessOf(singletonList(future1)).join());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {throw new RuntimeException();});
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(MEDIAN_WAIT_MS);
            return "";
        });
        assertEquals("result1", testCffuFac.iterableOps().anySuccessOf(asList(future2, future1, future3)).join());
    }

    @Test
    void test_anyOf_iterable() {
        assertCfStillIncompleteIn(testCffuFac.iterableOps().anyOf(emptyList()));

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", testCffuFac.iterableOps().anyOf(singletonList(future1)).join());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {throw new RuntimeException();});
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(MEDIAN_WAIT_MS);
            return "";
        });
        assertTrue(asList("result1", "result2", "result3").contains(testCffuFac.iterableOps().anyOf(asList(future1, future2, future3)).join()));
    }

    @Test
    void test_parApply() throws Exception {
        assertThat(testCffuFac.parOps().parApplyFailFastAsync(emptyList(), (Integer x) -> x + 1).get()).isEmpty();
        assertThat(testCffuFac.parOps().parApplyFailFastAsync(asList(1, 2), x -> x + 1).get()).containsExactly(2, 3);
        assertThat(testCffuFac.parOps().parApplyFailFastAsync(asList(1, 2), x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertThat(testCffuFac.parOps().parApplyAllSuccessAsync(emptyList(), -1, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(testCffuFac.parOps().parApplyAllSuccessAsync(asList(1, 2), -1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(testCffuFac.parOps().parApplyAllSuccessAsync(asList(1, 2), -1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(testCffuFac.parOps().parApplyMostSuccessAsync(emptyList(), -1, 1, SECONDS, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(testCffuFac.parOps().parApplyMostSuccessAsync(asList(1, 2), -1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(testCffuFac.parOps().parApplyMostSuccessAsync(asList(1, 2), -1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(testCffuFac.parOps().parApplyAsync(emptyList(), (Integer x) -> x + 1).get()).isEmpty();
        assertThat(testCffuFac.parOps().parApplyAsync(asList(1, 2), x -> x + 1).get()).containsExactly(2, 3);
        assertThat(testCffuFac.parOps().parApplyAsync(asList(1, 2), x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertCfWithExType(testCffuFac.parOps().parApplyAnySuccessAsync(emptyList(), (Integer x) -> x + 1), NoCfsProvidedException.class);
        assertEquals(3, testCffuFac.parOps().parApplyAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get());
        assertEquals(3, testCffuFac.parOps().parApplyAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(testCffuFac.parOps().parApplyAnyAsync(emptyList(), (Integer x) -> x + 1));
        assertEquals(3, testCffuFac.parOps().parApplyAnyAsync(asList(1, 2), x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }).get());
        assertEquals(3, testCffuFac.parOps().parApplyAnyAsync(asList(1, 2), x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }, testExecutor).get());
    }

    @Test
    void test_parAccept() throws Exception {
        assertNull(testCffuFac.parOps().parAcceptFailFastAsync(emptyList(), (Integer x) -> {}).get());
        assertNull(testCffuFac.parOps().parAcceptFailFastAsync(asList(1, 2), x -> {}).get());
        assertNull(testCffuFac.parOps().parAcceptFailFastAsync(asList(1, 2), x -> {}, testExecutor).get());

        assertNull(testCffuFac.parOps().parAcceptAsync(emptyList(), (Integer x) -> {}).get());
        assertNull(testCffuFac.parOps().parAcceptAsync(asList(1, 2), x -> {}).get());
        assertNull(testCffuFac.parOps().parAcceptAsync(asList(1, 2), x -> {}, testExecutor).get());

        assertCfWithExType(testCffuFac.parOps().parAcceptAnySuccessAsync(emptyList(), (Integer x) -> {}), NoCfsProvidedException.class);
        assertNull(testCffuFac.parOps().parAcceptAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
        }).get());
        assertNull(testCffuFac.parOps().parAcceptAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
        }, testExecutor).get());

        assertCfStillIncompleteIn(testCffuFac.parOps().parAcceptAnyAsync(emptyList(), (Integer x) -> {}));
        assertNull(testCffuFac.parOps().parAcceptAnyAsync(asList(1, 2), x -> {}).get());
        assertNull(testCffuFac.parOps().parAcceptAnyAsync(asList(1, 2), x -> {}, testExecutor).get());
    }

    // endregion
    // region# Test helper fields

    private final Executor dummyExecutor = Runnable::run;
}
