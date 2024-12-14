package io.foldright.cffu

import io.foldright.test_utils.n
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.future.shouldBeCompleted
import io.kotest.matchers.future.shouldCompleteExceptionallyWith
import io.kotest.matchers.ints.shouldBeExactly
import java.util.concurrent.CompletableFuture

class LLCFTest : FunSpec({
    test("completeCf0 - success") {
        val cf = CompletableFuture<Int>()
        LLCF.completeCf0(cf, n, null).shouldBeTrue()
        cf.shouldBeCompleted()
        cf.get() shouldBeExactly n

        LLCF.completeCf0(cf, n, null).shouldBeFalse()
    }

    test("completeCf0 - exceptionally") {
        val cf = CompletableFuture<Int>()
        val ex = RuntimeException("foo")
        LLCF.completeCf0(cf, n, ex).shouldBeTrue()
        cf.shouldCompleteExceptionallyWith(ex)

        LLCF.completeCf0(cf, n, ex).shouldBeFalse()
    }
})
