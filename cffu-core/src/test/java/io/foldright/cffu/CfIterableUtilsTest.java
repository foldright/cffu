package io.foldright.cffu;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static io.foldright.cffu.CfIterableUtils.*;
import static io.foldright.test_utils.TestUtils.sleep;
import static io.foldright.test_utils.TestingConstants.LONG_WAIT_MS;
import static io.foldright.test_utils.TestingConstants.MEDIAN_WAIT_MS;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class CfIterableUtilsTest {
    @Test
    void test_mSupply() throws Exception {
        assertThat(mSupplyFailFastAsync(emptyList()).get()).isEmpty();
        assertThat(mSupplyFailFastAsync(asList(() -> 1, () -> 2)).get()).containsExactly(1, 2);
        assertThat(mSupplyFailFastAsync(asList(() -> 1, () -> 2), testExecutor).get()).containsExactly(1, 2);

        assertThat(mSupplyAllSuccessAsync(-1, emptyList()).get()).isEmpty();
        assertThat(mSupplyAllSuccessAsync(-1, asList(() -> {throw new RuntimeException();}, () -> 2))
                .get()).containsExactly(-1, 2);
        assertThat(mSupplyAllSuccessAsync(-1, asList(() -> {throw new RuntimeException();}, () -> 2), testExecutor)
                .get()).containsExactly(-1, 2);

        assertThat(mSupplyMostSuccessAsync(-1, 1, SECONDS, emptyList()).get()).isEmpty();
        assertThat(mSupplyMostSuccessAsync(-1, 1, SECONDS,
                asList(() -> {throw new RuntimeException();}, () -> 2)).get()).containsExactly(-1, 2);
        assertThat(mSupplyMostSuccessAsync(-1, 1, SECONDS,
                asList(() -> {throw new RuntimeException();}, () -> 2), testExecutor).get()).containsExactly(-1, 2);

        assertThat(mSupplyAsync(emptyList()).get()).isEmpty();
        assertThat(mSupplyAsync(asList(() -> 1, () -> 2)).get()).containsExactly(1, 2);
        assertThat(mSupplyAsync(asList(() -> 1, () -> 2), testExecutor).get()).containsExactly(1, 2);

        final CompletionException ce = assertThrowsExactly(CompletionException.class,
                () -> mSupplyAnySuccessAsync(emptyList()).join());
        assertInstanceOf(NoCfsProvidedException.class, ce.getCause());
        assertEquals(2, mSupplyAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> 2)).get());
        assertEquals(2, mSupplyAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> 2), testExecutor).get());

        final CompletableFuture<Object> any = mSupplyAnyAsync(emptyList());
        sleep(MEDIAN_WAIT_MS);
        assertFalse(any.isDone());
        assertEquals(2, mSupplyAnyAsync(asList(() -> {
            sleep();
            return 1;
        }, () -> 2)).get());
        assertEquals(2, mSupplyAnyAsync(asList(() -> {
            sleep();
            return 1;
        }, () -> 2), testExecutor).get());
    }

    @Test
    void test_mRun() throws Exception {
        assertNull(mRunFailFastAsync(emptyList()).get());
        assertNull(mRunFailFastAsync(asList(() -> {}, () -> {})).get());
        assertNull(mRunFailFastAsync(asList(() -> {}, () -> {}), testExecutor).get());

        assertNull(mRunAsync(emptyList()).get());
        assertNull(mRunAsync(asList(() -> {}, () -> {})).get());
        assertNull(mRunAsync(asList(() -> {}, () -> {}), testExecutor).get());

        final CompletionException ce = assertThrowsExactly(CompletionException.class,
                () -> mRunAnySuccessAsync(emptyList()).join());
        assertInstanceOf(NoCfsProvidedException.class, ce.getCause());
        assertNull(mRunAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> {})).get());
        assertNull(mRunAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> {}), testExecutor).get());

        final CompletableFuture<Void> any = mRunAnyAsync(emptyList());
        sleep(MEDIAN_WAIT_MS);
        assertFalse(any.isDone());
        assertNull(mRunAnyAsync(asList(() -> {}, () -> {})).get());
        assertNull(mRunAnyAsync(asList(() -> {}, () -> {}), testExecutor).get());
    }

    @Test
    void test_allResultsFailFastOf() {
        // Test with an empty iterable
        assertThat(allResultsFailFastOf(emptyList()).join()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(allResultsFailFastOf(singletonList(future1)).join()).containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(allResultsFailFastOf(asList(future1, future2, future3)).join()).containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_allSuccessResultsOf() {
        // Test with an empty iterable
        assertThat(allSuccessResultsOf(null, emptyList()).join()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(allSuccessResultsOf(null, singletonList(future1)).join()).containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(allSuccessResultsOf(null, asList(future1, future2, future3)).join()).containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_mostSuccessResultsOf() throws Exception {
        assertThat(mostSuccessResultsOf(null, 10, MILLISECONDS, emptyList()).get()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(mostSuccessResultsOf(null, 1, TimeUnit.MILLISECONDS, singletonList(future1), Runnable::run).join())
                .containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(mostSuccessResultsOf(null, 1, TimeUnit.MILLISECONDS, asList(future1, future2, future3), Runnable::run).join())
                .containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_allResultsOf() {
        // Test with an empty iterable
        assertThat(allResultsOf(emptyList()).join()).isEmpty();

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertThat(allResultsOf(singletonList(future1)).join()).containsExactly("result1");

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertThat(allResultsOf(asList(future1, future2, future3)).join()).containsExactly("result1", "result2", "result3");
    }

    @Test
    void test_allFailFastOf() {
        // Test with an empty iterable
        assertNull(allFailFastOf(emptyList()).join());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertNull(allFailFastOf(singletonList(future1)).join());

        // Test with multiple completed futures
        RuntimeException rte = new RuntimeException();
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {throw rte;});
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> sleep(LONG_WAIT_MS));
        final ExecutionException ee = assertThrowsExactly(ExecutionException.class,
                () -> allFailFastOf(asList(future1, future2, future3)).get(MEDIAN_WAIT_MS, MILLISECONDS));
        assertSame(rte, ee.getCause());
    }

    @Test
    void test_allOf() {
        // Test with an empty iterable
        assertNull(allOf(emptyList()).join());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertNull(allOf(singletonList(future1)).join());

        // Test with multiple completed futures
        RuntimeException rte = new RuntimeException();
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {throw rte;});
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> sleep(LONG_WAIT_MS));
        assertThrowsExactly(TimeoutException.class,
                () -> allOf(asList(future1, future2, future3)).get(MEDIAN_WAIT_MS, MILLISECONDS));
    }

    @Test
    void test_anySuccessOf() {
        // Test with an empty iterable
        final CompletionException ce = assertThrowsExactly(CompletionException.class, () -> anySuccessOf(emptyList()).join());
        assertInstanceOf(NoCfsProvidedException.class, ce.getCause());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", anySuccessOf(singletonList(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {throw new RuntimeException();});
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(MEDIAN_WAIT_MS);
            return "";
        });
        assertEquals("result1", anySuccessOf(asList(future2, future1, future3)).join());
    }

    @Test
    void test_anyOf() {
        final CompletableFuture<Object> f = anyOf(emptyList());
        sleep(MEDIAN_WAIT_MS);
        assertFalse(f.isDone());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", anyOf(singletonList(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {throw new RuntimeException();});
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(MEDIAN_WAIT_MS);
            return "";
        });
        assertTrue(asList("result1", "result2", "result3").contains(anyOf(asList(future1, future2, future3)).join()));
    }
}
