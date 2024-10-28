package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;
import org.slf4j.spi.LocationAwareLogger;

import java.util.concurrent.CompletionStage;

import static io.foldright.cffu.CompletableFutureUtils.unwrapCfException;
import static io.foldright.cffu.InternalCommonUtils.mapArray;


/**
 * <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 *
 * @author HuHao (995483610 at qq dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
final class ExceptionReporter {
    private static final String FQCN = ExceptionReporter.class.getName();
    private static final String CFFU_PACKAGE_NAME = FQCN.replaceFirst("\\.[^.]*$", "");

    private static final String UNCAUGHT_MSG_HEAD = "Uncaught exception occurred at ";
    private static final String SWALLOWED_MSG_HEAD = "Swallowed uncaught exception occurred at ";

    private static final LoggerAdapter logger = getLogger();

    static void reportSwallowedExceptionsOf(CompletionStage<?>[] inputs, CompletionStage<?> output, String where) {
        reportSwallowedExceptionsOf(inputs, output, where, false);
    }

    static void reportSwallowedExceptionsOf(CompletionStage<?>[] inputs, CompletionStage<?> output, String where, boolean copyInputs) {
        // copies input for GC to avoid holding input array whose cf elements may hold huge biz data
        final CompletionStage<?>[] inputsCopy;
        if (copyInputs) inputsCopy = unlinkCfs(inputs);
        else inputsCopy = inputs;
        // whether the exceptions of input cfs is swallowed, depends on the result of output cf;
        // so MUST checks when output cf is completed.
        output.whenComplete((v, returnedEx) -> { // returnedEx may be null
            Throwable returnedBizEx = unwrapCfException(returnedEx);
            for (CompletionStage<?> cf : inputsCopy) {
                cf.exceptionally(ex -> { // ex of exceptionally never be null
                    if (unwrapCfException(ex) == returnedBizEx) return null;
                    return reportException0(ex, SWALLOWED_MSG_HEAD, where);
                });
            }
        });
    }

    private static CompletionStage<?>[] unlinkCfs(CompletionStage<?>[] inputs) {
        return mapArray(inputs, CompletionStage[]::new, s -> s.thenApply(x -> null));
    }

    static void reportSwallowedExceptionsOf(CompletionStage<?>[] cfs, String where) {
        for (CompletionStage<?> cf : cfs) reportExceptionOf0(cf, SWALLOWED_MSG_HEAD, where);
    }

    static void reportUncaughtExceptionOf(CompletionStage<?> cf, String where) {
        reportExceptionOf0(cf, UNCAUGHT_MSG_HEAD, where);
    }

    @SuppressWarnings("SameParameterValue")
    static void reportUncaughtException(Throwable ex, String where) {
        reportException0(ex, UNCAUGHT_MSG_HEAD, where);
    }

    private static void reportExceptionOf0(CompletionStage<?> cf, String msgHead, String where) {
        cf.exceptionally(ex -> reportException0(ex, msgHead, where));
    }

    @Nullable
    @Contract("_, _, _ -> null")
    @SuppressWarnings({"StatementWithEmptyBody", "SameReturnValue"})
    private static <T> T reportException0(Throwable ex, String msgHead, String where) {
        final String fullReport = "full";
        final String shortReport = "short";
        final String noneReport = "none";

        final String report = System.getProperty("cffu.uncaught.exception.report", fullReport);
        if (noneReport.equalsIgnoreCase(report)) {
            // pass silently when explicitly silenced.
        } else if (shortReport.equalsIgnoreCase(report)) {
            logger.error(msgHead + where + ", " + ex, null);
        } else {
            logger.error(msgHead + where, ex);
        }

        return null;
    }

    private static LoggerAdapter getLogger() {
        try {
            return new Slf4jLoggerAdapter();
        } catch (NoClassDefFoundError e) {
            return new JulLoggerAdapter();
        }
    }

    private interface LoggerAdapter {
        void error(String msg, @Nullable Throwable ex);
    }

    private static final class Slf4jLoggerAdapter implements LoggerAdapter {
        private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable ex) {
            if (logger instanceof LocationAwareLogger) {
                ((LocationAwareLogger) logger).log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, ex);
            } else {
                logger.error(msg, ex);
            }
        }
    }

    private static final class JulLoggerAdapter implements LoggerAdapter {
        private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable ex) {
            logger.log(java.util.logging.Level.SEVERE, msg, ex);
        }
    }

    private ExceptionReporter() {
    }
}
