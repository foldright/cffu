package io.foldright.cffu;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static io.foldright.cffu.ExceptionReporter.reportException;
import static java.util.Objects.requireNonNull;


/**
 * Integration with guava {@link ListenableFuture}.
 */
public class ListenableFutureUtils {

    /*
     * Implementation Note:
     *
     * The methods of this class MUST NOT be defined in `CompletableFutureUtils`;
     * Otherwise `NoClassDefFoundError` when loading `CompletableFutureUtils`
     * if `ListenableFuture` class(`ClassNotFoundException` aka. `Guava` dependency) is absent.
     */

    /**
     * @param fn
     * @param executor
     * @param interruptLfWhenCancellationException
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U> CompletableFuture<U> thenApplyBasedInterruptableLf(
            CompletableFuture<T> cfThis, Function<? super T, ? extends U> fn,
            Executor executor, boolean interruptLfWhenCancellationException) {
        final CompletableFuture<U> ret = new CompletableFuture<U>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                if (mayInterruptIfRunning) return completeExceptionally(new InterruptionCancellationException());
                else return super.cancel(false);
            }

            @Override
            public String toString() {
                return "CompletableFutureAdapter@ListenableFutureUtils.thenApplyBasedInterruptableLf" + super.toString();
            }
        };
        final AtomicBoolean preemption = new AtomicBoolean();

        final CompletableFuture<ListenableFuture<U>> handleByLf = cfThis.handle((v, ex) -> {
            if (ex != null || !preemption.compareAndSet(false, true)) return null;

            final ListenableFuture<U> lf = Futures.submit(() -> fn.apply(v), executor);
            transferLfResultToCf(lf, executor, ret);
            return lf;
        }).exceptionally(ex -> reportException("Exception occurred in handle of thenApplyLf XXX", ex));

        ret.handle((v, ex) -> ex == null || preemption.compareAndSet(false, true) ? null : ex)
                .thenAcceptBoth(handleByLf, (outerEx, innerLf) -> {
                    if (outerEx != null && innerLf != null)
                        propagateCancellationToLf(outerEx, interruptLfWhenCancellationException, innerLf);
                });

        return ret;
    }

    private static <T> void transferLfResultToCf(ListenableFuture<T> lf, Executor executor, CompletableFuture<T> cf) {
        Futures.addCallback(lf, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                cf.complete(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                cf.completeExceptionally(ex);
            }
        }, executor);
    }

    private static <T> void propagateCancellationToLf(Throwable ex, boolean interruptLfWhenCancellationException, ListenableFuture<T> lf) {
        ex = CompletableFutureUtils.unwrapCfException(ex);
        if (ex instanceof InterruptionCancellationException) lf.cancel(true);
        else if (ex instanceof CancellationException) lf.cancel(interruptLfWhenCancellationException);
    }

    private static class InterruptionCancellationException extends CancellationException {
    }

    /**
     * Converts input {@link ListenableFuture} to {@link CompletableFuture}.
     * <p>
     * Callback from ListenableFuture is executed using the given executor,
     * use {@link MoreExecutors#directExecutor()} if you need skip executor switch.
     * <p>
     * Cancelling({@link Future#cancel(boolean)}) the returned CompletableFuture
     * will also cancel underlying ListenableFuture.
     * <p>
     * Use param {@code interruptLfWhenCancellationException} to control whether to cancel ListenableFuture with
     * interruption when CancellationException occurred (including CompletionException/ExecutionException with
     * CancellationException cause, more info see {@link CompletableFutureUtils#unwrapCfException(Throwable)}).
     * <p>
     * It's recommended to avoid using direct write methods(e.g. {@link CompletableFuture#complete(Object)},
     * {@link CompletableFuture#completeExceptionally(Throwable)}) of the returned CompletableFuture:
     * <ul>
     * <li>the underlying ListenableFuture can benefit from cancellation propagation.
     * <li>the writing of the returned CompletableFuture won't affect the underlying ListenableFuture.
     * </ul>
     *
     * @param lf                                   the underlying ListenableFuture
     * @param executor                             the executor to use for ListenableFuture callback execution
     * @param interruptLfWhenCancellationException whether to cancel ListenableFuture with interruption when CancellationException occurred
     * @return the CompletableFuture adapter
     * @see CompletableFuture#cancel(boolean)
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> toCompletableFuture(
            ListenableFuture<T> lf, Executor executor, boolean interruptLfWhenCancellationException) {
        requireNonNull(lf, "listenableFuture is null");
        requireNonNull(executor, "executor is null");

        CompletableFuture<T> ret = new CompletableFuture<T>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                // propagate cancellation from outer adapter to LF
                final boolean ret = lf.cancel(mayInterruptIfRunning);
                super.cancel(mayInterruptIfRunning);
                return ret;
            }

            @Override
            public String toString() {
                return "CompletableFutureAdapter@ListenableFutureUtils.toCompletableFuture" +
                        " of ListenableFuture(" + lf + "), " + super.toString();
            }
        };
        // propagate cancellation by CancellationException from outer adapter to LF
        CompletableFutureUtils.peek(
                ret, (v, ex) -> propagateCancellationToLf(ex, interruptLfWhenCancellationException, lf));

        transferLfResultToCf(lf, executor, ret);
        return ret;
    }

    /**
     * Converts input {@link ListenableFuture} to {@link Cffu}.
     * <p>
     * Callback from ListenableFuture is executed using cffuFactory's default executor.
     * <p>
     * More info see {@link #toCompletableFuture(ListenableFuture, Executor, boolean)}.
     */
    @Contract(pure = true)
    public static <T> Cffu<T> toCffu(
            ListenableFuture<T> lf, CffuFactory cffuFactory, boolean interruptLfWhenCancellationException) {
        return cffuFactory.toCffu(toCompletableFuture(lf, cffuFactory.defaultExecutor(), interruptLfWhenCancellationException));
    }

    /**
     * Converts input {@link CompletableFuture} to {@link ListenableFuture}.
     */
    @Contract(pure = true)
    public static <T> ListenableFuture<T> toListenableFuture(CompletableFuture<T> cf) {
        requireNonNull(cf, "cf is null");
        if (CompletableFutureUtils.isMinStageCf(cf)) throw new UnsupportedOperationException();

        return new ListenableFuture<T>() {
            @Override
            public void addListener(Runnable listener, Executor executor) {
                CompletableFutureUtils.peekAsync(cf, (v, ex) -> listener.run(), executor);
            }

            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return cf.cancel(mayInterruptIfRunning);
            }

            @Override
            public boolean isCancelled() {
                return cf.isCancelled();
            }

            @Override
            public boolean isDone() {
                return cf.isDone();
            }

            @Override
            public T get() throws InterruptedException, ExecutionException {
                return cf.get();
            }

            @Override
            public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return cf.get(timeout, unit);
            }

            @Override
            public String toString() {
                return "ListenableFutureAdapter@ListenableFutureUtils.toListenableFuture of " + cf;
            }
        };
    }

    /**
     * Converts input {@link Cffu} to {@link ListenableFuture}.
     */
    @Contract(pure = true)
    public static <T> ListenableFuture<T> toListenableFuture(Cffu<T> cf) {
        requireNonNull(cf, "cf is null");
        if (cf.isMinimalStage()) throw new UnsupportedOperationException();

        return toListenableFuture(cf.cffuUnwrap());
    }

    private ListenableFutureUtils() {
    }
}
