package io.foldright.demo;

import io.foldright.cffu2.Cffu;
import io.foldright.cffu2.CffuFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DefaultExecutorSettingForCffu {
    private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
    private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

    public static void main(String[] args) {
        Cffu<Void> cf1 = cffuFactory.runAsync(() -> System.out.println("doing a long time work!"));

        Cffu<Void> cf2 = cffuFactory.supplyAsync(() -> {
            System.out.println("doing another long time work!");
            return 42;
        }).thenAcceptAsync(i -> System.out.println("doing third long time work!"));

        cffuFactory.allOf(cf1, cf2).join();

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizExecutor.shutdown();
    }
}
