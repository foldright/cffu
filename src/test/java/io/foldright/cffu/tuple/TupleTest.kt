package io.foldright.cffu.tuple

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

class TupleTest : FunSpec({
    val e1 = 1
    val e2 = "2"
    val e3 = 3.14
    val e4 = 4
    val e5 = '5'

    val value = "42"
    val pair2 = "foo" to "4242"

    test("tuple 2") {
        val tuple2 = Tuple2.of(e1, e2)
        tuple2._1 shouldBe e1
        tuple2._2 shouldBe e2
        tuple2 shouldBe Tuple2.of(e1, e2)
        tuple2 shouldNotBe Tuple2.of(e1, e2 + "X")
        mapOf(tuple2 to value, pair2)[tuple2] shouldBe value
        tuple2.toString() shouldBe "Tuple2(1, 2)"
    }

    test("tuple 3") {
        val tuple3 = Tuple3.of(e1, e2, e3)
        tuple3._1 shouldBe e1
        tuple3._2 shouldBe e2
        tuple3._3 shouldBe e3
        tuple3 shouldBe Tuple3.of(e1, e2, e3)
        tuple3 shouldNotBe Tuple3.of(e1, e2, e3 + 1.0)
        mapOf(tuple3 to value, pair2)[tuple3] shouldBe value
        tuple3.toString() shouldBe "Tuple3(1, 2, 3.14)"
    }

    test("tuple 4") {
        val tuple4 = Tuple4.of(e1, e2, e3, e4)
        tuple4._1 shouldBe e1
        tuple4._2 shouldBe e2
        tuple4._3 shouldBe e3
        tuple4._4 shouldBe e4
        tuple4 shouldBe Tuple4.of(e1, e2, e3, e4)
        tuple4 shouldNotBe Tuple4.of(e1, e2, e3, e4 + 1)
        mapOf(tuple4 to value, pair2)[tuple4] shouldBe value
        tuple4.toString() shouldBe "Tuple4(1, 2, 3.14, 4)"
    }

    test("tuple 5") {
        val tuple5 = Tuple5.of(e1, e2, e3, e4, e5)
        tuple5._1 shouldBe e1
        tuple5._2 shouldBe e2
        tuple5._3 shouldBe e3
        tuple5._4 shouldBe e4
        tuple5._5 shouldBe e5
        tuple5 shouldBe Tuple5.of(e1, e2, e3, e4, e5)
        tuple5 shouldNotBe Tuple5.of(e1, e2, e3, e4, '6')
        mapOf(tuple5 to value, pair2)[tuple5] shouldBe value
        tuple5.toString() shouldBe "Tuple5(1, 2, 3.14, 4, 5)"
    }
})
