package io.foldright.cffu

import io.foldright.test_utils.testThreadPoolExecutor
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.*


@Suppress("ClassName")
class CffuFactory_MinimalStage_Test : FunSpec({
    val cffuFactory = CffuFactoryBuilder.newCffuFactoryBuilder(testThreadPoolExecutor).build()

    fun testMinimalStage(cffuMin: CompletionStage<Int>): CompletableFuture<Int> {
        cffuMin.shouldBeTypeOf<Cffu<Int>>()
        shouldBeMinStage(cffuMin)
        shouldBeMinStage(cffuMin.copy())

        cffuMin.cf.shouldBeInstanceOf<CompletableFuture<Int>>()
        if (isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            shouldBeMinStage(cffuMin.cf)
        } else {
            shouldNotBeMinStage(cffuMin.cf)
        }

        // `toCompletableFuture` convert to a normal CF
        val cf = cffuMin.cf.toCompletableFuture()
        shouldNotBeMinStage(cf)
        return cf
    }

    test("cffuFactory.completedStage") {
        val stage: CompletionStage<Int> = cffuFactory.completedStage(42)

        testMinimalStage(stage).let { cf ->
            @Suppress("BlockingMethodInNonBlockingContext")
            cf.get() shouldBe 42
        }

        testMinimalStage(stage.thenApply { x -> x }).let { cf ->
            @Suppress("BlockingMethodInNonBlockingContext")
            cf.get() shouldBe 42
        }
    }

    test("cffuFactory.failedStage") {
        val rte = RuntimeException("Bang")
        val stage: CompletionStage<Int> = cffuFactory.failedStage(rte)

        testMinimalStage(stage).let { cf ->
            shouldThrow<CompletionException> {
                @Suppress("BlockingMethodInNonBlockingContext")
                cf.join()
            }.cause shouldBeSameInstanceAs rte
        }

        testMinimalStage(stage.exceptionally { _ -> 42 }).let { cf ->
            @Suppress("BlockingMethodInNonBlockingContext")
            cf.get() shouldBe 42
        }
    }
})

private fun shouldBeMinStage(stage: CompletableFuture<Int>) {
    // MinimalStage - UOE
    shouldThrow<UnsupportedOperationException> {
        stage.get()
    }
    shouldThrow<UnsupportedOperationException> {
        stage.join()
    }
    shouldThrow<UnsupportedOperationException> {
        stage.isDone
    }
    shouldThrow<UnsupportedOperationException> {
        stage.complete(42)
    }

    if (isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
        shouldThrow<UnsupportedOperationException> {
            stage.orTimeout(1, TimeUnit.MILLISECONDS)
        }
    }
}

private fun shouldBeMinStage(stage: Cffu<Int>) {
    shouldThrow<UnsupportedOperationException> {
        stage.orTimeout(1, TimeUnit.MILLISECONDS)
    }

    // MinimalStage - UOE
    shouldThrow<UnsupportedOperationException> {
        stage.get()
    }
    shouldThrow<UnsupportedOperationException> {
        stage.join()
    }
    shouldThrow<UnsupportedOperationException> {
        stage.isDone
    }
    shouldThrow<UnsupportedOperationException> {
        stage.complete(4242)
    }
}

private fun shouldNotBeMinStage(stage: CompletableFuture<Int>) {
    try {
        stage.get()
    } catch (_: ExecutionException) {
    }
    stage.isDone
    try {
        stage.join()
    } catch (_: CompletionException) {
    }

    stage.complete(4242)

    if (isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
        stage.orTimeout(1, TimeUnit.MILLISECONDS)
    }
}

private fun shouldNotBeMinStage(stage: Cffu<Int>) {
    try {
        stage.get()
    } catch (_: ExecutionException) {
    }
    stage.isDone
    try {
        stage.join()
    } catch (_: CompletionException) {
    }

    stage.complete(4242)

    stage.orTimeout(1, TimeUnit.MILLISECONDS)
}
