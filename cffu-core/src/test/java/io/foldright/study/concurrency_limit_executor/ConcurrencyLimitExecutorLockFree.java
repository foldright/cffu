package io.foldright.study.concurrency_limit_executor;

import io.vavr.Tuple2;
import io.vavr.collection.Queue;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicReference;


public final class ConcurrencyLimitExecutorLockFree implements Executor {
    private final int maxConcurrency;
    private final Executor executor;

    private AtomicReference<TaskQueue> queueRef = new AtomicReference<>(new TaskQueue());

    ConcurrencyLimitExecutorLockFree(int maxConcurrency, Executor executor) {
        this.maxConcurrency = maxConcurrency;
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        while (true) {
            final TaskQueue current = queueRef.get();
            final TaskQueue offer = current.offer(command);
            if (queueRef.compareAndSet(current, offer)) {
                break;
            }
        }

        while (true) {
            final TaskQueue queue = queueRef.get();
            if (queue.workerCount == maxConcurrency) break;
            if (queue.tasks.isEmpty()) break;

            final Tuple2<Runnable, TaskQueue> poll = queue.poll();
            if (queueRef.compareAndSet(queue, poll._2)) {
                // FIXME execute may fail! NOT exception safe!!
                executor.execute(queue.tasks.head());
            }
        }
    }

    Runnable poll() {
        return null;
    }

    private final class TaskQueue {
        final Queue<Runnable> tasks;

        final int workerCount;

        public TaskQueue(Queue<Runnable> tasks, int workerCount) {
            this.tasks = tasks;
            this.workerCount = workerCount;
        }

        public TaskQueue() {
            this(Queue.empty(), 0);
        }

        TaskQueue offer(Runnable task) {
            return new TaskQueue(tasks.enqueue(task), workerCount);
        }

        Tuple2<Runnable, TaskQueue> poll() {
            final Tuple2<Runnable, Queue<Runnable>> dequeue = tasks.dequeue();
            return new Tuple2<>(dequeue._1, new TaskQueue(dequeue._2, workerCount));
        }

        TaskQueue pollAfterSchedule() {
            return new TaskQueue(tasks.dequeue()._2, workerCount + 1);
        }
    }
}

