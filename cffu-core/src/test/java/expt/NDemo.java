package expt;

import io.foldright.cffu2.CfIterableUtils;
import io.foldright.cffu2.CfParallelUtils;
import io.foldright.cffu2.LLCF;
import io.foldright.cffu2.internal.CommonUtils;
import io.foldright.cffu2.tuple.Tuple2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;

import static io.foldright.cffu2.internal.CommonUtils.toArray;


public class NDemo {
    public static void main(String[] args) {
        logWithTimeAndThread("start");
        CfParallelUtils.parAcceptAsync(Arrays.asList(1, 2, 3), i -> {
            sleep(100);
            logWithTimeAndThread("warmup %s", i);
        }).join();

        final ArrayList<Runnable> tasks = CommonUtils.arrayList(13, i -> () -> {
            logWithTimeAndThread("task %2s begin", i);
            final int millis = 200 + ThreadLocalRandom.current().nextInt(800);
            sleep(millis);
//             logWithTimeAndThread("task %2s end, sleep %sms", i, millis);
        });

        logWithTimeAndThread("CfIterableUtils.mRunAsyncN");
        CfIterableUtils.mRunAsyncN(tasks, 3).join();
        ForkJoinPool.commonPool().awaitQuiescence(3, TimeUnit.SECONDS);

        logWithTimeAndThread("CfIterableUtils.mRunAsyncN2");
        CfIterableUtils.mRunAsyncN2(tasks, 3).join();
        ForkJoinPool.commonPool().awaitQuiescence(3, TimeUnit.SECONDS);

        logWithTimeAndThread("N3");
        N3(tasks, 3).join();
        ForkJoinPool.commonPool().awaitQuiescence(3, TimeUnit.SECONDS);
    }

    private static CompletableFuture<Void> N3(Iterable<? extends Runnable> tasks, int parallelism) {
        final ArrayList<? extends Runnable> list = CommonUtils.toArrayList(tasks);
        final int size = list.size();
        if (size <= parallelism) return CfIterableUtils.mRunAsync(list);

        final CompletableFuture<Void>[] cfSlots = CommonUtils.newCfArray(size);
        // pre create cfs for rest tasks that completes later
        for (int i = parallelism; i < size; i++) {
            cfSlots[i] = new CompletableFuture<>();
        }

        final CompletableFuture<Void> latch = new CompletableFuture<>();

        final SafeIterator<? extends Runnable> restTasks = new SafeIterator<>(toArray(
                list.subList(parallelism, size), new Runnable[0]));
        final CompletableFuture<Void>[] restCfs = Arrays.copyOfRange(cfSlots, parallelism, size);
        for (int i = 0; i < parallelism; i++) {
            final CompletableFuture<Void> cf = latch.thenRunAsync(list.get(i));
            // set cfs of first batch tasks
            cfSlots[i] = cf;

            LLCF.peek0(cf, (unused, ex) -> {
                while (true) {
                    final Tuple2<Integer, ? extends Runnable> next = restTasks.next();
                    if (next == null) break;

                    final Integer index = next._1;
                    final Runnable task = next._2;

                    CompletableFuture<Void> f = restCfs[index];
                    // release memory as soon as possible
                    restCfs[index] = null;
                    LLCF.completeCf0(f, task);
                }
            }, "N3");
        }

        latch.complete(null);
        return CompletableFuture.allOf(cfSlots);
    }

    private static CompletableFuture<Void> N2(Iterable<? extends Runnable> tasks, int parallelism) {
        final Collection<? extends Runnable> coll = CommonUtils.toCollection(tasks);
        if (coll.size() <= parallelism) {
            return CfIterableUtils.mRunAsync(coll);
        }

        final Iterator<? extends Runnable> iterator = new CopyOnWriteArrayList<>(coll).iterator();
        return null;
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
                final CompletableFuture<Void> relay = relayRunRestTasks(f, iterator);
                cfs[i] = relay;
            } catch (NoSuchElementException e) {
                // 先简单处理；实现合适的同步逻辑后，不再会有这个异常
                break;
            }
        }
        return CompletableFuture.allOf(cfs);
    }

    private static CompletableFuture<Void> relayRunRestTasks(CompletableFuture<Void> cf, Iterator<Runnable> iterator) {
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

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

    private static void logWithTimeAndThread(String format, Object... args) {
        final LocalDateTime now = LocalDateTime.now();
        System.out.printf("%s |%s| %s%n", dateTimeFormatter.format(now),
                Thread.currentThread().getName(), String.format(format, args));
    }
}

class SafeIterator<T> {
    private final T[] elements;
    /**
     * Index of the element to be returned by subsequent call to syncNext.
     */
    private int cursor;

    SafeIterator(T[] elements) {
        this.elements = elements;
    }

    synchronized Tuple2<Integer, T> next() {
        if (cursor >= elements.length) return null;

        final Tuple2<Integer, T> ret = Tuple2.of(cursor, elements[cursor]);
        // release memory as soon as possible
        elements[cursor++] = null;
        return ret;
    }
}

