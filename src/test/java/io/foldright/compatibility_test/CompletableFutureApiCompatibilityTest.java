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
import io.foldright.test_utils.TestUtils;
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
    // Static Methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    //# Factory Methods of CompletableFuture
    ////////////////////////////////////////

    @Test
    void factoryMethods_byImmediateValue() throws Exception {
        // completedFuture
        CompletableFuture<String> f0 = CompletableFuture.completedFuture(hello);
        assertEquals(hello, f0.get());
        TestUtils.shouldNotBeMinimalStage(f0);
        // below methods is tested in below test method
        // - completedStage
        // - failedFuture
        // - failedStage
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void factoryMethods_byImmediateValue__Java9() throws Exception {
        // completedStage
        CompletableFuture<String> cf = (CompletableFuture<String>) CompletableFuture.completedStage(hello);
        assertEquals(hello, cf.toCompletableFuture().get());
        TestUtils.shouldBeMinimalStage(cf);

        // failedFuture
        cf = CompletableFuture.failedFuture(rte);
        try {
            cf.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        TestUtils.shouldNotBeMinimalStage(cf);

        // failedStage
        cf = (CompletableFuture<String>) CompletableFuture.<String>failedStage(rte);
        try {
            cf.toCompletableFuture().get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        TestUtils.shouldBeMinimalStage(cf);
    }

    @Test
    void factoryMethods_byLambda() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        // runAsync
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        });
        assertNull(cf.get());
        assertEquals(hello, holder.get());
        TestUtils.shouldNotBeMinimalStage(cf);

        holder.set(null);
        cf = CompletableFuture.runAsync(() -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService);
        assertNull(cf.get());
        assertEquals(hello, holder.get());
        TestUtils.shouldNotBeMinimalStage(cf);

        // supplyAsync
        CompletableFuture<String> s_cf = CompletableFuture.supplyAsync(() -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return hello;
        });
        assertEquals(hello, s_cf.get());
        TestUtils.shouldNotBeMinimalStage(s_cf);
        s_cf = CompletableFuture.supplyAsync(() -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return hello;
        }, anotherExecutorService);
        assertEquals(hello, s_cf.get());
        TestUtils.shouldNotBeMinimalStage(s_cf);
    }

    @Test
    void constructor() throws Exception {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        assertFalse(cf.isDone());

        cf.complete(42);
        assertEquals(42, cf.get());
    }

    ////////////////////////////////////////
    //# allOf / anyOf methods
    ////////////////////////////////////////

    @Test
    void staticMethods_allOf_anyOf() throws Exception {
        CompletableFuture<Integer> f1 = CompletableFuture.completedFuture(42);
        CompletableFuture<Integer> f2 = CompletableFuture.completedFuture(4242);
        CompletableFuture<Integer> f3 = CompletableFuture.completedFuture(424242);

        assertNull(CompletableFuture.allOf().get());
        assertNull(CompletableFuture.allOf(f1, f2).get());
        assertNull(CompletableFuture.allOf(f1, f2, f3).get());

        assertFalse(CompletableFuture.anyOf().toCompletableFuture().isDone());
        assertNotNull(CompletableFuture.anyOf(f1, f2).get());
        assertNotNull(CompletableFuture.anyOf(f1, f2, f3).get());
    }

    ////////////////////////////////////////
    //# Delay Execution
    ////////////////////////////////////////

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void staticMethods_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }, delayer).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        delayer = CompletableFuture.delayedExecutor(1, TimeUnit.MILLISECONDS, anotherExecutorService);
        CompletableFuture.runAsync(() -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, delayer).get();
        assertEquals(hello, holder.get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Instance Methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void simpleThenMethods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.thenRun(() -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAccept(x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(43, cf.thenApply(x -> x + 1).get());
        assertEquals(44, cf.thenApplyAsync(x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return x + 2;
        }).get());
        assertEquals(45, cf.thenApplyAsync(x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return x + 3;
        }, anotherExecutorService).get());
    }

    @Test
    void thenBoth_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.runAfterBoth(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAcceptBoth(cf, (x, y) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(84, cf.thenCombine(cf, Integer::sum).get());
        assertEquals(84, cf.thenCombineAsync(cf, (a, b) -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return Integer.sum(a, b);
        }).get());
        assertEquals(84, cf.thenCombineAsync(cf, (a, b) -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return Integer.sum(a, b);
        }, anotherExecutorService).get());
    }

    @Test
    void thenEither_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        cf.runAfterEither(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.acceptEither(cf, x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(43, cf.applyToEither(cf, x -> x + 1).get());
        assertEquals(44, cf.applyToEitherAsync(cf, x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return x + 2;
        }).get());
        assertEquals(45, cf.applyToEitherAsync(cf, x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return x + 3;
        }, anotherExecutorService).get());
    }

    @Test
    void errorHandling_methods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = TestUtils.safeNewFailedCompletableFuture(executorService, rte);

        // exceptionally
        assertEquals(42, cf.exceptionally(t -> 43).get());
        assertEquals(43, failed.exceptionally(t -> 43).get());

        // below methods is tested in below test method
        // - exceptionallyAsync
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_12)
    void errorHandling_methods__Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = TestUtils.safeNewFailedCompletableFuture(executorService, rte);

        assertEquals(42, cf.exceptionallyAsync(t -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return 43;
        }).get());
        assertEquals(43, failed.exceptionallyAsync(t -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return 43;
        }).get());

        assertEquals(42, cf.exceptionallyAsync(t -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return 44;
        }, anotherExecutorService).get());
        assertEquals(44, failed.exceptionallyAsync(t -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return 44;
        }, anotherExecutorService).get());
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
        assertEquals(44, cf.thenComposeAsync(x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return CompletableFuture.completedFuture(44);
        }).get());
        assertEquals(45, cf.thenComposeAsync(x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return CompletableFuture.completedFuture(45);
        }, anotherExecutorService).get());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_12)
    void advancedMethods__exceptionallyCompose() throws Exception {
        // for success
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(42, cf.exceptionallyCompose(x -> CompletableFuture.completedFuture(43)).get());
        assertEquals(42, cf.exceptionallyComposeAsync(x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return CompletableFuture.completedFuture(44);
        }).get());
        assertEquals(42, cf.exceptionallyComposeAsync(x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return CompletableFuture.completedFuture(45);
        }, anotherExecutorService).get());

        // for failed
        CompletableFuture<Integer> failed = TestUtils.safeNewFailedCompletableFuture(executorService, rte);

        assertEquals(43, failed.exceptionallyCompose(x -> CompletableFuture.completedFuture(43)).get());
        assertEquals(44, failed.exceptionallyComposeAsync(x -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return CompletableFuture.completedFuture(44);
        }).get());
        assertEquals(45, failed.exceptionallyComposeAsync(x -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return CompletableFuture.completedFuture(45);
        }, anotherExecutorService).get());
    }

    @Test
    void advancedMethods__handle_whenComplete() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        assertEquals(43, cf.handle((x, e) -> 43).get());
        assertEquals(44, cf.handleAsync((x, e) -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return 44;
        }).get());
        assertEquals(45, cf.handleAsync((x, e) -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return 45;
        }, anotherExecutorService).get());

        final AtomicReference<String> holder = new AtomicReference<>();

        cf.whenComplete((x, e) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());
    }

    @Test
    void readExplicitlyMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = TestUtils.safeNewFailedCompletableFuture(executorService, rte);

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
    void readExplicitlyMethods__Java19() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = TestUtils.safeNewFailedCompletableFuture(executorService, rte);
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
    void readExplicitlyMethods__Java19_CanNotCompatible() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);
        CompletableFuture<Object> failed = TestUtils.safeNewFailedCompletableFuture(executorService, rte);
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
    void writeMethods__Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // completeAsync
        assertEquals(42, cf.completeAsync(() -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return 4242;
        }).get());
        assertEquals(42, cf.completeAsync(() -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return 424242;
        }, anotherExecutorService).get());

        CompletableFuture<Integer> incomplete = new CompletableFuture<>();
        assertEquals(4242, incomplete.completeAsync(() -> {
            TestUtils.assertCompletableFutureRunInDefaultThread(executorService);
            return 4242;
        }).get());

        incomplete = new CompletableFuture<>();
        assertEquals(424242, incomplete.completeAsync(() -> {
            TestUtils.assertCompletableFutureRunInThreadOf(anotherExecutorService);
            return 424242;
        }, anotherExecutorService).get());
    }

    ////////////////////////////////////////
    //# Re-Config methods
    ////////////////////////////////////////

    @Test
    void reConfigMethods() throws Exception {
        // below methods is tested in below test method
        // - minimalCompletionStage
        // - copy

        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // toCompletableFuture
        assertEquals(42, cf.toCompletableFuture().get());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void reConfigMethods__Java9() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // minimalCompletionStage
        TestUtils.shouldBeMinimalStage((CompletableFuture<Integer>) cf.minimalCompletionStage());

        // copy
        assertEquals(42, cf.copy().get());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_getterMethods() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // defaultExecutor
        assertNotNull(cf.defaultExecutor());
    }

    @Test
    void test_inspectionMethods() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // getNumberOfDependents
        assertNotEquals(-1, cf.getNumberOfDependents());
    }

    @Test
    void test_dangerousMethods() throws Exception {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

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
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_forApiCompatibility() {
        CompletableFuture<Integer> cf = CompletableFuture.completedFuture(42);

        // newIncompleteFuture
        assertFalse(cf.newIncompleteFuture().isDone());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# the behavior that is easy to misunderstand
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_similarities_and_differences_between_cancelled_and_exceptionally() throws Exception {
        final CompletableFuture<Integer> cancelledCf = new CompletableFuture<>();
        cancelledCf.cancel(false);

        final CompletableFuture<Integer> exceptionallyCf =
                TestUtils.safeNewFailedCompletableFuture(executorService, rte);

        ////////////////////////////////////////
        // different behavior
        ////////////////////////////////////////

        try {
            cancelledCf.get();
            fail();
        } catch (CancellationException expected) {
        }
        try {
            exceptionallyCf.get();
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cancelledCf.get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (CancellationException expected) {
        }
        try {
            exceptionallyCf.get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cancelledCf.join();
            fail();
        } catch (CancellationException expected) {
        }
        try {
            exceptionallyCf.join();
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cancelledCf.getNow(42);
            fail();
        } catch (CancellationException expected) {
        }
        try {
            exceptionallyCf.getNow(42);
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }

        assertTrue(cancelledCf.isCancelled());
        assertFalse(exceptionallyCf.isCancelled());

        ////////////////////////////////////////
        // same behavior
        ////////////////////////////////////////

        assertTrue(cancelledCf.isCompletedExceptionally());
        assertTrue(exceptionallyCf.isCompletedExceptionally());

        assertTrue(cancelledCf.isDone());
        assertTrue(exceptionallyCf.isDone());

        assertEquals(42, cancelledCf.exceptionally(throwable -> {
            assertInstanceOf(CancellationException.class, throwable);
            assertNull(throwable.getCause());
            return 42;
        }).get());
        assertEquals(42, exceptionallyCf.exceptionally(throwable -> {
            assertSame(rte, throwable);
            return 42;
        }).get());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19)
    void test_similarities_and_differences_between_cancelled_and_exceptionally__Java19() throws Exception {
        final CompletableFuture<Integer> cancelledCf = new CompletableFuture<>();
        cancelledCf.cancel(false);

        final CompletableFuture<Integer> exceptionallyCf =
                TestUtils.safeNewFailedCompletableFuture(executorService, rte);

        ////////////////////////////////////////
        // different behavior
        ////////////////////////////////////////

        try {
            //noinspection ThrowableNotThrown
            cancelledCf.exceptionNow();
            fail();
        } catch (IllegalStateException expected) {
        }
        assertSame(rte, exceptionallyCf.exceptionNow());

        ////////////////////////////////////////
        // same behavior
        ////////////////////////////////////////

        try {
            cancelledCf.resultNow();
            fail();
        } catch (IllegalStateException expected) {
        }
        try {
            exceptionallyCf.resultNow();
            fail();
        } catch (IllegalStateException expected) {
        }
    }

    @Test
    void nested_exception() throws Exception {
        CompletableFuture<Integer> cf = new CompletableFuture<>();
        cf.completeExceptionally(new CompletionException(rte));
        // auto unwrap first level CompletionException
        checkNo1MoreLevelForCompletionException(cf);

        cf = CompletableFuture.completedFuture(42);
        // auto unwrap first level CompletionException
        checkNo1MoreLevelForCompletionException(cf.thenRun(() -> {
            throw new CompletionException(rte);
        }));

        cf = new CompletableFuture<>();
        cf.completeExceptionally(new ExecutionException(rte));
        // auto unwrap first level ExecutionException
        check1MoreLevelForExecutionException(cf);

        cf = CompletableFuture.completedFuture(42);
        // auto unwrap first level ExecutionException
        check1MoreLevelForExecutionException(cf.thenRun(() -> {
            TestUtils.sneakyThrow(new ExecutionException(rte));
        }));
    }

    private <T> void checkNo1MoreLevelForCompletionException(CompletableFuture<T> cf) throws Exception {
        try {
            cf.get();
            fail();
        } catch (ExecutionException e) {
            assertSame(rte, e.getCause());
        }
        try {
            cf.join();
            fail();
        } catch (CompletionException e) {
            assertSame(rte, e.getCause());
        }
    }

    private <T> void check1MoreLevelForExecutionException(CompletableFuture<T> cf) throws Exception {
        try {
            cf.get();
            fail();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            assertInstanceOf(ExecutionException.class, cause);
            assertSame(rte, cause.getCause());
        }
        try {
            cf.join();
            fail();
        } catch (CompletionException e) {
            Throwable cause = e.getCause();
            assertInstanceOf(ExecutionException.class, cause);
            assertSame(rte, cause.getCause());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    /* GEN_MARK_FACTORY_FIELD */

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool(hello);
        /* GEN_MARK_FACTORY_INIT */

        anotherExecutorService = TestThreadPoolManager.createThreadPool(hello, true);
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService, anotherExecutorService);
    }
}
