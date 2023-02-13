package io.foldright.cffu

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe
import org.apache.commons.lang3.tuple.Pair
import org.apache.commons.lang3.tuple.Triple
import java.util.concurrent.CompletableFuture

class CffuTest : AnnotationSpec() {
    @Test
    fun testAllOf() {
        val f1 = CompletableFuture.completedFuture(42)
        val f2 = CompletableFuture.completedFuture(43)
        val f3 = CompletableFuture.completedFuture(44)

        val result = Cffu.allOf(listOf(f1, f2, f3)).get()
        result shouldBe listOf(42, 43, 44)
    }


    @Test
    fun testAllOf_2_3() {
        Cffu.allOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("S42"),
        ).get() shouldBe Pair.of(42, "S42")

        Cffu.allOf(
            CompletableFuture.completedFuture(42),
            CompletableFuture.completedFuture("S42"),
            CompletableFuture.completedFuture(42.1),
        ).get() shouldBe Triple.of(42, "S42", 42.1)
    }
}
