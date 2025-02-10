package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.foldright.cffu.spi.ExecutorWrapperProvider;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.VisibleForTesting;

import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.ServiceLoader;
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
    /// /////////////////////////////////////////////////////////////////////////////

    private final CffuExecutorWrapper cffuExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    CffuFactoryBuilder(Executor defaultExecutor) {
        this.cffuExecutor = makeCffuExecutorWrapper(defaultExecutor);
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
        return new CffuFactory(cffuExecutor, forbidObtrudeMethods);
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
    // region# Internal helper methods and fields

    /// /////////////////////////////////////////////////////////////////////////////

    @Contract(pure = true)
    static CffuFactory withDefaultExecutor(CffuFactory fac, Executor defaultExecutor) {
        if (fac.cffuExecutor.original == defaultExecutor) return fac;
        else return new CffuFactory(makeCffuExecutorWrapper(defaultExecutor), fac.forbidObtrudeMethods());
    }

    private static CffuExecutorWrapper makeCffuExecutorWrapper(final Executor defaultExecutor) {
        requireNonNull(defaultExecutor, "defaultExecutor is null");
        // check CffuMadeExecutor interface to avoid re-wrapping.
        if (defaultExecutor instanceof CffuExecutorWrapper) return (CffuExecutorWrapper) defaultExecutor;
        else return new CffuExecutorWrapper(defaultExecutor);
    }

    @VisibleForTesting
    static class CffuExecutorWrapper implements Executor {
        final Executor original;
        final Executor screened;
        final Executor unscreened;

        CffuExecutorWrapper(Executor defaultExecutor) {
            original = defaultExecutor;
            screened = cffuScreenExecutor(defaultExecutor);
            unscreened = cffuUnscreenExecutor(defaultExecutor);
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
            return "CffuMadeExecutor of executor(" + original + ")";
        }
    }

    static Executor cffuScreenExecutor(Executor defaultExecutor) {
        return wrapExecutorByProviders(LLCF.screenExecutor(defaultExecutor));
    }

    static Executor cffuUnscreenExecutor(Executor defaultExecutor) {
        return wrapExecutorByProviders(defaultExecutor);
    }

    private static Executor wrapExecutorByProviders(Executor executor) {
        for (ExecutorWrapperProvider provider : EXECUTOR_WRAPPER_PROVIDERS) {
            Supplier<String> msg = () -> provider + "(class: " + provider.getClass().getName() + ") return null";
            executor = requireNonNull(provider.wrap(executor), msg);
        }
        return executor;
    }

    private static final List<ExecutorWrapperProvider> EXECUTOR_WRAPPER_PROVIDERS = loadExecutorWrapperProviders();

    private static List<ExecutorWrapperProvider> loadExecutorWrapperProviders() {
        final ServiceLoader<ExecutorWrapperProvider> loader = ServiceLoader.load(ExecutorWrapperProvider.class);
        return StreamSupport.stream(loader.spliterator(), false).collect(Collectors.toList());
    }
}
