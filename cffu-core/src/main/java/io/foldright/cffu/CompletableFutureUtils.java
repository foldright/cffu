package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.internal.CommonUtils;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.*;

import static io.foldright.cffu.Delayer.atCfDelayerThread;
import static io.foldright.cffu.LLCF.*;
import static io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleAllSwallowedExceptions;
import static io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleSwallowedExceptions;
import static io.foldright.cffu.internal.CommonUtils.*;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * This class contains the new enhanced and backport methods for {@link CompletableFuture}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author HuHao (995483610 at qq dot com)
 * @author Eric Lin (linqinghua4 at gmail dot com)
 */
public final class CompletableFutureUtils {

    /*
     * Implementation Note about the name convention of internal methods:
     *
     * - methods with `f_` prefix means not type-safe, e.g.
     *    - return type CompletableFuture that may be a minimal-stage
     *    - force cast to CompletableFuture<T> from any CompletableFuture<?>
     *    - return generic type T but constrained runtime type TupleX
     * - methods with `0` suffix means no parameter validation, e.g.
     *    - no null check
     *
     * because these methods is not safe, caller logic SHOULD pay attention to keep implementation correct.
     */

    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Factory Methods(including static methods of CF)
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Multi-Actions(M*) Methods(create by actions)
    //
    //    - Supplier<T>[] -> CompletableFuture<List<T>>
    //    - Runnable[]    -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Shortcut to method {@link #allResultsFailFastOf allResultsFailFastOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFailFastAsync(Supplier<? extends T>... suppliers) {
        return mSupplyFailFastAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Shortcut to method {@link #allResultsFailFastOf allResultsFailFastOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFailFastAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        CompletableFuture<? extends T>[] inputs = wrapSuppliers0(executor, suppliers);
        CompletableFuture<List<T>> ret = allResultsOf0(true, inputs);
        handleSwallowedExceptions("mSupplyFailFastAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        return mSupplyAllSuccessAsync(ASYNC_POOL, valueIfFailed, suppliers);
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            Executor executor, @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        CompletableFuture<? extends T>[] inputs = wrapSuppliers0(executor, suppliers);
        handleAllSwallowedExceptions("mSupplyAllSuccessAsync", inputs);
        return allSuccessResultsOf0(valueIfFailed, inputs);
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf},
     * wraps input suppliers to CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf}
     * documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Supplier<? extends T>... suppliers) {
        return mSupplyMostSuccessAsync(ASYNC_POOL, valueIfNotSuccess, timeout, unit, suppliers);
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf},
     * wraps input suppliers to CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf}
     * documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            Executor executor, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        CompletableFuture<? extends T>[] inputs = wrapSuppliers0(executor, suppliers);
        handleAllSwallowedExceptions("mSupplyMostSuccessAsync", inputs);
        return mostSuccessResultsOf0(executor, valueIfNotSuccess, timeout, unit, inputs);
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        CompletableFuture<? extends T>[] inputs = wrapSuppliers0(executor, suppliers);
        CompletableFuture<List<T>> ret = allResultsOf0(false, inputs);
        handleSwallowedExceptions("mSupplyAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnySuccessAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        CompletableFuture<? extends T>[] inputs = wrapSuppliers0(executor, suppliers);
        CompletableFuture<T> ret = anySuccessOf0(inputs);
        handleSwallowedExceptions("mSupplyAnySuccessAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnyAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        CompletableFuture<? extends T>[] inputs = wrapSuppliers0(executor, suppliers);
        CompletableFuture<T> ret = f_cast(CompletableFuture.anyOf(inputs));
        handleSwallowedExceptions("mSupplyAnyAsync", ret, inputs);
        return ret;
    }

    private static <T> CompletableFuture<? extends T>[] wrapSuppliers0(Executor executor, Supplier<? extends T>[] suppliers) {
        return mapArray(suppliers, CompletableFuture[]::new, s -> CompletableFuture.supplyAsync(s, executor));
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunFailFastAsync(Runnable... actions) {
        return mRunFailFastAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunFailFastAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        CompletableFuture<Void>[] inputs = wrapRunnables0(executor, actions);
        CompletableFuture<Void> ret = allFailFastOf0(inputs);
        handleSwallowedExceptions("mRunFailFastAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunAsync(Runnable... actions) {
        return mRunAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        CompletableFuture<Void>[] inputs = wrapRunnables0(executor, actions);
        CompletableFuture<Void> ret = CompletableFuture.allOf(inputs);
        handleSwallowedExceptions("mRunAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnySuccessAsync(Runnable... actions) {
        return mRunAnySuccessAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnySuccessAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        CompletableFuture<Void>[] inputs = wrapRunnables0(executor, actions);
        CompletableFuture<Void> ret = anySuccessOf0(inputs);
        handleSwallowedExceptions("mRunAnySuccessAsync", ret, inputs);
        return ret;
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnyAsync(Runnable... actions) {
        return mRunAnyAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static CompletableFuture<Void> mRunAnyAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        CompletableFuture<Void>[] inputs = wrapRunnables0(executor, actions);
        CompletableFuture<Void> ret = f_cast(CompletableFuture.anyOf(inputs));
        handleSwallowedExceptions("mRunAnyAsync", ret, inputs);
        return ret;
    }

    private static CompletableFuture<Void>[] wrapRunnables0(Executor executor, Runnable[] actions) {
        return mapArray(actions, CompletableFuture[]::new, a -> CompletableFuture.runAsync(a, executor));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    private static <T> CompletableFuture<T> f_allTupleWithEhOf0(
            boolean failFast, CompletionStage<?>[] stages, String where) {
        CompletableFuture<T> ret = f_allTupleOf0(failFast, stages);
        handleSwallowedExceptions(where, ret, stages);
        return ret;
    }

    /**
     * Returns {@code CompletableFuture<T>} with generic type {@code T} but constrained to type TupleX.
     */
    private static <T> CompletableFuture<T> f_allTupleOf0(boolean failFast, CompletionStage<?>[] stages) {
        final AtomicReferenceArray<Object> results = new AtomicReferenceArray<>(stages.length);
        final CompletableFuture<Void>[] resultsSetterCfs = createAllResultsSetterCfs(stages, results);

        final CompletableFuture<Void> resultsSetter;
        if (failFast) resultsSetter = allFailFastOf0(resultsSetterCfs);
        else resultsSetter = CompletableFuture.allOf(resultsSetterCfs);

        return resultsSetter.thenApply(unused -> f_tupleOf0(toArray(results)));
    }

    /**
     * Returns generic type {@code T} but constrained to type TupleX.
     */
    @SuppressWarnings("unchecked")
    private static <T> T f_tupleOf0(Object[] xs) {
        final int len = xs.length;
        final Object ret;
        if (len == 2) ret = Tuple2.of(xs[0], xs[1]);
        else if (len == 3) ret = Tuple3.of(xs[0], xs[1], xs[2]);
        else if (len == 4) ret = Tuple4.of(xs[0], xs[1], xs[2], xs[3]);
        else ret = Tuple5.of(xs[0], xs[1], xs[2], xs[3], xs[4]);
        return (T) ret;
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    private static <T> CompletableFuture<T> f_allSuccessTupleWithEhOf0(CompletionStage<?>[] stages, String where) {
        handleAllSwallowedExceptions(where, stages);
        return f_allSuccessTupleOf0(stages);
    }

    private static <T> CompletableFuture<T> f_allSuccessTupleOf0(CompletionStage<?>[] stages) {
        return f_allTupleOf0(false, f_convertStageArray0(stages, s -> s.exceptionally(ex -> null)));
    }

    private static <T, U> CompletionStage<U>[] f_convertStageArray0(
            CompletionStage<? extends T>[] stages, Function<CompletionStage<T>, CompletionStage<U>> converter) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        CompletionStage<T>[] ss = (CompletionStage[]) stages;
        return mapArray(ss, CompletionStage[]::new, converter);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    private static <T> CompletableFuture<T> f_mostSuccessTupleWithEhOf0(
            Executor executorWhenTimeout, long timeout, TimeUnit unit, CompletionStage<?>[] stages, String where) {
        handleAllSwallowedExceptions(where, stages);
        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, stages);
    }

    private static <T> CompletableFuture<T> f_mostSuccessTupleOf0(
            Executor executorWhenTimeout, long timeout, TimeUnit unit, CompletionStage<?>[] stages) {
        // 1. MUST be non-minimal-stage CF instances in order to read results(`getSuccessNow`), otherwise UnsupportedOpException.
        // 2. SHOULD copy input cfs(by calling `exceptionally` method) to avoid memory leaks,
        //    otherwise all input cfs would be retained until output cf completes.
        CompletableFuture<?>[] cfArray = mapArray(stages, CompletableFuture[]::new,
                s -> toNonMinCf0(s).exceptionally(v -> null));
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, timeout, unit, executorWhenTimeout)
                .handle((unused, ex) -> f_tupleOf0(f_mGetSuccessNow0(null, cfArray)));
    }

    /**
     * Multi-Gets(MGet) the results in the <strong>same order</strong> of the given cfs arguments,
     * use the result value if the given stage is completed normally, else use the given valueIfNotSuccess
     *
     * @param cfs MUST be *Non-Minimal* CF instances in order to read results(`getSuccessNow`),
     *            otherwise UnsupportedOperationException
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] f_mGetSuccessNow0(@Nullable T valueIfNotSuccess, CompletableFuture<? extends T>[] cfs) {
        return (T[]) fillArray(new Object[cfs.length], i -> getSuccessNow(cfs[i], valueIfNotSuccess));
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostSuccessResultsOf)
    //
    //    CompletionStage<T>[] -> CompletableFuture<List<T>>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed normally with a list containing
     * the successful results of all given stages when all the given stages complete normally;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * WITHOUT waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method is the same as {@link #allResultsOf allResultsOf} method except for the fail-fast behavior.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#allAsList the equivalent Guava method allAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsFailFastOf(CompletionStage<? extends T>... cfs) {
        return allResultsOf0(true, requireCfsAndEleNonNull(cfs));
    }

    /**
     * Returns a new CompletableFuture that is completed normally with a list containing the successful results of
     * all given stages when all the given stages complete; The list of results is in the <strong>same order</strong>
     * as the input list, and if any of given stages complete exceptionally, their corresponding position will contain
     * {@code valueIfFailed} (which is indistinguishable from the stage having a successful value of {@code valueIfFailed}).
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method differs from {@link #allResultsFailFastOf allResultsFailFastOf} method in that it's tolerant
     * of failed stages for any of the items, representing them as {@code valueIfFailed} in the result list.
     *
     * @param valueIfFailed the value used as result if the input stage completed exceptionally
     * @throws NullPointerException if the cfs param or any of its elements is {@code null}
     * @see #getSuccessNow(CompletableFuture, Object)
     * @see Futures#successfulAsList the equivalent Guava method successfulAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allSuccessResultsOf(
            @Nullable T valueIfFailed, CompletionStage<? extends T>... cfs) {
        return allSuccessResultsOf0(valueIfFailed, requireCfsAndEleNonNull(cfs));
    }

    private static <T> CompletableFuture<List<T>> allSuccessResultsOf0(
            @Nullable T valueIfFailed, CompletionStage<? extends T>[] cfs) {
        return allResultsOf0(false, f_convertStageArray0(cfs, s -> s.exceptionally(ex -> valueIfFailed)));
    }

    /**
     * Returns a new CompletableFuture that is completed normally with a list containing the successful results of
     * the given stages before the given timeout (aka as many results as possible in the given time);
     * The list of results is in the <strong>same order</strong> as the input list, and if any of given stages
     * complete exceptionally or are incomplete, their corresponding positions will contain {@code valueIfNotSuccess}
     * (which is indistinguishable from the stage having a successful value of {@code valueIfNotSuccess}).
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method differs from {@link #allResultsFailFastOf allResultsFailFastOf} method in that it's tolerant of
     * failed or incomplete stages for any of the items, representing them as {@code valueIfNotSuccess} in the result list.
     *
     * @param valueIfNotSuccess the value used as result if the input stage not completed normally
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return mostSuccessResultsOf(ASYNC_POOL, valueIfNotSuccess, timeout, unit, cfs);
    }

    /**
     * Returns a new CompletableFuture that is completed normally with a list containing the successful results of
     * the given stages before the given timeout (aka as many results as possible in the given time);
     * The list of results is in the <strong>same order</strong> as the input list, and if any of given stages
     * complete exceptionally or are incomplete, their corresponding positions will contain {@code valueIfNotSuccess}
     * (which is indistinguishable from the stage having a successful value of {@code valueIfNotSuccess}).
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method differs from {@link #allResultsFailFastOf allResultsFailFastOf} method in that it's tolerant of failed
     * or incomplete stages for any of the items, representing them as {@code valueIfNotSuccess} in the result list.
     *
     * @param executorWhenTimeout the executor to use for asynchronous execution when timeout
     * @param valueIfNotSuccess   the value used as result if the input stage not completed normally
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            Executor executorWhenTimeout, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            CompletionStage<? extends T>... cfs) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        requireCfsAndEleNonNull(cfs);

        return mostSuccessResultsOf0(executorWhenTimeout, valueIfNotSuccess, timeout, unit, cfs);
    }

    private static <T> CompletableFuture<List<T>> mostSuccessResultsOf0(
            Executor executorWhenTimeout, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            CompletionStage<? extends T>[] cfs) {
        if (cfs.length == 0) return completedFuture(arrayList());
        if (cfs.length == 1) {
            // defensive copy input cf to non-minimal-stage instance in order to
            // 1. avoid writing it by `cffuCompleteOnTimeout` and is able to read its result(`getSuccessNow`)
            // 2. ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
            final CompletableFuture<T> f = toNonMinCfCopy0(cfs[0]);
            return cffuCompleteOnTimeout(f, valueIfNotSuccess, timeout, unit, executorWhenTimeout)
                    .handle((unused, ex) -> arrayList(getSuccessNow(f, valueIfNotSuccess)));
        }

        // 1. MUST be non-minimal-stage CF instances in order to read results(`getSuccessNow`), otherwise UnsupportedOpException.
        // 2. SHOULD copy input cfs(by calling `exceptionally` method) to avoid memory leaks,
        //    otherwise all input cfs would be retained until output cf completes.
        CompletableFuture<T>[] cfArray = mapArray(cfs, CompletableFuture[]::new,
                s -> LLCF.<T>toNonMinCf0(s).exceptionally(v -> valueIfNotSuccess));
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, timeout, unit, executorWhenTimeout)
                .handle((unused, ex) -> arrayList(f_mGetSuccessNow0(valueIfNotSuccess, cfArray)));
    }

    /**
     * Returns a new CompletableFuture that is completed normally with a list containing
     * the successful results of all given stages when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture
     * also does so, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * Comparing the waiting-all-<strong>complete</strong> behavior of this method, the fail-fast behavior
     * of {@link #allResultsFailFastOf allResultsFailFastOf} method is more responsive to user
     * and generally more desired in the application.
     * <p>
     * This method is the same as {@link #allOf allOf} method,
     * except that the returned CompletableFuture contains the results of the given stages.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        return allResultsOf0(false, requireCfsAndEleNonNull(cfs));
    }

    private static <T> CompletableFuture<List<T>> allResultsOf0(boolean failFast, CompletionStage<? extends T>[] cfs) {
        final int len = cfs.length;
        if (len == 0) return completedFuture(arrayList());
        // convert input cf to non-minimal-stage CF instance for SINGLE input in order to
        // ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCf0(cfs[0]).thenApply(CommonUtils::arrayList);

        final AtomicReferenceArray<T> results = new AtomicReferenceArray<>(len);
        final CompletableFuture<Void>[] resultsSetterCfs = createAllResultsSetterCfs(cfs, results);

        final CompletableFuture<Void> resultsSetter;
        if (failFast) resultsSetter = allFailFastOf0(resultsSetterCfs);
        else resultsSetter = CompletableFuture.allOf(resultsSetterCfs);

        return f_cast(resultsSetter.thenApply(unused -> arrayList(results)));
    }

    /**
     * Returns a new CompletableFuture that is completed normally when all the given stages complete normally;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * WITHOUT waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * The successful results, if any, of the given stages are not reflected in the returned CompletableFuture
     * ({@code CompletableFuture<Void>}), but may be obtained by inspecting them individually; Or using below methods
     * reflected results in the returned CompletableFuture which are more convenient, safer and best-practice of concurrency:
     * <ul>
     * <li>{@link #allResultsFailFastOf  allResultsFailFastOf}, {@link #allTupleFailFastOf allTupleFailFastOf}
     * <li>{@link #allSuccessResultsOf allSuccessResultsOf}, {@link #allSuccessTupleOf allSuccessTupleOf}
     * <li>{@link #mostSuccessResultsOf mostSuccessResultsOf}, {@link #mostSuccessTupleOf mostSuccessTupleOf}
     * <li>{@link #allResultsOf allResultsOf}, {@link #allTupleOf allTupleOf}
     * </ul>
     * <p>
     * This method is the same as {@link #allOf allOf} method except for the fail-fast behavior.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#whenAllSucceed the equivalent Guava method whenAllSucceed()
     */
    @Contract(pure = true)
    public static CompletableFuture<Void> allFailFastOf(CompletionStage<?>... cfs) {
        return allFailFastOf0(requireCfsAndEleNonNull(cfs));
    }

    private static CompletableFuture<Void> allFailFastOf0(CompletionStage<?>[] cfs) {
        final int len = cfs.length;
        if (len == 0) return completedFuture(null);
        // convert input cf to non-minimal-stage CF instance for SINGLE input in order to
        // ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCf0(cfs[0]).thenApply(unused -> null);

        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] failedOrBeIncomplete = new CompletableFuture[len + 1];
        fill0(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is completed normally when all given cfs completed normally, otherwise be incomplete
        failedOrBeIncomplete[len] = CompletableFuture.allOf(successOrBeIncomplete);

        return f_cast(CompletableFuture.anyOf(failedOrBeIncomplete));
    }

    /**
     * Returns a new CompletableFuture that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture
     * also does so, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * The successful results, if any, of the given stages are not reflected in the returned CompletableFuture
     * ({@code CompletableFuture<Void>}), but may be obtained by inspecting them individually; Or using below methods
     * reflected results in the returned CompletableFuture which are more convenient, safer and best-practice of concurrency:
     * <ul>
     * <li>{@link #allResultsOf allResultsOf}, {@link #allTupleOf allTupleOf}
     * <li>{@link #allResultsFailFastOf  allResultsFailFastOf}, {@link #allTupleFailFastOf allTupleFailFastOf}
     * <li>{@link #allSuccessResultsOf allSuccessResultsOf}, {@link #allSuccessTupleOf allSuccessTupleOf}
     * <li>{@link #mostSuccessResultsOf mostSuccessResultsOf}, {@link #mostSuccessTupleOf mostSuccessTupleOf}
     * </ul>
     * <p>
     * Among the applications of this method is to await completion of a set of independent stages
     * before continuing a program, as in: {@code CompletableFuture.allOf(c1, c2, c3).join();}.
     * <p>
     * This method is the same as {@link CompletableFuture#allOf CompletableFuture#allOf} method,
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture}.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#whenAllComplete the equivalent Guava method whenAllComplete()
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; forget to call its `join()` method?")
    @Contract(pure = true)
    public static CompletableFuture<Void> allOf(CompletionStage<?>... cfs) {
        requireCfsAndEleNonNull(cfs);
        if (cfs.length == 0) return completedFuture(null);
        // convert input cf to non-minimal-stage CF instance for SINGLE input in order to
        // ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (cfs.length == 1) return toNonMinCf0(cfs[0]).thenApply(unused -> null);
        return CompletableFuture.allOf(f_toCfArray0(cfs));
    }

    @SafeVarargs
    private static <S extends CompletionStage<?>> S[] requireCfsAndEleNonNull(S... stages) {
        return requireArrayAndEleNonNull("cf", stages);
    }

    /**
     * Returns a cf array whose elements do the result collection for <strong>AllResultsOf*</strong> methods.
     * <p>
     * Implementation Note: Uses AtomicReferenceArray and CAS operations to prevent memory leaks in `AllResultOf*`
     * methods. Without this protection, if any inputs complete exceptionally while others are still running,
     * the results array would unnecessarily retain memory for cf results that will never be used.
     */
    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<Void>[] createAllResultsSetterCfs(
            CompletionStage<? extends T>[] stages, AtomicReferenceArray<T> results) {
        final CompletableFuture<Void>[] resultSetterCfs = new CompletableFuture[stages.length];
        return fillArray(resultSetterCfs, i -> f_toCf0(stages[i]).<CompletableFuture<Void>>handle((v, ex) -> {
            if (ex == null) {
                // atomically store value if slot has not been marked as unneeded with SENTINEL_UNNEEDED
                results.compareAndSet(i, null, v);
                return completedFuture(null);
            } else {
                // this `if checking` is a simple optimization for benign race condition.
                // the code logic would still be correct if directly setting SENTINEL_UNNEEDED without checking
                if (results.get(0) != SENTINEL_UNNEEDED)
                    // if any stage has failed, all results are unneeded; mark all slots with SENTINEL_UNNEEDED
                    fillAtomicReferenceArray(results, (T) SENTINEL_UNNEEDED);
                return failedFuture(ex);
            }
        }).thenCompose(x -> x));
    }

    /**
     * A sentinel object used to mark slots in {@link AtomicReferenceArray} where values no longer need to be written.
     *
     * @see #createAllResultsSetterCfs
     */
    private static final Object SENTINEL_UNNEEDED = new Object();

    private static <T> void fill0(CompletionStage<? extends T>[] stages,
                                  CompletableFuture<? extends T>[] successOrBeIncomplete,
                                  CompletableFuture<Void>[] failedOrBeIncomplete) {
        for (int i = 0; i < stages.length; i++) {
            final CompletableFuture<T> f = f_toCf0(stages[i]);
            successOrBeIncomplete[i] = exceptionallyCompose(f, ex -> new CompletableFuture<>());
            failedOrBeIncomplete[i] = f.thenCompose(v -> new CompletableFuture<>());
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    //
    //    CompletionStage<T>[] -> CompletableFuture<T>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that completed normally when any of the given stages complete normally,
     * with the same result; Otherwise, when all the given stages complete exceptionally, the returned CompletableFuture
     * also does so, with a CompletionException holding an exception from any of the given stages as its cause.
     * If no stages are provided, returns a new CompletableFuture that is already completed exceptionally
     * with a {@link NoCfsProvidedException}.
     * <p>
     * This method differs from {@link #anyOf anyOf} method in that this method is any-<strong>success</strong>
     * instead of the any-<strong>complete</strong> behavior of method {@link #anyOf anyOf}.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anySuccessOf(CompletionStage<? extends T>... cfs) {
        return anySuccessOf0(requireCfsAndEleNonNull(cfs));
    }

    private static <T> CompletableFuture<T> anySuccessOf0(CompletionStage<? extends T>[] cfs) {
        final int len = cfs.length;
        if (len == 0) return failedFuture(new NoCfsProvidedException());
        // defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
        // 1. avoid writing the input cf unexpectedly by caller code
        // 2. the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCfCopy0(cfs[0]);

        // NOTE: fill ONE MORE element of successOrBeIncompleteCfs LATER
        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len + 1];
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] failedOrBeIncomplete = new CompletableFuture[len];
        fill0(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE:
        //       a cf that is completed exceptionally when all given cfs completed exceptionally, otherwise be incomplete
        successOrBeIncomplete[len] = CompletableFuture.allOf(failedOrBeIncomplete);

        return f_cast(CompletableFuture.anyOf(successOrBeIncomplete));
    }

    /**
     * Returns a new CompletableFuture that is completed with the same successful result or exception of any of
     * the given stages when one stage completes. If no stages are provided, returns an incomplete CompletableFuture.
     * <p>
     * Comparing the any-<strong>complete</strong> behavior(the complete one may be failed) of this method,
     * the any-<strong>success</strong> behavior of method {@link #anySuccessOf anySuccessOf}
     * is generally more desired in the application.
     * <p>
     * This method is the same as {@link CompletableFuture#anyOf CompletableFuture#anyOf} method,
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture}
     * and the return type is more specific {@code T} instead of {@code Object}.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anyOf(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        if (cfs.length == 0) return new CompletableFuture<>();
        // defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
        // 1. avoid writing the input cf unexpectedly by caller code
        // 2. the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (cfs.length == 1) return toNonMinCfCopy0(cfs[0]);
        return f_cast(CompletableFuture.anyOf(f_toCfArray0(cfs)));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostSuccessTupleOf Methods
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostSuccessTupleOf(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2, cf3);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4, cf5);
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Immediate Value Argument Factory Methods(backport methods)
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is already completed exceptionally with the given exception.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletableFuture
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> failedFuture(Throwable ex) {
        requireNonNull(ex, "ex is null");
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.failedFuture(ex);
        }
        final CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(ex);
        return cf;
    }

    /**
     * Returns a new CompletionStage that is already completed with the given value
     * and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <strong>CAUTION:</strong> if run on old Java 8 (which does not support *minimal* CompletionStage),
     * this method just returns a *normal* CompletableFuture instance which is NOT a *minimal* CompletionStage.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> completedStage(@Nullable T value) {
        return IS_JAVA9_PLUS ? CompletableFuture.completedStage(value) : completedFuture(value);
    }

    /**
     * Returns a new CompletionStage that is already completed exceptionally with
     * the given exception and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <strong>CAUTION:</strong> if run on old Java 8 (which does not support *minimal* CompletionStage),
     * this method just returns a *normal* CompletableFuture instance which is NOT a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> failedStage(Throwable ex) {
        return IS_JAVA9_PLUS ? CompletableFuture.failedStage(ex) : failedFuture(ex);
    }

    /**
     * Returns a new CompletableFuture that encapsulates the execution of synchronous logic. By wrapping synchronous
     * code in a CompletableFuture, exceptions can be handled consistently within the CompletableFuture pipeline,
     * eliminating the need to manage separate exceptional paths both inside and outside the flow.
     *
     * @throws NullPointerException if argument {@code callable} is {@code null}
     * @see CompletableFuture#runAsync(Runnable)
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, run directly instead of wrapping")
    public static <T> CompletableFuture<T> fromSyncCall(Callable<T> callable) {
        requireNonNull(callable, "callable is null");
        try {
            return completedFuture(callable.call());
        } catch (Throwable ex) {
            return failedFuture(ex);
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## Incomplete CompletableFuture Constructor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
     * <p>
     * In general, you won't use this method in application code, prefer other factory methods.
     *
     * @param <U> the type of the value
     * @see CompletableFuture#newIncompleteFuture()
     * @see CompletableFuture#CompletableFuture()
     */
    @Contract(pure = true)
    public static <U> CompletableFuture<U> newIncompleteFuture(CompletableFuture<?> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        return IS_JAVA9_PLUS ? cfThis.newIncompleteFuture() : new CompletableFuture<>();
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Delay Execution(backport methods)
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new Executor that submits a task to the default executor after the given delay (or no delay
     * if non-positive). Each delay commences upon invocation of the returned executor's {@code execute} method.
     *
     * @param delay how long to delay, in units of {@code unit}
     * @param unit  a {@code TimeUnit} determining how to interpret the {@code delay} parameter
     * @return the new delayed executor
     */
    @Contract(pure = true)
    public static Executor delayedExecutor(long delay, TimeUnit unit) {
        return delayedExecutor(delay, unit, ASYNC_POOL);
    }

    /**
     * Returns a new Executor that submits a task to the given base executor after the given delay (or no delay
     * if non-positive). Each delay commences upon invocation of the returned executor's {@code execute} method.
     *
     * @param delay    how long to delay, in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     */
    @Contract(pure = true)
    public static Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        requireNonNull(unit, "unit is null");
        // NOTE: do NOT translate executor by screenExecutor method; same as CompletableFuture.delayedExecutor
        requireNonNull(executor, "executor is null");
        return IS_JAVA9_PLUS ? CompletableFuture.delayedExecutor(delay, unit, executor)
                : new DelayedExecutor(delay, unit, executor);
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Instance Methods(including new enhanced + backport methods)
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions(thenM*) Methods
    //
    //    - thenMApply* (Function[]: T -> U)       -> CompletableFuture<List<U>>
    //    - thenMAccept*(Consumer[]: T -> Void)    -> CompletableFuture<Void>
    //    - thenMRun*   (Runnable[]: Void -> Void) -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Shortcut to method {@link #allResultsFailFastOf allResultsFailFastOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyFailFastAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Shortcut to method {@link #allResultsFailFastOf allResultsFailFastOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        // defensive shallow copy of input array argument by `clone`,
        //   since it is used asynchronously in `thenCompose` and could be mutated by caller (NOT thread-safe)
        // this same defensive copying pattern is used in similar methods below.
        Function<? super T, ? extends U>[] copy = requireArrayAndEleNonNull("fn", fns).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<U>[] inputs = wrapFunctions0(executor, v, copy);
            CompletableFuture<List<U>> ret = allResultsOf0(true, inputs);
            handleSwallowedExceptions("thenMApplyFailFastAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfFailed, Function<? super T, ? extends U>... fns) {
        return thenMApplyAllSuccessAsync(cfThis, defaultExecutor(cfThis), valueIfFailed, fns);
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            @Nullable U valueIfFailed, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ? extends U>[] copy = requireArrayAndEleNonNull("fn", fns).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<U>[] inputs = wrapFunctions0(executor, v, copy);
            handleAllSwallowedExceptions("thenMApplyAllSuccessAsync", inputs);
            return allSuccessResultsOf0(valueIfFailed, inputs);
        });
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * mostSuccessResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        return thenMApplyMostSuccessAsync(cfThis, defaultExecutor(cfThis), valueIfNotSuccess, timeout, unit, fns);
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])
     * mostSuccessResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])
     * mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ? extends U>[] copy = requireArrayAndEleNonNull("fn", fns).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<U>[] inputs = wrapFunctions0(executor, v, copy);
            handleAllSwallowedExceptions("thenMApplyMostSuccessAsync", inputs);
            return mostSuccessResultsOf0(executor, valueIfNotSuccess, timeout, unit, inputs);
        });
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ? extends U>[] copy = requireArrayAndEleNonNull("fn", fns).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<U>[] inputs = wrapFunctions0(executor, v, copy);
            CompletableFuture<List<U>> ret = allResultsOf0(false, inputs);
            handleSwallowedExceptions("thenMApplyAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyAnySuccessAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ? extends U>[] copy = requireArrayAndEleNonNull("fn", fns).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<U>[] inputs = wrapFunctions0(executor, v, copy);
            CompletableFuture<U> ret = anySuccessOf0(inputs);
            handleSwallowedExceptions("thenMApplyAnySuccessAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyAnyAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ? extends U>[] copy = requireArrayAndEleNonNull("fn", fns).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<U>[] inputs = wrapFunctions0(executor, v, copy);
            CompletableFuture<U> ret = f_cast(CompletableFuture.anyOf(inputs));
            handleSwallowedExceptions("thenMApplyAnyAsync", ret, inputs);
            return ret;
        });
    }

    private static <T, U> CompletableFuture<U>[] wrapFunctions0(
            Executor executor, @Nullable T v, Function<? super T, ? extends U>[] fns) {
        return mapArray(fns, CompletableFuture[]::new, f -> CompletableFuture.supplyAsync(() -> f.apply(v), executor));
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFailFastAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptFailFastAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Consumer<? super T>[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<Void>[] inputs = wrapConsumers0(executor, v, copy);
            CompletableFuture<Void> ret = allFailFastOf0(inputs);
            handleSwallowedExceptions("thenMAcceptFailFastAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Consumer<? super T>[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<Void>[] inputs = wrapConsumers0(executor, v, copy);
            CompletableFuture<Void> ret = CompletableFuture.allOf(inputs);
            handleSwallowedExceptions("thenMAcceptAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptAnySuccessAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Consumer<? super T>[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<Void>[] inputs = wrapConsumers0(executor, v, copy);
            CompletableFuture<Void> ret = anySuccessOf0(inputs);
            handleSwallowedExceptions("thenMAcceptAnySuccessAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnyAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptAnyAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the second argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Consumer<? super T>[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(v -> {
            CompletableFuture<Void>[] inputs = wrapConsumers0(executor, v, copy);
            CompletableFuture<Void> ret = f_cast(CompletableFuture.anyOf(inputs));
            handleSwallowedExceptions("thenMAcceptAnyAsync", ret, inputs);
            return ret;
        });
    }

    private static <T> CompletableFuture<Void>[] wrapConsumers0(Executor executor, T v, Consumer<? super T>[] actions) {
        return mapArray(actions, CompletableFuture[]::new, a -> CompletableFuture.runAsync(() -> a.accept(v), executor));
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunFailFastAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunFailFastAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunFailFastAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Runnable[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(unused -> {
            CompletableFuture<Void>[] inputs = wrapRunnables0(executor, copy);
            CompletableFuture<Void> ret = allFailFastOf0(inputs);
            handleSwallowedExceptions("thenMRunFailFastAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Runnable[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(unused -> {
            CompletableFuture<Void>[] inputs = wrapRunnables0(executor, copy);
            CompletableFuture<Void> ret = CompletableFuture.allOf(inputs);
            handleSwallowedExceptions("thenMRunAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAnySuccessAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Runnable[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(unused -> {
            CompletableFuture<Void>[] inputs = wrapRunnables0(executor, copy);
            CompletableFuture<Void> ret = anySuccessOf0(inputs);
            handleSwallowedExceptions("thenMRunAnySuccessAsync", ret, inputs);
            return ret;
        });
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnyAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAnyAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMRunAsync`")
    public static CompletableFuture<Void> thenMRunAnyAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Runnable[] copy = requireArrayAndEleNonNull("action", actions).clone();

        return cfThis.thenCompose(unused -> {
            CompletableFuture<Void>[] inputs = wrapRunnables0(executor, copy);
            CompletableFuture<Void> ret = f_cast(CompletableFuture.anyOf(inputs));
            handleSwallowedExceptions("thenMRunAnyAsync", ret, inputs);
            return ret;
        });
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions-Tuple(thenMTuple*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4,
            Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## thenBoth* Methods(binary input) with fail-fast support
    //
    //    - thenCombineFailFast*   (BiFunction: (T, U) -> V)    -> CompletableFuture<U>
    //    - thenAcceptBothFailFast*(BiConsumer: (T, U) -> Void) -> CompletableFuture<Void>
    //    - runAfterBothFailFast*  (Runnable:   Void -> Void)   -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFailFast(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(fn, "fn is null");

        return bothFailFast0(cfThis, other).thenApply(t -> fn.apply(t._1, t._2));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the default executor of parameter cfThis,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFailFastAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineFailFastAsync(cfThis, other, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFailFastAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return bothFailFast0(cfThis, other).thenApplyAsync(t -> fn.apply(t._1, t._2), executor);
    }

    private static void requireThisAndOtherNonNull(CompletionStage<?> cfThis, CompletionStage<?> other) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(other, "other is null");
    }

    /**
     * Implementation Note: Calling this method is necessary to keep the runtime type(including `minimal-stage`) of
     * return cf same as input `cfThis` argument. The runtime type of method {@link #allResultsFailFastOf(CompletionStage[])}
     * return cf is always CompletableFuture, does NOT keep the runtime type of input `cfThis` argument.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T1, T2> CompletableFuture<Tuple2<T1, T2>> bothFailFast0(
            CompletableFuture<? extends T1> cfThis, CompletionStage<? extends T2> other) {
        CompletableFuture thisSuccessOrBeIncomplete = exceptionallyCompose(cfThis, ex -> new CompletableFuture());
        CompletionStage otherSuccessOrBeIncomplete = exceptionallyCompose(other, ex -> new CompletableFuture());
        CompletableFuture cfValue = thisSuccessOrBeIncomplete.thenCombine(otherSuccessOrBeIncomplete, Tuple2::of);

        CompletableFuture thisFailedOrBeIncomplete = cfThis.thenCompose(v -> new CompletableFuture());
        CompletionStage otherFailedOrBeIncomplete = other.thenCompose(v -> new CompletableFuture());
        CompletableFuture cfEx = thisFailedOrBeIncomplete.applyToEither(otherFailedOrBeIncomplete, v -> null);

        return cfValue.applyToEither(cfEx, x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFailFast(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");

        return bothFailFast0(cfThis, other).thenAccept(t -> action.accept(t._1, t._2));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the default executor of parameter cfThis,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFailFastAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothFailFastAsync(cfThis, other, action, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFailFastAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return bothFailFast0(cfThis, other).thenAcceptAsync(t -> action.accept(t._1, t._2), executor);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally, executes the given action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFailFast(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");

        return bothFailFast0(cfThis, other).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using the default executor of parameter cfThis.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFailFastAsync(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action) {
        return runAfterBothFailFastAsync(cfThis, other, action, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using the supplied executor.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFailFastAsync(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return bothFailFast0(cfThis, other).thenRunAsync(action, executor);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## thenEither* Methods(binary input) with either(any)-success support
    //
    //    - applyToEitherSuccess* (Function: (T) -> U)     -> CompletableFuture<U>
    //    - acceptEitherSuccess*  (Consumer: (T) -> Void)  -> CompletableFuture<Void>
    //    - runAfterEitherSuccess*(Runnable: Void -> Void) -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied function.
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccess(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other, Function<? super T, ? extends U> fn) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(fn, "fn is null");

        return eitherSuccess0(cfThis, other).thenApply(fn);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the default executor of parameter cfThis,
     * with the corresponding result as argument to the supplied function.
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other, Function<? super T, ? extends U> fn) {
        return applyToEitherSuccessAsync(cfThis, other, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other,
            Function<? super T, ? extends U> fn, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return eitherSuccess0(cfThis, other).thenApplyAsync(fn, executor);
    }

    /**
     * Implementation Note: Calling this method is necessary to keep the runtime type(including `minimal-stage`) of
     * return cf same as input `cfThis` argument. The runtime type of method {@link #anySuccessOf(CompletionStage[])}
     * return cf is always CompletableFuture, does NOT keep the runtime type of input `cfThis` argument.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> CompletableFuture<T> eitherSuccess0(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other) {
        CompletableFuture thisSuccessOrBeIncomplete = exceptionallyCompose(cfThis, ex -> new CompletableFuture());
        CompletionStage otherSuccessOrBeIncomplete = exceptionallyCompose(other, ex -> new CompletableFuture());
        CompletableFuture cfValue = thisSuccessOrBeIncomplete.applyToEither(otherSuccessOrBeIncomplete, x -> x);

        CompletableFuture thisFailedOrBeIncomplete = cfThis.thenCompose(v -> new CompletableFuture());
        CompletionStage otherFailedOrBeIncomplete = other.thenCompose(v -> new CompletableFuture());
        CompletableFuture cfEx = thisFailedOrBeIncomplete.thenCombine(otherFailedOrBeIncomplete, (v1, v2) -> null);

        return cfValue.applyToEither(cfEx, x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied action.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccess(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other, Consumer<? super T> action) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");

        return eitherSuccess0(cfThis, other).thenAccept(action);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the default executor of parameter cfThis,
     * with the corresponding result as argument to the supplied action.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccessAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherSuccessAsync(cfThis, other, action, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     *
     * @param action   the action to perform before completing the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccessAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends T> other,
            Consumer<? super T> action, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return eitherSuccess0(cfThis, other).thenAcceptAsync(action, executor);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static CompletableFuture<Void> runAfterEitherSuccess(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");

        return eitherSuccess0(cfThis, other).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action
     * using the default executor of parameter cfThis.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static CompletableFuture<Void> runAfterEitherSuccessAsync(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action) {
        return runAfterEitherSuccessAsync(cfThis, other, action, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action
     * using the supplied executor.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     */
    public static CompletableFuture<Void> runAfterEitherSuccessAsync(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return eitherSuccess0(cfThis, other).thenRunAsync(action, executor);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Error Handling Methods of CompletionStage
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally with the given exceptionType,
     * is executed with the exception from the given stage({@code argument cfThis}) as the argument to the supplied function.
     * Otherwise, the returned stage contains same result as the given stage.
     * <p>
     * <strong>"The exception from the given stage({@code argument cfThis})"</strong> means the cause of
     * the {@link ExecutionException} thrown by {@code get()} or, if {@code get()} throws a different kind
     * of exception, that exception itself; aka the exception is unwrapped by {@link #unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched against
     *                      the exception from argument cfThis. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if cfThis fails with the expected exception type.
     *                      The function's argument is the exception from cfThis.
     * @see #unwrapCfException(Throwable)
     * @see Futures#catching the equivalent Guava method catching()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, F extends CompletionStage<? super T>>
    F catching(F cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");

        return (F) cfThis.handle((v, ex) -> {
            if (ex == null) return cfThis;
            Throwable unwrap = unwrapCfException(ex);
            if (!exceptionType.isInstance(unwrap)) return cfThis;
            return completedFuture(fallback.apply((X) unwrap));
        }).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally with the given exceptionType,
     * is executed with the exception from the given stage({@code argument cfThis}) as the argument to the supplied
     * function, using the default executor of parameter the given stage.
     * Otherwise, the returned stage contains same result as the given stage.
     * <p>
     * <strong>"The exception from the given stage({@code argument cfThis})"</strong> means the cause of
     * the {@link ExecutionException} thrown by {@code get()} or, if {@code get()} throws a different kind
     * of exception, that exception itself; aka the exception is unwrapped by {@link #unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched against
     *                      the exception from argument cfThis. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if cfThis fails with the expected exception type.
     *                      The function's argument is the exception from cfThis.
     * @see #unwrapCfException(Throwable)
     * @see Futures#catching the equivalent Guava method catching()
     */
    public static <T, X extends Throwable, F extends CompletionStage<? super T>>
    F catchingAsync(F cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        return catchingAsync(cfThis, exceptionType, fallback, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally with the given exceptionType,
     * is executed with the exception from the given stage({@code argument cfThis}) as the argument to the supplied
     * function, using the supplied Executor. Otherwise, the returned stage contains same result as the given stage.
     * <p>
     * <strong>"The exception from the given stage({@code argument cfThis})"</strong> means the cause of
     * the {@link ExecutionException} thrown by {@code get()} or, if {@code get()} throws a different kind
     * of exception, that exception itself; aka the exception is unwrapped by {@link #unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched against
     *                      the exception from argument cfThis. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if cfThis fails with the expected exception type.
     *                      The function's argument is the exception from cfThis.
     * @param executor      the executor to use for asynchronous execution
     * @see #unwrapCfException(Throwable)
     * @see Futures#catching the equivalent Guava method catching()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, F extends CompletionStage<? super T>>
    F catchingAsync(F cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");
        requireNonNull(executor, "executor is null");

        return (F) cfThis.handle((v, ex) -> {
            if (ex == null) return cfThis;
            Throwable unwrap = unwrapCfException(ex);
            if (!exceptionType.isInstance(unwrap)) return cfThis;
            return cfThis.<T>handleAsync((v1, ex1) -> fallback.apply((X) unwrap), executor);
        }).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the default executor of parameter cfThis.
     * Otherwise, if given stage completes normally, then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingAsync(CompletionStage, Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @see #catchingAsync(CompletionStage, Class, Function)
     */
    public static <T, F extends CompletionStage<? super T>>
    F exceptionallyAsync(F cfThis, Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the supplied Executor. Otherwise,
     * if given stage completes normally, then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingAsync(CompletionStage, Class, Function, Executor)}
     * instead in your business application.
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @see #catchingAsync(CompletionStage, Class, Function, Executor)
     */
    @SuppressWarnings("unchecked")
    public static <T, F extends CompletionStage<? super T>>
    F exceptionallyAsync(F cfThis, Function<Throwable, ? extends T> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA12_PLUS) {
            return (F) cfThis.exceptionallyAsync(fn, executor);
        }
        // below code is copied from CompletionStage#exceptionallyAsync
        return (F) cfThis.handle((v, ex) -> (ex == null) ? cfThis :
                cfThis.<T>handleAsync((v1, ex1) -> fn.apply(ex1), executor)
        ).thenCompose(x -> x);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Timeout Control Methods of CompletableFuture
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed exceptionally with a {@link TimeoutException}
     * when the given CompletableFuture is not completed before the given timeout; otherwise the returned
     * CompletableFuture completed with the same successful result or exception of the given CompletableFuture.
     * <p>
     * Uses the default executor of parameter cfThis as {@code executorWhenTimeout}.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture instead of {@code cfThis} to avoid
     * the subsequent usage of the delay thread; This behavior is DIFFERENT from the original CF method
     * {@link CompletableFuture#orTimeout CompletableFuture#orTimeout} and its backport method {@link #orTimeout orTimeout}.
     * More info see the javadoc of {@link #orTimeout orTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @see #cffuOrTimeout(CompletableFuture, long, TimeUnit, Executor)
     */
    public static <F extends CompletableFuture<?>> F cffuOrTimeout(F cfThis, long timeout, TimeUnit unit) {
        return cffuOrTimeout(cfThis, timeout, unit, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that is completed exceptionally with a {@link TimeoutException}
     * when the given CompletableFuture is not completed before the given timeout; otherwise the returned
     * CompletableFuture completed with the same successful result or exception of the given CompletableFuture.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture instead of {@code cfThis} to avoid
     * the subsequent usage of the delay thread; This behavior is DIFFERENT from the original CF method
     * {@link CompletableFuture#orTimeout CompletableFuture#orTimeout} and its backport method {@link #orTimeout orTimeout}.
     * More info see the javadoc of {@link #orTimeout orTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param timeout             how long to wait before completing exceptionally with a TimeoutException,
     *                            in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param executorWhenTimeout the executor to use for asynchronous execution when the wait timed out
     */
    public static <F extends CompletableFuture<?>> F cffuOrTimeout(
            F cfThis, long timeout, TimeUnit unit, Executor executorWhenTimeout) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");

        return hopExecutorIfAtCfDelayerThread(orTimeout(cfThis, timeout, unit), executorWhenTimeout);
    }

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> {@link CompletableFuture#orTimeout CompletableFuture#orTimeout}
     * and this backport method are <strong>UNSAFE</strong>!
     * <p>
     * When the wait timed out, the subsequent non-async actions of the dependent CompletableFutures are performed
     * in CompletableFuture's internal <strong>SINGLE-thread delay executor</strong> (including timeout functionality).
     * This means that the long-running subsequent non-async actions will block this executor thread, preventing it from
     * handling other timeouts and delays, effectively breaking CompletableFuture's timeout and delay functionality.
     * <p>
     * <strong>Strongly recommend</strong> using the safe method {@link #cffuOrTimeout(CompletableFuture, long, TimeUnit, Executor)
     * cffuOrTimeout} instead of {@link CompletableFuture#orTimeout CompletableFuture#orTimeout} and this backport method.
     * Using {@link CompletableFuture#orTimeout CompletableFuture#orTimeout} and this backport method is appropriate only when:
     * <ul>
     * <li>the returned CompletableFuture is only read explicitly(e.g. by get/join/resultNow methods), and/or
     * <li>all subsequent actions of dependent CompletableFutures are guaranteed to execute asynchronously
     *    (i.e., the dependent CompletableFutures are created using async methods).
     * </ul> In these cases, using these unsafe methods avoids an unnecessary thread switch when timeout occurs; However, these
     * conditions are difficult to guarantee in practice especially when the returned CompletableFuture is used by others' codes.
     * <p>
     * Note: Before Java 21(Java 20-), {@link CompletableFuture#orTimeout CompletableFuture#orTimeout} method leaks if the
     * future completes exceptionally, more info see <a href="https://bugs.openjdk.org/browse/JDK-8303742">issue JDK-8303742</a>,
     * <a href="https://github.com/openjdk/jdk/pull/13059">PR review openjdk/jdk/13059</a>
     * and <a href="https://github.com/openjdk/jdk/commit/ded6a8131970ac2f7ae59716769e6f6bae3b809a">JDK bugfix commit</a>.
     * The cffu backport logic(for Java 20-) has merged this JDK bugfix.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuOrTimeout(CompletableFuture, long, TimeUnit, Executor)
     */
    @Contract("_, _, _ -> param1")
    public static <F extends CompletableFuture<?>> F orTimeout(F cfThis, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        // NOTE: No need check minimal stage, because checked in cfThis.orTimeout() / cfThis.isDone() below

        // because of bug JDK-8303742, delegate to CF#orTimeout for Java 21+(the bug were fixed at Java 21)
        // instead of Java 9+(CF#orTimeout were introduced since Java 9)
        if (IS_JAVA21_PLUS) {
            cfThis.orTimeout(timeout, unit);
        } else {
            // below code is copied from CompletableFuture#orTimeout with small adoption
            if (!cfThis.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToTimeoutCf(cfThis, timeout, unit);
                peek0(cfThis, new FutureCanceller(f), "CFU#orTimeout");
            }
        }
        return cfThis;
    }

    /**
     * Returns a new CompletableFuture that is completed normally with the given value
     * when the given CompletableFuture is not completed before the given timeout; otherwise the returned
     * CompletableFuture completed with the same successful result or exception of the given CompletableFuture.
     * <p>
     * Uses the default executor of parameter cfThis as {@code executorWhenTimeout}.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture instead of {@code cfThis} to avoid
     * the subsequent usage of the delay thread. This behavior is DIFFERENT from the original CF method {@link
     * CompletableFuture#completeOnTimeout CompletableFuture#completeOnTimeout} and its backport method {@link #completeOnTimeout
     * completeOnTimeout}. More info see the javadoc of {@link #completeOnTimeout completeOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit, Executor)
     */
    public static <T, F extends CompletableFuture<? super T>>
    F cffuCompleteOnTimeout(F cfThis, @Nullable T value, long timeout, TimeUnit unit) {
        return cffuCompleteOnTimeout(cfThis, value, timeout, unit, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that is completed normally with the given value
     * when the given CompletableFuture is not completed before the given timeout; otherwise the returned
     * CompletableFuture completed with the same successful result or exception of the given CompletableFuture.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture instead of {@code cfThis} to avoid
     * the subsequent usage of the delay thread. This behavior is DIFFERENT from the original CF method {@link
     * CompletableFuture#completeOnTimeout CompletableFuture#completeOnTimeout} and its backport method {@link #completeOnTimeout
     * completeOnTimeout}. More info see the javadoc of {@link #completeOnTimeout completeOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param value               the value to use upon timeout
     * @param timeout             how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param executorWhenTimeout the executor to use for asynchronous execution when the wait timed out
     */
    public static <T, F extends CompletableFuture<? super T>>
    F cffuCompleteOnTimeout(F cfThis, @Nullable T value, long timeout, TimeUnit unit, Executor executorWhenTimeout) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");

        return hopExecutorIfAtCfDelayerThread(completeOnTimeout(cfThis, value, timeout, unit), executorWhenTimeout);
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> {@link CompletableFuture#completeOnTimeout CompletableFuture#completeOnTimeout}
     * and this backport method are <strong>UNSAFE</strong>!
     * <p>
     * When the wait timed out, the subsequent non-async actions of the dependent CompletableFutures are performed
     * in CompletableFuture's internal <strong>SINGLE-thread delay executor</strong> (including timeout functionality).
     * This means that the long-running subsequent non-async actions will block this executor thread, preventing it from
     * handling other timeouts and delays, effectively breaking CompletableFuture's timeout and delay functionality.
     * <p>
     * <strong>Strongly recommend</strong> using the safe method {@link #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit, Executor)
     * cffuCompleteOnTimeout} instead of {@link CompletableFuture#completeOnTimeout CompletableFuture#completeOnTimeout} and this backport method.
     * Using {@link CompletableFuture#completeOnTimeout CompletableFuture#completeOnTimeout} and this backport method is appropriate only when:
     * <ul>
     * <li>the returned CompletableFuture is only read explicitly(e.g. by get/join/resultNow methods), and/or
     * <li>all subsequent actions of dependent CompletableFutures are guaranteed to execute asynchronously
     *    (i.e., the dependent CompletableFutures are created using async methods).
     * </ul> In these cases, using these unsafe methods avoids an unnecessary thread switch when timeout occurs; However, these
     * conditions are difficult to guarantee in practice especially when the returned CompletableFuture is used by others' codes.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit, Executor)
     */
    @Contract("_, _, _, _ -> param1")
    public static <T, F extends CompletableFuture<? super T>>
    F completeOnTimeout(F cfThis, @Nullable T value, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        // NOTE: No need check minimal stage, because checked in cfThis.completeOnTimeout() / cfThis.isDone() below
        if (IS_JAVA9_PLUS) {
            cfThis.completeOnTimeout(value, timeout, unit);
        } else {
            // below code is copied from CompletableFuture#completeOnTimeout with small adoption
            if (!cfThis.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToCompleteCf(cfThis, value, timeout, unit);
                peek0(cfThis, new FutureCanceller(f), "CFU#completeOnTimeout");
            }
        }
        return cfThis;
    }

    @SuppressWarnings("unchecked")
    private static <F extends CompletableFuture<?>> F hopExecutorIfAtCfDelayerThread(F cf, Executor executor) {
        CompletableFuture<Object> ret = newIncompleteFuture(cf);

        peek0(cf, (v, ex) -> {
            if (!atCfDelayerThread()) completeCf0(ret, v, ex);
            else screenExecutor(executor).execute(() -> completeCf0(ret, v, ex));
        }, "CFU#hopExecutorIfAtCfDelayerThread");

        return (F) ret;
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Advanced Methods of CompletionStage(compose* and handle-like methods)
    //
    // NOTE about advanced meaning:
    //   - `compose` methods, input function argument return CompletionStage
    //   - handle successful and failed result together(handle*/whenComplete*/peek*)
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType, is composed
     * using the results of the supplied function applied to the exception from the given stage({@code argument cfThis}).
     * <p>
     * <strong>"The exception from the given stage({@code argument cfThis})"</strong> means the cause of
     * the {@link ExecutionException} thrown by {@code get()} or, if {@code get()} throws a different kind
     * of exception, that exception itself; aka the exception is unwrapped by {@link #unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched against
     *                      the exception from argument cfThis. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if cfThis fails with the expected exception type.
     *                      The function's argument is the exception from cfThis.
     * @see #unwrapCfException(Throwable)
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, F extends CompletionStage<? super T>>
    F catchingCompose(F cfThis, Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");

        return (F) cfThis.handle((v, ex) -> {
            if (ex == null) return cfThis;
            Throwable unwrap = unwrapCfException(ex);
            if (!exceptionType.isInstance(unwrap)) return cfThis;
            return fallback.apply((X) unwrap);
        }).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType, is composed
     * using the results of the supplied function applied to the exception from the given stage({@code argument cfThis}),
     * using the default executor of parameter the given stage.
     * <p>
     * <strong>"The exception from the given stage({@code argument cfThis})"</strong> means the cause of
     * the {@link ExecutionException} thrown by {@code get()} or, if {@code get()} throws a different kind
     * of exception, that exception itself; aka the exception is unwrapped by {@link #unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched against
     *                      the exception from argument cfThis. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if cfThis fails with the expected exception type.
     *                      The function's argument is the exception from cfThis.
     * @see #unwrapCfException(Throwable)
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    public static <T, X extends Throwable, F extends CompletionStage<? super T>> F catchingComposeAsync(
            F cfThis, Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback) {
        return catchingComposeAsync(cfThis, exceptionType, fallback, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType, is composed
     * using the results of the supplied function applied to the exception from the given stage({@code argument cfThis}),
     * using the supplied Executor.
     * <p>
     * <strong>"The exception from the given stage({@code argument cfThis})"</strong> means the cause of
     * the {@link ExecutionException} thrown by {@code get()} or, if {@code get()} throws a different kind
     * of exception, that exception itself; aka the exception is unwrapped by {@link #unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched against
     *                      the exception from argument cfThis. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if cfThis fails with the expected exception type.
     *                      The function's argument is the exception from cfThis.
     * @param executor      the executor to use for asynchronous execution
     * @see #unwrapCfException(Throwable)
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, F extends CompletionStage<? super T>> F catchingComposeAsync(
            F cfThis, Class<X> exceptionType,
            Function<? super X, ? extends CompletionStage<T>> fallback, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");
        requireNonNull(executor, "executor is null");

        return (F) cfThis.handle((v, ex) -> {
            if (ex == null) return cfThis;
            Throwable unwrap = unwrapCfException(ex);
            if (!exceptionType.isInstance(unwrap)) return cfThis;
            return cfThis.handleAsync((v1, ex1) -> fallback.apply((X) unwrap), executor).thenCompose(x -> x);
        }).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingCompose(CompletionStage, Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @see #catchingCompose(CompletionStage, Class, Function)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, F extends CompletionStage<? super T>>
    F exceptionallyCompose(F cfThis, Function<Throwable, ? extends CompletionStage<T>> fn) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        if (IS_JAVA12_PLUS) {
            return (F) cfThis.exceptionallyCompose((Function) fn);
        }
        // below code is copied from CompletionStage.exceptionallyCompose
        return (F) cfThis.handle((v, ex) -> (ex == null) ? cfThis : fn.apply(ex)).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using the default executor of parameter cfThis.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingComposeAsync(CompletionStage, Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @see #catchingComposeAsync(CompletionStage, Class, Function)
     */
    public static <T, F extends CompletionStage<? super T>>
    F exceptionallyComposeAsync(F cfThis, Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally, is composed using
     * the results of the supplied function applied to given stage's exception, using the supplied Executor.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingComposeAsync(CompletionStage, Class, Function, Executor)}
     * instead in your business application.
     *
     * @param fn       the function to use to compute the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @see #catchingComposeAsync(CompletionStage, Class, Function, Executor)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, F extends CompletionStage<? super T>>
    F exceptionallyComposeAsync(F cfThis, Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA12_PLUS) {
            return (F) cfThis.exceptionallyComposeAsync((Function) fn, executor);
        }
        // below code is copied from CompletionStage.exceptionallyComposeAsync
        return (F) cfThis.handle((v, ex) -> (ex == null) ? cfThis :
                cfThis.handleAsync((v1, ex1) -> fn.apply(ex1), executor).thenCompose(x -> x)
        ).thenCompose(x -> x);
    }

    /**
     * Peeks the result by executing the given action when the given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * <p>
     * <strong>NOTE:</strong> When using {@link CompletionStage#whenComplete(BiConsumer)},
     * if the input stage completes normally but the supplied action throws an exception, the returned stage will contain
     * a <strong>DIFFERENT</strong> result than the input stage. This subtle behavior of {@code whenComplete} can lead to
     * bugs when you only want to <strong>peek</strong> at the stage's result (e.g. for logging) without modifying it.<br>
     * In contrast, this {@code peek} method guarantees that the returned stage (which is the input stage)
     * will maintain its original result, regardless of whether the supplied action throws an exception or not.
     * <p>
     * <strong>CAUTION:</strong> Since this method returns the input stage directly, the execution order between
     * the given action and other actions added to the input stage cannot be guaranteed. The action should be treated
     * as "fire and forget" - do not make any assumptions about timing or execution sequence.
     * <p>
     * Unlike method {@link CompletionStage#handle(BiFunction)} and like method
     * {@link CompletionStage#whenComplete(BiConsumer)}, this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return the given stage
     * @see CompletionStage#whenComplete(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    @Contract("_, _ -> param1")
    public static <T, F extends CompletionStage<? extends T>>
    F peek(F cfThis, BiConsumer<? super T, ? super Throwable> action) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");

        return peek0(cfThis, action, "CFU#peek");
    }

    /**
     * Peeks the result by executing the given action using the default executor of parameter cfThis
     * when the given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * <p>
     * <strong>NOTE:</strong> When using {@link CompletionStage#whenCompleteAsync(BiConsumer)},
     * if the input stage completes normally but the supplied action throws an exception, the returned stage will contain
     * a <strong>DIFFERENT</strong> result than the input stage. This subtle behavior of {@code whenComplete} can lead to
     * bugs when you only want to <strong>peek</strong> at the stage's result (e.g. for logging) without modifying it.<br>
     * In contrast, this {@code peekAsync} method guarantees that the returned stage (which is the input stage)
     * will maintain its original result, regardless of whether the supplied action throws an exception or not.
     * <p>
     * <strong>CAUTION:</strong> Since this method returns the input stage directly, the execution order between
     * the given action and other actions added to the input stage cannot be guaranteed. The action should be treated
     * as "fire and forget" - do not make any assumptions about timing or execution sequence.
     * <p>
     * Unlike method {@link CompletionStage#handleAsync(BiFunction)} and like method {@link
     * CompletionStage#whenCompleteAsync(BiConsumer)}, this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return the given stage
     * @see CompletionStage#whenCompleteAsync(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    @Contract("_, _ -> param1")
    public static <T, F extends CompletionStage<? extends T>>
    F peekAsync(F cfThis, BiConsumer<? super T, ? super Throwable> action) {
        return peekAsync(cfThis, action, defaultExecutor(cfThis));
    }

    /**
     * Peeks the result by executing the given action using the supplied executor
     * when the given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * <p>
     * <strong>NOTE:</strong> When using {@link CompletionStage#whenCompleteAsync(BiConsumer, Executor)},
     * if the input stage completes normally but the supplied action throws an exception, the returned stage will contain
     * a <strong>DIFFERENT</strong> result than the input stage. This subtle behavior of {@code whenComplete} can lead to
     * bugs when you only want to <strong>peek</strong> at the stage's result (e.g. for logging) without modifying it.<br>
     * In contrast, this {@code peekAsync} method guarantees that the returned stage (which is the input stage)
     * will maintain its original result, regardless of whether the supplied action throws an exception or not.
     * <p>
     * <strong>CAUTION:</strong> Since this method returns the input stage directly, the execution order between
     * the given action and other actions added to the input stage cannot be guaranteed. The action should be treated
     * as "fire and forget" - do not make any assumptions about timing or execution sequence.
     * <p>
     * Unlike method {@link CompletionStage#handleAsync(BiFunction, Executor)} and like method {@link
     * CompletionStage#whenCompleteAsync(BiConsumer, Executor)}, this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return the given stage
     * @see CompletionStage#whenCompleteAsync(BiConsumer, Executor)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    @Contract("_, _, _ -> param1")
    public static <T, F extends CompletionStage<? extends T>>
    F peekAsync(F cfThis, BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return peekAsync0(cfThis, action, "CFU#peekAsync", executor);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Read(explicitly) Methods of CompletableFuture(including Future)
    //
    //    - get()               // BLOCKING!
    //    - get(timeout, unit)  // BLOCKING!
    //    - join()              // BLOCKING!
    //    - join(timeout, unit) // BLOCKING!
    //    - getNow(T valueIfAbsent)
    //    - getSuccessNow(T valueIfNotSuccess)
    //    - resultNow()
    //    - exceptionNow()
    //
    //    - isDone()
    //    - isCompletedExceptionally()
    //    - isCancelled()
    //    - state()
    //    - cffuState()
    //
    // NOTE about ExecutionException or CompletionException when the computation threw an exception:
    //   - get methods throw ExecutionException(checked exception)
    //     these methods exists in `Future` interface since Java 5
    //   - getNow/join throw CompletionException(unchecked exception),
    //     these methods exists in `CompletableFuture` since Java 8
    ////////////////////////////////////////////////////////////

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * <p>
     * <strong>CAUTION:</strong> if the wait timed out, this method throws an (unchecked) {@link CompletionException}
     * with the {@link TimeoutException} as its cause;
     * NOT throws a (checked) {@link TimeoutException} like {@link CompletableFuture#get(long, TimeUnit)}.
     * <p>
     * <strong>NOTE:</strong> Calling this method
     * <p>
     * {@code result = CompletableFutureUtils.join(cf, timeout, unit);}
     * <p>
     * is the same as:
     *
     * <pre>{@code result = cf.copy() // defensive copy to avoid writing this cf unexpectedly
     *     .orTimeout(timeout, unit)
     *     .join();
     * }</pre>
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if given future completed exceptionally
     *                               or a completion computation threw an exception
     *                               or the wait timed out(with the {@code TimeoutException} as its cause)
     * @see CompletableFuture#join()
     */
    @Blocking
    @Nullable
    public static <T> T join(CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        // defensive copy input cf to avoid writing it by `orTimeout`
        return cfThis.isDone() ? cfThis.join() : orTimeout(copy(cfThis), timeout, unit).join();
    }

    /**
     * Returns the result value if the given stage is completed normally, else returns the given valueIfNotSuccess.
     * <p>
     * This method is guaranteed not to throw {@link CompletionException}, {@link ExecutionException},
     * {@link CancellationException} and {@link IllegalStateException}.
     *
     * @param valueIfNotSuccess the value to return if not completed normally
     * @return the result value, if completed normally, else the given valueIfNotSuccess
     * @throws NullPointerException if the given CompletableFuture is {@code null}
     */
    @Contract(pure = true)
    @Nullable
    public static <T> T getSuccessNow(CompletableFuture<? extends T> cfThis, @Nullable T valueIfNotSuccess) {
        requireNonNull(cfThis, "cfThis is null");
        // NOTE: No need check minimal stage, because checked in cfThis.isDone() below
        try {
            return cfThis.isDone() && !cfThis.isCompletedExceptionally() ? cfThis.join() : valueIfNotSuccess;
        } catch (CancellationException | CompletionException e) {
            // these exceptions can only occur if the cfThis was re-completed exceptionally using obtrudeException
            return valueIfNotSuccess;
        }
    }

    /**
     * Returns the computed result, without waiting.
     * <p>
     * This method is for cases where the caller knows that the task has already completed normally,
     * for example when filtering a stream of Future objects for the successful tasks
     * and using a mapping operation to obtain a stream of results.
     *
     * <pre>{@code results = futures.stream()
     *     .filter(f -> f.state() == Future.State.SUCCESS)
     *     .map(Future::resultNow)
     *     .toList();
     * }</pre>
     *
     * @return the computed result
     * @throws IllegalStateException if the task has not completed or the task did not complete with a result
     */
    @Contract(pure = true)
    @Nullable
    public static <T> T resultNow(Future<? extends T> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        if (IS_JAVA19_PLUS) {
            return cfThis.resultNow();
        }

        // below code is copied from Future.resultNow

        if (!cfThis.isDone()) throw new IllegalStateException("Task has not completed");
        if (cfThis.isCancelled()) throw new IllegalStateException("Task was cancelled");
        // simple path for CompletableFuture/Cffu
        if (cfThis instanceof CompletableFuture) {
            if (((CompletableFuture<?>) cfThis).isCompletedExceptionally())
                throw new IllegalStateException("Task completed with exception");
        } else if (cfThis instanceof Cffu) {
            if (((Cffu<?>) cfThis).isCompletedExceptionally())
                throw new IllegalStateException("Task completed with exception");
        }

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return cfThis.get();
                } catch (InterruptedException e) {
                    interrupted = true;
                } catch (ExecutionException e) {
                    throw new IllegalStateException("Task completed with exception");
                } catch (CancellationException e) {
                    throw new IllegalStateException("Task was cancelled");
                }
            }
        } finally {
            if (interrupted) Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the exception thrown by the task, without waiting.
     * <p>
     * This method is for cases where the caller knows that the task has already completed with an exception.
     *
     * @return the exception thrown by the task
     * @throws IllegalStateException if the task has not completed, the task completed normally,
     *                               or the task was cancelled
     */
    @Contract(pure = true)
    public static Throwable exceptionNow(Future<?> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        if (IS_JAVA19_PLUS) {
            return cfThis.exceptionNow();
        }

        // below code is copied from Future.exceptionNow

        if (!cfThis.isDone()) throw new IllegalStateException("Task has not completed");
        if (cfThis.isCancelled()) throw new IllegalStateException("Task was cancelled");

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    cfThis.get();
                    throw new IllegalStateException("Task completed with a result");
                } catch (InterruptedException e) {
                    interrupted = true;
                } catch (ExecutionException e) {
                    return e.getCause();
                }
            }
        } finally {
            if (interrupted) Thread.currentThread().interrupt();
        }
    }

    /**
     * Returns the computation state ({@link CffuState}). This method provides equivalent functionality to
     * {@link CompletableFuture#state()} with backwards compatibility for {@code Java 18-}.
     *
     * @return the computation state
     * @see Future#state()
     */
    @Contract(pure = true)
    public static CffuState state(Future<?> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        if (IS_JAVA19_PLUS) {
            return CffuState.toCffuState(cfThis.state());
        }

        // below code is copied from Future#state() with small adoption

        if (!cfThis.isDone()) return CffuState.RUNNING;
        if (cfThis.isCancelled()) return CffuState.CANCELLED;
        // simple path for CompletableFuture/Cffu
        if (cfThis instanceof CompletableFuture)
            return ((CompletableFuture<?>) cfThis).isCompletedExceptionally() ? CffuState.FAILED : CffuState.SUCCESS;
        else if (cfThis instanceof Cffu)
            return ((Cffu<?>) cfThis).isCompletedExceptionally() ? CffuState.FAILED : CffuState.SUCCESS;

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    cfThis.get();  // may throw InterruptedException when done
                    return CffuState.SUCCESS;
                } catch (InterruptedException e) {
                    interrupted = true;
                } catch (ExecutionException e) {
                    return CffuState.FAILED;
                }
            }
        } finally {
            if (interrupted) Thread.currentThread().interrupt();
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Write Methods of CompletableFuture
    ////////////////////////////////////////////////////////////

    /**
     * Completes given CompletableFuture with the result of the given Supplier function invoked
     * from an asynchronous task using the default executor of parameter cfThis.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @return the given CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier)
     */
    @Contract("_, _ -> param1")
    public static <T, F extends CompletableFuture<? super T>> F completeAsync(F cfThis, Supplier<? extends T> supplier) {
        return completeAsync(cfThis, supplier, defaultExecutor(cfThis));
    }

    /**
     * Completes given CompletableFuture with the result of the given Supplier function invoked
     * from an asynchronous task using the given executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the given CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier, Executor)
     */
    @Contract("_, _, _ -> param1")
    public static <T, F extends CompletableFuture<? super T>>
    F completeAsync(F cfThis, Supplier<? extends T> supplier, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(supplier, "supplier is null");
        // NOTE: do NOT translate executor by screenExecutor method; same as CompletableFuture.completeAsync
        requireNonNull(executor, "executor is null");
        if (IS_JAVA9_PLUS) {
            cfThis.completeAsync(supplier, executor);
        } else {
            // NOTE: No need check minimal stage, because Java 8(not Java 9+) NOT support minimal stage

            // below code is copied from CompletableFuture#completeAsync with small adoption
            executor.execute(new CfCompleterBySupplier<>(cfThis, supplier));
        }
        return cfThis;
    }

    /**
     * If not already completed, completes given CompletableFuture with the exception result
     * of the given Supplier function invoked from an asynchronous task using the default executor of parameter cfThis.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @return the given CompletableFuture
     * @see CompletableFuture#completeExceptionally(Throwable)
     */
    @Contract("_, _ -> param1")
    public static <F extends CompletableFuture<?>>
    F completeExceptionallyAsync(F cfThis, Supplier<? extends Throwable> supplier) {
        return completeExceptionallyAsync(cfThis, supplier, defaultExecutor(cfThis));
    }

    /**
     * If not already completed, completes given CompletableFuture with the exception result
     * of the given Supplier function invoked from an asynchronous task using the given executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the given CompletableFuture
     * @see CompletableFuture#completeExceptionally(Throwable)
     */
    @Contract("_, _, _ -> param1")
    public static <F extends CompletableFuture<?>>
    F completeExceptionallyAsync(F cfThis, Supplier<? extends Throwable> supplier, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(supplier, "supplier is null");
        // NOTE: do NOT translate executor by screenExecutor method; same as CompletableFuture.completeAsync
        requireNonNull(executor, "executor is null");
        if (isMinStageCf(cfThis)) throw new UnsupportedOperationException();

        executor.execute(new CfExCompleterBySupplier(cfThis, supplier));
        return cfThis;
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Re-Config Methods of CompletableFuture
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletionStage that is completed normally with the same value as given CompletableFuture
     * when it completes normally, and cannot be independently completed or otherwise used in ways
     * not defined by the methods of interface {@link CompletionStage}.
     * If given CompletableFuture completes exceptionally, then the returned CompletionStage completes exceptionally
     * with a CompletionException with given exception as cause.
     * <p>
     * <strong>CAUTION:</strong> if run on old Java 8 (which does not support *minimal* CompletionStage),
     * this method just returns a *normal* CompletableFuture instance which is NOT a *minimal* CompletionStage.
     *
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> minimalCompletionStage(CompletableFuture<T> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        return IS_JAVA9_PLUS ? cfThis.minimalCompletionStage() : cfThis.thenApply(x -> x);
    }

    /**
     * Returns a new CompletableFuture that is completed normally with the same value as this CompletableFuture when
     * it completes normally. If this CompletableFuture completes exceptionally, then the returned CompletableFuture
     * completes exceptionally with a CompletionException with this exception as cause. The behavior is equivalent
     * to {@code thenApply(x -> x)}. This method may be useful as a form of "defensive copying", to prevent clients
     * from completing, while still being able to arrange dependent actions.
     *
     * @see CompletableFuture#copy()
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> copy(CompletableFuture<T> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        return IS_JAVA9_PLUS ? cfThis.copy() : cfThis.thenApply(x -> x);
    }

    /**
     * Returns the default Executor of parameter cfThis used for async methods that do not specify an Executor.
     * <p>
     * The default executor of CompletableFuture(<strong>NOT</strong> including the customized subclasses of CompletableFuture)
     * uses the {@link ForkJoinPool#commonPool()} if it supports more than one parallel thread, or else an Executor using one
     * thread per async task. <strong>CAUTION:</strong> This executor may be not suitable for common business use(io intensive).
     *
     * @see CompletableFuture#defaultExecutor()
     */
    @Contract(pure = true)
    public static Executor defaultExecutor(CompletionStage<?> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        // FIXME hard-code runtime type: CompletableFuture and Cffu...
        //       need a SPI in order to support other CompletionStage subclasses equivalently
        if (cfThis instanceof CompletableFuture)
            return IS_JAVA9_PLUS ? ((CompletableFuture<?>) cfThis).defaultExecutor() : ASYNC_POOL;
        if (cfThis instanceof Cffu) return ((Cffu<?>) cfThis).defaultExecutor();
        throw new UnsupportedOperationException("Unsupported CompletionStage subclass: " + cfThis.getClass());
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Convenient Util Methods
    //
    //    - toCompletableFutureArray:     CompletionStage[](including Cffu) -> CF[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts input {@link CompletionStage} (including {@link Cffu}/{@link CompletableFuture})
     * array element by {@link CompletionStage#toCompletableFuture()}.
     *
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see CompletionStage#toCompletableFuture()
     * @see CompletableFuture#toCompletableFuture()
     * @see Cffu#toCompletableFuture()
     * @see CffuFactory#toCffuArray(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] toCompletableFutureArray(CompletionStage<T>... stages) {
        requireNonNull(stages, "stages is null");
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = requireNonNull(stages[i], "stage" + (i + 1) + " is null").toCompletableFuture();
        }
        return ret;
    }

    /**
     * Converts input {@link CompletableFuture} list to CompletableFuture array.
     *
     * @see #toCompletableFutureArray(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T>[] completableFutureListToArray(List<CompletableFuture<T>> cfList) {
        requireNonNull(cfList, "cfList is null");
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] a = new CompletableFuture[cfList.size()];
        return cfList.toArray(a);
    }

    /**
     * Unwraps CompletableFuture exception ({@link CompletionException} or {@link ExecutionException})
     * to its cause exception. If the input exception is not a {@code CompletableFuture}/{@code ExecutionException}
     * or has no cause, contains a cyclic chain of CompletableFuture exceptions, or is null, returns the input exception.
     *
     * @param ex the exception to be unwrapped, may be null
     * @see com.google.common.base.Throwables#getRootCause(Throwable) Guava method Throwables#getRootCause(),
     * the loop detection code using fast and slow pointers is adapted from it
     */
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static @Nullable Throwable unwrapCfException(@Nullable final Throwable ex) {
        // keep a slow pointer that slowly walks the causal chain.
        // if the fast pointer ever catches the slower pointer, then there's a loop.
        Throwable fastPointer = ex, slowPointer = ex;
        boolean advanceSlowPointer = false;

        while (true) {
            if (!(fastPointer instanceof CompletionException) && !(fastPointer instanceof ExecutionException))
                return fastPointer;

            final Throwable cause = fastPointer.getCause();
            // if there is no cause exceptions except for CF exceptions, return the input exception as the best option.
            if (cause == null) return ex;

            fastPointer = cause;
            // if a loop is detected in the causal chain, return the input exception as the best option.
            if (fastPointer == slowPointer) return ex;

            if (advanceSlowPointer) slowPointer = slowPointer.getCause();
            advanceSlowPointer = !advanceSlowPointer; // only advance every other iteration
        }
    }

    private CompletableFutureUtils() {}
}
