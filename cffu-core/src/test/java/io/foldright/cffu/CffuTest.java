package io.foldright.cffu;

import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder;
import static io.foldright.test_utils.TestUtils.*;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


/**
 * NOTE:
 * <p>
 * Use {@code java} code to test the api usage problem of {@link Cffu};
 * Do NOT rewrite to {@code kotlin}.
 */
class CffuTest {
    private static CffuFactory cffuFactory;

    private static CffuFactory forbidObtrudeMethodsCffuFactory;

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
        try {
            cf.runAfterBothFastFail(failed, runnable).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cf.runAfterBothFastFailAsync(failed, runnable).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cf.runAfterBothFastFailAsync(failed, runnable, executorService).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        try {
            cf.thenAcceptBothFastFail(failed, bc).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cf.thenAcceptBothFastFailAsync(failed, bc).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cf.thenAcceptBothFastFailAsync(failed, bc, executorService).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cf.thenCombineFastFail(failed, Integer::sum).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cf.thenCombineFastFailAsync(failed, Integer::sum).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cf.thenCombineFastFailAsync(failed, Integer::sum, executorService).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
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
    // tested in CffuApiCompatibilityTest
    ////////////////////////////////////////

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
        try {
            incomplete.join(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CompletionException expected) {
            assertEquals(TimeoutException.class, expected.getCause().getClass());
        }

        // Failed Future -> CompletionException
        Cffu<Object> failed = cffuFactory.failedFuture(rte);
        try {
            failed.join(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }

        // Incomplete Future -> join before timeout

        Cffu<Integer> cffu = cffuFactory.supplyAsync(() -> {
            sleep(300);
            return 42;
        });
        assertEquals(42, cffu.join(3, TimeUnit.SECONDS));
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

        assertSame(stage, cffu.cffuUnwrap());
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

        cffuFactory = newCffuFactoryBuilder(executorService).build();
        forbidObtrudeMethodsCffuFactory = newCffuFactoryBuilder(executorService)
                .forbidObtrudeMethods(true).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
