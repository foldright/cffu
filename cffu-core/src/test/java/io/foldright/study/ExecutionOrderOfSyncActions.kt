package io.foldright.study

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import java.util.concurrent.CompletableFuture

fun main() {
    println("Execution order of sync actions added to a completed future:")
    println("  Actions are executed immediately, in the order they are added.")

    val cf = CompletableFuture.completedFuture(42)
    cf.thenRun { println("Action 1") }
    cf.thenRun { println("Action 2") }
    cf.thenRun { println("Action 3") }

    println()
    println("Execution sync order of actions added to an incomplete future:")
    println("  Actions are executed in reverse order of addition,")
    println("  triggered by the completion stack when the future is completed.")

    val cf2 = CompletableFuture<Int>();
    cf2.thenRun { println("Action 1") }
    cf2.thenRun { println("Action 2") }
    cf2.thenRun { println("Action 3") }

    cf2.complete(42)

    Thread.sleep(1000)
}

class ExecutionOrderOfActionsAddedToCfTest : FunSpec({

    test("Execution order of sync actions added to a completed future") {
        val addedByActions = mutableListOf<String>()

        val cf = CompletableFuture.completedFuture(42)
        cf.thenRun { addedByActions.add("A1") }
        cf.thenRun { addedByActions.add("A2") }
        cf.thenRun { addedByActions.add("A3") }

        addedByActions.shouldContainExactly("A1", "A2", "A3")
    }

    test("Execution order of sync actions added to an incomplete future") {
        val addedByActions = mutableListOf<String>()

        val cf = CompletableFuture<Int>()
        cf.thenRun { addedByActions.add("A1") }
        cf.thenRun { addedByActions.add("A2") }
        cf.thenRun { addedByActions.add("A3") }
        cf.complete(42)

        addedByActions.shouldContainExactly("A3", "A2", "A1")
    }
})
