package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.Arrays;
import java.util.concurrent.*;
import java.util.function.Function;

import static io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder;
import static io.foldright.test_utils.TestUtils.*;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static org.junit.jupiter.api.Assertions.*;


/**
 * see io.foldright.compatibility_test.CffuApiCompatibilityTest
 */
class CffuFactoryTest {
    ////////////////////////////////////////////////////////////////////////////////
    // test constants
    ////////////////////////////////////////////////////////////////////////////////

    static final int n = 42;
    static final int another_n = 424242;

    static final String s = "S42";

    static final double d = 42.1;

    static final RuntimeException rte = new RuntimeException("Bang");
    static final RuntimeException another_rte = new RuntimeException("BangBang");

    private static CffuFactory cffuFactory;

    ///////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  Create by immediate value
    //    - completedFuture/completedStage
    //    - failedFuture/failedStage
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_completedFuture() throws Exception {
        assertEquals(n, cffuFactory.completedFuture(n).get());
    }

    @Test
    void test_completedStage() throws Exception {
        CompletionStage<Integer> stage = cffuFactory.completedStage(n);
        CompletionStage<Integer> sa = stage.thenApply(Function.identity());

        assertEquals(n, stage.toCompletableFuture().get());
        assertEquals(n, sa.toCompletableFuture().get());

        // CAUTION: Last check minimal stage, may rewrite the CF by obtrude* methods
        shouldBeMinimalStage((Cffu<?>) stage);
        shouldBeMinimalStage((Cffu<?>) sa);
    }

    @Test
    void test_failedFuture() throws Exception {
        Cffu<Integer> cf = cffuFactory.failedFuture(rte);

        try {
            cf.join();
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }
        assertEquals(n, cf.exceptionally(throwable -> n).get());

        shouldNotBeMinimalStage(cf);
    }

    @Test
    void test_failedStage() throws Exception {
        CompletionStage<Integer> stage = cffuFactory.failedStage(rte);
        CompletionStage<Integer> sa = stage.thenApply(Function.identity());
        CompletionStage<Integer> se = stage.exceptionally(throwable -> n);

        try {
            createFailedFuture(rte).toCompletableFuture().join();
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());

        }
        assertEquals(n, se.toCompletableFuture().get());

        // CAUTION: Last check minimal stage, may rewrite the CF by obtrude* methods
        shouldBeMinimalStage((Cffu<Integer>) stage);
        shouldBeMinimalStage((Cffu<Integer>) sa);
        shouldBeMinimalStage((Cffu<Integer>) se);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  create by logic/lambda
    //    - runAsync*
    //    - supplyAsync*
    ////////////////////////////////////////////////////////////////////////////////

    // ...

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods
    //
    //    - newIncompleteCffu: equivalent to CompletableFuture constructor
    //
    //    - asCffu:      CF/CompletionStage -> Cffu
    //    - asCffuArray: CF/CompletionStage[] -> Cffu[]
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_newIncompleteCffu() {
        Cffu<Integer> cf = cffuFactory.newIncompleteCffu();
        assertFalse(cf.isDone());
    }

    @Test
    void test_asCffu() throws Exception {
        Cffu<Integer> cf = cffuFactory.asCffu(CompletableFuture.completedFuture(n));

        assertEquals(n, cf.get());
        shouldNotBeMinimalStage(cf);

        CffuFactory fac = newCffuFactoryBuilder(anotherExecutorService).forbidObtrudeMethods(true).build();
        Cffu<Integer> cffu = fac.asCffu(cffuFactory.completedFuture(42));
        assertSame(anotherExecutorService, cffu.defaultExecutor());
        assertSame(fac, cffu.cffuFactory());

        try {
            cffu.obtrudeValue(44);
            fail();
        } catch (UnsupportedOperationException expected) {
            assertEquals("obtrudeValue is forbidden by cffu", expected.getMessage());
        }
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_asCffu__for_factoryMethods_of_Java9() {
        CompletableFuture<Object> cf1 = CompletableFuture.failedFuture(rte);
        assertFalse(cffuFactory.asCffu(cf1).isMinimalStage());
        shouldNotBeMinimalStage(cf1);

        Cffu<Integer> cf2 = cffuFactory.asCffu(CompletableFuture.completedStage(n));
        assertTrue(cf2.isMinimalStage());
        shouldBeMinimalStage(cf2);

        Cffu<Object> cf3 = cffuFactory.asCffu(CompletableFuture.failedStage(rte));
        assertTrue(cf3.isMinimalStage());
        shouldBeMinimalStage(cf3);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_asCffuArray() throws Exception {
        Cffu<Integer>[] cffus = cffuFactory.asCffuArray(CompletableFuture.completedStage(n), CompletableFuture.completedFuture(n));
        assertEquals(n, cffus[1].get());

        shouldBeMinimalStage(cffus[0]);
        shouldNotBeMinimalStage(cffus[1]);
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allOf / anyOf methods, equivalent to same name static methods of CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allOf_CompletableFuture() throws Exception {
        Cffu<Void> cffus = cffuFactory.allOf(CompletableFuture.completedFuture(n), CompletableFuture.completedFuture(another_n));
        cffus.get();
    }

    @Test
    void test_anyOf_CompletableFuture() throws Exception {
        Cffu<Object> cffus = cffuFactory.anyOf(CompletableFuture.completedFuture(n), CompletableFuture.completedFuture(another_n));
        cffus.get();
    }

    ////////////////////////////////////////////////////////////////////////////////
    // new methods of CompletableFuture missing functions
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuAllOf() throws Exception {
        assertEquals(Arrays.asList(n, n + 1, n + 2),
                cffuFactory.cffuAllOf(
                                CompletableFuture.completedFuture(n),
                                CompletableFuture.completedFuture(n + 1),
                                CompletableFuture.completedFuture(n + 2))
                        .get()
        );

        assertEquals(Arrays.asList(n, n + 1, n + 2),
                cffuFactory.cffuAllOf(
                                cffuFactory.completedFuture(n),
                                cffuFactory.completedFuture(n + 1),
                                cffuFactory.completedFuture(n + 2))
                        .get()
        );

        assertTrue(cffuFactory.cffuAllOf().isDone());
    }

    @Test
    void test_cffuAllOf_exceptionally() throws Exception {
        try {
            cffuFactory.cffuAllOf(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void test_cffuAnyOf() throws Exception {
        assertEquals(n, cffuFactory.cffuAnyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n)
        ).get());

        assertEquals(n, cffuFactory.cffuAnyOf(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.completedFuture(n),
                cffuFactory.newIncompleteCffu()
        ).get());

        assertFalse(cffuFactory.cffuAnyOf().isDone());
    }

    @Test
    void test_cffuAnyOf_exceptionally() throws Exception {
        // first exceptionally completed cffuAnyOf cf win,
        // even later cfs normally completed!

        try {
            cffuFactory.cffuAnyOf(
                    createIncompleteFuture(),
                    createFailedFuture(rte),
                    createIncompleteFuture()
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // first normally completed cffuAnyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, cffuFactory.cffuAnyOf(
                createIncompleteFuture(),
                CompletableFuture.completedFuture(n),
                createIncompleteFuture()
        ).get());
    }

    @Test
    void test_cffuAnyOfSuccess__trivial_case() throws Exception {
        // success then success
        assertEquals(n, cffuFactory.cffuAnyOfSuccess(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.newIncompleteCffu(),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                cffuFactory.completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, cffuFactory.cffuAnyOfSuccess(
                cffuFactory.newIncompleteCffu(),
                cffuFactory.newIncompleteCffu(),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                cffuFactory.completedFuture(n)
        ).get());

        // all success
        assertEquals(n, cffuFactory.cffuAnyOfSuccess(
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                cffuFactory.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                cffuFactory.completedFuture(n)
        ).get());

        //////////////////////////////////////////////////////////////////////////////

        // success then success
        assertEquals(n, cffuFactory.cffuAnyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                CompletableFuture.completedFuture(n)
        ).get());
        // success then failed
        assertEquals(n, cffuFactory.cffuAnyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        // all success
        assertEquals(n, cffuFactory.cffuAnyOfSuccess(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return another_n;
                }),
                CompletableFuture.completedFuture(n)
        ).get());

        //////////////////////////////////////////////////////////////////////////////

        assertSame(NoCfsProvidedException.class, cffuFactory.cffuAnyOfSuccess().exceptionNow().getClass());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Delay Execution, equivalent to same name static methods of CompletableFuture
    //
    //    - delayedExecutor
    ////////////////////////////////////////////////////////////////////////////////

    // ...

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe allOf/anyOf Factory Methods
    //    method name prefix with `cffu`
    //
    //    - cffuAllOf
    //    - cffuAnyOf
    ////////////////////////////////////////////////////////////////////////////////

    // ...

    ////////////////////////////////////////////////////////////////////////////////
    //# New type-safe cffuCombine Factory Methods
    //  support 2~5 input arguments, method name prefix with `cffu`
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuCombine() throws Exception {
        assertEquals(Tuple2.of(n, s), cffuFactory.cffuCombine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.cffuCombine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, another_n), cffuFactory.cffuCombine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d),
                CompletableFuture.completedFuture(another_n)
        ).get());

        assertEquals(Tuple5.of(n, s, d, another_n, n + n), cffuFactory.cffuCombine(
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d),
                CompletableFuture.completedFuture(another_n),
                CompletableFuture.completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.cffuCombine(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.cffuCombine(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, another_n), cffuFactory.cffuCombine(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(another_n)
        ).get());

        assertEquals(Tuple5.of(n, s, d, another_n, n + n), cffuFactory.cffuCombine(
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(another_n),
                cffuFactory.completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.completedFuture(n).cffuCombine(
                CompletableFuture.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.completedFuture(n).cffuCombine(
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, another_n), cffuFactory.completedFuture(n).cffuCombine(
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d),
                CompletableFuture.completedFuture(another_n)
        ).get());

        assertEquals(Tuple5.of(n, s, d, another_n, n + n), cffuFactory.completedFuture(n).cffuCombine(
                CompletableFuture.completedFuture(s),
                CompletableFuture.completedFuture(d),
                CompletableFuture.completedFuture(another_n),
                CompletableFuture.completedFuture(n + n)
        ).get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Tuple2.of(n, s), cffuFactory.completedFuture(n).cffuCombine(
                cffuFactory.completedFuture(s)
        ).get());

        assertEquals(Tuple3.of(n, s, d), cffuFactory.completedFuture(n).cffuCombine(
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d)
        ).get());

        assertEquals(Tuple4.of(n, s, d, another_n), cffuFactory.completedFuture(n).cffuCombine(
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(another_n)
        ).get());

        assertEquals(Tuple5.of(n, s, d, another_n, n + n), cffuFactory.completedFuture(n).cffuCombine(
                cffuFactory.completedFuture(s),
                cffuFactory.completedFuture(d),
                cffuFactory.completedFuture(another_n),
                cffuFactory.completedFuture(n + n)
        ).get());
    }

    @Test
    void test_cffuCombine_exceptionally() throws Exception {
        try {
            cffuFactory.cffuCombine(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cffuFactory.cffuCombine(
                    CompletableFuture.completedFuture(n),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cffuFactory.cffuCombine(
                    CompletableFuture.completedFuture(n),
                    CompletableFuture.completedFuture(d),
                    createFailedFuture(rte),
                    CompletableFuture.completedFuture(s),
                    CompletableFuture.completedFuture(another_n)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion (Static) Methods
    //
    //    - toCompletableFutureArray:     Cffu -> CF
    //    - cffuArrayUnwrap:              Cffu -> CF
    //
    //    - cffuListToArray:              List<Cffu> -> Cffu[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_toCompletableFutureArray() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfArray = new CompletableFuture[]{
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(another_n)
        };
        @SuppressWarnings("unchecked")
        CompletionStage<Integer>[] csArray = new CompletableFuture[]{
                cfArray[0],
                cfArray[1],
        };
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] cffuArray = new Cffu[]{
                cffuFactory.asCffu(cfArray[0]),
                cffuFactory.asCffu(cfArray[1]),
        };

        assertArrayEquals(cfArray, CffuFactory.toCompletableFutureArray(cfArray));
        assertArrayEquals(cfArray, CffuFactory.toCompletableFutureArray(csArray));
        assertArrayEquals(cfArray, CffuFactory.toCompletableFutureArray(cffuArray));
    }

    @Test
    void test_cffuArrayUnwrap() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfArray = new CompletableFuture[]{
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(another_n)
        };
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] input = new Cffu[]{
                cffuFactory.asCffu(cfArray[0]),
                cffuFactory.asCffu(cfArray[1]),
        };
        assertArrayEquals(cfArray, CffuFactory.cffuArrayUnwrap(input));
    }

    @Test
    void test_cffuListToArray() {
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] input = new Cffu[]{
                cffuFactory.completedFuture(n),
                cffuFactory.completedFuture(another_n),
                cffuFactory.newIncompleteCffu()
        };

        assertArrayEquals(input, CffuFactory.cffuListToArray(Arrays.asList(input)));
    }

    @Test
    void test_completableFutureListToArray() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] input = new CompletableFuture[]{
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(another_n)
        };

        assertArrayEquals(input, CffuFactory.completableFutureListToArray(Arrays.asList(input)));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Getter methods of CffuFactory properties
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_getter() {
        assertSame(executorService, cffuFactory.defaultExecutor());
        assertFalse(cffuFactory.forbidObtrudeMethods());

        CffuFactory fac = newCffuFactoryBuilder(anotherExecutorService).forbidObtrudeMethods(true).build();
        assertSame(anotherExecutorService, fac.defaultExecutor());
        assertTrue(fac.forbidObtrudeMethods());
    }

    @Test
    void test_forbidObtrudeMethods_property() {
        CffuFactory fac2 = newCffuFactoryBuilder(executorService).forbidObtrudeMethods(true).build();

        Cffu<Object> cf = fac2.newIncompleteCffu();
        try {
            cf.obtrudeValue(42);
            fail();
        } catch (UnsupportedOperationException expected) {
            assertEquals("obtrudeValue is forbidden by cffu", expected.getMessage());
        }
        try {
            cf.obtrudeException(rte);
            fail();
        } catch (UnsupportedOperationException expected) {
            assertEquals("obtrudeException is forbidden by cffu", expected.getMessage());
        }
    }

    @Test
    void test_executorSetting_MayBe_ThreadPerTaskExecutor() throws Exception {
        final boolean USE_COMMON_POOL = (ForkJoinPool.getCommonPoolParallelism() > 1);

        CffuFactory fac = newCffuFactoryBuilder(commonPool()).build();
        if (USE_COMMON_POOL) {
            assertSame(commonPool(), fac.defaultExecutor());
        } else {
            String executorClassName = fac.defaultExecutor().getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }

        assertEquals(42, fac.supplyAsync(() -> 42).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Test helper methods
    ////////////////////////////////////////////////////////////////////////////////

    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("CffuFactoryTest");
        anotherExecutorService = TestThreadPoolManager.createThreadPool("CffuFactoryTest-Another", true);

        cffuFactory = newCffuFactoryBuilder(executorService).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService, anotherExecutorService);
    }
}
