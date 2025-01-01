package io.foldright.cffu.internal;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.spi.LocationAwareLogger;


/**
 * <strong>Internal</strong> exception logging utility for the cffu library.
 * <p>
 * By default, uncaught exceptions are logged with their complete stack traces. The logging behavior can be configured
 * through the system property {@code cffu.uncaught.exception.log.format} with the following values:
 * <ul>
 * <li>{@code full}: Log the complete exception stack trace (default)</li>
 * <li>{@code short}: Log only the exception message</li>
 * <li>{@code none}: Suppress all exception logging</li>
 * </ul>
 * <p>
 * Configure the logging format by either:
 * <ul>
 * <li>Setting the JVM argument {@code -Dcffu.uncaught.exception.log.format=<value>} at startup</li>
 * <li>Calling {@code System.setProperty("cffu.uncaught.exception.log.format", "<value>")} programmatically</li>
 * </ul>
 *
 * @author HuHao (995483610 at qq dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@ApiStatus.Internal
public final class ExceptionLogger {
    private static final String FQCN = ExceptionLogger.class.getName();
    private static final String CFFU_PACKAGE_NAME = FQCN.replaceFirst("\\.[^.]*\\.[^.]*$", "");

    private static final LoggerAdapter logger = getLogger();

    @SuppressWarnings("StatementWithEmptyBody")
    public static void logUncaughtException(String where, Throwable ex) {
        final String fullFormat = "full";
        final String shortFormat = "short";
        final String noneFormat = "none";

        final String format = System.getProperty("cffu.uncaught.exception.log.format", fullFormat);
        final String msgHead = "Uncaught exception occurred at ";
        if (noneFormat.equalsIgnoreCase(format)) {
            // pass silently when explicitly silenced.
        } else if (shortFormat.equalsIgnoreCase(format)) {
            logger.error(msgHead + where + ", " + ex, null);
        } else {
            logger.error(msgHead + where, ex);
        }
    }

    /**
     * Returns a logger adapter that uses {@code SLF4J} if available, otherwise uses {@link java.util.logging}.
     */
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

    private ExceptionLogger() {
    }
}
