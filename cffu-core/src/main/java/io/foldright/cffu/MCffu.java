package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu.CffuFactoryBuilder.cffuScreened;


/**
 * Cffu with result type {@link Iterable} (aka. multiply data/collection), {@code MCffu<T, List<T>>} is same as {@code MCffu<List<T>>} except with more methods.
 *
 * @param <E> data elements type
 * @param <T> The result type returned by this future's {@code join}
 */
public final class MCffu<E, T extends Iterable<? extends E>> extends BaseCffu<T, MCffu<E, T>> implements Future<T>, CompletionStage<T> {

    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal constructor
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * internal constructor
     */
    @Contract(pure = true)
    MCffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<T> cf) {
        super(cffuFactory, isMinimalStage, cf);
    }

    @Override
    MCffu<E, T> reset0(CffuFactory fac, boolean isMinimalStage, CompletableFuture<T> cf) {
        return new MCffu<>(fac, isMinimalStage, cf);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Par Ops
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a {@link CffuFactory.ParOps} instance to access the methods for parallel data processing using Cffu.
     */
    public ParOps parOps() {
        return new ParOps();
    }

    /**
     * The methods for parallel data processing using Cffu.
     */
    public final class ParOps {

        // endregion
        ////////////////////////////////////////////////////////////////////////////////
        // region# Instance Methods for Cffu<Iterable<T>>
        //
        //    - thenParApply* (CF<Iterable>, Function: T -> U)    -> MCffu<U, List<U>>
        //    - thenParAccept*(CF<Iterable>, Consumer: T -> Void) -> Cffu<Void>
        ////////////////////////////////////////////////////////////////////////////////

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyFailFastAsync(Function<? super E, ? extends U> fn) {
            return thenParApplyFailFastAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyFailFastAsync(Function<? super E, ? extends U> fn, Executor executor) {
            return resetToMCffu0(CfParallelUtils.thenParApplyFailFastAsync(cffuUnwrap(), fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnySuccessAsync(Function<? super E, ? extends U> fn) {
            return thenParApplyAnySuccessAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnySuccessAsync(Function<? super E, ? extends U> fn, Executor executor) {
            return resetToCffu0(CfParallelUtils.thenParApplyAnySuccessAsync(cffuUnwrap(), fn, cffuScreened(executor)));
        }

        private ParOps() {
        }
    }
}
