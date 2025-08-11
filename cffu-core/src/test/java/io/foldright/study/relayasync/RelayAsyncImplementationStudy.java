package io.foldright.study.relayasync;

import io.foldright.cffu2.LLCF;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import static io.foldright.cffu2.LLCF.completeCf0;
import static io.foldright.test_utils.TestUtils.sleep;
import static java.lang.Thread.currentThread;
import static org.junit.jupiter.api.Assertions.*;


/**
 * the implementation study of "relay async".
 */
@SuppressWarnings({"unused", "SameParameterValue", "UnusedReturnValue"})
public class RelayAsyncImplementationStudy {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        sleep(200);
        final Thread mainThread = currentThread();

        final CompletableFuture<Integer> completed = CompletableFuture.completedFuture(21);
        final CompletableFuture<Integer> relayed = relayAsyncV6(completed, f -> f.thenApply(v -> {
            System.out.printf("value %s in %s%n", v, currentThread());
            assertNotSame(mainThread, currentThread());
            return v * 2;
        }), myBizExecutor);
        assertEquals(21 * 2, relayed.join());

        final CompletableFuture<Thread> completeLater = CompletableFuture.supplyAsync(() -> {
            sleep(100);
            assertNotSame(mainThread, currentThread());
            return currentThread();
        });
        final CompletableFuture<Void> relayedOfCompleteLater = relayAsyncV6(completeLater, f -> f.thenAccept(v -> {
            System.out.printf("value %s in %s%n", v, currentThread());
            assertSame(v, currentThread());
            assertNotSame(mainThread, v);
        }), myBizExecutor);
        assertFalse(relayedOfCompleteLater.isDone());
        relayedOfCompleteLater.join();

        System.out.println("shutting down");
        myBizExecutor.shutdown();
    }

    private static <T, U> CompletableFuture<U> relayAsyncV6(
            CompletionStage<? extends T> cfThis,
            Function<CompletableFuture<T>, CompletableFuture<U>> operations,
            Executor executor) {
        final CompletableFuture<T> promise = new CompletableFuture<>();
        final CompletableFuture<U> ret = operations.apply(promise);

        final Thread callerThread = currentThread();
        final boolean[] finishAttach = {false};

        LLCF.peek0(cfThis, (v, ex) -> {
            if (!currentThread().equals(callerThread) || finishAttach[0]) completeCf0(promise, v, ex);
            else executor.execute(() -> completeCf0(promise, v, ex));
        }, "relayAsyncV6");

        finishAttach[0] = true;
        return ret;
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
