package io.foldright.cffu2

import com.google.common.util.concurrent.MoreExecutors
import io.foldright.cffu2.ConcurrencyLimitExecutor.isPowerOfTwo
import io.foldright.cffu2.config.CffuConfiguration
import io.foldright.cffu2.internal.CffuLogger
import io.foldright.cffu2.internal.peekCffuLoggerExceptionLogFormat
import io.foldright.test_utils.ConcurrencyChecker
import io.foldright.test_utils.logWithTimeAndThread
import io.foldright.test_utils.rangeList
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.random.Random

/**
 * Single-threaded submission, one case per test.
 *
 * Execution: every task succeeds, the `maxConcurrency` boundaries (1, and a limit above the pool
 * size), a task that throws or clears its own interrupt status, that status restored to the base
 * executor's thread, then synchronous execution. `directExecutor` is synchronous itself;
 * `CallerRunsPolicy` becomes synchronous only once its pool is saturated.
 *
 * Diagnostics: `toString` while idle, running and drained; the submitted task's `toString`;
 * `isPowerOfTwo`; the finalize report for tasks still queued.
 *
 * @see ConcurrencyLimitExecutorRejectionTest
 * @see ConcurrencyLimitExecutorBulkSubmissionTest
 */
class ConcurrencyLimitExecutorTest : FunSpec({
    val threadCount = 8
    val executor: ExecutorService = Executors.newFixedThreadPool(threadCount)

    test("asynchronous executor, single-threaded submission, simple test case (all tasks succeed)") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = threadCount * 9
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

    test("asynchronous executor, single-threaded submission, task is exceptional: RuntimeException/InterruptedException/Thread.interrupted()") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)
        val results = CopyOnWriteArrayList<Int>()

        val taskCount = maxConcurrency * 8
        val latch = CountDownLatch(taskCount)
        val tasks = List(taskCount) { index ->
            val r = Runnable {
                try {
                    results.add(index)
                    concurrencyChecker.enter()

                    val millis: Long = Random.nextLong(50, 150)
                    logWithTimeAndThread("task %2d begin, then sleep %s ms", index, millis)
                    sleep(millis)
                    logWithTimeAndThread("task %2d end", index)
                } catch (ex: Throwable) {
                    println("caught (index: $index): $ex")
                    throw ex
                } finally {
                    concurrencyChecker.leave()
                    latch.countDown()

                    if (index > taskCount / 2) {
                        when (index % 4) {
                            0 -> throw RuntimeException("intentional RuntimeException (index: $index)")
                            // internal interruption, output is InterruptedException
                            1 -> throw InterruptedException("intentional InterruptedException (index: $index)")
                            2 -> {
                                // internal interruption, output is the Interrupted thread status
                                println("interrupted currentThread (index: $index)")
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
        // external interruption
        repeat(taskCount) { index ->
            (tasks[index] as? FutureTask<*>)?.cancel(true)
        }
        latch.await()

        concurrencyChecker.check()
        results shouldContainExactlyInAnyOrder rangeList(taskCount)
    }

    test("maxConcurrency boundary - maxConcurrency = 1 (serial execution)") {
        val maxConcurrency = 1
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = 20
        val executionOrder = CopyOnWriteArrayList<Int>()
        val latch = CountDownLatch(taskCount)

        repeat(taskCount) { index ->
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()
                try {
                    executionOrder.add(index)
                    sleep(20)
                } finally {
                    concurrencyChecker.leave()
                    latch.countDown()
                }
            }
        }

        latch.await(10, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.check()

        // With maxConcurrency=1, execution should be strictly serial
        executionOrder shouldHaveSize taskCount
        // Verify order is preserved (since it's serial)
        executionOrder shouldBe rangeList(taskCount)
    }

    test("maxConcurrency above the pool size lets concurrency reach the pool size") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1000, executor)
        val concurrencyChecker = ConcurrencyChecker(threadCount)

        val entered = CountDownLatch(threadCount)
        val release = CountDownLatch(1)
        val extraTasks = 4
        val finished = CountDownLatch(threadCount + extraTasks)

        repeat(threadCount) {
            concurrencyLimitExecutor.execute {
                concurrencyChecker.enter()
                try {
                    entered.countDown()
                    release.await()
                } finally {
                    concurrencyChecker.leave()
                    finished.countDown()
                }
            }
        }

        entered.await(5, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.max.get() shouldBeExactly threadCount

        repeat(extraTasks) {
            concurrencyLimitExecutor.execute { finished.countDown() }
        }
        release.countDown()
        finished.await(5, TimeUnit.SECONDS) shouldBe true
        concurrencyChecker.check()
    }

    test("sync executor (MoreExecutors.directExecutor), single-threaded submission") {
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

    test("sync executor (ThreadPoolExecutor/CallerRunsPolicy), single-threaded submission") {
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

    test("interrupt status is restored to the base executor thread") {
        // Record the flag immediately after the worker runnable returns. A follow-up task on a
        // ThreadPoolExecutor would miss it: the pool clears the flag while taking the next task.
        fun flagAfterWorkerReturns(submit: (Executor) -> Unit): Boolean? {
            val carrier = Executors.newSingleThreadExecutor()
            val seen = AtomicReference<Boolean>()
            val done = CountDownLatch(1)
            try {
                val base = Executor { command ->
                    carrier.execute {
                        try {
                            command.run()
                            seen.set(currentThread().isInterrupted())
                        } finally {
                            Thread.interrupted()
                            done.countDown()
                        }
                    }
                }
                submit(ConcurrencyLimitExecutor(1, base))
                if (!done.await(5, TimeUnit.SECONDS)) return null
                return seen.get()
            } finally {
                carrier.shutdownNow()
            }
        }

        // InterruptedException does not set the flag; asyncWork must restore it when the worker returns.
        flagAfterWorkerReturns { cle ->
            cle.execute { throw InterruptedException("intentional") }
        } shouldBe true

        // The flag set by the first task is cleared before the second task and must be set again
        // when that same worker finally returns.
        val secondSawInterrupt = AtomicBoolean(true)
        flagAfterWorkerReturns { cle ->
            val running = CountDownLatch(1)
            val release = CountDownLatch(1)
            cle.execute {
                running.countDown()
                release.await()
                currentThread().interrupt()
            }
            running.await(5, TimeUnit.SECONDS) shouldBe true
            cle.execute { secondSawInterrupt.set(currentThread().isInterrupted()) }
            release.countDown()
        } shouldBe true
        secondSawInterrupt.get() shouldBe false

        flagAfterWorkerReturns { cle ->
            cle.execute { }
        } shouldBe false
    }

    test("toString contains all key information in different states") {
        val maxConcurrency = 3
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)

        // State 1: Idle (no tasks)
        val idleStr = concurrencyLimitExecutor.toString()
        idleStr shouldContain "worker count: 0"
        idleStr shouldContain "synchronous runner count: 0"
        idleStr shouldContain "queue size: 0"
        idleStr shouldContain "max concurrency: $maxConcurrency"

        // State 2: With running tasks. State 3: after they finish, the queue is empty and workers are gone.
        val taskCount = maxConcurrency + 5
        val blockingLatch = CountDownLatch(1)
        val submittedLatch = CountDownLatch(maxConcurrency)
        val finished = CountDownLatch(taskCount)

        repeat(taskCount) {
            concurrencyLimitExecutor.execute {
                submittedLatch.countDown()
                blockingLatch.await()
                finished.countDown()
            }
        }

        submittedLatch.await(2, TimeUnit.SECONDS) shouldBe true

        val activeStr = concurrencyLimitExecutor.toString()
        logWithTimeAndThread("Active state: $activeStr")
        activeStr shouldContain "worker count: $maxConcurrency"
        activeStr shouldContain "queue size: 5"
        activeStr shouldContain "max concurrency: $maxConcurrency"

        blockingLatch.countDown()
        finished.await(5, TimeUnit.SECONDS) shouldBe true

        // workerCount is decremented after the task returns, so poll briefly for the idle state
        val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(2)
        var drained = concurrencyLimitExecutor.toString()
        while (System.nanoTime() < deadline &&
            (!drained.contains("worker count: 0") || !drained.contains("queue size: 0"))
        ) {
            sleep(10)
            drained = concurrencyLimitExecutor.toString()
        }
        drained shouldContain "worker count: 0"
        drained shouldContain "synchronous runner count: 0"
        drained shouldContain "queue size: 0"
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
        shouldThrowExactly<RuntimeException> { executor.execute { throw rte } }
            .shouldBeSameInstanceAs(rte)
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

    test("finalize reports queued tasks that will be discarded") {
        // Clean up any leftover objects from previous tests before GC-finalize output capture.
        System.gc()

        val previousFormat = peekCffuLoggerExceptionLogFormat()
        CffuLogger.setExceptionLogFormat(CffuConfiguration.ExceptionLogFormat.FULL)

        try {
            var report = ""
            ConsoleCapture().use { capture ->
                // The local references die when this returns, so a later GC can finalize them.
                abandonQueuedExecutors()

                val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(5)
                while (System.nanoTime() < deadline) {
                    System.gc()
                    sleep(20)
                    report = capture.text()
                    if (report.contains("queued task(s) remained")) break
                }
                report shouldContain "queued task(s) remained when finalizing "
                report shouldContain "these tasks will be discarded"
            }
            println("=============== report ===============\n$report======================================")
        } finally {
            CffuLogger.setExceptionLogFormat(previousFormat)
        }
    }

    beforeSpec {
        // warmup executor
        List(threadCount) {
            executor.submit { sleep(50) }
        }.forEach { it.get() }
    }

    afterSpec {
        executor.shutdownNow()
    }
})

/** Executors with a non-empty queue, kept reachable only until this method returns. */
private fun abandonQueuedExecutors() {
    val discardAllExecutor = Executor { }
    repeat(3) {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(3, discardAllExecutor)
        repeat(it + 1) { concurrencyLimitExecutor.execute {} }
    }
}

private const val UTF_8 = "UTF-8"

/**
 * Text of the discard report while a GC is in progress.
 *
 * slf4j-simple writes to `System.err`. log4j2 writes to `System.out`
 * (`follow="true"` in the test config, so it sees [System.setOut]).
 * Without SLF4J, [CffuLogger] uses JUL, whose console handler keeps the
 * original stream, so those records are collected from a handler directly.
 */
private class ConsoleCapture : AutoCloseable {
    private val jul = Logger.getLogger(ConcurrencyLimitExecutor::class.java.name)
    private val julMessages = StringBuffer()
    private val handler = object : Handler() {
        override fun publish(record: LogRecord) {
            julMessages.append(record.message).append('\n')
        }

        override fun flush() {}
        override fun close() {}
    }
    private val previousOut = System.out
    private val previousErr = System.err
    private val buffer = ByteArrayOutputStream()
    private val captured = PrintStream(buffer, true, UTF_8)

    init {
        jul.addHandler(handler)
        System.setOut(captured)
        System.setErr(captured)
    }

    fun text(): String {
        // The finalizer thread writes while the test thread reads.
        synchronized(captured) {
            captured.flush()
            return buffer.toString(UTF_8) + julMessages
        }
    }

    override fun close() {
        jul.removeHandler(handler)
        System.setOut(previousOut)
        System.setErr(previousErr)
    }
}
