# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

CFFU (CompletableFuture-Fu) is a Java library that enhances CompletableFuture with improved functionality, better usability, and safer defaults. The library supports Java 8+ and provides backport features from Java 9+.

Key modules:
- `cffu-core`: Main library with Cffu wrapper classes and CompletableFutureUtils utilities
- `cffu-ttl-executor-wrapper`: TTL (TransmittableThreadLocal) executor wrapper SPI implementation
- `cffu-bom`: Bill of Materials for dependency management

## Build and Development Commands

### Building the Project

```bash
# Clean and build with tests
./mvnw clean install

# Build without tests
./mvnw clean install -DskipTests

# Build on specific Java version (requires Java 21 for release builds)
./mvnw clean install

# Run specific module
cd cffu-core && ../mvnw clean test
```

### Running Tests

```bash
# Run all tests
./mvnw test

# Run single test class
./mvnw test -Dtest=CompletableFutureUtilsTest

# Run tests with specific Java version
./mvnw test -Djava.version=8

# Run with logging (switch to log4j2)
./mvnw test -DswitchToLog4j2LoggingDependencies=true
```

### Code Quality and Linting

```bash
# Run SpotBugs analysis
./mvnw spotbugs:check

# Check API compatibility (revapi)
./mvnw revapi:check

# Run all code quality checks (activates with -DperformRelease=true)
./mvnw clean verify -DperformRelease=true

# Check for forbidden class usage
./scripts/check_forbidden_classes.sh

# Generate and check API compatibility test
./scripts/gen_CffuApiCompatibilityTest.sh
```

### Documentation

```bash
# Generate JavaDoc
./mvnw javadoc:javadoc

# Generate aggregated JavaDoc for all modules
./mvnw javadoc:aggregate -DperformRelease=true

# Check API documentation
./scripts/check_api_docs.sh

# Update markdown table of contents
./scripts/update_md_toc.sh
```

### Running Demo Code

```bash
# Run demo classes (from demos directory)
./mvnw exec:exec -Dexec.mainClass=io.foldright.demo.AllResultsOfDemo

# Run with debugging enabled
./mvnw exec:exec -Dexec.mainClass=<MainClass> -Penable-java-main-run-debug
```

## Architecture and Code Structure

### Core Components

1. **Cffu and MCffu Classes** (`Cffu.java`, `MCffu.java`, `BaseCffu.java`)
   - Wrapper classes around CompletableFuture with enhanced functionality
   - Cffu: Single-value CompletableFuture wrapper
   - MCffu: Multi-value (tuple-returning) CompletableFuture wrapper
   - BaseCffu: Shared base class containing common implementation

2. **Utility Classes**
   - `CompletableFutureUtils`: Core utility methods for CompletableFuture enhancement
   - `CfIterableUtils`: Methods for working with Iterable collections of CompletableFutures
   - `CfParallelUtils`: Parallel processing utilities for collection data (SIMD pattern)
   - `CfTupleUtils`: Utilities for working with tuple-typed operations

3. **Factory and Configuration**
   - `CffuFactory`: Factory for creating Cffu instances with default executor settings
   - `CffuFactoryBuilder`: Builder for configuring CffuFactory instances
   - `CffuConfiguration`: Configuration options for Cffu behavior

4. **Execution Control**
   - `ConcurrencyLimitExecutor`: Executor wrapper that limits concurrent task execution
   - `ExecutorWrapperProvider` (SPI): Service provider interface for custom executor wrapping
   - `LLCF` (Logging-Lazy-CompletableFuture): Internal helper for async operations

5. **Exception Handling**
   - `ExceptionHandler`: Framework for handling swallowed exceptions in concurrent operations
   - `SwallowedExceptionHandleUtils`: Utilities for reporting exceptions that can't be propagated
   - `NoCfsProvidedException`: Thrown when no CompletableFutures are provided to methods requiring at least one

### Key Design Patterns

- **Wrapper Pattern**: Cffu wraps CompletableFuture to add functionality while maintaining compatibility
- **Factory Pattern**: CffuFactory provides centralized creation with default executor configuration
- **SPI Pattern**: ExecutorWrapperProvider allows extension via Java SPI mechanism
- **Multiple Input Orchestration**: Three forms of inputs supported:
  1. Multiple Actions (varargs/Iterable/Tuple) - MISD pattern
  2. Multiple Data (processed by same Action) - SIMD pattern
  3. Multiple CompletableFutures (varargs/Iterable/Tuple)

### Concurrent Execution Strategies

The library supports multiple execution strategies for orchestrating concurrent operations:
- **AllFailFast**: Fail immediately when any input fails (most common)
- **AnySuccess**: Return first successful result, ignore failures
- **AllSuccess**: Return all successful results with default values for failures
- **MostSuccess**: Return results within timeout with defaults for incomplete/failed
- **AllComplete**: Wait for all to complete (CompletableFuture default)
- **AnyComplete**: Return first completed (CompletableFuture default)

### Package Structure

- `io.foldright.cffu2` - Main API classes
- `io.foldright.cffu2.tuple` - Tuple types (Tuple2-Tuple5) for heterogeneous results
- `io.foldright.cffu2.config` - Configuration classes
- `io.foldright.cffu2.eh` - Exception handling utilities
- `io.foldright.cffu2.spi` - Service Provider Interface for extensions
- `io.foldright.cffu2.internal` - Internal utilities (not part of public API)

### Test Structure

- `cffu-core/src/test/java/io/foldright/cffu2/` - Unit tests
- `cffu-core/src/test/java/io/foldright/demo/` - Demo/example code
- `cffu-core/src/test/java/io/foldright/study/` - Study and research code
- `cffu-core/src/test/java/io/foldright/compatibility_test/` - Compatibility tests
- `cffu-core/src/test/java/io/foldright/aspect_test/` - Aspect-specific tests
- `cffu-core/src/test/archunit/` - Architecture validation tests (Java 11+)

## Important Development Guidelines

### Code Quality Standards

1. **Annotation Preferences** (enforced by `check_forbidden_classes.sh`):
   - Use `@edu.umd.cs.findbugs.annotations.Nullable` (not javax or JetBrains versions)
   - Use `@edu.umd.cs.findbugs.annotations.NonNull` (not javax or JetBrains versions)
   - Use `@edu.umd.cs.findbugs.annotations.CheckForNull` instead of CheckReturnValue
   - Avoid Guava annotations (`com.google.common.annotations`)
   - Use static imports for JUnit assertions instead of `Assertions` class

2. **Java Version Compatibility**:
   - Source/target compatibility: Java 8
   - Build requires: Java 21 for releases, Java 19+ for development
   - Use backport implementations for Java 9+ features to maintain Java 8 compatibility

3. **Module System**:
   - Project uses Java 9+ module system via moditect plugin
   - Module descriptors in `src/main/moditect/module-info.java`

4. **Kotlin Integration**:
   - Kotlin code compiles before Java (kotlin-maven-plugin runs first)
   - Tests use Kotest framework alongside JUnit 5

### Exception Handling Philosophy

- **Never Swallow Exceptions**: When orchestrating multiple CompletableFutures/Actions, ensure exceptions are logged or reported
- Use `SwallowedExceptionHandleUtils` for proper exception reporting in orchestration methods
- When multiple operations fail, at most one exception can be returned in the result CF; others must be logged

### Timeout Safety

- The library provides timeout-safe implementations of `orTimeout` and `completeOnTimeout`
- These prevent timeout operations from breaking CompletableFuture's internal delay functionality
- Business logic must not execute in CF's single-threaded ScheduledThreadPoolExecutor

### Thread Pool Management

- Never use `ForkJoinPool.commonPool()` in business code (limited threads, unbounded queue)
- CffuFactory allows setting default business executor to avoid repeated executor parameter passing
- Use `ConcurrencyLimitExecutor` when limiting concurrent task execution

## Release Process

Version format must match: `\d\.\d+\.\d+(-Alpha\d*)?` for releases, or `\d\.\d+\.\d+-SNAPSHOT` for development.

```bash
# Bump version
./scripts/bump_cffu_version.sh <new-version>

# Deploy to Maven Central (requires performRelease=true)
./scripts/maven_deploy.sh

# The performRelease profile activates:
# - Source generation
# - JavaDoc generation
# - GPG signing
# - SpotBugs checking
# - Git properties generation
```

## Testing with Different Configurations

```bash
# Test with different Java versions (multi-version testing happens in CI)
./mvnw test

# Test with SLF4J v1 (default is v2)
./mvnw test -Pswitch-slf4j-to-v1

# Disable ArchUnit tests (requires Java 11+)
./mvnw test -DskipDefaultProfileArchUnitTest=true

# Enable code coverage (automatically enabled in CI)
./mvnw test # JaCoCo activates when env.CI=true
```

## CI/CD

Three GitHub Actions workflows:
- `fast_ci.yaml`: Quick build on Java 21 across OS (ubuntu, windows, macos)
- `ci.yaml`: Comprehensive testing on multiple Java versions (8, 11, 17, 20, 21, 23) with code coverage
- `api_checker.yaml`: API compatibility checks using revapi

## Additional Resources

- API Documentation: https://foldright.io/api-docs/cffu2/
- Project README: Comprehensive feature documentation in English and Chinese
- Demo Code: `cffu-core/src/test/java/io/foldright/demo/` contains runnable examples
