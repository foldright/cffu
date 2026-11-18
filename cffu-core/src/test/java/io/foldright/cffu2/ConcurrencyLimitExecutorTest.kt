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
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldMatch
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.lang.Thread.sleep
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger
import kotlin.random.Random

private const val THREAD_COUNT = 8
private val executor: ExecutorService = Executors.newFixedThreadPool(THREAD_COUNT)

/**
 * Simple test cases for ConcurrencyLimitExecutor:
 * - Single-threaded submission
 * - Simple and internal methods
 *
 * @see ConcurrencyLimitExecutorAdvancedTest
 */
class ConcurrencyLimitExecutorTest : FunSpec({

    test("asynchronous executor, single-threaded submission, simple test case (all tasks succeed)") {
        val maxConcurrency = 4
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(maxConcurrency, executor)
        val concurrencyChecker = ConcurrencyChecker(maxConcurrency)

        val taskCount = THREAD_COUNT * 9
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
        shouldThrowExactly<RuntimeException> { executor.execute { throw rte } }
            .shouldBeSameInstanceAs(rte)
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

    test("base executor silently drops the worker - slot leaks and queued tasks stay queued") {
        val concurrencyLimitExecutor = ConcurrencyLimitExecutor(1, Executor {})
        val ran = AtomicInteger()

        // execute() returns without running the worker, but the concurrency slot is still taken
        concurrencyLimitExecutor.execute { ran.incrementAndGet() }
        concurrencyLimitExecutor.execute { ran.incrementAndGet() }

        ran.get() shouldBe 0
        val stuck = concurrencyLimitExecutor.toString()
        stuck shouldContain "worker count: 1"
        stuck shouldContain "queue size: 2"
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
