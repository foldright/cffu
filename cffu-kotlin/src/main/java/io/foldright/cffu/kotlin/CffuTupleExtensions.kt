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

operator fun <T1> Tuple2<T1, *>.component1(): T1 = _1
operator fun <T2> Tuple2<*, T2>.component2(): T2 = _2

operator fun <T1> Tuple3<T1, *, *>.component1(): T1 = _1
operator fun <T2> Tuple3<*, T2, *>.component2(): T2 = _2
operator fun <T3> Tuple3<*, *, T3>.component3(): T3 = _3

operator fun <T1> Tuple4<T1, *, *, *>.component1(): T1 = _1
operator fun <T2> Tuple4<*, T2, *, *>.component2(): T2 = _2
operator fun <T3> Tuple4<*, *, T3, *>.component3(): T3 = _3
operator fun <T4> Tuple4<*, *, *, T4>.component4(): T4 = _4

operator fun <T1> Tuple5<T1, *, *, *, *>.component1(): T1 = _1
operator fun <T2> Tuple5<*, T2, *, *, *>.component2(): T2 = _2
operator fun <T3> Tuple5<*, *, T3, *, *>.component3(): T3 = _3
operator fun <T4> Tuple5<*, *, *, T4, *>.component4(): T4 = _4
operator fun <T5> Tuple5<*, *, *, *, T5>.component5(): T5 = _5
