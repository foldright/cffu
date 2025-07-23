package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu.CffuFactoryBuilder.cffuScreened;


/**
 * Cffu with result type {@link Iterable} (aka. multiply data/collection), {@code MCffu<T, List<T>>} is same as {@code MCffu<List<T>>} except with more methods.
 */
public class MCffu<T, C extends Iterable<T>> extends Cffu<C> {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal constructor

    /// /////////////////////////////////////////////////////////////////////////////

    @Contract(pure = true)
    MCffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<C> cf) {
        super(cffuFactory, isMinimalStage, cf);
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
        public <U> MCffu<U, List<U>> thenParApplyFailFastAsync(Function<? super T, ? extends U> fn) {
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
        public <U> MCffu<U, List<U>> thenParApplyFailFastAsync(Function<? super T, ? extends U> fn, Executor executor) {
            return resetItrCf(CfParallelUtils.thenParApplyFailFastAsync(cffuUnwrap(), fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnySuccessAsync(Function<? super T, ? extends U> fn) {
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
        public <U> Cffu<U> thenParApplyAnySuccessAsync(Function<? super T, ? extends U> fn, Executor executor) {
            return resetCf(CfParallelUtils.thenParApplyAnySuccessAsync(cffuUnwrap(), fn, cffuScreened(executor)));
        }

        private ParOps() {}
    }
}
