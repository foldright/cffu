package io.foldright.cffu.spi

import io.foldright.cffu.*
import io.foldright.test_utils.testExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class ExecutorWrapperProviderTest : FunSpec({
    test("disable TestExecutorWrapper") {
        val factory = CffuFactory.builder(testExecutor).build()
        val cffu = factory.runAsync {}
        cffu.defaultExecutor() shouldBeSameInstanceAs testExecutor
        cffu.getOriginalExecutor() shouldBeSameInstanceAs testExecutor
        cffu.getScreenedExecutor() shouldBeSameInstanceAs testExecutor
        cffu.getUnscreenedExecutor() shouldBeSameInstanceAs testExecutor
    }

    test("enable TestExecutorWrapper") {
        enableTestExecutorWrapper()

        val factory = CffuFactory.builder(testExecutor).build()
        val cffu = factory.runAsync {}
        cffu.defaultExecutor() shouldBeSameInstanceAs testExecutor
        cffu.getOriginalExecutor() shouldBeSameInstanceAs testExecutor
        cffu.getScreenedExecutor() shouldNotBeSameInstanceAs testExecutor
        cffu.getUnscreenedExecutor() shouldNotBeSameInstanceAs testExecutor
    }

    beforeTest {
        disableTestCffuExecutorWrapper()
    }

    afterTest {
        disableTestCffuExecutorWrapper()
    }
})
