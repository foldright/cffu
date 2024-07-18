package io.foldright.cffu.retry;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.time.Duration;
import java.util.*;
import java.util.function.Consumer;


/**
 * Immutable implementation of {@link RetryStrategy}.
 * <p>
 * Use the builder to create immutable instances:
 * {@code ImmutableRetryStrategy.builder()}.
 */
@SuppressWarnings({"all"})
@SuppressFBWarnings
@ParametersAreNonnullByDefault
@javax.annotation.processing.Generated("org.immutables.processor.ProxyProcessor")
@Immutable
public final class ImmutableRetryStrategy<T> implements RetryStrategy<T> {
    private final TriggerStrategy<T> triggerStrategy;
    private final DelayStrategy<T> delayStrategy;
    private final TerminateStrategy terminateStrategy;
    private final List<Consumer<T>> successListeners;
    private final List<Consumer<Throwable>> errorListeners;

    private ImmutableRetryStrategy(
            TriggerStrategy<T> triggerStrategy,
            DelayStrategy<T> delayStrategy,
            TerminateStrategy terminateStrategy,
            List<Consumer<T>> successListeners,
            List<Consumer<Throwable>> errorListeners) {
        this.triggerStrategy = triggerStrategy;
        this.delayStrategy = delayStrategy;
        this.terminateStrategy = terminateStrategy;
        this.successListeners = successListeners;
        this.errorListeners = errorListeners;
    }

    public static <U> ImmutableRetryStrategy<U> ofAttempts(long attempts) {
        return ImmutableRetryStrategy.<U>builder()
                .triggerStrategy(TriggerStrategy.byThrowable())
                .delayStrategy(DelayStrategy.sequential())
                .terminateStrategy(TerminateStrategy.ofAttempts(attempts))
                .build();
    }

    /**
     * Creates a builder for {@link ImmutableRetryStrategy ImmutableRetryStrategy}.
     * <pre>
     * ImmutableRetryStrategy.&amp;lt;T&amp;gt;builder()
     *    .triggerStrategy(io.foldright.cffu.retry.TriggerStrategy&amp;lt;T&amp;gt;) // required {@link RetryStrategy#triggerStrategy() triggerStrategy}
     *    .delayStrategy(io.foldright.cffu.retry.BackoffStrategy&amp;lt;T&amp;gt;) // required {@link RetryStrategy#delayStrategy() delayStrategy}
     *    .terminateStrategy(io.foldright.cffu.retry.TerminateStrategy) // required {@link RetryStrategy#terminateStrategy() terminateStrategy}
     *    .addSuccessListener|addAllSuccessListeners(function.Consumer&amp;lt;T&amp;gt;) // {@link RetryStrategy#successListeners() successListeners} elements
     *    .addErrorListener|addAllErrorListeners(function.Consumer&amp;lt;Throwable&amp;gt;) // {@link RetryStrategy#errorListeners() errorListeners} elements
     *    .build();
     * </pre>
     *
     * @param <T> generic parameter T
     * @return A new ImmutableRetryStrategy builder
     */
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static <U> ImmutableRetryStrategy<U> ofTimeout(Duration timeout) {
        return ImmutableRetryStrategy.<U>builder()
                .triggerStrategy(TriggerStrategy.byThrowable())
                .delayStrategy(DelayStrategy.sequential())
                .terminateStrategy(TerminateStrategy.ofTimeout(timeout))
                .build();
    }

    /**
     * Creates an immutable copy of a {@link RetryStrategy} value.
     * Uses accessors to get values to initialize the new immutable instance.
     * If an instance is already immutable, it is returned as is.
     *
     * @param <T>      generic parameter T
     * @param instance The instance to copy
     * @return A copied immutable RetryStrategy instance
     */
    public static <T> ImmutableRetryStrategy<T> copyOf(RetryStrategy<T> instance) {
        if (instance instanceof ImmutableRetryStrategy<?>) {
            return (ImmutableRetryStrategy<T>) instance;
        }
        return ImmutableRetryStrategy.<T>builder()
                .from(instance)
                .build();
    }

    /**
     * @return The value of the {@code triggerStrategy} attribute
     */
    @Override
    public TriggerStrategy<T> triggerStrategy() {
        return triggerStrategy;
    }

    /**
     * @return The value of the {@code delayStrategy} attribute
     */
    @Override
    public DelayStrategy<T> delayStrategy() {
        return delayStrategy;
    }

    /**
     * @return The value of the {@code terminateStrategy} attribute
     */
    @Override
    public TerminateStrategy terminateStrategy() {
        return terminateStrategy;
    }

    /**
     * @return The value of the {@code successListeners} attribute
     */
    @Override
    public List<Consumer<T>> successListeners() {
        return successListeners;
    }

    /**
     * @return The value of the {@code errorListeners} attribute
     */
    @Override
    public List<Consumer<Throwable>> errorListeners() {
        return errorListeners;
    }

    /**
     * Copy the current immutable object by setting a value for the {@link RetryStrategy#triggerStrategy() triggerStrategy} attribute.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param value A new value for triggerStrategy
     * @return A modified copy of the {@code this} object
     */
    public final ImmutableRetryStrategy<T> withTriggerStrategy(TriggerStrategy<T> value) {
        if (this.triggerStrategy == value) return this;
        TriggerStrategy<T> newValue = Objects.requireNonNull(value, "triggerStrategy");
        return new ImmutableRetryStrategy<>(
                newValue,
                this.delayStrategy,
                this.terminateStrategy,
                this.successListeners,
                this.errorListeners);
    }

    /**
     * Copy the current immutable object by setting a value for the {@link RetryStrategy#delayStrategy() delayStrategy} attribute.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param value A new value for delayStrategy
     * @return A modified copy of the {@code this} object
     */
    public final ImmutableRetryStrategy<T> withDelayStrategy(DelayStrategy<T> value) {
        if (this.delayStrategy == value) return this;
        DelayStrategy<T> newValue = Objects.requireNonNull(value, "delayStrategy");
        return new ImmutableRetryStrategy<>(
                this.triggerStrategy,
                newValue,
                this.terminateStrategy,
                this.successListeners,
                this.errorListeners);
    }

    /**
     * Copy the current immutable object by setting a value for the {@link RetryStrategy#terminateStrategy() terminateStrategy} attribute.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param value A new value for terminateStrategy
     * @return A modified copy of the {@code this} object
     */
    public final ImmutableRetryStrategy<T> withTerminateStrategy(TerminateStrategy value) {
        if (this.terminateStrategy == value) return this;
        TerminateStrategy newValue = Objects.requireNonNull(value, "terminateStrategy");
        return new ImmutableRetryStrategy<>(this.triggerStrategy, this.delayStrategy, newValue, this.successListeners, this.errorListeners);
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link RetryStrategy#successListeners() successListeners}.
     *
     * @param elements The elements to set
     * @return A modified copy of {@code this} object
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final ImmutableRetryStrategy<T> withSuccessListeners(Consumer<T>... elements) {
        List<Consumer<T>> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
        return new ImmutableRetryStrategy<>(this.triggerStrategy, this.delayStrategy, this.terminateStrategy, newValue, this.errorListeners);
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
     * Copy the current immutable object with elements that replace the content of {@link RetryStrategy#successListeners() successListeners}.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param elements An iterable of successListeners elements to set
     * @return A modified copy of {@code this} object
     */
    public final ImmutableRetryStrategy<T> withSuccessListeners(Iterable<? extends Consumer<T>> elements) {
        if (this.successListeners == elements) return this;
        List<Consumer<T>> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
        return new ImmutableRetryStrategy<>(this.triggerStrategy, this.delayStrategy, this.terminateStrategy, newValue, this.errorListeners);
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link RetryStrategy#errorListeners() errorListeners}.
     *
     * @param elements The elements to set
     * @return A modified copy of {@code this} object
     */
    @SafeVarargs
    @SuppressWarnings("varargs")
    public final ImmutableRetryStrategy<T> withErrorListeners(Consumer<Throwable>... elements) {
        List<Consumer<Throwable>> newValue = createUnmodifiableList(false, createSafeList(Arrays.asList(elements), true, false));
        return new ImmutableRetryStrategy<>(
                this.triggerStrategy,
                this.delayStrategy,
                this.terminateStrategy,
                this.successListeners,
                newValue);
    }

    /**
     * Copy the current immutable object with elements that replace the content of {@link RetryStrategy#errorListeners() errorListeners}.
     * A shallow reference equality check is used to prevent copying of the same value by returning {@code this}.
     *
     * @param elements An iterable of errorListeners elements to set
     * @return A modified copy of {@code this} object
     */
    public final ImmutableRetryStrategy<T> withErrorListeners(Iterable<? extends Consumer<Throwable>> elements) {
        if (this.errorListeners == elements) return this;
        List<Consumer<Throwable>> newValue = createUnmodifiableList(false, createSafeList(elements, true, false));
        return new ImmutableRetryStrategy<>(
                this.triggerStrategy,
                this.delayStrategy,
                this.terminateStrategy,
                this.successListeners,
                newValue);
    }

    /**
     * This instance is equal to all instances of {@code ImmutableRetryStrategy} that have equal attribute values.
     *
     * @return {@code true} if {@code this} is equal to {@code another} instance
     */
    @Override
    public boolean equals(@Nullable Object another) {
        if (this == another) return true;
        return another instanceof ImmutableRetryStrategy<?>
                && equalTo(0, (ImmutableRetryStrategy<?>) another);
    }

    private boolean equalTo(int synthetic, ImmutableRetryStrategy<?> another) {
        return triggerStrategy.equals(another.triggerStrategy)
                && delayStrategy.equals(another.delayStrategy)
                && terminateStrategy.equals(another.terminateStrategy)
                && successListeners.equals(another.successListeners)
                && errorListeners.equals(another.errorListeners);
    }

    /**
     * Computes a hash code from attributes: {@code triggerStrategy}, {@code delayStrategy}, {@code terminateStrategy}, {@code successListeners}, {@code errorListeners}.
     *
     * @return hashCode value
     */
    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + triggerStrategy.hashCode();
        h += (h << 5) + delayStrategy.hashCode();
        h += (h << 5) + terminateStrategy.hashCode();
        h += (h << 5) + successListeners.hashCode();
        h += (h << 5) + errorListeners.hashCode();
        return h;
    }

    /**
     * Prints the immutable value {@code RetryStrategy} with attribute values.
     *
     * @return A string representation of the value
     */
    @Override
    public String toString() {
        return "RetryStrategy{"
                + "triggerStrategy=" + triggerStrategy
                + ", delayStrategy=" + delayStrategy
                + ", terminateStrategy=" + terminateStrategy
                + ", successListeners=" + successListeners
                + ", errorListeners=" + errorListeners
                + "}";
    }

    /**
     * Builds instances of type {@link ImmutableRetryStrategy ImmutableRetryStrategy}.
     * Initialize attributes and then invoke the {@link #build()} method to create an
     * immutable instance.
     * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
     * but instead used immediately to create instances.</em>
     */
    @NotThreadSafe
    public static final class Builder<T> {
        private static final long INIT_BIT_TRIGGER_STRATEGY = 0x1L;
        private static final long INIT_BIT_DELAY_STRATEGY = 0x2L;
        private static final long INIT_BIT_TERMINATE_STRATEGY = 0x4L;
        private long initBits = 0x7L;

        private @Nullable TriggerStrategy<T> triggerStrategy;
        private @Nullable DelayStrategy<T> delayStrategy;
        private @Nullable TerminateStrategy terminateStrategy;
        private List<Consumer<T>> successListeners = new ArrayList<Consumer<T>>();
        private List<Consumer<Throwable>> errorListeners = new ArrayList<Consumer<Throwable>>();

        private Builder() {
        }

        /**
         * Fill a builder with attribute values from the provided {@code RetryStrategy} instance.
         * Regular attribute values will be replaced with those from the given instance.
         * Absent optional values will not replace present values.
         * Collection elements and entries will be added, not replaced.
         *
         * @param instance The instance from which to copy values
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> from(RetryStrategy<T> instance) {
            Objects.requireNonNull(instance, "instance");
            this.triggerStrategy(instance.triggerStrategy());
            this.delayStrategy(instance.delayStrategy());
            this.terminateStrategy(instance.terminateStrategy());
            addAllSuccessListeners(instance.successListeners());
            addAllErrorListeners(instance.errorListeners());
            return this;
        }

        /**
         * Initializes the value for the {@link RetryStrategy#triggerStrategy() triggerStrategy} attribute.
         *
         * @param triggerStrategy The value for triggerStrategy
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> triggerStrategy(TriggerStrategy<T> triggerStrategy) {
            this.triggerStrategy = Objects.requireNonNull(triggerStrategy, "triggerStrategy");
            initBits &= ~INIT_BIT_TRIGGER_STRATEGY;
            return this;
        }

        /**
         * Initializes the value for the {@link RetryStrategy#delayStrategy() delayStrategy} attribute.
         *
         * @param delayStrategy The value for delayStrategy
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> delayStrategy(DelayStrategy<T> delayStrategy) {
            this.delayStrategy = Objects.requireNonNull(delayStrategy, "delayStrategy");
            initBits &= ~INIT_BIT_DELAY_STRATEGY;
            return this;
        }

        /**
         * Initializes the value for the {@link RetryStrategy#terminateStrategy() terminateStrategy} attribute.
         *
         * @param terminateStrategy The value for terminateStrategy
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> terminateStrategy(TerminateStrategy terminateStrategy) {
            this.terminateStrategy = Objects.requireNonNull(terminateStrategy, "terminateStrategy");
            initBits &= ~INIT_BIT_TERMINATE_STRATEGY;
            return this;
        }

        /**
         * Adds elements to {@link RetryStrategy#successListeners() successListeners} list.
         *
         * @param elements An iterable of successListeners elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addAllSuccessListeners(Iterable<? extends Consumer<T>> elements) {
            for (Consumer<T> element : elements) {
                this.successListeners.add(Objects.requireNonNull(element, "successListeners element"));
            }
            return this;
        }

        /**
         * Adds elements to {@link RetryStrategy#errorListeners() errorListeners} list.
         *
         * @param elements An iterable of errorListeners elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addAllErrorListeners(Iterable<? extends Consumer<Throwable>> elements) {
            for (Consumer<Throwable> element : elements) {
                this.errorListeners.add(Objects.requireNonNull(element, "errorListeners element"));
            }
            return this;
        }

        /**
         * Adds one element to {@link RetryStrategy#successListeners() successListeners} list.
         *
         * @param element A successListeners element
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addSuccessListener(Consumer<T> element) {
            this.successListeners.add(Objects.requireNonNull(element, "successListeners element"));
            return this;
        }

        /**
         * Adds elements to {@link RetryStrategy#successListeners() successListeners} list.
         *
         * @param elements An array of successListeners elements
         * @return {@code this} builder for use in a chained invocation
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public final Builder<T> addSuccessListeners(Consumer<T>... elements) {
            for (Consumer<T> element : elements) {
                this.successListeners.add(Objects.requireNonNull(element, "successListeners element"));
            }
            return this;
        }

        /**
         * Sets or replaces all elements for {@link RetryStrategy#successListeners() successListeners} list.
         *
         * @param elements An iterable of successListeners elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> successListeners(Iterable<? extends Consumer<T>> elements) {
            this.successListeners.clear();
            return addAllSuccessListeners(elements);
        }

        /**
         * Adds one element to {@link RetryStrategy#errorListeners() errorListeners} list.
         *
         * @param element A errorListeners element
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> addErrorListener(Consumer<Throwable> element) {
            this.errorListeners.add(Objects.requireNonNull(element, "errorListeners element"));
            return this;
        }

        /**
         * Adds elements to {@link RetryStrategy#errorListeners() errorListeners} list.
         *
         * @param elements An array of errorListeners elements
         * @return {@code this} builder for use in a chained invocation
         */
        @SafeVarargs
        @SuppressWarnings("varargs")
        public final Builder<T> addErrorListeners(Consumer<Throwable>... elements) {
            for (Consumer<Throwable> element : elements) {
                this.errorListeners.add(Objects.requireNonNull(element, "errorListeners element"));
            }
            return this;
        }

        /**
         * Sets or replaces all elements for {@link RetryStrategy#errorListeners() errorListeners} list.
         *
         * @param elements An iterable of errorListeners elements
         * @return {@code this} builder for use in a chained invocation
         */
        public final Builder<T> errorListeners(Iterable<? extends Consumer<Throwable>> elements) {
            this.errorListeners.clear();
            return addAllErrorListeners(elements);
        }

        /**
         * Builds a new {@link ImmutableRetryStrategy ImmutableRetryStrategy}.
         *
         * @return An immutable instance of RetryStrategy
         * @throws IllegalStateException if any required attributes are missing
         */
        public ImmutableRetryStrategy<T> build() {
            if (initBits != 0) {
                throw new IllegalStateException(formatRequiredAttributesMessage());
            }
            return new ImmutableRetryStrategy<>(
                    triggerStrategy,
                    delayStrategy,
                    terminateStrategy,
                    createUnmodifiableList(true, successListeners),
                    createUnmodifiableList(true, errorListeners));
        }

        private String formatRequiredAttributesMessage() {
            List<String> attributes = new ArrayList<>();
            if ((initBits & INIT_BIT_TRIGGER_STRATEGY) != 0) attributes.add("triggerStrategy");
            if ((initBits & INIT_BIT_DELAY_STRATEGY) != 0) attributes.add("delayStrategy");
            if ((initBits & INIT_BIT_TERMINATE_STRATEGY) != 0) attributes.add("terminateStrategy");
            return "Cannot build RetryStrategy, some of required attributes are not set " + attributes;
        }
    }
}
