package io.foldright.cffu2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.foldright.cffu2.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu2.internal.ExceptionLogger.Level.WARN;
import static io.foldright.cffu2.internal.ExceptionLogger.log;
import static io.foldright.cffu2.internal.ExceptionLogger.logUncaughtException;
import static java.lang.System.identityHashCode;
import static java.lang.Thread.currentThread;


/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CompletableFutureUtils#concurrencyLimitExecutor(int)
 * @see CffuFactory#concurrencyLimitExecutor(int)
 * @see com.google.common.util.concurrent.MoreExecutors#newSequentialExecutor(Executor)
 * @since 2.1.0
 */
final class ConcurrencyLimitExecutor implements Executor {
    private final int maxConcurrency;
    private final Executor executor;

    private final Lock lock = new ReentrantLock();
    @GuardedBy("lock")
    private final Deque<Runnable> queue = new ArrayDeque<>();
    @GuardedBy("lock")
    private int workerCount = 0;

    // for debugging and monitoring
    @GuardedBy("lock")
    private long syncExecutionTimes = 0;
    @GuardedBy("lock")
    private long workerCountIncrementTimes = 0;

    ConcurrencyLimitExecutor(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    @SuppressFBWarnings("UL_UNRELEASED_LOCK")
    public void execute(Runnable command) {
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
            // NOTE: `returnedFromExecute` is only accessed by the caller thread (single-threaded) too.
            final boolean[] returnedFromExecute = {false};
            executor.execute(() -> {
                final boolean isSyncExecution = currentThread().equals(callerThread) && !returnedFromExecute[0];
                if (isSyncExecution) {
                    increaseWorkerCount();
                    warnLogSyncExecution();
                    lock.unlock();
                    locking[0] = false;

                    // When executing synchronously:
                    //  - execute only the input command
                    //  - do NOT catch exceptions, let them propagate to the caller
                    try {
                        command.run();
                    } finally {
                        decreaseWorkerCountWithLock();
                    }
                } else {
                    work(command);
                }
            });
            returnedFromExecute[0] = true;

            // NOTE: do NOT move the statement below into the `finally` block,
            // because `workerCount` must NOT be incremented if `executor.execute()` throws an exception
            if (locking[0]) increaseWorkerCount();
        } finally {
            if (locking[0]) lock.unlock();
        }
    }

    private void work(Runnable firstTask) {
        // remove the interrupt bit before the task
        boolean interruptedDuringTask = Thread.interrupted();
        safeRun(firstTask);

        while (true) {
            Runnable task;
            lock.lock();
            try {
                task = queue.poll();
                if (task == null) {
                    workerCount--;
                    // ensure that if the thread was interrupted at all while processing, it is returned to
                    // the base Executor interrupted so that it may handle the interruption if it likes.
                    if (interruptedDuringTask) currentThread().interrupt();
                    break;
                }
            } finally {
                lock.unlock();
            }
            // remove the interrupt bit before each task
            interruptedDuringTask |= Thread.interrupted();
            safeRun(task);
        }
    }

    private static void safeRun(Runnable task) {
        try {
            task.run();
        } catch (Throwable e) {
            logUncaughtException(ERROR, "ConcurrencyLimitExecutor#work", e);
        }
    }

    @GuardedBy("lock")
    private void increaseWorkerCount() {
        workerCount++;

        //  check the concurrency limit issue
        if (!isPowerOfTwo(++workerCountIncrementTimes)) return;
        if (workerCount > maxConcurrency) log(ERROR, "ConcurrencyLimitExecutor#" + identityHashCode(this)
                + " has concurrency level " + workerCount + " that exceeds max concurrency (" + maxConcurrency + "),"
                + " this should never happen - please report this issue to the cffu library!");
    }

    private void decreaseWorkerCountWithLock() {
        lock.lock();
        try {
            workerCount--;
        } finally {
            lock.unlock();
        }
    }

    @GuardedBy("lock")
    private void warnLogSyncExecution() {
        if (!isPowerOfTwo(++syncExecutionTimes)) return;
        log(WARN, "ConcurrencyLimitExecutor#" + identityHashCode(this)
                + " detected synchronous execution (" + syncExecutionTimes + " times) in base executor ("
                + executor + "), which likely prevent maximizing the concurrency limit"
                + " (current concurrency level: " + workerCount + ", max concurrency: " + maxConcurrency + ")");
    }

    private static boolean isPowerOfTwo(long n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    @Override
    public String toString() {
        return "ConcurrencyLimitExecutor@" + identityHashCode(this)
                + " (maxConcurrency: " + maxConcurrency + ", executor: " + executor + ")";
    }
}
