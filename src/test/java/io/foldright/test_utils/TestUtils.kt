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
// Helper functions for ApiCompatibilityTest
//    - CompletableFutureApiCompatibilityTest
//    - CffuApiCompatibilityTest
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

fun <T> CompletableFuture<T>.shouldBeMinimalStage() {
    shouldMinCf(true)
}

private fun <T> CompletableFuture<T>.shouldMinCf(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    // unsupported because this a minimal stage

    if (isJavaVersion9Plus()) shouldThrow<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }
    if (isJavaVersion9Plus()) shouldThrow<UnsupportedOperationException> {
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
        cancel(false)
    }

    //# Re-Config methods

    if (recursive) {
        if (isJavaVersion9Plus())
            (minimalCompletionStage() as CompletableFuture<T>).shouldMinCf()

        toCompletableFuture().shouldNotMinCf()

        if (isJavaVersion9Plus()) copy().shouldMinCf()
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
        if (isJavaVersion9Plus()) it.shouldMinCf()
        else it.shouldNotMinCf()
    }
}

fun <T> CompletableFuture<T>.shouldNotBeMinimalStage() {
    shouldNotMinCf(true)
}

private fun <T> CompletableFuture<T>.shouldNotMinCf(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    this.complete(null) // avoid running CF blocking

    if (recursive && isJavaVersion9Plus()) {
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

    if (isJavaVersion19Plus())
        if (isCompletedExceptionally) shouldThrow<IllegalStateException> { resultNow() }
        else resultNow()

    if (isJavaVersion19Plus())
        if (isCompletedExceptionally) exceptionNow()
        else shouldThrow<IllegalStateException> { exceptionNow() }

    this.isDone
    // this.isCompletedExceptionally // used above
    this.isCancelled
    if (isJavaVersion19Plus()) state()

    //# Write methods of CompletableFuture

    // complete(null) // used above
    if (recursive) {
        if (isJavaVersion9Plus()) {
            completeAsync { null }.shouldNotMinCf()
            completeAsync({ null }, commonPool()).shouldNotMinCf()
        }
        completeExceptionally(RuntimeException())
        cancel(false)
    }

    //# Re-Config methods

    if (recursive) {
        if (isJavaVersion9Plus()) (minimalCompletionStage() as CompletableFuture<T>).shouldMinCf()
        toCompletableFuture().shouldNotMinCf()
    }

    //# Getter methods of properties
    if (isJavaVersion9Plus()) defaultExecutor()

    //# Inspection methods of Cffu
    numberOfDependents

    //# Other dangerous methods of CompletableFuture

    obtrudeException(RuntimeException())
    obtrudeValue(null)

    if (recursive && isJavaVersion9Plus()) newIncompleteFuture<T>().shouldNotMinCf()
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
    if (isJavaVersion19Plus()) shouldThrow<UnsupportedOperationException> {
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
    if (recursive)
        if (isJavaVersion9Plus()) newIncompleteFuture<T>().shouldMinCffu()
        else newIncompleteFuture<T>().shouldNotBeMinimalStage()

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
        resetCffuFactory(newCffuFactoryBuilder(commonPool()).build()).shouldMinCffu()

    //# Getter methods of properties
    cffuFactory()
    forbidObtrudeMethods()
    isMinimalStage.shouldBeTrue()

    //# Inspection methods of Cffu
    if (recursive)
        if (isJavaVersion9Plus()) cffuUnwrap().shouldMinCf()
        else cffuUnwrap().shouldNotMinCf()
}

fun <T> Cffu<T>.shouldNotBeMinimalStage() {
    shouldNotMinCffu(true)
}

private fun <T> Cffu<T>.shouldNotMinCffu(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    this.complete(null) // avoid running CF blocking

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
    if (isJavaVersion19Plus()) state()

    //# Write methods of CompletableFuture

    // complete(null) // used above
    if (recursive) {
        completeAsync { null }.shouldNotMinCffu()
        completeAsync({ null }, commonPool()).shouldNotMinCffu()
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


    shouldCompletionStageMethodsAllowed(recursive)

    cffuJoin(1, TimeUnit.MILLISECONDS)
    cffuState()

    //# Cffu Re-Config methods
    if (recursive)
        resetCffuFactory(newCffuFactoryBuilder(commonPool()).build()).shouldNotMinCffu()

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
        thenRunAsync({}, commonPool()).let {
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
        thenAcceptAsync({}, commonPool()).let {
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
        thenApplyAsync({}, commonPool()).let {
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
        runAfterBothAsync(cf, {}, commonPool()).let {
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
        thenAcceptBothAsync(cf, { _, _ -> }, commonPool()).let {
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
        thenCombineAsync(cf, { _, _ -> }, commonPool()).let {
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
        runAfterEitherAsync(cf, {}, commonPool()).let {
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
        acceptEitherAsync(cf, {}, commonPool()).let {
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
        applyToEitherAsync(cf, {}, commonPool()).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }

    shouldNotThrow<UnsupportedOperationException> {
        exceptionally { null }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyAsync { null }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyAsync({ null }, commonPool()).let {
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
        thenComposeAsync({ cf }, commonPool()).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyCompose { cf }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyComposeAsync { cf }.let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
        }
    }
    if (isJavaVersion12Plus() && this !is Cffu<*>) shouldNotThrow<UnsupportedOperationException> {
        exceptionallyComposeAsync({ cf }, commonPool()).let {
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
        whenCompleteAsync({ _, _ -> }, commonPool()).let {
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
        handleAsync({ _, _ -> }, commonPool()).let {
            if (recursive) it.shouldCompletionStageMethodsAllowed()
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
