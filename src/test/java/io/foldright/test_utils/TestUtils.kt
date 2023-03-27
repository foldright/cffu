@file:JvmName("TestUtils")

package io.foldright.test_utils

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.test.TestCase
import io.kotest.matchers.booleans.shouldBeTrue
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.*
import java.util.concurrent.ForkJoinPool.commonPool


////////////////////////////////////////////////////////////////////////////////
// CF creation util functions
//
//   - with sleep delay
//   - compatibility logic of Java version
////////////////////////////////////////////////////////////////////////////////

fun <T> createNormallyCompletedFutureWithSleep(value: T): CompletableFuture<T> = CompletableFuture.supplyAsync {
    sleep(10)
    value
}

fun <T, E : Throwable> createExceptionallyCompletedFutureWithSleep(ex: E): CompletableFuture<T> =
    CompletableFuture.supplyAsync {
        sleep(10)
        throw ex
    }

////////////////////////////////////////////////////////////////////////////////
// Simple util functions
////////////////////////////////////////////////////////////////////////////////

/**
 * sleep without throwing checked exception
 */
@JvmOverloads
fun sleep(millis: Long = 2) {
    Thread.sleep(millis)
}

fun <T> merge(list1: List<T>, list2: List<T>) = list1.merge(list2)

@JvmName("mergeExt")
infix fun <T> List<T>.merge(other: List<T>): List<T> = mutableListOf<T>().apply {
    addAll(this@merge)
    addAll(other)
}

fun addCurrentThreadName(names: List<String>) = names + Thread.currentThread().name

////////////////////////////////////////////////////////////////////////////////
// Assertion functions for CF/Cffu
////////////////////////////////////////////////////////////////////////////////

fun <T> CompletableFuture<T>.shouldBeMinimalStage(disableRecursive: Boolean = false) {
    shouldMinCf(false)
}

fun <T> CompletableFuture<T>.shouldNotBeMinimalStage() {
    shouldCompletionStageMethodsAllowed()

    // FIXME  check more methods

    this.complete(null)

    try {
        this.get()
    } catch (_: ExecutionException) {
    }
    this.isDone
    try {
        this.join()
    } catch (_: CompletionException) {
    }

    if (isJavaVersion9Plus()) {
        this.orTimeout(1, TimeUnit.MILLISECONDS)
    }
}

private fun <T> CompletableFuture<T>.shouldMinCf(disableRecursive: Boolean) {
    shouldCompletionStageMethodsAllowed()

    // unsupported because this a minimal stage

    //# Read(explicitly) methods of CompletableFuture

    if (isJavaVersion9Plus()) shouldThrow<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }
    if (isJavaVersion9Plus()) shouldThrow<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }

    shouldThrow<UnsupportedOperationException> {
        get()
    }
    shouldThrow<UnsupportedOperationException> {
        get(1, TimeUnit.MILLISECONDS)
    }
    shouldThrow<UnsupportedOperationException> {
        join()
    }
    shouldThrow<UnsupportedOperationException> {
        get()
    }
    shouldThrow<UnsupportedOperationException> {
        getNow(null)
    }
    if (isJavaVersion19Plus()) shouldThrow<UnsupportedOperationException> {
        resultNow()
    }
    if (isJavaVersion19Plus()) shouldThrow<UnsupportedOperationException> {
        exceptionNow()
    }
    shouldThrow<UnsupportedOperationException> {
        isDone
    }
    shouldThrow<UnsupportedOperationException> {
        isCompletedExceptionally
    }
    shouldThrow<UnsupportedOperationException> {
        isCancelled
    }
    if (isJavaVersion19Plus()) shouldThrow<UnsupportedOperationException> {
        state()
    }

    //# Write methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        complete(null)
    }
    if (isJavaVersion9Plus()) shouldThrow<UnsupportedOperationException> {
        completeAsync(null)
    }
    if (isJavaVersion9Plus()) shouldThrow<UnsupportedOperationException> {
        completeAsync(null, null)
    }
    shouldThrow<UnsupportedOperationException> {
        completeExceptionally(null)
    }
    shouldThrow<UnsupportedOperationException> {
        completeExceptionally(null)
    }
    if (isJavaVersion9Plus()) {
        shouldThrow<UnsupportedOperationException> {
            cancel(false)
        }
    }

    //# Re-Config methods

    if (isJavaVersion9Plus()) minimalCompletionStage().let {
        if (!disableRecursive) (it as CompletableFuture<T>).shouldMinCf(true)
    }

    toCompletableFuture().shouldNotBeMinimalStage()

    if (isJavaVersion9Plus()) copy().let {
        if (!disableRecursive) it.shouldMinCf(true)
    }

    //# Getter methods of properties
    defaultExecutor()

    //# Inspection methods of Cffu
    shouldThrow<UnsupportedOperationException> {
        numberOfDependents
    }

    //# Other dangerous methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        obtrudeValue(null)
    }
    shouldThrow<UnsupportedOperationException> {
        obtrudeException(null)
    }

    newIncompleteFuture<T>().let {
        if (!disableRecursive)
            if (isJavaVersion9Plus()) it.shouldMinCf(true)
            else it.shouldNotBeMinimalStage()
    }
}

fun <T> Cffu<T>.shouldBeMinimalStage() {
    shouldMinCffu(false)
}

fun <T> Cffu<T>.shouldNotBeMinimalStage() {
    shouldCompletionStageMethodsAllowed()

    // FIXME  check more methods

    this.complete(null)

    try {
        this.get()
    } catch (_: ExecutionException) {
    }
    this.isDone
    try {
        this.join()
    } catch (_: CompletionException) {
    }

    this.orTimeout(1, TimeUnit.MILLISECONDS)
}

private fun <T> Cffu<T>.shouldMinCffu(disableRecursive: Boolean) {
    shouldCompletionStageMethodsAllowed()

    // unsupported because this a minimal stage

    //# Read(explicitly) methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }
    shouldThrow<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }

    shouldThrow<UnsupportedOperationException> {
        get()
    }
    shouldThrow<UnsupportedOperationException> {
        get(1, TimeUnit.MILLISECONDS)
    }
    shouldThrow<UnsupportedOperationException> {
        join()
    }
    shouldThrow<UnsupportedOperationException> {
        get()
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        getNow(null)
    }
    shouldThrow<UnsupportedOperationException> {
        resultNow()
    }
    shouldThrow<UnsupportedOperationException> {
        exceptionNow()
    }
    shouldThrow<UnsupportedOperationException> {
        isDone
    }
    shouldThrow<UnsupportedOperationException> {
        isCompletedExceptionally
    }
    shouldThrow<UnsupportedOperationException> {
        isCancelled
    }
    shouldThrow<UnsupportedOperationException> {
        state()
    }

    //# Write methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        complete(null)
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeAsync(null)
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeAsync(null, null)
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionally(null)
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionally(null)
    }
    shouldThrow<UnsupportedOperationException> {
        cancel(false)
    }

    //# Re-Config methods

    if (!disableRecursive) (minimalCompletionStage() as Cffu<T>).shouldMinCffu(true)
    toCompletableFuture().shouldNotBeMinimalStage()
    copy().let {
        if (!disableRecursive) (it as Cffu<T>).shouldMinCffu(true)
    }

    //# Getter methods of properties
    defaultExecutor()

    //# Inspection methods of Cffu
    shouldThrow<UnsupportedOperationException> {
        numberOfDependents
    }

    //# Other dangerous methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        obtrudeValue(null)
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        obtrudeException(null)
    }
    newIncompleteFuture<T>().let {
        if (!disableRecursive)
            if (isJavaVersion9Plus()) it.shouldMinCffu(true)
            else it.shouldNotBeMinimalStage()
    }

    ////////////////////////////////////////////////////////////
    // Cffu specified methods
    ////////////////////////////////////////////////////////////

    shouldThrow<UnsupportedOperationException> {
        cffuJoin(1, TimeUnit.MILLISECONDS)
    }
    shouldThrow<UnsupportedOperationException> {
        cffuState()
    }

    //# Cffu Re-Config methods

    resetCffuFactory(newCffuFactoryBuilder(commonPool()).build()).let {
        if (!disableRecursive) (it as Cffu<T>).shouldMinCffu(true)
    }

    //# Getter methods of properties
    cffuFactory()
    forbidObtrudeMethods()
    isMinimalStage.shouldBeTrue()

    //# Inspection methods of Cffu
    val unwrap: CompletableFuture<T> = cffuUnwrap()
    if (isJavaVersion9Plus()) unwrap.shouldMinCf(true)
    else unwrap.shouldNotBeMinimalStage()
}

private fun <T> CompletionStage<T>.shouldCompletionStageMethodsAllowed(disableRecursive: Boolean = false) {
    val cf: CompletableFuture<T> = CompletableFuture.completedFuture(null)

    shouldNotThrow<UnsupportedOperationException> {
        thenRun {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenRunAsync {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenRunAsync({}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAccept {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptAsync {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptAsync({}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenApply {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenApplyAsync {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenApplyAsync({}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        runAfterBoth(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterBothAsync(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterBothAsync(cf, {}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptBoth(cf) { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptBothAsync(cf) { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenAcceptBothAsync(cf, { _, _ -> }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenCombine(cf) { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenCombineAsync(cf) { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenCombineAsync(cf, { _, _ -> }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        runAfterEither(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterEitherAsync(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        runAfterEitherAsync(cf, {}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        acceptEither(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        acceptEitherAsync(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        acceptEitherAsync(cf, {}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        applyToEither(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        applyToEitherAsync(cf) {}.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        applyToEitherAsync(cf, {}, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        exceptionally { null }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyAsync { null }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyAsync({ null }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        thenCompose { cf }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenComposeAsync { cf }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        thenComposeAsync({ cf }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyCompose { cf }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyComposeAsync { cf }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyComposeAsync({ cf }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        whenComplete { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        whenCompleteAsync { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        whenCompleteAsync({ _, _ -> }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        handle { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        handleAsync { _, _ -> }.let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }
    shouldNotThrow<UnsupportedOperationException> {
        handleAsync({ _, _ -> }, commonPool()).let {
            if (!disableRecursive) it.shouldCompletionStageMethodsAllowed(true)
        }
    }

    toString()
}

////////////////////////////////////////////////////////////////////////////////
// Java Version Checker & Kotest conditions
////////////////////////////////////////////////////////////////////////////////

fun isJavaVersion9Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_9)

fun isJavaVersion12Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_12)

fun isJavaVersion19Plus(): Boolean = try {
    val cf = CompletableFuture.completedFuture(42)
    // `resultNow` is the new method of CompletableFuture since java 19
    cf.resultNow()
    true
} catch (e: NoSuchMethodError) {
    false
}

val java9Plus: (TestCase) -> Boolean = { isJavaVersion9Plus() }

val java12Plus: (TestCase) -> Boolean = { isJavaVersion12Plus() }
