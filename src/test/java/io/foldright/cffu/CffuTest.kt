package io.foldright.cffu

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple
import java.util.concurrent.CompletableFuture

class CffuTest : AnnotationSpec() {
    @Test
    fun test_resultAllOf() {
        val f1 = CompletableFuture.completedFuture(42)
        val f2 = CompletableFuture.completedFuture(43)
        val f3 = CompletableFuture.completedFuture(44)

        Cffu.resultAllOf(f1, f2, f3).get() shouldBe listOf(42, 43, 44)
        Cffu.resultAllOf(listOf(f1, f2, f3)).get() shouldBe listOf(42, 43, 44)
    }


    @Test
    fun test_resultOf_2_3() {
        val n = 42
        val s = "S42"
        val d = 42.1

        Cffu.resultOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(s),
        ).get() shouldBe Pair.of(n, s)

        Cffu.resultOf(
            CompletableFuture.completedFuture(n),
            CompletableFuture.completedFuture(s),
            CompletableFuture.completedFuture(d),
        ).get() shouldBe Triple.of(n, s, d)
    }

    @Test
    fun test_anyOf() {
        val gen: (Int) -> CompletableFuture<Int> = { CompletableFuture.supplyAsync { Thread.sleep(10); it } }
        val f = CompletableFuture.completedFuture(42)

        Cffu.anyOf(gen(44), gen(43), f).get() shouldBe 42
        Cffu.anyOf(listOf(gen(44), f, gen(43))).get() shouldBe 42
    }
}
