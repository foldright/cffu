package io.foldright.cffu2;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

import static io.foldright.cffu2.CfIterableUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.*;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("resource")
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

        assertCfWithExType(mSupplyAnySuccessAsync(emptyList()), NoCfsProvidedException.class);
        assertEquals(2, mSupplyAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> 2)).get());
        assertEquals(2, mSupplyAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> 2), testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(mSupplyAnyAsync(emptyList()));
        assertEquals(2, mSupplyAnyAsync(asList(() -> {
            sleep(MEDIAN_WAIT_MS);
            return 1;
        }, () -> 2)).get());
        assertEquals(2, mSupplyAnyAsync(asList(() -> {
            sleep(MEDIAN_WAIT_MS);
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

        assertCfWithExType(mRunAnySuccessAsync(emptyList()), NoCfsProvidedException.class);
        assertNull(mRunAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> {})).get());
        assertNull(mRunAnySuccessAsync(asList(() -> {
            throw new RuntimeException();
        }, () -> {}), testExecutor).get());

        assertCfStillIncompleteIn(mRunAnyAsync(emptyList()));
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
        assertCfWithEx(allFailFastOf(asList(future1, future2, future3)), rte);
    }

    @Test
    void test_allOf() {
        // Test with an empty iterable
        assertNull(allOf(emptyList()).join());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertNull(allOf(singletonList(future1)).join());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        // Test with multiple completed futures
        RuntimeException rte = new RuntimeException();
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {throw rte;});
        CompletableFuture<Void> future3 = CompletableFuture.runAsync(() -> sleep(LONG_WAIT_MS));
        assertCfStillIncompleteIn(allOf(asList(future1, future2, future3)), MEDIAN_WAIT_MS, MILLISECONDS);
    }

    @Test
    void test_anySuccessOf() {
        // Test with an empty iterable
        assertCfWithExType(anySuccessOf(emptyList()), NoCfsProvidedException.class);

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", anySuccessOf(singletonList(future1)).join());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

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
        assertCfStillIncompleteIn(anyOf(emptyList()));

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", anyOf(singletonList(future1)).join());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(() -> {throw new RuntimeException();});
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            sleep(MEDIAN_WAIT_MS);
            return "";
        });
        assertTrue(asList("result1", "result2", "result3").contains(anyOf(asList(future1, future2, future3)).join()));
    }

    @Test
    void test_thenMApply() throws Exception {
        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);

        assertThat(thenMApplyFailFastAsync(cf, emptyList()).get()).isEmpty();
        assertThat(thenMApplyFailFastAsync(cf, asList(v -> v + 1, v -> v + 2)).get()).containsExactly(n + 1, n + 2);
        assertThat(thenMApplyFailFastAsync(cf, asList(v -> v + 1, v -> v + 2), testExecutor).get()).containsExactly(n + 1, n + 2);

        assertThat(thenMApplyAllSuccessAsync(cf, -1, emptyList()).get()).isEmpty();
        assertThat(thenMApplyAllSuccessAsync(cf, -1,
                asList(v -> {throw new RuntimeException();}, v -> v + 2)).get())
                .containsExactly(-1, n + 2);
        assertThat(thenMApplyAllSuccessAsync(cf, -1,
                asList(v -> {throw new RuntimeException();}, v -> v + 2), testExecutor).get())
                .containsExactly(-1, n + 2);

        assertThat(thenMApplyMostSuccessAsync(cf, -1, 1, SECONDS, emptyList()).get()).isEmpty();
        assertThat(thenMApplyMostSuccessAsync(cf, -1, 1, SECONDS,
                asList(v -> {throw new RuntimeException();}, v -> v + 2)).get())
                .containsExactly(-1, n + 2);
        assertThat(thenMApplyMostSuccessAsync(cf, -1, 1, SECONDS,
                asList(v -> {throw new RuntimeException();}, v -> v + 2), testExecutor).get())
                .containsExactly(-1, n + 2);

        assertThat(thenMApplyAsync(cf, emptyList()).get()).isEmpty();
        assertThat(thenMApplyAsync(cf, asList(v -> v + 1, v -> v + 2)).get()).containsExactly(n + 1, n + 2);
        assertThat(thenMApplyAsync(cf, asList(v -> v + 1, v -> v + 2), testExecutor).get()).containsExactly(n + 1, n + 2);

        assertCfWithExType(thenMApplyAnySuccessAsync(cf, emptyList()), NoCfsProvidedException.class);
        assertEquals(2, thenMApplyAnySuccessAsync(cf, asList(v -> {
            throw new RuntimeException();
        }, v -> 2)).get());
        assertEquals(2, thenMApplyAnySuccessAsync(cf, asList(v -> {
            throw new RuntimeException();
        }, v -> 2), testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(thenMApplyAnyAsync(cf, emptyList()));
        assertEquals(2, thenMApplyAnyAsync(cf, asList(v -> {
            sleep(MEDIAN_WAIT_MS);
            return 1;
        }, v -> 2)).get());
        assertEquals(2, thenMApplyAnyAsync(cf, asList(v -> {
            sleep(MEDIAN_WAIT_MS);
            return 1;
        }, v -> 2), testExecutor).get());
    }

    @Test
    void test_thenMAccept() throws Exception {
        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        assertNull(thenMAcceptFailFastAsync(cf, emptyList()).get());
        assertNull(thenMAcceptFailFastAsync(cf, asList(v -> {}, v -> {})).get());
        assertNull(thenMAcceptFailFastAsync(cf, asList(v -> {}, v -> {}), testExecutor).get());

        assertNull(thenMAcceptAsync(cf, emptyList()).get());
        assertNull(thenMAcceptAsync(cf, asList(v -> {}, v -> {})).get());
        assertNull(thenMAcceptAsync(cf, asList(v -> {}, v -> {}), testExecutor).get());

        assertCfWithExType(thenMAcceptAnySuccessAsync(cf, emptyList()), NoCfsProvidedException.class);
        assertNull(thenMAcceptAnySuccessAsync(cf, asList(v -> {
            throw new RuntimeException();
        }, v -> {})).get());
        assertNull(thenMAcceptAnySuccessAsync(cf, asList(v -> {
            throw new RuntimeException();
        }, v -> {}), testExecutor).get());

        assertCfStillIncompleteIn(thenMAcceptAnyAsync(cf, emptyList()));
        assertNull(thenMAcceptAnyAsync(cf, asList(v -> {}, v -> {})).get());
        assertNull(thenMAcceptAnyAsync(cf, asList(v -> {}, v -> {}), testExecutor).get());
    }

    @Test
    void test_thenMRun() throws Exception {
        final CompletableFuture<Integer> cf = CompletableFuture.completedFuture(n);
        assertNull(thenMRunFailFastAsync(cf, emptyList()).get());
        assertNull(thenMRunFailFastAsync(cf, asList(() -> {}, () -> {})).get());
        assertNull(thenMRunFailFastAsync(cf, asList(() -> {}, () -> {}), testExecutor).get());

        assertNull(thenMRunAsync(cf, emptyList()).get());
        assertNull(thenMRunAsync(cf, asList(() -> {}, () -> {})).get());
        assertNull(thenMRunAsync(cf, asList(() -> {}, () -> {}), testExecutor).get());

        assertCfWithExType(thenMRunAnySuccessAsync(cf, emptyList()), NoCfsProvidedException.class);
        assertNull(thenMRunAnySuccessAsync(cf, asList(() -> {
            throw new RuntimeException();
        }, () -> {})).get());
        assertNull(thenMRunAnySuccessAsync(cf, asList(() -> {
            throw new RuntimeException();
        }, () -> {}), testExecutor).get());

        assertCfStillIncompleteIn(thenMRunAnyAsync(cf, emptyList()));
        assertNull(thenMRunAnyAsync(cf, asList(() -> {}, () -> {})).get());
        assertNull(thenMRunAnyAsync(cf, asList(() -> {}, () -> {}), testExecutor).get());
    }

}
