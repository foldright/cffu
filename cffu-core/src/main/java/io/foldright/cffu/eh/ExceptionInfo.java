package io.foldright.cffu.eh;

import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static java.util.Objects.requireNonNull;


/**
 * Exception info of exceptions, used as argument of {@link ExceptionHandler}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public final class ExceptionInfo {
    /**
     * The location where the exception occurs.
     * <p>
     * The location is provided through the {@code where} parameter of the handle methods in {@link SwallowedExceptionHandleUtils}.
     */
    public final String where;

    /**
     * The <strong>0-based</strong> index of the input {@code CompletionStage} that throws the exception.
     */
    public final int index;

    /**
     * The exception.
     */
    public final Throwable exception;

    /**
     * An optional attachment object that may contain additional context; can be {@code null}.
     * <p>
     * The attachment object is provided through the attachments parameter of the handle methods in {@link SwallowedExceptionHandleUtils}.
     *
     * @see SwallowedExceptionHandleUtils#handleAllSwallowedExceptions(String, Object[], ExceptionHandler, CompletionStage[])
     * @see SwallowedExceptionHandleUtils#handleSwallowedExceptions(String, Object[], ExceptionHandler, CompletableFuture, CompletionStage[])
     */
    @Nullable
    public final Object attachment;

    /**
     * Constructs an {@code ExceptionInfo} with the specified location, index, exception, and attachment.
     */
    public ExceptionInfo(String where, int index, Throwable exception, @Nullable Object attachment) {
        this.where = where;
        this.index = index;
        this.exception = requireNonNull(exception);
        this.attachment = attachment;
    }
}
