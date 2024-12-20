package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;

import static io.foldright.cffu.Delayer.atCfDelayerThread;
import static io.foldright.cffu.ExceptionReporter.reportUncaughtException;
import static io.foldright.cffu.InternalCommonUtils.*;
import static io.foldright.cffu.LLCF.*;
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
     *    - return generic type T but constrained type TupleX
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
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFailFastAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allResultsOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            Executor executor, @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allSuccessResultsOf0(valueIfFailed, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf},
     * wraps input suppliers to CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf}
     * documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            Executor executor, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return mostSuccessResultsOf0(executor, valueIfNotSuccess, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allResultsOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return anySuccessOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input suppliers to
     * CompletableFuture by {@link CompletableFuture#supplyAsync(Supplier)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return f_cast(CompletableFuture.anyOf(wrapSuppliers0(executor, suppliers)));
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
    public static CompletableFuture<Void> mRunFailFastAsync(Runnable... actions) {
        return mRunFailFastAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunFailFastAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return allFailFastOf0(wrapRunnables0(executor, actions));
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

        return CompletableFuture.allOf(wrapRunnables0(executor, actions));
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunAnySuccessAsync(Runnable... actions) {
        return mRunAnySuccessAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunAnySuccessAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return anySuccessOf0(wrapRunnables0(executor, actions));
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunAnyAsync(Runnable... actions) {
        return mRunAnyAsync(ASYNC_POOL, actions);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> mRunAnyAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return f_cast(CompletableFuture.anyOf(wrapRunnables0(executor, actions)));
    }

    private static CompletableFuture<Void>[] wrapRunnables0(Executor executor, Runnable[] actions) {
        return mapArray(actions, CompletableFuture[]::new, a -> CompletableFuture.runAsync(a, executor));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns {@code CompletableFuture<T>} with generic type {@code T} but constrained to type TupleX.
     */
    private static <T> CompletableFuture<T> f_allTupleOf0(boolean failFast, CompletionStage<?>[] stages) {
        final Object[] result = new Object[stages.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(stages, result);

        final CompletableFuture<Void> resultSetter;
        if (failFast) resultSetter = allFailFastOf0(resultSetterCfs);
        else resultSetter = CompletableFuture.allOf(resultSetterCfs);

        return resultSetter.thenApply(unused -> f_tupleOf0(result));
    }

    /**
     * Returns generic type {@code T} but constrained to type TupleX.
     */
    @SuppressWarnings("unchecked")
    private static <T> T f_tupleOf0(Object[] elements) {
        final int len = elements.length;
        final Object ret;
        if (len == 2) ret = Tuple2.of(elements[0], elements[1]);
        else if (len == 3) ret = Tuple3.of(elements[0], elements[1], elements[2]);
        else if (len == 4) ret = Tuple4.of(elements[0], elements[1], elements[2], elements[3]);
        else ret = Tuple5.of(elements[0], elements[1], elements[2], elements[3], elements[4]);
        return (T) ret;
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variance of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
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
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2);
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variance of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    private static <T> CompletableFuture<T> f_mostSuccessTupleOf0(
            Executor executorWhenTimeout, long timeout, TimeUnit unit, CompletionStage<?>[] stages) {
        // MUST be *Non-Minimal* CF instances in order to read results(`getSuccessNow`),
        // otherwise UnsupportedOperationException
        final CompletableFuture<Object>[] cfArray = toNonMinCfArray0(stages);
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
     * Tuple variance of {@link #mSupplyAsync(Supplier[])}.
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyAsync(Supplier[])}.
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyAsync(Supplier[])}.
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Tuple variance of {@link #mSupplyAsync(Supplier[])}.
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variance of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
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
     * @see Futures#allAsList(ListenableFuture[]) the equivalent Guava method allAsList()
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
     * @see Futures#successfulAsList(ListenableFuture[]) the equivalent Guava method successfulAsList()
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
     * @param executorWhenTimeout the async executor when triggered by timeout
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
            // Defensive copy input cf to non-minimal-stage instance in order to
            // 1. avoid writing it by `cffuCompleteOnTimeout` and is able to read its result(`getSuccessNow`)
            // 2. ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
            final CompletableFuture<T> f = toNonMinCfCopy0(cfs[0]);
            return cffuCompleteOnTimeout(f, valueIfNotSuccess, timeout, unit, executorWhenTimeout)
                    .handle((unused, ex) -> arrayList(getSuccessNow(f, valueIfNotSuccess)));
        }

        // MUST be non-minimal-stage CF instances in order to read results(`getSuccessNow`),
        // otherwise UnsupportedOperationException
        final CompletableFuture<T>[] cfArray = toNonMinCfArray0(cfs);
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
        if (len == 1) return toNonMinCf0(cfs[0]).thenApply(InternalCommonUtils::arrayList);

        final Object[] result = new Object[len];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(cfs, result);

        final CompletableFuture<Void> resultSetter;
        if (failFast) resultSetter = allFailFastOf0(resultSetterCfs);
        else resultSetter = CompletableFuture.allOf(resultSetterCfs);

        return f_cast(resultSetter.thenApply(unused -> arrayList(result)));
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
     * @see Futures#whenAllSucceed(ListenableFuture[]) the equivalent Guava method whenAllSucceed()
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
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[len + 1];
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
     * @see Futures#whenAllComplete(ListenableFuture[]) the equivalent Guava method whenAllComplete()
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
     * Returns a cf array whose elements do the result collection.
     */
    private static <T> CompletableFuture<Void>[] createResultSetterCfs(
            CompletionStage<? extends T>[] stages, T[] result) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] resultSetterCfs = new CompletableFuture[result.length];
        return fillArray(resultSetterCfs, i -> f_toCf0(stages[i]).thenAccept(v -> result[i] = v));
    }

    private static <T> void fill0(CompletionStage<? extends T>[] stages,
                                  CompletableFuture<? extends T>[] successOrBeIncomplete,
                                  CompletableFuture<? extends T>[] failedOrBeIncomplete) {
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
        // Defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
        // 1. avoid writing the input cf unexpectedly by caller code
        // 2. the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCfCopy0(cfs[0]);

        // NOTE: fill ONE MORE element of successOrBeIncompleteCfs LATER
        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len + 1];
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[len];
        fill0(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE:
        //       a cf that is completed exceptionally when all given cfs completed exceptionally, otherwise be incomplete
        successOrBeIncomplete[len] = CompletableFuture.allOf(failedOrBeIncomplete);

        return f_cast(CompletableFuture.anyOf(successOrBeIncomplete));
    }

    /**
     * Returns a new CompletableFuture that is completed with the same successful result or exception
     * of any of the given stages when one stage completes.
     * If no stages are provided, returns an incomplete CompletableFuture.
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
        // Defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
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
     * Tuple variance of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variance of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variance of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variance of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variance of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
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
     * Tuple variance of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
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
     * Tuple variance of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
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
     * Tuple variance of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
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
     * Tuple variance of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
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
     * Tuple variance of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variance of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variance of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variance of {@link #allResultsOf(CompletionStage[])}.
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
     * <strong>CAUTION:</strong> if run on old Java 8(not support *minimal* CompletionStage),
     * just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
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
     * <strong>CAUTION:</strong> if run on old Java 8(not support *minimal* CompletionStage),
     * just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> failedStage(Throwable ex) {
        return IS_JAVA9_PLUS ? CompletableFuture.failedStage(ex) : failedFuture(ex);
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
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> allResultsOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            @Nullable U valueIfFailed, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> allSuccessResultsOf0(valueIfFailed, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * mostSuccessResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * mostSuccessResultsOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> mostSuccessResultsOf0(
                executor, valueIfNotSuccess, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> allResultsOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> anySuccessOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input functions to CompletableFuture by
     * {@link CompletableFuture#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
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
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> f_cast(CompletableFuture.anyOf(wrapFunctions0(executor, v, fns))));
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
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(v -> allFailFastOf0(wrapConsumers0(executor, v, actions)));
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
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(v -> CompletableFuture.allOf(wrapConsumers0(executor, v, actions)));
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
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(v -> anySuccessOf0(wrapConsumers0(executor, v, actions)));
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
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(v -> f_cast(CompletableFuture.anyOf(wrapConsumers0(executor, v, actions))));
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
    public static CompletableFuture<Void> thenMRunFailFastAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunFailFastAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunFailFastAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> allFailFastOf0(wrapRunnables0(executor, actions)));
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
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> CompletableFuture.allOf(wrapRunnables0(executor, actions)));
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAnySuccessAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> anySuccessOf0(wrapRunnables0(executor, actions)));
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunAnyAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAnyAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to
     * CompletableFuture by {@link CompletableFuture#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    public static CompletableFuture<Void> thenMRunAnyAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> f_cast(CompletableFuture.anyOf(wrapRunnables0(executor, actions))));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions-Tuple(thenMTuple*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variance of {@link #thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variance of {@link #thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2);
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_mostSuccessTupleOf0(executor, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3);
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_mostSuccessTupleOf0(executor, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_mostSuccessTupleOf0(executor, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variance of {@link #thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4,
            Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_mostSuccessTupleOf0(executor, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Function[])}.
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variance of {@link #thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
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
        final CompletableFuture incomplete = new CompletableFuture();

        CompletableFuture thisSuccessOrBeIncomplete = exceptionallyCompose(cfThis, ex -> incomplete);
        CompletionStage otherSuccessOrBeIncomplete = exceptionallyCompose(other, ex -> incomplete);
        CompletableFuture cfValue = thisSuccessOrBeIncomplete.thenCombine(otherSuccessOrBeIncomplete, Tuple2::of);

        CompletableFuture thisFailedOrBeIncomplete = cfThis.thenCompose(v -> incomplete);
        CompletionStage otherFailedOrBeIncomplete = other.thenCompose(v -> incomplete);
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
        final CompletableFuture incomplete = new CompletableFuture();

        CompletableFuture thisSuccessOrBeIncomplete = exceptionallyCompose(cfThis, ex -> incomplete);
        CompletionStage otherSuccessOrBeIncomplete = exceptionallyCompose(other, ex -> incomplete);
        CompletableFuture cfValue = thisSuccessOrBeIncomplete.applyToEither(otherSuccessOrBeIncomplete, x -> x);

        CompletableFuture thisFailedOrBeIncomplete = cfThis.thenCompose(v -> incomplete);
        CompletionStage otherFailedOrBeIncomplete = other.thenCompose(v -> incomplete);
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
     * is executed with given stage's exception as the argument to the supplied function.
     * Otherwise, the returned stage contains same result as input CompletionStage.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @see Futures#catching the equivalent Guava method catching()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, C extends CompletionStage<? super T>>
    C catching(C cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");

        return (C) cfThis.handle((v, ex) -> (ex == null || !exceptionType.isAssignableFrom(ex.getClass()))
                ? cfThis : completedFuture(fallback.apply((X) ex))
        ).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally with the given exceptionType,
     * is executed with given stage's exception as the argument to the supplied function,
     * using the default executor of parameter cfThis.
     * Otherwise, the returned stage contains same result as input CompletionStage.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @see Futures#catching the equivalent Guava method catching()
     */
    public static <T, X extends Throwable, C extends CompletionStage<? super T>>
    C catchingAsync(C cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        return catchingAsync(cfThis, exceptionType, fallback, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally with the given exceptionType,
     * is executed with given stage's exception as the argument to the supplied function, using the supplied Executor.
     * Otherwise, the returned stage contains same result as input CompletionStage.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @param executor      the executor to use for asynchronous execution
     * @see Futures#catching the equivalent Guava method catching()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, C extends CompletionStage<? super T>>
    C catchingAsync(C cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");
        requireNonNull(executor, "executor is null");

        return (C) cfThis.handle((v, ex) -> (ex == null || !exceptionType.isAssignableFrom(ex.getClass()))
                ? cfThis : cfThis.<T>handleAsync((v1, ex1) -> fallback.apply((X) ex1), executor)
        ).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the default executor of parameter cfThis.
     * Otherwise, if given stage completes normally, then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strong recommend</strong> using {@link #catchingAsync(CompletionStage, Class, Function)}
     * instead in your biz application.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @see #catchingAsync(CompletionStage, Class, Function)
     */
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cfThis, Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the supplied Executor. Otherwise,
     * if given stage completes normally, then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strong recommend</strong> using {@link #catchingAsync(CompletionStage, Class, Function, Executor)}
     * instead in your biz application.
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @see #catchingAsync(CompletionStage, Class, Function, Executor)
     */
    @SuppressWarnings("unchecked")
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cfThis, Function<Throwable, ? extends T> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA12_PLUS) {
            return (C) cfThis.exceptionallyAsync(fn, executor);
        }
        // below code is copied from CompletionStage#exceptionallyAsync
        return (C) cfThis.handle((v, ex) -> (ex == null) ? cfThis :
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
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from
     * the original CF method {@link CompletableFuture#orTimeout} and its backport method {@link #orTimeout},
     * because the returned new CF instance avoids the subsequent usage of the delay thread.
     * More info see the javadoc of {@link #orTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @see #cffuOrTimeout(CompletableFuture, long, TimeUnit, Executor)
     */
    public static <C extends CompletableFuture<?>> C cffuOrTimeout(C cfThis, long timeout, TimeUnit unit) {
        return cffuOrTimeout(cfThis, timeout, unit, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that is completed exceptionally with a {@link TimeoutException}
     * when the given CompletableFuture is not completed before the given timeout; otherwise the returned
     * CompletableFuture completed with the same successful result or exception of the given CompletableFuture.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from
     * the original CF method {@link CompletableFuture#orTimeout} and its backport method {@link #orTimeout},
     * because the returned new CF instance avoids the subsequent usage of the delay thread.
     * More info see the javadoc of {@link #orTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param timeout             how long to wait before completing exceptionally with a TimeoutException,
     *                            in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param executorWhenTimeout the async executor when triggered by timeout
     */
    public static <C extends CompletableFuture<?>> C cffuOrTimeout(
            C cfThis, long timeout, TimeUnit unit, Executor executorWhenTimeout) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");

        return hopExecutorIfAtCfDelayerThread(orTimeout(cfThis, timeout, unit), executorWhenTimeout);
    }

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method and {@link CompletableFuture#orTimeout} is <strong>UNSAFE</strong>!
     * <p>
     * When triggered by timeout, the subsequent non-async actions of the dependent CompletableFutures
     * are performed in the <strong>SINGLE thread builtin executor</strong>
     * of CompletableFuture for delay execution (including timeout function).
     * So the long-running subsequent non-async actions lead to the CompletableFuture dysfunction
     * (including delay execution and timeout).
     * <p>
     * <strong>Strong recommend</strong> using the safe method {@link #cffuOrTimeout(CompletableFuture,
     * Executor, long, TimeUnit)} instead of this method and {@link CompletableFuture#orTimeout}.<br>
     * Unless all subsequent actions of dependent CompletableFutures is ensured executing async(aka. the dependent
     * CompletableFutures is created by async methods), using this method and {@link CompletableFuture#orTimeout}
     * is one less thread switch of task execution when triggered by timeout.
     * <p>
     * Note: Before Java 21(Java 20-), {@link CompletableFuture#orTimeout} leaks if the future completes exceptionally,
     * more info see <a href="https://bugs.openjdk.org/browse/JDK-8303742">issue JDK-8303742</a>,
     * <a href="https://github.com/openjdk/jdk/pull/13059">PR review openjdk/jdk/13059</a>
     * and <a href="https://github.com/openjdk/jdk/commit/ded6a8131970ac2f7ae59716769e6f6bae3b809a">JDK bugfix commit</a>.
     * The cffu backport logic(for Java 20-) has merged the fix of this JDK bug.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuOrTimeout(CompletableFuture, Executor, long, TimeUnit)
     */
    @Contract("_, _, _ -> param1")
    public static <C extends CompletableFuture<?>> C orTimeout(C cfThis, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        // NOTE: No need check minimal stage, because checked in cfThis.orTimeout() / cfThis.isDone() below

        // because of bug JDK-8303742, delegate to CompletableFuture.orTimeout() when Java 21+(not Java 9+)
        if (IS_JAVA21_PLUS) {
            cfThis.orTimeout(timeout, unit);
        } else {
            // below code is copied from CompletableFuture#orTimeout with small adoption
            if (!cfThis.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToTimeoutCf(cfThis, timeout, unit);
                cfThis.whenComplete(new FutureCanceller(f));
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
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from the
     * original CF method {@link CompletableFuture#completeOnTimeout} and its backport method {@link #completeOnTimeout},
     * because the returned new CF instance avoids the subsequent usage of the delay thread.
     * More info see the javadoc of {@link #completeOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit, Executor)
     */
    public static <T, C extends CompletableFuture<? super T>>
    C cffuCompleteOnTimeout(C cfThis, @Nullable T value, long timeout, TimeUnit unit) {
        return cffuCompleteOnTimeout(cfThis, value, timeout, unit, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that is completed normally with the given value
     * when the given CompletableFuture is not completed before the given timeout; otherwise the returned
     * CompletableFuture completed with the same successful result or exception of the given CompletableFuture.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is DIFFERENT from the
     * original CF method {@link CompletableFuture#completeOnTimeout} and its backport method {@link #completeOnTimeout},
     * because the returned new CF instance avoids the subsequent usage of the delay thread.
     * More info see the javadoc of {@link #completeOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param value               the value to use upon timeout
     * @param timeout             how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param executorWhenTimeout the async executor when triggered by timeout
     */
    public static <T, C extends CompletableFuture<? super T>>
    C cffuCompleteOnTimeout(C cfThis, @Nullable T value, long timeout, TimeUnit unit, Executor executorWhenTimeout) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");

        return hopExecutorIfAtCfDelayerThread(completeOnTimeout(cfThis, value, timeout, unit), executorWhenTimeout);
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method and {@link CompletableFuture#completeOnTimeout} is <strong>UNSAFE</strong>!
     * <p>
     * When triggered by timeout, the subsequent non-async actions of the dependent CompletableFutures
     * are performed in the <strong>SINGLE thread builtin executor</strong> of CompletableFuture for delay execution
     * (including timeout function). So the long-running subsequent non-async actions lead to
     * the CompletableFuture dysfunction (including delay execution and timeout).
     * <p>
     * <strong>Strong recommend</strong> using the safe method {@link #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit, Executor)} instead of this method and {@link CompletableFuture#completeOnTimeout}.<br>
     * Unless all subsequent actions of dependent CompletableFutures is ensured executing async(aka. the dependent
     * CompletableFutures is created by async methods), using this method and {@link CompletableFuture#completeOnTimeout}
     * is one less thread switch of task execution when triggered by timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit, Executor)
     */
    @Contract("_, _, _, _ -> param1")
    public static <T, C extends CompletableFuture<? super T>>
    C completeOnTimeout(C cfThis, @Nullable T value, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");
        // NOTE: No need check minimal stage, because checked in cfThis.completeOnTimeout() / cfThis.isDone() below
        if (IS_JAVA9_PLUS) {
            cfThis.completeOnTimeout(value, timeout, unit);
        } else {
            // below code is copied from CompletableFuture#completeOnTimeout with small adoption
            if (!cfThis.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToCompleteCf(cfThis, value, timeout, unit);
                cfThis.whenComplete(new FutureCanceller(f));
            }
        }
        return cfThis;
    }

    @SuppressWarnings("unchecked")
    private static <C extends CompletableFuture<?>> C hopExecutorIfAtCfDelayerThread(C cf, Executor executor) {
        CompletableFuture<Object> ret = newIncompleteFuture(cf);

        // use `cf.handle` method(instead of `cf.whenComplete`) and return null in order to
        // prevent reporting the handled exception argument of this `action` at subsequent `exceptionally`
        cf.handle((v, ex) -> {
            if (!atCfDelayerThread()) completeCf(ret, v, ex);
            else screenExecutor(executor).execute(() -> completeCf(ret, v, ex));
            return null;
        }).exceptionally(ex -> reportUncaughtException("handle of executor hop", ex));

        return (C) ret;
    }

    private static void completeCf(CompletableFuture<Object> cf, Object value, @Nullable Throwable ex) {
        try {
            if (ex == null) cf.complete(value);
            else cf.completeExceptionally(ex);
        } catch (Throwable t) {
            if (ex != null) t.addSuppressed(ex);
            reportUncaughtException("completeCf", t);
            throw t; // rethrow exception, report to caller
        }
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
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
     * is composed using the results of the supplied function applied to given stage's exception.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, C extends CompletionStage<? super T>>
    C catchingCompose(C cfThis, Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");

        return (C) cfThis.handle((v, ex) -> (ex == null || !exceptionType.isAssignableFrom(ex.getClass()))
                ? cfThis : fallback.apply((X) ex)
        ).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using the default executor of parameter cfThis.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    public static <T, X extends Throwable, C extends CompletionStage<? super T>> C catchingComposeAsync(
            C cfThis, Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback) {
        return catchingComposeAsync(cfThis, exceptionType, fallback, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
     * is composed using the results of the supplied function applied to given's exception,
     * using the supplied Executor.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @param executor      the executor to use for asynchronous execution
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    @SuppressWarnings("unchecked")
    public static <T, X extends Throwable, C extends CompletionStage<? super T>> C catchingComposeAsync(
            C cfThis, Class<X> exceptionType,
            Function<? super X, ? extends CompletionStage<T>> fallback, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(exceptionType, "exceptionType is null");
        requireNonNull(fallback, "fallback is null");
        requireNonNull(executor, "executor is null");

        return (C) cfThis.handle((v, ex) -> (ex == null || !exceptionType.isAssignableFrom(ex.getClass()))
                ? cfThis : cfThis.handleAsync((v1, ex1) -> fallback.apply((X) ex1), executor).thenCompose(x -> x)
        ).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strong recommend</strong> using {@link #catchingCompose(CompletionStage, Class, Function)}
     * instead in your biz application.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @see #catchingCompose(CompletionStage, Class, Function)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyCompose(C cfThis, Function<Throwable, ? extends CompletionStage<T>> fn) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        if (IS_JAVA12_PLUS) {
            return (C) cfThis.exceptionallyCompose((Function) fn);
        }
        // below code is copied from CompletionStage.exceptionallyCompose
        return (C) cfThis.handle((v, ex) -> (ex == null) ? cfThis : fn.apply(ex)).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using the default executor of parameter cfThis.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strong recommend</strong> using {@link #catchingComposeAsync(CompletionStage, Class, Function)}
     * instead in your biz application.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @see #catchingComposeAsync(CompletionStage, Class, Function)
     */
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyComposeAsync(C cfThis, Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally, is composed using
     * the results of the supplied function applied to given stage's exception, using the supplied Executor.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strong recommend</strong> using {@link #catchingComposeAsync(CompletionStage, Class, Function, Executor)}
     * instead in your biz application.
     *
     * @param fn       the function to use to compute the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @see #catchingComposeAsync(CompletionStage, Class, Function, Executor)
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyComposeAsync(C cfThis, Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA12_PLUS) {
            return (C) cfThis.exceptionallyComposeAsync((Function) fn, executor);
        }
        // below code is copied from CompletionStage.exceptionallyComposeAsync
        return (C) cfThis.handle((v, ex) -> (ex == null) ? cfThis :
                cfThis.handleAsync((v1, ex1) -> fn.apply(ex1), executor).thenCompose(x -> x)
        ).thenCompose(x -> x);
    }

    /**
     * Peeks the result by executing the given action when the given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * <p>
     * <strong>CAUTION:</strong> The return stage of method {@link CompletionStage#whenComplete(BiConsumer)}
     * will contain <strong>DIFFERENT</strong> result to the input stage when the input stage is completed normally
     * but the supplied action throws an exception. This behavior of method {@code whenComplete} is subtle,
     * and common misused if you just want to <strong>peek</strong> the input stage without affecting the result(e.g.
     * logging the cf result).<br>For this {@code peek} method, whether the supplied action throws an exception or not,
     * the result of return stage(aka. the given stage) is <strong>NOT</strong> affected.
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
    public static <T, C extends CompletionStage<? extends T>>
    C peek(C cfThis, BiConsumer<? super T, ? super Throwable> action) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");

        // use `cf.handle` method(instead of `cf.whenComplete`) and return null in order to
        // prevent reporting the handled exception argument of this `action` at subsequent `exceptionally`
        cfThis.handle((v, ex) -> {
            action.accept(v, ex);
            return null;
        }).exceptionally(ex -> reportUncaughtException("the action of peek", ex));
        return cfThis;
    }

    /**
     * Peeks the result by executing the given action using the default executor of parameter cfThis
     * when the given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * <p>
     * <strong>CAUTION:</strong> The return stage of method {@link CompletionStage#whenCompleteAsync(BiConsumer)}
     * will contain <strong>DIFFERENT</strong> result to the input stage when the input stage is completed normally
     * but the supplied action throws an exception. This behavior of method {@code whenComplete} is subtle,
     * and common misused if you just want to <strong>peek</strong> the input stage without affecting the result(e.g.
     * logging the cf result).<br>For this {@code peek} method, whether the supplied action throws an exception or not,
     * the result of return stage(aka. the given stage) is <strong>NOT</strong> affected.
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
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync(C cfThis, BiConsumer<? super T, ? super Throwable> action) {
        return peekAsync(cfThis, action, defaultExecutor(cfThis));
    }

    /**
     * Peeks the result by executing the given action using the supplied executor
     * when the given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * <p>
     * <strong>CAUTION:</strong> The return stage of method {@link CompletionStage#whenCompleteAsync(BiConsumer, Executor)}
     * will contain <strong>DIFFERENT</strong> result to the input stage when the input stage is completed normally
     * but the supplied action throws an exception. This behavior of method {@code whenComplete} is subtle,
     * and common misused if you just want to <strong>peek</strong> the input stage without affecting the result(e.g.
     * logging the cf result).<br>For this {@code peek} method, whether the supplied action throws an exception or not,
     * the result of return stage(aka. the given stage) is <strong>NOT</strong> affected.
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
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync(C cfThis, BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        // use `cf.handleAsync` method(instead of `cf.whenCompleteAsync`) and return null in order to
        // prevent reporting the handled exception argument of this `action` at subsequent `exceptionally`
        cfThis.handleAsync((v, ex) -> {
            action.accept(v, ex);
            return null;
        }, executor).exceptionally(ex -> reportUncaughtException("the action of peekAsync", ex));
        return cfThis;
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
    //     these old methods exists in `Future` interface since Java 5
    //   - getNow/join throw CompletionException(unchecked exception),
    //     these new methods exists in `CompletableFuture` since Java 8
    ////////////////////////////////////////////////////////////

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
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
     * <strong>CAUTION:</strong> if the wait timed out, this method throws an (unchecked) {@link CompletionException}
     * with the {@link TimeoutException} as its cause;
     * NOT throws a (checked) {@link TimeoutException} like {@link CompletableFuture#get(long, TimeUnit)}.
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
     * This method will not throw exceptions
     * (CancellationException/CompletionException/ExecutionException/IllegalStateException/...).
     *
     * @param valueIfNotSuccess the value to return if not completed normally
     * @return the result value, if completed normally, else the given valueIfNotSuccess
     */
    @Contract(pure = true)
    @Nullable
    public static <T> T getSuccessNow(CompletableFuture<? extends T> cfThis, @Nullable T valueIfNotSuccess) {
        requireNonNull(cfThis, "cfThis is null");
        // NOTE: No need check minimal stage, because checked in cfThis.isDone() below
        return cfThis.isDone() && !cfThis.isCompletedExceptionally() ? cfThis.join() : valueIfNotSuccess;
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
     * Returns the computation state({@link CffuState}), this method  is equivalent to {@link CompletableFuture#state()}
     * with java version compatibility logic, so you can invoke in old {@code java 18-}.
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
    public static <T, C extends CompletableFuture<? super T>> C completeAsync(C cfThis, Supplier<? extends T> supplier) {
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
    public static <T, C extends CompletableFuture<? super T>>
    C completeAsync(C cfThis, Supplier<? extends T> supplier, Executor executor) {
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
    public static <C extends CompletableFuture<?>>
    C completeExceptionallyAsync(C cfThis, Supplier<? extends Throwable> supplier) {
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
    public static <C extends CompletableFuture<?>>
    C completeExceptionallyAsync(C cfThis, Supplier<? extends Throwable> supplier, Executor executor) {
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
     * <strong>CAUTION:</strong> if run on old Java 8(not support *minimal* CompletionStage),
     * just return a *normal* CompletableFuture which is NOT a *minimal* CompletionStage.
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
     * uses the {@link ForkJoinPool#commonPool()} if it supports more than one parallel thread, or else an Executor using
     * one thread per async task. <strong>CAUTION:</strong> This executor may be not suitable for common biz use(io intensive).
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

    /**
     * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
     *
     * @param <U> the type of the value
     * @see CompletableFuture#newIncompleteFuture()
     */
    @Contract(pure = true)
    public static <U> CompletableFuture<U> newIncompleteFuture(CompletableFuture<?> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        return IS_JAVA9_PLUS ? cfThis.newIncompleteFuture() : new CompletableFuture<>();
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
     * A convenient util method for converting input {@link CompletionStage}(including
     * {@link Cffu}/{@link CompletableFuture}) array element by {@link CompletionStage#toCompletableFuture()}.
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
     * A convenient util method for converting input {@link CompletableFuture} list to CompletableFuture array.
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
     * A convenient util method for unwrapping CF exception
     * ({@link CompletionException}/{@link ExecutionException}) to the biz exception.
     */
    @Contract(value = "null -> null; !null -> !null", pure = true)
    public static @Nullable Throwable unwrapCfException(@Nullable Throwable ex) {
        if (!(ex instanceof CompletionException) && !(ex instanceof ExecutionException)) {
            return ex;
        }
        if (ex.getCause() == null) return ex;
        return ex.getCause();
    }

    private CompletableFutureUtils() {
    }
}
