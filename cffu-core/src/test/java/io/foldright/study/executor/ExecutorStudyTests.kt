package io.foldright.study.executor

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.throwable.shouldHaveCauseInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.lang.Thread.currentThread
import java.lang.Thread.sleep
import java.util.concurrent.*


@Volatile
private lateinit var executorThread: Thread
private val executor: ExecutorService = Executors.newFixedThreadPool(1)

class ExecutorStudyTests : FunSpec({
    test("task interruption - InterruptedException") {
        val runThreads = CopyOnWriteArrayList<Thread>()

        run {
            val submitLatch = CountDownLatch(1)
            val f: Future<*> = executor.submit {
                runThreads.add(currentThread())
                submitLatch.countDown()
                sleep(2_000)
            }
            // await submitted task start
            submitLatch.await()

            executorThread.interrupt()
            sleep(50)

            shouldThrowExactly<ExecutionException> { f.get() }
                .shouldHaveCauseInstanceOf<InterruptedException>()
            executorThread.isInterrupted.shouldBeFalse()

            currentThread().isInterrupted.shouldBeFalse()
        }

        // await the second submitted task finish
        executor.submit { runThreads.add(currentThread()) }.get().shouldBeNull()
        // interrupted status is unset
        currentThread().isInterrupted.shouldBeFalse()

        runThreads.shouldHaveSize(2)
        // thread is reused after interrupt (throw InterruptedException)
        runThreads.shouldForAll { it === executorThread }
    }

    test("task interruption - isInterrupted") {
        val runThreads = CopyOnWriteArrayList<Thread>()

        run {
            val submitLatch = CountDownLatch(1)
            val f: Future<*> = executor.submit {
                runThreads.add(currentThread())
                submitLatch.countDown()

                // busy loop
                var sum: Long = 0
                for (lng in (0..Long.MAX_VALUE)) {
                    if (currentThread().isInterrupted) break
                    sum += lng
                }
            }
            // await submitted task start
            submitLatch.await()

            executorThread.interrupt()
            sleep(50)

            f.get().shouldBeNull()
            // thread is reused after interrupt (set isInterrupted)
            executorThread.isInterrupted.shouldBeFalse()

            currentThread().isInterrupted.shouldBeFalse()
        }

        // await the second submitted task finish
        executor.submit { runThreads.add(currentThread()) }.get().shouldBeNull()
        currentThread().isInterrupted.shouldBeFalse()

        runThreads.shouldHaveSize(2)
        // reuse thread
        runThreads.shouldForAll { it === executorThread }
    }

    test("CallerRunsPolicy of ThreadPoolExecutor: exception thrown by command is propagated to caller") {
        val callerThread = currentThread()
        val rte = RuntimeException("Boom!!!")

        val latch = CountDownLatch(1)
        val executor = ThreadPoolExecutor(
            0, 1, 3, TimeUnit.SECONDS, SynchronousQueue(), ThreadPoolExecutor.CallerRunsPolicy()
        )
        val f: Future<*> = executor.submit {
            currentThread().shouldNotBeSameInstanceAs(callerThread)
            latch.await()
        }

        shouldThrowExactly<RuntimeException> {
            executor.execute {
                currentThread().shouldBeSameInstanceAs(callerThread)
                throw rte
            }
        }.shouldBeSameInstanceAs(rte)

        latch.countDown()
        f.get().shouldBeNull()
    }

    beforeSpec {
        // warmup and set executorThread
        executor.submit({ executorThread = currentThread() }).get()
    }

    afterSpec {
        executor.shutdownNow()
    }
})
