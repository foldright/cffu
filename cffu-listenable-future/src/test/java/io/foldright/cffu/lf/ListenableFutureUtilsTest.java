package io.foldright.cffu.lf;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SettableFuture;
import io.foldright.cffu.Cffu;
import io.foldright.cffu.CompletableFutureUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static io.foldright.cffu.lf.ListenableFutureUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.testCffuFac;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("RedundantThrows")
class ListenableFutureUtilsTest {
    @Test
    void test_toCompletableFuture() throws Exception {
        final ListenableFuture<Integer> lf = Futures.immediateFuture(n);
        final CompletableFuture<Integer> cf = toCompletableFuture(lf, testExecutor, true);
        assertEquals(n, cf.get());
        assertTrue(cf.toString().startsWith(
                "CompletableFutureAdapter@ListenableFutureUtils.toCompletableFuture of ListenableFuture(" + lf + "), ")
        );

        ListenableFuture<Integer> failed = Futures.immediateFailedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toCompletableFuture(failed, testExecutor, true).get()
        ).getCause());
    }

    @Test
    void test_toCffu() throws Exception {
        ListenableFuture<Integer> lf = Futures.immediateFuture(n);
        assertEquals(n, toCffu(lf, testCffuFac, true).get());

        ListenableFuture<Integer> failed = Futures.immediateFailedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toCffu(failed, testCffuFac, true).get()
        ).getCause());
    }

    @Test
    void test_toListenableFuture() throws Exception {
        CompletableFuture<Integer> cf = completedFuture(n);

        ListenableFuture<Integer> lf = toListenableFuture(cf);
        assertEquals(n, lf.get());
        assertEquals(n, lf.get(SHORT_WAIT_MS, MILLISECONDS));
        assertTrue(lf.isDone());
        assertEquals("ListenableFutureAdapter@ListenableFutureUtils.toListenableFuture of " + cf, lf.toString());

        FutureTask<Integer> task = new FutureTask<>(() -> anotherN);
        lf.addListener(task, testExecutor);
        assertEquals(anotherN, task.get());

        CompletableFuture<Integer> failed = CompletableFutureUtils.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toListenableFuture(failed).get()
        ).getCause());

        cf = incompleteCf();
        lf = toListenableFuture(cf);
        lf.cancel(false);
        assertTrue(lf.isCancelled());
        assertTrue(cf.isCancelled());

        Cffu<Integer> cffu = testCffuFac.completedFuture(n);
        assertEquals(n, toListenableFuture(cffu).get());

        Cffu<Integer> failedCffu = testCffuFac.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toListenableFuture(failedCffu).get()
        ).getCause());
    }

    @Test
    void test_toListenableFuture_exception() {
        assertThrowsExactly(UnsupportedOperationException.class, () ->
                toListenableFuture((Cffu<Integer>) testCffuFac.completedStage(n))
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
        final CompletableFuture<Integer> cf = toCompletableFuture(lf, testExecutor, true);

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
        {
            final ListenableFuture<Integer> lf = SettableFuture.create();
            final CompletableFuture<Integer> cf = toCompletableFuture(lf, testExecutor, true);

            assertTrue(cf.completeExceptionally(new CancellationException()));
            waitForAllCfsToComplete(cf);
            waitForAllLfsToComplete(lf);

            assertTrue(lf.isCancelled());
            assertThrowsExactly(CancellationException.class, lf::get);
            assertTrue(cf.isCancelled());
            assertThrowsExactly(CancellationException.class, cf::get);
        }
        // check interruption happened
        {
            final AtomicBoolean interrupted = new AtomicBoolean(false);
            final ListenableFuture<Integer> lf = Futures.submit(() -> {
                try {
                    Thread.sleep(LONG_WAIT_MS);
                } catch (InterruptedException ex) {
                    interrupted.set(true);
                }
                return n;
            }, testExecutor);
            final CompletableFuture<Integer> cf = toCompletableFuture(lf, testExecutor, true);

            // need nap to ensure Lf execution started before cancellation
            // it's ok for testing code...
            nap();
            assertTrue(cf.completeExceptionally(new CancellationException()));
            waitForAllCfsToComplete(cf);
            waitForAllLfsToComplete(lf);

            assertTrue(lf.isCancelled());
            assertThrowsExactly(CancellationException.class, lf::get);
            assertTrue(cf.isCancelled());
            assertThrowsExactly(CancellationException.class, cf::get);

            // need nap for interruption check
            // it's ok for testing code...
            snoreZzz();
            assertTrue(interrupted.get());
        }

        {
            final ListenableFuture<Integer> lf = SettableFuture.create();
            final CompletableFuture<Integer> cf = toCompletableFuture(lf, testExecutor, true);

            // completeExceptionally with non-CancellationException
            assertTrue(cf.completeExceptionally(new IllegalArgumentException()));
            snoreZzz();
            waitForAllCfsToComplete(cf);

            assertFalse(lf.isDone());
        }
    }

    @Test
    void test_lf2cf_dependentLf() throws Exception {
        {
            CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> n, testExecutor);
            CompletableFuture<Integer> cfWrapperOfLf = cf.thenCompose(v -> {
                ListenableFuture<Integer> lf = Futures.submit(() -> v + 1, testExecutor);
                return toCompletableFuture(lf, MoreExecutors.directExecutor(), true);
            });
            assertEquals(n + 1, cfWrapperOfLf.join());
        }
        {
            final ListenableFuture<Integer> lf = SettableFuture.create();
            CompletableFuture<Integer> cf = incompleteCf();

            CompletableFuture<Integer> cfWrapperOfLf = cf.thenCompose(v ->
                    toCompletableFuture(lf, MoreExecutors.directExecutor(), true));

            cf.cancel(false);
            nap();

            assertTrue(cf.isCancelled());
            assertThrowsExactly(CancellationException.class, cf::get);
            assertFalse(cfWrapperOfLf.isCancelled());
            final ExecutionException ee = assertThrowsExactly(ExecutionException.class, cfWrapperOfLf::get);
            assertInstanceOf(CancellationException.class, ee.getCause());

            assertFalse(lf.isDone());
        }
    }

    @Test
    void test_cf2lf_cancellationAndPropagation() throws Exception {
        final CompletableFuture<Integer> cf = incompleteCf();
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
        final ListenableFuture<Integer> transform = Futures.transform(lf, x -> x + 1, testExecutor);

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
        final CompletableFuture<Integer> cf = incompleteCf();
        final CompletableFuture<Integer> transform = cf.thenApplyAsync(x -> x + 1, testExecutor);

        assertTrue(cf.cancel(false));
        waitForAllCfsToComplete(cf, transform);

        // ✅ CompletableFuture support the cancellation
        assertTrue(cf.isCancelled());
        assertThrowsExactly(CancellationException.class, cf::get);
        // 🚫 CompletableFuture does NOT SUPPORT the propagation of cancellation
        //    the CancellationException is wrapped by CompletionException or ExecutionException
        assertFalse(transform.isCancelled());
        final ExecutionException ee = assertThrowsExactly(ExecutionException.class, transform::get);
        assertInstanceOf(CancellationException.class, ee.getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper methods/fields
    ////////////////////////////////////////////////////////////////////////////////

    static void waitForAllLfsToComplete(ListenableFuture<?>... lfs) throws Exception {
        for (ListenableFuture<?> lf : lfs) {
            try {
                lf.get(LONG_WAIT_MS, MILLISECONDS);
            } catch (TimeoutException ex) {
                throw ex;
            } catch (Exception ignored) {}
        }
    }

    static void waitForAllCfsToComplete(CompletableFuture<?>... cfs) throws Exception {
        CompletableFutureUtils.mostSuccessResultsOf(null, LONG_WAIT_MS, MILLISECONDS, cfs).join();
    }
}
