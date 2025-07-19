package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

/**
 * This Utility class provides list-based variants of methods from {@link CompletableFutureUtils}
 * for processing and composing multiple homogeneous asynchronous actions and CompletableFutures.
 * <p>
 * While {@link CfTupleUtils} uses strongly-typed tuples for heterogeneous CompletableFutures,
 * this class works with lists of CompletableFutures of the same type, providing a more flexible
 * approach when the number of actions is variable or all elements have the same type.
 *
 * @author Eric Lin (linqinghua4 at gmail dot com)
 * @see CompletableFutureUtils
 * @see CfTupleUtils
 */
public final class CfIterableUtils {
    /**
     * Iterable variant of {@link CompletableFutureUtils#allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allResultsOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.allResultsOf(toArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allResultsFailFastOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.allResultsFailFastOf(toArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])}.
     *
     * @param valueIfFailed the value to return if any of the futures fail
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allSuccessResultsOf(
            @Nullable T valueIfFailed, Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, toArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}.
     */
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            Executor executorWhenTimeout, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.mostSuccessResultsOf(
                executorWhenTimeout, valueIfNotSuccess, timeout, unit, toArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#anyOf(CompletionStage[])}.
     *
     * @return a CompletableFuture that completes with the result of the first completed stage
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> anyOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.anyOf(toArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#anySuccessOf(CompletionStage[])}.
     *
     * @return a CompletableFuture that completes with the result of the first successfully completed stage
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> anySuccessOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.anyOf(toArray(cfs));
    }

    @SuppressWarnings({"unchecked", "SuspiciousToArrayCall"})
    private static <T> CompletionStage<? extends T>[] toArray(Iterable<? extends CompletionStage<? extends T>> cfs) {
        requireNonNull(cfs, "cfs iterable is null");
        if (cfs instanceof Collection) {
            return ((Collection<T>) cfs).toArray(EMPTY);
        }
        List<CompletionStage<? extends T>> list = new ArrayList<>();
        for (CompletionStage<? extends T> s : cfs) {
            list.add(s);
        }
        return list.toArray(EMPTY);
    }

    @SuppressWarnings("rawtypes")
    private static final CompletionStage[] EMPTY = new CompletionStage[0];

    private CfIterableUtils() {
    }
}
