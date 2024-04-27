package io.foldright.cffu;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;


/**
 * Exception indicates that NO cfs({@link Cffu} / {@link CompletableFuture}) are provided
 * for methods require cf arguments.
 *
 * @see CffuFactory#anyOfSuccess(CompletionStage[])
 * @see CompletableFutureUtils#anyOfSuccess(CompletionStage[])
 */
@SuppressWarnings("serial")
public class NoCfsProvidedException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 0xCFF0;

    public NoCfsProvidedException() {
        super("NO cfs are provided");
    }
}
