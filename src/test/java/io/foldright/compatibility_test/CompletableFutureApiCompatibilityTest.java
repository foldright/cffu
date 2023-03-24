package io.foldright.compatibility_test;

////////////////////////////////////////////////////////////////////////////////
// CompletableFutureApiCompatibilityTest and CffuApiCompatibilityTest are used to test
//   the Cffu API compatibility to CompletableFuture.
//
// CffuApiCompatibilityTest.java file is auto generated
//   from CompletableFutureApiCompatibilityTest.java
//   by script gen_CffuApiCompatibilityTest.sh
////////////////////////////////////////////////////////////////////////////////

import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class CompletableFutureApiCompatibilityTest {
    private static final String hello = "CompletableFuture API Compatibility Test - Hello";

    private static final RuntimeException rte = new RuntimeException("Bang");

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void factoryMethods() throws Exception {
        // completedFuture
        assertEquals(hello, CompletableFuture.completedFuture(hello).get());
        // below methods is tested in below test method
        // - completedStage
        // - failedFuture
        // - failedStage

        final AtomicReference<String> holder = new AtomicReference<>();

        // runAsync
        assertNull(CompletableFuture.runAsync(() -> holder.set(hello)).get());
        assertEquals(hello, holder.get());

        holder.set(null);
        assertNull(CompletableFuture.runAsync(() -> holder.set(hello), executorService).get());
        assertEquals(hello, holder.get());

        // supplyAsync
        assertEquals(hello, CompletableFuture.supplyAsync(() -> hello).get());
        assertEquals(hello, CompletableFuture.supplyAsync(() -> hello, executorService).get());

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
    void staticMethods__delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> holder.set(hello), delayer).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS, executorService);
        CompletableFuture.runAsync(() -> holder.set(hello), delayer).get();
        assertEquals(hello, holder.get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Instance Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void simple_Then_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.thenRun(() -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAccept(x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());

        assertEquals(43, cf.thenApply(x -> x + 1).get());
        assertEquals(44, cf.thenApplyAsync(x -> x + 2).get());
        assertEquals(45, cf.thenApplyAsync(x -> x + 3, executorService).get());
    }

    @Test
    void thenBoth_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.runAfterBoth(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAcceptBoth(cf, (x, y) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());

        assertEquals(84, cf.thenCombine(cf, Integer::sum).get());
        assertEquals(84, cf.thenCombineAsync(cf, Integer::sum).get());
        assertEquals(84, cf.thenCombineAsync(cf, Integer::sum, executorService).get());
    }

    @Test
    void thenEither_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.runAfterEither(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.acceptEither(cf, x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());

        assertEquals(43, cf.applyToEither(cf, x -> x + 1).get());
        assertEquals(44, cf.applyToEitherAsync(cf, x -> x + 2).get());
        assertEquals(45, cf.applyToEitherAsync(cf, x -> x + 3, executorService).get());
    }

    @Test
    void errorHandling_methods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = CompatibilityTestHelper.newFailedCompletableFuture(rte);

        // exceptionally
        assertEquals(42, cf.exceptionally(t -> 43).get());
        assertEquals(43, failed.exceptionally(t -> 43).get());


        // below methods is tested in below test method
        // - exceptionallyAsync
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_12)
    void errorHandling_methods_Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = CompatibilityTestHelper.newFailedCompletableFuture(rte);

        assertEquals(42, cf.exceptionallyAsync(t -> 43).get());
        assertEquals(43, failed.exceptionallyAsync(t -> 43).get());

        assertEquals(42, cf.exceptionallyAsync(t -> 44, executorService).get());
        assertEquals(44, failed.exceptionallyAsync(t -> 44, executorService).get());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void timeoutControl_methods() throws Exception {
        // For completed
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(42,
                cf.orTimeout(20, TimeUnit.MILLISECONDS).get());
        assertEquals(42,
                cf.completeOnTimeout(43, 20, TimeUnit.MILLISECONDS).get());

        // for incomplete
        CompletableFuture<Integer> incomplete = new CompletableFuture<>();
        try {
            incomplete.orTimeout(20, TimeUnit.MILLISECONDS).get();
            fail();
        } catch (ExecutionException e) {
            assertEquals(TimeoutException.class, e.getCause().getClass());
        }

        incomplete = new CompletableFuture<>();
        assertEquals(43, incomplete.completeOnTimeout(43, 20, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void advancedMethods__thenCompose() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(43, cf.thenCompose(x -> CompletableFuture.completedFuture(43)).get());
        assertEquals(44, cf.thenComposeAsync(x -> CompletableFuture.completedFuture(44)).get());
        assertEquals(45, cf.thenComposeAsync(x -> CompletableFuture.completedFuture(45), executorService).get());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_12)
    void advancedMethods__exceptionallyCompose() throws Exception {
        // for success
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(42, cf.exceptionallyCompose(x -> CompletableFuture.completedFuture(43)).get());
        assertEquals(42, cf.exceptionallyComposeAsync(x -> CompletableFuture.completedFuture(44)).get());
        assertEquals(42, cf.exceptionallyComposeAsync(x -> CompletableFuture.completedFuture(45)).get());

        // for failed
        CompletableFuture<Integer> failed = CompatibilityTestHelper.newFailedCompletableFuture(rte);

        assertEquals(43, failed.exceptionallyCompose(x -> CompletableFuture.completedFuture(43)).get());
        assertEquals(44, failed.exceptionallyComposeAsync(x -> CompletableFuture.completedFuture(44)).get());
        assertEquals(45, failed.exceptionallyComposeAsync(x -> CompletableFuture.completedFuture(45)).get());
    }

    @Test
    void advancedMethods__handle() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(43, cf.handle((x, e) -> 43).get());
        assertEquals(44, cf.handleAsync((x, e) -> 44).get());
        assertEquals(45, cf.handleAsync((x, e) -> 45, executorService).get());

        final AtomicReference<String> holder = new AtomicReference<>();

        cf.whenComplete((x, e) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> holder.set(hello), executorService).get();
        assertEquals(hello, holder.get());
    }

    @Test
    void readExplicitlyMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = CompatibilityTestHelper.newFailedCompletableFuture(rte);

        Integer r = cf.get();
        assertEquals(42, r);

        try {
            failed.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        assertEquals(r, cf.get(1, TimeUnit.MILLISECONDS));
        try {
            failed.get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        assertEquals(r, cf.join());
        try {
            failed.join();
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }
        assertEquals(r, cf.getNow(0));
        try {
            failed.getNow(0);
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }
        // below methods is tested in below test method
        // - resultNow
        // - exceptionNow

        assertTrue(cf.isDone());
        assertTrue(failed.isDone());

        assertFalse(cf.isCompletedExceptionally());
        assertTrue(failed.isCompletedExceptionally());

        assertFalse(cf.isCancelled());
        assertFalse(failed.isCancelled());

        // below methods is tested in below test method
        // - state
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19)
    void readExplicitlyMethods_Java19() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = CompatibilityTestHelper.newFailedCompletableFuture(rte);
        Integer r = cf.get();
        assertEquals(42, r);

        // resultNow
        assertEquals(r, cf.resultNow());
        try {
            failed.resultNow();
            fail();
        } catch (IllegalStateException expected) {
            // do nothing
        }

        // exceptionNow
        try {
            Throwable t = cf.exceptionNow();
            fail(t);
        } catch (IllegalStateException expected) {
            // do nothing
        }
        assertSame(rte, failed.exceptionNow());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19) /* GEN_MARK_KEEP */
    void readExplicitlyMethods_Java19_CanNotCompatible() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = CompatibilityTestHelper.newFailedCompletableFuture(rte);
        CompletableFuture<Integer> incomplete = new CompletableFuture<>();

        // state
        assertEquals(Future.State.SUCCESS, cf.state());
        assertEquals(Future.State.FAILED, failed.state());
        assertEquals(Future.State.RUNNING, incomplete.state());
    }

    @Test
    void writeMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Integer> incomplete = new CompletableFuture<>();

        assertFalse(cf.complete(4242));
        assertEquals(42, cf.get());

        assertTrue(incomplete.complete(4242));
        assertEquals(4242, incomplete.get());
        // below methods is tested in below test method
        // - completeAsync

        // completeExceptionally
        assertFalse(cf.completeExceptionally(rte));
        assertEquals(42, cf.get());

        incomplete = new CompletableFuture<>();
        assertTrue(incomplete.completeExceptionally(rte));
        try {
            incomplete.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // cancel
        assertFalse(cf.cancel(true));
        incomplete = new CompletableFuture<>();
        assertTrue(incomplete.cancel(true));
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void writeMethods_Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // completeAsync
        assertEquals(42, cf.completeAsync(() -> 4343).get());
        assertEquals(42, cf.completeAsync(() -> 4343, executorService).get());

        CompletableFuture<Integer> incomplete = new CompletableFuture<>();
        assertEquals(4343, incomplete.completeAsync(() -> 4343).get());
        incomplete = new CompletableFuture<>();
        assertEquals(4343, incomplete.completeAsync(() -> 4343, executorService).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //   vs. user functional API
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void nonfunctionalMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // toCompletableFuture
        assertEquals(42, cf.toCompletableFuture().get());

        // obtrudeValue
        cf.obtrudeValue(44);
        assertEquals(44, cf.get());

        // obtrudeException
        cf.obtrudeException(rte);
        try {
            cf.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // getNumberOfDependents
        assertNotEquals(-1, cf.getNumberOfDependents());

        // below methods is tested in below test method
        // - copy
        // - defaultExecutor
        // - minimalCompletionStage
        // - newIncompleteFuture
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void nonfunctionalMethods_Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // copy
        assertEquals(42, cf.copy().get());

        // defaultExecutor
        assertNotNull(cf.defaultExecutor());

        // minimalCompletionStage
        assertNotNull(cf.minimalCompletionStage());

        // newIncompleteFuture
        assertFalse(cf.newIncompleteFuture().isDone());
    }

    private static ExecutorService executorService;

    /* GEN_MARK_FACTORY_FIELD */

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool(hello);
        /* GEN_MARK_FACTORY_INIT */
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
