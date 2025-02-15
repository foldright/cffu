package io.foldright.cffu.spi

import io.foldright.cffu.CffuFactory
import io.foldright.cffu.getOriginalExecutor
import io.foldright.test_utils.testExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs

class ExecutorWrapperProviderTest : FunSpec({
    test("disable TestExecutorWrapper") {
        val factory = CffuFactory.builder(testExecutor).build()
        val cffu = factory.runAsync {}
        cffu.getOriginalExecutor() shouldBeSameInstanceAs testExecutor
    }

    test("enable TestExecutorWrapper") {
        enableTestExecutorWrapper()

        val factory = CffuFactory.builder(testExecutor).build()
        val cffu = factory.runAsync {}
        // FIXME MORE test
        //       test the wrapped BEHAVIOR, not the wrapper instance
        cffu.getOriginalExecutor() shouldBeSameInstanceAs testExecutor
    }

    beforeTest {
        disableTestCffuExecutorWrapper()
    }

    afterTest {
        disableTestCffuExecutorWrapper()
    }
})
