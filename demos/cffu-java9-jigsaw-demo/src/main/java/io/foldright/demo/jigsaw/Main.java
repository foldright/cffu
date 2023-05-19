package io.foldright.demo.jigsaw;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;
import io.foldright.cffu.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder;


public class Main {
    public static void main(String[] args) {
        CompletableFuture<Object> cf = CompletableFutureUtils.failedFuture(new RuntimeException());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("CompletableFuture state: " + CompletableFutureUtils.cffuState(cf));
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

        CffuFactory factory = newCffuFactoryBuilder(Executors.newSingleThreadExecutor()).build();
        Cffu<Object> cffu = factory.failedFuture(new RuntimeException());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("Cffu state: " + cffu.cffuState());
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
