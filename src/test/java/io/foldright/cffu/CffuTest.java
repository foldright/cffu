package io.foldright.cffu;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Use {@code java} code to test the api usage problem of {@link Cffu};
 * Do NOT rewrite to {@code kotlin}.
 */
public class CffuTest {
    @Test
    public void test_resultAllOf() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(42);
        CompletableFuture<Integer> f2 = CompletableFuture.completedFuture(43);
        CompletableFuture<Integer> f3 = CompletableFuture.completedFuture(44);

        assertEquals(Arrays.asList(42, 43, 44), Cffu.resultAllOf(f1, f2, f3).get());
        assertEquals(Arrays.asList(42, 43, 44), Cffu.resultAllOf(Arrays.asList(f1, f2, f3)).get());
    }

    @Test
    public void test_resultOf_2_3() throws Exception {
        int n = 42;
        String s = "S42";
        double d = 42.1;

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
    public void test_anyOf() throws Exception {
        Supplier<CompletableFuture<Integer>> gen = () ->
                CompletableFuture.supplyAsync(() -> {
                    UtilsKt.sleep(10);
                    return 100;
                });
        CompletableFuture<Integer> f = CompletableFuture.completedFuture(42);

        assertEquals(42, Cffu.anyOf(gen.get(), gen.get(), f).get());
        assertEquals(42, Cffu.anyOf(Arrays.asList(gen.get(), f, gen.get())).get());
    }
}
