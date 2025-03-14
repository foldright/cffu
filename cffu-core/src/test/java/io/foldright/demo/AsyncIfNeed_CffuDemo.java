package io.foldright.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.test_utils.TestUtils.sleep;


public class AsyncIfNeed_CffuDemo {
    public static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        sleep();

        // shutting down
        sleep(1000);
        myBizExecutor.shutdown();
    }
}
