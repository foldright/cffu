package io.foldright.cffu.retry;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;


/**
 * Retry strategy for {@link CompletableFuture}.
 * <p>
 * Implementation should be immutable. Default implementation is {@link ImmutableRetryStrategy}
 *
 * @param <T> the type parameter
 * @author Eric Lin
 * @see ImmutableRetryStrategy
 */
public interface RetryStrategy<T> {

    /**
     * @return the trigger strategy
     */
    TriggerStrategy<T> triggerStrategy();

    /**
     * @return the delay strategy
     */
    DelayStrategy<T> delayStrategy();

    /**
     * @return the terminate strategy
     */
    TerminateStrategy terminateStrategy();

    /**
     * Called after a successful attempt;
     *
     * @return the success listeners
     */
    List<Consumer<T>> successListeners();


    /**
     * Called after every unsuccessful attempt at a retry.
     *
     * @return the error listeners
     */
    List<Consumer<Throwable>> errorListeners();
}
