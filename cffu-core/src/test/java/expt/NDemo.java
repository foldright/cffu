package expt;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.lang.System.currentTimeMillis;


public class NDemo {
    public static void main(String[] args) {
        final Runnable runnable = () -> {
            sleep(100 + ThreadLocalRandom.current().nextInt(100));
            logWithTimeAndThread("run");
        };
        N(new CopyOnWriteArrayList<>(Arrays.asList(
                runnable, runnable, runnable,
                runnable, runnable, runnable,
                runnable, runnable, runnable,
                runnable, runnable, runnable
        )), 3).join();
    }

    /**
     * @param tasks       先使用 {@link CopyOnWriteArrayList}以不考虑集合的并发问题
     * @param parallelism 并行度
     */
    private static CompletableFuture<Void> N(CopyOnWriteArrayList<Runnable> tasks, int parallelism) {
        final Iterator<Runnable> iterator = tasks.iterator();
        CompletableFuture<?>[] cfs = new CompletableFuture[parallelism];
        for (int i = 0; i < parallelism; i++) {
            if (!iterator.hasNext()) break;
            try {
                final CompletableFuture<Void> f = CompletableFuture.runAsync(iterator.next());
                final CompletableFuture<Void> relay = relay(f, iterator);
                cfs[i] = relay;
            } catch (NoSuchElementException e) {
                // 先简单处理；实现合适的同步逻辑后，不再会有这个异常
                break;
            }
        }
        return CompletableFuture.allOf(cfs);
    }

    private static CompletableFuture<Void> relay(CompletableFuture<Void> cf, Iterator<Runnable> iterator) {
        if (!iterator.hasNext()) return cf;
        // 这里要 relayAsync方式运行，否则对于快速完成 CF，会泄漏到 Caller线程中执行
        // 也可以 new 一个 未开始的CF 作为信号，挂载完成后 再完成 信号CF
        return cf.whenComplete((r, e) -> {
            while (iterator.hasNext()) {
                try {
                    iterator.next().run();
                } catch (NoSuchElementException e0) {
                    // 先简单处理；实现合适的同步逻辑后，不再会有这个异常
                }
            }
        });
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private static void logWithTimeAndThread(String format, Object... args) {
        String msg = String.format(format, args);
        System.out.printf("%tF %<tT.%<tL |%s| %s%n", currentTimeMillis(), Thread.currentThread().getName(), msg);
    }
}
