package io.foldright.cffu;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Supplier;

import static io.foldright.cffu.InternalCommonUtils.mapArray;
import static java.util.Objects.requireNonNull;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * Low Level CompletableFuture Utils.
 * <p>
 * In general, you should NEVER use this class in application codes,
 * unless you understand the underlying logic of CompletableFuture and need hack it.
 */
public final class LLCF {

    ////////////////////////////////////////////////////////////////////////////////
    // region# CF conversion and test methods
    ////////////////////////////////////////////////////////////////////////////////

    /*
     * Implementation Note about the name convention of internal methods:
     *
     * - methods with `f_` prefix means not type-safe, e.g.
     *    - return type CompletableFuture that may be a minimal-stage
     *    - force cast to CompletableFuture<T> from any CompletableFuture<?>
     *    - return generic type T but constrained type TupleX
     * - methods with `0` suffix means no parameter validation, e.g.
     *    - no null check
     *
     * because these methods is not safe, caller logic SHOULD pay attention to keep implementation correct.
     */

    /**
     * Force casts CompletableFuture with the value type, IGNORE the compile-time type check.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    static <T> CompletableFuture<T> f_cast(CompletableFuture<?> cf) {
        return (CompletableFuture) cf;
    }

    /**
     * Force converts {@link CompletionStage} array to {@link CompletableFuture} array,
     * reuse cf instance as many as possible. This method is NOT type safe!
     * More info see method {@link #f_toCf0(CompletionStage)}.
     */
    static <T> CompletableFuture<T>[] f_toCfArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::f_toCf0);
    }

    /**
     * Converts {@link CompletionStage} array to {@link CompletableFuture} array.
     * More info see method {@link #toNonMinCfCopy0(CompletionStage)}.
     */
    static <T> CompletableFuture<T>[] f_toCfCopyArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::f_toCfCopy0);
    }

    /**
     * Converts {@link CompletionStage} array to {@link CompletableFuture} array.
     * More info see method {@link #toNonMinCf0(CompletionStage)}.
     */
    static <T> CompletableFuture<T>[] toNonMinCfArray0(CompletionStage<? extends T>[] stages) {
        return mapArray(stages, CompletableFuture[]::new, LLCF::toNonMinCf0);
    }

    /**
     * Force converts CompletionStage to CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! Because reused the CF instance,
     * The returned cf may be a minimal-stage, MUST NOT be written or read(explicitly)
     * (e.g. {@link CompletableFuture#complete}); Otherwise, the caller usage of cf may throw UnsupportedOperationException.
     */
    @SuppressWarnings("unchecked")
    static <T> CompletableFuture<T> f_toCf0(CompletionStage<? extends T> s) {
        if (s instanceof CompletableFuture) return (CompletableFuture<T>) s;
        else if (s instanceof Cffu) return ((Cffu<T>) s).cffuUnwrap();
        else return (CompletableFuture<T>) s.toCompletableFuture();
    }

    /**
     * Converts CompletionStage to a CompletableFuture copy, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> This method is NOT type safe! Because reused the CF instance,
     * The returned cf may be a minimal-stage, MUST NOT be written or read(explicitly)
     * (e.g. {@link CompletableFuture#complete}); Otherwise, the caller usage of cf may throw UnsupportedOperationException.
     * <p>
     * Implementation Note: The returned instances of calling {@code copy} methods
     * ({@link CompletableFuture#copy}) on {@code minimal-stage} instances
     * is still {@code minimal-stage}(e.g. {@code minimalCompletionStage().copy()}, {@code completedStage().copy()})
     */
    static <T> CompletableFuture<T> f_toCfCopy0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return IS_JAVA9_PLUS ? f.copy() : f.thenApply(x -> x);
    }

    /**
     * Converts CompletionStage to non-minimal-stage CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instance, if the caller need defensive copy
     * instead of writing it directly, use method {@link #toNonMinCfCopy0(CompletionStage)}).
     */
    static <T> CompletableFuture<T> toNonMinCf0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : f;
    }

    /**
     * Converts CompletionStage to a non-minimal-stage CompletableFuture copy. This method is type safe.
     * <p>
     * Implementation Note: The returned instances of calling {@code copy} methods
     * ({@link CompletableFuture#copy}) on {@code minimal-stage} instances
     * is still {@code minimal-stage}(e.g. {@code minimalCompletionStage().copy()}, {@code completedStage().copy()})
     */
    static <T> CompletableFuture<T> toNonMinCfCopy0(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = f_toCf0(s);
        return isMinStageCf(f) ? f.toCompletableFuture() : IS_JAVA9_PLUS ? f.copy() : f.thenApply(x -> x);
    }

    public static boolean isMinStageCf(CompletableFuture<?> cf) {
        return cf.getClass().equals(MIN_STAGE_CLASS);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF execution/executor methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Null-checks user executor argument, and translates uses of commonPool to ASYNC_POOL
     * in case parallelism disabled. code is copied from {@link CompletableFuture#screenExecutor(Executor)}.
     * <p>
     * Implementation Note: The API methods of {@link CompletableFuture} have called this method, so only underlying methods
     * that direct use executor need call this method, e.g. {@link CompletableFutureUtils#hopExecutorIfAtCfDelayerThread}.
     */
    @SuppressWarnings({"resource", "JavadocReference"})
    static Executor screenExecutor(Executor e) {
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool())
            return ASYNC_POOL;
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

    /**
     * code is copied from {@link CompletableFuture#USE_COMMON_POOL}.
     */
    @SuppressWarnings("JavadocReference")
    private static final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;

    /**
     * Default executor of CompletableFuture(<strong>NOT</strong> including the customized subclasses
     * of CompletableFuture) -- {@link ForkJoinPool#commonPool()} unless it cannot support parallelism.
     * initialization code is copied from {@link CompletableFuture#ASYNC_POOL} with adoption.
     */
    @SuppressWarnings("JavadocReference")
    static final Executor ASYNC_POOL;

    private static final Class<?> MIN_STAGE_CLASS;

    static {
        if (IS_JAVA9_PLUS) ASYNC_POOL = completedFuture(null).defaultExecutor();
        else if (USE_COMMON_POOL) ASYNC_POOL = ForkJoinPool.commonPool();
        else ASYNC_POOL = new ThreadPerTaskExecutor();

        if (!IS_JAVA9_PLUS) MIN_STAGE_CLASS = null;
        else MIN_STAGE_CLASS = CompletableFuture.completedStage(null).getClass();
    }

    private LLCF() {
    }
}
