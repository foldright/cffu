package io.foldright.demo.cffu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.foldright.cffu.CompletableFutureUtils.anySuccessOf;
import static io.foldright.cffu.CompletableFutureUtils.orTimeout;


public class CompletableFutureUtilsDemo {
    private static final ExecutorService myBizThreadPool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        final CompletableFuture<Integer> cf42 = CompletableFuture
                .supplyAsync(() -> 21, myBizThreadPool)  // Run in myBizThreadPool
                .thenApply(n -> n * 2);

        final CompletableFuture<Integer> longTaskA = cf42.thenApplyAsync(n -> {
            sleep(1001);
            return n / 2;
        }, myBizThreadPool);
        final CompletableFuture<Integer> longTaskB = cf42.thenApplyAsync(n -> {
            sleep(1002);
            return n / 2;
        }, myBizThreadPool);
        final CompletableFuture<Integer> longTaskC = cf42.thenApplyAsync(n -> {
            sleep(100);
            return n * 2;
        }, myBizThreadPool);
        final CompletableFuture<Integer> longFailedTask = cf42.thenApplyAsync(unused -> {
            sleep(1000);
            throw new RuntimeException("Bang!");
        }, myBizThreadPool);

        final CompletableFuture<Integer> combined = longTaskA.thenCombine(longTaskB, Integer::sum);
        final CompletableFuture<Integer> combinedWithTimeout =
                orTimeout(combined, 1500, TimeUnit.MILLISECONDS);
        System.out.println("combined result: " + combinedWithTimeout.get());

        final CompletableFuture<Integer> anySuccess = anySuccessOf(longTaskC, longFailedTask);
        System.out.println("any success result: " + anySuccess.get());

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizThreadPool.shutdown();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
