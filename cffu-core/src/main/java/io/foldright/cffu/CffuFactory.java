package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
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
 * @author HuHao (995483610 at qq dot com)
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
        return new CffuFactoryBuilder(defaultExecutor);
    }

    /**
     * Returns a new CffuFactory from this CffuFactory that reset the defaultExecutor.
     */
    @Contract(pure = true)
    public CffuFactory resetDefaultExecutor(Executor defaultExecutor) {
        return CffuFactoryBuilder.resetDefaultExecutor(this, defaultExecutor);
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
     * In general, should not use this method in biz code, prefer other factory methods of Cffu.
     *
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
    //
    //    - Supplier<T> -> Cffu<T>
    //    - Runnable    -> Cffu<Void>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is asynchronously completed
     * by a task running in the {@link #defaultExecutor()} with
     * the value obtained by calling the given Supplier.
     *
     * @param supplier a function returning the value to be used to complete the returned Cffu
     * @param <T>      the function's return type
     * @return the new Cffu
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
     */
    public Cffu<Void> runAsync(Runnable action, Executor executor) {
        return create(CompletableFuture.runAsync(action, executor));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions(M*) Methods(create by actions)
    //
    //    - Supplier<T>[] -> Cffu<List<T>>
    //    - Runnable[]    -> Cffu<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyFastFailAsync(Supplier<? extends T>... suppliers) {
        return mSupplyFastFailAsync(defaultExecutor, suppliers);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyFastFailAsync(Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyFastFailAsync(executor, suppliers));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not failed
     * @param suppliers     the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>           the suppliers' return type
     * @return the new Cffu
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        return mSupplyAllSuccessAsync(valueIfFailed, defaultExecutor, suppliers);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not failed
     * @param executor      the executor to use for asynchronous execution
     * @param suppliers     the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>           the suppliers' return type
     * @return the new Cffu
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAllSuccessAsync(valueIfFailed, executor, suppliers));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
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
     * @param suppliers         the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>               the suppliers' return type
     * @return the new Cffu
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Supplier<? extends T>... suppliers) {
        return mSupplyMostSuccessAsync(valueIfNotSuccess, defaultExecutor, timeout, unit, suppliers);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
     * with the most values obtained by calling the given Suppliers
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
     * @param suppliers         the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>               the suppliers' return type
     * @return the new Cffu
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyMostSuccessAsync(valueIfNotSuccess, executor, timeout, unit, suppliers));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAsync(defaultExecutor, suppliers);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
     * with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAsync(executor, suppliers));
    }

    /**
     * Returns a new Cffu that is asynchronously successful
     * when any of tasks running in the Cffu's default asynchronous execution facility
     * by calling the given Suppliers success, with the same result.
     * Otherwise, all the given tasks complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause.
     * If no suppliers are provided, returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #anySuccessOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnySuccessAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnySuccessAsync(defaultExecutor, suppliers);
    }

    /**
     * Returns a new Cffu that is asynchronously successful
     * when any of tasks running in the given executor by calling the given Suppliers success, with the same result.
     * Otherwise, all the given tasks complete exceptionally,
     * the returned Cffu also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause.
     * If no suppliers are provided, returns a new Cffu that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #anySuccessOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnySuccessAsync(Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAnySuccessAsync(executor, suppliers));
    }

    /**
     * Returns a new Cffu that is completed
     * when any of tasks running in the Cffu's default asynchronous execution facility
     * by calling the given Suppliers complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     * If no suppliers are provided, returns an incomplete Cffu.
     *
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #anyOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnyAsync(defaultExecutor, suppliers);
    }

    /**
     * Returns a new Cffu that is completed
     * when any of tasks running in the given Executor by calling the given Suppliers complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     * If no suppliers are provided, returns an incomplete Cffu.
     *
     * @param executor  the executor to use for asynchronous execution
     * @param suppliers the suppliers returning the value to be used to complete the returned Cffu
     * @param <T>       the suppliers' return type
     * @return the new Cffu
     * @see #anyOf(CompletionStage[])
     */
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAnyAsync(executor, suppliers));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * after runs the given actions.
     *
     * @param actions the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #allFastFailOf(CompletionStage[])
     */
    public Cffu<Void> mRunFastFailAsync(Runnable... actions) {
        return mRunFastFailAsync(defaultExecutor, actions);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
     * after runs the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #allFastFailOf(CompletionStage[])
     */
    public Cffu<Void> mRunFastFailAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunFastFailAsync(executor, actions));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * after runs the given actions.
     *
     * @param actions the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #allOf(CompletionStage[])
     */
    public Cffu<Void> mRunAsync(Runnable... actions) {
        return mRunAsync(defaultExecutor, actions);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
     * after runs the given actions.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #allOf(CompletionStage[])
     */
    public Cffu<Void> mRunAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunAsync(executor, actions));
    }

    /**
     * Returns a new Cffu that is asynchronously successful
     * when any tasks running in the Cffu's default asynchronous execution facility success.
     *
     * @param actions the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #anySuccessOf(CompletionStage[])
     */
    public Cffu<Void> mRunAnySuccessAsync(Runnable... actions) {
        return mRunAnySuccessAsync(defaultExecutor, actions);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * when any tasks running in the Cffu's default asynchronous execution facility.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #anySuccessOf(CompletionStage[])
     */
    public Cffu<Void> mRunAnySuccessAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunAnySuccessAsync(executor, actions));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * when any tasks running in the Cffu's default asynchronous execution facility.
     *
     * @param actions the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #anyOf(CompletionStage[])
     */
    public Cffu<Void> mRunAnyAsync(Runnable... actions) {
        return mRunAnyAsync(defaultExecutor, actions);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * when any tasks running in the given executor complete.
     *
     * @param executor the executor to use for asynchronous execution
     * @param actions  the actions to run before completing the returned Cffu
     * @return the new Cffu
     * @see #anyOf(CompletionStage[])
     */
    public Cffu<Void> mRunAnyAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunAnyAsync(executor, actions));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Tuple-Multi-Actions(tupleM*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyFastFailAsync(defaultExecutor, supplier1, supplier2);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.tupleMSupplyFastFailAsync(executor, supplier1, supplier2));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyFastFailAsync(defaultExecutor, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.tupleMSupplyFastFailAsync(executor, supplier1, supplier2, supplier3));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyFastFailAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.tupleMSupplyFastFailAsync(executor, supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyFastFailAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyFastFailAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyFastFailAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.tupleMSupplyFastFailAsync(
                executor, supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new Cffu
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyAllSuccessAsync(defaultExecutor, supplier1, supplier2);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.tupleMSupplyAllSuccessAsync(executor, supplier1, supplier2));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new Cffu
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyAllSuccessAsync(defaultExecutor, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.tupleMSupplyAllSuccessAsync(executor, supplier1, supplier2, supplier3));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new Cffu
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyAllSuccessAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.tupleMSupplyAllSuccessAsync(executor, supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the successful values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @return the new Cffu
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAllSuccessAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyAllSuccessAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given executor with the successfully values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAllSuccessAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.tupleMSupplyAllSuccessAsync(
                executor, supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new Cffu
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyMostSuccessAsync(defaultExecutor, timeout, unit, supplier1, supplier2);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
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
     * @return the new Cffu
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.tupleMSupplyMostSuccessAsync(executor, timeout, unit, supplier1, supplier2));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new Cffu
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyMostSuccessAsync(defaultExecutor, timeout, unit, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
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
     * @return the new Cffu
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.tupleMSupplyMostSuccessAsync(
                executor, timeout, unit, supplier1, supplier2, supplier3));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new Cffu
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyMostSuccessAsync(defaultExecutor, timeout, unit, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
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
     * @return the new Cffu
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.tupleMSupplyMostSuccessAsync(
                executor, timeout, unit, supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the Cffu's default asynchronous execution facility
     * with the most values obtained by calling the given Suppliers
     * in the given time({@code timeout}, aka as many results as possible in the given time)
     * in the <strong>same order</strong> of the given Suppliers arguments.
     * <p>
     * If the given supplier is successful in the given time, the return result is the completed value;
     * Otherwise {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the new Cffu
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyMostSuccessAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyMostSuccessAsync(defaultExecutor, timeout, unit,
                supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by tasks running in the given Executor
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
     * @return the new Cffu
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyMostSuccessAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.tupleMSupplyMostSuccessAsync(
                executor, timeout, unit, supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return tupleMSupplyAsync(defaultExecutor, supplier1, supplier2);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2> Cffu<Tuple2<T1, T2>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.tupleMSupplyAsync(executor, supplier1, supplier2));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return tupleMSupplyAsync(defaultExecutor, supplier1, supplier2, supplier3);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.tupleMSupplyAsync(executor, supplier1, supplier2, supplier3));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return tupleMSupplyAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.tupleMSupplyAsync(executor, supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the {@link #defaultExecutor()} with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return tupleMSupplyAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Returns a new Cffu that is asynchronously completed
     * by tasks running in the given Executor with the values obtained by calling the given Suppliers
     * in the <strong>same order</strong> of the given Suppliers arguments.
     *
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see #allResultsOf(CompletionStage[])
     */
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> tupleMSupplyAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.tupleMSupplyAsync(
                executor, supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostSuccessResultsOf)
    //
    //    CompletionStage<T>[] -> Cffu<List<T>>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is successful with the results in the <strong>same order</strong>
     * of the given stages arguments when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * This method is the same as {@link #allResultsOf(CompletionStage[])} except for the fast-fail behavior.
     *
     * @param cfs the stages
     * @return a new Cffu that is successful when all the given stages success
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsFastFailOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allResultsFastFailOf(cfs));
    }

    /**
     * Returns a new Cffu that is successful with the results in the <strong>same order</strong>
     * of the given stages arguments when all the given stages completed;
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code valueIfFailed}.
     *
     * @param valueIfFailed the value to return if not completed successfully
     * @param cfs           the stages
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#successfulAsList(ListenableFuture[]) Guava method successfulAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allSuccessResultsOf(
            @Nullable T valueIfFailed, CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, cfs));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of
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
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.mostSuccessResultsOf(
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
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#allAsList(ListenableFuture[]) Guava method allAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allResultsOf(cfs));
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
     * @return a new Cffu that is successful when all the given stages success
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    public Cffu<Void> allFastFailOf(CompletionStage<?>... cfs) {
        return create(CompletableFutureUtils.allFastFailOf(cfs));
    }

    /**
     * Returns a new Cffu that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected in the returned
     * Cffu({@code Cffu<Void>}), but may be obtained by inspecting them individually.
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * Among the applications of this method is to await completion of a set of independent stages
     * before continuing a program, as in: {@code cffuFactory.allOf(c1, c2, c3).join();}.
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
     * <li>{@link #mostSuccessTupleOf(long, TimeUnit, CompletionStage, CompletionStage)} / {@link #mostSuccessTupleOf (long,
     *      TimeUnit, CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * </ul>
     *
     * @param cfs the stages
     * @return a new Cffu that is completed when all the given stages complete
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; forget to call its `join()` method?")
    @Contract(pure = true)
    public Cffu<Void> allOf(CompletionStage<?>... cfs) {
        return create(CompletableFutureUtils.allOf(cfs));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## anyOf* Methods
    //
    //    CompletionStage<T>[] -> Cffu<T>
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
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @SafeVarargs
    public final <T> Cffu<T> anySuccessOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.anySuccessOf(cfs));
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
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<T> anyOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.anyOf(cfs));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostSuccessTupleOf Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is successful when the given two stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given two stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allTupleFastFailOf(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is successful when the given three stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given three stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allTupleFastFailOf(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is successful when the given four stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given four stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allTupleFastFailOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is successful when the given five stages success.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is successful when the given five stages success
     * @see #allResultsFastFailOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleFastFailOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allTupleFastFailOf(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new Cffu that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new Cffu
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new Cffu
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new Cffu
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is successful
     * with the results of the given stages arguments when all the given stages completed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     *
     * @return a new Cffu
     * @see #allSuccessResultsOf(Object, CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of the given two stages arguments
     * in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given two stages complete
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> mostSuccessTupleOf(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of the given three stages arguments
     * in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given three stages complete
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of the given four stages arguments
     * in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given four stages complete
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu with the most results in the <strong>same order</strong> of the given five stages arguments
     * in the given time({@code timeout}, aka as many results as possible in the given time).
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the value {@code null}.
     *
     * @param timeout how long to wait in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return a new Cffu that is completed when the given five stages complete
     * @see #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Returns a new Cffu that is completed when the given two stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given two stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2));
    }

    /**
     * Returns a new Cffu that is completed when the given three stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given three stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3));
    }

    /**
     * Returns a new Cffu that is completed when the given four stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given four stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Returns a new Cffu that is completed when the given five stages complete.
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new Cffu that is completed when the given five stages complete
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4, cf5));
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
     * if run on old Java 8(not support *minimal* CompletionStage), just return a Cffu with
     * a *normal* underlying CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param value the value
     * @param <T>   the type of the value
     * @return the completed CompletionStage
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
     * if run on old Java 8(not support *minimal* CompletionStage), just return a Cffu with
     * a *normal* underlying CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> failedStage(Throwable ex) {
        return createMin((CompletableFuture<T>) CompletableFutureUtils.<T>failedStage(ex));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## CompletionStage Argument Factory Methods
    //
    //    - toCffu:      CF/CompletionStage   -> Cffu
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
     * A convenient util method for converting input {@link CompletionStage}(including {@link CompletableFuture})
     * array element by {@link #toCffu(CompletionStage)}.
     *
     * @throws NullPointerException if the stages param or any of its elements are {@code null}
     * @see #toCffu(CompletionStage)
     * @see CompletableFutureUtils#toCompletableFutureArray(CompletionStage[])
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
    // region# Conversion Methods
    //
    //    - cffuListToArray: List<Cffu> -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Convert Cffu list to Cffu array.
     *
     * @see #toCffuArray(CompletionStage[])
     * @see CompletableFutureUtils#completableFutureListToArray(List)
     */
    @Contract(pure = true)
    public static <T> Cffu<T>[] cffuListToArray(List<Cffu<T>> cffuList) {
        @SuppressWarnings("unchecked")
        Cffu<T>[] a = new Cffu[cffuList.size()];
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
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     */
    @Contract(pure = true)
    public boolean forbidObtrudeMethods() {
        return forbidObtrudeMethods;
    }
}
