package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.spi.LocationAwareLogger;


/**
 * <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 *
 * @author HuHao (995483610 at qq dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
class ExceptionReporter {
    private static final String FQCN = ExceptionReporter.class.getName();
    private static final String CFFU_PACKAGE_NAME = FQCN.replaceFirst("\\.[^.]*$", "");

    private static final LoggerAdapter logger = getLogger();

    @Nullable
    @SuppressWarnings("StatementWithEmptyBody")
    static <T> T reportException(String msg, Throwable ex) {
        final String fullReport = "full";
        final String shortReport = "short";
        final String noneReport = "none";

        String report = System.getProperty("cffu.uncaught.exception.report", shortReport);
        if (noneReport.equalsIgnoreCase(report)) {
            // pass silently when explicitly silenced.
        } else if (fullReport.equalsIgnoreCase(report)) {
            logger.error(msg, ex);
        } else {
            logger.error(msg + ", " + ex, null);
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

    private static class Slf4jLoggerAdapter implements LoggerAdapter {
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

    private static class JulLoggerAdapter implements LoggerAdapter {
        private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable ex) {
            logger.log(java.util.logging.Level.SEVERE, msg, ex);
        }
    }

    private ExceptionReporter() {
    }
}
