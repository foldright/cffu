package io.foldright.cffu.ex;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu.internal.CommonUtils;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.WeakHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.foldright.cffu.CompletableFutureUtils.unwrapCfException;
import static io.foldright.cffu.internal.ExceptionLogger.logUncaughtException;


/**
 * Using {@code BiConsumer<CompletionStage<?>, Throwable>} as exception handler type.
 */
public final class ExceptionHandleUtils {
    /**
     * Returns a logging exception handler that logs the exception from CompletionStage by java logger.
     */
    public static ExceptionHandler loggingExHandler(String where) {
        return exceptionInfo -> logUncaughtException(where, exceptionInfo.ex);
    }

    public static <T> void handleSwallowedExceptions(
            ExceptionHandler exceptionHandler, CompletableFuture<?> output, CompletionStage<? extends T>[] inputs) {
        // use unlinked cf to prevent memory leaks when some inputs complete quickly and retain large memory
        // while other inputs or output continue running
        CompletionStage<?>[] unlinkedInputs = unlink(inputs);

        // Whether to swallow exceptions from inputs depends on the output's result,
        // so must check when the output CompletionStage completes.
        output.whenComplete((v, outputEx) -> { // outputEx may be null
            Throwable outputBizEx = unwrapCfException(outputEx);
            for (int i = 0; i < unlinkedInputs.length; i++) {
                final int idx = i;
                CompletionStage<?> cf = unlinkedInputs[i];
                // argument ex of method `exceptionally` is never null
                cf.exceptionally(ex -> {
                    if (unwrapCfException(ex) == outputBizEx) return null;
                    else return safeHandle(new ExceptionInfo(idx, ex, null), exceptionHandler);
                });
            }
        });
    }

    public static <C extends CompletionStage<?>> void handleAllExceptions(ExceptionHandler exceptionHandler, C[] cfs) {
        // argument ex of method `exceptionally` is never null
        for (int i = 0; i < cfs.length; i++) {
            final int idx = i;
            cfs[idx].exceptionally(ex -> safeHandle(new ExceptionInfo(idx, ex, null), exceptionHandler));
        }
    }

    @Nullable
    @Contract("_, _ -> null")
    @SuppressWarnings("SameReturnValue")
    private static <T> T safeHandle(ExceptionInfo exceptionInfo, ExceptionHandler exceptionHandler) {
        try {
            exceptionHandler.accept(exceptionInfo);
        } catch (Throwable e) {
            logUncaughtException("handleExceptionsOf", e);
        }
        return null;
    }

    private static <K> WeakHashMap<K, Void> newWeakHashMap(K[] elements) {
        WeakHashMap<K, Void> ret = newWeakHashMap(elements.length);
        for (K e : elements) ret.put(e, null);
        return ret;
    }

    private static <K, V> HashMap<K, V> newHashMap(int expectedSize) {
        return new HashMap<>(computeMapInitialCapacity(expectedSize), DEFAULT_LOAD_FACTOR);
    }

    private static <K, V> WeakHashMap<K, V> newWeakHashMap(int expectedSize) {
        return new WeakHashMap<>(computeMapInitialCapacity(expectedSize), DEFAULT_LOAD_FACTOR);
    }

    private static int computeMapInitialCapacity(int expectedSize) {
        return (int) Math.ceil(expectedSize / (double) DEFAULT_LOAD_FACTOR);
    }

    /**
     * Default load factor for {@link HashMap}/{@link WeakHashMap}/{@link LinkedHashMap} variants.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;


    private static CompletionStage<Void>[] unlink(CompletionStage<?>[] css) {
        return CommonUtils.mapArray(css, CompletionStage[]::new, s -> s.thenRun(NOP));
    }

    private static CompletionStage<Void> unlink(CompletionStage<?> input) {
        return input.thenRun(NOP);
    }

    private static final Runnable NOP = () -> {
    };

    private ExceptionHandleUtils() {
    }
}
