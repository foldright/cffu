package io.foldright.study.generic;

import io.foldright.cffu2.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;


public class GenericStudy {
    /**
     * @see io.foldright.cffu2.CompletableFutureUtils#exceptionallyAsync(CompletionStage, Function)
     */
    private static <T, F extends CompletionStage<? extends T /* covariant here is BUG 🐞 */>>
    F exceptionallyAsync(F cfThis, Function<Throwable, ? extends T> fn) {
        return null;
    }

    private void show() {
        CompletionStage<? extends Number> stage1 = CompletableFuture.completedFuture(1);
        CompletionStage<? extends Number> stage2 = exceptionallyAsync(
                stage1, ex -> "set a string to a Number stage1!");
    }


    private void s2() {
        CompletionStage<? super Number> s = CompletableFuture.completedFuture(1);
        final CompletionStage<? super Number> completionStage = s.thenApply(x -> x);

        s.exceptionally(ex -> 42);
        CompletableFutureUtils.exceptionallyAsync(s, ex -> 42);

        s.thenAccept(System.out::println);
    }
}
