package io.foldright.cffu.kotlin

import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5


////////////////////////////////////////////////////////////////////////////////
// Destructuring declarations for cffu tuples
//
// https://kotlinlang.org/docs/destructuring-declarations.html
////////////////////////////////////////////////////////////////////////////////

operator fun <T1, T2> Tuple2<T1, T2>.component1(): T1 = _1
operator fun <T1, T2> Tuple2<T1, T2>.component2(): T2 = _2

operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component1(): T1 = _1
operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component2(): T2 = _2
operator fun <T1, T2, T3> Tuple3<T1, T2, T3>.component3(): T3 = _3

operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component1(): T1 = _1
operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component2(): T2 = _2
operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component3(): T3 = _3
operator fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.component4(): T4 = _4

operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component1(): T1 = _1
operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component2(): T2 = _2
operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component3(): T3 = _3
operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component4(): T4 = _4
operator fun <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5>.component5(): T5 = _5
