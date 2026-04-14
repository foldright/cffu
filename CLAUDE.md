## Project Overview

cffu (CompletableFuture-Fu) is a Java library that improves the `CompletableFuture` usage experience and reduces misuse. It is a multi-module Maven project (`cffu-core`, `cffu-bom`, `cffu-ttl-executor-wrapper`).

- **Runtime support**: Java 8+
- **Build requirement**: Java 21 (Maven enforcer requires Java 19+)

## Build, Test, and Development Commands

Use the Maven wrapper (`./mvnw`) for all Maven operations.

### Run all tests

```bash
./mvnw test
```

### Run tests with a different JDK without recompiling

The project supports testing across Java 8, 9, 11, 17, 21, and 25 without recompiling:

```bash
# Switch JDK (e.g., via SDKMAN or JAVA_HOME), then run:
./mvnw surefire:test
```

### Development Environment

- Prefer using the locally installed multiple Java versions:
  - SDKMAN path: `~/.sdkman/candidates/java`

## High-Level Architecture

The library provides two usage modes:

1. **`Cffu` class** (recommended): A fluent wrapper around `CompletableFuture`, similar to Guava's `FluentFuture`
2. **`CompletableFutureUtils` utility class**: Static utility methods for enhancing `CompletableFuture` directly

### Core Classes

- **`Cffu<T>`**: The main wrapper class. Implements `Future<T>` and `CompletionStage<T>`. Delegates to an underlying `CompletableFuture<T>` while adding new methods and safety features.
- **`MCffu<E, U>`**: A variant of `Cffu` for collection-type results (e.g., `List<T>`).
- **`BaseCffu<T, F>`**: Abstract base class containing all instance methods shared by `Cffu` and `MCffu`. Application code should not use this type directly.
- **`CffuFactory`**: Factory for creating `Cffu` instances (equivalent to `CompletableFuture` static factory methods). Holds the default executor configuration.
- **`CffuFactoryBuilder`**: Builder for `CffuFactory`. Supports setting a default business executor and forbidding `obtrude` methods.

### Utility Classes

- **`CompletableFutureUtils`**: Static utility methods for `CompletableFuture` enhancement. Alternative to using the `Cffu` class.
- **`CfIterableUtils`**: Variants of methods that accept `Iterable` / `Collection` inputs instead of varargs.
- **`CfTupleUtils`**: Variants of methods for heterogeneous inputs using `Tuple2`..`Tuple5` types.
- **`CfParallelUtils`**: SIMD-style parallel data processing methods (single instruction, multiple data).
- **`LLCF`**: Low-level `CompletableFuture` utilities, backport methods for Java 8, and internal helpers.
- **`ConcurrencyLimitExecutor`**: An executor implementation that limits concurrent execution.

### Packages

- `io.foldright.cffu2`: Main public API
- `io.foldright.cffu2.internal`: Internal utilities (`CommonUtils`, `CffuLogger`)
- `io.foldright.cffu2.spi`: Service Provider Interface (`ExecutorWrapperProvider`)
- `io.foldright.cffu2.tuple`: Tuple types (`Tuple2`..`Tuple5`)
- `io.foldright.cffu2.eh`: Exception handling utilities (`SwallowedExceptionHandleUtils`)
- `io.foldright.cffu2.config`: Configuration classes

### SPI Extension

The `cffu-ttl-executor-wrapper` module provides an `ExecutorWrapperProvider` implementation for `TransmittableThreadLocal` (TTL) integration. Executor wrappers are loaded via `ServiceLoader`.

## Key Engineering Principles

- **Concurrency correctness is paramount**: This is a concurrency library. All code changes must be rigorously analyzed for thread-safety, correctness, and performance implications.
- **Multi-Java-version support**: The library targets Java 8 bytecode but uses Java 21 for compilation. New methods from Java 9+ are backported for Java 8 users.
- **Comprehensive testing**: All changes must pass the full test suite. New features require thorough test coverage.
- **Static analysis**: The project uses SpotBugs, JSR-305 (`@Nullable`, `@CheckReturnValue`), and JetBrains annotations.

## Code Style

See `.editorconfig`
