package io.foldright.study.concurrency_limit_executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;


public final class NaiveConcurrencyLimitExecutor implements Executor {
    private final int limit;
    private final Executor executor;

    private final AtomicInteger submittingCount = new AtomicInteger();
    private final AtomicInteger workerCount = new AtomicInteger();
    private final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    NaiveConcurrencyLimitExecutor(int limit, Executor executor) {
        this.limit = limit;
        this.executor = executor;
    }

    @Override
    public void execute(Runnable firstTask) {
        submittingCount.incrementAndGet();
        try {
            while (true) {
                final int wc = workerCount.get();
                if (wc == limit) {
                    queue.add(firstTask);
                    return;
                }
                if (workerCount.compareAndSet(wc, wc + 1)) {
                    executor.execute(new Worker(firstTask));
                    return;
                }
            }
        } finally {
            submittingCount.decrementAndGet();
        }
    }

    private class Worker implements Runnable {
        Runnable firstTask;

        public Worker(Runnable firstTask) {
            this.firstTask = firstTask;
        }

        public void run() {
            firstTask.run();

            while (true) {
                final Runnable next = queue.poll();
                if (next != null) next.run();
                else if (submittingCount.get() > 0) continue;
                else break;
            }

            workerCount.decrementAndGet();
        }
    }
}
