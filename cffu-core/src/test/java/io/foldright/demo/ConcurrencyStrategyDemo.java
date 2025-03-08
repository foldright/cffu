package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.foldright.cffu.CompletableFutureUtils.*;


@SuppressWarnings({"ThrowablePrintedToSystemOut", "SameParameterValue"})
public class ConcurrencyStrategyDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////
        // CffuFactory#allResultsFailFastOf
        // CffuFactory#anySuccessOf
        // CffuFactory#mostSuccessResultsOf
        ////////////////////////////////////////////////////////////////////////
        final Cffu<Integer> success = cffuFactory.supplyAsync(() -> {
            sleep(300); // sleep SHORT time
            return 42;
        });
        final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 4242;
        });
        final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

        Cffu<List<Integer>> failFast = cffuFactory.allResultsFailFastOf(success, successAfterLongTime, failed);
        // fail fast without waiting successAfterLongTime
        System.out.println(failFast.exceptionNow());
        // output: java.lang.RuntimeException: Bang!

        Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(success, successAfterLongTime, failed);
        System.out.println(anySuccess.get());
        // output: 42

        Cffu<List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
                -1, 100, TimeUnit.MILLISECONDS, success, successAfterLongTime, failed);
        System.out.println(mostSuccess.get());
        // output: [42, -1, -1]

        ////////////////////////////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsFailFastOf
        //    CompletableFutureUtils#anySuccessOf
        //    CompletableFutureUtils#mostSuccessResultsOf
        ////////////////////////////////////////////////////////////////////////
        final CompletableFuture<Integer> successCf = CompletableFuture.supplyAsync(() -> {
            sleep(300); // sleep SHORT time
            return 42;
        });
        final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 4242;
        });
        final CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

        CompletableFuture<List<Integer>> failFast2 = allResultsFailFastOf(successCf, successAfterLongTimeCf, failedCf);
        // fail fast without waiting successAfterLongTime
        System.out.println(exceptionNow(failFast2));
        // output: java.lang.RuntimeException: Bang!

        CompletableFuture<Integer> anySuccess2 = anySuccessOf(successCf, successAfterLongTimeCf, failedCf);
        System.out.println(anySuccess2.get());
        // output: 42

        CompletableFuture<List<Integer>> mostSuccess2 = mostSuccessResultsOf(
                -1, 100, TimeUnit.MILLISECONDS, successCf, successAfterLongTime, failed);
        System.out.println(mostSuccess2.get());
        // output: [42, -1, -1]

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        System.out.println("shutting down");
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
