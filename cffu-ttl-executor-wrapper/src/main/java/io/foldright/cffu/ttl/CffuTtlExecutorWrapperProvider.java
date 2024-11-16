package io.foldright.cffu.ttl;

import com.alibaba.ttl.spi.TtlEnhanced;
import com.alibaba.ttl.threadpool.TtlExecutors;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.foldright.cffu.spi.ExecutorWrapperProvider;

import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;


/**
 * Cffu executor wrapper provider({@link ExecutorWrapperProvider}) SPI implementation for
 * <a href="https://github.com/alibaba/transmittable-thread-local">📌 TransmittableThreadLocal (TTL)</a>.
 */
public final class CffuTtlExecutorWrapperProvider implements ExecutorWrapperProvider {
    /**
     * Returns the <a href="https://github.com/alibaba/transmittable-thread-local">{@code TTL}</a>
     * executor wrapper of the given executor.
     */
    @NonNull
    @Override
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public Executor wrap(@NonNull Executor executor) {
        requireNonNull(executor, "executor is null");
        if (executor instanceof TtlEnhanced) return executor;
        return TtlExecutors.getTtlExecutor(executor);
    }
}
