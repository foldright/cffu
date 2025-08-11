package io.foldright.demo;

import io.foldright.cffu2.CompletableFutureUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;


/**
 * @see CompletableFutureUtils#fromSyncCall(Callable)
 */
public class UniformExceptionHandlingDemo {
    public static void main(String[] args) throws Exception {
        System.out.println("towPathsExceptionHandling");
        towPathsExceptionHandling();

        System.out.println("\nUniformExceptionHandlingDemo");
        uniformExceptionHandling();

        Thread.sleep(1000);
    }

    private static void towPathsExceptionHandling() {
        try {
            final String result = mayExceptionalSyncProcess();

            final CompletableFuture<Integer> cf = CompletableFuture.supplyAsync(() -> result.length() * 2);
            cf.exceptionally(ex -> 42)
                    .thenAccept(System.out::println);
        } catch (Exception e) {
            System.out.println(42);
        }
    }

    private static void uniformExceptionHandling() {
        final CompletableFuture<String> syncCf = CompletableFutureUtils.fromSyncCall(
                UniformExceptionHandlingDemo::mayExceptionalSyncProcess);

        final CompletableFuture<Integer> cf = syncCf.thenApplyAsync(s -> s.length() * 2);
        cf.exceptionally(ex -> 42)
                .thenAccept(System.out::println);
    }

    private static String mayExceptionalSyncProcess() {
        return "mock logic that may throw exception";
    }
}
