package io.foldright.cffu;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;


/**
 * This class {@link Cffu} is the equivalent class to {@link CompletableFuture},
 * contains the equivalent instance methods of {@link CompletionStage} and {@link CompletableFuture}.
 * <p>
 * The methods that equivalent to static factory methods of {@link CompletableFuture}
 * is in {@link CffuFactory} class.
 *
 * @param <T> The result type returned by this future's {@code join}
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author HuHao (995483610 at qq dot com)
 * @see CffuFactoryBuilder
 * @see CffuFactory
 * @see CompletionStage
 * @see CompletableFuture
 */
public final class Cffu<T> extends BaseCffu<T, Cffu<T>> implements Future<T>, CompletionStage<T> {
    @Contract(pure = true)
    Cffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<T> cf) {
        super(cffuFactory, isMinimalStage, cf);
    }

    @Override
    Cffu<T> reset0(CffuFactory fac, boolean isMinimalStage, CompletableFuture<T> cf) {
        return new Cffu<>(fac, isMinimalStage, cf);
    }
}
