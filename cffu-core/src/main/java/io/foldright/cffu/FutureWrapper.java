package io.foldright.cffu;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class FutureWrapper<T> implements Future<T> {
    private final Future<T> wrappedFuture;

    protected FutureWrapper(Future<T> wrappedFuture) {
        if (wrappedFuture == null) {
            throw new NullPointerException("Wrapped future can not be null");
        }
        this.wrappedFuture = wrappedFuture;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return wrappedFuture.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return wrappedFuture.isCancelled();
    }

    @Override
    public boolean isDone() {
        return wrappedFuture.isDone();
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return wrappedFuture.get();
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return wrappedFuture.get(timeout, unit);
    }

    protected Future<T> getWrappedFuture() {
        return wrappedFuture;
    }
}
