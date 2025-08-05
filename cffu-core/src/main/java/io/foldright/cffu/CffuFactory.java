package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.CffuFactoryBuilder.CffuDefaultExecutor;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Contract;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static io.foldright.cffu.CffuFactoryBuilder.cffuScreened;
import static io.foldright.cffu.CffuFactoryBuilder.cffuUnscreened;
import static java.util.Objects.requireNonNull;


/**
 * This class {@link CffuFactory} is equivalent to {@link CompletableFuture},
 * contains the static (factory) methods of {@link CompletableFuture}.
 * <p>
 * The methods that equivalent to the instance methods of {@link CompletableFuture} is in {@link Cffu} class.
 * <p>
 * Use {@link #builder(Executor)} to config and build {@link CffuFactory}.
 * <p>
 * About factory methods convention of {@link CffuFactory}:
 * <ul>
 * <li>factory methods return {@link Cffu} instead of {@link CompletableFuture}.
 * <li>only provide varargs methods for multiple Cffu/CF input arguments;
 *     if you have {@code List} input, use static utility methods {@link #cffuListToArray(List)}
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

    final CffuDefaultExecutor defaultExecutor;

    private final boolean forbidObtrudeMethods;

    CffuFactory(CffuDefaultExecutor defaultExecutor, boolean forbidObtrudeMethods) {
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
     * Returns a new CffuFactory from this CffuFactory with the defaultExecutor.
     */
    @Contract(pure = true)
    public CffuFactory withDefaultExecutor(Executor defaultExecutor) {
        return CffuFactoryBuilder.withDefaultExecutor(this, defaultExecutor);
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
     * In general, you should not use this method in application code, prefer other factory methods.
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
     * Returns a new Cffu that is asynchronously completed by a task running
     * in the {@link #defaultExecutor()} with the value obtained by calling the given Supplier.
     *
     * @param supplier a function returning the value to be used to complete the returned Cffu
     * @param <T>      the function's return type
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `runAsync`")
    public <T> Cffu<T> supplyAsync(Supplier<T> supplier, Executor executor) {
        return create(CompletableFuture.supplyAsync(supplier, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that is asynchronously completed by a task running
     * in the {@link #defaultExecutor()} after it runs the given action.
     *
     * @param action the action to run before completing the returned Cffu
     */
    public Cffu<Void> runAsync(Runnable action) {
        return runAsync(action, defaultExecutor);
    }

    /**
     * Returns a new Cffu that is asynchronously completed by a task running
     * in the given executor after it runs the given action.
     *
     * @param action   the action to run before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     */
    public Cffu<Void> runAsync(Runnable action, Executor executor) {
        return create(CompletableFuture.runAsync(action, cffuScreened(executor)));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions(M*) Methods(create by actions)
    //
    //    - Supplier<T>[] -> Cffu<List<T>>
    //    - Runnable[]    -> Cffu<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Shortcut to method {@link #allResultsFailFastOf allResultsFailFastOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyFailFastAsync(Supplier<? extends T>... suppliers) {
        return mSupplyFailFastAsync(defaultExecutor, suppliers);
    }

    /**
     * Shortcut to method {@link #allResultsFailFastOf allResultsFailFastOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyFailFastAsync(Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyFailFastAsync(cffuScreened(executor), suppliers));
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAllSuccessAsync(
            @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        return mSupplyAllSuccessAsync(defaultExecutor, valueIfFailed, suppliers);
    }

    /**
     * Shortcut to method {@link #allSuccessResultsOf allSuccessResultsOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAllSuccessAsync(
            Executor executor, @Nullable T valueIfFailed, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAllSuccessAsync(cffuScreened(executor), valueIfFailed, suppliers));
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf}
     * documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyMostSuccessAsync(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, Supplier<? extends T>... suppliers) {
        return mSupplyMostSuccessAsync(defaultExecutor, valueIfNotSuccess, timeout, unit, suppliers);
    }

    /**
     * Shortcut to method {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[]) mostSuccessResultsOf}
     * documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyMostSuccessAsync(
            Executor executor, @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit,
            Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyMostSuccessAsync(
                cffuScreened(executor), valueIfNotSuccess, timeout, unit, suppliers));
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf}, wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAsync(defaultExecutor, suppliers);
    }

    /**
     * Shortcut to method {@link #allResultsOf allResultsOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<List<T>> mSupplyAsync(
            Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAsync(cffuScreened(executor), suppliers));
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnySuccessAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnySuccessAsync(defaultExecutor, suppliers);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnySuccessAsync(Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAnySuccessAsync(cffuScreened(executor), suppliers));
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input suppliers to Cffu by {@link #supplyAsync(Supplier)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnyAsync(Supplier<? extends T>... suppliers) {
        return mSupplyAnyAsync(defaultExecutor, suppliers);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf},
     * wraps input suppliers to Cffu by {@link #supplyAsync(Supplier, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    @SafeVarargs
    public final <T> Cffu<T> mSupplyAnyAsync(Executor executor, Supplier<? extends T>... suppliers) {
        return create(CompletableFutureUtils.mSupplyAnyAsync(cffuScreened(executor), suppliers));
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf}, wraps input actions to Cffu by {@link #runAsync(Runnable)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public Cffu<Void> mRunFailFastAsync(Runnable... actions) {
        return mRunFailFastAsync(defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link #allFailFastOf allFailFastOf},
     * wraps input actions to Cffu by {@link #runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public Cffu<Void> mRunFailFastAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunFailFastAsync(cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input actions to Cffu by {@link #runAsync(Runnable)}.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     */
    public Cffu<Void> mRunAsync(Runnable... actions) {
        return mRunAsync(defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link #allOf allOf}, wraps input actions to Cffu by {@link #runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #allOf allOf} documentation for the rules of result computation.
     */
    public Cffu<Void> mRunAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunAsync(cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf}, wraps input actions to Cffu by {@link #runAsync(Runnable)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public Cffu<Void> mRunAnySuccessAsync(Runnable... actions) {
        return mRunAnySuccessAsync(defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link #anySuccessOf anySuccessOf},
     * wraps input actions to Cffu by {@link #runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public Cffu<Void> mRunAnySuccessAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunAnySuccessAsync(cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to Cffu by {@link #runAsync(Runnable)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public Cffu<Void> mRunAnyAsync(Runnable... actions) {
        return mRunAnyAsync(defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link #anyOf anyOf}, wraps input actions to Cffu by {@link #runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link #anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public Cffu<Void> mRunAnyAsync(Executor executor, Runnable... actions) {
        return create(CompletableFutureUtils.mRunAnyAsync(cffuScreened(executor), actions));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleFailFastAsync(defaultExecutor, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.mSupplyTupleFailFastAsync(cffuScreened(executor), supplier1, supplier2));
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleFailFastAsync(defaultExecutor, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.mSupplyTupleFailFastAsync(cffuScreened(executor), supplier1, supplier2, supplier3));
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleFailFastAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.mSupplyTupleFailFastAsync(
                cffuScreened(executor), supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleFailFastAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.mSupplyTupleFailFastAsync(
                cffuScreened(executor), supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyAllSuccessTupleAsync(defaultExecutor, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.mSupplyAllSuccessTupleAsync(cffuScreened(executor), supplier1, supplier2));
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyAllSuccessTupleAsync(defaultExecutor, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.mSupplyAllSuccessTupleAsync(cffuScreened(executor), supplier1, supplier2, supplier3));
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyAllSuccessTupleAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.mSupplyAllSuccessTupleAsync(
                cffuScreened(executor), supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyAllSuccessTupleAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.mSupplyAllSuccessTupleAsync(
                cffuScreened(executor), supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyMostSuccessTupleAsync(defaultExecutor, timeout, unit, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                cffuScreened(executor), timeout, unit, supplier1, supplier2));
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyMostSuccessTupleAsync(defaultExecutor, timeout, unit, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                cffuScreened(executor), timeout, unit, supplier1, supplier2, supplier3));
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyMostSuccessTupleAsync(defaultExecutor, timeout, unit, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                cffuScreened(executor), timeout, unit, supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyMostSuccessTupleAsync(defaultExecutor, timeout, unit,
                supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.mSupplyMostSuccessTupleAsync(
                cffuScreened(executor), timeout, unit, supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleAsync(defaultExecutor, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2> Cffu<Tuple2<T1, T2>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return create(CompletableFutureUtils.mSupplyTupleAsync(cffuScreened(executor), supplier1, supplier2));
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleAsync(defaultExecutor, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return create(CompletableFutureUtils.mSupplyTupleAsync(cffuScreened(executor), supplier1, supplier2, supplier3));
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return create(CompletableFutureUtils.mSupplyTupleAsync(
                cffuScreened(executor), supplier1, supplier2, supplier3, supplier4));
    }

    /**
     * Tuple variant of {@link #mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleAsync(defaultExecutor, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link #mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `mRunAsync`")
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return create(CompletableFutureUtils.mSupplyTupleAsync(
                cffuScreened(executor), supplier1, supplier2, supplier3, supplier4, supplier5));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## allOf* Methods(including mostSuccessResultsOf)
    //
    //    CompletionStage<T>[] -> Cffu<List<T>>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed normally with a list containing
     * the successful results of all given stages when all the given stages complete normally;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * WITHOUT waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method is the same as {@link #allResultsOf allResultsOf} method except for the fail-fast behavior.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#allAsList the equivalent Guava method allAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsFailFastOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allResultsFailFastOf(cfs));
    }

    /**
     * Returns a new Cffu that is completed normally with a list containing the successful results of
     * all given stages when all the given stages complete; The list of results is in the <strong>same order</strong>
     * as the input list, and if any of given stages complete exceptionally, their corresponding position will contain
     * {@code valueIfFailed} (which is indistinguishable from the stage having a successful value of {@code valueIfFailed}).
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method differs from {@link #allResultsFailFastOf allResultsFailFastOf} method in that it's tolerant
     * of failed stages for any of the items, representing them as {@code valueIfFailed} in the result list.
     *
     * @param valueIfFailed the value used as result if the input stage completed exceptionally
     * @throws NullPointerException if the cfs param or any of its elements is {@code null}
     * @see Cffu#getSuccessNow(Object)
     * @see Futures#successfulAsList the equivalent Guava method successfulAsList()
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allSuccessResultsOf(
            @Nullable T valueIfFailed, CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, cfs));
    }

    /**
     * Returns a new Cffu that is completed normally with a list containing the successful results of
     * the given stages before the given timeout (aka as many results as possible in the given time);
     * The list of results is in the <strong>same order</strong> as the input list, and if any of given stages
     * complete exceptionally or are incomplete, their corresponding positions will contain {@code valueIfNotSuccess}
     * (which is indistinguishable from the stage having a successful value of {@code valueIfNotSuccess}).
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * This method differs from {@link #allResultsFailFastOf allResultsFailFastOf} method in that it's tolerant of
     * failed or incomplete stages for any of the items, representing them as {@code valueIfNotSuccess} in the result list.
     *
     * @param valueIfNotSuccess the value used as result if the input stage not completed normally
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Cffu#getSuccessNow(Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> mostSuccessResultsOf(
            @Nullable T valueIfNotSuccess, long timeout, TimeUnit unit, CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.mostSuccessResultsOf(
                defaultExecutor, valueIfNotSuccess, timeout, unit, cfs));
    }

    /**
     * Returns a new Cffu that is completed normally with a list containing
     * the successful results of all given stages when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned Cffu
     * also does so, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value empty list.
     * <p>
     * The list of results is in the <strong>same order</strong> as the input list.
     * <p>
     * Comparing the waiting-all-<strong>complete</strong> behavior of this method, the fail-fast behavior
     * of {@link #allResultsFailFastOf allResultsFailFastOf} method is more responsive to user
     * and generally more desired in the application.
     * <p>
     * This method is the same as {@link #allOf allOf} method,
     * except that the returned Cffu contains the results of the given stages.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @Contract(pure = true)
    @SafeVarargs
    public final <T> Cffu<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.allResultsOf(cfs));
    }

    /**
     * Returns a new Cffu that is completed normally when all the given stages complete normally;
     * If any of the given stages complete exceptionally, then the returned Cffu also does so,
     * WITHOUT waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * The successful results, if any, of the given stages are not reflected in the returned Cffu
     * ({@code Cffu<Void>}), but may be obtained by inspecting them individually; Or using below methods
     * reflected results in the returned Cffu which are more convenient, safer and best-practice of concurrency:
     * <ul>
     * <li>{@link #allResultsFailFastOf  allResultsFailFastOf}, {@link #allTupleFailFastOf allTupleFailFastOf}
     * <li>{@link #allSuccessResultsOf allSuccessResultsOf}, {@link #allSuccessTupleOf allSuccessTupleOf}
     * <li>{@link #mostSuccessResultsOf mostSuccessResultsOf}, {@link #mostSuccessTupleOf mostSuccessTupleOf}
     * <li>{@link #allResultsOf allResultsOf}, {@link #allTupleOf allTupleOf}
     * </ul>
     * <p>
     * This method is the same as {@link #allOf allOf} method except for the fail-fast behavior.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#whenAllSucceed the equivalent Guava method whenAllSucceed()
     */
    @Contract(pure = true)
    public Cffu<Void> allFailFastOf(CompletionStage<?>... cfs) {
        return create(CompletableFutureUtils.allFailFastOf(cfs));
    }

    /**
     * Returns a new Cffu that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned Cffu
     * also does so, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a Cffu completed with the value {@code null}.
     * <p>
     * The successful results, if any, of the given stages are not reflected in the returned Cffu
     * ({@code Cffu<Void>}), but may be obtained by inspecting them individually; Or using below methods
     * reflected results in the returned Cffu which are more convenient, safer and best-practice of concurrency:
     * <ul>
     * <li>{@link #allResultsOf allResultsOf}, {@link #allTupleOf allTupleOf}
     * <li>{@link #allResultsFailFastOf  allResultsFailFastOf}, {@link #allTupleFailFastOf allTupleFailFastOf}
     * <li>{@link #allSuccessResultsOf allSuccessResultsOf}, {@link #allSuccessTupleOf allSuccessTupleOf}
     * <li>{@link #mostSuccessResultsOf mostSuccessResultsOf}, {@link #mostSuccessTupleOf mostSuccessTupleOf}
     * </ul>
     * <p>
     * Among the applications of this method is to await completion of a set of independent stages
     * before continuing a program, as in: {@code cffuFactory.allOf(c1, c2, c3).join();}.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     * @see Futures#whenAllComplete the equivalent Guava method whenAllComplete()
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
     * Returns a new Cffu that completed normally when any of the given stages complete normally,
     * with the same result; Otherwise, when all the given stages complete exceptionally, the returned Cffu
     * also does so, with a CompletionException holding an exception from any of the given stages as its cause.
     * If no stages are provided, returns a new Cffu that is already completed exceptionally
     * with a {@link NoCfsProvidedException}.
     * <p>
     * This method differs from {@link #anyOf anyOf} method in that this method is any-<strong>success</strong>
     * instead of the any-<strong>complete</strong> behavior of method {@link #anyOf anyOf}.
     *
     * @throws NullPointerException if the cfs param or any of its elements are {@code null}
     */
    @SafeVarargs
    public final <T> Cffu<T> anySuccessOf(CompletionStage<? extends T>... cfs) {
        return create(CompletableFutureUtils.anySuccessOf(cfs));
    }

    /**
     * Returns a new Cffu that is completed with the same successful result or exception of any of
     * the given stages when one stage completes. If no stages are provided, returns an incomplete Cffu.
     * <p>
     * Comparing the any-<strong>complete</strong> behavior of this method, the any-<strong>success</strong> behavior of
     * method {@link #anySuccessOf anySuccessOf} is more responsive to user and generally more desired in the application.
     *
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
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allTupleFailFastOf(cf1, cf2));
    }

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allTupleFailFastOf(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allTupleFailFastOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allTupleFailFastOf(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.allSuccessTupleOf(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> mostSuccessTupleOf(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2));
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4, T5> Cffu<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return create(CompletableFutureUtils.mostSuccessTupleOf(defaultExecutor, timeout, unit, cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2> Cffu<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2, T3> Cffu<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public <T1, T2, T3, T4> Cffu<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return create(CompletableFutureUtils.allTupleOf(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link #allResultsOf(CompletionStage[])}.
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
     * <strong>CAUTION:</strong> if run on old Java 8 (which does not support *minimal* CompletionStage),
     * this method just returns a *normal* Cffu instance which is NOT a *minimal* CompletionStage.
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
     * <strong>CAUTION:</strong> if run on old Java 8 (which does not support *minimal* CompletionStage),
     * this method just returns a *normal* Cffu instance which is NOT a *minimal* CompletionStage.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletionStage
     */
    @Contract(pure = true)
    public <T> CompletionStage<T> failedStage(Throwable ex) {
        return createMin((CompletableFuture<T>) CompletableFutureUtils.<T>failedStage(ex));
    }

    /**
     * Returns a new Cffu that encapsulates the execution of synchronous logic.
     * By wrapping synchronous code in a Cffu, exceptions can be handled consistently within the Cffu pipeline,
     * eliminating the need to manage separate exceptional paths both inside and outside the flow.
     *
     * @throws NullPointerException if argument {@code callable} is {@code null}
     * @see CompletableFuture#runAsync(Runnable)
     * @see CompletableFuture#supplyAsync(Supplier)
     */
    public <T> Cffu<T> fromSyncCall(Callable<T> callable) {
        return create(CompletableFutureUtils.fromSyncCall(callable));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region## CompletionStage Argument Factory Methods
    //
    //    - toCffu:      CF/CompletionStage   -> Cffu
    //    - toCffuArray: CF/CompletionStage[] -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a Cffu that maintains the same completion properties as the given stage, configured with this {@code CffuFactory}.
     * If the given stage is already a Cffu and uses this {@code CffuFactory}, this method may return the given stage.
     *
     * @throws NullPointerException if the given stage is null
     * @see CompletionStage#toCompletableFuture()
     * @see #toCffuArray(CompletionStage[])
     * @see Cffu#withCffuFactory(CffuFactory)
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
     * Converts input {@link CompletionStage}(including {@link CompletableFuture})
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
        return delayedExecutor(delay, unit, defaultExecutor);
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
        // NOTE: do NOT translate (ad hoc input)executor to screened executor; same as CompletableFuture.delayedExecutor
        return CompletableFutureUtils.delayedExecutor(delay, unit, cffuUnscreened(executor));
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
        return defaultExecutor.original;
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
