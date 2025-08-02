package io.foldright.study.relayasync;

import io.foldright.cffu.LLCF;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static io.foldright.test_utils.TestUtils.sleep;


/**
 * Execution of a new stage's computations may be arranged in any of three ways:
 * - default execution,
 * - default asynchronous execution (using methods with suffix async that employ the stage's default asynchronous execution facility),
 * - or custom asynchronous execution (via a supplied Executor).
 * <p>
 * The information above is referenced from {@link java.util.concurrent.CompletionStage}.
 */
public class RelayAsyncDescriptionExample {
    void actionExecutionToACompletableFuture(CompletableFuture<String> cf) {
        // Default execution of CompletableFuture
        cf.thenApply(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // if input cf is COMPLETED, do this long time computation in SYNCHRONIZED, aka. in the caller thread;
            // BLOCKS sequential code of caller...👎
            return s + s;
        });

        // Asynchronous execution of CompletableFuture(default executor or custom executor)
        cf.thenApplyAsync(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // always executes in an executor(guarantees not to block sequential code of caller).
            // if input cf is INCOMPLETE, computation in an executor leads to ONE MORE UNNECESSARY thread switch. 👎
            return s + s;
        });

        // How about the fourth way to arrange to computation of a new stage's computations?
        //
        // - if input cf is COMPLETED, asynchronous execution in an executor,
        //      won't block sequential code of caller 👍
        // - otherwise, use default execution, save one unnecessary thread switch 👍
        //
        // We call this way as "relay async":
        LLCF.relayAsync0(cf, f -> f.thenApply(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // if input cf is COMPLETED, executes in an executor
            // if input cf is INCOMPLETE, default execution
            return s + s;
        }), ForkJoinPool.commonPool());
    }
}
