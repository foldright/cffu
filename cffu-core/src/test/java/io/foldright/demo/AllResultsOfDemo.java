package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.MCffu;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CompletableFutureUtils.allResultsOf;


public class AllResultsOfDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // CffuFactory#allResultsOf
        //////////////////////////////////////////////////
        Cffu<Integer> cffu1 = cffuFactory.completedFuture(21);
        Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

        Cffu<Void> all = cffuFactory.allOf(cffu1, cffu2);
        // result type is Void!
        //
        // the result can be got by input argument `cf1.get()`, but it's cumbersome.
        // so we can see a lot of util methods to enhance `allOf` with result in our project.

        MCffu<Integer, List<Integer>> allResults = cffuFactory.allResultsOf(cffu1, cffu2);
        System.out.println(allResults.get());
        // output: [21, 42]

        //////////////////////////////////////////////////
        // or CompletableFutureUtils#allResultsOf
        //////////////////////////////////////////////////
        CompletableFuture<Integer> cf1 = CompletableFuture.completedFuture(21);
        CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

        CompletableFuture<Void> all2 = CompletableFuture.allOf(cf1, cf2);
        // result type is Void!

        CompletableFuture<List<Integer>> allResults2 = allResultsOf(cf1, cf2);
        System.out.println(allResults2.get());
        // output: [21, 42]

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
