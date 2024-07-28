# <div align="center"><a href="#dummy"><img src="https://github.com/foldright/cffu/assets/1063891/124658cd-025f-471e-8da1-7eea0e482915" alt="🦝 CompletableFuture Fu (CF-Fu)"></a></div>

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/fast_ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/fast_ci.yaml?branch=main&logo=github&logoColor=white&label=fast ci" alt="Github Workflow Build Status"></a>
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=main&logo=github&logoColor=white&label=strong ci" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/cffu/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/cffu/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.6+-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/cffu?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/api-docs/cffu/"><img src="https://img.shields.io/github/release/foldright/cffu?label=javadoc&color=339933&logo=microsoft-academic&logoColor=white" alt="Javadocs"></a>
<a href="https://foldright.io/api-docs/cffu-kotlin/"><img src="https://img.shields.io/github/release/foldright/cffu?label=dokka&color=339933&logo=kotlin&logoColor=white" alt="dokka"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/cffu/releases"><img src="https://img.shields.io/github/release/foldright/cffu.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/cffu/stargazers"><img src="https://img.shields.io/github/stars/foldright/cffu?style=flat" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/cffu/fork"><img src="https://img.shields.io/github/forks/foldright/cffu?style=flat" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/cffu/issues"><img src="https://img.shields.io/github/issues/foldright/cffu" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/graphs/contributors"><img src="https://img.shields.io/github/contributors/foldright/cffu" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/cffu"><img src="https://img.shields.io/github/repo-size/foldright/cffu" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/cffu"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

👉 cffu (CompletableFuture Fu 🦝) is a lightweight enhancement library for [`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html) (CF), 
designed to give you easy-to-use experience, reduce pitfalls, and provide more convenient, efficient, and secure application of CF in business contexts.

Welcome! 👏 💖

Feel free to:
- suggest and ask questions: [Submit issues](https://github.com/foldright/cffu/issues/new).
- contribute and improve: [Forking it and submitting pull request](https://github.com/foldright/cffu/fork).

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="23%" align="right" alt="shifu" /></a>

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🔧 Features](#-%E5%8A%9F%E8%83%BD)
  - [About `CompletableFuture`](#%E5%85%B3%E4%BA%8Ecompletablefuture)
- [👥 User Guide](#-user-guide)
  - [1. 3 ways to use `CFFU`](#1-cffu%E7%9A%84%E4%B8%89%E7%A7%8D%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F)
    - [1) `Cffu`Class](#1-cffu%E7%B1%BB)
    - [2) `CompletableFutureUtils`Utility Class](#2-completablefutureutils%E5%B7%A5%E5%85%B7%E7%B1%BB)
    - [3) `Kotlin`Extension Methods](#3-kotlin%E6%89%A9%E5%B1%95%E6%96%B9%E6%B3%95)
  - [2. `cffu`Features Overview](#2-cffu%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)
    - [2.1 Returning results from multiple CompletableFutures](#21-%E8%BF%94%E5%9B%9E%E5%A4%9A%E4%B8%AA%E8%BF%90%E8%A1%8Ccf%E7%9A%84%E7%BB%93%E6%9E%9C)
    - [2.2 Default business thread pool configuration encapsulation support](#22-%E6%94%AF%E6%8C%81%E8%AE%BE%E7%BD%AE%E7%BC%BA%E7%9C%81%E7%9A%84%E4%B8%9A%E5%8A%A1%E7%BA%BF%E7%A8%8B%E6%B1%A0%E5%B9%B6%E5%B0%81%E8%A3%85%E6%90%BA%E5%B8%A6)
    - [2.3 Efficient and flexible concurrency strategies (AllFastFail/AnySuccess/MostSuccess)（`AllFastFail`/`AnySuccess`/`MostSuccess`）](#23-%E9%AB%98%E6%95%88%E7%81%B5%E6%B4%BB%E7%9A%84%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%AD%96%E7%95%A5allfastfailanysuccessmostsuccess)
    - [2.4 Timeout `join` support](#24-%E6%94%AF%E6%8C%81%E8%B6%85%E6%97%B6%E7%9A%84join%E7%9A%84%E6%96%B9%E6%B3%95)
    - [2.5 `Java 8``Backport` support](#25-backport%E6%94%AF%E6%8C%81java-8)
    - [2.6 Returning specific type by anyOf method](#26-%E8%BF%94%E5%9B%9E%E5%85%B7%E4%BD%93%E7%B1%BB%E5%9E%8B%E7%9A%84anyof%E6%96%B9%E6%B3%95)
    - [More Feature Details](#%E6%9B%B4%E5%A4%9A%E5%8A%9F%E8%83%BD%E8%AF%B4%E6%98%8E)
  - [3. Migration from CompletableFuture to Cffu](#3-%E5%A6%82%E4%BD%95%E4%BB%8E%E7%9B%B4%E6%8E%A5%E4%BD%BF%E7%94%A8completablefuture%E7%B1%BB%E8%BF%81%E7%A7%BB%E5%88%B0cffu%E7%B1%BB)
- [🔌 API Docs](#-api-docs)
- [🍪 Dependencies](#%E4%BE%9D%E8%B5%96)
- [📚 More Resources](#-%E6%9B%B4%E5%A4%9A%E8%B5%84%E6%96%99)
- [👋 About the Library Name](#-%E5%85%B3%E4%BA%8E%E5%BA%93%E5%90%8D)


<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------
🔧 Features


🏪 More convenient functions, such as:
- Supporting the return of multiple CF results instead of void (allOf), e.g., `allResultsFastFailOf`, `allResultsOf`, `mSupplyFastFailAsync`, `thenMApplyFastFailAsync`.
- Supporting the return of multiple different type CF results instead of the same type, e.g., `allTupleFastFailOf`, `allTupleOf`, `tupleMSupplyFastFailAsync`, `thenTupleMApplyFastFailAsync`.
- Supporting direct execution of multiple actions without wrapping them into CompletableFuture, e.g., `tupleMSupplyFastFailAsync`, `mSupplyMostSuccessAsync`, `thenTupleMApplyFastFailAsync`, `thenMRunFastFailAsync`.

⚙️ More efficient and flexible concurrent execution strategies, such as:
- **AllFastFail strategy**: Quickly fails and returns when any input CF fails, rather than waiting for all CFs to complete (allOf).
- **AnySuccess strategy**: Returns the first successful CF result, rather than the first completed (which might be a failure) CF (anyOf).
- **MostSuccess strategy**: Returns the successful results of multiple CFs within a specified time, ignoring failed or incomplete CFs (returns a default value).

🦺 Safer usage, such as:
- 
- Supporting timeout-enabled join methods with `join(timeout, unit)`.
- Safe timeout execution with `cffuOrTimeout`/`cffuCompleteOnTimeout`.
- Peek method that ensures the result won't be modified.
- Forbidding forceful tampering with `CffuFactoryBuilder#forbidObtrudeMethods`.
- Comprehensive code quality annotations on class methods to prompt IDE issues early, such as `@NonNull`, `@Nullable`, `@CheckReturnValue`, `@Contract`, etc.

🧩 Missing basic functions, besides the above safety-oriented new methods (e.g., `join(timeout, unit)`, `cffuOrTimeout`, `peek`), also include:
- Asynchronous exceptional completion with `completeExceptionallyAsync`.
- Non-blocking successful result retrieval with `getSuccessNow`.
- Unwrapping CF exceptions into business exceptions with `unwrapCfException`.

💪 Enhanced existing functionalities, such as:
- `anySuccessOf`/`anyOf` methods: Return specific type T (type-safe) instead of `Object` (`CompletableFuture#anyOf`).

⏳ Backport support for Java 8: All new CF features from Java 9+ are available in Java 8, such as:
- Timeout control with `orTimeout`/`completeOnTimeout`.
- Delayed execution with `delayedExecutor`.
- Factory methods like `failedFuture`, `completedStage`, `failedStage`.
- Handling operations with `completeAsync`, `exceptionallyAsync`, `exceptionallyCompose`, `copy`.

🍩 First-class Kotlin support.

For more details on the usage and features of cffu, refer to the User Guide.

# 🔧 Features

The provided features include：

- ☘️ **Complementary functionalities**
  - 🏪 More convenient functions:
    - return multiple `CF` results instead of `void` (allOf), 
      e.g., `allResultsFastFailOf`, `allResultsOf`, `mSupplyFastFailAsync`, `thenMApplyFastFailAsync`.
    - return multiple different type CF results instead of the same type, 
      e.g.,`allTupleFastFailOf`, `allTupleOf`, `tupleMSupplyFastFailAsync`, `thenTupleMApplyFastFailAsync`.
    - direct execution of multiple actions without wrapping them into CompletableFuture,
      e.g.,`tupleMSupplyFastFailAsync` / `mSupplyMostSuccessAsync` / `thenTupleMApplyFastFailAsync` / `thenMRunFastFailAsync`
  - ⚙️ More effective and flexible concurrent execution strategies:
    - `AllFastFail` strategy: Quickly fails and returns when any input `CF` fails, rather than waiting for all CFs to complete (allOf).
    - `AnySuccess` strategy: Returns the first successful `CF` result, rather than the first completed (which might be a failure) `CF` (anyOf).
    - `MostSuccess` strategy: Returns the successful results of multiple CFs within a specified time, ignoring failed or incomplete CFs (returns a default value).
  - 🦺 **Safer Usage Modes**, such as:
    - **Default Business Thread Pool Configuration and Encapsulation**: Use `CffuFactory#builder(executor)` method.
    - **Timeout-enabled `join` Method**: Use `join(timeout, unit)` method.
    - **Safe Timeout Execution**: Use `cffuOrTimeout`/`cffuCompleteOnTimeout` methods.
    - **Result-immutable Handling**: Use `peek` method, which ensures the result won't be modified.
    - **Forbid Forced Tampering**: Use `CffuFactoryBuilder#forbidObtrudeMethods` method.
    - **Comprehensive Code Quality Annotations**: Methods are annotated to help the IDE catch issues early, e.g., `@NonNull`, `@Nullable`, `@CheckReturnValue`, `@Contract`.

  - 🧩 **Missing Basic Functions**, in addition to the new safety-oriented methods above (e.g., `join(timeout, unit)`, `cffuOrTimeout`, `peek`), also include:
    - **Asynchronous Exceptional Completion**: Use `completeExceptionallyAsync` method.
    - **Non-blocking Successful Result Retrieval**: Use `getSuccessNow` method.
    - **Unwrap CF Exception into Business Exception**: Use `unwrapCfException` method.

  - 💪 **Enhancements to Existing Functions**, such as:
    - **Type-safe `anySuccessOf`/`anyOf` Methods**: Return specific type `T` instead of `Object` (`CompletableFuture#anyOf`).

  - ⏳ **Backport Support for Java 8**: All new CF features from Java 9+ are available in Java 8, such as:
    - **Timeout Control**: Use `orTimeout`/`completeOnTimeout` methods.
    - **Delayed Execution**: Use `delayedExecutor` method.
    - **Factory Methods**: Use `failedFuture`, `completedStage`, `failedStage`.
    - **Handling Operations**: Use `completeAsync`, `exceptionallyAsync`, `exceptionallyCompose`, `copy`.

  - 🍩 **First-class Kotlin Support**

For more details on the usage modes and functionalities of cffu, refer to the [User Guide](#-user-guide).

## About `CompletableFuture`

Managing concurrent execution is complex and error-prone, but there are numerous tools and frameworks available in the industry.

> For a broad understanding of concurrency tools and frameworks, refer to books like "[Seven Concurrency Models in Seven Weeks](https://book.douban.com/subject/26337939/)," "[Java Concurrency in Practice](https://book.douban.com/subject/24533312/)," and "[Programming Scala, Second Edition](https://book.douban.com/subject/35448965/)." More books on concurrency can be found in this [book list](https://www.douban.com/doulist/41916951/).

`CompletableFuture (CF)` has its advantages:

- **Built into the `Java` standard library**
  - No additional dependencies are needed, making it almost always available
  - Trusted to have high implementation quality
- **Widely known and used, with a strong community foundation**
  - Released with `Java 8` in 2014, `CompletableFuture` has been around for 10 years
  - Its parent interface, [`Future`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Future.html), has been available since `Java 5` in 2004, totaling 20 years
  - Although the `Future` interface does not support asynchronous result retrieval and concurrent logic orchestration, it has familiarized many `Java` developers with this concept and tool
- **Powerful yet not overly complex**
  - Adequate for everyday business application development
  - Larger concurrency frameworks like [`Akka`](https://akka.io/) and [`RxJava`](https://github.com/ReactiveX/RxJava) require a deeper understanding
  - The basic concerns and complexities of concurrency remain the same, regardless of the specific tool used
- **High-level abstraction**
  - Allows expressing technical concurrency flows as business processes
  - Avoids cumbersome and error-prone low-level coordination tools such as locks ([`Lock`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/package-summary.html)), [`CountDownLatch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html), semaphores ([`Semaphore`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html)), and [`CyclicBarrier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CyclicBarrier.html)

Like other concurrency tools and frameworks, `CompletableFuture` is used for:

- Concurrent execution of business logic or orchestration of concurrent processes/tasks
- Leveraging multi-core parallel processing
- Enhancing business responsiveness

It is worth a deeper understanding and application. 💕

# 👥 User Guide

## 1. Three Ways to Use `cffu`

`cffu` supports three usage approaches:

- 🦝 1) **Using the `Cffu` Class**
  - Recommended when using the `Java` language in your project.
  - Migration from direct usage of the `CompletableFuture` class to the `Cffu` class involves two changes:
    - Change type declarations from `CompletableFuture` to `Cffu`.
    - Replace static method calls from the `CompletableFuture` class with the `cffuFactory` instance.
    - For more details, see [How to Migrate from Direct Use of the `CompletableFuture` Class to the `Cffu` Class](#3-%E5%A6%82%E4%BD%95%E4%BB%8E%E7%9B%B4%E6%8E%A5%E4%BD%BF%E7%94%A8completablefuture%E7%B1%BB%E8%BF%81%E7%A7%BB%E5%88%B0cffu%E7%B1%BB).
  - Depends on the `io.foldright:cffu` library.
- 🛠️️ 2) **Using the `CompletableFutureUtils` Utility Class**
  - If you prefer not to introduce new classes (`Cffu`) to your project, as it might add complexity:
    - You can use the `cffu` library as a utility class.
    - Optimizing `CompletableFuture` usage with utility methods is common in business projects, and `CompletableFutureUtils` offers a set of practical, reliable, efficient, and safe utility methods.
  - Some features of `cffu` are not available in this approach (and no implementation solution is planned) 😔, such as setting a default business thread pool and preventing forced tampering.
  - Depends on the `io.foldright:cffu` library.
- 🍩 3) **Using `Kotlin` Extension Methods**
  - Recommended for projects using the `Kotlin` language.
  - Requires the `io.foldright:cffu-kotlin` library.

Before diving into the feature points, check out the examples of the different ways to use `cffu`. 🎪

### 1) `Cffu` class

```java
public class CffuDemo {
  private static final ExecutorService myBizThreadPool = Executors.newCachedThreadPool();
  // Create a CffuFactory with configuration of the customized thread pool
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizThreadPool).build();

  public static void main(String[] args) throws Exception {
    final Cffu<Integer> cf42 = cffuFactory
        .supplyAsync(() -> 21)  // Run in myBizThreadPool
        .thenApply(n -> n * 2);

    // Below tasks all run in myBizThreadPool
    final Cffu<Integer> longTaskA = cf42.thenApplyAsync(n -> {
      sleep(1001);
      return n / 2;
    });
    final Cffu<Integer> longTaskB = cf42.thenApplyAsync(n -> {
      sleep(1002);
      return n / 2;
    });
    final Cffu<Integer> longTaskC = cf42.thenApplyAsync(n -> {
      sleep(100);
      return n * 2;
    });
    final Cffu<Integer> longFailedTask = cf42.thenApplyAsync(unused -> {
      sleep(1000);
      throw new RuntimeException("Bang!");
    });

    final Cffu<Integer> combined = longTaskA.thenCombine(longTaskB, Integer::sum)
        .orTimeout(1500, TimeUnit.MILLISECONDS);
    System.out.println("combined result: " + combined.get());

    final Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(longTaskC, longFailedTask);
    System.out.println("any success result: " + anySuccess.get());
  }
}
```

> \# See complete runnable demo code in [`CffuDemo.java`](demos/cffu-demo/src/main/java/io/foldright/demo/cffu/CffuDemo.java)。

### 2) `CompletableFutureUtils` utility class

```java
public class CompletableFutureUtilsDemo {
  private static final ExecutorService myBizThreadPool = Executors.newCachedThreadPool();

  public static void main(String[] args) throws Exception {
    final CompletableFuture<Integer> cf42 = CompletableFuture
        .supplyAsync(() -> 21, myBizThreadPool)  // Run in myBizThreadPool
        .thenApply(n -> n * 2);

    final CompletableFuture<Integer> longTaskA = cf42.thenApplyAsync(n -> {
      sleep(1001);
      return n / 2;
    }, myBizThreadPool);
    final CompletableFuture<Integer> longTaskB = cf42.thenApplyAsync(n -> {
      sleep(1002);
      return n / 2;
    }, myBizThreadPool);
    final CompletableFuture<Integer> longTaskC = cf42.thenApplyAsync(n -> {
      sleep(100);
      return n * 2;
    }, myBizThreadPool);
    final CompletableFuture<Integer> longFailedTask = cf42.thenApplyAsync(unused -> {
      sleep(1000);
      throw new RuntimeException("Bang!");
    }, myBizThreadPool);

    final CompletableFuture<Integer> combined = longTaskA.thenCombine(longTaskB, Integer::sum);
    final CompletableFuture<Integer> combinedWithTimeout =
        orTimeout(combined, 1500, TimeUnit.MILLISECONDS);
    System.out.println("combined result: " + combinedWithTimeout.get());

    final CompletableFuture<Integer> anySuccess = anySuccessOf(longTaskC, longFailedTask);
    System.out.println("any success result: " + anySuccess.get());
  }
}
```

> \# See complete runnable demo code in[`CompletableFutureUtilsDemo.java`](demos/cffu-demo/src/main/java/io/foldright/demo/cffu/CompletableFutureUtilsDemo.java)。

### 3) `Kotlin` extension class

```kt
private val myBizThreadPool: ExecutorService = Executors.newCachedThreadPool()

// Create a CffuFactory with configuration of the customized thread pool
private val cffuFactory: CffuFactory = CffuFactory.builder(myBizThreadPool).build()

fun main() {
  val cf42 = cffuFactory
    .supplyAsync { 21 }   // Run in myBizThreadPool
    .thenApply { it * 2 }

  // Below tasks all run in myBizThreadPool
  val longTaskA = cf42.thenApplyAsync { n: Int ->
    sleep(1001)
    n / 2
  }
  val longTaskB = cf42.thenApplyAsync { n: Int ->
    sleep(1002)
    n / 2
  }
  val longTaskC = cf42.thenApplyAsync { n: Int ->
    sleep(100)
    n * 2
  }
  val longFailedTask = cf42.thenApplyAsync<Int> { _ ->
    sleep(1000)
    throw RuntimeException("Bang!")
  }

  val combined = longTaskA.thenCombine(longTaskB, Integer::sum)
    .orTimeout(1500, TimeUnit.MILLISECONDS)
  println("combined result: ${combined.get()}")

  val anySuccess: Cffu<Int> = listOf(longTaskC, longFailedTask).anySuccessOfCffu()
  println("any success result: ${anySuccess.get()}")
}
```

> \# See complete runnable demo code in[`CffuDemo.kt`](demos/cffu-kotlin-demo/src/main/java/io/foldright/demo/cffu/kotlin/CffuDemo.kt)。

## 2. `cffu` Functionality Introduction

### 2.1 Returning Results from Multiple Running `CF`

The `allOf` method in `CompletableFuture` does not return results directly; it returns `Void`, making it inconvenient to retrieve results from multiple running `CF`:

- You need to perform additional read operations (like `join`/`get`) on the input `CF` after `allOf`.
  - This approach is cumbersome.
  - Read methods (like `join`/`get`) are blocking, increasing the risk of deadlock in business logic. ❗️
    For more details, refer to [Principles and Practices of CompletableFuture - 4.2.2 ThreadPool Circular Reference Leading to Deadlock](https://juejin.cn/post/7098727514725416967#heading-24)
- Alternatively, you can pass an `Action` and set external variables within it, but this requires careful consideration of thread safety ⚠️.
  - Managing concurrent data transfer in multi-threaded scenarios is complex, and mishandling concurrent logic in data read/write operations is a common issue in business code. ❗️

`cffu` provides `allResultsFastFailOf`/`allResultsOf` methods to facilitate retrieving results from multiple `CF`:

- Convenient and direct retrieval of overall results using library functions.
- Mitigates complex thread safety issues and logical errors associated with multi-threaded read/write operations.
- Returns `CF` with overall results (which can be further chained with non-blocking operations), naturally reducing reliance on blocking read methods like `join`/`get` and minimizing the risk of deadlock.

Example code:

```java
public class AllResultsOfDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    //////////////////////////////////////////////////
    // CffuFactory#allResultsOf
    //////////////////////////////////////////////////
    Cffu<Integer> cffu1 = cffuFactory.completedFuture(21);
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Void> all = cffuFactory.allOf(cffu1, cffu2);
    // Result type is Void!
    //
    // the result can be got by input argument `cf1.get()`, but it's cumbersome.
    // so we can see a lot of util methods to enhance `allOf` with result in our project.

    Cffu<List<Integer>> allResults = cffuFactory.allResultsOf(cffu1, cffu2);
    System.out.println(allResults.get());

    //////////////////////////////////////////////////
    // or CompletableFutureUtils#allResultsOf
    //////////////////////////////////////////////////
    CompletableFuture<Integer> cf1 = CompletableFuture.completedFuture(21);
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Void> all2 = CompletableFuture.allOf(cf1, cf2);
    // Result type is Void!

    CompletableFuture<List<Integer>> allResults2 = allResultsOf(cf1, cf2);
    System.out.println(allResults2.get());
  }
}
```

> \# See complete runnable demo code in[`AllResultsOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllResultsOfDemo.java)。

### 2.1.1 Returning Results from Multiple Different Types of `CF`

In addition to handling multiple `CF` instances with the same result type, `cffu` also offers methods to handle multiple `CF` instances with different result types using `allTupleFastFailOf`/`allTupleOf` methods.

Example code:

```java
public class AllTupleOfDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    //////////////////////////////////////////////////
    // allTupleFastFailOf / allTupleOf
    //////////////////////////////////////////////////
    Cffu<String> cffu1 = cffuFactory.completedFuture("21");
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Tuple2<String, Integer>> allTuple = cffuFactory.allTupleFastFailOf(cffu1, cffu2);
    System.out.println(allTuple.get());

    //////////////////////////////////////////////////
    // or CompletableFutureUtils.allTupleFastFailOf / allTupleOf
    //////////////////////////////////////////////////
    CompletableFuture<String> cf1 = CompletableFuture.completedFuture("21");
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Tuple2<String, Integer>> allTuple2 = allTupleFastFailOf(cf1, cf2);
    System.out.println(allTuple2.get());
  }
}
```

> \# See complete runnable demo code in[`AllTupleOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllTupleOfDemo.java)。

### 2.2 Supporting Default Business Thread Pool Setting and Encapsulation

- By default, `CompletableFuture` executes its `*Async` methods using the `ForkJoinPool.commonPool()`.
- This thread pool typically has a number of threads equal to the number of CPUs, making it suitable for CPU-intensive tasks. However, business logic often involves many waiting operations (such as network IO and blocking waits), which are not CPU-intensive.
- Using the default thread pool `ForkJoinPool.commonPool()` for business logic is risky❗

As a result,

- In business logic, calling `CompletableFuture`'s `*Async` methods often requires repeatedly passing a specific business thread pool, making the use of `CompletableFuture` cumbersome and error-prone 🤯.
- In underlying logic, when callbacks to business operations occur (such as RPC callbacks), it is neither suitable nor convenient to provide a thread pool for the business. In such cases, using the thread pool encapsulated by `Cffu` is convenient, reasonable, and safe.  
  For more on this use case, see [Principles and Practices of CompletableFuture - 4.2.3 Asynchronous RPC Calls Should Avoid Blocking IO Thread Pools](https://juejin.cn/post/7098727514725416967#heading-25).

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

> \# See complete runnable demo code in[`NoDefaultExecutorSettingForCompletableFuture.java`](cffu-core/src/test/java/io/foldright/demo/NoDefaultExecutorSettingForCompletableFuture.java)。

`Cffu` supports setting a default business thread pool, avoiding the aforementioned complexities and risks.

Example code:

```java
public class DefaultExecutorSettingForCffu {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

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

> \# See complete runnable demo code in[`DefaultExecutorSettingForCffu.java`](cffu-core/src/test/java/io/foldright/demo/DefaultExecutorSettingForCffu.java)。

### 2.3 Efficient and Flexible Concurrent Execution Strategies (`AllFastFail`/`AnySuccess`/`MostSuccess`)

- The `allOf` method in `CompletableFuture` waits for all input `CF` to complete; even if one `CF` fails, it waits for the remaining `CF` to complete before returning a failed `CF`.
  - For business logic, this fail-and-continue-waiting strategy slows down responsiveness; it's preferable to fail fast if any input `CF` fails, avoiding useless waiting.
  - `cffu` provides corresponding methods like `allResultsFastFailOf`.
  - Both `allOf` and `allResultsFastFailOf` return a successful result only when all input `CF` succeed.
- The `anyOf` method in `CompletableFuture` returns the first completed `CF` (without waiting for the others to complete, a "race" mode); even if the first completed `CF` is a failure, it returns this failed `CF` result.
  - For business logic, it's preferable for the race mode to return the first successful `CF` result, rather than the first completed but failed `CF`.
  - `cffu` provides corresponding methods like `anySuccessOf`.
  - `anySuccessOf` returns a failed result only if all input `CF` fail.
- Returning successful results from multiple `CF` within a specified time, ignoring failed or still-running `CF` (returning a specified default value).
  - For eventual business consistency, return whatever is available; results from still-running `CF` can be written to distributed cache to avoid recalculation, making them available next time.
  - This is a common business use case, and `cffu` provides corresponding methods like `mostSuccessResultsOf`.

> 📔 For more on concurrent execution strategies of multiple `CF`, see the JavaScript specification for [`Promise Concurrency`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise#promise_concurrency); in JavaScript, `Promise` corresponds to `CompletableFuture`.
>
> JavaScript Promise provides four concurrent execution methods:
>
> - [`Promise.all()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all): Waits for all `Promise` to succeed; if any one fails, it immediately returns a failure (corresponding to `cffu`'s `allResultsFastFailOf` method).
> - [`Promise.allSettled()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/allSettled): Waits for all `Promise` to complete, regardless of success or failure (corresponding to `cffu`'s `allResultsOf` method).
> - [`Promise.any()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/any): Race mode, immediately returns the first successful `Promise` (corresponding to `cffu`'s `anySuccessOf` method).
> - [`Promise.race()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/race): Race mode, immediately returns the first completed `Promise` (corresponding to `cffu`'s `anyOf` method).
>
> PS: The naming of JavaScript Promise methods is really well thought out～ 👍
>
> With the addition of two new methods, `cffu` aligns with the concurrent methods in the JavaScript Promise specification～ 👏

Example code:

```java
public class ConcurrencyStrategyDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    ////////////////////////////////////////////////////////////////////////
    // CffuFactory#allResultsFastFailOf
    // CffuFactory#anySuccessOf
    // CffuFactory#mostSuccessResultsOf
    ////////////////////////////////////////////////////////////////////////
    final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 42;
    });
    final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

    Cffu<List<Integer>> fastFailed = cffuFactory.allResultsFastFailOf(successAfterLongTime, failed);
    // fast failed without waiting successAfterLongTime
    System.out.println(fastFailed.exceptionNow());

    Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(successAfterLongTime, failed);
    System.out.println(anySuccess.get());

    Cffu<List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
        0, 100, TimeUnit.MILLISECONDS, successAfterLongTime, failed);
    System.out.println(mostSuccess.get());

    ////////////////////////////////////////////////////////////////////////
    // or CompletableFutureUtils#allResultsFastFailOf
    //    CompletableFutureUtils#anySuccessOf
    //    CompletableFutureUtils#mostSuccessResultsOf
    ////////////////////////////////////////////////////////////////////////
    final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 42;
    });
    final CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

    CompletableFuture<List<Integer>> fastFailed2 = allResultsFastFailOf(successAfterLongTimeCf, failedCf);
    // fast failed without waiting successAfterLongTime
    System.out.println(exceptionNow(fastFailed2));

    CompletableFuture<Integer> anySuccess2 = anySuccessOf(successAfterLongTimeCf, failedCf);
    System.out.println(anySuccess2.get());

    CompletableFuture<List<Integer>> mostSuccess2 = mostSuccessResultsOf(
        0, 100, TimeUnit.MILLISECONDS, successAfterLongTime, failed);
    System.out.println(mostSuccess2.get());
  }
}
```

> \# See complete runnable demo code in[`ConcurrencyStrategyDemo.java`](cffu-core/src/test/java/io/foldright/demo/ConcurrencyStrategyDemo.java)。

### 2.4 Support for Timed `join` Methods

`cf.join()` waits indefinitely, which is very dangerous in business applications❗️ When unexpectedly long waits occur, it can lead to:

- Blocking the main business logic, preventing timely user responses.
- Consuming a thread, which is a very limited resource (usually only a few hundred), and exhausting threads can cause service outages.

The `join(timeout, unit)` method supports a timed `join`, similar to how `cf.get(timeout, unit)` complements `cf.get()`.

This new method is simple to use and does not require a code example.

### 2.5 `Backport` Support for `Java 8`

All new `CF` features from higher versions (`Java 9+`) are directly available in the lower version `Java 8`.

Key backport features include:

- Timeout control: `orTimeout` / `completeOnTimeout` methods
- Delayed execution: `delayedExecutor` method
- Factory methods: `failedFuture` / `completedStage` / `failedStage`
- Handling operations: `completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`

These backported methods are existing functionalities of `CompletableFuture` and do not require code examples.

### 2.6 Return Type-Specific `anyOf` Method

The `CompletableFuture.anyOf` method returns a type of `Object`, losing the specific type and type safety, making it inconvenient to use due to required casting.

The `cffu` provides `anySuccessOf` / `anyOf` methods that return a specific type `T` instead of returning `Object`.

This new method is simple to use and does not require a code example.

### More Function Descriptions

For more information, refer to:

- `API` Documentation
  - [`Java API` Documentation](https://foldright.io/api-docs/cffu/)
  - [`Kotlin API` Documentation](https://foldright.io/api-docs/cffu-kotlin/)
- Source Code
  - `cffu`: [`Cffu.java`](cffu-core/src/main/java/io/foldright/cffu/Cffu.java), [`CffuFactory.java`](cffu-core/src/main/java/io/foldright/cffu/CffuFactory.java)
  - `CompletableFuture utils`: [`CompletableFutureUtils.java`](cffu-core/src/main/java/io/foldright/cffu/CompletableFutureUtils.java)
  - `Kotlin extensions`: [`CffuExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/CffuExtensions.kt), [`CompletableFutureExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/CompletableFutureExtensions.kt)

## 3. How to Migrate from Direct Use of `CompletableFuture` to `Cffu`

To utilize `cffu`'s enhanced features, you can migrate existing code that directly uses the `CompletableFuture` class to the `Cffu` class with two modifications:

- Change the type declarations from the `CompletableFuture` class to the `Cffu` class.
- Replace static method calls from `CompletableFuture` with `cffuFactory` instance calls.

This migration is possible because:

- All instance methods of the `CompletableFuture` class are present in the `Cffu` class with the same method signatures and functionalities.
- All static methods of the `CompletableFuture` class are present in the `CffuFactory` class with the same method signatures and functionalities.

# 🔌 API Docs

- Current version of `Java API` documentation: https://foldright.io/api-docs/cffu/
- Current version of `Kotlin API` documentation: https://foldright.io/api-docs/cffu-kotlin/
Example code:

# 🍪Dependencies

> check out [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions) for new or available versions。

- `cffu`library（including[`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html) enhanced `CompletableFutureUtils`）:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu</artifactId>
      <version>1.0.0-Alpha15</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu:1.0.0-Alpha15")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu:1.0.0-Alpha15'
    ```
- `cffu Kotlin` support library:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-kotlin</artifactId>
      <version>1.0.0-Alpha15</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu-kotlin:1.0.0-Alpha15")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu-kotlin:1.0.0-Alpha15'
    ```
- `cffu bom`:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-bom</artifactId>
      <version>1.0.0-Alpha15</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation(platform("io.foldright:cffu-bom:1.0.0-Alpha15"))
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation platform('io.foldright:cffu-bom:1.0.0-Alpha15')
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local) implementation for [`cffu executor wrapper SPI`](cffu-ttl-executor-wrapper)：
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-ttl-executor-wrapper</artifactId>
      <version>1.0.0-Alpha15</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:1.0.0-Alpha15")
    ```
    ```groovy
    // Gradle Groovy DSL
    runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:1.0.0-Alpha15'
    ```

# 📚 More resources

### Official Documentation

- [`CompletionStage` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletionStage.html)
- [`CompletableFuture` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)

### [`CompletableFuture` Guide](docs/completable-future-guide.md)

- A comprehensive guide to using `CompletableFuture`
- Offers best practices and pitfalls to avoid
- Provides strategies for effectively and safely integrating `CompletableFuture` into business applications

# 👋 About the Library Name

`cffu` is short for `CompletableFuture-Fu`, pronounced as "C Fu", which sounds like "Shifu" in Chinese, reminiscent of the beloved raccoon master from "Kung Fu Panda" 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
