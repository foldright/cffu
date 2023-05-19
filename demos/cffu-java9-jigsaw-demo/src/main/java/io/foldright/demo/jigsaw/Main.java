package io.foldright.demo.jigsaw;

import io.foldright.cffu.CompletableFutureUtils;

import java.util.concurrent.CompletableFuture;


public class Main {
    public static void main(String[] args) {
        CompletableFuture<Object> failed = CompletableFutureUtils.failedFuture(new RuntimeException());
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("CompletableFuture state: " + CompletableFutureUtils.cffuState(failed));
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
    }
}
