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
        assertEquals(n, toCompletableFuture(lf, executorService).get());

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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Test helper methods/fields
    ////////////////////////////////////////////////////////////////////////////////

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
