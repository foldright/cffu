package io.foldright.aspect_test

import io.foldright.cffu2.CompletableFutureUtils
import io.foldright.test_utils.sleep
import io.foldright.test_utils.testCffuFac
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import java.lang.Thread.currentThread
import java.util.concurrent.*
import java.util.function.Supplier

class CheckUnscreenedExecutorTests : FunSpec({

    val commonPool = ForkJoinPool.commonPool()

    fun isRunStrictlyInFjCommonPool(): Boolean {
        val runningThread = currentThread()
        return runningThread.name.startsWith("ForkJoinPool.commonPool-worker-")
    }

    test("check screen executor for unscreened methods") {
        var counter = 0
        val blockingQueue: BlockingQueue<Boolean> = ArrayBlockingQueue(1000)

        fun createRunnable(): Runnable {
            counter++
            return Runnable {
                sleep(ThreadLocalRandom.current().nextLong(4))
                blockingQueue.put(isRunStrictlyInFjCommonPool())
            }
        }

        fun <T> createSupplier(v: T): Supplier<T> {
            counter++
            return Supplier {
                blockingQueue.put(isRunStrictlyInFjCommonPool())
                v
            }
        }

        repeat(1000) {
            CompletableFutureUtils.delayedExecutor(1, TimeUnit.MILLISECONDS, commonPool).execute(createRunnable())
            testCffuFac.delayedExecutor(1, TimeUnit.MILLISECONDS, commonPool).execute(createRunnable())

            CompletableFutureUtils.completeAsync(
                CompletableFuture(),
                createSupplier(42), commonPool
            )
            testCffuFac.newIncompleteCffu<Int>().completeAsync(
                createSupplier(42), commonPool
            )

            CompletableFutureUtils.completeExceptionallyAsync(
                CompletableFuture<Int>(),
                createSupplier(RuntimeException()),
                commonPool
            )
            testCffuFac.newIncompleteCffu<Int>().completeExceptionallyAsync(
                createSupplier(RuntimeException()),
                commonPool
            )
        }

        repeat(counter) {
            blockingQueue.poll(1, TimeUnit.SECONDS)!!.shouldBeTrue()
        }
        println(blockingQueue.size)
        blockingQueue.isEmpty().shouldBeTrue()
    }
})
