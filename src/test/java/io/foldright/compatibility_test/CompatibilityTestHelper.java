package io.foldright.compatibility_test;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactoryBuilder;

import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.ForkJoinPool.commonPool;


class CompatibilityTestHelper {
    public static <T> CompletableFuture<T> newFailedCompletableFuture(Throwable t) {
        CompletableFuture<T> failed = new CompletableFuture<>();
        failed.completeExceptionally(t);
        return failed;
    }

    public static <T> Cffu<T> newFailedCffu(Throwable t) {
        return CffuFactoryBuilder
                .newCffuFactoryBuilder(commonPool())
                .build()
                .failedFuture(t);
    }
}
