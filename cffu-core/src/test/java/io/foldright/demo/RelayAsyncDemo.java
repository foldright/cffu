package io.foldright.demo;

import io.foldright.cffu.LLCF;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static io.foldright.cffu.LLCF.completeCf0;
import static io.foldright.test_utils.TestUtils.sleep;
import static java.lang.Thread.currentThread;


public class RelayAsyncDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        sleep(100);

        relayAsyncV5(
                CompletableFuture.completedFuture(100),
                f -> f.thenApply(v -> {
                    System.out.printf("value %s in %s%n", v, currentThread());
                    return v * 2;
                }),
                myBizExecutor
        );

        relayAsyncV5(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return 200;
                }),
                f -> f.thenApply(v -> {
                    System.out.printf("value %s in %s%n", v, currentThread());
                    return v * 2;
                }),
                myBizExecutor
        );

        System.out.println("shutting down");
        sleep(1000);
        myBizExecutor.shutdown();
    }

    private static <T, U> CompletableFuture<U> relayAsyncV5(
            CompletionStage<T> cfThis,
            Function<CompletableFuture<T>, CompletionStage<U>> operations,
            Executor executor) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        final CompletionStage<U> ret = operations.apply(promise);

        final Thread callerThread = currentThread();
        final boolean[] finishAttach = {false};
        LLCF.peek0(cfThis, (v, ex) -> {
            if (!currentThread().equals(callerThread) || finishAttach[0]) completeCf0(promise, v, ex);
            else executor.execute(() -> completeCf0(promise, v, ex));
        }, "relayAsyncV5");
        finishAttach[0] = true;

        return LLCF.f_toCf0(ret);
    }

    private static <T, U> CompletableFuture<U> relayAsyncV4(
            CompletionStage<T> cfThis,
            Function<CompletableFuture<T>, CompletionStage<U>> operations,
            Executor executor) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        final CompletionStage<U> ret = operations.apply(promise);

        final Thread callerThread = currentThread();
        final AtomicBoolean finishAttach = new AtomicBoolean(false);
        LLCF.peek0(cfThis, (v, ex) -> {
            if (finishAttach.get() || !currentThread().equals(callerThread)) completeCf0(promise, v, ex);
            else executor.execute(() -> completeCf0(promise, v, ex));
        }, "relayAsyncV4");
        finishAttach.set(true);

        return LLCF.f_toCf0(ret);
    }

    private static <T, U> CompletableFuture<U> relayAsyncV3(
            CompletionStage<T> cfThis,
            Function<CompletableFuture<T>, CompletionStage<U>> operations,
            Executor executor) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        final CompletionStage<U> ret = operations.apply(promise);

        final AtomicBoolean finishAttach = new AtomicBoolean(false);
        LLCF.peek0(cfThis, (v, ex) -> {
            if (finishAttach.get()) completeCf0(promise, v, ex);
            else executor.execute(() -> completeCf0(promise, v, ex));
        }, "relayAsyncV3");
        finishAttach.set(true);

        return LLCF.f_toCf0(ret);
    }

    private static <T, U> CompletableFuture<U> relayAsyncV2(
            CompletionStage<T> cfThis,
            Function<CompletableFuture<T>, CompletionStage<U>> operations,
            Executor executor) {
        final Thread callerThread = currentThread();

        final CompletableFuture<T> promise = new CompletableFuture<>();
        final CompletionStage<U> ret = operations.apply(promise);
        LLCF.peek0(cfThis, (v, ex) -> {
            if (!currentThread().equals(callerThread)) completeCf0(promise, v, ex);
            else executor.execute(() -> completeCf0(promise, v, ex));
        }, "relayAsyncV2");

        return LLCF.f_toCf0(ret);
    }

    private static <T, U> CompletableFuture<U> relayAsyncV1(
            CompletionStage<T> cfThis,
            Function<CompletableFuture<T>, CompletionStage<U>> operations,
            Executor executor) {
        final Thread callerThread = currentThread();

        return cfThis.toCompletableFuture().handle((v, ex) -> {
            final CompletableFuture<T> f = new CompletableFuture<>();
            final CompletionStage<U> ret = operations.apply(f);

            if (!currentThread().equals(callerThread)) completeCf0(f, v, ex);
            else executor.execute(() -> completeCf0(f, v, ex));

            return ret;
        }).thenCompose(x -> x);
    }
}
