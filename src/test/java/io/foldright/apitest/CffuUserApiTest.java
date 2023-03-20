package io.foldright.apitest;

////////////////////////////////////////////////////////////////////////////////
// CompletableFutureUserApiTest and CffuUserApiTest are used to test
//   the Cffu API compatibility to CompletableFuture.
//
// CffuUserApiTest.java file is auto generated
//   from CompletableFutureUserApiTest.java
//   by script gen_CffuUserApiTest.sh
////////////////////////////////////////////////////////////////////////////////

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CffuFactoryBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;

import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.junit.jupiter.api.Assertions.*;


public class CffuUserApiTest {
    private static final CffuFactory cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(commonPool()).build();

    private static final String hello = "Cffu User API test - Hello";
    private static final RuntimeException rte = new RuntimeException("Bang");

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void factoryMethods() throws Exception {
        // completedFuture
        assertEquals(hello, cffuFactory.completedFuture(hello).get());
        // - completedStage
        // - failedFuture
        // - failedStage
        // methods is tested in below method

        // runAsync
        System.setProperty(hello, "");
        assertNull(cffuFactory.runAsync(() -> System.setProperty(hello, "42")).get());
        assertEquals("42", System.getProperty(hello));

        System.setProperty(hello, "");
        assertNull(cffuFactory.runAsync(() -> System.setProperty(hello, "43"), commonPool()).get());
        assertEquals("43", System.getProperty(hello));
        System.setProperty(hello, "");

        // supplyAsync
        assertEquals(hello, cffuFactory.supplyAsync(() -> hello).get());
        assertEquals(hello, cffuFactory.supplyAsync(() -> hello, commonPool()).get());

    }

    @Test
    void factoryMethods_Java9() throws Exception {
        // completedStage
        assertEquals(hello, cffuFactory.completedStage(hello).toCompletableFuture().get());

        // failedFuture
        try {
            cffuFactory.failedFuture(rte).get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        // failedStage
        try {
            cffuFactory.failedStage(rte).toCompletableFuture().get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void staticMethods__allOf_anyOf() throws Exception {
        Cffu<Integer> f1 = cffuFactory.completedFuture(42);
        Cffu<Integer> f2 = cffuFactory.completedFuture(4242);
        Cffu<Integer> f3 = cffuFactory.completedFuture(424242);

        assertNull(cffuFactory.allOf().get());
        cffuFactory.allOf(f1, f2).get();
        cffuFactory.allOf(f1, f2, f3).get();

        assertFalse(cffuFactory.anyOf().toCompletableFuture().isDone());
        cffuFactory.anyOf(f1, f2).get();
        cffuFactory.anyOf(f1, f2, f3).get();
    }

    @Test
    void staticMethods__delayedExecutor() {
        Executor delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS);
        cffuFactory.runAsync(System::currentTimeMillis, delayer);

        delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS, commonPool());
        cffuFactory.runAsync(System::currentTimeMillis, delayer);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Instance Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void simple_Then_Methods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.thenRun(() -> System.setProperty(hello, "42"));
        cf.thenRunAsync(() -> System.setProperty(hello, "42"));
        cf.thenRunAsync(() -> System.setProperty(hello, "42"), commonPool());

        cf.thenAccept(x -> System.setProperty(hello, "42"));
        cf.thenAcceptAsync(x -> System.setProperty(hello, "42"));
        cf.thenAcceptAsync(x -> System.setProperty(hello, "42"), commonPool());

        cf.thenApply(x -> x + 1);
        cf.thenApplyAsync(x -> x + 1);
        cf.thenApplyAsync(x -> x + 1, commonPool());
    }

    @Test
    void thenBoth_Methods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.runAfterBoth(cf, () -> System.setProperty(hello, "42"));
        cf.runAfterBothAsync(cf, () -> System.setProperty(hello, "42"));
        cf.runAfterBothAsync(cf, () -> System.setProperty(hello, "42"), commonPool());

        cf.thenAcceptBoth(cf, (x, y) -> System.setProperty(hello, "42"));
        cf.thenAcceptBothAsync(cf, (x, y) -> System.setProperty(hello, "42"));
        cf.thenAcceptBothAsync(cf, (x, y) -> System.setProperty(hello, "42"), commonPool());

        cf.thenCombine(cf, Integer::sum);
        cf.thenCombineAsync(cf, Integer::sum);
        cf.thenCombineAsync(cf, Integer::sum, commonPool());
    }

    @Test
    void thenEither_Methods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.runAfterEither(cf, () -> System.setProperty(hello, "42"));
        cf.runAfterEitherAsync(cf, () -> System.setProperty(hello, "42"));
        cf.runAfterEitherAsync(cf, () -> System.setProperty(hello, "42"), commonPool());

        cf.acceptEither(cf, x -> System.setProperty(hello, "42"));
        cf.acceptEitherAsync(cf, x -> System.setProperty(hello, "42"));
        cf.acceptEitherAsync(cf, x -> System.setProperty(hello, "42"), commonPool());

        cf.applyToEither(cf, x -> x + 1);
        cf.applyToEitherAsync(cf, x -> x + 1);
        cf.applyToEitherAsync(cf, x -> x + 1, commonPool());
    }

    @Test
    void errorHandling_methods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.exceptionally(t -> 43);
    }

    @Test
    void errorHandling_methods_Java9() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.exceptionallyAsync(t -> 43);
        cf.exceptionallyAsync(t -> 43, commonPool());
    }

    @Test
    void timeoutControl_methods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(42,
                cf.orTimeout(1, TimeUnit.SECONDS).get());
        assertEquals(42,
                cf.completeOnTimeout(43, 1, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void advancedMethods__thenCompose() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.thenCompose(x -> cffuFactory.completedStage(43));
        cf.thenComposeAsync(x -> cffuFactory.completedStage(43));
        cf.thenComposeAsync(x -> cffuFactory.completedStage(43));
    }

    @Test
    void advancedMethods__exceptionallyCompose() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.exceptionallyCompose(x -> cffuFactory.completedStage(43));
        cf.exceptionallyComposeAsync(x -> cffuFactory.completedStage(43));
        cf.exceptionallyComposeAsync(x -> cffuFactory.completedStage(43));
    }

    @Test
    void advancedMethods__handle() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.handle((x, e) -> 42);
        cf.handleAsync((x, e) -> 42);
        cf.handleAsync((x, e) -> 42, commonPool());

        cf.whenComplete((x, e) -> System.setProperty(hello, "42"));
        cf.whenCompleteAsync((x, e) -> System.setProperty(hello, "42"));
        cf.whenCompleteAsync((x, e) -> System.setProperty(hello, "42"), commonPool());
    }

    @Test
    void readExplicitlyMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = ApiTestHelper.newFailedCffu(rte);

        Integer r = cf.get();
        assertEquals(r, cf.get(1, TimeUnit.MILLISECONDS));
        assertEquals(r, cf.join());
        assertEquals(r, cf.getNow(0));
        // - resultNow
        // - exceptionNow
        // methods is tested in below method

        assertTrue(cf.isDone());
        assertTrue(failed.isCompletedExceptionally());
        assertFalse(cf.isCancelled());

        // - state
        // methods is tested in below method
    }

    @Test
    void readExplicitlyMethods_Java19() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = ApiTestHelper.newFailedCffu(rte);

        Integer r = cf.get();
        assertEquals(r, cf.resultNow());
        assertSame(rte, failed.exceptionNow());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19) /* KEEP_IN_GEN */
    void readExplicitlyMethods_Java19_CanNotCompatible() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = ApiTestHelper.newFailedCffu(rte);

        assertEquals(Future.State.SUCCESS, cf.state());
        assertEquals(Future.State.FAILED, failed.state());
    }

    @Test
    void writeMethods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        cf.complete(4242);
        // - completeAsync
        // methods is tested in below method

        assertFalse(cf.completeExceptionally(rte));

        cf.cancel(true);
    }

    @Test
    void writeMethods_Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(42, cf.completeAsync(() -> 4343).get());
        assertEquals(42, cf.completeAsync(() -> 4343, commonPool()).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //   vs. user functional API
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void nonfunctionalMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        assertEquals(42, cf.toCompletableFuture().get());

        cf.obtrudeValue(44);
        assertEquals(44, cf.get());

        cf.obtrudeException(rte);
        try {
            cf.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        assertNotEquals(-1, cf.getNumberOfDependents());
    }

    @Test
    void nonfunctionalMethods_Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(42, cf.copy().get());

        assertNotNull(cf.defaultExecutor());

        assertNotNull(cf.minimalCompletionStage());
        assertFalse(cf.newIncompleteFuture().isDone());
    }
}
