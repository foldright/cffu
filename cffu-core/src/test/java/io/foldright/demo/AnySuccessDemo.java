package io.foldright.demo;

import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;
import io.foldright.cffu2.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AnySuccessDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // CffuFactory#allResultsFailFastOf
        //////////////////////////////////////////////////
        // a simulating fast-failure computation...
        Cffu<Integer> cffu1 = cffuFactory.failedFuture(new RuntimeException());
        Cffu<Integer> cffu2 = cffuFactory.supplyAsync(() -> {
            // a simulating long-running computation...
            sleep(2_000);
            return 42;
        });

        Cffu<Integer> any = cffuFactory.anyOf(cffu1, cffu2);
        // first completed: cffu1(but failed)
        try {
            any.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        // output: RuntimeException

        Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(cffu1, cffu2);
        // first completed and successful: cffu2
        System.out.println(anySuccess.join());
        // output: 42

        //////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsFailFast
        //////////////////////////////////////////////////
        CompletableFuture<Integer> cf1 = CompletableFutureUtils.failedFuture(new RuntimeException());
        CompletableFuture<Integer> cf2 = CompletableFuture.supplyAsync(() -> {
            // a simulating long-running computation...
            sleep(2_000);
            return 42;
        });

        CompletableFuture<Integer> anyCf = CompletableFutureUtils.anyOf(cf1, cf2);
        // first completed CF: cf1(but failed)
        try {
            anyCf.join();
        } catch (Exception e) {
            System.out.println(e);
        }
        // output: RuntimeException

        CompletableFuture<Integer> anySuccessCf = CompletableFutureUtils.anySuccessOf(cf1, cf2);
        // first completed and successful: cf2
        System.out.println(anySuccessCf.get());
        // output: 42

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
