package io.foldright.cffu.spi

import io.foldright.cffu.CffuFactory
import io.foldright.cffu.DefaultExecutorTestUtils.unwrapMadeExecutor
import io.foldright.test_utils.testThreadPoolExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class ExecutorWrapperProviderTest : FunSpec({
    test("disable TestExecutorWrapper") {
        val factory = CffuFactory.builder(testThreadPoolExecutor).build()
        val cffu = factory.runAsync {}
        unwrapMadeExecutor(cffu).shouldBeSameInstanceAs(testThreadPoolExecutor)
    }

    test("enable TestExecutorWrapper") {
        enableTestExecutorWrapper()

        val factory = CffuFactory.builder(testThreadPoolExecutor).build()
        val cffu = factory.runAsync {}
        unwrapMadeExecutor(cffu).shouldNotBeSameInstanceAs(testThreadPoolExecutor)
    }

    beforeTest {
        disableTestCffuExecutorWrapper()
    }

    afterTest {
        disableTestCffuExecutorWrapper()
    }
})
