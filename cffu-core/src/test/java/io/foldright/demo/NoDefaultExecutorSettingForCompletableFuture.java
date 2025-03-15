package io.foldright.demo;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NoDefaultExecutorSettingForCompletableFuture {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) {
        CompletableFuture<Void> cf1 = CompletableFuture.runAsync(
                () -> System.out.println("doing a long time work!"),
                myBizExecutor);

        CompletableFuture<Void> cf2 = CompletableFuture
                .supplyAsync(
                        () -> {
                            System.out.println("doing another long time work!");
                            return 42;
                        },
                        myBizExecutor)
                .thenAcceptAsync(
                        i -> System.out.println("doing third long time work!"),
                        myBizExecutor);

        CompletableFuture.allOf(cf1, cf2).join();

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
