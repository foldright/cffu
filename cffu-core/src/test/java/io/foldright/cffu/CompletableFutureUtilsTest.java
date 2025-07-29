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

import static io.foldright.cffu.CffuTestHelper.assertIsCfDefaultExecutor;
import static io.foldright.cffu.CompletableFutureUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.*;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("RedundantThrows")
class CompletableFutureUtilsTest {
    // region# CF Factory Methods(including static methods of CF)

    // region## Multi-Actions(M*) Methods(create by actions)

    @Test
    void test_mSupply() throws Exception {
        final Supplier<Integer> supplier = () -> {
            snoreZzz();
            return n;
        };

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<List<Integer>>[] cfs = new CompletableFuture[]{
                mSupplyFailFastAsync(supplier, supplier),
                mSupplyFailFastAsync(testExecutor, supplier, supplier),
                mSupplyAllSuccessAsync(anotherN, supplier, supplier),
                mSupplyAllSuccessAsync(testExecutor, anotherN, supplier, supplier),
                mSupplyMostSuccessAsync(anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                mSupplyMostSuccessAsync(testExecutor, anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
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
    void test_mSupply__failure() throws Exception {
        RuntimeException rte1 = new RuntimeException("rte1");
        RuntimeException rte2 = new RuntimeException("rte2");

        assertSame(rte1, assertThrowsExactly(ExecutionException.class, () -> mSupplyFailFastAsync(
                testExecutor,
                () -> 42,
                supplyLater(rte1, SHORT_WAIT_MS),
                supplyLater(rte2, LONG_WAIT_MS)
        ).get()).getCause());
        assertSame(rte2, assertThrowsExactly(ExecutionException.class, () -> mSupplyFailFastAsync(
                testExecutor,
                () -> 42,
                supplyLater(rte1, LONG_WAIT_MS),
                supplyLater(rte2, SHORT_WAIT_MS)).get()).getCause());
        assertEquals(42, mSupplyAnySuccessAsync(
                testExecutor,
                () -> 42,
                supplyLater(rte1, SHORT_WAIT_MS),
                supplyLater(rte2, LONG_WAIT_MS)
        ).get());

        assertEquals(Tuple3.of(42, null, null), mSupplyMostSuccessTupleAsync(
                testExecutor, LONG_WAIT_MS, MILLISECONDS,
                () -> 42,
                supplyLater(rte1, SHORT_WAIT_MS),
                supplyLater(rte2, LONG_WAIT_MS)
        ).get());
    }

    @Test
    void test_mRun() throws Exception {
        final Runnable runnable = TestUtils::snoreZzz;

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                mRunFailFastAsync(runnable, runnable),
                mRunFailFastAsync(testExecutor, runnable, runnable),
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
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)

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
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), mSupplyMostSuccessTupleAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), mSupplyMostSuccessTupleAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), mSupplyMostSuccessTupleAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), mSupplyMostSuccessTupleAsync(testExecutor, LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    @Test
    void test_mSupplyTupleAsync() throws Exception {
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
        assertEquals(Tuple2.of(n, s), mSupplyTupleAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), mSupplyTupleFailFastAsync(supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), mSupplyTupleAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    @Test
    void test_mSupplyAllSuccessTupleAsync() throws Exception {
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
        assertEquals(Tuple2.of(n, s), mSupplyAllSuccessTupleAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), mSupplyAllSuccessTupleAsync(testExecutor, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), mSupplyAllSuccessTupleAsync(testExecutor, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), mSupplyAllSuccessTupleAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), mSupplyAllSuccessTupleAsync(testExecutor, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    // endregion
    // region## allOf* Methods(including mostSuccessResultsOf)

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

        assertEquals(Arrays.asList(n, n + 1, n + 2), allResultsFailFastOf(
                completedStage(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allResultsFailFastOf(
                completedFuture(n),
                completedStage(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allResultsFailFastOf(completedFuture(n)).get());
        assertEquals(Collections.singletonList(n), allResultsFailFastOf(completedStage(n)).get());

        assertEquals(Collections.emptyList(), allResultsFailFastOf().get());

        ////////////////////////////////////////////////////////////////////////////////

        Arrays.asList(
                allOf(completedFuture(n), completedStage(n + 1), completedFuture(n + 2)),
                allOf(completedStage(n), completedFuture(n + 1)),
                allOf(completedFuture(n)),
                allOf(completedStage(n)),
                allOf(),

                allFailFastOf(completedFuture(n), completedStage(n + 1), completedFuture(n + 2)),
                allFailFastOf(completedStage(n), completedFuture(n + 1)),
                allFailFastOf(completedFuture(n)),
                allFailFastOf(completedStage(n)),
                allFailFastOf()
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
        // allResultsFailFastOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allResultsFailFastOf: the ex of first given cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsFailFastOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(anotherRte, assertThrowsExactly(ExecutionException.class, () ->
                // allResultsFailFastOf: the ex of first given cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsFailFastOf(
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
                allResultsFailFastOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsFailFastOf(
                        completedFuture(n),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsFailFastOf(
                        incompleteCf(),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // allFailFastOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                // allFailFastOf: the ex of first complete(in time) cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allFailFastOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(anotherRte, assertThrowsExactly(ExecutionException.class, () ->
                // allFailFastOf: the ex of first complete(in time) cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allFailFastOf(
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
                allFailFastOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allFailFastOf(
                        completedFuture(n),
                        failedFuture(rte),
                        incompleteCf()
                ).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allFailFastOf(
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
        assertTrue(mostSuccessResultsOf(null, SHORT_WAIT_MS, MILLISECONDS).get().isEmpty());

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
    // region## anyOf* Methods

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
    // region## allTupleOf*/mostSuccessTupleOf Methods

    @Test
    void test_allTupleOf() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletionStage<String> cf_s = completedStage(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        assertEquals(Tuple2.of(n, s), allTupleOf(cf_n, cf_s).get());
        assertEquals(Tuple2.of(n, s), allTupleFailFastOf(cf_n, cf_s).get());

        assertEquals(Tuple3.of(n, s, d), allTupleOf(cf_n, cf_s, cf_d).get());
        assertEquals(Tuple3.of(n, s, d), allTupleFailFastOf(cf_n, cf_s, cf_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), allTupleOf(cf_n, cf_s, cf_d, cf_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), allTupleFailFastOf(cf_n, cf_s, cf_d, cf_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), allTupleOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), allTupleFailFastOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
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
                allTupleFailFastOf(incomplete, fail).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, fail, cf_s).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFailFastOf(incomplete, fail, cf_s).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, fail, cf_d, cf_s).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFailFastOf(incomplete, fail, cf_d, cf_s).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleOf(cf_n, cf_d, fail, cf_s, cf_an).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allTupleFailFastOf(incomplete, cf_d, fail, cf_s, cf_an).get()
        ).getCause());
    }

    @Test
    void test_allTupleOf_NotFailFast() throws Exception {
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
    // region## Immediate Value Argument Factory Methods(backport methods)

    @Test
    void test_failedFuture() throws Exception {
        assertTrue(failedFuture(rte).isDone());
        assertEquals(n, completedStage(n).toCompletableFuture().get());
        assertTrue(failedStage(rte).toCompletableFuture().isDone());
    }

    // endregion
    // region## Delay Execution(backport methods)

    @Test
    void test_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = delayedExecutor(1, MILLISECONDS);
        CompletableFuture.runAsync(() -> holder.set(testName), delayer).get();
        assertEquals(testName, holder.get());
    }

    // endregion
    // endregion
    // region# CF Instance Methods(including new enhanced + backport methods)

    // region## Then-Multi-Actions(thenM*) Methods

    @Test
    void test_thenMRun() throws Exception {
        final Runnable runnable = TestUtils::snoreZzz;
        final CompletableFuture<Object> completed = completedFuture(null);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                thenMRunAsync(completed, runnable, runnable),
                thenMRunAsync(completed, testExecutor, runnable, runnable),
                thenMRunFailFastAsync(completed, runnable, runnable),
                thenMRunFailFastAsync(completed, testExecutor, runnable, runnable),
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
                thenMAcceptFailFastAsync(completed, consumer, consumer),
                thenMAcceptFailFastAsync(completed, testExecutor, consumer, consumer),
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
        final Function<Integer, Integer> supplier = x -> {
            assertEquals(n, x);
            snoreZzz();
            return x + n;
        };
        final CompletableFuture<Integer> completed = completedFuture(n);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<List<Integer>>[] cfs = new CompletableFuture[]{
                thenMApplyFailFastAsync(completed, supplier, supplier),
                thenMApplyFailFastAsync(completed, testExecutor, supplier, supplier),
                thenMApplyAllSuccessAsync(completed, anotherN, supplier, supplier),
                thenMApplyAllSuccessAsync(completed, testExecutor, anotherN, supplier, supplier),
                thenMApplyMostSuccessAsync(completed, anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                thenMApplyMostSuccessAsync(completed, testExecutor, anotherN, LONG_WAIT_MS, MILLISECONDS, supplier, supplier),
                thenMApplyAsync(completed, supplier, supplier),
                thenMApplyAsync(completed, testExecutor, supplier, supplier),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (CompletableFuture<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n + n, n + n), cf.get());
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
            assertEquals(n + n, cf.get());
        }
    }

    // endregion
    // region## Then-Multi-Actions-Tuple(thenMTuple*) Methods

    @Test
    void test_thenTuple_methods() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final Function<Integer, Integer> function_n = x -> {
            snoreZzz();
            return n + x;
        };

        final Function<Integer, String> function_s = x -> {
            snoreZzz();
            return s + x;
        };

        final Function<Integer, Double> function_d = x -> {
            snoreZzz();
            return d + x;
        };
        final Function<Integer, Integer> function_an = x -> {
            snoreZzz();
            return anotherN + x;
        };
        final Function<Integer, Integer> function_nn = x -> {
            snoreZzz();
            return n + x;
        };

        //  thenMApplyTupleFailFastAsync

        assertEquals(Tuple2.of(n + n, s + n), thenMApplyTupleFailFastAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), thenMApplyTupleFailFastAsync(completed, testExecutor, function_n, function_s).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyTupleFailFastAsync(completed, testExecutor, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyTupleFailFastAsync(completed, testExecutor, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyTupleFailFastAsync(completed, testExecutor, function_n, function_s, function_d, function_an, function_nn).get());

        // thenMApplyAllSuccessTupleAsync

        assertEquals(Tuple2.of(n + n, s + n), thenMApplyAllSuccessTupleAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), thenMApplyAllSuccessTupleAsync(completed, testExecutor, function_n, function_s).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyAllSuccessTupleAsync(completed, testExecutor, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyAllSuccessTupleAsync(completed, testExecutor, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyAllSuccessTupleAsync(completed, testExecutor, function_n, function_s, function_d, function_an, function_nn).get());

        // thenMApplyMostSuccessTupleAsync

        assertEquals(Tuple2.of(n + n, s + n), thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), thenMApplyMostSuccessTupleAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyMostSuccessTupleAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyMostSuccessTupleAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyMostSuccessTupleAsync(completed, testExecutor, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());

        //  thenMApplyTupleAsync

        assertEquals(Tuple2.of(n + n, s + n), thenMApplyTupleAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), thenMApplyTupleAsync(completed, testExecutor, function_n, function_s).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyTupleAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), thenMApplyTupleAsync(completed, testExecutor, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyTupleAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), thenMApplyTupleAsync(completed, testExecutor, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyTupleAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), thenMApplyTupleAsync(completed, testExecutor, function_n, function_s, function_d, function_an, function_nn).get());
    }

    // endregion
    // region## thenBoth* Methods(binary input) with fail-fast support

    @Test
    void test_both() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        final Runnable runnable = () -> {};
        assertNull(runAfterBothFailFast(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFailFastAsync(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFailFastAsync(cf_n, cf_nn, runnable, testExecutor).get());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {};
        assertNull(thenAcceptBothFailFast(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFailFastAsync(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFailFastAsync(cf_n, cf_nn, bc, testExecutor).get());

        assertEquals(3 * n, thenCombineFailFast(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFailFastAsync(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFailFastAsync(cf_n, cf_nn, Integer::sum, testExecutor).get());
    }

    @Test
    void bothFailFast() throws Exception {
        CompletableFuture<Integer> cf_n = completeLaterCf(n);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cf_ee = failedFuture(anotherRte);

        final Runnable runnable = () -> {};
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFast(cf_n, failed, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFastAsync(cf_n, failed, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFastAsync(cf_n, failed, runnable, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFast(failed, cf_n, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFastAsync(failed, cf_n, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFastAsync(failed, cf_n, runnable, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFast(failed, cf_ee, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFastAsync(failed, cf_ee, runnable).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFailFastAsync(failed, cf_ee, runnable, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {};
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFast(cf_n, failed, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFastAsync(cf_n, failed, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFastAsync(cf_n, failed, bc, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFast(failed, cf_n, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFastAsync(failed, cf_n, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFastAsync(failed, cf_n, bc, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFast(failed, cf_ee, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFastAsync(failed, cf_ee, bc).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFailFastAsync(failed, cf_ee, bc, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFast(cf_n, failed, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFastAsync(cf_n, failed, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFastAsync(cf_n, failed, Integer::sum, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFast(failed, cf_n, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFastAsync(failed, cf_n, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFastAsync(failed, cf_n, Integer::sum, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFast(failed, cf_ee, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFastAsync(failed, cf_ee, Integer::sum).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFailFastAsync(failed, cf_ee, Integer::sum, testExecutor).get(SHORT_WAIT_MS, MILLISECONDS)
        ).getCause());
    }

    // endregion
    // region## thenEither* Methods(binary input) with either(any)-success support

    @Test
    void test_either() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        CompletableFuture<Integer> incomplete = incompleteCf();

        final Runnable runnable = () -> {};
        assertNull(runAfterEitherSuccess(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable, testExecutor).get());

        Consumer<Integer> c = i -> {};
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

        final Runnable runnable = () -> {};
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

        Consumer<Integer> c = i -> {};
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
    // region## Error Handling Methods of CompletionStage

    @Test
    void test_exceptionallyAsync() throws Exception {
        CompletableFuture<Integer> cf = failedFuture(rte);
        assertEquals(n, exceptionallyAsync(cf, ex -> n).get());

        cf = completedFuture(n);
        assertEquals(n, exceptionallyAsync(cf, ex -> anotherN).get());

    }

    @Test
    void test_catching() throws Exception {
        test_catching_failedCf(failedFuture(rte));
        test_catching_failedCf(CompletableFuture.supplyAsync(() -> {
            throw rte;
        }));
        test_catching_failedCf(CompletableFutureUtils.<Integer>failedFuture(rte).thenApply(x -> x));


        CompletableFuture<Integer> success = completedFuture(n);

        assertEquals(n, catching(success, RuntimeException.class, ex -> anotherN).get());
        assertEquals(n, catching(success, IndexOutOfBoundsException.class, ex -> anotherN).get());

        assertEquals(n, catchingAsync(success, RuntimeException.class, ex -> anotherN).get());
        assertEquals(n, catchingAsync(success, IndexOutOfBoundsException.class, ex -> anotherN).get());
    }

    private static void test_catching_failedCf(CompletableFuture<Integer> failed) throws Exception {
        assertEquals(n, catching(failed, RuntimeException.class, ex -> {
            assertSame(rte, ex);
            return n;
        }).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catching(failed, IndexOutOfBoundsException.class, ex -> {
                    assertSame(rte, ex);
                    return n;
                }).get()
        ).getCause());

        assertEquals(n, catchingAsync(failed, RuntimeException.class, ex -> {
            assertSame(rte, ex);
            return n;
        }).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catchingAsync(failed, IndexOutOfBoundsException.class, ex -> {
                    assertSame(rte, ex);
                    return n;
                }).get()
        ).getCause());
    }

    // endregion
    // region## Timeout Control Methods of CompletableFuture

    //# Backport CF methods
    //  compatibility for low Java version

    @Test
    void test_timeout() throws Exception {
        ////////////////////////////////////////////////////////////////////
        // orTimeout*
        ////////////////////////////////////////////////////////////////////

        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                orTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuOrTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuOrTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS, testExecutor).get()
        ).getCause());

        assertEquals(n, orTimeout(completedFuture(n), SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), SHORT_WAIT_MS, MILLISECONDS, testExecutor).get());

        {
            CompletableFuture<Integer> incomplete = incompleteCf();
            @SuppressWarnings("unchecked")
            CompletableFuture<Integer>[] cfs = new CompletableFuture[]{
                    orTimeout(incomplete, MEDIAN_WAIT_MS, MILLISECONDS),
                    cffuOrTimeout(incomplete, MEDIAN_WAIT_MS, MILLISECONDS),
                    cffuOrTimeout(incomplete, MEDIAN_WAIT_MS, MILLISECONDS, testExecutor)
            };
            // complete input cf after orTimeout operation and before timeout tigger
            incomplete.complete(n);

            for (CompletableFuture<Integer> cf : cfs) {
                assertEquals(n, cf.get());
            }
        }

        ////////////////////////////////////////////////////////////////////
        // completeOnTimeout*
        ////////////////////////////////////////////////////////////////////

        assertEquals(n, completeOnTimeout(incompleteCf(), n, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(incompleteCf(), n, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(incompleteCf(), n, SHORT_WAIT_MS, MILLISECONDS, testExecutor).get());

        assertEquals(n, completeOnTimeout(completedFuture(n), anotherN, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, SHORT_WAIT_MS, MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, SHORT_WAIT_MS, MILLISECONDS, testExecutor).get());

        {
            CompletableFuture<Integer> incomplete = incompleteCf();
            @SuppressWarnings("unchecked")
            CompletableFuture<Integer>[] cfs = new CompletableFuture[]{
                    completeOnTimeout(incomplete, anotherN, MEDIAN_WAIT_MS, MILLISECONDS),
                    cffuCompleteOnTimeout(incomplete, anotherN, MEDIAN_WAIT_MS, MILLISECONDS),
                    cffuCompleteOnTimeout(incomplete, anotherN, MEDIAN_WAIT_MS, MILLISECONDS, testExecutor)
            };
            // complete input cf after completeOnTimeout operation and before timeout tigger
            incomplete.complete(n);

            for (CompletableFuture<Integer> cf : cfs) {
                assertEquals(n, cf.get());
            }
        }
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
                cffuOrTimeout(incompleteCf(), SHORT_WAIT_MS, MILLISECONDS, testExecutor).handle((v, ex) -> {
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
                cffuCompleteOnTimeout(incompleteCf(), i, SHORT_WAIT_MS, MILLISECONDS, testExecutor).handle((v, ex) -> {
                    assertNull(ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertRunningInExecutor(testExecutor);
                    return v;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    // endregion
    // region## Advanced Methods of CompletionStage(compose* and handle-like methods)

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
        test_catchingCompose_failedCf(failedFuture(rte));
        test_catchingCompose_failedCf(CompletableFuture.supplyAsync(() -> {
            throw rte;
        }));
        test_catchingCompose_failedCf(CompletableFutureUtils.<Integer>failedFuture(rte).thenApply(x -> x));

        CompletableFuture<Integer> success = completedFuture(n);

        assertEquals(n, catchingCompose(success, RuntimeException.class, ex -> completedFuture(anotherN)).get());
        assertEquals(n, catchingCompose(success, IndexOutOfBoundsException.class, ex -> completedFuture(anotherN)).get());

        assertEquals(n, catchingComposeAsync(success, RuntimeException.class, ex -> completedFuture(anotherN)).get());
        assertEquals(n, catchingComposeAsync(success, IndexOutOfBoundsException.class, ex -> completedFuture(anotherN)).get());
    }

    private static void test_catchingCompose_failedCf(CompletableFuture<Integer> failed) throws Exception {
        assertEquals(n, catchingCompose(failed, RuntimeException.class, ex -> {
            assertSame(rte, ex);
            return completedFuture(n);
        }).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catchingCompose(failed, IndexOutOfBoundsException.class, ex -> {
                    assertSame(rte, ex);
                    return completedFuture(n);
                }).get()
        ).getCause());

        assertEquals(n, catchingComposeAsync(failed, RuntimeException.class, ex -> {
            assertSame(rte, ex);
            return completedFuture(n);
        }).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                catchingComposeAsync(failed, IndexOutOfBoundsException.class, ex -> {
                    assertSame(rte, ex);
                    return completedFuture(n);
                }).get()
        ).getCause());
    }

    @Test
    void test_peek() throws Exception {
        BiConsumer<Object, Throwable> c = (v, ex) -> {};
        BiConsumer<Object, Throwable> ec = (v, ex) -> {throw anotherRte;};

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
    // region## Read(explicitly)/Write Methods of CompletableFuture(including Future)

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
        ////////////////////////////////////////
        // completeAsync
        ////////////////////////////////////////

        // tests in ForkJoinPool(commonPool)
        {
            final CompletableFuture<Integer> f = incompleteCf();
            final CompletableFuture<Integer> o = completeAsync(f, () -> n);
            assertEquals(n, o.get());
            assertSame(f, o);
        }
        // tests in ThreadPoolExecutor
        {
            final CompletableFuture<Integer> f = incompleteCf();
            final CompletableFuture<Integer> o = completeAsync(f, () -> n);
            assertEquals(n, o.get());
            assertSame(f, o);
        }

        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            assertThrowsExactly(UnsupportedOperationException.class, () ->
                    completeAsync(f, () -> anotherN)
            );
        } else {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            final CompletableFuture<Integer> o = completeAsync(f, () -> anotherN);
            assertEquals(n, o.get());
            assertSame(f, o);
        }

        {
            final CompletableFuture<Integer> f = incompleteCf();
            final CompletableFuture<Integer> o = completeAsync(f, () -> {
                throw rte;
            });
            assertSame(rte, assertThrowsExactly(ExecutionException.class, o::get).getCause());
            assertSame(f, o);
        }

        {
            final CompletableFuture<Integer> f = completedFuture(n);
            final CompletableFuture<Integer> o = completeAsync(f, () -> anotherN);
            assertEquals(n, o.get());
            assertSame(f, o);
        }

        ////////////////////////////////////////
        // completeExceptionallyAsync
        ////////////////////////////////////////

        // tests in ForkJoinPool(commonPool)
        {
            final CompletableFuture<Object> f = incompleteCf();
            final CompletableFuture<Object> o = completeExceptionallyAsync(f, () -> rte);
            assertSame(rte, assertThrowsExactly(ExecutionException.class, o::get).getCause());
            assertSame(f, o);
        }
        // tests in ThreadPoolExecutor
        {
            final CompletableFuture<Object> f = incompleteCf();
            final CompletableFuture<Object> o = completeExceptionallyAsync(f, () -> rte, testExecutor);
            assertSame(rte, assertThrowsExactly(ExecutionException.class, o::get).getCause());
            assertSame(f, o);
        }

        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            assertThrowsExactly(UnsupportedOperationException.class, () ->
                    completeExceptionallyAsync(f, () -> rte)
            );
        } else {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(n);
            final CompletableFuture<Integer> o = completeExceptionallyAsync(f, () -> rte);
            assertEquals(n, o.get());
            assertSame(f, o);
        }

        {
            final CompletableFuture<Object> f = incompleteCf();
            final CompletableFuture<Object> o = completeExceptionallyAsync(f, () -> {
                throw anotherRte;
            });
            assertSame(anotherRte, assertThrowsExactly(ExecutionException.class, o::get).getCause());
            assertSame(f, o);
        }

        {
            final CompletableFuture<Integer> f = completedFuture(n);
            final CompletableFuture<Integer> o = completeExceptionallyAsync(f, () -> rte);
            assertEquals(n, o.get());
            assertSame(f, o);
        }
    }

    // endregion
    // region## Re-Config Methods of CompletableFuture

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
    void test_defaultExecutor() {
        assertIsCfDefaultExecutor(defaultExecutor(completedFuture(null)));
        assertIsCfDefaultExecutor(LLCF.screenExecutor(commonPool()));

        ExecutorService e = Executors.newCachedThreadPool();
        assertSame(e, LLCF.screenExecutor(e));

        if (isJava9Plus())
            assertSame(testFjExecutor, defaultExecutor(new CustomizedExecutorCf<>()));
        else
            assertIsCfDefaultExecutor(defaultExecutor(new CustomizedExecutorCf<>()));

        // Cffu
        assertSame(testExecutor, defaultExecutor(testCffuFac.completedFuture(null)));

        final UnsupportedOperationException ex = assertThrowsExactly(UnsupportedOperationException.class,
                () -> defaultExecutor(new FooCs<>(new CompletableFuture<>())));
        assertTrue(ex.getMessage().startsWith("Unsupported CompletionStage subclass: "));
    }

    private static class CustomizedExecutorCf<T> extends CompletableFuture<T> {
        public Executor defaultExecutor() {
            return testFjExecutor;
        }
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
        RuntimeException rt1 = new RuntimeException();
        ExecutionException ee1 = new ExecutionException(rt1);
        assertSame(rt1, unwrapCfException(ee1));

        CompletionException ce1 = new CompletionException(rt1);
        assertSame(rt1, unwrapCfException(ce1));

        CompletionException nakedCe = new CompletionException() {
            @java.io.Serial
            private static final long serialVersionUID = 0;
        };
        assertSame(nakedCe, unwrapCfException(nakedCe));

        assertSame(rte, unwrapCfException(rte));

        Throwable ex = new RuntimeException(new RuntimeException(new IllegalArgumentException()));
        assertSame(ex, unwrapCfException(ex));
    }

    @Test
    void test_unwrapCfException_loop() {
        {
            CompletionException completionException = new CompletionException() {
                @java.io.Serial
                private static final long serialVersionUID = 0;
            };
            ExecutionException ee1 = new ExecutionException(completionException);

            completionException.initCause(ee1);

            assertLoopEx(completionException);
        }
        {
            CompletionException completionException = new CompletionException() {
                @java.io.Serial
                private static final long serialVersionUID = 0;
            };
            ExecutionException ee1 = new ExecutionException(completionException);
            CompletionException ce2 = new CompletionException(ee1);
            ExecutionException ee3 = new ExecutionException(ce2);

            completionException.initCause(ee3);

            assertLoopEx(completionException);
        }
    }

    private void assertLoopEx(Throwable ex) {
        assertSame(ex, unwrapCfException(ex));

        final ExecutionException ee = new ExecutionException(ex);
        assertSame(ee, unwrapCfException(ee));

        final CompletionException ce = new CompletionException(ex);
        assertSame(ce, unwrapCfException(ce));
    }

    // endregion
    // region# Test helper fields

    private static final String testName = "CompletableFutureUtilsTest";
}
