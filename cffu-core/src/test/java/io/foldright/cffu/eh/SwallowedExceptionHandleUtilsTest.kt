package io.foldright.cffu.eh

import io.foldright.cffu.CompletableFutureUtils.failedFuture
import io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleAllSwallowedExceptions
import io.foldright.cffu.eh.SwallowedExceptionHandleUtils.handleSwallowedExceptions
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import java.util.concurrent.CompletableFuture.completedFuture
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class SwallowedExceptionHandleUtilsTest : FunSpec({

    test("handleAllSwallowedExceptions") {
        val eiList = CopyOnWriteArrayList<ExceptionInfo>()
        val eh = ExceptionHandler { eiList.add(it) }

        shouldThrowExactly<NullPointerException> {
            handleAllSwallowedExceptions(null, eh, null, null)
        }.message shouldBe "where is null"
        shouldThrowExactly<NullPointerException> {
            handleAllSwallowedExceptions("shouldThrowExactly", arrayOf(), null)
        }.message shouldBe "exceptionHandler is null"
        shouldThrowExactly<NullPointerException> {
            handleAllSwallowedExceptions("shouldThrowExactly", null, eh, null)
        }.message shouldBe "input1 is null"
        shouldThrowExactly<NullPointerException> {
            handleAllSwallowedExceptions("shouldThrowExactly", null, eh, completedFuture(42), null)
        }.message shouldBe "input2 is null"

        val where = "test handleAllSwallowedExceptions"
        handleAllSwallowedExceptions(where, eh)
        eiList shouldHaveSize 0
        handleAllSwallowedExceptions(where, eh, completedFuture(0))
        eiList shouldHaveSize 0

        val rte = RuntimeException("Bang...")
        handleAllSwallowedExceptions(where, eh, failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 0
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()

        eiList.clear()
        handleAllSwallowedExceptions(where, eh, completedFuture(42), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()

        eiList.clear()
        handleAllSwallowedExceptions(where, arrayOf("a0"), eh, completedFuture(42), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()

        eiList.clear()
        handleAllSwallowedExceptions(where, arrayOf("a0", "a1", "a2"), eh, completedFuture(42), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment shouldBe "a1"

        eiList.clear()
        handleAllSwallowedExceptions(
            where, arrayOf("a0", "a1", "a2", "a3"), eh,
            failedFuture<Int>(rte), completedFuture(42), failedFuture<Int>(rte)
        )
        eiList shouldHaveSize 2
        eiList[0].where shouldBe where
        eiList[0].index shouldBe 0
        eiList[0].exception shouldBe rte
        eiList[0].attachment shouldBe "a0"
        eiList[1].where shouldBe where
        eiList[1].index shouldBe 2
        eiList[1].exception shouldBe rte
        eiList[1].attachment shouldBe "a2"
    }

    test("handleSwallowedExceptions") {
        val eiList = CopyOnWriteArrayList<ExceptionInfo>()
        val eh = ExceptionHandler { eiList.add(it) }

        shouldThrowExactly<NullPointerException> {
            handleSwallowedExceptions(null, eh, null, null)
        }.message shouldBe "where is null"
        shouldThrowExactly<NullPointerException> {
            handleSwallowedExceptions("shouldThrowExactly", arrayOf(), null, null)
        }.message shouldBe "exceptionHandler is null"
        shouldThrowExactly<NullPointerException> {
            handleSwallowedExceptions("shouldThrowExactly", null, eh, null)
        }.message shouldBe "output is null"
        shouldThrowExactly<NullPointerException> {
            handleSwallowedExceptions("shouldThrowExactly", null, eh, completedFuture(42), null)
        }.message shouldBe "input1 is null"
        shouldThrowExactly<NullPointerException> {
            handleSwallowedExceptions("shouldThrowExactly", null, eh, completedFuture(42), completedFuture(43), null)
        }.message shouldBe "input2 is null"

        val where = "test handleSwallowedExceptions"
        handleSwallowedExceptions(where, eh, completedFuture(42))
        eiList shouldHaveSize 0
        handleSwallowedExceptions(where, eh, completedFuture(42), completedFuture(0))
        eiList shouldHaveSize 0


        val rte = RuntimeException("Bang!")
        handleSwallowedExceptions(where, eh, completedFuture(42), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 0
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()

        eiList.clear()
        handleSwallowedExceptions(where, eh, completedFuture(42), completedFuture(1), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()

        eiList.clear()
        handleSwallowedExceptions(
            where, arrayOf("a0"), eh,
            completedFuture(42),
            completedFuture(42), failedFuture<Int>(rte)
        )
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()

        eiList.clear()
        handleSwallowedExceptions(
            where, arrayOf("a0", "a1", "a2"), eh,
            completedFuture(42),
            completedFuture(42), failedFuture<Int>(rte)
        )
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment shouldBe "a1"

        eiList.clear()
        handleSwallowedExceptions(
            where, arrayOf("a0", "a1", "a2", "a3"), eh,
            completedFuture(42),
            failedFuture<Int>(rte), completedFuture(42), failedFuture<Int>(rte)
        )
        eiList shouldHaveSize 2
        eiList[0].where shouldBe where
        eiList[0].index shouldBe 0
        eiList[0].exception shouldBe rte
        eiList[0].attachment shouldBe "a0"
        eiList[1].where shouldBe where
        eiList[1].index shouldBe 2
        eiList[1].exception shouldBe rte
        eiList[1].attachment shouldBe "a2"
    }

    test("handleSwallowedExceptions, wont report exception of output") {
        val eiList = CopyOnWriteArrayList<ExceptionInfo>()
        val eh = ExceptionHandler {
            if (it.index == 0) throw RuntimeException("intend exception in test cases")
            eiList.add(it)
        }

        val rte = RuntimeException("Bang...")
        val outRte = RuntimeException("exception of output")
        val where = "exceptionHandler"
        handleSwallowedExceptions(where, eh, failedFuture<Int>(outRte), failedFuture<Int>(outRte), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()
    }

    test("exception handler throws uncaught exception") {
        val eiList = CopyOnWriteArrayList<ExceptionInfo>()
        val eh = ExceptionHandler {
            if (it.index == 0) throw RuntimeException("intend exception in test cases")
            eiList.add(it)
        }

        val rte = RuntimeException("Bang...")
        val where = "exceptionHandler"
        handleAllSwallowedExceptions(where, eh, failedFuture<Int>(rte), failedFuture<Int>(rte))
        eiList shouldHaveSize 1
        eiList.first().where shouldBe where
        eiList.first().index shouldBe 1
        eiList.first().exception shouldBe rte
        eiList.first().attachment.shouldBeNull()
    }
})
