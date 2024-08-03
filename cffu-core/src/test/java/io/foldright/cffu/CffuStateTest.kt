package io.foldright.cffu

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledForJreRange
import org.junit.jupiter.api.condition.JRE.JAVA_19
import java.util.concurrent.Future

@EnabledForJreRange(min = JAVA_19)
private class CffuStateTest {
    @Test
    fun toCffuState() {
        CffuState.toCffuState(Future.State.RUNNING) shouldBe CffuState.RUNNING
        CffuState.toCffuState(Future.State.SUCCESS) shouldBe CffuState.SUCCESS
        CffuState.toCffuState(Future.State.FAILED) shouldBe CffuState.FAILED
        CffuState.toCffuState(Future.State.CANCELLED) shouldBe CffuState.CANCELLED
    }

    @Test
    fun toFutureState() {
        CffuState.RUNNING.toFutureState() shouldBe Future.State.RUNNING
        CffuState.SUCCESS.toFutureState() shouldBe Future.State.SUCCESS
        CffuState.FAILED.toFutureState() shouldBe Future.State.FAILED
        CffuState.CANCELLED.toFutureState() shouldBe Future.State.CANCELLED
    }
}
