package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CompletableFutureUtils;
import io.foldright.cffu.tuple.Tuple2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class AllTupleOfDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // allTupleOf
        //////////////////////////////////////////////////
        Cffu<String> cffu1 = cffuFactory.completedFuture("21");
        Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

        Cffu<Tuple2<String, Integer>> allTuple = cffu1.allTupleOf(cffu2);
        // or: cffuFactory.allTupleOf(cffu1, cffu2);
        System.out.println(allTuple.get());

        //////////////////////////////////////////////////
        // or CompletableFutureUtils.allTupleOf
        //////////////////////////////////////////////////
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("21");
        CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

        CompletableFuture<Tuple2<String, Integer>> allTuple2 = CompletableFutureUtils.allTupleOf(cf1, cf2);
        System.out.println(allTuple2.get());

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
