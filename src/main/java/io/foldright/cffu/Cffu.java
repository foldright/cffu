package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;
import java.util.function.*;


public final class Cffu<T> implements Future<T>, CompletionStage<T> {
    @NonNull
    private final CffuFactory fac;
    @NonNull
    private final CompletableFuture<T> cf;

    Cffu(@NonNull CffuFactory cffuFactory, @NonNull CompletableFuture<T> cf) {
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

    @NonNull
    @Override
    public Cffu<Void> thenRun(@NonNull Runnable action) {
        return fac.new0(cf.thenRun(action));
    }

    @NonNull
    @Override
    public Cffu<Void> thenRunAsync(@NonNull Runnable action) {
        return fac.new0(cf.thenRunAsync(action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<Void> thenRunAsync(@NonNull Runnable action, @NonNull Executor executor) {
        return fac.new0(cf.thenRunAsync(action, executor));
    }

    @NonNull
    @Override
    public Cffu<Void> thenAccept(@NonNull Consumer<? super T> action) {
        return fac.new0(cf.thenAccept(action));
    }

    @NonNull
    @Override
    public Cffu<Void> thenAcceptAsync(@NonNull Consumer<? super T> action) {
        return fac.new0(cf.thenAcceptAsync(action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<Void> thenAcceptAsync(@NonNull Consumer<? super T> action, @NonNull Executor executor) {
        return fac.new0(cf.thenAcceptAsync(action, executor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> thenApply(@NonNull Function<? super T, ? extends U> fn) {
        return fac.new0(cf.thenApply(fn));
    }

    @Override
    public <U> Cffu<U> thenApplyAsync(@NonNull Function<? super T, ? extends U> fn) {
        return fac.new0(cf.thenApplyAsync(fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> thenApplyAsync(@NonNull Function<? super T, ? extends U> fn, @NonNull Executor executor) {
        return fac.new0(cf.thenApplyAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods `then both(binary input)` methods:
    //
    //   runAfterBoth*(Runnable): Void, Void -> Void
    //   thenAcceptBoth*(BiConsumer): T1, T2 -> Void
    //   thenCombine*(BiFunction): T1, T2 -> U
    ////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public Cffu<Void> runAfterBoth(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return fac.new0(cf.runAfterBoth(other, action));
    }

    @NonNull
    @Override
    public Cffu<Void> runAfterBothAsync(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return fac.new0(cf.runAfterBothAsync(other, action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<Void> runAfterBothAsync(
            @NonNull CompletionStage<?> other, @NonNull Runnable action, @NonNull Executor executor) {
        return fac.new0(cf.runAfterBothAsync(other, action, executor));
    }

    @NonNull
    @Override
    public <U> Cffu<Void> thenAcceptBoth(
            @NonNull CompletionStage<? extends U> other, @NonNull BiConsumer<? super T, ? super U> action) {
        return fac.new0(cf.thenAcceptBoth(other, action));
    }

    @NonNull
    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(
            CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action) {
        return fac.new0(cf.thenAcceptBothAsync(other, action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(@NonNull CompletionStage<? extends U> other,
                                              @NonNull BiConsumer<? super T, ? super U> action,
                                              @NonNull Executor executor) {
        return fac.new0(cf.thenAcceptBothAsync(other, action, executor));
    }

    @NonNull
    @Override
    public <U, V> Cffu<V> thenCombine(
            @NonNull CompletionStage<? extends U> other, @NonNull BiFunction<? super T, ? super U, ? extends V> fn) {
        return fac.new0(cf.thenCombine(other, fn));
    }

    @NonNull
    @Override
    public <U, V> Cffu<V> thenCombineAsync(
            @NonNull CompletionStage<? extends U> other, @NonNull BiFunction<? super T, ? super U, ? extends V> fn) {
        return fac.new0(cf.thenCombineAsync(other, fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public <U, V> Cffu<V> thenCombineAsync(@NonNull CompletionStage<? extends U> other,
                                           @NonNull BiFunction<? super T, ? super U, ? extends V> fn,
                                           @NonNull Executor executor) {
        return fac.new0(cf.thenCombineAsync(other, fn, fac.defaultExecutor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods `then either(binary input)` methods:
    //
    //   runAfterEither*(Runnable): Void, Void -> Void
    //   acceptEither*(BiConsumer): T1, T2 -> Void
    //   applyToEither*(BiFunction): T1, T2 -> U
    ////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public Cffu<Void> runAfterEither(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return fac.new0(cf.runAfterEither(other, action));
    }

    @NonNull
    @Override
    public Cffu<Void> runAfterEitherAsync(@NonNull CompletionStage<?> other, @NonNull Runnable action) {
        return fac.new0(cf.runAfterEitherAsync(other, action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<Void> runAfterEitherAsync(
            @NonNull CompletionStage<?> other, @NonNull Runnable action, @NonNull Executor executor) {
        return fac.new0(cf.runAfterEitherAsync(other, action, executor));
    }

    @NonNull
    @Override
    public Cffu<Void> acceptEither(
            @NonNull CompletionStage<? extends T> other, @NonNull Consumer<? super T> action) {
        return fac.new0(cf.acceptEither(other, action));
    }

    @NonNull
    @Override
    public Cffu<Void> acceptEitherAsync(
            @NonNull CompletionStage<? extends T> other, @NonNull Consumer<? super T> action) {
        return fac.new0(cf.acceptEitherAsync(other, action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<Void> acceptEitherAsync(@NonNull CompletionStage<? extends T> other,
                                        @NonNull Consumer<? super T> action,
                                        @NonNull Executor executor) {
        return fac.new0(cf.acceptEitherAsync(other, action, executor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> applyToEither(
            @NonNull CompletionStage<? extends T> other, @NonNull Function<? super T, U> fn) {
        return fac.new0(cf.applyToEither(other, fn));
    }

    @NonNull
    @Override
    public <U> Cffu<U> applyToEitherAsync(
            @NonNull CompletionStage<? extends T> other, @NonNull Function<? super T, U> fn) {
        return fac.new0(cf.applyToEitherAsync(other, fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> applyToEitherAsync(@NonNull CompletionStage<? extends T> other,
                                          @NonNull Function<? super T, U> fn,
                                          @NonNull Executor executor) {
        return fac.new0(cf.applyToEitherAsync(other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# CompletionStage methods advanced methods:
    //
    //   thenCompose*
    //   handle*
    //   whenComplete*
    ////////////////////////////////////////////////////////////////////////////////

    @NonNull
    @Override
    public <U> Cffu<U> thenCompose(@NonNull Function<? super T, ? extends CompletionStage<U>> fn) {
        return fac.new0(cf.thenCompose(fn));
    }

    @NonNull
    @Override
    public <U> Cffu<U> thenComposeAsync(@NonNull Function<? super T, ? extends CompletionStage<U>> fn) {
        return fac.new0(cf.thenComposeAsync(fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> thenComposeAsync(
            @NonNull Function<? super T, ? extends CompletionStage<U>> fn, @NonNull Executor executor) {
        return fac.new0(cf.thenComposeAsync(fn, executor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> handle(@NonNull BiFunction<? super T, Throwable, ? extends U> fn) {
        return fac.new0(cf.handle(fn));
    }

    @NonNull
    @Override
    public <U> Cffu<U> handleAsync(@NonNull BiFunction<? super T, Throwable, ? extends U> fn) {
        return fac.new0(cf.handleAsync(fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public <U> Cffu<U> handleAsync(
            @NonNull BiFunction<? super T, Throwable, ? extends U> fn, @NonNull Executor executor) {
        return fac.new0(cf.handleAsync(fn, executor));
    }

    @NonNull
    @Override
    public Cffu<T> whenComplete(@NonNull BiConsumer<? super T, ? super Throwable> action) {
        return fac.new0(cf.whenComplete(action));
    }

    @NonNull
    @Override
    public Cffu<T> whenCompleteAsync(@NonNull BiConsumer<? super T, ? super Throwable> action) {
        return fac.new0(cf.whenCompleteAsync(action, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<T> whenCompleteAsync(
            @NonNull BiConsumer<? super T, ? super Throwable> action, @NonNull Executor executor) {
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
     */
    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> timeout(long timeout, @NonNull TimeUnit unit) {
        if (unit == null) throw new NullPointerException();
        if (!cf.isDone()) {
            whenComplete(new Canceller(Delayer.delay(new Timeout(cf), timeout, unit)));
        }
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
     */
    @NonNull
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> completeOnTimeout(T value, long timeout, @NonNull TimeUnit unit) {
        if (unit == null) throw new NullPointerException();
        if (!isDone()) {
            whenComplete(new Canceller(Delayer.delay(new DelayedCompleter<>(cf, value), timeout, unit)));
        }
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# read(explicitly) methods
    ////////////////////////////////////////////////////////////////////////////////

    @Nullable
    @Override
    public T get() throws InterruptedException, ExecutionException {
        return cf.get();
    }

    @Nullable
    @Override
    public T get(long timeout, @NotNull TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return cf.get(timeout, unit);
    }

    @Nullable
    public T join() {
        return cf.join();
    }

    @Nullable
    public T getNow(T valueIfAbsent) {
        return cf.getNow(valueIfAbsent);
    }

    @Nullable
    @Override
    public T resultNow() {
        return cf.resultNow();
    }

    @NonNull
    @Override
    public Throwable exceptionNow() {
        return cf.exceptionNow();
    }

    @Contract(pure = true)
    @Override
    public boolean isDone() {
        return cf.isDone();
    }

    @Contract(pure = true)
    public boolean isCompletedExceptionally() {
        return cf.isCompletedExceptionally();
    }

    @Contract(pure = true)
    @Override
    public boolean isCancelled() {
        return cf.isCancelled();
    }

    @NonNull
    @Contract(pure = true)
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

    @NonNull
    public Cffu<T> completeAsync(@NonNull Supplier<? extends T> supplier) {
        return fac.new0(cf.completeAsync(supplier, fac.defaultExecutor));
    }

    @NonNull
    public Cffu<T> completeAsync(@NonNull Supplier<? extends T> supplier, @NonNull Executor executor) {
        return fac.new0(cf.completeAsync(supplier, executor));
    }

    @NonNull
    @Override
    public Cffu<T> exceptionally(@NonNull Function<Throwable, ? extends T> fn) {
        return fac.new0(cf.exceptionally(fn));
    }

    @NonNull
    @Override
    public Cffu<T> exceptionallyAsync(@NonNull Function<Throwable, ? extends T> fn) {
        return fac.new0(cf.exceptionallyAsync(fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<T> exceptionallyAsync(@NonNull Function<Throwable, ? extends T> fn, @NonNull Executor executor) {
        return fac.new0(cf.exceptionallyAsync(fn, executor));
    }

    @NonNull
    @Override
    public Cffu<T> exceptionallyCompose(@NonNull Function<Throwable, ? extends CompletionStage<T>> fn) {
        return fac.new0(cf.exceptionallyCompose(fn));
    }

    @NonNull
    @Override
    public Cffu<T> exceptionallyComposeAsync(@NonNull Function<Throwable, ? extends CompletionStage<T>> fn) {
        return fac.new0(cf.exceptionallyComposeAsync(fn, fac.defaultExecutor));
    }

    @NonNull
    @Override
    public Cffu<T> exceptionallyComposeAsync(
            @NonNull Function<Throwable, ? extends CompletionStage<T>> fn, @NonNull Executor executor) {
        return fac.new0(cf.exceptionallyComposeAsync(fn, executor));
    }

    ////////////////////////////////////////
    //# write(explicitly) methods
    ////////////////////////////////////////

    public boolean complete(@Nullable T value) {
        return cf.complete(value);
    }

    public boolean completeExceptionally(@NonNull Throwable ex) {
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
    @NonNull
    @Contract(pure = true)
    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> toCompletableFuture() {
        return cf;
    }

    @NonNull
    @Contract(pure = true)
    public Cffu<T> copy() {
        return fac.new0(cf.copy());
    }

    public void obtrudeValue(@Nullable T value) {
        if (fac.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeValue is forbidden by cffu");
        }
        cf.obtrudeValue(value);
    }

    public void obtrudeException(@NonNull Throwable ex) {
        if (fac.forbidObtrudeMethods) {
            throw new UnsupportedOperationException("obtrudeException is forbidden by cffu");
        }
        cf.obtrudeException(ex);
    }

    @NonNull
    @Contract(pure = true)
    public Executor defaultExecutor() {
        return fac.defaultExecutor;
    }

    public int getNumberOfDependents() {
        return cf.getNumberOfDependents();
    }

    // FIXME public CompletionStage<T> minimalCompletionStage() {

    @NonNull
    @Contract(pure = true)
    public <U> Cffu<U> newIncompleteFuture() {
        return fac.new0(new CompletableFuture<>());
    }

    @NonNull
    @Contract(pure = true)
    @Override
    public String toString() {
        return "Cffu: " + cf;
    }
}
