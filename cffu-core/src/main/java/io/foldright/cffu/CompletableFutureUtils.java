package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Blocking;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


/**
 * This class contains the enhanced methods for {@link CompletableFuture}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CompletableFutureUtils {
    ////////////////////////////////////////////////////////////////////////////////
    //# allOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture with the results in the <strong>same order</strong> of all the given
     * CompletableFutures, the new CompletableFuture is completed when all the given CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
     * also does so, with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * Same to {@link CompletableFuture#allOf(CompletableFuture[])},
     * but the returned CompletableFuture contains the results of the given CompletableFutures.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed when all the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<List<T>> allOfWithResult(CompletableFuture<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(arrayList());
        if (size == 1) return cfs[0].thenApply(CompletableFutureUtils::arrayList);

        final Object[] result = new Object[size];

        final CompletableFuture<?>[] collectResultCfs = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final int index = i;
            collectResultCfs[index] = cfs[index].thenAccept(v -> result[index] = v);
        }

        return CompletableFuture.allOf(collectResultCfs)
                .thenApply(unused -> (List<T>) arrayList(result));
    }

    /**
     * Returns a new CompletableFuture that is successful when all the given CompletableFutures success,
     * the results({@code CompletableFuture<Void>}) of the given CompletableFutures are not reflected
     * in the returned CompletableFuture, but may be obtained by inspecting them individually.
     * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a CompletableFuture completed with the value {@code null}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CompletableFuture<Void> allOfFastFail(CompletableFuture<?>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(null);
        if (size == 1) return cfs[0].thenApply(v -> null);

        final CompletableFuture[] successOrBeIncomplete = new CompletableFuture[size];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture[] failedOrBeIncomplete = new CompletableFuture[size + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[size] = CompletableFuture.allOf(successOrBeIncomplete);

        return (CompletableFuture) CompletableFuture.anyOf(failedOrBeIncomplete);
    }

    /**
     * Returns a new CompletableFuture with the results in the <strong>same order</strong> of all the given
     * CompletableFutures, the new CompletableFuture success when all the given CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * Same to {@link #allOfFastFail(CompletableFuture[])},
     * but the returned CompletableFuture contains the results of the given CompletableFutures.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CompletableFuture<List<T>> allOfFastFailWithResult(CompletableFuture<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(arrayList());
        if (size == 1) return cfs[0].thenApply(CompletableFutureUtils::arrayList);

        final CompletableFuture[] successOrBeIncomplete = new CompletableFuture[size];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture[] failedOrBeIncomplete = new CompletableFuture[size + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[size] = allOfWithResult(successOrBeIncomplete);

        return (CompletableFuture) CompletableFuture.anyOf(failedOrBeIncomplete);
    }

    private static void requireCfsAndEleNonNull(CompletableFuture<?>... cfs) {
        requireNonNull(cfs, "cfs is null");
        for (int i = 0; i < cfs.length; i++) {
            requireNonNull(cfs[i], "cf" + (i + 1) + " is null");
        }
    }

    /**
     * Returns normal array list instead of unmodifiable or fixed-size list.
     * Safer for application code which may reuse the returned list as normal collection.
     */
    @SuppressWarnings("unchecked")
    private static <T> List<T> arrayList(T... elements) {
        List<T> ret = new ArrayList<>(elements.length);
        ret.addAll(Arrays.asList(elements));
        return ret;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void fill(CompletableFuture[] cfs,
                             CompletableFuture[] successOrBeIncomplete,
                             CompletableFuture[] failedOrBeIncomplete) {
        final CompletableFuture incomplete = new CompletableFuture();

        for (int i = 0; i < cfs.length; i++) {
            final CompletableFuture cf = cfs[i];

            successOrBeIncomplete[i] = cf.handle((v, ex) -> ex == null ? cf : incomplete)
                    .thenCompose(Function.identity());

            failedOrBeIncomplete[i] = cf.handle((v, ex) -> ex == null ? incomplete : cf)
                    .thenCompose(Function.identity());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed
     * when any of the given CompletableFutures complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
     * <p>
     * Same as {@link CompletableFuture#anyOf(CompletableFuture[])},
     * but return result type is specified type instead of {@code Object}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed with the result
     * or exception from any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfSuccessWithType(CompletableFuture[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> anyOfWithType(CompletableFuture<? extends T>... cfs) {
        return (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
    }

    /**
     * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
     * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
     * returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful
     * when any of the given CompletableFutures success, with the same result
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfSuccessWithType(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CompletableFuture<Object> anyOfSuccess(CompletableFuture<?>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return failedFuture(new NoCfsProvidedException());
        if (size == 1) return (CompletableFuture<Object>) copy(cfs[0]);

        // NOTE: fill ONE MORE element of successOrBeIncompleteCfs LATER
        final CompletableFuture[] successOrBeIncomplete = new CompletableFuture[size + 1];
        final CompletableFuture[] failedOrBeIncomplete = new CompletableFuture[size];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE
        //       a cf that is failed when all given cfs fail, otherwise be incomplete
        successOrBeIncomplete[size] = CompletableFuture.allOf(failedOrBeIncomplete);

        return CompletableFuture.anyOf(successOrBeIncomplete);
    }

    /**
     * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
     * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
     * returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     * <p>
     * Same as {@link #anyOfSuccess(CompletableFuture[])},
     * but return result type is specified type instead of {@code Object}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful
     * when any of the given CompletableFutures success, with the same result
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfWithType(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> anyOfSuccessWithType(CompletableFuture<? extends T>... cfs) {
        return (CompletableFuture<T>) anyOfSuccess(cfs);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# combine/combineFastFail methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed when the given two CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfWithResult(CompletableFuture[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        requireCfsAndEleNonNull(cf1, cf2);

        final Object[] result = new Object[2];
        return CompletableFuture.allOf(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2)
        ).thenApply(unused ->
                Tuple2.of((T1) result[0], (T2) result[1])
        );
    }

    /**
     * Returns a new CompletableFuture that is successful when the given two CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given two CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfFastFailWithResult(CompletableFuture[])
     * @see #allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> combineFastFail(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        requireCfsAndEleNonNull(cf1, cf2);

        final Object[] result = new Object[2];
        return allOfFastFail(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2)
        ).thenApply(unused ->
                Tuple2.of((T1) result[0], (T2) result[1])
        );
    }

    /**
     * Returns a new CompletableFuture that is completed when the given three CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 3 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfWithResult(CompletableFuture[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        requireCfsAndEleNonNull(cf1, cf2, cf3);

        final Object[] result = new Object[3];
        return CompletableFuture.allOf(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2),
                cf3.thenAccept(t3 -> result[2] = t3)
        ).thenApply(unused ->
                Tuple3.of((T1) result[0], (T2) result[1], (T3) result[2])
        );
    }

    /**
     * Returns a new CompletableFuture that is successful when the given three CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given three CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfFastFailWithResult(CompletableFuture[])
     * @see #allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> combineFastFail(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        requireCfsAndEleNonNull(cf1, cf2, cf3);

        final Object[] result = new Object[3];
        return allOfFastFail(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2),
                cf3.thenAccept(t3 -> result[2] = t3)
        ).thenApply(unused ->
                Tuple3.of((T1) result[0], (T2) result[1], (T3) result[2])
        );
    }

    /**
     * Returns a new CompletableFuture that is completed when the given 4 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 4 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfWithResult(CompletableFuture[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4) {
        requireCfsAndEleNonNull(cf1, cf2, cf3, cf4);

        final Object[] result = new Object[4];
        return CompletableFuture.allOf(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2),
                cf3.thenAccept(t3 -> result[2] = t3),
                cf4.thenAccept(t4 -> result[3] = t4)
        ).thenApply(unused ->
                Tuple4.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3])
        );
    }

    /**
     * Returns a new CompletableFuture that is successful when the given 4 CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given 4 CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfFastFailWithResult(CompletableFuture[])
     * @see #allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> combineFastFail(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4) {
        requireCfsAndEleNonNull(cf1, cf2, cf3, cf4);

        final Object[] result = new Object[4];
        return allOfFastFail(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2),
                cf3.thenAccept(t3 -> result[2] = t3),
                cf4.thenAccept(t4 -> result[3] = t4)
        ).thenApply(unused ->
                Tuple4.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3])
        );
    }

    /**
     * Returns a new CompletableFuture that is completed when the given 5 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 5 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfWithResult(CompletableFuture[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4, CompletableFuture<T5> cf5) {
        requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5);

        final Object[] result = new Object[5];
        return CompletableFuture.allOf(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2),
                cf3.thenAccept(t3 -> result[2] = t3),
                cf4.thenAccept(t4 -> result[3] = t4),
                cf5.thenAccept(t5 -> result[4] = t5)
        ).thenApply(unused ->
                Tuple5.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3], (T5) result[4])
        );
    }

    /**
     * Returns a new CompletableFuture that is successful when the given 5 CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is successful when the given 5 CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allOfFastFailWithResult(CompletableFuture[])
     * @see #allOfFastFail(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> combineFastFail(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4, CompletableFuture<T5> cf5) {
        requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5);

        final Object[] result = new Object[5];
        return allOfFastFail(
                cf1.thenAccept(t1 -> result[0] = t1),
                cf2.thenAccept(t2 -> result[1] = t2),
                cf3.thenAccept(t3 -> result[2] = t3),
                cf4.thenAccept(t4 -> result[3] = t4),
                cf5.thenAccept(t5 -> result[4] = t5)
        ).thenApply(unused ->
                Tuple5.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3], (T5) result[4])
        );
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF methods
    //  compatibility for low Java version
    ////////////////////////////////////////////////////////////////////////////////

    //# Factory methods

    /**
     * Returns a new CompletableFuture that is already completed exceptionally with the given exception.
     *
     * @param ex  the exception
     * @param <T> the type of the value
     * @return the exceptionally completed CompletableFuture
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> failedFuture(Throwable ex) {
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
     * <b><i>CAUTION:<br></i></b>
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
        return CompletableFuture.completedFuture(value);
    }

    /**
     * Returns a new CompletionStage that is already completed exceptionally with
     * the given exception and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <b><i>CAUTION:<br></i></b>
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
        CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(ex);
        return cf;
    }

    //# Delay Execution

    /**
     * Returns a new Executor that submits a task to the default executor after the given delay (or no delay
     * if non-positive). Each delay commences upon invocation of the returned executor's {@code execute} method.
     *
     * @param delay how long to delay, in units of {@code unit}
     * @param unit  a {@code TimeUnit} determining how to interpret the
     *              {@code delay} parameter
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
     * @param unit     a {@code TimeUnit} determining how to interpret the
     *                 {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     */
    @Contract(pure = true)
    public static Executor delayedExecutor(long delay, TimeUnit unit, Executor executor) {
        if (IS_JAVA9_PLUS) {
            return CompletableFuture.delayedExecutor(delay, unit, executor);
        }
        requireNonNull(unit, "unit is null");
        requireNonNull(executor, "executor is null");
        return new DelayedExecutor(delay, unit, executor);
    }

    ////////////////////////////////////////
    //# backport instance methods
    ////////////////////////////////////////

    //# Error Handling methods of CompletionStage

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using given stage's
     * default asynchronous execution facility. Otherwise, if given stage completes normally,
     * then the returned stage also completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned CompletionStage
     *           if given CompletionStage completed exceptionally
     * @return the new CompletionStage
     */
    public static <T> CompletableFuture<T> exceptionallyAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(cf, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using the supplied Executor. Otherwise,
     * if given stage completes normally, then the returned stage also completes normally with the same value.
     *
     * @param fn       the function to use to compute the value of the returned CompletionStage
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public static <T> CompletableFuture<T> exceptionallyAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends T> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return cf.exceptionallyAsync(fn, executor);
        }

        // below code is copied from CompletionStage#exceptionallyAsync

        return cf.handle((r, ex) -> (ex == null) ? cf :
                cf.<T>handleAsync((r1, ex1) -> fn.apply(ex1), executor)
        ).thenCompose(Function.identity());
    }

    //# Timeout Control methods

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return given CompletableFuture
     */
    public static <T> CompletableFuture<T> orTimeout(CompletableFuture<T> cf, long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            return cf.orTimeout(timeout, unit);
        }

        // below code is copied from CompletableFuture#orTimeout with small adoption

        requireNonNull(unit, "unit is null");
        if (!cf.isDone()) {
            ScheduledFuture<?> f = Delayer.delayToTimoutCf(cf, timeout, unit);
            cf.whenComplete(new FutureCanceller(f));
        }
        return cf;
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return given CompletableFuture
     */
    public static <T> CompletableFuture<T> completeOnTimeout(
            CompletableFuture<T> cf, @Nullable T value, long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            return cf.completeOnTimeout(value, timeout, unit);
        }

        // below code is copied from CompletableFuture#completeOnTimeout with small adoption

        requireNonNull(unit, "unit is null");
        if (!cf.isDone()) {
            ScheduledFuture<?> f = Delayer.delayToCompleteCf(cf, value, timeout, unit);
            cf.whenComplete(new FutureCanceller(f));
        }
        return cf;
    }

    //# Advanced methods of CompletionStage

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception.
     *
     * @param fn the function to use to compute the returned
     *           CompletionStage if given CompletionStage completed exceptionally
     * @return the new CompletionStage
     */
    public static <T> CompletableFuture<T> exceptionallyCompose(
            CompletableFuture<T> cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        if (IS_JAVA12_PLUS) {
            return cf.exceptionallyCompose(fn);
        }

        // below code is copied from CompletionStage.exceptionallyCompose

        return cf.handle((r, ex) -> (ex == null) ? cf : fn.apply(ex))
                .thenCompose(Function.identity());
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using given stage's default asynchronous execution facility.
     *
     * @param fn the function to use to compute the returned
     *           CompletionStage if given CompletionStage completed exceptionally
     * @return the new CompletionStage
     */
    public static <T> CompletableFuture<T> exceptionallyComposeAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(cf, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is composed using
     * the results of the supplied function applied to given stage's exception, using the supplied Executor.
     *
     * @param fn       the function to use to compute the returned CompletionStage
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletionStage
     */
    public static <T> CompletableFuture<T> exceptionallyComposeAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return cf.exceptionallyComposeAsync(fn, executor);
        }

        // below code is copied from CompletionStage.exceptionallyComposeAsync

        return cf.handle((r, ex) -> (ex == null) ? cf :
                cf.handleAsync((r1, ex1) -> fn.apply(ex1), executor).thenCompose(Function.identity())
        ).thenCompose(Function.identity());
    }

    //# Read(explicitly) methods of CompletableFuture

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * <p>
     * <b><i>NOTE:<br></i></b>
     * call this method
     * <p>
     * {@code result = CompletableFutureUtils.cffuJoin(cf, timeout, unit);}
     * <p>
     * is same as:
     *
     * <pre>{@code result = cf.copy() // defensive copy to avoid writing this cf unexpectedly
     *     .orTimeout(timeout, unit)
     *     .join();
     * }</pre>
     *
     * <b><i>CAUTION:<br></i></b>
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
    public static <T> T cffuJoin(CompletableFuture<T> cf, long timeout, TimeUnit unit) {
        if (cf.isDone()) return cf.join();

        CompletableFuture<T> f = copy(cf);
        orTimeout(f, timeout, unit);
        return f.join();
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
     */
    @Contract(pure = true)
    @Nullable
    public static <T> T resultNow(CompletableFuture<T> cf) {
        if (IS_JAVA19_PLUS) {
            return cf.resultNow();
        }

        // below code is copied from Future.resultNow

        if (!cf.isDone()) throw new IllegalStateException("Task has not completed");
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
     * @see CompletableFuture#resultNow()
     */
    @Contract(pure = true)
    public static <T> Throwable exceptionNow(CompletableFuture<T> cf) {
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
     * Returns the computation state({@link CffuState}), this method has java version compatibility logic,
     * so you can invoke in old {@code java 18-}.
     *
     * @return the computation state
     * @see Future#state()
     * @see CompletableFuture#state()
     * @see CffuState
     * @see Future.State
     */
    @Contract(pure = true)
    public static <T> CffuState cffuState(CompletableFuture<T> cf) {
        if (IS_JAVA19_PLUS) {
            return CffuState.toCffuState(cf.state());
        }

        // below code is copied from Future#state() with small adoption

        if (!cf.isDone()) return CffuState.RUNNING;
        if (cf.isCancelled()) return CffuState.CANCELLED;

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

    //# Write methods of CompletableFuture

    /**
     * Completes given CompletableFuture with the result of the given Supplier function invoked
     * from an asynchronous task using the default executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @return given CompletableFuture
     */
    public static <T> CompletableFuture<T> completeAsync(CompletableFuture<T> cf, Supplier<? extends T> supplier) {
        return completeAsync(cf, supplier, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Completes given CompletableFuture with the result of the given Supplier function invoked
     * from an asynchronous task using the given executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return given CompletableFuture
     */
    public static <T> CompletableFuture<T> completeAsync(
            CompletableFuture<T> cf, Supplier<? extends T> supplier, Executor executor) {
        if (IS_JAVA9_PLUS) {
            return cf.completeAsync(supplier, executor);
        }

        // below code is copied from CompletableFuture#completeAsync with small adoption

        requireNonNull(supplier, "supplier is null");
        requireNonNull(executor, "executor is null");
        executor.execute(new CfCompleterBySupplier<>(cf, supplier));
        return cf;
    }

    //# Re-Config methods

    /**
     * Returns a new CompletionStage that is completed normally with the same value as given CompletableFuture
     * when it completes normally, and cannot be independently completed or otherwise used in ways
     * not defined by the methods of interface {@link CompletionStage}.
     * If given CompletableFuture completes exceptionally, then the returned CompletionStage completes exceptionally
     * with a CompletionException with given exception as cause.
     * <p>
     * <b><i>CAUTION:<br></i></b>
     * if run on old Java 8, just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @return the new CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> minimalCompletionStage(CompletableFuture<T> cf) {
        if (IS_JAVA9_PLUS) {
            return cf.minimalCompletionStage();
        }
        return cf.thenApply(Function.identity());
    }

    /**
     * Returns a new CompletableFuture that is completed normally with the same value as this CompletableFuture when
     * it completes normally. If this CompletableFuture completes exceptionally, then the returned CompletableFuture
     * completes exceptionally with a CompletionException with this exception as cause. The behavior is equivalent
     * to {@code thenApply(x -> x)}. This method may be useful as a form of "defensive copying", to prevent clients
     * from completing, while still being able to arrange dependent actions.
     *
     * @return the new CompletableFuture
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> copy(CompletableFuture<T> cf) {
        if (IS_JAVA9_PLUS) {
            return cf.copy();
        }
        return cf.thenApply(Function.identity());
    }

    /**
     * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
     *
     * @param <T> the type of the value
     * @return a new CompletableFuture
     */
    @Contract(pure = true)
    public static <T, U> CompletableFuture<U> newIncompleteFuture(CompletableFuture<T> cf) {
        if (IS_JAVA9_PLUS) {
            return cf.newIncompleteFuture();
        }
        return new CompletableFuture<>();
    }

    //# Getter methods

    /**
     * Returns the default Executor used for async methods that do not specify an Executor.
     * This class uses the {@link ForkJoinPool#commonPool()} if it supports more than one parallel thread,
     * or else an Executor using one thread per async task.<br>
     * <b><i>CAUTION:</i></b>This executor may be not suitable for common biz use(io intensive).
     *
     * @return the executor
     */
    @Contract(pure = true)
    public static Executor defaultExecutor() {
        return AsyncPoolHolder.ASYNC_POOL;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Helper fields and classes
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
            if (IS_JAVA9_PLUS) return CompletableFuture.completedFuture(null).defaultExecutor();
            if (USE_COMMON_POOL) return ForkJoinPool.commonPool();
            return new ThreadPerTaskExecutor();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Java version check logic for compatibility
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

        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
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
