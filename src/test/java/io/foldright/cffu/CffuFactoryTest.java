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

import static io.foldright.cffu.CffuTestUtils.*;
import static io.foldright.test_utils.TestUtils.createExceptionallyCompletedFutureWithSleep;
import static io.foldright.test_utils.TestUtils.createNormallyCompletedFutureWithSleep;
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
        shouldBeMinStage((Cffu<Integer>) stage);

        assertEquals(n, testMinimalStage(stage).get());
        assertEquals(n, testMinimalStage(stage.thenApply(Function.identity())).get());
    }

    @Test
    void test_failedFuture() throws Exception {
        Cffu<Integer> cf = cffuFactory.failedFuture(rte);
        shouldNotBeMinStage(cf);

        try {
            cf.join();
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());
        }

        assertEquals(n, cf.exceptionally(throwable -> n).get());
    }

    @Test
    void test_failedStage() throws Exception {
        CompletionStage<Integer> stage = cffuFactory.failedStage(rte);

        CompletableFuture<Integer> cf = testMinimalStage(stage);
        try {
            cf.join();
            fail();
        } catch (CompletionException expected) {
            assertSame(rte, expected.getCause());

        }

        assertEquals(n, testMinimalStage(stage.exceptionally(throwable -> n)).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods, equivalent to same name static methods of CompletableFuture
    //
    //  create by logic/lambda
    //    - runAsync*
    //    - supplyAsync*
    ////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////
    //# Factory Methods
    //
    //    - newIncompleteCffu: equivalent to CompletableFuture constructor
    //    - asCffu: wrap an existed CompletableFuture/CompletionStage to Cffu
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_newIncompleteCffu() {
        Cffu<Integer> cf = cffuFactory.newIncompleteCffu();
        assertFalse(cf.isDone());
    }

    @Test
    void test_asCffu() throws Exception {
        Cffu<Integer> cf = cffuFactory.asCffu(CompletableFuture.completedFuture(n));
        shouldNotBeMinStage(cf);
        assertEquals(n, cf.get());

        CffuFactory fac = CffuFactoryBuilder.newCffuFactoryBuilder(anotherExecutorService).forbidObtrudeMethods(true).build();
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
    void test_asCffu__CompletableFuture_completedStage() {
        Cffu<Integer> cf = cffuFactory.asCffu(CompletableFuture.completedStage(n));
        shouldBeMinStage(cf);
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_9)
    void test_asCffuArray() throws Exception {
        Cffu<Integer>[] cffus = cffuFactory.asCffuArray(CompletableFuture.completedStage(n), CompletableFuture.completedFuture(n));

        shouldBeMinStage(cffus[0]);

        shouldNotBeMinStage(cffus[1]);
        assertEquals(n, cffus[1].get());
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
                    failedCf(),
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
                createNormallyCompletedFutureWithSleep(another_n),
                CompletableFuture.completedFuture(n),
                createNormallyCompletedFutureWithSleep(another_n)
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
                    createNormallyCompletedFutureWithSleep(another_n),
                    failedCf(),
                    createNormallyCompletedFutureWithSleep(another_n)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        // first normally completed cffuAnyOf cf win,
        // even later cfs exceptionally completed!

        assertEquals(n, cffuFactory.cffuAnyOf(
                createExceptionallyCompletedFutureWithSleep(rte),
                CompletableFuture.completedFuture(n),
                createExceptionallyCompletedFutureWithSleep(rte)
        ).get());
    }

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
                    failedCf()
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            cffuFactory.cffuCombine(
                    CompletableFuture.completedFuture(n),
                    failedCf(),
                    CompletableFuture.completedFuture(s)
            ).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion Methods
    //
    //    - asCffu: CF -> Cffu
    //    - asCffuArray: CF[] -> Cffu[]
    //    - toCompletableFutureArray: Cffu -> CF
    //
    //    - cffuListToArray: List<Cffu> -> Cffu[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_cffuArrayUnwrap() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfs = new CompletableFuture[]{
                CompletableFuture.completedFuture(n),
                CompletableFuture.completedFuture(another_n)
        };
        @SuppressWarnings("unchecked")
        Cffu<Integer>[] input = new Cffu[]{
                cffuFactory.asCffu(cfs[0]),
                cffuFactory.asCffu(cfs[1]),
        };
        assertArrayEquals(cfs, CffuFactory.cffuArrayUnwrap(input));
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
    //# getter/setter
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_getter() {
        assertSame(executorService, cffuFactory.defaultExecutor());
        assertFalse(cffuFactory.forbidObtrudeMethods());

        CffuFactory fac = CffuFactoryBuilder.newCffuFactoryBuilder(anotherExecutorService).forbidObtrudeMethods(true).build();
        assertSame(anotherExecutorService, fac.defaultExecutor());
        assertTrue(fac.forbidObtrudeMethods());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Test helper methods
    ////////////////////////////////////////////////////////////////////////////////

    private static <T> CompletableFuture<T> failedCf() {
        CompletableFuture<T> cf = new CompletableFuture<>();
        cf.completeExceptionally(rte);
        return cf;
    }

    private static ExecutorService executorService;

    private static ExecutorService anotherExecutorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("CffuFactoryTest");
        anotherExecutorService = TestThreadPoolManager.createThreadPool("CffuFactoryTest-Another", true);

        cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(executorService).build();
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService, anotherExecutorService);
    }
}
