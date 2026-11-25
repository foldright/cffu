# API design

Decisions for the public API.
`must` / `must not` below are requirements.
`should` is a recommendation: a legal alternative exists.
See [wording.md](wording.md) for those strengths.

The current decision is `*AndForget`. Add later decisions as new sections.

## `*AndForget`

`@CheckReturnValue` text for the sibling methods is in [check-return-value.md](check-return-value.md).

Read this section before adding or moving a `*AndForget` method.

### What the name means

A `*AndForget` method says the caller does not care about the computed value (`T`) and does not care about the completion event.

A method that returns `CompletableFuture<Void>` or `Cffu<Void>` still returns that event. It is not `*AndForget`. The caller who keeps that stage can wait for the action and can see its exception. The caller who drops it loses the event without saying so.

The forgotten work is a stage the method does not return. That stage is the return of the matching non-forget call (`runAsync`, `thenAccept`, `thenRun`, `thenAcceptBoth`, `runAfterBoth`, `acceptEither`, `runAfterEither`, or the existing multi-action and parallel calls). Pass that stage to `handleAllSwallowedExceptions`, then return the input stage. A method with no input stage returns `void`. Do not hide that pair behind another helper. Pass the action through when its type already matches the non-forget call.

### One method per action shape

One `*AndForget` per action shape, not per completion policy. FailFast, AllSuccess, MostSuccess, Any, AnySuccess, and EitherSuccess do not get their own method. Those policies are visible only on the returned stage. `*AndForget` does not return that stage. The action still runs on the successful trigger of the plain method: the actions run, both stages succeeded, or the first success.

A family that has a synchronous method gets a synchronous `*AndForget`, such as `thenAcceptAndForget`. A family that is only asynchronous gets only `*AsyncAndForget`. A synchronous caller points at the synchronous method. An asynchronous caller points at the asynchronous method.

A single-action method points at the single-action `*AndForget`. It **must not** point at an `M*` or `par*` method. Those are a different number of actions.

A value-producing method points at the side-effect `*AndForget` of the same family. `supply` and a start-now `run` point at `run`, because there is no input value to accept. `apply`, `accept`, `combine`, and `applyToEither` point at `accept`, because there is a value.

`handle` and `whenComplete` point at `peek` or `peekAsync`. `peek` already returns the original stage and documents the action as fire-and-forget. Do not add `peekAndForget`.

`*Of` methods compose stages that are already running. They have no `*AndForget`.

Tuple methods have no tuple `*AndForget`. They keep pointing at `mRunAsyncAndForget` or `thenMAcceptAsyncAndForget`.

### Where a method lives

The static method and the fluent method match.

| Methods | Static | Fluent |
| --- | --- | --- |
| `runAsyncAndForget` | `CompletableFutureUtils` | `CffuFactory` |
| `mRunAsyncAndForget` | `CompletableFutureUtils`, `CfIterableUtils` | `CffuFactory`, `CffuFactory.IterableOps` |
| `thenM*AndForget` | `CompletableFutureUtils`, `CfIterableUtils` | `BaseCffu`, `BaseCffu.IterableOps` |
| `parAcceptAsyncAndForget` | `CfParallelUtils` | `CffuFactory.ParOps` |
| `thenParAcceptAsyncAndForget` | `CfParallelUtils` | `MCffu.ParOps` |
| single-action, `thenBoth*`, and `thenEither*` `*AndForget` | `CompletableFutureUtils` | `BaseCffu` |

`Cffu` and `MCffu` inherit the `BaseCffu` methods.

`runAsyncAndForget`, `mRunAsyncAndForget`, and `parAcceptAsyncAndForget` return `void`. Every `then*AndForget` returns the original stage.

### Order

Variants of one action stay together. `*AndForget` is the last variant of that action: after the plain method, and after FailFast or EitherSuccess when that policy exists.

`thenAcceptAndForget` follows `thenAccept*`. `thenRunAndForget` follows `thenRun*`. `thenAcceptBothAndForget` follows `thenAcceptBoth*`. `acceptEitherAndForget` follows `acceptEither*`. `runAfterBothAndForget` and `runAfterEitherAndForget` follow their own `run*` methods.

### Section comments

A section comment names the action shapes in that section. It includes a shape whose methods this class does not declare, because `CompletableFuture` or `CompletionStage` already has them. The return type in the comment is the type that class returns. `CompletableFutureUtils` says `CompletableFuture`. `BaseCffu` and `CffuFactory` say `Cffu`.

The comment stays on the action-shape dimension. Do not add a second line for `*AndForget`. Two dimensions in one list are hard to read.

### `@Contract`

A `then*AndForget` that returns the input stage has `@Contract` when the annotated method's receiver is that stage.

- A static method uses `-> param1`. The first parameter is the input stage.
- An instance method of `BaseCffu` uses `-> this`.

A `void` `*AndForget` has no such contract.

**Must not** put `@Contract("... -> this")` on a method nested in `*Ops` (`IterableOps`, `ParOps`, `TupleOps`).

JetBrains `@Contract` return values are fixed tokens: `this`, `new`, `paramN`, and the other tokens in the [Contract](https://javadoc.io/doc/org.jetbrains/annotations/latest/org/jetbrains/annotations/Contract.html) documentation. `this` is defined as the qualifier value of the annotated method. It is the receiver of that method. There is no `Outer.this` token. On an `*Ops` method, `this` means the `*Ops` instance, not the enclosing `Cffu`, `MCffu`, or `CffuFactory`. This limit applies to every method nested in `*Ops`, not only to `*AndForget`.
