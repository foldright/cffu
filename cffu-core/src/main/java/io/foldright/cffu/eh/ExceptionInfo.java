package io.foldright.cffu.eh;

import edu.umd.cs.findbugs.annotations.Nullable;


/**
 * Exception info of exceptions from multiply {@code CompletionStage}s, used as argument of {@link ExceptionHandler}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public final class ExceptionInfo {
    /**
     * The location where the exception occurs.
     */
    public final String where;

    /**
     * The index of the {@code CompletionStage} that throws the exception.
     */
    public final int index;

    /**
     * The exception.
     */
    public final Throwable ex;

    /**
     * An optional attachment object that may contain additional context; can be {@code null}.
     */
    @Nullable
    public final Object attachment;

    /**
     * Constructs an {@code ExceptionInfo} with the specified location, index, exception, and attachment.
     */
    public ExceptionInfo(String where, int index, Throwable ex, @Nullable Object attachment) {
        this.where = where;
        this.index = index;
        this.ex = ex;
        this.attachment = attachment;
    }
}
