package io.foldright.cffu.ex;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.CompletableFutureUtils;
import io.foldright.cffu.LLCF;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static io.foldright.cffu.ex.ExceptionHandleUtils.handleAllExceptions;
import static io.foldright.cffu.ex.ExceptionHandleUtils.handleSwallowedExceptions;
import static java.util.Objects.requireNonNull;


public class CompletableFutureExHandleUtils {
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsFailFastOf(
            BiConsumer<Integer, Throwable> exceptionHandler, CompletionStage<? extends T>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<List<T>> ret = CompletableFutureUtils.allResultsFailFastOf(cfs);

        handleSwallowedExceptions(exceptionHandler, ret, cfs);

        return ret;
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allSuccessResultsOf(
            BiConsumer<Integer, Throwable> exceptionHandler,
            @Nullable T valueIfFailed, CompletionStage<? extends T>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<List<T>> ret = CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, cfs);

        handleAllExceptions(exceptionHandler, cfs);

        return ret;
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            BiConsumer<Integer, Throwable> exceptionHandler,
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return mostSuccessResultsOf(exceptionHandler, LLCF.ASYNC_POOL, valueIfNotSuccess, timeout, unit, cfs);
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            BiConsumer<Integer, Throwable> exceptionHandler,
            Executor executorWhenTimeout, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            CompletionStage<? extends T>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<List<T>> ret = CompletableFutureUtils.mostSuccessResultsOf(
                executorWhenTimeout, valueIfNotSuccess, timeout, unit, cfs);

        handleAllExceptions(exceptionHandler, cfs);

        return ret;
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOf(
            BiConsumer<Integer, Throwable> exceptionHandler, CompletionStage<? extends T>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<List<T>> ret = CompletableFutureUtils.allResultsOf(cfs);

        handleSwallowedExceptions(exceptionHandler, CompletableFutureUtils.allFailFastOf(cfs), cfs);

        return ret;
    }

    @Contract(pure = true)
    public static CompletableFuture<Void> allFailFastOf(
            BiConsumer<Integer, Throwable> exceptionHandler, CompletionStage<?>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<Void> ret = CompletableFutureUtils.allFailFastOf(cfs);

        handleSwallowedExceptions(exceptionHandler, ret, cfs);

        return ret;
    }

    @CheckReturnValue(explanation = "should use the returned CompletableFuture; forget to call its `join()` method?")
    @Contract(pure = true)
    public static CompletableFuture<Void> allOf(
            BiConsumer<Integer, Throwable> exceptionHandler, CompletionStage<?>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<Void> ret = CompletableFutureUtils.allOf(cfs);

        handleSwallowedExceptions(exceptionHandler, CompletableFutureUtils.allFailFastOf(cfs), cfs);

        return ret;
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anySuccessOf(
            BiConsumer<Integer, Throwable> exceptionHandler, CompletionStage<? extends T>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<T> ret = CompletableFutureUtils.anySuccessOf(cfs);

        handleSwallowedExceptions(exceptionHandler, ret, cfs);

        return ret;
    }

    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anyOf(
            BiConsumer<Integer, Throwable> exceptionHandler, CompletionStage<? extends T>... cfs) {
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        final CompletableFuture<T> ret = CompletableFutureUtils.anyOf(cfs);

        handleSwallowedExceptions(exceptionHandler, ret, cfs);

        return ret;
    }
}
