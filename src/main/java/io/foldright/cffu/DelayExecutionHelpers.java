package io.foldright.cffu;

////////////////////////////////////////////////////////////////////////////////
//# delay execution helper classes
//
// source codes is copied from CompletableFuture
////////////////////////////////////////////////////////////////////////////////


import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.concurrent.*;
import java.util.function.BiConsumer;


/**
 * Singleton delay scheduler, used only for starting and cancelling tasks.
 */
final class Delayer {
    static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
        return delayer.schedule(command, delay, unit);
    }

    private static final class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("CffuDelayScheduler");
            return t;
        }
    }

    private static final ScheduledThreadPoolExecutor delayer;

    static {
        (delayer = new ScheduledThreadPoolExecutor(1, new Delayer.DaemonThreadFactory()))
                .setRemoveOnCancelPolicy(true);
    }
}

// Little classified lambdas to better support monitoring

final class DelayedExecutor implements Executor {
    private final long delay;
    private final TimeUnit unit;
    private final Executor executor;

    DelayedExecutor(long delay, TimeUnit unit, Executor executor) {
        this.delay = delay;
        this.unit = unit;
        this.executor = executor;
    }

    @Override
    public void execute(@NonNull Runnable r) {
        Delayer.delay(new TaskSubmitter(executor, r), delay, unit);
    }
}

/**
 * Action to submit user task
 */
final class TaskSubmitter implements Runnable {
    private final Executor executor;
    private final Runnable action;

    TaskSubmitter(Executor executor, Runnable action) {
        this.executor = executor;
        this.action = action;
    }

    @Override
    public void run() {
        executor.execute(action);
    }
}

/**
 * Action to completeExceptionally on timeout
 */
final class Timeout implements Runnable {
    private final CompletableFuture<?> f;

    Timeout(CompletableFuture<?> f) {
        this.f = f;
    }

    @Override
    public void run() {
        if (f != null && !f.isDone())
            f.completeExceptionally(new TimeoutException());
    }
}

/**
 * Action to complete on timeout
 */
final class DelayedCompleter<U> implements Runnable {
    private final CompletableFuture<U> f;
    private final U u;

    @SuppressWarnings("BoundedWildcard")
    DelayedCompleter(CompletableFuture<U> f, U u) {
        this.f = f;
        this.u = u;
    }

    @Override
    public void run() {
        if (f != null) f.complete(u);
    }
}

/**
 * Action to cancel unneeded timeouts
 */
final class Canceller implements BiConsumer<Object, Throwable> {
    private final Future<?> f;

    Canceller(Future<?> f) {
        this.f = f;
    }

    @Override
    public void accept(Object ignore, Throwable ex) {
        if (ex == null && f != null && !f.isDone())
            f.cancel(false);
    }
}
