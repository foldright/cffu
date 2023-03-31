@file:JvmName("TestUtils")

package io.foldright.test_utils

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.test.TestCase
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils.isJavaVersionAtLeast
import java.util.concurrent.*


////////////////////////////////////////////////////////////////////////////////
// CF creation util functions
//
//   - with sleep delay
//   - compatibility logic of Java version
////////////////////////////////////////////////////////////////////////////////

fun <T> createIncompleteFuture(): CompletableFuture<T> = CompletableFuture()

fun <T> createFailedFuture(ex: Throwable) = CompletableFuture<T>().apply {
    completeExceptionally(ex)
}

////////////////////////////////////////////////////////////////////////////////
// Helper functions for api compatibility test:
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
fun <T> safeNewFailedCffu(executorService: ExecutorService, t: Throwable): Cffu<T>? {
    return newCffuFactoryBuilder(executorService).build().failedFuture(t)
}

@Suppress("UNUSED_PARAMETER")
fun assertDefaultRunThreadOfCompletableFuture(executorService: ExecutorService) {
    // do nothing
}

fun assertRunThreadOfCompletableFuture(executorService: ExecutorService) {
    assertRunInExecutor(executorService)
}

fun assertDefaultRunThreadOfCffu(executorService: ExecutorService) {
    assertRunInExecutor(executorService)
}

fun assertRunThreadOfCffu(executorService: ExecutorService) {
    assertRunInExecutor(executorService)
}

////////////////////////////////////////////////////////////////////////////////
// Simple util functions
////////////////////////////////////////////////////////////////////////////////

/**
 * sleep without throwing checked exception
 */
@JvmOverloads
fun sleep(millis: Long = 10) {
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

fun <T> CompletableFuture<T>.shouldBeMinimalStage() {
    shouldMinCf(true)
}

private val blackHoleExecutor = Executor { /* do nothing */ }

private fun <T> CompletableFuture<T>.shouldMinCf(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    // unsupported because this a minimal stage

    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }
    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }

    //# Read(explicitly) methods of CompletableFuture

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
        getNow(null)
    }
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
        resultNow()
    }
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
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
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
        state()
    }

    //# Write methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        complete(null)
    }
    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        completeAsync(null)
    }
    if (isJava9Plus()) shouldThrow<UnsupportedOperationException> {
        completeAsync(null, null)
    }
    shouldThrow<UnsupportedOperationException> {
        completeExceptionally(null)
    }
    shouldThrow<UnsupportedOperationException> {
        cancel(false)
    }

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
    }

    //# Other dangerous methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        obtrudeValue(null)
    }
    shouldThrow<UnsupportedOperationException> {
        obtrudeException(null)
    }

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
    if (isJava19Plus()) shouldThrow<UnsupportedOperationException> {
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
    }

    //# Other dangerous methods of CompletableFuture

    shouldThrow<UnsupportedOperationException> {
        obtrudeValue(null)
    }
    shouldThrow<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        obtrudeException(null)
    }
    if (recursive) newIncompleteFuture<T>().shouldMinCffu()

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
    if (recursive)
        resetCffuFactory(newCffuFactoryBuilder(blackHoleExecutor).build()).shouldMinCffu()

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

    cffuJoin(1, TimeUnit.MILLISECONDS)
    cffuState()

    //# Cffu Re-Config methods
    if (recursive)
        resetCffuFactory(newCffuFactoryBuilder(blackHoleExecutor).build()).shouldNotMinCffu()

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

////////////////////////////////////////////////////////////////////////////////
// Java Version Checker
////////////////////////////////////////////////////////////////////////////////

fun isJava9Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_9)

fun isJava12Plus() = isJavaVersionAtLeast(JavaVersion.JAVA_12)

fun isJava19Plus(): Boolean = try {
    val cf = CompletableFuture.completedFuture(42)
    // `resultNow` is the new method of CompletableFuture since java 19
    cf.resultNow()
    true
} catch (_: NoSuchMethodError) {
    false
}

// about CI env var
// https://docs.github.com/en/actions/learn-github-actions/variables#default-environment-variables
fun isCiEnv(): Boolean = System.getenv("CI")?.lowercase() == "true"

////////////////////////////////////////////////////////////////////////////////
// Kotest conditions
////////////////////////////////////////////////////////////////////////////////

val java9Plus: (TestCase) -> Boolean = { isJava9Plus() }

val java12Plus: (TestCase) -> Boolean = { isJava12Plus() }
