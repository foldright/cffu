package io.foldright.cffu.eh;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.Cffu;
import io.foldright.cffu.LLCF;
import io.foldright.cffu.internal.CommonUtils;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.foldright.cffu.CompletableFutureUtils.unwrapCfException;
import static io.foldright.cffu.internal.ExceptionLogger.logException;
import static io.foldright.cffu.internal.ExceptionLogger.logUncaughtException;


/**
 * Utilities to handle exceptions from <strong>MULTIPLE</strong> {@link java.util.concurrent.CompletionStage CompletionStage}s
 * (including {@link java.util.concurrent.CompletableFuture CompletableFuture}s and {@link Cffu Cffu}s).
 * <p>
 * Exception handling for SINGLE {@code CompletionStage} is already provided by existing functionality, such as
 * {@link CompletionStage#exceptionally}, {@link Cffu#catching} and {@link CompletionStage#handle}.
 * <p>
 * These utilities are designed for simple tasks like reporting and logging exceptions.
 * Do NOT use these utilities for business logic or complex exception handling - for those cases,
 * use the exception handling methods mentioned above for single {@code CompletionStage}s instead.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see ExceptionHandler
 * @see ExceptionInfo
 */
public final class ExHandleOfMultiplyCfsUtils {

    /**
     * Handles all exceptions from multiple input {@code CompletionStage}s.
     *
     * @param where            the location where the exception occurs
     * @param exceptionHandler the exception handler
     */
    public static void handleAllExceptions(String where, ExceptionHandler exceptionHandler, CompletionStage<?>[] inputs) {
        // argument ex of method `exceptionally` is never null
        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            inputs[idx].exceptionally(ex -> safeHandle(new ExceptionInfo(where, idx, ex, null), exceptionHandler));
        }
    }

    /**
     * Handles exceptions from multiple input {@code CompletionStage}s that are swallowed by the output {@code CompletionStage}.
     *
     * @param where            the location where the exception occurs
     * @param exceptionHandler the exception handler
     */
    public static void handleSwallowedExceptions(
            String where, ExceptionHandler exceptionHandler, CompletionStage<?>[] inputs, CompletableFuture<?> output) {
        // uses unreferenced cfs to prevent memory leaks, in case that
        // some inputs complete quickly and retain large memory while other inputs or output continue running
        CompletionStage<Void>[] unreferencedInputs = unreferenced(inputs);

        // Whether to swallow exceptions from inputs depends on the output's result,
        // so must check when the output CompletionStage completes.
        output.whenComplete((v, outputEx) -> { // outputEx may be null
            Throwable outputBizEx = unwrapCfException(outputEx);
            for (int i = 0; i < unreferencedInputs.length; i++) {
                final int idx = i;
                CompletionStage<Void> cf = unreferencedInputs[i];
                // argument ex of method `exceptionally` is never null
                cf.exceptionally(ex -> {
                    // if ex is returned to output cf(aka. not swallowed ex), do NOTHING
                    if (unwrapCfException(ex) == outputBizEx) return null;
                    return safeHandle(new ExceptionInfo(where, idx, ex, null), exceptionHandler);
                });
            }
        });
    }

    /**
     * Returns an exception handler that logs exceptions from CompletionStages using the cffu logger.
     */
    public static ExceptionHandler cffuExHandler() {
        return CFFU_EX_HANDLER;
    }

    private static final ExceptionHandler CFFU_EX_HANDLER = exInfo -> {
        String msg = "Swallowed exception of cf" + (exInfo.index + 1) + " at " + exInfo.where;
        logException(msg, exInfo.ex);
    };

    /**
     * Creates new CompletionStages that only capture exception results from the input CompletionStages,
     * avoiding references to the original stages to allow them to be garbage collected ASAP.
     */
    private static CompletionStage<Void>[] unreferenced(CompletionStage<?>[] css) {
        return CommonUtils.mapArray(css, CompletionStage[]::new, s -> {
            CompletableFuture<Void> ret = new CompletableFuture<>();
            LLCF.peek0(s, (v, ex) -> LLCF.completeCf0(ret, null, ex), "unreferenced");
            return ret;
        });
    }

    @Nullable
    @Contract("_, _ -> null")
    @SuppressWarnings("SameReturnValue")
    private static <T> T safeHandle(ExceptionInfo exceptionInfo, ExceptionHandler exceptionHandler) {
        try {
            exceptionHandler.accept(exceptionInfo);
        } catch (Throwable e) {
            logUncaughtException("exceptionHandler(" + exceptionHandler.getClass() + ")", e);
        }
        return null;
    }

    private ExHandleOfMultiplyCfsUtils() {
    }
}
