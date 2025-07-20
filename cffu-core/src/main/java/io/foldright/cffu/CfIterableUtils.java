package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

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

    private CfIterableUtils() {}

    /**
     * Iterable variant of {@link CompletableFutureUtils#allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allResultsOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        CompletionStage<T>[] completionStages = CfIterableUtils.IterHelper.iterToArray(cfs);
        return CompletableFutureUtils.allResultsOf(completionStages);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allResultsFailFastOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        CompletionStage<T>[] completionStages = CfIterableUtils.IterHelper.iterToArray(cfs);
        return CompletableFutureUtils.allResultsFailFastOf(completionStages);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])}.
     *
     * @param valueIfFailed the value to return if any of the futures fail
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allSuccessResultsOf(@Nullable T valueIfFailed,
                                                                     Iterable<? extends CompletionStage<? extends T>> cfs) {
        CompletionStage<T>[] completionStages = CfIterableUtils.IterHelper.iterToArray(cfs);
        return CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, completionStages);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}.
     */
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            Executor executorWhenTimeout, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Iterable<? extends CompletionStage<? extends T>> cfs) {
        CompletionStage<T>[] completionStages = CfIterableUtils.IterHelper.iterToArray(cfs);
        return CompletableFutureUtils.mostSuccessResultsOf(executorWhenTimeout, valueIfNotSuccess, timeout, unit, completionStages);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#anyOf(CompletionStage[])}.
     * @return a CompletableFuture that completes with the result of the first completed stage
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> anyOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        CompletionStage<T>[] completionStages = CfIterableUtils.IterHelper.iterToArray(cfs);
        return CompletableFutureUtils.anyOf(completionStages);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#anySuccessOf(CompletionStage[])}.
     * @return a CompletableFuture that completes with the result of the first successfully completed stage
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> anySuccessOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        CompletionStage<T>[] completionStages = CfIterableUtils.IterHelper.iterToArray(cfs);
        return CompletableFutureUtils.anyOf(completionStages);
    }

    static class IterHelper {
        @SuppressWarnings("unchecked")
        static <T> CompletionStage<T>[] iterToArray(Iterable<? extends CompletionStage<? extends T>> cfs) {
            Iterable<CompletionStage<T>> narrowed = narrowCompletions(cfs);
            if (cfs instanceof Collection) {
                ArrayList<CompletionStage<T>> ts = new ArrayList<>((Collection<CompletionStage<T>>) narrowed);
                return ts.toArray(new CompletionStage[0]);
            }
            List<CompletionStage<T>> result = new ArrayList<>();
            addAll(result, narrowed.iterator());
            return result.toArray(new CompletionStage[0]);
        }

        /**
         * This is eligible because immutable/read-only collections are covariant.
         */
        @SuppressWarnings("unchecked")
        static <T> Iterable<CompletionStage<T>> narrowCompletions(Iterable<? extends CompletionStage<? extends T>> iterable) {
            Objects.requireNonNull(iterable, "iterable is null");
            return (Iterable<CompletionStage<T>>) iterable;
        }

        static <T> boolean addAll(Collection<T> addTo, Iterator<? extends T> iterator) {
            boolean wasModified;
            for(wasModified = false; iterator.hasNext(); wasModified |= addTo.add(iterator.next())) {
                // loop
            }
            return wasModified;
        }
    }
}