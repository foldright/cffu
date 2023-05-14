package io.foldright.cffu.spi

import java.util.concurrent.Executor


class TestExecutorWrapperProvider : ExecutorWrapperProvider {
    override fun wrap(executor: Executor): Executor =
        if (isTestExecutorWrapperEnabled()) object : Executor by executor {}
        else executor
}

private const val PROPERTY_NAME = "cffu.test.executor.wrapper"

fun enableTestExecutorWrapper() {
    System.setProperty(PROPERTY_NAME, "true")
}

fun disableTestCffuExecutorWrapper() {
    System.clearProperty(PROPERTY_NAME)
}

private fun isTestExecutorWrapperEnabled(): Boolean = "true" == System.getProperty(PROPERTY_NAME)
