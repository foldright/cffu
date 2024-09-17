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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.*;

import static io.foldright.cffu.Delayer.atCfDelayerThread;
import static io.foldright.cffu.ExceptionReporter.reportUncaughtException;
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
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #allResultsFastFailOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFastFailAsync(Supplier<? extends T>... suppliers) {
        return mSupplyFastFailAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #allResultsFastFailOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFastFailAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allResultsFastFailOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not failed
     * @param suppliers     the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>           the suppliers' return type
     * @return the new CompletableFuture
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        return mSupplyAllSuccessAsync(valueIfFailed, ASYNC_POOL, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not failed
     * @param executor      the executor to use for asynchronous execution
     * @param suppliers     the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>           the suppliers' return type
     * @return the new CompletableFuture
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allSuccessResultsOf0(valueIfFailed, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers does not success(fails or incomplete) in given time,
     * its corresponding position will contain {@code valueIfNotSuccess}.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param suppliers         the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>               the suppliers' return type
     * @return the new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Supplier<? extends T>... suppliers) {
        return mSupplyMostSuccessAsync(valueIfNotSuccess, ASYNC_POOL, timeout, unit, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers does not success(fails or incomplete) in given time,
     * its corresponding position will contain {@code valueIfNotSuccess}.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param executor          the executor to use for asynchronous execution
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param suppliers         the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>               the suppliers' return type
     * @return the new CompletableFuture
     * @see #mostSuccessResultsOf(Object, Executor, long, TimeUnit, CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return mostSuccessResultsOf0(valueIfNotSuccess, executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allResultsOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously successful
     * when any of tasks running in the CompletableFuture's default asynchronous execution facility
     * by calling the given Suppliers success, with the same result.
     * Otherwise, all the given tasks complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause.
     * If no suppliers are provided, returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #anySuccessOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnySuccessAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously successful
     * when any of tasks running in the given executor by calling the given Suppliers success, with the same result.
     * Otherwise, all the given tasks complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause.
     * If no suppliers are provided, returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #anySuccessOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnySuccessAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return anySuccessOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is completed
     * when any of tasks running in the CompletableFuture's default asynchronous execution facility
     * by calling the given Suppliers complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no suppliers are provided, returns an incomplete CompletableFuture.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #anyOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnyAsync(ASYNC_POOL, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is completed
     * when any of tasks running in the given Executor by calling the given Suppliers complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no suppliers are provided, returns an incomplete CompletableFuture.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #anyOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<T> mSupplyAnyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return f_cast(CompletableFuture.anyOf(wrapSuppliers0(executor, suppliers)));
    }

    @SafeVarargs
    private static <T> T[] requireArrayAndEleNonNull(String varName, T... array) {
        requireNonNull(array, varName + "s is null");
        for (int i = 0; i < array.length; i++) {
            requireNonNull(array[i], varName + (i + 1) + " is null");
        }
        return array;
    }

    private static <T> CompletableFuture<? extends T>[] wrapSuppliers0(
            Executor executor, Supplier<? extends T>[] suppliers) {
        @SuppressWarnings("unchecked")
        CompletableFuture<? extends T>[] cfs = new CompletableFuture[suppliers.length];
        for (int i = 0; i < suppliers.length; i++) {
            cfs[i] = CompletableFuture.supplyAsync(suppliers[i], executor);
        }
        return cfs;
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * after runs the given actions.
     *
     * @param actions the actions to run before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #allFastFailOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable)
     */
    public static CompletableFuture<Void> mRunFastFailAsync(Runnable... actions) {
        return mRunFastFailAsync(ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor after runs the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #allFastFailOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public static CompletableFuture<Void> mRunFastFailAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return allFastFailOf0(wrapRunnables0(executor, actions));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * after runs the given actions.
     *
     * @param actions the actions to run before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #allOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable)
     */
    public static CompletableFuture<Void> mRunAsync(Runnable... actions) {
        return mRunAsync(ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor after runs the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #allOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public static CompletableFuture<Void> mRunAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return CompletableFuture.allOf(wrapRunnables0(executor, actions));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously successful
     * when any tasks running in the CompletableFuture's default asynchronous execution facility success.
     *
     * @param actions the actions to run to complete the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #anySuccessOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable)
     */
    public static CompletableFuture<Void> mRunAnySuccessAsync(Runnable... actions) {
        return mRunAnySuccessAsync(ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously successful
     * when any tasks running in the given executor success.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run to complete the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #anySuccessOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public static CompletableFuture<Void> mRunAnySuccessAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return anySuccessOf0(wrapRunnables0(executor, actions));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * when any tasks running in the CompletableFuture's default asynchronous execution facility.
     *
     * @param actions the actions to run to complete the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #anyOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable)
     */
    public static CompletableFuture<Void> mRunAnyAsync(Runnable... actions) {
        return mRunAnyAsync(ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * when any tasks running in the given executor complete.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run to complete the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #anyOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public static CompletableFuture<Void> mRunAnyAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return f_cast(CompletableFuture.anyOf(wrapRunnables0(executor, actions)));
    }

    private static CompletableFuture<Void>[] wrapRunnables0(Executor executor, Runnable[] actions) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[actions.length];
        for (int i = 0; i < actions.length; i++) {
            cfs[i] = CompletableFuture.runAsync(actions[i], executor);
        }
        return cfs;
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Tuple-Multi-Actions(tupleM*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyFastFailAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsFastFailOf(CompletionStage[])
     * @see #mSupplyFastFailAsync(Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyFastFailAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyFastFailAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyFastFailAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyFastFailAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleOf0(true, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns {@code CompletableFuture<T>} with generic type {@code T} but constrained to type TupleX.
     */
    private static <T> CompletableFuture<T> f_allTupleOf0(boolean fastFail, CompletionStage<?>[] stages) {
        final Object[] result = new Object[stages.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(stages, result);

        final CompletableFuture<Void> resultSetter;
        if (fastFail) resultSetter = allFastFailOf0(resultSetterCfs);
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
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyAllSuccessAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Executor, Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyAllSuccessAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Executor, Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyAllSuccessAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyAllSuccessAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Executor, Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyAllSuccessAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyAllSuccessAsync(Object, Executor, Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allSuccessTupleOf0(wrapSuppliers0(executor, suppliers));
    }

    private static <T> CompletableFuture<T> f_allSuccessTupleOf0(CompletionStage<?>[] stages) {
        Function<CompletionStage<Object>, CompletionStage<Object>> converter = s -> s.exceptionally(ex -> null);
        return f_allTupleOf0(false, convertStageArray0(converter, stages));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T, U> CompletionStage<U>[] convertStageArray0(
            Function<CompletionStage<T>, ? extends CompletionStage<U>> converter,
            CompletionStage<? extends T>[] stages) {
        CompletionStage[] ret = new CompletionStage[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = converter.apply((CompletionStage) stages[i]);
        }
        return ret;
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyMostSuccessAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed by tasks running in the given Executor
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, Executor, long, TimeUnit, Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyMostSuccessAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed by tasks running in the given Executor
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, Executor, long, TimeUnit, Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyMostSuccessAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed by tasks running in the given Executor
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, Executor, long, TimeUnit, Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_mostSuccessTupleOf0(executor, timeout, unit, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyMostSuccessAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed by tasks running in the given Executor
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #mSupplyMostSuccessAsync(Object, Executor, long, TimeUnit, Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyMostSuccessAsync(
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
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, executorWhenTimeout, timeout, unit)
                .handle((unused, ex) -> f_tupleOf0(MGetSuccessNow0(null, cfArray)));
    }

    /**
     * Multi-Gets(MGet) the results in the <strong>same order</strong> of the given cfs arguments,
     * use the result value if the given stage is completed successfully, else use the given valueIfNotSuccess
     *
     * @param cfs MUST be *Non-Minimal* CF instances in order to read results(`getSuccessNow`),
     *            otherwise UnsupportedOperationException
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] MGetSuccessNow0(@Nullable Object valueIfNotSuccess, CompletableFuture<?>[] cfs) {
        Object[] ret = new Object[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = getSuccessNow(cfs[i], valueIfNotSuccess);
        }
        return (T[]) ret;
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyAsync(Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyAsync(Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyAsync(Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleOf0(false, wrapSuppliers0(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #mSupplyAsync(Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #mSupplyFastFailAsync(Executor, Supplier[])
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAsync(
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
     * Returns a new CompletableFuture that is successful with the results in the <strong>same order</strong>
     * of the given stages arguments when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * This method is the same as {@link #allResultsOf(CompletionStage[])} except for the fast-fail behavior.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when all the given stages success
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsFastFailOf(CompletionStage<? extends T>... cfs) {
        return allResultsFastFailOf0(requireCfsAndEleNonNull(cfs));
    }

    private static <T> CompletableFuture<List<T>> allResultsFastFailOf0(CompletionStage<? extends T>[] cfs) {
        final int len = cfs.length;
        if (len == 0) return completedFuture(arrayList());
        // convert input cf to non-minimal-stage CF instance for SINGLE input in order to
        // ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCf0(cfs[0]).thenApply(CompletableFutureUtils::arrayList);

        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[len + 1];
        fill0(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[len] = allResultsOf0(successOrBeIncomplete);

        return f_cast(CompletableFuture.anyOf(failedOrBeIncomplete));
    }

    /**
     * Returns a new CompletableFuture that is successful with the results in the <strong>same order</strong>
     * of the given stages arguments when all the given stages completed;
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not completed successfully
     * @param cfs           the stages
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see #getSuccessNow(CompletableFuture, Object)
     * @see Futures#successfulAsList(ListenableFuture[]) Guava method successfulAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allSuccessResultsOf(
            @Nullable T valueIfFailed, CompletionStage<? extends T>... cfs) {
        return allSuccessResultsOf0(valueIfFailed, requireCfsAndEleNonNull(cfs));
    }

    private static <T> CompletableFuture<List<T>> allSuccessResultsOf0(
            @Nullable T valueIfFailed, CompletionStage<? extends T>[] cfs) {
        Function<CompletionStage<T>, CompletionStage<T>> converter = s -> s.exceptionally(ex -> valueIfFailed);
        return allResultsOf0(convertStageArray0(converter, cfs));
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If any of the provided stages does not success(fails or incomplete) in given time,
     * its corresponding position will contain {@code valueIfNotSuccess}.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param cfs               the stages
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return mostSuccessResultsOf(valueIfNotSuccess, ASYNC_POOL, timeout, unit, cfs);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If any of the provided stages does not success(fails or incomplete) in given time,
     * its corresponding position will contain {@code valueIfNotSuccess}.
     *
     * @param valueIfNotSuccess   the value to return if not completed successfully
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param cfs                 the stages
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T>... cfs) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        requireCfsAndEleNonNull(cfs);

        return mostSuccessResultsOf0(valueIfNotSuccess, executorWhenTimeout, timeout, unit, cfs);
    }

    private static <T> CompletableFuture<List<T>> mostSuccessResultsOf0(
            @Nullable T valueIfNotSuccess, Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T>[] cfs) {
        if (cfs.length == 0) return completedFuture(arrayList());
        if (cfs.length == 1) {
            // Defensive copy input cf to non-minimal-stage instance in order to
            // 1. avoid writing it by `cffuCompleteOnTimeout` and is able to read its result(`getSuccessNow`)
            // 2. ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
            final CompletableFuture<T> f = toNonMinCfCopy0(cfs[0]);
            return cffuCompleteOnTimeout(f, valueIfNotSuccess, executorWhenTimeout, timeout, unit)
                    .handle((unused, ex) -> arrayList(getSuccessNow(f, valueIfNotSuccess)));
        }

        // MUST be non-minimal-stage CF instances in order to read results(`getSuccessNow`),
        // otherwise UnsupportedOperationException
        final CompletableFuture<T>[] cfArray = toNonMinCfArray0(cfs);
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, executorWhenTimeout, timeout, unit)
                .handle((unused, ex) -> arrayList(MGetSuccessNow0(valueIfNotSuccess, cfArray)));
    }

    /**
     * Returns a new CompletableFuture with the results in the <strong>same order</strong> of the given stages arguments,
     * the new CompletableFuture is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * This method is the same as {@link #allOf(CompletionStage[])},
     * except that the returned CompletableFuture contains the results of the given stages.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed when all the given stages complete
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#allAsList(ListenableFuture[]) Guava method allAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        return allResultsOf0(requireCfsAndEleNonNull(cfs));
    }

    private static <T> CompletableFuture<List<T>> allResultsOf0(CompletionStage<? extends T>[] cfs) {
        final int len = cfs.length;
        if (len == 0) return completedFuture(arrayList());
        // convert input cf to non-minimal-stage CF instance for SINGLE input in order to
        // ensure that the returned cf is not minimal-stage instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCf0(cfs[0]).thenApply(CompletableFutureUtils::arrayList);

        final Object[] result = new Object[len];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(cfs, result);

        CompletableFuture<List<Object>> ret = CompletableFuture.allOf(resultSetterCfs)
                .thenApply(unused -> arrayList(result));
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture that is successful when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected
     * in the returned CompletableFuture({@code CompletableFuture<Void>}),
     * but may be obtained by inspecting them individually.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * This method is the same as {@link #allOf(CompletionStage[])} except for the fast-fail behavior.
     * <p>
     * If you need the results of given stages, prefer below methods:
     * <ul>
     * <li>{@link #allResultsFastFailOf(CompletionStage[])}
     * <li>{@link #allTupleFastFailOf(CompletionStage, CompletionStage)} /
     *     {@link #allTupleFastFailOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ul>
     * <p>
     * If you need the successful results of given stages in the given time, prefer below methods:
     * <ul>
     * <li>{@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * <li>{@link #mostSuccessTupleOf(long, TimeUnit, CompletionStage, CompletionStage)} / {@link #mostSuccessTupleOf(long,
     *      TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when all the given stages success
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    public static CompletableFuture<Void> allFastFailOf(CompletionStage<?>... cfs) {
        return allFastFailOf0(requireCfsAndEleNonNull(cfs));
    }

    private static CompletableFuture<Void> allFastFailOf0(CompletionStage<?>[] cfs) {
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
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[len] = CompletableFuture.allOf(successOrBeIncomplete);

        return f_cast(CompletableFuture.anyOf(failedOrBeIncomplete));
    }

    /**
     * Returns a new CompletableFuture that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected in the returned
     * CompletableFuture({@code CompletableFuture<Void>}), but may be obtained by inspecting them individually.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * Among the applications of this method is to await completion of a set of independent stages
     * before continuing a program, as in: {@code CompletableFuture.allOf(c1, c2, c3).join();}.
     * <p>
     * This method is the same as {@link CompletableFuture#allOf(CompletableFuture[])},
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture}.
     * <p>
     * If you need the results of given stages, prefer below methods:
     * <ul>
     * <li>{@link #allResultsOf(CompletionStage[])}
     * <li>{@link #allTupleOf(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     * <p>
     * If you need the successful results of given stages in the given time, prefer below methods:
     * <ul>
     * <li>{@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * <li>{@link #mostSuccessTupleOf(long, TimeUnit, CompletionStage, CompletionStage)} / {@link #mostSuccessTupleOf(long,
     *      TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed when all the given stages complete
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
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
     * Returns normal array list instead of unmodifiable or fixed-size list.
     * Safer for application code which may reuse the returned list as normal collection.
     */
    @SafeVarargs
    private static <T> List<T> arrayList(T... elements) {
        List<T> ret = new ArrayList<>(elements.length);
        ret.addAll(Arrays.asList(elements));
        return ret;
    }

    /**
     * Returns a cf array whose elements do the result collection.
     */
    private static <T> CompletableFuture<Void>[] createResultSetterCfs(
            CompletionStage<? extends T>[] stages, T[] result) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] resultSetterCfs = new CompletableFuture[result.length];
        for (int i = 0; i < result.length; i++) {
            final int index = i;
            resultSetterCfs[index] = f_toCf0(stages[index]).thenAccept(v -> result[index] = v);
        }
        return resultSetterCfs;
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

    /**
     * Force casts CompletableFuture with the value type,
     * IGNORE the compile-time type check.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> CompletableFuture<T> f_cast(CompletableFuture<?> cf) {
        return (CompletableFuture) cf;
    }

    /**
     * Force converts {@link CompletionStage} array to {@link CompletableFuture} array,
     * reuse cf instance as many as possible. This method is NOT type safe!
     * More info see method {@link #f_toCf0(CompletionStage)}.
     */
    private static <T> CompletableFuture<T>[] f_toCfArray0(CompletionStage<? extends T>[] stages) {
        return _toCfArray0(CompletableFutureUtils::f_toCf0, stages);
    }

    /**
     * Converts {@link CompletionStage} array to {@link CompletableFuture} array.
     * More info see method {@link #toNonMinCf0(CompletionStage)}.
     */
    private static <T> CompletableFuture<T>[] toNonMinCfArray0(CompletionStage<? extends T>[] stages) {
        return _toCfArray0(CompletableFutureUtils::toNonMinCf0, stages);
    }

    private static <T> CompletableFuture<T>[] _toCfArray0(
            Function<CompletionStage<? extends T>, CompletableFuture<T>> converter,
            CompletionStage<? extends T>[] stages) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = converter.apply(stages[i]);
        }
        return ret;
    }

    /**
     * Force converts CompletionStage to CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! Because reused the CF instance,
     * The returned cf may be a minimal-stage, MUST NOT be written or read(explicitly)
     * (e.g. {@link CompletableFuture#complete}); Otherwise, the caller usage of cf may throw UnsupportedOperationException.
     */
    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> f_toCf0(CompletionStage<? extends T> s) {
        if (s instanceof CompletableFuture) return (CompletableFuture<T>) s;
        else if (s instanceof Cffu) return ((Cffu<T>) s).cffuUnwrap();
        else return (CompletableFuture<T>) s.toCompletableFuture();
    }

    /**
     * Converts CompletionStage to non-minimal-stage CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instance, if the caller need defensive copy
     * instead of writing it directly, use method {@link #toNonMinCfCopy0(CompletionStage)}).
     */
    private static <T> CompletableFuture<T> toNonMinCf0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : f;
    }

    /**
     * Converts CompletionStage to a non-minimal-stage CompletableFuture copy. This method is type safe.
     * <p>
     * <strong>Implementation Note:</strong> The returned instances of calling {@code copy} methods
     * ({@link #copy}/{@link CompletableFuture#copy}) on {@code minimal-stage} instances
     * is still {@code minimal-stage}(e.g. {@code minimalCompletionStage().copy()}, {@code completedStage().copy()})
     */
    private static <T> CompletableFuture<T> toNonMinCfCopy0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : copy(f);
    }

    static boolean isMinStageCf(CompletableFuture<?> cf) {
        return cf.getClass().equals(MIN_STAGE_CLASS);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    //
    //    CompletionStage<T>[] -> CompletableFuture<T>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is successful when any of the given stages success,
     * with the same result. Otherwise, all the given stages complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause.
     * If no stages are provided, returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     * <p>
     * This method is the same as {@link #anyOf(CompletionStage[])}
     * except for the any-<strong>success</strong> behavior instead of any-<strong>complete</strong>.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when any of the given stages success, with the same result
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

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE
        //       a cf that is failed when all given cfs fail, otherwise be incomplete
        successOrBeIncomplete[len] = CompletableFuture.allOf(failedOrBeIncomplete);

        return f_cast(CompletableFuture.anyOf(successOrBeIncomplete));
    }

    /**
     * Returns a new CompletableFuture that is completed when any of the given stages complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns an incomplete CompletableFuture.
     * <p>
     * This method is the same as {@link CompletableFuture#anyOf(CompletableFuture[])},
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture},
     * and the return type is more specific {@code T} instead of {@code Object}.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed with the result or exception
     * from any of the given stages when one completes
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see #anySuccessOf(CompletionStage[])
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
     * Returns a new CompletableFuture that is successful when the given stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new CompletableFuture that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new CompletableFuture
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Returns a new CompletableFuture that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new CompletableFuture
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Returns a new CompletableFuture that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new CompletableFuture
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new CompletableFuture that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new CompletableFuture
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostSuccessTupleOf(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
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
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
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
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
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
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4, cf5);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
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
     * Returns a new CompletableFuture that is completed when the given stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given stages complete
     * @see #allResultsOf(CompletionStage[])
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
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8(not support *minimal* CompletionStage),
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
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8(not support *minimal* CompletionStage),
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyFastFailAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @param fns      the functions to use to compute the values of the returned CompletableFuture
     * @param <U>      the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> allResultsFastFailOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the default executor of parameter cfThis
     * with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not completed successfully
     * @param fns           the functions to use to compute the values of the returned CompletableFuture
     * @param <U>           the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfFailed, Function<? super T, ? extends U>... fns) {
        return thenMApplyAllSuccessAsync(cfThis, valueIfFailed, defaultExecutor(cfThis), fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the given Executor with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not completed successfully
     * @param fns           the functions to use to compute the values of the returned CompletableFuture
     * @param <U>           the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfFailed,
            Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> allSuccessResultsOf0(valueIfFailed, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions does not success(fails or incomplete) in given time,
     * its corresponding position will contain {@code valueIfNotSuccess}.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param fns               the functions to use to compute the values of the returned CompletableFuture
     * @param <U>               the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        return thenMApplyMostSuccessAsync(cfThis, valueIfNotSuccess, defaultExecutor(cfThis), timeout, unit, fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions does not success(fails or incomplete) in given time,
     * its corresponding position will contain {@code valueIfNotSuccess}.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param executor          the executor to use for asynchronous execution
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param fns               the functions to use to compute the values of the returned CompletableFuture
     * @param <U>               the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, @Nullable U valueIfNotSuccess,
            Executor executor, long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> mostSuccessResultsOf0(
                valueIfNotSuccess, executor, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @param fns      the functions to use to compute the values of the returned CompletableFuture
     * @param <U>      the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return cfThis.thenCompose(v -> allResultsOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with any successful value obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions).
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyAnySuccessAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with any successful value obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions).
     *
     * @param executor the executor to use for asynchronous execution
     * @param fns      the functions to use to compute the values of the returned CompletableFuture
     * @param <U>      the functions' return type
     * @return the new CompletableFuture
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with any completed result obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions).
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<U> thenMApplyAnyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U>... fns) {
        return thenMApplyAnyAsync(cfThis, defaultExecutor(cfThis), fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with any completed result obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions).
     *
     * @param executor the executor to use for asynchronous execution
     * @param fns      the functions to use to compute the values of the returned CompletableFuture
     * @param <U>      the functions' return type
     * @return the new CompletableFuture
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
        @SuppressWarnings("unchecked")
        CompletableFuture<U>[] cfs = new CompletableFuture[fns.length];
        for (int i = 0; i < fns.length; i++) {
            final int idx = i;
            cfs[i] = CompletableFuture.supplyAsync(() -> fns[idx].apply(v), executor);
        }
        return cfs;
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the given stage's result as the argument to the given actions.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFastFailAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptFastFailAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the given stage's result as the argument to the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFastFailAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(v -> allFastFailOf0(wrapConsumers0(executor, v, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the given stage's result as the argument to the given actions.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the given stage's result as the argument to the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the given stage's result as the argument to the given actions.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnySuccessAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptAnySuccessAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the given stage's result as the argument to the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the given stage's result as the argument to the given actions.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAnyAsync(
            CompletableFuture<? extends T> cfThis, Consumer<? super T>... actions) {
        return thenMAcceptAnyAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the given stage's result as the argument to the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
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
        @SuppressWarnings("unchecked")
        CompletableFuture<Void>[] cfs = new CompletableFuture[actions.length];
        for (int i = 0; i < actions.length; i++) {
            final int idx = i;
            cfs[idx] = CompletableFuture.runAsync(() -> actions[idx].accept(v), executor);
        }
        return cfs;
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the default executor of parameter cfThis.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable)
     * @see #allFastFailOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunFastFailAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunFastFailAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the given Executor.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see #allFastFailOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunFastFailAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> allFastFailOf0(wrapRunnables0(executor, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the default executor of parameter cfThis.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable)
     * @see #allOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the given Executor.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see #allOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> CompletableFuture.allOf(wrapRunnables0(executor, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the default executor of parameter cfThis.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable)
     * @see #anySuccessOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAnySuccessAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the given Executor.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see #anySuccessOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAnySuccessAsync(
            CompletableFuture<?> cfThis, Executor executor, Runnable... actions) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return cfThis.thenCompose(unused -> anySuccessOf0(wrapRunnables0(executor, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the default executor of parameter cfThis.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable)
     * @see #anyOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAnyAsync(CompletableFuture<?> cfThis, Runnable... actions) {
        return thenMRunAnyAsync(cfThis, defaultExecutor(cfThis), actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the given Executor.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see #anyOf(CompletionStage[])
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
    // region## Then-Tuple-Multi-Actions(thenTupleM*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenTupleMApplyFastFailAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenTupleMApplyFastFailAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenTupleMApplyFastFailAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenTupleMApplyFastFailAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyFastFailAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allTupleOf0(true, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the default executor of parameter cfThis,
     * with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions).
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenTupleMApplyAllSuccessAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the given Executor with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the default executor of parameter cfThis,
     * with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenTupleMApplyAllSuccessAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the given Executor with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the default executor of parameter cfThis,
     * with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenTupleMApplyAllSuccessAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the given Executor with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the default executor of parameter cfThis,
     * with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenTupleMApplyAllSuccessAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed in the given Executor with the successful values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyAllSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allSuccessTupleOf0(wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenTupleMApplyMostSuccessAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_mostSuccessTupleOf0(executor, timeout, unit, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenTupleMApplyMostSuccessAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyMostSuccessAsync(
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenTupleMApplyMostSuccessAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyMostSuccessAsync(
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyMostSuccessAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenTupleMApplyMostSuccessAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param executor the executor to use for asynchronous execution
     * @param timeout  how long to wait in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyMostSuccessAsync(
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
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenTupleMApplyAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenTupleMApplyAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        return thenTupleMApplyAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allTupleOf0(false, wrapFunctions0(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the default executor of parameter cfThis,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenTupleMApplyAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyAsync(
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
    // region## thenBoth* Methods(binary input) with fast-fail support
    //
    //    - thenCombineFastFail*   (BiFunction: (T, U) -> V)    -> CompletableFuture<U>
    //    - thenAcceptBothFastFail*(BiConsumer: (T, U) -> Void) -> CompletableFuture<Void>
    //    - runAfterBothFastFail*  (Runnable:   Void -> Void)   -> CompletableFuture<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFastFail(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(fn, "fn is null");

        return bothFastFail0(cfThis, other).thenApply(t -> fn.apply(t._1, t._2));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the default executor of parameter cfThis,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFastFailAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineFastFailAsync(cfThis, other, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFastFailAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return bothFastFail0(cfThis, other).thenApplyAsync(t -> fn.apply(t._1, t._2), executor);
    }

    private static <T> void requireThisAndOtherNonNull(
            CompletionStage<? extends T> cfThis, CompletionStage<? extends T> other) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(other, "other is null");
    }

    /**
     * Implementation Note: Calling this method is necessary to keep the runtime type(including `minimal-stage`) of
     * return cf same as input `cfThis` argument. The runtime type of method {@link #allResultsFastFailOf(CompletionStage[])}
     * return cf is always CompletableFuture, does NOT keep the runtime type of input `cfThis` argument.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T1, T2> CompletableFuture<Tuple2<T1, T2>> bothFastFail0(
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
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFail(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");

        return bothFastFail0(cfThis, other).thenAccept(t -> action.accept(t._1, t._2));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the default executor of parameter cfThis,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFailAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothFastFailAsync(cfThis, other, action, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFailAsync(
            CompletableFuture<? extends T> cfThis, CompletionStage<? extends U> other,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return bothFastFail0(cfThis, other).thenAcceptAsync(t -> action.accept(t._1, t._2), executor);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally, executes the given action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFastFail(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");

        return bothFastFail0(cfThis, other).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using the default executor of parameter cfThis.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFastFailAsync(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action) {
        return runAfterBothFastFailAsync(cfThis, other, action, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using the supplied executor.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFastFailAsync(
            CompletableFuture<?> cfThis, CompletionStage<?> other, Runnable action, Executor executor) {
        requireThisAndOtherNonNull(cfThis, other);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return bothFastFail0(cfThis, other).thenRunAsync(action, executor);
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
     * is executed with given stage's exception as the argument to the supplied function.
     * Otherwise, the returned stage contains same result as input CompletionStage.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @see Futures#catching Guava method catching()
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
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
     * is executed with given stage's exception as the argument to the supplied function,
     * using the default executor of parameter cfThis.
     * Otherwise, the returned stage contains same result as input CompletionStage.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @see Futures#catching Guava method catching()
     */
    public static <T, X extends Throwable, C extends CompletionStage<? super T>>
    C catchingAsync(C cfThis, Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        return catchingAsync(cfThis, exceptionType, fallback, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
     * is executed with given stage's exception as the argument to the supplied function, using the supplied Executor.
     * Otherwise, the returned stage contains same result as input CompletionStage.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the input's exception. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if {@code input} fails with the expected exception type.
     *                      The function's argument is the input's exception.
     * @param executor      the executor to use for asynchronous execution
     * @see Futures#catching Guava method catching()
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
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the default executor of parameter cfThis.
     * Otherwise, if given stage completes normally, then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strong recommend</strong> using {@link #catchingAsync(CompletionStage, Class, Function)}
     * instead in your biz application.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     * @see #catchingAsync(CompletionStage, Class, Function)
     */
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cfThis, Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(cfThis, fn, defaultExecutor(cfThis));
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
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
     * @return the new CompletableFuture
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
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     * <p>
     * Uses the default executor of parameter cfThis as {@code executorWhenTimeout}.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from the original
     * CF method {@link CompletableFuture#orTimeout} and its backport method {@link #orTimeout}, because the returning new CF instance
     * avoids the subsequent usage of the delay thread. More info see the javadoc of {@link #orTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java">DelayDysfunctionDemo</a>.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #cffuOrTimeout(CompletableFuture, Executor, long, TimeUnit)
     */
    public static <C extends CompletableFuture<?>> C cffuOrTimeout(C cfThis, long timeout, TimeUnit unit) {
        return cffuOrTimeout(cfThis, defaultExecutor(cfThis), timeout, unit);
    }

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from the original
     * CF method {@link CompletableFuture#orTimeout} and its backport method {@link #orTimeout}, because the returning new CF instance
     * avoids the subsequent usage of the delay thread. More info see the javadoc of {@link #orTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java">DelayDysfunctionDemo</a>.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait before completing exceptionally with a TimeoutException,
     *                            in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <C extends CompletableFuture<?>> C cffuOrTimeout(
            C cfThis, Executor executorWhenTimeout, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");

        return hopExecutorIfAtCfDelayerThread(orTimeout(cfThis, timeout, unit), executorWhenTimeout);
    }

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method and {@link CompletableFuture#orTimeout(long, TimeUnit)}
     * is <strong>UNSAFE</strong>!
     * <p>
     * When triggered by timeout, the subsequent non-async actions of the dependent CompletableFutures
     * are performed in the <strong>SINGLE thread builtin executor</strong>
     * of CompletableFuture for delay execution (including timeout function).
     * So the long-running subsequent non-async actions lead to the CompletableFuture dysfunction
     * (including delay execution and timeout).
     * <p>
     * <strong>Strong recommend</strong> using the safe method {@link #cffuOrTimeout(CompletableFuture, long, TimeUnit)}
     * instead of this method and {@link CompletableFuture#orTimeout(long, TimeUnit)}.
     * <p>
     * Unless all subsequent actions of dependent CompletableFutures is ensured executing async(aka. the dependent
     * CompletableFutures is created by async methods), using this method and {@link CompletableFuture#orTimeout(long,
     * TimeUnit)} is one less thread switch of task execution when triggered by timeout.
     * <p>
     * Note: Before Java 21(Java 20-), {@link CompletableFuture#orTimeout(long, TimeUnit)}
     * leaks if the future completes exceptionally, more info see
     * <a href="https://bugs.openjdk.org/browse/JDK-8303742">issue JDK-8303742</a>,
     * <a href="https://github.com/openjdk/jdk/pull/13059">PR review openjdk/jdk/13059</a>
     * and <a href="https://github.com/openjdk/jdk/commit/ded6a8131970ac2f7ae59716769e6f6bae3b809a">JDK bugfix commit</a>.
     * The cffu backport logic(for Java 20-) has merged the fix of this JDK bug.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuOrTimeout(CompletableFuture, long, TimeUnit)
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
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     * <p>
     * Uses the default executor of parameter cfThis as {@code executorWhenTimeout}.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from the original
     * CF method {@link CompletableFuture#completeOnTimeout} and its backport method {@link #completeOnTimeout},
     * because the returning new CF instance avoids the subsequent usage of the delay thread.
     * More info see the javadoc of {@link #completeOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java">DelayDysfunctionDemo</a>.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, Executor, long, TimeUnit)
     */
    public static <T, C extends CompletableFuture<? super T>>
    C cffuCompleteOnTimeout(C cfThis, @Nullable T value, long timeout, TimeUnit unit) {
        return cffuCompleteOnTimeout(cfThis, value, defaultExecutor(cfThis), timeout, unit);
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new CompletableFuture and this behavior is different from the original
     * CF method {@link CompletableFuture#completeOnTimeout} and its backport method {@link #completeOnTimeout},
     * because the returning new CF instance avoids the subsequent usage of the delay thread.
     * More info see the javadoc of {@link #completeOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java">DelayDysfunctionDemo</a>.
     *
     * @param value               the value to use upon timeout
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, C extends CompletableFuture<? super T>>
    C cffuCompleteOnTimeout(C cfThis, @Nullable T value, Executor executorWhenTimeout, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");

        return hopExecutorIfAtCfDelayerThread(completeOnTimeout(cfThis, value, timeout, unit), executorWhenTimeout);
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method and {@link CompletableFuture#completeOnTimeout(Object, long, TimeUnit)}
     * is <strong>UNSAFE</strong>!
     * <p>
     * When triggered by timeout, the subsequent non-async actions of the dependent CompletableFutures
     * are performed in the <strong>SINGLE thread builtin executor</strong>
     * of CompletableFuture for delay execution (including timeout function).
     * So the long-running subsequent non-async actions lead to the CompletableFuture dysfunction
     * (including delay execution and timeout).
     * <p>
     * <strong>Strong recommend</strong>
     * using the safe method {@link #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit)}
     * instead of this method and {@link CompletableFuture#completeOnTimeout}.
     * <p>
     * Unless all subsequent actions of dependent CompletableFutures is ensured executing async(aka. the dependent
     * CompletableFutures is created by async methods), using this method and {@link CompletableFuture#completeOnTimeout}
     * is one less thread switch of task execution when triggered by timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit)
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
            else executor.execute(() -> completeCf(ret, v, ex));
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
     * @see Futures#catchingAsync Guava method catchingAsync()
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
     * @see Futures#catchingAsync Guava method catchingAsync()
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
     * @see Futures#catchingAsync Guava method catchingAsync()
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * @return the new CompletableFuture
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
     * Peeks the result by executing the given action when given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments. Whether the supplied action
     * throws an exception or not, the given stage is <strong>NOT</strong> affected.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
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
     * Peeks the result by executing the given action when given stage completes,
     * executes the given action using the default executor of parameter cfThis,
     * returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments. Whether the supplied action
     * throws an exception or not, the given stage is <strong>NOT</strong> affected.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
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
     * Peeks the result by executing the given action when given stage completes,
     * executes the given action using the supplied Executor, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments. Whether the supplied action
     * throws an exception or not, the given stage is <strong>NOT</strong> affected.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
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
     * <strong>NOTE:<br></strong>
     * Calling this method
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
     * <strong>CAUTION:<br></strong>
     * if the wait timed out, this method throws an (unchecked) {@link CompletionException}
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
    public static <T> T join(CompletableFuture<T> cfThis, long timeout, TimeUnit unit) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(unit, "unit is null");

        if (cfThis.isDone()) return cfThis.join();
        // defensive copy input cf to avoid writing it by `orTimeout`
        return orTimeout(copy(cfThis), timeout, unit).join();
    }

    /**
     * Returns the result value if the given stage is completed successfully, else returns the given valueIfNotSuccess.
     * <p>
     * This method will not throw exceptions
     * (CancellationException/CompletionException/ExecutionException/IllegalStateException/...).
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @return the result value, if completed successfully, else the given valueIfNotSuccess
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
     * This method is for cases where the caller knows that the task has already completed successfully,
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
    public static <T> T resultNow(Future<T> cfThis) {
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
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8(not support *minimal* CompletionStage),
     * just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @return the new CompletionStage
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
     * @return the new CompletableFuture
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
        // FIXME hard-code type(CompletableFuture and Cffu...
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
     * @return the new CompletableFuture
     * @see CompletableFuture#newIncompleteFuture()
     */
    @Contract(pure = true)
    public static <U> CompletableFuture<U> newIncompleteFuture(CompletableFuture<?> cfThis) {
        requireNonNull(cfThis, "cfThis is null");
        return IS_JAVA9_PLUS ? cfThis.newIncompleteFuture() : new CompletableFuture<U>();
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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal helper fields and classes
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Null-checks user executor argument, and translates uses of
     * commonPool to ASYNC_POOL in case parallelism disabled.
     */
    @SuppressWarnings("resource")
    static Executor screenExecutor(Executor e) {
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool())
            return ASYNC_POOL;
        return requireNonNull(e, "defaultExecutor is null");
    }

    private static final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;

    /**
     * Fallback if ForkJoinPool.commonPool() cannot support parallelism
     */
    private static final class ThreadPerTaskExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            new Thread(requireNonNull(r)).start();
        }
    }

    /**
     * Default executor of CompletableFuture(<strong>NOT</strong> including the customized subclasses
     * of CompletableFuture) -- {@link ForkJoinPool#commonPool()} unless it cannot support parallelism.
     */
    static final Executor ASYNC_POOL = _asyncPool();

    private static Executor _asyncPool() {
        if (IS_JAVA9_PLUS) return completedFuture(null).defaultExecutor();
        if (USE_COMMON_POOL) return ForkJoinPool.commonPool();
        return new ThreadPerTaskExecutor();
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal Java version check logic for compatibility
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * a naive black hole to prevent code elimination, more info see <a href=
     * "https://github.com/openjdk/jmh/blob/1.37/jmh-core/src/main/java/org/openjdk/jmh/infra/Blackhole.java">JMH black hole</a>
     */
    private static volatile int BLACK_HOLE = 0xCFF0CFF0;

    // `CompletableFuture.completedStage` is the new method since java 9
    private static final boolean IS_JAVA9_PLUS = methodExists(() -> CompletableFuture.completedStage(null));

    // `CompletableFuture.exceptionallyCompose` is the new method since java 12
    private static final boolean IS_JAVA12_PLUS = methodExists(() ->
            completedFuture(null).exceptionallyCompose(ex -> null));

    // `CompletableFuture.resultNow` is the new method since java 19
    private static final boolean IS_JAVA19_PLUS = methodExists(() -> completedFuture(null).resultNow());

    // `List.reversed` is the new method since java 21
    private static final boolean IS_JAVA21_PLUS = methodExists(() -> new ArrayList<>().reversed());

    private static boolean methodExists(Supplier<?> methodCallCheck) {
        try {
            int i = BLACK_HOLE; // volatile read
            BLACK_HOLE = Objects.hashCode(methodCallCheck.get()) ^ i;
            return true;
        } catch (NoSuchMethodError e) {
            return false;
        }
    }

    /**
     * <strong>CAUTION / Implementation Note:</strong> do NOT move this static field before {@link #IS_JAVA9_PLUS},
     * otherwise will use uninitialized {@link #IS_JAVA9_PLUS}!
     */
    private static final Class<?> MIN_STAGE_CLASS;

    static {
        if (!IS_JAVA9_PLUS) MIN_STAGE_CLASS = null;
        else MIN_STAGE_CLASS = CompletableFuture.completedStage(null).getClass();
    }

    private CompletableFutureUtils() {
    }
}
