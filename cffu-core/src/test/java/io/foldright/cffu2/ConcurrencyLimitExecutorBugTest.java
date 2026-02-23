package io.foldright.cffu2;

import org.junit.jupiter.api.Test;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConcurrencyLimitExecutorBugTest {

    @Test
    public void testSyncRunLeavesTasksInQueue() throws InterruptedException {
        // A direct executor that runs synchronously
        Executor directExecutor = Runnable::run;
        
        ConcurrencyLimitExecutor executor = new ConcurrencyLimitExecutor(1, directExecutor);
        
        AtomicBoolean task2Run = new AtomicBoolean(false);
        
        // We need task1 to pause so task2 can be added to the queue while workerCount == 1
        Thread thread1 = new Thread(() -> {
            executor.execute(() -> {
                System.out.println("Task 1 running");
                try {
                    Thread.sleep(500); // Wait so task2 is submitted while this runs
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                System.out.println("Task 1 finished");
            });
        });
        
        thread1.start();
        Thread.sleep(100); // Ensure task1 has started and is inside execute/syncRun
        
        // Now submit task2. workerCount is 1 (maxConcurrency is 1).
        // It should just be added to the queue and NOT throw RejectedExecutionException.
        executor.execute(() -> {
            System.out.println("Task 2 running");
            task2Run.set(true);
        });
        
        thread1.join();
        
        // Wait a bit to see if task2 runs
        Thread.sleep(500);
        
        assertTrue(task2Run.get(), "Task 2 was not run!");
    }
}