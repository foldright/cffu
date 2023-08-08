package io.foldright.demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CompletableFutureUtils;
import io.foldright.cffu.tuple.Tuple2;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder;


public class CffuCombineDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    public static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizExecutor).build();

    public static void main(String[] args) throws Exception {
        //////////////////////////////////////////////////
        // cffuCombine
        //////////////////////////////////////////////////
        Cffu<String> cffu1 = cffuFactory.completedFuture("21");
        Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

        Cffu<Tuple2<String, Integer>> allOfWithResult = cffu1.cffuCombine(cffu2);
        // or: cffuFactory.cffuCombine(cffu1, cffu2);
        System.out.println(allOfWithResult.get());

        //////////////////////////////////////////////////
        // or CompletableFutureUtils.combine
        //////////////////////////////////////////////////
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture("21");
        CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

        CompletableFuture<Tuple2<String, Integer>> allOfWithResult2 = CompletableFutureUtils.combine(cf1, cf2);
        System.out.println(allOfWithResult2.get());

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
