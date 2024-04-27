package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.*;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.*;
import java.util.function.*;

import static java.util.Objects.requireNonNull;


/**
 * This class {@link Cffu} is the equivalent class to {@link CompletableFuture},
 * contains the equivalent instance methods of {@link CompletionStage} and {@link CompletableFuture}.
 * <p>
 * The methods that equivalent to static factory methods of {@link CompletableFuture}
 * is in {@link CffuFactory} class.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CffuFactoryBuilder
 * @see CffuFactory
 * @see CompletionStage
 * @see CompletableFuture
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class Cffu<T> implements Future<T>, CompletionStage<T> {
    @NonNull
    private final CffuFactory fac;

    private final boolean isMinimalStage;
    @NonNull
    private final CompletableFuture<T> cf;

    Cffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<T> cf) {
        this.fac = requireNonNull(cffuFactory, "cffuFactory is null");
        this.isMinimalStage = isMinimalStage;
        this.cf = requireNonNull(cf, "cf is null");
    }

    @Contract(pure = true)
    private <U> Cffu<U> reset0(CompletableFuture<U> cf) {
        return new Cffu<>(this.fac, this.isMinimalStage, cf);
    }

    @Contract(pure = true)
    private <U> Cffu<U> resetToMin(CompletableFuture<U> cf) {
        return new Cffu<>(this.fac, true, cf);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Simple `then*` methods of `CompletionStage`:
    //
    //    - thenRun*(Runnable):    Void -> Void
    //    - thenAccept*(Consumer): T -> Void
    //    - thenApply*(Function):  T -> U
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when this stage completes normally, executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     * @see CompletionStage#thenRun(Runnable)
     */
    @Override
    public Cffu<Void> thenRun(Runnable action) {
        return reset0(cf.thenRun(action));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     * @see CompletionStage#thenRunAsync(Runnable)
     */
    @Override
    public Cffu<Void> thenRunAsync(Runnable action) {
        return thenRunAsync(action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * executes the given action using the supplied Executor.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     * @see CompletionStage#thenRunAsync(Runnable, Executor)
     */
    @Override
    public Cffu<Void> thenRunAsync(Runnable action, Executor executor) {
        return reset0(cf.thenRunAsync(action, executor));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed with this stage's result as the argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     * @see CompletionStage#thenAccept(Consumer)
     */
    @Override
    public Cffu<Void> thenAccept(Consumer<? super T> action) {
        return reset0(cf.thenAccept(action));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed using {@link #defaultExecutor()},
     * with this stage's result as the argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     * @see CompletionStage#thenAcceptAsync(Consumer)
     */
    @Override
    public Cffu<Void> thenAcceptAsync(Consumer<? super T> action) {
        return thenAcceptAsync(action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed using the supplied Executor, with this stage's result as the argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     * @see CompletionStage#thenAcceptAsync(Consumer, Executor)
     */
    @Override
    public Cffu<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return reset0(cf.thenAcceptAsync(action, executor));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed with this stage's result as the argument to the supplied function.
     * <p>
     * This method is analogous to {@link java.util.Optional#map Optional.map} and
     * {@link java.util.stream.Stream#map Stream.map}.
     * <p>
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param fn  the function to use to compute the value of the returned Cffu
     * @param <U> the function's return type
     * @return the new Cffu
     * @see CompletionStage#thenApply(Function)
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenRun`")
    @Override
    public <U> Cffu<U> thenApply(Function<? super T, ? extends U> fn) {
        return reset0(cf.thenApply(fn));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed using {@link #defaultExecutor()},
     * with this stage's result as the argument to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param fn  the function to use to compute the value of the returned Cffu
     * @param <U> the function's return type
     * @return the new Cffu
     * @see CompletionStage#thenApplyAsync(Function)
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenRunAsync`")
    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return thenApplyAsync(fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed using the supplied Executor,
     * with this stage's result as the argument to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new Cffu
     * @see CompletionStage#thenApplyAsync(Function, Executor)
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenRunAsync`")
    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return reset0(cf.thenApplyAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then both(binary input)` methods of CompletionStage:
    //
    //    - runAfterBoth*(Runnable):     Void, Void -> Void
    //    - thenAcceptBoth*(BiConsumer): (T1, T2) -> Void
    //    - thenCombine*(BiFunction):    (T1, T2) -> U
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally, executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return reset0(cf.runAfterBoth(other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return runAfterBothAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * executes the given action using the supplied executor.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return reset0(cf.runAfterBothAsync(other, action, executor));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally, executes the given action.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #runAfterBoth(CompletionStage, Runnable)}
     * except for the fast-fail behavior.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    public Cffu<Void> runAfterBothFastFail(CompletionStage<?> other, Runnable action) {
        return reset0(CompletableFutureUtils.runAfterBothFastFail(this, other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #runAfterBothAsync(CompletionStage, Runnable)}
     * except for the fast-fail behavior.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    public Cffu<Void> runAfterBothFastFailAsync(CompletionStage<?> other, Runnable action) {
        return runAfterBothFastFailAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * executes the given action using the supplied executor.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #runAfterBothAsync(CompletionStage, Runnable, Executor)}
     * except for the fast-fail behavior.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public Cffu<Void> runAfterBothFastFailAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return reset0(CompletableFutureUtils.runAfterBothFastFailAsync(this, other, action, executor));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed with the two results as arguments to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @param <U>    the type of the other CompletionStage's result
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<Void> thenAcceptBoth(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return reset0(cf.thenAcceptBoth(other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()}, with the two results as arguments to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @param <U>    the type of the other CompletionStage's result
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using the supplied executor, with the two results as arguments to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules
     * covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the other CompletionStage's result
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
                                              BiConsumer<? super T, ? super U> action,
                                              Executor executor) {
        return reset0(cf.thenAcceptBothAsync(other, action, executor));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #thenAcceptBoth(CompletionStage, BiConsumer)}
     * except for the fast-fail behavior.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @param <U>    the type of the other CompletionStage's result
     * @return the new Cffu
     */
    public <U> Cffu<Void> thenAcceptBothFastFail(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return reset0(CompletableFutureUtils.thenAcceptBothFastFail(this, other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()}, with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #thenAcceptBothAsync(CompletionStage, BiConsumer)}
     * except for the fast-fail behavior.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @param <U>    the type of the other CompletionStage's result
     * @return the new Cffu
     */
    public <U> Cffu<Void> thenAcceptBothFastFailAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothFastFailAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using the supplied executor, with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #thenAcceptBothAsync(CompletionStage, BiConsumer, Executor)}
     * except for the fast-fail behavior.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the other CompletionStage's result
     * @return the new Cffu
     */
    public <U> Cffu<Void> thenAcceptBothFastFailAsync(CompletionStage<? extends U> other,
                                                      BiConsumer<? super T, ? super U> action,
                                                      Executor executor) {
        return reset0(CompletableFutureUtils.thenAcceptBothFastFailAsync(this, other, action, executor));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBoth`")
    @Override
    public <U, V> Cffu<V> thenCombine(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return reset0(cf.thenCombine(other, fn));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()}, with the two results as arguments to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothAsync`")
    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineAsync(other, fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using the supplied executor, with the two results as arguments to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the other CompletionStage's result
     * @param <V>      the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothAsync`")
    @Override
    public <U, V> Cffu<V> thenCombineAsync(CompletionStage<? extends U> other,
                                           BiFunction<? super T, ? super U, ? extends V> fn,
                                           Executor executor) {
        return reset0(cf.thenCombineAsync(other, fn, executor));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #thenCombine(CompletionStage, BiFunction)}
     * except for the fast-fail behavior.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothFastFail`")
    public <U, V> Cffu<V> thenCombineFastFail(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return reset0(CompletableFutureUtils.thenCombineFastFail(this, other, fn));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()}, with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #thenCombineAsync(CompletionStage, BiFunction)}
     * except for the fast-fail behavior.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothFastFailAsync`")
    public <U, V> Cffu<V> thenCombineFastFailAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineFastFailAsync(other, fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using the supplied executor, with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned Cffu
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #thenCombineAsync(CompletionStage, BiFunction, Executor)}
     * except for the fast-fail behavior.
     *
     * @param other    the other CompletionStage
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the other CompletionStage's result
     * @param <V>      the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothFastFailAsync`")
    public <U, V> Cffu<V> thenCombineFastFailAsync(CompletionStage<? extends U> other,
                                                   BiFunction<? super T, ? super U, ? extends V> fn,
                                                   Executor executor) {
        return reset0(CompletableFutureUtils.thenCombineFastFailAsync(this, other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# convenient `allTupleOf` methods:
    //  providing these method is convenient for method chaining
    //
    //    - allTupleOf(CompletionStage...)
    //    - allTupleOfFastFail(CompletionStage...)
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * This method is the same as {@link CffuFactory#allTupleOf(CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOf(cf2);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOf(cffu, cf2);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOf(CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2> Cffu<Tuple2<T, T2>> allTupleOf(CompletionStage<? extends T2> cf2) {
        return fac.allTupleOf(cf, cf2);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOfFastFail(cf2);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOfFastFail(cffu, cf2);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2> Cffu<Tuple2<T, T2>> allTupleOfFastFail(CompletionStage<? extends T2> cf2) {
        return fac.allTupleOfFastFail(cf, cf2);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOf(CompletionStage, CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOf(cf2, cf3);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOf(cffu, cf2, cf3);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOf(CompletionStage, CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2, T3> Cffu<Tuple3<T, T2, T3>> allTupleOf(
            CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return fac.allTupleOf(cf, cf2, cf3);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOfFastFail(cf2, cf3);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOfFastFail(cffu, cf2, cf3);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2, T3> Cffu<Tuple3<T, T2, T3>> allTupleOfFastFail(
            CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return fac.allTupleOfFastFail(cf, cf2, cf3);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOf(cf2, cf3, cf4);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOf(cffu, cf2, cf3, cf4);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2, T3, T4> Cffu<Tuple4<T, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return fac.allTupleOf(cf, cf2, cf3, cf4);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOfFastFail(cf2, cf3, cf4);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOfFastFail(cffu, cf2, cf3, cf4);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2, T3, T4> Cffu<Tuple4<T, T2, T3, T4>> allTupleOfFastFail(
            CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return fac.allTupleOfFastFail(cf, cf2, cf3, cf4);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOf(cf2, cf3, cf4, cf5);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOf(cffu, cf2, cf3, cf4, cf5);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2, T3, T4, T5> Cffu<Tuple5<T, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return fac.allTupleOf(cf, cf2, cf3, cf4, cf5);
    }

    /**
     * This method is the same as {@link CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)},
     * providing this method is convenient for method chaining.
     * <p>
     * Calling this method
     * <p>
     * {@code allTuple = cffu.allTupleOfFastFail(cf2, cf3, cf4, cf5);}
     * <p>
     * is the same as:
     * <p>
     * {@code allTuple = cffu.cffuFactory().allTupleOfFastFail(cffu, cf2, cf3, cf4, cf5);}
     *
     * @return the new Cffu
     * @see CffuFactory#allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     */
    @Contract(pure = true)
    public <T2, T3, T4, T5> Cffu<Tuple5<T, T2, T3, T4, T5>> allTupleOfFastFail(
            CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return fac.allTupleOfFastFail(cf, cf2, cf3, cf4, cf5);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then either(binary input)` methods of CompletionStage:
    //
    //    - runAfterEither*(Runnable):  Void, Void -> Void
    //    - acceptEither*(Consumer):  (T, T) -> Void
    //    - applyToEither*(Function): (T, T) -> U
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return reset0(cf.runAfterEither(other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return runAfterEitherAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using the supplied executor.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterEitherAsync(
            CompletionStage<?> other, Runnable action, Executor executor) {
        return reset0(cf.runAfterEitherAsync(other, action, executor));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally, executes the given action.
     * Otherwise, all two complete exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding an exception from any of as its cause.
     * <p>
     * This method is the same as {@link #runAfterEither(CompletionStage, Runnable)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    public Cffu<Void> runAfterEitherSuccess(CompletionStage<?> other, Runnable action) {
        return reset0(CompletableFutureUtils.runAfterEitherSuccess(this, other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    public Cffu<Void> runAfterEitherSuccessAsync(CompletionStage<?> other, Runnable action) {
        return runAfterEitherSuccessAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using the supplied executor.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public Cffu<Void> runAfterEitherSuccessAsync(
            CompletionStage<?> other, Runnable action, Executor executor) {
        return reset0(CompletableFutureUtils.runAfterEitherSuccessAsync(this, other, action, executor));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> acceptEither(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return reset0(cf.acceptEither(other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the corresponding result as argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> acceptEitherAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> acceptEitherAsync(CompletionStage<? extends T> other,
                                        Consumer<? super T> action,
                                        Executor executor) {
        return reset0(cf.acceptEitherAsync(other, action, executor));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link #acceptEither(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    public Cffu<Void> acceptEitherSuccess(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return reset0(CompletableFutureUtils.acceptEitherSuccess(this, other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link #acceptEitherAsync(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    public Cffu<Void> acceptEitherSuccessAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherSuccessAsync(other, action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link #acceptEitherAsync(CompletionStage, Consumer, Executor)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    public Cffu<Void> acceptEitherSuccessAsync(CompletionStage<? extends T> other,
                                               Consumer<? super T> action,
                                               Executor executor) {
        return reset0(CompletableFutureUtils.acceptEitherSuccessAsync(this, other, action, executor));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEither`")
    @Override
    public <U> Cffu<U> applyToEither(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return reset0(cf.applyToEither(other, fn));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the corresponding result as argument to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherAsync`")
    @Override
    public <U> Cffu<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEitherAsync(other, fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other    the other CompletionStage
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherAsync`")
    @Override
    public <U> Cffu<U> applyToEitherAsync(CompletionStage<? extends T> other,
                                          Function<? super T, U> fn,
                                          Executor executor) {
        return reset0(cf.applyToEitherAsync(other, fn, executor));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link #applyToEither(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherSuccess`")
    public <U> Cffu<U> applyToEitherSuccess(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return reset0(CompletableFutureUtils.applyToEitherSuccess(this, other, fn));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link #applyToEitherAsync(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherSuccessAsync`")
    public <U> Cffu<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEitherSuccessAsync(other, fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link #applyToEitherAsync(CompletionStage, Function, Executor)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param other    the other CompletionStage
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherSuccessAsync`")
    public <U> Cffu<U> applyToEitherSuccessAsync(CompletionStage<? extends T> other,
                                                 Function<? super T, U> fn,
                                                 Executor executor) {
        return reset0(CompletableFutureUtils.applyToEitherSuccessAsync(this, other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Error Handling methods of CompletionStage:
    //
    //    - exceptionally*: throwable -> T
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is executed with this stage's exception as the argument to the supplied function.
     * Otherwise, if this stage completes normally,
     * then the returned stage also completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned Cffu
     *           if this Cffu completed exceptionally
     * @return the new Cffu
     */
    @Override
    public Cffu<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return reset0(cf.exceptionally(fn));
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is executed with this stage's exception as the argument to the supplied function,
     * using {@link #defaultExecutor()}.
     * Otherwise, if this stage completes normally,
     * then the returned stage also completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned Cffu
     *           if this Cffu completed exceptionally
     * @return the new Cffu
     */
    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is executed with this stage's exception as the argument to the supplied function,
     * using the supplied Executor. Otherwise, if this stage completes normally,
     * then the returned stage also completes normally with the same value.
     *
     * @param fn       the function to use to compute the value of the returned Cffu
     *                 if this Cffu completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) {
        return reset0(CompletableFutureUtils.exceptionallyAsync(cf, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Timeout Control methods:
    //
    //    - orTimeout:         timeout event -> complete with the given value
    //    - completeOnTimeout: timeout event -> complete with TimeoutException
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Exceptionally completes this Cffu with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     *
     * @param timeout how long to wait before completing exceptionally
     *                with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return this Cffu
     * @see CffuFactory#delayedExecutor(long, TimeUnit)
     */
    public Cffu<T> orTimeout(long timeout, TimeUnit unit) {
        checkMinimalStage();
        CompletableFutureUtils.orTimeout(cf, timeout, unit);
        return this;
    }

    /**
     * Completes this Cffu with the given value if not otherwise completed before the given timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return this Cffu
     * @see CffuFactory#delayedExecutor(long, TimeUnit)
     */
    public Cffu<T> completeOnTimeout(@Nullable T value, long timeout, TimeUnit unit) {
        checkMinimalStage();
        CompletableFutureUtils.completeOnTimeout(cf, value, timeout, unit);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Advanced methods of CompletionStage:
    //
    //    - thenCompose*:          T -> CompletionStage<T>
    //    - exceptionallyCompose*: throwable -> CompletionStage<T>
    //
    //    - handle*:       (T, throwable) -> U
    //    - whenComplete*: (T, throwable) -> Void
    //    - peek*:         (T, throwable) -> Void
    //
    // NOTE about advanced meaning:
    //   - `compose` methods, input function argument return CompletionStage
    //   - handle successful and failed result together(handle*/whenComplete*)
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed with the same value
     * as the CompletionStage returned by the given function.
     * <p>
     * When this stage completes normally, the given function is invoked
     * with this stage's result as the argument, returning another CompletionStage.
     * When that stage completes normally, the Cffu returned by this method
     * is completed with the same value.
     * <p>
     * To ensure progress, the supplied function must arrange eventual completion of its result.
     * <p>
     * This method is analogous to {@link java.util.Optional#flatMap Optional.flatMap} and
     * {@link java.util.stream.Stream#flatMap Stream.flatMap}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param fn  the function to use to compute another CompletionStage
     * @param <U> the type of the returned Cffu's result
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return reset0(cf.thenCompose(fn));
    }

    /**
     * Returns a new Cffu that is completed with the same value as the CompletionStage
     * returned by the given function, executed using {@link #defaultExecutor()}.
     * <p>
     * When this stage completes normally, the given function is invoked with this stage's result as the argument,
     * returning another CompletionStage.  When that stage completes normally,
     * the Cffu returned by this method is completed with the same value.
     * <p>
     * To ensure progress, the supplied function must arrange eventual completion of its result.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param fn  the function to use to compute another CompletionStage
     * @param <U> the type of the returned Cffu's result
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return thenComposeAsync(fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that is completed with the same value as the CompletionStage
     * returned by the given function, executed using the supplied Executor.
     * <p>
     * When this stage completes normally, the given function is invoked with this stage's result as the argument,
     * returning another CompletionStage.  When that stage completes normally,
     * the Cffu returned by this method is completed with the same value.
     * <p>
     * To ensure progress, the supplied function must arrange eventual completion of its result.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param fn       the function to use to compute another CompletionStage
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the returned Cffu's result
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return reset0(cf.thenComposeAsync(fn, executor));
    }

    /**
     * Returns a new CompletionStage that, when this stage completes exceptionally,
     * is composed using the results of the supplied function applied to this stage's exception.
     *
     * @param fn the function to use to compute the returned CompletionStage
     *           if this CompletionStage completed exceptionally
     * @return the new CompletionStage
     */
    @Override
    public Cffu<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return reset0(CompletableFutureUtils.exceptionallyCompose(cf, fn));
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is composed using the results of the supplied function applied to this stage's exception,
     * using {@link #defaultExecutor()}.
     *
     * @param fn the function to use to compute the returned CompletionStage
     *           if this Cffu completed exceptionally
     * @return the new Cffu
     */
    @Override
    public Cffu<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is composed using the results of the supplied function applied to this stage's exception,
     * using the supplied Executor.
     *
     * @param fn       the function to use to compute the returned CompletionStage
     *                 if this Cffu completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    @Override
    public Cffu<T> exceptionallyComposeAsync(
            Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        return reset0(CompletableFutureUtils.exceptionallyComposeAsync(cf, fn, executor));
    }

    /**
     * Returns a new Cffu that, when this cffu completes either normally or exceptionally,
     * is executed with this cffu's result and exception as arguments to the supplied function.
     * <p>
     * When this cffu is complete, the given function is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments,
     * and the function's result is used to complete the returned cffu.
     *
     * @param fn  the function to use to compute the value of the returned Cffu
     * @param <U> the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `whenComplete`")
    @Override
    public <U> Cffu<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return reset0(cf.handle(fn));
    }

    /**
     * Returns a new Cffu that, when this cffu completes either normally or exceptionally,
     * is executed using {@link #defaultExecutor()},
     * with this cffu's result and exception as arguments to the supplied function.
     * <p>
     * When this Cffu is complete, the given function is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this Cffu as arguments,
     * and the function's result is used to complete the returned Cffu.
     *
     * @param fn  the function to use to compute the value of the returned Cffu
     * @param <U> the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `whenCompleteAsync`")
    @Override
    public <U> Cffu<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return handleAsync(fn, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu that, when this cffu completes either normally or exceptionally,
     * is executed using the supplied executor, with this cffu's result and exception
     * as arguments to the supplied function.
     * <p>
     * When this cffu is complete, the given function is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments,
     * and the function's result is used to complete the returned cffu.
     *
     * @param fn       the function to use to compute the value of the returned cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new Cffu
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `whenCompleteAsync`")
    @Override
    public <U> Cffu<U> handleAsync(
            BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return reset0(cf.handleAsync(fn, executor));
    }

    /**
     * Returns a new Cffu with the same result or exception as this stage,
     * that executes the given action when this stage completes.
     * <p>
     * When this stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this stage as arguments.
     * The returned stage is completed when the action returns.
     * <p>
     * Unlike method {@link #handle handle}, this method is not designed to translate completion outcomes,
     * so the supplied action should not throw an exception. However, if it does, the following rules apply:
     * if this stage completed normally but the supplied action throws an exception,
     * then the returned stage completes exceptionally with the supplied action's exception.
     * Or, if this stage completed exceptionally and the supplied action throws an exception,
     * then the returned stage completes exceptionally with this stage's exception.
     *
     * @param action the action to perform
     * @return the new Cffu
     */
    @Override
    public Cffu<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return reset0(cf.whenComplete(action));
    }

    /**
     * Returns a new Cffu with the same result or exception as this stage,
     * that executes the given action using {@link #defaultExecutor()}
     * when this stage completes.
     * <p>
     * When this stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this stage as arguments.
     * The returned stage is completed when the action returns.
     * <p>
     * Unlike method {@link #handleAsync(BiFunction) handleAsync}, this method is not designed to
     * translate completion outcomes, so the supplied action should not throw an exception.
     * However, if it does, the following rules apply:
     * If this stage completed normally but the supplied action throws an exception,
     * then the returned stage completes exceptionally with the supplied action's exception.
     * Or, if this stage completed exceptionally and the supplied action throws an exception,
     * then the returned stage completes exceptionally with this stage's exception.
     *
     * @param action the action to perform
     * @return the new Cffu
     */
    @Override
    public Cffu<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return whenCompleteAsync(action, fac.defaultExecutor());
    }

    /**
     * Returns a new Cffu with the same result or exception as this stage,
     * that executes the given action using the supplied Executor when this stage completes.
     * <p>
     * When this stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this stage as arguments.
     * The returned stage is completed when the action returns.
     * <p>
     * Unlike method {@link #handleAsync(BiFunction, Executor) handleAsync}, this method is not designed to
     * translate completion outcomes, so the supplied action should not throw an exception.
     * However, if it does, the following rules apply:
     * If this stage completed normally but the supplied action throws an exception,
     * then the returned stage completes exceptionally with the supplied action's exception.
     * Or, if this stage completed exceptionally and the supplied action throws an exception,
     * then the returned stage completes exceptionally with this stage's exception.
     *
     * @param action   the action to perform
     * @param executor the executor to use for asynchronous execution
     * @return the new Cffu
     */
    @Override
    public Cffu<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return reset0(cf.whenCompleteAsync(action, executor));
    }

    /**
     * Peeks the result by executing the given action when this cffu completes, returns this cffu.
     * <p>
     * When this cffu is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments.
     * Whether the supplied action throws an exception or not, do <strong>NOT</strong> affect this cffu.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return this Cffu
     * @see CompletionStage#whenComplete(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    public Cffu<T> peek(BiConsumer<? super T, ? super Throwable> action) {
        cf.whenComplete(action);
        return this;
    }

    /**
     * Peeks the result by executing the given action when this cffu completes,
     * executes the given action using {@link #defaultExecutor()}, returns this cffu.
     * <p>
     * When this cffu is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments.
     * Whether the supplied action throws an exception or not, do <strong>NOT</strong> affect this cffu.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return this Cffu
     * @see CompletionStage#whenCompleteAsync(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    public Cffu<T> peekAsync(BiConsumer<? super T, ? super Throwable> action) {
        return peekAsync(action, fac.defaultExecutor());
    }

    /**
     * Peeks the result by executing the given action when this cffu completes,
     * that executes the given action using the supplied Executor when this cffu completes, returns this cffu.
     * <p>
     * When this cffu is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments.
     * Whether the supplied action throws an exception or not, do <strong>NOT</strong> affect this cffu.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return this Cffu
     * @see CompletionStage#whenCompleteAsync(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    public Cffu<T> peekAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        cf.whenCompleteAsync(action, executor);
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Read(explicitly) methods of CompletableFuture
    //
    //    - get()               // BLOCKING!
    //    - get(timeout, unit)  // BLOCKING!
    //    - join()              // BLOCKING!
    //    - join(timeout, unit) // BLOCKING!
    //    - getNow(T valueIfAbsent)
    //    - resultNow()
    //    - exceptionNow()
    //
    //    - isDone()
    //    - isCompletedExceptionally()
    //    - isCancelled()
    //    - state()
    //    - state()
    //
    // NOTE about ExecutionException or CompletionException when the computation threw an exception:
    //   - get methods throw ExecutionException(checked exception)
    //     these old methods existed in `Future` interface since Java 5
    //   - getNow/join throw CompletionException(unchecked exception),
    //     these new methods existed in `CompletableFuture` since Java 8
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an exception
     * @throws InterruptedException  if the current thread was interrupted while waiting
     * @see #join()
     * @see #join(long, TimeUnit)
     * @see #getNow(Object)
     * @see #resultNow()
     * @see #get(long, TimeUnit)
     */
    @Blocking
    @Nullable
    @Override
    public T get() throws InterruptedException, ExecutionException {
        checkMinimalStage();
        return cf.get();
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result, if available.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an exception
     * @throws InterruptedException  if the current thread was interrupted while waiting
     * @throws TimeoutException      if the wait timed out
     * @see #join(long, TimeUnit)
     * @see #getNow(Object)
     * @see #resultNow()
     * @see #join()
     * @see #get()
     */
    @Blocking
    @Nullable
    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        checkMinimalStage();
        return cf.get(timeout, unit);
    }

    /**
     * Returns the result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * <p>
     * To better conform with the use of common functional forms, if a computation involved in the completion
     * of this Cffu threw an exception, this method throws an (unchecked) {@link CompletionException}
     * with the underlying exception as its cause.
     *
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally
     *                               or a completion computation threw an exception
     * @see #join(long, TimeUnit)
     * @see #getNow(Object)
     * @see #resultNow()
     * @see #get(long, TimeUnit)
     * @see #get()
     */
    @Blocking
    @Nullable
    public T join() {
        checkMinimalStage();
        return cf.join();
    }

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * <p>
     * <strong>NOTE:<br></strong>
     * Calling this method
     * <p>
     * {@code result = cffu.join(timeout, unit);}
     * <p>
     * is the same as:
     *
     * <pre>{@code result = cffu.copy() // defensive copy to avoid writing this cffu unexpectedly
     *     .orTimeout(timeout, unit)
     *     .join();
     * }</pre>
     *
     * <strong>CAUTION:<br></strong>
     * if the wait timed out, this method throws an (unchecked) {@link CompletionException}
     * with the {@link TimeoutException} as its cause;
     * NOT throws a (checked) {@link TimeoutException} like {@link #get(long, TimeUnit)}.
     *
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally
     *                               or a completion computation threw an exception
     *                               or the wait timed out(with the {@code TimeoutException} as its cause)
     * @see #join()
     * @see #getNow(Object)
     * @see #resultNow()
     * @see #get(long, TimeUnit)
     * @see #get()
     * @see #orTimeout(long, TimeUnit)
     */
    @Blocking
    @Nullable
    public T join(long timeout, TimeUnit unit) {
        checkMinimalStage();
        return CompletableFutureUtils.join(cf, timeout, unit);
    }

    /**
     * Returns the result value (or throws any encountered exception) if completed,
     * else returns the given valueIfAbsent.
     *
     * @param valueIfAbsent the value to return if not completed
     * @return the result value, if completed, else the given valueIfAbsent
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally
     *                               or a completion computation threw an exception
     * @see #resultNow()
     * @see #join(long, TimeUnit)
     * @see #join()
     * @see #get(long, TimeUnit)
     * @see #get()
     */
    @Contract(pure = true)
    @Nullable
    public T getNow(T valueIfAbsent) {
        checkMinimalStage();
        return cf.getNow(valueIfAbsent);
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
     * @see #getNow(Object)
     */
    @Contract(pure = true)
    @Nullable
    @Override
    public T resultNow() {
        checkMinimalStage();
        return CompletableFutureUtils.resultNow(cf);
    }

    /**
     * Returns the exception thrown by the task, without waiting.
     * <p>
     * This method is for cases where the caller knows that the task has already completed with an exception.
     *
     * @return the exception thrown by the task
     * @throws IllegalStateException if the task has not completed, the task completed normally,
     *                               or the task was cancelled
     * @see #resultNow()
     */
    @Contract(pure = true)
    @Override
    public Throwable exceptionNow() {
        checkMinimalStage();
        return CompletableFutureUtils.exceptionNow(cf);
    }

    /**
     * Returns {@code true} if this task completed.
     * <p>
     * Completion may be due to normal termination, an exception, or cancellation -- in all of these cases,
     * this method will return {@code true}.
     *
     * @return {@code true} if this task completed
     */
    @Contract(pure = true)
    @Override
    public boolean isDone() {
        checkMinimalStage();
        return cf.isDone();
    }

    /**
     * Returns {@code true} if this Cffu completed exceptionally, in any way.
     * Possible causes include cancellation, explicit invocation of {@code completeExceptionally},
     * and abrupt termination of a CompletionStage action.
     *
     * @return {@code true} if this Cffu completed exceptionally
     */
    @Contract(pure = true)
    public boolean isCompletedExceptionally() {
        checkMinimalStage();
        return cf.isCompletedExceptionally();
    }

    /**
     * Returns {@code true} if this Cffu was cancelled before it completed normally.
     *
     * @return {@code true} if this Cffu was cancelled
     * before it completed normally
     */
    @Contract(pure = true)
    @Override
    public boolean isCancelled() {
        checkMinimalStage();
        return cf.isCancelled();
    }

    /**
     * Returns the computation state, this method contains <strong>NO</strong> java version compatibility logic,
     * if you need this function in old {@code java 18-}, use {@link #cffuState()} instead.
     * <p>
     * <strong>NOTE:<br></strong>
     * {@link CompletableFuture#state} is new method since Java 19,
     * this method should have compatibility logic for old Java version;
     * But the return type {@link Future.State} is also added since Java 19,
     * so it's IMPOSSIBLE to backport by compatibility logic of wrapper class({@code Cffu}).
     *
     * @return the computation state
     * @see #cffuState()
     * @see Future.State
     * @see CompletableFuture#state()
     */
    @Contract(pure = true)
    @Override
    public Future.State state() {
        checkMinimalStage();
        return cf.state();
    }

    /**
     * Returns the computation state({@link CffuState}), this method  is equivalent to {@link CompletableFuture#state()}
     * with java version compatibility logic, so you can invoke in old {@code java 18-}.
     *
     * @return the computation state
     * @see CffuState
     * @see Future.State
     * @see Future#state()
     * @see #state()
     */
    @Contract(pure = true)
    public CffuState cffuState() {
        checkMinimalStage();
        return CompletableFutureUtils.state(cf);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Write methods of CompletableFuture
    //
    //    - complete(value): boolean
    //    - completeAsync*: -> Cffu
    //
    //    - completeExceptionally(ex): boolean
    //    - cancel(boolean): boolean
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * If not already completed, sets the value returned by {@link #get()} and related methods to the given value.
     *
     * @param value the result value
     * @return {@code true} if this invocation caused this Cffu to transition to a completed state, else {@code false}
     */
    public boolean complete(@Nullable T value) {
        checkMinimalStage();
        return cf.complete(value);
    }

    /**
     * Completes this Cffu with the result of the given Supplier function invoked
     * from an asynchronous task using {@link #defaultExecutor()}.
     *
     * @param supplier a function returning the value to be used to complete this Cffu
     * @return this Cffu
     */
    public Cffu<T> completeAsync(Supplier<? extends T> supplier) {
        return completeAsync(supplier, fac.defaultExecutor());
    }

    /**
     * Completes this Cffu with the result of the given Supplier function invoked
     * from an asynchronous task using the given executor.
     *
     * @param supplier a function returning the value to be used to complete this Cffu
     * @param executor the executor to use for asynchronous execution
     * @return this Cffu
     */
    public Cffu<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
        checkMinimalStage();
        CompletableFutureUtils.completeAsync(cf, supplier, executor);
        return this;
    }

    /**
     * If not already completed, causes invocations of {@link #get()} and related methods to throw the given exception.
     *
     * @param ex the exception
     * @return {@code true} if this invocation caused this Cffu to transition to a completed state, else {@code false}
     */
    public boolean completeExceptionally(Throwable ex) {
        checkMinimalStage();
        return cf.completeExceptionally(ex);
    }

    /**
     * If not already completed, completes this Cffu with a {@link CancellationException}.
     * Dependent Cffus that have not already completed will also complete exceptionally,
     * with a {@link CompletionException} caused by this {@code CancellationException}.
     *
     * @param mayInterruptIfRunning this value has no effect in this implementation
     *                              because interrupts are not used to control processing.
     * @return {@code true} if this task is now cancelled
     */
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        checkMinimalStage();
        return cf.cancel(mayInterruptIfRunning);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Cffu Re-Config methods
    //
    //    - minimalCompletionStage()
    //    - resetCffuFactory(cffuFactory)
    //
    //    - toCompletableFuture()
    //    - copy()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletionStage that is completed normally with the same value
     * as this CompletableFuture when it completes normally, and cannot be independently completed
     * or otherwise used in ways not defined by the methods of interface {@link CompletionStage}.
     * If this CompletableFuture completes exceptionally, then the returned CompletionStage completes
     * exceptionally with a CompletionException with this exception as cause.
     * <p>
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8, just return a Cffu with
     * a *normal* underlying CompletableFuture which is NOT with a *minimal* CompletionStage.
     * <p>
     * demo code about re-config methods of Cffu:
     *
     * <pre>{@code cffu2 = cffu
     *     .resetCffuFactory(cffuFactory2) // reset to use config from cffuFactory2
     *     .minimalCompletionStage();      // restrict to methods of CompletionStage
     * }</pre>
     *
     * @see #resetCffuFactory(CffuFactory)
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public CompletionStage<T> minimalCompletionStage() {
        return resetToMin((CompletableFuture<T>) CompletableFutureUtils.minimalCompletionStage(cf));
    }

    /**
     * Returns a new Cffu with given CffuFactory(contained configuration)
     * that is completed normally with the same value as this Cffu when it completes normally.
     * If this Cffu completes exceptionally, then the returned Cffu completes exceptionally
     * with a CompletionException with this exception as cause.
     * <p>
     * demo code about re-config methods of Cffu:
     *
     * <pre>{@code cffu2 = cffu
     *     .resetCffuFactory(cffuFactory2) // reset to use config from cffuFactory2
     *     .minimalCompletionStage();      // restrict to methods of CompletionStage
     * }</pre>
     *
     * @param cffuFactory cffuFactory contained configuration
     * @return the new Cffu
     * @see #minimalCompletionStage()
     */
    @Contract(pure = true)
    public Cffu<T> resetCffuFactory(CffuFactory cffuFactory) {
        return new Cffu<>(cffuFactory, this.isMinimalStage, this.cf);
    }

    /**
     * Returns a {@link CompletableFuture} maintaining the same completion properties as this stage.
     * <p>
     * call {@link CompletableFuture#toCompletableFuture()} method of the underlying CompletableFuture:
     * {@code underlyingCf.toCompletableFuture()}; if you need the underlying CompletableFuture instance,
     * call method {@link #cffuUnwrap()}.
     * <p>
     * {@link CffuFactory#toCompletableFutureArray(CompletionStage[])} is the batch operation to this method.
     *
     * @return the CompletableFuture
     * @see CompletionStage#toCompletableFuture()
     * @see #cffuUnwrap()
     * @see CffuFactory#toCompletableFutureArray(CompletionStage[])
     */
    @Contract(pure = true)
    @Override
    public CompletableFuture<T> toCompletableFuture() {
        return cf.toCompletableFuture();
    }

    /**
     * Returns a new Cffu that is completed normally with the same value as this Cffu when it completes normally.
     * If this Cffu completes exceptionally, then the returned Cffu completes exceptionally
     * with a CompletionException with this exception as cause. The behavior is equivalent to {@code thenApply(x -> x)}.
     * This method may be useful as a form of "defensive copying", to prevent clients from completing,
     * while still being able to arrange dependent actions.
     *
     * @return the new Cffu
     */
    @Contract(pure = true)
    public Cffu<T> copy() {
        return reset0(CompletableFutureUtils.copy(cf));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Getter methods of Cffu properties
    //
    //    - defaultExecutor()
    //    - forbidObtrudeMethods()
    //    - isMinimalStage()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * Config from the {@link CffuFactory#defaultExecutor()},
     * and can re-configured by {@link #resetCffuFactory(CffuFactory)}.
     *
     * @return the default executor
     * @see CffuFactory#defaultExecutor()
     * @see CffuFactoryBuilder#newCffuFactoryBuilder(Executor)
     * @see #cffuFactory()
     */
    @Contract(pure = true)
    public Executor defaultExecutor() {
        return fac.defaultExecutor();
    }

    /**
     * Returns the {@link CffuFactory} of this Cffu.
     * This can be re-configured by {@link #resetCffuFactory(CffuFactory)}.
     *
     * @return the CffuFactory
     * @see #defaultExecutor()
     */
    @Contract(pure = true)
    public CffuFactory cffuFactory() {
        return fac;
    }

    /**
     * Returns {@code forbidObtrudeMethods} or not.
     * This can be re-configured by {@link #resetCffuFactory(CffuFactory)}.
     *
     * @see Cffu#obtrudeValue(Object)
     * @see Cffu#obtrudeException(Throwable)
     * @see CffuFactory#forbidObtrudeMethods()
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     */
    @Contract(pure = true)
    public boolean forbidObtrudeMethods() {
        return fac.forbidObtrudeMethods();
    }

    /**
     * Returns whether is a {@code minimal stage} or not.
     * <p>
     * create a {@code minimal stage} by below methods:
     * <ul>
     * <li>{@link CffuFactory#completedStage(Object)}
     * <li>{@link CffuFactory#failedStage(Throwable)}
     * <li>{@link #minimalCompletionStage()}
     * <li>{@link CffuFactory#asCffu(CompletionStage)}, this method return a {@code minimal stage}
     *     when input a{@code minimal stage}, otherwise return a normal stage.
     * </ul>
     */
    @Contract(pure = true)
    public boolean isMinimalStage() {
        return isMinimalStage;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Inspection methods of Cffu
    //
    //    - cffuUnwrap()
    //    - getNumberOfDependents()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the underlying CompletableFuture.
     * <p>
     * {@link CffuFactory#asCffu(CompletionStage)} is inverse operation to this method.
     * {@link CffuFactory#cffuArrayUnwrap(Cffu[])} is the batch operation to this method.
     *
     * @return the underlying CompletableFuture
     * @see CffuFactory#asCffu(CompletionStage)
     * @see CffuFactory#cffuArrayUnwrap(Cffu[])
     * @see #toCompletableFuture()
     */
    @Contract(pure = true)
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> cffuUnwrap() {
        return cf;
    }

    /**
     * Returns the estimated number of Cffus whose completions are awaiting completion of this Cffu.
     * This method is designed for use in monitoring system state, not for synchronization control.
     *
     * @return the number of dependent Cffus
     * @see CompletableFuture#getNumberOfDependents()
     */
    @Contract(pure = true)
    public int getNumberOfDependents() {
        checkMinimalStage();
        return cf.getNumberOfDependents();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Other dangerous methods of CompletableFuture
    //
    //    - obtrudeValue(value)
    //    - obtrudeException(ex)
    //
    //# methods of CompletableFuture for API compatibility
    //
    //    - newIncompleteFuture()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Forcibly sets or resets the value subsequently returned by method {@link #get()} and related methods,
     * whether or not already completed. This method is designed for use only in error recovery actions,
     * and even in such situations may result in ongoing dependent completions using established versus overwritten outcomes.
     *
     * @param value the completion value
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     * @see CffuFactory#forbidObtrudeMethods()
     */
    @SuppressFBWarnings("NP_PARAMETER_MUST_BE_NONNULL_BUT_MARKED_AS_NULLABLE")
    public void obtrudeValue(@Nullable T value) {
        checkMinimalStage();
        checkForbidObtrudeMethods();
        cf.obtrudeValue(value);
    }

    /**
     * Forcibly causes subsequent invocations of method {@link #get()} and related methods to throw the given exception,
     * whether or not already completed. This method is designed for use only in error recovery actions,
     * and even in such situations may result in ongoing dependent completions using established versus overwritten outcomes.
     *
     * @param ex the exception
     * @throws NullPointerException if the exception is null
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     * @see CffuFactory#forbidObtrudeMethods()
     */
    public void obtrudeException(Throwable ex) {
        checkMinimalStage();
        checkForbidObtrudeMethods();
        cf.obtrudeException(ex);
    }

    /**
     * Returns a new incomplete Cffu with CompletableFuture of the type to be returned by a CompletionStage method.
     * Subclasses of CompletableFuture should normally override this method to return an instance of the same class
     * as this CompletableFuture. The default implementation returns an instance of class CompletableFuture.
     * <p>
     * <strong>NOTE:<br></strong>
     * this method existed mainly for API compatibility to {@code CompletableFuture},
     * prefer {@link CffuFactory#newIncompleteCffu()}.
     * <p>
     * <strong>CAUTION:<br></strong>
     * for minimal stage instance({@link #isMinimalStage()}), if run on old Java 8,
     * just return a Cffu with a *normal* underlying CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @see CffuFactory#newIncompleteCffu()
     * @see CompletableFuture#newIncompleteFuture()
     */
    @Contract(pure = true)
    public <U> Cffu<U> newIncompleteFuture() {
        return reset0(CompletableFutureUtils.newIncompleteFuture(cf));
    }

    /**
     * Returns a string identifying this Cffu, as well as its completion state.
     * <p>
     * The state, in brackets, contains the String {@code "Completed Normally"} or
     * the String {@code "Completed Exceptionally"}, or the String {@code "Not completed"}
     * followed by the number of Cffus dependent upon its completion, if any.
     *
     * @return a string identifying this Cffu, as well as its state
     * @see CompletableFuture#toString()
     */
    @Contract(pure = true)
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this))
                + "(" + cf + ")";
    }

    private void checkMinimalStage() {
        if (isMinimalStage) throw new UnsupportedOperationException("unsupported because this is a minimal stage");
    }

    private void checkForbidObtrudeMethods() {
        if (fac.forbidObtrudeMethods()) throw new UnsupportedOperationException("obtrude methods is forbidden by cffu");
    }
}
