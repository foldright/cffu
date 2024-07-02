package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.spi.LocationAwareLogger;


class ExceptionReporter {
    private static final String CFFU_PACKAGE_NAME = ExceptionReporter.class.getName().replaceFirst("\\.[^.]*$", "");

    private static final LoggerAdapter logger = getLogger();

    @Nullable
    static <T> T reportException(String msg, Throwable ex) {
        final String fullReport = "full";
        final String shortReport = "short";

        String report = System.getProperty("cffu.uncaught.exception.report", shortReport);
        if (fullReport.equals(report)) {
            logger.error(msg, ex);
        } else if (shortReport.equals(report)) {
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
        private final String FQCN = Slf4jLoggerAdapter.class.getName();

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
}
