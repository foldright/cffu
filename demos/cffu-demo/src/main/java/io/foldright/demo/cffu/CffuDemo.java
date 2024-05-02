package io.foldright.demo.cffu;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class CffuDemo {
    private static final ExecutorService myBizThreadPool = Executors.newCachedThreadPool();
    // Create a CffuFactory with configuration of the customized thread pool
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizThreadPool).build();

    public static void main(String[] args) throws Exception {
        final Cffu<Integer> cf42 = cffuFactory
                .supplyAsync(() -> 21)  // Run in myBizThreadPool
                .thenApply(n -> n * 2);

        // Below tasks all run in myBizThreadPool
        final Cffu<Integer> longTaskA = cf42.thenApplyAsync(n -> {
            sleep(1001);
            return n / 2;
        });
        final Cffu<Integer> longTaskB = cf42.thenApplyAsync(n -> {
            sleep(1002);
            return n / 2;
        });
        final Cffu<Integer> longTaskC = cf42.thenApplyAsync(n -> {
            sleep(100);
            return n * 2;
        });
        final Cffu<Integer> longFailedTask = cf42.thenApplyAsync(unused -> {
            sleep(1000);
            throw new RuntimeException("Bang!");
        });

        final Cffu<Integer> combined = longTaskA.thenCombine(longTaskB, Integer::sum)
                .orTimeout(1500, TimeUnit.MILLISECONDS);
        System.out.println("combined result: " + combined.get());

        final Cffu<Integer> anyOfSuccess = cffuFactory.anyOfSuccess(longTaskC, longFailedTask);
        System.out.println("anyOfSuccess result: " + anyOfSuccess.get());

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizThreadPool.shutdown();
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
