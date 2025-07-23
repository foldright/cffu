package demo;

import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;

import java.util.concurrent.ForkJoinPool;


public class Demo {
    public static void main(String[] args) throws Exception {
        CffuFactory cffuFactory = CffuFactory.builder(ForkJoinPool.commonPool()).build();
        Cffu<Void> v = cffuFactory.runAsync(() -> System.out.print("Hello"));
        v.thenRun(() -> System.out.println(" World!"));

        final Cffu<Integer> catching = cffuFactory.supplyAsync(() -> 42).catching(RuntimeException.class, ex -> 0);
        catching.thenAccept(System.out::println);

        Thread.sleep(1000);
    }
}
