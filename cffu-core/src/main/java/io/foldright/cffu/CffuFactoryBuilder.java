package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.foldright.cffu.spi.ExecutorWrapperProvider;
import org.jetbrains.annotations.Contract;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;


/**
 * {@link CffuFactoryBuilder} is the builder of {@link CffuFactory}, creates by {@link CffuFactory#builder(Executor)}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CffuFactory
 * @see Cffu
 */
@ThreadSafe
public final class CffuFactoryBuilder {
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal constructor and fields
    ////////////////////////////////////////////////////////////////////////////////

    private final CffuDefaultExecutor defaultExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    CffuFactoryBuilder(Executor defaultExecutor) {
        this.defaultExecutor = new CffuDefaultExecutor(defaultExecutor);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Builder Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Sets {@code forbidObtrudeMethods} or not.
     * <p>
     * {@code obtrude methods} are {@link Cffu#obtrudeValue(Object)} and {@link Cffu#obtrudeException(Throwable)}.
     *
     * @see CffuFactory#forbidObtrudeMethods()
     * @see Cffu#forbidObtrudeMethods()
     */
    public CffuFactoryBuilder forbidObtrudeMethods(boolean forbid) {
        this.forbidObtrudeMethods = forbid;
        return this;
    }

    /**
     * Builds the cffu factory.
     *
     * @return the built cffu factory
     */
    @Contract(pure = true)
    public CffuFactory build() {
        return new CffuFactory(defaultExecutor, forbidObtrudeMethods);
    }

    /**
     * A poison object of type CffuFactory.
     * because poison object is dysfunctional, in general you should NEVER use it in your application logic.
     */
    public static final CffuFactory POISON_FACTORY = _poisonObject();

    @SuppressFBWarnings(value = "NP_NONNULL_PARAM_VIOLATION", justification = "null executor param is intentional for poison")
    @SuppressWarnings("DataFlowIssue")
    private static CffuFactory _poisonObject() {
        return new CffuFactory(null, false);
    }

    // endregion
    ////////////////////////////////////////////////////////////////////////////////
    // region# Internal helpers(classes/methods/fields)
    ////////////////////////////////////////////////////////////////////////////////

    @Contract(pure = true)
    static CffuFactory withDefaultExecutor(CffuFactory fac, Executor defaultExecutor) {
        if (fac.defaultExecutor.original == defaultExecutor) return fac;
        return new CffuFactory(new CffuDefaultExecutor(defaultExecutor), fac.forbidObtrudeMethods());
    }

    /**
     * Cffu default executor is ALWAYS screened({@link CompletableFuture#screenExecutor}),
     * same as {@link CompletableFuture#ASYNC_POOL}(the default executor of CompletableFuture).
     *
     * @see LLCF#screenExecutor(Executor)
     * @see LLCF#ASYNC_POOL
     */
    @SuppressWarnings("JavadocReference")
    static class CffuDefaultExecutor implements Executor {
        final Executor original;
        final Executor screened;

        @SuppressFBWarnings("CT_CONSTRUCTOR_THROW")
        private CffuDefaultExecutor(Executor defaultExecutor) {
            requireNonNull(defaultExecutor, "defaultExecutor is null");
            if (defaultExecutor instanceof CffuDefaultExecutor)
                throw new IllegalArgumentException("input defaultExecutor should never be a CffuDefaultExecutor");
            if (defaultExecutor instanceof CffuMadeExecutor)
                throw new IllegalArgumentException("input defaultExecutor should never be a CffuMadeExecutor");
            original = defaultExecutor;
            screened = wrapExecutorWithProviders(LLCF.screenExecutor(defaultExecutor), defaultExecutor);
        }

        /**
         * Delegates execution to {@link #screened} to treat this executor same as screened executor.
         */
        @Override
        public void execute(Runnable command) {
            screened.execute(command);
        }

        @Override
        public String toString() {
            return "CffuDefaultExecutor, original: " + original;
        }
    }

    @SuppressWarnings("ConstantValue")
    static Executor cffuScreened(Executor executor) {
        if (executor == null) return null;
        if (executor instanceof CffuDefaultExecutor) return executor;
        return wrapExecutorWithProviders(LLCF.screenExecutor(executor), executor);
    }

    @SuppressWarnings("ConstantValue")
    static Executor cffuUnscreened(Executor executor) {
        if (executor == null) return null;
        if (executor instanceof CffuDefaultExecutor) return executor;
        return wrapExecutorWithProviders(executor, executor);
    }

    private static CffuMadeExecutor wrapExecutorWithProviders(Executor executor, Executor original) {
        if (executor instanceof CffuMadeExecutor)
            throw new IllegalArgumentException("input executor should never be a CffuMadeExecutor");

        for (ExecutorWrapperProvider provider : EXECUTOR_WRAPPER_PROVIDERS) {
            Supplier<String> msg = () -> provider + "(class: " + provider.getClass().getName() + ") return null";
            executor = requireNonNull(provider.wrap(executor), msg);
        }
        return new CffuMadeExecutor(executor, original);
    }

    private static final List<ExecutorWrapperProvider> EXECUTOR_WRAPPER_PROVIDERS = loadExecutorWrapperProviders();

    private static List<ExecutorWrapperProvider> loadExecutorWrapperProviders() {
        final ServiceLoader<ExecutorWrapperProvider> loader = ServiceLoader.load(ExecutorWrapperProvider.class);
        return StreamSupport.stream(loader.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * A little class to prevent duplicate wrapping of executors.
     */
    private static final class CffuMadeExecutor implements Executor {
        final Executor wrappee;
        final Executor original;

        CffuMadeExecutor(Executor wrappee, Executor original) {
            this.wrappee = wrappee;
            this.original = original;
        }

        @Override
        public void execute(Runnable command) {
            wrappee.execute(command);
        }

        @Override
        public String toString() {
            return "CffuMadeExecutor, wrappee: " + wrappee + " original: " + original;
        }
    }
}
