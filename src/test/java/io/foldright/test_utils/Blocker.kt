package io.foldright.test_utils

import java.util.concurrent.CountDownLatch

class Blocker : AutoCloseable {
    private val latch = CountDownLatch(1)

    fun block(): Unit = latch.await()

    @Suppress("MemberVisibilityCanBePrivate")
    fun unblock(): Unit = latch.countDown()

    override fun close() = unblock()
}
