package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;

import static io.foldright.cffu.Delayer.atCfDelayerThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * This class contains the new enhanced and backport methods for {@link CompletableFuture}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public final class CompletableFutureUtils {
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Factory Methods(including static methods of CF)
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Multi-Actions(M*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #mSupplyAsync(Supplier[])} except for the fast-fail behavior.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFastFailAsync(Supplier<? extends T>... suppliers) {
        return mSupplyFastFailAsync(AsyncPoolHolder.ASYNC_POOL, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #mSupplyAsync(Executor, Supplier[])} except for the fast-fail behavior.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>       the suppliers' return type
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyFastFailAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return allResultsOfFastFail(wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param suppliers         the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>               the suppliers' return type
     * @return the new CompletableFuture
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Supplier<? extends T>... suppliers) {
        return mSupplyMostSuccessAsync(valueIfNotSuccess, AsyncPoolHolder.ASYNC_POOL, timeout, unit, suppliers);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param executor          the executor to use for asynchronous execution
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param suppliers         the suppliers returning the value to be used to complete the returned CompletableFuture
     * @param <T>               the suppliers' return type
     * @return the new CompletableFuture
     * @see #mostResultsOfSuccess(Object, Executor, long, TimeUnit, CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T>... suppliers) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier", suppliers);

        return mostResultsOfSuccess(valueIfNotSuccess, executor, timeout, unit, wrapSuppliers(executor, suppliers));
    }


//     /**
//      * Returns a new CompletableFuture that is asynchronously completed
//      * by tasks running in the given Executor with the values obtained by calling the given Suppliers
//      * in the <strong>same order</strong> of the given Suppliers arguments.
//      * <p>
//      * This method is the same as {@link #tupleMSupplyAsync(Supplier, Supplier)} except for the most-success behavior.
//      *
//      * @return the new CompletableFuture
//      */
//     public static <T1,T2> CompletableFuture<Tuple2<T1,T2>> tupleMSupplyMostSuccessAsync(
//             long timeout, TimeUnit unit, Supplier<? extends T1>  supplier1, Supplier<? extends T2>  supplier2) {
//         return tupleMSupplyMostSuccessAsync(AsyncPoolHolder.ASYNC_POOL, timeout, unit, supplier1,supplier2);
//     }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier)} except for the most-success behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T1,T2> CompletableFuture<Tuple2<T1,T2>> tupleMSupplyMostSuccessAsync(
             Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1>  supplier1, Supplier<? extends T2>  supplier2) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier1", supplier1);
        requireArrayAndEleNonNull("supplier2", supplier2);
        CompletionStage<? extends T1> cf1 = CompletableFuture.supplyAsync(supplier1, executor);
        CompletionStage<? extends T2> cf2 = CompletableFuture.supplyAsync(supplier2, executor);
        return mostTupleOfSuccess(executor,timeout, unit, cf1,cf2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T,U1,U2> CompletableFuture<Tuple2<U1,U2>> tupleMApplyMostSuccessAsync(
            CompletionStage<? extends T> cf, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return tupleMApplyMostSuccessAsync(AsyncPoolHolder.ASYNC_POOL,cf,timeout, unit, fn1,fn2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T,U1,U2> CompletableFuture<Tuple2<U1,U2>> tupleMApplyMostSuccessAsync(
            Executor executor,CompletionStage<? extends T> cf, long timeout, TimeUnit unit,Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn1", fn1);
        requireArrayAndEleNonNull("fn2", fn2);

        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        return mostTupleOfSuccess(executor,timeout, unit, completableFuture1,completableFuture2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T,U1,U2,U3> CompletableFuture<Tuple3<U1,U2,U3>> tupleMApplyMostSuccessAsync(
            CompletionStage<? extends T> cf, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return tupleMApplyMostSuccessAsync(AsyncPoolHolder.ASYNC_POOL,cf,timeout, unit, fn1,fn2,fn3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T,U1,U2,U3> CompletableFuture<Tuple3<U1,U2,U3>> tupleMApplyMostSuccessAsync(
            Executor executor,CompletionStage<? extends T> cf, long timeout, TimeUnit unit,Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn1", fn1);
        requireArrayAndEleNonNull("fn2", fn2);
        requireArrayAndEleNonNull("fn3", fn3);

        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        return mostTupleOfSuccess(executor,timeout, unit, completableFuture1,completableFuture2,completableFuture3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T,U1,U2,U3,U4> CompletableFuture<Tuple4<U1,U2,U3,U4>> tupleMApplyMostSuccessAsync(
            CompletionStage<? extends T> cf, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return tupleMApplyMostSuccessAsync(AsyncPoolHolder.ASYNC_POOL,cf,timeout, unit, fn1,fn2,fn3,fn4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T,U1,U2,U3,U4> CompletableFuture<Tuple4<U1,U2,U3,U4>> tupleMApplyMostSuccessAsync(
            Executor executor,CompletionStage<? extends T> cf, long timeout, TimeUnit unit,Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn1", fn1);
        requireArrayAndEleNonNull("fn2", fn2);
        requireArrayAndEleNonNull("fn3", fn3);
        requireArrayAndEleNonNull("fn4", fn4);

        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletableFuture<U4>  completableFuture4 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn4.apply(v), executor));
        return mostTupleOfSuccess(executor,timeout, unit, completableFuture1,completableFuture2,completableFuture3,completableFuture4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T,U1,U2,U3,U4,U5> CompletableFuture<Tuple5<U1,U2,U3,U4,U5>> tupleMApplyMostSuccessAsync(
            CompletionStage<? extends T> cf, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return tupleMApplyMostSuccessAsync(AsyncPoolHolder.ASYNC_POOL,cf,timeout, unit, fn1,fn2,fn3,fn4,fn5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Functions
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T,U1,U2,U3,U4,U5> CompletableFuture<Tuple5<U1,U2,U3,U4,U5>> tupleMApplyMostSuccessAsync(
            Executor executor,CompletionStage<? extends T> cf, long timeout, TimeUnit unit,Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn1", fn1);
        requireArrayAndEleNonNull("fn2", fn2);
        requireArrayAndEleNonNull("fn3", fn3);
        requireArrayAndEleNonNull("fn4", fn4);
        requireArrayAndEleNonNull("fn5", fn5);

        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletableFuture<U4>  completableFuture4 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn4.apply(v), executor));
        CompletableFuture<U5>  completableFuture5 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn5.apply(v), executor));
        return mostTupleOfSuccess(executor,timeout, unit, completableFuture1,completableFuture2,completableFuture3,completableFuture4,completableFuture5);
    }


    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier,Supplier)} except for the most-success behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T1,T2,T3> CompletableFuture<Tuple3<T1,T2,T3>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1>  supplier1, Supplier<? extends T2>  supplier2, Supplier<? extends T3>  supplier3) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier1", supplier1);
        requireArrayAndEleNonNull("supplier2", supplier2);
        requireArrayAndEleNonNull("supplier3", supplier3);
        CompletionStage<? extends T1> cf1 = CompletableFuture.supplyAsync(supplier1, executor);
        CompletionStage<? extends T2> cf2 = CompletableFuture.supplyAsync(supplier2, executor);
        CompletionStage<? extends T3> cf3 = CompletableFuture.supplyAsync(supplier3, executor);
        return mostTupleOfSuccess(executor,timeout, unit, cf1,cf2,cf3);
    }


    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier,Supplier,Supplier)} except for the most-success behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T1,T2,T3,T4> CompletableFuture<Tuple4<T1,T2,T3,T4>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1>  supplier1, Supplier<? extends T2>  supplier2, Supplier<? extends T3>  supplier3, Supplier<? extends T4>  supplier4) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier1", supplier1);
        requireArrayAndEleNonNull("supplier2", supplier2);
        requireArrayAndEleNonNull("supplier3", supplier3);
        requireArrayAndEleNonNull("supplier4", supplier4);
        CompletionStage<? extends T1> cf1 = CompletableFuture.supplyAsync(supplier1, executor);
        CompletionStage<? extends T2> cf2 = CompletableFuture.supplyAsync(supplier2, executor);
        CompletionStage<? extends T3> cf3 = CompletableFuture.supplyAsync(supplier3, executor);
        CompletionStage<? extends T4> cf4 = CompletableFuture.supplyAsync(supplier4, executor);
        return mostTupleOfSuccess(executor,timeout, unit, cf1,cf2,cf3,cf4);
    }


    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier,Supplier,Supplier,Supplier)} except for the most-success behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T1,T2,T3,T4,T5> CompletableFuture<Tuple5<T1,T2,T3,T4,T5>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1>  supplier1, Supplier<? extends T2>  supplier2, Supplier<? extends T3>  supplier3, Supplier<? extends T4>  supplier4, Supplier<? extends T5>  supplier5) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("supplier1", supplier1);
        requireArrayAndEleNonNull("supplier2", supplier2);
        requireArrayAndEleNonNull("supplier3", supplier3);
        requireArrayAndEleNonNull("supplier4", supplier4);
        requireArrayAndEleNonNull("supplier5", supplier5);
        CompletionStage<? extends T1> cf1 = CompletableFuture.supplyAsync(supplier1, executor);
        CompletionStage<? extends T2> cf2 = CompletableFuture.supplyAsync(supplier2, executor);
        CompletionStage<? extends T3> cf3 = CompletableFuture.supplyAsync(supplier3, executor);
        CompletionStage<? extends T4> cf4 = CompletableFuture.supplyAsync(supplier4, executor);
        CompletionStage<? extends T5> cf5 = CompletableFuture.supplyAsync(supplier5, executor);
        return mostTupleOfSuccess(executor,timeout, unit, cf1,cf2,cf3,cf4,cf5);
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
        return mSupplyAsync(AsyncPoolHolder.ASYNC_POOL, suppliers);
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

        return allResultsOf(wrapSuppliers(executor, suppliers));
    }

    @SafeVarargs
    private static <T> T[] requireArrayAndEleNonNull(String varName, T... array) {
        requireNonNull(array, varName + "s is null");
        for (int i = 0; i < array.length; i++) {
            requireNonNull(array[i], varName + (i + 1) + " is null");
        }
        return array;
    }

    private static <T> CompletableFuture<? extends T>[] wrapSuppliers(
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
     * @see #allOf(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable)
     */
    public static CompletableFuture<Void> mRunAsync(Runnable... actions) {
        return mRunAsync(AsyncPoolHolder.ASYNC_POOL, actions);
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

        return CompletableFuture.allOf(wrapRunnables(executor, actions));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * after runs the given actions.
     * <p>
     * This method is the same as {@link #mRunAsync(Runnable...)} except for the fast-fail behavior.
     *
     * @param actions the actions to run before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #allOfFastFail(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable)
     */
    public static CompletableFuture<Void> mRunFastFailAsync(Runnable... actions) {
        return mRunFastFailAsync(AsyncPoolHolder.ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor after runs the given actions.
     * <p>
     * This method is the same as {@link #mRunAsync(Executor, Runnable...)} except for the fast-fail behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see #allOfFastFail(CompletionStage[])
     * @see CompletableFuture#runAsync(Runnable, Executor)
     */
    public static CompletableFuture<Void> mRunFastFailAsync(Executor executor, Runnable... actions) {
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return allOfFastFail(wrapRunnables(executor, actions));
    }

    private static CompletableFuture<Void>[] wrapRunnables(Executor executor, Runnable[] actions) {
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
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Supplier, Supplier)} except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyFastFailAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier)} except for the fast-fail behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return allTupleOf0(true, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Supplier, Supplier, Supplier)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyFastFailAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier, Supplier)}
     * except for the fast-fail behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyFastFailAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return allTupleOf0(true, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Supplier, Supplier, Supplier, Supplier)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyFastFailAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Executor, Supplier, Supplier, Supplier, Supplier)}
     * except for the fast-fail behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return allTupleOf0(true, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Supplier, Supplier, Supplier, Supplier, Supplier)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyFastFailAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * This method is the same as {@link #tupleMSupplyAsync(Supplier, Supplier, Supplier, Supplier, Supplier)}
     * except for the fast-fail behavior.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier, Executor)
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return allTupleOf0(true, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return allTupleOf0(false, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> tupleMSupplyAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return allTupleOf0(false, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return allTupleOf0(false, wrapSuppliers(executor, suppliers));
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the CompletableFuture's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyAsync(AsyncPoolHolder.ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new CompletableFuture that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new CompletableFuture
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return allTupleOf0(false, wrapSuppliers(executor, suppliers));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostResultsOfSuccess)
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
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOfFastFail(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int len = cfs.length;
        if (len == 0) return completedFuture(arrayList());
        // Defensive copy input cf to non-minimal-stage instance(toNonMinCfCopy) for SINGLE input
        // in order to ensure that the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCfCopy(cfs[0]).thenApply(CompletableFutureUtils::arrayList);

        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[len + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[len] = allResultsOf(successOrBeIncomplete);

        CompletableFuture<Object> ret = CompletableFuture.anyOf(failedOrBeIncomplete);
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param cfs               the stages
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostResultsOfSuccess(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return mostResultsOfSuccess(valueIfNotSuccess, AsyncPoolHolder.ASYNC_POOL, timeout, unit, cfs);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
     *
     * @param valueIfNotSuccess   the value to return if not completed successfully
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param cfs                 the stages
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostResultsOfSuccess(
            @Nullable T valueIfNotSuccess, Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T>... cfs) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        requireNonNull(cfs, "cfs is null");

        if (cfs.length == 0) return completedFuture(arrayList());
        if (cfs.length == 1) {
            // Defensive copy input cf to non-minimal-stage instance in order to
            // 1. avoid writing it by `cffuCompleteOnTimeout` and is able to read its result(`getSuccessNow`)
            // 2. ensure that the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
            final CompletableFuture<T> f = toNonMinCfCopy(requireNonNull(cfs[0], "cf1 is null"));
            return cffuCompleteOnTimeout(f, valueIfNotSuccess, executorWhenTimeout, timeout, unit)
                    .handle((unused, ex) -> arrayList(getSuccessNow(f, valueIfNotSuccess)));
        }

        // MUST be non-minimal-stage CF instances in order to read results(`getSuccessNow`),
        // otherwise UnsupportedOperationException
        final CompletableFuture<T>[] cfArray = toNonMinCfArray(cfs);
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, executorWhenTimeout, timeout, unit)
                .handle((unused, ex) -> arrayList(MGetSuccessNow0(valueIfNotSuccess, cfArray)));
    }

    /**
     * Multi-Gets(MGet) the results in the <strong>same order</strong> of the given cfs arguments,
     * use the result value if the given stage is completed successfully, else use the given valueIfNotSuccess
     *
     * @param cfs MUST be *Non-Minimal* CF instances in order to read results(`getSuccessNow`),
     *            otherwise UnsupportedOperationException
     */
    @SuppressWarnings("unchecked")
    private static <T> T[] MGetSuccessNow0(@Nullable Object valueIfNotSuccess, CompletableFuture<?>... cfs) {
        Object[] ret = new Object[cfs.length];
        for (int i = 0; i < cfs.length; i++) {
            ret[i] = getSuccessNow(cfs[i], valueIfNotSuccess);
        }
        return (T[]) ret;
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
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int len = cfs.length;
        if (len == 0) return completedFuture(arrayList());
        // Defensive copy input cf to non-minimal-stage instance(toNonMinCfCopy) for SINGLE input
        // in order to ensure that the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCfCopy(cfs[0]).thenApply(CompletableFutureUtils::arrayList);

        final Object[] result = new Object[len];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(cfs, result);

        CompletableFuture<List<Object>> ret = CompletableFuture.allOf(resultSetterCfs)
                .thenApply(unused -> arrayList(result));
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected in the returned
     * CompletableFuture({@code CompletableFuture<Void>}), but may be obtained by inspecting them individually.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
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
     * <li>{@link #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])}
     * <li>{@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage)} /
     *     {@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    public static CompletableFuture<Void> allOf(CompletionStage<?>... cfs) {
        requireNonNull(cfs, "cfs is null");
        if (cfs.length == 0) return completedFuture(null);
        // Defensive copy input cf to non-minimal-stage instance(toNonMinCfCopy) for SINGLE input
        // in order to ensure that the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
        if (cfs.length == 1) return toNonMinCfCopy(requireNonNull(cfs[0], "cf1 is null")).thenApply(unused -> null);
        return CompletableFuture.allOf(f_toCfArray(cfs));
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
     * <li>{@link #allResultsOfFastFail(CompletionStage[])}
     * <li>{@link #allTupleOfFastFail(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ul>
     * <p>
     * If you need the successful results of given stages in the given time, prefer below methods:
     * <ul>
     * <li>{@link #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])}
     * <li>{@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage)} /
     *     {@link #mostTupleOfSuccess(long, TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    @Contract(pure = true)
    public static CompletableFuture<Void> allOfFastFail(CompletionStage<?>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int len = cfs.length;
        if (len == 0) return completedFuture(null);
        // Defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
        // the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCfCopy(cfs[0]).thenApply(unused -> null);

        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[len + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[len] = CompletableFuture.allOf(successOrBeIncomplete);

        CompletableFuture<Object> ret = CompletableFuture.anyOf(failedOrBeIncomplete);
        return f_cast(ret);
    }

    @SafeVarargs
    private static <S extends CompletionStage<?>> S[] requireCfsAndEleNonNull(S... css) {
        return requireArrayAndEleNonNull("cf", css);
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
    private static <T> CompletableFuture<Void>[] createResultSetterCfs(CompletionStage<? extends T>[] css, T[] result) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] resultSetterCfs = new CompletableFuture[result.length];
        for (int i = 0; i < result.length; i++) {
            final int index = i;
            resultSetterCfs[index] = f_toCf(css[index]).thenAccept(v -> result[index] = v);
        }
        return resultSetterCfs;
    }

    private static <T> void fill(CompletionStage<? extends T>[] css,
                                 CompletableFuture<? extends T>[] successOrBeIncomplete,
                                 CompletableFuture<? extends T>[] failedOrBeIncomplete) {
        final CompletableFuture<T> incomplete = new CompletableFuture<>();
        for (int i = 0; i < css.length; i++) {
            final CompletableFuture<T> f = f_toCf(css[i]);
            successOrBeIncomplete[i] = f.handle((v, ex) -> ex == null ? f : incomplete).thenCompose(x -> x);
            failedOrBeIncomplete[i] = f.handle((v, ex) -> ex == null ? incomplete : f).thenCompose(x -> x);
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
     * More info see method {@link #f_toCf(CompletionStage)}.
     */
    private static <T> CompletableFuture<T>[] f_toCfArray(CompletionStage<? extends T>[] stages) {
        return toCfArray0(CompletableFutureUtils::f_toCf, stages);
    }

    /**
     * Converts {@link CompletionStage} array to {@link CompletableFuture} array.
     * More info see method {@link #toNonMinCf(CompletionStage)}.
     */
    private static <T> CompletableFuture<T>[] toNonMinCfArray(CompletionStage<? extends T>[] stages) {
        return toCfArray0(CompletableFutureUtils::toNonMinCf, stages);
    }

    private static <T> CompletableFuture<T>[] toCfArray0(
            Function<CompletionStage<? extends T>, CompletableFuture<T>> converter,
            CompletionStage<? extends T>[] stages) {
        requireNonNull(stages, "cfs is null");
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = converter.apply(requireNonNull(stages[i], "cf" + (i + 1) + " is null"));
        }
        return ret;
    }

    /**
     * Force converts CompletionStage to CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! Because reused the CF instances,
     * The returned cf may be a minimal-stage, MUST NOT be written or read(explicitly)
     * (e.g. complete(Object)}); Otherwise, the caller may trigger UnsupportedOperationException.
     */
    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> f_toCf(CompletionStage<? extends T> s) {
        if (s instanceof CompletableFuture) return (CompletableFuture<T>) s;
        else if (s instanceof Cffu) return ((Cffu<T>) s).cffuUnwrap();
        else return (CompletableFuture<T>) s.toCompletableFuture();
    }

    /**
     * Converts CompletionStage to non-minimal-stage CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instances,
     * so the returned CF instances should NOT be written(e.g. {@link CompletableFuture#complete(Object)});
     * Otherwise, the caller should defensive copy instead of writing it directly.
     */
    private static <T> CompletableFuture<T> toNonMinCf(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : f;
    }
    /**

     * Converts CompletionStage to a non-minimal-stage CompletableFuture copy. This method is type safe.
     * <p>
     * <strong>Implemetation Note:</strong> The return of {@code copy} methods
     * ({@link #copy(CompletableFuture)}/{@link CompletableFuture#copy()}) on {@code minimal-stage}
     * is still a {@code minimal-stage}.<br>
     * e.g. {@code minimalCompletionStage().copy()}, {@code completedStage().copy()}.
     */
    private static <T> CompletableFuture<T> toNonMinCfCopy(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : copy(f);
    }

    private static boolean isMinStageCf(CompletableFuture<?> cf) {
        return "java.util.concurrent.CompletableFuture$MinimalStage".equals(cf.getClass().getName());
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## anyOf* Methods
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
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anyOfSuccess(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int len = cfs.length;
        if (len == 0) return failedFuture(new NoCfsProvidedException());
        // Defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
        // 1. avoid writing the input cf unexpectedly it by caller code
        // 2. the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
        if (len == 1) return toNonMinCfCopy(cfs[0]);

        // NOTE: fill ONE MORE element of successOrBeIncompleteCfs LATER
        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[len + 1];
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[len];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE
        //       a cf that is failed when all given cfs fail, otherwise be incomplete
        successOrBeIncomplete[len] = CompletableFuture.allOf(failedOrBeIncomplete);

        CompletableFuture<Object> ret = CompletableFuture.anyOf(successOrBeIncomplete);
        return f_cast(ret);
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
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfSuccess(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anyOf(CompletionStage<? extends T>... cfs) {
        requireNonNull(cfs, "cfs is null");
        if (cfs.length == 0) return new CompletableFuture<>();
        // Defensive copy input cf to non-minimal-stage instance for SINGLE input in order to ensure that
        // 1. avoid writing the input cf unexpectedly it by caller code
        // 2. the returned cf is not minimal-stage CF instance(UnsupportedOperationException)
        if (cfs.length == 1) return toNonMinCfCopy(requireNonNull(cfs[0], "cf1 is null"));
        CompletableFuture<Object> ret = CompletableFuture.anyOf(f_toCfArray(cfs));
        return f_cast(ret);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostTupleOfSuccess Methods
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is successful when the given two stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given two stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given two stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given two stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given three stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given three stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given three stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given three stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given four stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given four stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given four stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given four stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given five stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given five stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given five stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given five stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    private static <T> CompletableFuture<T> allTupleOf0(boolean fastFail, CompletionStage<?>[] css) {
        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        final CompletableFuture<Void> resultSetter;
        if (fastFail) resultSetter = allOfFastFail(resultSetterCfs);
        else resultSetter = CompletableFuture.allOf(resultSetterCfs);

        return resultSetter.thenApply(unused -> tupleOf0(result));
    }

    @SuppressWarnings("unchecked")
    private static <T> T tupleOf0(Object... elements) {
        final int len = elements.length;
        final Object ret;
        if (len == 2) ret = Tuple2.of(elements[0], elements[1]);
        else if (len == 3) ret = Tuple3.of(elements[0], elements[1], elements[2]);
        else if (len == 4) ret = Tuple4.of(elements[0], elements[1], elements[2], elements[3]);
        else ret = Tuple5.of(elements[0], elements[1], elements[2], elements[3], elements[4]);
        return (T) ret;
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given two stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given two stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostTupleOfSuccess(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return mostTupleOfSuccess(AsyncPoolHolder.ASYNC_POOL, timeout, unit, cf1, cf2);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given two stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given two stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostTupleOfSuccess(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return mostTupleOfSuccess0(executorWhenTimeout, timeout, unit, cf1, cf2);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given three stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given three stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostTupleOfSuccess(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return mostTupleOfSuccess(AsyncPoolHolder.ASYNC_POOL, timeout, unit, cf1, cf2, cf3);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given three stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given three stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostTupleOfSuccess(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return mostTupleOfSuccess0(executorWhenTimeout, timeout, unit, cf1, cf2, cf3);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given four stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given four stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostTupleOfSuccess(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return mostTupleOfSuccess(AsyncPoolHolder.ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given four stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given four stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostTupleOfSuccess(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return mostTupleOfSuccess0(executorWhenTimeout, timeout, unit, cf1, cf2, cf3, cf4);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given five stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given five stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostTupleOfSuccess(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return mostTupleOfSuccess(AsyncPoolHolder.ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4, cf5);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given five stages arguments in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new CompletableFuture that is completed when the given five stages complete
     * @see #mostResultsOfSuccess(Object, long, TimeUnit, CompletionStage[])
     * @see #getSuccessNow(CompletableFuture, Object)
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostTupleOfSuccess(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return mostTupleOfSuccess0(executorWhenTimeout, timeout, unit, cf1, cf2, cf3, cf4, cf5);
    }

    private static <T> CompletableFuture<T> mostTupleOfSuccess0(
            Executor executorWhenTimeout, long timeout, TimeUnit unit, CompletionStage<?>... css) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        // MUST be *Non-Minimal* CF instances in order to read results(`getSuccessNow`),
        // otherwise UnsupportedOperationException
        final CompletableFuture<Object>[] cfArray = toNonMinCfArray(css);
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, executorWhenTimeout, timeout, unit)
                .handle((unused, ex) -> tupleOf0(MGetSuccessNow0(null, cfArray)));
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
     * if run on old Java 8, just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> completedStage(@Nullable T value) {
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.completedStage(value);
        }
        return completedFuture(value);
    }

    /**
     * Returns a new CompletionStage that is already completed exceptionally with
     * the given exception and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8, just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> failedStage(Throwable ex) {
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.failedStage(ex);
        }
        return failedFuture(ex);
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
        return delayedExecutor(delay, unit, AsyncPoolHolder.ASYNC_POOL);
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
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.delayedExecutor(delay, unit, executor);
        }
        return new DelayedExecutor(delay, unit, executor);
    }

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * This class uses the {@link ForkJoinPool#commonPool()} if it supports more than one parallel thread,
     * or else an Executor using one thread per async task.<br>
     * <strong>CAUTION:</strong> This executor may be not suitable for common biz use(io intensive).
     */
    @Contract(pure = true)
    public static Executor defaultExecutor() {
        return AsyncPoolHolder.ASYNC_POOL;
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Instance Methods(including new enhanced + backport methods)
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions(thenM*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenMApplyAsync(CompletionStage, Function[])}
     * except for the fast-fail behavior.
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U>... fns) {
        return thenMApplyFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenMApplyAsync(CompletionStage, Executor, Function[])}
     * except for the fast-fail behavior.
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return toNonMinCf(cf).thenCompose(v -> allResultsOfFastFail(wrapFunctions(executor, v, fns)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
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
            CompletionStage<? extends T> cf, @Nullable U valueIfNotSuccess,
            long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        return thenMApplyMostSuccessAsync(cf, valueIfNotSuccess, AsyncPoolHolder.ASYNC_POOL, timeout, unit, fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the most values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * If the given function is successful in the given time, the return result is the completed value;
     * Otherwise the given valueIfNotSuccess.
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
            CompletionStage<? extends T> cf, @Nullable U valueIfNotSuccess,
            Executor executor, long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        requireArrayAndEleNonNull("fn", fns);

        return toNonMinCf(cf).thenCompose(v -> mostResultsOfSuccess(
                valueIfNotSuccess, executor, timeout, unit, wrapFunctions(executor, v, fns)
        ));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
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
            CompletionStage<? extends T> cf, Function<? super T, ? extends U>... fns) {
        return thenMApplyAsync(cf, AsyncPoolHolder.ASYNC_POOL, fns);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @param fns the functions to use to compute the values of the returned CompletableFuture
     * @param <U> the functions' return type
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T, U> CompletableFuture<List<U>> thenMApplyAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U>... fns) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fns);

        return toNonMinCf(cf).thenCompose(v -> allResultsOf(wrapFunctions(executor, v, fns)));
    }

    private static <T, U> CompletableFuture<U>[] wrapFunctions(
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
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the given stage's result as the argument to the given actions.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletionStage<? extends T> cf, Consumer<? super T>... actions) {
        return thenMAcceptAsync(cf, AsyncPoolHolder.ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the given stage's result as the argument to the given actions.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptAsync(
            CompletionStage<? extends T> cf, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return toNonMinCf(cf).thenCompose(v -> CompletableFuture.allOf(wrapConsumers(executor, v, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the given stage's result as the argument to the given actions.
     * <p>
     * This method is the same as {@link #thenMAcceptAsync(CompletionStage, Consumer[])}
     * except for the fast-fail behavior.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFastFailAsync(
            CompletionStage<? extends T> cf, Consumer<? super T>... actions) {
        return thenMAcceptFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the given stage's result as the argument to the given actions.
     * <p>
     * This method is the same as {@link #thenMAcceptAsync(CompletionStage, Executor, Consumer[])}
     * except for the fast-fail behavior.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SafeVarargs
    public static <T> CompletableFuture<Void> thenMAcceptFastFailAsync(
            CompletionStage<? extends T> cf, Executor executor, Consumer<? super T>... actions) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return toNonMinCf(cf).thenCompose(v -> allOfFastFail(wrapConsumers(executor, v, actions)));
    }

    private static <T> CompletableFuture<Void>[] wrapConsumers(Executor executor, T v, Consumer<? super T>[] actions) {
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
     * executes the given actions using the CompletableFuture's default asynchronous execution facility.
     * <p>
     * This method is the same as {@link #thenMRunAsync(CompletionStage, Runnable...)}
     * except for the fast-fail behavior.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable)
     * @see #allOfFastFail(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunFastFailAsync(CompletionStage<?> cf, Runnable... actions) {
        return thenMRunFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, actions);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the given Executor.
     * <p>
     * This method is the same as {@link #thenMRunAsync(CompletionStage, Executor, Runnable...)}
     * except for the fast-fail behavior.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see #allOfFastFail(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunFastFailAsync(
            CompletionStage<?> cf, Executor executor, Runnable... actions) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return toNonMinCf(cf).thenCompose(unused -> allOfFastFail(wrapRunnables(executor, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the given Executor.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable, Executor)
     * @see #allOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAsync(CompletionStage<?> cf, Executor executor, Runnable... actions) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("action", actions);

        return toNonMinCf(cf).thenCompose(unused -> CompletableFuture.allOf(wrapRunnables(executor, actions)));
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * executes the given actions using the CompletableFuture's default asynchronous execution facility.
     *
     * @param actions the actions to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#thenRunAsync(Runnable)
     * @see #allOf(CompletionStage[])
     */
    public static CompletableFuture<Void> thenMRunAsync(CompletionStage<?> cf, Runnable... actions) {
        return thenMRunAsync(cf, AsyncPoolHolder.ASYNC_POOL, actions);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Then-Tuple-Multi-Actions(thenTupleM*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenTupleMApplyFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Executor, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2};
        return allTupleOf0(true,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Function, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenTupleMApplyFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2, fn3);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Executor, Function, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2,fn3);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2,completableFuture3};
        return allTupleOf0(true,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Function, Function, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        return thenTupleMApplyFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2, fn3, fn4);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Executor, Function, Function, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2,fn3,fn4);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletableFuture<U4>  completableFuture4 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn4.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2,completableFuture3,completableFuture4};
        return allTupleOf0(true,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Function, Function, Function, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenTupleMApplyFastFailAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     * <p>
     * This method is the same as {@link #thenTupleMApplyAsync(CompletionStage, Executor, Function, Function, Function, Function, Function)}
     * except for the fast-fail behavior.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyFastFailAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2,fn3,fn4,fn5);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletableFuture<U4>  completableFuture4 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn4.apply(v), executor));
        CompletableFuture<U5>  completableFuture5 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn5.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2,completableFuture3,completableFuture4,completableFuture5};
        return allTupleOf0(true,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenTupleMApplyAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2};
        return allTupleOf0(false,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenTupleMApplyAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2, fn3);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2,fn3);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2,completableFuture3};
        return allTupleOf0(false,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        return thenTupleMApplyAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2, fn3, fn4);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2,fn3,fn4);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletableFuture<U4>  completableFuture4 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn4.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2,completableFuture3,completableFuture4};
        return allTupleOf0(false,css);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the CompletableFuture's default asynchronous execution facility,
     * with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenTupleMApplyAsync(cf, AsyncPoolHolder.ASYNC_POOL, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Returns a new CompletableFuture that, when the given stage completes normally,
     * is executed using the given Executor, with the values obtained by calling the given Functions
     * (with the given stage's result as the argument to the given functions)
     * in the <strong>same order</strong> of the given Functions arguments.
     *
     * @return the new CompletableFuture
     */
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenTupleMApplyAsync(
            CompletionStage<? extends T> cf, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executor, "executor is null");
        requireArrayAndEleNonNull("fn", fn1,fn2,fn3,fn4,fn5);
        CompletableFuture<T>  cf1= f_toCf(cf);
        CompletableFuture<U1>  completableFuture1 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn1.apply(v), executor));
        CompletableFuture<U2>  completableFuture2 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn2.apply(v), executor));
        CompletableFuture<U3>  completableFuture3 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn3.apply(v), executor));
        CompletableFuture<U4>  completableFuture4 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn4.apply(v), executor));
        CompletableFuture<U5>  completableFuture5 = cf1.thenCompose(v->CompletableFuture.supplyAsync(() -> fn5.apply(v), executor));
        CompletionStage<?>[] css = {completableFuture1,completableFuture2,completableFuture3,completableFuture4,completableFuture5};
        return allTupleOf0(false,css);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## thenBoth* Methods(binary input) with fast-fail support
    //
    //    - thenCombineFastFail*(BiFunction):    (T1, T2) -> U
    //    - thenAcceptBothFastFail*(BiConsumer): (T1, T2) -> Void
    //    - runAfterBothFastFail*(Runnable):     Void, Void -> Void
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenCombine(CompletionStage, BiFunction)}
     * except for the fast-fail behavior.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SuppressWarnings("unchecked")
    public static <T, U, V> CompletableFuture<V> thenCombineFastFail(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenApply(unused -> fn.apply((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using CompletableFuture's default asynchronous execution facility,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenCombineAsync(CompletionStage, BiFunction)}
     * except for the fast-fail behavior.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U, V> CompletableFuture<V> thenCombineFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineFastFailAsync(cf1, cf2, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenCombineAsync(CompletionStage, BiFunction, Executor)}
     * except for the fast-fail behavior.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SuppressWarnings("unchecked")
    public static <T, U, V> CompletableFuture<V> thenCombineFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs)
                .thenApplyAsync(unused -> fn.apply((T) result[0], (U) result[1]), executor);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenAcceptBoth(CompletionStage, BiConsumer)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SuppressWarnings("unchecked")
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFail(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiConsumer<? super T, ? super U> action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenRun(() -> action.accept((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using CompletableFuture's default asynchronous execution facility,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenAcceptBothAsync(CompletionStage, BiConsumer)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothFastFailAsync(cf1, cf2, action, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenAcceptBothAsync(CompletionStage, BiConsumer, Executor)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    @SuppressWarnings("unchecked")
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenRunAsync(() -> action.accept((T) result[0], (U) result[1]), executor);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally, executes the given action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterBoth(CompletionStage, Runnable)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFastFail(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return allOfFastFail(css).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using CompletableFuture's default asynchronous execution facility.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterBothAsync(CompletionStage, Runnable)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFastFailAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        return runAfterBothFastFailAsync(cf1, cf2, action, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using the supplied executor.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterBothAsync(CompletionStage, Runnable, Executor)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterBothFastFailAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return allOfFastFail(css).thenRunAsync(action, executor);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## thenEither* Methods(binary input) with either(any)-success support
    //
    //    - applyToEitherSuccess*(Function): (T, T) -> U
    //    - acceptEitherSuccess*(Consumer):  (T, T) -> Void
    //    - runAfterEitherSuccess*(Runnable):  Void, Void -> Void
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEither(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccess(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Function<? super T, ? extends U> fn) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        return anyOfSuccess(css).thenApply(fn);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using this CompletableFuture's default asynchronous execution facility,
     * with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEitherAsync(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Function<? super T, ? extends U> fn) {
        return applyToEitherSuccessAsync(cf1, cf2, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEitherAsync(CompletionStage, Function, Executor)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new CompletableFuture
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2,
            Function<? super T, ? extends U> fn, Executor executor) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return anyOfSuccess(css).thenApplyAsync(fn, executor);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEither(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccess(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Consumer<? super T> action) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return anyOfSuccess(css).thenAccept(action);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using this CompletableFuture's default asynchronous execution facility,
     * with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEitherAsync(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Consumer<? super T> action) {
        return acceptEitherSuccessAsync(cf1, cf2, action, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEitherAsync(CompletionStage, Consumer, Executor)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action   the action to perform before completing the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2,
            Consumer<? super T> action, Executor executor) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return anyOfSuccess(css).thenAcceptAsync(action, executor);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterEither(CompletionStage, Runnable)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterEitherSuccess(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return anyOfSuccess(css).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action
     * using CompletableFuture's default asynchronous execution facility.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterEitherAsync(CompletionStage, Runnable)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterEitherSuccessAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        return runAfterEitherSuccessAsync(cf1, cf2, action, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action
     * using the supplied executor.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterEitherAsync(CompletionStage, Runnable, Executor)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     */
    public static CompletableFuture<Void> runAfterEitherSuccessAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return anyOfSuccess(css).thenRunAsync(action, executor);
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Error Handling Methods of CompletionStage
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using given stage's
     * default asynchronous execution facility. Otherwise, if given stage completes normally,
     * then the returned stage also completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cf, Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(cf, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the supplied Executor. Otherwise,
     * if given stage completes normally, then the returned stage also completes normally with the same value.
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    @SuppressWarnings("unchecked")
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cf, Function<Throwable, ? extends T> fn, Executor executor) {
        requireNonNull(cf, "cf is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA12_PLUS) {
            return (C) cf.exceptionallyAsync(fn, executor);
        }
        // below code is copied from CompletionStage#exceptionallyAsync
        return (C) cf.handle((r, ex) -> (ex == null) ? cf :
                cf.<T>handleAsync((r1, ex1) -> fn.apply(ex1), executor)
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
     * Uses CompletableFuture's default asynchronous execution facility as {@code executorWhenTimeout}.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #cffuOrTimeout(CompletableFuture, Executor, long, TimeUnit)
     */
    public static <C extends CompletableFuture<?>> C cffuOrTimeout(C cf, long timeout, TimeUnit unit) {
        return cffuOrTimeout(cf, AsyncPoolHolder.ASYNC_POOL, timeout, unit);
    }

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     *
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <C extends CompletableFuture<?>> C cffuOrTimeout(
            C cf, Executor executorWhenTimeout, long timeout, TimeUnit unit) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");

        return hopExecutorIfAtCfDelayerThread(orTimeout(cf, timeout, unit), executorWhenTimeout);
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
     * Unless all subsequent actions of dependent CompletableFutures is ensured executing async
     * (aka. the dependent CompletableFutures is created by async methods), using this method and {@link CompletableFuture#orTimeout(long, TimeUnit)}
     * is one less thread switch of task execution when triggered by timeout.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuOrTimeout(CompletableFuture, long, TimeUnit)
     */
    public static <C extends CompletableFuture<?>> C orTimeout(C cf, long timeout, TimeUnit unit) {
        requireNonNull(cf, "cf is null");
        requireNonNull(unit, "unit is null");
        // NOTE: No need check minimal stage, since checked at cf.orTimeout() / cf.isDone()
        if (IS_JAVA9_PLUS) {
            cf.orTimeout(timeout, unit);
        } else {
            // below code is copied from CompletableFuture#orTimeout with small adoption
            if (!cf.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToTimoutCf(cf, timeout, unit);
                cf.whenComplete(new FutureCanceller(f));
            }
        }
        return cf;
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     * <p>
     * Uses CompletableFuture's default asynchronous execution facility as {@code executorWhenTimeout}.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, Executor, long, TimeUnit)
     */
    public static <T, C extends CompletableFuture<? super T>>
    C cffuCompleteOnTimeout(C cf, @Nullable T value, long timeout, TimeUnit unit) {
        return cffuCompleteOnTimeout(cf, value, AsyncPoolHolder.ASYNC_POOL, timeout, unit);
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     *
     * @param value               the value to use upon timeout
     * @param executorWhenTimeout the async executor when triggered by timeout
     * @param timeout             how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit                a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new CompletableFuture
     */
    public static <T, C extends CompletableFuture<? super T>>
    C cffuCompleteOnTimeout(C cf, @Nullable T value, Executor executorWhenTimeout, long timeout, TimeUnit unit) {
        requireNonNull(cf, "cf is null");
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");

        return hopExecutorIfAtCfDelayerThread(completeOnTimeout(cf, value, timeout, unit), executorWhenTimeout);
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
     * <strong>Strong recommend</strong> using the safe method {@link #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit)}
     * instead of this method and {@link CompletableFuture#completeOnTimeout(Object, long, TimeUnit)}.
     * <p>
     * Unless all subsequent actions of dependent CompletableFutures is ensured executing async
     * (aka. the dependent CompletableFutures is created by async methods), using this method and {@link CompletableFuture#completeOnTimeout(Object, long, TimeUnit)}
     * is one less thread switch of task execution when triggered by timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     * @see #cffuCompleteOnTimeout(CompletableFuture, Object, long, TimeUnit)
     */
    public static <T, C extends CompletableFuture<? super T>>
    C completeOnTimeout(C cf, @Nullable T value, long timeout, TimeUnit unit) {
        requireNonNull(cf, "cf is null");
        requireNonNull(unit, "unit is null");
        // NOTE: No need check minimal stage, since checked at cf.completeOnTimeout() / cf.isDone()
        if (IS_JAVA9_PLUS) {
            cf.completeOnTimeout(value, timeout, unit);
        } else {
            // below code is copied from CompletableFuture#completeOnTimeout with small adoption
            if (!cf.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToCompleteCf(cf, value, timeout, unit);
                cf.whenComplete(new FutureCanceller(f));
            }
        }
        return cf;
    }

    @SuppressWarnings("unchecked")
    private static <C extends CompletableFuture<?>> C hopExecutorIfAtCfDelayerThread(C cf, Executor asyncExecutor) {
        CompletableFuture<Object> ret = newIncompleteFuture(cf);

        cf.handle((v, ex) -> {
            if (!atCfDelayerThread()) completeCf(ret, v, ex);
            else delayedExecutor(0, TimeUnit.SECONDS, asyncExecutor)
                    .execute(() -> completeCf(ret, v, ex));
            // use `cf.handle` method(instead of `whenComplete`) and return null,
            // in order to prevent below `exceptionally` reporting the handled argument exception in this action
            return null;
        }).exceptionally(ex -> reportException("Exception occurred in the input cf whenComplete of hop executor:", ex));

        return (C) ret;
    }

    private static void completeCf(CompletableFuture<Object> cf, Object value, @Nullable Throwable ex) {
        try {
            if (ex == null) cf.complete(value);
            else cf.completeExceptionally(ex);
        } catch (Throwable t) {
            if (ex != null) t.addSuppressed(ex);
            reportException("Exception occurred in completeCf:", t);
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
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyCompose(C cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        requireNonNull(cf, "cf is null");
        requireNonNull(fn, "fn is null");
        if (IS_JAVA12_PLUS) {
            return (C) cf.exceptionallyCompose((Function) fn);
        }
        // below code is copied from CompletionStage.exceptionallyCompose
        return (C) cf.handle((r, ex) -> (ex == null) ? cf : fn.apply(ex)).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using given CompletableFuture's default asynchronous execution facility.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyComposeAsync(C cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(cf, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally, is composed using
     * the results of the supplied function applied to given stage's exception, using the supplied Executor.
     *
     * @param fn       the function to use to compute the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyComposeAsync(C cf, Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        requireNonNull(cf, "cf is null");
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA12_PLUS) {
            return (C) cf.exceptionallyComposeAsync((Function) fn, executor);
        }
        // below code is copied from CompletionStage.exceptionallyComposeAsync
        return (C) cf.handle((r, ex) -> (ex == null) ? cf :
                cf.handleAsync((r1, ex1) -> fn.apply(ex1), executor).thenCompose(x -> x)
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
    public static <T, C extends CompletionStage<? extends T>>
    C peek(C cf, BiConsumer<? super T, ? super Throwable> action) {
        requireNonNull(cf, "cf is null");
        requireNonNull(action, "action is null");

        cf.whenComplete(action).exceptionally(ex -> reportException("Exception occurred in the action of peek:", ex));
        return cf;
    }

    /**
     * Peeks the result by executing the given action when given stage completes,
     * executes the given action using given stage's default asynchronous execution facility,
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
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync(C cf, BiConsumer<? super T, ? super Throwable> action) {
        return peekAsync(cf, action, AsyncPoolHolder.ASYNC_POOL);
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
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync(C cf, BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        requireNonNull(cf, "cf is null");
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        cf.whenCompleteAsync(action, executor).exceptionally(ex ->
                reportException("Exception occurred in the action of peekAsync:", ex));
        return cf;
    }

    @Nullable
    @SuppressWarnings("SameReturnValue")
    private static <T> T reportException(String msg, Throwable ex) {
        StringWriter sw = new StringWriter(4096);
        PrintWriter writer = new PrintWriter(sw);

        writer.println(msg);
        ex.printStackTrace(writer);

        System.err.println(sw);
        return null;
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Read(explicitly) Methods of CompletableFuture(including Future)
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
    public static <T> T join(CompletableFuture<T> cf, long timeout, TimeUnit unit) {
        requireNonNull(cf, "cf is null");
        requireNonNull(unit, "unit is null");

        if (cf.isDone()) return cf.join();
        // defensive copy input cf to avoid writing it by `orTimeout`
        return orTimeout(copy(cf), timeout, unit).join();
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
    public static <T> T getSuccessNow(CompletableFuture<? extends T> cf, @Nullable T valueIfNotSuccess) {
        requireNonNull(cf, "cf is null");
        // NOTE: No need check minimal stage, since checked at cf.isDone()
        return cf.isDone() && !cf.isCompletedExceptionally() ? cf.join() : valueIfNotSuccess;
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
    public static <T> T resultNow(Future<T> cf) {
        requireNonNull(cf, "cf is null");
        if (IS_JAVA19_PLUS) {
            return cf.resultNow();
        }

        // below code is copied from Future.resultNow

        if (!cf.isDone()) throw new IllegalStateException("Task has not completed");
        if (cf.isCancelled()) throw new IllegalStateException("Task was cancelled");
        // simple path for CompletableFuture/Cffu
        if (cf instanceof CompletableFuture) {
            if (((CompletableFuture<?>) cf).isCompletedExceptionally())
                throw new IllegalStateException("Task completed with exception");
        } else if (cf instanceof Cffu) {
            if (((Cffu<?>) cf).isCompletedExceptionally())
                throw new IllegalStateException("Task completed with exception");
        }

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return cf.get();
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
    public static Throwable exceptionNow(Future<?> cf) {
        requireNonNull(cf, "cf is null");
        if (IS_JAVA19_PLUS) {
            return cf.exceptionNow();
        }

        // below code is copied from Future.exceptionNow

        if (!cf.isDone()) throw new IllegalStateException("Task has not completed");
        if (cf.isCancelled()) throw new IllegalStateException("Task was cancelled");

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    cf.get();
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
    public static CffuState state(Future<?> cf) {
        requireNonNull(cf, "cf is null");
        if (IS_JAVA19_PLUS) {
            return CffuState.toCffuState(cf.state());
        }

        // below code is copied from Future#state() with small adoption

        if (!cf.isDone()) return CffuState.RUNNING;
        if (cf.isCancelled()) return CffuState.CANCELLED;
        // simple path for CompletableFuture/Cffu
        if (cf instanceof CompletableFuture) {
            if (((CompletableFuture<?>) cf).isCompletedExceptionally())
                return CffuState.FAILED;
            else return CffuState.SUCCESS;
        } else if (cf instanceof Cffu) {
            if (((Cffu<?>) cf).isCompletedExceptionally())
                return CffuState.FAILED;
            else return CffuState.SUCCESS;
        }

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    cf.get();  // may throw InterruptedException when done
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
     * from an asynchronous task using the default executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @return the given CompletableFuture
     * @see CompletableFuture#completeAsync(Supplier)
     */
    public static <T, C extends CompletableFuture<? super T>> C completeAsync(C cf, Supplier<? extends T> supplier) {
        return completeAsync(cf, supplier, AsyncPoolHolder.ASYNC_POOL);
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
    public static <T, C extends CompletableFuture<? super T>>
    C completeAsync(C cf, Supplier<? extends T> supplier, Executor executor) {
        requireNonNull(cf, "cf is null");
        requireNonNull(supplier, "supplier is null");
        requireNonNull(executor, "executor is null");
        if (IS_JAVA9_PLUS) {
            cf.completeAsync(supplier, executor);
        } else {
            // NOTE: No need check minimal stage, because on Java 8(not Java 9+) NOT support minimal stage

            // below code is copied from CompletableFuture#completeAsync with small adoption
            executor.execute(new CfCompleterBySupplier<>(cf, supplier));
        }
        return cf;
    }

    /**
     * If not already completed, completes given CompletableFuture with the exception result
     * of the given Supplier function invoked from an asynchronous task using the default executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @return the given CompletableFuture
     * @see CompletableFuture#completeExceptionally(Throwable)
     */
    public static <C extends CompletableFuture<?>>
    C completeExceptionallyAsync(C cf, Supplier<? extends Throwable> supplier) {
        return completeExceptionallyAsync(cf, supplier, AsyncPoolHolder.ASYNC_POOL);
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
    public static <C extends CompletableFuture<?>>
    C completeExceptionallyAsync(C cf, Supplier<? extends Throwable> supplier, Executor executor) {
        requireNonNull(cf, "cf is null");
        requireNonNull(supplier, "supplier is null");
        requireNonNull(executor, "executor is null");
        if (isMinStageCf(cf)) throw new UnsupportedOperationException();

        executor.execute(new CfExCompleterBySupplier(cf, supplier));
        return cf;
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
     * if run on old Java 8, just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @return the new CompletionStage
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> minimalCompletionStage(CompletableFuture<T> cf) {
        requireNonNull(cf, "cf is null");
        if (IS_JAVA9_PLUS) {
            return cf.minimalCompletionStage();
        }
        return cf.thenApply(x -> x);
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
    public static <T> CompletableFuture<T> copy(CompletableFuture<T> cf) {
        requireNonNull(cf, "cf is null");
        if (IS_JAVA9_PLUS) {
            return cf.copy();
        }
        return cf.thenApply(x -> x);
    }

    /**
     * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
     *
     * @param <U> the type of the value
     * @return the new CompletableFuture
     * @see CompletableFuture#newIncompleteFuture()
     */
    @Contract(pure = true)
    public static <U> CompletableFuture<U> newIncompleteFuture(CompletableFuture<?> cf) {
        requireNonNull(cf, "cf is null");
        if (IS_JAVA9_PLUS) {
            return cf.newIncompleteFuture();
        }
        return new CompletableFuture<>();
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Conversion Methods(static methods)
    //
    //    - toCompletableFutureArray:     CompletionStage[](including Cffu) -> CF[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A convenient util method for converting input {@link CompletionStage} (including
     * {@link Cffu}/{@link CompletableFuture}) array element by {@link CompletionStage#toCompletableFuture()}.
     *
     * @see Cffu#toCompletableFuture()
     * @see CompletableFuture#toCompletableFuture()
     * @see CompletionStage#toCompletableFuture()
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
        final CompletableFuture<T>[] a = new CompletableFuture[cfList.size()];
        return cfList.toArray(a);
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
            return AsyncPoolHolder.ASYNC_POOL;
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
     * hold {@link #ASYNC_POOL} as field of static inner class for lazy loading(init only when needed).
     */
    private static class AsyncPoolHolder {
        /**
         * Default executor -- ForkJoinPool.commonPool() unless it cannot support parallelism.
         */
        private static final Executor ASYNC_POOL = _asyncPool0();

        private static Executor _asyncPool0() {
            if (IS_JAVA9_PLUS) return completedFuture(null).defaultExecutor();
            if (USE_COMMON_POOL) return ForkJoinPool.commonPool();
            return new ThreadPerTaskExecutor();
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal Java version check logic for compatibility
    ////////////////////////////////////////////////////////////////////////////////

    private static final boolean IS_JAVA9_PLUS;

    private static final boolean IS_JAVA12_PLUS;

    private static final boolean IS_JAVA19_PLUS;

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

        final CompletableFuture<Integer> cf = completedFuture(42);
        try {
            // `exceptionallyCompose` is the new method of CompletableFuture since java 12
            cf.exceptionallyCompose(v -> cf);
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

    private CompletableFutureUtils() {
    }
}
