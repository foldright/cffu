package io.foldright.cffu;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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
public final class CffuFactory {
    final Executor defaultExecutor;

    final boolean forbidObtrudeMethods;

    CffuFactory(Executor defaultExecutor, boolean forbidObtrudeMethods) {
        this.defaultExecutor = defaultExecutor;
        this.forbidObtrudeMethods = forbidObtrudeMethods;
    }

    private <T> Cffu<T> new0(CompletableFuture<T> cf) {
        return new Cffu<>(this, cf);
    }

    /**
     * Convert {@link CompletableFuture} to {@link Cffu}.
     *
     * @see Cffu#toCompletableFuture()
     */
    public <T> Cffu<T> toCffu(CompletableFuture<T> cf) {
        return new0(cf);
    }

    /**
     * Convert {@link CompletableFuture} array to {@link Cffu} array.
     *
     * @see Cffu#toCompletableFuture()
     */
    public <T> Cffu<T>[] toCffuArray(CompletableFuture<T>... cfs) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] ret = new Cffu[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = toCffu(cfs[i]);
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <U> Cffu<U>[] cffuListToArray(List<Cffu<U>> cffuList) {
        return cffuList.toArray(new Cffu[0]);
    }

    @SuppressWarnings("unchecked")
    public static <U> CompletableFuture<U>[] completableFutureListToArray(List<CompletableFuture<U>> cfList) {
        return cfList.toArray(new CompletableFuture[0]);
    }

    @SafeVarargs
    private static <T> CompletableFuture<T>[] toCompletableFuture(Cffu<T>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = cfs[i].toCompletableFuture();
        }
        return ret;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Factory Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @see CompletableFuture#completedFuture(Object)
     */
    public <T> Cffu<T> completedFuture(T value) {
        CompletableFuture<T> cf = CompletableFuture.completedFuture(value);
        return new0(cf);
    }

    private <T> Cffu<T> dummy() {
        return completedFuture(null);
    }

    /**
     * @see CompletableFuture#failedFuture(Throwable)
     */
    public <T> Cffu<T> failedFuture(Throwable ex) {
        // FIXME CompletableFuture.failedFuture is since java 9
        //       need compatibility logic
        CompletableFuture<T> cf = CompletableFuture.failedFuture(ex);
        return new0(cf);
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
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier) {
        return dummy().thenApplyAsync(unused -> supplier.get());
    }

    /**
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return dummy().thenApplyAsync(unused -> supplier.get(), executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // allOf / anyOf methods
    //
    // similar to CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #allOf(CompletableFuture[])} with argument type {@link Cffu}.
     */
    public Cffu<Void> allOf(Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFuture((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.allOf(args));
    }

    /**
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    public Cffu<Void> allOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.allOf(cfs));
    }

    /**
     * overloaded method of {@link #anyOf(CompletableFuture[])} with argument type {@link Cffu}.
     */
    public Cffu<Object> anyOf(Cffu<?>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Object>[] args = toCompletableFuture((Cffu<Object>[]) cfs);
        return new0(CompletableFuture.anyOf(args));
    }

    /**
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    public Cffu<Object> anyOf(CompletableFuture<?>... cfs) {
        return new0(CompletableFuture.anyOf(cfs));
    }

    ////////////////////////////////////////////////////////////////////////////////
    // type-safe allOf / anyOf methods
    //
    // method name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #cffuAllOfWithResults(CompletableFuture[])} with argument type {@link Cffu}.
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> cffuAllOfWithResults(Cffu<T>... cfs) {
        return cffuAllOfWithResults(toCompletableFuture(cfs));
    }

    /**
     * same to {@link #allOf(CompletableFuture[])}, but return results of input {@link CompletableFuture}.
     */
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
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(Cffu<T>... cfs) {
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] args = new CompletableFuture[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            args[i] = cfs[i].toCompletableFuture();
        }
        return cffuAnyOf(args);
    }

    /**
     * similar to {@link #anyOf(CompletableFuture[])}, but return type {@code T} instead of {@code Object}.
     */
    @SuppressWarnings("unchecked")
    @SafeVarargs
    public final <T> Cffu<T> cffuAnyOf(CompletableFuture<T>... cfs) {
        CompletableFuture<T> ret = (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
        return new0(ret);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // of2, of3 methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * overloaded method of {@link #cffuOf2(CompletableFuture, CompletableFuture)} with argument type {@link Cffu}.
     */
    public <T1, T2> Cffu<Pair<T1, T2>> cffuOf2(Cffu<T1> cf1, Cffu<T2> cf2) {
        return cffuOf2(cf1.toCompletableFuture(), cf2.toCompletableFuture());
    }

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
     * overloaded method of {@link #cffuOf3(CompletableFuture, CompletableFuture, CompletableFuture)} with argument type {@link Cffu}.
     */
    public <T1, T2, T3> Cffu<Triple<T1, T2, T3>> cffuOf3(Cffu<T1> cf1, Cffu<T2> cf2, Cffu<T3> cf3) {
        return cffuOf3(cf1.toCompletableFuture(), cf2.toCompletableFuture(), cf3.toCompletableFuture());
    }

    @SuppressWarnings("unchecked")
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
}
