package io.foldright.cffu;

import java.util.concurrent.CompletableFuture;


/**
 * Exception indicates that NO cfs({@link Cffu}/{@link CompletableFuture}) are provided
 * for methods require cf arguments.
 *
 * @see CffuFactory#cffuAnyOfSuccess(Cffu[])
 * @see CffuFactory#cffuAnyOfSuccess(CompletableFuture[])
 * @see CffuFactory#anyOfSuccess(Cffu[])
 * @see CffuFactory#anyOfSuccess(CompletableFuture[])
 * @see CompletableFutureUtils#anyOfSuccessWithType(CompletableFuture[])
 * @see CompletableFutureUtils#anyOfSuccess(CompletableFuture[])
 */
@SuppressWarnings("serial")
public class NoCfsProvidedException extends RuntimeException {
    @java.io.Serial
    private static final long serialVersionUID = 0xCFF0;

    public NoCfsProvidedException() {
        super("NO cfs are provided");
    }
}
