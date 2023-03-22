@file:JvmName("CffuTestUtils")

package io.foldright.cffu

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeTypeOf
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.*

fun shouldBeMinStage(stage: CompletableFuture<Int>) {
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

fun shouldBeMinStage(stage: Cffu<Int>) {
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

fun shouldNotBeMinStage(stage: CompletableFuture<Int>) {
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

fun shouldNotBeMinStage(stage: Cffu<Int>) {
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
