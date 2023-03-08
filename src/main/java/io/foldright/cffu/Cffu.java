package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.*;
import java.util.function.*;

import static io.foldright.cffu.CffuFactory.IS_JAVA12_PLUS;
import static io.foldright.cffu.CffuFactory.IS_JAVA9_PLUS;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class Cffu<T> implements Future<T>, CompletionStage<T> {
    @NonNull
    private final CffuFactory fac;
    @NonNull
    private final CompletableFuture<T> cf;

    Cffu(CffuFactory cffuFactory, CompletableFuture<T> cf) {
        this.fac = cffuFactory;
        this.cf = cf;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Simple `then*` methods of `CompletionStage`:
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
        return fac.new0(cf.thenRunAsync(action, fac.defaultExecutor));
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
        return fac.new0(cf.thenAcceptAsync(action, fac.defaultExecutor));
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
        return fac.new0(cf.thenApplyAsync(fn, fac.defaultExecutor));
    }

    @Override
    public <U> Cffu<U> thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
        return fac.new0(cf.thenApplyAsync(fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then both(binary input)` methods of CompletionStage:
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
        return fac.new0(cf.runAfterBothAsync(other, action, fac.defaultExecutor));
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
        return fac.new0(cf.thenAcceptBothAsync(other, action, fac.defaultExecutor));
    }

    @Override
    public <U> Cffu<Void> thenAcceptBothAsync(CompletionStage<? extends U> other,
                                              BiConsumer<? super T, ? super U> action,
                                              Executor executor) {
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
        return fac.new0(cf.thenCombineAsync(other, fn, fac.defaultExecutor));
    }

    @Override
    public <U, V> Cffu<V> thenCombineAsync(CompletionStage<? extends U> other,
                                           BiFunction<? super T, ? super U, ? extends V> fn,
                                           Executor executor) {
        return fac.new0(cf.thenCombineAsync(other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# `then either(binary input)` methods of CompletionStage:
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
        return fac.new0(cf.runAfterEitherAsync(other, action, fac.defaultExecutor));
    }

    @Override
    public Cffu<Void> runAfterEitherAsync(
            CompletionStage<?> other, Runnable action, Executor executor) {
        return fac.new0(cf.runAfterEitherAsync(other, action, executor));
    }

    @Override
    public Cffu<Void> acceptEither(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return fac.new0(cf.acceptEither(other, action));
    }

    @Override
    public Cffu<Void> acceptEitherAsync(
            CompletionStage<? extends T> other, Consumer<? super T> action) {
        return fac.new0(cf.acceptEitherAsync(other, action, fac.defaultExecutor));
    }

    @Override
    public Cffu<Void> acceptEitherAsync(CompletionStage<? extends T> other,
                                        Consumer<? super T> action,
                                        Executor executor) {
        return fac.new0(cf.acceptEitherAsync(other, action, executor));
    }

    @Override
    public <U> Cffu<U> applyToEither(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return fac.new0(cf.applyToEither(other, fn));
    }

    @Override
    public <U> Cffu<U> applyToEitherAsync(
            CompletionStage<? extends T> other, Function<? super T, U> fn) {
        return fac.new0(cf.applyToEitherAsync(other, fn, fac.defaultExecutor));
    }

    @Override
    public <U> Cffu<U> applyToEitherAsync(CompletionStage<? extends T> other,
                                          Function<? super T, U> fn,
                                          Executor executor) {
        return fac.new0(cf.applyToEitherAsync(other, fn, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Error Handling methods of CompletionStage:
    //
    //   exceptionally*
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public Cffu<T> exceptionally(Function<Throwable, ? extends T> fn) {
        return fac.new0(cf.exceptionally(fn));
    }

    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn) {
        return exceptionallyAsync(fn, fac.defaultExecutor);
    }

    @Override
    public Cffu<T> exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return fac.new0(cf.exceptionallyAsync(fn, executor));
        }

        return handleAsync((x, throwable) -> {
            if (throwable == null) return x;
            else return fn.apply(throwable);
        }, executor);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Timeout Control methods:
    //
    //   orTimeout
    //   completeOnTimeout
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
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> orTimeout(long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            cf.orTimeout(timeout, unit);
            return this;
        }

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
    @Contract(pure = true)
    @SuppressWarnings("ConstantValue")
    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    public Cffu<T> completeOnTimeout(@Nullable T value, long timeout, TimeUnit unit) {
        if (IS_JAVA9_PLUS) {
            cf.completeOnTimeout(value, timeout, unit);
            return this;
        }

        if (unit == null) throw new NullPointerException();
        if (!isDone()) {
            whenComplete(new Canceller(Delayer.delay(new DelayedCompleter<>(cf, value), timeout, unit)));
        }
        return this;
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Advanced methods of CompletionStage:
    //
    //   thenCompose*
    //   exceptionallyCompose*
    //   handle*
    //   whenComplete*
    //
    //   NOTE, advanced means:
    //      - `compose` methods, input function argument return CompletionStage
    //      - process succeed and failed result at one time(handle*/whenComplete*)
    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public <U> Cffu<U> thenCompose(Function<? super T, ? extends CompletionStage<U>> fn) {
        return fac.new0(cf.thenCompose(fn));
    }

    @Override
    public <U> Cffu<U> thenComposeAsync(Function<? super T, ? extends CompletionStage<U>> fn) {
        return fac.new0(cf.thenComposeAsync(fn, fac.defaultExecutor));
    }

    @Override
    public <U> Cffu<U> thenComposeAsync(
            Function<? super T, ? extends CompletionStage<U>> fn, Executor executor) {
        return fac.new0(cf.thenComposeAsync(fn, executor));
    }

    @Override
    public Cffu<T> exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn) {
        if (IS_JAVA12_PLUS) {
            return fac.new0(cf.exceptionallyCompose(fn));
        }

        return handle((r, ex) -> (ex == null)
                ? this
                : fn.apply(ex)).thenCompose(Function.identity()
        );
    }

    @Override
    public Cffu<T> exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn) {
        return exceptionallyComposeAsync(fn, fac.defaultExecutor);
    }

    @Override
    public Cffu<T> exceptionallyComposeAsync(
            Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor) {
        if (IS_JAVA12_PLUS) {
            return fac.new0(cf.exceptionallyComposeAsync(fn, executor));
        }

        return handle((r, ex) -> (ex == null)
                ? this
                : this.handleAsync((r1, ex1) -> fn.apply(ex1), executor).thenCompose(Function.identity())
        ).thenCompose(Function.identity());
    }

    @Override
    public <U> Cffu<U> handle(BiFunction<? super T, Throwable, ? extends U> fn) {
        return fac.new0(cf.handle(fn));
    }

    @Override
    public <U> Cffu<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> fn) {
        return fac.new0(cf.handleAsync(fn, fac.defaultExecutor));
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
        return fac.new0(cf.whenCompleteAsync(action, fac.defaultExecutor));
    }

    @Override
    public Cffu<T> whenCompleteAsync(
            BiConsumer<? super T, ? super Throwable> action, Executor executor) {
        return fac.new0(cf.whenCompleteAsync(action, executor));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Read(explicitly) methods
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

    @Contract(pure = true)
    @Override
    public State state() {
        return cf.state();
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Write methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    //# write(implicitly) methods
    ////////////////////////////////////////

    public Cffu<T> completeAsync(Supplier<? extends T> supplier) {
        return completeAsync(supplier, fac.defaultExecutor);
    }

    @SuppressWarnings("ConstantValue")
    public Cffu<T> completeAsync(Supplier<? extends T> supplier, Executor executor) {
        if (IS_JAVA9_PLUS) {
            cf.completeAsync(supplier, executor);
        } else {
            if (supplier == null || executor == null) throw new NullPointerException();
            executor.execute(new AsyncSupply<>(cf, supplier));
        }
        return this;
    }

    /**
     * code is copied from {@code CompletableFuture.AsyncSupply} with small adoption.
     */
    @SuppressWarnings({"NonSerializableFieldInSerializableClass", "serial"})
    @SuppressFBWarnings("SE_BAD_FIELD")
    private static final class AsyncSupply<T> extends ForkJoinTask<Void>
            implements Runnable, CompletableFuture.AsynchronousCompletionTask {
        CompletableFuture<T> dep;
        Supplier<? extends T> fn;

        AsyncSupply(CompletableFuture<T> dep, Supplier<? extends T> fn) {
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
            CompletableFuture<T> d;
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

    ////////////////////////////////////////
    //# write(explicitly) methods
    ////////////////////////////////////////

    public boolean complete(@Nullable T value) {
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
    @Contract(pure = true)
    @Override
    @SuppressFBWarnings("EI_EXPOSE_REP")
    public CompletableFuture<T> toCompletableFuture() {
        return cf;
    }

    @Contract(pure = true)
    public Cffu<T> copy() {
        if (IS_JAVA9_PLUS) {
            return fac.new0(cf.copy());
        }
        return thenApply(Function.identity());
    }

    public void obtrudeValue(@Nullable T value) {
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

    @Contract(pure = true)
    public Executor defaultExecutor() {
        return fac.defaultExecutor;
    }

    public int getNumberOfDependents() {
        return cf.getNumberOfDependents();
    }

    // FIXME add methods, need compatibility logic for java 9
    //      public CompletionStage<T> minimalCompletionStage() {

    @Contract(pure = true)
    public <U> Cffu<U> newIncompleteFuture() {
        if (IS_JAVA9_PLUS) {
            return fac.new0(cf.newIncompleteFuture());
        }
        return fac.new0(new CompletableFuture<>());
    }

    @Contract(pure = true)
    @Override
    public String toString() {
        return "Cffu: " + cf;
    }
}
