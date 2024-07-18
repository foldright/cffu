package io.foldright.cffu.retry;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.function.Predicate;


/**
 * Default implementation of {@link TriggerStrategy}.
 * Only consider throwable as a possible retryable result.
 * This class support allowList & rejectList of Exception types.
 * {@link CompositeTriggerStrategy#retryByThrowable()} support user-defined trigger predicate.
 * <p>
 * It is recommended to use {@link ImmutableTriggerStrategy}
 *
 * @param <T> the type parameter of CompletableFuture
 * @author Eric Lin
 * @see ImmutableTriggerStrategy
 */
abstract class CompositeTriggerStrategy<T> implements TriggerStrategy<T> {

    /**
     * Exceptions are considered retryable.
     */
    abstract List<Class<? extends Throwable>> retryExceptions();

    /**
     * Exceptions besides this list are considered retryable.
     * <p>
     * This property has higher priority over {@link CompositeTriggerStrategy#retryExceptions}.
     */
    abstract List<Class<? extends Throwable>> ignoredExceptions();

    /**
     * Retry by throwable predicate.
     *
     * @return the predicate
     */
    @Nullable
    abstract Predicate<? super Throwable> retryByThrowable();

    @Override
    public final boolean shouldRetry(T result, Throwable throwable) {
        return shouldRetry(throwable);
    }

    /**
     * Composite method to test if a throwable is retryable.
     *
     * @param throwable the throwable
     * @return the boolean
     */
    protected boolean shouldRetry(Throwable throwable) {
        if (throwable == null) {
            return false;
        }
        if (throwable instanceof CompletionException || throwable instanceof ExecutionException) {
            return shouldRetry(throwable.getCause());
        }

        boolean result;
        if (!ignoredExceptions().isEmpty()) {
            result = ignoredExceptions().stream()
                    .noneMatch(throwable.getClass()::isAssignableFrom);
        } else if (!retryExceptions().isEmpty()) {
            result = ignoredExceptions().stream()
                    .anyMatch(throwable.getClass()::isAssignableFrom);
        } else {
            result = false;
        }
        return result ||
                Optional.ofNullable(retryByThrowable())
                        .map(p -> p.test(throwable))
                        .orElse(false);
    }
}
