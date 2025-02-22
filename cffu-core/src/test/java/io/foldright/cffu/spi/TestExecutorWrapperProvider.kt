package io.foldright.cffu.spi

import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicLong


class TestExecutorWrapperProvider : ExecutorWrapperProvider {
    override fun wrap(executor: Executor): Executor =
        if (isTestExecutorWrapperEnabled()) Executor { command ->
            executionCounter.incrementAndGet()
            executor.execute(command)
        } else executor

    companion object {
        val executionCounter = AtomicLong(0)
    }
}

private const val PROPERTY_NAME = "cffu.test.executor.wrapper"

fun enableTestExecutorWrapper() {
    System.setProperty(PROPERTY_NAME, "true")
}

fun disableTestCffuExecutorWrapper() {
    System.clearProperty(PROPERTY_NAME)
}

private fun isTestExecutorWrapperEnabled(): Boolean = "true" == System.getProperty(PROPERTY_NAME)
