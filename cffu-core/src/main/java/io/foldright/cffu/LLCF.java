package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.unwrapCfException;
import static io.foldright.cffu.internal.CommonUtils.containsInArray;
import static io.foldright.cffu.internal.CommonUtils.mapArray;
import static io.foldright.cffu.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu.internal.ExceptionLogger.logUncaughtException;
import static java.lang.Thread.currentThread;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * Low Level CompletableFuture utility methods for manipulating CompletableFuture. This class is for library writers,
 * the methods intended for end users are in the {@link CompletableFutureUtils} class.
 * <p>
 * In general, you should NEVER use this class, unless you understand the underlying logic of CompletableFuture
 * and need hack it. Because the methods are Low Level, use below the method name convention intentionally:
 * <ul>
 * <li>methods with {@code f_} prefix means not type-safe, e.g.
 *    <ul>
 *    <li>return type CompletableFuture that may be a minimal-stage
 *    <li>force cast to {@code CompletableFuture<T>} from any {@code CompletableFuture<?>}
 *    <li>return generic type T but constrained runtime type TupleX
 *    </ul>
 * <li>methods with {@code 0} suffix means no parameter validation, e.g.
 *    <ul><li>no null check</li></ul>
 * </ul>
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CompletableFutureUtils
 */
public final class LLCF {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Low Level conversion and test methods for CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Force casts CompletableFuture with the value type, IGNORE the compile-time type check.
     */
    @Contract(pure = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CompletableFuture<T> f_cast(CompletableFuture<?> cf) {
        return (CompletableFuture) cf;
    }

    /**
     * Force converts CompletionStage to CompletableFuture, reuse cf instances as many as possible.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! Because reused the CF instance, The returned cf
     * may be a minimal-stage, MUST NOT be written or read(explicitly) (e.g. {@link CompletableFuture#complete});
     * Otherwise, the caller usage of cf may throw UnsupportedOperationException.
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> f_toCf0(CompletionStage<? extends T> stage) {
        if (stage instanceof CompletableFuture) return (CompletableFuture<T>) stage;
        else if (stage instanceof Cffu) return ((Cffu<T>) stage).cffuUnwrap();
        else return (CompletableFuture<T>) stage.toCompletableFuture();
    }

    /**
     * Force converts CompletionStage array to CompletableFuture array, reuse cf instances as many as possible.
     * This method is NOT type safe! More info see method {@link #f_toCf0(CompletionStage)}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T>[] f_toCfArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::f_toCf0);
    }

    /**
     * Converts CompletionStage to a CompletableFuture copy.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! Because reused the CF instance, The returned cf
     * may be a minimal-stage, MUST NOT be written or read(explicitly) (e.g. {@link CompletableFuture#complete});
     * Otherwise, the caller usage of cf may throw UnsupportedOperationException.
     * <p>
     * Implementation Note: The returned instances of calling {@code copy} methods
     * ({@link CompletableFuture#copy}) on minimal-stage instances is still minimal-stage
     * (e.g. {@code minimalCompletionStage().copy()}, {@code completedStage().copy()})
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> f_toCfCopy0(CompletionStage<? extends T> stage) {
        final CompletableFuture<T> f = f_toCf0(stage);
        return IS_JAVA9_PLUS ? f.copy() : f.thenApply(x -> x);
    }

    /**
     * Converts CompletionStage array to a CompletableFuture copy array. This method is NOT type safe!
     * More info see method {@link #f_toCfCopy0(CompletionStage)}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T>[] f_toCfCopyArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::f_toCfCopy0);
    }

    /**
     * Converts CompletionStage to non-minimal-stage CompletableFuture, reuse cf instances as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instance, if the caller need defensive copy
     * instead of writing it directly, use method {@link #toNonMinCfCopy0(CompletionStage)}).
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> toNonMinCf0(CompletionStage<? extends T> stage) {
        final CompletableFuture<T> f = f_toCf0(stage);
        return isMinStageCf(f) ? f.toCompletableFuture() : f;
    }

    /**
     * Converts CompletionStage array to non-minimal-stage CompletableFuture array,
     * reuse cf instances as many as possible. More info see method {@link #toNonMinCf0(CompletionStage)}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T>[] toNonMinCfArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::toNonMinCf0);
    }

    /**
     * Converts CompletionStage to a non-minimal-stage CompletableFuture copy. This method is type safe.
     * <p>
     * Implementation Note: The returned instances of calling {@code copy} methods
     * ({@link CompletableFuture#copy}) on minimal-stage instances is still minimal-stage
     * (e.g. {@code minimalCompletionStage().copy()}, {@code completedStage().copy()}).
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> toNonMinCfCopy0(CompletionStage<? extends T> stage) {
        final CompletableFuture<T> f = f_toCf0(stage);
        return isMinStageCf(f) ? f.toCompletableFuture() : IS_JAVA9_PLUS ? f.copy() : f.thenApply(x -> x);
    }

    /**
     * Converts CompletionStage array to a non-minimal-stage CompletableFuture copy array. This method is type safe.
     * More info see method {@link #toNonMinCfCopy0(CompletionStage)}.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T>[] toNonMinCfCopyArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::toNonMinCfCopy0);
    }

    /**
     * Checks if the given {@code CompletableFuture} instance is a minimal-stage.
     * <p>
     * Implementation Note: While minimal-stage is implemented as a private subclass of CompletableFuture,
     * the CompletableFuture API consistently uses CompletionStage type for minimal-stage instances
     * and reserves CompletableFuture type for non-minimal-stage instances only.
     * <p>
     * This type contract for minimal-stage MUST be followed for end users APIs.
     */
    @Contract(pure = true)
    public static boolean isMinStageCf(CompletableFuture<?> cf) {
        return cf.getClass().equals(MIN_STAGE_CLASS);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Low level operations of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Peeks the result by executing the given action when the given stage completes.
     * This method is guaranteed to return the given stage without modifying it;
     * The uncaught exceptions thrown by the action are reported.
     * <p>
     * <strong>CAUTION:</strong> Since this method returns the input stage directly, the execution order between
     * the given action and other actions added to the input stage cannot be guaranteed. The action should be treated
     * as "fire and forget" - do not make any assumptions about timing or execution sequence.
     *
     * @see CompletableFutureUtils#peek(CompletionStage, BiConsumer)
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     */
    @Contract("_, _, _ -> param1")
    public static <T, F extends CompletionStage<? extends T>>
    F peek0(F cfThis, BiConsumer<? super T, ? super Throwable> action, String where) {
        cfThis.whenComplete(safePeekAction(action, where));
        return cfThis;
    }

    /**
     * Peeks the result by executing the given action using the supplied executor when the given stage completes.
     * This method is guaranteed to return the given stage without modifying it;
     * The uncaught exceptions thrown by the action are reported.
     * <p>
     * <strong>CAUTION:</strong> Since this method returns the input stage directly, the execution order between
     * the given action and other actions added to the input stage cannot be guaranteed. The action should be treated
     * as "fire and forget" - do not make any assumptions about timing or execution sequence.
     *
     * @see CompletableFutureUtils#peekAsync(CompletionStage, BiConsumer, Executor)
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     */
    @Contract("_, _, _, _ -> param1")
    public static <T, F extends CompletionStage<? extends T>>
    F peekAsync0(F cfThis, BiConsumer<? super T, ? super Throwable> action, String where, Executor executor) {
        cfThis.whenCompleteAsync(safePeekAction(action, where), executor);
        return cfThis;
    }

    private static <T> BiConsumer<T, Throwable> safePeekAction(BiConsumer<? super T, ? super Throwable> action, String where) {
        return (v, ex) -> {
            try {
                action.accept(v, ex);
            } catch (Throwable e) {
                safeAddSuppressedEx(ex, e);
                logUncaughtException(ERROR, where, e);
            }
        };
    }

    /**
     * Completes the given CompletableFuture with the exception(if non-null), otherwise with the value.
     * In general, you should NEVER use this method in application codes, use {@link
     * CompletableFuture#complete(Object)} or {@link CompletableFuture#completeExceptionally(Throwable)} instead.
     */
    public static <T> boolean completeCf0(CompletableFuture<? super T> cf, @Nullable T value, @Nullable Throwable ex) {
        if (ex == null) return cf.complete(value);
        else return cf.completeExceptionally(ex);
    }

    /**
     * Guarantees the execution of new stage's computations not in the caller thread.
     * <p>
     * In {@link CompletionStage} / {@link CompletableFuture}, Execution of a new stage's computations
     * may be arranged in any of three ways:
     * <ul>
     * <li>default execution,
     * <li>default asynchronous execution (using methods with suffix async
     * that employ the stage's default asynchronous execution facility),
     * <li>or custom asynchronous execution (via a supplied Executor).
     * </ul>
     * The information above is referenced from {@link java.util.concurrent.CompletionStage}.
     * <p>
     * This method introduces the fourth way "relay async" to arrange to computation of a new stage's computations:
     * <ul>
     * <li> if input cf is COMPLETED, asynchronous execution in an executor, won't block sequential code of caller。
     * <li> otherwise, use default execution, save one unnecessary thread switch.
     * </ul>
     * <p>
     * One more thread switch generally won't lead any performance problems,
     * make wise use of "relay async" when necessary.
     * <p>
     * More info about "relay async" see <a href=
     * "https://github.com/foldright/cffu/blob/oldratlee/main/cffu-core/src/test/java/io/foldright/study/relayasync/RelayAsyncDescriptionExample.java"
     * >RelayAsyncDescriptionExample.java</a>
     *
     * @param cfThis            the input stage(including CompletableFuture)
     * @param relayComputations the computations of input
     * @param executor          the executor used to asynchronous execution
     * @return the return value of function {@code relayComputations}
     */
    public static <T, F extends CompletionStage<?>> F relayAsync0(
            CompletionStage<? extends T> cfThis,
            Function<CompletableFuture<T>, F> relayComputations, Executor executor) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        final F ret = relayComputations.apply(promise);

        final Thread callerThread = currentThread();
        final boolean[] finishAttach = {false};

        LLCF.peek0(cfThis, (v, ex) -> {
            if (!currentThread().equals(callerThread) || finishAttach[0]) completeCf0(promise, v, ex);
            else executor.execute(() -> completeCf0(promise, v, ex));
        }, "relayAsync0");

        finishAttach[0] = true;
        return ret;
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Non-exception-swallowed safety methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Adds a suppressed exception to a target exception, first unwrapping the target exception
     * if it is a CompletionException or ExecutionException.
     * <p>
     * Unwrapping target exception is necessary to ensures suppressed exceptions are properly preserved,
     * because CompletableFuture internally wraps exceptions in CompletionException / ExecutionException, which can
     * later be unwrapped and discarded during CompletableFuture processing (e.g. {@link CompletableFuture#exceptionNow}),
     * potentially losing any suppressed exceptions that were attached to the wrapper.
     *
     * @param suppressed the exception to be added as a suppressed exception. If null, no action is taken
     * @param target     the target exception to add the suppressed exception to
     * @see CompletableFutureUtils#unwrapCfException(Throwable)
     */
    public static void safeAddSuppressedEx(@Nullable Throwable suppressed, Throwable target) {
        if (suppressed == null) return;
        target = unwrapCfException(target);
        if (suppressed != target && !containsInArray(target.getSuppressed(), suppressed))
            target.addSuppressed(suppressed);
    }

    /**
     * Wraps a function that processes exceptions to ensure that if error handling throws a new exception,
     * the error context is preserved by calling {@link Throwable#addSuppressed}.
     *
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     */
    @Contract(value = "null, _ -> null; !null, _ -> !null", pure = true)
    public static <X extends Throwable, T, F extends Function<? super X, ? extends T>>
    @Nullable F nonExSwallowedFunction(@Nullable F fn, boolean addSuppressedToOriginalEx) {
        if (fn == null) return null;
        return _wrapFn(fn, addSuppressedToOriginalEx);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <F extends Function> F _wrapFn(Function fn, boolean addSuppressedToOriginalEx) {
        final Function f = originalEx -> {
            try {
                return fn.apply(originalEx);
            } catch (Throwable newEx) {
                if (originalEx != null) {
                    // when exceptions occur in this exception process function,
                    // the error context is preserved by calling addSuppressed
                    if (addSuppressedToOriginalEx) safeAddSuppressedEx(newEx, (Throwable) originalEx);
                    else safeAddSuppressedEx((Throwable) originalEx, newEx);
                }

                throw newEx;
            }
        };
        return (F) f;
    }

    /**
     * Wraps a BiFunction that processes exceptions to ensure that if error handling throws a new exception,
     * the error context is preserved by calling {@link Throwable#addSuppressed}.
     *
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     */
    @Contract(value = "null, _ -> null; !null, _ -> !null", pure = true)
    public static <T, X extends Throwable, U, F extends BiFunction<? super T, ? extends X, ? extends U>>
    @Nullable F nonExSwallowedBiFunction(@Nullable F fn, boolean addSuppressedToOriginalEx) {
        if (fn == null) return null;
        return _wrapBiFn(fn, addSuppressedToOriginalEx);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <F extends BiFunction> F _wrapBiFn(BiFunction fn, boolean addSuppressedToOriginalEx) {
        final BiFunction f = (v, originalEx) -> {
            try {
                return fn.apply(v, originalEx);
            } catch (Throwable newEx) {
                if (originalEx != null) {
                    // when exceptions occur in this exception process function,
                    // the error context is preserved by calling addSuppressed
                    if (addSuppressedToOriginalEx) safeAddSuppressedEx(newEx, (Throwable) originalEx);
                    else safeAddSuppressedEx((Throwable) originalEx, newEx);
                }

                throw newEx;
            }
        };
        return (F) f;
    }

    /**
     * Wraps a BiConsumer that processes exceptions to ensure that if error handling throws a new exception,
     * the error context is preserved by calling {@link Throwable#addSuppressed}.
     *
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     */
    @Contract(value = "null, _ -> null; !null, _ -> !null", pure = true)
    public static <T, X extends Throwable, F extends BiConsumer<? super T, ? super X>>
    @Nullable F nonExSwallowedBiConsumer(@Nullable F action, boolean addSuppressedToOriginalEx) {
        if (action == null) return null;
        return _wrapBiConsumer(action, addSuppressedToOriginalEx);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <F extends BiConsumer> F _wrapBiConsumer(BiConsumer action, boolean addSuppressedToOriginalEx) {
        final BiConsumer a = (v, originalEx) -> {
            try {
                action.accept(v, originalEx);
            } catch (Throwable newEx) {
                if (originalEx != null) {
                    // when exceptions occur in this exception process action,
                    // the error context is preserved by calling addSuppressed
                    if (addSuppressedToOriginalEx) safeAddSuppressedEx(newEx, (Throwable) originalEx);
                    else safeAddSuppressedEx((Throwable) originalEx, newEx);
                }

                throw newEx;
            }
        };
        return (F) a;
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF execution/executor methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Null-checks user executor argument, and translates uses of commonPool to ASYNC_POOL in case parallelism disabled.
     */
    @Contract(pure = true)
    @SuppressWarnings("resource")
    public static Executor screenExecutor(Executor e) {
        // Implementation note: CompletableFuture API methods already call this method internally; Only underlying
        // methods that directly use an executor need to call this method (e.g. CFU#hopExecutorIfAtCfDelayerThread)
        //
        // below code is copied from CompletableFuture#screenExecutor with small adoption
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool()) return ASYNC_POOL;
        return requireNonNull(e, "executor is null");
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal Static Fields/Helpers
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    // region# Java version check logic for compatibility
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * a naive black hole to prevent code elimination, more info see <a href=
     * "https://github.com/openjdk/jmh/blob/1.37/jmh-core/src/main/java/org/openjdk/jmh/infra/Blackhole.java">JMH black hole</a>
     */
    private static volatile int BLACK_HOLE = 0xCFF0CFF0;

    // `CompletableFuture.completedStage` is the new method since java 9
    static final boolean IS_JAVA9_PLUS = methodExists(() -> CompletableFuture.completedStage(null));
    // `CompletableFuture.exceptionallyCompose` is the new method since java 12
    static final boolean IS_JAVA12_PLUS = methodExists(() ->
            completedFuture(null).exceptionallyCompose(ex -> null));
    // `CompletableFuture.resultNow` is the new method since java 19
    static final boolean IS_JAVA19_PLUS = methodExists(() -> completedFuture(null).resultNow());
    // `List.reversed` is the new method since java 21
    static final boolean IS_JAVA21_PLUS = methodExists(() -> new ArrayList<>().reversed());

    private static boolean methodExists(Supplier<?> methodCallCheck) {
        try {
            int i = BLACK_HOLE; // volatile read
            BLACK_HOLE = Objects.hashCode(methodCallCheck.get()) ^ i;
            return true;
        } catch (NoSuchMethodError e) {
            return false;
        }
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF execution/executor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * code is copied from CompletableFuture#USE_COMMON_POOL
     */
    private static final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;

    // IMPORTANT: The initialization order of static fields matters. Do not place static fields
    // before their dependencies, as this will result in using uninitialized dependency values.
    //
    // Dependencies:
    // - ASYNC_POOL depends on IS_JAVA9_PLUS and USE_COMMON_POOL
    // - MIN_STAGE_CLASS depends on IS_JAVA9_PLUS

    /**
     * Default executor of CompletableFuture(<strong>NOT</strong> including the customized subclasses
     * of CompletableFuture) -- {@link ForkJoinPool#commonPool()} unless it cannot support parallelism.
     *
     * @see CompletableFutureUtils#defaultExecutor(CompletionStage)
     */
    // field initialization code is copied from CompletableFuture#ASYNC_POOL with small adoption.
    public static final Executor ASYNC_POOL;

    private static final Class<?> MIN_STAGE_CLASS;

    static {
        if (IS_JAVA9_PLUS) ASYNC_POOL = completedFuture(null).defaultExecutor();
        else if (USE_COMMON_POOL) ASYNC_POOL = ForkJoinPool.commonPool();
        else ASYNC_POOL = new ThreadPerTaskExecutor();

        if (!IS_JAVA9_PLUS) MIN_STAGE_CLASS = null;
        else MIN_STAGE_CLASS = CompletableFuture.completedStage(null).getClass();
    }

    /**
     * Fallback if {@link ForkJoinPool#commonPool()} cannot support parallelism.
     * code is copied from {@link CompletableFuture.ThreadPerTaskExecutor}.
     */
    @SuppressWarnings("JavadocReference")
    private static final class ThreadPerTaskExecutor implements Executor {
        @Override
        public void execute(Runnable r) {
            new Thread(requireNonNull(r)).start();
        }
    }

    private LLCF() {}
}
