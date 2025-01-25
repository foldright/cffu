package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.internal.ExceptionLogger;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static io.foldright.cffu.internal.CommonUtils.mapArray;
import static io.foldright.cffu.internal.ExceptionLogger.logUncaughtException;
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
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> f_toCf0(CompletionStage<? extends T> s) {
        if (s instanceof CompletableFuture) return (CompletableFuture<T>) s;
        else if (s instanceof Cffu) return ((Cffu<T>) s).cffuUnwrap();
        else return (CompletableFuture<T>) s.toCompletableFuture();
    }

    /**
     * Force converts CompletionStage array to CompletableFuture array, reuse cf instances as many as possible.
     * This method is NOT type safe! More info see method {@link #f_toCf0(CompletionStage)}.
     */
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
    public static <T> CompletableFuture<T> f_toCfCopy0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return IS_JAVA9_PLUS ? f.copy() : f.thenApply(x -> x);
    }

    /**
     * Converts CompletionStage array to a CompletableFuture copy array. This method is NOT type safe!
     * More info see method {@link #f_toCfCopy0(CompletionStage)}.
     */
    public static <T> CompletableFuture<T>[] f_toCfCopyArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::f_toCfCopy0);
    }

    /**
     * Converts CompletionStage to non-minimal-stage CompletableFuture, reuse cf instances as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instance, if the caller need defensive copy
     * instead of writing it directly, use method {@link #toNonMinCfCopy0(CompletionStage)}).
     */
    public static <T> CompletableFuture<T> toNonMinCf0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : f;
    }

    /**
     * Converts CompletionStage array to non-minimal-stage CompletableFuture array,
     * reuse cf instances as many as possible. More info see method {@link #toNonMinCf0(CompletionStage)}.
     */
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
    public static <T> CompletableFuture<T> toNonMinCfCopy0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : IS_JAVA9_PLUS ? f.copy() : f.thenApply(x -> x);
    }

    /**
     * Converts CompletionStage array to a non-minimal-stage CompletableFuture copy array. This method is type safe.
     * More info see method {@link #toNonMinCfCopy0(CompletionStage)}.
     */
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
    public static boolean isMinStageCf(CompletableFuture<?> cf) {
        return cf.getClass().equals(MIN_STAGE_CLASS);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Low level operations of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Peeks the result by executing the given action when the given stage completes, returns the given stage.
     * The uncaught exceptions thrown by the action are reported.
     *
     * @see CompletableFutureUtils#peek(CompletionStage, BiConsumer)
     */
    @Contract("_, _, _ -> param1")
    public static <T, C extends CompletionStage<? extends T>>
    C peek0(C cfThis, BiConsumer<? super T, ? super Throwable> action, String where) {
        cfThis.whenComplete((v, ex) -> {
            try {
                action.accept(v, ex);
            } catch (Throwable e) {
                if (ex != null) e.addSuppressed(ex);
                logUncaughtException(ExceptionLogger.Level.ERROR, where, e);
            }
        });
        return cfThis;
    }

    /**
     * Peeks the result by executing the given action using the supplied executor when the given stage completes,
     * returns the given stage. The uncaught exceptions thrown by the action are reported.
     *
     * @see CompletableFutureUtils#peekAsync(CompletionStage, BiConsumer, Executor)
     */
    @Contract("_, _, _, _ -> param1")
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync0(C cfThis, BiConsumer<? super T, ? super Throwable> action, String where, Executor executor) {
        cfThis.whenCompleteAsync((v, ex) -> {
            try {
                action.accept(v, ex);
            } catch (Throwable e) {
                if (ex != null) e.addSuppressed(ex);
                logUncaughtException(ExceptionLogger.Level.ERROR, where, e);
            }
        }, executor);
        return cfThis;
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

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF execution/executor methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Null-checks user executor argument, and translates uses of commonPool to ASYNC_POOL in case parallelism disabled.
     */
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
     * code is copied from {@link CompletableFuture#USE_COMMON_POOL}
     */
    @SuppressWarnings("JavadocReference")
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
    // field initialization code is copied from CompletableFuture#ASYNC_POOL with adoption.
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

    private LLCF() {
    }
}
