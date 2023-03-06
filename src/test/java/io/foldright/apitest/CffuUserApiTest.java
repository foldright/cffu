package io.foldright.apitest;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CffuFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.junit.jupiter.api.Assertions.*;

public class CffuUserApiTest {
    private static final CffuFactory cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder().defaultExecutor(commonPool()).forbidObtrudeMethods(true).build();

    private static final String hello = "CffuUserApiTest-Hello";
    private static final RuntimeException rte = new RuntimeException("Bang");

    @Test
    void factoryMethods() throws Exception {
        assertEquals(hello, cffuFactory.completedFuture(hello).get());

        assertEquals(hello, cffuFactory.supplyAsync(() -> hello).get());
        assertEquals(hello, cffuFactory.supplyAsync(() -> hello, commonPool()).get());

        System.setProperty(hello, "");
        assertNull(cffuFactory.runAsync(() -> System.setProperty(hello, hello)).get());
        assertEquals(hello, System.getProperty(hello));

        System.setProperty(hello, "");
        assertNull(cffuFactory.runAsync(() -> System.setProperty(hello, hello), commonPool()).get());
        assertEquals(hello, System.getProperty(hello));
        System.setProperty(hello, "");
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void factoryMethodsOfJ9() throws Exception {
        try {
            cffuFactory.failedFuture(rte).get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void factoryMethods_allOf_anyOf() throws Exception {
        Cffu<Integer> f1 = cffuFactory.completedFuture(42);
        Cffu<Integer> f2 = cffuFactory.completedFuture(4224);

        cffuFactory.allOf(f1, f2).get();
        cffuFactory.anyOf(f1, f2).get();
    }
}
