package io.foldright.cffu;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.*;

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
     * Converts input {@link ListenableFuture} to {@link CompletableFuture}.
     * <p>
     * Callback from ListenableFuture is executed using the given executor,
     * use {{@link MoreExecutors#directExecutor()}} if you need skip executor switch.
     */
    @Contract(pure = true)
    public static <T> CompletableFuture<T> toCompletableFuture(ListenableFuture<T> lf, Executor executor) {
        requireNonNull(lf, "listenableFuture is null");

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
        ret.whenComplete((result, throwable) -> {
            if (ret.isCancelled()) {
                lf.cancel(false);
            } else {
                Throwable cause = CompletableFutureUtils.unwrapCfException(throwable);
                if (cause instanceof CancellationException) {
                    lf.cancel(false);
                }
            }
        });

        Futures.addCallback(lf, new FutureCallback<T>() {
            @Override
            public void onSuccess(T result) {
                ret.complete(result);
            }

            @Override
            public void onFailure(Throwable ex) {
                ret.completeExceptionally(ex);
            }
        }, executor);
        return ret;
    }

    /**
     * Converts input {@link ListenableFuture} to {@link Cffu}.
     * <p>
     * Callback from ListenableFuture is executed using cffuFactory's default executor.
     */
    @Contract(pure = true)
    public static <T> Cffu<T> toCffu(ListenableFuture<T> lf, CffuFactory cffuFactory) {
        return cffuFactory.toCffu(toCompletableFuture(lf, cffuFactory.defaultExecutor()));
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
