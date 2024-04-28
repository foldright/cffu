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
import java.util.function.*;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;


/**
 * This class contains the enhanced and backport methods for {@link CompletableFuture}.
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
     * Returns a new CompletableFuture that is completed when all the given stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected
     * in the returned CompletableFuture, but may be obtained by inspecting them individually.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * if you need the results of given stages, prefer below methods:
     * <ul>
     * <li>{@link #allResultsOf(CompletionStage[])}
     * <li>{@link #allTupleOf(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ul>
     * <p>
     * This method is the same as {@link CompletableFuture#allOf(CompletableFuture[])},
     * except the parameter type is more generic({@link CompletionStage}).
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CompletableFuture<Void> allOf(CompletionStage<?>... cfs) {
        return CompletableFuture.allOf(toCompletableFutureArray((CompletionStage[]) cfs));
    }

    /**
     * Returns a new CompletableFuture with the results in the <strong>same order</strong> of all the given
     * CompletableFutures, the new CompletableFuture is completed when all the given CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
     * also does so, with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * This method is the same as {@link CompletableFuture#allOf(CompletableFuture[])},
     * except the returned CompletableFuture contains the results of the given CompletableFutures.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed when all the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings({"unchecked"})
    public static <T> CompletableFuture<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(arrayList());
        if (size == 1) return csToListCf(cfs[0]);

        final Object[] result = new Object[size];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(cfs, result);

        return CompletableFuture.allOf(resultSetterCfs).thenApply(unused -> (List<T>) arrayList(result));
    }

    /**
     * Returns a new CompletableFuture that is successful when all the given CompletableFutures success,
     * the results({@code CompletableFuture<Void>}) of the given CompletableFutures are not reflected
     * in the returned CompletableFuture, but may be obtained by inspecting them individually.
     * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * This method is the same as {@link CompletableFuture#allOf(CompletableFuture[])} except for the fast-fail behavior.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CompletableFuture<Void> allOfFastFail(CompletionStage<?>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(null);
        if (size == 1) return cfs[0].toCompletableFuture().thenApply(v -> null);

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
     * This method is the same as {@link #allOfFastFail(CompletionStage[])},
     * except the returned CompletableFuture contains the results of the given CompletableFutures.
     * <p>
     * This method is the same as {@link #allResultsOf(CompletionStage[])} except for the fast-fail behavior.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful when all the given CompletableFutures success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CompletableFuture<List<T>> allResultsOfFastFail(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(arrayList());
        if (size == 1) return csToListCf(cfs[0]);

        final CompletableFuture[] successOrBeIncomplete = new CompletableFuture[size];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture[] failedOrBeIncomplete = new CompletableFuture[size + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[size] = allResultsOf(successOrBeIncomplete);

        return (CompletableFuture) CompletableFuture.anyOf(failedOrBeIncomplete);
    }

    @SafeVarargs
    private static <T> CompletionStage<? extends T>[] requireCfsAndEleNonNull(CompletionStage<? extends T>... css) {
        requireNonNull(css, "cfs is null");
        for (int i = 0; i < css.length; i++) {
            requireNonNull(css[i], "cf" + (i + 1) + " is null");
        }
        return css;
    }

    /**
     * Returns normal array list instead of unmodifiable or fixed-size list.
     * Safer for application code which may reuse the returned list as normal collection.
     */
    @SafeVarargs
    private static <T> List<T> arrayList(T... elements) {
        List<T> ret = new ArrayList<>(elements.length);
        ret.addAll(Arrays.asList(elements));
        return ret;
    }

    private static <T> CompletableFuture<List<T>> csToListCf(CompletionStage<? extends T> s) {
        return s.toCompletableFuture().thenApply(CompletableFutureUtils::arrayList);
    }

    /**
     * Returns a cf array whose elements do the result collection.
     */
    private static <T> CompletableFuture<Void>[] createResultSetterCfs(CompletionStage<? extends T>[] css, T[] result) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] resultSetterCfs = new CompletableFuture[result.length];
        for (int i = 0; i < result.length; i++) {
            final int index = i;
            resultSetterCfs[index] = css[index].toCompletableFuture().thenAccept(v -> result[index] = v);
        }
        return resultSetterCfs;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void fill(CompletionStage[] cfs,
                             CompletableFuture[] successOrBeIncomplete,
                             CompletableFuture[] failedOrBeIncomplete) {
        final CompletableFuture incomplete = new CompletableFuture();

        for (int i = 0; i < cfs.length; i++) {
            final CompletionStage cf = cfs[i];

            successOrBeIncomplete[i] = cf.toCompletableFuture()
                    .handle((v, ex) -> ex == null ? cf : incomplete).thenCompose(identity());

            failedOrBeIncomplete[i] = cf.toCompletableFuture()
                    .handle((v, ex) -> ex == null ? incomplete : cf).thenCompose(identity());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed
     * when any of the given CompletableFutures(stages) complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
     * <p>
     * This method is the same as {@link CompletableFuture#anyOf(CompletableFuture[])},
     * except the parameter type is more generic type {@link CompletionStage}.
     * and the return result type is generic type instead of {@code Object}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed with the result
     * or exception from any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfSuccess(CompletionStage[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CompletableFuture<T> anyOf(CompletionStage<? extends T>... cfs) {
        return (CompletableFuture) CompletableFuture.anyOf(toCompletableFutureArray((CompletionStage[]) cfs));
    }

    /**
     * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
     * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
     * returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     * <p>
     * This method is the same as {@link #anyOf(CompletionStage[])}
     * except for the any-<strong>success</strong> behavior(not any-<strong>complete</strong>).
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is successful
     * when any of the given CompletableFutures success, with the same result
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> CompletableFuture<T> anyOfSuccess(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return failedFuture(new NoCfsProvidedException());
        if (size == 1) return (CompletableFuture) copy(cfs[0].toCompletableFuture());

        // NOTE: fill ONE MORE element of successOrBeIncompleteCfs LATER
        final CompletableFuture[] successOrBeIncomplete = new CompletableFuture[size + 1];
        final CompletableFuture[] failedOrBeIncomplete = new CompletableFuture[size];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE
        //       a cf that is failed when all given cfs fail, otherwise be incomplete
        successOrBeIncomplete[size] = CompletableFuture.allOf(failedOrBeIncomplete);

        return (CompletableFuture) CompletableFuture.anyOf(successOrBeIncomplete);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allTupleOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed when the given two CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return CompletableFuture.allOf(resultSetterCfs).thenApply(unused -> Tuple2.of((T1) result[0], (T2) result[1]));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given two CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given two CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenApply(unused -> Tuple2.of((T1) result[0], (T2) result[1]));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given three CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 3 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2, cf3);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return CompletableFuture.allOf(resultSetterCfs)
                .thenApply(unused -> Tuple3.of((T1) result[0], (T2) result[1], (T3) result[2]));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given three CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given three CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2, cf3);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs)
                .thenApply(unused -> Tuple3.of((T1) result[0], (T2) result[1], (T3) result[2]));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given 4 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 4 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return CompletableFuture.allOf(resultSetterCfs)
                .thenApply(unused -> Tuple4.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3]));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given 4 CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given 4 CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs)
                .thenApply(unused -> Tuple4.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3]));
    }

    /**
     * Returns a new CompletableFuture that is completed when the given 5 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 5 CompletableFutures complete
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return CompletableFuture.allOf(resultSetterCfs).thenApply(unused ->
                Tuple5.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3], (T5) result[4]));
    }

    /**
     * Returns a new CompletableFuture that is successful when the given 5 CompletableFutures success.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so *without* waiting other incomplete given CompletableFutures,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given 5 CompletableFutures success
     * @throws NullPointerException if any of the given CompletableFutures are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5);

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenApply(unused ->
                Tuple5.of((T1) result[0], (T2) result[1], (T3) result[2], (T4) result[3], (T5) result[4]));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then both(binary input)` methods with fast-fail support:
    //
    //    - runAfterBothFastFail*(Runnable):     Void, Void -> Void
    //    - thenAcceptBothFastFail*(BiConsumer): (T1, T2) -> Void
    //    - thenCombineFastFail*(BiFunction):    (T1, T2) -> U
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally, executes the given action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterBoth(CompletionStage, Runnable)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#runAfterBoth(CompletionStage, Runnable)
     */
    public static CompletableFuture<Void> runAfterBothFastFail(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return allOfFastFail(css).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using CompletableFuture's default asynchronous execution facility.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterBothAsync(CompletionStage, Runnable)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#runAfterBothAsync(CompletionStage, Runnable)
     */
    public static CompletableFuture<Void> runAfterBothFastFailAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return allOfFastFail(css).thenRunAsync(action);
    }

    /**
     * Returns a new CompletableFuture that, when two given stages both complete normally,
     * executes the given action using the supplied executor.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterBothAsync(CompletionStage, Runnable, Executor)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#runAfterBothAsync(CompletionStage, Runnable, Executor)
     */
    public static CompletableFuture<Void> runAfterBothFastFailAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return allOfFastFail(css).thenRunAsync(action, executor);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenAcceptBoth(CompletionStage, BiConsumer)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#thenAcceptBoth(CompletionStage, BiConsumer)
     */
    @SuppressWarnings("unchecked")
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFail(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiConsumer<? super T, ? super U> action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenAccept(unused -> action.accept((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using CompletableFuture's default asynchronous execution facility,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenAcceptBothAsync(CompletionStage, BiConsumer)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#thenAcceptBothAsync(CompletionStage, BiConsumer)
     */
    @SuppressWarnings("unchecked")
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiConsumer<? super T, ? super U> action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenAcceptAsync(unused -> action.accept((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenAcceptBothAsync(CompletionStage, BiConsumer, Executor)}
     * except for the fast-fail behavior.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#thenAcceptBothAsync(CompletionStage, BiConsumer, Executor)
     */
    @SuppressWarnings("unchecked")
    public static <T, U> CompletableFuture<Void> thenAcceptBothFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiConsumer<? super T, ? super U> action, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs)
                .thenAcceptAsync(unused -> action.accept((T) result[0], (U) result[1]), executor);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenCombine(CompletionStage, BiFunction)}
     * except for the fast-fail behavior.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#thenCombine(CompletionStage, BiFunction)
     */
    @SuppressWarnings("unchecked")
    public static <T, U, V> CompletableFuture<V> thenCombineFastFail(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiFunction<? super T, ? super U, ? extends V> fn) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenApply(unused -> fn.apply((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using CompletableFuture's default asynchronous execution facility,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenCombineAsync(CompletionStage, BiFunction)}
     * except for the fast-fail behavior.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#thenCombineAsync(CompletionStage, BiFunction)
     */
    @SuppressWarnings("unchecked")
    public static <T, U, V> CompletableFuture<V> thenCombineFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiFunction<? super T, ? super U, ? extends V> fn
    ) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs).thenApplyAsync(unused -> fn.apply((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture
     * also does so *without* waiting other incomplete given CompletionStage,
     * with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#thenCombineAsync(CompletionStage, BiFunction, Executor)}
     * except for the fast-fail behavior.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#thenCombineAsync(CompletionStage, BiFunction, Executor)
     */
    @SuppressWarnings("unchecked")
    public static <T, U, V> CompletableFuture<V> thenCombineFastFailAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends U> cf2,
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor
    ) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        final Object[] result = new Object[css.length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        return allOfFastFail(resultSetterCfs)
                .thenApplyAsync(unused -> fn.apply((T) result[0], (U) result[1]), executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then either(binary input)` methods with either(any)-success support:
    //
    //    - runAfterEitherSuccess*(Runnable):  Void, Void -> Void
    //    - acceptEitherSuccess*(Consumer):  (T, T) -> Void
    //    - applyToEitherSuccess*(Function): (T, T) -> U
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     * <p>
     * This method is the same as {@link CompletableFuture#runAfterEither(CompletionStage, Runnable)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletableFuture#runAfterEither(CompletionStage, Runnable)
     */
    public static CompletableFuture<Void> runAfterEitherSuccess(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return anyOfSuccess(css).thenRun(action);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action
     * using CompletableFuture's default asynchronous execution facility.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterEitherAsync(CompletionStage, Runnable)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#runAfterEitherAsync(CompletionStage, Runnable)
     */
    public static CompletableFuture<Void> runAfterEitherSuccessAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return anyOfSuccess(css).thenRunAsync(action);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success, executes the given action
     * using the supplied executor.
     * Otherwise, all two given CompletionStage complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given CompletionStage as its cause.
     * <p>
     * This method is the same as {@link CompletionStage#runAfterEitherAsync(CompletionStage, Runnable, Executor)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#runAfterEitherAsync(CompletionStage, Runnable, Executor)
     */
    public static CompletableFuture<Void> runAfterEitherSuccessAsync(
            CompletionStage<?> cf1, CompletionStage<?> cf2, Runnable action, Executor executor) {
        final CompletionStage<?>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return anyOfSuccess(css).thenRunAsync(action, executor);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEither(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#acceptEither(CompletionStage, Consumer)
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccess(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Consumer<? super T> action) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return anyOfSuccess(css).thenAccept(action);
    }

    /**
     * Returns a new CompletionStage that, when either given stage success,
     * is executed using this stage's default asynchronous execution facility,
     * with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEitherAsync(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#acceptEitherAsync(CompletionStage, Consumer)
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Consumer<? super T> action) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");

        return anyOfSuccess(css).thenAcceptAsync(action);
    }

    /**
     * Returns a new CompletionStage that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEitherAsync(CompletionStage, Consumer, Executor)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param action   the action to perform before completing the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     * @see CompletionStage#acceptEitherAsync(CompletionStage, Consumer, Executor)
     */
    public static <T> CompletableFuture<Void> acceptEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2,
            Consumer<? super T> action, Executor executor) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(action, "action is null");
        requireNonNull(executor, "executor is null");

        return anyOfSuccess(css).thenAcceptAsync(action, executor);
    }

    /**
     * Returns a new CompletionStage that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEither(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     * @return the new CompletableFuture
     * @see CompletionStage#applyToEither(CompletionStage, Function)
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccess(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Function<? super T, U> fn) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        return anyOfSuccess(css).thenApply(fn);
    }

    /**
     * Returns a new CompletionStage that, when either given stage success,
     * is executed using this stage's default asynchronous execution facility,
     * with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEitherAsync(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     * @return the new CompletableFuture
     * @see CompletionStage#applyToEitherAsync(CompletionStage, Function)
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Function<? super T, U> fn) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        return anyOfSuccess(css).thenApplyAsync(fn);
    }

    /**
     * Returns a new CompletionStage that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEitherAsync(CompletionStage, Function, Executor)}
     * except for the either-<strong>success</strong> behavior(not either-<strong>complete</strong>).
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new CompletableFuture
     * @see CompletionStage#applyToEitherAsync(CompletionStage, Function, Executor)
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2,
            Function<? super T, U> fn, Executor executor) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");

        return anyOfSuccess(css).thenApplyAsync(fn, executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New enhanced methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Peeks the result by executing the given action when given stage completes, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * Whether the supplied action throws an exception or not, do <strong>NOT</strong> affect this cffu.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return the given stage
     * @see CompletionStage#whenComplete(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    public static <T, C extends CompletionStage<? extends T>> C peek(
            C cf, BiConsumer<? super T, ? super Throwable> action) {
        cf.whenComplete(action);
        return cf;
    }

    /**
     * Peeks the result by executing the given action when given stage completes,
     * executes the given action using given stage's default asynchronous execution facility,
     * returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * Whether the supplied action throws an exception or not, do <strong>NOT</strong> affect this cffu.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return the given stage
     * @see CompletionStage#whenCompleteAsync(BiConsumer)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    public static <T, C extends CompletionStage<? extends T>> C peekAsync(
            C cf, BiConsumer<? super T, ? super Throwable> action) {
        cf.whenCompleteAsync(action);
        return cf;
    }

    /**
     * Peeks the result by executing the given action when given stage completes,
     * executes the given action using the supplied Executor, returns the given stage.
     * <p>
     * When the given stage is complete, the given action is invoked with the result (or {@code null} if none)
     * and the exception (or {@code null} if none) of given stage as arguments.
     * Whether the supplied action throws an exception or not, do <strong>NOT</strong> affect this cffu.
     * <p>
     * Unlike method {@link CompletionStage#handle handle} and like method
     * {@link CompletionStage#whenComplete(BiConsumer) whenComplete},
     * this method is not designed to translate completion outcomes.
     *
     * @param action the action to perform
     * @return the given stage
     * @see CompletionStage#whenCompleteAsync(BiConsumer, Executor)
     * @see java.util.stream.Stream#peek(Consumer)
     */
    public static <T, C extends CompletionStage<? extends T>> C peekAsync(
            C cf, BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        cf.whenCompleteAsync(action, executor);
        return cf;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF static methods
    //  compatibility for low Java versions
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
        requireNonNull(ex, "ex is null");
        final CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(ex);
        return cf;
    }

    /**
     * Returns a new CompletionStage that is already completed with the given value
     * and supports only those methods in interface {@link CompletionStage}.
     * <p>
     * <strong>CAUTION:<br></strong>
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
     * <strong>CAUTION:<br></strong>
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
     * @param unit  a {@code TimeUnit} determining how to interpret the {@code delay} parameter
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
     * @param unit     a {@code TimeUnit} determining how to interpret the {@code delay} parameter
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
    //# Backport CF instance methods
    ////////////////////////////////////////

    //# Error Handling methods of CompletionStage

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
     * stage's exception as the argument to the supplied function, using given stage's
     * default asynchronous execution facility. Otherwise, if given stage completes normally,
     * then the returned stage also completes normally with the same value.
     *
     * @param fn the function to use to compute the value of the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
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
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<T> exceptionallyAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends T> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return cf.exceptionallyAsync(fn, executor);
        }

        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        // below code is copied from CompletionStage#exceptionallyAsync
        return cf.handle((r, ex) -> (ex == null) ? cf :
                cf.<T>handleAsync((r1, ex1) -> fn.apply(ex1), executor)
        ).thenCompose(identity());
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

        requireNonNull(unit, "unit is null");
        // below code is copied from CompletableFuture#orTimeout with small adoption
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

        requireNonNull(unit, "unit is null");
        // below code is copied from CompletableFuture#completeOnTimeout with small adoption
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
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<T> exceptionallyCompose(
            CompletableFuture<T> cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        if (IS_JAVA12_PLUS) {
            return cf.exceptionallyCompose(fn);
        }
        requireNonNull(fn, "fn is null");
        // below code is copied from CompletionStage.exceptionallyCompose
        return cf.handle((r, ex) -> (ex == null) ? cf : fn.apply(ex)).thenCompose(identity());
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using given stage's default asynchronous execution facility.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<T> exceptionallyComposeAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(cf, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletionStage that, when given stage completes exceptionally, is composed using
     * the results of the supplied function applied to given stage's exception, using the supplied Executor.
     *
     * @param fn       the function to use to compute the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    public static <T> CompletableFuture<T> exceptionallyComposeAsync(
            CompletableFuture<T> cf, Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return cf.exceptionallyComposeAsync(fn, executor);
        }
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        // below code is copied from CompletionStage.exceptionallyComposeAsync
        return cf.handle((r, ex) -> (ex == null) ? cf :
                cf.handleAsync((r1, ex1) -> fn.apply(ex1), executor).thenCompose(identity())
        ).thenCompose(identity());
    }

    //# Read(explicitly) methods of CompletableFuture

    /**
     * Waits if necessary for at most the given time for the computation to complete,
     * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
     * <p>
     * <strong>NOTE:<br></strong>
     * Calling this method
     * <p>
     * {@code result = CompletableFutureUtils.join(cf, timeout, unit);}
     * <p>
     * is the same as:
     *
     * <pre>{@code result = cf.copy() // defensive copy to avoid writing this cf unexpectedly
     *     .orTimeout(timeout, unit)
     *     .join();
     * }</pre>
     *
     * <strong>CAUTION:<br></strong>
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
    public static <T> T join(CompletableFuture<T> cf, long timeout, TimeUnit unit) {
        if (cf.isDone()) return cf.join();
        return orTimeout(copy(cf), timeout, unit).join();
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
     * Returns the computation state({@link CffuState}), this method  is equivalent to {@link CompletableFuture#state()}
     * with java version compatibility logic, so you can invoke in old {@code java 18-}.
     *
     * @return the computation state
     * @see Future#state()
     * @see CompletableFuture#state()
     * @see CffuState
     * @see Future.State
     */
    @Contract(pure = true)
    public static <T> CffuState state(CompletableFuture<T> cf) {
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
        requireNonNull(supplier, "supplier is null");
        requireNonNull(executor, "executor is null");
        // below code is copied from CompletableFuture#completeAsync with small adoption
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
     * <strong>CAUTION:<br></strong>
     * if run on old Java 8, just return a *normal* CompletableFuture which is NOT with a *minimal* CompletionStage.
     *
     * @return the new CompletionStage
     */
    @Contract(pure = true)
    public static <T> CompletionStage<T> minimalCompletionStage(CompletableFuture<T> cf) {
        if (IS_JAVA9_PLUS) {
            return cf.minimalCompletionStage();
        }
        return cf.thenApply(identity());
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
        return cf.thenApply(identity());
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
     * <strong>CAUTION:</strong> This executor may be not suitable for common biz use(io intensive).
     *
     * @return the executor
     */
    @Contract(pure = true)
    public static Executor defaultExecutor() {
        return AsyncPoolHolder.ASYNC_POOL;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion (Static) Methods
    //
    //    - toCompletableFutureArray:     Cffu -> CF
    //    - cffuArrayUnwrap:              Cffu -> CF
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A convenient util method for converting input {@link Cffu} / {@link CompletableFuture} / {@link CompletionStage}
     * array element by {@link Cffu#toCompletableFuture()} / {@link CompletableFuture#toCompletableFuture()} /
     * {@link CompletionStage#toCompletableFuture()}.
     *
     * @see Cffu#toCompletableFuture()
     * @see CompletableFuture#toCompletableFuture()
     * @see CompletionStage#toCompletableFuture()
     * @see CffuFactory#toCffuArray(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T>[] toCompletableFutureArray(CompletionStage<T>... stages) {
        requireNonNull(stages, "stages is null");
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = requireNonNull(stages[i], "stage" + (i + 1) + " is null").toCompletableFuture();
        }
        return ret;
    }

    /**
     * Convert CompletableFuture list to CompletableFuture array.
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T>[] completableFutureListToArray(List<CompletableFuture<T>> cfList) {
        return cfList.toArray(new CompletableFuture[0]);
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
