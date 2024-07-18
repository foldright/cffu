package io.foldright.cffu.retry;


import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;


/**
 * The interface delay strategy.
 *
 * @param <T> CompletableFuture result type
 * @author Eric Lin
 */
@FunctionalInterface
public interface DelayStrategy<T> {

    /**
     * By attempts backoff strategy.
     */
    static <U> DelayStrategy<U> ofAttempts(long attempts, Function<Long, Long> strategy) {
        requireNonNull(strategy, "strategy is null");
        return (a, r, t) -> strategy.apply(attempts);
    }

    /**
     * Fixed duration backoff strategy.
     */
    static <U> DelayStrategy<U> fixedDuration(long millis) {
        return (a, r, t) -> millis;
    }

    /**
     * Sequential backoff strategy.
     */
    static <U> DelayStrategy<U> sequential() {
        return (a, r, t) -> 0L;
    }

    /**
     * Gets delayed time in milliseconds.Either result or throwable is null.
     *
     * @param attempts  the attempts
     * @param result    the result
     * @param throwable the throwable
     * @return the delay
     */
    long getDelay(long attempts, @Nullable T result, @Nullable Throwable throwable);
}
