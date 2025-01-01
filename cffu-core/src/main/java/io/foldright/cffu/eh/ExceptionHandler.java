package io.foldright.cffu.eh;

import java.util.function.Consumer;


/**
 * Exception handler that handles the exceptions from multiply {@code CompletionStage}s.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 */
public interface ExceptionHandler extends Consumer<ExceptionInfo> {
}
