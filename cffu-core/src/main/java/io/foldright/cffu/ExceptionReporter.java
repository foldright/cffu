package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.spi.LocationAwareLogger;

import java.util.logging.Level;


class ExceptionReporter {
    private static final LoggerAdapter logger = getLogger();

    private static final String CFFU_PACKAGE_NAME = ExceptionReporter.class.getPackageName();

    @Nullable
    public static <T> T reportException(String msg, Throwable ex) {
        final String fullReport = "full";
        final String shortReport = "short";

        String report = System.getProperty("cffu.uncaught.exception.report", shortReport);
        if (fullReport.equals(report)) {
            logger.error(msg, ex);
        } else if (shortReport.equals(report)) {
            logger.error(msg + ", cause: " + ex.getMessage(), null);
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
        void error(String msg, @Nullable Throwable thrown);
    }

    private static class Slf4jLoggerAdapter implements LoggerAdapter {
        private final String FQCN = Slf4jLoggerAdapter.class.getName();

        private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable thrown) {
            if (logger instanceof LocationAwareLogger) {
                ((LocationAwareLogger) logger).log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, thrown);
            } else {
                logger.error(msg, thrown);
            }
        }
    }

    private static class JulLoggerAdapter implements LoggerAdapter {
        private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable thrown) {
            logger.log(Level.SEVERE, msg, thrown);
        }
    }
}
