package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;

import static io.foldright.cffu.CompletableFutureUtils.*;
import static io.foldright.cffu.LLCF.ASYNC_POOL;
import static io.foldright.cffu.LLCF.f_cast;
import static io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleAllSwallowedExceptions;
import static io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleSwallowedExceptions;
import static java.util.Objects.requireNonNull;


/**
 * Utility class for parallel data processing using CompletableFuture.
 * <p>
 * Supports different concurrency strategies:
 * all-fail-fast, all-success, most-success, all-complete, any-success and any-complete.
 * <p>
 * The parallel processing methods are divided into two categories:
 * <ul>
 * <li>Factory methods that create CompletableFutures from input collections</li>
 * <li>Instance methods that chain parallel operations on existing CompletableFutures(CompletionStages)</li>
 * </ul>
 * <p>
 * <strong>NOTE:</strong> For all methods, the input Iterable is allowed to contain {@code null} elements which are
 * passed directly to the sequential actions, matching CompletableFuture's behavior of allowing {@code null} values.
 * To skip processing {@code null} values, filter them out from the input collection beforehand.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CompletableFutureUtils
 * @see java.util.stream.Stream#parallel()
 * @see java.util.Collection#parallelStream()
 */
public final class CfParallelUtils {
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Factory Methods(create by multiply data and one action)
    //
    //    - parApply* (Iterable, Function: T -> U)    -> CompletableFuture<List<U>>
    //    - parAccept*(Iterable, Consumer: T -> Void) -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyFailFastAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn) {
        return parApplyFailFastAsync(elements, fn, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyFailFastAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<U>[] cfs = wrapEleFunction0(elements, fn, executor);
        CompletableFuture<List<U>> ret = allResultsOf0(true, cfs);
        handleSwallowedExceptions("parApplyFailFastAsync", ret, cfs);
        return ret;
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyAllSuccessAsync(
            Iterable<? extends T> elements, @Nullable U valueIfFailed, Function<? super T, ? extends U> fn) {
        return parApplyAllSuccessAsync(elements, valueIfFailed, fn, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyAllSuccessAsync(
            Iterable<? extends T> elements, @Nullable U valueIfFailed,
            Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<U>[] cfs = wrapEleFunction0(elements, fn, executor);
        handleAllSwallowedExceptions("parApplyAllSuccessAsync", cfs);
        return allSuccessResultsOf0(valueIfFailed, cfs);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyMostSuccessAsync(
            Iterable<? extends T> elements, @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit,
            Function<? super T, ? extends U> fn) {
        return parApplyMostSuccessAsync(elements, valueIfNotSuccess, timeout, unit, fn, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyMostSuccessAsync(
            Iterable<? extends T> elements, @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit,
            Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<U>[] cfs = wrapEleFunction0(elements, fn, executor);
        handleAllSwallowedExceptions("parApplyMostSuccessAsync", cfs);
        return mostSuccessResultsOf0(executor, valueIfNotSuccess, timeout, unit, cfs);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn) {
        return parApplyAsync(elements, fn, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> parApplyAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<U>[] cfs = wrapEleFunction0(elements, fn, executor);
        CompletableFuture<List<U>> ret = allResultsOf0(false, cfs);
        handleSwallowedExceptions("parApplyAsync", ret, cfs);
        return ret;
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<U> parApplyAnySuccessAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn) {
        return parApplyAnySuccessAsync(elements, fn, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<U> parApplyAnySuccessAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<U>[] cfs = wrapEleFunction0(elements, fn, executor);
        CompletableFuture<U> ret = anySuccessOf0(cfs);
        handleSwallowedExceptions("parApplyAnySuccessAsync", ret, cfs);
        return ret;
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<U> parApplyAnyAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn) {
        return parApplyAnyAsync(elements, fn, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes multiple input elements in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T, U> CompletableFuture<U> parApplyAnyAsync(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<U>[] cfs = wrapEleFunction0(elements, fn, executor);
        CompletableFuture<U> ret = f_cast(CompletableFuture.anyOf(cfs));
        handleSwallowedExceptions("parApplyAnyAsync", ret, cfs);
        return ret;
    }

    @SuppressWarnings("unchecked")
    private static <T, U> CompletableFuture<U>[] wrapEleFunction0(
            Iterable<? extends T> elements, Function<? super T, ? extends U> fn, Executor executor) {
        return StreamSupport.stream(elements.spliterator(), false)
                .map(e -> CompletableFuture.supplyAsync(() -> fn.apply(e), executor))
                .toArray(CompletableFuture[]::new);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T> CompletableFuture<Void> parAcceptFailFastAsync(
            Iterable<? extends T> elements, Consumer<? super T> action) {
        return parAcceptFailFastAsync(elements, action, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T> CompletableFuture<Void> parAcceptFailFastAsync(
            Iterable<? extends T> elements, Consumer<? super T> action, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<Void>[] inputs = wrapEleRunnable0(elements, action, executor);
        CompletableFuture<Void> ret = allFailFastOf0(inputs);
        handleSwallowedExceptions("parAcceptFailFastAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allOf allOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link CompletableFutureUtils#allOf allOf} documentation for the rules of result computation.
     */
    public static <T> CompletableFuture<Void> parAcceptAsync(
            Iterable<? extends T> elements, Consumer<? super T> action) {
        return parAcceptAsync(elements, action, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allOf allOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allOf allOf} documentation for the rules of result computation.
     */
    public static <T> CompletableFuture<Void> parAcceptAsync(
            Iterable<? extends T> elements, Consumer<? super T> action, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<Void>[] inputs = wrapEleRunnable0(elements, action, executor);
        CompletableFuture<Void> ret = CompletableFuture.allOf(inputs);
        handleSwallowedExceptions("parAcceptAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T> CompletableFuture<Void> parAcceptAnySuccessAsync(
            Iterable<? extends T> elements, Consumer<? super T> action) {
        return parAcceptAnySuccessAsync(elements, action, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T> CompletableFuture<Void> parAcceptAnySuccessAsync(
            Iterable<? extends T> elements, Consumer<? super T> action, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<Void>[] inputs = wrapEleRunnable0(elements, action, executor);
        CompletableFuture<Void> ret = anySuccessOf0(inputs);
        handleSwallowedExceptions("parAcceptAnySuccessAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T> CompletableFuture<Void> parAcceptAnyAsync(
            Iterable<? extends T> elements, Consumer<? super T> action) {
        return parAcceptAnyAsync(elements, action, ASYNC_POOL);
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes multiple input elements in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `parAcceptAsync`")
    public static <T> CompletableFuture<Void> parAcceptAnyAsync(
            Iterable<? extends T> elements, Consumer<? super T> action, Executor executor) {
        requireNonNull(elements, "elements is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<Void>[] inputs = wrapEleRunnable0(elements, action, executor);
        CompletableFuture<Void> ret = f_cast(CompletableFuture.anyOf(inputs));
        handleSwallowedExceptions("parAcceptAnyAsync", ret, inputs);
        return ret;
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<Void>[] wrapEleRunnable0(
            Iterable<? extends T> elements, Consumer<? super T> action, Executor executor) {
        return StreamSupport.stream(elements.spliterator(), false)
                .map(e -> CompletableFuture.runAsync(() -> action.accept(e), executor))
                .toArray(CompletableFuture[]::new);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Instance Methods for CF<Iterable<T>>
    //
    //    - thenParApply* (CF<Iterable>, Function: T -> U)    -> CompletableFuture<List<U>>
    //    - thenParAccept*(CF<Iterable>, Consumer: T -> Void) -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyFailFastAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn) {
        return thenParApplyFailFastAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyFailFastAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parApplyFailFastAsync(elements, fn, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyAllSuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis,
            @Nullable U valueIfFailed, Function<? super T, ? extends U> fn) {
        return thenParApplyAllSuccessAsync(cfThis, valueIfFailed, fn, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyAllSuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis,
            @Nullable U valueIfFailed, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parApplyAllSuccessAsync(elements, valueIfFailed, fn, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyMostSuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U> fn) {
        return thenParApplyMostSuccessAsync(cfThis, valueIfNotSuccess, timeout, unit, fn, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyMostSuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parApplyMostSuccessAsync(elements, valueIfNotSuccess, timeout, unit, fn, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn) {
        return thenParApplyAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenParApplyAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parApplyAsync(elements, fn, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenParApplyAnySuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn) {
        return thenParApplyAnySuccessAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenParApplyAnySuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parApplyAnySuccessAsync(elements, fn, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenParApplyAnyAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn) {
        return thenParApplyAnyAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's function computation
     * into a CompletableFuture using {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenParApplyAnyAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Function<? super T, ? extends U> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parApplyAnyAsync(elements, fn, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T> CompletableFuture<Void> thenParAcceptFailFastAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action) {
        return thenParAcceptFailFastAsync(cfThis, action, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T> CompletableFuture<Void> thenParAcceptFailFastAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parAcceptFailFastAsync(elements, action, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allOf allOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#allOf allOf} documentation for the rules of result computation.
     */
    public static <T> CompletableFuture<Void> thenParAcceptAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action) {
        return thenParAcceptAsync(cfThis, action, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#allOf allOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#allOf allOf} documentation for the rules of result computation.
     */
    public static <T> CompletableFuture<Void> thenParAcceptAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parAcceptAsync(elements, action, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T> CompletableFuture<Void> thenParAcceptAnySuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action) {
        return thenParAcceptAnySuccessAsync(cfThis, action, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T> CompletableFuture<Void> thenParAcceptAnySuccessAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parAcceptAnySuccessAsync(elements, action, executor));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)} with the default executor of parameter cfThis.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T> CompletableFuture<Void> thenParAcceptAnyAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action) {
        return thenParAcceptAnyAsync(cfThis, action, defaultExecutor(cfThis));
    }

    /**
     * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
     * processes elements from the result of parameter cfThis in parallel by wrapping each element's action computation
     * into a CompletableFuture using {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenParAcceptAsync`")
    public static <T> CompletableFuture<Void> thenParAcceptAnyAsync(
            CompletableFuture<? extends Iterable<? extends T>> cfThis, Consumer<? super T> action, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return cfThis.thenCompose(elements -> parAcceptAnyAsync(elements, action, executor));
    }

    private CfParallelUtils() {}
}
