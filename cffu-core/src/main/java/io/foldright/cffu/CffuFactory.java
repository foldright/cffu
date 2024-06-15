package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Contract;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


/**
 * This class {@link CffuFactory} is equivalent to {@link CompletableFuture},
 * contains the static (factory) methods of {@link CompletableFuture}.
 * <p>
 * The methods that equivalent to the instance methods of {@link CompletableFuture} is in {@link Cffu} class.
 * <p>
 * Use {@link #builder(Executor)} to config and build {@link CffuFactory}.
 * <p>
 * About factory methods conventions of {@link CffuFactory}:
 * <ul>
 * <li>factory methods return {@link Cffu} instead of {@link CompletableFuture}.
 * <li>only provide varargs methods for multiply Cffu/CF input arguments;
 *     if you have {@code List} input, use static util methods {@link #cffuListToArray(List)}
 *     or {@link CompletableFutureUtils#completableFutureListToArray(List)} to convert it to array first.
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Cffu
 * @see CompletableFuture
 */
@ThreadSafe
public final class CffuFactory {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Builder and Constructor Methods(including internal constructors and fields)
    ////////////////////////////////////////////////////////////////////////////////

    private final Executor defaultExecutor;

    private final boolean forbidObtrudeMethods;

    CffuFactory(Executor defaultExecutor, boolean forbidObtrudeMethods) {
        this.defaultExecutor = defaultExecutor;
        this.forbidObtrudeMethods = forbidObtrudeMethods;
    }

    /**
     * Returns a {@link CffuFactoryBuilder} with {@code defaultExecutor} setting.
     *
     * @see Cffu#defaultExecutor()
     * @see CffuFactory#defaultExecutor()
     */
    @Contract(pure = true)
    public static CffuFactoryBuilder builder(Executor defaultExecutor) {
        return new CffuFactoryBuilder(CompletableFutureUtils.screenExecutor(defaultExecutor));
    }

    @Contract(pure = true)
    private <T> Cffu<T> create(CompletableFuture<T> cf) {
        return new Cffu<>(this, false, cf);
    }

    @Contract(pure = true)
    private <T> CompletionStage<T> createMin(CompletableFuture<T> cf) {
        return new Cffu<>(this, true, cf);
    }

    /**
     * Return an incomplete Cffu, equivalent to {@link CompletableFuture#CompletableFuture()} constructor.
     * <p>
     * In general, should not use this method in biz code, prefer below factory methods of Cffu:
     *
     * <ul>
     * <li>{@link #runAsync(Runnable)}
     * <li>{@link #supplyAsync(Supplier, Executor)}
     * </ul>
     *
     * @see #supplyAsync(Supplier)
     * @see #supplyAsync(Supplier, Executor)
     * @see #runAsync(Runnable)
     * @see #runAsync(Runnable, Executor)
     * @see CompletableFuture#CompletableFuture()
     */
    @Contract(pure = true)
    public <T> Cffu<T> newIncompleteCffu() {
        return create(new CompletableFuture<>());
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // region## supplyAsync*/runAsync* Methods(create by action)
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is asynchronously completed
     * by a task running in the {@link #defaultExecutor()} with
     * the value obtained by calling the given Supplier.
     *
     * @param supplier a function returning the value to be used to complete the returned Cffu
     * @param <T>      the function's return type
     * @return the new Cffu
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `runAsync`")
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier) {
        return supplyAsync(supplier, defaultExecutor);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by a task running
     * in the given executor with the value obtained by calling the given Supplier.
     *
     * @param supplier a function returning the value to be used to complete the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <T>      the function's return type
     * @return the new Cffu
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `runAsync`")
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return create(CompletableFuture.supplyAsync(supplier, executor));
    }

    /**
     * Returns a new Cffu that is asynchronously completed by a task running
     * in the {@link #defaultExecutor()} after it runs the given action.
     *
     * @param action the action to run before completing the returned Cffu
     * @return the new Cffu
     * @see CompletableFuture#runAsync(Runnable)
     */
    public Cffu<Void> runAsync(Runnable action) {
        return runAsync(action, defaultExecutor);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by a task running in the given executor after it runs the given action.
     *
     * @param action   the action to run before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public Cffu<Void> runAsync(Runnable action, Executor executor) {
        return create(CompletableFuture.runAsync(action, executor));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostResultsOfSuccess)
    //
    //  - allResultsOfFastFail
    //  - mostResultsOfSuccess
    //  - allResultsOf
    //  - allOf / allOfFastFail
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is successful with the results in the <strong>same order</strong>
     * of the given stages arguments when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * This method is the same as {@link #allResultsOf(CompletionStage[])} except for the fast-fail behavior.
     * <p>
     * This method is the same as {@link #allOfFastFail(CompletionStage[])},
     * except that the returned Cffu contains the results of the given stages.
     *
     * @param cfs the stages
     * @return a new Cffu that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsOfFastFail(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allResultsOfFastFail(cfs));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param cfs               the stages
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> mostResultsOfSuccess(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.mostResultsOfSuccess(
                valueIfNotSuccess, defaultExecutor, timeout, unit, cfs));
    }

    /**
     * Returns a new Cffu with the results in the <strong>same order</strong> of the given stages arguments,
     * the new Cffu is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * This method is the same as {@link #allOf(CompletionStage[])},
     * except that the returned Cffu contains the results of the given stages.
     *
     * @param cfs the stages
     * @return a new Cffu that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allResultsOf(cfs));
    }

    /**
     * Returns a new Cffu that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected in the returned
     * Cffu({@code Cffu<Void>}), but may be obtained by inspecting them individually.
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * This method is the same as {@link CompletableFuture#allOf(CompletableFuture[])},
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture}.
     * <p>
     * If you need the results of given stages, prefer below methods:
     * <ul>
     * <li>{@link #allResultsOf(CompletionStage[])}
     * <li>{@link #allTupleOf(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ul>
     * <p>
     * If you need the successful results of given stages in the given time, prefer below methods:
     * <ul>
     * <li>{@link #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])}
     * <li>{@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage)}
     * <li>{@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new Cffu that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see #allTupleOf(CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage)
     * @see #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see CompletableFutureUtils#allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOf(CompletionStage<?>... cfs) {
        return create(CompletableFutureUtils.allOf(cfs));
    }

    /**
     * Returns a new Cffu that is successful when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * Otherwise, the results of the given stages are not reflected in the returned Cffu({@code Cffu<Void>}),
     * but may be obtained by inspecting them individually.
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * This method is the same as {@link #allOf(CompletionStage[])} except for the fast-fail behavior.
     * <p>
     * If you need the results of given stages, prefer below methods:
     * <ul>
     * <li>{@link #allResultsOfFastFail(CompletionStage[])}
     * <li>{@link #allTupleOfFastFail(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ul>
     * <p>
     * If you need the successful results of given stages in the given time, prefer below methods:
     * <ul>
     * <li>{@link #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])}
     * <li>{@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage)}
     * <li>{@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new Cffu that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage)
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage)
     * @see #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOfFastFail(CompletionStage<?>... cfs) {
        return create(CompletableFutureUtils.allOfFastFail(cfs));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is successful when any of the given stages success,
     * with the same result. Otherwise, all the given stages complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause. If no stages are provided,
     * returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     * <p>
     * This method is the same as {@link #anyOf(CompletionStage[])}
     * except for the any-<strong>success</strong> behavior instead of any-<strong>complete</strong>.
     *
     * @param cfs the stages
     * @return a new Cffu that is successful when any of the given stages success, with the same result
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<T> anyOfSuccess(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.anyOfSuccess(cfs));
    }

    /**
     * Returns a new Cffu that is completed when any of the given stages complete, with the same result.<br>
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.<br>
     * If no stages are provided, returns an incomplete Cffu.
     * <p>
     * This method is the same as {@link CompletableFuture#anyOf(CompletableFuture[])},
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture},
     * and the return type is more specific {@code T} instead of {@code Object}.
     *
     * @param cfs the stages
     * @return a new Cffu that is completed with the result or exception
     * from any of the given stages when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfSuccess(CompletionStage[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> anyOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.anyOf(cfs));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostTupleOfSuccess Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is successful when the given two stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new Cffu that is successful when the given two stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is completed when the given two stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given two stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is successful when the given three stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new Cffu that is successful when the given three stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is completed when the given three stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given three stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is successful when the given four stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new Cffu that is successful when the given four stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is completed when the given four stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given four stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is successful when the given five stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new Cffu that is successful when the given five stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new Cffu that is completed when the given five stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given five stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of
     * the given two stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given two stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> mostTupleOfSuccess(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.mostTupleOfSuccess(defaultExecutor, timeout, unit, cf1, cf2));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of
     * the given three stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given three stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mostTupleOfSuccess(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.mostTupleOfSuccess(defaultExecutor, timeout, unit, cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of
     * the given four stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given four stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mostTupleOfSuccess(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.mostTupleOfSuccess(defaultExecutor, timeout, unit, cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of
     * the given five stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given five stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mostTupleOfSuccess(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.mostTupleOfSuccess(defaultExecutor, timeout, unit, cf1, cf2, cf3, cf4, cf5));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## Immediate Value Argument Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is already completed with the given value.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed Cffu
     * @see CompletableFuture#completedFuture(Object)
     */
    @Contract(pure = true)
    public <T> Cffu<T> completedFuture(@Nullable T value) {
        return create(CompletableFuture.completedFuture(value));
    }

    /**
     * Returns a new CompletionStage that is already completed with the given value
     * and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8, just return a Cffu with
     * a *normal* underlying CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed CompletionStage
     * @see CompletableFuture#completedStage(Object)
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> completedStage(@Nullable T value) {
        return createMin((CompletableFuture<T>) CompletableFutureUtils.completedStage(value));
    }

    /**
     * Returns a new Cffu that is already completed exceptionally with the given exception.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed Cffu
     * @see CompletableFuture#failedFuture(Throwable)
     */
    @Contract(pure = true)
    public <T> Cffu<T> failedFuture(Throwable ex) {
        return create(CompletableFutureUtils.failedFuture(ex));
    }

    /**
     * Returns a new CompletionStage that is already completed exceptionally
     * with the given exception and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8, just return a Cffu with
     * a *normal* underlying CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     * @see CompletableFuture#failedStage(Throwable)
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> failedStage(Throwable ex) {
        return createMin((CompletableFuture<T>) CompletableFutureUtils.<T>failedStage(ex));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## CompletionStage Argument Factory Methods
    //
    //    - toCffu:      CF/CompletionStage -> Cffu
    //    - toCffuArray: CF/CompletionStage[] -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a Cffu maintaining the same completion properties as this stage and this {@code CffuFactory} config.
     * If this stage is already a Cffu and have the same {@code CffuFactory}, this method may return this stage itself.
     *
     * @throws NullPointerException if the given stage is null
     * @see #toCffuArray(CompletionStage[])
     * @see CompletionStage#toCompletableFuture()
     * @see Cffu#resetCffuFactory(CffuFactory)
     */
    @Contract(pure = true)
    public <T> Cffu<T> toCffu(CompletionStage<T> stage) {
        requireNonNull(stage, "stage is null");
        if (stage instanceof Cffu) {
            Cffu<T> f = ((Cffu<T>) stage);
            if (f.cffuFactory() == this && !f.isMinimalStage()) return f;
        }
        return create(stage.toCompletableFuture());
    }

    /**
     * A convenient util method for wrap input {@link CompletableFuture} / {@link CompletionStage} / {@link Cffu}
     * array element by {@link #toCffu(CompletionStage)}.
     *
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #toCffu(CompletionStage)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] toCffuArray(CompletionStage<T>... stages) {
        requireNonNull(stages, "stages is null");
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = toCffu(requireNonNull(stages[i], "stage" + (i + 1) + " is null"));
        }
        return ret;
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Delay Execution
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Executor that submits a task to the default executor
     * after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's {@code execute} method.
     *
     * @param delay how long to delay, in units of {@code unit}
     * @param unit  a {@code TimeUnit} determining how to interpret the {@code delay} parameter
     * @return the new delayed executor
     */
    @Contract(pure = true)
    public Executor delayedExecutor(long delay, TimeUnit unit) {
        return CompletableFutureUtils.delayedExecutor(delay, unit, defaultExecutor);
    }

    /**
     * Returns a new Executor that submits a task to the given base executor
     * after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's {@code execute} method.
     *
     * @param delay    how long to delay, in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     */
    @Contract(pure = true)
    public Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        return CompletableFutureUtils.delayedExecutor(delay, unit, executor);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Conversion Methods(static methods)
    //
    //    - cffuArrayUnwrap: Cffu[] -> CompletableFuture[]
    //    - cffuListToArray: List<Cffu> -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A convenient util method for unwrap input {@link Cffu} array elements by {@link Cffu#cffuUnwrap()}.
     *
     * @param cfs the Cffus
     * @see CompletableFutureUtils#toCompletableFutureArray(CompletionStage[])
     * @see Cffu#cffuUnwrap()
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] cffuArrayUnwrap(Cffu<T>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = requireNonNull(cfs[i], "cf" + (i + 1) + " is null").cffuUnwrap();
        }
        return ret;
    }

    /**
     * Convert Cffu list to Cffu array.
     *
     * @see CompletableFutureUtils#completableFutureListToArray(List)
     */
    @Contract(pure = true)
    public static <T> Cffu<T>[] cffuListToArray(List<Cffu<T>> cffuList) {
        @SuppressWarnings("unchecked")
        final Cffu<T>[] a = new Cffu[cffuList.size()];
        return cffuList.toArray(a);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Getter Methods of CffuFactory properties
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * Configured by {@link CffuFactory#builder(Executor)}.
     *
     * @return the default executor
     * @see Cffu#defaultExecutor()
     * @see CffuFactory#builder(Executor)
     */
    @Contract(pure = true)
    public Executor defaultExecutor() {
        return defaultExecutor;
    }

    /**
     * Returns {@code forbidObtrudeMethods} or not.
     *
     * @see Cffu#obtrudeValue(Object)
     * @see Cffu#obtrudeException(Throwable)
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     */
    @Contract(pure = true)
    public boolean forbidObtrudeMethods() {
        return forbidObtrudeMethods;
    }
}
