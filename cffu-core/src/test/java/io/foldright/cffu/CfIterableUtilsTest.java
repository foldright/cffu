package io.foldright.cffu;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class CfIterableUtilsTest {

    @Test
    void allResultsOf() {
        // Test with an empty iterable
        assertTrue(CfIterableUtils.allResultsOf(new ArrayList<>()).join().isEmpty());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals(List.of("result1"), CfIterableUtils.allResultsOf(List.of(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertEquals(List.of("result1", "result2", "result3"),
                CfIterableUtils.allResultsOf(List.of(future1, future2, future3)).join());
    }

    @Test
    void allResultsFailFastOf() {
        // Test with an empty iterable
        assertTrue(CfIterableUtils.allResultsFailFastOf(new ArrayList<>()).join().isEmpty());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals(List.of("result1"), CfIterableUtils.allResultsFailFastOf(List.of(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertEquals(List.of("result1", "result2", "result3"),
                CfIterableUtils.allResultsFailFastOf(List.of(future1, future2, future3)).join());
    }

    @Test
    void allSuccessResultsOf() {
        // Test with an empty iterable
        assertTrue(CfIterableUtils.allSuccessResultsOf(null, new ArrayList<>()).join().isEmpty());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals(List.of("result1"), CfIterableUtils.allSuccessResultsOf(null, List.of(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertEquals(List.of("result1", "result2", "result3"),
                CfIterableUtils.allSuccessResultsOf(null, List.of(future1, future2, future3)).join());
    }

    @Test
    void mostSuccessResultsOf() {
        // Test with an empty iterable
        // assertTrue(CfIterableUtils.mostSuccessResultsOf(null, null, 0, null, new ArrayList<>()).join().isEmpty());

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals(List.of("result1"), CfIterableUtils.mostSuccessResultsOf(Runnable::run, null, 1, TimeUnit.MILLISECONDS, List.of(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertEquals(List.of("result1", "result2", "result3"),
                CfIterableUtils.mostSuccessResultsOf(Runnable::run, null, 1, TimeUnit.MILLISECONDS, List.of(future1, future2, future3)).join());
    }

    @Test
    void anyOf() {
        // Test with an empty iterable
        // assertThrows(IllegalArgumentException.class, () -> CfIterableUtils.anyOf(new ArrayList<>()));

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", CfIterableUtils.anyOf(List.of(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertTrue(List.of("result1", "result2", "result3").contains(CfIterableUtils.anyOf(List.of(future1, future2, future3)).join()));
    }

    @Test
    void anySuccessOf() {
        // Test with an empty iterable
        // assertThrows(IllegalArgumentException.class, () -> CfIterableUtils.anySuccessOf(new ArrayList<>()));

        // Test with a single completed future
        CompletableFuture<String> future1 = CompletableFuture.completedFuture("result1");
        assertEquals("result1", CfIterableUtils.anySuccessOf(List.of(future1)).join());

        // Test with multiple completed futures
        CompletableFuture<String> future2 = CompletableFuture.completedFuture("result2");
        CompletableFuture<String> future3 = CompletableFuture.completedFuture("result3");
        assertTrue(List.of("result1", "result2", "result3").contains(CfIterableUtils.anySuccessOf(List.of(future1, future2, future3)).join()));
    }
}