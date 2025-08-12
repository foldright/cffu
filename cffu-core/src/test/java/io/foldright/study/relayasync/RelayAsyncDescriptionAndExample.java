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
        // Default execution way
        // ================================================================================

        cf.thenApply(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // if input cf is COMPLETED when create a continuation stage (calling `thenApply`),
            //   executes the long time computation SYNCHRONOUSLY (aka. in the caller thread);
            //   this SYNCHRONIZED execution leads to BLOCKing sequential codes of caller... ⚠️
            // otherwise, triggers the computation when input cf completes.

            return s + s;
        });

        // ================================================================================
        // Asynchronous execution way(default executor or custom executor)
        // ================================================================================

        cf.thenApplyAsync(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // always executes via an executor(guarantees not to block sequential code of caller).
            //
            // if input cf is INCOMPLETE when create a continuation stage (calling `thenApplyAsync`),
            //   executes the computation via an executor when input cf completes,
            //   this execution via an executor to ONE MORE thread switching  ⚠️
            // otherwise, executes the computation via an executor immediately.

            return s + s;
        });

        // ================================================================================
        // How about the fourth way to arrange execution of a new stage's computations?
        // ================================================================================
        //
        // - if input cf is COMPLETED when create a continuation stage,
        //   use "asynchronous execution" way (via supplied Executor), won't block sequential code of caller ✅
        // - otherwise, use "default execution" way, save one thread switching ✅
        //
        // Let's call this way as "relay async".

        LLCF.relayAsync0(cf, f -> f.thenApply(s -> {
            // a simulating long-running computation...
            sleep(1000);
            // if input cf is COMPLETED when create a continuation stage (calling `thenApply`), executes via supplied executor
            // if input cf is INCOMPLETE when create a continuation stage (calling `thenApply`), use "default execution" way

            return s + s;
        }), ForkJoinPool.commonPool());
    }
}
