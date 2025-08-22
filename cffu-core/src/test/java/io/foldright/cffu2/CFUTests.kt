package io.foldright.cffu2

import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.function.BiConsumer
import java.util.function.BiFunction
import java.util.function.Function

class CFUTests : FunSpec({
    @Suppress("USELESS_CAST", "INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
    test("nonExSwallowedFunction") {
        CompletableFutureUtils.nonExSwallowedFunction(
            null as? java.util.function.Function<RuntimeException, Int>,
            false
        ).shouldBeNull()

        run {
            val new = RuntimeException("new")
            val f: java.util.function.Function<RuntimeException?, Int> = CompletableFutureUtils.nonExSwallowedFunction(
                Function<RuntimeException?, Int> { throw new },
                false
            )!!
            shouldThrowExactly<RuntimeException> { f.apply(null) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            val f: Function<RuntimeException, Int> = CompletableFutureUtils.nonExSwallowedFunction(
                Function<RuntimeException, Int> { throw new },
                false
            )!!
            shouldThrowExactly<RuntimeException> { f.apply(original) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldContainExactly(original)
            original.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            val f: Function<RuntimeException, Int> = CompletableFutureUtils.nonExSwallowedFunction(
                Function<RuntimeException, Int> { throw new },
                true
            )!!
            shouldThrowExactly<RuntimeException> { f.apply(original) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
            original.suppressed.shouldContainExactly(new)
        }
    }

    @Suppress("USELESS_CAST", "INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
    test("nonExSwallowedBiFunction") {
        CompletableFutureUtils.nonExSwallowedBiFunction(
            null as? BiFunction<Int, RuntimeException, Int>,
            false
        ).shouldBeNull()

        run {
            val new = RuntimeException("new")
            val f: BiFunction<Int, RuntimeException?, Int> = CompletableFutureUtils.nonExSwallowedBiFunction(
                BiFunction<Int, RuntimeException?, Int> { _, _ -> throw new },
                false
            )!!
            shouldThrowExactly<RuntimeException> { f.apply(42, null) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")

            val f: BiFunction<Int, RuntimeException, Int> = CompletableFutureUtils.nonExSwallowedBiFunction(
                BiFunction<Int, RuntimeException, Int> { _, _ -> throw new },
                false
            )!!
            shouldThrowExactly<RuntimeException> { f.apply(42, original) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldContainExactly(original)
            original.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            val f: BiFunction<Int, RuntimeException, Int> = CompletableFutureUtils.nonExSwallowedBiFunction(
                BiFunction<Int, RuntimeException, Int> { _, _ -> throw new },
                true
            )!!
            shouldThrowExactly<RuntimeException> { f.apply(42, original) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
            original.suppressed.shouldContainExactly(new)
        }
    }

    @Suppress("USELESS_CAST", "INFERRED_TYPE_VARIABLE_INTO_EMPTY_INTERSECTION_WARNING")
    test("nonExSwallowedBiConsumer") {
        CompletableFutureUtils.nonExSwallowedBiConsumer(
            null as? BiConsumer<Int, RuntimeException>,
            false
        ).shouldBeNull()

        run {
            val new = RuntimeException("new")

            val f: BiConsumer<Int, RuntimeException?> = CompletableFutureUtils.nonExSwallowedBiConsumer(
                BiConsumer<Int, RuntimeException?> { _, _ -> throw new },
                false
            )!!
            shouldThrowExactly<RuntimeException> { f.accept(42, null) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")

            val f: BiConsumer<Int, RuntimeException> = CompletableFutureUtils.nonExSwallowedBiConsumer(
                BiConsumer<Int, RuntimeException> { _, _ -> throw new },
                false
            )!!
            shouldThrowExactly<RuntimeException> { f.accept(42, original) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldContainExactly(original)
            original.suppressed.shouldBeEmpty()
        }

        run {
            val original = RuntimeException("original")
            val new = RuntimeException("new")
            val f: BiConsumer<Int, RuntimeException> = CompletableFutureUtils.nonExSwallowedBiConsumer(
                BiConsumer<Int, RuntimeException> { _, _ -> throw new },
                true
            )!!
            shouldThrowExactly<RuntimeException> { f.accept(42, original) }.shouldBeSameInstanceAs(new)
            new.suppressed.shouldBeEmpty()
            original.suppressed.shouldContainExactly(new)
        }
    }
})
