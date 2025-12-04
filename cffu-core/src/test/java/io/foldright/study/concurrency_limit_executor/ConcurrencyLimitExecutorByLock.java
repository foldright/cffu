package io.foldright.study.concurrency_limit_executor;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.foldright.cffu2.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu2.internal.ExceptionLogger.logUncaughtException;
import static java.lang.Thread.currentThread;


public final class ConcurrencyLimitExecutorByLock implements Executor {
    private final int maxConcurrency;
    private final Executor executor;

    private final Lock lock = new ReentrantLock();
    @GuardedBy("lock")
    private final Deque<Runnable> queue = new ArrayDeque<>(64);
    @GuardedBy("lock")
    private int workerCount = 0;

    ConcurrencyLimitExecutorByLock(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        final IdempotentUnlocker unlocker = lock(lock);
        try {
            if (workerCount >= maxConcurrency) {
                queue.add(command);
                return;
            }

            final Thread callerThread = currentThread();
            final boolean[] returnedFromCmdRun = {false};
            executor.execute(() -> {
                if (currentThread().equals(callerThread) && !returnedFromCmdRun[0]) {
                    // If executing synchronously, run the input command only and do NOT increment workerCount!
                    command.run();
                    unlocker.unlock();
                    return;
                }
                work(command);
            });
            returnedFromCmdRun[0] = true;

            if (unlocker.isLocking()) workerCount++;
        } finally {
            unlocker.unlock();
        }
    }

    private void work(Runnable firstTask) {
        safeRun(firstTask);

        while (true) {
            Runnable task;
            lock.lock();
            try {
                task = queue.poll();
                if (task == null) {
                    workerCount--;
                    break;
                }
            } finally {
                lock.unlock();
            }
            safeRun(task);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void safeRun(Runnable task) {
        // NOTE: The interrupted status of the thread needs to be cleared before running the task.
        Thread.interrupted();
        try {
            task.run();
        } catch (Throwable e) {
            logUncaughtException(ERROR, "ConcurrencyLimitExecutorByLock#Worker", e);
        }
    }

    @CheckReturnValue
    private IdempotentUnlocker lock(Lock lock) {
        lock.lock();
        return new IdempotentUnlocker(lock);
    }

    private static class IdempotentUnlocker {
        private final Lock lock;
        private final AtomicBoolean unlocked = new AtomicBoolean(false);

        public IdempotentUnlocker(Lock lock) {this.lock = lock;}

        public void unlock() {
            if (unlocked.compareAndSet(false, true)) {
                lock.unlock();
            }
        }

        public boolean isLocking() {return !unlocked.get();}
    }
}
