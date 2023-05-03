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
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.screenExecutor;
import static java.util.Objects.requireNonNull;


/**
 * This class {@link CffuFactory} is equivalent to {@link CompletableFuture},
 * contains the static factory methods of {@link CompletableFuture}.
 * <p>
 * The methods that equivalent to the instance methods of {@link CompletableFuture} is in {@link Cffu} class.
 * <p>
 * Use {@link CffuFactoryBuilder} to config and build {@link CffuFactory}.
 * <p>
 * About factory methods conventions of {@link CffuFactory}:
 * <ul>
 *   <li>factory methods return {@link Cffu} instead of {@link CompletableFuture}.
 *   <li>new methods, aka. no equivalent method in {@link CompletableFuture}, prefix method name with {@code cffu}.
 *   <li>only provide varargs methods for multiply Cffu/CF input arguments;
 *     if you have {@code List} input, use static util methods {@link #cffuListToArray(List)}
 *     or {@link #completableFutureListToArray(List)} to convert it to array first.
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CffuFactoryBuilder
 * @see Cffu
 * @see CompletableFuture
 */
@Immutable
@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CffuFactory {
    @NonNull
    private final Executor defaultExecutor;

    private final boolean forbidObtrudeMethods;

    CffuFactory(Executor defaultExecutor, boolean forbidObtrudeMethods) {
        this.defaultExecutor = screenExecutor(defaultExecutor);
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
     * <b><i>CAUTION:<br></i></b>
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
     * <b><i>CAUTION:<br></i></b>
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
    private <T> Cffu<T> dummy() {
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
     *     <li>{@link #runAsync(Runnable)}
     *     <li>{@link #supplyAsync(Supplier, Executor)}
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
     * Wrap an existed {@link CompletableFuture}/{@link CompletionStage}/{@link Cffu} to {@link Cffu}.
     * for {@link CompletableFuture} class instances,
     * {@link Cffu#cffuUnwrap()} is the inverse operation to this method.
     * <p>
     * <b><i>NOTE</i></b>, keep input stage unchanged if possible when wrap:<br>
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
     * A convenient util method for wrap
     * input {@link CompletableFuture}/{@link CompletionStage}/{@link Cffu} array element
     * by {@link #asCffu(CompletionStage)}.
     *
     * @see #asCffu(CompletionStage)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] asCffuArray(CompletionStage<T>... stages) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = asCffu(requireNonNull(stages[i], "stage" + i + " is null"));
        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf / anyOf methods, without return result type enhancement
    //  - allOf*, return Cffu<Void>, equivalent to same name methods:
    //    - CompletableFuture.allOf()
    //    - CompletableFutureUtils.allOfFastFail()
    //  - anyOf*, return Cffu<Object>, equivalent to same name methods:
    //    - CompletableFuture.anyOf()
    //    - CompletableFutureUtils.anyOfSuccess()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when all the given Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.<br>
     * Otherwise, the results, if any, of the given Cffus are not reflected in
     * the returned Cffu({@code Cffu<Void>}), but may be obtained by inspecting them individually.<br>
     * If no Cffus are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * if you need the results of given Cffus, prefer below methods:
     * <ol>
     *   <li>{@link #cffuAllOf(Cffu[])}
     *   <li>{@link #cffuCombine(Cffu, Cffu)}/{@link #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)}
     *       (provided overloaded methods with 2~5 input)
     * </ol>
     * <p>
     * Among the applications of this method is to await completion of a set of
     * independent Cffus before continuing a program,
     * as in: {@code CffuFactory.allOf(c1, c2, c3).join();}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed when all the given Cffus complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAllOf(Cffu[])
     * @see #cffuCombine(Cffu, Cffu)
     * @see #cffuCombine(Cffu, Cffu, Cffu)
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu)
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public Cffu<Void> allOf(Cffu<?>... cfs) {
        return allOf(toCompletableFutureArray((Cffu<Object>[]) cfs));
    }

    /**
     * Returns a new Cffu that is completed when all the given CompletableFutures complete.
     * If any of the given CompletableFuture complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.<br>
     * Otherwise, the results, if any, of the given CompletableFutures are not reflected in
     * the returned Cffu({@code Cffu<Void>}), but may be obtained by inspecting them individually.<br>
     * If no CompletableFutures are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * Same as {@link #allOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     * <p>
     * if you need the results of given CompletableFutures, prefer below methods:
     * <ol>
     *   <li>{@link #cffuAllOf(CompletableFuture[])}
     *   <li>{@link #cffuCombine(CompletableFuture, CompletableFuture)}/{@link #cffuCombine(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture)}
     *       (provided overloaded methods with 2~5 input)
     * </ol>
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed when all the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOf(Cffu[])
     * @see #cffuAllOf(CompletableFuture[])
     * @see #cffuCombine(CompletableFuture, CompletableFuture)
     * @see #cffuCombine(CompletableFuture, CompletableFuture, CompletableFuture)
     * @see #cffuCombine(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture)
     * @see #cffuCombine(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture)
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.allOf(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "allOf is ambiguous" problem
     * when call {@code allOf} with empty arguments: {@code cffuFactory.allOf()}.
     *
     * @see #allOf(Cffu[])
     * @see #allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOf() {
        return dummy();
    }

    /**
     * Returns a new Cffu that is successful when all the given Cffus success,
     * the results({@code Cffu<Void>}) of the given Cffus are not reflected
     * in the returned Cffu, but may be obtained by inspecting them individually.
     * If any of the given Cffus complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given Cffus,
     * with a CompletionException holding this exception as its cause.
     * If no Cffus are provided, returns a Cffu completed with the value {@code null}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is successful when all the given Cffus success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFutureUtils#allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public Cffu<Void> allOfFastFail(Cffu<?>... cfs) {
        return allOfFastFail(toCompletableFutureArray((Cffu<Object>[]) cfs));
    }

    /**
     * Returns a new Cffu that is successful when all the given CompletableFutures success,
     * the results({@code Cffu<Void>}) of the given CompletableFutures are not reflected
     * in the returned Cffu, but may be obtained by inspecting them individually.
     * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * Same as {@link #allOfFastFail(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOfFastFail(Cffu[])
     * @see #cffuAllOfFastFail(CompletableFuture[])
     * @see CompletableFutureUtils#allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOfFastFail(CompletableFuture<?>... cfs) {
        return new0(CompletableFutureUtils.allOfFastFail(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "allOfFastFail is ambiguous" problem
     * when call {@code allOfFastFail} with empty arguments: {@code cffuFactory.allOfFastFail()}.
     *
     * @see #allOfFastFail(Cffu[])
     * @see #allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOfFastFail() {
        return new0(CompletableFutureUtils.allOfFastFail());
    }

    /**
     * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.<br>
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.<br>
     * If no Cffus are provided, returns an incomplete Cffu.
     * <p>
     * prefer {@link #cffuAnyOf(Cffu[])} method if the given Cffus have same result type,
     * because {@link #cffuAnyOf(Cffu[])} return type {@code T} instead of type {@code Object}, more type safe.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed with the result
     * or exception from any of the given Cffus when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(Cffu[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public Cffu<Object> anyOf(Cffu<?>... cfs) {
        return anyOf(toCompletableFutureArray((Cffu<Object>[]) cfs));
    }

    /**
     * Same as {@link #anyOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     * <p>
     * prefer {@link #cffuAnyOf(CompletableFuture[])} method if the given Cffus have same result type,
     * because {@link #cffuAnyOf(CompletableFuture[])} return type {@code T}
     * instead of type {@code Object}, more type safe.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed with the result
     * or exception from any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(CompletableFuture[])
     * @see #anyOf(Cffu[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Object> anyOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.anyOf(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "anyOf is ambiguous" problem
     * when call {@code anyOf} with empty arguments: {@code cffuFactory.anyOf()}.
     *
     * @see #anyOf(Cffu[])
     * @see #anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Object> anyOf() {
        return newIncompleteCffu();
    }

    /**
     * Returns a new Cffu that is successful when any of the given Cffus success,
     * with the same result. Otherwise, all the given Cffus complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given Cffu as its cause. If no Cffu are provided,
     * returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param cfs the Cffus
     * @return a new Cffu
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(Cffu[])
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Cffu<Object> anyOfSuccess(Cffu<?>... cfs) {
        return anyOfSuccess(toCompletableFutureArray((Cffu[]) cfs));
    }

    /**
     * Returns a new Cffu that is successful when any of the given CompletableFutures success,
     * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
     * returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOfSuccess(Cffu[])
     * @see #cffuAnyOf(Cffu[])
     */
    public Cffu<Object> anyOfSuccess(CompletableFuture<?>... cfs) {
        return new0(CompletableFutureUtils.anyOfSuccess(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "cffuAnyOfSuccess is ambiguous" problem
     * when call {@code anyOfSuccess} with empty arguments: {@code cffuFactory.anyOfSuccess()}.
     *
     * @see #cffuAnyOfSuccess(Cffu[])
     * @see #cffuAnyOfSuccess(CompletableFuture[])
     */
    public Cffu<Object> anyOfSuccess() {
        return new0(CompletableFutureUtils.anyOfSuccess());
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
    //# New type-safe allOf/anyOf Factory Methods
    //    method name prefix with `cffu`
    //
    //    - cffuAllOf
    //    - cffuAnyOf
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu with the results of all the given Cffus,
     * the new Cffu is completed when all the given Cffus complete.
     * Returns a new Cffu that is completed when all the given Cffus complete.
     * If any of the given Cffus complete exceptionally, then the returned Cffu
     * also does so, with a CompletionException holding this exception as its cause.
     * If no Cffus are provided, returns a Cffu completed
     * with the value {@link java.util.Collections#emptyList() emptyList}.
     * <p>
     * Same to {@link #allOf(Cffu[])}, but the returned Cffu
     * contains the results of input Cffus.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed when all the given Cffus complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOf(Cffu<T>... cfs) {
        return cffuAllOf(toCompletableFutureArray(cfs));
    }

    /**
     * Returns a new Cffu with the results of all the given CompletableFutures,
     * the new Cffu is completed when all the given CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
     * also does so, with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a Cffu completed
     * with the value {@link java.util.Collections#emptyList() emptyList}.
     * <p>
     * Same as {@link #cffuAllOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed when all the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOf(CompletableFuture<T>... cfs) {
        return new0(CompletableFutureUtils.allOfWithResult(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "cffuAllOf is ambiguous" problem
     * when call {@code cffuAllOf} with empty arguments: {@code cffuFactory.cffuAllOf()}.
     *
     * @see #cffuAllOf(Cffu[])
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T> Cffu<List<T>> cffuAllOf() {
        return new0(CompletableFutureUtils.allOfWithResult());
    }

    /**
     * Returns a new Cffu with the results of all the given Cffus,
     * the new Cffu success when all the given Cffus success.
     * If any of the given Cffus complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given Cffus,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a Cffu completed
     * with the value {@link Collections#emptyList() emptyList}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFutureUtils#allOfFastFail(CompletableFuture[])
     * @see #cffuAllOfFastFail(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfFastFail(Cffu<T>... cfs) {
        return cffuAllOfFastFail(toCompletableFutureArray(cfs));
    }

    /**
     * Returns a new Cffu with the results of all the given CompletableFutures,
     * the new Cffu success when all the given CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a Cffu completed
     * with the value {@link Collections#emptyList() emptyList}.
     * <p>
     * Same as {@link #cffuAllOfFastFail(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFutureUtils#allOfFastFail(CompletableFuture[])
     * @see #cffuAllOfFastFail(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfFastFail(CompletableFuture<T>... cfs) {
        return new0(CompletableFutureUtils.allOfFastFailWithResult(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "cffuAllOfFastFail is ambiguous" problem
     * when call {@code cffuAllOfFastFail} with empty arguments: {@code cffuFactory.cffuAllOfFastFail()}.
     *
     * @see #cffuAllOfFastFail(Cffu[])
     * @see #cffuAllOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T> Cffu<List<T>> cffuAllOfFastFail() {
        return new0(CompletableFutureUtils.allOfFastFailWithResult());
    }

    /**
     * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
     * <p>
     * Same as {@link #anyOf(Cffu[])}, but return result type is specified type instead of type {@code Object}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed with the result
     * or exception from any of the given Cffus when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(Cffu<T>... cfs) {
        return cffuAnyOf(toCompletableFutureArray(cfs));
    }

    /**
     * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
     * <p>
     * Same as {@link #cffuAllOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed with the result
     * or exception from any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(CompletableFuture<T>... cfs) {
        return new0(CompletableFutureUtils.anyOfWithType(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "cffuAnyOf is ambiguous" problem
     * when call {@code cffuAnyOf} with empty arguments: {@code cffuFactory.cffuAnyOf()}.
     *
     * @see #cffuAnyOf(Cffu[])
     * @see #cffuAnyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T> Cffu<T> cffuAnyOf() {
        return newIncompleteCffu();
    }

    /**
     * Returns a new Cffu that is successful when any of the given Cffus success,
     * with the same result. Otherwise, all the given Cffus complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given Cffu as its cause. If no Cffu are provided,
     * returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param cfs the Cffus
     * @return a new Cffu
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(Cffu[])
     */
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOfSuccess(Cffu<T>... cfs) {
        return cffuAnyOfSuccess(toCompletableFutureArray(cfs));
    }

    /**
     * Returns a new Cffu that is successful when any of the given CompletableFutures success,
     * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
     * returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     * <p>
     * Same as {@link #cffuAnyOfSuccess(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOfSuccess(Cffu[])
     * @see #cffuAnyOf(Cffu[])
     */
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOfSuccess(CompletableFuture<T>... cfs) {
        return new0(CompletableFutureUtils.anyOfSuccessWithType(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "cffuAnyOfSuccess is ambiguous" problem
     * when call {@code cffuAnyOfSuccess} with empty arguments: {@code cffuFactory.cffuAnyOfSuccess()}.
     *
     * @see #cffuAnyOfSuccess(Cffu[])
     * @see #cffuAnyOfSuccess(CompletableFuture[])
     */
    public <T> Cffu<T> cffuAnyOfSuccess() {
        return new0(CompletableFutureUtils.anyOfSuccessWithType());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe cffuCombine Factory Methods
    //  support 2~5 input arguments, method name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when the given two Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuAllOf(Cffu[])} but with two inputs and return results as {@code Tuple2}.
     *
     * @return a new Cffu that is completed when the given two Cffus complete
     * @throws NullPointerException if any input Cffus are {@code null}
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> cffuCombine(Cffu<T1> cf1, Cffu<T2> cf2) {
        return cffuCombine(cf1.toCompletableFuture(), cf2.toCompletableFuture());
    }

    /**
     * Returns a new Cffu that is completed when the given two CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuCombine(Cffu, Cffu)} with overloaded argument type {@link CompletableFuture}.
     *
     * @return a new Cffu that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> cffuCombine(CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        return new0(CompletableFutureUtils.combine(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is completed when the given three Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuAllOf(Cffu[])} but with three inputs and return results as {@code Tuple3}.
     *
     * @return a new Cffu that is completed when the given three Cffus complete
     * @throws NullPointerException if any input Cffus are {@code null}
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> cffuCombine(Cffu<T1> cf1, Cffu<T2> cf2, Cffu<T3> cf3) {
        return cffuCombine(cf1.toCompletableFuture(), cf2.toCompletableFuture(), cf3.toCompletableFuture());
    }

    /**
     * Returns a new Cffu that is completed when the given three CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuCombine(Cffu, Cffu, Cffu)} with overloaded argument type {@link CompletableFuture}.
     *
     * @return a new Cffu that is completed when the given 3 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> cffuCombine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        return new0(CompletableFutureUtils.combine(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is completed when the given 4 Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuAllOf(Cffu[])} but with 4 inputs and return results as {@code Tuple4}.
     *
     * @return a new Cffu that is completed when the given 4 Cffus complete
     * @throws NullPointerException if any input Cffus are {@code null}
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> cffuCombine(
            Cffu<T1> cf1, Cffu<T2> cf2, Cffu<T3> cf3, Cffu<T4> cf4) {
        return cffuCombine(cf1.toCompletableFuture(), cf2.toCompletableFuture(),
                cf3.toCompletableFuture(), cf4.toCompletableFuture());
    }

    /**
     * Returns a new Cffu that is completed when the given 4 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuCombine(Cffu, Cffu, Cffu, Cffu)} with overloaded argument type {@link CompletableFuture}.
     *
     * @return a new Cffu that is completed when the given 4 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> cffuCombine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4) {
        return new0(CompletableFutureUtils.combine(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is completed when the given 5 Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuAllOf(Cffu[])} but with 5 inputs and return results as {@code Tuple5}.
     *
     * @return a new Cffu that is completed when the given 5 Cffus complete
     * @throws NullPointerException if any input Cffus are {@code null}
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> cffuCombine(
            Cffu<T1> cf1, Cffu<T2> cf2, Cffu<T3> cf3, Cffu<T4> cf4, Cffu<T5> cf5) {
        return cffuCombine(cf1.toCompletableFuture(), cf2.toCompletableFuture(),
                cf3.toCompletableFuture(), cf4.toCompletableFuture(), cf5.toCompletableFuture());
    }

    /**
     * Returns a new Cffu that is completed when the given 5 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)}
     * with overloaded argument type {@link CompletableFuture}.
     *
     * @return a new Cffu that is completed when the given 5 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> cffuCombine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4, CompletableFuture<T5> cf5) {
        return new0(CompletableFutureUtils.combine(cf1, cf2, cf3, cf4, cf5));
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
     * A convenient util method for converting
     * input {@link Cffu}/{@link CompletableFuture}/{@link CompletionStage} array element
     * by {@link Cffu#toCompletableFuture()}/{@link CompletableFuture#toCompletableFuture()}/{@link CompletionStage#toCompletableFuture()}.
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
            ret[i] = requireNonNull(stages[i], "stage" + i + " is null").toCompletableFuture();
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
            ret[i] = requireNonNull(cfs[i], "cf" + i + " is null").cffuUnwrap();
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
     * Returns {@code defaultExecutor}.
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
