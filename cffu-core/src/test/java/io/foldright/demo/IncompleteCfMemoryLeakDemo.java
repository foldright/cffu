package io.foldright.demo;

import java.util.concurrent.CompletableFuture;


/**
 * This shows the memory leak caused by incomplete CompletableFuture instance.
 * <p>
 * Run by maven: {@code
 * mvn -pl cffu-core test-compile exec:exec -Dexec.mainClass=io.foldright.demo.IncompleteCfMemoryLeakDemo
 * }
 */
@SuppressWarnings("InfiniteLoopStatement")
public class IncompleteCfMemoryLeakDemo {
    public static void main(String[] args) {
        System.err.println("start IncompleteCfMemoryLeakDemo");

        CompletableFuture<Void> incomplete = new CompletableFuture<>();
        for (int i = 0; true; i++) {
            // about 1M memory
            final byte[] memoryChuck = new byte[1_000_000];
            incomplete.thenRunAsync(() -> System.err.println(memoryChuck.length));

            if (i % 10 == 0) System.err.printf("loop %4d%n", i);
        }
    }
}

/*
Output Sample(set `-Xmx256m -Xms256m` JVM option):

start IncompleteCfMemoryLeakDemo
loop    0
loop   10
loop   20
loop   30
loop   40
loop   50
loop   60
loop   70
loop   80
loop   90
loop  100
loop  110
loop  120
loop  130
loop  140
loop  150
loop  160
loop  170
loop  180
loop  190
loop  200
loop  210
loop  220
loop  230
loop  240
Exception in thread "main" java.lang.OutOfMemoryError: Java heap space
	at java.base/jdk.internal.misc.Unsafe.allocateInstance(Native Method)
	at java.base/java.lang.invoke.DirectMethodHandle.allocateInstance(DirectMethodHandle.java:501)
	at java.base/java.lang.invoke.DirectMethodHandle$Holder.newInvokeSpecial(DirectMethodHandle$Holder)
	at java.base/java.lang.invoke.Invokers$Holder.linkToTargetMethod(Invokers$Holder)
	at io.foldright.demo.IncompleteCfMemoryLeakDemo.main(IncompleteCfMemoryLeakDemo.java:22)

 */
