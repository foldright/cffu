package io.foldright.study.concurrency_limit_executor;

import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.foldright.cffu2.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu2.internal.ExceptionLogger.logUncaughtException;


public final class ConcurrencyLimitExecutorByLock implements Executor {
    private final int maxConcurrency;
    private final Executor executor;

    private final Lock lock = new ReentrantLock();

    @GuardedBy("lock")
    private final Deque<Runnable> queue = new ArrayDeque<>();
    @GuardedBy("lock")
    private int workerCount = 0;

    ConcurrencyLimitExecutorByLock(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        lock.lock();
        try {
            if (workerCount < maxConcurrency) {
                // FIXME may execute command synchronously.
                executor.execute(new Worker(command));
                // FIXME if execute synchronously, must NOT increment workerCount!
                workerCount++;
            } else {
                queue.add(command);
            }
        } finally {
            lock.unlock();
        }
    }

    private class Worker implements Runnable {
        Runnable firstTask;

        public Worker(Runnable firstTask) {
            this.firstTask = firstTask;
        }

        public void run() {
            safeRun(firstTask);

            // FIXME if execute synchronously, must NOT relay run other tasks!
            while (true) {
                Runnable task;
                try {
                    lock.lock();
                    task = queue.poll();
                    if (task == null && Thread.currentThread().isInterrupted()) {
                        workerCount--;
                        break;
                    }
                } finally {
                    lock.unlock();
                }
                if (task != null) safeRun(task);
            }
        }

        void safeRun(Runnable task) {
            try {
                task.run();
            } catch (Throwable e) {
                logUncaughtException(ERROR, "ConcurrencyLimitExecutorByLock#Worker", e);
            }
        }
    }
}

