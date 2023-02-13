package io.foldright.cffu;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Cffu {
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<List<T>> resultAllOf(CompletableFuture<T>... cfs) {
        return resultAllOf(Arrays.asList(cfs));
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<List<T>> resultAllOf(List<? extends CompletableFuture<T>> cfs) {
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
    public static <T1, T2> CompletableFuture<Pair<T1, T2>> resultOf(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        final Object[] result = {null, null};

        return CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2)
                )
                .thenApply(unused ->
                        Pair.of((T1) result[0], (T2) result[1])
                );
    }

    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> CompletableFuture<Triple<T1, T2, T3>> resultOf(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        final Object[] ret = {null, null, null};

        return CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> ret[0] = t1),
                        cf2.thenAccept(t2 -> ret[1] = t2),
                        cf3.thenAccept(t3 -> ret[2] = t3)
                )
                .thenApply(unused ->
                        Triple.of((T1) ret[0], (T2) ret[1], (T3) ret[2])
                );
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> anyOf(CompletableFuture<T>... cfs) {
        return (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
    }

    public static <T> CompletableFuture<T> anyOf(List<? extends CompletableFuture<T>> cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] arr = cfs.toArray(new CompletableFuture[0]);
        return anyOf(arr);
    }
}
