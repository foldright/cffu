package io.foldright.cffu.internal;

import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.Arrays;
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
        for (int i = 0; i < array.length; i++)
            requireNonNull(array[i], varName + (i + 1) + " is null");
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
        Object[] ret = destConstructor.apply(source.length);
        for (int i = 0; i < source.length; i++)
            ret[i] = mapper.apply(source[i]);
        return (R[]) ret;
    }

    public static <T> T[] fillArray(T[] array, IntFunction<T> init) {
        for (int i = 0; i < array.length; i++)
            array[i] = init.apply(i);
        return array;
    }

    /**
     * Returns normal array list instead of unmodifiable({@link java.util.List#of}) or fixed-size
     * ({@link Arrays#asList}) list. Safer for application code which may reuse the return list as normal collection.
     */
    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    private CommonUtils() {
    }
}
