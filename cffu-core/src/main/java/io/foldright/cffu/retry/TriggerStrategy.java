package io.foldright.cffu.retry;


/**
 * The interface Trigger strategy.
 *
 * @param <T> the type parameter of the retrying CompletableFuture
 * @author Eric Lin
 */
@FunctionalInterface
public interface TriggerStrategy<T> {
    /**
     * Should retry boolean.
     *
     * @param result    the result
     * @param throwable the throwable
     * @return the boolean
     */
    boolean shouldRetry(T result, Throwable throwable);

    /**
     * By throwable trigger strategy.
     *
     * @param <U> the type parameter
     * @return the trigger strategy
     */
    static <U> TriggerStrategy<U> byThrowable() {
        return (r, t) -> t != null;
    }
}
