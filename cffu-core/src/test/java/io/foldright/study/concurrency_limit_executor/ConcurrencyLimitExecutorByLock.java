package io.foldright.study.concurrency_limit_executor;

import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.foldright.cffu2.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu2.internal.ExceptionLogger.Level.WARN;
import static io.foldright.cffu2.internal.ExceptionLogger.logException;
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
    @GuardedBy("lock")
    private long syncExecutionTimes = 0;

    ConcurrencyLimitExecutorByLock(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    public void execute(@NonNull Runnable command) {
        lock.lock();
        // NOTE: variable `locking` is only accessed by the caller thread (single-threaded),
        // so no need to use AtomicBoolean
        final boolean[] locking = {true};
        try {
            if (workerCount >= maxConcurrency) {
                queue.add(command);
                return;
            }

            final Thread callerThread = currentThread();
            // NOTE: `returnedFromCmdRun` is only accessed by the caller thread (single-threaded) too.
            final boolean[] returnedFromCmdRun = {false};
            executor.execute(() -> {
                if (currentThread().equals(callerThread) && !returnedFromCmdRun[0]) {
                    // If executing synchronously, run the input command only
                    workerCount++;
                    increaseSyncExecutionAndWarn();
                    lock.unlock();
                    locking[0] = false;

                    try {
                        // do NOT catch exception when executing synchronously, propagate exception to caller
                        command.run();
                    } finally {
                        decreaseWorkerCountWithLock();
                    }
                } else {
                    work(command);
                }
            });
            returnedFromCmdRun[0] = true;

            // NOTE: do NOT move the statement below into the finally block,
            // because `workerCount` must NOT be incremented if `executor.execute()` throws an exception
            if (locking[0]) workerCount++;
        } finally {
            if (locking[0]) lock.unlock();
        }
    }

    private void decreaseWorkerCountWithLock() {
        lock.lock();
        try {
            workerCount--;
        } finally {
            lock.unlock();
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

    @GuardedBy("lock")
    private void increaseSyncExecutionAndWarn() {
        if (!isPowerOfTwo(syncExecutionTimes++)) return;
        logException(WARN, "Detected synchronous execution (" + syncExecutionTimes + " times) in delegate executor ("
                + executor + "), which may prevent maximizing the concurrency limit (" + maxConcurrency + ")", null);
    }

    private static boolean isPowerOfTwo(long n) {
        return n >= 0 && (n & (n - 1)) == 0;
    }
}
