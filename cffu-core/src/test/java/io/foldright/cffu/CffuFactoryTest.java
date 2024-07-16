package io.foldright.cffu;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
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
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.failedFuture;
import static io.foldright.cffu.CompletableFutureUtils.toCompletableFuture;
import static io.foldright.cffu.CompletableFutureUtils.toCompletableFutureArray;
import static io.foldright.cffu.CompletableFutureUtils.toListenableFuture;
import static io.foldright.test_utils.TestUtils.*;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


/**
 * see io.foldright.compatibility_test.CffuApiCompatibilityTest
 */
@SuppressWarnings("RedundantThrows")
class CffuFactoryTest {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Constructor Method
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_newIncompleteCffu() {
        Cffu<Integer> cf = cffuFactory.newIncompleteCffu();
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
        final Runnable runnable = () -> sleep(100);

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Void>[] cfs = new Cffu[]{
                cffuFactory.mRunAsync(runnable, runnable),
                cffuFactory.mRunFastFailAsync(runnable, runnable),
                cffuFactory.mRunAnySuccessAsync(runnable, runnable),
                cffuFactory.mRunAnyAsync(runnable, runnable),
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (Cffu<Void> cf : cfs) {
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
        Cffu<List<Integer>>[] cfs = new Cffu[]{
                cffuFactory.mSupplyFastFailAsync(supplier, supplier),
                cffuFactory.mSupplyFastFailAsync(executorService, supplier, supplier),
                cffuFactory.mSupplyAllSuccessAsync(anotherN, supplier, supplier),
                cffuFactory.mSupplyAllSuccessAsync(anotherN, executorService, supplier, supplier),
                cffuFactory.mSupplyMostSuccessAsync(anotherN, 500, TimeUnit.MILLISECONDS, supplier, supplier),
                cffuFactory.mSupplyMostSuccessAsync(anotherN, executorService, 500, TimeUnit.MILLISECONDS, supplier, supplier),
                cffuFactory.mSupplyAsync(supplier, supplier),
                cffuFactory.mSupplyAsync(executorService, supplier, supplier),
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (Cffu<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }

        final long tick1 = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        Cffu<Integer>[] cfs1 = new Cffu[]{
                cffuFactory.mSupplyAnySuccessAsync(supplier, supplier),
                cffuFactory.mSupplyAnySuccessAsync(executorService, supplier, supplier),
                cffuFactory.mSupplyAnyAsync(supplier, supplier),
                cffuFactory.mSupplyAnyAsync(executorService, supplier, supplier),
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

        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyFastFailAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyFastFailAsync(executorService, supplier_n, supplier_s).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyFastFailAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyFastFailAsync(executorService, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyFastFailAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyFastFailAsync(executorService, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyFastFailAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyFastFailAsync(executorService, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());

        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyAllSuccessAsync(executorService, supplier_n, supplier_s).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyAllSuccessAsync(executorService, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyAllSuccessAsync(executorService, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyAllSuccessAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyAllSuccessAsync(executorService, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());

        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyAsync(executorService, supplier_n, supplier_s).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyAsync(executorService, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyAsync(executorService, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyAsync(executorService, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
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
        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), cffuFactory.tupleMSupplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), cffuFactory.tupleMSupplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.tupleMSupplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.tupleMSupplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostSuccessResultsOf)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allOf() throws Exception {
        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allResultsFastFailOf(completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allResultsFastFailOf(completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                cffuFactory.allResultsFastFailOf().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allResultsFastFailOf(cffuFactory.completedFuture(n), cffuFactory.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allResultsFastFailOf(cffuFactory.completedFuture(n)).get()
        );

        ////////////////////////////////////////

        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allSuccessResultsOf(anotherN, completedFuture(n), completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allSuccessResultsOf(anotherN, completedFuture(n)).get()
        );

        assertEquals(Collections.emptyList(),
                cffuFactory.allResultsFastFailOf().get()
        );

        assertEquals(Arrays.asList(n, n + 1),
                cffuFactory.allSuccessResultsOf(anotherN, cffuFactory.completedFuture(n), cffuFactory.completedFuture(n + 1)).get()
        );
        assertEquals(Collections.singletonList(n),
                cffuFactory.allSuccessResultsOf(anotherN, cffuFactory.completedFuture(n)).get()
        );

        ////////////////////////////////////////
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
    }

    @Test
    void test_allOf_CompletableFuture() throws Exception {
        cffuFactory.allOf(completedFuture(n), completedFuture(anotherN)).get();
        cffuFactory.allOf(completedFuture(anotherN)).get();

        assertNull(cffuFactory.allOf().get());

        ////////////////////////////////////////

        cffuFactory.allFastFailOf(completedFuture(n), completedFuture(anotherN)).get();
        cffuFactory.allFastFailOf(completedFuture(anotherN)).get();

        assertNull(cffuFactory.allFastFailOf().get());

        cffuFactory.allFastFailOf(cffuFactory.completedFuture(n), cffuFactory.completedFuture(anotherN)).get();
        cffuFactory.allFastFailOf(cffuFactory.completedFuture(anotherN)).get();
    }

    @Test
    void test_allOf_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allResultsOf(
                        cffuFactory.completedFuture(n),
                        cffuFactory.failedFuture(rte),
                        cffuFactory.completedFuture(s)
                ).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allResultsFastFailOf(
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

        assertEquals(0, cffuFactory.mostSuccessResultsOf(null, 10, TimeUnit.MILLISECONDS).get().size());

        assertEquals(Arrays.asList(n, null, null, null), cffuFactory.mostSuccessResultsOf(
                null, 10, TimeUnit.MILLISECONDS, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(Arrays.asList(n, anotherN, anotherN, anotherN), cffuFactory.mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(Arrays.asList(anotherN, anotherN, anotherN), cffuFactory.mostSuccessResultsOf(
                anotherN, 10, TimeUnit.MILLISECONDS, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(Arrays.asList(anotherN, anotherN), cffuFactory.mostSuccessResultsOf(
                anotherN, 10, TimeUnit.DAYS, failed, cancelled
        ).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    ////////////////////////////////////////////////////////////////////////////////

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

        assertEquals(n, cffuFactory.anySuccessOf(createIncompleteFuture(), completedFuture(n)).get());
        assertEquals(n, cffuFactory.anySuccessOf(completedFuture(n)).get());

        assertSame(NoCfsProvidedException.class, cffuFactory.anySuccessOf().exceptionNow().getClass());

        assertEquals(n, cffuFactory.anySuccessOf(
                cffuFactory.completedFuture(n),
                cffuFactory.newIncompleteCffu()
        ).get());
        assertEquals(n, cffuFactory.anySuccessOf(cffuFactory.completedFuture(n)).get());
    }

    @Test
    void test_anyOf_CompletableFuture() throws Exception {
        cffuFactory.anyOf(completedFuture(n), completedFuture(anotherN)).get();
        assertEquals(anotherN, cffuFactory.anyOf(completedFuture(anotherN)).get());

        assertFalse(cffuFactory.anyOf().isDone());

        ////////////////////////////////////////

        cffuFactory.anySuccessOf(completedFuture(n), completedFuture(anotherN)).get();
        assertEquals(anotherN, cffuFactory.anySuccessOf(completedFuture(anotherN)).get());

        assertInstanceOf(NoCfsProvidedException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.anySuccessOf().get()
        ).getCause());

        cffuFactory.anySuccessOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(anotherN)
        ).get();
        assertEquals(anotherN, cffuFactory.anySuccessOf(
                cffuFactory.completedFuture(anotherN)
        ).get());
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
    void test_anySuccessOf__trivial_case() throws Exception {
        // success then success
        assertEquals(n, cffuFactory.anySuccessOf(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.newIncompleteCffu(),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                cffuFactory.completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, cffuFactory.anySuccessOf(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.newIncompleteCffu(),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                cffuFactory.completedFuture(n)
        ).get());

        // all success
        assertEquals(n, cffuFactory.anySuccessOf(
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
        assertEquals(n, cffuFactory.anySuccessOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, cffuFactory.anySuccessOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // all success
        assertEquals(n, cffuFactory.anySuccessOf(
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

        assertSame(NoCfsProvidedException.class, cffuFactory.anySuccessOf().exceptionNow().getClass());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostSuccessTupleOf Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allTupleFastFailOf() throws Exception {
        assertEquals(Tuple2.of(n, s), cffuFactory.allTupleFastFailOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allTupleFastFailOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allTupleFastFailOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allTupleFastFailOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.allTupleFastFailOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allTupleFastFailOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allTupleFastFailOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allTupleFastFailOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN),
                cffuFactory.completedFuture(n + n)
        ).get());
    }

    @Test
    void test_allTupleFastFail_Of_exceptionally() throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleFastFailOf(completedFuture(n), failedFuture(rte)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleFastFailOf(completedFuture(n), failedFuture(rte), completedFuture(s)).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.allTupleFastFailOf(
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
        assertEquals(Tuple2.of(n, s), cffuFactory.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allSuccessTupleOf(
                completedFuture(n),
                completedFuture(s),
                completedFuture(d),
                completedFuture(anotherN),
                completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.allSuccessTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.allSuccessTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), cffuFactory.allSuccessTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN)
        ).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), cffuFactory.allSuccessTupleOf(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(anotherN),
                cffuFactory.completedFuture(n + n)
        ).get());
    }

    @Test
    void test_mostSuccessTupleOf() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(n);
        final CompletionStage<String> anotherCompleted = cffuFactory.completedStage(s);
        final Cffu<Integer> failed = cffuFactory.failedFuture(rte);
        final Cffu<Integer> cancelled = cffuFactory.toCffu(createCancelledFuture());
        final Cffu<Integer> incomplete = cffuFactory.toCffu(createIncompleteFuture());

        assertEquals(Tuple2.of(n, s), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), cffuFactory.mostSuccessTupleOf(
                10, TimeUnit.MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## Immediate Value Argument Factory Methods
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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## CompletionStage Argument Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

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
    //    - cffuArrayUnwrap: Cffu[] -> CompletableFuture[]
    //    - cffuListToArray: List<Cffu> -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

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

    @Test
    void test_convertListenableFuture() throws Exception{


        ListenableFuture orginalListenableFuture = executor.submit(()->n);
        ListenableFuture orginalAnotherListenableFuture = executor.submit(()->anotherN);

        CompletableFuture srcCompletableFuture1 = toCompletableFuture(orginalListenableFuture);
        CompletableFuture srcCompletableFuture2 = toCompletableFuture(orginalAnotherListenableFuture,executorService);

        assertEquals(orginalListenableFuture.get(), srcCompletableFuture1.get());
        assertEquals(orginalAnotherListenableFuture.get(), srcCompletableFuture2.get());

        CompletableFuture orginalCompletableFuture = completedFuture(n);

        ListenableFuture srcListenableFuture = toListenableFuture(orginalCompletableFuture);

        assertEquals(orginalCompletableFuture.get(), srcListenableFuture.get());
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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Getter Methods of CffuFactory properties
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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper methods/fields
    ////////////////////////////////////////////////////////////////////////////////

    private static CffuFactory cffuFactory;

    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    private static ListeningExecutorService executor = MoreExecutors.listeningDecorator(newCachedThreadPool());

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
