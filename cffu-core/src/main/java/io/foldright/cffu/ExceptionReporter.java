package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

import java.util.logging.Level;


class ExceptionReporter {
    private static final String PACKAGE_NAME = ExceptionReporter.class.getPackageName();

    private static final LoggerAdapter logger = getLogger();

    @SuppressWarnings("DataFlowIssue")
    @SuppressFBWarnings("NP_NONNULL_RETURN_VIOLATION")
    public static <T> T reportException(String msg, Throwable ex) {
        String report = System.getProperty("cffu.uncaught.exception.report", "short");
        if ("full".equals(report)) {
            logger.error(msg, ex);
        } else if ("short".equals(report)) {
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
        private static final String FQCN = Slf4jLoggerAdapter.class.getName();

        private final org.slf4j.Logger logger = LoggerFactory.getLogger(PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable thrown) {
            if (logger instanceof LocationAwareLogger) {
                ((LocationAwareLogger) logger).log(null, FQCN, LocationAwareLogger.ERROR_INT, msg, null, thrown);
            } else {
                if (thrown != null) logger.error(msg, thrown);
                else logger.error(msg);
            }
        }
    }

    private static class JulLoggerAdapter implements LoggerAdapter {
        private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PACKAGE_NAME);

        @Override
        public void error(String msg, @Nullable Throwable thrown) {
            logger.log(Level.SEVERE, msg, thrown);
        }
    }
}
