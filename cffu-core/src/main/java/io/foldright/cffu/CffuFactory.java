package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
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
 * Use {@link CffuFactoryBuilder} to config and build {@link CffuFactory}.
 * <p>
 * About factory methods conventions of {@link CffuFactory}:
 * <ul>
 * <li>factory methods return {@link Cffu} instead of {@link CompletableFuture}.
 * <li>only provide varargs methods for multiply Cffu/CF input arguments;
 *     if you have {@code List} input, use static util methods {@link #cffuListToArray(List)}
 *     or {@link #completableFutureListToArray(List)} to convert it to array first.
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CffuFactoryBuilder
 * @see Cffu
 * @see CompletableFuture
 */
@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CffuFactory {
    @NonNull
    private final Executor defaultExecutor;

    private final boolean forbidObtrudeMethods;

    CffuFactory(Executor defaultExecutor, boolean forbidObtrudeMethods) {
        this.defaultExecutor = CompletableFutureUtils.screenExecutor(defaultExecutor);
        this.forbidObtrudeMethods = forbidObtrudeMethods;
    }

    @Contract(pure = true)
    private <T> Cffu<T> new0(CompletableFuture<T> cf) {
        return new Cffu<>(this, false, cf);
    }

    @Contract(pure = true)
    private <T> Cffu<T> newMin(CompletableFuture<T> cf) {
        return new Cffu<>(this, true, cf);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  Create by immediate value
    //    - completedFuture/completedStage
    //    - failedFuture/failedStage
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
        return new0(CompletableFuture.completedFuture(value));
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
        return newMin((CompletableFuture<T>) CompletableFutureUtils.completedStage(value));
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
        return new0(CompletableFutureUtils.failedFuture(ex));
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
        return newMin((CompletableFuture<T>) CompletableFutureUtils.<T>failedStage(ex));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  create by logic/lambda
    //    - runAsync*
    //    - supplyAsync*
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * a completed Cffu with the value {@code null}
     */
    @Contract(pure = true)
    private Cffu<Void> dummy() {
        return completedFuture(null);
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
        return dummy().thenRunAsync(action);
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
        return dummy().thenRunAsync(action, executor);
    }

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
    @SuppressWarnings("BoundedWildcard")
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier) {
        return dummy().thenApplyAsync(unused -> supplier.get());
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
    @SuppressWarnings("BoundedWildcard")
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return dummy().thenApplyAsync(unused -> supplier.get(), executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods
    //
    //    - newIncompleteCffu: equivalent to CompletableFuture constructor
    //
    //    - asCffu:      CF/CompletionStage -> Cffu
    //    - asCffuArray: CF/CompletionStage[] -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Return an incomplete Cffu, equivalent to {@link CompletableFuture#CompletableFuture()} constructor.
     * <p>
     * In general, should not use this method in biz code, prefer below factory methods of Cffu:
     *
     * <ol>
     * <li>{@link #runAsync(Runnable)}
     * <li>{@link #supplyAsync(Supplier, Executor)}
     * </ol>
     *
     * @see #runAsync(Runnable)
     * @see #runAsync(Runnable, Executor)
     * @see #supplyAsync(Supplier)
     * @see #supplyAsync(Supplier, Executor)
     * @see CompletableFuture#CompletableFuture()
     */
    @Contract(pure = true)
    public <T> Cffu<T> newIncompleteCffu() {
        return new0(new CompletableFuture<>());
    }

    /**
     * Wrap an existed {@link CompletableFuture} / {@link CompletionStage} / {@link Cffu} to {@link Cffu}.
     * for {@link CompletableFuture} class instances,
     * {@link Cffu#cffuUnwrap()} is the inverse operation to this method.
     * <p>
     * <strong>NOTE</strong>, keep input stage unchanged if possible when wrap:<br>
     * <ol>
     * <li>if input stage is a {@link Cffu}, re-wrapped with the config of
     *     this {@link CffuFactory} from {@link CffuFactoryBuilder} by {@link Cffu#resetCffuFactory(CffuFactory)}.
     * <li>if input stage is a CompletableFuture, wrap it by setting it as the underlying cf of returned cffu.
     * <li>otherwise use input {@code stage.toCompletableFuture} as the underlying cf of returned cffu.
     * </ol>
     *
     * @see Cffu#cffuUnwrap()
     * @see Cffu#resetCffuFactory(CffuFactory)
     * @see #asCffuArray(CompletionStage[])
     * @see CompletionStage#toCompletableFuture()
     */
    @Contract(pure = true)
    public <T> Cffu<T> asCffu(CompletionStage<T> stage) {
        requireNonNull(stage, "stage is null");

        if ("java.util.concurrent.CompletableFuture$MinimalStage".equals(stage.getClass().getName())) {
            return newMin((CompletableFuture<T>) stage);
        } else if (stage instanceof CompletableFuture) {
            return new0((CompletableFuture<T>) stage);
        } else if (stage instanceof Cffu) {
            return ((Cffu<T>) stage).resetCffuFactory(this);
        }
        return new0(stage.toCompletableFuture());
    }

    /**
     * A convenient util method for wrap input {@link CompletableFuture} / {@link CompletionStage} / {@link Cffu}
     * array element by {@link #asCffu(CompletionStage)}.
     *
     * @see #asCffu(CompletionStage)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] asCffuArray(CompletionStage<T>... stages) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = asCffu(requireNonNull(stages[i], "stage" + (i + 1) + " is null"));
        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf* methods
    //
    //  - allOf / allOfFastFail
    //  - allResultsOf / allResultsOfFastFail
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when all the given stages complete.
     * If any of the given stages complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.<br>
     * Otherwise, the results, if any, of the given stages are not reflected in
     * the returned Cffu({@code Cffu<Void>}), but may be obtained by inspecting them individually.<br>
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * if you need the results of given stages, prefer below methods:
     * <ol>
     * <li>{@link #allResultsOf(CompletionStage[])}
     * <li>{@link #allTupleOf(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ol>
     *
     * @param cfs the stages
     * @return a new Cffu that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see #allTupleOf(CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Cffu<Void> allOf(CompletionStage<?>... cfs) {
        return new0(CompletableFuture.allOf(toCompletableFutureArray((CompletionStage[]) cfs)));
    }

    /**
     * Returns a new Cffu that is successful when all the given stages success,
     * the results({@code Cffu<Void>}) of the given stages are not reflected
     * in the returned Cffu, but may be obtained by inspecting them individually.
     * If any of the given stages complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given stages,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     *
     * @param cfs the stages
     * @return a new Cffu that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFutureUtils#allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOfFastFail(CompletionStage<?>... cfs) {
        return new0(CompletableFutureUtils.allOfFastFail(cfs));
    }

    /**
     * Returns a new Cffu with the results in the <strong>same order</strong> of all the given stages,
     * the new Cffu is completed when all the given stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu
     * also does so, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     *
     * @param cfs the stages
     * @return a new Cffu that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        return new0(CompletableFutureUtils.allResultsOf(cfs));
    }

    /**
     * Returns a new Cffu with the results in the <strong>same order</strong> of all the given stages,
     * the new Cffu success when all the given stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given stages,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     *
     * @param cfs the stages
     * @return a new Cffu that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFutureUtils#allOfFastFail(CompletionStage[])
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsOfFastFail(CompletionStage<? extends T>... cfs) {
        return new0(CompletableFutureUtils.allResultsOfFastFail(cfs));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods:
    //
    //  - anyOf
    //  - anyOfSuccess
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when any of the given stages complete, with the same result.<br>
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.<br>
     * If no stages are provided, returns an incomplete Cffu.
     *
     * @param cfs the stages
     * @return a new Cffu that is completed with the result
     * or exception from any of the given stages when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOf(CompletionStage[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> anyOf(CompletionStage<? extends T>... cfs) {
        return new0(CompletableFutureUtils.anyOf(cfs));
    }

    /**
     * Returns a new Cffu that is successful when any of the given stages success,
     * with the same result. Otherwise, all the given stages complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause. If no stages are provided,
     * returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param cfs the stages
     * @return a new Cffu
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    @SafeVarargs
    public final <T> Cffu<T> anyOfSuccess(CompletionStage<? extends T>... cfs) {
        return new0(CompletableFutureUtils.anyOfSuccess(cfs));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe allTupleOf Factory Methods, support 2~5 input arguments
    //
    //  - allTupleOf
    //  - allTupleOfFastFail
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when the given two CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOf(CompletionStage)
     * @see #allResultsOf(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleOf(CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return new0(CompletableFutureUtils.allTupleOf(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is successful when the given two CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given two CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOfFastFail(CompletionStage)
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleOfFastFail(CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return new0(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is completed when the given three CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given 3 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOf(CompletionStage, CompletionStage)
     * @see #allResultsOf(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return new0(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is successful when the given three CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given three CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOfFastFail(CompletionStage, CompletionStage)
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return new0(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is completed when the given 4 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given 4 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOf(CompletionStage, CompletionStage, CompletionStage)
     * @see #allResultsOf(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return new0(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4));
    }


    /**
     * Returns a new Cffu that is successful when the given four CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given four CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage)
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return new0(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is completed when the given 5 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given 5 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allResultsOf(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return new0(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new Cffu that is successful when the given five CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given five CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see Cffu#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage)
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return new0(CompletableFutureUtils.allTupleOfFastFail(cf1, cf2, cf3, cf4, cf5));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Delay Execution, equivalent to same name static methods of CompletableFuture
    //
    //    - delayedExecutor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Executor that submits a task to the default executor
     * after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's {@code execute} method.
     *
     * @param delay how long to delay, in units of {@code unit}
     * @param unit  a {@code TimeUnit} determining how to interpret the
     *              {@code delay} parameter
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
     * @param unit     a {@code TimeUnit} determining how to interpret the
     *                 {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     */
    @Contract(pure = true)
    public Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        return CompletableFutureUtils.delayedExecutor(delay, unit, executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion (Static) Methods
    //
    //    - toCompletableFutureArray:     Cffu -> CF
    //    - cffuArrayUnwrap:              Cffu -> CF
    //
    //    - cffuListToArray:              List<Cffu> -> Cffu[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A convenient util method for converting input {@link Cffu} / {@link CompletableFuture} / {@link CompletionStage}
     * array element by {@link Cffu#toCompletableFuture()} / {@link CompletableFuture#toCompletableFuture()} /
     * {@link CompletionStage#toCompletableFuture()}.
     *
     * @see Cffu#toCompletableFuture()
     * @see CompletableFuture#toCompletableFuture()
     * @see CompletionStage#toCompletableFuture()
     * @see #asCffuArray(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] toCompletableFutureArray(CompletionStage<T>... stages) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = requireNonNull(stages[i], "stage" + (i + 1) + " is null").toCompletableFuture();
        }
        return ret;
    }

    /**
     * A convenient util method for unwrap input {@link Cffu} array elements by {@link Cffu#cffuUnwrap()}.
     *
     * @param cfs the Cffus
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
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> Cffu<T>[] cffuListToArray(List<Cffu<T>> cffuList) {
        return cffuList.toArray(new Cffu[0]);
    }

    /**
     * Convert CompletableFuture list to CompletableFuture array.
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T>[] completableFutureListToArray(List<CompletableFuture<T>> cfList) {
        return cfList.toArray(new CompletableFuture[0]);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Getter methods of CffuFactory properties
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * Configured by {@link CffuFactoryBuilder#newCffuFactoryBuilder(Executor)}.
     *
     * @return the default executor
     * @see Cffu#defaultExecutor()
     * @see CffuFactoryBuilder#newCffuFactoryBuilder(Executor)
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
