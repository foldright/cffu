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
         * Shortcut to method {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAllSuccessAsync(@Nullable U valueIfFailed, Function<? super T, ? extends U> fn) {
            return thenParApplyAllSuccessAsync(valueIfFailed, fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAllSuccessAsync(@Nullable U valueIfFailed, Function<? super T, ? extends U> fn, Executor executor) {
            return resetItrCf(CfParallelUtils.thenParApplyAllSuccessAsync(cffuUnwrap(), valueIfFailed, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyMostSuccessAsync(
                @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit, Function<? super T, ? extends U> fn) {
            return thenParApplyMostSuccessAsync(valueIfNotSuccess, timeout, unit, fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyMostSuccessAsync(
                @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit, Function<? super T, ? extends U> fn, Executor executor) {
            return resetItrCf(CfParallelUtils.thenParApplyMostSuccessAsync(cffuUnwrap(), valueIfNotSuccess, timeout, unit, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAsync(Function<? super T, ? extends U> fn) {
            return thenParApplyAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAsync(Function<? super T, ? extends U> fn, Executor executor) {
            return resetItrCf(CfParallelUtils.thenParApplyAsync(cffuUnwrap(), fn, cffuScreened(executor)));
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

        /**
         * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnyAsync(Function<? super T, ? extends U> fn) {
            return thenParApplyAnyAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnyAsync(Function<? super T, ? extends U> fn, Executor executor) {
            return resetCf(CfParallelUtils.thenParApplyAnyAsync(cffuUnwrap(), fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptFailFastAsync(Consumer<? super T> action) {
            return thenParAcceptFailFastAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptFailFastAsync(Consumer<? super T> action, Executor executor) {
            return resetCf(CfParallelUtils.thenParAcceptFailFastAsync(cffuUnwrap(), action, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAsync(Consumer<? super T> action) {
            return thenParAcceptAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAsync(Consumer<? super T> action, Executor executor) {
            return resetCf(CfParallelUtils.thenParAcceptAsync(cffuUnwrap(), action, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnySuccessAsync(Consumer<? super T> action) {
            return thenParAcceptAnySuccessAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnySuccessAsync(Consumer<? super T> action, Executor executor) {
            return resetCf(CfParallelUtils.thenParAcceptAnySuccessAsync(cffuUnwrap(), action, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnyAsync(Consumer<? super T> action) {
            return thenParAcceptAnyAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CompletableFutureUtils#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CompletableFutureUtils#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnyAsync(Consumer<? super T> action, Executor executor) {
            return resetCf(CfParallelUtils.thenParAcceptAnyAsync(cffuUnwrap(), action, cffuScreened(executor)));
        }

        private ParOps() {}
    }
}
