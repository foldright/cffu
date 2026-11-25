# `@CheckReturnValue`

Rules for `edu.umd.cs.findbugs.annotations.CheckReturnValue` on public methods that return a stage.
`must` / `must not` below are requirements.
`should` is a recommendation: a legal alternative exists, and the following text says what it costs.
See [wording.md](wording.md) for those strengths.

Read this document before adding, removing, or rewriting `@CheckReturnValue`.

## Two checkers, two annotations

`@Contract(pure = true)` and `@CheckReturnValue` are not substitutes.

| Checker | What it reads | Warning when the return is ignored |
| --- | --- | --- |
| IntelliJ `IgnoreResultOfCallInspection` | `@Contract(pure = true)`, via `JavaMethodContractUtil.isPure` | `Result of method() is ignored` |
| SpotBugs 4.10.5 (`RV_RETURN_VALUE_IGNORED`) | `@CheckReturnValue` (`edu.umd.cs.findbugs.annotations`, `javax.annotation`, or the simple name) and `CanIgnoreReturnValue` | `Return value of … ignored` |

SpotBugs uses JetBrains `@Contract` for nullness analysis. It does not treat `pure = true` as a return-value check.
A method that has only `@Contract(pure = true)` is warned about in IntelliJ and is silent in `./mvnw` SpotBugs.

**Must** keep `@Contract(pure = true)` where it already describes a method with no visible side effect.
**Must** also add `@CheckReturnValue` when an ignored return must fail CI.

`explanation` is the reason the return is kept. The annotation is `@Documented`, so the sentence is part of the method and of generated javadoc.
Neither IntelliJ's inspection nor SpotBugs 4.10.5 copies `explanation` into the warning text. Write it anyway.

## Where the annotation goes

**Must** put `@CheckReturnValue` on a method that returns a new stage, when completion or the result is visible only through that return.
Ignoring the return drops that completion, and drops any exception the stage would have reported.

**Must not** put `@CheckReturnValue` on a `*AndForget` method.

- `mRunAsyncAndForget` and `parAcceptAsyncAndForget` return `void`. There is no return to check.
- `thenMAcceptAsyncAndForget`, `thenMRunAsyncAndForget`, and `thenParAcceptAsyncAndForget` return the original stage so the caller can keep chaining.
  The new work is unobserved on purpose. Annotating them warns on that call.

The siblings of those methods (`mSupply*` / `mRun*` other than AndForget, `thenM*`, `par*` / `thenPar*`, and the tuple `mSupplyTuple*` / `thenMApplyTuple*` forms, on both `*Utils` and `Cffu`) **must** have the annotation.
The explanation names the AndForget method:

```text
should use the returned <Type>; otherwise, use method `<AndForget>`
```

`should` is the right strength. Dropping the return is legal Java, and a named method is the alternative.

Methods that already say `prefer method` (`supplyAsync`, `thenApply`, `thenCombine`, and the same single-stage methods) are a separate group.
They point at another stage method, not at `*AndForget`. Leave those explanations.

## `*Of` methods

`*Of` methods compose stages that are already running. They have no `*AndForget` sibling.
They already have `@Contract(pure = true)`. **Must** add `@CheckReturnValue` so SpotBugs sees an ignored return.

Two explanations. Pick from the return type, not from the method name alone.

`allOf` and `allFailFastOf` return `CompletableFuture<Void>` or `Cffu<Void>`.
The return carries no business result. The call exists so the caller can wait.

```text
should use the returned <Type>; forget to call its `join()` method?
```

Every other `*Of` returns the aggregated result: `List`, `Tuple2`..`Tuple5`, or `T`, wrapped as `CompletableFuture`, `Cffu`, or `MCffu`.
That returned stage is the only handle to the result.

```text
should use the returned <Type>; otherwise, the result data is discarded
```

**Must not** reuse the `join()` sentence on a result-bearing `*Of`.
`join()` is how a caller waits on a `Void` stage. A stage that carries a result may be chained (`thenApply`, returned to the caller) without blocking.
The `join()` sentence tells the caller to block.

`otherwise, use method …` does not fit `*Of`. There is no other method to call. `otherwise` states the consequence: the result data is discarded.

## Stage factories and rewraps

These public methods return a new stage, or a new handle around a stage the caller already has.
They have `@Contract(pure = true)`. **Must** also have `@CheckReturnValue`.
SpotBugs does not warn when the caller drops the return of a `pure` method.

| Class | Methods |
| --- | --- |
| `CompletableFutureUtils` | `failedFuture`, `completedStage`, `failedStage`, `newIncompleteFuture`, `copy`, `minimalCompletionStage` |
| `CffuFactory` | `completedFuture`, `failedFuture`, `completedMCffu`, `failedMCffu`, `completedStage`, `failedStage`, `toCffu`, `toMCffu`, `newIncompleteCffu`, `newIncompleteMCffu` |
| `BaseCffu` | `copy`, `toCompletableFuture`, `minimalCompletionStage`, `withDefaultExecutor`, `withCffuFactory`, `cffuUnwrap`, `newIncompleteFuture` |
| `Cffu` | `asMCffu` |
| `MCffu` | `asCffu` |

```text
should use the returned <Type>; otherwise, the new stage is discarded
```

`<Type>` is the declared return type: `CompletableFuture`, `CompletionStage`, `Cffu`, or `MCffu`.
`copy`, `withDefaultExecutor`, and `withCffuFactory` on `BaseCffu` return the type parameter `F`.
That method is `Cffu` on a `Cffu` and `MCffu` on an `MCffu`, so the explanation says `returned stage`.

**Must not** write `otherwise, the result data is discarded` here.
These methods do not aggregate input results. `newIncompleteCffu` and `newIncompleteFuture` have no result yet.
`copy`, `toCffu`, `asCffu`, and `withDefaultExecutor` leave the original stage with the caller. The lost object is the new stage.

**Must not** write `forget to call its \`join()\` method?` here.
That sentence tells the caller to block. An incomplete stage is not ready to join. A failed stage fails on `join()`.
A rewrapped stage may be chained without blocking.

`CffuFactory.withDefaultExecutor` returns a `CffuFactory`, not a stage. It is not in this table.

## Leave `@Contract(pure = true)` alone

**Must not** add `@CheckReturnValue` to the methods below.
An ignored call does not drop a stage. IntelliJ already warns through `@Contract(pure = true)`.
SpotBugs stays silent, and that is the decision. Do not add the annotation later, and do not remove it from the stage list above, without changing this document.

Reads. The return is a value or a flag, and the caller already holds the stage or the factory:

- `BaseCffu`: `getNow`, `getSuccessNow`, `resultNow`, `exceptionNow`, `isDone`, `isCompletedExceptionally`, `isCancelled`, `state`, `cffuState`, `defaultExecutor`, `cffuFactory`, `forbidObtrudeMethods`, `isMinimalStage`, `getNumberOfDependents`, `toString`
- `CompletableFutureUtils`: `getSuccessNow`, `resultNow`, `exceptionNow`, `state`, `defaultExecutor`, `unwrapCfException`, `nonExSwallowedFunction`, `nonExSwallowedBiFunction`, `nonExSwallowedBiConsumer`
- `CffuFactory`: `defaultExecutor`, `forbidObtrudeMethods`
- `CffuState`: `toFutureState`, `toCffuState`

Executor and factory objects. The return is not a stage:

- `delayedExecutor`, `concurrencyLimitExecutor`, `sequentialExecutor` on `CompletableFutureUtils` and `CffuFactory`
- `CffuFactory.builder`, `CffuFactory.withDefaultExecutor`, `CffuFactoryBuilder.build`
- `LLCF`, including `f_toCf0`, `copy0`, `screenExecutor`, and the other `pure` helpers in that class. Those methods are internal.

## The type name in `explanation`

**Must** name the type the method actually returns: `CompletableFuture`, `CompletionStage`, `Cffu`, `MCffu`, or `stage` when the declared return is the type parameter `F`.

`MCffu` does not extend `Cffu`. A method that returns `MCffu` **must not** say `returned Cffu`.
The `Iterable` overloads already say `returned MCffu`. A varargs overload of the same method uses the same word.

## Shape

```text
should use the returned <Type>; otherwise, …
```

- A named method is the right call when the caller does not want the result: `otherwise, use method \`<AndForget>\``.
- No such method exists, and the return holds aggregated result data (`*Of` other than `allOf` and `allFailFastOf`): `otherwise, the result data is discarded`.
- The return is a new stage or a rewrap (the table in "Stage factories and rewraps"): `otherwise, the new stage is discarded`.
- The return is `Void` and the call exists to be awaited: `forget to call its \`join()\` method?`
