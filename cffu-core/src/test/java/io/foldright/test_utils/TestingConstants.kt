@file:JvmName("TestingConstants")

package io.foldright.test_utils


////////////////////////////////////////////////////////////////////////////////
// constants for testing
////////////////////////////////////////////////////////////////////////////////

const val n = 42
const val anotherN = 4242
const val nnn = 424242
const val s = "S42"
const val d = 42.5

@JvmField
val rte = RuntimeException("Bang")

@JvmField
val anotherRte = RuntimeException("AnotherBang")

const val LONG_WAIT_MS = 1000L
const val MEDIAN_WAIT_MS = 100L
const val SHORT_WAIT_MS = 10L
