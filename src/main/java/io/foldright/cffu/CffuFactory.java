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
import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Supplier;


/**
 * About factory methods conventions of {@link CffuFactory}:
 * <ul>
 *   <li>all factory methods return {@link Cffu}.
 *   <li>only provide varargs methods for multiply inputs;
 *     if you have {@code List} input, use static util methods
 *     {@link #cffuListToArray(List)} and {@link #completableFutureListToArray(List)}to convert it first.
 * </ul>
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
    @Contract(pure = true)
    public <T> Cffu<T> toCffu(CompletionStage<T> cf) {
        return new0(cf.toCompletableFuture());
    }

    /**
     * Convert {@link CompletableFuture} array to {@link Cffu} array.
     *
     * @see Cffu#toCompletableFuture()
     * @see CffuFactory#toCffu(CompletionStage)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T>[] toCffu(CompletableFuture<T>... cfs) {
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
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] toCompletableFuture(Cffu<T>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = cfs[i].toCompletableFuture();
        }
        return ret;
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> Cffu<T>[] cffuListToArray(List<Cffu<T>> cffuList) {
        return cffuList.toArray(new Cffu[0]);
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T>[] completableFutureListToArray(List<CompletableFuture<T>> cfList) {
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
    @Contract(pure = true)
    public <T> Cffu<T> completedFuture(@Nullable T value) {
        CompletableFuture<T> cf = CompletableFuture.completedFuture(value);
        return new0(cf);
    }

    /**
     * @see CompletableFuture#completedStage(Object)
     */
    @Contract(pure = true)
    public <U> CompletionStage<U> completedStage(@Nullable U value) {
        return completedFuture(value);
    }

    /**
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

    @Contract(pure = true)
    public <U> CompletionStage<U> failedStage(Throwable ex) {
        return failedFuture(ex);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, similar to CompletableFuture static methods
    //
    //  create by logic/lambda
    //   - runAsync*
    //   - supplyAsync*
    ////////////////////////////////////////////////////////////////////////////////

    @Contract(pure = true)
    private <T> Cffu<T> dummy() {
        return completedFuture(null);
    }

    /**
     * @see CompletableFuture#runAsync(Runnable)
     */
    public Cffu<Void> runAsync(Runnable action) {
        return dummy().thenRunAsync(action);
    }

    /**
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public Cffu<Void> runAsync(Runnable action, Executor executor) {
        return dummy().thenRunAsync(action, executor);
    }

    /**
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SuppressWarnings("BoundedWildcard")
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier) {
        return dummy().thenApplyAsync(unused -> supplier.get());
    }

    /**
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
     * overloaded method of {@link #allOf(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @Contract(pure = true)
    public Cffu<Void> allOf(Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFuture((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.allOf(args));
    }

    /**
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Void> allOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.allOf(cfs));
    }

    /**
     * overloaded method of {@link #anyOf(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @Contract(pure = true)
    public Cffu<Object> anyOf(Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFuture((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.anyOf(args));
    }

    /**
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    public Cffu<Object> anyOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.anyOf(cfs));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# type-safe allOf / anyOf methods
    //  method name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #cffuAllOfWithResults(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfWithResults(Cffu<T>... cfs) {
        return cffuAllOfWithResults(toCompletableFuture(cfs));
    }

    /**
     * same to {@link #allOf(CompletableFuture[])}, but return results of input {@link CompletableFuture}.
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfWithResults(CompletableFuture<T>... cfs) {
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
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(Cffu<T>... cfs) {
        return cffuAnyOf(toCompletableFuture(cfs));
    }

    /**
     * similar to {@link #anyOf(CompletableFuture[])}, but return type {@code T} instead of {@code Object}.
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public final <T> Cffu<T> cffuAnyOf(CompletableFuture<T>... cfs) {
        CompletableFuture<T> ret = (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
        return new0(ret);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# type-safe of2/of3 methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #cffuOf2(CompletableFuture, CompletableFuture)}
     * with argument type {@link Cffu}.
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(Cffu<T1> cf1, Cffu<T2> cf2) {
        return cffuOf2(cf1.toCompletableFuture(), cf2.toCompletableFuture());
    }

    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
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
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(Cffu<T1> cf1, Cffu<T2> cf2, Cffu<T3> cf3) {
        return cffuOf3(cf1.toCompletableFuture(), cf2.toCompletableFuture(), cf3.toCompletableFuture());
    }

    @SuppressWarnings("unchecked")
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
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
    ////////////////////////////////////////////////////////////////////////////////

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
    @Contract(pure = true)
    public Executor delayedExecutor(long delay, TimeUnit unit) {
        return delayedExecutor(delay, unit, defaultExecutor);
    }

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
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    public Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.delayedExecutor(delay, unit, executor);
        }

        if (unit == null || executor == null) throw new NullPointerException();
        return new DelayedExecutor(delay, unit, executor);
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
    @SuppressWarnings({"resource", "ConstantValue"})
    private static Executor screenExecutor(Executor e) {
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool())
            return AsyncPoolHolder.ASYNC_POOL;
        if (e == null) throw new NullPointerException();
        return e;
    }

    ////////////////////////////////////////
    //# version checker for compatibility logic
    ////////////////////////////////////////

    static final boolean IS_JAVA9_PLUS;

    static final boolean IS_JAVA12_PLUS;

    static {
        boolean b;
        try {
            CompletableFuture.completedStage(null);
            b = true;
        } catch (NoSuchMethodError e) {
            b = false;
        }
        IS_JAVA9_PLUS = b;

        try {
            CompletableFuture.completedFuture(42)
                    .exceptionallyCompose(e -> CompletableFuture.completedFuture(42));
            b = true;
        } catch (NoSuchMethodError e) {
            b = false;
        }
        IS_JAVA12_PLUS = b;
    }
}
