package io.foldright.cffu.retry;

import java.time.Duration;
import java.util.Optional;


/**
 * The interface Terminate strategy.
 *
 * @author Eric Lin
 */
public interface TerminateStrategy {

    /**
     * Of attempts terminate strategy.
     *
     * @param attempts the attempts
     * @return the terminate strategy
     */
    static TerminateStrategy ofAttempts(long attempts) {
        return new TerminateStrategy() {
            @Override
            public Optional<Long> maxAttempts() {
                return Optional.of(attempts);
            }
        };
    }

    /**
     * Of timeout terminate strategy.
     *
     * @param duration the duration
     * @return the terminate strategy
     */
    static TerminateStrategy ofTimeout(Duration duration) {
        return new TerminateStrategy() {
            @Override
            public Optional<Duration> timeout() {
                return Optional.of(duration);
            }
        };
    }

    /**
     * Util success terminate strategy.
     *
     * @return the terminate strategy
     */
    static TerminateStrategy utilSuccess() {
        return new TerminateStrategy() {
        };
    }

    /**
     * Max attempts optional.
     *
     * @return the optional
     */
    default Optional<Long> maxAttempts() {
        return Optional.empty();
    }

    /**
     * Timeout optional.
     *
     * @return the optional
     */
    default Optional<Duration> timeout() {
        return Optional.empty();
    }
}
