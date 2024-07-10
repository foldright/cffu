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
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.foldright.test_utils.TestUtils.*;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


/**
 * NOTE:
 * <p>
 * Use {@code java} code to test the api usage problem of {@link Cffu};
 * Do NOT rewrite to {@code kotlin}.
 */
@SuppressWarnings("RedundantThrows")
class CffuTest {
    private static CffuFactory cffuFactory;

    private static CffuFactory forbidObtrudeMethodsCffuFactory;

    ////////////////////////////////////////////////////////////////////////////////
    //# multi-actions(M*) methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_thenMApply() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(n);

        final Function<Integer, Integer> function_n = (x) -> {
            sleep(100);
            return n;
        };


        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Void>[] cfs = new Cffu[]{
                completed.thenMApplyFastFailAsync(function_n, function_n),
                completed.thenMApplyFastFailAsync(executorService, function_n, function_n),
                completed.thenMApplyMostSuccessAsync(100, 500, TimeUnit.MILLISECONDS, function_n, function_n),
                completed.thenMApplyMostSuccessAsync(100, executorService, 500, TimeUnit.MILLISECONDS, function_n, function_n),
                completed.thenMApplyAsync(function_n, function_n),
                completed.thenMApplyAsync(executorService, function_n, function_n)
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (Cffu<Void> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }


        final long tick1 = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Void>[] cfs1 = new Cffu[]{
                completed.thenMApplyAnySuccessAsync(function_n, function_n),
                completed.thenMApplyAnySuccessAsync(executorService, function_n, function_n),
                completed.thenMApplyAnyAsync(function_n, function_n),
                completed.thenMApplyAnyAsync(executorService, function_n, function_n),
        };

        assertTrue(System.currentTimeMillis() - tick1 < 50);
        for (Cffu<Void> cf : cfs1) {
            assertEquals(n, cf.get());
        }
    }

    @Test
    void test_thenMAccept() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(n);
        final Consumer<Integer> consumer = (x) -> {
            assertEquals(n, x);
            sleep(100);
        };

        final long tick = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        Cffu<List<Integer>>[] cfs = new Cffu[]{
                completed.thenMAcceptAsync(consumer, consumer),
                completed.thenMAcceptAsync(executorService, consumer, consumer),
                completed.thenMAcceptFastFailAsync(consumer, consumer),
                completed.thenMAcceptFastFailAsync(executorService, consumer, consumer),
                completed.thenMAcceptAnySuccessAsync(consumer, consumer),
                completed.thenMAcceptAnySuccessAsync(executorService, consumer, consumer),
                completed.thenMAcceptAnyAsync(consumer, consumer),
                completed.thenMAcceptAnyAsync(executorService, consumer, consumer)
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (Cffu<List<Integer>> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_thenMRun() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(null);
        final Runnable runnable = () -> {
            sleep(100);
        };

        final long tick = System.currentTimeMillis();

        @SuppressWarnings("unchecked")
        Cffu<List<Integer>>[] cfs = new Cffu[]{
                completed.thenMRunAsync(runnable, runnable),
                completed.thenMRunAsync(executorService, runnable, runnable),
                completed.thenMRunFastFailAsync(runnable, runnable),
                completed.thenMRunFastFailAsync(executorService, runnable, runnable),
                completed.thenMRunAnySuccessAsync(runnable, runnable),
                completed.thenMRunAnyAsync(executorService, runnable, runnable)
        };

        assertTrue(System.currentTimeMillis() - tick < 50);
        for (Cffu<List<Integer>> cf : cfs) {
            assertNull(cf.get());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Then-Tuple-Multi-Actions(thenTupleM*) Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_thenTupleMApplyAsync() throws Exception {
        final Cffu<Integer> completed = cffuFactory.completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            sleep(10);
            return n;
        };

        final Function<Integer, String> function_s = (x) -> {
            sleep(10);
            return s;
        };

        final Function<Integer, Double> function_d = (x) -> {
            sleep(10);
            return d;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            sleep(10);
            return anotherN;
        };
        final Function<Integer, Integer> function_nn = (x) -> {
            sleep(10);
            return n + n;
        };
        assertEquals(Tuple2.of(n, s), completed.thenTupleMApplyAsync(function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), completed.thenTupleMApplyAsync(executorService, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), completed.thenTupleMApplyFastFailAsync(function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), completed.thenTupleMApplyFastFailAsync(executorService, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), completed.thenTupleMApplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple2.of(n, s), completed.thenTupleMApplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, function_n, function_s).get());

        assertEquals(Tuple3.of(n, s, d), completed.thenTupleMApplyAsync(function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), completed.thenTupleMApplyAsync(executorService, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), completed.thenTupleMApplyFastFailAsync(function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), completed.thenTupleMApplyFastFailAsync(executorService, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), completed.thenTupleMApplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n, s, d), completed.thenTupleMApplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, function_n, function_s, function_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), completed.thenTupleMApplyAsync(function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), completed.thenTupleMApplyAsync(executorService, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), completed.thenTupleMApplyFastFailAsync(function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), completed.thenTupleMApplyFastFailAsync(executorService, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), completed.thenTupleMApplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), completed.thenTupleMApplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), completed.thenTupleMApplyAsync(function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), completed.thenTupleMApplyAsync(executorService, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), completed.thenTupleMApplyFastFailAsync(function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), completed.thenTupleMApplyFastFailAsync(executorService, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), completed.thenTupleMApplyMostSuccessAsync(100, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), completed.thenTupleMApplyMostSuccessAsync(executorService, 100, TimeUnit.MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
    }
    ////////////////////////////////////////////////////////////////////////////////
    //# both methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void both_fastFail() throws Exception {
        Cffu<Integer> cf = cffuFactory.supplyAsync(() -> {
            sleep(2_000);
            return n;
        });
        final Cffu<Integer> failed = cffuFactory.failedFuture(rte);

        final Runnable runnable = () -> {
        };
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.runAfterBothFastFail(failed, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.runAfterBothFastFailAsync(failed, runnable).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.runAfterBothFastFailAsync(failed, runnable, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenAcceptBothFastFail(failed, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenAcceptBothFastFailAsync(failed, bc).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenAcceptBothFastFailAsync(failed, bc, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenCombineFastFail(failed, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenCombineFastFailAsync(failed, Integer::sum).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenCombineFastFailAsync(failed, Integer::sum, executorService).get(1, TimeUnit.MILLISECONDS)
        ).getCause());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# either methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_either_success() throws Exception {
        final Cffu<Integer> failed = cffuFactory.failedFuture(rte);
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        final Runnable runnable = () -> {
        };
        assertNull(failed.runAfterEitherSuccess(cf, runnable).get());
        assertNull(failed.runAfterEitherSuccessAsync(cf, runnable).get());
        assertNull(failed.runAfterEitherSuccessAsync(cf, runnable, executorService).get());

        Consumer<Integer> c = i -> {
        };
        assertNull(failed.acceptEitherSuccess(cf, c).get());
        assertNull(failed.acceptEitherSuccessAsync(cf, c).get());
        assertNull(failed.acceptEitherSuccessAsync(cf, c, executorService).get());

        assertEquals(n, failed.applyToEitherSuccess(cf, identity()).get());
        assertEquals(n, failed.applyToEitherSuccessAsync(cf, identity()).get());
        assertEquals(n, failed.applyToEitherSuccessAsync(cf, identity(), executorService).get());
    }

    ////////////////////////////////////////
    // timeout control
    //
    // also tested in CffuApiCompatibilityTest
    ////////////////////////////////////////

    @Test
    void test_timeout() throws Exception {
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.newIncompleteCffu().orTimeout(1, TimeUnit.MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.newIncompleteCffu().orTimeout(executorService, 1, TimeUnit.MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                cffuFactory.newIncompleteCffu().unsafeOrTimeout(1, TimeUnit.MILLISECONDS).get()
        ).getCause());

        assertEquals(n, cffuFactory.completedFuture(n).orTimeout(1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuFactory.completedFuture(n).orTimeout(executorService, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuFactory.completedFuture(n).unsafeOrTimeout(1, TimeUnit.MILLISECONDS).get());

        assertEquals(n, cffuFactory.newIncompleteCffu().completeOnTimeout(
                n, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuFactory.newIncompleteCffu().completeOnTimeout(
                n, executorService, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuFactory.newIncompleteCffu().unsafeCompleteOnTimeout(
                n, 1, TimeUnit.MILLISECONDS).get());

        assertEquals(n, cffuFactory.completedFuture(n).completeOnTimeout(
                anotherN, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuFactory.completedFuture(n).completeOnTimeout(
                anotherN, executorService, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuFactory.completedFuture(n).unsafeCompleteOnTimeout(
                anotherN, 1, TimeUnit.MILLISECONDS).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Read(explicitly) methods
    //
    //    - get()               // BLOCKING
    //    - get(timeout, unit)  // BLOCKING
    //    - join()              // BLOCKING
    //    - join()          // BLOCKING
    //    - getNow(T valueIfAbsent)
    //    - resultNow()
    //    - exceptionNow()
    //
    //    - isDone()
    //    - isCompletedExceptionally()
    //    - isCancelled()
    //    - state()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuJoin() {
        // Completed Future
        assertEquals(n, cffuFactory.completedFuture(n).join(1, TimeUnit.MILLISECONDS));

        // Incomplete Future -> CompletionException with TimeoutException
        Cffu<Object> incomplete = cffuFactory.newIncompleteCffu();
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(CompletionException.class, () ->
                incomplete.join(1, TimeUnit.MILLISECONDS)
        ).getCause());

        // Failed Future -> CompletionException
        Cffu<Object> failed = cffuFactory.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                failed.join(1, TimeUnit.MILLISECONDS)
        ).getCause());

        // Incomplete Future -> join before timeout

        Cffu<Integer> cffu = cffuFactory.supplyAsync(() -> {
            sleep(300);
            return 42;
        });
        assertEquals(42, cffu.join(3, TimeUnit.SECONDS));
    }

    @Test
    void test_getSuccessNow() {
        final Consumer<Cffu<Integer>> check = (cf) -> {
            assertNull(cf.getSuccessNow(null));
            assertEquals(42, cf.getSuccessNow(42));
        };

        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();
        check.accept(incomplete);

        Cffu<Integer> failed = cffuFactory.failedFuture(rte);
        check.accept(failed);

        incomplete.cancel(false);
        check.accept(incomplete);
    }

    @Test
    void test_cffuState() {
        Cffu<Object> incomplete = cffuFactory.newIncompleteCffu();

        assertEquals(CffuState.RUNNING, incomplete.cffuState());
        assertEquals(CffuState.SUCCESS, cffuFactory.completedFuture(42).cffuState());
        assertEquals(CffuState.FAILED, cffuFactory.failedFuture(rte).cffuState());

        incomplete.cancel(false);
        assertEquals(CffuState.CANCELLED, incomplete.cffuState());
    }

    @Test
    void test_peek() throws Exception {
        BiConsumer<Object, Throwable> c = (v, ex) -> {
        };
        BiConsumer<Object, Throwable> ec = (v, ex) -> {
            throw anotherRte;
        };

        Cffu<Object> failed = cffuFactory.failedFuture(rte);
        assertSame(failed.peek(c), failed);
        assertSame(failed.peekAsync(c), failed);
        assertSame(failed.peekAsync(c, executorService), failed);
        assertSame(failed.peek(ec), failed);
        assertSame(failed.peekAsync(ec), failed);
        assertSame(failed.peekAsync(ec, executorService), failed);

        Cffu<Integer> success = cffuFactory.completedFuture(n);
        assertEquals(n, success.peek(c).get());
        assertEquals(n, success.peekAsync(c).get());
        assertEquals(n, success.peekAsync(c).get());
        assertEquals(n, success.peek(ec).get());
        assertEquals(n, success.peekAsync(ec).get());
        assertEquals(n, success.peekAsync(ec).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Cffu Re-Config methods
    //
    //    - minimalCompletionStage()
    //    - resetCffuFactory(cffuFactory)
    //
    //    - toCompletableFuture()
    //    - copy()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_resetCffuFactory() {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        assertSame(cffuFactory, cf.cffuFactory());

        assertSame(forbidObtrudeMethodsCffuFactory, cf.resetCffuFactory(forbidObtrudeMethodsCffuFactory).cffuFactory());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Getter methods of Cffu properties
    //
    //    - defaultExecutor()
    //    - forbidObtrudeMethods()
    //    - isMinimalStage()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_forbidObtrudeMethods() {
        assertFalse(cffuFactory.completedFuture(42).forbidObtrudeMethods());
        assertTrue(forbidObtrudeMethodsCffuFactory.completedFuture(42).forbidObtrudeMethods());
    }

    @Test
    void test_isMinimalStage() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        assertFalse(cf.isMinimalStage());

        assertTrue(((Cffu<Integer>) cffuFactory.completedStage(42)).isMinimalStage());
        assertTrue(((Cffu<Object>) cffuFactory.failedStage(rte)).isMinimalStage());

        assertTrue(((Cffu<Integer>) cf.minimalCompletionStage()).isMinimalStage());

        assertFalse(forbidObtrudeMethodsCffuFactory.completedFuture(42).isMinimalStage());
        assertTrue(((Cffu<Integer>) forbidObtrudeMethodsCffuFactory.completedStage(42)).isMinimalStage());
        assertTrue(((Cffu<Object>) forbidObtrudeMethodsCffuFactory.failedStage(rte)).isMinimalStage());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Inspection methods of Cffu
    //
    //    - cffuUnwrap()
    //    - getNumberOfDependents()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuUnwrap() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        Cffu<Integer> cffu = cffuFactory.toCffu(cf);

        assertSame(cf, cffu.cffuUnwrap());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_cffuUnwrap_9_completedStage() {
        CompletionStage<Integer> stage = CompletableFuture.completedStage(n);
        Cffu<Integer> cffu = cffuFactory.toCffu(stage);

        assertNotSame(stage, cffu.cffuUnwrap());
        assertEquals(n, cffu.join());
    }

    @Test
    void test_toString() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        Cffu<Integer> cffu = cffuFactory.toCffu(cf);

        assertTrue(cffu.toString().contains(cf.toString()));
        assertTrue(cffu.toString().startsWith("Cffu@"));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Test helper methods
    ////////////////////////////////////////////////////////////////////////////////

    private static ExecutorService executorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("CffuTest");

        cffuFactory = CffuFactory.builder(executorService).build();
        forbidObtrudeMethodsCffuFactory = CffuFactory.builder(executorService).forbidObtrudeMethods(true).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
