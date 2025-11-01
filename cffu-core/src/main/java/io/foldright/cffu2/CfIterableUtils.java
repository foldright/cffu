package io.foldright.cffu2;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu2.CompletableFutureUtils.*;
import static io.foldright.cffu2.internal.CommonUtils.toArray;


/**
 * This utility class provides {@link Iterable}-based variants (including {@link Collection}, {@link List}, etc.) of
 * same-named varargs methods from {@link CompletableFutureUtils}. These methods handle multiple actions
 * and CompletableFutures with the same type (aka. homogeneous).
 * <p>
 * While {@link CfTupleUtils} uses strongly typed tuples for handling different types of actions and
 * CompletableFutures (aka. heterogeneous), this class and {@link CompletableFutureUtils} work with homogeneous types,
 * offering a flexible approach for handling collections of actions and CompletableFutures of the same type.
 *
 * @author Eric Lin (linqinghua4 at gmail dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CompletableFutureUtils
 * @see CfParallelUtils
 * @see CfTupleUtils
 */
public final class CfIterableUtils {
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Multi-Actions(M*) Methods(create by actions)
    //
    //    - Iterable<Supplier<T>> -> CompletableFuture<List<T>>
    //    - Iterable<Runnable>    -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyFailFastAsync(Iterable<? extends Supplier<? extends T>> suppliers) {
        return CompletableFutureUtils.mSupplyFailFastAsync(toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyFailFastAsync(
            Iterable<? extends Supplier<? extends T>> suppliers, Executor executor) {
        return CompletableFutureUtils.mSupplyFailFastAsync(executor, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Object, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Iterable<? extends Supplier<? extends T>> suppliers) {
        return CompletableFutureUtils.mSupplyAllSuccessAsync(valueIfFailed, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Executor, Object, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Iterable<? extends Supplier<? extends T>> suppliers, Executor executor) {
        return CompletableFutureUtils.mSupplyAllSuccessAsync(executor, valueIfFailed, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Iterable<? extends Supplier<? extends T>> suppliers) {
        return CompletableFutureUtils.mSupplyMostSuccessAsync(valueIfNotSuccess, timeout, unit, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Iterable<? extends Supplier<? extends T>> suppliers, Executor executor) {
        return CompletableFutureUtils.mSupplyMostSuccessAsync(
                executor, valueIfNotSuccess, timeout, unit, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyAsync(Iterable<? extends Supplier<? extends T>> suppliers) {
        return CompletableFutureUtils.mSupplyAsync(toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<List<T>> mSupplyAsync(
            Iterable<? extends Supplier<? extends T>> suppliers, Executor executor) {
        return CompletableFutureUtils.mSupplyAsync(executor, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAnySuccessAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(Iterable<? extends Supplier<? extends T>> suppliers) {
        return CompletableFutureUtils.mSupplyAnySuccessAsync(toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAnySuccessAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(Iterable<? extends Supplier<? extends T>> suppliers, Executor executor) {
        return CompletableFutureUtils.mSupplyAnySuccessAsync(executor, toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAnyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<T> mSupplyAnyAsync(Iterable<? extends Supplier<? extends T>> suppliers) {
        return CompletableFutureUtils.mSupplyAnyAsync(toSupplierArray(suppliers));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mSupplyAnyAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T> CompletableFuture<T> mSupplyAnyAsync(
            Iterable<? extends Supplier<? extends T>> suppliers, Executor executor) {
        return CompletableFutureUtils.mSupplyAnyAsync(executor, toSupplierArray(suppliers));
    }

    @SuppressWarnings("unchecked")
    private static <T> Supplier<? extends T>[] toSupplierArray(Iterable<? extends Supplier<? extends T>> suppliers) {
        return toArray(suppliers, EMPTY_SUPPLIERS);
    }

    @SuppressWarnings("rawtypes")
    private static final Supplier[] EMPTY_SUPPLIERS = {};

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunFailFastAsync(Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunFailFastAsync(Iterable<? extends Runnable> actions) {
        return CompletableFutureUtils.mRunFailFastAsync(toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunFailFastAsync(Executor, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunFailFastAsync(Iterable<? extends Runnable> actions, Executor executor) {
        return CompletableFutureUtils.mRunFailFastAsync(executor, toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunAsync(Runnable...)}.
     */
    public static CompletableFuture<Void> mRunAsync(Iterable<? extends Runnable> actions) {
        return CompletableFutureUtils.mRunAsync(toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunAsync(Executor, Runnable...)}.
     */
    public static CompletableFuture<Void> mRunAsync(Iterable<? extends Runnable> actions, Executor executor) {
        return CompletableFutureUtils.mRunAsync(executor, toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunAnySuccessAsync(Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnySuccessAsync(Iterable<? extends Runnable> actions) {
        return CompletableFutureUtils.mRunAnySuccessAsync(toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunAnySuccessAsync(Executor, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnySuccessAsync(Iterable<? extends Runnable> actions, Executor executor) {
        return CompletableFutureUtils.mRunAnySuccessAsync(executor, toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunAnyAsync(Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnyAsync(Iterable<? extends Runnable> actions) {
        return CompletableFutureUtils.mRunAnyAsync(toRunnableArray(actions));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mRunAnyAsync(Executor, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnyAsync(Iterable<? extends Runnable> actions, Executor executor) {
        return CompletableFutureUtils.mRunAnyAsync(executor, toRunnableArray(actions));
    }

    private static Runnable[] toRunnableArray(Iterable<? extends Runnable> actions) {
        return toArray(actions, EMPTY_RUNNABLES);
    }

    private static final Runnable[] EMPTY_RUNNABLES = {};

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allOf* Methods (including mostSuccessResultsOf)
    //
    //    Iterable<CompletionStage<T>> -> CompletableFuture<List<T>>
    ////////////////////////////////////////////////////////////

    /**
     * Iterable variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allResultsFailFastOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.allResultsFailFastOf(toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allSuccessResultsOf(
            @Nullable T valueIfFailed, Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Iterable<? extends CompletionStage<? extends T>> cfs, Executor executorWhenTimeout) {
        return CompletableFutureUtils.mostSuccessResultsOf(
                executorWhenTimeout, valueIfNotSuccess, timeout, unit, toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allResultsOf}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<List<T>> allResultsOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.allResultsOf(toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static CompletableFuture<Void> allFailFastOf(Iterable<? extends CompletionStage<?>> cfs) {
        return CompletableFutureUtils.allFailFastOf(toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#allOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static CompletableFuture<Void> allOf(Iterable<? extends CompletionStage<?>> cfs) {
        return CompletableFutureUtils.allOf(toStageArray(cfs));
    }

    @SuppressWarnings("unchecked")
    private static <T> CompletionStage<? extends T>[] toStageArray(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return toArray(cfs, EMPTY_STAGES);
    }

    @SuppressWarnings("rawtypes")
    private static final CompletionStage[] EMPTY_STAGES = {};

    // endregion
    ////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    //
    //    Iterable<CompletionStage<T>> -> CompletableFuture<T>
    ////////////////////////////////////////////////////////////

    /**
     * Iterable variant of {@link CompletableFutureUtils#anySuccessOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> anySuccessOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.anySuccessOf(toStageArray(cfs));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#anyOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> anyOf(Iterable<? extends CompletionStage<? extends T>> cfs) {
        return CompletableFutureUtils.anyOf(toStageArray(cfs));
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Instance Methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions(thenM*) Methods
    //
    //    - thenMApply* (Iterable<Function>: T -> U)       -> CompletableFuture<List<U>>
    //    - thenMAccept*(Iterable<Consumer>: T -> Void)    -> CompletableFuture<Void>
    //    - thenMRun*   (Iterable<Runnable>: Void -> Void) -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyFailFastAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns) {
        return thenMApplyFailFastAsync(cfThis, fns, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyFailFastAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns, Executor executor) {
        return _thenMApplyFailFastAsync(cfThis, executor, toFunctionArray(fns), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfFailed, Iterable<? extends Function<? super T, ? extends U>> fns) {
        return thenMApplyAllSuccessAsync(cfThis, valueIfFailed, fns, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfFailed,
            Iterable<? extends Function<? super T, ? extends U>> fns, Executor executor) {
        return _thenMApplyAllSuccessAsync(cfThis, executor, valueIfFailed, toFunctionArray(fns), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Iterable<? extends Function<? super T, ? extends U>> fns) {
        return thenMApplyMostSuccessAsync(cfThis, valueIfNotSuccess, timeout, unit, fns, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Iterable<? extends Function<? super T, ? extends U>> fns, Executor executor) {
        return _thenMApplyMostSuccessAsync(cfThis, executor, valueIfNotSuccess, timeout, unit, toFunctionArray(fns), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns) {
        return thenMApplyAsync(cfThis, fns, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns, Executor executor) {
        return _thenMApplyAsync(cfThis, executor, toFunctionArray(fns), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAnySuccessAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenMApplyAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns) {
        return thenMApplyAnySuccessAsync(cfThis, fns, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAnySuccessAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenMApplyAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns, Executor executor) {
        return _thenMApplyAnySuccessAsync(cfThis, executor, toFunctionArray(fns), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAnyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenMApplyAnyAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns) {
        return thenMApplyAnyAsync(cfThis, fns, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMApplyAnyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U> CompletableFuture<U> thenMApplyAnyAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Function<? super T, ? extends U>> fns, Executor executor) {
        return _thenMApplyAnyAsync(cfThis, executor, toFunctionArray(fns), false);
    }

    @SuppressWarnings("unchecked")
    private static <T, U> Function<? super T, ? extends U>[] toFunctionArray(
            Iterable<? extends Function<? super T, ? extends U>> fns) {
        return toArray(fns, EMPTY_FUNCTIONS);
    }

    @SuppressWarnings("rawtypes")
    private static final Function[] EMPTY_FUNCTIONS = {};

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptFailFastAsync(CompletableFuture, Consumer[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T> CompletableFuture<Void> thenMAcceptFailFastAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions) {
        return thenMAcceptFailFastAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptFailFastAsync(CompletableFuture, Executor, Consumer[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T> CompletableFuture<Void> thenMAcceptFailFastAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions, Executor executor) {
        return _thenMAcceptFailFastAsync(cfThis, executor, toConsumerArray(actions), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptAsync(CompletableFuture, Consumer[])}.
     */
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions) {
        return thenMAcceptAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptAsync(CompletableFuture, Executor, Consumer[])}.
     */
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions, Executor executor) {
        return _thenMAcceptAsync(cfThis, executor, toConsumerArray(actions), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptAnySuccessAsync(CompletableFuture, Consumer[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T> CompletableFuture<Void> thenMAcceptAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions) {
        return thenMAcceptAnySuccessAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptAnySuccessAsync(CompletableFuture, Executor, Consumer[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T> CompletableFuture<Void> thenMAcceptAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions, Executor executor) {
        return _thenMAcceptAnySuccessAsync(cfThis, executor, toConsumerArray(actions), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptAnyAsync(CompletableFuture, Consumer[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T> CompletableFuture<Void> thenMAcceptAnyAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions) {
        return thenMAcceptAnyAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMAcceptAnyAsync(CompletableFuture, Executor, Consumer[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T> CompletableFuture<Void> thenMAcceptAnyAsync(
            CompletableFuture<? extends T> cfThis, Iterable<? extends Consumer<? super T>> actions, Executor executor) {
        return _thenMAcceptAnyAsync(cfThis, executor, toConsumerArray(actions), false);
    }

    @SuppressWarnings("unchecked")
    private static <T> Consumer<? super T>[] toConsumerArray(Iterable<? extends Consumer<? super T>> actions) {
        return toArray(actions, EMPTY_CONSUMERS);
    }

    @SuppressWarnings("rawtypes")
    private static final Consumer[] EMPTY_CONSUMERS = {};

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunFailFastAsync(CompletableFuture, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunFailFastAsync(
            CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions) {
        return thenMRunFailFastAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunFailFastAsync(CompletableFuture, Executor, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunFailFastAsync(
            CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions, Executor executor) {
        return _thenMRunFailFastAsync(cfThis, executor, toRunnableArray(actions), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunAsync(CompletableFuture, Runnable...)}.
     */
    public static CompletableFuture<Void> thenMRunAsync(CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions) {
        return thenMRunAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunAsync(CompletableFuture, Executor, Runnable...)}.
     */
    public static CompletableFuture<Void> thenMRunAsync(
            CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions, Executor executor) {
        return _thenMRunAsync(cfThis, executor, toRunnableArray(actions), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunAnySuccessAsync(CompletableFuture, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(
            CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions) {
        return thenMRunAnySuccessAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunAnySuccessAsync(CompletableFuture, Executor, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(
            CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions, Executor executor) {
        return _thenMRunAnySuccessAsync(cfThis, executor, toRunnableArray(actions), false);
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunAnyAsync(CompletableFuture, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnyAsync(CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions) {
        return thenMRunAnyAsync(cfThis, actions, defaultExecutor(cfThis));
    }

    /**
     * Iterable variant of {@link CompletableFutureUtils#thenMRunAnyAsync(CompletableFuture, Executor, Runnable...)}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnyAsync(
            CompletableFuture<?> cfThis, Iterable<? extends Runnable> actions, Executor executor) {
        return _thenMRunAnyAsync(cfThis, executor, toRunnableArray(actions), false);
    }

    private CfIterableUtils() {}
}
