package io.foldright.cffu2

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeSameInstanceAs

class CFUTests : FunSpec({
    test("nonExSwallowedFunction") {
        CompletableFutureUtils.nonExSwallowedFunction<RuntimeException, Int>(
            null, false
        ).shouldBeNull()

        run {
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedFunction<RuntimeException?, Int>(
                    { throw new },
                    false
                )!!.apply(null)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedFunction<RuntimeException, Int>(
                    { throw new }, false
                )!!.apply(original)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldContainExactly(original)
            original.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedFunction<RuntimeException, Int>(
                    { throw new }, true
                )!!.apply(original)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
            original.suppressed.shouldContainExactly(new)
        }
    }

    test("nonExSwallowedBiFunction") {
        CompletableFutureUtils.nonExSwallowedBiFunction<Int, RuntimeException, Int>(
            null, false
        ).shouldBeNull()

        run {
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedBiFunction<Int, RuntimeException?, Int>(
                    { _, _ -> throw new }, false
                )!!.apply(42, null)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")

            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedBiFunction<Int, RuntimeException, Int>(
                    { _, _ -> throw new }, false
                )!!.apply(42, original)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldContainExactly(original)
            original.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedBiFunction<Int, RuntimeException, Int>(
                    { _, _ -> throw new }, true
                )!!.apply(42, original)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
            original.suppressed.shouldContainExactly(new)
        }
    }

    test("nonExSwallowedBiConsumer") {
        CompletableFutureUtils.nonExSwallowedBiConsumer<Int, RuntimeException>(
            null, false
        ).shouldBeNull()

        run {
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedBiConsumer<Int, RuntimeException?>(
                    { _, _ -> throw new }, false
                )!!.accept(42, null)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")

            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedBiConsumer<Int, RuntimeException>(
                    { _, _ -> throw new }, false
                )!!.accept(42, original)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldContainExactly(original)
            original.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            shouldThrowExactly<RuntimeException> {
                CompletableFutureUtils.nonExSwallowedBiConsumer<Int, RuntimeException>(
                    { _, _ -> throw new }, true
                )!!.accept(42, original)
            }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
            original.suppressed.shouldContainExactly(new)
        }
    }
})
