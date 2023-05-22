package io.foldright.cffu;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledForJreRange;
import org.junit.jupiter.api.condition.JRE;

import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CffuStateTest {
    @Test
    @EnabledForJreRange(min = JRE.JAVA_19)
    void toCffuState() {
        assertEquals(CffuState.RUNNING, CffuState.toCffuState(Future.State.RUNNING));
        assertEquals(CffuState.SUCCESS, CffuState.toCffuState(Future.State.SUCCESS));
        assertEquals(CffuState.FAILED, CffuState.toCffuState(Future.State.FAILED));
        assertEquals(CffuState.CANCELLED, CffuState.toCffuState(Future.State.CANCELLED));
    }

    @Test
    @EnabledForJreRange(min = JRE.JAVA_19)
    void toFutureState() {
        assertEquals(Future.State.RUNNING, CffuState.RUNNING.toFutureState());
        assertEquals(Future.State.SUCCESS, CffuState.SUCCESS.toFutureState());
        assertEquals(Future.State.FAILED, CffuState.FAILED.toFutureState());
        assertEquals(Future.State.CANCELLED, CffuState.CANCELLED.toFutureState());
    }
}
