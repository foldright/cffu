package io.foldright.cffu.ttl;

import com.alibaba.ttl.threadpool.TtlExecutors;
import edu.umd.cs.findbugs.annotations.NonNull;
import io.foldright.cffu.spi.ExecutorWrapper;

import java.util.concurrent.Executor;

import static java.util.Objects.requireNonNull;


/**
 * cffu executor wrapper({@link ExecutorWrapper}) SPI implementation for TTL.
 */
public class CffuTtlExecutorWrapper implements ExecutorWrapper {
    @NonNull
    @Override
    public Executor wrap(@NonNull Executor executor) {
        return TtlExecutors.getTtlExecutor(requireNonNull(executor, "executor is null"));
    }
}
