# <div align="center"><a href="#dummy"><img src="https://github.com/foldright/cffu/assets/1063891/124658cd-025f-471e-8da1-7eea0e482915" alt="🦝 CompletableFuture Fu (CF-Fu)"></a></div>

> 🚧 项目还在开发中，发布了`v0.x`版本： [![Maven Central](https://img.shields.io/maven-central/v/io.foldright/cffu?logo=apache-maven&label=cffu&logoColor=white)](https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions)
>
> 工作项列表及其进展，参见 [issue 6](https://github.com/foldright/cffu/issues/6)。

----------------------------------------

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/fast_ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/fast_ci.yaml?branch=main&logo=github&logoColor=white&label=fast ci" alt="Github Workflow Build Status"></a>
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=main&logo=github&logoColor=white&label=strong ci" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/cffu/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/cffu/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.5+-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/cffu?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/cffu/apidocs/"><img src="https://img.shields.io/github/release/foldright/cffu?label=javadoc&color=339933&logo=microsoft-academic&logoColor=white" alt="Javadocs"></a>
<a href="https://foldright.io/cffu/dokka/"><img src="https://img.shields.io/github/release/foldright/cffu?label=dokka&color=339933&logo=kotlin&logoColor=white" alt="dokka"></a>
<a href="https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/cffu/releases"><img src="https://img.shields.io/github/release/foldright/cffu.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/cffu/stargazers"><img src="https://img.shields.io/github/stars/foldright/cffu" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/cffu/fork"><img src="https://img.shields.io/github/forks/foldright/cffu" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/cffu/issues"><img src="https://img.shields.io/github/issues/foldright/cffu" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/graphs/contributors"><img src="https://img.shields.io/github/contributors/foldright/cffu" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/cffu"><img src="https://img.shields.io/github/repo-size/foldright/cffu" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/cffu"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

👉 `cffu`（`CompletableFuture Fu` 🦝）是一个小小的[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/CompletableFuture.html)辅助增强库，提升`CF`使用体验并减少误用，期望在业务中更方便高效安全地使用`CF`。

欢迎 👏 💖

- 建议和提问，[提交 Issue](https://github.com/foldright/cffu/issues/new)
- 贡献和改进，[Fork 后提通过 Pull Request 贡献代码](https://github.com/foldright/cffu/fork)

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="23%" align="right" alt="shifu" /></a>

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🔧 功能](#-%E5%8A%9F%E8%83%BD)
  - [关于`CompletableFuture`](#%E5%85%B3%E4%BA%8Ecompletablefuture)
- [👥 User Guide](#-user-guide)
  - [1. `cffu`的三种使用方式](#1-cffu%E7%9A%84%E4%B8%89%E7%A7%8D%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F)
    - [1) `Cffu`类](#1-cffu%E7%B1%BB)
    - [2) `CompletableFutureUtils`工具类](#2-completablefutureutils%E5%B7%A5%E5%85%B7%E7%B1%BB)
    - [3) `Kotlin`扩展方法](#3-kotlin%E6%89%A9%E5%B1%95%E6%96%B9%E6%B3%95)
  - [2. `cffu`功能介绍](#2-cffu%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)
    - [2.1 返回多个运行`CF`的结果](#21-%E8%BF%94%E5%9B%9E%E5%A4%9A%E4%B8%AA%E8%BF%90%E8%A1%8Ccf%E7%9A%84%E7%BB%93%E6%9E%9C)
    - [2.2 支持设置缺省的业务线程池](#22-%E6%94%AF%E6%8C%81%E8%AE%BE%E7%BD%AE%E7%BC%BA%E7%9C%81%E7%9A%84%E4%B8%9A%E5%8A%A1%E7%BA%BF%E7%A8%8B%E6%B1%A0)
    - [2.3 高效灵活的并发执行策略（`allOfFastFail`/`anyOfSuccess`）](#23-%E9%AB%98%E6%95%88%E7%81%B5%E6%B4%BB%E7%9A%84%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%AD%96%E7%95%A5alloffastfailanyofsuccess)
    - [2.4 支持超时的`join`的方法](#24-%E6%94%AF%E6%8C%81%E8%B6%85%E6%97%B6%E7%9A%84join%E7%9A%84%E6%96%B9%E6%B3%95)
    - [2.5 `Backport`支持`Java 8`](#25-backport%E6%94%AF%E6%8C%81java-8)
    - [2.5 返回具体类型的`anyOf`方法](#25-%E8%BF%94%E5%9B%9E%E5%85%B7%E4%BD%93%E7%B1%BB%E5%9E%8B%E7%9A%84anyof%E6%96%B9%E6%B3%95)
    - [更多功能说明](#%E6%9B%B4%E5%A4%9A%E5%8A%9F%E8%83%BD%E8%AF%B4%E6%98%8E)
  - [3. 如何从直接使用`CompletableFuture`类迁移到`Cffu`类](#3-%E5%A6%82%E4%BD%95%E4%BB%8E%E7%9B%B4%E6%8E%A5%E4%BD%BF%E7%94%A8completablefuture%E7%B1%BB%E8%BF%81%E7%A7%BB%E5%88%B0cffu%E7%B1%BB)
- [🔌 API Docs](#-api-docs)
- [🍪依赖](#%E4%BE%9D%E8%B5%96)
- [📚 更多资料](#-%E6%9B%B4%E5%A4%9A%E8%B5%84%E6%96%99)
- [👋 关于库名](#-%E5%85%B3%E4%BA%8E%E5%BA%93%E5%90%8D)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

# 🔧 功能

提供的功能有：

- ☘️ **补全业务使用中缺失的功能**
  - 更方便的功能，如
    - `cffuAllOf`/`allOfWithResult`方法：返回多个`CF`的结果，而不是无返回结果`Void`（`allOf`）
    - `cffuCombine`/`combine`方法：返回多个`CF`不同类型的结果，而不是同一类型（`cffuAllOf`/`allOfWithResult`）
  - 更高效灵活的并发执行策略，如
    - `cffuAllOfFastFail`/`allOfFastFail`方法：有`CF`失败时快速返回，而不再等待所有`CF`运行完成（`allOf`）
    - `cffuAnyOfSuccess`/`anyOfSuccess`方法：返回首个成功的`CF`结果，而不是首个完成（但可能失败）的`CF`（`anyOf`）
  - 更安全的使用方式，如
    - 支持设置缺省的业务线程池（`CffuFactoryBuilder#newCffuFactoryBuilder(executor)`方法）
    - `cffuJoin(timeout, unit)`方法：支持超时的`join`的方法
    - 支持禁止强制篡改（`CffuFactoryBuilder#forbidObtrudeMethods`方法）
    - 在类方法附加完善的代码质量注解（如`@NonNull`、`@Nullable`、`@CheckReturnValue`、`@Contract`等），在编码时`IDE`能尽早提示出问题
- 💪 **已有功能的增强**，如
  - `cffuAnyOf`/`anyOfWithType`方法：返回类型是`T`（类型安全），而不是返回`Object`（`anyOf`）
- ⏳ **`Backport`支持`Java 8`**，`Java 9+`高版本的所有`CF`新功能在`Java 8`等低`Java`版本直接可用，如
  - 超时控制：`orTimeout`/`completeOnTimeout`方法
  - 延迟执行：`delayedExecutor`方法
  - 工厂方法：`failedFuture`/`completedStage`/`failedStage`
  - 处理操作：`completeAsync`/`exceptionallyAsync`/`exceptionallyCompose`/`copy`
- 🍩 **一等公民支持`Kotlin`**

更多`cffu`的使用方式与功能说明详见 [User Guide](#-user-guide)。

## 关于`CompletableFuture`

如何管理并发执行是个复杂易错的问题，业界有大量的工具、框架可以采用。

> 并发工具、框架的广度了解，可以看看如《[七周七并发模型](https://book.douban.com/subject/26337939/)》、《[Java虚拟机并发编程](https://book.douban.com/subject/24533312/)》、《[Scala并发编程（第2版）](https://book.douban.com/subject/35448965/)》；更多关于并发主题的书籍参见[书单](https://www.douban.com/doulist/41916951/)。

其中[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- **`Java`标准库内置**
  - 无需额外依赖，几乎总是可用
  - 相信有极高的实现质量
- **广为人知广泛使用，有一流的群众基础**
  - `CompletableFuture`在2014年发布的`Java 8`提供，有～10年了
  - `CompletableFuture`的父接口[`Future`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/Future.html)早在2004年发布的`Java 5`中提供，有～20年了
  - 虽然`Future`接口不支持 执行结果的异步获取与并发执行逻辑的编排，但也让广大`Java`开发者熟悉了`Future`这个典型的概念与工具
- **功能强大、但不会非常庞大复杂**
  - 足以应对日常的业务需求开发
  - 其它的大型并发框架（比如[`Akka`](https://akka.io/)、[`RxJava`](https://github.com/ReactiveX/RxJava)）在使用上需要理解的内容要多很多
  - 当然基本的并发关注方面及其复杂性，与具体使用哪个工具无关，都是要理解与注意的
- **高层抽象**
  - 或说 以业务流程的形式表达技术的并发流程
  - 可以不使用繁琐易错的基础并发协调工具，如[`CountDownLatch`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/CountDownLatch.html)、锁（[`Lock`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/locks/package-summary.html)）、信号量（[`Semaphore`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/Semaphore.html)）

和其它并发工具、框架一样，`CompletableFuture`用于

- 并发执行业务逻辑，或说编排并发的处理流程/处理任务
- 利用多核并行处理
- 提升业务响应性

值得更深入了解和应用。 💕

# 👥 User Guide

## 1. `cffu`的三种使用方式

`cffu`支持三种使用方式：

- 🦝 1) **使用`Cffu`类**
  - 项目使用`Java`语言时，推荐这种使用方式
  - 直接使用`CompletableFuture`类的代码可以比较简单的迁移到`Cffu`类，包含2步修改：
    - 在类型声明地方，由`CompletableFuture`改成`Cffu`
    - 在`CompletableFuture`静态方法调用的地方，类名`CompletableFuture`改成`cffuFactory`实例
    - 更多参见[如何从直接使用`CompletableFuture`类迁移到`Cffu`类](#3-%E5%A6%82%E4%BD%95%E4%BB%8E%E7%9B%B4%E6%8E%A5%E4%BD%BF%E7%94%A8completablefuture%E7%B1%BB%E8%BF%81%E7%A7%BB%E5%88%B0cffu%E7%B1%BB)
  - 依赖`io.foldright:cffu`库
- 🛠️️ 2) **使用`CompletableFutureUtils`工具类**
  - 如果你不想在项目中引入新类（`Cffu`类）、觉得这样增加了复杂性的话，
    - 完全可以把`cffu`库作为一个工具类来用
    - 优化`CompletableFuture`使用的工具方法在业务项目中很常见，`CompletableFutureUtils`提供了一系列实用可靠的工具方法
  - 这种使用方式有些`cffu`功能没有提供（也没有想到实现方案） 😔  
    如支持设置缺省的业务线程池、禁止强制篡改
  - 依赖`io.foldright:cffu`库
- 🍩 3) **使用`Kotlin`扩展方法**
  - 项目使用`Kotlin`语言时，推荐这种使用方式
  - 要依赖`io.foldright:cffu-kotlin`库

在介绍功能点之前，可以先看看`cffu`不同使用方式的示例。 🎪

### 1) `Cffu`类

```java
public class CffuDemo {
  private static final ExecutorService myBizThreadPool = Executors.newCachedThreadPool();
  // Create a CffuFactory with configuration of the customized thread pool
  private static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizThreadPool).build();

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
    final Cffu<Integer> anyOfSuccess = cffuFactory.cffuAnyOfSuccess(longTaskC, longFailedTask);
    System.out.println("anyOfSuccess result: " + anyOfSuccess.get());
  }
}
```

> \# 完整可运行的Demo代码参见[`CffuDemo.java`](demos/cffu-demo/src/main/java/io/foldright/demo/cffu/CffuDemo.java)。

### 2) `CompletableFutureUtils`工具类

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
    final CompletableFuture<Integer> combinedWithTimeout = CompletableFutureUtils.orTimeout(combined, 1500, TimeUnit.MILLISECONDS);
    System.out.println("combined result: " + combinedWithTimeout.get());
    final CompletableFuture<Integer> anyOfSuccess = CompletableFutureUtils.anyOfSuccessWithType(longTaskC, longFailedTask);
    System.out.println("anyOfSuccess result: " + anyOfSuccess.get());
  }
}
```

> \# 完整可运行的Demo代码参见[`CompletableFutureUtilsDemo.java`](demos/cffu-demo/src/main/java/io/foldright/demo/cffu/CompletableFutureUtilsDemo.java)。

### 3) `Kotlin`扩展方法

```kt
private val myBizThreadPool: ExecutorService = Executors.newCachedThreadPool()

// Create a CffuFactory with configuration of the customized thread pool
private val cffuFactory: CffuFactory = newCffuFactoryBuilder(myBizThreadPool).build()

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
  val anyOfSuccess: Cffu<Int> = listOf(longTaskC, longFailedTask).anyOfSuccessCffu()
  println("anyOfSuccess result: ${anyOfSuccess.get()}")
}
```

> \# 完整可运行的Demo代码参见[`CffuDemo.kt`](demos/cffu-kotlin-demo/src/main/java/io/foldright/demo/cffu/kotlin/CffuDemo.kt)。

## 2. `cffu`功能介绍

### 2.1 返回多个运行`CF`的结果

`CompletableFuture`的`allOf`方法没有返回结果，只是返回`Void`，不方便获得所运行的多个`CF`结果。  
\# 要再通过入参`CF`的`get`方法来获取结果。

`cffu`的`cffuAllOf`/`allOfWithResult`方法提供了返回多个`CF`结果的功能。

示例代码如下：

```java
public class AllOfWithResultDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizExecutor).build();

  public static void main(String[] args) {
    //////////////////////////////////////////////////
    // CffuFactory#cffuAllOf
    //////////////////////////////////////////////////
    Cffu<Integer> cffu1 = cffuFactory.completedFuture(21);
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Void> allOf2 = cffuFactory.allOf(cffu1, cffu2);
    // Result type is Void!!
    // the result can be got by input argument `cf1.get()`, but it's cumbersome.
    // so we can see a lot the util methods to enhance allOf with result in our project.

    Cffu<List<Integer>> allOfWithResult = cffuFactory.cffuAllOf(cffu1, cffu2);
    System.out.println(allOfWithResult.get());

    //////////////////////////////////////////////////
    // or CompletableFutureUtils#allOfWithResult
    //////////////////////////////////////////////////
    CompletableFuture<Integer> cf1 = CompletableFuture.completedFuture(21);
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Void> allOf = CompletableFuture.allOf(cf1, cf2);
    // Result type is Void!!

    CompletableFuture<List<Integer>> allOfWithResult2 = CompletableFutureUtils.allOfWithResult(cf1, cf2);
    System.out.println(allOfWithResult2.get());
  }
}
```

> \# 完整可运行的Demo代码参见[`AllOfWithResultDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllOfWithResultDemo.java)。

上面多个相同结果类型的`CF`，`cffu`还提供了返回多个不同类型`CF`结果的方法，`cffuCombine`/`CompletableFutureUtils#combine`方法。

示例代码如下：

```java
public class CffuCombineDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    //////////////////////////////////////////////////
    // cffuCombine
    //////////////////////////////////////////////////
    Cffu<String> cffu1 = cffuFactory.completedFuture("21");
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Tuple2<String, Integer>> allOfWithResult = cffu1.cffuCombine(cffu2);
    // or: cffuFactory.cffuCombine(cffu1, cffu2);
    System.out.println(allOfWithResult.get());

    //////////////////////////////////////////////////
    // or CompletableFutureUtils.combine
    //////////////////////////////////////////////////
    CompletableFuture<String> cf1 = CompletableFuture.completedFuture("21");
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Tuple2<String, Integer>> allOfWithResult2 = CompletableFutureUtils.combine(cf1, cf2);
    System.out.println(allOfWithResult2.get());
  }
}
```

> \# 完整可运行的Demo代码参见[`CffuCombineDemo.java`](cffu-core/src/test/java/io/foldright/demo/CffuCombineDemo.java)。

### 2.2 支持设置缺省的业务线程池

- `CompletableFuture`执行执行（即`CompletableFuture`的`*Async`方法），使用的缺省线程池是`ForkJoinPool.commonPool()`。
- 这个线程池差不多是`CPU`个线程，合适执行`CPU`密集的任务；对于业务逻辑，往往有很多等待操作（如网络`IO`、阻塞等待），并不是`CPU`密集的。
- 业务使用这个缺省线程池`ForkJoinPool.commonPool()`是很危险的❗

结果就是，业务调用`CompletableFuture`的`*Async`方法时，几乎每次都要反复传入业务线程池；这让`CompletableFuture`的使用很繁琐易错 🤯

示例代码如下：

```java
public class NoDefaultExecutorSettingForCompletableFuture {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();

  public static void main(String[] args) {
    CompletableFuture<Void> cf1 = CompletableFuture.runAsync(() -> System.out.println("doing a long time work!"),
        myBizExecutor);

    CompletableFuture<Void> cf2 = CompletableFuture
        .supplyAsync(
            () -> {
              System.out.println("doing another long time work!!");
              return 42;
            },
            myBizExecutor)
        .thenAcceptAsync(
            i -> System.out.println("doing third long time work!!!"),
            myBizExecutor);

    CompletableFuture.allOf(cf1, cf2).join();
  }
}
```

> \# 完整可运行的Demo代码参见[`NoDefaultExecutorSettingForCompletableFuture.java`](cffu-core/src/test/java/io/foldright/demo/NoDefaultExecutorSettingForCompletableFuture.java)。

`Cffu`支持设置缺省的业务线程池，规避上面的繁琐与危险。示例代码如下：

```java
public class DefaultExecutorSettingForCffu {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizExecutor).build();

  public static void main(String[] args) {
    Cffu<Void> cf1 = cffuFactory.runAsync(() -> System.out.println("doing a long time work!"));

    Cffu<Void> cf2 = cffuFactory.supplyAsync(() -> {
      System.out.println("doing another long time work!!");
      return 42;
    }).thenAcceptAsync(i -> System.out.println("doing third long time work!!!"));

    cffuFactory.allOf(cf1, cf2).join();
  }
}
```

> \# 完整可运行的Demo代码参见[`DefaultExecutorSettingForCffu.java`](cffu-core/src/test/java/io/foldright/demo/DefaultExecutorSettingForCffu.java)。

### 2.3 高效灵活的并发执行策略（`allOfFastFail`/`anyOfSuccess`）

- `CompletableFuture`的`allOf`方法会等待所有输入`CF`运行完成；即使有`CF`失败了也要等待后续`CF`运行完成，再返回一个失败的`CF`。
  - 对于业务逻辑来说，这样失败且继续等待策略，减慢了业务响应性；会希望如果有输入`CF`失败了，则快速失败不再做对结果无用的等待
  - `cffu`提供了相应的`cffuAllOfFastFail`/`allOfFastFail`方法
  - `allOf`/`allOfFastFail`两者都是，只有当所有的输入`CF`都成功时，才返回成功结果
- `CompletableFuture`的`anyOf`方法返回首个完成的`CF`（不会等待后续没有完成的`CF`，赛马模式）；即使首个完成的`CF`是失败的，也会返回这个失败的`CF`结果。
  - 对于业务逻辑来说，会希望赛马模式返回首个成功的`CF`结果，而不是首个完成但失败的`CF`
  - `cffu`提供了相应的`cffuAnyOfSuccess`/`anyOfSuccess`方法
  - `anyOfSuccess`只有当所有的输入`CF`都失败时，才返回失败结果

> 📔 关于多个`CF`的并发执行策略，可以看看`JavaScript`规范[`Promise Concurrency`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise#promise_concurrency)；在`JavaScript`中，`Promise`即对应`CompletableFuture`。
>
> `JavaScript Promise`提供了4个并发执行方法：
>
> - [`Promise.all()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all)：等待所有`Promise`运行完成，只要有一个失败就返回失败（对应`cffu`的`allOfFastFail`方法）
> - [`Promise.allSettled()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/allSettled)：等待所有`Promise`运行完成，不管成功失败（对应`cffu`的`allOf`方法）
> - [`Promise.any()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/any)：赛马模式，返回首个成功的`Promise`（对应`cffu`的`anyOfSuccess`方法）
> - [`Promise.race()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/race)：赛马模式，返回首个完成的`Promise`（对应`cffu`的`anyOf`方法）
>
> PS：`JavaScript Promise`的方法命名真考究～ 👍
>
> `cffu`新加2个方法后，对齐了`JavaScript Promise`规范的并发方法～ 👏

示例代码如下：

```java
public class ConcurrencyStrategyDemo {
  public static final Executor myBizExecutor = Executors.newCachedThreadPool();
  public static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    ////////////////////////////////////////////////////////////////////////
    // CffuFactory#cffuAllOfFastFail / allOfFastFail
    // CffuFactory#cffuAnyOfSuccess / anyOfSuccess
    ////////////////////////////////////////////////////////////////////////
    final Cffu<Integer> successAfterLongTime = cffuFactory.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 42;
    });
    final Cffu<Integer> failed = cffuFactory.failedFuture(new RuntimeException("Bang!"));

    // Result type is Void!
    Cffu<Void> cffuAll = cffuFactory.allOfFastFail(successAfterLongTime, failed);
    Cffu<List<Integer>> fastFailed = cffuFactory.cffuAllOfFastFail(successAfterLongTime, failed);
    // fast failed without waiting successAfterLongTime
    System.out.println(fastFailed.exceptionNow());

    // Result type is Object!
    Cffu<Object> cffuAny = cffuFactory.anyOfSuccess(successAfterLongTime, failed);
    System.out.println(cffuAny.get());
    Cffu<Integer> anyOfSuccess = cffuFactory.cffuAnyOfSuccess(successAfterLongTime, failed);
    System.out.println(anyOfSuccess.get());

    ////////////////////////////////////////////////////////////////////////
    // or CompletableFutureUtils#allOfFastFailWithResult / allOfFastFail
    //    CompletableFutureUtils#anyOfSuccessWithType / anyOfSuccess
    ////////////////////////////////////////////////////////////////////////
    final CompletableFuture<Integer> successAfterLongTimeCf = CompletableFuture.supplyAsync(() -> {
      sleep(3000); // sleep LONG time
      return 42;
    });
    final CompletableFuture<Integer> failedCf = CompletableFutureUtils.failedFuture(new RuntimeException("Bang!"));

    // Result type is Void!
    CompletableFuture<Void> cfAll = CompletableFutureUtils.allOfFastFail(successAfterLongTimeCf, failedCf);
    CompletableFuture<List<Integer>> fastFailedCf = CompletableFutureUtils.allOfFastFailWithResult(successAfterLongTimeCf, failedCf);
    // fast failed without waiting successAfterLongTime
    System.out.println(CompletableFutureUtils.exceptionNow(fastFailedCf));

    // Result type is Object!
    CompletableFuture<Object> cfAny = CompletableFutureUtils.anyOfSuccess(successAfterLongTimeCf, failedCf);
    System.out.println(cfAny.get());
    CompletableFuture<Integer> cfSuccess = CompletableFutureUtils.anyOfSuccessWithType(successAfterLongTimeCf, failedCf);
    System.out.println(cfSuccess.get());
  }
}
```

> \# 完整可运行的Demo代码参见[`ConcurrencyStrategyDemo.java`](cffu-core/src/test/java/io/foldright/demo/ConcurrencyStrategyDemo.java)。

### 2.4 支持超时的`join`的方法

`cf.join()`会「不超时永远等待」，在业务中很危险❗️当意外出现长时间等待时，会导致：

- 主业务逻辑阻塞，没有机会做相应的处理，以及时响应用户
- 会费掉一个线程，线程是很有限的资源（一般几百个），耗尽线程意味着服务瘫痪故障

`cffuJoin(timeout, unit)`方法即支持超时的`join`的方法；就像`cf.get(timeout, unit)` 之于 `cf.get()`。

这个新方法使用简单类似，不附代码示例。

### 2.5 `Backport`支持`Java 8`

`Java 9+`高版本的所有`CF`新功能在`Java 8`等低`Java`版本直接可用。

其中重要的Backport功能有：

- 超时控制：`orTimeout`/`completeOnTimeout`方法
- 延迟执行：`delayedExecutor`方法
- 工厂方法：`failedFuture`/`completedStage`/`failedStage`
- 处理操作：`completeAsync`/`exceptionallyAsync`/`exceptionallyCompose`/`copy`

这些`backport`的方法是`CompletableFuture`的已有功能，不附代码示例。

### 2.5 返回具体类型的`anyOf`方法

`CompletableFuture.anyOf`方法返回类型是`Object`，丢失具体类型，不够类型安全，使用时需要转型也不方便。

`cffu`提供了`cffuAnyOf`/`anyOfWithType`方法，返回类型是`T`（类型安全），而不是返回`Object`（`anyOf`）。

这个新方法使用简单类似，不附代码示例。

### 更多功能说明

可以参见：

- `API`文档
  - [`Java API`文档](https://foldright.io/cffu/apidocs/)
  - [`Kotlin API`文档](https://foldright.io/cffu/dokka/)
- 实现源码
  - `cffu`：[`Cffu.java`](cffu-core/src/main/java/io/foldright/cffu/Cffu.java)、[`CffuFactory.java`](cffu-core/src/main/java/io/foldright/cffu/CffuFactory.java)
  - `CompletableFuture utils`：[`CompletableFutureUtils.java`](cffu-core/src/main/java/io/foldright/cffu/CompletableFutureUtils.java)
  - `Kotlin extensions`：[`CffuExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/CffuExtensions.kt)、[`CompletableFutureExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/CompletableFutureExtensions.kt)

## 3. 如何从直接使用`CompletableFuture`类迁移到`Cffu`类

为了使用`cffu`增强功能，可以迁移已有直接使用`CompletableFuture`的代码到`Cffu`。包含2步修改：

- 在类型声明地方，`CompletableFuture`改成`Cffu`
- 在`CompletableFuture`静态方法调用的地方，类名`CompletableFuture`改成`cffuFactory`实例

之所以可以这样迁移，是因为：

- `CompletableFuture`类的所有实例方法都在`Cffu`类，且有相同的方法签名与功能
- `CompletableFuture`类的所有静态方法都在`CffuFactory`类，且有相同的方法签名与功能

# 🔌 API Docs

- 当前版本的`Java API`文档： https://foldright.io/cffu/apidocs/
- 当前版本的`Kotlin API`文档： https://foldright.io/cffu/dokka/

代码示例：

# 🍪依赖

> 可以在 [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions) 查看最新版本与可用版本列表。

- `cffu`库（包含[`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/CompletableFuture.html)的增强`CompletableFutureUtils`）:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu</artifactId>
      <version>0.9.8</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu:0.9.8")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu:0.9.8'
    ```
- `cffu Kotlin`支持库:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-kotlin</artifactId>
      <version>0.9.8</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu-kotlin:0.9.8")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu-kotlin:0.9.8'
    ```
- `cffu bom`:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-bom</artifactId>
      <version>0.9.8</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation(platform("io.foldright:cffu-bom:0.9.8"))
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation platform('io.foldright:cffu-bom:0.9.8')
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local)的[`cffu executor wrapper SPI`实现](cffu-ttl-executor-wrapper)：
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-ttl-executor-wrapper</artifactId>
      <version>0.9.8</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:0.9.8")
    ```
    ```groovy
    // Gradle Groovy DSL
    runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:0.9.8'
    ```

# 📚 更多资料

- 官方资料
  - [`CompletionStage` JavaDoc](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/CompletionStage.html)
  - [`CompletableFuture` JavaDoc](https://docs.oracle.com/en/java/javase/20/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
- [`CompletableFuture` Guide](docs/completable-future-guide.md)
  - 完备说明`CompletableFuture`的使用方式
  - 给出 最佳实践建议 与 使用陷阱注意
  - 期望在业务中，更有效安全地使用`CompletableFuture`

# 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，想到了《功夫熊猫》里可爱的小浣熊师傅吧～ 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
