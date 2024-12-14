package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.spi.LocationAwareLogger;


/**
 * <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 *
 * @author HuHao (995483610 at qq dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
final class ExceptionReporter {
    private static final String FQCN = ExceptionReporter.class.getName();
    private static final String CFFU_PACKAGE_NAME = FQCN.replaceFirst("\\.[^.]*$", "");

    private static final LoggerAdapter logger = getLogger();

    @SuppressWarnings("StatementWithEmptyBody")
    static void reportUncaughtException(String where, Throwable ex) {
        final String fullReport = "full";
        final String shortReport = "short";
        final String noneReport = "none";

        final String report = System.getProperty("cffu.uncaught.exception.report", fullReport);
        final String msgHead = "Uncaught exception occurred at ";
        if (noneReport.equalsIgnoreCase(report)) {
            // pass silently when explicitly silenced.
        } else if (shortReport.equalsIgnoreCase(report)) {
            logger.error(msgHead + where + ", " + ex, null);
        } else {
            logger.error(msgHead + where, ex);
        }
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
