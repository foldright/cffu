package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.foldright.cffu.CffuFactoryTest.*;
import static io.foldright.cffu.CompletableFutureUtils.anyOfWithType;
import static io.foldright.test_utils.TestUtils.createFailedFuture;
import static io.foldright.test_utils.TestUtils.createIncompleteFuture;
import static org.junit.jupiter.api.Assertions.*;


class CompletableFutureUtilsTest {
    @Test
    void test_allOfWithResult() throws Exception {
        assertEquals(Arrays.asList(n, n + 1, n + 2), CompletableFutureUtils.allOfWithResult(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(n + 1),
                CompletableFuture.completedFuture(n + 2)
        ).get());

        assertTrue(CompletableFutureUtils.allOfWithResult().isDone());
    }

    @Test
    void test_allOfWithResult_exceptionally() throws Exception {
        try {
            CompletableFutureUtils.allOfWithResult(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void test_anyOfWithType() throws Exception {
        assertEquals(n, anyOfWithType(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n)
        ).get());

        assertFalse(anyOfWithType().isDone());
    }

    @Test
    void test_anyOfWithType_exceptionally() throws Exception {
        // first exceptionally completed cffuAnyOf cf win,
        // even later cfs normally completed!

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

        // first normally completed cffuAnyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, anyOfWithType(
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n),
                createIncompleteFuture()
        ).get());
    }

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
}
