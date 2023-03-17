package io.foldright.cffu;

////////////////////////////////////////////////////////////////////////////////
//# delay execution helper classes
//
//  below code is copied from CompletableFuture with small adoption
////////////////////////////////////////////////////////////////////////////////

import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.concurrent.*;
import java.util.function.BiConsumer;


/**
 * Singleton delay scheduler, used only for starting and cancelling tasks
 */
final class Delayer {
    private static final ScheduledThreadPoolExecutor delayer;

    static {
        delayer = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory());
        delayer.setRemoveOnCancelPolicy(true);
    }

    /**
     * @return a Future that can be used to cancel the delayed task
     * @see FutureCanceller
     * @see DelayedExecutor#execute(Runnable)
     */
    static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
        return delayer.schedule(command, delay, unit);
    }

    /**
     * @return a Future can be used to cancel the delayed task(timeout CF)
     * @see FutureCanceller
     * @see Cffu#orTimeout(long, TimeUnit)
     */
    public static ScheduledFuture<?> delayToTimoutCf(CompletableFuture<?> cf, long delay, TimeUnit unit) {
        return delay(new CfTimeout(cf), delay, unit);
    }

    /**
     * @return a Future can be used to cancel the delayed task(complete CF)
     * @see FutureCanceller
     * @see Cffu#completeOnTimeout(Object, long, TimeUnit)
     */
    public static <T> ScheduledFuture<?> delayToCompleteCf(CompletableFuture<T> cf, T value, long delay, TimeUnit unit) {
        return delay(new CfCompleter<>(cf, value), delay, unit);
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
}

/**
 * An executor wrapper with delayed execution.
 */
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

////////////////////////////////////////////////////////////////////////////////
// Little classified lambdas to better support monitoring
////////////////////////////////////////////////////////////////////////////////

/**
 * Action to submit task(Runnable) to executor
 *
 * @see CffuFactory#delayedExecutor(long, TimeUnit)
 * @see CffuFactory#delayedExecutor(long, TimeUnit, Executor)
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
 * Action to cf.completeExceptionally with TimeoutException
 */
final class CfTimeout implements Runnable {
    private final CompletableFuture<?> cf;

    CfTimeout(CompletableFuture<?> cf) {
        this.cf = cf;
    }

    @Override
    public void run() {
        if (cf != null && !cf.isDone())
            cf.completeExceptionally(new TimeoutException());
    }
}

/**
 * Action to complete cf
 */
final class CfCompleter<T> implements Runnable {
    private final CompletableFuture<T> cf;
    private final T value;

    @SuppressWarnings("BoundedWildcard")
    CfCompleter(CompletableFuture<T> cf, T value) {
        this.cf = cf;
        this.value = value;
    }

    @Override
    public void run() {
        if (cf != null) cf.complete(value);
    }
}

/**
 * Action to cancel unneeded scheduled task by Future (for example timeouts)
 *
 * @see Cffu#orTimeout(long, TimeUnit)
 * @see Cffu#completeOnTimeout(Object, long, TimeUnit)
 * @see Delayer#delay(Runnable, long, TimeUnit)
 * @see Delayer#delayToTimoutCf(CompletableFuture, long, TimeUnit)
 * @see Delayer#delayToCompleteCf(CompletableFuture, Object, long, TimeUnit)
 */
final class FutureCanceller implements BiConsumer<Object, Throwable> {
    private final Future<?> f;

    FutureCanceller(Future<?> f) {
        this.f = f;
    }

    @Override
    public void accept(Object ignore, Throwable ex) {
        if (ex == null && f != null && !f.isDone())
            f.cancel(false);
    }
}
