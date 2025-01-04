package io.foldright.cffu.eh;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.Cffu;
import io.foldright.cffu.LLCF;
import io.foldright.cffu.internal.CommonUtils;
import io.foldright.cffu.internal.ExceptionLogger;
import org.jetbrains.annotations.Contract;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.foldright.cffu.CompletableFutureUtils.unwrapCfException;
import static io.foldright.cffu.internal.ExceptionLogger.logException;
import static io.foldright.cffu.internal.ExceptionLogger.logUncaughtException;


/**
 * Utilities to handle swallowed exceptions from <strong>MULTIPLE</strong> {@link CompletionStage}s
 * (including {@link CompletableFuture}s and {@link Cffu}s).
 * <p>
 * These utilities are designed for noncritical tasks like reporting and logging exceptions. For business logic
 * exception handling, use the standard exception handling methods for SINGLE {@code CompletionStage}s instead: {@link
 * CompletionStage#exceptionally exceptionally}, {@link Cffu#catching catching}, or {@link CompletionStage#handle handle}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 */
public final class SwallowedExceptionHandleUtils {
    /**
     * Handles all exceptions from multiple input {@code CompletionStage}s as swallowed exceptions,
     * using {@link #cffuSwallowedExceptionHandler()}.
     *
     * @param where the location where the exception occurs
     */
    public static void handleAllSwallowedExceptions(String where, CompletionStage<?>... inputs) {
        handleAllSwallowedExceptions(where, cffuSwallowedExceptionHandler(), inputs);
    }

    /**
     * Handles all exceptions from multiple input {@code CompletionStage}s as swallowed exceptions.
     *
     * @param where            the location where the exception occurs
     * @param exceptionHandler the exception handler
     */
    public static void handleAllSwallowedExceptions(
            String where, ExceptionHandler exceptionHandler, CompletionStage<?>... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            // argument ex of method `exceptionally` is never null
            inputs[idx].exceptionally(ex -> safeHandle(new ExceptionInfo(where, idx, ex, null), exceptionHandler));
        }
    }

    /**
     * Handles swallowed exceptions from multiple input {@code CompletionStage}s that are discarded (not propagated)
     * by the output {@code CompletionStage}, using {@link #cffuSwallowedExceptionHandler()}.
     *
     * @param where the location where the exception occurs
     */
    public static void handleSwallowedExceptions(
            String where, CompletableFuture<?> output, CompletionStage<?>... inputs) {
        handleSwallowedExceptions(where, cffuSwallowedExceptionHandler(), output, inputs);
    }

    /**
     * Handles swallowed exceptions from multiple input {@code CompletionStage}s
     * that are discarded (not propagated) by the output {@code CompletionStage}.
     *
     * @param where            the location where the exception occurs
     * @param exceptionHandler the exception handler
     */
    public static void handleSwallowedExceptions(
            String where, ExceptionHandler exceptionHandler, CompletableFuture<?> output, CompletionStage<?>... inputs) {
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
     * Returns an exception handler that logs swallowed exceptions from CompletionStages
     * at warning level using the cffu logger.
     */
    public static ExceptionHandler cffuSwallowedExceptionHandler() {
        return CFFU_SWALLOWED_EX_HANDLER;
    }

    private static final ExceptionHandler CFFU_SWALLOWED_EX_HANDLER = exInfo -> {
        String msg = "Swallowed exception of cf" + (exInfo.index + 1) + " at " + exInfo.where;
        logException(ExceptionLogger.Level.WARN, msg, exInfo.exception);
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
            exceptionHandler.handle(exceptionInfo);
        } catch (Throwable e) {
            logUncaughtException(ExceptionLogger.Level.ERROR, "exceptionHandler(" + exceptionHandler.getClass() + ")", e);
        }
        return null;
    }

    private SwallowedExceptionHandleUtils() {
    }
}
