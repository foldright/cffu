package io.foldright.cffu.internal;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.Cffu;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;


/**
 * <strong>Internal</strong> common utility methods.
 */
@ApiStatus.Internal
public final class CommonUtils {
    // region# Array Utility Methods

    @SafeVarargs
    public static <T> T[] requireArrayAndEleNonNull(String varName, T... array) {
        requireNonNull(array, varName + "s is null");
        for (int i = 0; i < array.length; i++) requireNonNull(array[i], varName + (i + 1) + " is null");
        return array;
    }

    /**
     * example code of "map int[] to string[]":
     *
     * <pre>{@code Integer[] source = new Integer[3];
     * mapArray(source, String[]::new, i -> "integer: " + i);
     * }</pre>
     */
    public static <T, R> R[] mapArray(T[] source, IntFunction<R[]> destConstructor, Function<? super T, ? extends R> mapper) {
        int len = source.length;
        R[] ret = destConstructor.apply(len);
        for (int i = 0; i < len; i++) ret[i] = mapper.apply(source[i]);
        return ret;
    }

    public static <T> T[] fillArray(T[] array, IntFunction<T> init) {
        Arrays.setAll(array, init);
        return array;
    }

    public static boolean containsInArray(final Object[] array, final Object objectToFind) {
        return Arrays.asList(array).contains(objectToFind);
    }

    // endregion
    // region# Array Creation Methods
    //         safe to suppress "unchecked" warning because all elements are null value

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T>[] createCfArray(int length) {
        return new CompletableFuture[length];
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletionStage<T>[] createStageArray(int length) {
        return new CompletionStage[length];
    }

    @SuppressWarnings("unchecked")
    public static <T> Cffu<T>[] createCffuArray(int length) {
        return new Cffu[length];
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] f_createArray(int length) {
        return (T[]) new Object[length];
    }

    // endregion
    // region# List Utility Methods

    /**
     * Returns normal array list instead of unmodifiable({@link java.util.List#of}) or fixed-size
     * ({@link Arrays#asList}) list. Safer for application code which may reuse the return list as normal collection.
     */
    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Returns a new {@link ArrayList} with the same elements as the given {@link AtomicReferenceArray}.
     */
    public static <E> ArrayList<E> arrayList(AtomicReferenceArray<? extends E> array) {
        int len = array.length();
        ArrayList<E> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) ret.add(array.get(i));
        return ret;
    }

    // endregion
    // region# AtomicReferenceArray Utility Methods

    /**
     * Returns a new array with the same elements as the given {@link AtomicReferenceArray}.
     */
    public static <E> E[] f_toArray(AtomicReferenceArray<? extends E> array) {
        int len = array.length();
        E[] ret = f_createArray(len);
        for (int i = 0; i < len; i++) ret[i] = array.get(i);
        return ret;
    }

    /**
     * Fills the given {@link AtomicReferenceArray} with the same elements as the given value.
     *
     * @see Arrays#fill(Object[], Object)
     */
    public static <E> void fillAtomicReferenceArray(
            AtomicReferenceArray<? super E> array, E value) {
        for (int i = 0, len = array.length(); i < len; i++) array.set(i, value);
    }

    // endregion
    // region# Other Mics Methods

    public static <T> @Nullable T castOrNull(Class<T> type, @Nullable Object obj) {
        return type.isInstance(obj) ? type.cast(obj) : null;
    }

    private CommonUtils() {}
}
