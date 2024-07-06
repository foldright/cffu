package io.foldright.demo.cffu;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.foldright.cffu.CompletableFutureUtils.allResultsOf;
import static io.foldright.cffu.CompletableFutureUtils.anySuccessOf;
import static java.util.concurrent.CompletableFuture.completedFuture;


public class CovariantDemo {
    public static void main(String[] args) {
        useAllOf();
        useAnyOf();
    }

    public static void useAllOf() {
        CompletableFuture<List<Number>> cf1 = allResultsOf(
                completedFuture(42),
                completedFuture(42.0)
        );
        cf1.thenAccept(System.out::println);

        allResultsOf(
                completedFuture(42),
                completedFuture(42.0)
        )
                .thenApply(numbers -> "to string: " + numbers.toString())
                .thenAccept(System.out::println);

        CompletableFuture<List<Integer>> cf5 = allResultsOf(
                completedFuture(42),
                completedFuture(42)
        );
        cf5.thenAccept(System.out::println);
    }

    public static void useAnyOf() {
        CompletableFuture<Number> cf1 = anySuccessOf(
                completedFuture(42),
                completedFuture(42.0)
        ).thenApply(Number::byteValue);
        cf1.thenAccept(System.out::println);

        CompletableFuture<? extends Number> cf2 = anySuccessOf(
                completedFuture(42),
                completedFuture(42.0)
        );
        cf2.thenAccept(System.out::println);

        anySuccessOf(completedFuture(42), completedFuture(42.0))
                .thenApply(Number::byteValue)
                .thenAccept(System.out::println);

        CompletableFuture<Integer> cf5 = anySuccessOf(
                completedFuture(42),
                completedFuture(42)
        );
        cf5.thenAccept(System.out::println);
    }
}
