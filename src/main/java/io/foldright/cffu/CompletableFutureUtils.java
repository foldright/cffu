package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;


/**
 * This class contains some enhanced methods for {@link CompletableFuture}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CompletableFutureUtils {
    /**
     * Returns a new CompletableFuture with the result of all the given CompletableFutures,
     * the new CompletableFuture is completed when all the given CompletableFutures complete.
     * <p>
     * Same to {@link CompletableFuture#allOf(CompletableFuture[])},
     * but the returned CompletableFuture contains the results of input CompletableFutures.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed when all the given CompletableFutures complete
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<List<T>> allOfWithResult(CompletableFuture<T>... cfs) {
        final int size = cfs.length;
        for (int i = 0; i < size; i++) {
            requireNonNull(cfs[i], "cf" + i + " is null");
        }

        final Object[] result = new Object[size];

        final CompletableFuture<?>[] thenCfs = new CompletableFuture[size];
        for (int i = 0; i < size; i++) {
            final int index = i;
            final CompletableFuture<T> cf = cfs[index];

            CompletableFuture<Void> thenCf = cf.thenAccept(x -> result[index] = x);
            thenCfs[index] = thenCf;
        }

        return CompletableFuture.allOf(thenCfs)
                .thenApply(unused -> (List<T>) Arrays.asList(result));
    }

    /**
     * Returns a new CompletableFuture that is completed
     * when any of the given CompletableFutures complete, with the same result.
     * <p>
     * Same as {@link CompletableFuture#anyOf(CompletableFuture[])},
     * but return result type is specified type instead of {@code Object}.
     *
     * @param cfs the CompletableFutures
     * @return a new CompletableFuture that is completed with the result
     * or exception of any of the given CompletableFutures when one completes
     * @throws NullPointerException if the array or any of its elements are {@code null}
     * @see CompletableFuture#anyOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> anyOfWithType(CompletableFuture<T>... cfs) {
        return (CompletableFuture<T>) CompletableFuture.anyOf(cfs);
    }

    @SafeVarargs
    public static <T> CompletableFuture<T> anyOfSuccess(CompletableFuture<T>... cfs) {
        if (cfs.length == 0) return new CompletableFuture<>();

        for (int i = 0; i < cfs.length; i++) {
            requireNonNull(cfs[i], "cf" + i + " is null");
        }

        CompletableFuture<T> ret = new CompletableFuture<>();
        CompletableFuture<Throwable> lastAllFailedExCf = null;

        for (final CompletableFuture<T> cf : cfs) {
            // success path: if any input cf success, complete returned cf
            cf.thenAccept(ret::complete);

            CompletableFuture<Throwable> exCf = cf.handle((v, ex) -> ex);
            if (lastAllFailedExCf == null) lastAllFailedExCf = exCf;
            else lastAllFailedExCf = lastAllFailedExCf.thenCombine(exCf,
                    (prevEx, combinedEx) -> prevEx != null && combinedEx != null ? combinedEx : null);
        }
        // failed path: if all input cfs failed, completeExceptionally returned cf with last exception
        lastAllFailedExCf.thenAccept(ex -> {
            if (ex != null) ret.completeExceptionally(ex);
        });

        return ret;
    }

    /**
     * Returns a new CompletableFuture that is completed when the given two CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 2 CompletableFutures complete
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2> CompletableFuture<Tuple2<T1, T2>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");

        final Object[] result = new Object[2];
        return CompletableFuture.allOf(
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
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3> CompletableFuture<Tuple3<T1, T2, T3>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");

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
     * Returns a new CompletableFuture that is completed when the given 4 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 4 CompletableFutures complete
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4> CompletableFuture<Tuple4<T1, T2, T3, T4>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");
        requireNonNull(cf4, "cf4 is null");

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
     * Returns a new CompletableFuture that is completed when the given 5 CompletableFutures complete.
     * If any of the given CompletableFutures complete exceptionally, then the returned
     * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
     *
     * @return a new CompletableFuture that is completed when the given 5 CompletableFutures complete
     * @throws NullPointerException if any input CompletableFutures are {@code null}
     * @see CompletableFuture#allOf(CompletableFuture[])
     */
    @Contract(pure = true)
    @SuppressWarnings("unchecked")
    public static <T1, T2, T3, T4, T5> CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> combine(
            CompletableFuture<T1> cf1, CompletableFuture<T2> cf2,
            CompletableFuture<T3> cf3, CompletableFuture<T4> cf4, CompletableFuture<T5> cf5) {
        requireNonNull(cf1, "cf1 is null");
        requireNonNull(cf2, "cf2 is null");
        requireNonNull(cf3, "cf3 is null");
        requireNonNull(cf4, "cf4 is null");
        requireNonNull(cf5, "cf5 is null");

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

    private CompletableFutureUtils() {
    }
}
