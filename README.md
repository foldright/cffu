# <div align="center"><a href="#dummy"><img src="https://github.com/foldright/cffu/assets/1063891/124658cd-025f-471e-8da1-7eea0e482915" alt="🦝 CompletableFuture-Fu(CF-Fu)"></a></div>

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/fast_ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/fast_ci.yaml?branch=2.x-dev&logo=github&logoColor=white&label=fast%20ci" alt="Fast Build CI"></a>
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=2.x-dev&logo=github&logoColor=white&label=strong%20ci" alt="Strong Build CI"></a>
<a href="https://app.codecov.io/gh/foldright/cffu/tree/2.x-dev"><img src="https://img.shields.io/codecov/c/github/foldright/cffu/2.x-dev?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://qodana.cloud/projects/A61Yy/reports?branch=2.x-dev"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/qodana_code_quality.yml?branch=2.x-dev&logo=jetbrains&logoColor=white&label=qodana" alt="Qodana Code Inspections"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/cffu?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/api-docs/cffu2/"><img src="https://img.shields.io/github/release/foldright/cffu?label=javadoc&color=339933&logo=read-the-docs&logoColor=white&filter=v2.*" alt="Javadocs"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu2/2.0.0-Alpha6/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu2?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/cffu/releases"><img src="https://img.shields.io/github/release/foldright/cffu.svg?filter=v2.*" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/cffu/stargazers"><img src="https://img.shields.io/github/stars/foldright/cffu?style=flat" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/cffu/fork"><img src="https://img.shields.io/github/forks/foldright/cffu?style=flat" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/cffu/issues"><img src="https://img.shields.io/github/issues/foldright/cffu" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/graphs/contributors"><img src="https://img.shields.io/github/contributors/foldright/cffu" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/cffu"><img src="https://img.shields.io/github/repo-size/foldright/cffu" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/cffu"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

📖 English Documentation | 📖 [中文文档](docs/README_CN.md)

--------------------------------------------------------------------------------

👉 Java CompletableFuture-Fu ("CF-Fu", pronounced "Shifu" 🦝) is a tiny 0-dependency library that improves
the [`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
usage experience and reduces misuse, enabling more convenient, efficient, and safe use of `CF` in your application. 😋🚀🦺

Welcome 👏💖

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="23%" align="right" alt="shifu" /></a>

- For suggestions and questions, [submit an Issue](https://github.com/foldright/cffu/issues/new)
- For contributions and improvements, play [fork](https://github.com/foldright/cffu/fork) and pull request dance

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🔧 Features](#-features)
  - [About `CompletableFuture`](#about-completablefuture)
- [👥 User Guide](#-user-guide)
  - [1. `cffu` usage modes](#1-cffu-usage-modes)
    - [1.1 recommended `Cffu` class usage 🌟](#11-recommended-cffu-class-usage-)
    - [1.2 migrating code from using `CompletableFuture` class to using `Cffu` class](#12-migrating-code-from-using-completablefuture-class-to-using-cffu-class)
    - [1.3 dependencies (including `CompletableFutureUtils` utility class)](#13-dependencies-including-completablefutureutils-utility-class)
  - [2. `cffu` feature introduction](#2-cffu-feature-introduction)
    - [2.1 support for returning overall results of multiple input `CF`s](#21-support-for-returning-overall-results-of-multiple-input-cfs)
    - [2.2 support for setting default business thread pool](#22-support-for-setting-default-business-thread-pool)
    - [2.3 efficient and flexible concurrent execution strategies (`AllFailFast` / `AnySuccess` / `AllSuccess` / `MostSuccess`)](#23-efficient-and-flexible-concurrent-execution-strategies-allfailfast--anysuccess--allsuccess--mostsuccess)
    - [2.4 support for directly running multiple `action`s instead of wrapping them into `Completablefuture`s first](#24-support-for-directly-running-multiple-actions-instead-of-wrapping-them-into-completablefutures-first)
    - [2.5 Support for async parallel processing of collection data, instead of wrapping data and `Action` into `CompletableFuture`s first](#25-support-for-async-parallel-processing-of-collection-data-instead-of-wrapping-data-and-action-into-completablefutures-first)
    - [2.6 support for handling specific exception types instead of all `Throwable` exceptions](#26-support-for-handling-specific-exception-types-instead-of-all-throwable-exceptions)
    - [2.7 backport support for `Java 8`](#27-backport-support-for-java-8)
    - [2.8 timeout-safe new implementation of `orTimeout` / `completeOnTimeout`](#28-timeout-safe-new-implementation-of-ortimeout--completeontimeout)
    - [2.9 support for timeout-enabled `join` method](#29-support-for-timeout-enabled-join-method)
    - [2.10 `anyOf` method that returns specific types](#210-anyof-method-that-returns-specific-types)
    - [2.11 `allOf/anyOf` methods that accept broader input types](#211-allofanyof-methods-that-accept-broader-input-types)
    - [more feature documentation](#more-feature-documentation)
- [🔌 API Docs](#-api-docs)
- [🍪 Dependencies](#-dependencies)
- [📚 More Resources](#-more-resources)
- [👋 About the Library Name](#-about-the-library-name)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

# 🔧 Features

☘️ **Completing missing functionality in business development**

- 🏪 More convenient features, such as:
  - Support for returning overall results of multiple input `CF`s
    instead of returning `CF<Void>` without input `CF` results (`CompletableFuture#allOf`)
    - Such as methods `allResultsFailFastOf` / `mSupplyFailFastAsync` / `thenMApplyMostSuccessAsync`
  - Support for directly running multiple `Action`s instead of wrapping them into `CompletableFuture`s first
    - Such as methods `mSupplyAsync` / `mRunFailFastAsync` / `thenMApplyAllSuccessAsync`
    - Aka. multiple instruction, single data (`MISD`) style processing
  - Support for async parallel processing of collection data,
    instead of wrapping data with actions into `CompletableFuture`s first
    - Such as methods `CfParallelUtils#parApplyFailFastAsync` / `CfParallelUtils#thenParAcceptAnySuccessAsync`
    - Aka. single instruction, multiple data (`SIMD`) style processing
  - Support for inputting collections of `CF`s / `Action`s instead of converting them to array first
    - Such as methods `CfIterableUtils#allResultsFailFastOf` /
      `CfIterableUtils#mSupplyFailFastAsync` / `CfIterableUtils#thenMApplyMostSuccessAsync`
  - Support for setting a default business thread pool via `CffuFactory#builder(executor)` method,
    instead of repeatedly passing business thread pool parameters during async execution
  - Support for handling specific exception types via `catching` methods
    instead of handling all `Throwable` exceptions (`CompletableFuture#exceptionally`)
- 🚦 More efficient and flexible concurrent execution strategies, such as:
  - `AllFailFast` strategy: fail fast when any of the multiple input `CF`s complete
    exceptionally instead of futilely waiting for all `CF`s to complete (`CompletableFuture#allOf`)
  - `AnySuccess` strategy: Return the first successful `CF` result
    instead of the first completed but possibly exceptional `CF` (`CompletableFuture#anyOf`)
  - `AllSuccess` strategy: Return successful results from multiple `CF`s,
    returning specified default values for exceptional `CF`s
  - `MostSuccess` strategy: Return successful results from multiple `CF`s within a specified time,
    returning specified default values for exceptional or timed-out `CF`s
  - `All(Complete)` / `Any(Complete)` strategies: These two are strategies already supported by `CompletableFuture`
- 🦺 Safer usage patterns, such as:
  - Timeout-safe new implementation of `orTimeout` / `completeOnTimeout` methods
    - The `CF#orTimeout` / `CF#completeOnTimeout` methods can break CompletableFuture's timeout and delay functionality❗️
  - `peek` processing method that definitely won't modify `CF` results
    - The `whenComplete` method may modify the `CF` result,
      and the returned `CF` result may not be consistent with the input
  - Support for timeout-enabled `join(timeout, unit)` method
  - Support for the forbidding `obtrude` methods via `CffuFactoryBuilder#forbidObtrudeMethods` method
  - Complete code quality annotations attached to class methods,
    enabling `IDE`s to provide early problem warnings during coding
    - Such as `@NonNull`, `@Nullable`, `@CheckReturnValue`, `@Contract`, etc.
- 🧩 Missing basic functionality, in addition to the safety-oriented new implementations above, including:
  - Async exception completion, `completeExceptionallyAsync` method
  - Non-blocking retrieval of successful results, returning specified default values
    for exceptional or incomplete `CF`s, `getSuccessNow` method
  - Unwrapping `CF` exceptions into business exceptions, `unwrapCfException` method

⏳ **`Backport` support for `Java 8`**, all new `CF` methods from `Java 9+` versions
are directly available in `Java 8` version, such as:

- Timeout control: `orTimeout` / `completeOnTimeout`
- Delayed execution: `delayedExecutor`
- Factory methods: `failedFuture` / `completedStage` / `failedStage`
- Processing operations: `completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`
- Non-blocking reads: `resultNow` / `exceptionNow` / `state`

💪 **Enhancement of existing features**, such as:

- `anyOf` method: Returns specific type `T` (type-safe)
  instead of returning `Object` (`CompletableFuture#anyOf`)
- `allOf` / `anyOf` methods: Accept broader `CompletionStage` parameter types
  instead of `CompletableFuture` class (`CompletableFuture#allOf/anyOf`)

For more information about `cffu` features and usage,
see the [`cffu` Feature Introduction](#2-cffu-feature-introduction).

## About `CompletableFuture`

Managing concurrent execution is a complex and error-prone problem,
and the industry has a large number of tools and frameworks available.

> For a broad understanding of concurrency tools and frameworks, you can check out books like
> "[Seven Concurrency Models in Seven Weeks](https://www.google.com/books/edition/Seven_Concurrency_Models_in_Seven_Weeks/TspYEQAAQBAJ)",
> "[Programming Concurrency on the JVM](https://www.google.com/books/edition/Programming_Concurrency_on_the_JVM/xstYEQAAQBAJ)",
> "[Learning Concurrent Programming in Scala (2nd Edition)](https://www.google.com/books/edition/Learning_Concurrent_Programming_in_Scala/D1QoDwAAQBAJ)".

Among them, [`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
has its advantages:

- **Widely Known and Widely Used**
  - `CompletableFuture` was provided in `Java 8` released in 2014, 10 years ago
  - The parent interface [`Future`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrnt/Future.html)
    of `CompletableFuture` was provided as early as `Java 5` released in 2004, 20 years ago. Although the `Future`
    interface doesn't support asynchronous retrieval of execution results and orchestration of concurrent execution logic,
    it has made the majority of `Java` developers familiar with the typical concept and tool of `Future`
- **Powerful but Not Excessively Complex**
  - Sufficient to handle daily business asynchronous concurrency needs
  - Other large-scale concurrency frameworks (such as [`Akka`](https://akka.io/), [`RxJava`](https://github.com/ReactiveX/RxJava))
    require much more understanding to use. Of course, basic concurrency concerns and their complexity
    are independent of which specific tool is used and must all be understood and paid attention to
- **High-Level Abstraction**
  - Or expressing technical concurrent processes in the form of business processes
  - Can avoid or reduce the use of cumbersome and error-prone basic concurrent coordination tools:
    [Synchronizers](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html#synchronizers-heading)
    (such as [`CountDownLatch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html),
    [`CyclicBarrier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CyclicBarrier.html),
    [`Phaser`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Phaser.html)),
    [Locks](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/package-summary.html),
    and [atomic classes](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html)
- **Built into `Java` Standard Library**
  - No additional dependencies required, almost always available
  - Believed to have extremely high implementation quality

Like other concurrency tools and frameworks, `CompletableFuture` is used for:

- Concurrent execution of business logic, or orchestrating concurrent processing flows or asynchronous tasks
- Multicore parallel processing, making full use of resources
- Shortening request response time and improving business responsiveness

It's worth understanding and applying in more depth. 💕

# 👥 User Guide

## 1. `cffu` usage modes

- 🦝 **Using the `Cffu` class**
- 🔧 **Using the `CompletableFutureUtils` utility class**

### 1.1 recommended `Cffu` class usage 🌟

Compared to calling static methods of the `CompletableFutureUtils` utility class:

- Using the `Cffu` class is like using `CompletableFuture`, with new features as instance methods
  of the `Cffu` class, which can be called naturally and conveniently
  - The `Cffu` class to the `CompletableFuture` utility class `CompletableFutureUtils`
    is like Guava's `FluentFuture` to the `ListenableFuture` utility class `Futures`
- The `Java` language doesn't support extending methods on existing classes
  (`CompletableFuture`), so a new wrapper class (`Cffu`) is needed

If you don't want to introduce a new class (`Cffu` class) to your project and feel that
this adds complexity, you can completely use the `cffu` library as a utility class:

- Utility methods for optimizing `CompletableFuture` usage are very common in business projects
- `CompletableFutureUtils` provides a series of practical, efficient, safe, and reliable utility methods
- Some `cffu` features are not provided in this usage mode (and no suitable implementation approach has been found)  
  Such as support for setting default business thread pools and forbidding `obtrude` methods

### 1.2 migrating code from using `CompletableFuture` class to using `Cffu` class

To conveniently and naturally use the enhanced features and methods of the `cffu` library,
you can migrate existing code that uses the `CompletableFuture` class to the `Cffu` class.

1\) If you can modify code that uses `CompletableFuture`

Migrating to the `Cffu` class involves 2 simple changes:

- In type declaration places, change the `CompletableFuture` class to the `Cffu` class
- In `CompletableFuture` static method call places, change the class name `CompletableFuture` to a `cffuFactory` instance

> The reason this migration is possible is that:
>
> - All instance methods of the `CompletableFuture` class are implemented
    in the `Cffu` class with the same method signatures and functionality
> - All static methods of the `CompletableFuture` class are implemented
    in the `CffuFactory` class with the same method signatures and functionality

2\) If you cannot modify code that uses `CompletableFuture` (such as `CF` returned from external libraries)

Use the [`CffuFactory.toCffu(CompletionStage)` method](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/CffuFactory.html#toCffu(java.util.concurrent.CompletionStage))
to convert `CompletableFuture` or `CompletionStage` to `Cffu` type.

### 1.3 dependencies (including `CompletableFutureUtils` utility class)

- For `Maven` projects:

  ```xml
  <dependency>
    <groupId>io.foldright</groupId>
    <artifactId>cffu2</artifactId>
    <version>2.0.0-Alpha6</version>
  </dependency>
  ```
- For `Gradle` projects:

  Gradle Kotlin DSL
  ```groovy
  implementation("io.foldright:cffu2:2.0.0-Alpha6")
  ```
  Gradle Groovy DSL
  ```groovy
  implementation 'io.foldright:cffu2:2.0.0-Alpha6'
  ```

## 2. `cffu` feature introduction

### 2.1 support for returning overall results of multiple input `CF`s

The return type of `CompletableFuture#allOf` method is `CF<Void>` without the execution results of the input `CF`s.
To get the execution results of input `CF`s, you need to:

- After the `allOf` method, get results through explicit read operations (like `join` / `get`) on the input parameter `CF`s
  - Cumbersome operation 🔧🤯
  - Read methods like `join` / `get` are blocking, increasing the deadlock risk in business logic❗️  
    For more explanation, see article [CompletableFuture Principles and Practice - 4.2.2 Thread Pool Circular References Can Cause Deadlocks](https://juejin.cn/post/7098727514725416967#heading-24)
- Or set external variables in the passed `CompletableFuture Action`
  - Need to pay attention to thread safety issues of multithreaded read-write ⚠️🔀  
    Multi-threaded read-write involves the complexity of multithreaded data transfer;
    omitting correct handling of concurrent logic data read-write is a common problem in business code❗️
  - Avoid concurrent pitfalls; concurrent logic is complex and bug-prone 🐞  
    If timeouts are involved, it becomes even more complex; even `JDK CompletableFuture` itself has
    [bug fixes](https://github.com/foldright/cffu/releases/tag/v1.0.0-Alpha20) in this area in `Java 21` ⏰

Methods in `cffu` like `allResultsFailFastOf` / `mSupplyFailFastAsync` / `thenMApplyMostSuccessAsync`
provide functionality to return results of input `CF`s.
Using these methods to get overall execution results of input `CF`s:

- Convenient and straightforward
- Because the returned result is a `CF` with overall results,
  you can continue chaining non-blocking operations,
  naturally reducing the use of blocking read methods (like `join` / `get`)
  and minimizing deadlock risk in business logic
- Avoids complex thread safety issues and logic errors of
  directly implementing multithreaded read-write logic in business logic
- Using "reliably implemented and tested" library-provided concurrency features
  instead of implementing them directly is a best practice 🏆✅

Example code:

```java
public class AllResultsOfDemo {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    //////////////////////////////////////////////////
    // CffuFactory#allResultsOf
    //////////////////////////////////////////////////
    Cffu<Integer> cffu1 = cffuFactory.completedFuture(21);
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Void> all = cffuFactory.allOf(cffu1, cffu2);
    // result type is Void!
    //
    // the result can be got by input argument `cf1.get()`, but it's cumbersome.
    // so we can see a lot of util methods to enhance `allOf` with result in our project.

    MCffu<Integer, List<Integer>> allResults = cffuFactory.allResultsOf(cffu1, cffu2);
    System.out.println(allResults.get());
    // output: [21, 42]

    //////////////////////////////////////////////////
    // or CompletableFutureUtils#allResultsOf
    //////////////////////////////////////////////////
    CompletableFuture<Integer> cf1 = CompletableFuture.completedFuture(21);
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Void> all2 = CompletableFuture.allOf(cf1, cf2);
    // result type is Void!

    CompletableFuture<List<Integer>> allResults2 = allResultsOf(cf1, cf2);
    System.out.println(allResults2.get());
    // output: [21, 42]
  }
}
```

> \# Complete runnable demo code can be found at
> [`AllResultsOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllResultsOfDemo.java).

### 2.2 support for setting default business thread pool

The default thread pool used by `CompletableFuture` async execution (i.e., `*Async` methods)
is `ForkJoinPool.commonPool()`; using this default thread pool in business is very dangerous❗

- `ForkJoinPool.commonPool()` has about as many threads as CPUs, suitable for executing CPU-intensive tasks;
  for business logic, there are often many waiting operations (such as network `IO`, blocking waits)
  that are not CPU-intensive, leading to low business processing capabilities 🐌
- `ForkJoinPool` uses unbounded queues; during high traffic, tasks will accumulate,
  causing memory exhaustion and service crashes 🚨  
  For more information about this problem and its causes, see [this article](https://juejin.cn/post/7476755577193824295)

The result is that in business logic, when calling `CompletableFuture`'s `*Async` methods,
you almost always need to repeatedly pass in a specified business thread pool;
this makes using `CompletableFuture` cumbersome and error-prone 🤯❌

Additionally, when under-layer operations call back to business logic (such as `RPC` callbacks),
it's not appropriate or convenient to provide a thread pool for the business;
using `Cffu` to set the default business thread pool specified by upper-level business
is both convenient, reasonable, and safe.  
For more information about this usage scenario, see
[CompletableFuture Principles and Practice - 4.2.3 Asynchronous RPC Calls Should Not Block IO Thread Pools](https://juejin.cn/post/7098727514725416967#heading-25)

Example code:

```java
public class NoDefaultExecutorSettingForCompletableFuture {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    CompletableFuture<Void> cf1 = CompletableFuture.runAsync(
        () -> System.out.println("doing a long time work!"),
        myBizExecutor);

    CompletableFuture<Void> cf2 = CompletableFuture
        .supplyAsync(
            () -> {
              System.out.println("doing another long time work!");
              return 42;
            },
            myBizExecutor)
        .thenAcceptAsync(
            i -> System.out.println("doing third long time work!"),
            myBizExecutor);

    CompletableFuture.allOf(cf1, cf2).join();
  }
}
```

> \# Complete runnable demo code can be found at
> [`NoDefaultExecutorSettingForCompletableFuture.java`](cffu-core/src/test/java/io/foldright/demo/NoDefaultExecutorSettingForCompletableFuture.java).

The `Cffu` class supports setting a default business thread pool,
avoiding the above cumbersomeness and dangers. Example code:

```java
public class DefaultExecutorSettingForCffu {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) {
    Cffu<Void> cf1 = cffuFactory.runAsync(() -> System.out.println("doing a long time work!"));

    Cffu<Void> cf2 = cffuFactory.supplyAsync(() -> {
      System.out.println("doing another long time work!");
      return 42;
    }).thenAcceptAsync(i -> System.out.println("doing third long time work!"));

    cffuFactory.allOf(cf1, cf2).join();
  }
}
```

> \# Complete runnable demo code can be found at
> [`DefaultExecutorSettingForCffu.java`](cffu-core/src/test/java/io/foldright/demo/DefaultExecutorSettingForCffu.java).

### 2.3 efficient and flexible concurrent execution strategies (`AllFailFast` / `AnySuccess` / `AllSuccess` / `MostSuccess`)

- `CompletableFuture`'s `allOf` method waits for all input `CF`s to complete; even if a `CF` completes
  exceptionally, it still waits for subsequent `CF`s to complete before returning an exceptional `CF`.
  - For business logic, this failure-and-continue-waiting strategy (`AllComplete`) slows down business responsiveness;
    businesses would prefer fail fast when any of input `CF` complete exceptionally, instead of futilely waiting
  - `cffu` provides corresponding methods like `allResultsFailFastOf`
    that support the `AllFailFast` concurrent execution strategy
  - Both `allOf` and `allResultsFailFastOf` return successful results only when all input `CF`s successful
- `CompletableFuture`'s `anyOf` method returns the first completed `CF` without waiting for subsequent
  incomplete `CF`s; even if the first completed `CF` is exceptional, it returns this exceptional `CF` result.
  - For business logic, what's often wanted is not the first completed but exceptional `CF` result (`AnyComplete`),
    but rather the first successful `CF` result
  - `cffu` provides corresponding methods like `anySuccessOf`
    that support the `AnySuccess` concurrent execution strategy
  - `anySuccessOf` returns a exceptional result only when all input `CF`s exceptional
- Return successful results from multiple `CF`s, returning specified default values for exceptional `CF`s
  - When business logic includes fault tolerance, successful partial results can be used,
    instead of overall failure when some `CF`s fail
  - `cffu` provides corresponding methods like `allSuccessOf`
    that support the `AllSuccess` concurrent execution strategy
- Return successful results from multiple `CF`s within a specified time,
  returning specified default values for incomplete or timed-out `CF`s
  - When business is eventually consistent, return available results as much as possible;
    for `CF`s that couldn't return in time, results will be written to distributed cache
    for the next business request, avoiding duplicate calculations
  - This is a common business usage pattern; `cffu` provides corresponding methods
    like `mostSuccessResultsOf` that support the `MostSuccess` concurrent execution strategy

> 📔 For more about concurrent execution strategies for multiple `CF`s, see the JavaScript specification
> [`Promise Concurrency`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise#promise_concurrency);
> in JavaScript, `Promise` corresponds to `CompletableFuture`.
>
> JavaScript `Promise` provides 4 concurrent execution methods:
>
> - [`Promise.all()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all):
    Wait for all `Promise`s to complete, immediately return exceptionally if any input complete exceptionally (`AllFailFast`)
> - [`Promise.allSettled()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/allSettled):
    Wait for all `Promise`s to complete, regardless of success or failure (`AllComplete`)
> - [`Promise.any()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/any):
    Racing mode, immediately return the first successful `Promise` (`AnySuccess`)
> - [`Promise.race()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/race):
    Racing mode, immediately return the first completed `Promise` (`AnyComplete`)
>
> PS: The method naming of JavaScript `Promise` is well-considered~ 👍
>
> `cffu`'s new methods support the concurrent execution approaches of the JavaScript `Promise` specification~

Example code:

```java
public class ConcurrencyStrategyDemo {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    ////////////////////////////////////////////////////////////////////////
    // CffuFactory#allResultsFailFastOf
    // CffuFactory#anySuccessOf
    // CffuFactory#mostSuccessResultsOf
    ////////////////////////////////////////////////////////////////////////
    final Cffu<Integer> success = cffuFactory.supplyAsync(() -> {
      sleep(300); // sleep SHORT time
      return 42;
    });
    final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 4242;
    });
    final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

    MCffu<Integer, List<Integer>> failFast = cffuFactory.allResultsFailFastOf(success, successAfterLongTime, failed);
    // fail fast without waiting successAfterLongTime
    System.out.println(failFast.exceptionNow());
    // output: java.lang.RuntimeException: Bang!

    Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(success, successAfterLongTime, failed);
    System.out.println(anySuccess.get());
    // output: 42

    MCffu<Integer, List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
        -1, 100, TimeUnit.MILLISECONDS, success, successAfterLongTime, failed);
    System.out.println(mostSuccess.get());
    // output: [42, -1, -1]

    ////////////////////////////////////////////////////////////////////////
    // or CompletableFutureUtils#allResultsFailFastOf
    //    CompletableFutureUtils#anySuccessOf
    //    CompletableFutureUtils#mostSuccessResultsOf
    ////////////////////////////////////////////////////////////////////////
    final CompletableFuture<Integer> successCf = CompletableFuture.supplyAsync(() -> {
      sleep(300); // sleep SHORT time
      return 42;
    });
    final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 4242;
    });
    final CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

    CompletableFuture<List<Integer>> failFast2 = allResultsFailFastOf(successCf, successAfterLongTimeCf, failedCf);
    // fail fast without waiting successAfterLongTime
    System.out.println(exceptionNow(failFast2));
    // output: java.lang.RuntimeException: Bang!

    CompletableFuture<Integer> anySuccess2 = anySuccessOf(successCf, successAfterLongTimeCf, failedCf);
    System.out.println(anySuccess2.get());
    // output: 42

    CompletableFuture<List<Integer>> mostSuccess2 = mostSuccessResultsOf(
        -1, 100, TimeUnit.MILLISECONDS, successCf, successAfterLongTime, failed);
    System.out.println(mostSuccess2.get());
    // output: [42, -1, -1]
  }
}
```

> \# Complete runnable demo code can be found at
> [`ConcurrencyStrategyDemo.java`](cffu-core/src/test/java/io/foldright/demo/ConcurrencyStrategyDemo.java).

### 2.4 support for directly running multiple `action`s instead of wrapping them into `Completablefuture`s first

The `allOf/anyOf` methods of `CompletableFuture` take `CompletableFuture` as input;
when business logic directly has methods to orchestrate,
you still need to wrap them into `CompletableFuture` first before running:

- Cumbersome
- Blurs business processes
- Simply wrapping multiple `Action`s into `CF`s and submitting them to `allOf/anyOf`
  (which is how business code often implements this) **will swallow exceptions**❗️
  - When multiple input `Action`s throw exceptions during execution,
    at most one of these exceptions can be fed back to the business through the returned `CF`;
    other exceptions are silently swallowed, affecting business problem troubleshooting

`cffu` provides methods for directly running multiple `Action`s, solving the above problems:

- Convenient and clear expression and orchestration of business processes
- Doesn't swallow exceptions, facilitating business problem troubleshooting
  - When multiple input `Action`s throw exceptions during execution,
    log reports will be printed for exceptions not fed back to the business through the returned `CF`

Example code:

```java
public class MultipleActionsDemo {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  static void mRunAsyncDemo() {
    // wrap actions to CompletableFutures first, AWKWARD! 😖
    CompletableFuture.allOf(
        CompletableFuture.runAsync(() -> System.out.println("task1")),
        CompletableFuture.runAsync(() -> System.out.println("task2")),
        CompletableFuture.runAsync(() -> System.out.println("task3"))
    );
    completedFuture("task").thenCompose(v ->
        CompletableFuture.allOf(
            CompletableFuture.runAsync(() -> System.out.println(v + "1")),
            CompletableFuture.runAsync(() -> System.out.println(v + "2")),
            CompletableFuture.runAsync(() -> System.out.println(v + "3"))
        )
    );

    // just run multiple actions, fresh and cool 😋
    CompletableFutureUtils.mRunAsync(
        () -> System.out.println("task1"),
        () -> System.out.println("task2"),
        () -> System.out.println("task3")
    );
    cffuFactory.completedFuture("task").thenMAcceptAsync(
        (String v) -> System.out.println(v + "1"),
        v -> System.out.println(v + "2"),
        v -> System.out.println(v + "3")
    );
  }
}
```

These multiple-`Action` methods also support "different concurrent execution strategies"
and "returning results of multiple input `CF`s".

Example code:

```java
public class MultipleActionsDemo {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  static void thenMApplyAsyncDemo() {
    // wrap actions to CompletableFutures first, AWKWARD! 😖
    completedFuture(42).thenCompose(v ->
        CompletableFutureUtils.allResultsFailFastOf(
            CompletableFuture.supplyAsync(() -> v + 1),
            CompletableFuture.supplyAsync(() -> v + 2),
            CompletableFuture.supplyAsync(() -> v + 3)
        )
    ).thenAccept(System.out::println);
    // output: [43, 44, 45]
    cffuFactory.completedFuture(42).thenCompose(v ->
        CompletableFutureUtils.allSuccessResultsOf(
            -1,
            CompletableFuture.supplyAsync(() -> v + 1),
            CompletableFuture.supplyAsync(() -> v + 2),
            CompletableFuture.supplyAsync(() -> v + 3)
        )
    ).thenAccept(System.out::println);
    // output: [43, 44, 45]

    // just run multiple actions, fresh and cool 😋
    CompletableFutureUtils.thenMApplyFailFastAsync(
        completedFuture(42),
        v -> v + 1,
        v -> v + 2,
        v -> v + 3
    ).thenAccept(System.out::println);
    // output: [43, 44, 45]
    cffuFactory.completedFuture(42).thenMApplyAllSuccessAsync(
        -1,
        v -> v + 1,
        v -> v + 2,
        v -> v + 3
    ).thenAccept(System.out::println);
    // output: [43, 44, 45]
  }
}
```

> \# Complete runnable demo code can be found at
> [`MultipleActionsDemo.java`](cffu-core/src/test/java/io/foldright/demo/MultipleActionsDemo.java).

### 2.5 Support for async parallel processing of collection data, instead of wrapping data and `Action` into `CompletableFuture`s first

Async parallel processing of multiple data items is a common business requirement,
but implementing it with the `CompletableFuture` API is cumbersome and complex,
also obscures the business logic,` and a simple implementation **may swallow exceptions**❗

`cffu` provides methods for async parallel data processing to solve these problems.

Example code:

```java
public class CfParallelDemo {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  static void parApplyFailFastAsyncDemo() {
    final Function<Integer, Integer> fn = x -> x + 1;
    final List<Integer> list = asList(42, 43, 44);

    // wrap data with action to CompletableFutures first, AWKWARD and COMPLEX! 😖
    CompletableFuture<Integer>[] cfs = new CompletableFuture[list.size()];
    for (int i = 0; i < list.size(); i++) {
      Integer e = list.get(i);
      cfs[i] = CompletableFuture.supplyAsync(() -> fn.apply(e));
    }
    CompletableFutureUtils.allResultsFailFastOf(cfs).thenAccept(System.out::println);
    // output: [43, 44, 45]
    cffuFactory.allResultsFailFastOf(cfs).thenAccept(System.out::println);
    // output: [43, 44, 45]

    // just process multiple data, fresh and cool 😋
    CfParallelUtils.parApplyFailFastAsync(
        asList(42, 43, 44),
        x -> x + 1
    ).thenAccept(System.out::println);
    // output: [43, 44, 45]
    cffuFactory.parOps().parApplyFailFastAsync(
        asList(42, 43, 44),
        x -> x + 1
    ).thenAccept(System.out::println);
    // output: [43, 44, 45]
  }

  static void thenParApplyFailFastAsyncDemo() {
    final Function<Integer, Integer> fn = x -> x + 1;
    final CompletableFuture<List<Integer>> cf = completedFuture(asList(42, 43, 44));

    // wrap data with action to CompletableFutures first, AWKWARD and COMPLEX! 😖
    cf.thenCompose(list -> {
      CompletableFuture<Integer>[] cfs = new CompletableFuture[list.size()];
      for (int i = 0; i < list.size(); i++) {
        Integer e = list.get(i);
        cfs[i] = CompletableFuture.supplyAsync(() -> fn.apply(e));
      }
      return CompletableFutureUtils.allResultsFailFastOf(cfs);
    }).thenAccept(System.out::println);
    // output: [43, 44, 45]
    final MCffu<Integer, List<Integer>> mCffu = cffuFactory.completedMCffu(asList(42, 43, 44));
    mCffu.thenCompose(list -> {
      CompletableFuture<Integer>[] cfs = new CompletableFuture[list.size()];
      for (int i = 0; i < list.size(); i++) {
        Integer e = list.get(i);
        cfs[i] = CompletableFuture.supplyAsync(() -> fn.apply(e));
      }
      return CompletableFutureUtils.allResultsFailFastOf(cfs);
    }).thenAccept(System.out::println);
    // output: [43, 44, 45]

    // just process multiple data, fresh and cool 😋
    CfParallelUtils.thenParApplyFailFastAsync(cf, x -> x + 1)
        .thenAccept(System.out::println);
    // output: [43, 44, 45]
    mCffu.parOps().thenParApplyFailFastAsync(x -> x + 1)
        .thenAccept(System.out::println);
    // output: [43, 44, 45]
  }
}
```

> \# Complete runnable demo code can be found at
> [`CfParallelDemo.java`](cffu-core/src/test/java/io/foldright/demo/CfParallelDemo.java).

### 2.6 support for handling specific exception types instead of all `Throwable` exceptions

In business processing `try-catch` statements, catching all exceptions (`Throwable`) is often not a good practice.
Similarly, the `CompletableFuture#exceptionally` method also handles all exceptions (`Throwable`).

You should only handle specific exceptions that the current business clearly understands
and can recover from, letting outer layers handle other exceptions;
avoid masking bugs or incorrectly handling exceptions that you cannot recover from.

`cffu` provides corresponding [`catching*` methods](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/CompletableFutureUtils.html#catching(F,java.lang.Class,java.util.function.Function))
that support specifying exception types to handle; compared to the `CF#exceptionally` method,
it adds an exception type parameter, with similar usage, so no code example is provided.

### 2.7 backport support for `Java 8`

All new `CF` methods from `Java 9+` higher versions are directly available in `Java 8` lower versions.

Important `backport` features include:

- Timeout control: `orTimeout` / `completeOnTimeout`
- Delayed execution: `delayedExecutor`
- Factory methods: `failedFuture` / `completedStage` / `failedStage`
- Processing operations: `completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`
- Non-blocking reads: `resultNow` / `exceptionNow` / `state`

These `backport` methods are existing functionality of `CompletableFuture`, so no code examples are provided.

### 2.8 timeout-safe new implementation of `orTimeout` / `completeOnTimeout`

The `CF#orTimeout()` / `CF#completeOnTimeout()` methods use
the internal single-threaded `ScheduledThreadPoolExecutor` of `CF` to trigger business logic execution
when timeouts occur, which can break CompletableFuture's timeout and delay functionality❗

Because timeout and delayed execution are basic functionalities, once they fail, it can lead to:

- Business functionality correctness issues, with timeout triggers being inaccurate and delayed
- System stability issues, such as waiting operations in threads not returning,
  other dependent `CF`s not completing, and thread pool exhaustion and memory leaks

The `cffu` library provides timeout-safe new implementation methods:

- [`Cffu#orTimeout()`](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/BaseCffu.html#orTimeout(long,java.util.concurrent.TimeUnit))
  / [`Cffu#completeOnTimeoutTimeout()`](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/BaseCffu.html#completeOnTimeout(java.lang.Object,long,java.util.concurrent.TimeUnit))
- [`CFU#cffuOrTimeout()`](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/CompletableFutureUtils.html#cffuOrTimeout(F,long,java.util.concurrent.TimeUnit))
  / [`CFU#cffuCompleteOnTimeout()`](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/CompletableFutureUtils.html#cffuCompleteOnTimeout(F,T,long,java.util.concurrent.TimeUnit))

Ensuring business logic won't execute in `CF`'s single-threaded `ScheduledThreadPoolExecutor`.

For more information, see:

- Problem demonstration [`DelayDysfunctionDemo.java`](https://github.com/foldright/cffu/blob/2.x-dev/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java)
- `cffu backport` method JavaDoc: [`CFU#orTimeout()`](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/CompletableFutureUtils.html#orTimeout(F,long,java.util.concurrent.TimeUnit))
  / [`CFU#completeOnTimeout()`](https://foldright.io/api-docs/cffu2/2.0.0-Alpha6/io/foldright/cffu2/CompletableFutureUtils.html#completeOnTimeout(F,T,long,java.util.concurrent.TimeUnit))
- Article [Improper Use of `CompletableFuture` Timeout Functionality Causes Production Incidents](https://juejin.cn/post/7411686792342274089)

### 2.9 support for timeout-enabled `join` method

The `cf.join()` method "waits forever without timeout", which is very dangerous in business❗️
When unexpected long waits occur, it can lead to:

- Main business logic blocking, with no opportunity for appropriate handling to respond to users in time
- Consuming a thread, and threads are very limited resources (usually a few hundred);
  exhausting threads means service paralysis and failure

The `join(timeout, unit)` method is a `join` method that supports timeout;
it's like `cf.get(timeout, unit)` compared to `cf.get()`.

This new method is simple and similar to use, so no code example is provided.

### 2.10 `anyOf` method that returns specific types

`CompletableFuture`'s `anyOf()` method returns type `Object`, losing specific types,
making it inconvenient to use return values requiring casting operations, and it's not type-safe.

`cffu`'s `anySuccessOf()` / `anyOf()` methods return specific type `T` instead of returning `Object`.

This method is simple and similar to use, so no code example is provided.

### 2.11 `allOf/anyOf` methods that accept broader input types

`CompletableFuture`'s `allOf()` / `anyOf()` methods take `CompletableFuture` parameter types,
not the broader `CompletionStage` types; for `CompletionStage` type inputs,
you need to call the `CompletionStage#toCompletableFuture` method for conversion.

`cffu`'s `allOf()` / `anyOf()` methods accept broader `CompletionStage` parameter types,
making them more convenient to use.

The method usage is simple and similar, so no code example is provided.

### more feature documentation

You can refer to:

- [`Java API` Documentation](https://foldright.io/api-docs/cffu2/)
- Implementation source code, such as
  - [`Cffu.java`](cffu-core/src/main/java/io/foldright/cffu2/Cffu.java),
    [`MCffu.java`](cffu-core/src/main/java/io/foldright/cffu2/MCffu.java),
    [`BaseCffu.java`](cffu-core/src/main/java/io/foldright/cffu2/BaseCffu.java),
    [`CffuFactory.java`](cffu-core/src/main/java/io/foldright/cffu2/CffuFactory.java)
  - [`CompletableFutureUtils.java`](cffu-core/src/main/java/io/foldright/cffu2/CompletableFutureUtils.java),
    [`CfIterableUtils.java`](cffu-core/src/main/java/io/foldright/cffu2/CfIterableUtils.java),
    [`CfParallelUtils.java`](cffu-core/src/main/java/io/foldright/cffu2/CfParallelUtils.java)

# 🔌 API Docs

- Current version `Java API` documentation: https://foldright.io/api-docs/cffu2/

# 🍪 Dependencies

> You can check the latest available version list at
> [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu2/2.0.0-Alpha6/versions).

- `cffu` library (including enhanced `CompletableFutureUtils` for
  [`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)):
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu2</artifactId>
      <version>2.0.0-Alpha6</version>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation("io.foldright:cffu2:2.0.0-Alpha6")
    ```
    Gradle Groovy DSL
    ```groovy
    implementation 'io.foldright:cffu2:2.0.0-Alpha6'
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local) [`cffu executor wrapper SPI` implementation](cffu-ttl-executor-wrapper):
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu2-ttl-executor-wrapper</artifactId>
      <version>2.0.0-Alpha6</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    runtimeOnly("io.foldright:cffu2-ttl-executor-wrapper:2.0.0-Alpha6")
    ```
    Gradle Groovy DSL
    ```groovy
    runtimeOnly 'io.foldright:cffu2-ttl-executor-wrapper:2.0.0-Alpha6'
    ```
- `cffu bom`:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu2-bom</artifactId>
      <version>2.0.0-Alpha6</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation(platform("io.foldright:cffu2-bom:2.0.0-Alpha6"))
    ```
    Gradle Groovy DSL
    ```groovy
    implementation platform('io.foldright:cffu2-bom:2.0.0-Alpha6')
    ```

# 📚 More Resources

- Official Documentation
  - [`CompletionStage` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletionStage.html)
  - [`CompletableFuture` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
- [`CF/cffu` Juejin Column](https://juejin.cn/column/7413672189316038719) by `cffu` developer [`@linzee1`](https://github.com/linzee1)
- [`CompletableFuture` Guide](docs/completable-future-guide.md)
  - Complete explanation of `CompletableFuture` usage
  - Provides best practice recommendations and usage pitfall warnings
  - For more effective and safe use of `CompletableFuture` in business

# 👋 About the Library Name

`cffu` is short for `CompletableFuture-Fu`; pronounced as `C Fu`, which sounds like `Shifu`.

Yes, it may remind you of the cute raccoon shifu from "Kung Fu Panda"~ 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
