package io.foldright.cffu.eh;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.Cffu;
import io.foldright.cffu.internal.CommonUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.foldright.cffu.CompletableFutureUtils.unwrapCfException;
import static io.foldright.cffu.LLCF.*;
import static io.foldright.cffu.internal.CommonUtils.requireArrayAndEleNonNull;
import static io.foldright.cffu.internal.ExceptionLogger.Level.ERROR;
import static io.foldright.cffu.internal.ExceptionLogger.Level.WARN;
import static io.foldright.cffu.internal.ExceptionLogger.logException;
import static io.foldright.cffu.internal.ExceptionLogger.logUncaughtException;
import static java.util.Objects.requireNonNull;


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
     * using {@link #cffuSwallowedExceptionHandler()} and calling back it with null attachment.
     *
     * @param where the location where the exception occurs
     */
    public static void handleAllSwallowedExceptions(String where, CompletionStage<?>... inputs) {
        handleAllSwallowedExceptions(where, cffuSwallowedExceptionHandler(), inputs);
    }

    /**
     * Handles all exceptions from multiple input {@code CompletionStage}s as swallowed exceptions,
     * calling back the exceptionHandler with null attachment.
     *
     * @param where            the location where the exception occurs
     * @param exceptionHandler the exception handler
     */
    public static void handleAllSwallowedExceptions(
            String where, ExceptionHandler exceptionHandler, CompletionStage<?>... inputs) {
        handleAllSwallowedExceptions(where, null, exceptionHandler, inputs);
    }

    /**
     * Handles all exceptions from multiple input {@code CompletionStage}s as swallowed exceptions.
     *
     * @param where            the location where the exception occurs
     * @param attachments      the attachment objects
     * @param exceptionHandler the exception handler
     */
    public static void handleAllSwallowedExceptions(
            String where, @Nullable Object[] attachments, ExceptionHandler exceptionHandler, CompletionStage<?>... inputs) {
        requireNonNull(where, "where is null");
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        requireArrayAndEleNonNull("input", inputs);

        for (int i = 0; i < inputs.length; i++) {
            final int idx = i;
            peek0(inputs[i], (v, ex) -> {
                if (ex == null) return;
                safeHandle(new ExceptionInfo(where, idx, ex, safeGet(attachments, idx)), exceptionHandler);
            }, "handleAllSwallowedExceptions");
        }
    }

    /**
     * Handles swallowed exceptions from multiple input {@code CompletionStage}s that are discarded (not propagated)
     * by the output {@code CompletionStage}, using {@link #cffuSwallowedExceptionHandler()} and calling back it with null attachment.
     *
     * @param where the location where the exception occurs
     */
    public static void handleSwallowedExceptions(
            String where, CompletableFuture<?> output, CompletionStage<?>... inputs) {
        handleSwallowedExceptions(where, cffuSwallowedExceptionHandler(), output, inputs);
    }

    /**
     * Handles swallowed exceptions from multiple input {@code CompletionStage}s that are discarded (not propagated)
     * by the output {@code CompletionStage}, calling back the exceptionHandler with null attachment.
     *
     * @param where            the location where the exception occurs
     * @param exceptionHandler the exception handler
     */
    public static void handleSwallowedExceptions(
            String where, ExceptionHandler exceptionHandler, CompletableFuture<?> output, CompletionStage<?>... inputs) {
        handleSwallowedExceptions(where, null, exceptionHandler, output, inputs);
    }

    /**
     * Handles swallowed exceptions from multiple input {@code CompletionStage}s
     * that are discarded (not propagated) by the output {@code CompletionStage}.
     *
     * @param where            the location where the exception occurs
     * @param attachments      the attachment objects
     * @param exceptionHandler the exception handler
     */
    public static void handleSwallowedExceptions(
            String where, @Nullable Object[] attachments, ExceptionHandler exceptionHandler,
            CompletableFuture<?> output, CompletionStage<?>... inputs) {
        requireNonNull(where, "where is null");
        requireNonNull(exceptionHandler, "exceptionHandler is null");
        requireNonNull(output, "output is null");
        requireArrayAndEleNonNull("input", inputs);

        // uses unreferenced cfs to prevent memory leaks, in case that
        // some inputs complete quickly and retain large memory while other inputs or output continue running
        CompletionStage<Void>[] unreferencedInputs = unreferenced(inputs);

        // whether to swallow exceptions from inputs depends on the output's result,
        // so must check when the output CompletionStage completes.
        peek0(output, (v, outputEx) -> { // outputEx may be null
            Throwable outputBizEx = unwrapCfException(outputEx);
            for (int i = 0; i < unreferencedInputs.length; i++) {
                final int idx = i;
                peek0(unreferencedInputs[i], (v1, ex) -> {
                    if (ex == null) return;
                    // if ex is returned to output cf(aka. not swallowed ex), do NOTHING
                    if (unwrapCfException(ex) == outputBizEx) return;
                    safeHandle(new ExceptionInfo(where, idx, ex, safeGet(attachments, idx)), exceptionHandler);
                }, "handleSwallowedExceptions(handle the input cf)");
            }
        }, "handleSwallowedExceptions(handle the output cf)");
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
        logException(WARN, msg, exInfo.exception);
    };

    /**
     * Creates new CompletionStages that only observe exception results from the input CompletionStages,
     * ensuring the original stages and their results can be garbage collected ASAP by avoiding references.
     */
    private static CompletionStage<Void>[] unreferenced(CompletionStage<?>[] stages) {
        return CommonUtils.mapArray(stages, CompletionStage[]::new, s -> {
            CompletableFuture<Void> ret = new CompletableFuture<>();
            peek0(s, (v, ex) -> completeCf0(ret, null, ex), "unreferenced");
            return ret;
        });
    }

    private static @Nullable Object safeGet(@Nullable Object[] attachments, int index) {
        if (attachments == null) return null;
        else if (index < attachments.length) return attachments[index];
        else return null;
    }

    private static void safeHandle(ExceptionInfo info, ExceptionHandler handler) {
        try {
            handler.handle(info);
        } catch (Throwable e) {
            safeAddSuppressedEx(info.exception, e);
            logUncaughtException(ERROR, "exceptionHandler(" + handler.getClass() + ")", e);
        }
    }

    private SwallowedExceptionHandleUtils() {}
}
