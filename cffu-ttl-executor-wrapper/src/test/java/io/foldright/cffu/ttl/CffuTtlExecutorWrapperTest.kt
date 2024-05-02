package io.foldright.cffu.ttl

import com.alibaba.ttl.TransmittableThreadLocal
import io.foldright.cffu.CffuFactory
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.future.await
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.random.Random
import kotlin.random.nextULong

class CffuTtlExecutorWrapperTest : FunSpec({
    val availableProcessors = Runtime.getRuntime().availableProcessors()
    val executorService = ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES, ArrayBlockingQueue(availableProcessors * 2))

    val ttl = TransmittableThreadLocal<String>()

    test("CffuTtlExecutorWrapper") {
        val factory = CffuFactory.builder(executorService).build()

        val v1 = "v1: ${Random.nextULong()}"
        ttl.set(v1)
        factory.supplyAsync {
            ttl.get()
        }.await() shouldBe v1

        val v2 = "v2: ${Random.nextULong()}"
        ttl.set(v2)
        factory.completedFuture("42")
            .thenApplyAsync { v2 }
            .await() shouldBe v2
    }

    beforeTest {
        // warmup
        (0 until availableProcessors).map {
            executorService.submit { Thread.sleep(10) }
        }.forEach { it.get() }
    }

    afterTest {
        executorService.shutdown()
        executorService.awaitTermination(1, TimeUnit.SECONDS).shouldBeTrue()
    }
})

