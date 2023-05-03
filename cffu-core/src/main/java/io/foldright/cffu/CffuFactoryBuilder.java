package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import io.foldright.cffu.spi.ExecutorWrapper;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.ThreadSafe;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Objects.requireNonNull;


/**
 * {@link CffuFactoryBuilder} is the builder of {@link CffuFactory}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see CffuFactory
 * @see Cffu
 */
@ThreadSafe
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public final class CffuFactoryBuilder {
    @NonNull
    private final Executor defaultExecutor;

    private volatile boolean forbidObtrudeMethods = false;

    private CffuFactoryBuilder(Executor defaultExecutor) {
        this.defaultExecutor = requireNonNull(defaultExecutor, "defaultExecutor is null");
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Builder Methods
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * Returns a {@link CffuFactoryBuilder} with {@code defaultExecutor} setting.
     *
     * @see CffuFactory#defaultExecutor()
     * @see Cffu#defaultExecutor()
     */
    @Contract(pure = true)
    public static CffuFactoryBuilder newCffuFactoryBuilder(Executor defaultExecutor) {
        return new CffuFactoryBuilder(requireNonNull(defaultExecutor, "defaultExecutor is null"));
    }

    /**
     * Sets {@code forbidObtrudeMethods} or not.
     *
     * @see CffuFactory#forbidObtrudeMethods()
     * @see Cffu#obtrudeValue(Object)
     * @see Cffu#obtrudeException(Throwable)
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
        return new CffuFactory(wrapExecutor(), forbidObtrudeMethods);
    }

    private Executor wrapExecutor() {
        Executor executor = defaultExecutor;
        for (ExecutorWrapper wrapper : executorWrappers) {
            executor = wrapper.wrap(executor);
        }
        return executor;
    }

    private static final List<ExecutorWrapper> executorWrappers = loadExecutorWrappers();

    private static List<ExecutorWrapper> loadExecutorWrappers() {
        final ServiceLoader<ExecutorWrapper> loader = ServiceLoader.load(ExecutorWrapper.class);
        return StreamSupport.stream(loader.spliterator(), false).collect(Collectors.toList());
    }
}
