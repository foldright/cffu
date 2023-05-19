// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu {
    requires transitive io.foldright.cffu.utils;

    exports io.foldright.cffu;
    exports io.foldright.cffu.spi;

    uses io.foldright.cffu.spi.ExecutorWrapperProvider;
}
