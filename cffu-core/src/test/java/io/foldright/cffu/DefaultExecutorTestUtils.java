package io.foldright.cffu;

import java.util.concurrent.Executor;


public class DefaultExecutorTestUtils {
    public static Executor unwrapMadeExecutor(CffuFactory factory) {
        final CffuFactoryBuilder.CffuMadeExecutor executor = (CffuFactoryBuilder.CffuMadeExecutor) factory.defaultExecutor();
        return executor.unwrap();
    }

    public static Executor unwrapMadeExecutor(Cffu<?> cffu) {
        final CffuFactoryBuilder.CffuMadeExecutor executor = (CffuFactoryBuilder.CffuMadeExecutor) cffu.defaultExecutor();
        return executor.unwrap();
    }
}
