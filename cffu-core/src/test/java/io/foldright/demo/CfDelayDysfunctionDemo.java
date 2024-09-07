package io.foldright.demo;

import java.util.concurrent.CompletableFuture;

import static io.foldright.cffu.CompletableFutureUtils.cffuOrTimeout;
import static io.foldright.test_utils.TestUtils.sleep;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


public class CfDelayDysfunctionDemo {
    public static void main(String[] args) {
        dysfunctionDemo();

        System.out.println();

        cffuOrTimeoutFixDysfunctionDemo();
    }

    private static void dysfunctionDemo() {
        logWithTimeAndThread("dysfunctionDemo begin");

        final CompletableFuture<Void> incomplete = new CompletableFuture<>();

        final int size = 3;
        final CompletableFuture<?>[] subsequentCfs = new CompletableFuture<?>[size];
        final long tick = currentTimeMillis();
        for (int i = 0; i < size; i++) {
            final int idx = i;
            logWithTimeAndThread("[" + idx + "] start a 1-second-sleep task after 100ms timeout");

            CompletableFuture<?> cf = incomplete.orTimeout(100, MILLISECONDS).handle((v, ex) -> {
                logWithTimeAndThread("[" + idx + "] triggered 1-second-sleep task after 100ms timeout");
                sleep(1000);
                logWithTimeAndThread("[" + idx + "] 1-second-sleep end");
                return null;
            });

            subsequentCfs[idx] = cf;
        }

        CompletableFuture.allOf(subsequentCfs).join();
        // end in ~3.1 seconds(UNEXPECTED), because all 3 subsequent tasks is executed
        // in the SINGLE thread CompletableFutureDelayScheduler!
        logWithTimeAndThread("dysfunctionDemo end in " + (currentTimeMillis() - tick) + "ms");
    }

    private static void cffuOrTimeoutFixDysfunctionDemo() {
        logWithTimeAndThread("cffuOrTimeoutFixDysfunctionDemo begin");

        final CompletableFuture<Void> incomplete = new CompletableFuture<>();

        final int size = 3;
        final CompletableFuture<?>[] subsequentCfs = new CompletableFuture<?>[size];
        final long tick = currentTimeMillis();
        for (int i = 0; i < size; i++) {
            final int idx = i;
            logWithTimeAndThread("[" + idx + "] start a 1-second-sleep task after 100ms timeout");

            CompletableFuture<?> cf = cffuOrTimeout(incomplete, 100, MILLISECONDS).handle((v, ex) -> {
                logWithTimeAndThread("[" + idx + "] triggered 1-second-sleep task after 100ms timeout");
                sleep(1000);
                logWithTimeAndThread("[" + idx + "] 1-second-sleep end");
                return null;
            });

            subsequentCfs[idx] = cf;
        }

        CompletableFuture.allOf(subsequentCfs).join();
        // end in ~1.1 seconds(expected), because 3 subsequent tasks is executed in
        // the commonPool(customizable by the executor param of cffuOrTimeout)
        logWithTimeAndThread("cffuOrTimeoutFixDysfunctionDemo end in " + (currentTimeMillis() - tick) + "ms");
    }

    private static void logWithTimeAndThread(String msg) {
        System.out.printf("%tF %<tT.%<tL |%s| %s%n",
                currentTimeMillis(), Thread.currentThread().getName(), msg);
    }
}

/*
Output Example:

2024-09-01 00:00:01.505 |main| dysfunctionDemo begin
2024-09-01 00:00:01.601 |main| [0] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:01.606 |main| [1] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:01.607 |main| [2] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:01.707 |CompletableFutureDelayScheduler| [2] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:02.717 |CompletableFutureDelayScheduler| [2] 1-second-sleep end
2024-09-01 00:00:02.718 |CompletableFutureDelayScheduler| [1] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.721 |CompletableFutureDelayScheduler| [1] 1-second-sleep end
2024-09-01 00:00:03.721 |CompletableFutureDelayScheduler| [0] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.724 |CompletableFutureDelayScheduler| [0] 1-second-sleep end
2024-09-01 00:00:04.725 |main| dysfunctionDemo end in 3124ms

2024-09-01 00:00:04.726 |main| cffuOrTimeoutFixDysfunctionDemo begin
2024-09-01 00:00:04.726 |main| [0] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.735 |main| [1] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.735 |main| [2] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.840 |ForkJoinPool.commonPool-worker-1| [2] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.840 |ForkJoinPool.commonPool-worker-2| [1] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.840 |ForkJoinPool.commonPool-worker-3| [0] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:05.842 |ForkJoinPool.commonPool-worker-1| [2] 1-second-sleep end
2024-09-01 00:00:05.842 |ForkJoinPool.commonPool-worker-2| [1] 1-second-sleep end
2024-09-01 00:00:05.846 |ForkJoinPool.commonPool-worker-3| [0] 1-second-sleep end
2024-09-01 00:00:05.846 |main| cffuOrTimeoutFixDysfunctionDemo end in 1120ms

 */
