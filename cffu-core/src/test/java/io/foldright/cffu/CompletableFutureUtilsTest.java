package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.foldright.cffu.CompletableFutureUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.assertRunningInExecutor;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("RedundantThrows")
class CompletableFutureUtilsTest {
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Factory Methods(including static methods of CF)
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Multi-Actions(M*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    @Test
    void test_mSupply() throws Exception {
        final Supplier<Integer> supplier = () -> {
            snoreZzz();
            return n;
        };

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<List<Integer>>[] cfs = new CompletableFuture[]{
                mSupplyFastFailAsync(supplier, supplier),
                mSupplyFastFailAsync(testExecutor, supplier, supplier),
                mSupplyAllSuccessAsync(anotherN, supplier, supplier),
                mSupplyAllSuccessAsync(anotherN, testExecutor, supplier, supplier),
                mSupplyMostSuccessAsync(anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                mSupplyMostSuccessAsync(anotherN, testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                mSupplyAsync(supplier, supplier),
                mSupplyAsync(testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (CompletableFuture<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }

        final long tick1 = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfs1 = new CompletableFuture[]{
                mSupplyAnySuccessAsync(supplier, supplier),
                mSupplyAnySuccessAsync(testExecutor, supplier, supplier),
                mSupplyAnyAsync(supplier, supplier),
                mSupplyAnyAsync(testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick1 < 50);

        for (CompletableFuture<Integer> cf : cfs1) {
            assertEquals(n, cf.get());
        }
    }

    @Test
    void test_mRun() throws Exception {
        final Runnable runnable = TestUtils::snoreZzz;

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                mRunFastFailAsync(runnable, runnable),
                mRunFastFailAsync(testExecutor, runnable, runnable),
                mRunAsync(runnable, runnable),
                mRunAsync(testExecutor, runnable, runnable),
                mRunAnySuccessAsync(runnable, runnable),
                mRunAnySuccessAsync(testExecutor, runnable, runnable),
                mRunAnyAsync(runnable, runnable),
                mRunAnyAsync(testExecutor, runnable, runnable),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (CompletableFuture<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Tuple-Multi-Actions(tupleM*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

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
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyMostSuccessAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyMostSuccessAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    @Test
    void test_tupleMSupplyAsync() throws Exception {
        final Supplier<Integer> supplier_n = () -> {
            snoreZzz();
            return n;
        };
        final Supplier<String> supplier_s = () -> {
            snoreZzz();
            return s;
        };

        final Supplier<Double> supplier_d = () -> {
            snoreZzz();
            return d;
        };
        final Supplier<Integer> supplier_an = () -> {
            snoreZzz();
            return anotherN;
        };
        final Supplier<Integer> supplier_nn = () -> {
            snoreZzz();
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), tupleMSupplyAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), tupleMSupplyFastFailAsync(supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), tupleMSupplyAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), tupleMSupplyFastFailAsync(supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyFastFailAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyFastFailAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    @Test
    void test_tupleMSupplyAllSuccessAsync() throws Exception {
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
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), tupleMSupplyAllSuccessAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyAllSuccessAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostSuccessResultsOf)
    ////////////////////////////////////////////////////////////

    @Test
    void test_allOf_methods__success__trivial_case() throws Exception {
        assertEquals(Arrays.asList(n, n + 1, n + 2), allResultsOf(
                completedFuture(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allResultsOf(
                completedStage(n),
                completedFuture(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allResultsOf(completedFuture(n)).get());
        assertEquals(Collections.singletonList(n), allResultsOf(completedStage(n)).get());

        assertEquals(Collections.emptyList(), allResultsOf().get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Arrays.asList(n, n + 1, n + 2), allResultsFastFailOf(
                completedStage(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allResultsFastFailOf(
                completedFuture(n),
                completedStage(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allResultsFastFailOf(completedFuture(n)).get());
        assertEquals(Collections.singletonList(n), allResultsFastFailOf(completedStage(n)).get());

        assertEquals(Collections.emptyList(), allResultsFastFailOf().get());

        ////////////////////////////////////////////////////////////////////////////////

        Arrays.asList(
                allOf(completedFuture(n), completedStage(n + 1), completedFuture(n + 2)),
                allOf(completedStage(n), completedFuture(n + 1)),
                allOf(completedFuture(n)),
                allOf(completedStage(n)),
                allOf(),

                allFastFailOf(completedFuture(n), completedStage(n + 1), completedFuture(n + 2)),
                allFastFailOf(completedStage(n), completedFuture(n + 1)),
                allFastFailOf(completedFuture(n)),
                allFastFailOf(completedStage(n)),
                allFastFailOf()
        ).forEach(f -> assertNull(f.join()));
    }

    @Test
    void test_allOf__exceptionally() throws Exception {
        final RuntimeException ex1 = new RuntimeException("ex1");
        final RuntimeException ex2 = new RuntimeException("ex2");

        ////////////////////////////////////////////////////////////////////////////////
        // allResultsOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allResultsOf: the ex of first given cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allResultsOf: the ex of first given cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertThrowsExactly(TimeoutException.class, () ->
                allResultsOf(
                        completedFuture(n),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        );

        // incomplete fail incomplete
        assertThrowsExactly(TimeoutException.class, () ->
                allResultsOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        );

        ////////////////////////////////////////////////////////////////////////////////
        // allResultsFastFailOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allResultsFastFailOf: the ex of first given cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsFastFailOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(anotherRte, assertThrowsExactly(ExecutionException.class, () ->
                // allResultsFastFailOf: the ex of first given cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsFastFailOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsFastFailOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsFastFailOf(
                        completedFuture(n),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsFastFailOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // allFastFailOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allFastFailOf: the ex of first complete(in time) cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allFastFailOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(anotherRte, assertThrowsExactly(ExecutionException.class, () ->
                // allFastFailOf: the ex of first complete(in time) cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allFastFailOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allFastFailOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allFastFailOf(
                        completedFuture(n),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allFastFailOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // allOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allOf: the ex of first complete(in time) cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allOf: the ex of first complete(in time) cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertThrowsExactly(TimeoutException.class, () ->
                allOf(
                        completedFuture(n),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        );

        // incomplete fail incomplete
        assertThrowsExactly(TimeoutException.class, () ->
                allOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        );
    }

    @Test
    void test_mostOf() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final CompletionStage<Integer> completedStage = completedStage(n);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cancelled = cancelledFuture();
        final CompletableFuture<Integer> incomplete = incompleteCf();

        // 0 input cf
        assertEquals(0, mostSuccessResultsOf(null, SHORT_WAIT_MS, MILLISECONDS).get().size());

        // 1 input cf
        assertEquals(Collections.singletonList(n), mostSuccessResultsOf(
                null, SHORT_WAIT_MS, MILLISECONDS, completed).get());
        assertEquals(Collections.singletonList(n), mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, completedStage).get());

        assertEquals(Collections.singletonList(anotherN), mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, failed).get());
        assertEquals(Collections.singletonList(anotherN), mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, cancelled).get());
        assertEquals(Collections.singletonList(anotherN), mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, incomplete).get());

        // more input cf
        assertEquals(Arrays.asList(n, null, null, null), mostSuccessResultsOf(
                null, SHORT_WAIT_MS, MILLISECONDS, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(Arrays.asList(n, anotherN, anotherN, anotherN), mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(Arrays.asList(anotherN, anotherN, anotherN), mostSuccessResultsOf(
                anotherN, SHORT_WAIT_MS, MILLISECONDS, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(Arrays.asList(anotherN, anotherN), mostSuccessResultsOf(
                anotherN, LONG_WAIT_MS, MILLISECONDS, failed, cancelled
        ).get());
    }

    @Test
    void test_mostOf_wontModifyInputCf() throws Exception {
        final CompletableFuture<Integer> incomplete = incompleteCf();
        final CompletableFuture<Integer> incomplete2 = incompleteCf();

        assertEquals(Collections.singletonList(null), mostSuccessResultsOf(
                null, SHORT_WAIT_MS, MILLISECONDS, incomplete
        ).get());
        assertEquals(Arrays.asList(null, null), mostSuccessResultsOf(
                null, SHORT_WAIT_MS, MILLISECONDS, incomplete, incomplete2
        ).get());

        assertEquals(CffuState.RUNNING, state(incomplete));
        assertEquals(CffuState.RUNNING, state(incomplete2));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    ////////////////////////////////////////////////////////////

    @Test
    void test_anyOf_anySuccessOf__trivial_case() throws Exception {
        assertEquals(n, anyOf(
                completedFuture(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(n, anyOf(
                completedStage(n),
                completedStage(n + 1)
        ).get());

        assertEquals(n, anyOf(completedFuture(n)).get());
        assertEquals(n, anyOf(completedStage(n)).get());

        assertFalse(anyOf().isDone());

        // success with incomplete CF
        assertEquals(n, anyOf(
                incompleteCf(),
                incompleteCf(),
                completedFuture(n)
        ).get());
        assertEquals(n, anyOf(
                incompleteCf(),
                incompleteCf(),
                completedStage(n)
        ).get());

        ////////////////////////////////////////

        assertEquals(n, anySuccessOf(
                completedFuture(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());
        assertEquals(n, anySuccessOf(
                completedStage(n),
                completedFuture(n + 1)
        ).get());

        assertEquals(n, anySuccessOf(completedFuture(n)).get());
        assertEquals(n, anySuccessOf(completedStage(n)).get());

        assertInstanceOf(NoCfsProvidedException.class, assertThrowsExactly(ExecutionException.class, () ->
                anySuccessOf().get()
        ).getCause());

        // success with incomplete CF
        assertEquals(n, anySuccessOf(
                incompleteCf(),
                incompleteCf(),
                completedFuture(n)
        ).get());
        assertEquals(n, anySuccessOf(
                incompleteCf(),
                incompleteCf(),
                completedStage(n)
        ).get());
    }

    @Test
    void test_anyOf__exceptionally() throws Exception {
        final RuntimeException ex1 = new RuntimeException("ex1");
        final RuntimeException ex2 = new RuntimeException("ex2");

        ////////////////////////////////////////////////////////////////////////////////
        // anyOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(anotherRte, assertThrowsExactly(ExecutionException.class, () ->
                // anyOf: the ex of first failed cf argument win.
                //   ❗dependent on the implementation behavior of `CF.anyOf`️
                anyOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                anyOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get()
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // anySuccessOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // anySuccessOf: the ex of first failed cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                anySuccessOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // incomplete fail incomplete
        assertThrowsExactly(TimeoutException.class, () ->
                anySuccessOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause();
    }

    @Test
    void test_anyOf__concurrent() throws Exception {
        // incomplete/wait-success then success
        assertEquals(n, anyOf(
                incompleteCf(),
                incompleteCf(),
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOf(
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // success then failed
        assertEquals(n, anyOf(
                incompleteCf(),
                incompleteCf(),
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // failed then success
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                anyOf(
                        CompletableFuture.supplyAsync(() -> {
                            snoreZzz();
                            return n;
                        }),
                        failedFuture(rte),
                        failedFuture(rte)
                ).get()
        ).getCause());

        ////////////////////////////////////////

        // incomplete/wait-success then success
        assertEquals(n, anySuccessOf(
                incompleteCf(),
                incompleteCf(),
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anySuccessOf(
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // success then failed
        assertEquals(n, anySuccessOf(
                incompleteCf(),
                incompleteCf(),
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // failed then success
        assertEquals(n, anySuccessOf(
                CompletableFuture.supplyAsync(() -> {
                    snoreZzz();
                    return n;
                }),
                failedFuture(rte),
                failedFuture(rte)
        ).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostSuccessTupleOf Methods
    ////////////////////////////////////////////////////////////

    @Test
    void test_allTupleOf() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletionStage<String> cf_s = completedStage(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        assertEquals(Tuple2.of(n, s), allTupleOf(cf_n, cf_s).get());
        assertEquals(Tuple2.of(n, s), allTupleFastFailOf(cf_n, cf_s).get());

        assertEquals(Tuple3.of(n, s, d), allTupleOf(cf_n, cf_s, cf_d).get());
        assertEquals(Tuple3.of(n, s, d), allTupleFastFailOf(cf_n, cf_s, cf_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), allTupleOf(cf_n, cf_s, cf_d, cf_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), allTupleFastFailOf(cf_n, cf_s, cf_d, cf_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), allTupleOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), allTupleFastFailOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
    }

    @Test
    void test_allTupleOf_exceptionally() throws Exception {
        final CompletableFuture<Object> incomplete = incompleteCf();
        final CompletableFuture<Object> fail = failedFuture(rte);

        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletionStage<String> cf_s = completedStage(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, fail).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFastFailOf(incomplete, fail).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, fail, cf_s).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFastFailOf(incomplete, fail, cf_s).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, fail, cf_d, cf_s).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFastFailOf(incomplete, fail, cf_d, cf_s).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, cf_d, fail, cf_s, cf_an).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFastFailOf(incomplete, cf_d, fail, cf_s, cf_an).get()
        ).getCause());
    }

    @Test
    void test_allTupleOf_NotFastFail() throws Exception {
        final CompletableFuture<Object> incomplete = incompleteCf();
        final CompletableFuture<Object> fail = failedFuture(rte);

        final CompletableFuture<String> cf_s = completedFuture(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);

        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, fail).get(SHORT_WAIT_MS, MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, fail, cf_s).get(SHORT_WAIT_MS, MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, fail, cf_d, cf_s).get(SHORT_WAIT_MS, MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, cf_d, fail, cf_s, cf_an).get(SHORT_WAIT_MS, MILLISECONDS)
        );
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Immediate Value Argument Factory Methods(backport methods)
    ////////////////////////////////////////////////////////////

    @Test
    void test_failedFuture() throws Exception {
        assertTrue(failedFuture(rte).isDone());
        assertEquals(n, completedStage(n).toCompletableFuture().get());
        assertTrue(failedStage(rte).toCompletableFuture().isDone());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Delay Execution(backport methods)
    ////////////////////////////////////////////////////////////

    @Test
    void test_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = delayedExecutor(1, MILLISECONDS);
        CompletableFuture.runAsync(() -> holder.set(testName), delayer).get();
        assertEquals(testName, holder.get());
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Instance Methods(including new enhanced + backport methods)
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions(thenM*) Methods
    ////////////////////////////////////////////////////////////

    @Test
    void test_thenMRun() throws Exception {
        final Runnable runnable = TestUtils::snoreZzz;
        final CompletableFuture<Object> completed = completedFuture(null);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                thenMRunAsync(completed, runnable, runnable),
                thenMRunAsync(completed, testExecutor, runnable, runnable),
                thenMRunFastFailAsync(completed, runnable, runnable),
                thenMRunFastFailAsync(completed, testExecutor, runnable, runnable),
                thenMRunAnySuccessAsync(completed, runnable, runnable),
                thenMRunAnySuccessAsync(completed, testExecutor, runnable, runnable),
                thenMRunAnyAsync(completed, runnable, runnable),
                thenMRunAnyAsync(completed, testExecutor, runnable, runnable),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (CompletableFuture<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_thenMAccept() throws Exception {
        final Consumer<Integer> consumer = (x) -> {
            assertEquals(n, x);
            snoreZzz();
        };
        final CompletableFuture<Integer> completed = completedFuture(n);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                thenMAcceptAsync(completed, consumer, consumer),
                thenMAcceptAsync(completed, testExecutor, consumer, consumer),
                thenMAcceptFastFailAsync(completed, consumer, consumer),
                thenMAcceptFastFailAsync(completed, testExecutor, consumer, consumer),
                thenMAcceptAnySuccessAsync(completed, consumer, consumer),
                thenMAcceptAnySuccessAsync(completed, testExecutor, consumer, consumer),
                thenMAcceptAnyAsync(completed, consumer, consumer),
                thenMAcceptAnyAsync(completed, testExecutor, consumer, consumer),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (CompletableFuture<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_thenMApply() throws Exception {
        final Function<Integer, Integer> supplier = (x) -> {
            snoreZzz();
            return n;
        };
        final CompletableFuture<Integer> completed = completedFuture(n);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<List<Integer>>[] cfs = new CompletableFuture[]{
                thenMApplyFastFailAsync(completed, supplier, supplier),
                thenMApplyFastFailAsync(completed, testExecutor, supplier, supplier),
                thenMApplyAllSuccessAsync(completed, anotherN, supplier, supplier),
                thenMApplyAllSuccessAsync(completed, anotherN, testExecutor, supplier, supplier),
                thenMApplyMostSuccessAsync(completed, anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                thenMApplyMostSuccessAsync(completed, anotherN, testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                thenMApplyAsync(completed, supplier, supplier),
                thenMApplyAsync(completed, testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (CompletableFuture<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }

        final long tick1 = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfs1 = new CompletableFuture[]{
                thenMApplyAnySuccessAsync(completed, supplier, supplier),
                thenMApplyAnySuccessAsync(completed, testExecutor, supplier, supplier),
                thenMApplyAnyAsync(completed, supplier, supplier),
                thenMApplyAnyAsync(completed, testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick1 < 50);

        for (CompletableFuture<Integer> cf : cfs1) {
            assertEquals(n, cf.get());
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Then-Tuple-Multi-Actions(thenTupleM*) Methods
    ////////////////////////////////////////////////////////////

    @Test
    void test_thenTupleMApplyMostSuccessAsync() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            snoreZzz();
            return n;
        };

        final Function<Integer, String> function_s = (x) -> {
            snoreZzz();
            return s;
        };

        final Function<Integer, Double> function_d = (x) -> {
            snoreZzz();
            return d;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            snoreZzz();
            return anotherN;
        };
        final Function<Integer, Integer> function_nn = (x) -> {
            snoreZzz();
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), thenTupleMApplyMostSuccessAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), thenTupleMApplyMostSuccessAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s).get());

        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyMostSuccessAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyMostSuccessAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyMostSuccessAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyMostSuccessAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyMostSuccessAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyMostSuccessAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
    }

    @Test
    void test_thenTupleMApplyAllSuccessAsync() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            snoreZzz();
            return n;
        };

        final Function<Integer, String> function_s = (x) -> {
            snoreZzz();
            return s;
        };

        final Function<Integer, Double> function_d = (x) -> {
            snoreZzz();
            return d;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            snoreZzz();
            return anotherN;
        };
        final Function<Integer, Integer> function_nn = (x) -> {
            snoreZzz();
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), thenTupleMApplyAllSuccessAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), thenTupleMApplyAllSuccessAsync(completed, testExecutor, function_n, function_s).get());

        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyAllSuccessAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyAllSuccessAsync(completed, testExecutor, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyAllSuccessAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyAllSuccessAsync(completed, testExecutor, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyAllSuccessAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyAllSuccessAsync(completed, testExecutor, function_n, function_s, function_d, function_an, function_nn).get());
    }

    @Test
    void test_thenTupleMApplyAsync() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            snoreZzz();
            return n;
        };

        final Function<Integer, String> function_s = (x) -> {
            snoreZzz();
            return s;
        };

        final Function<Integer, Double> function_d = (x) -> {
            snoreZzz();
            return d;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            snoreZzz();
            return anotherN;
        };
        final Function<Integer, Integer> function_nn = (x) -> {
            snoreZzz();
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), thenTupleMApplyAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), thenTupleMApplyFastFailAsync(completed, function_n, function_s).get());

        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyFastFailAsync(completed, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyFastFailAsync(completed, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyFastFailAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
    }

    @Test
    void test_mostSuccessTupleOf() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final CompletionStage<String> anotherCompleted = completedStage(s);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cancelled = cancelledFuture();
        final CompletableFuture<Integer> incomplete = incompleteCf();

        assertEquals(Tuple2.of(n, s), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());

        // with `executorWhenTimeout`

        assertEquals(Tuple2.of(n, s), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## thenBoth* Methods(binary input) with fast-fail support
    ////////////////////////////////////////////////////////////

    @Test
    void test_both() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        final Runnable runnable = () -> {
        };
        assertNull(runAfterBothFastFail(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFastFailAsync(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFastFailAsync(cf_n, cf_nn, runnable, testExecutor).get());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        assertNull(thenAcceptBothFastFail(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFastFailAsync(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFastFailAsync(cf_n, cf_nn, bc, testExecutor).get());

        assertEquals(3 * n, thenCombineFastFail(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFastFailAsync(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFastFailAsync(cf_n, cf_nn, Integer::sum, testExecutor).get());
    }

    @Test
    void both_fastFail() throws Exception {
        CompletableFuture<Integer> cf_n = completeLaterCf(n);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cf_ee = failedFuture(anotherRte);

        final Runnable runnable = () -> {
        };
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFail(cf_n, failed, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(cf_n, failed, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(cf_n, failed, runnable, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFail(failed, cf_n, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_n, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_n, runnable, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFail(failed, cf_ee, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_ee, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_ee, runnable, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFail(cf_n, failed, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(cf_n, failed, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(cf_n, failed, bc, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFail(failed, cf_n, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_n, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_n, bc, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFail(failed, cf_ee, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_ee, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_ee, bc, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFail(cf_n, failed, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(cf_n, failed, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(cf_n, failed, Integer::sum, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFail(failed, cf_n, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_n, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_n, Integer::sum, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFail(failed, cf_ee, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_ee, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_ee, Integer::sum, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## thenEither* Methods(binary input) with either(any)-success support
    ////////////////////////////////////////////////////////////

    @Test
    void test_either() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        CompletableFuture<Integer> incomplete = incompleteCf();

        final Runnable runnable = () -> {
        };
        assertNull(runAfterEitherSuccess(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable, testExecutor).get());

        Consumer<Integer> c = i -> {
        };
        assertNull(acceptEitherSuccess(cf_n, incomplete, c).get());
        assertNull(acceptEitherSuccessAsync(cf_n, incomplete, c).get());
        assertNull(acceptEitherSuccessAsync(cf_n, incomplete, c, testExecutor).get());

        assertEquals(n, applyToEitherSuccess(cf_n, incomplete, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf_n, incomplete, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf_n, incomplete, identity(), testExecutor).get());
    }

    @Test
    void test_either_success() throws Exception {
        CompletableFuture<Integer> cf = completedFuture(n);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cf_ee = failedFuture(anotherRte);

        final Runnable runnable = () -> {
        };
        assertNull(runAfterEitherSuccess(cf, failed, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf, failed, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf, failed, runnable, testExecutor).get());
        assertNull(runAfterEitherSuccess(failed, cf, runnable).get());
        assertNull(runAfterEitherSuccessAsync(failed, cf, runnable).get());
        assertNull(runAfterEitherSuccessAsync(failed, cf, runnable, testExecutor).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterEitherSuccess(failed, cf_ee, runnable).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterEitherSuccessAsync(failed, cf_ee, runnable).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterEitherSuccessAsync(failed, cf_ee, runnable, testExecutor).get()
        ).getCause());

        Consumer<Integer> c = i -> {
        };
        assertNull(acceptEitherSuccess(cf, failed, c).get());
        assertNull(acceptEitherSuccessAsync(cf, failed, c).get());
        assertNull(acceptEitherSuccessAsync(cf, failed, c, testExecutor).get());
        assertNull(acceptEitherSuccess(failed, cf, c).get());
        assertNull(acceptEitherSuccessAsync(failed, cf, c).get());
        assertNull(acceptEitherSuccessAsync(failed, cf, c, testExecutor).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                acceptEitherSuccess(failed, cf_ee, c).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                acceptEitherSuccessAsync(failed, cf_ee, c).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                acceptEitherSuccessAsync(failed, cf_ee, c, testExecutor).get()
        ).getCause());

        assertEquals(n, applyToEitherSuccess(cf, failed, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf, failed, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf, failed, identity(), testExecutor).get());
        assertEquals(n, applyToEitherSuccess(failed, cf, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(failed, cf, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(failed, cf, identity(), testExecutor).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                applyToEitherSuccess(failed, cf_ee, identity()).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                applyToEitherSuccessAsync(failed, cf_ee, identity()).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                applyToEitherSuccessAsync(failed, cf_ee, identity(), testExecutor).get()
        ).getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Error Handling Methods of CompletionStage
    ////////////////////////////////////////////////////////////

    @Test
    void test_exceptionallyAsync() throws Exception {
        CompletableFuture<Integer> cf = failedFuture(rte);
        assertEquals(n, exceptionallyAsync(cf, ex -> n).get());

        cf = completedFuture(n);
        assertEquals(n, exceptionallyAsync(cf, ex -> anotherN).get());

    }

    @Test
    void test_catching() throws Exception {
        CompletableFuture<Integer> failed = failedFuture(rte);

        assertEquals(n, catching(failed, RuntimeException.class, ex -> n).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catching(failed, IndexOutOfBoundsException.class, ex -> n).get()
        ).getCause());

        assertEquals(n, catchingAsync(failed, RuntimeException.class, ex -> n).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catchingAsync(failed, IndexOutOfBoundsException.class, ex -> n).get()
        ).getCause());

        CompletableFuture<Integer> success = completedFuture(n);

        assertEquals(n, catching(success, RuntimeException.class, ex -> anotherN).get());
        assertEquals(n, catching(success, IndexOutOfBoundsException.class, ex -> anotherN).get());

        assertEquals(n, catchingAsync(success, RuntimeException.class, ex -> anotherN).get());
        assertEquals(n, catchingAsync(success, IndexOutOfBoundsException.class, ex -> anotherN).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Timeout Control Methods of CompletableFuture
    ////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF methods
    //  compatibility for low Java version
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_timeout() throws Exception {
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                orTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuOrTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuOrTimeout(incompleteCf(), testExecutor, SHORT_WAIT_MS, MILLISECONDS).get()
        ).getCause());

        assertEquals(n, orTimeout(completedFuture(n), SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), testExecutor, SHORT_WAIT_MS, MILLISECONDS).get());

        assertEquals(n, completeOnTimeout(incompleteCf(), n, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(incompleteCf(), n, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(incompleteCf(), n, testExecutor, SHORT_WAIT_MS, MILLISECONDS).get());

        assertEquals(n, completeOnTimeout(completedFuture(n), anotherN, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, testExecutor, SHORT_WAIT_MS, MILLISECONDS).get());
    }

    @Test
    void test_safeBehavior_orTimeout() {
        final Thread testThread = currentThread();
        final List<Integer> results = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        assertEquals(results, results.stream().map(i ->
                orTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS).handle((v, ex) -> {
                    assertInstanceOf(TimeoutException.class, ex);
                    assertTrue(Delayer.atCfDelayerThread());
                    return i;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));

        assertEquals(results, results.stream().map(i ->
                cffuOrTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS).handle((v, ex) -> {
                    assertInstanceOf(TimeoutException.class, ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertNotSame(testThread, currentThread());
                    return i;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
        assertEquals(results, results.stream().map(i ->
                cffuOrTimeout(incompleteCf(), testExecutor, SHORT_WAIT_MS, MILLISECONDS).handle((v, ex) -> {
                    assertInstanceOf(TimeoutException.class, ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertRunningInExecutor(testExecutor);
                    return i;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    @Test
    void test_safeBehavior_completeOnTimeout() {
        final Thread testThread = currentThread();
        final List<Integer> results = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        assertEquals(results, results.stream().map(i ->
                completeOnTimeout(incompleteCf(), i, SHORT_WAIT_MS, MILLISECONDS).handle((v, ex) -> {
                    assertNull(ex);
                    assertTrue(Delayer.atCfDelayerThread());
                    return v;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));

        assertEquals(results, results.stream().map(i ->
                cffuCompleteOnTimeout(incompleteCf(), i, SHORT_WAIT_MS, MILLISECONDS).handle((v, ex) -> {
                    assertNull(ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertNotSame(testThread, currentThread());
                    return v;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
        assertEquals(results, results.stream().map(i ->
                cffuCompleteOnTimeout(incompleteCf(), i, testExecutor, SHORT_WAIT_MS, MILLISECONDS).handle((v, ex) -> {
                    assertNull(ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertRunningInExecutor(testExecutor);
                    return v;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Advanced Methods of CompletionStage(compose* and handle-like methods)
    ////////////////////////////////////////////////////////////

    @Test
    void test_exceptionallyCompose() throws Exception {
        CompletableFuture<Object> completed = completedFuture(n);
        CompletableFuture<Object> failed = failedFuture(rte);

        assertEquals(n, exceptionallyCompose(failed, ex -> completedFuture(n)).get());
        assertEquals(n, exceptionallyCompose(completed, ex -> completedFuture(anotherN)).get());

        assertEquals(n, exceptionallyComposeAsync(failed, ex -> completedFuture(n)).get());
        assertEquals(n, exceptionallyComposeAsync(completed, ex -> completedFuture(anotherN)).get());
    }

    @Test
    void test_catchingCompose() throws Exception {
        CompletableFuture<Integer> failed = failedFuture(rte);

        assertEquals(n, catchingCompose(failed, RuntimeException.class, ex -> completedFuture(n)).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catchingCompose(failed, IndexOutOfBoundsException.class, ex -> completedFuture(n)).get()
        ).getCause());

        assertEquals(n, catchingComposeAsync(failed, RuntimeException.class, ex -> completedFuture(n)).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catchingComposeAsync(failed, IndexOutOfBoundsException.class, ex -> completedFuture(n)).get()
        ).getCause());

        CompletableFuture<Integer> success = completedFuture(n);

        assertEquals(n, catchingCompose(success, RuntimeException.class, ex -> completedFuture(anotherN)).get());
        assertEquals(n, catchingCompose(success, IndexOutOfBoundsException.class, ex -> completedFuture(anotherN)).get());

        assertEquals(n, catchingComposeAsync(success, RuntimeException.class, ex -> completedFuture(anotherN)).get());
        assertEquals(n, catchingComposeAsync(success, IndexOutOfBoundsException.class, ex -> completedFuture(anotherN)).get());
    }

    @Test
    void test_peek() throws Exception {
        BiConsumer<Object, Throwable> c = (v, ex) -> {
        };
        BiConsumer<Object, Throwable> ec = (v, ex) -> {
            throw anotherRte;
        };

        CompletableFuture<Object> failed = failedFuture(rte);
        assertSame(peek(failed, c), failed);
        assertSame(peekAsync(failed, c), failed);
        assertSame(peekAsync(failed, c, testExecutor), failed);

        assertSame(peek(failed, ec), failed);
        assertSame(peekAsync(failed, ec), failed);
        assertSame(peekAsync(failed, ec, testExecutor), failed);

        CompletableFuture<Integer> success = completedFuture(n);
        assertEquals(n, peek(success, c).get());
        assertEquals(n, peekAsync(success, c).get());
        assertEquals(n, peekAsync(success, c).get());

        assertEquals(n, peek(success, ec).get());
        assertEquals(n, peekAsync(success, ec).get());
        assertEquals(n, peekAsync(success, ec).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Read(explicitly)/Write Methods of CompletableFuture(including Future)
    ////////////////////////////////////////////////////////////

    @Test
    @SuppressWarnings("ThrowableNotThrown")
    void test_read() {
        ////////////////////////////////////////////////////////////////////////////////
        // completed tasks
        ////////////////////////////////////////////////////////////////////////////////

        final CompletableFuture<Integer> completed = completedFuture(n);
        final FutureTask<Integer> completedTask = new FutureTask<>(() -> n);
        completedTask.run();

        final CffuFactory cffuFactory = CffuFactory.builder(Executors.newCachedThreadPool()).build();
        final Cffu<Integer> completedCffu = cffuFactory.completedFuture(n);

        assertEquals(n, join(completed, SHORT_WAIT_MS, MILLISECONDS));
        assertEquals(n, getSuccessNow(completed, anotherN));
        assertEquals(n, getSuccessNow(completed, null));
        assertEquals(n, resultNow(completed));
        assertEquals(n, resultNow(completedTask));
        assertEquals(n, resultNow(completedCffu));
        final String m1 = assertThrowsExactly(IllegalStateException.class, () ->
                exceptionNow(completed)
        ).getMessage();
        if (m1 != null) assertEquals("Task completed with a result", m1);
        assertEquals("Task completed with a result", assertThrowsExactly(IllegalStateException.class, () ->
                exceptionNow(completedTask)
        ).getMessage());
        final String m12 = assertThrowsExactly(IllegalStateException.class, () ->
                exceptionNow(completedCffu)
        ).getMessage();
        if (m12 != null) assertEquals("Task completed with a result", m12);
        assertSame(CffuState.SUCCESS, state(completed));
        assertSame(CffuState.SUCCESS, state(completedTask));
        assertSame(CffuState.SUCCESS, state(completedCffu));

        ////////////////////////////////////////////////////////////////////////////////
        // failed tasks
        ////////////////////////////////////////////////////////////////////////////////

        final CompletableFuture<Object> failed = failedFuture(rte);
        final FutureTask<Integer> failedTask = new FutureTask<>(() -> {
            throw rte;
        });
        failedTask.run();
        final Cffu<Object> failedCffu = cffuFactory.failedFuture(rte);

        assertSame(rte, assertThrowsExactly(CompletionException.class, failed::join).getCause());
        // same as CompletableFuture.join method
        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                join(failed, SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertEquals(anotherN, getSuccessNow(failed, anotherN));
        assertNull(getSuccessNow(failed, null));
        final String m2 = assertThrowsExactly(IllegalStateException.class, () ->
                resultNow(failed)
        ).getMessage();
        if (m2 != null) assertEquals("Task completed with exception", m2);
        assertNull(getSuccessNow(failed, null));
        final String m22 = assertThrowsExactly(IllegalStateException.class, () ->
                resultNow(failedTask)
        ).getMessage();
        final String m23 = assertThrowsExactly(IllegalStateException.class, () ->
                resultNow(failedCffu)
        ).getMessage();
        if (m23 != null) assertEquals("Task completed with exception", m22);
        assertSame(rte, exceptionNow(failed));
        assertSame(rte, exceptionNow(failedTask));
        assertSame(rte, exceptionNow(failedCffu));
        assertSame(CffuState.FAILED, state(failed));
        assertSame(CffuState.FAILED, state(failedTask));
        assertSame(CffuState.FAILED, state(failedCffu));

        ////////////////////////////////////////////////////////////////////////////////
        // cancelled tasks
        ////////////////////////////////////////////////////////////////////////////////

        CompletableFuture<Object> cancelled = cancelledFuture();

        assertThrowsExactly(CancellationException.class, cancelled::join);
        // same as CompletableFuture.join method
        assertThrowsExactly(CancellationException.class, () ->
                join(cancelled, SHORT_WAIT_MS, MILLISECONDS)
        );
        final String m3 = assertThrowsExactly(IllegalStateException.class, () ->
                resultNow(cancelled)
        ).getMessage();
        if (m3 != null) assertEquals("Task was cancelled", m3);
        final String m4 = assertThrowsExactly(IllegalStateException.class, () ->
                exceptionNow(cancelled)
        ).getMessage();
        if (m4 != null) assertEquals("Task was cancelled", m4);
        assertSame(CffuState.CANCELLED, state(cancelled));

        ////////////////////////////////////////////////////////////////////////////////
        // incomplete tasks
        ////////////////////////////////////////////////////////////////////////////////

        final CompletableFuture<Object> incomplete = incompleteCf();

        assertInstanceOf(TimeoutException.class, assertThrowsExactly(CompletionException.class, () ->
                join(incomplete, SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertEquals(anotherN, getSuccessNow(incomplete, anotherN));
        assertNull(getSuccessNow(incomplete, null));
        final String m5 = assertThrowsExactly(IllegalStateException.class, () ->
                resultNow(incomplete)
        ).getMessage();
        if (m5 != null) assertEquals("Task has not completed", m5);
        final String m6 = assertThrowsExactly(IllegalStateException.class, () ->
                exceptionNow(incomplete)
        ).getMessage();
        if (m6 != null) assertEquals("Task has not completed", m6);
        assertSame(CffuState.RUNNING, state(incomplete));

        // Incomplete Future -> join before timeout
        CompletableFuture<Integer> later = completeLaterCf(n);
        assertEquals(n, join(later, LONG_WAIT_MS, MILLISECONDS));
    }

    @Test
    void test_write() throws Exception {
        assertEquals(n, completeAsync(incompleteCf(), () -> n).get());
        assertEquals(n, completeAsync(incompleteCf(), () -> n, commonPool()).get());
        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            assertThrowsExactly(UnsupportedOperationException.class, () ->
                    completeAsync(f, () -> null)
            );
        } else {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            completeAsync(f, () -> null);
        }

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                completeAsync(incompleteCf(), () -> {
                    throw rte;
                }).get()
        ).getCause());

        CompletableFuture<Integer> completed = completedFuture(n);
        assertEquals(n, completeAsync(completed, () -> anotherN).get());

        ////////////////////////////////////////

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                completeExceptionallyAsync(incompleteCf(), () -> rte).get()
        ).getCause());
        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            assertThrowsExactly(UnsupportedOperationException.class, () ->
                    completeExceptionallyAsync(f, () -> rte)
            );
        } else {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            completeExceptionallyAsync(f, () -> rte);
        }

        assertEquals(n, completeExceptionallyAsync(completedFuture(n), () -> rte).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Re-Config Methods of CompletableFuture
    ////////////////////////////////////////////////////////////

    @Test
    void test_re_config() throws Exception {
        CompletionStage<Integer> mf = minimalCompletionStage(completedFuture(n));
        assertEquals(n, mf.toCompletableFuture().get());

        CompletableFuture<Integer> cf = incompleteCf();
        copy(cf).complete(n);
        assertFalse(cf.isDone());

        CompletableFuture<Object> incomplete = newIncompleteFuture(cf);
        assertFalse(incomplete.isDone());
        incomplete.complete(n);
        assertFalse(cf.isDone());
    }

    @Test
    void test_executor() {
        Executor executor = defaultExecutor();
        assertIsCfDefaultExecutor(executor);

        assertIsCfDefaultExecutor(screenExecutor(commonPool()));

        ExecutorService e = Executors.newCachedThreadPool();
        assertSame(e, screenExecutor(e));
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Util Methods(static methods)
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * {@link CompletableFutureUtils#toCompletableFutureArray}
     * is tested in {@link CffuFactoryTest#test_toCompletableFutureArray()}
     */
    @Test
    void test_completableFutureListToArray() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] input = new CompletableFuture[]{completedFuture(n), completedFuture(anotherN)};

        assertArrayEquals(input, CompletableFutureUtils.completableFutureListToArray(Arrays.asList(input)));
    }

    @Test
    void test_unwrapCfException() {
        CompletableFuture<Object> failed = failedFuture(rte);

        ExecutionException ee = assertThrowsExactly(ExecutionException.class, () -> failed.get(SHORT_WAIT_MS, MILLISECONDS));
        assertSame(rte, unwrapCfException(ee));

        CompletionException ce = assertThrowsExactly(CompletionException.class, failed::join);
        assertSame(rte, unwrapCfException(ce));

        CompletionException nakedCe = new CompletionException() {
        };
        assertSame(nakedCe, unwrapCfException(nakedCe));

        assertSame(rte, unwrapCfException(rte));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper methods/fields
    ////////////////////////////////////////////////////////////////////////////////

    private static void assertIsCfDefaultExecutor(Executor executor) {
        final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;
        if (USE_COMMON_POOL) {
            assertSame(commonPool(), executor);
        } else {
            String executorClassName = executor.getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private static final String testName = "CompletableFutureUtilsTest";
}
