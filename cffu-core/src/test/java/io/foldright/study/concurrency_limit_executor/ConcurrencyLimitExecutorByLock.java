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
    private final LockHandler lockHandler = new LockHandler(lock);

    @GuardedBy("lock")
    private final Deque<Runnable> queue = new ArrayDeque<>();
    @GuardedBy("lock")
    private int workerCount = 0;

    ConcurrencyLimitExecutorByLock(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        final LockHandler.Unlocker unlocker = lockHandler.lock();
        try {
            if (workerCount >= maxConcurrency) {
                queue.add(command);
                return;
            }

            final Thread callerThread = currentThread();
            final boolean[] returnedFromCmdRun = {false};

            executor.execute(() -> {
                if (currentThread().equals(callerThread) && returnedFromCmdRun[0]) {
                    // if execute synchronously, run input command only and must NOT increment workerCount!
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
            try {
                lock.lock();
                task = queue.poll();
                if (task == null && Thread.interrupted()) {
                    workerCount--;
                    break;
                }
            } finally {
                lock.unlock();
            }
            if (task != null) safeRun(task);
        }
    }

    private void safeRun(Runnable task) {
        try {
            task.run();
        } catch (Throwable e) {
            logUncaughtException(ERROR, "ConcurrencyLimitExecutorByLock#Worker", e);
        }
    }

    private static class LockHandler {
        private final Lock lock;

        private LockHandler(Lock lock) {this.lock = lock;}

        @CheckReturnValue
        public Unlocker lock() {
            lock.lock();
            return new Unlocker();
        }

        public class Unlocker {
            private final AtomicBoolean unlocked = new AtomicBoolean(false);

            public void unlock() {
                if (unlocked.compareAndSet(false, true)) {
                    lock.unlock();
                }
            }

            public boolean isLocking() {return !unlocked.get();}
        }
    }
}

