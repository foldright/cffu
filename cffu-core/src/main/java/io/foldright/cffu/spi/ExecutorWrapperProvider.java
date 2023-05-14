package io.foldright.cffu.spi;

import edu.umd.cs.findbugs.annotations.NonNull;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CffuFactoryBuilder;

import java.util.concurrent.Executor;


/**
 * An SPI for wrapping the executor when {@link CffuFactoryBuilder}
 * {@link CffuFactoryBuilder#build() build} {@link CffuFactory}.
 *
 * @see CffuFactoryBuilder#newCffuFactoryBuilder(Executor)
 * @see CffuFactoryBuilder#build()
 */
@FunctionalInterface
public interface ExecutorWrapperProvider {
    /**
     * Wraps given executor.
     */
    @NonNull
    Executor wrap(@NonNull Executor executor);
}
