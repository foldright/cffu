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
 * @param <T> The result collection type returned by this future's {@code join}
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @author HuHao (995483610 at qq dot com)
 * @see CffuFactoryBuilder
 * @see CffuFactory
 * @see CompletionStage
 * @see CompletableFuture
 * @see MCffu
 */
public final class Cffu<T> extends BaseCffu<T, Cffu<T>> implements Future<T>, CompletionStage<T> {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal constructor/methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * INTERNAL constructor.
     */
    Cffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<T> cf) {
        super(cffuFactory, isMinimalStage, cf);
    }

    @Override
    Cffu<T> create(CffuFactory fac, boolean isMinimalStage, CompletableFuture<T> cf) {
        return new Cffu<>(fac, isMinimalStage, cf);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Conversion Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Converts to {@link MCffu}, reuse the underlying CompletableFuture instance and rewraps it to {@link MCffu}.
     *
     * @param <E> the data element type of result collection
     * @see MCffu#asCffu()
     * @see CffuFactory#toMCffu(CompletionStage)
     */
    @Contract(pure = true)
    public static <E, U extends Iterable<? extends E>> MCffu<E, U> asMCffu(Cffu<U> cffu) {
        return new MCffu<>(cffu.fac, cffu.isMinimalStage, cffu.cf);
    }
}
