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
     * Sets the exception logging format for cffu operations.
     * <p>
     * By default, (uncaught) exceptions are logged with their complete stack traces. The default logging behavior can be
     * configured through the system property {@code cffu.exception.log.format} at JVM startup with the following values:
     * <ul>
     * <li>{@code full}: Log the complete exception stack trace (default)</li>
     * <li>{@code short}: Log only the exception message</li>
     * <li>{@code none}: Suppress all exception logging</li>
     * </ul>
     *
     * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
     */
    public static void setExceptionLoggingFormat(ExceptionLoggingFormat format) {
        CffuLogger.setExceptionLoggingFormat(format);
    }

    public enum ExceptionLoggingFormat {FULL, SHORT, NONE}

    private CffuConfiguration() {}
}
