// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu.ttl.executor.wrapper {
    requires io.foldright.cffu;

    provides io.foldright.cffu.spi.ExecutorWrapperProvider with io.foldright.cffu.ttl.CffuTtlExecutorWrapperProvider;
}
