package io.foldright.demo;

import io.foldright.cffu2.CfParallelUtils;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.CompletableFutureUtils;
import io.foldright.cffu2.MCffu;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * This shows the usage of Multiple Actions methods (<b>M</b> Methods).
 * <p>
 * Run by maven: {@code
 * mvn -pl cffu-core test-compile exec:exec -Dexec.mainClass=io.foldright.demo.MultipleActionsDemo
 * }
 */
public class CfParallelDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) {
        parApplyFailFastAsyncDemo();
        thenParApplyFailFastAsyncDemo();

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }

    static void parApplyFailFastAsyncDemo() {
        ////////////////////////////////////////////////////////////////////////
        // wrap data with action to CompletableFutures first, AWKWARD and COMPLEX! 😖
        ////////////////////////////////////////////////////////////////////////
        Function<Integer, Integer> fn = x -> x + 1;
        List<Integer> list = asList(42, 43, 44);

        @SuppressWarnings("unchecked")
        CompletableFuture<Integer>[] cfs = new CompletableFuture[list.size()];
        for (int i = 0; i < list.size(); i++) {
            Integer e = list.get(i);
            cfs[i] = CompletableFuture.supplyAsync(() -> fn.apply(e));
        }
        CompletableFutureUtils.allResultsFailFastOf(cfs).thenAccept(System.out::println);
        // output: [43, 44, 45]
        cffuFactory.allResultsFailFastOf(cfs).thenAccept(System.out::println);
        // output: [43, 44, 45]

        ////////////////////////////////////////////////////////////////////////
        // just parallel process multiple data, fresh and cool 😋
        ////////////////////////////////////////////////////////////////////////
        CfParallelUtils.parApplyFailFastAsync(
                asList(42, 43, 44),
                x -> x + 1
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]
        cffuFactory.parOps().parApplyFailFastAsync(
                asList(42, 43, 44),
                x -> x + 1
        ).thenAccept(System.out::println);
        // output: [43, 44, 45]

        sleep(1000);
    }

    static void thenParApplyFailFastAsyncDemo() {
        ////////////////////////////////////////////////////////////////////////
        // wrap data with action to CompletableFutures first, AWKWARD and COMPLEX! 😖
        ////////////////////////////////////////////////////////////////////////
        Function<Integer, Integer> fn = x -> x + 1;
        CompletableFuture<List<Integer>> cf = completedFuture(asList(42, 43, 44));

        cf.thenCompose(list -> {
            @SuppressWarnings("unchecked")
            CompletableFuture<Integer>[] cfs = new CompletableFuture[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Integer e = list.get(i);
                cfs[i] = CompletableFuture.supplyAsync(() -> fn.apply(e));
            }
            return CompletableFutureUtils.allResultsFailFastOf(cfs);
        }).thenAccept(System.out::println);
        // output: [43, 44, 45]
        MCffu<Integer, List<Integer>> mCffu = cffuFactory.completedMCffu(asList(42, 43, 44));
        mCffu.thenCompose(list -> {
            @SuppressWarnings("unchecked")
            CompletableFuture<Integer>[] cfs = new CompletableFuture[list.size()];
            for (int i = 0; i < list.size(); i++) {
                Integer e = list.get(i);
                cfs[i] = CompletableFuture.supplyAsync(() -> fn.apply(e));
            }
            return CompletableFutureUtils.allResultsFailFastOf(cfs);
        }).thenAccept(System.out::println);
        // output: [43, 44, 45]

        ////////////////////////////////////////////////////////////////////////
        // just parallel process multiple data, fresh and cool 😋
        ////////////////////////////////////////////////////////////////////////
        CfParallelUtils.thenParApplyFailFastAsync(cf, x -> x + 1)
                .thenAccept(System.out::println);
        // output: [43, 44, 45]
        mCffu.parOps().thenParApplyFailFastAsync(x -> x + 1)
                .thenAccept(System.out::println);
        // output: [43, 44, 45]

        sleep(1000);
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
