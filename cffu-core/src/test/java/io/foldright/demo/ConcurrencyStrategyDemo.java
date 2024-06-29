package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CompletableFutureUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ConcurrencyStrategyDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////
        // CffuFactory#allOfFastFail / allResultsOfFastFail
        // CffuFactory#anySuccessOf
        ////////////////////////////////////////////////////////////////////////
        final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 42;
        });
        final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

        // Result type is Void!
        Cffu<Void> cffuAll = cffuFactory.allOfFastFail(successAfterLongTime, failed);

        Cffu<List<Integer>> fastFailed = cffuFactory.allResultsOfFastFail(successAfterLongTime, failed);
        // fast failed without waiting successAfterLongTime
        System.out.println(fastFailed.exceptionNow());

        Cffu<Integer> anySuccessOf = cffuFactory.anySuccessOf(successAfterLongTime, failed);
        System.out.println(anySuccessOf.get());

        ////////////////////////////////////////////////////////////////////////
        // or CompletableFutureUtils#allOfFastFail / allResultsOfFastFail
        //    CompletableFutureUtils#anySuccessOf
        ////////////////////////////////////////////////////////////////////////
        final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 42;
        });
        final CompletableFuture<Integer> failedCf = CompletableFutureUtils.failedFuture(new RuntimeException("Bang!"));

        // Result type is Void!
        CompletableFuture<Void> cfAll = CompletableFutureUtils.allOfFastFail(successAfterLongTimeCf, failedCf);

        CompletableFuture<List<Integer>> fastFailedCf = CompletableFutureUtils.allResultsOfFastFail(successAfterLongTimeCf, failedCf);
        // fast failed without waiting successAfterLongTime
        System.out.println(CompletableFutureUtils.exceptionNow(fastFailedCf));

        CompletableFuture<Integer> cfSuccess = CompletableFutureUtils.anySuccessOf(successAfterLongTimeCf, failedCf);
        System.out.println(cfSuccess.get());

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
