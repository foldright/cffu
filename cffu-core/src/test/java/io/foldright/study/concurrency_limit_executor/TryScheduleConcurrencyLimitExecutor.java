package io.foldright.study.concurrency_limit_executor;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

public final class TryScheduleConcurrencyLimitExecutor implements Executor {
    private final int maxConcurrency;
    private final Executor delegate;

    private final AtomicInteger activeCount = new AtomicInteger(0);
    private final ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

    TryScheduleConcurrencyLimitExecutor(int maxConcurrency, Executor delegate) {
        this.maxConcurrency = maxConcurrency;
        this.delegate = delegate;
    }

    public void execute(Runnable task) {
        queue.offer(task); // Thread-safe queue, no lock needed
        trySchedule();
    }

    private void trySchedule() {
        while (activeCount.get() < maxConcurrency) {
            Runnable next = queue.poll();
            if (next == null) break;

            if (activeCount.incrementAndGet() < maxConcurrency) {
                delegate.execute(() -> {
                    try {
                        next.run();
                    } finally {
                        activeCount.decrementAndGet();
                        trySchedule();
                    }
                });
            } else {
                activeCount.decrementAndGet();
                queue.offer(next);
                break;
            }
        }
    }
}
