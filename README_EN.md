# <div align="center"><a href="#dummy"><img src="https://github.com/foldright/cffu/assets/1063891/124658cd-025f-471e-8da1-7eea0e482915" alt="🦝 CompletableFuture Fu (CF-Fu)"></a></div>

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/fast_ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/fast_ci.yaml?branch=main&logo=github&logoColor=white&label=fast ci" alt="Fast CI - GH Workflow Build Status"></a>
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=main&logo=github&logoColor=white&label=strong ci" alt="Strong CI - GH Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/cffu/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/cffu/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://qodana.cloud/projects/A61Yy"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/qodana_code_quality.yml?branch=main&logo=jetbrains&logoColor=white&label=qodana" alt="Qodana - GH Workflow Build Status"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/cffu?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/api-docs/cffu/"><img src="https://img.shields.io/github/release/foldright/cffu?label=javadoc&color=339933&logo=read-the-docs&logoColor=white" alt="Javadocs"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu/1.0.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/cffu/releases"><img src="https://img.shields.io/github/release/foldright/cffu.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/cffu/stargazers"><img src="https://img.shields.io/github/stars/foldright/cffu?style=flat" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/cffu/fork"><img src="https://img.shields.io/github/forks/foldright/cffu?style=flat" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/cffu/issues"><img src="https://img.shields.io/github/issues/foldright/cffu" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/graphs/contributors"><img src="https://img.shields.io/github/contributors/foldright/cffu" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/cffu"><img src="https://img.shields.io/github/repo-size/foldright/cffu" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/cffu"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

👉 cffu (CompletableFuture Fu 🦝) is a lightweight enhancement library for [`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html) (CF), 
designed to give you quick and easy development experience with less pitfalls, and to provide more convenient, efficient, and safe application of CF in business contexts.

Welcome! 👏 💖

Feel free to:
- suggest and ask questions: [Submit issues](https://github.com/foldright/cffu/issues/new).
- contribute and improve: [Forking it and submitting pull request](https://github.com/foldright/cffu/fork).

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="23%" align="right" alt="shifu" /></a>

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🔧 Features](#-features)
  - [About `CompletableFuture`](#about-completablefuture)
- [👥 User Guide](#-user-guide)
  - [1. Three ways to use `cffu`](#1-three-ways-to-use-cffu)
    - [1) `Cffu` class](#1-cffu-class)
    - [2) `CompletableFutureUtils` utility class](#2-completablefutureutils-utility-class)
    - [3) `Kotlin` extension class](#3-kotlin-extension-class)
  - [2. `cffu` Functionality Introduction](#2-cffu-functionality-introduction)
    - [2.1 Return results from multiple Running `CF`](#21-return-results-from-multiple-running-cf)
    - [2.1.1 Returning Results from Multiple Different Types of `CF`](#211-returning-results-from-multiple-different-types-of-cf)
    - [2.2 Default business-related thread pool encapsulation](#22-default-business-related-thread-pool-encapsulation)
    - [2.3 Efficient and Flexible Concurrent Execution Strategies (`AllFailFast`/`AnySuccess`/`MostSuccess`)](#23-efficient-and-flexible-concurrent-execution-strategies-allfailfastanysuccessmostsuccess)
    - [2.4 Support for Timed `join` Methods](#24-support-for-timed-join-methods)
    - [2.5 `Java 8``Backport` support](#25-java-8backport-support)
    - [2.6 Return Type-Specific `anyOf` Method](#26-return-type-specific-anyof-method)
    - [More Function Descriptions](#more-function-descriptions)
  - [3. How to Migrate from Direct Use of `CompletableFuture` to `Cffu`](#3-how-to-migrate-from-direct-use-of-completablefuture-to-cffu)
- [🔌 API Docs](#-api-docs)
- [🍪Dependencies](#dependencies)
- [📚 See also](#-see-also)
    - [Official Documentation](#official-documentation)
    - [`CompletableFuture` Guide](#completablefuture-guide)
- [👋 About the Library Name](#-about-the-library-name)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------
🔧 Features




For more details on the usage and features of cffu, refer to the User Guide.

# 🔧 Features

The provided features include：

🏪 More convenient methods, such as:
- Return multiple CF results instead of void (allOf), e.g., `allResultsFailFastOf`, `allResultsOf`, `mSupplyFailFastAsync`, `thenMApplyFailFastAsync`.
- Return multiple different type CF results instead of the same type, e.g., `allTupleFailFastOf`, `allTupleOf`, `mSupplyTupleFailFastAsync`, `thenMApplyTupleFailFastAsync`.
- Direct execution of multiple actions without wrapping them into CompletableFuture, e.g., `mSupplyTupleFailFastAsync`, `mSupplyMostSuccessAsync`, `thenMApplyTupleFailFastAsync`, `thenMRunFailFastAsync`.

⚙️ More efficient and flexible concurrent execution strategies, such as:
- **AllFailFast strategy**: Fail fast and return when any input CF fails, rather than waiting for all CFs to complete (allOf).
- **AnySuccess strategy**: Returns the first successful CF result, rather than the first completed (which might be a failure) (anyOf).
- **MostSuccess strategy**: Returns the successful results of multiple CFs within a specified time, ignoring failed or incomplete CFs (returns a default value).

🦺 Safer usage, such as:
- Timeout-enabled join methods with `join(timeout, unit)`.
- Safe timeout execution with `cffuOrTimeout`/`cffuCompleteOnTimeout`.
- Peek method that ensures the result won't be modified.
- Forbidding forceful write with `CffuFactoryBuilder#forbidObtrudeMethods`.
- Comprehensive code quality annotations on class methods to prompt IDE issues early, such as `@NonNull`, `@Nullable`, `@CheckReturnValue`, `@Contract`, etc.

🧩 New methods not provided by Java CF (e.g., `join(timeout, unit)`, `cffuOrTimeout`, `peek`), such as:
- Asynchronous exceptional completion with `completeExceptionallyAsync`.
- Non-blocking successful result retrieval with `getSuccessNow`.
- Unwrapping CF wrapped exception into business exception with `unwrapCfException`.

💪 Enhanced existing methods, such as:
- `anySuccessOf`/`anyOf` methods: Return specific type T (type-safe) instead of `Object` (`CompletableFuture#anyOf`).

⏳ Backport support for Java 8: All new CF features from Java 9+ are available in Java 8, such as:
- Timeout control with `orTimeout`/`completeOnTimeout`.
- Delayed execution with `delayedExecutor`.
- Factory methods like `failedFuture`, `completedStage`, `failedStage`.
- Handling operations with `completeAsync`, `exceptionallyAsync`, `exceptionallyCompose`, `copy`.

🍩 Support Kotlin as first-class citizen.

For more details on the usage modes and functionalities of cffu, refer to the [User Guide](#-user-guide).

## About `CompletableFuture`

Managing concurrent execution is complex and error-prone, but there are numerous tools and frameworks available in the industry.

> For a comprehensive understanding of concurrency tools and frameworks, refer to books like "[Seven Concurrency Models in Seven Weeks](https://book.douban.com/subject/26337939/)," "[Java Concurrency in Practice](https://book.douban.com/subject/24533312/)," and "[Programming Scala, Second Edition](https://book.douban.com/subject/35448965/)."
> More books on concurrency can be found in this [book list](https://www.douban.com/doulist/41916951/).

`CompletableFuture (CF)` has its advantages:

- **Built into the `Java` standard library**
  - No additional dependencies are needed, making it almost always available
  - Trusted to have high code quality
- **Widely known and used with a strong community foundation**
  - Released with `Java 8` in 2014, `CompletableFuture` has been around for 10 years
  - Its parent interface, [`Future`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Future.html), has been available since `Java 5` in 2004, totally 20 years
  - Although the `Future` interface does not support asynchronous result retrieval and concurrent task orchestration, it has made this concept and tool well-known among many `Java` developers.
- **Powerful yet not overly complex**
  - Adequate for everyday business application development
  - Larger concurrency frameworks like [`Akka`](https://akka.io/) and [`RxJava`](https://github.com/ReactiveX/RxJava) require a deeper understanding
  - The basic concerns and complexities of concurrency remain the same, regardless of the specific tool used
- **High-level abstraction**
  - Allow expressing technical concurrency flows as business processes
  - Avoids cumbersome and error-prone low-level coordination tools such as locks ([`Lock`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/package-summary.html)), [`CountDownLatch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html), semaphores ([`Semaphore`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html)), and [`CyclicBarrier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CyclicBarrier.html)

Like other concurrency tools and frameworks, `CompletableFuture` is used for:

- Concurrent execution of business logic or orchestration of concurrent processes/tasks
- Leveraging multi-core parallel processing
- Enhancing business responsiveness

A deeper understanding is essential before applying it to real-world scenarios. 💕

# 👥 User Guide

## 1. Three ways to use `cffu`

`cffu` supports three ways to use:

- 🦝 1) **Using the `Cffu` Class**
  - Recommended when using `Java`  in your project.
  - Migration from direct use of `CompletableFuture` to `Cffu` involves two changes:
    - Change type declarations from `CompletableFuture` to `Cffu`.
    - Replace static method calls from `CompletableFuture` class with the `cffuFactory` instance.
    - For more details, see [How to Migrate from Direct Use of the `CompletableFuture` Class to the `Cffu` Class](#3-%E5%A6%82%E4%BD%95%E4%BB%8E%E7%9B%B4%E6%8E%A5%E4%BD%BF%E7%94%A8completablefuture%E7%B1%BB%E8%BF%81%E7%A7%BB%E5%88%B0cffu%E7%B1%BB).
  - Depends on the `io.foldright:cffu` library.
- 🛠️️ 2) **Using the `CompletableFutureUtils` utility Class**
  - If you prefer not to introduce new classes (`Cffu`) to your project, as it might add some complexity:
    - You can use `cffu` library as a utility class.
    - Optimizing `CompletableFuture` usage with utility methods is common in business projects, and `CompletableFutureUtils` offers a set of practical, reliable, efficient, and safe utility methods.
  - Some features of `cffu` are not available in this approach (and no implementation solution is planned) 😔, such as setting a default business thread pool and preventing forced write.
  - Depends on the `io.foldright:cffu` library.
- 🍩 3) **Using `Kotlin` Extension Methods**
  - Recommended for projects using `Kotlin`.
  - Requires the `io.foldright:cffu-kotlin` library.

Before diving into the feature points, check out the examples of the different ways to use `cffu`. 🎪

### 1) `Cffu` class

```java
public class CffuDemo {
  private static final ExecutorService myBizThreadPool = Executors.newCachedThreadPool();
  // create a CffuFactory with configuration of the customized thread pool
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizThreadPool).build();

  public static void main(String[] args) throws Exception {
    final Cffu<Integer> cf42 = cffuFactory
        .supplyAsync(() -> 21)  // run in myBizThreadPool
        .thenApply(n -> n * 2);

    // below tasks all run in myBizThreadPool
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
        .supplyAsync(() -> 21, myBizThreadPool)  // run in myBizThreadPool
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

// create a CffuFactory with configuration of the customized thread pool
private val cffuFactory: CffuFactory = CffuFactory.builder(myBizThreadPool).build()

fun main() {
  val cf42 = cffuFactory
    .supplyAsync { 21 }   // run in myBizThreadPool
    .thenApply { it * 2 }

  // below tasks all run in myBizThreadPool
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

### 2.1 Return results from multiple Running `CF`

The `allOf` method in `CompletableFuture` does not return results directly; it returns `Void`, making it inconvenient to retrieve results from multiple running `CF`:

- You need to perform additional read operations (like `join`/`get`) on the input `CF` after `allOf`.
  - This approach is cumbersome.
  - Read methods (like `join`/`get`) are blocking, increasing the risk of deadlock in business logic. ❗️
    For more details, refer to [Principles and Practices of CompletableFuture - 4.2.2 ThreadPool Circular Reference Leading to Deadlock](https://juejin.cn/post/7098727514725416967#heading-24)
- Alternatively, you can pass an `Action` and set external variables within it, but this requires careful consideration of thread safety ⚠️.
  - It is complex to manage concurrent data transfers across multiple threads, and it is common to mishandle the logic for reading and writing data concurrently in business code.❗️

`cffu` provides `allResultsFailFastOf`/`allResultsOf` methods to facilitate retrieving results from multiple `CF`:

- Convenient and direct retrieval of results using library functions.
- Reduces the complexity of thread safety and logical errors that come with managing read/write operations across multiple threads.
- Returns a CF with combined results, allowing further chaining with non-blocking operations. This naturally reduces the need for blocking methods like join or get and lowers the risk of deadlocks.

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
    // result type is Void!
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
    // result type is Void!

    CompletableFuture<List<Integer>> allResults2 = allResultsOf(cf1, cf2);
    System.out.println(allResults2.get());
  }
}
```

> \# See complete runnable demo code in[`AllResultsOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllResultsOfDemo.java)。

### 2.1.1 Returning Results from Multiple Different Types of `CF`

In addition to handling multiple `CF` instances with the same result type, `cffu` also offers methods to handle multiple `CF` instances with different result types using `allTupleFailFastOf`/`allTupleOf` methods.

Example code:

```java
public class AllTupleOfDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    //////////////////////////////////////////////////
    // allTupleFailFastOf / allTupleOf
    //////////////////////////////////////////////////
    Cffu<String> cffu1 = cffuFactory.completedFuture("21");
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Tuple2<String, Integer>> allTuple = cffuFactory.allTupleFailFastOf(cffu1, cffu2);
    System.out.println(allTuple.get());

    //////////////////////////////////////////////////
    // or CompletableFutureUtils.allTupleFailFastOf / allTupleOf
    //////////////////////////////////////////////////
    CompletableFuture<String> cf1 = CompletableFuture.completedFuture("21");
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Tuple2<String, Integer>> allTuple2 = allTupleFailFastOf(cf1, cf2);
    System.out.println(allTuple2.get());
  }
}
```

> \# See complete runnable demo code in[`AllTupleOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllTupleOfDemo.java)。

### 2.2 Default business-related thread pool encapsulation

- By default, `CompletableFuture` executes its `*Async` methods using the `ForkJoinPool.commonPool()`.
- This thread pool typically has a number of threads equal to the number of CPUs, making it suitable for CPU-intensive tasks. However, business logic often involves many waiting operations (such as network IO and blocking waits), which are not CPU-intensive.
- Using the default thread pool `ForkJoinPool.commonPool()` for business logic is risky❗

As a result,

- In business logic, calling `CompletableFuture`'s `*Async` methods often requires repeatedly passing a specific business thread pool, making the use of `CompletableFuture` cumbersome and error-prone 🤯.
- In underlying logic, when callbacks to business operations occur (such as RPC callbacks), it is neither suitable nor convenient to provide a thread pool for the business. In such cases, using the thread pool encapsulated by `Cffu` is convenient, reasonable, and safe.  
  For more on use case, see [Principles and Practices of CompletableFuture - 4.2.3 Asynchronous RPC Calls Should Avoid Blocking IO Thread Pools](https://juejin.cn/post/7098727514725416967#heading-25).

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

`Cffu` supports setting a default business thread pool, avoiding the aforementioned complexities and pitfalls.

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

### 2.3 Efficient and Flexible Concurrent Execution Strategies (`AllFailFast`/`AnySuccess`/`MostSuccess`)

- The `allOf` method in `CompletableFuture` waits for all input `CF` to complete; even if one `CF` fails, it waits for the remaining `CF` to complete before returning a failed `CF`.
  - For business logic, a fail-and-continue-waiting strategy slows down responsiveness. It's better to fail fast if any input CF fails, so you can avoid unnecessary waiting.
  - `cffu` provides corresponding methods like `allResultsFailFastOf`.
  - Both `allOf` and `allResultsFailFastOf` return a successful result only when all input `CF` succeed.
- The `anyOf` method in `CompletableFuture` returns the first completed `CF` (without waiting for the others to complete, a "race" mode); even if the first completed `CF` is a failure, it returns this failed `CF` result.
  - For business logic, it's preferable for the race mode to return the first successful `CF` result, rather than the first completed but failed `CF`.
  - `cffu` provides corresponding methods like `anySuccessOf`.
  - `anySuccessOf` returns a failed result only if all input `CF` fail.
- Return successful results from multiple `CF` within a specified time, ignoring failed or running `CF` (returning a specified default value).
  - To maintain eventual business consistency, return whatever results are available. You can store results from running `CF` in a distributed cache to avoid recalculation and make them accessible for future use.
  - This is a common business use case. `cffu` provides corresponding methods like `mostSuccessResultsOf`.

> 📔 For more on concurrent execution strategies of multiple `CF`, see the JavaScript specification for [`Promise Concurrency`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise#promise_concurrency); in JavaScript, `Promise` corresponds to `CompletableFuture`.
>
> JavaScript Promise provides four concurrent execution methods:
>
> - [`Promise.all()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all): Waits for all `Promise` to succeed; if any one fails, it immediately returns a failure (corresponding to `cffu`'s `allResultsFailFastOf` method).
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
    // CffuFactory#allResultsFailFastOf
    // CffuFactory#anySuccessOf
    // CffuFactory#mostSuccessResultsOf
    ////////////////////////////////////////////////////////////////////////
    final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 42;
    });
    final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

    Cffu<List<Integer>> failFast = cffuFactory.allResultsFailFastOf(successAfterLongTime, failed);
    // fail fast without waiting successAfterLongTime
    System.out.println(failFast.exceptionNow());

    Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(successAfterLongTime, failed);
    System.out.println(anySuccess.get());

    Cffu<List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
        0, 100, TimeUnit.MILLISECONDS, successAfterLongTime, failed);
    System.out.println(mostSuccess.get());

    ////////////////////////////////////////////////////////////////////////
    // or CompletableFutureUtils#allResultsFailFastOf
    //    CompletableFutureUtils#anySuccessOf
    //    CompletableFutureUtils#mostSuccessResultsOf
    ////////////////////////////////////////////////////////////////////////
    final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 42;
    });
    final CompletableFuture<Integer> failedCf = failedFuture(new RuntimeException("Bang!"));

    CompletableFuture<List<Integer>> failFast2 = allResultsFailFastOf(successAfterLongTimeCf, failedCf);
    // fail fast without waiting successAfterLongTime
    System.out.println(exceptionNow(failFast2));

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

`cf.join()` waits indefinitely, which is very dangerous in business applications❗️ When unexpectedly long waits occur, they can:

- Block the main business logic, delaying user responses.
- Tie up threads, which are a limited resource (usually only a few hundred). If too many threads are consumed, it can lead to service outages.

The `join(timeout, unit)` method supports a timed `join`, similar to how `cf.get(timeout, unit)` complements `cf.get()`.

This new method is simple to use and does not require a code example.

### 2.5 `Java 8``Backport` support

All new `CF` features from higher versions (`Java 9+`) are directly available in the lower version `Java 8`.

Key backport features include:

- Timeout control: `orTimeout` / `completeOnTimeout` methods
- Delayed execution: `delayedExecutor` method
- Factory methods: `failedFuture` / `completedStage` / `failedStage`
- Handling operations: `completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`

These backported methods are existing functionalities of `CompletableFuture` and do not require code examples.

### 2.6 Return Type-Specific `anyOf` Method

The CompletableFuture.anyOf method returns an Object, which loses type specificity and type safety, making it inconvenient to use since it requires explicit casting.

`cffu` provides `anySuccessOf` / `anyOf` methods that return a specific type `T` instead of returning `Object`.

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

Current version of `Java API` documentation: https://foldright.io/api-docs/cffu/

# 🍪Dependencies

> check out [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/1.0.0/versions) for new or available versions。

- `cffu`library（including[`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html) enhanced `CompletableFutureUtils`）:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu</artifactId>
      <version>1.1.3</version>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation("io.foldright:cffu:1.1.3")
    ```
    Gradle Groovy DSL
    ```groovy
    implementation 'io.foldright:cffu:1.1.3'
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local) implementation for [`cffu executor wrapper SPI`](cffu-ttl-executor-wrapper)：
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-ttl-executor-wrapper</artifactId>
      <version>1.1.3</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:1.1.3")
    ```
    Gradle Groovy DSL
    ```groovy
    runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:1.1.3'
    ```

# 📚 See also

### Official Documentation

- [`CompletionStage` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletionStage.html)
- [`CompletableFuture` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)

### [`CompletableFuture` Guide](docs/completable-future-guide.md)

- A comprehensive guide to use `CompletableFuture`
- Offers best practices and pitfalls to avoid
- Provides strategies for effectively and safely integrating `CompletableFuture` into business applications

# 👋 About the Library Name

The library name `cffu` is short for `CompletableFuture-Fu`, pronounced as "C Fu", which sounds like "Shifu" in Chinese, reminiscent of the beloved raccoon master from "Kung Fu Panda" 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
