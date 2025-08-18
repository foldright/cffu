package io.foldright.study

import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.LLCF
import io.foldright.test_utils.isJava9Plus
import io.foldright.test_utils.java9Plus
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.concurrent.CompletableFuture

/**
 * Completable Future Conversation Methods:
 *
 * - `toCompletableFuture()`
 *     - guarantee to return a non-min-stage instance
 *     - for CompletableFuture, return THIS(a non-min-stage instance)
 *     - for min stage, return NEW a non-min-stage instance
 * - `copy()`
 *     - guarantee to return a NEW instance
 *     - for CompletableFuture, return a NEW non-min-stage instance
 *     - for min stage, return a NEW min-stage instance(STILL min stage)
 * - `minimalCompletionStage()`
 *     - guarantee to return a NEW min-stage instance
 */
class InstanceCreationOfCompletableFutureConversationMethods : FunSpec({
    test("CompletableFuture") {
        val cf = CompletableFuture.completedFuture(42)

        cf.toCompletableFuture().apply {
            // CompletableFuture.toCompletableFuture() return THIS
            shouldBeSameInstanceAs(cf)

            LLCF.isMinStageCf(this).shouldBeFalse()
        }

        cf.copy().apply {
            // CompletableFuture.copy() return a NEW instance
            shouldNotBeSameInstanceAs(cf)

            LLCF.isMinStageCf(this).shouldBeFalse()
        }

        if (isJava9Plus()) cf.minimalCompletionStage().apply {
            // CompletableFuture.minimalCompletionStage() return a NEW instance
            shouldNotBeSameInstanceAs(cf)

            LLCF.isMinStageCf(this as CompletableFuture<*>).shouldBeTrue()
        }
    }

    test("min stage").config(enabledIf = java9Plus) {
        val minStage = CompletableFutureUtils.completedStage(42) as CompletableFuture

        minStage.toCompletableFuture().apply {
            // minStage.toCompletableFuture() return a NEW instance
            shouldNotBeSameInstanceAs(minStage)

            // minStage.toCompletableFuture() return a new CompletableFuture for min stage
            LLCF.isMinStageCf(this).shouldBeFalse()
        }

        minStage.copy().apply {
            // minStage.copy() return a NEW instance
            shouldNotBeSameInstanceAs(minStage)

            LLCF.isMinStageCf(this).shouldBeTrue()
        }

        minStage.minimalCompletionStage().apply {
            // minStage.minimalCompletionStage() return a NEW instance
            shouldNotBeSameInstanceAs(minStage)

            LLCF.isMinStageCf(this as CompletableFuture<*>).shouldBeTrue()
        }
    }
})
