package io.foldright.cffu.ex;

public class ExceptionInfo {
    public final int index;

    public final Throwable ex;

    public final Object attachment;

    public ExceptionInfo(int index, Throwable ex, Object attachment) {
        this.index = index;
        this.ex = ex;
        this.attachment = attachment;
    }
}
