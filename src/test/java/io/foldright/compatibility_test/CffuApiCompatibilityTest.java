package io.foldright.compatibility_test;

////////////////////////////////////////////////////////////////////////////////
// CompletableFutureApiCompatibilityTest and CffuApiCompatibilityTest are used to test
//   the Cffu API compatibility to CompletableFuture.
//
// CffuApiCompatibilityTest.java file is auto generated
//   from CompletableFutureApiCompatibilityTest.java
//   by script gen_CffuApiCompatibilityTest.sh
////////////////////////////////////////////////////////////////////////////////

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CffuFactoryBuilder;
import io.foldright.test_utils.TestThreadPoolManager;
import io.foldright.test_utils.TestUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;


class CffuApiCompatibilityTest {
    private static final String hello = "Cffu API Compatibility Test - Hello";

    private static final RuntimeException rte = new RuntimeException("Bang");

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void factoryMethods() throws Exception {
        // completedFuture
        Cffu<String> f0 = cffuFactory.completedFuture(hello);
        assertEquals(hello, f0.get());
        TestUtils.shouldNotBeMinimalStage(f0);
        // below methods is tested in below test method
        // - completedStage
        // - failedFuture
        // - failedStage

        final AtomicReference<String> holder = new AtomicReference<>();

        // runAsync
        Cffu<Void> cf = cffuFactory.runAsync(() -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        });
        assertNull(cf.get());
        assertEquals(hello, holder.get());
        TestUtils.shouldNotBeMinimalStage(cf);

        holder.set(null);
        cf = cffuFactory.runAsync(() -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService);
        assertNull(cf.get());
        assertEquals(hello, holder.get());
        TestUtils.shouldNotBeMinimalStage(cf);

        // supplyAsync
        Cffu<String> s_cf = cffuFactory.supplyAsync(() -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return hello;
        });
        assertEquals(hello, s_cf.get());
        TestUtils.shouldNotBeMinimalStage(s_cf);
        s_cf = cffuFactory.supplyAsync(() -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return hello;
        }, anotherExecutorService);
        assertEquals(hello, s_cf.get());
        TestUtils.shouldNotBeMinimalStage(s_cf);
    }

    @Test
    void factoryMethods_Java9() throws Exception {
        // completedStage
        Cffu<String> cf = (Cffu<String>) cffuFactory.completedStage(hello);
        assertEquals(hello, cf.toCompletableFuture().get());
        TestUtils.shouldBeMinimalStage(cf);

        // failedFuture
        cf = cffuFactory.failedFuture(rte);
        try {
            cf.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        TestUtils.shouldNotBeMinimalStage(cf);

        // failedStage
        cf = (Cffu<String>) cffuFactory.<String>failedStage(rte);
        try {
            cf.toCompletableFuture().get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        TestUtils.shouldBeMinimalStage(cf);
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
    void staticMethods__delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS);
        cffuFactory.runAsync(() -> holder.set(hello), delayer).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS, executorService);
        cffuFactory.runAsync(() -> holder.set(hello), delayer).get();
        assertEquals(hello, holder.get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Instance Methods of Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void simple_Then_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        final Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.thenRun(() -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAccept(x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(43, cf.thenApply(x -> x + 1).get());
        assertEquals(44, cf.thenApplyAsync(x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return x + 2;
        }).get());
        assertEquals(45, cf.thenApplyAsync(x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return x + 3;
        }, anotherExecutorService).get());
    }

    @Test
    void thenBoth_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.runAfterBoth(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAcceptBoth(cf, (x, y) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(84, cf.thenCombine(cf, Integer::sum).get());
        assertEquals(84, cf.thenCombineAsync(cf, (a, b) -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return Integer.sum(a, b);
        }).get());
        assertEquals(84, cf.thenCombineAsync(cf, (a, b) -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return Integer.sum(a, b);
        }, anotherExecutorService).get());
    }

    @Test
    void thenEither_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        cf.runAfterEither(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.acceptEither(cf, x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(43, cf.applyToEither(cf, x -> x + 1).get());
        assertEquals(44, cf.applyToEitherAsync(cf, x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return x + 2;
        }).get());
        assertEquals(45, cf.applyToEitherAsync(cf, x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return x + 3;
        }, anotherExecutorService).get());
    }

    @Test
    void errorHandling_methods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        // exceptionally
        assertEquals(42, cf.exceptionally(t -> 43).get());
        assertEquals(43, failed.exceptionally(t -> 43).get());

        // below methods is tested in below test method
        // - exceptionallyAsync
    }

    @Test
    void errorHandling_methods_Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        assertEquals(42, cf.exceptionallyAsync(t -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return 43;
        }).get());
        assertEquals(43, failed.exceptionallyAsync(t -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return 43;
        }).get());

        assertEquals(42, cf.exceptionallyAsync(t -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return 44;
        }, anotherExecutorService).get());
        assertEquals(44, failed.exceptionallyAsync(t -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return 44;
        }, anotherExecutorService).get());
    }

    @Test
    void timeoutControl_methods() throws Exception {
        // For completed
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(42,
                cf.orTimeout(20, TimeUnit.MILLISECONDS).get());
        assertEquals(42,
                cf.completeOnTimeout(43, 20, TimeUnit.MILLISECONDS).get());

        // for incomplete
        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();
        try {
            incomplete.orTimeout(20, TimeUnit.MILLISECONDS).get();
            fail();
        } catch (ExecutionException e) {
            assertEquals(TimeoutException.class, e.getCause().getClass());
        }

        incomplete = cffuFactory.newIncompleteCffu();
        assertEquals(43, incomplete.completeOnTimeout(43, 20, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void advancedMethods__thenCompose() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(43, cf.thenCompose(x -> cffuFactory.completedFuture(43)).get());
        assertEquals(44, cf.thenComposeAsync(x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return cffuFactory.completedFuture(44);
        }).get());
        assertEquals(45, cf.thenComposeAsync(x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return cffuFactory.completedFuture(45);
        }, anotherExecutorService).get());
    }

    @Test
    void advancedMethods__exceptionallyCompose() throws Exception {
        // for success
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(42, cf.exceptionallyCompose(x -> cffuFactory.completedFuture(43)).get());
        assertEquals(42, cf.exceptionallyComposeAsync(x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return cffuFactory.completedFuture(44);
        }).get());
        assertEquals(42, cf.exceptionallyComposeAsync(x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return cffuFactory.completedFuture(45);
        }, anotherExecutorService).get());

        // for failed
        Cffu<Integer> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        assertEquals(43, failed.exceptionallyCompose(x -> cffuFactory.completedFuture(43)).get());
        assertEquals(44, failed.exceptionallyComposeAsync(x -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return cffuFactory.completedFuture(44);
        }).get());
        assertEquals(45, failed.exceptionallyComposeAsync(x -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return cffuFactory.completedFuture(45);
        }, anotherExecutorService).get());
    }

    @Test
    void advancedMethods__handle() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        assertEquals(43, cf.handle((x, e) -> 43).get());
        assertEquals(44, cf.handleAsync((x, e) -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return 44;
        }).get());
        assertEquals(45, cf.handleAsync((x, e) -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return 45;
        }, anotherExecutorService).get());

        final AtomicReference<String> holder = new AtomicReference<>();

        cf.whenComplete((x, e) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());
    }

    @Test
    void readExplicitlyMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);

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
    void readExplicitlyMethods_Java19() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);
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
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);
        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();

        // state
        assertEquals(Future.State.SUCCESS, cf.state());
        assertEquals(Future.State.FAILED, failed.state());
        assertEquals(Future.State.RUNNING, incomplete.state());
    }

    @Test
    void writeMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);
        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();

        assertFalse(cf.complete(4242));
        assertEquals(42, cf.get());

        assertTrue(incomplete.complete(4242));
        assertEquals(4242, incomplete.get());
        // below methods is tested in below test method
        // - completeAsync

        // completeExceptionally
        assertFalse(cf.completeExceptionally(rte));
        assertEquals(42, cf.get());

        incomplete = cffuFactory.newIncompleteCffu();
        assertTrue(incomplete.completeExceptionally(rte));
        try {
            incomplete.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // cancel
        assertFalse(cf.cancel(true));
        incomplete = cffuFactory.newIncompleteCffu();
        assertTrue(incomplete.cancel(true));
    }

    @Test
    void writeMethods_Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        // completeAsync
        assertEquals(42, cf.completeAsync(() -> 4343).get());
        assertEquals(42, cf.completeAsync(() -> 4343, executorService).get());

        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();
        assertEquals(4343, incomplete.completeAsync(() -> {
            TestUtils.assertDefaultRunThreadOfCffu(executorService);
            return 4343;
        }).get());
        incomplete = cffuFactory.newIncompleteCffu();
        assertEquals(4343, incomplete.completeAsync(() -> {
            TestUtils.assertRunThreadOfCffu(anotherExecutorService);
            return 4343;
        }, anotherExecutorService).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# nonfunctional methods
    //   vs. user functional API
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void nonfunctionalMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

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
    void nonfunctionalMethods_Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(42);

        // copy
        assertEquals(42, cf.copy().get());

        // defaultExecutor
        assertNotNull(cf.defaultExecutor());

        // minimalCompletionStage
        TestUtils.shouldBeMinimalStage((Cffu<Integer>) cf.minimalCompletionStage());

        // newIncompleteFuture
        assertFalse(cf.newIncompleteFuture().isDone());
    }


    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    private static CffuFactory cffuFactory;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool(hello);
        cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(executorService).build();

        anotherExecutorService = TestThreadPoolManager.createThreadPool(hello);
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService, anotherExecutorService);
    }
}
