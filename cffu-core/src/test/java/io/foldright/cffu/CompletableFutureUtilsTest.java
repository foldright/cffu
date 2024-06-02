package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple2;
import io.foldright.cffu.tuple.Tuple3;
import io.foldright.cffu.tuple.Tuple4;
import io.foldright.cffu.tuple.Tuple5;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static io.foldright.cffu.CompletableFutureUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;
import static java.util.function.Function.identity;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("RedundantThrows")
class CompletableFutureUtilsTest {
    ////////////////////////////////////////////////////////////////////////////////
    //# allOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allOf_methods__success__trivial_case() throws Exception {
        assertEquals(Arrays.asList(n, n + 1, n + 2), allResultsOf(
                completedFuture(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allResultsOf(
                completedStage(n),
                completedFuture(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allResultsOf(completedFuture(n)).get());
        assertEquals(Collections.singletonList(n), allResultsOf(completedStage(n)).get());

        assertEquals(Collections.emptyList(), allResultsOf().get());

        ////////////////////////////////////////////////////////////////////////////////

        assertEquals(Arrays.asList(n, n + 1, n + 2), allResultsOfFastFail(
                completedStage(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(Arrays.asList(n, n + 1), allResultsOfFastFail(
                completedFuture(n),
                completedStage(n + 1)
        ).get());

        assertEquals(Collections.singletonList(n), allResultsOfFastFail(completedFuture(n)).get());
        assertEquals(Collections.singletonList(n), allResultsOfFastFail(completedStage(n)).get());

        assertEquals(Collections.emptyList(), allResultsOfFastFail().get());

        ////////////////////////////////////////////////////////////////////////////////

        Arrays.asList(
                allOf(completedFuture(n), completedStage(n + 1), completedFuture(n + 2)),
                allOf(completedStage(n), completedFuture(n + 1)),
                allOf(completedFuture(n)),
                allOf(completedStage(n)),
                allOf(),

                allOfFastFail(completedFuture(n), completedStage(n + 1), completedFuture(n + 2)),
                allOfFastFail(completedStage(n), completedFuture(n + 1)),
                allOfFastFail(completedFuture(n)),
                allOfFastFail(completedStage(n)),
                allOfFastFail()
        ).forEach(f -> assertNull(f.join()));
    }

    @Test
    void test_allOf__exceptionally() throws Exception {
        final RuntimeException ex1 = new RuntimeException("ex1");
        final RuntimeException ex2 = new RuntimeException("ex2");

        ////////////////////////////////////////////////////////////////////////////////
        // allResultsOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // allResultsOf: the ex of first given cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // allResultsOf: the ex of first given cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsOf(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allResultsOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertThrows(TimeoutException.class, () ->
                allResultsOf(
                        completedFuture(n),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        );

        // incomplete fail incomplete
        assertThrows(TimeoutException.class, () ->
                allResultsOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
        );

        ////////////////////////////////////////////////////////////////////////////////
        // allResultsOfFastFail
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // allResultsOfFastFail: the ex of first given cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsOfFastFail(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(anotherRte, assertThrows(ExecutionException.class, () ->
                // allResultsOfFastFail: the ex of first given cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allResultsOfFastFail(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allResultsOfFastFail(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allResultsOfFastFail(
                        completedFuture(n),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allResultsOfFastFail(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // allOfFastFail
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // allOfFastFail: the ex of first complete(in time) cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allOfFastFail(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(anotherRte, assertThrows(ExecutionException.class, () ->
                // allOfFastFail: the ex of first complete(in time) cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allOfFastFail(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allOfFastFail(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allOfFastFail(
                        completedFuture(n),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allOfFastFail(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // allOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // allOf: the ex of first complete(in time) cf argument win.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allOf(
                        failedFuture(rte),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // all failed - concurrent
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // allOf: the ex of first complete(in time) cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                allOf(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // success and failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                allOf(
                        completedFuture(n),
                        failedFuture(rte),
                        completedFuture(s),
                        failedFuture(anotherRte)
                ).get()
        ).getCause());

        // failed/incomplete/failed
        assertThrows(TimeoutException.class, () ->
                allOf(
                        completedFuture(n),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        );

        // incomplete fail incomplete
        assertThrows(TimeoutException.class, () ->
                allOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(100, TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void test_mostOf() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final CompletionStage<Integer> completedStage = completedStage(n);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cancelled = createCancelledFuture();
        final CompletableFuture<Integer> incomplete = createIncompleteFuture();

        // 0 input cf
        assertEquals(0, mostResultsOfSuccess(10, TimeUnit.MILLISECONDS, null).get().size());

        // 1 input cf
        assertEquals(Collections.singletonList(n), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, null, completed).get());
        assertEquals(Collections.singletonList(n), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, anotherN, completedStage).get());

        assertEquals(Collections.singletonList(anotherN), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, anotherN, failed).get());
        assertEquals(Collections.singletonList(anotherN), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, anotherN, cancelled).get());
        assertEquals(Collections.singletonList(anotherN), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, anotherN, incomplete).get());

        // more input cf
        assertEquals(Arrays.asList(n, null, null, null), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, null, completed, failed, cancelled, incomplete
        ).get());
        assertEquals(Arrays.asList(n, anotherN, anotherN, anotherN), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, anotherN, completedStage, failed, cancelled, incomplete
        ).get());

        assertEquals(Arrays.asList(anotherN, anotherN, anotherN), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, anotherN, failed, cancelled, incomplete
        ).get());

        // do not wait for failed and cancelled
        assertEquals(Arrays.asList(anotherN, anotherN), mostResultsOfSuccess(
                10, TimeUnit.DAYS, anotherN, failed, cancelled
        ).get());
    }

    @Test
    void test_mostOf_wontModifyInputCf() throws Exception {
        final CompletableFuture<Integer> incomplete = createIncompleteFuture();
        final CompletableFuture<Integer> incomplete2 = createIncompleteFuture();

        assertEquals(Collections.singletonList(null), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, null, incomplete
        ).get());
        assertEquals(Arrays.asList(null, null), mostResultsOfSuccess(
                10, TimeUnit.MILLISECONDS, null, incomplete, incomplete2
        ).get());

        assertEquals(CffuState.RUNNING, state(incomplete));
        assertEquals(CffuState.RUNNING, state(incomplete2));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# anyOf* methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_anyOf__success__trivial_case() throws Exception {
        assertEquals(n, anyOf(
                completedFuture(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());

        assertEquals(n, anyOf(
                completedStage(n),
                completedStage(n + 1)
        ).get());

        assertEquals(n, anyOf(completedFuture(n)).get());
        assertEquals(n, anyOf(completedStage(n)).get());

        assertFalse(anyOf().isDone());

        // success with incomplete CF
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                completedFuture(n)
        ).get());
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                completedStage(n)
        ).get());

        ////////////////////////////////////////

        assertEquals(n, anyOfSuccess(
                completedFuture(n),
                completedStage(n + 1),
                completedFuture(n + 2)
        ).get());
        assertEquals(n, anyOfSuccess(
                completedStage(n),
                completedFuture(n + 1)
        ).get());

        assertEquals(n, anyOfSuccess(completedFuture(n)).get());
        assertEquals(n, anyOfSuccess(completedStage(n)).get());

        assertInstanceOf(NoCfsProvidedException.class,
                assertThrows(ExecutionException.class,
                        () -> anyOfSuccess().get()
                ).getCause());

        // success with incomplete CF
        assertEquals(n, anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                completedFuture(n)
        ).get());
        assertEquals(n, anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                completedStage(n)
        ).get());
    }

    @Test
    void test_anyOf__exceptionally() throws Exception {
        final RuntimeException ex1 = new RuntimeException("ex1");
        final RuntimeException ex2 = new RuntimeException("ex2");

        ////////////////////////////////////////////////////////////////////////////////
        // anyOf
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(anotherRte, assertThrows(ExecutionException.class, () ->
                // anyOf: the ex of first failed cf argument win.
                //   ❗dependent on the implementation behavior of `CF.anyOf`️
                anyOf(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // incomplete fail incomplete
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                anyOf(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get()
        ).getCause());

        ////////////////////////////////////////////////////////////////////////////////
        // anyOfSuccess
        ////////////////////////////////////////////////////////////////////////////////

        // all failed
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                // anyOfSuccess: the ex of first failed cf argument win, even subsequent cf failed early.
                //   ❗dependent on the implementation behavior of `CF.allOf`️
                anyOfSuccess(
                        CompletableFuture.supplyAsync(() -> {
                            sleep(100);
                            throw rte;
                        }),
                        failedFuture(anotherRte),
                        failedFuture(ex1),
                        failedFuture(ex2)
                ).get()
        ).getCause());

        // incomplete fail incomplete
        assertThrows(TimeoutException.class, () ->
                anyOfSuccess(
                        createIncompleteFuture(),
                        failedFuture(rte),
                        createIncompleteFuture()
                ).get(30, TimeUnit.MILLISECONDS)
        ).getCause();
    }

    @Test
    void test_anyOf__concurrent() throws Exception {
        // incomplete/wait-success then success
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOf(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // success then failed
        assertEquals(n, anyOf(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // failed then success
        assertSame(rte, assertThrows(ExecutionException.class, () -> {
            anyOf(
                    CompletableFuture.supplyAsync(() -> {
                        sleep(100);
                        return n;
                    }),
                    failedFuture(rte),
                    failedFuture(rte)
            ).get();
        }).getCause());

        ////////////////////////////////////////

        // incomplete/wait-success then success
        assertEquals(n, anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // wait/success then success
        assertEquals(n, anyOfSuccess(
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    return anotherN;
                }),
                completedFuture(n)
        ).get());

        // success then failed
        assertEquals(n, anyOfSuccess(
                createIncompleteFuture(),
                createIncompleteFuture(),
                CompletableFuture.supplyAsync(() -> {
                    sleep(300);
                    throw rte;
                }),
                completedFuture(n)
        ).get());

        // failed then success
        assertEquals(n, anyOfSuccess(
                CompletableFuture.supplyAsync(() -> {
                    sleep(100);
                    return n;
                }),
                failedFuture(rte),
                failedFuture(rte)
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# allTupleOf methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_allTupleOf() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletionStage<String> cf_s = completedStage(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        assertEquals(Tuple2.of(n, s), allTupleOf(cf_n, cf_s).get());
        assertEquals(Tuple2.of(n, s), allTupleOfFastFail(cf_n, cf_s).get());

        assertEquals(Tuple3.of(n, s, d), allTupleOf(cf_n, cf_s, cf_d).get());
        assertEquals(Tuple3.of(n, s, d), allTupleOfFastFail(cf_n, cf_s, cf_d).get());

        assertEquals(Tuple4.of(n, s, d, anotherN), allTupleOf(cf_n, cf_s, cf_d, cf_an).get());
        assertEquals(Tuple4.of(n, s, d, anotherN), allTupleOfFastFail(cf_n, cf_s, cf_d, cf_an).get());

        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), allTupleOf(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
        assertEquals(Tuple5.of(n, s, d, anotherN, n + n), allTupleOfFastFail(cf_n, cf_s, cf_d, cf_an, cf_nn).get());
    }

    @Test
    void test_allTupleOf_exceptionally() throws Exception {
        final CompletableFuture<Object> incomplete = new CompletableFuture<>();
        final CompletableFuture<Object> fail = failedFuture(rte);

        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletionStage<String> cf_s = completedStage(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);

        try {
            allTupleOf(cf_n, fail).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            allTupleOfFastFail(incomplete, fail).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            allTupleOf(cf_n, fail, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            allTupleOfFastFail(incomplete, fail, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            allTupleOf(cf_n, fail, cf_d, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            allTupleOfFastFail(incomplete, fail, cf_d, cf_s).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            allTupleOf(cf_n, cf_d, fail, cf_s, cf_an).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            allTupleOfFastFail(incomplete, cf_d, fail, cf_s, cf_an).get();

            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    @Test
    void test_allTupleOf_NotFastFail() throws Exception {
        final CompletableFuture<Object> incomplete = new CompletableFuture<>();
        final CompletableFuture<Object> fail = failedFuture(rte);

        final CompletableFuture<String> cf_s = completedFuture(s);
        final CompletableFuture<Double> cf_d = completedFuture(d);
        final CompletionStage<Integer> cf_an = completedStage(anotherN);

        try {
            allTupleOf(incomplete, fail).get(100, TimeUnit.MILLISECONDS);
            fail();
        } catch (TimeoutException expected) {
        }
        try {
            allTupleOf(incomplete, fail, cf_s).get(100, TimeUnit.MILLISECONDS);
            fail();
        } catch (TimeoutException expected) {
        }
        try {
            allTupleOf(incomplete, fail, cf_d, cf_s).get(100, TimeUnit.MILLISECONDS);
            fail();
        } catch (TimeoutException expected) {
        }
        try {
            allTupleOf(incomplete, cf_d, fail, cf_s, cf_an).get(100, TimeUnit.MILLISECONDS);
            fail();
        } catch (TimeoutException expected) {
        }
    }

    @Test
    void test_mostTupleOfSuccess() throws Exception {
        final CompletableFuture<Integer> completed = completedFuture(n);
        final CompletionStage<String> anotherCompleted = completedStage(s);
        final CompletableFuture<Integer> failed = failedFuture(rte);
        final CompletableFuture<Integer> cancelled = createCancelledFuture();
        final CompletableFuture<Integer> incomplete = createIncompleteFuture();

        assertEquals(Tuple2.of(n, s), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), mostTupleOfSuccess(
                10, TimeUnit.MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());

        // with `executorWhenTimeout`

        assertEquals(Tuple2.of(n, s), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, completed, anotherCompleted
        ).get());
        assertEquals(Tuple2.of(n, null), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, completed, failed
        ).get());

        assertEquals(Tuple3.of(n, s, null), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled
        ).get());
        assertEquals(Tuple3.of(null, null, s), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, incomplete, failed, anotherCompleted
        ).get());

        assertEquals(Tuple4.of(n, s, null, null), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, completed, anotherCompleted, cancelled, incomplete
        ).get());
        assertEquals(Tuple4.of(null, null, null, null), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, incomplete, failed, cancelled, incomplete
        ).get());

        assertEquals(Tuple5.of(null, n, s, null, null), mostTupleOfSuccess(
                executorService, 10, TimeUnit.MILLISECONDS, cancelled, completed, anotherCompleted, incomplete, failed
        ).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# both methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_both() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        final CompletableFuture<Integer> cf_nn = completedFuture(n + n);

        final Runnable runnable = () -> {
        };
        assertNull(runAfterBothFastFail(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFastFailAsync(cf_n, cf_nn, runnable).get());
        assertNull(runAfterBothFastFailAsync(cf_n, cf_nn, runnable, executorService).get());

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        assertNull(thenAcceptBothFastFail(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFastFailAsync(cf_n, cf_nn, bc).get());
        assertNull(thenAcceptBothFastFailAsync(cf_n, cf_nn, bc, executorService).get());

        assertEquals(3 * n, thenCombineFastFail(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFastFailAsync(cf_n, cf_nn, Integer::sum).get());
        assertEquals(3 * n, thenCombineFastFailAsync(cf_n, cf_nn, Integer::sum, executorService).get());
    }

    @Test
    void both_fastFail() throws Exception {
        CompletableFuture<Integer> cf_n = CompletableFuture.supplyAsync(() -> {
            sleep(2_000);
            return n;
        });
        final CompletableFuture<Integer> failed = failedFuture(rte);

        final Runnable runnable = () -> {
        };
        try {
            runAfterBothFastFail(cf_n, failed, runnable).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            runAfterBothFastFailAsync(cf_n, failed, runnable).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            runAfterBothFastFailAsync(cf_n, failed, runnable, executorService).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        BiConsumer<Integer, Integer> bc = (i1, i2) -> {
        };
        try {
            thenAcceptBothFastFail(cf_n, failed, bc).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            thenAcceptBothFastFailAsync(cf_n, failed, bc).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            thenAcceptBothFastFailAsync(cf_n, failed, bc, executorService).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }

        try {
            thenCombineFastFail(cf_n, failed, Integer::sum).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            thenCombineFastFailAsync(cf_n, failed, Integer::sum).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
        try {
            thenCombineFastFailAsync(cf_n, failed, Integer::sum, executorService).get(1, TimeUnit.MILLISECONDS);
            fail();
        } catch (ExecutionException expected) {
            assertSame(rte, expected.getCause());
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# either methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_either() throws Exception {
        final CompletableFuture<Integer> cf_n = completedFuture(n);
        CompletableFuture<Integer> incomplete = createIncompleteFuture();

        final Runnable runnable = () -> {
        };
        assertNull(runAfterEitherSuccess(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable).get());
        assertNull(runAfterEitherSuccessAsync(cf_n, incomplete, runnable, executorService).get());

        Consumer<Integer> c = i -> {
        };
        assertNull(acceptEitherSuccess(cf_n, incomplete, c).get());
        assertNull(acceptEitherSuccessAsync(cf_n, incomplete, c).get());
        assertNull(acceptEitherSuccessAsync(cf_n, incomplete, c, executorService).get());

        assertEquals(n, applyToEitherSuccess(cf_n, incomplete, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf_n, incomplete, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(cf_n, incomplete, identity(), executorService).get());
    }

    @Test
    void test_either_success() throws Exception {
        final CompletableFuture<Integer> failed = failedFuture(rte);
        CompletableFuture<Integer> cf = completedFuture(n);

        final Runnable runnable = () -> {
        };
        assertNull(runAfterEitherSuccess(failed, cf, runnable).get());
        assertNull(runAfterEitherSuccessAsync(failed, cf, runnable).get());
        assertNull(runAfterEitherSuccessAsync(failed, cf, runnable, executorService).get());

        Consumer<Integer> c = i -> {
        };
        assertNull(acceptEitherSuccess(failed, cf, c).get());
        assertNull(acceptEitherSuccessAsync(failed, cf, c).get());
        assertNull(acceptEitherSuccessAsync(failed, cf, c, executorService).get());

        assertEquals(n, applyToEitherSuccess(failed, cf, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(failed, cf, identity()).get());
        assertEquals(n, applyToEitherSuccessAsync(failed, cf, identity(), executorService).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# New enhanced methods
    ////////////////////////////////////////////////////////////////////////////////

    @Test
    void test_peek() throws Exception {
        BiConsumer<Object, Throwable> c = (v, ex) -> {
        };
        BiConsumer<Object, Throwable> ec = (v, ex) -> {
            throw anotherRte;
        };

        CompletableFuture<Object> failed = failedFuture(rte);
        assertSame(peek(failed, c), failed);
        assertSame(peekAsync(failed, c), failed);
        assertSame(peekAsync(failed, c, executorService), failed);
        assertSame(peek(failed, ec), failed);
        assertSame(peekAsync(failed, ec), failed);
        assertSame(peekAsync(failed, ec, executorService), failed);

        CompletableFuture<Integer> success = completedFuture(n);
        assertEquals(n, peek(success, c).get());
        assertEquals(n, peekAsync(success, c).get());
        assertEquals(n, peekAsync(success, c).get());
        assertEquals(n, peek(success, ec).get());
        assertEquals(n, peekAsync(success, ec).get());
        assertEquals(n, peekAsync(success, ec).get());
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Backport CF methods
    //  compatibility for low Java version
    ////////////////////////////////////////////////////////////////////////////////

    //# Factory methods

    @Test
    void test_failedFuture() throws Exception {
        assertTrue(failedFuture(rte).isDone());
        assertEquals(n, completedStage(n).toCompletableFuture().get());
        assertTrue(failedStage(rte).toCompletableFuture().isDone());
    }

    @Test
    void test_delayedExecutor() throws Exception {
        final AtomicReference<String> holder = new AtomicReference<>();

        Executor delayer = delayedExecutor(1, TimeUnit.MILLISECONDS);
        CompletableFuture.runAsync(() -> holder.set(testName), delayer).get();
        assertEquals(testName, holder.get());
    }

    @Test
    void test_exceptionallyAsync() throws Exception {
        CompletableFuture<Integer> cf = failedFuture(rte);
        assertEquals(n, exceptionallyAsync(cf, ex -> n).get());

        cf = completedFuture(n);
        assertEquals(n, exceptionallyAsync(cf, ex -> anotherN).get());

    }

    @Test
    void test_timeout() throws Exception {
        assertInstanceOf(TimeoutException.class,
                assertThrows(ExecutionException.class, () ->
                        orTimeout(createIncompleteFuture(), 1, TimeUnit.MILLISECONDS).get()
                ).getCause());
        assertInstanceOf(TimeoutException.class,
                assertThrows(ExecutionException.class, () ->
                        cffuOrTimeout(createIncompleteFuture(), 1, TimeUnit.MILLISECONDS).get()
                ).getCause());
        assertInstanceOf(TimeoutException.class,
                assertThrows(ExecutionException.class, () ->
                        cffuOrTimeout(createIncompleteFuture(), defaultExecutor(), 1, TimeUnit.MILLISECONDS).get()
                ).getCause());

        assertEquals(n, orTimeout(completedFuture(n), 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuOrTimeout(completedFuture(n), defaultExecutor(), 1, TimeUnit.MILLISECONDS).get());

        assertEquals(n, completeOnTimeout(createIncompleteFuture(), n, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(createIncompleteFuture(), n, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(createIncompleteFuture(), n, defaultExecutor(), 1, TimeUnit.MILLISECONDS).get());

        assertEquals(n, completeOnTimeout(completedFuture(n), anotherN, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, 1, TimeUnit.MILLISECONDS).get());
        assertEquals(n, cffuCompleteOnTimeout(completedFuture(n), anotherN, defaultExecutor(), 1, TimeUnit.MILLISECONDS).get());
    }

    @Test
    void test_safeBehavior_orTimeout() {
        final Thread testThread = currentThread();

        assertEquals(n, orTimeout(createIncompleteFuture(), 5, TimeUnit.MILLISECONDS).handle((r, ex) -> {
            assertInstanceOf(TimeoutException.class, ex);
            assertTrue(Delayer.atCfDelayerThread());
            return n;
        }).join());

        assertEquals(n, cffuOrTimeout(createIncompleteFuture(), 5, TimeUnit.MILLISECONDS).handle((r, ex) -> {
            assertInstanceOf(TimeoutException.class, ex);
            assertFalse(Delayer.atCfDelayerThread());
            assertNotSame(testThread, currentThread());
            return n;
        }).join());
        assertEquals(n, cffuOrTimeout(createIncompleteFuture(), executorService, 5, TimeUnit.MILLISECONDS).handle((r, ex) -> {
            assertInstanceOf(TimeoutException.class, ex);
            assertFalse(Delayer.atCfDelayerThread());
            assertTrue(TestThreadPoolManager.isRunInExecutor(executorService));
            return n;
        }).join());
    }

    @Test
    void test_safeBehavior_completeOnTimeout() {
        final Thread testThread = currentThread();

        assertEquals(n, completeOnTimeout(createIncompleteFuture(), n, 5, TimeUnit.MILLISECONDS).handle((r, ex) -> {
            assertNull(ex);
            assertTrue(Delayer.atCfDelayerThread());
            return r;
        }).join());

        assertEquals(n, cffuCompleteOnTimeout(createIncompleteFuture(), n, 5, TimeUnit.MILLISECONDS).handle((r, ex) -> {
            assertNull(ex);
            assertFalse(Delayer.atCfDelayerThread());
            assertNotSame(testThread, currentThread());
            return r;
        }).join());
        assertEquals(n, cffuCompleteOnTimeout(createIncompleteFuture(), n, executorService, 5, TimeUnit.MILLISECONDS).handle((r, ex) -> {
            assertNull(ex);
            assertFalse(Delayer.atCfDelayerThread());
            assertTrue(TestThreadPoolManager.isRunInExecutor(executorService));
            return r;
        }).join());
    }

    @Test
    void test_exceptionallyCompose() throws Exception {
        CompletableFuture<Object> completed = completedFuture(n);
        CompletableFuture<Object> failed = failedFuture(rte);

        assertEquals(n, exceptionallyCompose(failed, ex -> completedFuture(n)).get());
        assertEquals(n, exceptionallyCompose(completed, ex -> completedFuture(anotherN)).get());

        assertEquals(n, exceptionallyComposeAsync(failed, ex -> completedFuture(n)).get());
        assertEquals(n, exceptionallyComposeAsync(completed, ex -> completedFuture(anotherN)).get());
    }

    @Test
    @SuppressWarnings("ThrowableNotThrown")
    void test_read() {
        ////////////////////////////////////////////////////////////////////////////////
        // completed tasks
        ////////////////////////////////////////////////////////////////////////////////

        final CompletableFuture<Integer> completed = completedFuture(n);
        final FutureTask<Integer> completedTask = new FutureTask<>(() -> n);
        completedTask.run();

        final CffuFactory cffuFactory = CffuFactory.builder(Executors.newCachedThreadPool()).build();
        final Cffu<Integer> completedCffu = cffuFactory.completedFuture(n);

        assertEquals(n, join(completed, 1, TimeUnit.MILLISECONDS));
        assertEquals(n, getSuccessNow(completed, anotherN));
        assertEquals(n, getSuccessNow(completed, null));
        assertEquals(n, resultNow(completed));
        assertEquals(n, resultNow(completedTask));
        assertEquals(n, resultNow(completedCffu));
        final String m1 = assertThrows(IllegalStateException.class, () ->
                exceptionNow(completed)
        ).getMessage();
        if (m1 != null) assertEquals("Task completed with a result", m1);
        assertEquals("Task completed with a result",
                assertThrows(IllegalStateException.class, () ->
                        exceptionNow(completedTask)
                ).getMessage());
        final String m12 = assertThrows(IllegalStateException.class, () ->
                exceptionNow(completedCffu)
        ).getMessage();
        if (m12 != null) assertEquals("Task completed with a result", m12);
        assertSame(CffuState.SUCCESS, state(completed));
        assertSame(CffuState.SUCCESS, state(completedTask));
        assertSame(CffuState.SUCCESS, state(completedCffu));

        ////////////////////////////////////////////////////////////////////////////////
        // failed tasks
        ////////////////////////////////////////////////////////////////////////////////

        final CompletableFuture<Object> failed = failedFuture(rte);
        final FutureTask<Integer> failedTask = new FutureTask<>(() -> {
            throw rte;
        });
        failedTask.run();
        final Cffu<Object> failedCffu = cffuFactory.failedFuture(rte);

        assertSame(rte, assertThrows(CompletionException.class, failed::join).getCause());
        // same as CompletableFuture.join method
        assertSame(rte,
                assertThrows(CompletionException.class, () ->
                        join(failed, 1, TimeUnit.MILLISECONDS)
                ).getCause());
        assertEquals(anotherN, getSuccessNow(failed, anotherN));
        assertNull(getSuccessNow(failed, null));
        final String m2 = assertThrows(IllegalStateException.class, () ->
                resultNow(failed)
        ).getMessage();
        if (m2 != null) assertEquals("Task completed with exception", m2);
        assertNull(getSuccessNow(failed, null));
        final String m22 = assertThrows(IllegalStateException.class, () ->
                resultNow(failedTask)
        ).getMessage();
        final String m23 = assertThrows(IllegalStateException.class, () ->
                resultNow(failedCffu)
        ).getMessage();
        if (m23 != null) assertEquals("Task completed with exception", m22);
        assertSame(rte, exceptionNow(failed));
        assertSame(rte, exceptionNow(failedTask));
        assertSame(rte, exceptionNow(failedCffu));
        assertSame(CffuState.FAILED, state(failed));
        assertSame(CffuState.FAILED, state(failedTask));
        assertSame(CffuState.FAILED, state(failedCffu));

        ////////////////////////////////////////////////////////////////////////////////
        // cancelled tasks
        ////////////////////////////////////////////////////////////////////////////////

        CompletableFuture<Object> cancelled = createCancelledFuture();

        assertThrows(CancellationException.class, cancelled::join);
        // same as CompletableFuture.join method
        assertThrows(CancellationException.class, () -> join(cancelled, 1, TimeUnit.MILLISECONDS));
        final String m3 = assertThrows(IllegalStateException.class, () ->
                resultNow(cancelled)
        ).getMessage();
        if (m3 != null) assertEquals("Task was cancelled", m3);
        final String m4 = assertThrows(IllegalStateException.class, () ->
                exceptionNow(cancelled)
        ).getMessage();
        if (m4 != null) assertEquals("Task was cancelled", m4);
        assertSame(CffuState.CANCELLED, state(cancelled));

        ////////////////////////////////////////////////////////////////////////////////
        // incomplete tasks
        ////////////////////////////////////////////////////////////////////////////////

        final CompletableFuture<Object> incomplete = createIncompleteFuture();

        assertInstanceOf(TimeoutException.class, assertThrows(CompletionException.class, () -> {
            join(incomplete, 1, TimeUnit.MILLISECONDS);
        }).getCause());
        assertEquals(anotherN, getSuccessNow(incomplete, anotherN));
        assertNull(getSuccessNow(incomplete, null));
        final String m5 = assertThrows(IllegalStateException.class, () ->
                resultNow(incomplete)
        ).getMessage();
        if (m5 != null) assertEquals("Task has not completed", m5);
        final String m6 = assertThrows(IllegalStateException.class, () ->
                exceptionNow(incomplete)
        ).getMessage();
        if (m6 != null) assertEquals("Task has not completed", m6);
        assertSame(CffuState.RUNNING, state(incomplete));

        // Incomplete Future -> join before timeout
        CompletableFuture<Integer> later = createFutureCompleteLater(n);
        assertEquals(n, join(later, 3, TimeUnit.SECONDS));
    }

    @Test
    void test_write() throws Exception {
        assertEquals(n, completeAsync(createIncompleteFuture(), () -> n).get());
        assertEquals(n, completeAsync(createIncompleteFuture(), () -> n, commonPool()).get());
        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(42);
            assertThrows(UnsupportedOperationException.class, () -> completeAsync(f, () -> null));
        }
        assertSame(rte, assertThrows(ExecutionException.class, () ->
                completeAsync(createIncompleteFuture(), () -> {
                    throw rte;
                }).get()
        ).getCause());

        CompletableFuture<Integer> completed = completedFuture(n);
        assertEquals(n, completeAsync(completed, () -> anotherN).get());

        ////////////////////////////////////////

        assertSame(rte, assertThrows(ExecutionException.class, () ->
                completeExceptionallyAsync(createIncompleteFuture(), () -> rte).get()
        ).getCause());
        if (isJava9Plus()) {
            CompletableFuture<Integer> f = (CompletableFuture<Integer>) completedStage(42);
            assertThrows(UnsupportedOperationException.class, () -> completeExceptionallyAsync(f, () -> rte));
        }
        assertEquals(n, completeExceptionallyAsync(completedFuture(n), () -> rte).get());
    }

    @Test
    void test_re_config() throws Exception {
        CompletionStage<Integer> mf = minimalCompletionStage(completedFuture(n));
        assertEquals(n, mf.toCompletableFuture().get());

        CompletableFuture<Integer> cf = createIncompleteFuture();
        copy(cf).complete(n);
        assertFalse(cf.isDone());

        CompletableFuture<Object> incomplete = newIncompleteFuture(cf);
        assertFalse(incomplete.isDone());
        incomplete.complete(n);
        assertFalse(cf.isDone());
    }

    @Test
    void test_executor() {
        Executor executor = defaultExecutor();
        assertIsDefaultExecutor(executor);

        assertIsDefaultExecutor(screenExecutor(commonPool()));

        ExecutorService e = Executors.newCachedThreadPool();
        assertSame(e, screenExecutor(e));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# Conversion (Static) Methods
    //
    //    - toCompletableFutureArray:     CompletionStage[](including Cffu) -> CF[]
    //    - completableFutureListToArray: List<CF> -> CF[]
    ////////////////////////////////////////////////////////////////////////////////

    /**
     * {@link CompletableFutureUtils#toCompletableFutureArray}
     * is tested in {@link CffuFactoryTest#test_toCompletableFutureArray()}
     */
    @Test
    void test_completableFutureListToArray() {
        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] input = new CompletableFuture[]{completedFuture(n), completedFuture(anotherN)};

        assertArrayEquals(input, CompletableFutureUtils.completableFutureListToArray(Arrays.asList(input)));
    }

    ////////////////////////////////////////////////////////////////////////////////
    //# test helper fields
    ////////////////////////////////////////////////////////////////////////////////

    private static void assertIsDefaultExecutor(Executor executor) {
        final boolean USE_COMMON_POOL = ForkJoinPool.getCommonPoolParallelism() > 1;
        if (USE_COMMON_POOL) {
            assertSame(commonPool(), executor);
        } else {
            String executorClassName = executor.getClass().getName();
            assertTrue(executorClassName.endsWith("$ThreadPerTaskExecutor"));
        }
    }

    private static final String testName = "CompletableFutureUtilsTest";
    private static ExecutorService executorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("CffuTest");
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
