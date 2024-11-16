// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu {
    requires java.logging;
    requires static org.slf4j;

    exports io.foldright.cffu;
    exports io.foldright.cffu.tuple;
    exports io.foldright.cffu.spi;

    uses io.foldright.cffu.spi.ExecutorWrapperProvider;
}
