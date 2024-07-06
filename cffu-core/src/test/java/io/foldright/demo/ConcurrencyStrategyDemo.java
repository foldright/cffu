package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CompletableFutureUtils.*;


public class ConcurrencyStrategyDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////
        // CffuFactory#allResultsFastFailOf / allFastFailOf
        // CffuFactory#anySuccessOf
        ////////////////////////////////////////////////////////////////////////
        final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 42;
        });
        final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

        // Result type is Void!
        Cffu<Void> all = cffuFactory.allFastFailOf(successAfterLongTime, failed);

        Cffu<List<Integer>> fastFailed = cffuFactory.allResultsFastFailOf(successAfterLongTime, failed);
        // fast failed without waiting successAfterLongTime
        System.out.println(fastFailed.exceptionNow());

        Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(successAfterLongTime, failed);
        System.out.println(anySuccess.get());

        ////////////////////////////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsFastFailOf / allFastFailOf
        //    CompletableFutureUtils#anySuccessOf
        ////////////////////////////////////////////////////////////////////////
        final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 42;
        });
        final CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

        // Result type is Void!
        CompletableFuture<Void> all2 = allFastFailOf(successAfterLongTimeCf, failedCf);

        CompletableFuture<List<Integer>> fastFailedCf = allResultsFastFailOf(successAfterLongTimeCf, failedCf);
        // fast failed without waiting successAfterLongTime
        System.out.println(exceptionNow(fastFailedCf));

        CompletableFuture<Integer> anySuccessCf = anySuccessOf(successAfterLongTimeCf, failedCf);
        System.out.println(anySuccessCf.get());

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
