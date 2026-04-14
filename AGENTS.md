# AGENTS.md

cffu (CompletableFuture-Fu) is a Java library that improves the `CompletableFuture` usage experience and reduces misuse.

## Project Overview

- **Runtime support**: Java 8+ (bytecode target 1.8)
- **Build requirement**: Java 21 (Maven enforcer requires Java 19+)
- **Tested JDKs**: 8, 9, 11, 17, 21, 25 (authoritative list: `JDK_VERSIONS` in `scripts/integration_test`)

## Engineering Principles

cffu is a concurrency library: correctness, reliability and high performance are required in every change.

- **Concurrency correctness is paramount.** For any logic change, analyze thread-safety
  (visibility, atomicity, ordering, exception/cancellation propagation, executor usage)
  and document why it is correct (code comments / PR description).
- **Performance matters.** When more than one implementation strategy is plausible,
  evaluate and compare the alternatives (allocations, extra async hops, contention) before choosing.
- **Multi-Java-version support.** The library targets Java 8 bytecode but compiles with Java 21.
  The build does not use `--release 8`, so the compiler will **not** flag Java 9+ API calls and a mistake
  only shows up as `NoSuchMethodError` on an older JDK. Therefore a Java 9+ API (e.g. `completedStage`,
  `newIncompleteFuture`, `exceptionallyCompose`, `resultNow`) may be called in library code only behind a runtime
  guard (`LLCF.IS_JAVA9_PLUS` / `IS_JAVA12_PLUS` / `IS_JAVA19_PLUS` / `IS_JAVA21_PLUS`) with a working Java 8
  fallback (e.g. `CompletableFutureUtils.failedFuture`). When adding such a call, add the guard and
  fallback together, and run the tests on JDK 8 (see below). Consider behavior differences across the supported JDKs.
- **Rigorous testing.** All changes must pass `./mvnw test` from the project root,
  and new features need comprehensive, relevant tests.
- **Static analysis.** Code must pass SpotBugs and the quality checks listed below.
  Run `./mvnw verify -DperformRelease -P!gen-sign -P!enforce-release-pom-version-when-perform-release`.
  That reaches the `lint` profile on `verify`; `./mvnw test` does not. Strong CI does this inside `scripts/integration_test`.

## Development Environment

- Compile with Java 21 (the build refuses Java below 19).
- Multiple JDKs are needed locally to run tests on lower versions. With SDKMAN they are under
  `~/.sdkman/candidates/java`; any other way of setting `JAVA_HOME` works too.

### Modules

Root `pom.xml` modules:

- `cffu-core`: the library itself (public API in `io.foldright.cffu2`)
- `cffu-bom`: Bill of Materials POM
- `cffu-ttl-executor-wrapper`: SPI extension for `TransmittableThreadLocal` (TTL) integration

Not part of the root reactor:

- `demos`: usage examples (not published). It is a **separate Maven build**,
  run in CI via `demos/scripts/integration_test`; `./mvnw test` at the root does not cover it.

## Build and Test Commands

Always use the Maven wrapper (`./mvnw`) from the project root.

```bash
# all tests (compiles with the current JDK, must be Java 19+; use Java 21)
./mvnw test

# single test class / method (in cffu-core)
./mvnw test -pl cffu-core -Dtest=CffuTest
./mvnw test -pl cffu-core -Dtest=CffuTest#test_method_name
```

### Run tests on a lower JDK without recompiling

Compile with Java 19+ first, then switch to a lower JDK (Java 18-, via SDKMAN / `JAVA_HOME`) and run:

```bash
./mvnw surefire:test
```

CI passes extra per-JDK Maven options (e.g. logging dependency switches).
If a lower-JDK run behaves differently than expected, check `scripts/integration_test`.

### Full multi-JDK build (CI parity, slow)

```bash
scripts/integration_test
```

Builds with Java 21, then runs the tests on every supported JDK. This is what the "Strong CI"
workflow runs; use it for release-level verification, not routine iteration.

## Testing

- Test sources (JUnit 5 + AssertJ, and Kotest for the Kotlin tests):
  - `cffu-core/src/test/java`: **both** Java (`*.java`) and Kotlin (`*.kt`) tests live here
  - `cffu-core/src/test/archunit`: architecture rules (ArchUnit, Kotlin)
- All changes must pass `./mvnw test`; new features need comprehensive tests.
- **Generated file, do not hand-edit**:
  `cffu-core/src/test/java/io/foldright/compatibility_test/CffuApiCompatibilityTest.java`
  is generated from `CompletableFutureApiCompatibilityTest.java` by
  `scripts/gen_CffuApiCompatibilityTest.sh` (CI regenerates it; needs `gsed` on macOS:
  `brew install gnu-sed`). After editing the source test, run the script instead of editing the target.

## Static Analysis and Quality Checks

Run the relevant ones before finishing; all are run by CI:

| Script | Purpose |
| --- | --- |
| `scripts/check_forbidden_classes.sh` | Fails on the forbidden names below |
| `scripts/check_api_docs.sh` | Fails on Javadoc `invalid reference` in generated apidocs |
| `scripts/run_api_checker.sh` | Binary/API compatibility vs. released versions (needs network) |

Conventions enforced by these checks:

- `@Nullable`, `@NonNull`, and `@CheckReturnValue`: use `edu.umd.cs.findbugs.annotations`.
  Forbidden: `javax.annotation.Nullable`, `javax.annotation.Nonnull`, `javax.annotation.CheckReturnValue`,
  `javax.annotation.ParametersAreNonnullByDefault`, `org.jetbrains.annotations.Nullable`,
  `org.jetbrains.annotations.NotNull`, `org.jetbrains.annotations.CheckReturnValue`,
  and `com.google.common.annotations`.
  Allowed: other `javax.annotation` and `org.jetbrains.annotations` types, including
  `@Contract`, `@Blocking`, `@ApiStatus`, `@VisibleForTesting`, `@ThreadSafe`, and `@GuardedBy`.
  Parameters-nonnull default: `@edu.umd.cs.findbugs.annotations.DefaultAnnotationForParameters(NonNull.class)`.
  `package-info.java` may keep `javax.annotation.ParametersAreNonnullByDefault`.
- Tests: static-import `Assertions` methods. Importing `org.junit.jupiter.api.Assertions` itself is forbidden.

`scripts/check_forbidden_classes.sh` enforces those forbidden names with a fixed-string search.
Any match fails the script. It skips `package-info.java`, `scripts/`, and `src/package-list/`,
so `ParametersAreNonnullByDefault` in `package-info.java` is not reported.
The Assertions entry is the text `org.junit.jupiter.api.Assertions;`, so
`import org.junit.jupiter.api.Assertions;` fails and a static method import does not.

- SpotBugs exclusions go in `src/spotbugs-exclude-filter-file.xml` (repo root `src/`).
  Prefer fixing the finding over adding an exclusion.

## Architecture

Two usage modes:

1. **`Cffu`** (recommended): fluent wrapper around `CompletableFuture`, like Guava's `FluentFuture`
2. **`CompletableFutureUtils`**: static utility methods operating on `CompletableFuture` directly

### Core classes (package `io.foldright.cffu2`)

- `Cffu<T>`: main wrapper; implements `Future<T>` and `CompletionStage<T>`, delegates to an underlying `CompletableFuture<T>`
- `MCffu<E, T extends Iterable<? extends E>>`: `Cffu` variant for collection-type results
- `BaseCffu<T, F>`: abstract base with the instance methods shared by `Cffu` and `MCffu`;
  **internal to the library, application code must not use it directly**.
  Add or change a shared instance method here so both `Cffu` and `MCffu` get it.
  Collection-only methods stay on `MCffu`.
- `CffuFactory` / `CffuFactoryBuilder`: create `Cffu` instances (like `CompletableFuture` static factories);
  hold the default executor and the "forbid `obtrude` methods" setting

### Utility classes

- `CompletableFutureUtils`: main static utilities
- `CfIterableUtils`: `Iterable` / `Collection` input variants (instead of varargs)
- `CfTupleUtils`: heterogeneous input variants using `Tuple2`..`Tuple5`
- `CfParallelUtils`: SIMD-style parallel data processing (single instruction, multiple data)
- `LLCF`: low-level utilities, Java 9+ method backports for Java 8, internal helpers
- `ConcurrencyLimitExecutor`: package-private executor that limits concurrent execution.
  Public entry points are `CompletableFutureUtils.concurrencyLimitExecutor` and `CffuFactory.concurrencyLimitExecutor`.

### Packages

- `io.foldright.cffu2`: main public API
- `io.foldright.cffu2.internal`: internal utilities (`CommonUtils`, `CffuLogger`), not public API
- `io.foldright.cffu2.spi`: SPI (`ExecutorWrapperProvider`)
- `io.foldright.cffu2.tuple`: `Tuple2`..`Tuple5`
- `io.foldright.cffu2.eh`: exception handling (`SwallowedExceptionHandleUtils`)
- `io.foldright.cffu2.config`: configuration classes

### SPI extension

`cffu-ttl-executor-wrapper` implements `ExecutorWrapperProvider` for TTL integration;
wrappers are discovered via `ServiceLoader` (`META-INF/services` file and moditect `module-info.java`).
When adding or renaming SPI classes, keep both in sync.

## Code Style

See `.editorconfig`:

- Java/Kotlin: 4-space indent (8-space continuation); most other files: 2-space; XML: tabs
- UTF-8, LF line endings, final newline, no trailing whitespace
