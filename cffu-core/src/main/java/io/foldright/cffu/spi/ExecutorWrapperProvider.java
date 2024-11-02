package io.foldright.cffu.spi;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CffuFactoryBuilder;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.Executor;


/**
 * An SPI for wrapping the executor when {@link CffuFactoryBuilder}
 * {@link CffuFactoryBuilder#build() build} {@link CffuFactory}.
 *
 * @see CffuFactory#builder(Executor)
 * @see CffuFactoryBuilder#build()
 */
@ApiStatus.OverrideOnly
@FunctionalInterface
public interface ExecutorWrapperProvider {
    /**
     * Returns the executor wrapper of the given executor.
     */
    @NonNull
    Executor wrap(@NonNull Executor executor);
}
