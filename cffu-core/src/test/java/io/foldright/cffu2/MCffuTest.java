package io.foldright.cffu2;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.ForkJoinPool;

import static io.foldright.test_utils.TestUtils.*;
import static io.foldright.test_utils.TestingConstants.MEDIAN_WAIT_MS;
import static io.foldright.test_utils.TestingExecutorUtils.testCffuFac;
import static io.foldright.test_utils.TestingExecutorUtils.testExecutor;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class MCffuTest {
    // region# Conversion Methods

    @Test
    void test_asCffu() throws Exception {
        final List<Integer> list = asList(1, 2);

        final MCffu<Integer, List<Integer>> mCffu = testCffuFac.completedMCffu(list);
        final Cffu<List<Integer>> cffu = mCffu.asCffu();

        assertSame(testCffuFac, cffu.cffuFactory());
        assertFalse(cffu.isMinimalStage);
        assertSame(list, cffu.get());

        final List<Integer> list3 = asList(1, 2, 3);
        mCffu.obtrudeValue(list3);
        assertSame(list3, cffu.get());
    }

    // endregion
    // region# More Ops

    @Test
    void test_thenParApply() throws Exception {
        final MCffu<Integer, List<Integer>> cfEmpty = testCffuFac.completedMCffu(emptyList());
        final MCffu<Integer, List<Integer>> cf = testCffuFac.completedMCffu(asList(1, 2));

        assertThat(cfEmpty.parOps().thenParApplyFailFastAsync((Integer x) -> x + 1).get()).isEmpty();
        assertThat(cf.parOps().thenParApplyFailFastAsync(x -> x + 1).get()).containsExactly(2, 3);
        assertThat(cf.parOps().thenParApplyFailFastAsync(x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertThat(cfEmpty.parOps().thenParApplyAllSuccessAsync(-1, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(cf.parOps().thenParApplyAllSuccessAsync(-1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(cf.parOps().thenParApplyAllSuccessAsync(-1, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(cfEmpty.parOps().thenParApplyMostSuccessAsync(-1, 1, SECONDS, (Integer x) -> x + 1).get()).isEmpty();
        assertThat(cf.parOps().thenParApplyMostSuccessAsync(-1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get()).containsExactly(-1, 3);
        assertThat(cf.parOps().thenParApplyMostSuccessAsync(-1, 1, SECONDS, x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get()).containsExactly(-1, 3);

        assertThat(cfEmpty.parOps().thenParApplyAsync((Integer x) -> x + 1).get()).isEmpty();
        assertThat(cf.parOps().thenParApplyAsync(x -> x + 1).get()).containsExactly(2, 3);
        assertThat(cf.parOps().thenParApplyAsync(x -> x + 1, testExecutor).get()).containsExactly(2, 3);

        assertCfWithExType(cfEmpty.parOps().thenParApplyAnySuccessAsync((Integer x) -> x + 1), NoCfsProvidedException.class);
        assertEquals(3, cf.parOps().thenParApplyAnySuccessAsync(x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }).get());
        assertEquals(3, cf.parOps().thenParApplyAnySuccessAsync(x -> {
            if (x == 1) throw new RuntimeException();
            return x + 1;
        }, testExecutor).get());

        assertTrue(ForkJoinPool.commonPool().awaitQuiescence(2, MINUTES));

        assertCfStillIncompleteIn(cfEmpty.parOps().thenParApplyAnyAsync((Integer x) -> x + 1));
        assertEquals(3, cf.parOps().thenParApplyAnyAsync(x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }).get());
        assertEquals(3, cf.parOps().thenParApplyAnyAsync(x -> {
            if (x == 1) sleep(MEDIAN_WAIT_MS);
            return x + 1;
        }, testExecutor).get());
    }

    @Test
    void test_thenParAccept() throws Exception {
        final MCffu<Integer, List<Integer>> cfEmpty = testCffuFac.completedMCffu(emptyList());
        final MCffu<Integer, List<Integer>> cf = testCffuFac.completedMCffu(asList(1, 2));

        assertNull(cfEmpty.parOps().thenParAcceptFailFastAsync((Integer x) -> {}).get());
        assertNull(cf.parOps().thenParAcceptFailFastAsync(x -> {}).get());
        assertNull(cf.parOps().thenParAcceptFailFastAsync(x -> {}, testExecutor).get());

        assertNull(cfEmpty.parOps().thenParAcceptAsync((Integer x) -> {}).get());
        assertNull(cf.parOps().thenParAcceptAsync(x -> {}).get());
        assertNull(cf.parOps().thenParAcceptAsync(x -> {}, testExecutor).get());

        assertCfWithExType(cfEmpty.parOps().thenParAcceptAnySuccessAsync((Integer x) -> {}), NoCfsProvidedException.class);
        assertNull(cf.parOps().thenParAcceptAnySuccessAsync(x -> {
            if (x == 1) throw new RuntimeException();
        }).get());
        assertNull(cf.parOps().thenParAcceptAnySuccessAsync(x -> {
            if (x == 1) throw new RuntimeException();
        }, testExecutor).get());

        assertCfStillIncompleteIn(cfEmpty.parOps().thenParAcceptAnyAsync((Integer x) -> {}));
        assertNull(cf.parOps().thenParAcceptAnyAsync(x -> {}).get());
        assertNull(cf.parOps().thenParAcceptAnyAsync(x -> {}, testExecutor).get());
    }
}
