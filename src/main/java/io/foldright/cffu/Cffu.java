package io.foldright.cffu;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Cffu {
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<List<T>> allOf(List<? extends CompletableFuture<T>> cfs) {
        final int size = cfs.size();
        final Object[] result = new Object[size];

        final CompletableFuture<?>[] wrappedCfs = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final int index = i;

            CompletableFuture<T> cf = cfs.get(index);

            CompletableFuture<Void> wrapped = cf.thenAccept(t -> result[index] = t);
            wrappedCfs[index] = wrapped;
        }

        return CompletableFuture.allOf(wrappedCfs)
                .thenApply(unused -> (List<T>) Arrays.asList(result));
    }

    @SuppressWarnings("unchecked")
    public static <T, U> CompletableFuture<Pair<T, U>> allOf(
            CompletableFuture<T> cf1, CompletableFuture<U> cf2) {
        final Object[] result = {null, null};

        return CompletableFuture.allOf(
                        cf1.thenAccept(t -> result[0] = t),
                        cf2.thenAccept(u -> result[1] = u)
                )
                .thenApply(unused ->
                        Pair.of((T) result[0], (U) result[1])
                );
    }

    @SuppressWarnings("unchecked")
    public static <T, U, V> CompletableFuture<Triple<T, U, V>> allOf(
            CompletableFuture<T> cf1, CompletableFuture<U> cf2, CompletableFuture<V> cf3) {
        final Object[] ret = {null, null, null};

        return CompletableFuture.allOf(
                        cf1.thenAccept(t -> ret[0] = t),
                        cf2.thenAccept(u -> ret[1] = u),
                        cf3.thenAccept(v -> ret[2] = v)
                )
                .thenApply(unused ->
                        Triple.of((T) ret[0], (U) ret[1], (V) ret[2])
                );
    }
}
