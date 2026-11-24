package io.foldright.cffu2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Exception indicates that NO cfs({@link CompletionStage}, including {@link Cffu} and
 * {@link CompletableFuture}) are provided for methods that require cf arguments.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CffuFactory#anySuccessOf(CompletionStage[])
 * @see CompletableFutureUtils#anySuccessOf(CompletionStage[])
 */
public final class NoCfsProvidedException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 0xCFF0;

    public NoCfsProvidedException() {
        super("NO cfs are provided");
    }
}
