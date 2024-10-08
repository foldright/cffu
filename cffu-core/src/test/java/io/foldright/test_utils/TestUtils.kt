@file:JvmName("TestUtils")

package io.foldright.test_utils

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.test.TestCase
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.*
import java.util.function.Supplier


////////////////////////////////////////////////////////////////////////////////
// region# util methods for testing
////////////////////////////////////////////////////////////////////////////////

fun <T> incompleteCf(): CompletableFuture<T> = CompletableFuture()

@JvmOverloads
fun <T> completeLaterCf(value: T, millis: Long = MEDIAN_WAIT_MS): CompletableFuture<T> = CompletableFuture.supplyAsync {
    sleep(millis)
    value
}

@JvmOverloads
fun <T> completeLaterCf(
    value: () -> T, millis: Long = MEDIAN_WAIT_MS, executor: Executor = DEFAULT_EXECUTOR
): CompletableFuture<T> {
    val action = Supplier {
        sleep(millis)
        value()
    }
    return if (executor === DEFAULT_EXECUTOR) CompletableFuture.supplyAsync(action)
    else CompletableFuture.supplyAsync(action, executor)
}

@JvmOverloads
fun <T> cancelledFuture(mayInterruptIfRunning: Boolean = false): CompletableFuture<T> = CompletableFuture<T>().apply {
    cancel(mayInterruptIfRunning)
}

private val DEFAULT_EXECUTOR: Executor = Executor { /* do nothing */ }

@JvmOverloads
fun <T> supplyLater(value: T, millis: Long = MEDIAN_WAIT_MS) = Supplier<T> {
    sleep(millis)
    value
}

@JvmOverloads
fun <T> supplyLater(ex: Throwable, millis: Long = MEDIAN_WAIT_MS) = Supplier<T> {
    sleep(millis)
    throw ex
}

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Helper functions for API compatibility test
//  - CompletableFutureApiCompatibilityTest
//  - CffuApiCompatibilityTest
////////////////////////////////////////////////////////////////////////////////

/**
 * safe means works under java 8
 */
@Suppress("UNUSED_PARAMETER")
fun <T> safeNewFailedCompletableFuture(executorService: ExecutorService, t: Throwable): CompletableFuture<T> {
    val failed: CompletableFuture<T> = CompletableFuture<T>()
    failed.completeExceptionally(t)
    return failed
}

/**
 * safe means works under java 8
 */
fun <T> safeNewFailedCffu(executorService: ExecutorService, t: Throwable): Cffu<T> {
    return CffuFactory.builder(executorService).build().failedFuture(t)
}

fun assertCompletableFutureRunInDefaultThread(executorService: ExecutorService) {
    assertNotRunningInExecutor(executorService)
}

fun assertCompletableFutureRunInThreadOf(executorService: ExecutorService) {
    assertRunningInExecutor(executorService)
}

fun assertCffuRunInDefaultThread(executorService: ExecutorService) {
    assertRunningInExecutor(executorService)
}

fun assertCffuRunInThreadOf(executorService: ExecutorService) {
    assertRunningInExecutor(executorService)
}

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# some simple util functions
////////////////////////////////////////////////////////////////////////////////

/**
 * sleep without throwing checked exception
 */
@JvmOverloads
fun sleep(millis: Long = SHORT_WAIT_MS) {
    Thread.sleep(millis)
}

/**
 * sleep short time
 */
@JvmOverloads
fun nap(millis: Long = SHORT_WAIT_MS) {
    Thread.sleep(millis)
}

/**
 * sleep long time
 */
@JvmOverloads
fun snoreZzz(millis: Long = MEDIAN_WAIT_MS) {
    Thread.sleep(millis)
}

fun sneakyThrow(ex: Throwable): Unit = throw ex

fun <T> merge(list1: List<T>, list2: List<T>) = list1.merge(list2)

@JvmName("mergeExt")
infix fun <T> List<T>.merge(other: List<T>): List<T> = mutableListOf<T>().apply {
    addAll(this@merge)
    addAll(other)
}

fun addCurrentThreadName(names: List<String>) = names + Thread.currentThread().name

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Assertion functions for CF/Cffu
////////////////////////////////////////////////////////////////////////////////

fun <T> CompletableFuture<T>.shouldBeMinimalStage() {
    shouldMinCf(true)
}

private val blackHoleExecutor = Executor { /* do nothing */ }

private fun <T> CompletableFuture<T>.shouldMinCf(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    // unsupported because this is a minimal stage

    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }.message.shouldBeNull()
    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }.message.shouldBeNull()

    //# Read(explicitly) methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        get()
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        get(1, TimeUnit.MILLISECONDS)
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        join()
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        getNow(null)
    }.message.shouldBeNull()
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
        resultNow()
    }.message.shouldBeNull()
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
        exceptionNow()
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        isDone
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        isCompletedExceptionally
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        isCancelled
    }.message.shouldBeNull()
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
        state()
    }.message.shouldBeNull()

    //# Write methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        complete(null)
    }.message.shouldBeNull()
    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        completeAsync(null)
    }.message.shouldBeNull()
    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        completeAsync(null, null)
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        completeExceptionally(null)
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        cancel(false)
    }.message.shouldBeNull()

    //# Re-Config methods

    if (recursive) {
        if (isJava9Plus())
            (minimalCompletionStage() as CompletableFuture<T>).shouldMinCf()

        toCompletableFuture().shouldNotMinCf()

        if (isJava9Plus()) copy().shouldMinCf()
    }

    //# Getter methods of properties
    defaultExecutor()

    //# Inspection methods of Cffu
    shouldThrow<UnsupportedOperationException> {
        numberOfDependents
    }.message.shouldBeNull()

    //# Other dangerous methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        obtrudeValue(null)
    }.message.shouldBeNull()
    shouldThrow<UnsupportedOperationException> {
        obtrudeException(null)
    }.message.shouldBeNull()

    if (recursive) newIncompleteFuture<T>().let {
        if (isJava9Plus()) it.shouldMinCf()
        else it.shouldNotMinCf()
    }
}

fun <T> CompletableFuture<T>.shouldNotBeMinimalStage() {
    shouldNotMinCf(true)
}

private fun <T> CompletableFuture<T>.shouldNotMinCf(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    this.complete(null) // make sure completed, avoid running CF blocking

    if (recursive && isJava9Plus()) {
        orTimeout(1, TimeUnit.MILLISECONDS).shouldNotMinCf()
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS).shouldNotMinCf()
    }

    //# Read(explicitly) methods of CompletableFuture

    if (isCompletedExceptionally) shouldThrow<ExecutionException> { get() }
    else get()

    if (isCompletedExceptionally) shouldThrow<ExecutionException> { get(1, TimeUnit.MILLISECONDS) }
    else get(1, TimeUnit.MILLISECONDS)

    if (isCompletedExceptionally) shouldThrow<CompletionException> {
        join()
    } else join()

    if (isCompletedExceptionally) shouldThrow<CompletionException> {
        getNow(null)
    } else getNow((null))

    if (isJava19Plus())
        if (isCompletedExceptionally) shouldThrow<IllegalStateException> { resultNow() }
        else resultNow()

    if (isJava19Plus())
        if (isCompletedExceptionally) exceptionNow()
        else shouldThrow<IllegalStateException> { exceptionNow() }

    this.isDone
    // this.isCompletedExceptionally // used above
    this.isCancelled
    if (isJava19Plus()) state()

    //# Write methods of CompletableFuture

    // complete(null) // used above
    if (recursive) {
        if (isJava9Plus()) {
            completeAsync { null }.shouldNotMinCf()
            completeAsync({ null }, blackHoleExecutor).shouldNotMinCf()
        }
        completeExceptionally(RuntimeException())
        cancel(false)
    }

    //# Re-Config methods

    if (recursive) {
        if (isJava9Plus()) (minimalCompletionStage() as CompletableFuture<T>).shouldMinCf()

        toCompletableFuture().shouldNotMinCf()
    }

    //# Getter methods of properties
    if (isJava9Plus()) defaultExecutor()

    //# Inspection methods of Cffu
    numberOfDependents

    //# Other dangerous methods of CompletableFuture

    obtrudeException(RuntimeException())
    obtrudeValue(null)

    if (recursive && isJava9Plus()) newIncompleteFuture<T>().shouldNotMinCf()
}

fun <T> Cffu<T>.shouldBeMinimalStage() {
    shouldMinCffu(true)
}

private fun <T> Cffu<T>.shouldMinCffu(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    // unsupported because this is a minimal stage

    //# Read(explicitly) methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"

    shouldThrow<UnsupportedOperationException> {
        get()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        get(1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        join()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        get()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        getNow(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        getSuccessNow(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        resultNow()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        exceptionNow()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        isDone
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        isCompletedExceptionally
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        isCancelled
    }.message shouldBe "unsupported because this is a minimal stage"
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
        state()
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Write methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        complete(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeAsync(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeAsync(null, null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionally(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionally(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionallyAsync(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionallyAsync(null, null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        cancel(false)
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Re-Config methods

    if (recursive) {
        (minimalCompletionStage() as Cffu<T>).shouldMinCffu()
        toCompletableFuture().shouldNotMinCf()
        (copy() as Cffu<T>).shouldMinCffu()
    }

    //# Getter methods of properties
    defaultExecutor()

    //# Inspection methods of Cffu
    shouldThrow<UnsupportedOperationException> {
        numberOfDependents
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Other dangerous methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        obtrudeValue(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        obtrudeException(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    if (recursive) newIncompleteFuture<T>().shouldNotMinCffu()

    ////////////////////////////////////////////////////////////
    // Cffu specified methods
    ////////////////////////////////////////////////////////////

    shouldThrow<UnsupportedOperationException> {
        join(1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrow<UnsupportedOperationException> {
        cffuState()
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Cffu Re-Config methods
    if (recursive)
        resetCffuFactory(CffuFactory.builder(blackHoleExecutor).build()).shouldMinCffu()

    //# Getter methods of properties
    cffuFactory()
    forbidObtrudeMethods()
    isMinimalStage.shouldBeTrue()

    //# Inspection methods of Cffu
    if (recursive)
        if (isJava9Plus()) cffuUnwrap().shouldMinCf()
        else cffuUnwrap().shouldNotMinCf()
}

fun <T> Cffu<T>.shouldNotBeMinimalStage() {
    shouldNotMinCffu(true)
}

private fun <T> Cffu<T>.shouldNotMinCffu(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    this.complete(null) // make sure completed, avoid running CF blocking

    if (recursive) {
        orTimeout(1, TimeUnit.MILLISECONDS).shouldNotMinCffu()
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS).shouldNotMinCffu()
    }

    //# Read(explicitly) methods of CompletableFuture

    if (isCompletedExceptionally) shouldThrow<ExecutionException> { get() }
    else get()

    if (isCompletedExceptionally) shouldThrow<ExecutionException> { get(1, TimeUnit.MILLISECONDS) }
    else get(1, TimeUnit.MILLISECONDS)

    if (isCompletedExceptionally) shouldThrow<CompletionException> {
        join()
    } else join()

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    if (isCompletedExceptionally) shouldThrow<CompletionException> {
        getNow(null)
    } else getNow((null))

    getSuccessNow(null)

    if (isCompletedExceptionally) shouldThrow<IllegalStateException> { resultNow() }
    else resultNow()

    if (isCompletedExceptionally) exceptionNow()
    else shouldThrow<IllegalStateException> { exceptionNow() }

    this.isDone
    // this.isCompletedExceptionally // used above
    this.isCancelled
    if (isJava19Plus()) state()

    //# Write methods of CompletableFuture

    // complete(null) // used above
    if (recursive) {
        completeAsync { null }.shouldNotMinCffu()
        completeAsync({ null }, blackHoleExecutor).shouldNotMinCffu()
        completeExceptionally(RuntimeException())
        completeExceptionallyAsync { null }.shouldNotMinCffu()
        completeExceptionallyAsync({ null }, blackHoleExecutor).shouldNotMinCffu()
        cancel(false)
    }

    //# Re-Config methods

    if (recursive) {
        (minimalCompletionStage() as Cffu<T>).shouldMinCffu()

        toCompletableFuture().shouldNotMinCf()
    }

    //# Getter methods of properties
    defaultExecutor()

    //# Inspection methods of Cffu
    numberOfDependents

    //# Other dangerous methods of CompletableFuture

    obtrudeException(RuntimeException())
    obtrudeValue(null)

    if (recursive) newIncompleteFuture<T>().shouldNotMinCffu()

    ////////////////////////////////////////////////////////////
    // Cffu specified methods
    ////////////////////////////////////////////////////////////

    join(1, TimeUnit.MILLISECONDS)
    cffuState()

    //# Cffu Re-Config methods
    if (recursive)
        resetCffuFactory(CffuFactory.builder(blackHoleExecutor).build()).shouldNotMinCffu()

    //# Getter methods of properties
    cffuFactory()
    forbidObtrudeMethods()
    isMinimalStage.shouldBeFalse()

    //# Inspection methods of Cffu
    if (recursive) cffuUnwrap().shouldNotMinCf()
}

private fun <T> CompletionStage<T>.shouldCompletionStageMethodsAllowed(recursive: Boolean = false) {
    val cf: CompletableFuture<T> = CompletableFuture.completedFuture(null)

    shouldNotThrow<UnsupportedOperationException> {
        thenRun {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenRunAsync {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenRunAsync({}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAccept {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptAsync {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptAsync({}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenApply {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenApplyAsync {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenApplyAsync({}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        runAfterBoth(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterBothAsync(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterBothAsync(cf, {}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptBoth(cf) { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptBothAsync(cf) { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptBothAsync(cf, { _, _ -> }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenCombine(cf) { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenCombineAsync(cf) { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenCombineAsync(cf, { _, _ -> }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        runAfterEither(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterEitherAsync(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterEitherAsync(cf, {}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        acceptEither(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        acceptEitherAsync(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        acceptEitherAsync(cf, {}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        applyToEither(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        applyToEitherAsync(cf) {}.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        applyToEitherAsync(cf, {}, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        exceptionally { null }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJava12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyAsync { null }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJava12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyAsync({ null }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        thenCompose { cf }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenComposeAsync { cf }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenComposeAsync({ cf }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJava12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyCompose { cf }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJava12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyComposeAsync { cf }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJava12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyComposeAsync({ cf }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        whenComplete { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        whenCompleteAsync { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        whenCompleteAsync({ _, _ -> }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        handle { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        handleAsync { _, _ -> }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        handleAsync({ _, _ -> }, blackHoleExecutor).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    toString()
}

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Java Version Checker
////////////////////////////////////////////////////////////////////////////////

fun isJava9Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_9)

fun isJava12Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_12)

fun isJava19Plus(): Boolean = isJavaVersionAtLeast(JavaVersion.JAVA_19)

// about CI env var
// https://docs.github.com/en/actions/learn-github-actions/variables#default-environment-variables
fun isCiEnv(): Boolean = System.getenv("CI")?.lowercase() == "true"

fun isCiMacEnv(): Boolean = isCiEnv() && SystemUtils.IS_OS_MAC

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# Kotest conditions
////////////////////////////////////////////////////////////////////////////////

val java9Plus: (TestCase) -> Boolean = { isJava9Plus() }

val java12Plus: (TestCase) -> Boolean = { isJava12Plus() }
