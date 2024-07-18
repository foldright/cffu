package io.foldright.cffu;

import io.foldright.cffu.retry.RetryStrategy;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.orTimeout;

/**
 * This class {@link RetryingCompletableFuture} execute specific stateless retry strategy.
 *
 * @author Eric Lin
 * @see io.foldright.cffu.retry.ImmutableRetryStrategy
 * @see CompletableFutureUtils
 */
class RetryingCompletableFuture<T> {

    private final RetryStrategy<T> retryStrategy;

    private final LongAdder attemptCounter = new LongAdder();

    private final List<Future<?>> futures = new CopyOnWriteArrayList<>();

    public RetryingCompletableFuture(RetryStrategy<T> retryStrategy) {
        this.retryStrategy = retryStrategy;
    }

    /**
     * Support cancellation for retrying tasks.
     * @param supplier retryable completionStage task
     * @param delayer ScheduledExecutorService to delay retryable task
     * @return retry result
     */
    CompletableFuture<T> toCompletableFuture(Supplier<CompletionStage<T>> supplier, ScheduledExecutorService delayer) {
        CompletableFuture<T> result = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                futures.forEach(f -> f.cancel(mayInterruptIfRunning));
                return super.cancel(mayInterruptIfRunning);
            }
        };
        retryStrategy.terminateStrategy()
                .timeout()
                .ifPresent(duration -> orTimeout(result, duration.toMillis(), TimeUnit.MILLISECONDS));
        retry(supplier, delayer, result);
        return result;
    }

    private void retry(Supplier<CompletionStage<T>> stageSupplier, ScheduledExecutorService delayer, CompletableFuture<T> promise) {
        stageSupplier.get()
            .thenApply(result -> {
                retryStrategy.successListeners().forEach(c -> c.accept(result));
                long delay = getDelayByResult(result);
                if (delay < 0L) {
                    promise.complete(result);
                }
                return -1L;
            })
            .exceptionally(throwable -> {
                retryStrategy.errorListeners().forEach(c -> c.accept(throwable));
                long delay = getDelayByException(throwable);
                if (delay < 0L) {
                    promise.completeExceptionally(throwable);
                }
                return delay;
            })
            .thenAccept(delay -> {
                if (delay == 0L) {
                    Future<?> task = delayer.submit(() -> retry(stageSupplier, delayer, promise));
                    futures.add(task);
                } else if (delay > 0L) {
                    ScheduledFuture<?> schedule = delayer.schedule(() ->
                        retry(stageSupplier, delayer, promise), delay, TimeUnit.MILLISECONDS);
                    futures.add(schedule);
                }
            });
    }

    private long getDelayByResult(T result) {
        attemptCounter.increment();
        long attempts = attemptCounter.longValue();
        Optional<Long> maxAttemptsOpt = retryStrategy.terminateStrategy()
                .maxAttempts();
        if (maxAttemptsOpt.isPresent()) {
            if (attempts > maxAttemptsOpt.get()) {
                throw new IllegalStateException("more attempts than expected");
            }
            if (attempts == maxAttemptsOpt.get()) {
                return -1L;
            }
        }
        if (!retryStrategy.triggerStrategy().shouldRetry(result, null)) {
            return -1L;
        }
        return retryStrategy.delayStrategy().getDelay(attempts, result, null);
    }

    private long getDelayByException(Throwable throwable) {
        attemptCounter.increment();
        long attempts = attemptCounter.longValue();
        Optional<Long> maxAttemptsOpt = retryStrategy.terminateStrategy()
                .maxAttempts();
        if (maxAttemptsOpt.isPresent()) {
            if (attempts > maxAttemptsOpt.get()) {
                throw new IllegalStateException("more attempts than expected");
            }
            if (attempts == maxAttemptsOpt.get()) {
                return -1L;
            }
        }
        if (!retryStrategy.triggerStrategy().shouldRetry(null, throwable)) {
            return -1L;
        }
        return retryStrategy.delayStrategy().getDelay(attempts, null, throwable);
    }
}
