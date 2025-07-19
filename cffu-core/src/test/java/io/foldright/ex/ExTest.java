package io.foldright.ex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;


/**
 * @see ExceptionReportionSafetyTest
 */
public class ExTest {
    @Test
    void test_addSuppressed_not_allowed_add_self() {
        final IllegalArgumentException ex = assertThrowsExactly(IllegalArgumentException.class, () -> {
            RuntimeException exception = new RuntimeException();
            exception.addSuppressed(exception);
        });
        assertEquals("Self-suppression not permitted", ex.getMessage());
    }
}
