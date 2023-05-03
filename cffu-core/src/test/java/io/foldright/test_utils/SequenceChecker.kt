package io.foldright.test_utils

import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

class SequenceChecker(private val logSequenceInfo: Boolean = false) {
    private val sequencer = AtomicInteger()

    @JvmOverloads
    fun assertSeq(message: String, seqShouldBe: Int = -1, forceLog: Boolean = false) {
        val s = sequencer.getAndIncrement()
        if (forceLog || logSequenceInfo) p(s, message)

        if (seqShouldBe > 0) s shouldBe seqShouldBe
    }

    @JvmOverloads
    @Suppress("unused")
    fun logSeq(message: String, forceLog: Boolean = false, increaseSeq: Boolean = false) {
        val s = if (increaseSeq) sequencer.getAndIncrement() else sequencer.get()

        if (forceLog || logSequenceInfo) p(s, message)
    }

    private fun p(s: Int, message: String) {
        println(String.format("[%3d] [%-20s]: %s", s, Thread.currentThread().name, message))
    }
}
