package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.*;
import java.util.function.*;

import static io.foldright.cffu.CffuFactory.*;
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
    @NonNull
    private final CompletableFuture<T> cf;

    Cffu(CffuFactory cffuFactory, CompletableFuture<T> cf) {
        this.fac = cffuFactory;
        this.cf = cf;
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
        return fac.new0(cf.thenRun(action));
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
        return fac.new0(cf.thenRunAsync(action, fac.defaultExecutor));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     * @see CompletionStage#thenRunAsync(Runnable, Executor)
     */
    @Override
    public Cffu<Void> thenRunAsync(Runnable action, Executor executor) {
        return fac.new0(cf.thenRunAsync(action, executor));
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
        return fac.new0(cf.thenAccept(action));
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
        return fac.new0(cf.thenAcceptAsync(action, fac.defaultExecutor));
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
        return fac.new0(cf.thenAcceptAsync(action, executor));
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
    @Override
    public <U> Cffu<U> thenApply(Function<? super T, ? extends U> fn) {
        return fac.new0(cf.thenApply(fn));
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
    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return fac.new0(cf.thenApplyAsync(fn, fac.defaultExecutor));
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
    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return fac.new0(cf.thenApplyAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then both(binary input)` methods of CompletionStage:
    //
    //    - runAfterBoth*(Runnable):     Void, Void -> Void
    //    - thenAcceptBoth*(BiConsumer): (T1, T2) -> Void
    //    - thenCombine*(BiFunction):    (T1, T2) -> U
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when this and the other given stage
     * both complete normally, executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @return the new Cffu
     */
    @Override
    public Cffu<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return fac.new0(cf.runAfterBoth(other, action));
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
        return fac.new0(cf.runAfterBothAsync(other, action, fac.defaultExecutor));
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
        return fac.new0(cf.runAfterBothAsync(other, action, executor));
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
        return fac.new0(cf.thenAcceptBoth(other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the two results as arguments to the supplied action.
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
        return fac.new0(cf.thenAcceptBothAsync(other, action, fac.defaultExecutor));
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
        return fac.new0(cf.thenAcceptBothAsync(other, action, executor));
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
    @Override
    public <U, V> Cffu<V> thenCombine(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return fac.new0(cf.thenCombine(other, fn));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the two results as arguments to the supplied function.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     * @return the new Cffu
     */
    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return fac.new0(cf.thenCombineAsync(other, fn, fac.defaultExecutor));
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
    @Override
    public <U, V> Cffu<V> thenCombineAsync(CompletionStage<? extends U> other,
                                           BiFunction<? super T, ? super U, ? extends V> fn,
                                           Executor executor) {
        return fac.new0(cf.thenCombineAsync(other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then either(binary input)` methods of CompletionStage:
    //
    //    - runAfterEither*(Runnable):  Void, Void -> Void
    //    - acceptEither*(BiConsumer):  (T1, T2) -> Void
    //    - applyToEither*(BiFunction): (T1, T2) -> U
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
        return fac.new0(cf.runAfterEither(other, action));
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
        return fac.new0(cf.runAfterEitherAsync(other, action, fac.defaultExecutor));
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
        return fac.new0(cf.runAfterEitherAsync(other, action, executor));
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
        return fac.new0(cf.acceptEither(other, action));
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
        return fac.new0(cf.acceptEitherAsync(other, action, fac.defaultExecutor));
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
        return fac.new0(cf.acceptEitherAsync(other, action, executor));
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
    @Override
    public <U> Cffu<U> applyToEither(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return fac.new0(cf.applyToEither(other, fn));
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
    @Override
    public <U> Cffu<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return fac.new0(cf.applyToEitherAsync(other, fn, fac.defaultExecutor));
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
    @Override
    public <U> Cffu<U> applyToEitherAsync(CompletionStage<? extends T> other,
                                          Function<? super T, U> fn,
                                          Executor executor) {
        return fac.new0(cf.applyToEitherAsync(other, fn, executor));
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
        return fac.new0(cf.exceptionally(fn));
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
        return exceptionallyAsync(fn, fac.defaultExecutor);
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
        if (IS_JAVA12_PLUS) {
            return fac.new0(cf.exceptionallyAsync(fn, executor));
        }

        return handle((r, ex) -> (ex == null) ? this :
                this.<T>handleAsync((r1, ex1) -> fn.apply(ex1), executor)
        ).thenCompose(Function.identity());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Timeout Control methods:
    //
    //    - orTimeout:         timeout event -> given value
    //    - completeOnTimeout: timeout event -> TimeoutException
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Exceptionally completes this Cffu with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     *
     * @param timeout how long to wait before completing exceptionally
     *                with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return this Cffu
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> orTimeout(long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            cf.orTimeout(timeout, unit);
            return this;
        }

        requireNonNull(unit, "unit is null");
        if (!cf.isDone()) {
            cf.whenComplete(new Canceller(Delayer.delay(new Timeout(cf), timeout, unit)));
        }
        return this;
    }

    /**
     * Completes this Cffu with the given value if not otherwise completed before the given timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return this Cffu
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> completeOnTimeout(@Nullable T value, long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            cf.completeOnTimeout(value, timeout, unit);
            return this;
        }

        requireNonNull(unit, "unit is null");
        if (!cf.isDone()) {
            cf.whenComplete(new Canceller(Delayer.delay(new DelayedCompleter<>(cf, value), timeout, unit)));
        }
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Advanced methods of CompletionStage:
    //
    //    - thenCompose*:          T -> CompletionStage<T>
    //    - exceptionallyCompose*: throwable -> CompletionStage<T>
    //
    //    - whenComplete*:         (T, throwable) -> Void
    //    - handle*:               (T, throwable) -> T
    //
    // NOTE about advanced meaning:
    //   - `compose` methods, input function argument return CompletionStage
    //   - handle succeed and failed result together(handle*/whenComplete*)
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
        return fac.new0(cf.thenCompose(fn));
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
        return fac.new0(cf.thenComposeAsync(fn, fac.defaultExecutor));
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
        return fac.new0(cf.thenComposeAsync(fn, executor));
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
        if (IS_JAVA12_PLUS) {
            return fac.new0(cf.exceptionallyCompose(fn));
        }

        return handle((r, ex) -> (ex == null) ? this : fn.apply(ex))
                .thenCompose(Function.identity());
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
        return exceptionallyComposeAsync(fn, fac.defaultExecutor);
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
        if (IS_JAVA12_PLUS) {
            return fac.new0(cf.exceptionallyComposeAsync(fn, executor));
        }

        return handle((r, ex) -> (ex == null) ? this :
                this.handleAsync((r1, ex1) -> fn.apply(ex1), executor).thenCompose(Function.identity())
        ).thenCompose(Function.identity());
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
        return fac.new0(cf.whenComplete(action));
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
        return fac.new0(cf.whenCompleteAsync(action, fac.defaultExecutor));
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
    public Cffu<T> whenCompleteAsync(
            BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return fac.new0(cf.whenCompleteAsync(action, executor));
    }

    /**
     * Returns a new Cffu that, when this stage completes either normally or exceptionally,
     * is executed with this stage's result and exception as arguments to the supplied function.
     * <p>
     * When this stage is complete, the given function is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this stage as arguments,
     * and the function's result is used to complete the returned stage.
     *
     * @param fn  the function to use to compute the value of the returned Cffu
     * @param <U> the function's return type
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return fac.new0(cf.handle(fn));
    }

    /**
     * Returns a new Cffu that, when this stage completes either normally or exceptionally,
     * is executed using {@link #defaultExecutor()},
     * with this stage's result and exception as arguments to the supplied function.
     * <p>
     * When this stage is complete, the given function is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this stage as arguments,
     * and the function's result is used to complete the returned stage.
     *
     * @param fn  the function to use to compute the value of the returned Cffu
     * @param <U> the function's return type
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return fac.new0(cf.handleAsync(fn, fac.defaultExecutor));
    }

    /**
     * Returns a new Cffu that, when this stage completes either normally or exceptionally,
     * is executed using the supplied executor, with this stage's result and exception
     * as arguments to the supplied function.
     * <p>
     * When this stage is complete, the given function is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of this stage as arguments,
     * and the function's result is used to complete the returned stage.
     *
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new Cffu
     */
    @Override
    public <U> Cffu<U> handleAsync(
            BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return fac.new0(cf.handleAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Read(explicitly) methods
    //
    //    - get()
    //    - get(timeout, unit)
    //    - join()
    //    - getNow(T valueIfAbsent)
    //    - resultNow()
    //    - exceptionNow()
    //
    //    - isDone()
    //    - isCompletedExceptionally()
    //    - isCancelled()
    //    - state()
    //
    // NOTE:
    //   - about ExecutionException or CompletionException when the computation threw an exception:
    //     - get methods throw ExecutionException(checked exception)
    //       these old methods existed in `Future` interface since Java 5
    //     - getNow/join throw CompletionException(unchecked exception),
    //       these new methods existed in `CompletableFuture` since Java 8
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an exception
     * @throws InterruptedException  if the current thread was interrupted while waiting
     */
    @Nullable
    @Override
    public T get() throws InterruptedException, ExecutionException {
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
     */
    @Nullable
    @Override
    public T get(long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return cf.get(timeout, unit);
    }

    /**
     * Returns the result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * To better conform with the use of common functional forms, if a computation involved in the completion
     * of this Cffu threw an exception, this method throws an (unchecked) {@link CompletionException}
     * with the underlying exception as its cause.
     *
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally
     *                               or a completion computation threw an exception
     */
    @Nullable
    public T join() {
        return cf.join();
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
     */
    @Nullable
    public T getNow(T valueIfAbsent) {
        return cf.getNow(valueIfAbsent);
    }

    /**
     * Returns the computed result, without waiting.
     * <p>
     * This method is for cases where the caller knows that the task has already completed successfully,
     * for example when filtering a stream of Future objects for the successful tasks
     * and using a mapping operation to obtain a stream of results.
     * <pre>{@code results = futures.stream()
     *     .filter(f -> f.state() == Future.State.SUCCESS)
     *     .map(Future::resultNow)
     *     .toList();
     * }</pre>
     */
    @Nullable
    @Override
    public T resultNow() {
        if (IS_JAVA19_PLUS) {
            return cf.resultNow();
        }

        // below code is copied from java.util.concurrent.Future.resultNow

        if (!isDone()) throw new IllegalStateException("Task has not completed");
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return get();
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
    @Override
    public Throwable exceptionNow() {
        if (IS_JAVA19_PLUS) {
            return cf.exceptionNow();
        }

        // below code is copied from java.util.concurrent.Future.exceptionNow

        if (!isDone()) throw new IllegalStateException("Task has not completed");
        if (isCancelled()) throw new IllegalStateException("Task was cancelled");

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    get();
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
        return cf.isCancelled();
    }

    /**
     * @return the computation state
     */
    @Contract(pure = true)
    @Override
    public State state() {
        // CompletableFuture.state is new method since Java 19,
        // should need compatibility logic of Java version.
        // But the return type `State` is also added since Java 19,
        // so it's IMPOSSIBLE to work by compatibility logic of wrapped class(`Cffu`).
        //
        // just invoke without compatibility logic~
        return cf.state();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Write methods
    //
    //    - complete*()
    //    - completeExceptionally(ex)
    //    - cancel()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * If not already completed, sets the value returned by {@link #get()} and related methods to the given value.
     *
     * @param value the result value
     * @return {@code true} if this invocation caused this Cffu to transition to a completed state, else {@code false}
     */
    public boolean complete(@Nullable T value) {
        return cf.complete(value);
    }

    /**
     * Completes this Cffu with the result of the given Supplier function invoked
     * from an asynchronous task using the default executor.
     *
     * @param supplier a function returning the value to be used to complete this Cffu
     * @return this Cffu
     */
    public Cffu<T> completeAsync(Supplier<? extends T> supplier) {
        return completeAsync(supplier, fac.defaultExecutor);
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
        if (IS_JAVA9_PLUS) {
            cf.completeAsync(supplier, executor);
        } else {
            requireNonNull(supplier, "supplier is null");
            requireNonNull(executor, "executor is null");
            executor.execute(new AsyncSupply<>(cf, supplier));
        }
        return this;
    }

    /**
     * code is copied from {@code CompletableFuture.AsyncSupply} with small adoption.
     */
    @SuppressWarnings({"NonSerializableFieldInSerializableClass", "serial"})
    @SuppressFBWarnings("SE_BAD_FIELD")
    private static final class AsyncSupply<T> extends ForkJoinTask<Void>
            implements Runnable, CompletableFuture.AsynchronousCompletionTask {
        CompletableFuture<T> dep;
        Supplier<? extends T> fn;

        AsyncSupply(CompletableFuture<T> dep, Supplier<? extends T> fn) {
            this.dep = dep;
            this.fn = fn;
        }

        @Override
        public Void getRawResult() {
            return null;
        }

        @Override
        public void setRawResult(Void v) {
        }

        @Override
        public boolean exec() {
            run();
            return false;
        }

        @Override
        public void run() {
            CompletableFuture<T> d;
            Supplier<? extends T> f;
            if ((d = dep) != null && (f = fn) != null) {
                dep = null;
                fn = null;
                if (!d.isDone()) {
                    try {
                        d.complete(f.get());
                    } catch (Throwable ex) {
                        d.completeExceptionally(ex);
                    }
                }
            }
        }
    }

    /**
     * If not already completed, causes invocations of {@link #get()} and related methods to throw the given exception.
     *
     * @param ex the exception
     * @return {@code true} if this invocation caused this Cffu to transition to a completed state, else {@code false}
     */
    public boolean completeExceptionally(Throwable ex) {
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
        return cf.cancel(mayInterruptIfRunning);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //    vs. user functional API
    //
    //    - toCompletableFuture()
    //    - copy()
    //
    //    - obtrudeValue(value)
    //    - obtrudeException(ex)
    //
    //    - defaultExecutor()
    //    - getNumberOfDependents()
    //
    //    - minimalCompletionStage()
    //    - newIncompleteFuture()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns underneath wrapped CompletableFuture.
     *
     * @return underneath wrapped CompletableFuture
     */
    @Contract(pure = true)
    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> toCompletableFuture() {
        return cf;
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
        if (IS_JAVA9_PLUS) {
            return fac.new0(cf.copy());
        }
        return thenApply(Function.identity());
    }

    /**
     * Forcibly sets or resets the value subsequently returned by method {@link #get()} and related methods,
     * whether or not already completed. This method is designed for use only in error recovery actions,
     * and even in such situations may result in ongoing dependent completions using established versus overwritten outcomes.
     *
     * @param value the completion value
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     * @see CffuFactory#isForbidObtrudeMethods()
     */
    public void obtrudeValue(@Nullable T value) {
        if (fac.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeValue is forbidden by cffu");
        }
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
     * @see CffuFactory#isForbidObtrudeMethods()
     */
    public void obtrudeException(Throwable ex) {
        if (fac.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeException is forbidden by cffu");
        }
        cf.obtrudeException(ex);
    }

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * This class uses the {@code CffuFactory.defaultExecutor}
     * config by {@link CffuFactoryBuilder#newCffuFactoryBuilder(Executor)}.
     *
     * @return the executor
     * @see CffuFactoryBuilder#newCffuFactoryBuilder(Executor)
     * @see CffuFactory#getDefaultExecutor()
     */
    @Contract(pure = true)
    public Executor defaultExecutor() {
        return fac.defaultExecutor;
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
        return cf.getNumberOfDependents();
    }

    /**
     * Just return a normal CompletableFuture for API compatibility,
     * NOT a *minimal* CompletionStage.
     *
     * @see CompletableFuture#minimalCompletionStage()
     */
    @Contract(pure = true)
    public CompletionStage<T> minimalCompletionStage() {
        if (IS_JAVA9_PLUS) {
            return fac.new0((CompletableFuture<T>) cf.minimalCompletionStage());
        }
        return fac.incomplete();
    }

    /**
     * Just return a normal CompletableFuture for API compatibility.
     *
     * @see CompletableFuture#newIncompleteFuture()
     */
    @Contract(pure = true)
    public <U> Cffu<U> newIncompleteFuture() {
        if (IS_JAVA9_PLUS) {
            return fac.new0(cf.newIncompleteFuture());
        }
        return fac.incomplete();
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
}
