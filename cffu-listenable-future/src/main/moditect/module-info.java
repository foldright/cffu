// https://www.oracle.com/corporate/features/understanding-java-9-modules.html

module io.foldright.cffu.lf {
    requires transitive io.foldright.cffu;
    requires transitive com.google.common;
    requires static kotlin.stdlib.jdk8;

    exports io.foldright.cffu.lf;
    exports io.foldright.cffu.lf.kotlin;
}
