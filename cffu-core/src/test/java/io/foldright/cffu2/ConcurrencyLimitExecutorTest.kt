package io.foldright.cffu2

import com.google.common.util.concurrent.MoreExecutors
import io.foldright.cffu2.ConcurrencyLimitExecutor.isPowerOfTwo
import io.foldright.test_utils.ConcurrencyChecker
import io.foldright.test_utils.testExecutor
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.*
import kotlin.random.Random

private const val THREAD_COUNT = 8
private val executor: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

class ConcurrencyLimitExecutorTest : FunSpec({

    test("common case, single-threaded submission") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = THREAD_COUNT * 3
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) { index ->
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()

                val millis: Long = Random.nextLong(100, 200)
                logWithTimeAndThread("task %2d begin, then sleep %s ms", index, millis)
                sleep(millis)
                logWithTimeAndThread("task %2d end", index)

                concurrencyChecker.leave()
                latch.countDown()
            }
        }
        latch.await()

        concurrencyChecker.check()
    }

    test("InterruptedException/RuntimeException, single-threaded submission") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)
        val results = CopyOnWriteArrayList<Int>()

        val taskCount = THREAD_COUNT * 4
        val latch = CountDownLatch(taskCount)
        val indexes: List<Int> = (0 until taskCount).toList()
        val tasks = indexes.map { index ->
            val r = Runnable {
                try {
                    results.add(index)
                    concurrencyChecker.enter()

                    val millis: Long = Random.nextLong(100, 200)
                    logWithTimeAndThread("task %2d begin, then sleep %s ms", index, millis)
                    sleep(millis)
                    logWithTimeAndThread("task %2d end", index)
                } catch (ex: Throwable) {
                    println("caught: $ex")
                    throw ex
                } finally {
                    concurrencyChecker.leave()
                    latch.countDown()

                    if (index > taskCount / 2 && index % 2 == 0) {
                        when (index / 2 % 3) {
                            0 -> throw RuntimeException("intentional exception (index: $index)")
                            1 -> throw InterruptedException("intentional exception (index: $index)")
                            2 -> {
                                println("interrupted currentThread")
                                Thread.interrupted()
                            }
                        }
                    }
                }
            }
            val task = if (index < maxConcurrency) FutureTask(r, null) else r
            concurrencyLimitExecutor.execute(task)
            task
        }

        sleep(100)
        (0 until maxConcurrency).forEach { index ->
            (tasks[index] as? FutureTask<*>)?.cancel(true)
        }
        latch.await()

        concurrencyChecker.check()
        results.shouldContainExactlyInAnyOrder((0 until taskCount).toList())
    }

    test("sync execution at MoreExecutors.directExecutor(), single-threaded submission") {
        val maxConcurrency = 3
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, MoreExecutors.directExecutor())
        val concurrencyChecker = ConcurrencyChecker(1)

        val taskCount = 32
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()

                sleep(Random.nextLong(10, 20))
                logWithTimeAndThread("running task %2d", it + 1)

                concurrencyChecker.leave()
                latch.countDown()
            }
        }
        latch.await()

        concurrencyChecker.check()
    }

    test("sync execution at ThreadPoolExecutor/CallerRunsPolicy, single-threaded submission") {
        val executor = ThreadPoolExecutor(
            0, 1, 3, TimeUnit.SECONDS, SynchronousQueue(), ThreadPoolExecutor.CallerRunsPolicy()
        )
        val latchForBlockerTask = CountDownLatch(1)
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(3, executor)
        val f: Future<*> = executor.submit {
            latchForBlockerTask.await()
        }

        val concurrencyChecker = ConcurrencyChecker(1)

        val taskCount = 32
        val latch = CountDownLatch(taskCount)
        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()

                sleep(Random.nextLong(10, 20))
                logWithTimeAndThread("running task %2d", it + 1)

                concurrencyChecker.leave()
                latch.countDown()
            }
        }
        latch.await()

        concurrencyChecker.check()

        latchForBlockerTask.countDown()
        f.get().shouldBeNull()
    }

    test("sync execution at MoreExecutors.directExecutor(), multi-threaded submission") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(3, MoreExecutors.directExecutor())

        val concurrencyChecker = ConcurrencyChecker(3)

        val taskCount = 32
        val latch = CountDownLatch(taskCount)
        val futures = (0 until taskCount).map {
            testExecutor.submit {
                logWithTimeAndThread("submit task %2d", it + 1)
                concurrencyLimitExecutor.execute {
                    concurrencyChecker.enter()

                    sleep(Random.nextLong(10, 20))
                    logWithTimeAndThread("running task %2d", it + 1)

                    concurrencyChecker.leave()
                    latch.countDown()
                }
            }
        }
        futures.forEach { it.get(5, TimeUnit.SECONDS) }
        latch.await(5, TimeUnit.SECONDS).shouldBeTrue()

        concurrencyChecker.check()
    }

    test("rejected submission should not remain in internal queue") {
        val pool = Executors.newSingleThreadExecutor()
        try {
            val rejectFirst = booleanArrayOf(true)
            val baseExecutor = Executor { task ->
                if (rejectFirst[0]) {
                    rejectFirst[0] = false
                    throw RejectedExecutionException("intentional reject once")
                }
                pool.execute(task)
            }
            val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1, baseExecutor)

            val leakedTaskRan = booleanArrayOf(false)
            shouldThrowExactly<RejectedExecutionException> {
                concurrencyLimitExecutor.execute { leakedTaskRan[0] = true }
            }

            val acceptedTaskDone = CountDownLatch(1)
            concurrencyLimitExecutor.execute { acceptedTaskDone.countDown() }
            acceptedTaskDone.await(5, TimeUnit.SECONDS).shouldBeTrue()

            // Give queued tasks a chance to run if leaked due to rollback bug.
            sleep(100)
            leakedTaskRan[0].shouldBeFalse()
        } finally {
            pool.shutdownNow()
        }
    }

    test("isPowerOfTwo") {
        isPowerOfTwo(-100).shouldBeFalse()
        isPowerOfTwo(-1).shouldBeFalse()
        isPowerOfTwo(0).shouldBeFalse()
        isPowerOfTwo(1).shouldBeTrue()
        isPowerOfTwo(2).shouldBeTrue()
        isPowerOfTwo(3).shouldBeFalse()
        isPowerOfTwo(4).shouldBeTrue()
        isPowerOfTwo(5).shouldBeFalse()
        isPowerOfTwo(1023).shouldBeFalse()
        isPowerOfTwo(1024).shouldBeTrue()
    }

    test("toString of SubmittedTask") {
        val executor = ConcurrencyLimitExecutor(1) { r ->
            r.toString().shouldMatch(
                "Submitted task \\(command: .*\\)" +
                        " of io\\.foldright\\.cffu2\\.ConcurrencyLimitExecutor@[0-9A-Fa-f]{1,8} \\(.*\\)"
            )
            r.run()
        }

        val rte = RuntimeException("foo")
        shouldThrowExactly<RuntimeException> { executor.execute { throw rte } }.shouldBeSameInstanceAs(rte)
    }

    test("test report in finalize") {
        logWithTimeAndThread("${testCase.name.testName}: start")
        val discardAllExecutor = Executor { r -> }
        repeat(10) {
            val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1, discardAllExecutor)
            repeat(it + 1) { concurrencyLimitExecutor.execute {} }
        }
        repeat(10) {
            sleep(2)
            logWithTimeAndThread("${testCase.name.testName}: gc")
            System.gc()
        }
        logWithTimeAndThread("${testCase.name.testName}: end")
    }

    beforeSpec {
        // warmup executor
        List(THREAD_COUNT) {
            executor.submit { sleep(50) }
        }.forEach { it.get() }
    }

    afterSpec {
        executor.shutdownNow()
    }
})

private fun logWithTimeAndThread(format: String = "", vararg args: Any?) {
    val msg = String.format(format, *args)
    System.out.printf("%tF %<tT.%<tL |%s| %s%n", System.currentTimeMillis(), currentThread().name, msg)
}
