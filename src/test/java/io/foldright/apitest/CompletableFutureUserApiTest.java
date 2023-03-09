package io.foldright.apitest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.junit.jupiter.api.Assertions.*;


public class CompletableFutureUserApiTest {
    private static final String hello = "CompletableFuture User API test - Hello";
    private static final RuntimeException rte = new RuntimeException("Bang");

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void factoryMethods() throws Exception {
        assertEquals(hello, CompletableFuture.completedFuture(hello).get());

        assertEquals(hello, CompletableFuture.supplyAsync(() -> hello).get());
        assertEquals(hello, CompletableFuture.supplyAsync(() -> hello, commonPool()).get());

        System.setProperty(hello, "");
        assertNull(CompletableFuture.runAsync(() -> System.setProperty(hello, hello)).get());
        assertEquals(hello, System.getProperty(hello));

        System.setProperty(hello, "");
        assertNull(CompletableFuture.runAsync(() -> System.setProperty(hello, hello), commonPool()).get());
        assertEquals(hello, System.getProperty(hello));
        System.setProperty(hello, "");
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void factoryMethodsOfJ9() throws Exception {
        assertEquals(hello, CompletableFuture.completedStage(hello).toCompletableFuture().get());

        try {
            CompletableFuture.failedFuture(rte).get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            CompletableFuture.failedStage(rte).toCompletableFuture().get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void factoryMethods_allOf_anyOf() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(42);
        CompletableFuture<Integer> f2 = CompletableFuture.completedFuture(4224);

        CompletableFuture.allOf(f1, f2).get();
        CompletableFuture.anyOf(f1, f2).get();
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void delayedExecutor() {
        Executor delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(System::currentTimeMillis, delayer);

        delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS, commonPool());
        CompletableFuture.runAsync(System::currentTimeMillis, delayer);
    }
}
