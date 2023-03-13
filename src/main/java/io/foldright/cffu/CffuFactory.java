package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
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
 * The methods that equivalent to the instance methods of {@link CompletableFuture}
 * is in {@link Cffu} class.
 * <p>
 * Use {@link CffuFactoryBuilder} to config and build {@link CffuFactory}.
 * <p>
 * About factory methods conventions of {@link CffuFactory}:
 * <ul>
 *   <li>all factory methods return {@link Cffu}.
 *   <li>new factory methods, aka. no equivalent method in {@link CompletableFuture},
 *     prefix method name with `cffu`.
 *   <li>only provide varargs methods for multiply Cffu/CF input arguments;
 *     if you have {@code List} input, use static util methods {@link #cffuListToArray(List)}
 *     and {@link #completableFutureListToArray(List)} to convert it to array first.
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

    @Contract(pure = true)
    <T> Cffu<T> new0(CompletableFuture<T> cf) {
        return new Cffu<>(this, requireNonNull(cf, "cf is null"));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion Methods
    //
    //    - toCffu: CF -> Cffu
    //    - toCffuArray: CF[] -> Cffu[]
    //    - toCompletableFutureArray: Cffu -> CF
    //
    //    - cffuListToArray: List<Cffu> -> Cffu[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Convert {@link CompletionStage} to {@link Cffu}.
     * <p>
     * <b><i>NOTE:<br></i></b>
     * if input is a {@link Cffu}, re-wrapped with the config of
     * this {@link CffuFactory} from {@link CffuFactoryBuilder}.
     *
     * @see Cffu#toCompletableFuture()
     * @see #toCffuArray(CompletableFuture[])
     */
    @Contract(pure = true)
    public <T> Cffu<T> toCffu(CompletionStage<T> cf) {
        return new0(cf.toCompletableFuture());
    }

    /**
     * Convert {@link CompletableFuture} array to {@link Cffu} array.
     *
     * @see Cffu#toCompletableFuture()
     * @see #toCffu(CompletionStage)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] toCffuArray(CompletableFuture<T>... cfs) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = new0(cfs[i]);
        }
        return ret;
    }

    /**
     * Convert Cffu array to CompletableFuture array.
     *
     * @see Cffu#toCompletableFuture()
     * @see #toCffuArray(CompletableFuture[])
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
    //# Factory Methods, similar to CompletableFuture static methods
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
        CompletableFuture<T> cf = CompletableFuture.completedFuture(value);
        return new0(cf);
    }

    /**
     * Returns a new CompletionStage that is already completed with
     * the given value and supports only those methods in
     * interface {@link CompletionStage}.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed CompletionStage
     * @see CompletableFuture#completedStage(Object)
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> completedStage(@Nullable T value) {
        return completedFuture(value);
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
        final CompletableFuture<T> cf;
        if (IS_JAVA9_PLUS) {
            cf = CompletableFuture.failedFuture(ex);
        } else {
            cf = new CompletableFuture<>();
            cf.completeExceptionally(ex);
        }

        return new0(cf);
    }

    /**
     * Returns a new CompletionStage that is already completed exceptionally
     * with the given exception and supports only those methods in interface {@link CompletionStage}.
     *
     * @param ex  the exception
     * @param <U> the type of the value
     * @return the exceptionally completed CompletionStage
     * @see CompletableFuture#failedStage(Throwable)
     */
    @Contract(pure = true)
    public <U> CompletionStage<U> failedStage(Throwable ex) {
        return failedFuture(ex);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, similar to CompletableFuture static methods
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
     * an incomplete Cffu.
     */
    @Contract(pure = true)
    <T> Cffu<T> incomplete() {
        return new0(new CompletableFuture<>());
    }

    /**
     * Returns a new Cffu that is asynchronously completed by a task running
     * in the {@link  CffuFactory#getDefaultExecutor()} after it runs the given action.
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
     * by a task running in the {@link  CffuFactory#getDefaultExecutor()} with
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
    //# allOf / anyOf methods, similar to CompletableFuture static methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when all of the given Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given Cffus are not reflected in
     * the returned Cffu, but may be obtained by inspecting them individually.
     * <p>
     * If no Cffus are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * Among the applications of this method is to await completion of a set of
     * independent Cffus before continuing a program,
     * as in: {@code Cffu.allOf(c1, c2, c3).join();}.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed when all of the given Cffus complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
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
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed when all of the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOf(Cffu[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.allOf(cfs));
    }

    /**
     * Provided this overloaded method just for resolving "allOf is ambiguous" problem
     * when call {@code allOf} with empty arguments: {@code cffuFactory.allOf()}.
     */
    @Contract(pure = true)
    public Cffu<Void> allOf() {
        return dummy();
    }

    /**
     * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * If no Cffus are provided, returns an incomplete Cffu.
     *
     * @param cfs the Cffus
     * @return a new Cffu that is completed with the result or exception of
     * any of the given Cffus when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
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
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed with the result or exception of
     * any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
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
     */
    @Contract(pure = true)
    public Cffu<Object> anyOf() {
        return incomplete();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# new type-safe allOf / anyOf factory methods
    //    method name prefix with `cffu`
    //
    //    - cffuAllOfWithResults
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
    public final <T> Cffu<List<T>> cffuAllOfWithResults(Cffu<T>... cfs) {
        return cffuAllOfWithResults(toCompletableFutureArray(cfs));
    }

    /**
     * Same as {@link #cffuAllOfWithResults(Cffu[])} with overloaded argument type {@link CompletableFuture}.
     *
     * @param cfs the CompletableFutures
     * @return a new Cffu that is completed when all of the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #cffuAllOfWithResults(Cffu[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfWithResults(CompletableFuture<T>... cfs) {
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
     * Provided this overloaded method just for resolving "cffuAllOfWithResults is ambiguous" problem
     * when call {@code cffuAllOfWithResults} with empty arguments: {@code cffuFactory.cffuAllOfWithResults()}.
     */
    @Contract(pure = true)
    public <T> Cffu<List<T>> cffuAllOfWithResults() {
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
     * Same as {@link #cffuAllOfWithResults(Cffu[])} with overloaded argument type {@link CompletableFuture}.
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
        CompletableFuture<T> ret = (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
        return new0(ret);
    }

    /**
     * Provided this overloaded method just for resolving "cffuAnyOf is ambiguous" problem
     * when call {@code cffuAnyOf} with empty arguments: {@code cffuFactory.cffuAnyOf()}.
     */
    @Contract(pure = true)
    public <T> Cffu<T> cffuAnyOf() {
        return incomplete();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# new type-safe of2/of3 factory methods
    //    method name prefix with `cffu`
    //
    //    - cffuOf2
    //    - cffuOf3
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed when the given two Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuAllOfWithResults(Cffu[])} but with two inputs
     * and return results as {@code Pair}.
     *
     * @return a new Cffu that is completed when the given two Cffus complete
     * @throws NullPointerException if any input Cffus are {@code null}
     * @see #cffuAllOfWithResults(Cffu[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(Cffu<T1> cf1, Cffu<T2> cf2) {
        return cffuOf2(cf1.toCompletableFuture(), cf2.toCompletableFuture());
    }

    /**
     * Returns a new Cffu that is completed when the given two CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuOf2(Cffu, Cffu)} with overloaded argument type {@link CompletableFuture}.
     *
     * @return a new Cffu that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see #cffuOf2(Cffu, Cffu)
     * @see #cffuAllOfWithResults(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");

        final Object[] result = new Object[2];

        CompletableFuture<Pair<T1, T2>> ret = CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2)
                )
                .thenApply(unused ->
                        Pair.of((T1) result[0], (T2) result[1])
                );
        return new0(ret);
    }

    /**
     * Returns a new Cffu that is completed when the given three Cffus complete.
     * If any of the given Cffu complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuAllOfWithResults(Cffu[])} but with three inputs
     * and return results as {@code Triple}.
     *
     * @return a new Cffu that is completed when the given three Cffus complete
     * @throws NullPointerException if any input Cffus are {@code null}
     * @see #cffuAllOfWithResults(Cffu[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(Cffu<T1> cf1, Cffu<T2> cf2, Cffu<T3> cf3) {
        return cffuOf3(cf1.toCompletableFuture(), cf2.toCompletableFuture(), cf3.toCompletableFuture());
    }

    /**
     * Returns a new Cffu that is completed when the given three CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * Cffu also does so, with a CompletionException holding this exception as its cause.
     * <p>
     * Same as {@link #cffuOf3(Cffu, Cffu, Cffu)} with overloaded argument type {@link CompletableFuture}.
     *
     * @return a new Cffu that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see #cffuOf3(Cffu, Cffu, Cffu)
     * @see #cffuAllOfWithResults(CompletableFuture[])
     */
    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");

        final Object[] result = new Object[3];

        CompletableFuture<Triple<T1, T2, T3>> ret = CompletableFuture.allOf(
                        cf1.thenAccept(t1 -> result[0] = t1),
                        cf2.thenAccept(t2 -> result[1] = t2),
                        cf3.thenAccept(t3 -> result[2] = t3)
                )
                .thenApply(unused ->
                        Triple.of((T1) result[0], (T2) result[1], (T3) result[2])
                );
        return new0(ret);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# delay execution, similar to CompletableFuture static methods
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
    // getter/setter
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @see CffuFactoryBuilder#newCffuFactoryBuilder(Executor)
     * @see Cffu#defaultExecutor()
     */
    public Executor getDefaultExecutor() {
        return defaultExecutor;
    }

    /**
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     * @see Cffu#obtrudeValue(Object)
     * @see Cffu#obtrudeException(Throwable)
     */
    public boolean isForbidObtrudeMethods() {
        return forbidObtrudeMethods;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Executors
    ////////////////////////////////////////////////////////////////////////////////

    private static final boolean USE_COMMON_POOL =
            (ForkJoinPool.getCommonPoolParallelism() > 1);

    /**
     * Fallback if ForkJoinPool.commonPool() cannot support parallelism
     */
    private static final class ThreadPerTaskExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            requireNonNull(r);
            new Thread(r).start();
        }
    }

    /**
     * hold {@link #ASYNC_POOL} as static field to lazy load.
     */
    private static class AsyncPoolHolder {
        /**
         * Default executor -- ForkJoinPool.commonPool()
         * unless it cannot support parallelism.
         */
        private static final Executor ASYNC_POOL = USE_COMMON_POOL ?
                ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();
    }

    /**
     * Null-checks user executor argument, and translates uses of
     * commonPool to ASYNC_POOL in case parallelism disabled.
     */
    @SuppressWarnings("resource")
    private static Executor screenExecutor(Executor e) {
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool())
            return AsyncPoolHolder.ASYNC_POOL;
        return requireNonNull(e, "e is null");
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# java version check logic for compatibility
    ////////////////////////////////////////////////////////////////////////////////

    static final boolean IS_JAVA9_PLUS;

    static final boolean IS_JAVA12_PLUS;

    static final boolean IS_JAVA19_PLUS;

    static {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        boolean b;

        try {
            // `completedStage` is the new method of CompletableFuture since java 9
            CompletableFuture.completedStage(null);
            b = true;
        } catch (NoSuchMethodError e) {
            b = false;
        }
        IS_JAVA9_PLUS = b;

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
