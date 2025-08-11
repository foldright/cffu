package io.foldright.cffu2;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;

import static io.foldright.cffu2.CfParallelUtils.*;
import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.MEDIAN_WAIT_MS;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@SuppressWarnings("resource")
class CfParallelUtilsTest {
    @Test
    void test_parApply() throws Exception {
        assertThat(parApplyFailFastAsync(emptyList(), (Integer x) -> x + 1).get()).isEmpty();
        assertThat(parApplyFailFastAsync(asList(1, 2), x -> x + 1).get()).containsExactly(2, 3);
        assertThat(parApplyFailFastAsync(asList(1, 2), x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertThat(parApplyAllSuccessAsync(emptyList(), -1, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(parApplyAllSuccessAsync(asList(1, 2), -1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(parApplyAllSuccessAsync(asList(1, 2), -1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(parApplyMostSuccessAsync(emptyList(), -1, 1, SECONDS, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(parApplyMostSuccessAsync(asList(1, 2), -1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(parApplyMostSuccessAsync(asList(1, 2), -1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(parApplyAsync(emptyList(), (Integer x) -> x + 1).get()).isEmpty();
        assertThat(parApplyAsync(asList(1, 2), x -> x + 1).get()).containsExactly(2, 3);
        assertThat(parApplyAsync(asList(1, 2), x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertCfWithExType(parApplyAnySuccessAsync(emptyList(), (Integer x) -> x + 1), NoCfsProvidedException.class);
        assertEquals(3, parApplyAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get());
        assertEquals(3, parApplyAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(parApplyAnyAsync(emptyList(), (Integer x) -> x + 1));
        assertEquals(3, parApplyAnyAsync(asList(1, 2), x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }).get());
        assertEquals(3, parApplyAnyAsync(asList(1, 2), x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }, testExecutor).get());
    }

    @Test
    void test_parAccept() throws Exception {
        assertNull(parAcceptFailFastAsync(emptyList(), (Integer x) -> {}).get());
        assertNull(parAcceptFailFastAsync(asList(1, 2), x -> {}).get());
        assertNull(parAcceptFailFastAsync(asList(1, 2), x -> {}, testExecutor).get());

        assertNull(parAcceptAsync(emptyList(), (Integer x) -> {}).get());
        assertNull(parAcceptAsync(asList(1, 2), x -> {}).get());
        assertNull(parAcceptAsync(asList(1, 2), x -> {}, testExecutor).get());

        assertCfWithExType(parAcceptAnySuccessAsync(emptyList(), (Integer x) -> {}), NoCfsProvidedException.class);
        assertNull(parAcceptAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
        }).get());
        assertNull(parAcceptAnySuccessAsync(asList(1, 2), x -> {
            if (x == 1) throw new RuntimeException();
        }, testExecutor).get());

        assertCfStillIncompleteIn(parAcceptAnyAsync(emptyList(), (Integer x) -> {}));
        assertNull(parAcceptAnyAsync(asList(1, 2), x -> {}).get());
        assertNull(parAcceptAnyAsync(asList(1, 2), x -> {}, testExecutor).get());
    }

    @Test
    void test_thenParApply() throws Exception {
        final CompletableFuture<List<Integer>> cfEmpty = CompletableFuture.completedFuture(emptyList());
        final CompletableFuture<List<Integer>> cf = CompletableFuture.completedFuture(asList(1, 2));

        assertThat(thenParApplyFailFastAsync(cfEmpty, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(thenParApplyFailFastAsync(cf, x -> x + 1).get()).containsExactly(2, 3);
        assertThat(thenParApplyFailFastAsync(cf, x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertThat(thenParApplyAllSuccessAsync(cfEmpty, -1, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(thenParApplyAllSuccessAsync(cf, -1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(thenParApplyAllSuccessAsync(cf, -1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(thenParApplyMostSuccessAsync(cfEmpty, -1, 1, SECONDS, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(thenParApplyMostSuccessAsync(cf, -1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(thenParApplyMostSuccessAsync(cf, -1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(thenParApplyAsync(cfEmpty, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(thenParApplyAsync(cf, x -> x + 1).get()).containsExactly(2, 3);
        assertThat(thenParApplyAsync(cf, x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertCfWithExType(thenParApplyAnySuccessAsync(cfEmpty, (Integer x) -> x + 1), NoCfsProvidedException.class);
        assertEquals(3, thenParApplyAnySuccessAsync(cf, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get());
        assertEquals(3, thenParApplyAnySuccessAsync(cf, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(thenParApplyAnyAsync(cfEmpty, (Integer x) -> x + 1));
        assertEquals(3, thenParApplyAnyAsync(cf, x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }).get());
        assertEquals(3, thenParApplyAnyAsync(cf, x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }, testExecutor).get());
    }

    @Test
    void test_thenParAccept() throws Exception {
        final CompletableFuture<List<Integer>> cfEmpty = CompletableFuture.completedFuture(emptyList());
        final CompletableFuture<List<Integer>> cf = CompletableFuture.completedFuture(asList(1, 2));

        assertNull(thenParAcceptFailFastAsync(cfEmpty, (Integer x) -> {}).get());
        assertNull(thenParAcceptFailFastAsync(cf, x -> {}).get());
        assertNull(thenParAcceptFailFastAsync(cf, x -> {}, testExecutor).get());

        assertNull(thenParAcceptAsync(cfEmpty, (Integer x) -> {}).get());
        assertNull(thenParAcceptAsync(cf, x -> {}).get());
        assertNull(thenParAcceptAsync(cf, x -> {}, testExecutor).get());

        assertCfWithExType(thenParAcceptAnySuccessAsync(cfEmpty, (Integer x) -> {}), NoCfsProvidedException.class);
        assertNull(thenParAcceptAnySuccessAsync(cf, x -> {
            if (x == 1) throw new RuntimeException();
        }).get());
        assertNull(thenParAcceptAnySuccessAsync(cf, x -> {
            if (x == 1) throw new RuntimeException();
        }, testExecutor).get());

        assertCfStillIncompleteIn(thenParAcceptAnyAsync(cfEmpty, (Integer x) -> {}));
        assertNull(thenParAcceptAnyAsync(cf, x -> {}).get());
        assertNull(thenParAcceptAnyAsync(cf, x -> {}, testExecutor).get());
    }
}
