package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CompletableFutureUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder;


public class ConcurrencyStrategyDemo {
    public static final Executor myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // CffuFactory#cffuAllOfFastFail
        // CffuFactory#allOfFastFail
        //////////////////////////////////////////////////
        Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
            sleep(10_000); // sleep LONG time
            return 42;
        });
        Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

        Cffu<Void> cffu1 = cffuFactory.allOfFastFail(successAfterLongTime, failed);
        // Result type is Void!
        // fast failed without waiting successAfterLongTime

        Cffu<List<Integer>> fastFailed = cffuFactory.cffuAllOfFastFail(successAfterLongTime, failed);
        // fast failed without waiting successAfterLongTime
        System.out.println(fastFailed.get());

        //////////////////////////////////////////////////
        // or CompletableFutureUtils#allOfFastFailWithResult
        //    CompletableFutureUtils#allOfFastFail
        //////////////////////////////////////////////////
        CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
            sleep(10_000); // sleep LONG time
            return 42;
        });
        CompletableFuture<Integer> failedCf = CompletableFutureUtils.failedFuture(new RuntimeException("Bang!"));

        CompletableFuture<Void> cf1 = CompletableFutureUtils.allOfFastFail(successAfterLongTimeCf, failedCf);
        // Result type is Void!
        // fast failed without waiting successAfterLongTimeCf

        CompletableFuture<List<Integer>> fastFailedCf = CompletableFutureUtils.allOfFastFailWithResult(successAfterLongTimeCf, failedCf);
        // fast failed without waiting successAfterLongTime
        System.out.println(fastFailedCf.get());
    }

    private static void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
