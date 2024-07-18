package io.foldright.cffu.retry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;
import java.util.function.Predicate;


/**
 * Immutable implementation of {@link CompositeTriggerStrategy}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableTriggerStrategy.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
public final class ImmutableTriggerStrategy<T> extends CompositeTriggerStrategy<T> {
    private final List<Class<? extends Throwable>> retryExceptions;
    private final List<Class<? extends Throwable>> ignoredExceptions;
    private final @Nullable Predicate<? super Throwable> retryByThrowable;

    private ImmutableTriggerStrategy(
            List<Class<? extends Throwable>> retryExceptions,
            List<Class<? extends Throwable>> ignoredExceptions,
            @Nullable Predicate<? super Throwable> retryByThrowable) {
        this.retryExceptions = retryExceptions;
        this.ignoredExceptions = ignoredExceptions;
        this.retryByThrowable = retryByThrowable;
    }

    /**
     * Creates an immutable copy of a {@link CompositeTriggerStrategy} value.
     * Uses accessors to get values to initialize the new immutable instance.
     * If an instance is already immutable, it is returned as is.
     *
     * @param <T>      generic parameter T
     * @param instance The instance to copy
     * @return A copied immutable CompositeTriggerStrategy instance
     */
    public static <T> ImmutableTriggerStrategy<T> copyOf(CompositeTriggerStrategy<T> instance) {
        if (instance instanceof ImmutableTriggerStrategy<?>) {
            return (ImmutableTriggerStrategy<T>) instance;
        }
        return ImmutableTriggerStrategy.<T>builder()
                .from(instance)
                .build();
    }

    /**
     * Creates a builder for {@link ImmutableTriggerStrategy ImmutableTriggerStrategy}.
     * <pre>
     * ImmutableTriggerStrategy.&amp;lt;T&amp;gt;builder()
     *    .addRetryException|addAllRetryExceptions(Class&amp;lt;? extends Throwable&amp;gt;) // {@link CompositeTriggerStrategy#retryExceptions() retryExceptions} elements
     *    .addIgnoredException|addAllIgnoredExceptions(Class&amp;lt;? extends Throwable&amp;gt;) // {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions} elements
     *    .retryByThrowable(function.Predicate&amp;lt;? super Throwable&amp;gt; | null) // nullable {@link CompositeTriggerStrategy#retryByThrowable() retryByThrowable}
     *    .build();
     * </pre>
     *
     * @param <T> generic parameter T
     * @return A new ImmutableTriggerStrategy builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    /**
     * @return The value of the {@code retryExceptions} attribute
     */
    @Override
    List<Class<? extends Throwable>> retryExceptions() {
        return retryExceptions;
    }

    /**
     * @return The value of the {@code ignoredExceptions} attribute
     */
    @Override
    List<Class<? extends Throwable>> ignoredExceptions() {
        return ignoredExceptions;
    }

    /**
     * @return The value of the {@code retryByThrowable} attribute
     */
    @Override
    @Nullable
    Predicate<? super Throwable> retryByThrowable() {
        return retryByThrowable;
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link CompositeTriggerStrategy#retryExceptions() retryExceptions}.
     *
     * @param elements The elements to set
     * @return A modified copy of {@code this} object
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final ImmutableTriggerStrategy<T> withRetryExceptions(Class<? extends Throwable>... elements) {
        List<Class<? extends Throwable>> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
        return new ImmutableTriggerStrategy<>(newValue, this.ignoredExceptions, this.retryByThrowable);
    }

    private static <T> List<T> createUnmodifiableList(boolean clone, List<T> list) {
        switch (list.size()) {
            case 0:
                return Collections.emptyList();
            case 1:
                return Collections.singletonList(list.get(0));
            default:
                if (clone) {
                    return Collections.unmodifiableList(new ArrayList<>(list));
                } else {
                    if (list instanceof ArrayList<?>) {
                        ((ArrayList<?>) list).trimToSize();
                    }
                    return Collections.unmodifiableList(list);
                }
        }
    }

    private static <T> List<T> createSafeList(Iterable<? extends T> iterable, boolean checkNulls, boolean skipNulls) {
        ArrayList<T> list;
        if (iterable instanceof Collection<?>) {
            int size = ((Collection<?>) iterable).size();
            if (size == 0) return Collections.emptyList();
            list = new ArrayList<>(size);
        } else {
            list = new ArrayList<>();
        }
        for (T element : iterable) {
            if (skipNulls && element == null) continue;
            if (checkNulls) Objects.requireNonNull(element, "element");
            list.add(element);
        }
        return list;
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link CompositeTriggerStrategy#retryExceptions() retryExceptions}.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param elements An iterable of retryExceptions elements to set
     * @return A modified copy of {@code this} object
     */
    public final ImmutableTriggerStrategy<T> withRetryExceptions(Iterable<? extends Class<? extends Throwable>> elements) {
        if (this.retryExceptions == elements) return this;
        List<Class<? extends Throwable>> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
        return new ImmutableTriggerStrategy<>(newValue, this.ignoredExceptions, this.retryByThrowable);
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions}.
     *
     * @param elements The elements to set
     * @return A modified copy of {@code this} object
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final ImmutableTriggerStrategy<T> withIgnoredExceptions(Class<? extends Throwable>... elements) {
        List<Class<? extends Throwable>> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
        return new ImmutableTriggerStrategy<>(this.retryExceptions, newValue, this.retryByThrowable);
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions}.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param elements An iterable of ignoredExceptions elements to set
     * @return A modified copy of {@code this} object
     */
    public final ImmutableTriggerStrategy<T> withIgnoredExceptions(Iterable<? extends Class<? extends Throwable>> elements) {
        if (this.ignoredExceptions == elements) return this;
        List<Class<? extends Throwable>> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
        return new ImmutableTriggerStrategy<>(this.retryExceptions, newValue, this.retryByThrowable);
    }

    /**
     * Copy the current immutable object by setting a value for the {@link CompositeTriggerStrategy#retryByThrowable() retryByThrowable} attribute.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param value A new value for retryByThrowable (can be {@code null})
     * @return A modified copy of the {@code this} object
     */
    public final ImmutableTriggerStrategy<T> withRetryByThrowable(@Nullable Predicate<? super Throwable> value) {
        if (this.retryByThrowable == value) return this;
        return new ImmutableTriggerStrategy<>(this.retryExceptions, this.ignoredExceptions, value);
    }

    /**
     * This instance is equal to all instances of {@code ImmutableTriggerStrategy} that have equal attribute values.
     *
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */
    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another) return true;
        return another instanceof ImmutableTriggerStrategy<?>
                && equalTo(0, (ImmutableTriggerStrategy<?>) another);
    }

    private boolean equalTo(int synthetic, ImmutableTriggerStrategy<?> another) {
        return retryExceptions.equals(another.retryExceptions)
                && ignoredExceptions.equals(another.ignoredExceptions)
                && Objects.equals(retryByThrowable, another.retryByThrowable);
    }

    /**
     * Computes a hash code from attributes: {@code retryExceptions}, {@code ignoredExceptions}, {@code retryByThrowable}.
     *
     * @return hashCode value
     */
    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + retryExceptions.hashCode();
        h += (h << 5) + ignoredExceptions.hashCode();
        h += (h << 5) + Objects.hashCode(retryByThrowable);
        return h;
    }

    /**
     * Prints the immutable value {@code CompositeTriggerStrategy} with attribute values.
     *
     * @return A string representation of the value
     */
    @Override
    public String toString() {
        return "CompositeTriggerStrategy{"
                + "retryExceptions=" + retryExceptions
                + ", ignoredExceptions=" + ignoredExceptions
                + ", retryByThrowable=" + retryByThrowable
                + "}";
    }

    /**
     * Builds instances of type {@link ImmutableTriggerStrategy ImmutableTriggerStrategy}.
     * Initialize attributes and then invoke the {@link #build()} method to create an
     * immutable instance.
     * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
     * but instead used immediately to create instances.</em>
     */
    @NotThreadSafe
    public static final class Builder<T> {
        private List<Class<? extends Throwable>> retryExceptions = new ArrayList<Class<? extends Throwable>>();
        private List<Class<? extends Throwable>> ignoredExceptions = new ArrayList<Class<? extends Throwable>>();
        private @Nullable Predicate<? super Throwable> retryByThrowable;

        private Builder() {
        }

        /**
         * Fill a builder with attribute values from the provided {@code CompositeTriggerStrategy} instance.
         * Regular attribute values will be replaced with those from the given instance.
         * Absent optional values will not replace present values.
         * Collection elements and entries will be added, not replaced.
         *
         * @param instance The instance from which to copy values
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> from(CompositeTriggerStrategy<T> instance) {
            Objects.requireNonNull(instance, "instance");
            addAllRetryExceptions(instance.retryExceptions());
            addAllIgnoredExceptions(instance.ignoredExceptions());
            @Nullable Predicate<? super Throwable> retryByThrowableValue = instance.retryByThrowable();
            if (retryByThrowableValue != null) {
                retryByThrowable(retryByThrowableValue);
            }
            return this;
        }

        /**
         * Adds elements to {@link CompositeTriggerStrategy#retryExceptions() retryExceptions} list.
         *
         * @param elements An iterable of retryExceptions elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addAllRetryExceptions(Iterable<? extends Class<? extends Throwable>> elements) {
            for (Class<? extends Throwable> element : elements) {
                this.retryExceptions.add(Objects.requireNonNull(element, "retryExceptions element"));
            }
            return this;
        }

        /**
         * Adds elements to {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions} list.
         *
         * @param elements An iterable of ignoredExceptions elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addAllIgnoredExceptions(Iterable<? extends Class<? extends Throwable>> elements) {
            for (Class<? extends Throwable> element : elements) {
                this.ignoredExceptions.add(Objects.requireNonNull(element, "ignoredExceptions element"));
            }
            return this;
        }

        /**
         * Initializes the value for the {@link CompositeTriggerStrategy#retryByThrowable() retryByThrowable} attribute.
         *
         * @param retryByThrowable The value for retryByThrowable (can be {@code null})
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> retryByThrowable(@Nullable Predicate<? super Throwable> retryByThrowable) {
            this.retryByThrowable = retryByThrowable;
            return this;
        }

        /**
         * Adds one element to {@link CompositeTriggerStrategy#retryExceptions() retryExceptions} list.
         *
         * @param element A retryExceptions element
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addRetryException(Class<? extends Throwable> element) {
            this.retryExceptions.add(Objects.requireNonNull(element, "retryExceptions element"));
            return this;
        }

        /**
         * Adds elements to {@link CompositeTriggerStrategy#retryExceptions() retryExceptions} list.
         *
         * @param elements An array of retryExceptions elements
         * @return {@code this} builder for use in a chained invocation
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public final Builder<T> addRetryExceptions(Class<? extends Throwable>... elements) {
            for (Class<? extends Throwable> element : elements) {
                this.retryExceptions.add(Objects.requireNonNull(element, "retryExceptions element"));
            }
            return this;
        }

        /**
         * Sets or replaces all elements for {@link CompositeTriggerStrategy#retryExceptions() retryExceptions} list.
         *
         * @param elements An iterable of retryExceptions elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> retryExceptions(Iterable<? extends Class<? extends Throwable>> elements) {
            this.retryExceptions.clear();
            return addAllRetryExceptions(elements);
        }

        /**
         * Adds one element to {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions} list.
         *
         * @param element A ignoredExceptions element
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addIgnoredException(Class<? extends Throwable> element) {
            this.ignoredExceptions.add(Objects.requireNonNull(element, "ignoredExceptions element"));
            return this;
        }

        /**
         * Adds elements to {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions} list.
         *
         * @param elements An array of ignoredExceptions elements
         * @return {@code this} builder for use in a chained invocation
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public final Builder<T> addIgnoredExceptions(Class<? extends Throwable>... elements) {
            for (Class<? extends Throwable> element : elements) {
                this.ignoredExceptions.add(Objects.requireNonNull(element, "ignoredExceptions element"));
            }
            return this;
        }

        /**
         * Sets or replaces all elements for {@link CompositeTriggerStrategy#ignoredExceptions() ignoredExceptions} list.
         *
         * @param elements An iterable of ignoredExceptions elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> ignoredExceptions(Iterable<? extends Class<? extends Throwable>> elements) {
            this.ignoredExceptions.clear();
            return addAllIgnoredExceptions(elements);
        }

        /**
         * Builds a new {@link ImmutableTriggerStrategy ImmutableTriggerStrategy}.
         *
         * @return An immutable instance of CompositeTriggerStrategy
         * @throws IllegalStateException if any required attributes are missing
         */
        public ImmutableTriggerStrategy<T> build() {
            return new ImmutableTriggerStrategy<>(
                    createUnmodifiableList(true, retryExceptions),
                    createUnmodifiableList(true, ignoredExceptions),
                    retryByThrowable);
        }
    }
}
