// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu.kotlin {
    requires transitive io.foldright.cffu;
    requires transitive kotlin.stdlib.jdk8;

    exports io.foldright.cffu.kotlin;
}
