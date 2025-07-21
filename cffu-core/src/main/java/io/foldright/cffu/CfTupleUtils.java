package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.*;
import static io.foldright.cffu.LLCF.ASYNC_POOL;
import static io.foldright.cffu.LLCF.toNonMinCf0;
import static io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleAllSwallowedExceptions;
import static io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleSwallowedExceptions;
import static io.foldright.cffu.internal.CommonUtils.*;
import static java.util.Objects.requireNonNull;


/**
 * This Utility class provides tuple-based variants of methods from {@link CompletableFutureUtils}
 * for processing and composing multiple asynchronous actions and CompletableFutures in a type-safe manner.
 * <p>
 * While {@link CompletableFutureUtils} uses array-based methods with varargs, this class uses strongly-typed tuples
 * containing 2 to 5 elements. The tuple approach provides better type safety when working with a fixed number of
 * heterogeneous actions or CompletableFutures, as type mismatches are caught at compile time rather than runtime.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author HuHao (995483610 at qq dot com)
 * @see CompletableFutureUtils
 * @see CfIterableUtils
 * @see CfParallelUtils
 */
public final class CfTupleUtils {

    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Parallel Factory Methods
    ////////////////////////////////////////////////////////////
    // region## Multi-Actions-Tuple(MTuple*) Methods(create by actions)
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleFailFastAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleFailFastAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleFailFastAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleWithEhOf0(true, wrapSuppliers0(executor, suppliers), "mSupplyTupleFailFastAsync");
    }

    private static <T> CompletableFuture<T> f_allTupleWithEhOf0(
            boolean failFast, CompletionStage<?>[] stages, String where) {
        CompletableFuture<T> ret = f_allTupleOf0(failFast, stages);
        handleSwallowedExceptions(where, ret, stages);
        return ret;
    }

    /**
     * Returns {@code CompletableFuture<T>} with generic type {@code T} but constrained to type TupleX.
     */
    private static <T> CompletableFuture<T> f_allTupleOf0(boolean failFast, CompletionStage<?>[] stages) {
        final AtomicReferenceArray<Object> results = new AtomicReferenceArray<>(stages.length);
        final CompletableFuture<Void>[] resultsSetterCfs = createAllResultsSetterCfs(stages, results);

        final CompletableFuture<Void> resultsSetter;
        if (failFast) resultsSetter = allFailFastOf0(resultsSetterCfs);
        else resultsSetter = CompletableFuture.allOf(resultsSetterCfs);

        return resultsSetter.thenApply(unused -> f_tupleOf0(toArray(results)));
    }

    /**
     * Returns generic type {@code T} but constrained to type TupleX.
     */
    @SuppressWarnings("unchecked")
    private static <T> T f_tupleOf0(Object[] xs) {
        final int len = xs.length;
        final Object ret;
        if (len == 2) ret = Tuple2.of(xs[0], xs[1]);
        else if (len == 3) ret = Tuple3.of(xs[0], xs[1], xs[2]);
        else if (len == 4) ret = Tuple4.of(xs[0], xs[1], xs[2], xs[3]);
        else ret = Tuple5.of(xs[0], xs[1], xs[2], xs[3], xs[4]);
        return (T) ret;
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyAllSuccessTupleAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyAllSuccessTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAllSuccessAsync(Executor, Object, Supplier[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided suppliers fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyAllSuccessTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allSuccessTupleWithEhOf0(wrapSuppliers0(executor, suppliers), "mSupplyAllSuccessTupleAsync");
    }

    private static <T> CompletableFuture<T> f_allSuccessTupleWithEhOf0(CompletionStage<?>[] stages, String where) {
        handleAllSwallowedExceptions(where, stages);
        return f_allSuccessTupleOf0(stages);
    }

    private static <T> CompletableFuture<T> f_allSuccessTupleOf0(CompletionStage<?>[] stages) {
        return f_allTupleOf0(false, f_convertStageArray0(stages, s -> s.exceptionally(ex -> null)));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Object, long, TimeUnit, Supplier[])} with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyMostSuccessTupleAsync(ASYNC_POOL, timeout, unit, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyMostSuccessAsync(Executor, Object, long, TimeUnit, Supplier[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyMostSuccessTupleAsync(
            Executor executor, long timeout, TimeUnit unit, Supplier<? extends T1> supplier1,
            Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3,
            Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapSuppliers0(executor, suppliers), "mSupplyMostSuccessTupleAsync");
    }

    private static <T> CompletableFuture<T> f_mostSuccessTupleWithEhOf0(
            Executor executorWhenTimeout, long timeout, TimeUnit unit, CompletionStage<?>[] stages, String where) {
        handleAllSwallowedExceptions(where, stages);
        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, stages);
    }

    private static <T> CompletableFuture<T> f_mostSuccessTupleOf0(
            Executor executorWhenTimeout, long timeout, TimeUnit unit, CompletionStage<?>[] stages) {
        // 1. MUST be non-minimal-stage CF instances in order to read results(`getSuccessNow`), otherwise UnsupportedOpException.
        // 2. SHOULD copy input cfs(by calling `exceptionally` method) to avoid memory leaks,
        //    otherwise all input cfs would be retained until output cf completes.
        CompletableFuture<?>[] cfArray = mapArray(stages, CompletableFuture[]::new,
                s -> toNonMinCf0(s).exceptionally(v -> null));
        return cffuCompleteOnTimeout(CompletableFuture.allOf(cfArray), null, timeout, unit, executorWhenTimeout)
                .handle((unused, ex) -> f_tupleOf0(f_mGetSuccessNow0(null, cfArray)));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mSupplyTupleAsync(
            Executor executor,
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2, Supplier<? extends T3> supplier3) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyAsync(Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        return mSupplyTupleAsync(ASYNC_POOL, supplier1, supplier2, supplier3, supplier4, supplier5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mSupplyFailFastAsync(Executor, Supplier[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `mRunAsync`")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mSupplyTupleAsync(
            Executor executor, Supplier<? extends T1> supplier1, Supplier<? extends T2> supplier2,
            Supplier<? extends T3> supplier3, Supplier<? extends T4> supplier4, Supplier<? extends T5> supplier5) {
        requireNonNull(executor, "executor is null");
        Supplier<?>[] suppliers = requireArrayAndEleNonNull("supplier", supplier1, supplier2, supplier3, supplier4, supplier5);

        return f_allTupleWithEhOf0(false, wrapSuppliers0(executor, suppliers), "mSupplyTupleAsync");
    }

    // endregion
    ////////////////////////////////////////////////////////////
    // region## allTupleOf*/mostSuccessTupleOf Methods
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsFailFastOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleFailFastOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allTupleOf0(true, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allSuccessResultsOf(Object, CompletionStage[])} with {@code null} valueIfFailed.
     * <p>
     * If any of the provided stages fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the stage having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allSuccessTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allSuccessTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostSuccessTupleOf(
            long timeout, TimeUnit unit, CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2, cf3);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return mostSuccessTupleOf(ASYNC_POOL, timeout, unit, cf1, cf2, cf3, cf4, cf5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#mostSuccessResultsOf(Executor, Object, long, TimeUnit, CompletionStage[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided stages is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> mostSuccessTupleOf(
            Executor executorWhenTimeout, long timeout, TimeUnit unit,
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        requireNonNull(executorWhenTimeout, "executorWhenTimeout is null");
        requireNonNull(unit, "unit is null");
        CompletionStage<?>[] cfs = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5);

        return f_mostSuccessTupleOf0(executorWhenTimeout, timeout, unit, cfs);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#allResultsOf(CompletionStage[])}.
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return f_allTupleOf0(false, requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    // endregion
    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# CF Instance Methods
    ////////////////////////////////////////////////////////////
    // region## Then-Multi-Actions-Tuple(thenMTuple*) Methods
    ////////////////////////////////////////////////////////////

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleFailFastAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyFailFastAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleFailFastAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v ->
                f_allTupleWithEhOf0(true, wrapFunctions0(executor, v, fns), "thenMApplyTupleFailFastAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyAllSuccessTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAllSuccessAsync(CompletableFuture, Executor, Object, Function[])}
     * with {@code null} valueIfFailed.
     * <p>
     * If any of the provided functions fails, its corresponding position will contain {@code null}
     * (which is indistinguishable from the function having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyAllSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v ->
                f_allSuccessTupleWithEhOf0(wrapFunctions0(executor, v, fns), "thenMApplyAllSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, long timeout, TimeUnit unit, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyMostSuccessTupleAsync(cfThis, defaultExecutor(cfThis), timeout, unit, fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyMostSuccessAsync(CompletableFuture, Executor, Object, long, TimeUnit, Function[])}
     * with {@code null} valueIfNotSuccess.
     * <p>
     * If any of the provided suppliers is not completed normally, its corresponding position will contain {@code null}
     * (which is indistinguishable from the supplier having a successful value of {@code null}).
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyMostSuccessTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, long timeout, TimeUnit unit,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2,
            Function<? super T, ? extends U3> fn3, Function<? super T, ? extends U4> fn4,
            Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        requireNonNull(unit, "unit is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_mostSuccessTupleWithEhOf0(executor, timeout, unit,
                wrapFunctions0(executor, v, fns), "thenMApplyMostSuccessTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2> CompletableFuture<Tuple2<U1, U2>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor,
            Function<? super T, ? extends U1> fn1, Function<? super T, ? extends U2> fn2) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3> CompletableFuture<Tuple3<U1, U2, U3>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4> CompletableFuture<Tuple4<U1, U2, U3, U4>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        return thenMApplyTupleAsync(cfThis, defaultExecutor(cfThis), fn1, fn2, fn3, fn4, fn5);
    }

    /**
     * Tuple variant of {@link CompletableFutureUtils#thenMApplyAsync(CompletableFuture, Executor, Function[])}.
     */
    @CheckReturnValue(explanation = "should use the returned CompletableFuture; otherwise, prefer simple method `thenMAcceptAsync`")
    public static <T, U1, U2, U3, U4, U5> CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> thenMApplyTupleAsync(
            CompletableFuture<? extends T> cfThis, Executor executor, Function<? super T, ? extends U1> fn1,
            Function<? super T, ? extends U2> fn2, Function<? super T, ? extends U3> fn3,
            Function<? super T, ? extends U4> fn4, Function<? super T, ? extends U5> fn5) {
        requireNonNull(cfThis, "cfThis is null");
        requireNonNull(executor, "executor is null");
        Function<? super T, ?>[] fns = requireArrayAndEleNonNull("fn", fn1, fn2, fn3, fn4, fn5);

        return cfThis.thenCompose(v -> f_allTupleWithEhOf0(false, wrapFunctions0(executor, v, fns), "thenMApplyTupleAsync"));
    }

    private CfTupleUtils() {}
}
