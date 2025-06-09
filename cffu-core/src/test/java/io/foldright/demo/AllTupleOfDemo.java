package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.tuple.Tuple2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CfTupleUtils.allTupleFailFastOf;


public class AllTupleOfDemo {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // allTupleFailFastOf / allTupleOf
        //////////////////////////////////////////////////
        Cffu<String> cffu1 = cffuFactory.completedFuture("foo");
        Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

        Cffu<Tuple2<String, Integer>> allTuple = cffuFactory.tupleOps().allTupleFailFastOf(cffu1, cffu2);
        System.out.println(allTuple.get());
        // output: Tuple2(foo, 42)

        //////////////////////////////////////////////////
        // or CompletableFutureUtils.allTupleFailFastOf / allTupleOf
        //////////////////////////////////////////////////
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("foo");
        CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

        CompletableFuture<Tuple2<String, Integer>> allTuple2 = allTupleFailFastOf(cf1, cf2);
        System.out.println(allTuple2.get());
        // output: Tuple2(foo, 42)

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
