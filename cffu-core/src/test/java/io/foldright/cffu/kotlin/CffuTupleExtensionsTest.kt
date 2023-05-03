package io.foldright.cffu.kotlin

import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CffuTupleExtensionsTest : FunSpec({
    test("destructuring declarations, Tuple2") {
        val (i, s) = Tuple2.of(1, "2")
        i shouldBe 1
        s shouldBe "2"
    }

    test("destructuring declarations, Tuple3") {
        val (i, s, d) = Tuple3.of(1, "2", 3.0)
        i shouldBe 1
        s shouldBe "2"
        d shouldBe 3.0
    }

    test("destructuring declarations, Tuple4") {
        val (i, s, d, i4) = Tuple4.of(1, "2", 3.0, 4)
        i shouldBe 1
        s shouldBe "2"
        d shouldBe 3.0
        i4 shouldBe 4
    }

    test("destructuring declarations, Tuple5") {
        val (i, s, d, i4, s5) = Tuple5.of(1, "2", 3.0, 4, "5")
        i shouldBe 1
        s shouldBe "2"
        d shouldBe 3.0
        i4 shouldBe 4
        s5 shouldBe "5"
    }
})
