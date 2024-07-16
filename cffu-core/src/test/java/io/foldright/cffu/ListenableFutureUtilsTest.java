package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import static io.foldright.cffu.ListenableFutureUtils.toCompletableFuture;
import static io.foldright.cffu.ListenableFutureUtils.toListenableFuture;
import static io.foldright.test_utils.TestUtils.n;
import static io.foldright.test_utils.TestUtils.rte;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.junit.jupiter.api.Assertions.*;


class ListenableFutureUtilsTest {
    @Test
    void test_toCompletableFuture() throws Exception {
        ListenableFuture<Integer> lf = Futures.immediateFuture(n);
        assertEquals(n, toCompletableFuture(lf).get());
        assertEquals(n, toCompletableFuture(lf, executorService).get());

        ListenableFuture<Integer> failed = Futures.immediateFailedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toCompletableFuture(failed).get()
        ).getCause());
    }

    @Test
    void test_toListenableFuture() throws Exception {
        CompletableFuture<Integer> cf = completedFuture(n);
        assertEquals(n, toListenableFuture(cf).get());

        CompletableFuture<Integer> failed = CompletableFutureUtils.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class,
                () -> toListenableFuture(failed).get()
        ).getCause());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper methods/fields
    ////////////////////////////////////////////////////////////////////////////////

    private static ExecutorService executorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("ListenableFutureUtilsTest");
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
