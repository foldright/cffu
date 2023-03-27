package io.foldright.cffu;

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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

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
    final Executor defaultExecutor;

    final boolean forbidObtrudeMethods;

    CffuFactory(Executor defaultExecutor, boolean forbidObtrudeMethods) {
        this.defaultExecutor = screenExecutor(defaultExecutor);
        this.forbidObtrudeMethods = forbidObtrudeMethods;
    }

    /**
     * Null-checks user executor argument, and translates uses of
     * commonPool to ASYNC_POOL in case parallelism disabled.
     */
    @SuppressWarnings("resource")
    private static Executor screenExecutor(Executor e) {
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool())
            return AsyncPoolHolder.ASYNC_POOL;
        return requireNonNull(e, "defaultExecutor is null");
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
     * a *normal* underneath CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed CompletionStage
     * @see CompletableFuture#completedStage(Object)
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> completedStage(@Nullable T value) {
        if (IS_JAVA9_PLUS) {
            return newMin((CompletableFuture<T>) CompletableFuture.completedStage(value));
        }
        return newMin(CompletableFuture.completedFuture(value));
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
        if (IS_JAVA9_PLUS) {
            return new0(CompletableFuture.failedFuture(ex));
        }
        final CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(ex);
        return new0(cf);
    }

    /**
     * Returns a new CompletionStage that is already completed exceptionally
     * with the given exception and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <b><i>CAUTION:<br></i></b>
     * if run on old Java 8, just return a Cffu with
     * a *normal* underneath CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     * @see CompletableFuture#failedStage(Throwable)
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> failedStage(Throwable ex) {
        if (IS_JAVA9_PLUS) {
            return newMin((CompletableFuture<T>) CompletableFuture.<T>failedStage(ex));
        }
        CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(ex);
        return newMin(cf);
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
     * Wrap an existed {@link CompletableFuture}/{@link CompletionStage} to {@link Cffu}.
     * for {@link CompletableFuture} class instances,
     * {@link Cffu#cffuUnwrap()} is the inverse operation to this method.
     * <p>
     * <b><i>NOTE</i></b>, keep input stage unchanged if possible when wrap:<br>
     * <ol>
     * <li>if input stage is a {@link Cffu}, re-wrapped with the config of
     *     this {@link CffuFactory} from {@link CffuFactoryBuilder}.
     * <li>if input stage is a CompletableFuture, set it as the underneath cf of returned cffu.
     * <li>otherwise use input {@code stage.toCompletableFuture} as the underneath cf of returned cffu.
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
     * A convenient util method for wrap input {@link CompletableFuture}/{@link CompletionStage} array element
     * using {@link #asCffu(CompletionStage)}.
     *
     * @see #asCffu(CompletionStage)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] asCffuArray(CompletionStage<T>... stages) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = asCffu(stages[i]);
        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf / anyOf methods, equivalent to same name static methods of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when all of the given Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.<br>
     * Otherwise, the results, if any, of the given Cffus are not reflected in
     * the returned Cffu({@code Cffu<Void>}), but may be obtained by inspecting them individually.<br>
     * If no Cffus are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * if you need the results of given Cffus, prefer below methods:
     * <ol>
     *   <li>{{@link #cffuAllOf(Cffu[])}}
     *   <li>{@link #cffuCombine(Cffu, Cffu)}/{@link #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)}
     *       (provided overloaded methods with 2~5 input)
     * </ol>
     * <p>
     * Among the applications of this method is to await completion of a set of
     * independent Cffus before continuing a program,
     * as in: {@code CffuFactory.allOf(c1, c2, c3).join();}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed when all of the given Cffus complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAllOf(Cffu[])
     * @see #cffuCombine(Cffu, Cffu)
     * @see #cffuCombine(Cffu, Cffu, Cffu)
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu)
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOf(Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFutureArray((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.allOf(args));
    }

    /**
     * Same as {@link #allOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     * <p>
     * if you need the results of given Cffus, prefer below methods:
     * <ol>
     *   <li>{{@link #cffuAllOf(CompletableFuture[])}}
     *   <li>{@link #cffuCombine(CompletableFuture, CompletableFuture)}/{@link #cffuCombine(CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture, CompletableFuture)}
     *       (provided overloaded methods with 2~5 input)
     * </ol>
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed when all of the given CompletableFutures complete
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
     * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.<br>
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.<br>
     * If no Cffus are provided, returns an incomplete Cffu.
     * <p>
     * prefer {@link #cffuAnyOf(Cffu[])} method if the given Cffus have same result type,
     * because {@link #cffuAnyOf(Cffu[])} return type {@code T} instead of {@code Object}, more type safe.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed with the result or exception of
     * any of the given Cffus when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(Cffu[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Object> anyOf(Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFutureArray((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.anyOf(args));
    }

    /**
     * Same as {@link #anyOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     * <p>
     * prefer {@link #cffuAnyOf(CompletableFuture[])} method if the given Cffus have same result type,
     * because {@link #cffuAnyOf(CompletableFuture[])} return type {@code T} instead of {@code Object}, more type safe.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed with the result or exception of
     * any of the given CompletableFutures when one completes
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
        return delayedExecutor(delay, unit, defaultExecutor);
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
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.delayedExecutor(delay, unit, executor);
        }

        requireNonNull(unit, "unit is null");
        requireNonNull(executor, "executor is null");
        return new DelayedExecutor(delay, unit, executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe allOf/anyOf Factory Methods
    //    method name prefix with `cffu`
    //
    //    - cffuAllOf
    //    - cffuAnyOf
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Same to {@link #allOf(Cffu[])}, but return the results of input {@link Cffu}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed when all of the given Cffus complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOf(Cffu<T>... cfs) {
        return cffuAllOf(toCompletableFutureArray(cfs));
    }

    /**
     * Same as {@link #cffuAllOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed when all of the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOf(CompletableFuture<T>... cfs) {
        for (CompletableFuture<T> cf : cfs) {
            requireNonNull(cf, "cf is null");
        }

        final int size = cfs.length;
        final Object[] result = new Object[size];

        final CompletableFuture<?>[] wrappedCfs = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final int index = i;

            CompletableFuture<T> cf = cfs[index];

            CompletableFuture<Void> wrapped = cf.thenAccept(x -> result[index] = x);
            wrappedCfs[index] = wrapped;
        }

        @SuppressWarnings("unchecked")
        CompletableFuture<List<T>> ret = CompletableFuture.allOf(wrappedCfs)
                .thenApply(unused -> (List<T>) Arrays.asList(result));

        return new0(ret);
    }

    /**
     * Provided this overloaded method just for resolving "cffuAllOf is ambiguous" problem
     * when call {@code cffuAllOf} with empty arguments: {@code cffuFactory.cffuAllOf()}.
     *
     * @see #cffuAllOf(Cffu[])
     * @see #cffuAllOf(Cffu[])
     */
    @Contract(pure = true)
    public <T> Cffu<List<T>> cffuAllOf() {
        return dummy();
    }

    /**
     * Same as {@link #anyOf(Cffu[])}, but return result type is specified type instead of {@code Object}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed with the result or exception of
     * any of the given Cffus when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(Cffu<T>... cfs) {
        return cffuAnyOf(toCompletableFutureArray(cfs));
    }

    /**
     * Same as {@link #cffuAllOf(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed with the result or exception of
     * any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAnyOf(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> Cffu<T> cffuAnyOf(CompletableFuture<T>... cfs) {
        return new0((CompletableFuture<T>) CompletableFuture.anyOf(cfs));
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
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2> Cffu<Tuple2<T1, T2>> cffuCombine(CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");

        final Object[] result = new Object[2];

        CompletableFuture<Tuple2<T1, T2>> ret = CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2)
                )
                .thenApply(unused ->
                        Tuple2.of((T1) result[0], (T2) result[1])
                );
        return new0(ret);
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
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> cffuCombine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");

        final Object[] result = new Object[3];

        CompletableFuture<Tuple3<T1, T2, T3>> ret = CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2),
                        cf3.thenAccept(t3 -> result[2] = t3)
                )
                .thenApply(unused ->
                        Tuple3.of((T1) result[0], (T2) result[1], (T3) result[2])
                );
        return new0(ret);
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
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> cffuCombine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");
        requireNonNull(cf4, "cf4 is null");

        final Object[] result = new Object[4];

        CompletableFuture<Tuple4<T1, T2, T3, T4>> ret = CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2),
                        cf3.thenAccept(t3 -> result[2] = t3),
                        cf4.thenAccept(t4 -> result[3] = t4)
                )
                .thenApply(unused ->
                        Tuple4.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3])
                );
        return new0(ret);
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
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see #cffuCombine(Cffu, Cffu, Cffu, Cffu, Cffu)
     * @see #cffuAllOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> cffuCombine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4, CompletableFuture<T5> cf5) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");
        requireNonNull(cf4, "cf4 is null");
        requireNonNull(cf5, "cf5 is null");

        final Object[] result = new Object[5];

        CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> ret = CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2),
                        cf3.thenAccept(t3 -> result[2] = t3),
                        cf4.thenAccept(t4 -> result[3] = t4),
                        cf5.thenAccept(t5 -> result[4] = t5)
                )
                .thenApply(unused ->
                        Tuple5.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3], (T5) result[4])
                );
        return new0(ret);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion Methods
    //
    //    - toCompletableFutureArray:     Cffu -> CF
    //
    //    - cffuListToArray:              List<Cffu> -> Cffu[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A convenient util method for unwrap input {@link Cffu} array elements using {@link Cffu#toCompletableFuture()}.
     *
     * @see Cffu#toCompletableFuture()
     * @see #asCffuArray(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] toCompletableFutureArray(Cffu<T>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = cfs[i].toCompletableFuture();
        }
        return ret;
    }

    /**
     * A convenient util method for unwrap input {@link Cffu} array elements using {@link Cffu#cffuUnwrap()}.
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
            ret[i] = cfs[i].cffuUnwrap();
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
    public boolean forbidObtrudeMethods() {
        return forbidObtrudeMethods;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Helper fields and classes
    ////////////////////////////////////////////////////////////////////////////////

    private static final boolean USE_COMMON_POOL =
            (ForkJoinPool.getCommonPoolParallelism() > 1);

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
     * hold {@link #ASYNC_POOL} as field of static inner class for lazy loading(init only when needed).
     */
    private static class AsyncPoolHolder {
        /**
         * Default executor -- ForkJoinPool.commonPool() unless it cannot support parallelism.
         */
        private static final Executor ASYNC_POOL = USE_COMMON_POOL ?
                ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Java version check logic for compatibility
    ////////////////////////////////////////////////////////////////////////////////

    static final boolean IS_JAVA9_PLUS;

    static final boolean IS_JAVA12_PLUS;

    static final boolean IS_JAVA19_PLUS;

    static {
        boolean b;

        try {
            // `completedStage` is the new method of CompletableFuture since java 9
            CompletableFuture.completedStage(null);
            b = true;
        } catch (NoSuchMethodError e) {
            b = false;
        }
        IS_JAVA9_PLUS = b;

        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        try {
            // `exceptionallyCompose` is the new method of CompletableFuture since java 12
            cf.exceptionallyCompose(x -> cf);
            b = true;
        } catch (NoSuchMethodError e) {
            b = false;
        }
        IS_JAVA12_PLUS = b;

        try {
            // `resultNow` is the new method of CompletableFuture since java 19
            cf.resultNow();
            b = true;
        } catch (NoSuchMethodError e) {
            b = false;
        }
        IS_JAVA19_PLUS = b;
    }
}
