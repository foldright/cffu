package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.*;


public class Cffu<T> implements Future<T>, CompletionStage<T> {
    private final CffuFactory fac;

    private final CompletableFuture<T> cf;

    Cffu(CffuFactory cffuFactory, CompletableFuture<T> cf) {
        this.fac = cffuFactory;
        this.cf = cf;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods simple then* methods:
    //
    //   thenRun*(Runnable): Void -> Void
    //   thenAccept*(Consumer): T -> Void
    //   thenApply*(Function): T -> U
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public Cffu<Void> thenRun(Runnable action) {
        return fac.new0(cf.thenRun(action));
    }

    @Override
    public Cffu<Void> thenRunAsync(Runnable action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.thenRunAsync(action, fac.defaultExecutor));
        }
        return fac.new0(cf.thenRunAsync(action));
    }

    @Override
    public Cffu<Void> thenRunAsync(Runnable action, Executor executor) {
        return fac.new0(cf.thenRunAsync(action, executor));
    }

    @Override
    public Cffu<Void> thenAccept(Consumer<? super T> action) {
        return fac.new0(cf.thenAccept(action));
    }

    @Override
    public Cffu<Void> thenAcceptAsync(Consumer<? super T> action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.thenAcceptAsync(action, fac.defaultExecutor));
        }
        return fac.new0(cf.thenAcceptAsync(action));
    }

    @Override
    public Cffu<Void> thenAcceptAsync(Consumer<? super T> action, Executor executor) {
        return fac.new0(cf.thenAcceptAsync(action, executor));
    }

    @Override
    public <U> Cffu<U> thenApply(Function<? super T, ? extends U> fn) {
        return fac.new0(cf.thenApply(fn));
    }

    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.thenApplyAsync(fn, fac.defaultExecutor));
        }
        return fac.new0(cf.thenApplyAsync(fn));
    }

    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return fac.new0(cf.thenApplyAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods `then both(binary input)` methods:
    //
    //   runAfterBoth*(Runnable): Void, Void -> Void
    //   thenAcceptBoth*(BiConsumer): T1, T2 -> Void
    //   thenCombine*(BiFunction): T1, T2 -> U
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public Cffu<Void> runAfterBoth(CompletionStage<?> other, Runnable action) {
        return fac.new0(cf.runAfterBoth(other, action));
    }

    @Override
    public Cffu<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.runAfterBothAsync(other, action, fac.defaultExecutor));
        }
        return fac.new0(cf.runAfterBothAsync(other, action));
    }

    @Override
    public Cffu<Void> runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor) {
        return fac.new0(cf.runAfterBothAsync(other, action, executor));
    }

    @Override
    public <U> Cffu<Void> thenAcceptBoth(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return fac.new0(cf.thenAcceptBoth(other, action));
    }

    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.thenAcceptBothAsync(other, action, fac.defaultExecutor));
        }
        return fac.new0(cf.thenAcceptBothAsync(other, action));
    }

    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor) {
        return fac.new0(cf.thenAcceptBothAsync(other, action, executor));
    }

    @Override
    public <U, V> Cffu<V> thenCombine(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        return fac.new0(cf.thenCombine(other, fn));
    }

    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.thenCombineAsync(other, fn, fac.defaultExecutor));
        }
        return fac.new0(cf.thenCombineAsync(other, fn));
    }

    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor) {
        return fac.new0(cf.thenCombineAsync(other, fn, fac.defaultExecutor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods `then either(binary input)` methods:
    //
    //   runAfterEither*(Runnable): Void, Void -> Void
    //   acceptEither*(BiConsumer): T1, T2 -> Void
    //   applyToEither*(BiFunction): T1, T2 -> U
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public Cffu<Void> runAfterEither(CompletionStage<?> other, Runnable action) {
        return fac.new0(cf.runAfterEither(other, action));
    }

    @Override
    public Cffu<Void> runAfterEitherAsync(CompletionStage<?> other, Runnable action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.runAfterEitherAsync(other, action, fac.defaultExecutor));
        }
        return fac.new0(cf.runAfterEitherAsync(other, action));
    }

    @Override
    public Cffu<Void> runAfterEitherAsync(
            CompletionStage<?> other, Runnable action, Executor executor) {
        return fac.new0(cf.runAfterEitherAsync(other, action, executor));
    }

    @Override
    public Cffu<Void> acceptEither(CompletionStage<? extends T> other, Consumer<? super T> action) {
        return fac.new0(cf.acceptEither(other, action));
    }

    @Override
    public Cffu<Void> acceptEitherAsync(CompletionStage<? extends T> other, Consumer<? super
            T> action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.acceptEitherAsync(other, action, fac.defaultExecutor));
        }
        return fac.new0(cf.acceptEitherAsync(other, action));
    }

    @Override
    public Cffu<Void> acceptEitherAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action, Executor executor) {
        return fac.new0(cf.acceptEitherAsync(other, action, executor));
    }

    @Override
    public <U> Cffu<U> applyToEither(CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return fac.new0(cf.applyToEither(other, fn));
    }

    @Override
    public <U> Cffu<U> applyToEitherAsync(CompletionStage<? extends T> other, Function<? super
            T, U> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.applyToEitherAsync(other, fn, fac.defaultExecutor));
        }
        return fac.new0(cf.applyToEitherAsync(other, fn));
    }

    @Override
    public <U> Cffu<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn, Executor executor) {
        return fac.new0(cf.applyToEitherAsync(other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods advanced methods:
    //
    //   thenCompose*
    //   handle*
    //   whenComplete*
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public <U> Cffu<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return fac.new0(cf.thenCompose(fn));
    }

    @Override
    public <U> Cffu<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.thenComposeAsync(fn, fac.defaultExecutor));
        }
        return fac.new0(cf.thenComposeAsync(fn));
    }

    @Override
    public <U> Cffu<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return fac.new0(cf.thenComposeAsync(fn, executor));
    }

    @Override
    public <U> Cffu<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return null;
    }

    @Override
    public <U> Cffu<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.handleAsync(fn, fac.defaultExecutor));
        }
        return fac.new0(cf.handleAsync(fn));
    }

    @Override
    public <U> Cffu<U> handleAsync(
            BiFunction<? super T, Throwable, ? extends U> fn, Executor executor) {
        return fac.new0(cf.handleAsync(fn, executor));
    }

    @Override
    public Cffu<T> whenComplete(BiConsumer<? super T, ? super Throwable> action) {
        return fac.new0(cf.whenComplete(action));
    }

    @Override
    public Cffu<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> action) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.whenCompleteAsync(action, fac.defaultExecutor));
        }
        return fac.new0(cf.whenCompleteAsync(action));
    }

    @Override
    public Cffu<T> whenCompleteAsync(
            BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return fac.new0(cf.whenCompleteAsync(action, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# timeout control
    ////////////////////////////////////////////////////////////////////////////////

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
            whenComplete(new CffuFactory.Canceller(
                    CffuFactory.Delayer.delay(new CffuFactory.Timeout(cf), timeout, unit)
            ));
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
            whenComplete(new CffuFactory.Canceller(
                    CffuFactory.Delayer.delay(new CffuFactory.DelayedCompleter<T>(cf, value), timeout, unit)
            ));
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# read(explicitly) methods
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return cf.get();
    }

    @Override
    public T get(long timeout, @NotNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return cf.get(timeout, unit);
    }

    public T join() {
        return cf.join();
    }

    public T getNow(T valueIfAbsent) {
        return cf.getNow(valueIfAbsent);
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
    //# write methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    //# write(implicitly) methods
    ////////////////////////////////////////

    public Cffu<T> completeAsync(Supplier<? extends T> supplier) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.completeAsync(supplier, fac.defaultExecutor));
        }
        return fac.new0(cf.completeAsync(supplier));
    }

    public Cffu<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
        return fac.new0(cf.completeAsync(supplier, executor));
    }

    @Override
    public Cffu<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return fac.new0(cf.exceptionally(fn));
    }

    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.exceptionallyAsync(fn, fac.defaultExecutor));
        }
        return fac.new0(cf.exceptionallyAsync(fn));
    }

    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) {
        return fac.new0(cf.exceptionallyAsync(fn, executor));
    }

    @Override
    public Cffu<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return fac.new0(cf.exceptionallyCompose(fn));
    }

    @Override
    public Cffu<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
        if (fac.defaultExecutor != null) {
            return fac.new0(cf.exceptionallyComposeAsync(fn, fac.defaultExecutor));
        }

        return fac.new0(cf.exceptionallyComposeAsync(fn));
    }

    @Override
    public Cffu<T> exceptionallyComposeAsync(
            Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        return fac.new0(cf.exceptionallyComposeAsync(fn, executor));
    }

    ////////////////////////////////////////
    //# write(explicitly) methods
    ////////////////////////////////////////

    public boolean complete(T value) {
        return cf.complete(value);
    }

    public boolean completeExceptionally(Throwable ex) {
        return cf.completeExceptionally(ex);
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return cf.cancel(mayInterruptIfRunning);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //   vs. user functional API
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns underneath wrapped CompletableFuture.
     *
     * @return underneath wrapped CompletableFuture
     */
    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> toCompletableFuture() {
        return cf;
    }

    public Cffu<T> copy() {
        return fac.new0(cf.copy());
    }

    public void obtrudeValue(T value) {
        if (fac.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeValue is forbidden by cffu");
        }
        cf.obtrudeValue(value);
    }

    public void obtrudeException(Throwable ex) {
        if (fac.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeException is forbidden by cffu");
        }
        cf.obtrudeException(ex);
    }

    public Executor defaultExecutor() {
        if (fac.defaultExecutor == null) return cf.defaultExecutor();
        else return fac.defaultExecutor;
    }

    public int getNumberOfDependents() {
        return cf.getNumberOfDependents();
    }

    // FIXME public CompletionStage<T> minimalCompletionStage() {

    public <U> Cffu<U> newIncompleteFuture() {
        return fac.new0(new CompletableFuture<>());
    }

    @Override
    public String toString() {
        return "Cffu: " + cf.toString();
    }
}
