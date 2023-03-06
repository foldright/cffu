package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.*;


public class Cffu<T> implements Future<T>, CompletionStage<T> {
    private final CffuFactory factory;

    private final CompletableFuture<T> cf;

    Cffu(CffuFactory cffuFactory, CompletableFuture<T> cf) {
        this.factory = cffuFactory;
        this.cf = cf;
    }

    /**
     * Returns wrapped CompletableFuture.
     *
     * @return wrapped CompletableFuture
     */
    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> toCompletableFuture() {
        return cf;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // read explicitly methods
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return cf.get();
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return cf.get(timeout, unit);
    }

    public T getNow(T valueIfAbsent) {
        return cf.getNow(valueIfAbsent);
    }

    public T join() {
        return cf.join();
    }

    @Override
    public T resultNow() {
        return cf.resultNow();
    }

    @Override
    public Throwable exceptionNow() {
        return cf.exceptionNow();
    }

    @Override
    public boolean isDone() {
        return cf.isDone();
    }

    public boolean isCompletedExceptionally() {
        return cf.isCompletedExceptionally();
    }

    @Override
    public boolean isCancelled() {
        return cf.isCancelled();
    }

    @Override
    public State state() {
        return cf.state();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // write explicitly methods
    ////////////////////////////////////////////////////////////////////////////////

    public boolean complete(T value) {
        return cf.complete(value);
    }

    public Cffu<T> completeAsync(Supplier<? extends T> supplier) {
        return factory.toCffu(cf.completeAsync(supplier));
    }

    public Cffu<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
        return factory.toCffu(cf.completeAsync(supplier, executor));
    }

    public boolean completeExceptionally(Throwable ex) {
        return cf.completeExceptionally(ex);
    }

    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) {
        return factory.toCffu(cf.exceptionallyAsync(fn));
    }

    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) {
        return factory.toCffu(cf.exceptionallyAsync(fn, executor));
    }

    @Override
    public Cffu<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return factory.toCffu(cf.exceptionallyCompose(fn));
    }

    @Override
    public Cffu<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return factory.toCffu(cf.exceptionallyComposeAsync(fn));
    }

    @Override
    public Cffu<T> exceptionallyComposeAsync(
            Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        return factory.toCffu(cf.exceptionallyComposeAsync(fn, executor));
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return cf.cancel(mayInterruptIfRunning);
    }

    ////////////////////////////////////////
    // nonfunctional methods
    //   vs. user functional API
    ////////////////////////////////////////

    public void obtrudeValue(T value) {
        if (factory.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeValue is forbidden by cffu");
        }
        cf.obtrudeValue(value);
    }

    public void obtrudeException(Throwable ex) {
        if (factory.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeException is forbidden by cffu");
        }
        cf.obtrudeException(ex);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // backport codes from CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    // Executors
    ////////////////////////////////////////

    private static final boolean USE_COMMON_POOL =
            (ForkJoinPool.getCommonPoolParallelism() > 1);

    /**
     * Default executor -- ForkJoinPool.commonPool() unless it cannot
     * support parallelism.
     */
    private static final Executor ASYNC_POOL = USE_COMMON_POOL ?
            ForkJoinPool.commonPool() : new ThreadPerTaskExecutor();

    /**
     * Fallback if ForkJoinPool.commonPool() cannot support parallelism
     */
    private static final class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable r) {
            Objects.requireNonNull(r);
            new Thread(r).start();
        }
    }

    /**
     * Null-checks user executor argument, and translates uses of
     * commonPool to ASYNC_POOL in case parallelism disabled.
     */
    static Executor screenExecutor(Executor e) {
        if (!USE_COMMON_POOL && e == ForkJoinPool.commonPool())
            return ASYNC_POOL;
        if (e == null) throw new NullPointerException();
        return e;
    }

    ////////////////////////////////////////
    // timeout control
    ////////////////////////////////////////

    /**
     * Exceptionally completes this CompletableFuture with
     * a {@link TimeoutException} if not otherwise completed
     * before the given timeout.
     *
     * @param timeout how long to wait before completing exceptionally
     *                with a TimeoutException, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the
     *                {@code timeout} parameter
     * @return this CompletableFuture
     * @since 9
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> timeout(long timeout, TimeUnit unit) {
        if (unit == null)
            throw new NullPointerException();
        if (!cf.isDone())
            whenComplete(new Canceller(Delayer.delay(new Timeout(cf),
                    timeout, unit)));
        return this;
    }

    /**
     * Completes this CompletableFuture with the given value if not
     * otherwise completed before the given timeout.
     *
     * @param value   the value to use upon timeout
     * @param timeout how long to wait before completing normally
     *                with the given value, in units of {@code unit}
     * @param unit    a {@code TimeUnit} determining how to interpret the
     *                {@code timeout} parameter
     * @return this CompletableFuture
     * @since 9
     */
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> completeOnTimeout(T value, long timeout, TimeUnit unit) {
        if (unit == null)
            throw new NullPointerException();
        if (!isDone())
            whenComplete(new Canceller(Delayer.delay(
                    new DelayedCompleter<T>(cf, value),
                    timeout, unit)));
        return this;
    }

    ////////////////////////////////////////
    // delay execution
    ////////////////////////////////////////

    /**
     * Returns a new Executor that submits a task to the given base
     * executor after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's
     * {@code execute} method.
     *
     * @param delay    how long to delay, in units of {@code unit}
     * @param unit     a {@code TimeUnit} determining how to interpret the
     *                 {@code delay} parameter
     * @param executor the base executor
     * @return the new delayed executor
     * @since 9
     */
    public static Executor delayedExecutor(long delay, TimeUnit unit,
                                           Executor executor) {
        if (unit == null || executor == null)
            throw new NullPointerException();
        return new DelayedExecutor(delay, unit, executor);
    }

    /**
     * Returns a new Executor that submits a task to the default
     * executor after the given delay (or no delay if non-positive).
     * Each delay commences upon invocation of the returned executor's
     * {@code execute} method.
     *
     * @param delay how long to delay, in units of {@code unit}
     * @param unit  a {@code TimeUnit} determining how to interpret the
     *              {@code delay} parameter
     * @return the new delayed executor
     * @since 9
     */
    public static Executor delayedExecutor(long delay, TimeUnit unit) {
        if (unit == null)
            throw new NullPointerException();
        return new DelayedExecutor(delay, unit, ASYNC_POOL);
    }


    /**
     * Singleton delay scheduler, used only for starting and
     * cancelling tasks.
     */
    private static final class Delayer {
        static ScheduledFuture<?> delay(Runnable command, long delay,
                                        TimeUnit unit) {
            return delayer.schedule(command, delay, unit);
        }

        static final class DaemonThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("CompletableFutureDelayScheduler");
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

    private static final class DelayedExecutor implements Executor {
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
    private static final class TaskSubmitter implements Runnable {
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
    private static final class Timeout implements Runnable {
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
    private static final class DelayedCompleter<U> implements Runnable {
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
    private static final class Canceller implements BiConsumer<Object, Throwable> {
        final Future<?> f;

        Canceller(Future<?> f) {
            this.f = f;
        }

        public void accept(Object ignore, Throwable ex) {
            if (ex == null && f != null && !f.isDone())
                f.cancel(false);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // overridden methods of CF
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public Cffu<Void> thenRunAsync(Runnable action) {
        if (factory.defaultExecutor != null) {
            return convert(cf.thenRunAsync(action, factory.defaultExecutor));
        }
        return convert(cf.thenRunAsync(action));
    }

    @Override
    public Cffu<Void> thenRunAsync(Runnable action, Executor executor) {
        return convert(cf.thenRunAsync(action, executor));
    }

    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        final CompletableFuture<U> u;
        if (factory.defaultExecutor != null) u = cf.thenApplyAsync(fn, factory.defaultExecutor);
        else u = cf.thenApplyAsync(fn);

        return convert(u);
    }

    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return convert(cf.thenApplyAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Constructor and implementation methods, for internal usage
    ////////////////////////////////////////////////////////////////////////////////

    private <U> Cffu<U> convert(CompletableFuture<U> cf) {
        return new Cffu<>(factory, cf);
    }

    public Executor defaultExecutor() {
        if (factory.defaultExecutor == null) return cf.defaultExecutor();
        else return factory.defaultExecutor;
    }

    ////////////////////////////////////////////////////////////////////////////////
    // TBD
    ////////////////////////////////////////////////////////////////////////////////


    @Override
    public <U> CompletionStage<U> thenApply(Function<? super T, ? extends U> fn) {
        return null;
    }

    @Override
    public CompletionStage<Void> thenAccept(Consumer<? super T> action) {
        return null;
    }

    @Override
    public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action) {
        return null;
    }

    @Override
    public CompletionStage<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return null;
    }

    @Override
    public CompletionStage<Void> thenRun(Runnable action) {
        return null;
    }

    @Override
    public <U, V> CompletionStage<V> thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ?
            super U, ? extends V> fn) {
        return null;
    }

    @Override
    public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends
            U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return null;
    }

    @Override
    public <U, V> CompletionStage<V> thenCombineAsync(CompletionStage<? extends
            U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return null;
    }

    @Override
    public <U> CompletionStage<Void> thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super
            T, ? super U> action) {
        return null;
    }

    @Override
    public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends
            U> other, BiConsumer<? super T, ? super U> action) {
        return null;
    }

    @Override
    public <U> CompletionStage<Void> thenAcceptBothAsync(CompletionStage<? extends
            U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return null;
    }

    @Override
    public CompletionStage<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return null;
    }

    @Override
    public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        return null;
    }

    @Override
    public CompletionStage<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> applyToEither(CompletionStage<? extends T> other, Function<? super
            T, U> fn) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super
            T, U> fn) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super
            T, U> fn, Executor executor) {
        return null;
    }

    @Override
    public CompletionStage<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return null;
    }

    @Override
    public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super
            T> action) {
        return null;
    }

    @Override
    public CompletionStage<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super
            T> action, Executor executor) {
        return null;
    }

    @Override
    public CompletionStage<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return null;
    }

    @Override
    public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        return null;
    }

    @Override
    public CompletionStage<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action, Executor
            executor) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>>
                                                           fn, Executor executor) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return null;
    }

    @Override
    public <U> CompletionStage<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn, Executor
            executor) {
        return null;
    }

    @Override
    public CompletionStage<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return null;
    }

    @Override
    public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        return null;
    }

    @Override
    public CompletionStage<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action, Executor
            executor) {
        return null;
    }

    @Override
    public CompletionStage<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return null;
    }
}
