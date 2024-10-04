package io.foldright.demo;

import io.foldright.cffu.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;

import static io.foldright.test_utils.TestUtils.sleep;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * This shows the usage of Multiply Actions methods(<b>M</b> Methods).
 * <p>
 * Run by maven: {@code
 * mvn -pl cffu-core test-compile exec:exec -Dexec.mainClass=io.foldright.demo.MultiplyActionsDemo
 * }
 */
public class MultiplyActionsDemo {
    public static void main(String[] args) {
        mRunAsyncDemo();
        thenMApplyAsyncDemo();
    }

    static void mRunAsyncDemo() {
        // MUST wrap tasks to CompletableFuture first, AWKWARD! 😖
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> System.out.println("task1")),
                CompletableFuture.runAsync(() -> System.out.println("task2")),
                CompletableFuture.runAsync(() -> System.out.println("task3"))
        );

        // just run multiply actions, fresh and cool 😋
        CompletableFutureUtils.mRunAsync(
                () -> System.out.println("task1"),
                () -> System.out.println("task2"),
                () -> System.out.println("task3")
        );

        sleep(1000);
    }

    static void thenMApplyAsyncDemo() {
        // MUST wrap tasks to CompletableFuture first, AWKWARD! 😖
        completedFuture(42).thenCompose(v ->
                CompletableFutureUtils.allResultsFastFailOf(
                        CompletableFuture.supplyAsync(() -> v + 1),
                        CompletableFuture.supplyAsync(() -> v + 2),
                        CompletableFuture.supplyAsync(() -> v + 3)
                )
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]

        // just run multiply actions, fresh and cool 😋
        CompletableFutureUtils.thenMApplyFastFailAsync(
                completedFuture(42),
                v -> v + 1,
                v -> v + 2,
                v -> v + 3
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]

        sleep(1000);
    }
}
