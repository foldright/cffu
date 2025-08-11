// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu2 {
    requires java.logging;
    requires static org.slf4j;

    exports io.foldright.cffu2;
    exports io.foldright.cffu2.eh;
    exports io.foldright.cffu2.tuple;
    exports io.foldright.cffu2.spi;

    uses io.foldright.cffu2.spi.ExecutorWrapperProvider;
}
