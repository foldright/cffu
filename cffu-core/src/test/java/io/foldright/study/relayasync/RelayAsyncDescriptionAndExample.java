package io.foldright.study.relayasync;

import io.foldright.cffu2.LLCF;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ForkJoinPool;

import static io.foldright.test_utils.TestUtils.sleep;


/**
 * the description and example codes for {@link LLCF#relayAsync0 the "relay async" way}
 * <blockquote>
 * For {@link CompletionStage} (including subclass {@link CompletableFuture}), execution of
 * a new stage's computations may be arranged in any of three ways:
 * <ul>
 * <li>"default execution",
 * <li>default "asynchronous execution" (using methods with suffix async
 * that employ the stage's default asynchronous execution facility),
 * <li>or custom "asynchronous execution" (via a supplied Executor).
 * </ul>
 * <cite>— the javadoc of {@link CompletionStage}</cite>
 * </blockquote>
 *
 * @see LLCF#relayAsync0
 * @see CompletionStage
 */
@SuppressWarnings("unused")
public class RelayAsyncDescriptionAndExample {
    static void executeComputationsOfNewStage(CompletableFuture<String> cf) {

        // ================================================================================
        // Default execution
        // ================================================================================

        cf.thenApply(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // if input cf is COMPLETED when computations execute,
            // executes the long time computation SYNCHRONOUSLY (aka. in the caller thread);
            // this SYNCHRONIZED execution leads to BLOCKing sequential codes of caller... ⚠️

            return s + s;
        });

        // ================================================================================
        // Asynchronous execution of CompletableFuture(default executor or custom executor)
        // ================================================================================

        cf.thenApplyAsync(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // always executes via an executor(guarantees not to block sequential code of caller).
            // if input cf is INCOMPLETE when computations execute,
            // the execution via an executor leads to ONE MORE thread switching. ⚠️

            return s + s;
        });

        // ================================================================================
        // How about the fourth way to arrange execution of a new stage's computations?
        // ================================================================================
        //
        // - if input cf is COMPLETED when computations execute, use "asynchronous execution" (via supplied Executor),
        //   won't block sequential code of caller ✅
        // - otherwise, use "default execution", save one thread switching ✅
        //
        // Let's call this way as "relay async".

        LLCF.relayAsync0(cf, f -> f.thenApply(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // if input cf is COMPLETED, executes via supplied executor
            // if input cf is INCOMPLETE, use "default execution"

            return s + s;
        }), ForkJoinPool.commonPool());
    }
}
