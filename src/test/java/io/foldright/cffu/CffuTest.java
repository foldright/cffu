package io.foldright.cffu;

import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;

import static io.foldright.cffu.CffuFactoryTest.n;
import static io.foldright.cffu.CffuFactoryTest.rte;
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

    ////////////////////////////////////////
    // timeout control
    ////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    //# Read(explicitly) methods
    //
    //    - get()               // BLOCKING
    //    - get(timeout, unit)  // BLOCKING
    //    - join()              // BLOCKING
    //    - cffuJoin()          // BLOCKING
    //    - getNow(T valueIfAbsent)
    //    - resultNow()
    //    - exceptionNow()
    //
    //    - isDone()
    //    - isCompletedExceptionally()
    //    - isCancelled()
    //    - state()
    //
    // NOTE about ExecutionException or CompletionException when the computation threw an exception:
    //   - get methods throw ExecutionException(checked exception)
    //     these old methods existed in `Future` interface since Java 5
    //   - getNow/join throw CompletionException(unchecked exception),
    //     these new methods existed in `CompletableFuture` since Java 8
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuJoin() {
        assertEquals(n, cffuFactory.completedFuture(n).cffuJoin(1, TimeUnit.MILLISECONDS));

        Cffu<Object> incomplete = cffuFactory.newIncompleteCffu();
        try {
            incomplete.cffuJoin(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CompletionException expected) {
            assertEquals(TimeoutException.class, expected.getCause().getClass());
        }

        Cffu<Object> failed = cffuFactory.failedFuture(rte);
        try {
            failed.cffuJoin(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }
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

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //    vs. user functional API
    //
    //    - toCompletableFuture()
    //    - cffuUnwrap()
    //    - copy()
    //
    //    - obtrudeValue(value)
    //    - obtrudeException(ex)
    //
    //    - defaultExecutor()
    //    - getNumberOfDependents()
    //
    //    - newIncompleteFuture()
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuUnwrap() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        Cffu<Integer> cffu = cffuFactory.asCffu(cf);

        assertSame(cf, cffu.cffuUnwrap());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_cffuUnwrap_9_completedStage() {
        CompletionStage<Integer> stage = CompletableFuture.completedStage(n);
        Cffu<Integer> cffu = cffuFactory.asCffu(stage);

        assertSame(stage, cffu.cffuUnwrap());
    }

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

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_Java9_CompletableFuture_failedStage_asCffu() {
        assertFalse(cffuFactory.asCffu(CompletableFuture.failedFuture(rte)).isMinimalStage());

        assertTrue(cffuFactory.asCffu(CompletableFuture.completedStage(42)).isMinimalStage());
        assertTrue(cffuFactory.asCffu(CompletableFuture.failedStage(rte)).isMinimalStage());
    }

    @Test
    void test_toString() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        Cffu<Integer> cffu = cffuFactory.asCffu(cf);

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

        cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(executorService).build();
        forbidObtrudeMethodsCffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(executorService)
                .forbidObtrudeMethods(true).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
