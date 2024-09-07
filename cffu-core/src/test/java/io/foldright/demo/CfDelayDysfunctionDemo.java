package io.foldright.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static io.foldright.cffu.CompletableFutureUtils.allOf;
import static io.foldright.cffu.CompletableFutureUtils.cffuOrTimeout;
import static io.foldright.test_utils.TestUtils.sleep;
import static java.lang.System.currentTimeMillis;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * This demo shows the dysfunction of CompletableFuture Timeout.
 * <p>
 * Needs Java 9+ to run, because uses the new {@link CompletableFuture#orTimeout} method since Java 9.
 * <p>
 * Run by maven: {@code
 * mvn -pl cffu-core test-compile exec:exec -Dexec.mainClass=io.foldright.demo.CfDelayDysfunctionDemo
 * }
 */
public class CfDelayDysfunctionDemo {
    public static void main(String[] args) {
        warmupPools();

        System.out.println();
        dysfunctionDemo();

        System.out.println();
        cffuOrTimeoutFixDysfunctionDemo();
    }

    private static void dysfunctionDemo() {
        logWithTimeAndThread("dysfunctionDemo begin");

        final int size = 3;
        final CompletableFuture<?>[] subsequentCfs = new CompletableFuture<?>[size];
        final long tick = currentTimeMillis();
        for (int i = 0; i < size; i++) {
            final int idx = i;
            logWithTimeAndThread("[%d] start a 1-second-sleep task after 100ms timeout", idx);

            CompletableFuture<?> cf = new CompletableFuture<Void>().orTimeout(100, MILLISECONDS)
                    .handle((v, ex) -> {
                        logWithTimeAndThread("[%d] triggered 1-second-sleep task after 100ms timeout", idx);
                        sleep(1000);
                        logWithTimeAndThread("[%d] 1-second-sleep end", idx);
                        return null;
                    });

            subsequentCfs[idx] = cf;
        }

        allOf(subsequentCfs).join();
        // end in ~3.1 seconds(UNEXPECTED❗️), because all 3 subsequent tasks is executed
        // in the SINGLE thread CompletableFutureDelayScheduler!
        logWithTimeAndThread("dysfunctionDemo end in %dms", currentTimeMillis() - tick);
    }

    private static void cffuOrTimeoutFixDysfunctionDemo() {
        logWithTimeAndThread("cffuOrTimeoutFixDysfunctionDemo begin");

        final int size = 3;
        final CompletableFuture<?>[] subsequentCfs = new CompletableFuture<?>[size];
        final long tick = currentTimeMillis();
        for (int i = 0; i < size; i++) {
            final int idx = i;
            logWithTimeAndThread("[%d] start a 1-second-sleep task after 100ms timeout", idx);

            CompletableFuture<?> cf = cffuOrTimeout(new CompletableFuture<Void>(), 100, MILLISECONDS)
                    .handle((v, ex) -> {
                        logWithTimeAndThread("[%d] triggered 1-second-sleep task after 100ms timeout", idx);
                        sleep(1000);
                        logWithTimeAndThread("[%d] 1-second-sleep end", idx);
                        return null;
                    });

            subsequentCfs[idx] = cf;
        }

        allOf(subsequentCfs).join();
        // end in ~1.1 seconds(expected✅), because 3 subsequent tasks is executed
        // in the commonPool(customizable by the executor param of cffuOrTimeout)
        logWithTimeAndThread("cffuOrTimeoutFixDysfunctionDemo end in %dms", currentTimeMillis() - tick);
    }

    private static void logWithTimeAndThread(String format, Object... args) {
        String msg = String.format(format, args);
        System.out.printf("%tF %<tT.%<tL |%s| %s%n", currentTimeMillis(), Thread.currentThread().getName(), msg);
    }

    private static void warmupPools() {
        logWithTimeAndThread("warmup pools begin");

        final int size = 5;
        final List<CompletableFuture<?>> subsequentCfs = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            CompletableFuture<?> cf = new CompletableFuture<Void>().orTimeout(10, MILLISECONDS)
                    .handle((v, ex) -> {
                        sleep(3);
                        return null;
                    });
            subsequentCfs.add(cf);

            cf = cffuOrTimeout(new CompletableFuture<Void>(), 10, MILLISECONDS)
                    .handle((v, ex) -> {
                        sleep(3);
                        return null;
                    });
            subsequentCfs.add(cf);
        }

        allOf(subsequentCfs.toArray(new CompletableFuture<?>[0])).join();
        logWithTimeAndThread("warmup pools end");
    }
}

/*
Output Sample:

2024-09-01 00:00:00.223 |main| warmup pools begin
2024-09-01 00:00:00.346 |main| warmup pools end

2024-09-01 00:00:00.347 |main| dysfunctionDemo begin
2024-09-01 00:00:00.347 |main| [0] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:00.348 |main| [1] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:00.349 |main| [2] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:00.451 |CompletableFutureDelayScheduler| [0] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:01.454 |CompletableFutureDelayScheduler| [0] 1-second-sleep end
2024-09-01 00:00:01.454 |CompletableFutureDelayScheduler| [1] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:02.458 |CompletableFutureDelayScheduler| [1] 1-second-sleep end
2024-09-01 00:00:02.458 |CompletableFutureDelayScheduler| [2] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.464 |CompletableFutureDelayScheduler| [2] 1-second-sleep end
2024-09-01 00:00:03.464 |main| dysfunctionDemo end in 3117ms

2024-09-01 00:00:03.464 |main| cffuOrTimeoutFixDysfunctionDemo begin
2024-09-01 00:00:03.465 |main| [0] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.466 |main| [1] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.466 |main| [2] start a 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.567 |ForkJoinPool.commonPool-worker-2| [1] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.567 |ForkJoinPool.commonPool-worker-1| [0] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:03.567 |ForkJoinPool.commonPool-worker-3| [2] triggered 1-second-sleep task after 100ms timeout
2024-09-01 00:00:04.569 |ForkJoinPool.commonPool-worker-1| [0] 1-second-sleep end
2024-09-01 00:00:04.569 |ForkJoinPool.commonPool-worker-3| [2] 1-second-sleep end
2024-09-01 00:00:04.569 |ForkJoinPool.commonPool-worker-2| [1] 1-second-sleep end
2024-09-01 00:00:04.571 |main| cffuOrTimeoutFixDysfunctionDemo end in 1106ms

 */
