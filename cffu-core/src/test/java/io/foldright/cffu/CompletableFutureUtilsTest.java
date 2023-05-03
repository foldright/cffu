package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static io.foldright.cffu.CompletableFutureUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.junit.jupiter.api.Assertions.*;


class CompletableFutureUtilsTest {
    ////////////////////////////////////////////////////////////////////////////////
    //# allOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allOf__success__trivial_case() throws Exception {
        assertEquals(Arrays.asList(n, n + 1, n + 2), allOfWithResult(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allOfWithResult(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allOfWithResult(
                CompletableFuture.completedFuture(n)
        ).get());

        assertEquals(Collections.emptyList(), allOfWithResult().get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Arrays.asList(n, n + 1, n + 2), allOfFastFailWithResult(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allOfFastFailWithResult(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allOfFastFailWithResult(
                CompletableFuture.completedFuture(n)
        ).get());

        assertEquals(Collections.emptyList(), allOfFastFailWithResult().get());

        ////////////////////////////////////////////////////////////////////////////////

        assertNull(allOfFastFail(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());

        assertNull(allOfFastFail(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1)
        ).get());

        assertNull(allOfFastFail(
                CompletableFuture.completedFuture(n)
        ).get());

        assertNull(allOfFastFail().get());
    }

    @Test
    void test_allOf__exceptionally() throws Exception {
        // all failed
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            allOfWithResult(
                    failedFuture(rte),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first given cf argument win
            //   ❗dependent on the implementation behavior of `CF.allOf`️
            assertSame(rte, expected.getCause());
        }

        // all failed - concurrent
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            allOfWithResult(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        throw rte;
                    }),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first given cf argument win
            //   ❗dependent on the implementation behavior of `CF.allOf`️
            assertSame(rte, expected.getCause());
        }

        // success and failed
        try {
            allOfWithResult(
                    CompletableFuture.completedFuture(n),
                    failedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    failedFuture(anotherRte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // failed/incomplete/failed
        try {
            allOfWithResult(
                    CompletableFuture.completedFuture(n),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (TimeoutException expected) {
            // do nothing
        }

        // incomplete fail incomplete
        try {
            allOfWithResult(
                    createIncompleteFuture(),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(100, TimeUnit.MILLISECONDS);

            fail();
        } catch (TimeoutException expected) {
            // do nothing
        }

        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            allOfFastFailWithResult(
                    failedFuture(rte),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(rte, expected.getCause());
        }

        // all failed - concurrent
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            allOfFastFailWithResult(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        throw rte;
                    }),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(anotherRte, expected.getCause());
        }

        // success and failed
        try {
            allOfFastFailWithResult(
                    CompletableFuture.completedFuture(n),
                    failedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    failedFuture(anotherRte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // failed/incomplete/failed
        try {
            allOfFastFailWithResult(
                    CompletableFuture.completedFuture(n),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // incomplete fail incomplete
        try {
            allOfFastFailWithResult(
                    createIncompleteFuture(),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(100, TimeUnit.MILLISECONDS);

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            allOfFastFailWithResult(
                    failedFuture(rte),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(rte, expected.getCause());
        }

        // all failed - concurrent
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            allOfFastFail(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        throw rte;
                    }),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(anotherRte, expected.getCause());
        }

        // success and failed
        try {
            allOfFastFail(
                    CompletableFuture.completedFuture(n),
                    failedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    failedFuture(anotherRte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // failed/incomplete/failed
        try {
            allOfFastFail(
                    CompletableFuture.completedFuture(n),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // incomplete fail incomplete
        try {
            allOfFastFail(
                    createIncompleteFuture(),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(100, TimeUnit.MILLISECONDS);

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_anyOf__success__trivial_case() throws Exception {
        assertEquals(n, anyOfWithType(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());
        assertEquals(n, anyOfWithType(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1)
        ).get());

        assertEquals(n, anyOfWithType(
                CompletableFuture.completedFuture(n)
        ).get());
        assertFalse(anyOfWithType().isDone());

        // success with incomplete CF
        assertEquals(n, anyOfWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n)
        ).get());

        ////////////////////////////////////////

        assertEquals(n, anyOfSuccessWithType(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());
        assertEquals(n, anyOfSuccessWithType(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1)
        ).get());

        assertEquals(n, anyOfSuccessWithType(
                CompletableFuture.completedFuture(n)
        ).get());
        try {
            anyOfSuccessWithType().get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(NoCfsProvidedException.class, expected.getCause().getClass());
        }

        // success with incomplete CF
        assertEquals(n, anyOfSuccessWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n)
        ).get());

        ////////////////////////////////////////

        assertEquals(n, anyOfSuccess(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());
        assertEquals(n, anyOfSuccess(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1)
        ).get());

        assertEquals(n, anyOfSuccess(
                CompletableFuture.completedFuture(n)
        ).get());
        try {
            anyOfSuccess().get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(NoCfsProvidedException.class, expected.getCause().getClass());
        }

        // success with incomplete CF
        assertEquals(n, anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n)
        ).get());

    }

    @Test
    void test_anyOf__exceptionally() throws Exception {
        // NOTE: skip anyOfSuccess test intended
        //       same implementation with anyOfSuccessWithType

        ////////////////////////////////////////

        // all failed
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            anyOfWithType(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        throw rte;
                    }),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(anotherRte, expected.getCause());
        }
        // incomplete fail incomplete
        try {
            anyOfWithType(
                    createIncompleteFuture(),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        ////////////////////////////////////////

        // all failed
        try {
            RuntimeException ex1 = new RuntimeException("ex1");
            RuntimeException ex2 = new RuntimeException("ex2");
            anyOfSuccessWithType(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        throw rte;
                    }),
                    failedFuture(anotherRte),
                    failedFuture(ex1),
                    failedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first given cf argument win
            //   ❗dependent on the implementation behavior of `CF.allOf`️
            assertSame(rte, expected.getCause());
        }
        // incomplete fail incomplete
        try {
            anyOfSuccessWithType(
                    createIncompleteFuture(),
                    failedFuture(rte),
                    createIncompleteFuture()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (TimeoutException expected) {
            // do nothing
        }
    }

    @Test
    void test_anyOf__concurrent() throws Exception {
        // NOTE: skip anyOfSuccess test intended
        //       same implementation with anyOfSuccessWithType

        ////////////////////////////////////////

        // incomplete/wait-success then success
        assertEquals(n, anyOfWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOfWithType(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // success then failed
        assertEquals(n, anyOfWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // failed then success
        try {
            anyOfWithType(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        return n;
                    }),
                    failedFuture(rte),
                    failedFuture(rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        ////////////////////////////////////////

        // incomplete/wait-success then success
        assertEquals(n, anyOfSuccessWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOfSuccessWithType(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // success then failed
        assertEquals(n, anyOfSuccessWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // failed then success
        assertEquals(n, anyOfSuccessWithType(
                CompletableFuture.supplyAsync(() -> {
                    sleep(100);
                    return n;
                }),
                failedFuture(rte),
                failedFuture(rte)
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# combine methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_combine() throws Exception {
        final CompletableFuture<Integer> cf_n = CompletableFuture.completedFuture(n);
        final CompletableFuture<String> cf_s = CompletableFuture.completedFuture(s);
        final CompletableFuture<Double> cf_d = CompletableFuture.completedFuture(d);
        final CompletableFuture<Integer> cf_an = CompletableFuture.completedFuture(anotherN);
        final CompletableFuture<Integer> cf_nn = CompletableFuture.completedFuture(n + n);

        assertEquals(Tuple2.of(n, s), CompletableFutureUtils.combine(cf_n, cf_s).get());
        assertEquals(Tuple2.of(n, s), CompletableFutureUtils.combineFastFail(cf_n, cf_s).get());

        assertEquals(Tuple3.of(n, s, d), CompletableFutureUtils.combine(cf_n, cf_s, cf_d).get());
        assertEquals(Tuple3.of(n, s, d), CompletableFutureUtils.combineFastFail(cf_n, cf_s, cf_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), CompletableFutureUtils.combine(cf_n, cf_s, cf_d, cf_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), CompletableFutureUtils.combineFastFail(cf_n, cf_s, cf_d, cf_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CompletableFutureUtils.combine(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), CompletableFutureUtils.combineFastFail(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
    }

    @Test
    void test_combine_exceptionally() throws Exception {
        final CompletableFuture<Object> incomplete = new CompletableFuture<>();
        final CompletableFuture<Object> fail = CompletableFutureUtils.failedFuture(rte);

        final CompletableFuture<Integer> cf_n = CompletableFuture.completedFuture(n);
        final CompletableFuture<String> cf_s = CompletableFuture.completedFuture(s);
        final CompletableFuture<Double> cf_d = CompletableFuture.completedFuture(d);
        final CompletableFuture<Integer> cf_an = CompletableFuture.completedFuture(anotherN);

        try {
            CompletableFutureUtils.combine(cf_n, fail).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            CompletableFutureUtils.combineFastFail(incomplete, fail).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            CompletableFutureUtils.combine(cf_n, fail, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            CompletableFutureUtils.combineFastFail(incomplete, fail, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            CompletableFutureUtils.combine(cf_n, fail, cf_d, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            CompletableFutureUtils.combineFastFail(incomplete, fail, cf_d, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            CompletableFutureUtils.combine(cf_n, cf_d, fail, cf_s, cf_an).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            CompletableFutureUtils.combineFastFail(incomplete, cf_d, fail, cf_s, cf_an).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
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

        cf = CompletableFuture.completedFuture(n);
        assertEquals(n, exceptionallyAsync(cf, ex -> anotherN).get());

    }

    @Test
    void test_timeout() throws Exception {
        CompletableFuture<Integer> cf = createIncompleteFuture();
        try {
            orTimeout(cf, 1, TimeUnit.MILLISECONDS).get();
        } catch (ExecutionException expected) {
            assertEquals(TimeoutException.class, expected.getCause().getClass());
        }

        cf = createIncompleteFuture();
        assertEquals(n, completeOnTimeout(cf, n, 1, TimeUnit.MILLISECONDS).get());

        cf = CompletableFuture.completedFuture(n);
        assertEquals(n, completeOnTimeout(cf, anotherN, 1, TimeUnit.MILLISECONDS).get());

        cf = CompletableFuture.completedFuture(n);
        assertEquals(n, orTimeout(cf, 1, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void test_exceptionallyCompose() throws Exception {
        CompletableFuture<Object> completed = CompletableFuture.completedFuture(n);
        CompletableFuture<Object> failed = failedFuture(rte);

        assertEquals(n, exceptionallyCompose(failed,
                ex -> CompletableFuture.completedFuture(n)).get());
        assertEquals(n, exceptionallyCompose(completed,
                ex -> CompletableFuture.completedFuture(anotherN)).get());

        assertEquals(n, exceptionallyComposeAsync(failed,
                ex -> CompletableFuture.completedFuture(n)).get());
        assertEquals(n, exceptionallyComposeAsync(completed,
                ex -> CompletableFuture.completedFuture(anotherN)).get());
    }

    @Test
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ThrowableNotThrown"})
    void test_read() {
        final CompletableFuture<Integer> completed = CompletableFuture.completedFuture(n);

        assertEquals(n, cffuJoin(completed, 1, TimeUnit.MILLISECONDS));
        assertEquals(n, resultNow(completed));
        try {
            exceptionNow(completed);
            fail();
        } catch (IllegalStateException expected) {
            if (expected.getMessage() != null) assertEquals("Task completed with a result", expected.getMessage());
        }
        assertSame(CffuState.SUCCESS, cffuState(completed));

        ////////////////////////////////////////

        final CompletableFuture<Object> failed = failedFuture(rte);

        try {
            cffuJoin(failed, 1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            resultNow(failed);
            fail();
        } catch (IllegalStateException expected) {
            if (expected.getMessage() != null) assertEquals("Task completed with exception", expected.getMessage());
        }
        assertSame(rte, exceptionNow(failed));
        assertSame(CffuState.FAILED, cffuState(failed));

        ////////////////////////////////////////

        CompletableFuture<Object> cancelled = createCancelledFuture();
        try {
            resultNow(cancelled);
            fail();
        } catch (IllegalStateException expected) {
            if (expected.getMessage() != null) assertEquals("Task was cancelled", expected.getMessage());
        }
        try {
            exceptionNow(cancelled);
            fail();
        } catch (IllegalStateException expected) {
            if (expected.getMessage() != null) assertEquals("Task was cancelled", expected.getMessage());
        }
        assertSame(CffuState.CANCELLED, cffuState(cancelled));

        ////////////////////////////////////////

        final CompletableFuture<Object> incomplete = createIncompleteFuture();

        try {
            cffuJoin(incomplete, 1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CompletionException expected) {
            assertTrue(expected.getCause() instanceof TimeoutException);
        }
        try {
            resultNow(incomplete);
            fail();
        } catch (IllegalStateException expected) {
            if (expected.getMessage() != null) assertEquals("Task has not completed", expected.getMessage());
        }
        try {
            exceptionNow(incomplete);
            fail();
        } catch (IllegalStateException expected) {
            if (expected.getMessage() != null) assertEquals("Task has not completed", expected.getMessage());
        }
        assertSame(CffuState.RUNNING, cffuState(incomplete));

        // Incomplete Future -> join before timeout
        CompletableFuture<Integer> later = createFutureCompleteLater(n);
        assertEquals(n, cffuJoin(later, 3, TimeUnit.SECONDS));
    }

    @Test
    void test_write() throws Exception {
        assertEquals(n, completeAsync(createIncompleteFuture(), () -> n).get());
        assertEquals(n, completeAsync(createIncompleteFuture(), () -> n, commonPool()).get());
        try {
            completeAsync(createIncompleteFuture(), () -> {
                throw rte;
            }).get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        CompletableFuture<Integer> completed = CompletableFuture.completedFuture(n);
        assertEquals(n, completeAsync(completed, () -> anotherN).get());
    }

    @Test
    void test_re_config() throws Exception {
        CompletionStage<Integer> mf = minimalCompletionStage(CompletableFuture.completedFuture(n));
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
    //# test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private static void assertIsDefaultExecutor(Executor executor) {
        final boolean USE_COMMON_POOL = (ForkJoinPool.getCommonPoolParallelism() > 1);
        if (USE_COMMON_POOL) {
            assertSame(commonPool(), executor);
        } else {
            String executorClassName = executor.getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }
    }

    private static final String testName = "CompletableFutureUtilsTest";
}
