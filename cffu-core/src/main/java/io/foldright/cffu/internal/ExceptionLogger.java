package io.foldright.cffu.internal;

import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.spi.LocationAwareLogger;


/**
 * <strong>Internal</strong> exception logging utility for the cffu library.
 * <p>
 * By default, uncaught exceptions are logged with their complete stack traces. The logging behavior can be configured
 * through the system property {@code cffu.exception.log.format} with the following values:
 * <ul>
 * <li>{@code full}: Log the complete exception stack trace (default)</li>
 * <li>{@code short}: Log only the exception message</li>
 * <li>{@code none}: Suppress all exception logging</li>
 * </ul>
 * <p>
 * Configure the logging format by either:
 * <ul>
 * <li>Setting the JVM argument {@code -Dcffu.exception.log.format=<value>} at startup</li>
 * <li>Calling {@code System.setProperty("cffu.exception.log.format", "<value>")} programmatically</li>
 * </ul>
 *
 * @author HuHao (995483610 at qq dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 */
@ApiStatus.Internal
public final class ExceptionLogger {
    private static final String FQCN = ExceptionLogger.class.getName();
    private static final String CFFU_PACKAGE_NAME = FQCN.replaceFirst("\\.[^.]*\\.[^.]*$", "");

    private static final LoggerAdapter logger = getLogger();

    @SuppressWarnings("StatementWithEmptyBody")
    public static void logException(Level level, String msg, Throwable ex) {
        final String fullFormat = "full";
        final String shortFormat = "short";
        final String noneFormat = "none";

        final String format = System.getProperty("cffu.exception.log.format", fullFormat);
        if (noneFormat.equalsIgnoreCase(format)) {
            // pass silently when explicitly silenced.
        } else if (shortFormat.equalsIgnoreCase(format)) {
            logger.log(level, msg + ", " + ex, null);
        } else {
            logger.log(level, msg, ex);
        }
    }

    public static void logUncaughtException(Level level, String where, Throwable ex) {
        logException(level, "Uncaught exception occurred at " + where, ex);
    }

    public enum Level {ERROR, WARN}

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
        void log(Level level, String msg, @Nullable Throwable ex);
    }

    private static final class Slf4jLoggerAdapter implements LoggerAdapter {
        private final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void log(Level level, String msg, @Nullable Throwable ex) {
            if (logger instanceof LocationAwareLogger) {
                int lvl = level == Level.ERROR ? LocationAwareLogger.ERROR_INT : LocationAwareLogger.WARN_INT;
                ((LocationAwareLogger) logger).log(null, FQCN, lvl, msg, null, ex);
            } else {
                if (level == Level.ERROR) logger.error(msg, ex);
                else logger.warn(msg, ex);
            }
        }
    }

    private static final class JulLoggerAdapter implements LoggerAdapter {
        private final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(CFFU_PACKAGE_NAME);

        @Override
        public void log(Level level, String msg, @Nullable Throwable ex) {
            logger.log(level == Level.ERROR ? java.util.logging.Level.SEVERE : java.util.logging.Level.WARNING, msg, ex);
        }
    }

    private ExceptionLogger() {}
}
