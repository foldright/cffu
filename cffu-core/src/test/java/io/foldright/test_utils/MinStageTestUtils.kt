@file:JvmName("MinStageTestUtils")

package io.foldright.test_utils

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.concurrent.*

fun <T> CompletableFuture<T>.shouldBeMinimalStage() {
    // Java 8 not support minimal stage CF
    if (isJava9Plus()) shouldMinCf(true)
    else shouldNotMinCf(true)
}

private val blackHoleExecutor = Executor { /* do nothing */ }

private fun <T> CompletableFuture<T>.shouldMinCf(recursive: Boolean = false) {
    shouldCompletionStageMethodsAllowed(recursive)

    // unsupported because this is a minimal stage

    if (isJava9Plus()) shouldThrowExactly<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }.message.shouldBeNull()
    if (isJava9Plus()) shouldThrowExactly<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }.message.shouldBeNull()

    //# Read(explicitly) methods of CompletableFuture

    shouldThrowExactly<UnsupportedOperationException> {
        get()
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        get(1, TimeUnit.MILLISECONDS)
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        join()
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        getNow(null)
    }.message.shouldBeNull()
    if (isJava19Plus()) shouldThrowExactly<UnsupportedOperationException> {
        resultNow()
    }.message.shouldBeNull()
    if (isJava19Plus()) shouldThrowExactly<UnsupportedOperationException> {
        exceptionNow()
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        isDone
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        isCompletedExceptionally
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        isCancelled
    }.message.shouldBeNull()
    if (isJava19Plus()) shouldThrowExactly<UnsupportedOperationException> {
        state()
    }.message.shouldBeNull()

    //# Write methods of CompletableFuture

    shouldThrowExactly<UnsupportedOperationException> {
        complete(null)
    }.message.shouldBeNull()
    if (isJava9Plus()) shouldThrowExactly<UnsupportedOperationException> {
        completeAsync(null)
    }.message.shouldBeNull()
    if (isJava9Plus()) shouldThrowExactly<UnsupportedOperationException> {
        completeAsync(null, null)
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
        completeExceptionally(null)
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
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
    shouldThrowExactly<UnsupportedOperationException> {
        numberOfDependents
    }.message.shouldBeNull()

    //# Other dangerous methods of CompletableFuture

    shouldThrowExactly<UnsupportedOperationException> {
        obtrudeValue(null)
    }.message.shouldBeNull()
    shouldThrowExactly<UnsupportedOperationException> {
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

    if (isCompletedExceptionally) shouldThrowExactly<ExecutionException> { get() }
    else get()

    if (isCompletedExceptionally) shouldThrowExactly<ExecutionException> { get(1, TimeUnit.MILLISECONDS) }
    else get(1, TimeUnit.MILLISECONDS)

    if (isCompletedExceptionally) shouldThrowExactly<CompletionException> {
        join()
    } else join()

    if (isCompletedExceptionally) shouldThrowExactly<CompletionException> {
        getNow(null)
    } else getNow((null))

    if (isJava19Plus())
        if (isCompletedExceptionally) shouldThrowExactly<IllegalStateException> { resultNow() }
        else resultNow()

    if (isJava19Plus())
        if (isCompletedExceptionally) exceptionNow()
        else shouldThrowExactly<IllegalStateException> { exceptionNow() }

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

    shouldThrowExactly<UnsupportedOperationException> {
        orTimeout(1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        completeOnTimeout(null, 1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"

    shouldThrowExactly<UnsupportedOperationException> {
        get()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        get(1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        join()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        get()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        getNow(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        getSuccessNow(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        resultNow()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        exceptionNow()
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        isDone
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        isCompletedExceptionally
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        isCancelled
    }.message shouldBe "unsupported because this is a minimal stage"
    if (isJava19Plus()) shouldThrowExactly<UnsupportedOperationException> {
        state()
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Write methods of CompletableFuture

    shouldThrowExactly<UnsupportedOperationException> {
        complete(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeAsync(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeAsync(null, null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionally(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionally(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionallyAsync(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        completeExceptionallyAsync(null, null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
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
    shouldThrowExactly<UnsupportedOperationException> {
        numberOfDependents
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Other dangerous methods of CompletableFuture

    shouldThrowExactly<UnsupportedOperationException> {
        obtrudeValue(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
        obtrudeException(null)
    }.message shouldBe "unsupported because this is a minimal stage"
    if (recursive) newIncompleteFuture<T>().shouldNotMinCffu()

    ////////////////////////////////////////////////////////////
    // Cffu specified methods
    ////////////////////////////////////////////////////////////

    shouldThrowExactly<UnsupportedOperationException> {
        join(1, TimeUnit.MILLISECONDS)
    }.message shouldBe "unsupported because this is a minimal stage"
    shouldThrowExactly<UnsupportedOperationException> {
        cffuState()
    }.message shouldBe "unsupported because this is a minimal stage"

    //# Cffu Re-Config methods
    if (recursive)
        withCffuFactory(CffuFactory.builder(blackHoleExecutor).build()).shouldMinCffu()

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

    if (isCompletedExceptionally) shouldThrowExactly<ExecutionException> { get() }
    else get()

    if (isCompletedExceptionally) shouldThrowExactly<ExecutionException> { get(1, TimeUnit.MILLISECONDS) }
    else get(1, TimeUnit.MILLISECONDS)

    if (isCompletedExceptionally) shouldThrowExactly<CompletionException> {
        join()
    } else join()

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    if (isCompletedExceptionally) shouldThrowExactly<CompletionException> {
        getNow(null)
    } else getNow((null))

    getSuccessNow(null)

    if (isCompletedExceptionally) shouldThrowExactly<IllegalStateException> { resultNow() }
    else resultNow()

    if (isCompletedExceptionally) exceptionNow()
    else shouldThrowExactly<IllegalStateException> { exceptionNow() }

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
        withCffuFactory(CffuFactory.builder(blackHoleExecutor).build()).shouldNotMinCffu()

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
