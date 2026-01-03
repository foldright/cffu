package io.foldright.cffu2.config;

import io.foldright.cffu2.internal.CffuLogger;


/**
 * Configuration for the cffu library, such as exception logging behavior.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @since 2.1.0
 */
public final class CffuConfiguration {
    /**
     * Sets the exception logging format for cffu operations programmatically at runtime.
     * <p>
     * By default, uncaught exceptions are logged with their complete stack traces
     * (i.e. {@link ExceptionLogFormat#FULL}). The initial format can be configured via
     * the system property {@code cffu.exception.log.format} at JVM startup, with values:
     * {@code full}, {@code short}, or {@code none}.
     *
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     * @see ExceptionLogFormat
     */
    public static void setExceptionLogFormat(ExceptionLogFormat format) {
        CffuLogger.setExceptionLogFormat(format);
    }

    /**
     * the exception logging format for cffu operations.
     *
     * @see #setExceptionLogFormat
     */
    public enum ExceptionLogFormat {
        /**
         * Log the complete exception stack trace (default)
         */
        FULL,
        /**
         * Log only the exception message
         */
        SHORT,
        /**
         * Suppress all exception logging
         */
        NONE
    }

    private CffuConfiguration() {}
}
