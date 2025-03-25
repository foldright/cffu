package io.foldright.cffu;

import com.google.common.util.concurrent.Futures;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.*;

import static io.foldright.cffu.CffuFactoryBuilder.cffuScreened;
import static io.foldright.cffu.CffuFactoryBuilder.cffuUnscreened;
import static java.util.Objects.requireNonNull;


/**
 * This class {@link Cffu} is the equivalent class to {@link CompletableFuture},
 * contains the equivalent instance methods of {@link CompletionStage} and {@link CompletableFuture}.
 * <p>
 * The methods that equivalent to static factory methods of {@link CompletableFuture}
 * is in {@link CffuFactory} class.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author HuHao (995483610 at qq dot com)
 * @see CffuFactoryBuilder
 * @see CffuFactory
 * @see CompletionStage
 * @see CompletableFuture
 */
public final class Cffu<T> implements Future<T>, CompletionStage<T> {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal constructor and fields
    ////////////////////////////////////////////////////////////////////////////////

    private final CffuFactory fac;

    private final boolean isMinimalStage;

    private final CompletableFuture<T> cf;

    @Contract(pure = true)
    Cffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<T> cf) {
        this.fac = requireNonNull(cffuFactory, "cffuFactory is null");
        this.isMinimalStage = isMinimalStage;
        this.cf = requireNonNull(cf, "cf is null");
    }

    @Contract(pure = true)
    private <U> Cffu<U> resetCf(CompletableFuture<U> cf) {
        return new Cffu<>(fac, isMinimalStage, cf);
    }

    @Contract(pure = true)
    private <U> CompletionStage<U> resetToMin(CompletableFuture<U> cf) {
        return new Cffu<>(fac, true, cf);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Simple then* Methods of CompletionStage
    //
    //    - thenApply* (Function: T -> U)       -> Cffu<U>
    //    - thenAccept*(Consumer: T -> Void)    -> Cffu<Void>
    //    - thenRun*   (Runnable: Void -> Void) -> Cffu<Void>
    ////////////////////////////////////////////////////////////////////////////////

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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAccept`")
    @Override
    public <U> Cffu<U> thenApply(Function<? super T, ? extends U> fn) {
        return resetCf(cf.thenApply(fn));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptAsync`")
    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        return thenApplyAsync(fn, fac.defaultExecutor);
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptAsync`")
    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return resetCf(cf.thenApplyAsync(fn, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed with this stage's result as the argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> thenAccept(Consumer<? super T> action) {
        return resetCf(cf.thenAccept(action));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed using {@link #defaultExecutor()},
     * with this stage's result as the argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> thenAcceptAsync(Consumer<? super T> action) {
        return thenAcceptAsync(action, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * is executed using the supplied Executor, with this stage's result as the argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     */
    @Override
    public Cffu<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return resetCf(cf.thenAcceptAsync(action, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally, executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> thenRun(Runnable action) {
        return resetCf(cf.thenRun(action));
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> thenRunAsync(Runnable action) {
        return thenRunAsync(action, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this stage completes normally,
     * executes the given action using the supplied Executor.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> thenRunAsync(Runnable action, Executor executor) {
        return resetCf(cf.thenRunAsync(action, cffuScreened(executor)));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region# Then-Multi-Actions(thenM*) Methods
    //
    //    - thenMApply* (Function[]: T -> U)       -> Cffu<List<U>>
    //    - thenMAccept*(Consumer[]: T -> Void)    -> Cffu<Void>
    //    - thenMRun*   (Runnable[]: Void -> Void) -> Cffu<Void>
    ////////////////////////////////////////////////////////////

    /**
     * Shortcut to method {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf},
     * wraps input functions to Cffu by {@link CffuFactory#supplyAsync(Supplier)};
     * The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyFailFastAsync(Function<? super T, ? extends U>... fns) {
        return thenMApplyFailFastAsync(fac.defaultExecutor, fns);
    }

    /**
     * Shortcut to method {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf},
     * wraps input functions to Cffu by {@link CffuFactory#supplyAsync(Supplier, Executor)};
     * The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyFailFastAsync(Executor executor, Function<? super T, ? extends U>... fns) {
        return resetCf(CompletableFutureUtils.thenMApplyFailFastAsync(cf, cffuScreened(executor), fns));
    }

    /**
     * Shortcut to method {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf},
     * wraps input functions to Cffu by {@link CffuFactory#supplyAsync(Supplier)};
     * The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyAllSuccessAsync(
            @Nullable U valueIfFailed, Function<? super T, ? extends U>... fns) {
        return thenMApplyAllSuccessAsync(fac.defaultExecutor, valueIfFailed, fns);
    }

    /**
     * Shortcut to method {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf},
     * wraps input functions to Cffu by {@link CffuFactory#supplyAsync(Supplier, Executor)};
     * The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyAllSuccessAsync(
            Executor executor, @Nullable U valueIfFailed, Function<? super T, ? extends U>... fns) {
        return resetCf(CompletableFutureUtils.thenMApplyAllSuccessAsync(cf, cffuScreened(executor), valueIfFailed, fns));
    }

    /**
     * Shortcut to method {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf},
     * wraps input functions to Cffu by {@link CffuFactory#supplyAsync(Supplier)};
     * The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyMostSuccessAsync(
            @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit, Function<? super T, ? extends U>... fns) {
        return thenMApplyMostSuccessAsync(fac.defaultExecutor, valueIfNotSuccess, timeout, unit, fns);
    }

    /**
     * Shortcut to method {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf},
     * wraps input functions to Cffu by {@link CffuFactory#supplyAsync(Supplier, Executor)};
     * The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyMostSuccessAsync(
            Executor executor, @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit,
            Function<? super T, ? extends U>... fns) {
        return resetCf(CompletableFutureUtils.thenMApplyMostSuccessAsync(
                cf, cffuScreened(executor), valueIfNotSuccess, timeout, unit, fns));
    }

    /**
     * Shortcut to method {@link CffuFactory#allResultsOf allResultsOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#allResultsOf allResultsOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyAsync(Function<? super T, ? extends U>... fns) {
        return thenMApplyAsync(fac.defaultExecutor, fns);
    }

    /**
     * Shortcut to method {@link CffuFactory#allResultsOf allResultsOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#allResultsOf allResultsOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<List<U>> thenMApplyAsync(Executor executor, Function<? super T, ? extends U>... fns) {
        return resetCf(CompletableFutureUtils.thenMApplyAsync(cf, cffuScreened(executor), fns));
    }

    /**
     * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<U> thenMApplyAnySuccessAsync(Function<? super T, ? extends U>... fns) {
        return thenMApplyAnySuccessAsync(fac.defaultExecutor, fns);
    }

    /**
     * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<U> thenMApplyAnySuccessAsync(Executor executor, Function<? super T, ? extends U>... fns) {
        return resetCf(CompletableFutureUtils.thenMApplyAnySuccessAsync(cf, cffuScreened(executor), fns));
    }

    /**
     * Shortcut to method {@link CffuFactory#anyOf anyOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<U> thenMApplyAnyAsync(Function<? super T, ? extends U>... fns) {
        return thenMApplyAnyAsync(fac.defaultExecutor, fns);
    }

    /**
     * Shortcut to method {@link CffuFactory#anyOf anyOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of functions.
     * <p>
     * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the {@code executor} argument is passed by lambda, the {@code Runnable} lambda parameter type
     * need be declared to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/0367d8a2-c3bd-414b-9f9a-4eaf64a16f96" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final <U> Cffu<U> thenMApplyAnyAsync(Executor executor, Function<? super T, ? extends U>... fns) {
        return resetCf(CompletableFutureUtils.thenMApplyAnyAsync(cf, cffuScreened(executor), fns));
    }

    /**
     * Shortcut to method {@link CffuFactory#allFailFastOf allFailFastOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#allFailFastOf allFailFastOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final Cffu<Void> thenMAcceptFailFastAsync(Consumer<? super T>... actions) {
        return thenMAcceptFailFastAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#allFailFastOf allFailFastOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#allFailFastOf allFailFastOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final Cffu<Void> thenMAcceptFailFastAsync(Executor executor, Consumer<? super T>... actions) {
        return resetCf(CompletableFutureUtils.thenMAcceptFailFastAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#allOf allOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#allOf allOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @SafeVarargs
    public final Cffu<Void> thenMAcceptAsync(Consumer<? super T>... actions) {
        return thenMAcceptAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#allOf allOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#allOf allOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @SafeVarargs
    public final Cffu<Void> thenMAcceptAsync(Executor executor, Consumer<? super T>... actions) {
        return resetCf(CompletableFutureUtils.thenMAcceptAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final Cffu<Void> thenMAcceptAnySuccessAsync(Consumer<? super T>... actions) {
        return thenMAcceptAnySuccessAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final Cffu<Void> thenMAcceptAnySuccessAsync(Executor executor, Consumer<? super T>... actions) {
        return resetCf(CompletableFutureUtils.thenMAcceptAnySuccessAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#anyOf anyOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final Cffu<Void> thenMAcceptAnyAsync(Consumer<? super T>... actions) {
        return thenMAcceptAnyAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#anyOf anyOf}, wraps input functions to Cffu by
     * {@link CffuFactory#supplyAsync(Supplier, Executor)}; The given stage's result is used as the argument of actions.
     * <p>
     * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
     * <p>
     * <strong>NOTE:</strong> if the first argument is passed by lambda, need declare the lambda parameter type
     * to avoid the compilation error, more info see <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/LambdaCompilationErrorSolutionOfMultipleActionsMethodsDemo.java">
     * the demo code</a><br><img src="https://github.com/user-attachments/assets/4952e8e1-20af-4967-a4a7-b8885b816203" alt="demo code" />
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    @SafeVarargs
    public final Cffu<Void> thenMAcceptAnyAsync(Executor executor, Consumer<? super T>... actions) {
        return resetCf(CompletableFutureUtils.thenMAcceptAnyAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#allFailFastOf allFailFastOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable)}.
     * <p>
     * See the {@link CffuFactory#allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMRunAsync`")
    public Cffu<Void> thenMRunFailFastAsync(Runnable... actions) {
        return thenMRunFailFastAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#allFailFastOf allFailFastOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CffuFactory#allFailFastOf allFailFastOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMRunAsync`")
    public Cffu<Void> thenMRunFailFastAsync(Executor executor, Runnable... actions) {
        return resetCf(CompletableFutureUtils.thenMRunFailFastAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#allOf allOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable)}.
     * <p>
     * See the {@link CffuFactory#allOf allOf} documentation for the rules of result computation.
     */
    public Cffu<Void> thenMRunAsync(Runnable... actions) {
        return thenMRunAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#allOf allOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CffuFactory#allOf allOf} documentation for the rules of result computation.
     */
    public Cffu<Void> thenMRunAsync(Executor executor, Runnable... actions) {
        return resetCf(CompletableFutureUtils.thenMRunAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable)}.
     * <p>
     * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMRunAsync`")
    public Cffu<Void> thenMRunAnySuccessAsync(Runnable... actions) {
        return thenMRunAnySuccessAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMRunAsync`")
    public Cffu<Void> thenMRunAnySuccessAsync(Executor executor, Runnable... actions) {
        return resetCf(CompletableFutureUtils.thenMRunAnySuccessAsync(cf, cffuScreened(executor), actions));
    }

    /**
     * Shortcut to method {@link CffuFactory#anyOf anyOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable)}.
     * <p>
     * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMRunAsync`")
    public Cffu<Void> thenMRunAnyAsync(Runnable... actions) {
        return thenMRunAnyAsync(fac.defaultExecutor, actions);
    }

    /**
     * Shortcut to method {@link CffuFactory#anyOf anyOf},
     * wraps input actions to Cffu by {@link CffuFactory#runAsync(Runnable, Executor)}.
     * <p>
     * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMRunAsync`")
    public Cffu<Void> thenMRunAnyAsync(Executor executor, Runnable... actions) {
        return resetCf(CompletableFutureUtils.thenMRunAnyAsync(cf, cffuScreened(executor), actions));
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions-Tuple(thenMTuple*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleFailFastAsync(fac.defaultExecutor, fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleFailFastAsync(cf, cffuScreened(executor), fn1, fn2));
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleFailFastAsync(fac.defaultExecutor, fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleFailFastAsync(cf, cffuScreened(executor), fn1, fn2, fn3));
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleFailFastAsync(fac.defaultExecutor, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleFailFastAsync(cf, cffuScreened(executor), fn1, fn2, fn3, fn4));
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleFailFastAsync(fac.defaultExecutor, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyFailFastAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleFailFastAsync(cf, cffuScreened(executor), fn1, fn2, fn3, fn4, fn5));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyAllSuccessTupleAsync(fac.defaultExecutor, fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Executor, Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return resetCf(CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cf, cffuScreened(executor), fn1, fn2));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyAllSuccessTupleAsync(fac.defaultExecutor, fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Executor, Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return resetCf(CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cf, cffuScreened(executor), fn1, fn2, fn3));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyAllSuccessTupleAsync(fac.defaultExecutor, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Executor, Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return resetCf(CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cf, cffuScreened(executor), fn1, fn2, fn3, fn4));
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyAllSuccessTupleAsync(fac.defaultExecutor, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyAllSuccessAsync(Executor, Object, Function[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return resetCf(CompletableFutureUtils.thenMApplyAllSuccessTupleAsync(cf, cffuScreened(executor), fn1, fn2, fn3, fn4, fn5));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyMostSuccessTupleAsync(fac.defaultExecutor, timeout, unit, fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return resetCf(CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cf, cffuScreened(executor), timeout, unit, fn1, fn2));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyMostSuccessTupleAsync(fac.defaultExecutor, timeout, unit, fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return resetCf(CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cf, cffuScreened(executor), timeout, unit, fn1, fn2, fn3));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyMostSuccessTupleAsync(fac.defaultExecutor, timeout, unit, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return resetCf(CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cf, cffuScreened(executor), timeout, unit, fn1, fn2, fn3, fn4));
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyMostSuccessTupleAsync(fac.defaultExecutor, timeout, unit, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyMostSuccessAsync(Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return resetCf(CompletableFutureUtils.thenMApplyMostSuccessTupleAsync(
                cf, cffuScreened(executor), timeout, unit, fn1, fn2, fn3, fn4, fn5));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyTupleAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleAsync(fac.defaultExecutor, fn1, fn2);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2> Cffu<Tuple2<U1, U2>> thenMApplyTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleAsync(cf, cffuScreened(executor), fn1, fn2));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleAsync(fac.defaultExecutor, fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3> Cffu<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleAsync(cf, cffuScreened(executor), fn1, fn2, fn3));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleAsync(fac.defaultExecutor, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4> Cffu<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleAsync(cf, cffuScreened(executor), fn1, fn2, fn3, fn4));
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4,
            Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleAsync(fac.defaultExecutor, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link #thenMApplyAsync(Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenMAcceptAsync`")
    public <U1, U2, U3, U4, U5> Cffu<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            Executor executor, Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4,
            Function<? super T, ? extends U5> fn5) {
        return resetCf(CompletableFutureUtils.thenMApplyTupleAsync(cf, cffuScreened(executor), fn1, fn2, fn3, fn4, fn5));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# thenBoth* Methods(binary input) of CompletionStage
    //
    //    - thenCombine*   (BiFunction: (T, U) -> V)    -> Cffu<V>
    //    - thenAcceptBoth*(BiConsumer: (T, U) -> Void) -> Cffu<Void>
    //    - runAfterBoth*  (Runnable:   Void -> Void)   -> Cffu<Void>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothFailFast`")
    public <U, V> Cffu<V> thenCombineFailFast(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return resetCf(CompletableFutureUtils.thenCombineFailFast(cf, other, fn));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()}, with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the type of the other CompletionStage's result
     * @param <V>   the function's return type
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothFailFastAsync`")
    public <U, V> Cffu<V> thenCombineFailFastAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineFailFastAsync(other, fn, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using the supplied executor, with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other    the other CompletionStage
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the other CompletionStage's result
     * @param <V>      the function's return type
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothFailFastAsync`")
    public <U, V> Cffu<V> thenCombineFailFastAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return resetCf(CompletableFutureUtils.thenCombineFailFastAsync(cf, other, fn, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @param <U>    the type of the other CompletionStage's result
     */
    public <U> Cffu<Void> thenAcceptBothFailFast(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return resetCf(CompletableFutureUtils.thenAcceptBothFailFast(cf, other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using {@link #defaultExecutor()}, with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     * @param <U>    the type of the other CompletionStage's result
     */
    public <U> Cffu<Void> thenAcceptBothFailFastAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothFailFastAsync(other, action, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * is executed using the supplied executor, with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the type of the other CompletionStage's result
     */
    public <U> Cffu<Void> thenAcceptBothFailFastAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return resetCf(CompletableFutureUtils.thenAcceptBothFailFastAsync(cf, other, action, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally, executes the given action.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    public Cffu<Void> runAfterBothFailFast(CompletionStage<?> other, Runnable action) {
        return resetCf(CompletableFutureUtils.runAfterBothFailFast(cf, other, action));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    public Cffu<Void> runAfterBothFailFastAsync(CompletionStage<?> other, Runnable action) {
        return runAfterBothFailFastAsync(other, action, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally,
     * executes the given action using the supplied executor.
     * if any of the given stage complete exceptionally, then the returned Cffu also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     */
    public Cffu<Void> runAfterBothFailFastAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return resetCf(CompletableFutureUtils.runAfterBothFailFastAsync(cf, other, action, cffuScreened(executor)));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBoth`")
    @Override
    public <U, V> Cffu<V> thenCombine(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return resetCf(cf.thenCombine(other, fn));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothAsync`")
    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return thenCombineAsync(other, fn, fac.defaultExecutor);
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `thenAcceptBothAsync`")
    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return resetCf(cf.thenCombineAsync(other, fn, cffuScreened(executor)));
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
     */
    @Override
    public <U> Cffu<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return resetCf(cf.thenAcceptBoth(other, action));
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
     */
    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return thenAcceptBothAsync(other, action, fac.defaultExecutor);
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
     */
    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return resetCf(cf.thenAcceptBothAsync(other, action, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this and the other given stage both complete normally, executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return resetCf(cf.runAfterBoth(other, action));
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
     */
    @Override
    public Cffu<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return runAfterBothAsync(other, action, fac.defaultExecutor);
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
     */
    @Override
    public Cffu<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return resetCf(cf.runAfterBothAsync(other, action, cffuScreened(executor)));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# thenEither* Methods(binary input) of CompletionStage
    //
    //    - applyToEither* (Function: (T) -> U)     -> Cffu<U>
    //    - acceptEither*  (Consumer: (T) -> Void)  -> Cffu<Void>
    //    - runAfterEither*(Runnable: Void -> Void) -> Cffu<Void>
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied function.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the function's return type
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherSuccess`")
    public <U> Cffu<U> applyToEitherSuccess(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return resetCf(CompletableFutureUtils.applyToEitherSuccess(cf, other, fn));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the corresponding result as argument to the supplied function.
     *
     * @param other the other CompletionStage
     * @param fn    the function to use to compute the value of the returned Cffu
     * @param <U>   the function's return type
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherSuccessAsync`")
    public <U> Cffu<U> applyToEitherSuccessAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEitherSuccessAsync(other, fn, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     *
     * @param other    the other CompletionStage
     * @param fn       the function to use to compute the value of the returned Cffu
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherSuccessAsync`")
    public <U> Cffu<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
        return resetCf(CompletableFutureUtils.applyToEitherSuccessAsync(cf, other, fn, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied action.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    public Cffu<Void> acceptEitherSuccess(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return resetCf(CompletableFutureUtils.acceptEitherSuccess(cf, other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using {@link #defaultExecutor()},
     * with the corresponding result as argument to the supplied action.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    public Cffu<Void> acceptEitherSuccessAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherSuccessAsync(other, action, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     */
    public Cffu<Void> acceptEitherSuccessAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return resetCf(CompletableFutureUtils.acceptEitherSuccessAsync(cf, other, action, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally, executes the given action.
     * Otherwise, all two complete exceptionally, the returned Cffu also does so,
     * with a CompletionException holding an exception from any of as its cause.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    public Cffu<Void> runAfterEitherSuccess(CompletionStage<?> other, Runnable action) {
        return resetCf(CompletableFutureUtils.runAfterEitherSuccess(cf, other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * Otherwise, all two complete exceptionally, the returned Cffu also does so,
     * with a CompletionException holding an exception from any of as its cause.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    public Cffu<Void> runAfterEitherSuccessAsync(CompletionStage<?> other, Runnable action) {
        return runAfterEitherSuccessAsync(other, action, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using the supplied executor.
     * Otherwise, all two complete exceptionally, the returned Cffu also does so,
     * with a CompletionException holding an exception from any of as its cause.
     *
     * @param other    the other CompletionStage
     * @param action   the action to perform before completing the returned Cffu
     * @param executor the executor to use for asynchronous execution
     */
    public Cffu<Void> runAfterEitherSuccessAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return resetCf(CompletableFutureUtils.runAfterEitherSuccessAsync(cf, other, action, cffuScreened(executor)));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEither`")
    @Override
    public <U> Cffu<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return resetCf(cf.applyToEither(other, fn));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherAsync`")
    @Override
    public <U> Cffu<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return applyToEitherAsync(other, fn, fac.defaultExecutor);
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `acceptEitherAsync`")
    @Override
    public <U> Cffu<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
        return resetCf(cf.applyToEitherAsync(other, fn, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * is executed with the corresponding result as argument to the supplied action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return resetCf(cf.acceptEither(other, action));
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
     */
    @Override
    public Cffu<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return acceptEitherAsync(other, action, fac.defaultExecutor);
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
     */
    @Override
    public Cffu<Void> acceptEitherAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return resetCf(cf.acceptEitherAsync(other, action, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return resetCf(cf.runAfterEither(other, action));
    }

    /**
     * Returns a new Cffu that, when either this or the other given stage complete normally,
     * executes the given action using {@link #defaultExecutor()}.
     * <p>
     * See the {@link CompletionStage} documentation for rules covering exceptional completion.
     *
     * @param other  the other CompletionStage
     * @param action the action to perform before completing the returned Cffu
     */
    @Override
    public Cffu<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return runAfterEitherAsync(other, action, fac.defaultExecutor);
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
     */
    @Override
    public Cffu<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return resetCf(cf.runAfterEitherAsync(other, action, cffuScreened(executor)));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Error Handling Methods of CompletionStage
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that, when this Cffu completes exceptionally with the given exceptionType,
     * is executed with the exception from this Cffu as the argument to the supplied function.
     * Otherwise, the returned Cffu contains same result as this Cffu.
     * <p>
     * <strong>"The exception from this Cffu"</strong> means the cause of the {@link ExecutionException}
     * thrown by {@code get()} or, if {@code get()} throws a different kind of exception, that exception itself;
     * aka the exception is unwrapped by {@link CompletableFutureUtils#unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the exception from this Cffu. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if this Cffu fails with the expected exception type.
     *                      The function's argument is the exception from this Cffu.
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     * @see Futures#catching the equivalent Guava method catching()
     */
    public <X extends Throwable> Cffu<T> catching(Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        return resetCf(CompletableFutureUtils.catching(cf, exceptionType, fallback));
    }

    /**
     * Returns a new Cffu that, when this Cffu completes exceptionally with the given exceptionType,
     * is executed with the exception from this Cffu as the argument to the supplied function, using {@link #defaultExecutor()}.
     * Otherwise, the returned Cffu contains same result as this Cffu.
     * <p>
     * <strong>"The exception from this Cffu"</strong> means the cause of the {@link ExecutionException}
     * thrown by {@code get()} or, if {@code get()} throws a different kind of exception, that exception itself;
     * aka the exception is unwrapped by {@link CompletableFutureUtils#unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the exception from this Cffu. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if this Cffu fails with the expected exception type.
     *                      The function's argument is the exception from this Cffu.
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     * @see Futures#catching the equivalent Guava method catching()
     */
    public <X extends Throwable> Cffu<T> catchingAsync(
            Class<X> exceptionType, Function<? super X, ? extends T> fallback) {
        return catchingAsync(exceptionType, fallback, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this Cffu completes exceptionally with the given exceptionType,
     * is executed with the exception from this Cffu as the argument to the supplied function, using the supplied Executor.
     * Otherwise, the returned Cffu contains same result as this Cffu.
     * <p>
     * <strong>"The exception from this Cffu"</strong> means the cause of the {@link ExecutionException}
     * thrown by {@code get()} or, if {@code get()} throws a different kind of exception, that exception itself;
     * aka the exception is unwrapped by {@link CompletableFutureUtils#unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the exception from this Cffu. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if this Cffu fails with the expected exception type.
     *                      The function's argument is the exception from this Cffu.
     * @param executor      the executor to use for asynchronous execution
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     * @see Futures#catching the equivalent Guava method catching()
     */
    public <X extends Throwable> Cffu<T> catchingAsync(
            Class<X> exceptionType, Function<? super X, ? extends T> fallback, Executor executor) {
        return resetCf(CompletableFutureUtils.catchingAsync(cf, exceptionType, fallback, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is executed with this stage's exception as the argument to the supplied function.
     * Otherwise, if this stage completes normally,
     * then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catching(Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the value of the returned Cffu
     *           if this Cffu completed exceptionally
     * @see #catching(Class, Function)
     */
    @Override
    public Cffu<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return resetCf(cf.exceptionally(fn));
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is executed with this stage's exception as the argument to the supplied function,
     * using {@link #defaultExecutor()}.
     * Otherwise, if this stage completes normally,
     * then the returned stage also completes normally with the same value.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingAsync(Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the value of the returned Cffu
     *           if this Cffu completed exceptionally
     * @see #catchingAsync(Class, Function)
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
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingAsync(Class, Function, Executor)}
     * instead in your business application.
     *
     * @param fn       the function to use to compute the value of the returned Cffu
     *                 if this Cffu completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @see #catchingAsync(Class, Function, Executor)
     */
    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) {
        return resetCf(CompletableFutureUtils.exceptionallyAsync(cf, fn, cffuScreened(executor)));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Timeout Control Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new Cffu that is completed exceptionally with a {@link TimeoutException}
     * when this Cffu is not completed before the given timeout; otherwise the returned
     * Cffu completed with the same successful result or exception of this Cffu.
     * <p>
     * Uses {@link #defaultExecutor()} as {@code executorWhenTimeout}.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new Cffu instead of this Cffu to avoid the subsequent usage of the
     * delay thread; This behavior is DIFFERENT from the original CF method {@link CompletableFuture#orTimeout} and its
     * backport method {@link #unsafeOrTimeout}. More info see the javadoc of {@link #unsafeOrTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     */
    public Cffu<T> orTimeout(long timeout, TimeUnit unit) {
        checkMinimalStage();
        return resetCf(CompletableFutureUtils.cffuOrTimeout(cf, timeout, unit, fac.defaultExecutor));
    }

    /**
     * Exceptionally completes this Cffu with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method is <strong>UNSAFE</strong>!
     * <p>
     * When the wait timed out, the subsequent non-async actions of the dependent Cffus/CompletableFutures are performed
     * in CompletableFuture's internal <strong>SINGLE-thread delay executor</strong> (including timeout functionality).
     * This means that the long-running subsequent non-async actions will block this executor thread, preventing it from
     * handling other timeouts and delays, effectively breaking CompletableFuture's timeout and delay functionality.
     * <p>
     * <strong>Strongly recommend</strong> using the safe method {@link #orTimeout(long, TimeUnit)} instead of this method.
     * Using this method is appropriate only when either:
     * <ul>
     * <li>the returned Cffu is read in a blocking manner, or
     * <li>all subsequent actions of dependent Cffus/CompletableFutures are guaranteed to execute asynchronously
     *    (i.e., the dependent Cffus/CompletableFutures are created using async methods).
     * </ul> In these cases, using these methods avoids an unnecessary thread switch when timeout occurs; However,
     * these conditions are difficult to guarantee in practice especially when the returned Cffu is used by other code.
     * <p>
     * Note: Before Java 21(Java 20-), {@link CompletableFuture#orTimeout} leaks if the future completes exceptionally,
     * more info see <a href="https://bugs.openjdk.org/browse/JDK-8303742">issue JDK-8303742</a>,
     * <a href="https://github.com/openjdk/jdk/pull/13059">PR review openjdk/jdk/13059</a>
     * and <a href="https://github.com/openjdk/jdk/commit/ded6a8131970ac2f7ae59716769e6f6bae3b809a">JDK bugfix commit</a>.
     * The cffu backport logic(for Java 20-) has merged this JDK bugfix.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return this Cffu
     * @see #orTimeout(long, TimeUnit)
     */
    @Contract("_, _ -> this")
    public Cffu<T> unsafeOrTimeout(long timeout, TimeUnit unit) {
        checkMinimalStage();
        CompletableFutureUtils.orTimeout(cf, timeout, unit);
        return this;
    }

    /**
     * Returns a new Cffu that is completed normally with the given value
     * when this Cffu is not completed before the given timeout; otherwise the returned
     * Cffu completed with the same successful result or exception of this Cffu.
     * <p>
     * Uses {@link #defaultExecutor()} as {@code executorWhenTimeout}.
     * <p>
     * <strong>CAUTION:</strong> This method returns a new Cffu instead of this Cffu
     * to avoid the subsequent usage of the delay thread; This behavior is DIFFERENT from the original CF method
     * {@link CompletableFuture#completeOnTimeout} and its backport method {@link #unsafeCompleteOnTimeout}.
     * More info see the javadoc of {@link #unsafeCompleteOnTimeout} and the demo <a href=
     * "https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java"
     * >DelayDysfunctionDemo</a>.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     */
    public Cffu<T> completeOnTimeout(@Nullable T value, long timeout, TimeUnit unit) {
        checkMinimalStage();
        return resetCf(CompletableFutureUtils.cffuCompleteOnTimeout(cf, value, timeout, unit, fac.defaultExecutor));
    }

    /**
     * Completes this Cffu with the given value if not otherwise completed before the given timeout.
     * <p>
     * <strong>CAUTION:</strong> This method is <strong>UNSAFE</strong>!
     * <p>
     * When the wait timed out, the subsequent non-async actions of the dependent Cffus/CompletableFutures are performed
     * in CompletableFuture's internal <strong>SINGLE-thread delay executor</strong> (including timeout functionality).
     * This means that the long-running subsequent non-async actions will block this executor thread, preventing it from
     * handling other timeouts and delays, effectively breaking CompletableFuture's timeout and delay functionality.
     * <p>
     * <strong>Strongly recommend</strong> using the safe method {@link #completeOnTimeout(Object, long, TimeUnit)}
     * instead of this method.
     * Using this method is appropriate only when either:
     * <ul>
     * <li>the returned Cffu is read in a blocking manner, or
     * <li>all subsequent actions of dependent Cffus/CompletableFutures are guaranteed to execute asynchronously
     *    (i.e., the dependent Cffus/CompletableFutures are created using async methods).
     * </ul> In these cases, using these methods avoids an unnecessary thread switch when timeout occurs; However, these
     * conditions are difficult to guarantee in practice especially when the returned Cffu is used by other code.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return this Cffu
     * @see #completeOnTimeout(Object, long, TimeUnit)
     */
    @Contract("_, _, _ -> this")
    public Cffu<T> unsafeCompleteOnTimeout(@Nullable T value, long timeout, TimeUnit unit) {
        checkMinimalStage();
        CompletableFutureUtils.completeOnTimeout(cf, value, timeout, unit);
        return this;
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Advanced Methods(compose* and handle-like methods)
    //
    // NOTE about advanced meaning:
    //   - `compose` methods, input function argument return CompletionStage
    //   - handle successful and failed result together(handle*/whenComplete*/peek*)
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
     */
    @Override
    public <U> Cffu<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return resetCf(cf.thenCompose(fn));
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
     */
    @Override
    public <U> Cffu<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return thenComposeAsync(fn, fac.defaultExecutor);
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
     */
    @Override
    public <U> Cffu<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return resetCf(cf.thenComposeAsync(fn, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this Cffu completes exceptionally with the given exceptionType,
     * is composed using the results of the supplied function applied to the exception from this Cffu.
     * <p>
     * <strong>"The exception from this Cffu"</strong> means the cause of the {@link ExecutionException}
     * thrown by {@code get()} or, if {@code get()} throws a different kind of exception, that exception itself;
     * aka the exception is unwrapped by {@link CompletableFutureUtils#unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the exception from this Cffu. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if this Cffu fails with the expected exception type.
     *                      The function's argument is the exception from this Cffu.
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    public <X extends Throwable> Cffu<T> catchingCompose(
            Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback) {
        return resetCf(CompletableFutureUtils.catchingCompose(cf, exceptionType, fallback));
    }

    /**
     * Returns a new Cffu that, when this Cffu completes exceptionally with the given exceptionType, is composed using
     * the results of the supplied function applied to the exception from this Cffu using {@link #defaultExecutor()}.
     * <p>
     * <strong>"The exception from this Cffu"</strong> means the cause of the {@link ExecutionException}
     * thrown by {@code get()} or, if {@code get()} throws a different kind of exception, that exception itself;
     * aka the exception is unwrapped by {@link CompletableFutureUtils#unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the exception from this Cffu. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if this Cffu fails with the expected exception type.
     *                      The function's argument is the exception from this Cffu.
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    public <X extends Throwable> Cffu<T> catchingComposeAsync(
            Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback) {
        return catchingComposeAsync(exceptionType, fallback, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this Cffu completes exceptionally with the given exceptionType, is composed using
     * the results of the supplied function applied to the exception from this Cffu, using the supplied Executor.
     * <p>
     * <strong>"The exception from this Cffu"</strong> means the cause of the {@link ExecutionException}
     * thrown by {@code get()} or, if {@code get()} throws a different kind of exception, that exception itself;
     * aka the exception is unwrapped by {@link CompletableFutureUtils#unwrapCfException(Throwable)}.
     *
     * @param exceptionType the exception type that triggers use of {@code fallback}. The exception type is matched
     *                      against the exception from this Cffu. To avoid hiding bugs and other unrecoverable errors,
     *                      callers should prefer more specific types, avoiding {@code Throwable.class} in particular.
     * @param fallback      the Function to be called if this Cffu fails with the expected exception type.
     *                      The function's argument is the exception from this Cffu.
     * @param executor      the executor to use for asynchronous execution
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     * @see Futures#catchingAsync the equivalent Guava method catchingAsync()
     */
    public <X extends Throwable> Cffu<T> catchingComposeAsync(
            Class<X> exceptionType, Function<? super X, ? extends CompletionStage<T>> fallback, Executor executor) {
        return resetCf(CompletableFutureUtils.catchingComposeAsync(cf, exceptionType, fallback, cffuScreened(executor)));
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is composed using the results of the supplied function applied to this stage's exception.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingCompose(Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the returned CompletionStage
     *           if this CompletionStage completed exceptionally
     * @see #catchingCompose(Class, Function)
     */
    @Override
    public Cffu<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return resetCf(CompletableFutureUtils.exceptionallyCompose(cf, fn));
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is composed using the results of the supplied function applied to this stage's exception,
     * using {@link #defaultExecutor()}.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingComposeAsync(Class, Function)}
     * instead in your business application.
     *
     * @param fn the function to use to compute the returned CompletionStage
     *           if this Cffu completed exceptionally
     * @see #catchingComposeAsync(Class, Function)
     */
    @Override
    public Cffu<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(fn, fac.defaultExecutor);
    }

    /**
     * Returns a new Cffu that, when this stage completes exceptionally,
     * is composed using the results of the supplied function applied to this stage's exception,
     * using the supplied Executor.
     * <p>
     * Just as catching {@code Throwable} is not best practice in general, this method handles the {@code Throwable};
     * <strong>Strongly recommend</strong> using {@link #catchingComposeAsync(Class, Function, Executor)}
     * instead in your business application.
     *
     * @param fn       the function to use to compute the returned CompletionStage
     *                 if this Cffu completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @see #catchingComposeAsync(Class, Function, Executor)
     */
    @Override
    public Cffu<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        return resetCf(CompletableFutureUtils.exceptionallyComposeAsync(cf, fn, cffuScreened(executor)));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `peek`")
    @Override
    public <U> Cffu<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return resetCf(cf.handle(fn));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `peekAsync`")
    @Override
    public <U> Cffu<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return handleAsync(fn, fac.defaultExecutor);
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `peekAsync`")
    @Override
    public <U> Cffu<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return resetCf(cf.handleAsync(fn, cffuScreened(executor)));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `peek`")
    @Override
    public Cffu<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return resetCf(cf.whenComplete(action));
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `peekAsync`")
    @Override
    public Cffu<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return whenCompleteAsync(action, fac.defaultExecutor);
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
     */
    @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer method `peekAsync`")
    @Override
    public Cffu<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return resetCf(cf.whenCompleteAsync(action, cffuScreened(executor)));
    }

    /**
     * Peeks the result by executing the given action when this cffu completes, returns this cffu.
     * <p>
     * When this cffu is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments.
     * <p>
     * <strong>CAUTION:</strong> The return cffu of method {@link #whenComplete(BiConsumer)}
     * will contain <strong>DIFFERENT</strong> result to this cffu when this cffu completed normally
     * but the supplied action throws an exception. This behavior of method {@code whenComplete} is subtle,
     * and common misused if you just want to <strong>peek</strong> this cffu without affecting the result(e.g.
     * logging the cf result).<br>For this {@code peek} method, whether the supplied action throws an exception or not,
     * the result of return cffu(aka. this cffu) is <strong>NOT</strong> affected.
     * <p>
     * Unlike method {@link #handle(BiFunction)} and like method {@link #whenComplete(BiConsumer)},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return this Cffu
     * @see #whenComplete(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    @Contract("_ -> this")
    public Cffu<T> peek(BiConsumer<? super T, ? super Throwable> action) {
        CompletableFutureUtils.peek(cf, action);
        return this;
    }

    /**
     * Peeks the result by executing the given action using {@link #defaultExecutor()}
     * when this cffu completes, returns this cffu.
     * <p>
     * When this cffu is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments.
     * <p>
     * <strong>CAUTION:</strong> The return cffu of method {@link #whenCompleteAsync(BiConsumer)}
     * will contain <strong>DIFFERENT</strong> result to this cffu when this cffu completed normally
     * but the supplied action throws an exception. This behavior of method {@code whenComplete} is subtle,
     * and common misused if you just want to <strong>peek</strong> this cffu without affecting the result(e.g.
     * logging the cf result).<br>For this {@code peek} method, whether the supplied action throws an exception or not,
     * the result of return cffu(aka. this cffu) is <strong>NOT</strong> affected.
     * <p>
     * Unlike method {@link #handleAsync(BiFunction)} and like method {@link #whenCompleteAsync(BiConsumer)},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return this Cffu
     * @see #whenCompleteAsync(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    @Contract("_ -> this")
    public Cffu<T> peekAsync(BiConsumer<? super T, ? super Throwable> action) {
        return peekAsync(action, fac.defaultExecutor);
    }

    /**
     * Peeks the result by executing the given action using the supplied executor
     * when this cffu completes, returns this cffu.
     * <p>
     * When this cffu is complete, the given action is invoked with the result(or {@code null} if none)
     * and the exception (or {@code null} if none) of this cffu as arguments.
     * <p>
     * <strong>CAUTION:</strong> The return cffu of method {@link #whenCompleteAsync(BiConsumer, Executor)}
     * will contain <strong>DIFFERENT</strong> result to this cffu when this cffu completed normally
     * but the supplied action throws an exception. This behavior of method {@code whenComplete} is subtle,
     * and common misused if you just want to <strong>peek</strong> this cffu without affecting the result(e.g.
     * logging the cf result).<br>For this {@code peek} method, whether the supplied action throws an exception or not,
     * the result of return cffu(aka. this cffu) is <strong>NOT</strong> affected.
     * <p>
     * Unlike method {@link #handleAsync(BiFunction, Executor)} and like method {@link
     * #whenCompleteAsync(BiConsumer, Executor)}, this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return this Cffu
     * @see #whenCompleteAsync(BiConsumer, Executor)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    @Contract("_, _ -> this")
    public Cffu<T> peekAsync(BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        CompletableFutureUtils.peekAsync(cf, action, cffuScreened(executor));
        return this;
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Read(explicitly) Methods
    //
    //    - get()               // BLOCKING!
    //    - get(timeout, unit)  // BLOCKING!
    //    - join()              // BLOCKING!
    //    - join(timeout, unit) // BLOCKING!
    //    - getNow(T valueIfAbsent)
    //    - getSuccessNow(T valueIfNotSuccess)
    //    - resultNow()
    //    - exceptionNow()
    //
    //    - isDone()
    //    - isCompletedExceptionally()
    //    - isCancelled()
    //    - state()
    //    - cffuState()
    //
    // NOTE about ExecutionException or CompletionException when the computation threw an exception:
    //   - get methods throw ExecutionException(checked exception)
    //     these methods exists in `Future` interface since Java 5
    //   - getNow/join throw CompletionException(unchecked exception),
    //     these methods exists in `CompletableFuture` since Java 8
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Waits if necessary for the computation to complete, and then retrieves its result.
     *
     * @return the computed result
     * @throws CancellationException if the computation was cancelled
     * @throws ExecutionException    if the computation threw an exception
     * @throws InterruptedException  if the current thread was interrupted while waiting
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
     * <strong>CAUTION:</strong> if the wait timed out, this method throws an (unchecked) {@link CompletionException}
     * with the {@link TimeoutException} as its cause;
     * NOT throws a (checked) {@link TimeoutException} like {@link #get(long, TimeUnit)}.
     * <p>
     * <strong>NOTE:</strong> Calling this method
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
     * @param timeout the maximum time to wait
     * @param unit    the time unit of the timeout argument
     * @return the result value
     * @throws CancellationException if the computation was cancelled
     * @throws CompletionException   if this future completed exceptionally
     *                               or a completion computation threw an exception
     *                               or the wait timed out(with the {@code TimeoutException} as its cause)
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
     * @see #getSuccessNow(Object)
     */
    @Contract(pure = true)
    @Nullable
    public T getNow(T valueIfAbsent) {
        checkMinimalStage();
        return cf.getNow(valueIfAbsent);
    }

    /**
     * Returns the result value if completed normally, else returns the given valueIfNotSuccess.
     * <p>
     * This method is guaranteed not to throw {@link CompletionException}, {@link ExecutionException},
     * {@link CancellationException} and {@link IllegalStateException}.
     *
     * @param valueIfNotSuccess the value to return if not completed normally
     * @return the result value, if completed normally, else the given valueIfNotSuccess
     */
    @Contract(pure = true)
    @Nullable
    public T getSuccessNow(@Nullable T valueIfNotSuccess) {
        checkMinimalStage();
        return CompletableFutureUtils.getSuccessNow(cf, valueIfNotSuccess);
    }

    /**
     * Returns the computed result, without waiting.
     * <p>
     * This method is for cases where the caller knows that the task has already completed normally,
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
     * @see #getSuccessNow(Object)
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
     * Returns the computation state. This method does not provide Java version compatibility logic;
     * For {@code Java 18-}, use {@link #cffuState()} instead.
     * <p>
     * <strong>NOTE:</strong> Both {@link CompletableFuture#state} method and its return type
     * {@link Future.State} were introduced in {@code Java 19}. The dependency on the new return type
     * makes it IMPOSSIBLE to provide backward compatibility through the {@code Cffu} wrapper.
     *
     * @return the computation state
     * @see #cffuState()
     */
    @Contract(pure = true)
    @Override
    public Future.State state() {
        checkMinimalStage();
        return cf.state();
    }

    /**
     * Returns the computation state ({@link CffuState}). This method provides equivalent functionality to
     * {@link CompletableFuture#state()} with backwards compatibility for {@code Java 18-}.
     *
     * @return the computation state
     */
    @Contract(pure = true)
    public CffuState cffuState() {
        checkMinimalStage();
        return CompletableFutureUtils.state(cf);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Write Methods
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
    @Contract("_ -> this")
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
    @Contract("_, _ -> this")
    public Cffu<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
        checkMinimalStage();
        // NOTE: do NOT translate (ad hoc input)executor to screened executor; same as CompletableFuture.completeAsync
        CompletableFutureUtils.completeAsync(cf, supplier, cffuUnscreened(executor));
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
     * If not already completed, completes this Cffu with the exception result
     * of the given Supplier function invoked from an asynchronous task using the default executor.
     *
     * @param supplier a function returning the value to be used to complete this Cffu
     * @return this Cffu
     */
    @Contract("_ -> this")
    public Cffu<T> completeExceptionallyAsync(Supplier<? extends Throwable> supplier) {
        return completeExceptionallyAsync(supplier, fac.defaultExecutor);
    }

    /**
     * If not already completed, completes this Cffu with the exception result
     * of the given Supplier function invoked from an asynchronous task using the given executor.
     *
     * @param supplier a function returning the value to be used to complete this Cffu
     * @param executor the executor to use for asynchronous execution
     * @return this Cffu
     */
    @Contract("_, _ -> this")
    public Cffu<T> completeExceptionallyAsync(Supplier<? extends Throwable> supplier, Executor executor) {
        checkMinimalStage();
        // NOTE: do NOT translate (ad hoc input)executor to screened executor; same as CompletableFuture.completeAsync
        CompletableFutureUtils.completeExceptionallyAsync(cf, supplier, cffuUnscreened(executor));
        return this;
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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Re-Config Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletionStage that is completed normally with the same value
     * as this Cffu when it completes normally, and cannot be independently completed
     * or otherwise used in ways not defined by the methods of interface {@link CompletionStage}.
     * If this Cffu completes exceptionally, then the returned CompletionStage completes
     * exceptionally with a CompletionException with this exception as cause.
     * <p>
     * demo code about re-config methods of Cffu:
     *
     * <pre>{@code cffu2 = cffu
     *     .withDefaultExecutor(executor2) // reset to use executor2
     *     .minimalCompletionStage();      // restrict to methods of CompletionStage
     * }</pre>
     * <p>
     * <strong>CAUTION:</strong> if run on old Java 8 (which does not support *minimal* CompletionStage),
     * this method just returns a *normal* Cffu instance which is NOT a *minimal* CompletionStage.
     */
    @Contract(pure = true)
    public CompletionStage<T> minimalCompletionStage() {
        return resetToMin((CompletableFuture<T>) CompletableFutureUtils.minimalCompletionStage(cf));
    }

    /**
     * Returns a new Cffu with the given defaultExecutor.
     * <p>
     * Note: The provided defaultExecutor will only affect subsequent newly created Cffu instances,
     * and will not affect this Cffu instance.
     * <p>
     * demo code about re-config methods of Cffu:
     *
     * <pre>{@code cffu2 = cffu
     *     .withDefaultExecutor(executor2) // reset to use executor2
     *     .minimalCompletionStage();      // restrict to methods of CompletionStage
     * }</pre>
     *
     * @see #minimalCompletionStage()
     */
    @Contract(pure = true)
    public Cffu<T> withDefaultExecutor(Executor defaultExecutor) {
        return withCffuFactory(fac.withDefaultExecutor(defaultExecutor));
    }

    /**
     * Returns a new Cffu with the given CffuFactory(contained configuration).
     * <p>
     * Note: The provided CffuFactory configuration will only affect subsequent newly created Cffu instances,
     * and will not affect this Cffu instance.
     * <p>
     * demo code about re-config methods of Cffu:
     *
     * <pre>{@code cffu2 = cffu
     *     .withCffuFactory(cffuFactory2) // reset to use config from cffuFactory2
     *     .minimalCompletionStage();     // restrict to methods of CompletionStage
     * }</pre>
     *
     * @param cffuFactory cffuFactory contained configuration
     * @see #minimalCompletionStage()
     */
    @Contract(pure = true)
    public Cffu<T> withCffuFactory(CffuFactory cffuFactory) {
        // NOTE: Copy the underlying cf to ensure this method/with* methods create new Cffu instance with NEW cf instance,
        //       preventing modifications/write methods to the new returned Cffu instance from affecting this Cffu in surprise
        return new Cffu<>(cffuFactory, isMinimalStage, CompletableFutureUtils.copy(cf));
    }

    /**
     * Returns a {@link CompletableFuture} maintaining the same completion properties as this Cffu.
     * <p>
     * Calling this method is same as calling the {@link CompletableFuture#toCompletableFuture()} method
     * of the underlying CompletableFuture: {@code underlyingCf.toCompletableFuture()};
     * {@link CompletableFutureUtils#toCompletableFutureArray(CompletionStage[])} is the batch operation of this method.
     * If you need the underlying CompletableFuture instance, call method {@link #cffuUnwrap()}.
     *
     * @return the CompletableFuture
     * @see CompletableFutureUtils#toCompletableFutureArray(CompletionStage[])
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
     */
    @Contract(pure = true)
    public Cffu<T> copy() {
        return resetCf(CompletableFutureUtils.copy(cf));
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Getter Methods of Cffu properties
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * Config from the {@link CffuFactory#defaultExecutor()},
     * and can re-configured by {@link #withCffuFactory(CffuFactory)}.
     *
     * @return the default executor
     * @see CffuFactory#defaultExecutor()
     * @see CffuFactory#builder(Executor)
     * @see #cffuFactory()
     */
    @Contract(pure = true)
    public Executor defaultExecutor() {
        return fac.defaultExecutor();
    }

    /**
     * Returns the {@link CffuFactory} of this Cffu.
     * This can be re-configured by {@link #withCffuFactory(CffuFactory)}.
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
     * This can be re-configured by {@link #withCffuFactory(CffuFactory)}.
     *
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
     * <li>{@link CffuFactory#toCffu(CompletionStage)}, this method returns a {@code minimal stage}
     *     when input a{@code minimal stage}, otherwise returns a normal stage.
     * </ul>
     */
    @Contract(pure = true)
    public boolean isMinimalStage() {
        return isMinimalStage;
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Inspection Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns the estimated number of CompletableFuture(including Cffu wrapper)
     * whose completions are awaiting completion of this Cffu(aka its underlying CompletableFuture).
     * This method is designed for use in monitoring system state, not for synchronization control.
     *
     * @return the estimated number of dependent CompletableFutures(including Cffu wrapper)
     * @see CompletableFuture#getNumberOfDependents()
     */
    @Contract(pure = true)
    public int getNumberOfDependents() {
        checkMinimalStage();
        return cf.getNumberOfDependents();
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Other Uncommon Methods(dangerous or trivial)
    //
    //  - dangerous
    //    - obtrudeValue(value)
    //    - obtrudeException(ex)
    //    - cffuUnwrap()
    //  - for API compatibility with CompletableFuture
    //    - newIncompleteFuture()
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Forcibly sets or resets the value subsequently returned by method {@link #get()} and related methods,
     * whether already completed or not. This method is designed for use only in error recovery actions, and even in
     * such situations may result in ongoing dependent completions using established versus overwritten outcomes.
     *
     * @param value the completion value
     * @see CffuFactoryBuilder#forbidObtrudeMethods(boolean)
     * @see CffuFactory#forbidObtrudeMethods()
     */
    public void obtrudeValue(@Nullable T value) {
        checkMinimalStage();
        checkForbidObtrudeMethods();
        cf.obtrudeValue(value);
    }

    /**
     * Forcibly causes subsequent invocations of method {@link #get()} and related methods to throw the given exception,
     * whether already completed or not. This method is designed for use only in error recovery actions, and even
     * in such situations may result in ongoing dependent completions using established versus overwritten outcomes.
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
     * Returns the underlying CompletableFuture.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! The returned CompletableFuture may be a minimal-stage.
     * In general, you should NEVER use this method, use {@link #toCompletableFuture()} instead.
     *
     * @return the underlying CompletableFuture
     * @see #toCompletableFuture()
     */
    @Contract(pure = true)
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> cffuUnwrap() {
        return cf;
    }

    /**
     * Returns a new incomplete Cffu with CompletableFuture of the type to be returned by a CompletionStage method.
     * <p>
     * <strong>NOTE:</strong> This method existed mainly for API compatibility with {@code CompletableFuture},
     * prefer {@link CffuFactory#newIncompleteCffu()}.
     *
     * @see CffuFactory#newIncompleteCffu()
     */
    @Contract(pure = true)
    public <U> Cffu<U> newIncompleteFuture() {
        return fac.newIncompleteCffu();
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
        return getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(this)) + "(" + cf + ")";
    }

    private void checkMinimalStage() {
        if (isMinimalStage) throw new UnsupportedOperationException("unsupported because this is a minimal stage");
    }

    private void checkForbidObtrudeMethods() {
        if (fac.forbidObtrudeMethods()) throw new UnsupportedOperationException("obtrude methods is forbidden by cffu");
    }
}
