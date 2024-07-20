package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;

import static io.foldright.cffu.ListenableFutureUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("RedundantThrows")
class ListenableFutureUtilsTest {
    @Test
    void test_toCompletableFuture() throws Exception {
        ListenableFuture<Integer> lf = Futures.immediateFuture(n);
        assertEquals(n, toCompletableFuture(lf).get());
        final CompletableFuture<Integer> cf = toCompletableFuture(lf, executorService);
        assertEquals(n, cf.get());
        assertTrue(cf.toString().startsWith(
                "CompletableFutureAdapter@ListenableFutureUtils.toCompletableFuture of ListenableFuture(" + lf + "), ")
        );

        ListenableFuture<Integer> failed = Futures.immediateFailedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toCompletableFuture(failed).get()
        ).getCause());
    }

    @Test
    void test_toCffu() throws Exception {
        ListenableFuture<Integer> lf = Futures.immediateFuture(n);
        assertEquals(n, toCffu(lf, cffuFactory).get());

        ListenableFuture<Integer> failed = Futures.immediateFailedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toCffu(failed, cffuFactory).get()
        ).getCause());
    }

    @Test
    void test_toListenableFuture() throws Exception {
        CompletableFuture<Integer> cf = completedFuture(n);

        ListenableFuture<Integer> lf = toListenableFuture(cf);
        assertEquals(n, lf.get());
        assertEquals(n, lf.get(10, TimeUnit.MILLISECONDS));
        assertTrue(lf.isDone());
        assertEquals("ListenableFutureAdapter@ListenableFutureUtils.toListenableFuture of " + cf, lf.toString());

        FutureTask<Integer> task = new FutureTask<>(() -> anotherN);
        lf.addListener(task, executorService);
        assertEquals(anotherN, task.get());

        CompletableFuture<Integer> failed = CompletableFutureUtils.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toListenableFuture(failed).get()
        ).getCause());

        cf = new CompletableFuture<>();
        lf = toListenableFuture(cf);
        lf.cancel(false);
        assertTrue(lf.isCancelled());
        assertTrue(cf.isCancelled());

        Cffu<Integer> cffu = cffuFactory.completedFuture(n);
        assertEquals(n, toListenableFuture(cffu).get());

        Cffu<Integer> failedCffu = cffuFactory.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toListenableFuture(failedCffu).get()
        ).getCause());
    }

    @Test
    void test_toListenableFuture_exception() {
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                toListenableFuture((Cffu<Integer>) cffuFactory.completedStage(n))
        );
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_toListenableFuture_exception_java9plus() {
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                toListenableFuture((CompletableFuture<Integer>) CompletableFuture.completedStage(n))
        );
    }

    ////////////////////////////////////////////////////////////////////////////////
    // region# cancellation and propagation
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_lf2cf_cancellationAndPropagation() throws Exception {
        final ListenableFuture<Integer> lf = SettableFuture.create();
        final CompletableFuture<Integer> cf = toCompletableFuture(lf);

        assertTrue(cf.cancel(false));
        waitForAllCfsToComplete(cf);
        waitForAllLfsToComplete(lf);

        assertTrue(lf.isCancelled());
        assertThrowsExactly(CancellationException.class, lf::get);
        assertTrue(cf.isCancelled());
        assertThrowsExactly(CancellationException.class, cf::get);
    }

    @Test
    void test_lf2cf_setCancellationExceptionToCf_cancellationAndPropagation() throws Exception {
        final ListenableFuture<Integer> lf = SettableFuture.create();
        final CompletableFuture<Integer> cf = toCompletableFuture(lf);

        assertTrue(cf.completeExceptionally(new CancellationException()));
        waitForAllCfsToComplete(cf);
        waitForAllLfsToComplete(lf);

        assertTrue(lf.isCancelled());
        assertThrowsExactly(CancellationException.class, lf::get);
        assertTrue(cf.isCancelled());
        assertThrowsExactly(CancellationException.class, cf::get);
    }

    @Test
    void test_cf2lf_cancellationAndPropagation() throws Exception {
        final CompletableFuture<Integer> cf = new CompletableFuture<>();
        final ListenableFuture<Integer> lf = toListenableFuture(cf);

        assertTrue(lf.cancel(false));
        waitForAllLfsToComplete(lf);
        waitForAllCfsToComplete(cf);

        assertTrue(cf.isCancelled());
        assertThrowsExactly(CancellationException.class, cf::get);
        assertTrue(lf.isCancelled());
        assertThrowsExactly(CancellationException.class, lf::get);
    }

    @Test
    void showCase_ListenableFuture_cancellationAndPropagation() throws Exception {
        final ListenableFuture<Integer> lf = SettableFuture.create();
        final ListenableFuture<Integer> transform = Futures.transform(lf, x -> x + 1, executorService);

        assertTrue(lf.cancel(false));
        waitForAllLfsToComplete(lf, transform);

        // ✅ ListenableFuture support the cancellation
        assertTrue(lf.isCancelled());
        assertThrowsExactly(CancellationException.class, lf::get);
        // ✅ ListenableFuture support the propagation of cancellation
        assertTrue(transform.isCancelled());
        assertThrowsExactly(CancellationException.class, transform::get);
    }

    @Test
    void showCase_CompletableFuture_cancellationAndPropagation() throws Exception {
        final CompletableFuture<Integer> cf = new CompletableFuture<>();
        final CompletableFuture<Integer> transform = cf.thenApplyAsync(x -> x + 1, executorService);

        assertTrue(cf.cancel(false));
        waitForAllCfsToComplete(cf, transform);

        // ✅ CompletableFuture support the cancellation
        assertTrue(cf.isCancelled());
        assertThrowsExactly(CancellationException.class, cf::get);
        // 🚫 CompletableFuture does NOT SUPPORT the propagation of cancellation
        //    the CancellationException is wrapped by CompletionException or ExecutionException
        assertFalse(transform.isCancelled());
        final ExecutionException ce = assertThrowsExactly(ExecutionException.class, transform::get);
        assertInstanceOf(CancellationException.class, ce.getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper methods/fields
    ////////////////////////////////////////////////////////////////////////////////

    void waitForAllLfsToComplete(ListenableFuture<?>... lfs) throws Exception {
        for (ListenableFuture<?> lf : lfs) {
            try {
                lf.get(2, TimeUnit.SECONDS);
            } catch (TimeoutException ex) {
                throw ex;
            } catch (Exception ignored) {
            }
        }
    }

    void waitForAllCfsToComplete(CompletableFuture<?>... cfs) throws Exception {
        CompletableFutureUtils.mostSuccessResultsOf(null, 2, TimeUnit.SECONDS, cfs).join();
    }

    private static ExecutorService executorService;

    private static CffuFactory cffuFactory;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("ListenableFutureUtilsTest");

        cffuFactory = CffuFactory.builder(executorService).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
