package io.foldright.study.concurrency_limit_executor;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFutureTask;
import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.concurrent.GuardedBy;
import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static io.foldright.cffu2.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu2.internal.ExceptionLogger.logUncaughtException;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newCachedThreadPool;
import static java.util.concurrent.Executors.newSingleThreadScheduledExecutor;


public final class ConcurrencyLimitExecutorByLock implements Executor {

    public static void main(String[] args) {
        ExecutorService pool = newCachedThreadPool();
        ConcurrencyLimitExecutorByLock executor = new ConcurrencyLimitExecutorByLock(2, pool);

        List<ListenableFutureTask<Integer>> tasks = IntStream.range(0, 5)
                .mapToObj(__ -> ListenableFutureTask.create(() -> {
                    if (currentThread().isInterrupted()) {
                        return -1;
                    }
                    try {
                        Thread.sleep(Duration.ofSeconds(3));
                    } catch (InterruptedException ex) {
                        currentThread().interrupt();
                        return 0;
                    }
                    return 1;
                }))
                .toList();
        tasks.forEach(executor::execute);

        Futures.withTimeout(Futures.allAsList(tasks), 2, TimeUnit.SECONDS, newSingleThreadScheduledExecutor());
        Futures.whenAllComplete(tasks)
                .run(() -> tasks.forEach(t -> {
                    switch (t.state()) {
                        case SUCCESS -> System.out.println("ok: " + t.resultNow());
                        case FAILED -> System.out.println("err" + t.exceptionNow().getMessage());
                        case CANCELLED -> System.out.println("canceled");
                        case RUNNING -> throw new AssertionError();
                    }
                }), directExecutor());
    }

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

