package io.foldright.cffu;

import java.util.function.Consumer;


/**
     * Source of values. Let's say we are converting from RxJava Single to CompletableFuture. In such case Single is
     * value source (the original object) and the library registers CompletableFuture (target object) to listen on Singles
     * events. When someone calls cancel on the CompletableFuture, we want to unsubscribe from the Single, that's why we do
     * have cancel method here.
     */
    public interface ValueSource<T> {
        /**
         * Used to notify target object about changes in the original object.
         */
        void addCallbacks(Consumer<T> successCallback, Consumer<Throwable> failureCallback);

        /**
         * Cancels execution of the original object if cancel is called on the target object
         */
        boolean cancel(boolean mayInterruptIfRunning);
    }
