package io.foldright.cffu2.internal;

import edu.umd.cs.findbugs.annotations.Nullable;
import io.foldright.cffu2.config.CffuConfiguration.ExceptionLogFormat;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.spi.LocationAwareLogger;


/**
 * <strong>Internal</strong> logger for the cffu library.
 *
 * @author HuHao (995483610 at qq dot com)
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see io.foldright.cffu2.config.CffuConfiguration#setExceptionLogFormat
 * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 */
@ApiStatus.Internal
public final class CffuLogger {
    private static final String FQCN = CffuLogger.class.getName();

    private final LoggerAdapter logger;

    private CffuLogger(LoggerAdapter logger) {this.logger = logger;}

    public static CffuLogger getLogger(Class<?> clazz) {
        return new CffuLogger(getLoggerAdapter(clazz));
    }

    public void logException(Level level, String msg, Throwable ex) {
        log0(level, msg, ex);
    }

    public void logUncaughtException(Level level, String where, Throwable ex) {
        log0(level, "Uncaught exception occurred at " + where, ex);
    }

    public void log(Level level, String msg) {
        log0(level, msg, null);
    }

    public enum Level {ERROR, WARN}

    @VisibleForTesting
    static volatile ExceptionLogFormat exceptionLogFormat = initExceptionLogFormat();

    public static void setExceptionLogFormat(ExceptionLogFormat format) {exceptionLogFormat = format;}

    @SuppressWarnings("StatementWithEmptyBody")
    private void log0(Level level, String msg, @Nullable Throwable ex) {
        // read volatile field once into local var for consistent use within this method
        final ExceptionLogFormat format = exceptionLogFormat;
        if (format == ExceptionLogFormat.NONE) {
            // pass silently when explicitly silenced.
        } else if (format == ExceptionLogFormat.SHORT) {
            if (ex != null) msg += ", exception: " + ex;
            logger.log(level, msg, null);
        } else {
            logger.log(level, msg, ex);
        }
    }

    private static ExceptionLogFormat initExceptionLogFormat() {
        final String fullFormat = "full";
        final String shortFormat = "short";
        final String noneFormat = "none";

        final String format = System.getProperty("cffu.exception.log.format", fullFormat);
        if (noneFormat.equalsIgnoreCase(format)) {
            return ExceptionLogFormat.NONE;
        } else if (shortFormat.equalsIgnoreCase(format)) {
            return ExceptionLogFormat.SHORT;
        } else {
            return ExceptionLogFormat.FULL;
        }
    }

    private static volatile boolean slf4jUnavailable = false;

    /**
     * Returns a logger adapter that uses {@code SLF4J} if available, otherwise uses {@link java.util.logging}.
     */
    private static LoggerAdapter getLoggerAdapter(Class<?> clazz) {
        if (slf4jUnavailable) return new JulLoggerAdapter(clazz);
        try {
            return new Slf4jLoggerAdapter(clazz);
        } catch (NoClassDefFoundError e) {
            slf4jUnavailable = true;
            return new JulLoggerAdapter(clazz);
        }
    }

    private interface LoggerAdapter {
        void log(Level level, String msg, @Nullable Throwable ex);
    }

    private static final class Slf4jLoggerAdapter implements LoggerAdapter {
        private final org.slf4j.Logger logger;

        Slf4jLoggerAdapter(Class<?> clazz) {logger = org.slf4j.LoggerFactory.getLogger(clazz);}

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
        private final java.util.logging.Logger logger;

        JulLoggerAdapter(Class<?> clazz) {logger = java.util.logging.Logger.getLogger(clazz.getName());}

        @Override
        public void log(Level level, String msg, @Nullable Throwable ex) {
            java.util.logging.Level lvl = level == Level.ERROR
                    ? java.util.logging.Level.SEVERE
                    : java.util.logging.Level.WARNING;
            logger.log(lvl, msg, ex);
        }
    }
}
