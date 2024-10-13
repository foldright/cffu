package io.foldright.cffu;

////////////////////////////////////////////////////////////////////////////////
//# delay execution helper classes
//
//  below code is copied from CompletableFuture with small adoption
////////////////////////////////////////////////////////////////////////////////

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;


/**
 * Singleton delay scheduler, used only for starting and cancelling tasks
 * <p>
 * code is copied from {@link CompletableFuture.Delayer} with small adoption.
 */
@SuppressWarnings("JavadocReference")
final class Delayer {
    /**
     * @return a Future that can be used to cancel the delayed task
     * @see FutureCanceller
     * @see DelayedExecutor#execute(Runnable)
     */
    static ScheduledFuture<?> delay(Runnable command, long delay, TimeUnit unit) {
        return DelayerHolder.delayer.schedule(command, delay, unit);
    }

    /**
     * @return a Future can be used to cancel the delayed task(timeout CF)
     * @see FutureCanceller
     */
    static ScheduledFuture<?> delayToTimeoutCf(CompletableFuture<?> cf, long delay, TimeUnit unit) {
        return delay(new CfTimeout(cf), delay, unit);
    }

    /**
     * @return a Future can be used to cancel the delayed task(complete CF)
     * @see FutureCanceller
     */
    static <T> ScheduledFuture<?> delayToCompleteCf(
            CompletableFuture<? super T> cf, @Nullable T value, long delay, TimeUnit unit) {
        return delay(new CfCompleter<>(cf, value), delay, unit);
    }

    /**
     * Checks whether execution is at the thread of CompletableFuture/Cffu delayer.
     * <p>
     * The constant {@code "CompletableFutureDelayScheduler"} is defined
     * at {@link CompletableFuture.Delayer.DaemonThreadFactory}.
     */
    @SuppressWarnings("JavadocReference")
    static boolean atCfDelayerThread() {
        final String name = Thread.currentThread().getName();
        return "CompletableFutureDelayScheduler".equals(name) || THREAD_NAME_OF_CFFU_DELAY_SCHEDULER.equals(name);
    }

    private static final String THREAD_NAME_OF_CFFU_DELAY_SCHEDULER = "CffuBuiltinDelayScheduler";

    /**
     * Holds {@link #delayer} scheduler as field of static inner class for lazy loading(init only when needed).
     * <p>
     * The lazy loading is need because {@link #atCfDelayerThread()} method of
     * class {@link Delayer} is used on {@code Java 9+}.
     */
    private static class DelayerHolder {
        static final ScheduledThreadPoolExecutor delayer;

        static {
            delayer = new ScheduledThreadPoolExecutor(1, new DaemonThreadFactory());
            delayer.setRemoveOnCancelPolicy(true);
        }
    }

    private static final class DaemonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName(THREAD_NAME_OF_CFFU_DELAY_SCHEDULER);
            return t;
        }
    }

    private Delayer() {
    }
}

/**
 * An executor wrapper with delayed execution.
 * <p>
 * code is copied from {@link CompletableFuture.DelayedExecutor} with small adoption.
 */
@SuppressWarnings("JavadocReference")
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
    public void execute(Runnable r) {
        Delayer.delay(new TaskSubmitter(executor, requireNonNull(r, "runnable is null")), delay, unit);
    }
}

////////////////////////////////////////////////////////////////////////////////
// Little classified lambdas to better support monitoring
////////////////////////////////////////////////////////////////////////////////

/**
 * Action to submit task(Runnable) to executor.
 * <p>
 * code is copied from {@link CompletableFuture.TaskSubmitter} with small adoption.
 */
@SuppressWarnings("JavadocReference")
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
 * Action to cf.completeExceptionally with TimeoutException.
 * <p>
 * code is copied from {@link CompletableFuture.Timeout} with small adoption.
 */
@SuppressWarnings("JavadocReference")
final class CfTimeout implements Runnable {
    private final CompletableFuture<?> cf;

    CfTimeout(CompletableFuture<?> cf) {
        this.cf = cf;
    }

    @Override
    public void run() {
        if (!cf.isDone())
            cf.completeExceptionally(new TimeoutException());
    }
}

/**
 * Action to complete cf.
 * <p>
 * code is copied from {@link CompletableFuture.DelayedCompleter} with small adoption.
 */
@SuppressWarnings("JavadocReference")
final class CfCompleter<T> implements Runnable {
    private final CompletableFuture<? super T> cf;
    @Nullable
    private final T value;

    CfCompleter(CompletableFuture<? super T> cf, @Nullable T value) {
        this.cf = cf;
        this.value = value;
    }

    @Override
    public void run() {
        cf.complete(value);
    }
}

/**
 * Action to cancel unneeded scheduled task by Future (for example timeouts).
 * <p>
 * code is copied from {@link CompletableFuture.Canceller} with small adoption.
 *
 * @see Delayer#delay(Runnable, long, TimeUnit)
 * @see Delayer#delayToTimeoutCf(CompletableFuture, long, TimeUnit)
 * @see Delayer#delayToCompleteCf(CompletableFuture, Object, long, TimeUnit)
 */
@SuppressWarnings("JavadocReference")
final class FutureCanceller implements BiConsumer<Object, Throwable> {
    @Nullable
    private final Future<?> f;

    FutureCanceller(Future<?> f) {
        this.f = requireNonNull(f);
    }

    /**
     * Note: Before Java 21(Java 20-), {@link CompletableFuture#orTimeout(long, TimeUnit)}
     * leaks if the future completes exceptionally, more info see
     * <a href="https://bugs.openjdk.org/browse/JDK-8303742">issue JDK-8303742</a>,
     * <a href="https://github.com/openjdk/jdk/pull/13059">PR review openjdk/jdk/13059</a>
     * and <a href="https://github.com/openjdk/jdk/commit/ded6a8131970ac2f7ae59716769e6f6bae3b809a">JDK bugfix commit</a>.
     */
    @Override
    public void accept(Object ignore, @Nullable Throwable ex) {
        if (f != null && !f.isDone())
            f.cancel(false);
    }
}

/**
 * code is copied from {@link CompletableFuture.AsyncSupply} with small adoption.
 */
@SuppressWarnings("JavadocReference")
@SuppressFBWarnings("SE_BAD_FIELD")
final class CfCompleterBySupplier<T> extends ForkJoinTask<Void>
        implements Runnable, CompletableFuture.AsynchronousCompletionTask {
    @Nullable
    private CompletableFuture<? super T> dep;
    @Nullable
    private Supplier<? extends T> fn;

    CfCompleterBySupplier(CompletableFuture<? super T> dep, Supplier<? extends T> fn) {
        this.dep = dep;
        this.fn = fn;
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    public void setRawResult(Void v) {
    }

    @Override
    public boolean exec() {
        run();
        return false;
    }

    @Override
    public void run() {
        CompletableFuture<? super T> d;
        Supplier<? extends T> f;
        if ((d = dep) != null && (f = fn) != null) {
            dep = null;
            fn = null;
            if (!d.isDone()) {
                try {
                    d.complete(f.get());
                } catch (Throwable ex) {
                    d.completeExceptionally(ex);
                }
            }
        }
    }
}

/**
 * code is copied from {@link CompletableFuture.AsyncSupply} with small adoption.
 */
@SuppressWarnings("JavadocReference")
@SuppressFBWarnings("SE_BAD_FIELD")
final class CfExCompleterBySupplier extends ForkJoinTask<Void>
        implements Runnable, CompletableFuture.AsynchronousCompletionTask {
    @Nullable
    private CompletableFuture<?> dep;
    @Nullable
    private Supplier<? extends Throwable> fn;

    CfExCompleterBySupplier(CompletableFuture<?> dep, Supplier<? extends Throwable> fn) {
        this.dep = dep;
        this.fn = fn;
    }

    @Override
    public Void getRawResult() {
        return null;
    }

    @Override
    public void setRawResult(Void v) {
    }

    @Override
    public boolean exec() {
        run();
        return false;
    }

    @Override
    public void run() {
        CompletableFuture<?> d;
        Supplier<? extends Throwable> f;
        if ((d = dep) != null && (f = fn) != null) {
            dep = null;
            fn = null;
            if (!d.isDone()) {
                try {
                    d.completeExceptionally(f.get());
                } catch (Throwable ex) {
                    d.completeExceptionally(ex);
                }
            }
        }
    }
}
