package io.foldright.cffu;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.foldright.cffu.Utils.sleep;
import static org.junit.jupiter.api.Assertions.*;

/**
 * NOTE:
 * <p>
 * Use {@code java} code to test the api usage problem of {@link Cffu};
 * Do NOT rewrite to {@code kotlin}.
 */
public class CffuTest {
    ////////////////////////////////////////////////////////////////////////////////
    // test helper fields and methods
    ////////////////////////////////////////////////////////////////////////////////

    private static final int n = 42;
    private static final String s = "S42";
    private static final double d = 42.1;
    private static final RuntimeException rte = new RuntimeException("Bang");

    private static CompletableFuture<Integer> createNormallyCompletedFutureWithSleep() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(10);
            return 100;
        });
    }

    private static CompletableFuture<Integer> createExceptionallyCompletedFutureWithSleep() {
        return CompletableFuture.supplyAsync(() -> {
            sleep(10);
            throw rte;
        });
    }

    ////////////////////////////////////////////////////////////////////////////////
    // test logic
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    public void test_resultAllOf() throws Exception {
        final CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(n);
        final CompletableFuture<Integer> f2 = CompletableFuture.completedFuture(n + 1);
        final CompletableFuture<Integer> f3 = CompletableFuture.completedFuture(n + 2);

        assertEquals(Arrays.asList(n, n + 1, n + 2), Cffu.resultAllOf(f1, f2, f3).get());
        assertEquals(Arrays.asList(n, n + 1, n + 2), Cffu.resultAllOf(Arrays.asList(f1, f2, f3)).get());
    }

    @Test
    public void test_resultOf_2_or_3() throws Exception {
        assertEquals(Pair.of(n, s), Cffu.resultOf(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s)
        ).get());

        assertEquals(Triple.of(n, s, d), Cffu.resultOf(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d)
        ).get());
    }

    @Test
    void test_resultAllOf_exceptionally() throws Exception {
        try {
            Cffu.resultAllOf(
                    CompletableFuture.completedFuture(n),
                    CompletableFuture.failedFuture(rte),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void test_resultOf_2_or_3_exceptionally() throws Exception {
        try {
            Cffu.resultOf(
                    CompletableFuture.completedFuture(n),
                    CompletableFuture.failedFuture(rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            Cffu.resultOf(
                    CompletableFuture.completedFuture(n),
                    CompletableFuture.failedFuture(rte),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    public void test_anyOf() throws Exception {
        assertEquals(n, Cffu.anyOf(
                createNormallyCompletedFutureWithSleep(),
                createNormallyCompletedFutureWithSleep(),
                CompletableFuture.completedFuture(n)
        ).get());
        assertEquals(n, Cffu.anyOf(Arrays.asList(
                createNormallyCompletedFutureWithSleep(),
                CompletableFuture.completedFuture(n),
                createNormallyCompletedFutureWithSleep()
        )).get());
    }

    @Test
    public void test_anyOf_exceptionally() throws Exception {
        // first exceptionally completed anyOf cf win,
        // even later cfs normally completed!

        try {
            Cffu.anyOf(
                    createNormallyCompletedFutureWithSleep(),
                    createNormallyCompletedFutureWithSleep(),
                    CompletableFuture.failedFuture(rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            Cffu.anyOf(Arrays.asList(
                    createNormallyCompletedFutureWithSleep(),
                    CompletableFuture.failedFuture(rte),
                    createNormallyCompletedFutureWithSleep()
            )).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // first normally completed anyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, Cffu.anyOf(
                createExceptionallyCompletedFutureWithSleep(),
                createExceptionallyCompletedFutureWithSleep(),
                CompletableFuture.completedFuture(n)
        ).get());

        assertEquals(n, Cffu.anyOf(Arrays.asList(
                createExceptionallyCompletedFutureWithSleep(),
                CompletableFuture.completedFuture(n),
                createExceptionallyCompletedFutureWithSleep()
        )).get());
    }
}
