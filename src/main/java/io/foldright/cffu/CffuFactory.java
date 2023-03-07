package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.Contract;

import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.ThreadSafe;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;


/**
 * About factory methods conventions of {@link CffuFactory}:
 * <ul>
 *      <li>all methods return {@link Cffu}.
 *      <li>only provide varargs methods for multiply inputs;
 *          if you have {@code List} input, use static util methods
 *          {@link #cffuListToArray(List)} and {@link #completableFutureListToArray(List)} to convert it first.
 * </ul>
 */
@Immutable
@ThreadSafe
public final class CffuFactory {
    final Executor defaultExecutor;

    final boolean forbidObtrudeMethods;

    CffuFactory(@NonNull Executor defaultExecutor, boolean forbidObtrudeMethods) {
        this.defaultExecutor = screenExecutor(defaultExecutor);
        this.forbidObtrudeMethods = forbidObtrudeMethods;
    }

    @NonNull
    @Contract(pure = true)
    <T> Cffu<T> new0(@NonNull CompletableFuture<T> cf) {
        return new Cffu<>(this, cf);
    }

    /**
     * Convert {@link CompletionStage} to {@link Cffu}.
     * <p>
     * <b><i>NOTE:<br></i></b>
     * if input is a {@link Cffu}, re-wrapped with the config of this {@link CffuFactory} from {@link CffuFactoryBuilder}.
     *
     * @see Cffu#toCompletableFuture()
     * @see CffuFactory#toCffu(CompletableFuture[])
     */
    @NonNull
    @Contract(pure = true)
    public <T> Cffu<T> toCffu(@NonNull CompletionStage<T> cf) {
        return new0(cf.toCompletableFuture());
    }

    /**
     * Convert {@link CompletableFuture} array to {@link Cffu} array.
     *
     * @see Cffu#toCompletableFuture()
     * @see CffuFactory#toCffu(CompletionStage)
     */
    @NonNull
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] toCffu(@NonNull CompletableFuture<T>... cfs) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = new0(cfs[i]);
        }
        return ret;
    }

    /**
     * @see Cffu#toCompletableFuture()
     * @see CffuFactory#toCffu(CompletableFuture[])
     */
    @NonNull
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] toCompletableFuture(@NonNull Cffu<T>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = cfs[i].toCompletableFuture();
        }
        return ret;
    }

    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> Cffu<T>[] cffuListToArray(@NonNull List<Cffu<T>> cffuList) {
        return cffuList.toArray(new Cffu[0]);
    }

    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T>[] completableFutureListToArray(@NonNull List<CompletableFuture<T>> cfList) {
        return cfList.toArray(new CompletableFuture[0]);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, similar to CompletableFuture static methods
    //
    //  create by immediate value
    //   - completedFuture/completedStage
    //   - failedFuture/failedStage
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @see CompletableFuture#completedFuture(Object)
     */
    @NonNull
    @Contract(pure = true)
    public <T> Cffu<T> completedFuture(@Nullable T value) {
        CompletableFuture<T> cf = CompletableFuture.completedFuture(value);
        return new0(cf);
    }

    /**
     * @see CompletableFuture#completedStage(Object)
     */
    @NonNull
    @Contract(pure = true)
    public <U> CompletionStage<U> completedStage(@Nullable U value) {
        return completedFuture(value);
    }

    /**
     * @see CompletableFuture#failedFuture(Throwable)
     */
    @NonNull
    @Contract(pure = true)
    public <T> Cffu<T> failedFuture(@NonNull Throwable ex) {
        // FIXME CompletableFuture.failedFuture is since java 9
        //       need compatibility logic
        CompletableFuture<T> cf = CompletableFuture.failedFuture(ex);
        return new0(cf);
    }

    @NonNull
    @Contract(pure = true)
    public <U> CompletionStage<U> failedStage(@NonNull Throwable ex) {
        return failedFuture(ex);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, similar to CompletableFuture static methods
    //
    //  create by logic/lambda
    //   - runAsync*
    //   - supplyAsync*
    ////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Contract(pure = true)
    private <T> Cffu<T> dummy() {
        return completedFuture(null);
    }

    /**
     * @see CompletableFuture#runAsync(Runnable)
     */
    @NonNull
    public Cffu<Void> runAsync(@NonNull Runnable action) {
        return dummy().thenRunAsync(action);
    }

    /**
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    @NonNull
    public Cffu<Void> runAsync(@NonNull Runnable action, @NonNull Executor executor) {
        return dummy().thenRunAsync(action, executor);
    }

    /**
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @NonNull
    @SuppressWarnings("BoundedWildcard")
    public <T> Cffu<T> supplyAsync(@NonNull Supplier<T> supplier) {
        return dummy().thenApplyAsync(unused -> supplier.get());
    }

    /**
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @NonNull
    @SuppressWarnings("BoundedWildcard")
    public <T> Cffu<T> supplyAsync(@NonNull Supplier<T> supplier, @NonNull Executor executor) {
        return dummy().thenApplyAsync(unused -> supplier.get(), executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf / anyOf methods, similar to CompletableFuture static methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #allOf(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @NonNull
    @Contract(pure = true)
    public Cffu<Void> allOf(@NonNull Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFuture((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.allOf(args));
    }

    /**
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @NonNull
    @Contract(pure = true)
    public Cffu<Void> allOf(@NonNull CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.allOf(cfs));
    }

    /**
     * overloaded method of {@link #anyOf(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @NonNull
    @Contract(pure = true)
    public Cffu<Object> anyOf(@NonNull Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFuture((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.anyOf(args));
    }

    /**
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @NonNull
    @Contract(pure = true)
    public Cffu<Object> anyOf(@NonNull CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.anyOf(cfs));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# type-safe allOf / anyOf methods
    //  method name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #cffuAllOfWithResults(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @NonNull
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfWithResults(@NonNull Cffu<T>... cfs) {
        return cffuAllOfWithResults(toCompletableFuture(cfs));
    }

    /**
     * same to {@link #allOf(CompletableFuture[])}, but return results of input {@link CompletableFuture}.
     */
    @NonNull
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfWithResults(@NonNull CompletableFuture<T>... cfs) {
        final int size = cfs.length;
        final Object[] result = new Object[size];

        final CompletableFuture<?>[] wrappedCfs = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final int index = i;

            CompletableFuture<T> cf = cfs[index];

            CompletableFuture<Void> wrapped = cf.thenAccept(u -> result[index] = u);
            wrappedCfs[index] = wrapped;
        }

        @SuppressWarnings("unchecked")
        CompletableFuture<List<T>> ret = CompletableFuture.allOf(wrappedCfs)
                .thenApply(unused -> (List<T>) Arrays.asList(result));

        return new0(ret);
    }

    /**
     * same as {@link #cffuAnyOf(CompletableFuture[])}, but argument type is {@link Cffu}.
     */
    @NonNull
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(@NonNull Cffu<T>... cfs) {
        return cffuAnyOf(toCompletableFuture(cfs));
    }

    /**
     * similar to {@link #anyOf(CompletableFuture[])}, but return type {@code T} instead of {@code Object}.
     */
    @NonNull
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> Cffu<T> cffuAnyOf(@NonNull CompletableFuture<T>... cfs) {
        CompletableFuture<T> ret = (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
        return new0(ret);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# type-safe of2/of3 methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #cffuOf2(CompletableFuture, CompletableFuture)} with argument type {@link Cffu}.
     */
    @NonNull
    @Contract(pure = true)
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(@NonNull Cffu<T1> cf1, @NonNull Cffu<T2> cf2) {
        return cffuOf2(cf1.toCompletableFuture(), cf2.toCompletableFuture());
    }

    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(@NonNull CompletableFuture<T1> cf1, @NonNull CompletableFuture<T2> cf2) {
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
     * overloaded method of {@link #cffuOf3(CompletableFuture, CompletableFuture, CompletableFuture)}
     * with argument type {@link Cffu}.
     */
    @NonNull
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(@NonNull Cffu<T1> cf1, Cffu<T2> cf2, @NonNull Cffu<T3> cf3) {
        return cffuOf3(cf1.toCompletableFuture(), cf2.toCompletableFuture(), cf3.toCompletableFuture());
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(@NonNull CompletableFuture<T1> cf1,
                                                         @NonNull CompletableFuture<T2> cf2,
                                                         @NonNull CompletableFuture<T3> cf3) {
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
    //#  backport codes from CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    //# delay execution, similar to CompletableFuture static methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Executor that submits a task to the given base
     * executor after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's
     * {@code execute} method.
     *
     * @param delay    how long to delay, in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the
     *                 {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     */
    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    public static Executor delayedExecutor(long delay, @NonNull TimeUnit unit, @NonNull Executor executor) {
        if (unit == null || executor == null) throw new NullPointerException();
        return new DelayedExecutor(delay, unit, executor);
    }

    /**
     * Returns a new Executor that submits a task to the default
     * executor after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's
     * {@code execute} method.
     *
     * @param delay how long to delay, in units of {@code unit}
     * @param unit  a {@code TimeUnit} determining how to interpret the
     *              {@code delay} parameter
     * @return the new delayed executor
     */
    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    public static Executor delayedExecutor(long delay, @NonNull TimeUnit unit) {
        if (unit == null) throw new NullPointerException();
        return new DelayedExecutor(delay, unit, AsyncPoolHolder.ASYNC_POOL);
    }

    ////////////////////////////////////////
    //# Executors
    ////////////////////////////////////////

    private static final boolean USE_COMMON_POOL =
            (ForkJoinPool.getCommonPoolParallelism() > 1);

    /**
     * Fallback if ForkJoinPool.commonPool() cannot support parallelism
     */
    private static final class ThreadPerTaskExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            Objects.requireNonNull(r);
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
        if (e == null) throw new NullPointerException();
        return e;
    }
}
