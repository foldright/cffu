package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.foldright.testutils.TestThreadPoolManager.getTestThreadPoolExecutor;
import static io.foldright.testutils.TestUtils.createExceptionallyCompletedFutureWithSleep;
import static io.foldright.testutils.TestUtils.createNormallyCompletedFutureWithSleep;
import static org.junit.jupiter.api.Assertions.*;


/**
 * NOTE:
 * <p>
 * Use {@code java} code to test the api usage problem of {@link Cffu};
 * Do NOT rewrite to {@code kotlin}.
 */
public class CffuTest {
    ////////////////////////////////////////////////////////////////////////////////
    // test constants
    ////////////////////////////////////////////////////////////////////////////////

    private static final int n = 42;
    private static final int another_n = 424242;

    private static final String s = "S42";

    private static final double d = 42.1;

    private static final RuntimeException rte = new RuntimeException("Bang");

    private static final CffuFactory cffuFactory =
            CffuFactoryBuilder.newCffuFactoryBuilder(getTestThreadPoolExecutor()).build();

    ////////////////////////////////////////////////////////////////////////////////
    // new methods of CompletableFuture missing functions
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_cffuAllOf() throws Exception {
        final CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(n);
        final CompletableFuture<Integer> f2 = CompletableFuture.completedFuture(n + 1);
        final CompletableFuture<Integer> f3 = CompletableFuture.completedFuture(n + 2);

        assertEquals(Arrays.asList(n, n + 1, n + 2), cffuFactory.cffuAllOf(f1, f2, f3).get());
    }

    @Test
    void test_cffuAllOf_exceptionally() throws Exception {
        try {
            cffuFactory.cffuAllOf(
                    CompletableFuture.completedFuture(n),
                    failedCf(),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    public void test_cffuCombine() throws Exception {
        assertEquals(Tuple2.of(n, s), cffuFactory.cffuCombine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.cffuCombine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d)
        ).get());
    }

    @Test
    void test_cffuCombine_exceptionally() throws Exception {
        try {
            cffuFactory.cffuCombine(
                    CompletableFuture.completedFuture(n),
                    failedCf()
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cffuFactory.cffuCombine(
                    CompletableFuture.completedFuture(n),
                    failedCf(),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    public void test_cffuAnyOf() throws Exception {
        assertEquals(n, cffuFactory.cffuAnyOf(
                createNormallyCompletedFutureWithSleep(another_n),
                CompletableFuture.completedFuture(n),
                createNormallyCompletedFutureWithSleep(another_n)
        ).get());
    }

    @Test
    public void test_cffuAnyOf_exceptionally() throws Exception {
        // first exceptionally completed cffuAnyOf cf win,
        // even later cfs normally completed!

        try {
            cffuFactory.cffuAnyOf(
                    createNormallyCompletedFutureWithSleep(another_n),
                    failedCf(),
                    createNormallyCompletedFutureWithSleep(another_n)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // first normally completed cffuAnyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, cffuFactory.cffuAnyOf(
                createExceptionallyCompletedFutureWithSleep(rte),
                CompletableFuture.completedFuture(n),
                createExceptionallyCompletedFutureWithSleep(rte)
        ).get());
    }

    private static <T> CompletableFuture<T> failedCf() {
        CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(rte);
        return cf;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // backport codes from CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    // timeout control
    ////////////////////////////////////////


    @Test
    void name() {
    }
}
