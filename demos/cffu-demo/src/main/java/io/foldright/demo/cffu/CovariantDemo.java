package io.foldright.demo.cffu;

import io.foldright.cffu.CompletableFutureUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class CovariantDemo {
    public static void main(String[] args) {
        useAllOf();
        useAnyOf();
    }

    public static void useAllOf() {
        CompletableFuture<List<Number>> cf1 = CompletableFutureUtils.allResultsOf(
                CompletableFuture.completedFuture(42),
                CompletableFuture.completedFuture(42.0)
        );
        cf1.thenAccept(System.out::println);

        CompletableFutureUtils.allResultsOf(
                        CompletableFuture.completedFuture(42),
                        CompletableFuture.completedFuture(42.0)
                )
                .thenApply(numbers -> "to string: " + numbers.toString())
                .thenAccept(System.out::println);

        CompletableFuture<List<Integer>> cf5 = CompletableFutureUtils.allResultsOf(
                CompletableFuture.completedFuture(42),
                CompletableFuture.completedFuture(42)
        );
        cf5.thenAccept(System.out::println);
    }

    public static void useAnyOf() {
        CompletableFuture<Number> cf1 = CompletableFutureUtils.anySuccessOf(
                CompletableFuture.completedFuture(42),
                CompletableFuture.completedFuture(42.0)
        ).thenApply(Number::byteValue);
        cf1.thenAccept(System.out::println);

        CompletableFuture<? extends Number> cf2 = CompletableFutureUtils.anySuccessOf(
                CompletableFuture.completedFuture(42),
                CompletableFuture.completedFuture(42.0)
        );
        cf2.thenAccept(System.out::println);

        CompletableFutureUtils.anySuccessOf(
                        CompletableFuture.completedFuture(42),
                        CompletableFuture.completedFuture(42.0)
                )
                .thenApply(Number::byteValue)
                .thenAccept(System.out::println);

        CompletableFuture<Integer> cf5 = CompletableFutureUtils.anySuccessOf(
                CompletableFuture.completedFuture(42),
                CompletableFuture.completedFuture(42)
        );
        cf5.thenAccept(System.out::println);
    }
}
