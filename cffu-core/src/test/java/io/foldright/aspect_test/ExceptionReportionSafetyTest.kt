package io.foldright.aspect_test

import io.foldright.cffu.Cffu
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.test_utils.isJava9Plus
import io.foldright.test_utils.java12Plus
import io.foldright.test_utils.testCffuFac
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSingleElement
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException

/**
 * @see ExTest.test_addSuppressed_not_allowed_add_self
 */
class ExceptionReportionSafetyTest : FunSpec({
    ////////////////////////////////////////////////////////////////////////////////
    // CompletableFuture
    ////////////////////////////////////////////////////////////////////////////////

    test("👎 CF.exceptionally() swallows original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionally")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .exceptionally { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        // the exception is swallowed! 👎
        original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👎 CF.exceptionallyAsync() swallows original exception️").config(enabledIf = java12Plus) {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyAsync")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .exceptionallyAsync { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        // the exception is swallowed! 👎
        original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👎 CF.exceptionallyCompose() swallows original exception️").config(enabledIf = java12Plus) {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyCompose")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .exceptionallyCompose { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        // the exception is swallowed! 👎
        original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👎 CF.exceptionallyComposeAsync() swallows original exception️").config(enabledIf = java12Plus) {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyComposeAsync")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .exceptionallyComposeAsync { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        // the exception is swallowed! 👎
        original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👎 CF.handle() swallows original exception") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("handle")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .handle { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        // the exception is swallowed! 👎
        original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👎 CF.handleAsync() swallows original exception") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("handleAsync")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .handleAsync { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        // the exception is swallowed! 👎
        original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👍 CF.whenComplete() does not swallow new exception for Java 9+") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("whenComplete")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .whenComplete { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe original
        // the new exception is not swallowed! 👍
        if (isJava9Plus()) original.suppressed.shouldHaveSingleElement(new)
        // the new exception is swallowed in Java 8! 👎
        else original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    test("👍 CF.whenCompleteAsync() does not swallow new exception for Java 9+") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("whenCompleteAsync")

        val cf: CompletableFuture<Int> = CompletableFutureUtils.failedFuture<Int>(original)
            .whenCompleteAsync { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe original
        // the new exception is not swallowed! 👍
        if (isJava9Plus()) original.suppressed.shouldHaveSingleElement(new)
        // the new exception is swallowed in Java 8! 👎
        else original.suppressed.shouldBeEmpty()
        new.suppressed.shouldBeEmpty()
    }

    ////////////////////////////////////////////////////////////////////////////////
    // Cffu
    ////////////////////////////////////////////////////////////////////////////////

    test("👍 Cffu.catching() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionally")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .catching(RuntimeException::class.java) { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.catchingAsync() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyAsync")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .catchingAsync(RuntimeException::class.java) { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.exceptionally() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionally")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .exceptionally { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.exceptionallyAsync() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyAsync")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .exceptionallyAsync { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.catchingCompose() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyCompose")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .catchingCompose(RuntimeException::class.java) { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.catchingComposeAsync() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyComposeAsync")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .catchingComposeAsync(RuntimeException::class.java) { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.exceptionallyCompose() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyCompose")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .exceptionallyCompose { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.exceptionallyComposeAsync() does not swallow original exception️") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("exceptionallyComposeAsync")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .exceptionallyComposeAsync { throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.handle() does not swallow original exception") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("handle")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .handle { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.handleAsync() does not swallow original exception") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("handleAsync")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .handleAsync { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe new
        original.suppressed.shouldBeEmpty()
        // the original exception is not swallow 👍
        new.suppressed.shouldHaveSingleElement(original)
    }

    test("👍 Cffu.whenComplete() does not swallow new exception") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("whenComplete")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .whenComplete { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe original
        // the new exception is not swallowed! 👍
        original.suppressed.shouldHaveSingleElement(new)
        new.suppressed.shouldBeEmpty()
    }

    test("👍 Cffu.whenCompleteAsync() does not swallow new exception") {
        val original = RuntimeException("Bang")
        val new = RuntimeException("whenCompleteAsync")

        val cf: Cffu<Int> = testCffuFac.failedFuture<Int>(original)
            .whenCompleteAsync { _, _ -> throw new }

        val ee = shouldThrowExactly<ExecutionException> { cf.get() }
        ee.suppressed.shouldBeEmpty()
        ee.suppressed.shouldBeEmpty()

        ee.cause shouldBe original
        // the new exception is not swallowed! 👍
        original.suppressed.shouldHaveSingleElement(new)
        new.suppressed.shouldBeEmpty()
    }
})
