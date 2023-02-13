package io.foldright.cffu

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random
import kotlin.random.nextULong

class CompletableFutureUseTest : AnnotationSpec() {
    /**
     * - `Async` methods(e.g. `runAsync`/`thenRunAsync`, etc.) can set `executor`;
     * if executor argument is absent use default executor of [CompletableFuture.ASYNC_POOL] (normally is a [ForkJoinPool]).
     * - non-`Async` methods use the executor of previous [CompletableFuture].
     */
    @Test
    fun executor_inheritance_behavior() {
        val testThread = Thread.currentThread()
        CompletableFuture
            .runAsync {
                Thread.currentThread() shouldNotBe testThread

                Thread.currentThread().name shouldNotStartWith threadNamePrefix
            }
            .thenRunAsync({
                Thread.currentThread().name shouldStartWith threadNamePrefix
            }, executor) // !! switched executor !!
            .thenRun {
                // when run NOT async,
                // executor is INHERITED after switch.
                Thread.currentThread().name shouldStartWith threadNamePrefix
            }
            .thenRunAsync {
                // when run ASYNC,
                //
                // - executor is NOT inherited after switch!!
                // - use the DEFAULT EXECUTOR of CompletableFuture, if no executor specified.
                Thread.currentThread().name shouldNotStartWith threadNamePrefix
            }
            .join()
    }

    ////////////////////////////////////////////////////////////////////////////////
    // executor field
    ////////////////////////////////////////////////////////////////////////////////

    private lateinit var executor: ExecutorService

    private val threadNamePrefix = "CompletableFutureUseTest_${Random.nextULong()}-"

    @BeforeAll
    fun beforeAll() {
        val threadFactory = ThreadFactory { r ->
            val counter = AtomicLong()
            Thread(r).apply {
                name = "$threadNamePrefix${counter.getAndIncrement()}"
                isDaemon = true
            }
        }
        executor = ThreadPoolExecutor(
            4, 4, 1, TimeUnit.MINUTES,
            ArrayBlockingQueue(10), threadFactory
        )
    }

    @AfterAll
    fun afterAll() {
        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.SECONDS) shouldBe true
    }
}
