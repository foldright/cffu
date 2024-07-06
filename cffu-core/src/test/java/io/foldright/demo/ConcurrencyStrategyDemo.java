package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.foldright.cffu.CompletableFutureUtils.*;


public class ConcurrencyStrategyDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////
        // CffuFactory#allResultsFastFailOf
        // CffuFactory#anySuccessOf
        // CffuFactory#mostSuccessResultsOf
        ////////////////////////////////////////////////////////////////////////
        final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 42;
        });
        final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

        Cffu<List<Integer>> fastFailed = cffuFactory.allResultsFastFailOf(successAfterLongTime, failed);
        // fast failed without waiting successAfterLongTime
        System.out.println(fastFailed.exceptionNow());

        Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(successAfterLongTime, failed);
        System.out.println(anySuccess.get());

        Cffu<List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
                0, 100, TimeUnit.MILLISECONDS, successAfterLongTime, failed);
        System.out.println(mostSuccess.get());

        ////////////////////////////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsFastFailOf
        //    CompletableFutureUtils#anySuccessOf
        //    CompletableFutureUtils#mostSuccessResultsOf
        ////////////////////////////////////////////////////////////////////////
        final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 42;
        });
        final CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

        CompletableFuture<List<Integer>> fastFailed2 = allResultsFastFailOf(successAfterLongTimeCf, failedCf);
        // fast failed without waiting successAfterLongTime
        System.out.println(exceptionNow(fastFailed2));

        CompletableFuture<Integer> anySuccess2 = anySuccessOf(successAfterLongTimeCf, failedCf);
        System.out.println(anySuccess2.get());

        CompletableFuture<List<Integer>> mostSuccess2 = mostSuccessResultsOf(
                0, 100, TimeUnit.MILLISECONDS, successAfterLongTime, failed);
        System.out.println(mostSuccess2.get());

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
