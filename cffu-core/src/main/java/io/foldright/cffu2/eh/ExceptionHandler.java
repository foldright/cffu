package io.foldright.cffu2.eh;


/**
 * Exception handler, used by the methods of {@link SwallowedExceptionHandleUtils}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
@FunctionalInterface
public interface ExceptionHandler {
    /**
     * Handles the exception.
     *
     * @param exceptionInfo the exception information
     */
    void handle(ExceptionInfo exceptionInfo);
}
