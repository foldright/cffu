package io.foldright.cffu2

import io.foldright.test_utils.testCffuFac
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CffuFactoryBuilderTest : FunSpec({
    test("cffu (un)screened") {
        CffuFactoryBuilder.cffuScreened(null).shouldBeNull()
        CffuFactoryBuilder.cffuUnscreened(null).shouldBeNull()

        shouldThrowExactly<IllegalArgumentException> {
            CffuFactoryBuilder.cffuScreened(testCffuFac.defaultExecutor.screened)
        }.message shouldBe "input executor should never be a CffuMadeExecutor"
        shouldThrowExactly<IllegalArgumentException> {
            CffuFactoryBuilder.cffuUnscreened(testCffuFac.defaultExecutor.screened)
        }.message shouldBe "input executor should never be a CffuMadeExecutor"

        testCffuFac.defaultExecutor.screened.toString().shouldStartWith("CffuMadeExecutor, wrappee: ")
            .shouldContain(" original: ")

        shouldThrowExactly<NullPointerException> {
            testCffuFac.defaultExecutor.execute(null)
        }
    }
})
