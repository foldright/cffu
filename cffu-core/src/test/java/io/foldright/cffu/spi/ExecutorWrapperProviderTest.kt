package io.foldright.cffu.spi

import io.foldright.cffu.CffuFactory
import io.foldright.cffu.getOriginalExecutor
import io.foldright.cffu.getScreenedExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldNotBeSameInstanceAs
import java.util.concurrent.Executor


class ExecutorWrapperProviderTest : FunSpec({

    val name = "Test executor of ExecutorWrapperProviderTest"
    val executor = object : Executor {
        override fun execute(command: Runnable) = command.run()
        override fun toString(): String = name
    }

    test("disable TestExecutorWrapper") {
        val count = TestExecutorWrapperProvider.executionCounter.get()

        val factory = CffuFactory.builder(executor).build()
        val cffu = factory.runAsync {}
        cffu.defaultExecutor() shouldBeSameInstanceAs executor
        cffu.getOriginalExecutor() shouldBeSameInstanceAs executor
        cffu.getScreenedExecutor() shouldNotBeSameInstanceAs executor

        cffu.join()
        TestExecutorWrapperProvider.executionCounter.get() shouldBe count

        cffu.getScreenedExecutor().execute {}
        TestExecutorWrapperProvider.executionCounter.get() shouldBe count
    }

    test("enable TestExecutorWrapper") {
        enableTestExecutorWrapper()

        val count = TestExecutorWrapperProvider.executionCounter.get()

        val factory = CffuFactory.builder(executor).build()
        val cffu = factory.runAsync {
            println("Starting executor")
        }
        cffu.defaultExecutor() shouldBeSameInstanceAs executor
        cffu.getOriginalExecutor() shouldBeSameInstanceAs executor
        cffu.getScreenedExecutor() shouldNotBeSameInstanceAs executor


        cffu.join()
        TestExecutorWrapperProvider.executionCounter.get().shouldBe(count + 1)

        cffu.getScreenedExecutor().execute {}
        TestExecutorWrapperProvider.executionCounter.get().shouldBe(count + 2)
    }

    beforeTest {
        disableTestCffuExecutorWrapper()
    }

    afterTest {
        disableTestCffuExecutorWrapper()
    }
})
