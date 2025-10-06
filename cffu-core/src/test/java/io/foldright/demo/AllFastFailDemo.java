package io.foldright.demo;

import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.CompletableFutureUtils;
import io.foldright.cffu2.MCffu;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AllFastFailDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // CffuFactory#allResultsFailFastOf
        //////////////////////////////////////////////////
        Cffu<Integer> cffu1 = cffuFactory.supplyAsync(() -> {
            // a simulating long-running computation...
            sleep(2_000);
            return 42;
        });
        Cffu<Integer> cffu2 = cffuFactory.supplyAsync(() -> {
            // a simulating fast-failure computation...
            sleep(1);
            throw new RuntimeException();
        });

        MCffu<Integer, List<Integer>> allResultsFailFast = cffuFactory.allResultsFailFastOf(cffu1, cffu2);
        // fail-fast without waiting long-running cffu1
        try {
            allResultsFailFast.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        // output: RuntimeException

        MCffu<Integer, List<Integer>> all = cffuFactory.allResultsOf(cffu1, cffu2);
        // same failure result as allResultsFailFast but waiting long-running cffu1...
        try {
            all.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        // output: RuntimeException

        //////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsFailFast
        //////////////////////////////////////////////////
        CompletableFuture<Integer> cf1 = CompletableFuture.supplyAsync(() -> {
            // a simulating long-running computation...
            sleep(2_000);
            return 42;
        });
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            // a simulating fast-failure computation...
            sleep(1);
            throw new RuntimeException();
        });

        CompletableFuture<List<Integer>> allResultsFailFastCf = CompletableFutureUtils.allResultsFailFastOf(cf1, cf2);
        // fail-fast without waiting long-running cf1
        try {
            allResultsFailFastCf.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        // output: RuntimeException

        CompletableFuture<List<Integer>> allCf = CompletableFutureUtils.allResultsOf(cf1, cf2);
        // same failure result as allResultsFailFastCf but waiting long-running cf1...
        try {
            allCf.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        // output: RuntimeException

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
