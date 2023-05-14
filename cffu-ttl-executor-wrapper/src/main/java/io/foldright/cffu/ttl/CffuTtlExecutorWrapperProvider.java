package io.foldright.cffu.ttl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.foldright.cffu.spi.ExecutorWrapperProvider;

import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;


/**
 * cffu executor wrapper provider({@link ExecutorWrapperProvider}) SPI implementation for TTL.
 */
public class CffuTtlExecutorWrapperProvider implements ExecutorWrapperProvider {
    @NonNull
    @Override
    public Executor wrap(@NonNull Executor executor) {
        return TtlExecutors.getTtlExecutor(requireNonNull(executor, "executor is null"));
    }
}
