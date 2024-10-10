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
    ////////////////////////////////////////////////////////////////////////////////

    private final Executor defaultExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    CffuFactoryBuilder(Executor defaultExecutor) {
        this.defaultExecutor = makeExecutor(defaultExecutor);
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
    // region# Internal helper methods and fields
    ////////////////////////////////////////////////////////////////////////////////

    @Contract(pure = true)
    static CffuFactory resetDefaultExecutor(CffuFactory fac, Executor defaultExecutor) {
        return new CffuFactory(makeExecutor(defaultExecutor), fac.forbidObtrudeMethods());
    }

    private static Executor makeExecutor(Executor executor) {
        // check CffuMadeExecutor interface to avoid re-wrapping.
        if (executor instanceof CffuMadeExecutor) return executor;

        Executor wrapByProviders = wrapExecutorByProviders(CompletableFutureUtils.screenExecutor(executor));
        return wrapMadeInterface(wrapByProviders);
    }

    private static CffuMadeExecutor wrapMadeInterface(Executor executor) {
        return new CffuMadeExecutor() {
            @Override
            public void execute(Runnable command) {
                executor.execute(command);
            }

            @Override
            public Executor unwrap() {
                return executor;
            }

            @Override
            public String toString() {
                return "CffuMadeExecutor of executor(" + executor + ")";
            }
        };
    }

    /**
     * An interface for avoiding re-wrapping.
     */
    @VisibleForTesting
    interface CffuMadeExecutor extends Executor {
        @VisibleForTesting
        Executor unwrap();
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
