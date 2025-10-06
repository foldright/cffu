package io.foldright.demo;

import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.CompletableFutureUtils;
import io.foldright.cffu2.MCffu;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static io.foldright.cffu2.CompletableFutureUtils.failedFuture;
import static io.foldright.cffu2.CompletableFutureUtils.mostSuccessResultsOf;


/**
 * @see AllFastFailDemo
 * @see AnySuccessDemo
 */
@SuppressWarnings({"ThrowablePrintedToSystemOut", "SameParameterValue"})
public class ConcurrencyStrategyDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        ////////////////////////////////////////////////////////////////////////
        // CffuFactory#mostSuccessResultsOf
        // CffuFactory#allSuccessResultsOf
        ////////////////////////////////////////////////////////////////////////
        Cffu<Integer> success = cffuFactory.supplyAsync(() -> {
            sleep(300); // sleep SHORT time
            return 42;
        });
        Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 4242;
        });
        Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

        MCffu<Integer, List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
                -1, 100, TimeUnit.MILLISECONDS, success, successAfterLongTime, failed);
        System.out.println(mostSuccess.get());
        // output: [42, -1, -1]

        MCffu<Integer, List<Integer>> allSuccess = cffuFactory.allSuccessResultsOf(
                -1, success, successAfterLongTime, failed);
        System.out.println(allSuccess.get());
        // output: [42, -1, 4242]

        ////////////////////////////////////////////////////////////////////////
        // or CompletableFutureUtils#mostSuccessResultsOf
        //    CompletableFutureUtils#allSuccessResultsOf
        ////////////////////////////////////////////////////////////////////////
        CompletableFuture<Integer> successCf = CompletableFuture.supplyAsync(() -> {
            sleep(300); // sleep SHORT time
            return 42;
        });
        CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
            sleep(3000); // sleep LONG time
            return 4242;
        });
        CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

        CompletableFuture<List<Integer>> mostSuccessCf = mostSuccessResultsOf(
                -1, 100, TimeUnit.MILLISECONDS, successCf, successAfterLongTimeCf, failedCf);
        System.out.println(mostSuccessCf.get());
        // output: [42, -1, -1]

        CompletableFuture<List<Integer>> allSuccessCf = CompletableFutureUtils.allSuccessResultsOf(
                -1, successCf, successAfterLongTimeCf, failed);
        System.out.println(allSuccessCf.get());
        // output: [42, -1, 4242]

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
