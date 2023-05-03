package io.foldright.cffu.spi

import io.foldright.cffu.CffuFactoryBuilder
import io.foldright.test_utils.testThreadPoolExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class ExecutorWrapperTest : FunSpec({
    test("disable TestExecutorWrapper") {
        val factory = CffuFactoryBuilder.newCffuFactoryBuilder(testThreadPoolExecutor).build()
        val cffu = factory.runAsync {}
        cffu.defaultExecutor().shouldBeSameInstanceAs(testThreadPoolExecutor)
    }

    test("enable TestExecutorWrapper") {
        enableTestExecutorWrapper()

        val factory = CffuFactoryBuilder.newCffuFactoryBuilder(testThreadPoolExecutor).build()
        val cffu = factory.runAsync {}
        cffu.defaultExecutor().shouldNotBeSameInstanceAs(testThreadPoolExecutor)
    }

    beforeTest {
        disableTestCffuExecutorWrapper()
    }

    afterTest {
        disableTestCffuExecutorWrapper()
    }
})
