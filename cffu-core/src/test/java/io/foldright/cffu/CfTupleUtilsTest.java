package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.completedStage;
import static io.foldright.cffu.CompletableFutureUtils.failedFuture;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.*;


class CfTupleUtilsTest {

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

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
        assertEquals(Tuple2.of(n, s), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, testExecutor).get());

        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, testExecutor).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, testExecutor).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.mSupplyMostSuccessTupleAsync(LONG_WAIT_MS, MILLISECONDS, supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn, testExecutor).get());
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
        assertEquals(Tuple2.of(n, s), CfTupleUtils.mSupplyTupleAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), CfTupleUtils.mSupplyTupleFailFastAsync(supplier_n, supplier_s).get());

        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.mSupplyTupleAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.mSupplyTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.mSupplyTupleFailFastAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
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
        assertEquals(Tuple2.of(n, s), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s).get());
        assertEquals(Tuple2.of(n, s), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, testExecutor).get());

        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d).get());
        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, testExecutor).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, testExecutor).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.mSupplyAllSuccessTupleAsync(supplier_n, supplier_s, supplier_d, supplier_an, supplier_nn, testExecutor).get());
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

        assertEquals(Tuple2.of(n, s), CfTupleUtils.allTupleOf(cf_n, cf_s).get());
        assertEquals(Tuple2.of(n, s), CfTupleUtils.allTupleFailFastOf(cf_n, cf_s).get());

        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.allTupleOf(cf_n, cf_s, cf_d).get());
        assertEquals(Tuple3.of(n, s, d), CfTupleUtils.allTupleFailFastOf(cf_n, cf_s, cf_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.allTupleOf(cf_n, cf_s, cf_d, cf_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), CfTupleUtils.allTupleFailFastOf(cf_n, cf_s, cf_d, cf_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.allTupleOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CfTupleUtils.allTupleFailFastOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
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
                CfTupleUtils.allTupleOf(cf_n, fail).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleFailFastOf(incomplete, fail).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleOf(cf_n, fail, cf_s).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleFailFastOf(incomplete, fail, cf_s).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleOf(cf_n, fail, cf_d, cf_s).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleFailFastOf(incomplete, fail, cf_d, cf_s).get()
        ).getCause());

        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleOf(cf_n, cf_d, fail, cf_s, cf_an).get()
        ).getCause());
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                CfTupleUtils.allTupleFailFastOf(incomplete, cf_d, fail, cf_s, cf_an).get()
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
                CfTupleUtils.allTupleOf(incomplete, fail).get(SHORT_WAIT_MS, MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                CfTupleUtils.allTupleOf(incomplete, fail, cf_s).get(SHORT_WAIT_MS, MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                CfTupleUtils.allTupleOf(incomplete, fail, cf_d, cf_s).get(SHORT_WAIT_MS, MILLISECONDS)
        );
        assertThrowsExactly(TimeoutException.class, () ->
                CfTupleUtils.allTupleOf(incomplete, cf_d, fail, cf_s, cf_an).get(SHORT_WAIT_MS, MILLISECONDS)
        );
    }

    @Test
    void test_mostSuccessTupleOf() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final CompletionStage<String> anotherCompleted = completedStage(s);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cancelled = cancelledFuture();
        final CompletableFuture<Integer> incomplete = incompleteCf();

        assertEquals(Tuple2.of(n, s), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), CfTupleUtils.mostSuccessTupleOf(
                SHORT_WAIT_MS, MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());

        // with `executorWhenTimeout`

        assertEquals(Tuple2.of(n, s), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), CfTupleUtils.mostSuccessTupleOf(
                testExecutor, SHORT_WAIT_MS, MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions-Tuple(thenMTuple*) Methods
    ////////////////////////////////////////////////////////////

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

        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, testExecutor).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, testExecutor).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, function_an, testExecutor).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyTupleFailFastAsync(completed, function_n, function_s, function_d, function_an, function_nn, testExecutor).get());

        // thenMApplyAllSuccessTupleAsync

        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, testExecutor).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, testExecutor).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, function_an, testExecutor).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyAllSuccessTupleAsync(completed, function_n, function_s, function_d, function_an, function_nn, testExecutor).get());

        // thenMApplyMostSuccessTupleAsync

        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, testExecutor).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, testExecutor).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, testExecutor).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyMostSuccessTupleAsync(completed, LONG_WAIT_MS, MILLISECONDS, function_n, function_s, function_d, function_an, function_nn, testExecutor).get());

        //  thenMApplyTupleAsync

        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s).get());
        assertEquals(Tuple2.of(n + n, s + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, testExecutor).get());

        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, function_d).get());
        assertEquals(Tuple3.of(n + n, s + n, d + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, function_d, testExecutor).get());

        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, function_d, function_an).get());
        assertEquals(Tuple4.of(n + n, s + n, d + n, anotherN + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, function_d, function_an, testExecutor).get());

        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, function_d, function_an, function_nn).get());
        assertEquals(Tuple5.of(n + n, s + n, d + n, anotherN + n, n + n), CfTupleUtils.thenMApplyTupleAsync(completed, function_n, function_s, function_d, function_an, function_nn, testExecutor).get());
    }
}
