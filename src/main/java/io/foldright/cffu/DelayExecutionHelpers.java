package io.foldright.cffu;

////////////////////////////////////////////////////////////////////////////////
//# delay execution helper classes
//
// source codes is copied from CompletableFuture
////////////////////////////////////////////////////////////////////////////////


import java.util.concurrent.*;
import java.util.function.BiConsumer;


/**
 * Singleton delay scheduler, used only for starting and
 * cancelling tasks.
 */
final class Delayer {
    static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
        return delayer.schedule(command, delay, unit);
    }

    static final class DaemonThreadFactory implements ThreadFactory {
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("CffuDelayScheduler");
            return t;
        }
    }

    static final ScheduledThreadPoolExecutor delayer;

    static {
        (delayer = new ScheduledThreadPoolExecutor(
                1, new Delayer.DaemonThreadFactory())).
                setRemoveOnCancelPolicy(true);
    }
}

// Little class-ified lambdas to better support monitoring

final class DelayedExecutor implements Executor {
    final long delay;
    final TimeUnit unit;
    final Executor executor;

    DelayedExecutor(long delay, TimeUnit unit, Executor executor) {
        this.delay = delay;
        this.unit = unit;
        this.executor = executor;
    }

    public void execute(Runnable r) {
        Delayer.delay(new TaskSubmitter(executor, r), delay, unit);
    }
}

/**
 * Action to submit user task
 */
final class TaskSubmitter implements Runnable {
    final Executor executor;
    final Runnable action;

    TaskSubmitter(Executor executor, Runnable action) {
        this.executor = executor;
        this.action = action;
    }

    public void run() {
        executor.execute(action);
    }
}

/**
 * Action to completeExceptionally on timeout
 */
final class Timeout implements Runnable {
    final CompletableFuture<?> f;

    Timeout(CompletableFuture<?> f) {
        this.f = f;
    }

    public void run() {
        if (f != null && !f.isDone())
            f.completeExceptionally(new TimeoutException());
    }
}

/**
 * Action to complete on timeout
 */
final class DelayedCompleter<U> implements Runnable {
    final CompletableFuture<U> f;
    final U u;

    DelayedCompleter(CompletableFuture<U> f, U u) {
        this.f = f;
        this.u = u;
    }

    public void run() {
        if (f != null)
            f.complete(u);
    }
}

/**
 * Action to cancel unneeded timeouts
 */
final class Canceller implements BiConsumer<Object, Throwable> {
    final Future<?> f;

    Canceller(Future<?> f) {
        this.f = f;
    }

    public void accept(Object ignore, Throwable ex) {
        if (ex == null && f != null && !f.isDone())
            f.cancel(false);
    }
}
