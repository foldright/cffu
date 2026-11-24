# Wording

Rules for Javadoc, comments, exception and log messages, and the READMEs.
Code identifiers stay as they are. Do not rename a method or field so that it matches these prose rules.

Read this before changing `must` / `should` / `may`, `i.e.` / `e.g.`, or a fixed term such as `minimal stage`.
The last section lists text that already has the right strength. Leave it.

Chinese README text uses the same distinctions: `即` = `i.e.`, `如` = `e.g.`, `尤其是` = `in particular`, `应该` = `should`, `必须` / `不得` = `must` / `must not`. `即使` is not `即`.

## Choose the strength first

Pick the word from the claim, not from emphasis.

| Word | Claim | Use it for | Do not use it for |
| --- | --- | --- | --- |
| `must` / `must not` | requirement or prohibition | a violated call is illegal: preconditions, invariants, type safety | advice that has a legal alternative |
| `should` / `should not` | recommendation | a legal alternative exists, or the next sentence states what happens if the advice is ignored | an `IllegalArgumentException` precondition |
| `may` | permission, or a possible outcome | `may be a minimal stage`, `may throw` | an outcome the contract guarantees |
| `can` | ability | the method is able to break something | an obligation |
| `will` | guaranteed outcome | a failed slot `will contain valueIfNotSuccess` | a merely possible outcome |

`if` is for something that might not happen (`if it does throw`). `when` is for an expected event (`when this stage completes`).
In a contract sentence, state a cause with `because`. `since` also means "after a point in time".
Put `only` next to the words it limits (`is only read explicitly`).

A `should` whose next sentence defines the failure behavior stays `should`.
`whenComplete` says the action should not throw, then states what the returned stage does if it throws.
Changing that to `must not` contradicts the following rules. It is also the JDK wording. Copy a JDK contract with the JDK's `should` or `must`.

## An invariant that has already failed

`should never happen` means "this was not expected".
`must never happen` is a prohibition spoken at the moment the prohibition has already failed.
Neither is the message to log.

State the invariant with `must not`. State the observation separately.

```text
// Invariant: workerCount must not exceed maxConcurrency.
... Invariant violated: workerCount must not exceed maxConcurrency.
Please report this issue to the cffu library!
```

A precondition that throws is the same strength. `defaultExecutor must not be a CffuDefaultExecutor` matches `IllegalArgumentException`. `should never be` reads as advice.

`in general` allows exceptions. `NEVER` does not. Do not write them in one sentence.

- Still a legal entry point: `Do not use this unless ...` or `Do not use this method; use X instead`. `LLCF` and `cffuUnwrap()` are this case.
- Using it is wrong: `must not`. `CffuFactoryBuilder.POISON_FACTORY` is this case: application code must not use it.

## Equivalence and examples

`i.e.` and `即` restate the same thing. The text after them is the definition, not a sample.
`e.g.` and `如` introduce examples. The list is not exhaustive.
`in particular` and `尤其是` name one part of a wider claim. They are not a restatement.

Use `i.e.` only when both sides are the same fact.

- Same fact: `locking[0]` is true if and only if `syncRun()` has not run. `i.e.` fits. `iff` is the stricter form of that same claim.
- Same fact: the default log format is `ExceptionLogFormat.FULL`.
- Same fact: `M*` methods are the Multi-Actions methods. That is a name expanded, not an example.
- Not the same fact: `CallerRunsPolicy` is one executor that runs on the caller thread. `DirectExecutor` is another. Write `e.g. CallerRunsPolicy or a direct executor`, not `i.e. CallerRunsPolicy`.
- Not the same fact: "good exception reporting" is wider than "do not swallow exceptions". Write `in particular, do not swallow exceptions`.
- Not the same fact: a collection is one kind of `Iterable`. `MCffu` says `an Iterable of elements, e.g. a collection`.

`including` names members of a set. The list may be incomplete, so do not add `etc.` or `等` after `including`, `such as`, `e.g.`, or `如`.
`subclass` means `extends`. An implementation of an interface is not a subclass. `CompletionStage, including Cffu and CompletableFuture` is the membership claim. Keep `subclass` for a class that extends another class, such as a customized subclass of `CompletableFuture`.

A causal or `i.e.` clause has to be true of that method. Do not copy the neighboring method's reason.
`f_toCf0` may return a minimal stage because it reuses the input instance.
`f_toCfCopy0` may return a minimal stage because a minimal stage input is returned as-is: a copy of a minimal stage is still a minimal stage.

When `may` and `must not` share a sentence, say what the `must not` attaches to.

```text
the returned cf may be a minimal stage, and then it must not be written or explicitly read
(e.g. complete, get, join). Otherwise, those calls may throw UnsupportedOperationException.
```

`complete` is a write. An example of "written or read" needs a read as well.

One proper name is not the whole category.

- `*Async` methods that do not take an `Executor` use `ForkJoinPool.commonPool()`. An overload that takes an `Executor` does not.
- An async method whose executor runs on the caller thread still runs on the completing thread. "Guaranteed to execute asynchronously" is not the same statement as "created with an async method". The timeout caution uses `e.g.` and names that executor limit.

For new text, write `or` when either condition is enough and `and` when both are required. Do not write `and/or` in new text.
The existing timeout CAUTION already uses `and/or` between two sufficient conditions. Leave those four bullets. The wording fix there was `i.e.` to `e.g.`, and it is done.

In Javadoc and comments, prefer `e.g.` to `for example`. `ExecutionHelpers` uses `e.g. timeouts`.

## Fixed terms

In prose, write `minimal stage` and `non-minimal stage`. Do not write `minimal-stage` or `non-minimal-stage`.
That is the JDK term. A hyphen is a second name for the same concept.

This applies to Javadoc, comments, and README text. It does not apply to identifiers. Leave `isMinimalStage`, `checkMinimalStage`, `minimalCompletionStage`, and the same camel-case names.

When a term already has one spelling in this library, use that spelling in new prose. Do not introduce a synonym.

## Leave these alone

Changing these makes the contract less accurate, or it edits a quotation.

| Text | Why it stays |
| --- | --- |
| `Errors should never pass silently. Unless explicitly silenced.` | Quotation from Python PEP 20. |
| `whenComplete`: the supplied action `should not throw` | The next sentences define the result if it throws. `must not` would contradict them. This matches the JDK. |
| `@CheckReturnValue`: `should use the returned ...; otherwise, use ...` | There is a named alternative, so `should` is the right strength. |
| README advice: handle specific exceptions, prefer collections to arrays, treat multi-`CompletableFuture` methods as lower-level | Recommendations, not prohibitions. |
| `locking[0]` is true `iff` the caller still holds the lock (`i.e.` `syncRun()` has not run) | The two sides are the same condition. |
| Default log format `i.e. ExceptionLogFormat.FULL` | The `i.e.` names that enum constant. |
| `M*` methods, `i.e.` Multi-Actions methods, and the same name expansions | The `i.e.` expands the name. |
| `e.g., Java 25+ uses ForkJoinPool.asyncCommonPool()` | Java 25 is one example of matching JDK behavior. |
| Timeout CAUTION bullets that already say `and/or` | Each bullet is sufficient. Do not edit them only to replace `and/or`. |
| Test log text such as `this task should never run` | Test narrative, not a library contract. |
