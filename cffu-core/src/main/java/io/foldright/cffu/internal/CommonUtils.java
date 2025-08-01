package io.foldright.cffu.internal;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.IntFunction;

import static java.util.Objects.requireNonNull;


/**
 * <strong>Internal</strong> common utility methods.
 */
@ApiStatus.Internal
public final class CommonUtils {
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
    @SuppressWarnings("unchecked")
    public static <T, R> R[] mapArray(T[] source, IntFunction<Object[]> destConstructor, Function<T, R> mapper) {
        int len = source.length;
        R[] ret = (R[]) destConstructor.apply(len);
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
    public static <E> ArrayList<E> arrayList(AtomicReferenceArray<E> array) {
        int len = array.length();
        ArrayList<E> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) ret.add(array.get(i));
        return ret;
    }

    /**
     * Returns a new array with the same elements as the given {@link AtomicReferenceArray}.
     */
    public static <E> E[] toArray(AtomicReferenceArray<E> array) {
        int len = array.length();
        @SuppressWarnings("unchecked")
        E[] ret = (E[]) new Object[len];
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

    private CommonUtils() {}
}
