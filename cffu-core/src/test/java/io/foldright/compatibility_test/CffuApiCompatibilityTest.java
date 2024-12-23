package io.foldright.compatibility_test;

import io.foldright.test_utils.MinStageTestUtils;
import io.foldright.test_utils.TestUtils;
import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.test_utils.TestingExecutorUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static io.foldright.test_utils.TestingConstants.*;
import static org.junit.jupiter.api.Assertions.*;


/**
 * {@link CompletableFutureApiCompatibilityTest} and {@link CffuApiCompatibilityTest} are used to
 * check the Cffu <strong>API compatibility</strong> with CompletableFuture.
 * <p>
 * {@link CffuApiCompatibilityTest} file is auto generated from {@link CompletableFutureApiCompatibilityTest} file
 * by script {@code scripts/gen_CffuApiCompatibilityTest.sh}.
 */
@SuppressWarnings("RedundantThrows")
class CffuApiCompatibilityTest {
    private static final String hello = "Cffu API Compatibility Test - Hello";

    ////////////////////////////////////////////////////////////////////////////////
    // Static Methods
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////
    //# Factory Methods of Cffu
    ////////////////////////////////////////

    @Test
    void factoryMethods_byImmediateValue() throws Exception {
        // completedFuture
        Cffu<String> f0 = cffuFactory.completedFuture(hello);
        assertEquals(hello, f0.get());
        MinStageTestUtils.shouldNotBeMinimalStage(f0);
        // below methods is tested in below test method
        // - completedStage
        // - failedFuture
        // - failedStage
    }

    @Test
    void factoryMethods_byImmediateValue__Java9() throws Exception {
        // completedStage
        Cffu<String> cf = (Cffu<String>) cffuFactory.completedStage(hello);
        assertEquals(hello, cf.toCompletableFuture().get());
        MinStageTestUtils.shouldBeMinimalStage(cf);

        // failedFuture
        Cffu<String> ff = cffuFactory.failedFuture(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class, ff::get).getCause());
        MinStageTestUtils.shouldNotBeMinimalStage(ff);

        // failedStage
        Cffu<String> fs = (Cffu<String>) cffuFactory.<String>failedStage(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                fs.toCompletableFuture().get()
        ).getCause());
        MinStageTestUtils.shouldBeMinimalStage(fs);
    }

    @Test
    void factoryMethods_byLambda() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        // runAsync
        Cffu<Void> cf = cffuFactory.runAsync(() -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        });
        assertNull(cf.get());
        assertEquals(hello, holder.get());
        MinStageTestUtils.shouldNotBeMinimalStage(cf);

        holder.set(null);
        cf = cffuFactory.runAsync(() -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService);
        assertNull(cf.get());
        assertEquals(hello, holder.get());
        MinStageTestUtils.shouldNotBeMinimalStage(cf);

        // supplyAsync
        Cffu<String> s_cf = cffuFactory.supplyAsync(() -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return hello;
        });
        assertEquals(hello, s_cf.get());
        MinStageTestUtils.shouldNotBeMinimalStage(s_cf);
        s_cf = cffuFactory.supplyAsync(() -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return hello;
        }, anotherExecutorService);
        assertEquals(hello, s_cf.get());
        MinStageTestUtils.shouldNotBeMinimalStage(s_cf);
    }

    @Test
    void constructor() throws Exception {
        Cffu<Integer> cf = cffuFactory.newIncompleteCffu();
        assertFalse(cf.isDone());

        cf.complete(n);
        assertEquals(n, cf.get());
    }

    ////////////////////////////////////////
    //# allOf / anyOf methods
    ////////////////////////////////////////

    @Test
    void staticMethods_allOf_anyOf() throws Exception {
        Cffu<Integer> f1 = cffuFactory.completedFuture(n);
        Cffu<Integer> f2 = cffuFactory.completedFuture(anotherN);
        Cffu<Integer> f3 = cffuFactory.completedFuture(nnn);

        assertNull(cffuFactory.allOf().get());
        assertNull(cffuFactory.allOf(f1, f2).get());
        assertNull(cffuFactory.allOf(f1, f2, f3).get());

        assertFalse(cffuFactory.anyOf().toCompletableFuture().isDone());
        assertNotNull(cffuFactory.anyOf(f1, f2).get());
        assertNotNull(cffuFactory.anyOf(f1, f2, f3).get());
    }

    ////////////////////////////////////////
    //# Delay Execution
    ////////////////////////////////////////

    @Test
    void staticMethods_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS);
        cffuFactory.runAsync(() -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }, delayer).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        delayer = cffuFactory.delayedExecutor(1, TimeUnit.MILLISECONDS, anotherExecutorService);
        cffuFactory.runAsync(() -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
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

        final Cffu<Integer> cf = cffuFactory.completedFuture(n);

        cf.thenRun(() -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenRunAsync(() -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAccept(x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptAsync(x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(n + 1, cf.thenApply(x -> x + 1).get());
        assertEquals(n + 2, cf.thenApplyAsync(x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return x + 2;
        }).get());
        assertEquals(n + 3, cf.thenApplyAsync(x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return x + 3;
        }, anotherExecutorService).get());
    }

    @Test
    void thenBoth_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        cf.runAfterBoth(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterBothAsync(cf, () -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.thenAcceptBoth(cf, (x, y) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.thenAcceptBothAsync(cf, (x, y) -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(n * 2, cf.thenCombine(cf, Integer::sum).get());
        assertEquals(n * 2, cf.thenCombineAsync(cf, (a, b) -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return Integer.sum(a, b);
        }).get());
        assertEquals(n * 2, cf.thenCombineAsync(cf, (a, b) -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return Integer.sum(a, b);
        }, anotherExecutorService).get());
    }

    @Test
    void thenEither_Methods() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        cf.runAfterEither(cf, () -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.runAfterEitherAsync(cf, () -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        holder.set(null);
        cf.acceptEither(cf, x -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.acceptEitherAsync(cf, x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());

        assertEquals(n + 1, cf.applyToEither(cf, x -> x + 1).get());
        assertEquals(n + 2, cf.applyToEitherAsync(cf, x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return x + 2;
        }).get());
        assertEquals(n + 3, cf.applyToEitherAsync(cf, x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return x + 3;
        }, anotherExecutorService).get());
    }

    @Test
    void errorHandling_methods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        // exceptionally
        assertEquals(n, cf.exceptionally(t -> anotherN).get());
        assertEquals(anotherN, failed.exceptionally(t -> anotherN).get());

        // below methods is tested in below test method
        // - exceptionallyAsync
    }

    @Test
    void errorHandling_methods__Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        assertEquals(n, cf.exceptionallyAsync(t -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return anotherN;
        }).get());
        assertEquals(anotherN, failed.exceptionallyAsync(t -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return anotherN;
        }).get());

        assertEquals(n, cf.exceptionallyAsync(t -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return anotherN;
        }, anotherExecutorService).get());
        assertEquals(anotherN, failed.exceptionallyAsync(t -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return anotherN;
        }, anotherExecutorService).get());
    }

    @Test
    void timeoutControl_methods() throws Exception {
        // For completed
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        assertEquals(n,
                cf.orTimeout(20, TimeUnit.MILLISECONDS).get());
        assertEquals(n,
                cf.completeOnTimeout(anotherN, 20, TimeUnit.MILLISECONDS).get());

        // for incomplete
        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();
        assertEquals(TimeoutException.class, assertThrowsExactly(ExecutionException.class, () ->
                incomplete.orTimeout(20, TimeUnit.MILLISECONDS).get()
        ).getCause().getClass());

        Cffu<Integer> incomplete2 = cffuFactory.newIncompleteCffu();
        assertEquals(anotherN, incomplete2.completeOnTimeout(anotherN, 20, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void advancedMethods__thenCompose() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        assertEquals(anotherN, cf.thenCompose(x -> cffuFactory.completedFuture(anotherN)).get());
        assertEquals(anotherN, cf.thenComposeAsync(x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return cffuFactory.completedFuture(anotherN);
        }).get());
        assertEquals(anotherN, cf.thenComposeAsync(x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return cffuFactory.completedFuture(anotherN);
        }, anotherExecutorService).get());
    }

    @Test
    void advancedMethods__exceptionallyCompose() throws Exception {
        // for success
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        assertEquals(n, cf.exceptionallyCompose(x -> cffuFactory.completedFuture(anotherN)).get());
        assertEquals(n, cf.exceptionallyComposeAsync(x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return cffuFactory.completedFuture(anotherN);
        }).get());
        assertEquals(n, cf.exceptionallyComposeAsync(x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return cffuFactory.completedFuture(anotherN);
        }, anotherExecutorService).get());

        // for failed
        Cffu<Integer> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        assertEquals(anotherN, failed.exceptionallyCompose(x -> cffuFactory.completedFuture(anotherN)).get());
        assertEquals(anotherN, failed.exceptionallyComposeAsync(x -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return cffuFactory.completedFuture(anotherN);
        }).get());
        assertEquals(anotherN, failed.exceptionallyComposeAsync(x -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return cffuFactory.completedFuture(anotherN);
        }, anotherExecutorService).get());
    }

    @Test
    void advancedMethods__handle_whenComplete() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        assertEquals(anotherN, cf.handle((x, e) -> anotherN).get());
        assertEquals(anotherN, cf.handleAsync((x, e) -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return anotherN;
        }).get());
        assertEquals(anotherN, cf.handleAsync((x, e) -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return anotherN;
        }, anotherExecutorService).get());

        final AtomicReference<String> holder = new AtomicReference<>();

        cf.whenComplete((x, e) -> holder.set(hello)).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            holder.set(hello);
        }).get();
        assertEquals(hello, holder.get());
        holder.set(null);
        cf.whenCompleteAsync((x, e) -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            holder.set(hello);
        }, anotherExecutorService).get();
        assertEquals(hello, holder.get());
    }

    @Test
    void readExplicitlyMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);

        Integer r = cf.get();
        assertEquals(n, r);
        assertSame(rte, assertThrowsExactly(ExecutionException.class, failed::get).getCause());

        assertEquals(r, cf.get(1, TimeUnit.MILLISECONDS));
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                failed.get(1, TimeUnit.MILLISECONDS)
        ).getCause());

        assertEquals(r, cf.join());
        assertSame(rte, assertThrowsExactly(CompletionException.class, failed::join).getCause());

        assertEquals(r, cf.getNow(0));
        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                failed.getNow(0)
        ).getCause());

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
    void readExplicitlyMethods__Java19() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);
        Integer r = cf.get();
        assertEquals(n, r);

        // resultNow
        assertEquals(r, cf.resultNow());
        assertThrowsExactly(IllegalStateException.class, failed::resultNow);

        // exceptionNow
        assertThrowsExactly(IllegalStateException.class, cf::exceptionNow);
        assertSame(rte, failed.exceptionNow());
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19) /* GEN_MARK_KEEP */
    void readExplicitlyMethods__Java19_CanNotCompatible() {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        Cffu<Object> failed = TestUtils.safeNewFailedCffu(executorService, rte);
        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();

        // state
        assertEquals(Future.State.SUCCESS, cf.state());
        assertEquals(Future.State.FAILED, failed.state());
        assertEquals(Future.State.RUNNING, incomplete.state());
    }

    @Test
    void writeMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);
        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();

        assertFalse(cf.complete(anotherN));
        assertEquals(n, cf.get());

        assertTrue(incomplete.complete(anotherN));
        assertEquals(anotherN, incomplete.get());
        // below methods is tested in below test method
        // - completeAsync

        // completeExceptionally
        assertFalse(cf.completeExceptionally(rte));
        assertEquals(n, cf.get());

        incomplete = cffuFactory.newIncompleteCffu();
        assertTrue(incomplete.completeExceptionally(rte));
        assertSame(rte, assertThrowsExactly(ExecutionException.class, incomplete::get).getCause());

        // cancel
        assertFalse(cf.cancel(true));
        incomplete = cffuFactory.newIncompleteCffu();
        assertTrue(incomplete.cancel(true));
    }

    @Test
    void writeMethods__Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // completeAsync
        assertEquals(n, cf.completeAsync(() -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return anotherN;
        }).get());
        assertEquals(n, cf.completeAsync(() -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return anotherN;
        }, anotherExecutorService).get());

        Cffu<Integer> incomplete = cffuFactory.newIncompleteCffu();
        assertEquals(anotherN, incomplete.completeAsync(() -> {
            TestUtils.assertCffuRunInDefaultThread(executorService);
            return anotherN;
        }).get());
        incomplete = cffuFactory.newIncompleteCffu();
        assertEquals(anotherN, incomplete.completeAsync(() -> {
            TestUtils.assertCffuRunInThreadOf(anotherExecutorService);
            return anotherN;
        }, anotherExecutorService).get());

        RuntimeException ex = new RuntimeException();
        Cffu<Integer> incomplete2 = cffuFactory.newIncompleteCffu();
        incomplete2.completeAsync(() -> {
            throw ex;
        });
        assertSame(ex, assertThrowsExactly(ExecutionException.class, incomplete2::get).getCause());
    }

    ////////////////////////////////////////
    //# Re-Config methods
    ////////////////////////////////////////

    @Test
    void reConfigMethods() throws Exception {
        // below methods is tested in below test method
        // - minimalCompletionStage
        // - copy

        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // toCompletableFuture
        assertEquals(n, cf.toCompletableFuture().get());
    }

    @Test
    void reConfigMethods__Java9() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // minimalCompletionStage
        MinStageTestUtils.shouldBeMinimalStage((Cffu<Integer>) cf.minimalCompletionStage());

        // copy
        assertEquals(n, cf.copy().get());
    }

    @Test
    void test_getterMethods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // defaultExecutor
        assertNotNull(cf.defaultExecutor());
    }

    @Test
    void test_inspectionMethods() {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // getNumberOfDependents
        assertNotEquals(-1, cf.getNumberOfDependents());
    }

    @Test
    void test_dangerousMethods() throws Exception {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // obtrudeValue
        cf.obtrudeValue(anotherN);
        assertEquals(anotherN, cf.get());

        // obtrudeException
        cf.obtrudeException(rte);
        assertSame(rte, assertThrowsExactly(ExecutionException.class, cf::get).getCause());
    }

    @Test
    void test_forApiCompatibility() {
        Cffu<Integer> cf = cffuFactory.completedFuture(n);

        // newIncompleteFuture
        assertFalse(cf.newIncompleteFuture().isDone());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# the behavior that is easy to misunderstand
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_similarities_and_differences_between_cancelled_and_exceptionally() throws Exception {
        final Cffu<Integer> cancelledCf = cffuFactory.newIncompleteCffu();
        cancelledCf.cancel(false);

        final Cffu<Integer> exceptionallyCf =
                TestUtils.safeNewFailedCffu(executorService, rte);

        ////////////////////////////////////////
        // different behavior
        ////////////////////////////////////////

        assertThrowsExactly(CancellationException.class, cancelledCf::get);
        assertSame(rte, assertThrowsExactly(ExecutionException.class, exceptionallyCf::get).getCause());

        assertThrowsExactly(CancellationException.class, () ->
                cancelledCf.get(1, TimeUnit.MILLISECONDS)
        );
        assertSame(rte, assertThrowsExactly(ExecutionException.class, () ->
                exceptionallyCf.get(1, TimeUnit.MILLISECONDS)
        ).getCause());

        assertThrowsExactly(CancellationException.class, cancelledCf::join);
        assertSame(rte, assertThrowsExactly(CompletionException.class, exceptionallyCf::join).getCause());

        assertThrowsExactly(CancellationException.class, () ->
                cancelledCf.getNow(n)
        );
        assertSame(rte, assertThrowsExactly(CompletionException.class, () ->
                exceptionallyCf.getNow(n)
        ).getCause());

        assertTrue(cancelledCf.isCancelled());
        assertFalse(exceptionallyCf.isCancelled());

        ////////////////////////////////////////
        // same behavior
        ////////////////////////////////////////

        assertTrue(cancelledCf.isCompletedExceptionally());
        assertTrue(exceptionallyCf.isCompletedExceptionally());

        assertTrue(cancelledCf.isDone());
        assertTrue(exceptionallyCf.isDone());

        assertEquals(n, cancelledCf.exceptionally(throwable -> {
            assertInstanceOf(CancellationException.class, throwable);
            assertNull(throwable.getCause());
            return n;
        }).get());
        assertEquals(n, exceptionallyCf.exceptionally(throwable -> {
            assertSame(rte, throwable);
            return n;
        }).get());
    }

    @Test
    void test_similarities_and_differences_between_cancelled_and_exceptionally__Java19() throws Exception {
        final Cffu<Integer> cancelledCf = cffuFactory.newIncompleteCffu();
        cancelledCf.cancel(false);

        final Cffu<Integer> exceptionallyCf =
                TestUtils.safeNewFailedCffu(executorService, rte);

        ////////////////////////////////////////
        // different behavior
        ////////////////////////////////////////

        assertThrowsExactly(IllegalStateException.class, cancelledCf::exceptionNow);
        assertSame(rte, exceptionallyCf.exceptionNow());

        ////////////////////////////////////////
        // same behavior
        ////////////////////////////////////////

        assertThrowsExactly(IllegalStateException.class, cancelledCf::resultNow);
        assertThrowsExactly(IllegalStateException.class, exceptionallyCf::resultNow);
    }

    @Test
    void nested_exception() throws Exception {
        Cffu<Integer> cf = cffuFactory.newIncompleteCffu();
        cf.completeExceptionally(new CompletionException(rte));
        // auto unwrap first level CompletionException
        checkNo1MoreLevelForCompletionException(cf);

        cf = cffuFactory.completedFuture(n);
        // auto unwrap first level CompletionException
        checkNo1MoreLevelForCompletionException(cf.thenRun(() -> {
            throw new CompletionException(rte);
        }));

        cf = cffuFactory.newIncompleteCffu();
        cf.completeExceptionally(new ExecutionException(rte));
        // auto unwrap first level ExecutionException
        check1MoreLevelForExecutionException(cf);

        cf = cffuFactory.completedFuture(n);
        // auto unwrap first level ExecutionException
        check1MoreLevelForExecutionException(cf.thenRun(() ->
                TestUtils.sneakyThrow(new ExecutionException(rte))
        ));
    }

    private <T> void checkNo1MoreLevelForCompletionException(Cffu<T> cf) throws Exception {
        assertSame(rte, assertThrowsExactly(ExecutionException.class, cf::get).getCause());

        assertSame(rte, assertThrowsExactly(CompletionException.class, cf::join).getCause());
    }

    private <T> void check1MoreLevelForExecutionException(Cffu<T> cf) throws Exception {

        final ExecutionException getEx = assertThrowsExactly(ExecutionException.class, cf::get);
        assertInstanceOf(ExecutionException.class, getEx.getCause());
        assertSame(rte, getEx.getCause().getCause());

        final CompletionException joinEx = assertThrowsExactly(CompletionException.class, cf::join);
        assertInstanceOf(ExecutionException.class, joinEx.getCause());
        assertSame(rte, joinEx.getCause().getCause());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    private static CffuFactory cffuFactory;

    @BeforeAll
    static void beforeAll() {
        executorService = TestingExecutorUtils.createThreadPool(hello);
        cffuFactory = CffuFactory.builder(executorService).build();

        anotherExecutorService = TestingExecutorUtils.createThreadPool(hello, true);

        TestingExecutorUtils.warmupExecutorService(executorService, anotherExecutorService);
    }

    @AfterAll
    static void afterAll() {
        TestingExecutorUtils.shutdownExecutorService(executorService, anotherExecutorService);
    }
}
