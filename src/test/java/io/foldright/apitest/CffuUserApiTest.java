package io.foldright.apitest;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CffuFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.junit.jupiter.api.Assertions.*;


public class CffuUserApiTest {
    private static final CffuFactory cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(commonPool()).forbidObtrudeMethods(true).build();

    private static final String hello = "Cffu User API test - Hello";
    private static final RuntimeException rte = new RuntimeException("Bang");

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

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
        assertEquals(hello, cffuFactory.completedStage(hello).toCompletableFuture().get());

        try {
            cffuFactory.failedFuture(rte).get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            cffuFactory.failedStage(rte).toCompletableFuture().get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void factoryMethods_allOf_anyOf() throws Exception {
        Cffu<Integer> f1 = cffuFactory.completedFuture(42);
        Cffu<Integer> f2 = cffuFactory.completedFuture(4242);
        Cffu<Integer> f3 = cffuFactory.completedFuture(424242);

        cffuFactory.allOf().get();
        cffuFactory.allOf(f1, f2).get();
        cffuFactory.allOf(f1, f2, f3).get();

        assertFalse(cffuFactory.anyOf().toCompletableFuture().isDone());
        cffuFactory.anyOf(f1, f2).get();
        cffuFactory.anyOf(f1, f2, f3).get();
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void delayedExecutor() {
        Executor delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS);
        cffuFactory.runAsync(System::currentTimeMillis, delayer);

        delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS, commonPool());
        cffuFactory.runAsync(System::currentTimeMillis, delayer);
    }
}
