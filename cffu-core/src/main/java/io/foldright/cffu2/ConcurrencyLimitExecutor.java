package io.foldright.cffu2;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.concurrent.GuardedBy;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static io.foldright.cffu2.internal.CffuLogger.Level.ERROR;
import static io.foldright.cffu2.internal.CffuLogger.Level.WARN;
import static io.foldright.cffu2.internal.CffuLogger.log;
import static io.foldright.cffu2.internal.CffuLogger.logUncaughtException;
import static java.lang.Thread.currentThread;


/**
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CompletableFutureUtils#concurrencyLimitExecutor(int)
 * @see CffuFactory#concurrencyLimitExecutor(int)
 * @see com.google.common.util.concurrent.SequentialExecutor
 * @since 2.1.0
 */
@SuppressWarnings("JavadocReference")
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
    private long syncRunTimes = 0;
    @GuardedBy("lock")
    private long exceedLimitTimes = 0;

    ConcurrencyLimitExecutor(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    @SuppressFBWarnings("UL_UNRELEASED_LOCK")
    public void execute(Runnable command) {
        lock.lock();
        // NOTE: variable `locking` is only accessed by the caller thread (single-threaded);
        //       so no need to declare it as type AtomicBoolean for thread safety.
        final boolean[] locking = {true};
        try {
            queue.add(command);
            if (workerCount >= maxConcurrency) return;

            final Thread callerThread = currentThread();
            // NOTE: `returnedFromExecute` is only accessed by the caller thread (single-threaded) too.
            final boolean[] returnedFromExecute = {false};
            final Runnable submittedTask = new Runnable() {
                @Override
                public void run() {
                    final boolean isSyncExecution = currentThread().equals(callerThread) && !returnedFromExecute[0];
                    if (isSyncExecution) syncRun();
                    else asyncWork();
                }

                private void syncRun() {
                    try {
                        incrementWorkerCount();
                        queue.removeLastOccurrence(command);
                        warnLogSyncRunning();
                    } finally {
                        lock.unlock();
                        locking[0] = false;
                    }
                    // For synchronous running of the submitted task:
                    //  - run the submitted command only, do NOT run other commands in the queue
                    //  - do NOT catch exceptions, let them propagate to the caller
                    try {
                        command.run();
                    } finally {
                        decrementWorkerCountWithLock();
                    }
                }

                // overrides method toString for debugging and monitoring
                @Override
                public String toString() {
                    return "Submitted task (command: " + command + ") of " + ConcurrencyLimitExecutor.this;
                }
            };
            executor.execute(submittedTask);
            returnedFromExecute[0] = true;

            // NOTE 1: if `locking` is true, the submitted task will run asynchronously;
            //   increment worker count here in the `execute` method; otherwise, for synchronous running,
            //   the worker count is incremented within the submitted task before returning from `execute`.
            // NOTE 2: do NOT move the worker count increment below into the `finally` block, because `workerCount`
            //   must NOT be incremented if `executor.execute()` throws exceptions (e.g. RejectedExecutionEx).
            if (locking[0]) incrementWorkerCount();
        } finally {
            if (locking[0]) lock.unlock();
        }
    }

    private void asyncWork() {
        boolean interruptedDuringTask = false;
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
                    return;
                }
            } finally {
                lock.unlock();
            }
            // remove the interrupt bit before each task
            interruptedDuringTask |= Thread.interrupted();
            // safely execute the task in `try-catch` block
            try {
                task.run();
            } catch (Throwable e) {
                logUncaughtException(ERROR, super.toString() + "#asyncWork", e);
            }
        }
    }

    @GuardedBy("lock")
    @SuppressFBWarnings("AT_NONATOMIC_OPERATIONS_ON_SHARED_VARIABLE")
    private void incrementWorkerCount() {
        workerCount++;
        //  check the concurrency limit issue
        if (workerCount <= maxConcurrency) return;
        if (isPowerOfTwo(++exceedLimitTimes)) log(ERROR, exceedLimitTimes + " concurrency limit violation(s)"
                + " (current: " + workerCount + " > max: " + maxConcurrency + ") detected in " + this
                + ". This should never happen - please report this issue to the cffu library!");
    }

    private void decrementWorkerCountWithLock() {
        lock.lock();
        try {
            workerCount--;
        } finally {
            lock.unlock();
        }
    }

    @GuardedBy("lock")
    private void warnLogSyncRunning() {
        if (isPowerOfTwo(++syncRunTimes)) log(WARN, syncRunTimes + " synchronous execution(s) detected"
                + " in base executor of " + this + " - base executor runs task on caller thread, likely prevent"
                + "reaching max concurrency! (current: " + workerCount + ", max: " + maxConcurrency + ")");
    }

    /**
     * Checks if a number is a power of two. Used for throttling repetitive log volume by emitting logs
     * only when the count reaches a power of two (1, 2, 4, 8, 16, 32, 64, ...). This exponential sampling strategy
     * reduces log volume while ensuring that early occurrences are always captured for debugging and monitoring.
     *
     * @see <a href="https://books.google.com/books/about/Hacker_s_Delight.html?id=VicPJYM0I5QC">Algorithm reference:
     * "Hacker's Delight" (2nd edition) by Henry S. Warren Jr., Chapter 3. Power-of-2 Boundaries</a>
     */
    @VisibleForTesting
    static boolean isPowerOfTwo(long n) {
        return n > 0 && (n & (n - 1)) == 0;
    }

    @Override
    public String toString() {
        return super.toString() + " (maxConcurrency: " + maxConcurrency + ", base executor: " + executor + ")";
    }
}
