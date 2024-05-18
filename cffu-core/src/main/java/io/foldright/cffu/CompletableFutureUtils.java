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


/**
 * This class contains the enhanced and backport methods for {@link CompletableFuture}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CompletableFutureUtils {
    ////////////////////////////////////////////////////////////////////////////////
    //# allOf*/mostResultsOfSuccess methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected in the returned
     * CompletableFuture({@code CompletableFuture<Void>}), but may be obtained by inspecting them individually.
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
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture}.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see #allTupleOf(CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    public static CompletableFuture<Void> allOf(CompletionStage<?>... cfs) {
        return CompletableFuture.allOf(f_toCfArray(cfs));
    }

    /**
     * Returns a new CompletableFuture with the results in the <strong>same order</strong> of all the given stages,
     * the new CompletableFuture is completed when all the given stages complete;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * This method is the same as {@link #allOf(CompletionStage[])},
     * except that the returned CompletableFuture contains the results of the given stages.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed when all the given stages complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOf(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(arrayList());
        if (size == 1) return csToListCf(cfs[0]);

        final Object[] result = new Object[size];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(cfs, result);

        CompletableFuture<List<Object>> ret = CompletableFuture.allOf(resultSetterCfs)
                .thenApply(unused -> arrayList(result));
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture that is successful when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * Otherwise, the results, if any, of the given stages are not reflected
     * in the returned CompletableFuture({@code CompletableFuture<Void>}),
     * but may be obtained by inspecting them individually.
     * If no stages are provided, returns a CompletableFuture completed with the value {@code null}.
     * <p>
     * If you need the results of given stages, prefer below methods:
     * <ol>
     * <li>{@link #allResultsOfFastFail(CompletionStage[])}
     * <li>{@link #allTupleOfFastFail(CompletionStage, CompletionStage)} /
     *     {@link #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     *     (provided overloaded methods with 2~5 input)
     * </ol>
     * <p>
     * This method is the same as {@link #allOf(CompletionStage[])} except for the fast-fail behavior.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage)
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allTupleOfFastFail(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static CompletableFuture<Void> allOfFastFail(CompletionStage<?>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(null);
        if (size == 1) return toCf(cfs[0]).thenApply(unused -> null);

        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[size];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[size + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[size] = CompletableFuture.allOf(successOrBeIncomplete);

        CompletableFuture<Object> ret = CompletableFuture.anyOf(failedOrBeIncomplete);
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture that is successful with the results in the <strong>same order</strong>
     * of all the given stages when all the given stages success;
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns a CompletableFuture completed with the value empty list.
     * <p>
     * This method is the same as {@link #allResultsOf(CompletionStage[])} except for the fast-fail behavior.
     * <p>
     * This method is the same as {@link #allOfFastFail(CompletionStage[])},
     * except that the returned CompletableFuture contains the results of the given stages.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when all the given stages success
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #allResultsOf(CompletionStage[])
     * @see #allOfFastFail(CompletionStage[])
     * @see #allOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> allResultsOfFastFail(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return CompletableFuture.completedFuture(arrayList());
        if (size == 1) return csToListCf(cfs[0]);

        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[size];
        // NOTE: fill ONE MORE element of failedOrBeIncomplete LATER
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[size + 1];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of failedOrBeIncomplete HERE:
        //       a cf that is successful when all given cfs success, otherwise be incomplete
        failedOrBeIncomplete[size] = allResultsOf(successOrBeIncomplete);

        CompletableFuture<Object> ret = CompletableFuture.anyOf(failedOrBeIncomplete);
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture with the most results in the <strong>same order</strong> of
     * the given stages in the given time({@code timeout}), aka as many results as possible in the given time.
     * <p>
     * If the given stage is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
     * (aka the result extraction logic is {@link #getSuccessNow(CompletionStage, Object)}).
     *
     * @param timeout           how long to wait in units of {@code unit}
     * @param unit              a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @param cfs               the stages
     * @see #getSuccessNow(CompletionStage, Object)
     */
    // * @see #MGetSuccessNow(Object, CompletionStage[])
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<List<T>> mostResultsOfSuccess(
            long timeout, TimeUnit unit, @Nullable T valueIfNotSuccess, CompletionStage<? extends T>... cfs) {
        requireNonNull(unit, "unit is null");
        requireCfsAndEleNonNull(cfs);

        if (cfs.length == 0) return CompletableFuture.completedFuture(arrayList());
        if (cfs.length == 1) {
            final CompletableFuture<T> f = toCfCopy(cfs[0]);
            return orTimeout(f, timeout, unit).handle((unused, ex) -> arrayList(getSuccessNow(f, valueIfNotSuccess)));
        }

        // MUST be Non-Minimal CF instances in order to read results, otherwise UnsupportedOperationException
        final CompletableFuture<T>[] cfArray = f_toNonMinCfArray(cfs);
        return orTimeout(CompletableFuture.allOf(cfArray), timeout, unit)
                .handle((unused, ex) -> MGetSuccessNow(valueIfNotSuccess, cfArray));
    }

    @SafeVarargs
    private static <S extends CompletionStage<?>> S[] requireCfsAndEleNonNull(S... css) {
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
        return toCf(s).thenApply(CompletableFutureUtils::arrayList);
    }

    /**
     * Returns a cf array whose elements do the result collection.
     */
    private static <T> CompletableFuture<Void>[] createResultSetterCfs(CompletionStage<? extends T>[] css, T[] result) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<Void>[] resultSetterCfs = new CompletableFuture[result.length];
        for (int i = 0; i < result.length; i++) {
            final int index = i;
            resultSetterCfs[index] = toCf(css[index]).thenAccept(v -> result[index] = v);
        }
        return resultSetterCfs;
    }

    private static <T> void fill(CompletionStage<? extends T>[] css,
                                 CompletableFuture<? extends T>[] successOrBeIncomplete,
                                 CompletableFuture<? extends T>[] failedOrBeIncomplete) {
        final CompletableFuture<T> incomplete = new CompletableFuture<>();
        for (int i = 0; i < css.length; i++) {
            final CompletableFuture<T> f = toCf(css[i]);
            successOrBeIncomplete[i] = f.handle((v, ex) -> ex == null ? f : incomplete).thenCompose(x -> x);
            failedOrBeIncomplete[i] = f.handle((v, ex) -> ex == null ? incomplete : f).thenCompose(x -> x);
        }
    }

    /**
     * Force casts CompletableFuture with the value type,
     * IGNORE the compile-time type check.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T> CompletableFuture<T> f_cast(CompletableFuture<?> f) {
        return (CompletableFuture) f;
    }

    /**
     * Force converts {@link CompletionStage} array to {@link CompletableFuture} array,
     * IGNORE the compile-time type check.
     */
    private static <T> CompletableFuture<T>[] f_toCfArray(CompletionStage<? extends T>[] stages) {
        return toCfArray0(stages, CompletableFutureUtils::toCf);
    }

    /**
     * Force converts {@link CompletionStage} array to {@link CompletableFuture} array,
     * IGNORE the compile-time type check.
     */
    private static <T> CompletableFuture<T>[] f_toNonMinCfArray(CompletionStage<? extends T>[] stages) {
        return toCfArray0(stages, CompletableFutureUtils::toNonMinCf);
    }

    private static <T> CompletableFuture<T>[] toCfArray0(
            CompletionStage<? extends T>[] stages,
            Function<CompletionStage<? extends T>, CompletableFuture<T>> converter) {
        requireNonNull(stages, "cfs is null");
        @SuppressWarnings("unchecked")
        CompletableFuture<T>[] ret = new CompletableFuture[stages.length];
        for (int i = 0; i < stages.length; i++) {
            ret[i] = converter.apply(requireNonNull(stages[i], "cf" + (i + 1) + " is null"));
        }
        return ret;
    }

    /**
     * Converts CompletionStage to CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instances,
     * so the returned CF instances MUST NOT be written(e.g. {@link CompletableFuture#complete(Object)}).
     * Otherwise, the caller should defensive copy instead of writing it directly.
     */
    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> toCf(CompletionStage<? extends T> s) {
        if (s instanceof CompletableFuture) return (CompletableFuture<T>) s;
        else if (s instanceof Cffu) return ((Cffu<T>) s).cffuUnwrap();
        else return (CompletableFuture<T>) s.toCompletableFuture();
    }

    /**
     * Converts CompletionStage to Non-MinimalStage CompletableFuture, reuse cf instance as many as possible.
     * <p>
     * <strong>CAUTION:</strong> because reused the CF instances,
     * so the returned CF instances MUST NOT be written(e.g. {@link CompletableFuture#complete(Object)}).
     * Otherwise, the caller should defensive copy instead of writing it directly.
     */
    @SuppressWarnings("unchecked")
    private static <T> CompletableFuture<T> toNonMinCf(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f;
        if (s instanceof CompletableFuture) f = (CompletableFuture<T>) s;
        else if (s instanceof Cffu) f = ((Cffu<T>) s).cffuUnwrap();
        else return (CompletableFuture<T>) s.toCompletableFuture();

        return isMinimalStageCf(s) ? f.toCompletableFuture() : f;
    }

    /**
     * Converts CompletionStage to CompletableFuture copy.
     */
    private static <T> CompletableFuture<T> toCfCopy(CompletionStage<? extends T> s) {
        final CompletableFuture<T> f = toCf(s);
        return isMinimalStageCf(f) ? f.toCompletableFuture() : copy(f);
    }

    static <T> boolean isMinimalStageCf(CompletionStage<? extends T> s) {
        return "java.util.concurrent.CompletableFuture$MinimalStage".equals(s.getClass().getName());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed when any of the given stages complete, with the same result.
     * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     * If no stages are provided, returns an incomplete CompletableFuture.
     * <p>
     * This method is the same as {@link CompletableFuture#anyOf(CompletableFuture[])},
     * except that the parameter type is more generic {@link CompletionStage} instead of {@link CompletableFuture},
     * and the return type is more specific {@code T} instead of {@code Object}.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is completed with the result or exception
     * from any of the given stages when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOfSuccess(CompletionStage[])
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anyOf(CompletionStage<? extends T>... cfs) {
        CompletableFuture<Object> ret = CompletableFuture.anyOf(f_toCfArray(cfs));
        return f_cast(ret);
    }

    /**
     * Returns a new CompletableFuture that is successful when any of the given stages success,
     * with the same result. Otherwise, all the given stages complete exceptionally,
     * the returned CompletableFuture also does so, with a CompletionException holding
     * an exception from any of the given stages as its cause.
     * If no stages are provided, returns a new CompletableFuture that is already completed exceptionally
     * with a CompletionException holding a {@link NoCfsProvidedException} as its cause.
     * <p>
     * This method is the same as {@link #anyOf(CompletionStage[])}
     * except for the any-<strong>success</strong> behavior instead of any-<strong>complete</strong>.
     *
     * @param cfs the stages
     * @return a new CompletableFuture that is successful when any of the given stages success, with the same result
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see #anyOf(CompletionStage[])
     */
    @Contract(pure = true)
    @SafeVarargs
    public static <T> CompletableFuture<T> anyOfSuccess(CompletionStage<? extends T>... cfs) {
        requireCfsAndEleNonNull(cfs);
        final int size = cfs.length;
        if (size == 0) return failedFuture(new NoCfsProvidedException());
        if (size == 1) return toCfCopy(cfs[0]);

        // NOTE: fill ONE MORE element of successOrBeIncompleteCfs LATER
        final CompletableFuture<?>[] successOrBeIncomplete = new CompletableFuture[size + 1];
        final CompletableFuture<?>[] failedOrBeIncomplete = new CompletableFuture[size];
        fill(cfs, successOrBeIncomplete, failedOrBeIncomplete);

        // NOTE: fill the ONE MORE element of successOrBeIncompleteCfs HERE
        //       a cf that is failed when all given cfs fail, otherwise be incomplete
        successOrBeIncomplete[size] = CompletableFuture.allOf(failedOrBeIncomplete);

        CompletableFuture<Object> ret = CompletableFuture.anyOf(successOrBeIncomplete);
        return f_cast(ret);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allTupleOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a new CompletableFuture that is completed when the given two stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given two stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2), false);
    }

    /**
     * Returns a new CompletableFuture that is successful when the given two stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given two stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2), true);
    }

    /**
     * Returns a new CompletableFuture that is completed when the given three stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given three stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3), false);
    }

    /**
     * Returns a new CompletableFuture that is successful when the given three stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given three stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3), true);
    }

    /**
     * Returns a new CompletableFuture that is completed when the given four stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given four stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4), false);
    }

    /**
     * Returns a new CompletableFuture that is successful when the given four stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given four stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4), true);
    }

    /**
     * Returns a new CompletableFuture that is completed when the given five stages complete.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
     * with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given five stages complete
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOf(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOf(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5), false);
    }

    /**
     * Returns a new CompletableFuture that is successful when the given five stages success.
     * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given stages, with a CompletionException holding this exception as its cause.
     * <p>
     * This method is the same as {@link #allTupleOf(CompletionStage, CompletionStage, CompletionStage, CompletionStage, CompletionStage)}
     * except for the fast-fail behavior.
     *
     * @return a new CompletableFuture that is successful when the given five stages success
     * @throws NullPointerException if any of the given stages are {@code null}
     * @see #allResultsOfFastFail(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> allTupleOfFastFail(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return allTupleOf0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5), true);
    }

    private static <T> CompletableFuture<T> allTupleOf0(CompletionStage<?>[] css, boolean fastFail) {
        final int length = css.length;
        final Object[] result = new Object[length];
        final CompletableFuture<Void>[] resultSetterCfs = createResultSetterCfs(css, result);

        final CompletableFuture<Void> resultSetter;
        if (fastFail) resultSetter = allOfFastFail(resultSetterCfs);
        else resultSetter = CompletableFuture.allOf(resultSetterCfs);

        final CompletableFuture<Object> ret = resultSetter.thenApply(unused -> tupleOf0(result));
        return f_cast(ret);
    }

    @SuppressWarnings("unchecked")
    private static <T> T tupleOf0(Object... elements) {
        final int length = elements.length;
        final Object ret;
        if (length == 2) ret = Tuple2.of(elements[0], elements[1]);
        else if (length == 3) ret = Tuple3.of(elements[0], elements[1], elements[2]);
        else if (length == 4) ret = Tuple4.of(elements[0], elements[1], elements[2], elements[3]);
        else ret = Tuple5.of(elements[0], elements[1], elements[2], elements[3], elements[4]);
        return (T) ret;
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
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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

        return allOfFastFail(resultSetterCfs).thenRun(() -> action.accept((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using CompletableFuture's default asynchronous execution facility,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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

        return allOfFastFail(resultSetterCfs).thenRunAsync(() -> action.accept((T) result[0], (U) result[1]));
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed using the supplied executor,
     * with the two results as arguments to the supplied action.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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

        return allOfFastFail(resultSetterCfs).thenRunAsync(() -> action.accept((T) result[0], (U) result[1]), executor);
    }

    /**
     * Returns a new CompletableFuture that, when tow given stage both complete normally,
     * is executed with the two results as arguments to the supplied function.
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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
            BiFunction<? super T, ? super U, ? extends V> fn) {
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
     * if any of the given stage complete exceptionally, then the returned CompletableFuture also does so
     * *without* waiting other incomplete given CompletionStage,
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
            BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
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
     * This method is the same as {@link CompletionStage#runAfterEither(CompletionStage, Runnable)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param action the action to perform before completing the returned CompletableFuture
     * @return the new CompletableFuture
     * @see CompletionStage#runAfterEither(CompletionStage, Runnable)
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
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
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
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
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
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
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
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using this CompletableFuture's default asynchronous execution facility,
     * with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEitherAsync(CompletionStage, Consumer)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
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
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
     * <p>
     * This method is the same as {@link CompletionStage#acceptEitherAsync(CompletionStage, Consumer, Executor)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
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
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEither(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     * @return the new CompletableFuture
     * @see CompletionStage#applyToEither(CompletionStage, Function)
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccess(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Function<? super T, ? extends U> fn) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        return anyOfSuccess(css).thenApply(fn);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using this CompletableFuture's default asynchronous execution facility,
     * with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEitherAsync(CompletionStage, Function)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param fn  the function to use to compute the value of the returned CompletableFuture
     * @param <U> the function's return type
     * @return the new CompletableFuture
     * @see CompletionStage#applyToEitherAsync(CompletionStage, Function)
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2, Function<? super T, ? extends U> fn) {
        final CompletionStage<? extends T>[] css = requireCfsAndEleNonNull(cf1, cf2);
        requireNonNull(fn, "fn is null");

        return anyOfSuccess(css).thenApplyAsync(fn);
    }

    /**
     * Returns a new CompletableFuture that, when either given stage success,
     * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
     * <p>
     * This method is the same as {@link CompletionStage#applyToEitherAsync(CompletionStage, Function, Executor)}
     * except for the either-<strong>success</strong> behavior instead of either-<strong>complete</strong>.
     *
     * @param fn       the function to use to compute the value of the returned CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @param <U>      the function's return type
     * @return the new CompletableFuture
     * @see CompletionStage#applyToEitherAsync(CompletionStage, Function, Executor)
     */
    public static <T, U> CompletableFuture<U> applyToEitherSuccessAsync(
            CompletionStage<? extends T> cf1, CompletionStage<? extends T> cf2,
            Function<? super T, ? extends U> fn, Executor executor) {
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
    public static <T, C extends CompletionStage<? extends T>>
    C peek(C cf, BiConsumer<? super T, ? super Throwable> action) {
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
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync(C cf, BiConsumer<? super T, ? super Throwable> action) {
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
    public static <T, C extends CompletionStage<? extends T>>
    C peekAsync(C cf, BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        cf.whenCompleteAsync(action, executor);
        return cf;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF static methods + related enhanced methods
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
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cf, Function<Throwable, ? extends T> fn) {
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
    @SuppressWarnings("unchecked")
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyAsync(C cf, Function<Throwable, ? extends T> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return (C) cf.exceptionallyAsync(fn, executor);
        }
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        // below code is copied from CompletionStage#exceptionallyAsync
        return (C) cf.handle((r, ex) -> (ex == null) ? cf :
                cf.<T>handleAsync((r1, ex1) -> fn.apply(ex1), executor)
        ).thenCompose(x -> x);
    }

    //# Timeout Control methods

    /**
     * Exceptionally completes given CompletableFuture with a {@link TimeoutException}
     * if not otherwise completed before the given timeout.
     *
     * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     */
    public static <C extends CompletableFuture<?>> C orTimeout(C cf, long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            cf.orTimeout(timeout, unit);
        } else {
            requireNonNull(unit, "unit is null");
            // below code is copied from CompletableFuture#orTimeout with small adoption
            if (!cf.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToTimoutCf(cf, timeout, unit);
                cf.whenComplete(new FutureCanceller(f));
            }
        }
        return cf;
    }

    /**
     * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the {@code timeout} parameter
     * @return the given CompletableFuture
     */
    public static <T, C extends CompletableFuture<? super T>>
    C completeOnTimeout(C cf, @Nullable T value, long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            cf.completeOnTimeout(value, timeout, unit);
        } else {
            requireNonNull(unit, "unit is null");
            // below code is copied from CompletableFuture#completeOnTimeout with small adoption
            if (!cf.isDone()) {
                ScheduledFuture<?> f = Delayer.delayToCompleteCf(cf, value, timeout, unit);
                cf.whenComplete(new FutureCanceller(f));
            }
        }
        return cf;
    }

    //# Advanced methods of CompletionStage

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyCompose(C cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        if (IS_JAVA12_PLUS) {
            return (C) cf.exceptionallyCompose((Function) fn);
        }
        requireNonNull(fn, "fn is null");
        // below code is copied from CompletionStage.exceptionallyCompose
        return (C) cf.handle((r, ex) -> (ex == null) ? cf : fn.apply(ex)).thenCompose(x -> x);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally,
     * is composed using the results of the supplied function applied to given stage's exception,
     * using given CompletableFuture's default asynchronous execution facility.
     *
     * @param fn the function to use to compute the returned CompletableFuture
     *           if given CompletionStage completed exceptionally
     * @return the new CompletableFuture
     */
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyComposeAsync(C cf, Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(cf, fn, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Returns a new CompletableFuture that, when given CompletableFuture completes exceptionally, is composed using
     * the results of the supplied function applied to given stage's exception, using the supplied Executor.
     *
     * @param fn       the function to use to compute the returned CompletableFuture
     *                 if given CompletionStage completed exceptionally
     * @param executor the executor to use for asynchronous execution
     * @return the new CompletableFuture
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T, C extends CompletionStage<? super T>>
    C exceptionallyComposeAsync(C cf, Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return (C) cf.exceptionallyComposeAsync((Function) fn, executor);
        }
        requireNonNull(fn, "fn is null");
        requireNonNull(executor, "executor is null");
        // below code is copied from CompletionStage.exceptionallyComposeAsync
        return (C) cf.handle((r, ex) -> (ex == null) ? cf :
                cf.handleAsync((r1, ex1) -> fn.apply(ex1), executor).thenCompose(x -> x)
        ).thenCompose(x -> x);
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
     * Returns the result value if the given stage is completed successfully, else returns the given valueIfNotSuccess.
     * <p>
     * This method will not throw exceptions
     * (CancellationException/CompletionException/ExecutionException/IllegalStateException/...).
     *
     * @param valueIfNotSuccess the value to return if not completed successfully
     * @return the result value, if completed successfully, else the given valueIfNotSuccess
     */
    // * @see #MGetSuccessNow(Object, CompletionStage[])
    @Contract(pure = true)
    @Nullable
    public static <T> T getSuccessNow(CompletionStage<? extends T> cf, @Nullable T valueIfNotSuccess) {
        final CompletableFuture<T> f = toCf(cf);
        return f.isDone() && !f.isCompletedExceptionally() ? f.join() : valueIfNotSuccess;
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
    public static Throwable exceptionNow(CompletableFuture<?> cf) {
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
    public static CffuState state(CompletableFuture<?> cf) {
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

    //# New enhanced multi-read(explicitly) methods

    /**
     * Multi-Gets(MGet) the results in the <strong>same order</strong> of the given cfs,
     * use the result value if the given stage is completed successfully, else use the given valueIfNotSuccess
     * (aka the result extraction logic is {@link #getSuccessNow(CompletionStage, Object)}).
     *
     * @param cfs the stages
     * @see #mostResultsOfSuccess(long, TimeUnit, Object, CompletionStage[])
     * @see #getSuccessNow(CompletionStage, Object)
     */
    @Contract(pure = true)
    @SafeVarargs
    static <T> List<T> MGetSuccessNow(@Nullable T valueIfNotSuccess, CompletionStage<? extends T>... cfs) {
        return arrayList(MGet0(cf -> getSuccessNow(cf, valueIfNotSuccess), cfs));
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    private static <T> T[] MGet0(Function<CompletableFuture<T>, ? extends T> resultGetter,
                                 CompletionStage<? extends T>... cfs) {
        final CompletableFuture<T>[] cfArray = f_toCfArray(cfs);
        Object[] ret = new Object[cfs.length];
        for (int i = 0; i < cfArray.length; i++) {
            CompletableFuture<T> cf = cfArray[i];
            ret[i] = resultGetter.apply(cf);
        }
        return (T[]) ret;
    }

    /**
     * Multi-Gets(MGet) the result value in the <strong>same order</strong> of the given cfs,
     * use the result value if the cf is completed successfully, else use the value {@code null}
     * (aka the result extraction logic is {@code getSuccessNow(cf, null)}).
     */
    static <T1, T2> Tuple2<T1, T2> tupleGetSuccessNow(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2) {
        return tupleGet0(requireCfsAndEleNonNull(cf1, cf2));
    }

    /**
     * Multi-Gets(MGet) the result value in the <strong>same order</strong> of the given cfs,
     * use the result value if the cf is completed successfully, else use the value {@code null}
     * (aka the result extraction logic is {@code getSuccessNow(cf, null)}).
     */
    static <T1, T2, T3> Tuple3<T1, T2, T3> tupleGetSuccessNow(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3) {
        return tupleGet0(requireCfsAndEleNonNull(cf1, cf2, cf3));
    }

    /**
     * Multi-Gets(MGet) the result value in the <strong>same order</strong> of the given cfs,
     * use the result value if the cf is completed successfully, else use the value {@code null}
     * (aka the result extraction logic is {@code getSuccessNow(cf, null)}).
     */
    static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tupleGetSuccessNow(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2,
            CompletionStage<? extends T3> cf3, CompletionStage<? extends T4> cf4) {
        return tupleGet0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4));
    }

    /**
     * Multi-Gets(MGet) the result value in the <strong>same order</strong> of the given cfs,
     * use the result value if the cf is completed successfully, else the value {@code null}
     * (aka the result extraction logic is {@code getSuccessNow(cf, null)}).
     */
    static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tupleGetSuccessNow(
            CompletionStage<? extends T1> cf1, CompletionStage<? extends T2> cf2, CompletionStage<? extends T3> cf3,
            CompletionStage<? extends T4> cf4, CompletionStage<? extends T5> cf5) {
        return tupleGet0(requireCfsAndEleNonNull(cf1, cf2, cf3, cf4, cf5));
    }

    private static <T> T tupleGet0(CompletionStage<?>... css) {
        return tupleOf0(MGet0(cf -> getSuccessNow(cf, null), css));
    }

    //# Write methods of CompletableFuture

    /**
     * Completes given CompletableFuture with the result of the given Supplier function invoked
     * from an asynchronous task using the default executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @return the given CompletableFuture
     */
    public static <T, C extends CompletableFuture<? super T>>
    C completeAsync(C cf, Supplier<? extends T> supplier) {
        return completeAsync(cf, supplier, AsyncPoolHolder.ASYNC_POOL);
    }

    /**
     * Completes given CompletableFuture with the result of the given Supplier function invoked
     * from an asynchronous task using the given executor.
     *
     * @param supplier a function returning the value to be used to complete given CompletableFuture
     * @param executor the executor to use for asynchronous execution
     * @return the given CompletableFuture
     */
    public static <T, C extends CompletableFuture<? super T>>
    C completeAsync(C cf, Supplier<? extends T> supplier, Executor executor) {
        if (IS_JAVA9_PLUS) {
            cf.completeAsync(supplier, executor);
        } else {
            requireNonNull(supplier, "supplier is null");
            requireNonNull(executor, "executor is null");
            // below code is copied from CompletableFuture#completeAsync with small adoption
            executor.execute(new CfCompleterBySupplier<>(cf, supplier));
        }
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
        return cf.thenApply(x -> x);
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
        return cf.thenApply(x -> x);
    }

    /**
     * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
     *
     * @param <U> the type of the value
     * @return a new CompletableFuture
     */
    @Contract(pure = true)
    public static <U> CompletableFuture<U> newIncompleteFuture(CompletableFuture<?> cf) {
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
    //# Conversion Methods
    //
    //    - toCompletableFutureArray:     CompletionStage[](including Cffu) -> CF[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * A convenient util method for converting input {@link CompletionStage} (including
     * {@link Cffu}/{@link CompletableFuture}) array element by {@link CompletionStage#toCompletableFuture()}.
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
     * A convenient util method for converting input {@link CompletableFuture} list to CompletableFuture array.
     *
     * @see #toCompletableFutureArray(CompletionStage[])
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T>[] completableFutureListToArray(List<CompletableFuture<T>> cfList) {
        @SuppressWarnings("unchecked")
        final CompletableFuture<T>[] a = new CompletableFuture[cfList.size()];
        return cfList.toArray(a);
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
