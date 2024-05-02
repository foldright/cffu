package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CompletableFutureUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AllResultsOfDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // CffuFactory#allResultsOf
        //////////////////////////////////////////////////
        Cffu<Integer> cffu1 = cffuFactory.completedFuture(21);
        Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

        Cffu<Void> allOf = cffuFactory.allOf(cffu1, cffu2);
        // Result type is Void!
        //
        // the result can be got by input argument `cf1.get()`, but it's cumbersome.
        // so we can see a lot the util methods to enhance allOf with result in our project.

        Cffu<List<Integer>> allResults = cffuFactory.allResultsOf(cffu1, cffu2);
        System.out.println(allResults.get());

        //////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsOf
        //////////////////////////////////////////////////
        CompletableFuture<Integer> cf1 = CompletableFuture.completedFuture(21);
        CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

        CompletableFuture<Void> allOf2 = CompletableFuture.allOf(cf1, cf2);
        // Result type is Void!

        CompletableFuture<List<Integer>> allResults2 = CompletableFutureUtils.allResultsOf(cf1, cf2);
        System.out.println(allResults2.get());

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
