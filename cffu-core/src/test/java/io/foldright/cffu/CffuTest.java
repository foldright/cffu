package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static io.foldright.test_utils.TestUtils.nap;
import static io.foldright.test_utils.TestUtils.snoreZzz;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.testCffuFac;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
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
    ////////////////////////////////////////////////////////////////////////////////
    // region# Simple then* Methods of CompletionStage
    //
    //    - thenApply*(Function):  T -> U
    //    - thenAccept*(Consumer): T -> Void
    //    - thenRun*(Runnable):    Void -> Void
    ////////////////////////////////////////////////////////////////////////////////

    // tested in implementation??

    // endregion
    ////////////////////////////////////////////////////////////
    // region# Then-Multi-Actions(thenM*) Methods
    ////////////////////////////////////////////////////////////

    @Test
    void test_thenMApply() throws Exception {
        final Cffu<Integer> completed = testCffuFac.completedFuture(n);

        final Function<Integer, Integer> function_n = (x) -> {
            snoreZzz();
            return n;
        };


        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<List<Integer>>[] cfs = new Cffu[]{
                completed.thenMApplyFailFastAsync(function_n, function_n),
                completed.thenMApplyFailFastAsync(testExecutor, function_n, function_n),
                completed.thenMApplyAllSuccessAsync(anotherN, function_n, function_n),
                completed.thenMApplyAllSuccessAsync(testExecutor, anotherN, function_n, function_n),
                completed.thenMApplyMostSuccessAsync(anotherN, LONG_WAIT_MS, MILLISECONDS, function_n, function_n),
                completed.thenMApplyMostSuccessAsync(testExecutor, anotherN, LONG_WAIT_MS, MILLISECONDS, function_n, function_n),
                completed.thenMApplyAsync(function_n, function_n),
                completed.thenMApplyAsync(testExecutor, function_n, function_n)
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (Cffu<List<Integer>> cf : cfs) {
            assertEquals(Arrays.asList(n, n), cf.get());
        }


        final long tick1 = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] cfs1 = new Cffu[]{
                completed.thenMApplyAnySuccessAsync(function_n, function_n),
                completed.thenMApplyAnySuccessAsync(testExecutor, function_n, function_n),
                completed.thenMApplyAnyAsync(function_n, function_n),
                completed.thenMApplyAnyAsync(testExecutor, function_n, function_n),
        };
        assertTrue(System.currentTimeMillis() - tick1 < 50);

        for (Cffu<Integer> cf : cfs1) {
            assertEquals(n, cf.get());
        }
    }

    @Test
    void test_thenMAccept() throws Exception {
        final Cffu<Integer> completed = testCffuFac.completedFuture(n);
        final Consumer<Integer> consumer = (x) -> {
            assertEquals(n, x);
            snoreZzz();
        };

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<List<Integer>>[] cfs = new Cffu[]{
                completed.thenMAcceptAsync(consumer, consumer),
                completed.thenMAcceptAsync(testExecutor, consumer, consumer),
                completed.thenMAcceptFailFastAsync(consumer, consumer),
                completed.thenMAcceptFailFastAsync(testExecutor, consumer, consumer),
                completed.thenMAcceptAnySuccessAsync(consumer, consumer),
                completed.thenMAcceptAnySuccessAsync(testExecutor, consumer, consumer),
                completed.thenMAcceptAnyAsync(consumer, consumer),
                completed.thenMAcceptAnyAsync(testExecutor, consumer, consumer)
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (Cffu<List<Integer>> cf : cfs) {
            assertNull(cf.get());
        }
    }

    @Test
    void test_thenMRun() throws Exception {
        final Cffu<Integer> completed = testCffuFac.completedFuture(null);
        final Runnable runnable = TestUtils::snoreZzz;

        final long tick = System.currentTimeMillis();
        @SuppressWarnings("unchecked")
        Cffu<Void>[] cfs = new Cffu[]{
                completed.thenMRunAsync(runnable, runnable),
                completed.thenMRunAsync(testExecutor, runnable, runnable),
                completed.thenMRunFailFastAsync(runnable, runnable),
                completed.thenMRunFailFastAsync(testExecutor, runnable, runnable),
                completed.thenMRunAnySuccessAsync(runnable, runnable),
                completed.thenMRunAnySuccessAsync(testExecutor, runnable, runnable),
                completed.thenMRunAnyAsync(runnable, runnable),
                completed.thenMRunAnyAsync(testExecutor, runnable, runnable),
        };
        assertTrue(System.currentTimeMillis() - tick < 50);

        for (Cffu<Void> cf : cfs) {
            assertNull(cf.get());
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region# Then-Multi-Actions-Tuple(thenMTuple*) Methods
    ////////////////////////////////////////////////////////////

    @Test
    void test_thenMApplyTupleAsync() throws Exception {
        final Cffu<Integer> completed = testCffuFac.completedFuture(n);
        final Function<Integer, Integer> function_n = (x) -> {
            nap();
            return n + n;
        };

        final Function<Integer, String> function_s = (x) -> {
            nap();
            return s + n;
        };

        final Function<Integer, Double> function_d = (x) -> {
            nap();
            return d + n;
        };
        final Function<Integer, Integer> function_an = (x) -> {
            nap();
            return anotherN + n;
        };
        final Function<Integer, Integer> function_nnn = (x) -> {
            nap();
            return nnn + n;
        };

        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyTupleFailFastAsync(function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyTupleFailFastAsync(testExecutor, function_n, function_s).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyTupleFailFastAsync(function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyTupleFailFastAsync(testExecutor, function_n, function_s, function_d).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyTupleFailFastAsync(function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyTupleFailFastAsync(testExecutor, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyTupleFailFastAsync(function_n, function_s, function_d, function_an, function_nnn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyTupleFailFastAsync(testExecutor, function_n, function_s, function_d, function_an, function_nnn).get());

        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyAllSuccessTupleAsync(function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyAllSuccessTupleAsync(testExecutor, function_n, function_s).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyAllSuccessTupleAsync(function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyAllSuccessTupleAsync(testExecutor, function_n, function_s, function_d).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyAllSuccessTupleAsync(function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyAllSuccessTupleAsync(testExecutor, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyAllSuccessTupleAsync(function_n, function_s, function_d, function_an, function_nnn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyAllSuccessTupleAsync(testExecutor, function_n, function_s, function_d, function_an, function_nnn).get());

        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyMostSuccessTupleAsync(MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyMostSuccessTupleAsync(testExecutor, MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyMostSuccessTupleAsync(MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyMostSuccessTupleAsync(testExecutor, MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyMostSuccessTupleAsync(MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyMostSuccessTupleAsync(testExecutor, MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyMostSuccessTupleAsync(MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nnn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyMostSuccessTupleAsync(testExecutor, MEDIAN_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nnn).get());

        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyTupleAsync(function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), completed.thenMApplyTupleAsync(testExecutor, function_n, function_s).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyTupleAsync(function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), completed.thenMApplyTupleAsync(testExecutor, function_n, function_s, function_d).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyTupleAsync(function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), completed.thenMApplyTupleAsync(testExecutor, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyTupleAsync(function_n, function_s, function_d, function_an, function_nnn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, nnn + n), completed.thenMApplyTupleAsync(testExecutor, function_n, function_s, function_d, function_an, function_nnn).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# thenBoth* Methods(binary input) of CompletionStage
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void bothFailFast() throws Exception {
        Cffu<Integer> cf = testCffuFac.supplyAsync(() -> {
            snoreZzz(LONG_WAIT_MS);
            return n;
        });
        final Cffu<Integer> failed = testCffuFac.failedFuture(rte);

        final Runnable runnable = () -> {};
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.runAfterBothFailFast(failed, runnable).get(1, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.runAfterBothFailFastAsync(failed, runnable).get(1, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.runAfterBothFailFastAsync(failed, runnable, testExecutor).get(1, MILLISECONDS)
        ).getCause());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {};
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenAcceptBothFailFast(failed, bc).get(1, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenAcceptBothFailFastAsync(failed, bc).get(1, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenAcceptBothFailFastAsync(failed, bc, testExecutor).get(1, MILLISECONDS)
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenCombineFailFast(failed, Integer::sum).get(1, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenCombineFailFastAsync(failed, Integer::sum).get(1, MILLISECONDS)
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                cf.thenCombineFailFastAsync(failed, Integer::sum, testExecutor).get(1, MILLISECONDS)
        ).getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# thenEither* Methods(binary input) of CompletionStage
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_either_success() throws Exception {
        final Cffu<Integer> failed = testCffuFac.failedFuture(rte);
        Cffu<Integer> cf = testCffuFac.completedFuture(n);

        final Runnable runnable = () -> {};
        assertNull(failed.runAfterEitherSuccess(cf, runnable).get());
        assertNull(failed.runAfterEitherSuccessAsync(cf, runnable).get());
        assertNull(failed.runAfterEitherSuccessAsync(cf, runnable, testExecutor).get());

        Consumer<Integer> c = i -> {};
        assertNull(failed.acceptEitherSuccess(cf, c).get());
        assertNull(failed.acceptEitherSuccessAsync(cf, c).get());
        assertNull(failed.acceptEitherSuccessAsync(cf, c, testExecutor).get());

        assertEquals(n, failed.applyToEitherSuccess(cf, identity()).get());
        assertEquals(n, failed.applyToEitherSuccessAsync(cf, identity()).get());
        assertEquals(n, failed.applyToEitherSuccessAsync(cf, identity(), testExecutor).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Error Handling Methods of CompletionStage
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_catching() throws Exception {
        Cffu<Integer> failed = testCffuFac.failedFuture(rte);

        assertEquals(n, failed.catching(RuntimeException.class, ex -> n).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                failed.catching(IndexOutOfBoundsException.class, ex -> n).get()
        ).getCause());

        assertEquals(n, failed.catchingAsync(RuntimeException.class, ex -> n).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                failed.catchingAsync(IndexOutOfBoundsException.class, ex -> n).get()
        ).getCause());

        Cffu<Integer> success = testCffuFac.completedFuture(n);

        assertEquals(n, success.catching(RuntimeException.class, ex -> anotherN).get());
        assertEquals(n, success.catching(IndexOutOfBoundsException.class, ex -> anotherN).get());

        assertEquals(n, success.catchingAsync(RuntimeException.class, ex -> anotherN).get());
        assertEquals(n, success.catchingAsync(IndexOutOfBoundsException.class, ex -> anotherN).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Timeout Control Methods
    // also tested in CffuApiCompatibilityTest
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_timeout() throws Exception {
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.newIncompleteCffu().orTimeout(1, MILLISECONDS).get()
        ).getCause());
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                testCffuFac.newIncompleteCffu().unsafeOrTimeout(1, MILLISECONDS).get()
        ).getCause());

        assertEquals(n, testCffuFac.completedFuture(n).orTimeout(1, MILLISECONDS).get());
        assertEquals(n, testCffuFac.completedFuture(n).unsafeOrTimeout(1, MILLISECONDS).get());

        assertEquals(n, testCffuFac.newIncompleteCffu().completeOnTimeout(
                n, 1, MILLISECONDS).get());
        assertEquals(n, testCffuFac.newIncompleteCffu().unsafeCompleteOnTimeout(
                n, 1, MILLISECONDS).get());

        assertEquals(n, testCffuFac.completedFuture(n).completeOnTimeout(
                anotherN, 1, MILLISECONDS).get());
        assertEquals(n, testCffuFac.completedFuture(n).unsafeCompleteOnTimeout(
                anotherN, 1, MILLISECONDS).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Advanced Methods(compose* and handle-like methods)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_catchingCompose() throws Exception {
        Cffu<Integer> failed = testCffuFac.failedFuture(rte);

        assertEquals(n, failed.catchingCompose(RuntimeException.class, ex -> completedFuture(n)).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                failed.catchingCompose(IndexOutOfBoundsException.class, ex -> completedFuture(n)).get()
        ).getCause());

        assertEquals(n, failed.catchingComposeAsync(RuntimeException.class, ex -> completedFuture(n)).get());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                failed.catchingComposeAsync(IndexOutOfBoundsException.class, ex -> completedFuture(n)).get()
        ).getCause());

        Cffu<Integer> success = testCffuFac.completedFuture(n);

        assertEquals(n, success.catchingCompose(RuntimeException.class, ex -> completedFuture(anotherN)).get());
        assertEquals(n, success.catchingCompose(IndexOutOfBoundsException.class, ex -> completedFuture(anotherN)).get());

        assertEquals(n, success.catchingComposeAsync(RuntimeException.class, ex -> completedFuture(anotherN)).get());
        assertEquals(n, success.catchingComposeAsync(IndexOutOfBoundsException.class, ex -> completedFuture(anotherN)).get());
    }

    @Test
    void test_peek() throws Exception {
        BiConsumer<Object, Throwable> c = (v, ex) -> {};
        BiConsumer<Object, Throwable> ec = (v, ex) -> {throw anotherRte;};

        Cffu<Object> failed = testCffuFac.failedFuture(rte);
        assertSame(failed.peek(c), failed);
        assertSame(failed.peekAsync(c), failed);
        assertSame(failed.peekAsync(c, testExecutor), failed);
        assertSame(failed.peek(ec), failed);
        assertSame(failed.peekAsync(ec), failed);
        assertSame(failed.peekAsync(ec, testExecutor), failed);

        Cffu<Integer> success = testCffuFac.completedFuture(n);
        assertEquals(n, success.peek(c).get());
        assertEquals(n, success.peekAsync(c).get());
        assertEquals(n, success.peekAsync(c).get());
        assertEquals(n, success.peek(ec).get());
        assertEquals(n, success.peekAsync(ec).get());
        assertEquals(n, success.peekAsync(ec).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Read(explicitly) Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuJoin() {
        // Completed Future
        assertEquals(n, testCffuFac.completedFuture(n).join(1, MILLISECONDS));

        // Incomplete Future -> CompletionException with TimeoutException
        Cffu<Object> incomplete = testCffuFac.newIncompleteCffu();
        assertInstanceOf(TimeoutException.class, assertThrowsExactly(CompletionException.class, () ->
                incomplete.join(1, MILLISECONDS)
        ).getCause());

        // Failed Future -> CompletionException
        Cffu<Object> failed = testCffuFac.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                failed.join(1, MILLISECONDS)
        ).getCause());

        // Incomplete Future -> join before timeout

        Cffu<Integer> cffu = testCffuFac.supplyAsync(() -> {
            snoreZzz();
            return n;
        });
        assertEquals(n, cffu.join(LONG_WAIT_MS, MILLISECONDS));
    }

    @Test
    void test_getSuccessNow() {
        final Consumer<Cffu<Integer>> check = (cf) -> {
            assertNull(cf.getSuccessNow(null));
            assertEquals(n, cf.getSuccessNow(n));
        };

        Cffu<Integer> incomplete = testCffuFac.newIncompleteCffu();
        check.accept(incomplete);

        Cffu<Integer> failed = testCffuFac.failedFuture(rte);
        check.accept(failed);

        incomplete.cancel(false);
        check.accept(incomplete);
    }

    @Test
    void test_cffuState() {
        Cffu<Object> incomplete = testCffuFac.newIncompleteCffu();

        assertEquals(CffuState.RUNNING, incomplete.cffuState());
        assertEquals(CffuState.SUCCESS, testCffuFac.completedFuture(n).cffuState());
        assertEquals(CffuState.FAILED, testCffuFac.failedFuture(rte).cffuState());

        incomplete.cancel(false);
        assertEquals(CffuState.CANCELLED, incomplete.cffuState());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Write Methods
    ////////////////////////////////////////////////////////////////////////////////

    // also tested in CffuApiCompatibilityTest

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Re-Config Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_withCffuFactory() {
        Cffu<Integer> cf = testCffuFac.completedFuture(n);
        assertSame(testCffuFac, cf.cffuFactory());

        assertSame(forbidObtrudeMethodsCffuFactory, cf.withCffuFactory(forbidObtrudeMethodsCffuFactory).cffuFactory());

        Executor executor = Runnable::run;
        final Cffu<Integer> f2 = cf.withDefaultExecutor(executor);
        assertSame(executor, f2.defaultExecutor());
        assertEquals(testCffuFac.forbidObtrudeMethods(), f2.cffuFactory().forbidObtrudeMethods());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Getter Methods of Cffu properties
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_isMinimalStage() {
        Cffu<Integer> cf = testCffuFac.completedFuture(n);
        assertFalse(cf.isMinimalStage());

        assertTrue(((Cffu<Integer>) testCffuFac.completedStage(n)).isMinimalStage());
        assertTrue(((Cffu<Object>) testCffuFac.failedStage(rte)).isMinimalStage());

        assertTrue(((Cffu<Integer>) cf.minimalCompletionStage()).isMinimalStage());

        assertFalse(forbidObtrudeMethodsCffuFactory.completedFuture(n).isMinimalStage());
        assertTrue(((Cffu<Integer>) forbidObtrudeMethodsCffuFactory.completedStage(n)).isMinimalStage());
        assertTrue(((Cffu<Object>) forbidObtrudeMethodsCffuFactory.failedStage(rte)).isMinimalStage());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Inspection Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuUnwrap() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        Cffu<Integer> cffu = testCffuFac.toCffu(cf);

        assertSame(cf, cffu.cffuUnwrap());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_cffuUnwrap_9_completedStage() {
        CompletionStage<Integer> stage = CompletableFuture.completedStage(n);
        Cffu<Integer> cffu = testCffuFac.toCffu(stage);

        assertNotSame(stage, cffu.cffuUnwrap());
        assertEquals(n, cffu.join());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Other Uncommon Methods(dangerous or trivial)
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_forbidObtrudeMethods() {
        assertFalse(testCffuFac.completedFuture(n).forbidObtrudeMethods());
        assertTrue(forbidObtrudeMethodsCffuFactory.completedFuture(n).forbidObtrudeMethods());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Inspection methods of Cffu
    //
    //    - cffuUnwrap()
    //    - getNumberOfDependents()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_toString() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        Cffu<Integer> cffu = testCffuFac.toCffu(cf);

        assertTrue(cffu.toString().contains(cf.toString()));
        assertTrue(cffu.toString().startsWith("Cffu@"));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private final CffuFactory forbidObtrudeMethodsCffuFactory = CffuFactory.builder(testExecutor).forbidObtrudeMethods(true).build();
}
