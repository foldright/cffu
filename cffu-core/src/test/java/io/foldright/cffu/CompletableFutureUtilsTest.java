package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
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
import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("RedundantThrows")
class CompletableFutureUtilsTest {
    ////////////////////////////////////////////////////////////////////////////////
    //# multi-actions(M*) methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_mRun() throws Exception {
        final Runnable runnable = () -> sleep(100);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                mRunAsync(runnable, runnable),
                mRunAsync(executorService, runnable, runnable),
                mRunAnySuccessAsync(runnable, runnable),
                mRunAnySuccessAsync(executorService, runnable, runnable),
                mRunAnyAsync(runnable, runnable),
                mRunAnyAsync(executorService, runnable, runnable),
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (CompletableFuture<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_mSupply() throws Exception {
        final Supplier<Integer> supplier = () -> {
            sleep(100);
            return n;
        };

        final long tick = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        CompletableFuture<List<Integer>>[] cfs = new CompletableFuture[]{
                mSupplyAsync(supplier, supplier),
                mSupplyAsync(executorService, supplier, supplier),
                mSupplyFastFailAsync(supplier, supplier),
                mSupplyFastFailAsync(executorService, supplier, supplier),
                mSupplyMostSuccessAsync(anotherN, 500, TimeUnit.MILLISECONDS, supplier, supplier),
                mSupplyMostSuccessAsync(anotherN, executorService, 500, TimeUnit.MILLISECONDS, supplier, supplier),
                mSupplyAnySuccessAsync(supplier, supplier),
                mSupplyAnySuccessAsync(executorService, supplier, supplier),
                mSupplyAnyAsync(supplier, supplier),
                mSupplyAnyAsync(executorService, supplier, supplier),
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (CompletableFuture<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf* methods
    ////////////////////////////////////////////////////////////////////////////////

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
                            sleep(100);
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
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        );

        // incomplete fail incomplete
        assertThrowsExactly(TimeoutException.class, () ->
                allResultsOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
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
                            sleep(100);
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
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allResultsFastFailOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
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
                            sleep(100);
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
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                allFastFailOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
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
                            sleep(100);
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
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        );

        // incomplete fail incomplete
        assertThrowsExactly(TimeoutException.class, () ->
                allOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void test_mostOf() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final CompletionStage<Integer> completedStage = completedStage(n);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cancelled = createCancelledFuture();
        final CompletableFuture<Integer> incomplete = createIncompleteFuture();

        // 0 input cf
        assertEquals(0, mostSuccessResultsOf(null, 10, TimeUnit.MILLISECONDS).get().size());

        // 1 input cf
        assertEquals(Collections.singletonList(n), mostSuccessResultsOf(
                null, 10, TimeUnit.MILLISECONDS, completed).get());
        assertEquals(Collections.singletonList(n), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, completedStage).get());

        assertEquals(Collections.singletonList(anotherN), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, failed).get());
        assertEquals(Collections.singletonList(anotherN), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, cancelled).get());
        assertEquals(Collections.singletonList(anotherN), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, incomplete).get());

        // more input cf
        assertEquals(Arrays.asList(n, null, null, null), mostSuccessResultsOf(
                null, 10, TimeUnit.MILLISECONDS, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(Arrays.asList(n, anotherN, anotherN, anotherN), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(Arrays.asList(anotherN, anotherN, anotherN), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(Arrays.asList(anotherN, anotherN), mostSuccessResultsOf(
                anotherN, 10, TimeUnit.DAYS, failed, cancelled
        ).get());
    }

    @Test
    void test_mostOf_wontModifyInputCf() throws Exception {
        final CompletableFuture<Integer> incomplete = createIncompleteFuture();
        final CompletableFuture<Integer> incomplete2 = createIncompleteFuture();

        assertEquals(Collections.singletonList(null), mostSuccessResultsOf(
                null, 10, TimeUnit.MILLISECONDS, incomplete
        ).get());
        assertEquals(Arrays.asList(null, null), mostSuccessResultsOf(
                null, 10, TimeUnit.MILLISECONDS, incomplete, incomplete2
        ).get());

        assertEquals(CffuState.RUNNING, state(incomplete));
        assertEquals(CffuState.RUNNING, state(incomplete2));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_anySuccessOf__trivial_case() throws Exception {
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
                createIncompleteFuture(),
                createIncompleteFuture(),
                completedFuture(n)
        ).get());
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
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
                createIncompleteFuture(),
                createIncompleteFuture(),
                completedFuture(n)
        ).get());
        assertEquals(n, anySuccessOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
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
                            sleep(100);
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
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
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
                            sleep(100);
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
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        ).getCause();
    }

    @Test
    void test_anyOf__concurrent() throws Exception {
        // incomplete/wait-success then success
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOf(
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

        // success then failed
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // failed then success
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                anyOf(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            return n;
                        }),
                        failedFuture(rte),
                        failedFuture(rte)
                ).get()
        ).getCause());

        ////////////////////////////////////////

        // incomplete/wait-success then success
        assertEquals(n, anySuccessOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anySuccessOf(
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

        // success then failed
        assertEquals(n, anySuccessOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // failed then success
        assertEquals(n, anySuccessOf(
                CompletableFuture.supplyAsync(() -> {
                    sleep(100);
                    return n;
                }),
                failedFuture(rte),
                failedFuture(rte)
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allTupleOf methods
    ////////////////////////////////////////////////////////////////////////////////

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
        final CompletableFuture<Object> incomplete = new CompletableFuture<>();
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
        final CompletableFuture<Object> incomplete = new CompletableFuture<>();
        final CompletableFuture<Object> fail = failedFuture(rte);

        final CompletableFuture<String> cf_s = completedFuture(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);

        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, fail).get(100, TimeUnit.MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, fail, cf_s).get(100, TimeUnit.MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, fail, cf_d, cf_s).get(100, TimeUnit.MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                allTupleOf(incomplete, cf_d, fail, cf_s, cf_an).get(100, TimeUnit.MILLISECONDS)
        );
    }


    @Test
    void test_tupleMSupplyAsync() throws Exception {
        final Supplier<Integer> supplier_n = () -> {
            sleep(100);
            return n;
        };
        final Supplier<String> supplier_s = () -> {
            sleep(100);
            return s;
        };

        final Supplier<Double> supplier_d = () -> {
            sleep(100);
            return d;
        };
        final Supplier<Integer> supplier_an = () -> {
            sleep(100);
            return anotherN;
        };
        final Supplier<Integer> supplier_nn = () -> {
            sleep(100);
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
    void test_tupleMSupplyMostSuccessAsync() throws Exception {
        final Supplier<Integer> supplier_n = () -> {
            sleep(10);
            return n;
        };
        final Supplier<String> supplier_s = () -> {
            sleep(10);
            return s;
        };

        final Supplier<Double> supplier_d = () -> {
            sleep(10);
            return d;
        };
        final Supplier<Integer> supplier_an = () -> {
            sleep(10);
            return anotherN;
        };
        final Supplier<Integer> supplier_nn = () -> {
            sleep(10);
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), tupleMSupplyMostSuccessAsync(defaultExecutor(), 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), tupleMSupplyMostSuccessAsync(defaultExecutor(), 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), tupleMSupplyMostSuccessAsync(defaultExecutor(), 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), tupleMSupplyMostSuccessAsync(defaultExecutor(), 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }


    @Test
    void test_tupleMApplyMostSuccessAsync() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            sleep(100);
            return n;
        };

        final Function<Integer, String> function_s = (x) -> {
            sleep(100);
            return s;
        };

        final Function<Integer, Double> function_d = (x) -> {
            sleep(100);
            return d;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            sleep(100);
            return anotherN;
        };
        final Function<Integer, Integer> function_nn = (x) -> {
            sleep(100);
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), thenTupleMApplyMostSuccessAsync(completed, 500, TimeUnit.MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), thenTupleMApplyMostSuccessAsync(completed, defaultExecutor(), 500, TimeUnit.MILLISECONDS, function_n, function_s).get());

        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyMostSuccessAsync(completed, 500, TimeUnit.MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), thenTupleMApplyMostSuccessAsync(completed, defaultExecutor(), 500, TimeUnit.MILLISECONDS, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyMostSuccessAsync(completed, 500, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), thenTupleMApplyMostSuccessAsync(completed, defaultExecutor(), 500, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyMostSuccessAsync(completed, 500, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), thenTupleMApplyMostSuccessAsync(completed, defaultExecutor(), 500, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
    }


    @Test
    void test_thenTupleMApplyAsync() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            sleep(100);
            return n;
        };

        final Function<Integer, String> function_s = (x) -> {
            sleep(100);
            return s;
        };

        final Function<Integer, Double> function_d = (x) -> {
            sleep(100);
            return d;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            sleep(100);
            return anotherN;
        };
        final Function<Integer, Integer> function_nn = (x) -> {
            sleep(100);
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
        final CompletableFuture<Integer> cancelled = createCancelledFuture();
        final CompletableFuture<Integer> incomplete = createIncompleteFuture();

        assertEquals(Tuple2.of(n, s), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());

        // with `executorWhenTimeout`

        assertEquals(Tuple2.of(n, s), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), mostSuccessTupleOf(
                executorService, 10, TimeUnit.MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# then-multi-actions(M*) methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_thenMRun() throws Exception {
        final Runnable runnable = () -> sleep(100);
        final CompletableFuture<Object> completed = completedFuture(null);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                thenMRunAsync(completed, runnable, runnable),
                thenMRunAsync(completed, executorService, runnable, runnable),
                thenMRunFastFailAsync(completed, runnable, runnable),
                thenMRunFastFailAsync(completed, executorService, runnable, runnable),
                thenMRunAnySuccessAsync(completed, runnable, runnable),
                thenMRunAnySuccessAsync(completed, executorService, runnable, runnable),
                thenMRunAnyAsync(completed, runnable, runnable),
                thenMRunAnyAsync(completed, executorService, runnable, runnable),
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
            sleep(100);
        };
        final CompletableFuture<Integer> completed = completedFuture(n);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[]{
                thenMAcceptAsync(completed, consumer, consumer),
                thenMAcceptAsync(completed, executorService, consumer, consumer),
                thenMAcceptFastFailAsync(completed, consumer, consumer),
                thenMAcceptFastFailAsync(completed, executorService, consumer, consumer),
                thenMAcceptAnySuccessAsync(completed, consumer, consumer),
                thenMAcceptAnySuccessAsync(completed, executorService, consumer, consumer),
                thenMAcceptAnyAsync(completed, consumer, consumer),
                thenMAcceptAnyAsync(completed, executorService, consumer, consumer),
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (CompletableFuture<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_thenMApply() throws Exception {
        final Function<Integer, Integer> supplier = (x) -> {
            sleep(100);
            return n;
        };
        final CompletableFuture<Integer> completed = completedFuture(n);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        CompletableFuture<List<Integer>>[] cfs = new CompletableFuture[]{
                thenMApplyAsync(completed, supplier, supplier),
                thenMApplyAsync(completed, executorService, supplier, supplier),
                thenMApplyFastFailAsync(completed, supplier, supplier),
                thenMApplyFastFailAsync(completed, executorService, supplier, supplier),
                thenMApplyMostSuccessAsync(completed, anotherN, 500, TimeUnit.MILLISECONDS, supplier, supplier),
                thenMApplyMostSuccessAsync(completed, anotherN, executorService, 500, TimeUnit.MILLISECONDS, supplier, supplier),
                thenMApplyAnySuccessAsync(completed, supplier, supplier),
                thenMApplyAnySuccessAsync(completed, executorService, supplier, supplier),
                thenMApplyAnyAsync(completed, supplier, supplier),
                thenMApplyAnyAsync(completed, executorService, supplier, supplier),
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (CompletableFuture<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }
    }
    ////////////////////////////////////////////////////////////////////////////////
    //# both methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_both() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        final Runnable runnable = () -> {
        };
        assertNull(runAfterBothFastFail(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFastFailAsync(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFastFailAsync(cf_n, cf_nn, runnable, executorService).get());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        assertNull(thenAcceptBothFastFail(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFastFailAsync(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFastFailAsync(cf_n, cf_nn, bc, executorService).get());

        assertEquals(3 * n, thenCombineFastFail(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFastFailAsync(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFastFailAsync(cf_n, cf_nn, Integer::sum, executorService).get());
    }

    @Test
    void both_fastFail() throws Exception {
        CompletableFuture<Integer> cf_n = CompletableFuture.supplyAsync(() -> {
            sleep(2_000);
            return n;
        });
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cf_ee = failedFuture(anotherRte);

        final Runnable runnable = () -> {
        };
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFail(cf_n, failed, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(cf_n, failed, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(cf_n, failed, runnable, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFail(failed, cf_n, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_n, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_n, runnable, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFail(failed, cf_ee, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_ee, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterBothFastFailAsync(failed, cf_ee, runnable, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFail(cf_n, failed, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(cf_n, failed, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(cf_n, failed, bc, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFail(failed, cf_n, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_n, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_n, bc, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFail(failed, cf_ee, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_ee, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenAcceptBothFastFailAsync(failed, cf_ee, bc, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFail(cf_n, failed, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(cf_n, failed, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(cf_n, failed, Integer::sum, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFail(failed, cf_n, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_n, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_n, Integer::sum, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFail(failed, cf_ee, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_ee, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                thenCombineFastFailAsync(failed, cf_ee, Integer::sum, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# either methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_either() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        CompletableFuture<Integer> incomplete = createIncompleteFuture();

        final Runnable runnable = () -> {
        };
        assertNull(runAfterEitherSuccess(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable, executorService).get());

        Consumer<Integer> c = i -> {
        };
        assertNull(acceptEitherSuccess(cf_n, incomplete, c).get());
        assertNull(acceptEitherSuccessAsync(cf_n, incomplete, c).get());
        assertNull(acceptEitherSuccessAsync(cf_n, incomplete, c, executorService).get());

        assertEquals(n, applyToEitherSuccess(cf_n, incomplete, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf_n, incomplete, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf_n, incomplete, identity(), executorService).get());
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
        assertNull(runAfterEitherSuccessAsync(cf, failed, runnable, executorService).get());
        assertNull(runAfterEitherSuccess(failed, cf, runnable).get());
        assertNull(runAfterEitherSuccessAsync(failed, cf, runnable).get());
        assertNull(runAfterEitherSuccessAsync(failed, cf, runnable, executorService).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterEitherSuccess(failed, cf_ee, runnable).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterEitherSuccessAsync(failed, cf_ee, runnable).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                runAfterEitherSuccessAsync(failed, cf_ee, runnable, executorService).get()
        ).getCause());

        Consumer<Integer> c = i -> {
        };
        assertNull(acceptEitherSuccess(cf, failed, c).get());
        assertNull(acceptEitherSuccessAsync(cf, failed, c).get());
        assertNull(acceptEitherSuccessAsync(cf, failed, c, executorService).get());
        assertNull(acceptEitherSuccess(failed, cf, c).get());
        assertNull(acceptEitherSuccessAsync(failed, cf, c).get());
        assertNull(acceptEitherSuccessAsync(failed, cf, c, executorService).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                acceptEitherSuccess(failed, cf_ee, c).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                acceptEitherSuccessAsync(failed, cf_ee, c).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                acceptEitherSuccessAsync(failed, cf_ee, c, executorService).get()
        ).getCause());

        assertEquals(n, applyToEitherSuccess(cf, failed, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf, failed, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf, failed, identity(), executorService).get());
        assertEquals(n, applyToEitherSuccess(failed, cf, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(failed, cf, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(failed, cf, identity(), executorService).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                applyToEitherSuccess(failed, cf_ee, identity()).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                applyToEitherSuccessAsync(failed, cf_ee, identity()).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                applyToEitherSuccessAsync(failed, cf_ee, identity(), executorService).get()
        ).getCause());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New enhanced methods
    ////////////////////////////////////////////////////////////////////////////////

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
        assertSame(peekAsync(failed, c, executorService), failed);
        assertSame(peek(failed, ec), failed);
        assertSame(peekAsync(failed, ec), failed);
        assertSame(peekAsync(failed, ec, executorService), failed);

        CompletableFuture<Integer> success = completedFuture(n);
        assertEquals(n, peek(success, c).get());
        assertEquals(n, peekAsync(success, c).get());
        assertEquals(n, peekAsync(success, c).get());
        assertEquals(n, peek(success, ec).get());
        assertEquals(n, peekAsync(success, ec).get());
        assertEquals(n, peekAsync(success, ec).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF methods
    //  compatibility for low Java version
    ////////////////////////////////////////////////////////////////////////////////

    //# Factory methods

    @Test
    void test_failedFuture() throws Exception {
        assertTrue(failedFuture(rte).isDone());
        assertEquals(n, completedStage(n).toCompletableFuture().get());
        assertTrue(failedStage(rte).toCompletableFuture().isDone());
    }

    @Test
    void test_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> holder.set(testName), delayer).get();
        assertEquals(testName, holder.get());
    }

    @Test
    void test_exceptionallyAsync() throws Exception {
        CompletableFuture<Integer> cf = failedFuture(rte);
        assertEquals(n, exceptionallyAsync(cf, ex -> n).get());

        cf = completedFuture(n);
        assertEquals(n, exceptionallyAsync(cf, ex -> anotherN).get());

    }

    @Test
    void test_timeout() throws Exception {
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                orTimeout(createIncompleteFuture(), 1, TimeUnit.MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuOrTimeout(createIncompleteFuture(), 1, TimeUnit.MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuOrTimeout(createIncompleteFuture(), defaultExecutor(), 1, TimeUnit.MILLISECONDS).get()
        ).getCause());

        assertEquals(n, orTimeout(completedFuture(n), 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), defaultExecutor(), 1, TimeUnit.MILLISECONDS).get());

        assertEquals(n, completeOnTimeout(createIncompleteFuture(), n, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(createIncompleteFuture(), n, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(createIncompleteFuture(), n, defaultExecutor(), 1, TimeUnit.MILLISECONDS).get());

        assertEquals(n, completeOnTimeout(completedFuture(n), anotherN, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, defaultExecutor(), 1, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void test_safeBehavior_orTimeout() {
        final Thread testThread = currentThread();
        final List<Integer> results = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        assertEquals(results, results.stream().map(i ->
                orTimeout(createIncompleteFuture(), 100, TimeUnit.MILLISECONDS).handle((r1, ex1) -> {
                    assertInstanceOf(TimeoutException.class, ex1);
                    assertTrue(Delayer.atCfDelayerThread());
                    return i;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));

        assertEquals(results, results.stream().map(i ->
                cffuOrTimeout(createIncompleteFuture(), 100, TimeUnit.MILLISECONDS).handle((r, ex) -> {
                    assertInstanceOf(TimeoutException.class, ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertNotSame(testThread, currentThread());
                    return i;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
        assertEquals(results, results.stream().map(i ->
                cffuOrTimeout(createIncompleteFuture(), executorService, 100, TimeUnit.MILLISECONDS).handle((r, ex) -> {
                    assertInstanceOf(TimeoutException.class, ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertTrue(TestThreadPoolManager.isRunInExecutor(executorService));
                    return i;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    @Test
    void test_safeBehavior_completeOnTimeout() {
        final Thread testThread = currentThread();
        final List<Integer> results = IntStream.range(0, 10).boxed().collect(Collectors.toList());

        assertEquals(results, results.stream().map(i ->
                completeOnTimeout(createIncompleteFuture(), i, 100, TimeUnit.MILLISECONDS).handle((r, ex) -> {
                    assertNull(ex);
                    assertTrue(Delayer.atCfDelayerThread());
                    return r;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));

        assertEquals(results, results.stream().map(i ->
                cffuCompleteOnTimeout(createIncompleteFuture(), i, 100, TimeUnit.MILLISECONDS).handle((r, ex) -> {
                    assertNull(ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertNotSame(testThread, currentThread());
                    return r;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
        assertEquals(results, results.stream().map(i ->
                cffuCompleteOnTimeout(createIncompleteFuture(), i, executorService, 100, TimeUnit.MILLISECONDS).handle((r, ex) -> {
                    assertNull(ex);
                    assertFalse(Delayer.atCfDelayerThread());
                    assertTrue(TestThreadPoolManager.isRunInExecutor(executorService));
                    return r;
                })
        ).collect(Collectors.toList()).stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

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

        assertEquals(n, join(completed, 1, TimeUnit.MILLISECONDS));
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
                join(failed, 1, TimeUnit.MILLISECONDS)
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

        CompletableFuture<Object> cancelled = createCancelledFuture();

        assertThrowsExactly(CancellationException.class, cancelled::join);
        // same as CompletableFuture.join method
        assertThrowsExactly(CancellationException.class, () ->
                join(cancelled, 1, TimeUnit.MILLISECONDS)
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

        final CompletableFuture<Object> incomplete = createIncompleteFuture();

        assertInstanceOf(TimeoutException.class, assertThrowsExactly(CompletionException.class, () ->
                join(incomplete, 1, TimeUnit.MILLISECONDS)
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
        CompletableFuture<Integer> later = createFutureCompleteLater(n);
        assertEquals(n, join(later, 3, TimeUnit.SECONDS));
    }

    @Test
    void test_write() throws Exception {
        assertEquals(n, completeAsync(createIncompleteFuture(), () -> n).get());
        assertEquals(n, completeAsync(createIncompleteFuture(), () -> n, commonPool()).get());
        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(42);
            assertThrowsExactly(UnsupportedOperationException.class, () ->
                    completeAsync(f, () -> null)
            );
        }
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                completeAsync(createIncompleteFuture(), () -> {
                    throw rte;
                }).get()
        ).getCause());

        CompletableFuture<Integer> completed = completedFuture(n);
        assertEquals(n, completeAsync(completed, () -> anotherN).get());

        ////////////////////////////////////////

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                completeExceptionallyAsync(createIncompleteFuture(), () -> rte).get()
        ).getCause());
        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(42);
            assertThrowsExactly(UnsupportedOperationException.class, () ->
                    completeExceptionallyAsync(f, () -> rte)
            );
        }
        assertEquals(n, completeExceptionallyAsync(completedFuture(n), () -> rte).get());
    }

    @Test
    void test_re_config() throws Exception {
        CompletionStage<Integer> mf = minimalCompletionStage(completedFuture(n));
        assertEquals(n, mf.toCompletableFuture().get());

        CompletableFuture<Integer> cf = createIncompleteFuture();
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
        assertIsDefaultExecutor(executor);

        assertIsDefaultExecutor(screenExecutor(commonPool()));

        ExecutorService e = Executors.newCachedThreadPool();
        assertSame(e, screenExecutor(e));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion (Static) Methods
    //
    //    - toCompletableFutureArray:     CompletionStage[](including Cffu) -> CF[]
    //    - completableFutureListToArray: List<CF> -> CF[]
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
    @SuppressWarnings("ThrowableNotThrown")
    void test_unwrapCfException() {
        CompletableFuture<Object> failed = failedFuture(rte);

        ExecutionException ee = assertThrowsExactly(ExecutionException.class, () -> failed.get(0, TimeUnit.MILLISECONDS));
        assertSame(rte, unwrapCfException(ee));

        CompletionException ce = assertThrowsExactly(CompletionException.class, failed::join);
        assertSame(rte, unwrapCfException(ce));

        CompletionException nakedCe = new CompletionException() {
        };
        assertSame(nakedCe, unwrapCfException(nakedCe));

        assertSame(rte, unwrapCfException(rte));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private static void assertIsDefaultExecutor(Executor executor) {
        final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;
        if (USE_COMMON_POOL) {
            assertSame(commonPool(), executor);
        } else {
            String executorClassName = executor.getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }
    }

    private static final String testName = "CompletableFutureUtilsTest";
    private static ExecutorService executorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("CffuTest");
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
