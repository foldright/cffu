package io.foldright.cffu.testee;

import java.util.concurrent.CompletableFuture;


public class TestSample {
    public static void main(String[] args) {
        final CompletableFuture<Void> hello = CompletableFuture.runAsync(() -> System.out.println("hello"));
        System.out.println(hello.join());
    }
}
