package io.foldright.apitest;

////////////////////////////////////////////////////////////////////////////////
// CompletableFutureUserApiTest and CffuUserApiTest are used to test
//   the Cffu API compatibility to CompletableFuture.
//
// CffuUserApiTest.java file is auto generated
//   from CompletableFutureUserApiTest.java
//   by script gen_CffuUserApiTest.sh
////////////////////////////////////////////////////////////////////////////////

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;

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
        // completedFuture
        assertEquals(hello, CompletableFuture.completedFuture(hello).get());
        // - completedStage
        // - failedFuture
        // - failedStage
        // methods is tested in below method

        // runAsync
        System.setProperty(hello, "");
        assertNull(CompletableFuture.runAsync(() -> System.setProperty(hello, "42")).get());
        assertEquals("42", System.getProperty(hello));

        System.setProperty(hello, "");
        assertNull(CompletableFuture.runAsync(() -> System.setProperty(hello, "43"), commonPool()).get());
        assertEquals("43", System.getProperty(hello));
        System.setProperty(hello, "");

        // supplyAsync
        assertEquals(hello, CompletableFuture.supplyAsync(() -> hello).get());
        assertEquals(hello, CompletableFuture.supplyAsync(() -> hello, commonPool()).get());

    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void factoryMethods_Java9() throws Exception {
        // completedStage
        assertEquals(hello, CompletableFuture.completedStage(hello).toCompletableFuture().get());

        // failedFuture
        try {
            CompletableFuture.failedFuture(rte).get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        // failedStage
        try {
            CompletableFuture.failedStage(rte).toCompletableFuture().get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void staticMethods__allOf_anyOf() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(42);
        CompletableFuture<Integer> f2 = CompletableFuture.completedFuture(4242);
        CompletableFuture<Integer> f3 = CompletableFuture.completedFuture(424242);

        assertNull(CompletableFuture.allOf().get());
        CompletableFuture.allOf(f1, f2).get();
        CompletableFuture.allOf(f1, f2, f3).get();

        assertFalse(CompletableFuture.anyOf().toCompletableFuture().isDone());
        CompletableFuture.anyOf(f1, f2).get();
        CompletableFuture.anyOf(f1, f2, f3).get();
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void staticMethods__delayedExecutor() {
        Executor delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(System::currentTimeMillis, delayer);

        delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS, commonPool());
        CompletableFuture.runAsync(System::currentTimeMillis, delayer);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Instance Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void simple_Then_Methods() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

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
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

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
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

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
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.exceptionally(t -> 43);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_12)
    void errorHandling_methods_Java9() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.exceptionallyAsync(t -> 43);
        cf.exceptionallyAsync(t -> 43, commonPool());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void timeoutControl_methods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(42,
                cf.orTimeout(1, TimeUnit.SECONDS).get());
        assertEquals(42,
                cf.completeOnTimeout(43, 1, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void advancedMethods__thenCompose() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.thenCompose(x -> CompletableFuture.completedStage(43));
        cf.thenComposeAsync(x -> CompletableFuture.completedStage(43));
        cf.thenComposeAsync(x -> CompletableFuture.completedStage(43));
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_12)
    void advancedMethods__exceptionallyCompose() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.exceptionallyCompose(x -> CompletableFuture.completedStage(43));
        cf.exceptionallyComposeAsync(x -> CompletableFuture.completedStage(43));
        cf.exceptionallyComposeAsync(x -> CompletableFuture.completedStage(43));
    }

    @Test
    void advancedMethods__handle() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.handle((x, e) -> 42);
        cf.handleAsync((x, e) -> 42);
        cf.handleAsync((x, e) -> 42, commonPool());

        cf.whenComplete((x, e) -> System.setProperty(hello, "42"));
        cf.whenCompleteAsync((x, e) -> System.setProperty(hello, "42"));
        cf.whenCompleteAsync((x, e) -> System.setProperty(hello, "42"), commonPool());
    }

    @Test
    void readExplicitlyMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = ApiTestHelper.newFailedCompletableFuture(rte);

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
    @EnabledForJreRange(min = JRE.JAVA_19)
    void readExplicitlyMethods_Java19() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = ApiTestHelper.newFailedCompletableFuture(rte);

        Integer r = cf.get();
        assertEquals(r, cf.resultNow());
        assertSame(rte, failed.exceptionNow());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19) /* KEEP_IN_GEN */
    void readExplicitlyMethods_Java19_CanNotCompatible() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = ApiTestHelper.newFailedCompletableFuture(rte);

        assertEquals(Future.State.SUCCESS, cf.state());
        assertEquals(Future.State.FAILED, failed.state());
    }

    @Test
    void writeMethods() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        cf.complete(4242);
        // - completeAsync
        // methods is tested in below method

        cf.completeExceptionally(rte);

        cf.cancel(true);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void writeMethods_Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(42, cf.completeAsync(() -> 4343).get());
        assertEquals(42, cf.completeAsync(() -> 4343, commonPool()).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //   vs. user functional API
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void nonfunctionalMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
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
    @EnabledForJreRange(min = JRE.JAVA_9)
    void nonfunctionalMethods_Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(42, cf.copy().get());

        assertNotNull(cf.defaultExecutor());

        assertNotNull(cf.minimalCompletionStage());
        assertFalse(cf.newIncompleteFuture().isDone());
    }
}
