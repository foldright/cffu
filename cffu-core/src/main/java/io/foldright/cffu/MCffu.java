package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.CheckReturnValue;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.foldright.cffu.CffuFactoryBuilder.cffuScreened;


/**
 * Cffu with result type {@link Iterable} (aka. multiply data/collection),
 * {@code MCffu<E, List<E>>} is same as {@code Cffu<List<E>>} except with more methods.
 *
 * @param <T> The result collection type returned by this future's {@code join}
 * @param <E> the data element type of result collection
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Cffu
 */
public final class MCffu<E, T extends Iterable<? extends E>>
        extends BaseCffu<T, MCffu<E, T>> implements Future<T>, CompletionStage<T> {
    /**
     * INTERNAL constructor.
     */
    MCffu(CffuFactory cffuFactory, boolean isMinimalStage, CompletableFuture<T> cf) {
        super(cffuFactory, isMinimalStage, cf);
    }

    @Override
    MCffu<E, T> create(CffuFactory fac, boolean isMinimalStage, CompletableFuture<T> cf) {
        return new MCffu<>(fac, isMinimalStage, cf);
    }

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
        ////////////////////////////////////////////////////////////////////////////////
        //    - thenParApply* (CF<Iterable>, Function: E -> U)    -> MCffu<U, List<U>>
        //    - thenParAccept*(CF<Iterable>, Consumer: E -> Void) -> Cffu<Void>
        ////////////////////////////////////////////////////////////////////////////////

        /**
         * Shortcut to method {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyFailFastAsync(Function<? super E, ? extends U> fn) {
            return thenParApplyFailFastAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyFailFastAsync(Function<? super E, ? extends U> fn, Executor executor) {
            return createMCffu(CfParallelUtils.thenParApplyFailFastAsync(cf, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAllSuccessAsync(@Nullable U valueIfFailed, Function<? super E, ? extends U> fn) {
            return thenParApplyAllSuccessAsync(valueIfFailed, fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CffuFactory#allSuccessResultsOf allSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAllSuccessAsync(@Nullable U valueIfFailed, Function<? super E, ? extends U> fn, Executor executor) {
            return createMCffu(CfParallelUtils.thenParApplyAllSuccessAsync(cf, valueIfFailed, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyMostSuccessAsync(
                @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit, Function<? super E, ? extends U> fn) {
            return thenParApplyMostSuccessAsync(valueIfNotSuccess, timeout, unit, fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CffuFactory#mostSuccessResultsOf mostSuccessResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyMostSuccessAsync(
                @Nullable U valueIfNotSuccess, long timeout, TimeUnit unit, Function<? super E, ? extends U> fn, Executor executor) {
            return createMCffu(CfParallelUtils.thenParApplyMostSuccessAsync(
                    cf, valueIfNotSuccess, timeout, unit, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAsync(Function<? super E, ? extends U> fn) {
            return thenParApplyAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CffuFactory#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> MCffu<U, List<U>> thenParApplyAsync(Function<? super E, ? extends U> fn, Executor executor) {
            return createMCffu(CfParallelUtils.thenParApplyAsync(cf, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnySuccessAsync(Function<? super E, ? extends U> fn) {
            return thenParApplyAnySuccessAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnySuccessAsync(Function<? super E, ? extends U> fn, Executor executor) {
            return createCffu(CfParallelUtils.thenParApplyAnySuccessAsync(cf, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnyAsync(Function<? super E, ? extends U> fn) {
            return thenParApplyAnyAsync(fn, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's function computation
         * into a Cffu using {@link CffuFactory#supplyAsync(Supplier, Executor)}.
         * <p>
         * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public <U> Cffu<U> thenParApplyAnyAsync(Function<? super E, ? extends U> fn, Executor executor) {
            return createCffu(CfParallelUtils.thenParApplyAnyAsync(cf, fn, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptFailFastAsync(Consumer<? super E> action) {
            return thenParAcceptFailFastAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CffuFactory#allResultsFailFastOf allResultsFailFastOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptFailFastAsync(Consumer<? super E> action, Executor executor) {
            return createCffu(CfParallelUtils.thenParAcceptFailFastAsync(cf, action, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAsync(Consumer<? super E> action) {
            return thenParAcceptAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#allResultsOf allResultsOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CffuFactory#allResultsOf allResultsOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAsync(Consumer<? super E> action, Executor executor) {
            return createCffu(CfParallelUtils.thenParAcceptAsync(cf, action, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnySuccessAsync(Consumer<? super E> action) {
            return thenParAcceptAnySuccessAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#anySuccessOf anySuccessOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CffuFactory#anySuccessOf anySuccessOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnySuccessAsync(Consumer<? super E> action, Executor executor) {
            return createCffu(CfParallelUtils.thenParAcceptAnySuccessAsync(cf, action, cffuScreened(executor)));
        }

        /**
         * Shortcut to method {@link CffuFactory#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable)} with the executor {@link #defaultExecutor()}.
         * <p>
         * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnyAsync(Consumer<? super E> action) {
            return thenParAcceptAnyAsync(action, fac.defaultExecutor);
        }

        /**
         * Shortcut to method {@link CffuFactory#anyOf anyOf},
         * processes elements from the result of parameter stage in parallel by wrapping each element's consumer computation
         * into a Cffu using {@link CffuFactory#runAsync(Runnable, Executor)}.
         * <p>
         * See the {@link CffuFactory#anyOf anyOf} documentation for the rules of result computation.
         */
        @CheckReturnValue(explanation = "should use the returned Cffu; otherwise, prefer simple method `thenParAcceptAsync`")
        public Cffu<Void> thenParAcceptAnyAsync(Consumer<? super E> action, Executor executor) {
            return createCffu(CfParallelUtils.thenParAcceptAnyAsync(cf, action, cffuScreened(executor)));
        }

        private ParOps() {}
    }
}
