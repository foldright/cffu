# Writing tests to raise coverage

Coverage is evidence that a reachable, observable behavior has an assertion.
A lit line is a side effect of that assertion. An uncovered line is not, by itself, a defect.

`must` / `must not` below are requirements.
`should` is a recommendation: a legal alternative exists, and the following text says what it costs.
See [wording.md](wording.md) for those strengths.

Read this document before adding a test for an uncovered line, or before deciding to leave a line uncovered.

## Requirements

### State why the line is uncovered, then choose

**Must** classify the line before writing a test:

- a caller can reach it on a supported JDK
- it is chosen once per process, during static initialization
- no caller can reach it (a defensive branch, a JDK-required empty method,
  or a branch reserved for a future enum constant)

**Must** add a behavior test for a line a caller can reach.

**Must** leave an unreachable line uncovered and record why, in a new note in `docs/`
or in an existing explanation such as [execution-helpers-raw-result.md](execution-helpers-raw-result.md).

Reason: a test that only forces the line to run does not protect a behavior.
The report can stay below 100%. That is an acceptable result.

A test that constructs a `ForkJoinTask` and calls `getRawResult` turns the line green.
It still passes if `completeAsync` stops completing the future.
The behavior worth holding a test for is already the future's outcome.
The tradeoff is a red line on the coverage report against a test that cannot fail for a real break.

### Assert a result the caller can see

**Must** assert a result a caller can observe: a return value, completion, a later task running,
how many times an executor ran, an exception type, or the interrupt flag.

**Must** build that situation through a public method, a public subclass hook such as `join()`,
or a `Future` the test itself implements.

Reason: the test then locks the contract.
Renaming a field or splitting a private method keeps the test green when the behavior stays the same.

Example: caller-thread reuse in
[`ConcurrencyLimitExecutor`](../cffu-core/src/main/java/io/foldright/cffu2/ConcurrencyLimitExecutor.java).
`execute` treats the run as synchronous only when the task runs on the caller thread and `execute` has not returned yet.
A later reuse of that same thread is asynchronous and drains the queue.
A single-thread pool calls `execute` twice and returns; that same thread later runs the worker.
Both tasks finishing is the assertion. A synchronous run would leave the second task queued.

```kotlin
val pool = Executors.newSingleThreadExecutor()
try {
    val limited = ConcurrencyLimitExecutor(1, pool)
    val ran = CopyOnWriteArrayList<String>()
    val done = CountDownLatch(2)
    pool.submit {
        limited.execute { ran.add("first"); done.countDown() }
        limited.execute { ran.add("second"); done.countDown() }
    }.get(5, TimeUnit.SECONDS)
    done.await(5, TimeUnit.SECONDS) shouldBe true
    ran shouldBe listOf("first", "second")
} finally {
    pool.shutdownNow()
}
```

Example: [`LLCF.relayAsync0`](../cffu-core/src/main/java/io/foldright/cffu2/LLCF.java)
completes directly when the input completes on the caller thread after `peek0` has returned.
Count executor hops. After `relayAsync0` returns, complete the input on the caller thread
and assert the hop count is 0 and the relayed stage has the computed value.

Example: `getSuccessNow` after a normal-completion check.
Subclass `CompletableFuture`, `complete` it, then override `join()` so it throws.
`isDone` is true and `isCompletedExceptionally` is false, and `join()` still throws.
That is the `obtrudeException` race as a caller can see it. Assert the fallback value.

Example: `resultNow` / `exceptionNow` / `state` on Java 18 and earlier.
Implement `Future` directly, not as a `CompletableFuture`.
Make `get()` throw `InterruptedException` once, then return the real outcome.
Assert the retry result and that the interrupt flag was restored.
A `CancellationException` from `get()` is cancelled.
Clear the interrupt flag before the test returns, so later tests do not inherit it.
On Java 19 and newer the same test runs the JDK default methods.

### Give a once-per-process arm its own process

**Must** cover each arm of a `static final` choice in a JVM that was started with that choice already latched.

[`LLCF.ASYNC_POOL`](../cffu-core/src/main/java/io/foldright/cffu2/LLCF.java) is the case.
Java 9 and newer call `CompletableFuture.defaultExecutor()` and do not evaluate the inner ternary.
On Java 8 the field is:

```java
USE_COMMON_POOL ? ForkJoinPool.commonPool() : new ThreadPerTaskExecutor()
```

`USE_COMMON_POOL` and `ASYNC_POOL` are both `static final`.
`ForkJoinPool` reads `java.util.concurrent.ForkJoinPool.common.parallelism` in its own static initializer,
which runs before any test can call `System.setProperty`.
One JVM therefore executes exactly one arm.

[`scripts/integration_test`](../scripts/integration_test) already runs Java 8 twice:

- `parallelism=1` takes `ThreadPerTaskExecutor`
- a second JVM with `parallelism=2` takes `ForkJoinPool.commonPool()`

Do not merely omit `-D`: the default parallelism is `availableProcessors() - 1`, so the arm follows the machine.
A 2-core machine takes `ThreadPerTaskExecutor`; a larger one takes `ForkJoinPool.commonPool()`.
Only the explicit values pin both arms.

The test on Java 8 asserts the arm this JVM was started with:

```kotlin
if (!LLCF.IS_JAVA9_PLUS) {
    if (ForkJoinPool.getCommonPoolParallelism() > 1) {
        LLCF.ASYNC_POOL shouldBeSameInstanceAs ForkJoinPool.commonPool()
    } else {
        LLCF.ASYNC_POOL.javaClass.name shouldEndWith "\$ThreadPerTaskExecutor"
    }
}
```

In CI, `jacoco:prepare-agent` uses `append=true`, so the second JVM's data merges into `jacoco.exec`.
A later JDK's `jacoco:report` is what Codecov uploads.

Reason: the branch really is chosen once per process. A second process matches that fact.
Rewriting the `static final` from a test does not.

### Wait until the asserted result has happened

**Must** assert a state that the awaited event has already produced, and **must** bound the wait with a timeout.

Reason: a latch can open while a worker is still polling an empty queue or still releasing a slot.
Reading "worker count is 0" at that moment flakes.
"Both tasks completed" or "the next task ran" is the result the test means.

## Prohibitions

### Must not light a line by reflection or by an internal call no caller makes

This includes invoking a private method and breaking an invariant so that a defensive branch runs.

Reason: the test then proves that the test can damage internal state.
It fails when a private name changes and the behavior does not.
It stays green when the public behavior breaks, as long as the forced line still runs.

These lines stay uncovered for that reason:

- `ConcurrencyLimitExecutor.reportExceedWorkerCount` when `workerCount > maxConcurrency`.
  Increments of `workerCount` happen under the same lock,
  and the method returns immediately when `workerCount <= maxConcurrency`.
  The log runs only after that invariant has already failed.
- `CfCompleterBySupplier` / `CfExCompleterBySupplier` `getRawResult` and `setRawResult`,
  and a second `run()` after `dep` or `fn` is already null.
  The full reason for the raw-result methods is [execution-helpers-raw-result.md](execution-helpers-raw-result.md).
  `completeAsync` (Java 8) and `completeExceptionallyAsync` submit the task once as a `Runnable`.
  `ForkJoinPool` calls `exec()`, which calls `run()` once.
  Success, a supplier exception, and an already completed future stay covered by `CompletableFutureUtilsTest`.
- `CffuState.toCffuState`'s final `throw`. `Future.State` has the four constants the `switch` handles.
  The `throw` is for an enum constant a future JDK might add. `toCffuState(null)` is reachable
  and is a normal null check: on Java 19 and newer it must throw `NullPointerException`.

A different reflection call does not fix this kind of test. A public behavior that distinguishes the branch replaces it.
No such behavior means delete the test and record the reason.

### Must not change a latched static from inside the test

**Must not** call `System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", ...)`
in a test and expect `ASYNC_POOL` to change. The property is read while `ForkJoinPool` is initializing.

The assertion reads `ForkJoinPool.getCommonPoolParallelism()` and compares the executor this JVM already chose.

### Must not record the CI procedure in source comments

Production and test comments state the version branch: Java 9 and newer use `defaultExecutor()`;
the inner ternary is Java 8 only; this JVM asserts the arm it was started with. How the second JVM is launched,
and how JaCoCo merges the two runs, stays in [`scripts/integration_test`](../scripts/integration_test).

Reason: a reader of `LLCF` needs the branch. The launch command changes.
Putting it in a source comment welds a CI detail into the implementation note.

## Recommendations

### Should use the smallest public setup that separates the two arms

A second task that runs only when a slot was released distinguishes "the worker released its slot"
from "the worker is still holding it". `maxConcurrency = 1`, a task whose `toString()` throws `Error` while the failure
is logged, then one more task. The second task running is the assertion. The `Error` identity is the other assertion.

A direct executor, or a pool that runs the task inside `execute`,
does not show caller-thread reuse after `execute` returns.
The single-thread pool above does, because `execute` returns before that thread is free to run the worker.

### Should treat the resulting coverage drop as expected

Deleting a reflection test lowers the percentage. The lines that fall are the ones with no caller.
The observable outcomes remain under the black-box tests.
That drop is the goal of the deletion, not a regression to restore.

## Practice

1. Name the uncovered line and which of the three classes above it belongs to.
2. For a reachable line, add the assertion a caller can see. Run `./mvnw test -pl cffu-core -Dtest=...`.
3. For an unreachable line, add no test. Point at the existing note, or write a new note in `docs/`.
4. For a once-per-process arm, extend the JVM that [`scripts/integration_test`](../scripts/integration_test)
   already starts with the property set on the command line.
   Assert the arm inside the test. Do not set the property after the JVM is up.
5. Re-read the comment. It names the Java version branch. The script names the extra JVM.
