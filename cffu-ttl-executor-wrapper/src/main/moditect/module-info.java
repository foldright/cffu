// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu2.ttl.executor.wrapper {
    requires io.foldright.cffu2;

    provides io.foldright.cffu2.spi.ExecutorWrapperProvider with io.foldright.cffu2.ttl.CffuTtlExecutorWrapperProvider;
}
