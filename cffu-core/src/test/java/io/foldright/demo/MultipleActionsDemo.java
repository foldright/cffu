package io.foldright.demo;

import io.foldright.cffu2.CfTupleUtils;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.test_utils.TestUtils.sleep;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * This shows the usage of Multiple Actions methods(<b>M</b> Methods).
 * <p>
 * Run by maven: {@code
 * mvn -pl cffu-core test-compile exec:exec -Dexec.mainClass=io.foldright.demo.MultipleActionsDemo
 * }
 */
public class MultipleActionsDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) {
        mRunAsyncDemo();
        thenMApplyAsyncDemo();

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }

    static void mRunAsyncDemo() {
        // wrap actions to CompletableFutures first, AWKWARD! 😖
        CompletableFuture.allOf(
                CompletableFuture.runAsync(() -> System.out.println("task1")),
                CompletableFuture.runAsync(() -> System.out.println("task2")),
                CompletableFuture.runAsync(() -> System.out.println("task3"))
        );
        completedFuture("task").thenCompose(v ->
                CompletableFuture.allOf(
                        CompletableFuture.runAsync(() -> System.out.println(v + "1")),
                        CompletableFuture.runAsync(() -> System.out.println(v + "2")),
                        CompletableFuture.runAsync(() -> System.out.println(v + "3"))
                )
        );

        // just run multiple actions, fresh and cool 😋
        CompletableFutureUtils.mRunAsync(
                () -> System.out.println("task1"),
                () -> System.out.println("task2"),
                () -> System.out.println("task3")
        );
        cffuFactory.completedFuture("task").thenMAcceptAsync(
                (String v) -> System.out.println(v + "1"),
                v -> System.out.println(v + "2"),
                v -> System.out.println(v + "3")
        );

        sleep(1000);
    }

    static void thenMApplyAsyncDemo() {
        // wrap actions to CompletableFutures first, AWKWARD! 😖
        completedFuture(42).thenCompose(v ->
                CompletableFutureUtils.allResultsFailFastOf(
                        CompletableFuture.supplyAsync(() -> v + 1),
                        CompletableFuture.supplyAsync(() -> v + 2),
                        CompletableFuture.supplyAsync(() -> v + 3)
                )
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]
        cffuFactory.completedFuture(42).thenCompose(v ->
                CompletableFutureUtils.allSuccessResultsOf(
                        -1,
                        CompletableFuture.supplyAsync(() -> v + 1),
                        CompletableFuture.supplyAsync(() -> v + 2),
                        CompletableFuture.supplyAsync(() -> v + 3)
                )
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]

        // just run multiple actions, fresh and cool 😋
        CompletableFutureUtils.thenMApplyFailFastAsync(
                completedFuture(42),
                v -> v + 1,
                v -> v + 2,
                v -> v + 3
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]
        cffuFactory.completedFuture(42).thenMApplyAllSuccessAsync(
                -1,
                v -> v + 1,
                v -> v + 2,
                v -> v + 3
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]

        CfTupleUtils.thenMApplyTupleFailFastAsync(
                completedFuture(42),
                v -> "string" + v,
                v -> v + 1,
                v -> v + 2.1
        ).thenAccept(System.out::println);
        // output: Tuple3(string42, 43, 44.1)
        cffuFactory.completedFuture(42).tupleOps().thenMApplyAllSuccessTupleAsync(
                v -> "string" + v,
                v -> v + 1,
                v -> v + 2.1
        ).thenAccept(System.out::println);
        // output: Tuple3(string42, 43, 44.1)

        sleep(1000);
    }
}
