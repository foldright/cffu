package io.foldright.cffu2.internal;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.StreamSupport;

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
     * <pre>{@code  Integer[] source = new Integer[3];
     * mapArray(source, String[]::new, i -> "integer: " + i);}</pre>
     */
    public static <T, R> R[] mapArray(
            T[] source, IntFunction<R[]> destConstructor, Function<? super T, ? extends R> mapper) {
        int len = source.length;
        R[] ret = destConstructor.apply(len);
        for (int i = 0; i < len; i++) ret[i] = mapper.apply(source[i]);
        return ret;
    }

    /**
     * Fills the specified array where each element is calculated by calling the specified init function.
     */
    public static <T> T[] fillArray(T[] array, IntFunction<T> init) {
        Arrays.setAll(array, init);
        return array;
    }

    /**
     * Fills the specified range of the specified array
     * where each element is calculated by calling the specified init function.
     */
    public static <T> T[] fillArrayRange(T[] array, int from, int to, IntFunction<T> init) {
        for (int i = from; i < to; i++) array[i] = init.apply(i);
        return array;
    }

    /**
     * Checks if the object is in the given array.
     */
    public static boolean containsInArray(final Object[] array, final Object objectToFind) {
        return Arrays.asList(array).contains(objectToFind);
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T>[] newCfArray(int length) {
        return new CompletableFuture[length];
    }

    @SuppressWarnings("unchecked")
    public static <T> CompletionStage<T>[] newStageArray(int length) {
        return new CompletionStage[length];
    }

    // endregion
    // region# List Utility Methods

    /**
     * Returns a new {@link ArrayList} which is not unmodifiable({@link java.util.List#of}) or fixed-size
     * ({@link Arrays#asList}) list. Safer for application code which may reuse the return list as a normal collection.
     */
    @Contract("_ -> new")
    @SafeVarargs
    public static <T> ArrayList<T> arrayList(T... elements) {
        return new ArrayList<>(Arrays.asList(elements));
    }

    /**
     * Creates a new {@link ArrayList} with the specified size,
     * where each element is calculated by calling the specified init function.
     */
    @Contract("_, _ -> new")
    public static <T> ArrayList<T> arrayList(int size, IntFunction<? extends T> init) {
        final ArrayList<T> ret = new ArrayList<>(size);
        for (int i = 0; i < size; i++) ret.add(init.apply(i));
        return ret;
    }

    // endregion
    // region# Conversation Methods (Iterable -> Array)

    /**
     * Converts an Iterable to an array.
     */
    @Contract(value = "null, _ -> null; !null, _ -> !null")
    public static @Nullable <T> T[] toArray(@Nullable Iterable<? extends T> iterable, T[] typeToken) {
        if (iterable == null) return null;
        if (iterable instanceof Collection) {
            return ((Collection<? extends T>) iterable).toArray(typeToken);
        }
        List<T> list = new ArrayList<>();
        for (T e : iterable) list.add(e);
        return list.toArray(typeToken);
    }

    /**
     * Converts an Iterable to an array.
     */
    @Contract(value = "null, _, _ -> null; !null, _, _ -> !null")
    public static @Nullable <T, U> U[] toArray(
            @Nullable Iterable<? extends T> iterable, IntFunction<U[]> generator, Function<? super T, ? extends U> mapper) {
        if (iterable == null) return null;
        return StreamSupport.stream(iterable.spliterator(), false).map(mapper).toArray(generator);
    }

    /**
     * Converts an Iterable to an array.
     */
    @Contract(value = "null -> null; !null -> !null")
    public static @Nullable <T> Collection<? extends T> toCollection(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) return null;
        if (iterable instanceof Collection) {
            return (Collection<? extends T>) iterable;
        }
        List<T> list = new ArrayList<>();
        for (T e : iterable) list.add(e);
        return list;
    }

    /**
     * Converts an Iterable to an array.
     */
    @Contract(value = "null -> null; !null -> !null")
    public static @Nullable <T> ArrayList<T> toArrayList(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) return null;
        if (iterable instanceof Collection) {
            return new ArrayList<>((Collection<? extends T>) iterable);
        }
        ArrayList<T> list = new ArrayList<>();
        for (T e : iterable) list.add(e);
        return list;
    }

    /**
     * Converts an Iterable to an array.
     */
    @Contract(value = "null -> null; !null -> !null")
    public static @Nullable <T> CopyOnWriteArrayList<T> toCopyOnWriteArrayList(@Nullable Iterable<? extends T> iterable) {
        if (iterable == null) return null;
        return new CopyOnWriteArrayList<>(toCollection(iterable));
    }

    // endregion
    // region# AtomicReferenceArray Utility Methods

    /**
     * Fills the given {@link AtomicReferenceArray} with the same elements as the given value.
     *
     * @see Arrays#fill(Object[], Object)
     */
    public static <E> void fillAtomicReferenceArray(
            AtomicReferenceArray<? super E> array, E value) {
        for (int i = 0, len = array.length(); i < len; i++) array.set(i, value);
    }

    /**
     * Returns a new {@link ArrayList} with the same elements as the given {@link AtomicReferenceArray}.
     */
    @Contract("_ -> new")
    public static <E> ArrayList<E> toArrayList(AtomicReferenceArray<? extends E> array) {
        int len = array.length();
        ArrayList<E> ret = new ArrayList<>(len);
        for (int i = 0; i < len; i++) ret.add(array.get(i));
        return ret;
    }

    // endregion
    // region# Other Mics Methods

    public static <T> @Nullable T castOrNull(Class<T> clazz, @Nullable Object obj) {
        return clazz.isInstance(obj) ? clazz.cast(obj) : null;
    }

    private CommonUtils() {}
}
