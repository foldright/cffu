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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static io.foldright.cffu.CffuFactoryTest.*;
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
                    createFailedFuture(rte),
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
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
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
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
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    createFailedFuture(another_rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // failed/incomplete/failed
        try {
            allOfWithResult(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    new CompletableFuture<>()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (TimeoutException expected) {
            // do nothing
        }

        // incomplete fail incomplete
        try {
            allOfWithResult(
                    createIncompleteFuture(),
                    createFailedFuture(rte),
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
                    createFailedFuture(rte),
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
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
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(another_rte, expected.getCause());
        }

        // success and failed
        try {
            allOfFastFailWithResult(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    createFailedFuture(another_rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // failed/incomplete/failed
        try {
            allOfFastFailWithResult(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    new CompletableFuture<>()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // incomplete fail incomplete
        try {
            allOfFastFailWithResult(
                    createIncompleteFuture(),
                    createFailedFuture(rte),
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
                    createFailedFuture(rte),
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
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
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(another_rte, expected.getCause());
        }

        // success and failed
        try {
            allOfFastFail(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    createFailedFuture(another_rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // failed/incomplete/failed
        try {
            allOfFastFail(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    new CompletableFuture<>()
            ).get(30, TimeUnit.MILLISECONDS);

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // incomplete fail incomplete
        try {
            allOfFastFail(
                    createIncompleteFuture(),
                    createFailedFuture(rte),
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
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            // anyOfSuccessWithType: the ex of first complete(in time) cf argument win
            //   ❗dependent on the implementation behavior of `CF.anyOf`️
            assertSame(another_rte, expected.getCause());
        }
        // incomplete fail incomplete
        try {
            anyOfWithType(
                    createIncompleteFuture(),
                    createFailedFuture(rte),
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
                    createFailedFuture(another_rte),
                    createFailedFuture(ex1),
                    createFailedFuture(ex2)
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
                    createFailedFuture(rte),
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
                    return another_n;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOfWithType(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
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
                    createFailedFuture(rte),
                    createFailedFuture(rte)
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
                    return another_n;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOfSuccessWithType(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
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
                createFailedFuture(rte),
                createFailedFuture(rte)
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# combine methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_combine() throws Exception {
        assertEquals(Tuple2.of(n, s), CompletableFutureUtils.combine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), CompletableFutureUtils.combine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, another_n), CompletableFutureUtils.combine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d),
                CompletableFuture.completedFuture(another_n)
        ).get());

        assertEquals(Tuple5.of(n, s, d, another_n, n + n), CompletableFutureUtils.combine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d),
                CompletableFuture.completedFuture(another_n),
                CompletableFuture.completedFuture(n + n)
        ).get());
    }

    @Test
    void test_combine_exceptionally() throws Exception {
        try {
            CompletableFutureUtils.combine(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            CompletableFutureUtils.combine(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            CompletableFutureUtils.combine(
                    CompletableFuture.completedFuture(n),
                    CompletableFuture.completedFuture(d),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    CompletableFuture.completedFuture(another_n)
            ).get();

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
        assertTrue(CompletableFutureUtils.failedFuture(rte).isDone());
        assertEquals(n, CompletableFutureUtils.completedStage(n).toCompletableFuture().get());
        assertTrue(CompletableFutureUtils.failedStage(rte).toCompletableFuture().isDone());
    }

    @Test
    void test_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = CompletableFutureUtils.delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> holder.set(testName), delayer).get();
        assertEquals(testName, holder.get());
    }

    @Test
    void test_exceptionallyAsync() throws Exception {
        CompletableFuture<Integer> cf = CompletableFutureUtils.failedFuture(rte);
        assertEquals(n, CompletableFutureUtils.exceptionallyAsync(cf, ex -> n).get());
    }

    @Test
    void test_timeout() throws Exception {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        try {
            CompletableFutureUtils.orTimeout(cf, 1, TimeUnit.MILLISECONDS).get();
        } catch (ExecutionException expected) {
            assertEquals(TimeoutException.class, expected.getCause().getClass());
        }

        cf = new CompletableFuture<>();
        assertEquals(n, CompletableFutureUtils.completeOnTimeout(cf, n, 1, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void test_exceptionallyCompose() throws Exception {
        CompletableFuture<Object> cf = failedFuture(rte);

        assertEquals(n, CompletableFutureUtils.exceptionallyCompose(cf,
                ex -> CompletableFuture.completedFuture(n)).get());
        assertEquals(n, CompletableFutureUtils.exceptionallyComposeAsync(cf,
                ex -> CompletableFuture.completedFuture(n)).get());
    }

    @Test
    void test_read() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        CompletableFuture<Object> ff = failedFuture(rte);
        assertEquals(n, CompletableFutureUtils.cffuJoin(cf, 1, TimeUnit.MILLISECONDS));
        assertEquals(n, CompletableFutureUtils.resultNow(cf));
        assertSame(rte, CompletableFutureUtils.exceptionNow(ff));
        assertSame(CffuState.SUCCESS, CompletableFutureUtils.cffuState(cf));
        assertSame(CffuState.FAILED, CompletableFutureUtils.cffuState(ff));
    }

    @Test
    void test_write() throws Exception {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        assertEquals(n, CompletableFutureUtils.completeAsync(cf, () -> n).get());
    }

    @Test
    void test_re_config() throws Exception {
        CompletionStage<Integer> mf = minimalCompletionStage(CompletableFuture.completedFuture(n));
        assertEquals(n, mf.toCompletableFuture().get());

        CompletableFuture<Integer> cf = new CompletableFuture<>();
        CompletableFutureUtils.copy(cf).complete(n);
        assertFalse(cf.isDone());

        CompletableFuture<Object> incomplete = newIncompleteFuture(cf);
        assertFalse(incomplete.isDone());
        incomplete.complete(n);
        assertFalse(cf.isDone());
    }

    @Test
    void test_getter() {
        Executor executor = defaultExecutor();
        assertIsDefaultExecutor(executor);
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

    private static ExecutorService executorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool(testName);
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
