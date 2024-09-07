# <div align="center"><a href="#dummy"><img src="https://github.com/foldright/cffu/assets/1063891/124658cd-025f-471e-8da1-7eea0e482915" alt="🦝 CompletableFuture-Fu(CF-Fu)"></a></div>

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/fast_ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/fast_ci.yaml?branch=main&logo=github&logoColor=white&label=fast ci" alt="Github Workflow Build Status"></a>
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=main&logo=github&logoColor=white&label=strong ci" alt="Github Workflow Build Status"></a>
<a href="https://app.codecov.io/gh/foldright/cffu/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/cffu/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-339933?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://kotlinlang.org"><img src="https://img.shields.io/badge/Kotlin-1.6+-7F52FF.svg?logo=kotlin&logoColor=white" alt="Kotlin"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/cffu?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://foldright.io/api-docs/cffu/"><img src="https://img.shields.io/github/release/foldright/cffu?label=javadoc&color=339933&logo=read-the-docs&logoColor=white" alt="Javadocs"></a>
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

👉 `cffu`（`CompletableFuture-Fu` 🦝）是一个小小的[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)辅助增强库，提升`CF`使用体验并减少误用，在业务中更方便高效安全地使用`CF`。

欢迎 👏 💖

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="23%" align="right" alt="shifu" /></a>

- 建议和提问，[提交 Issue](https://github.com/foldright/cffu/issues/new)
- 贡献和改进，[Fork 后提通过 Pull Request 贡献代码](https://github.com/foldright/cffu/fork)

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
    - [2.2 支持设置缺省的业务线程池并封装携带](#22-%E6%94%AF%E6%8C%81%E8%AE%BE%E7%BD%AE%E7%BC%BA%E7%9C%81%E7%9A%84%E4%B8%9A%E5%8A%A1%E7%BA%BF%E7%A8%8B%E6%B1%A0%E5%B9%B6%E5%B0%81%E8%A3%85%E6%90%BA%E5%B8%A6)
    - [2.3 高效灵活的并发执行策略（`AllFastFail`/`AnySuccess`/`AllSuccess`/`MostSuccess`）](#23-%E9%AB%98%E6%95%88%E7%81%B5%E6%B4%BB%E7%9A%84%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%AD%96%E7%95%A5allfastfailanysuccessallsuccessmostsuccess)
    - [2.4 支持超时的`join`的方法](#24-%E6%94%AF%E6%8C%81%E8%B6%85%E6%97%B6%E7%9A%84join%E7%9A%84%E6%96%B9%E6%B3%95)
    - [2.5 `Backport`支持`Java 8`](#25-backport%E6%94%AF%E6%8C%81java-8)
    - [2.6 返回具体类型的`anyOf`方法](#26-%E8%BF%94%E5%9B%9E%E5%85%B7%E4%BD%93%E7%B1%BB%E5%9E%8B%E7%9A%84anyof%E6%96%B9%E6%B3%95)
    - [2.7 输入宽泛类型的`allof/anyOf`方法](#27-%E8%BE%93%E5%85%A5%E5%AE%BD%E6%B3%9B%E7%B1%BB%E5%9E%8B%E7%9A%84allofanyof%E6%96%B9%E6%B3%95)
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
  - 🏪 更方便的功能，如
    - 支持返回多个`CF`的结果，而不是无返回结果的`Void`（`allOf`）  
      如方法`allResultsFastFailOf` / `allResultsOf` / `mSupplyFastFailAsync` / `thenMApplyFastFailAsync`
    - 支持返回多个不同类型`CF`的结果，而不是同一类型  
      如方法`allTupleFastFailOf` / `allTupleOf` / `tupleMSupplyFastFailAsync` / `thenTupleMApplyFastFailAsync`
    - 支持直接运行多个`action`，而不是要先包装成`CompletableFuture`  
      如方法`tupleMSupplyFastFailAsync` / `mSupplyMostSuccessAsync` / `thenTupleMApplyFastFailAsync` / `thenMRunFastFailAsync`
    - 支持处理指定异常类型的`catching`方法，而不是处理所有异常`Throwable`（`exceptionally`）
  - ⚙️ 更高效灵活的并发执行策略，如
    - `AllFastFail`策略：当输入的多个`CF`有失败时快速失败返回，而不再于事无补地等待所有`CF`运行完成（`allOf`）
    - `AnySuccess`策略：返回首个成功的`CF`结果，而不是首个完成（但可能失败）的`CF`（`anyOf`）
    - `AllSuccess`策略：返回多个`CF`中成功的结果，对于失败的`CF`返回指定的缺省值
    - `MostSuccess`策略：指定时间内返回多个`CF`中成功的结果，对于失败或还没有运行完成的`CF`返回指定的缺省值
    - `All(Complete)`/`Any(Complete)`策略：这2个是`CompletableFuture`已有支持的策略
  - 🦺 更安全的使用方式，如
    - 支持设置缺省的业务线程池并封装携带，`CffuFactory#builder(executor)`方法
    - 支持超时的`join`的方法，`join(timeout, unit)`方法
    - 超时执行安全的`cffuOrTimeout`/`cffuCompleteOnTimeout`方法
    - 一定不会修改结果的`peek`处理方法  
      （`whenComplete`方法会修改输入`CF`的结果）
    - 支持禁止强制篡改，`CffuFactoryBuilder#forbidObtrudeMethods`方法
    - 在类方法附加完善的代码质量注解，在编码时`IDE`能尽早提示出问题  
      如`@NonNull`、`@Nullable`、`@CheckReturnValue`、`@Contract`等
  - 🧩 缺失的基础基本功能，除了上面面向安全而新实现方法（如`join(timeout, unit)`/`cffuOrTimeout`/`peek`），还有
    - 异步异常完成，`completeExceptionallyAsync`方法
    - 非阻塞地获取成功结果，如果`CF`失败或还没有运行完成则返回指定的缺省值，`getSuccessNow`方法
    - 解包装`CF`异常成业务异常，`unwrapCfException`方法
- 💪 **已有功能的增强**，如
  - `anyOf`方法：返回具体类型`T`（类型安全），而不是返回`Object`（`CompletableFuture#anyOf`）
  - `allof`/`anyOf`方法：输入更宽泛的`CompletionStage`参数类型，而不是`CompletableFuture`类（`CompletableFuture#allOf/anyOf`）
- ⏳ **`Backport`支持`Java 8`**，`Java 9+`高版本的所有`CF`新功能在`Java 8`低版本直接可用，如
  - 超时控制：`orTimeout`/`completeOnTimeout`方法
  - 延迟执行：`delayedExecutor`方法
  - 工厂方法：`failedFuture` / `completedStage` / `failedStage`
  - 处理操作：`completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`
- 🍩 **一等公民支持`Kotlin`**

更多`cffu`的使用方式与功能说明详见 [User Guide](#-user-guide)。

## 关于`CompletableFuture`

如何管理并发执行是个复杂易错的问题，业界有大量的工具、框架可以采用。

> 并发工具、框架的广度了解，可以看看如《[七周七并发模型](https://book.douban.com/subject/26337939/)》、《[Java虚拟机并发编程](https://book.douban.com/subject/24533312/)》、《[Scala并发编程（第2版）](https://book.douban.com/subject/35448965/)》；更多关于并发主题的书籍参见[书单](https://www.douban.com/doulist/41916951/)。

其中[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- **`Java`标准库内置**
  - 无需额外依赖，几乎总是可用
  - 相信有极高的实现质量
- **广为人知广泛使用，有一流的群众基础**
  - `CompletableFuture`在2014年发布的`Java 8`提供，有10年了
  - `CompletableFuture`的父接口[`Future`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Future.html)早在2004年发布的`Java 5`中提供，有20年了
  - 虽然`Future`接口不支持 执行结果的异步获取与并发执行逻辑的编排，但也让广大`Java`开发者熟悉了`Future`这个典型的概念与工具
- **功能强大、但不会非常庞大复杂**
  - 足以应对日常的业务需求开发
  - 其它的大型并发框架（比如[`Akka`](https://akka.io/)、[`RxJava`](https://github.com/ReactiveX/RxJava)）在使用上需要理解的内容要多很多
  - 当然基本的并发关注方面及其复杂性，与具体使用哪个工具无关，都是要理解与注意的
- **高层抽象**
  - 或说 以业务流程的形式表达技术的并发流程
  - 可以不使用繁琐易错的基础并发协调工具，如锁（[`Lock`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/package-summary.html)）、[`CountDownLatch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html)、信号量（[`Semaphore`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Semaphore.html)）、[`CyclicBarrier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CyclicBarrier.html)

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
    - 优化`CompletableFuture`使用的工具方法在业务项目中很常见，`CompletableFutureUtils`提供了一系列实用可靠高效安全的工具方法
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
    final CompletableFuture<Integer> combinedWithTimeout =
        orTimeout(combined, 1500, TimeUnit.MILLISECONDS);
    System.out.println("combined result: " + combinedWithTimeout.get());

    final CompletableFuture<Integer> anySuccess = anySuccessOf(longTaskC, longFailedTask);
    System.out.println("any success result: " + anySuccess.get());
  }
}
```

> \# 完整可运行的Demo代码参见[`CompletableFutureUtilsDemo.java`](demos/cffu-demo/src/main/java/io/foldright/demo/cffu/CompletableFutureUtilsDemo.java)。

### 3) `Kotlin`扩展方法

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

> \# 完整可运行的Demo代码参见[`CffuDemo.kt`](demos/cffu-kotlin-demo/src/main/java/io/foldright/demo/cffu/kotlin/CffuDemo.kt)。

## 2. `cffu`功能介绍

### 2.1 返回多个运行`CF`的结果

`CompletableFuture`的`allOf`方法没有返回结果，只是返回`Void`。不方便获取所运行的多个`CF`结果：

- 需要在`allOf`方法之后再通过入参`CF`的读操作（如`join`/`get`）来获取结果
  - 操作繁琐
  - 读方法（如`join`/`get`）是阻塞的，增加了业务逻辑的死锁风险❗️
    更多说明可以看看[CompletableFuture原理与实践 - 4.2.2 线程池循环引用会导致死锁](https://juejin.cn/post/7098727514725416967#heading-24)
- 或是在传入的`Action`并在`Action`中设置外部的变量，需要注意多线程读写的线程安全问题 ⚠️
  - 多线程读写涉及多线程数据传递的复杂性，遗漏并发逻辑的数据读写的正确处理是业务代码中的常见问题❗️

`cffu`的`allResultsFastFailOf`/`allResultsOf`方法提供了返回多个`CF`结果的功能，使用库的功能直接获取整体结果：

- 方便直接
- 规避了多线程读写的复杂线程安全问题与逻辑错误
- 因为返回的是有整体结果的`CF`（可以继续串接非阻塞的操作），自然减少了阻塞的读方法（如`join`/`get`）使用，尽量降低死锁风险

示例代码如下：

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

> \# 完整可运行的Demo代码参见[`AllResultsOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllResultsOfDemo.java)。

上面多个相同结果类型的`CF`，`cffu`还提供了返回多个不同类型`CF`结果的方法，`allTupleFastFailOf`/`allTupleOf`方法。

示例代码如下：

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

> \# 完整可运行的Demo代码参见[`AllTupleOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllTupleOfDemo.java)。

### 2.2 支持设置缺省的业务线程池并封装携带

- `CompletableFuture`执行执行（即`CompletableFuture`的`*Async`方法），使用的缺省线程池是`ForkJoinPool.commonPool()`。
- 这个线程池差不多是`CPU`个线程，合适执行`CPU`密集的任务；对于业务逻辑，往往有很多等待操作（如网络`IO`、阻塞等待），并不是`CPU`密集的。
- 业务使用这个缺省线程池`ForkJoinPool.commonPool()`是很危险的❗

结果就是，

- 在业务逻辑中，调用`CompletableFuture`的`*Async`方法时，几乎每次都要反复传入指定的业务线程池；这让`CompletableFuture`的使用很繁琐易错 🤯
- 在底层逻辑中，当底层操作回调业务时（如`RPC`回调），不合适或方便为业务提供线程池；这时使用`Cffu`封装携带的线程池既方便又合理安全  
  这个使用场景更多可以看看[CompletableFuture原理与实践 - 4.2.3 异步RPC调用注意不要阻塞IO线程池](https://juejin.cn/post/7098727514725416967#heading-25)

示例代码如下：

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

> \# 完整可运行的Demo代码参见[`NoDefaultExecutorSettingForCompletableFuture.java`](cffu-core/src/test/java/io/foldright/demo/NoDefaultExecutorSettingForCompletableFuture.java)。

`Cffu`支持设置缺省的业务线程池，规避上面的繁琐与危险。示例代码如下：

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

> \# 完整可运行的Demo代码参见[`DefaultExecutorSettingForCffu.java`](cffu-core/src/test/java/io/foldright/demo/DefaultExecutorSettingForCffu.java)。

### 2.3 高效灵活的并发执行策略（`AllFastFail`/`AnySuccess`/`AllSuccess`/`MostSuccess`）

- `CompletableFuture`的`allOf`方法会等待所有输入`CF`运行完成；即使有`CF`失败了也要等待后续`CF`都运行完成，再返回一个失败的`CF`。
  - 对于业务逻辑来说，这样失败且继续等待策略，减慢了业务响应性；会希望如果有输入`CF`失败了，则快速失败不再做于事无补的等待
  - `cffu`提供了相应的`allResultsFastFailOf`等方法
  - `allOf`/`allResultsFastFailOf`两者都是，只有当所有的输入`CF`都成功时，才返回成功结果
- `CompletableFuture`的`anyOf`方法返回首个完成的`CF`（不会等待后续没有完成的`CF`，赛马模式）；即使首个完成的`CF`是失败的，也会返回这个失败的`CF`结果。
  - 对于业务逻辑来说，会希望赛马模式返回首个成功的`CF`结果，而不是首个完成但失败的`CF`
  - `cffu`提供了相应的`anySuccessOf`等方法
  - `anySuccessOf`只有当所有的输入`CF`都失败时，才返回失败结果
- 返回多个`CF`中成功的结果，对于失败的`CF`返回指定的缺省值
  - 业务有容错逻辑时，有处理出错时可以使用成功那部分结果，而不是整体失败
  - `cffu`提供了相应的`allSuccessOf`等方法
- 返回指定时间内多个`CF`中成功的结果，对于失败或还没有运行完成的`CF`返回指定的缺省值
  - 业务最终一致性时，能返回就尽量返回有的；对于没有及时返回还在运行中处理的`CF`，结果会写到分布式缓存中避免重复计算，下次就有了
  - 这是个常见业务使用模式，`cffu`提供了相应的`mostSuccessResultsOf`等方法

> 📔 关于多个`CF`的并发执行策略，可以看看`JavaScript`规范[`Promise Concurrency`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise#promise_concurrency)；在`JavaScript`中，`Promise`即对应`CompletableFuture`。
>
> `JavaScript Promise`提供了4个并发执行方法：
>
> - [`Promise.all()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all)：等待所有`Promise`运行成功，只要有一个失败就立即返回失败（对应`cffu`的`allResultsFastFailOf`方法）
> - [`Promise.allSettled()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/allSettled)：等待所有`Promise`运行完成，不管成功失败（对应`cffu`的`allResultsOf`方法）
> - [`Promise.any()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/any)：赛马模式，立即返回首个成功的`Promise`（对应`cffu`的`anySuccessOf`方法）
> - [`Promise.race()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/race)：赛马模式，立即返回首个完成的`Promise`（对应`cffu`的`anyOf`方法）
>
> PS：`JavaScript Promise`的方法命名真考究～ 👍
>
> `cffu`新加2个方法后，对齐了`JavaScript Promise`规范的并发方法～ 👏

示例代码如下：

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

> \# 完整可运行的Demo代码参见[`ConcurrencyStrategyDemo.java`](cffu-core/src/test/java/io/foldright/demo/ConcurrencyStrategyDemo.java)。

### 2.4 支持超时的`join`的方法

`cf.join()`会「不超时永远等待」，在业务中很危险❗️当意外出现长时间等待时，会导致：

- 主业务逻辑阻塞，没有机会做相应的处理，以及时响应用户
- 会费掉一个线程，线程是很有限的资源（一般几百个），耗尽线程意味着服务瘫痪故障

`join(timeout, unit)`方法即支持超时的`join`的方法；就像`cf.get(timeout, unit)` 之于 `cf.get()`。

这个新方法使用简单类似，不附代码示例。

### 2.5 `Backport`支持`Java 8`

`Java 9+`高版本的所有`CF`新功能在`Java 8`低版本直接可用。

其中重要的Backport功能有：

- 超时控制：`orTimeout`/`completeOnTimeout`方法
- 延迟执行：`delayedExecutor`方法
- 工厂方法：`failedFuture` / `completedStage` / `failedStage`
- 处理操作：`completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`

这些`backport`的方法是`CompletableFuture`的已有功能，不附代码示例。

### 2.6 返回具体类型的`anyOf`方法

`CompletableFuture.anyOf`方法返回类型是`Object`，丢失具体类型，不类型安全，使用时需要转型也不方便。

`cffu`提供的`anySuccessOf`/`anyOf`方法，返回具体类型`T`，而不是返回`Object`。

这个方法使用简单类似，不附代码示例。

### 2.7 输入宽泛类型的`allof/anyOf`方法

`CompletableFuture#allof/anyOf`方法输入参数类型是`CompletableFuture`，而输入更宽泛的`CompletionStage`类型；对于`CompletionStage`类型的输入，则需要调用`CompletionStage#toCompletableFuture`方法做转换。

`cffu`提供的`allof`/`anyOf`方法输入更宽泛的`CompletionStage`参数类型，使用更方便。

方法使用简单类似，不附代码示例。

### 更多功能说明

可以参见：

- `API`文档
  - [`Java API`文档](https://foldright.io/api-docs/cffu/)
  - [`Kotlin API`文档](https://foldright.io/api-docs/cffu-kotlin/)
- 实现源码
  - `cffu`：[`Cffu.java`](cffu-core/src/main/java/io/foldright/cffu/Cffu.java)、[`CffuFactory.java`](cffu-core/src/main/java/io/foldright/cffu/CffuFactory.java)
  - `CompletableFuture utils`：[`CompletableFutureUtils.java`](cffu-core/src/main/java/io/foldright/cffu/CompletableFutureUtils.java)、[`ListenableFutureUtils.java`](cffu-core/src/main/java/io/foldright/cffu/ListenableFutureUtils.java)
  - `Kotlin extensions`：[`CffuExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/CffuExtensions.kt)、[`CompletableFutureExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/CompletableFutureExtensions.kt)、[`ListenableFutureExtensions.kt`](cffu-kotlin/src/main/java/io/foldright/cffu/kotlin/ListenableFutureExtensions.kt)

## 3. 如何从直接使用`CompletableFuture`类迁移到`Cffu`类

为了使用`cffu`增强功能，可以迁移已有直接使用`CompletableFuture`类的代码到`Cffu`类。包含2步修改：

- 在类型声明地方，`CompletableFuture`类改成`Cffu`类
- 在`CompletableFuture`静态方法调用的地方，类名`CompletableFuture`改成`cffuFactory`实例

之所以可以这样迁移，是因为：

- `CompletableFuture`类的所有实例方法都在`Cffu`类，且有相同的方法签名与功能
- `CompletableFuture`类的所有静态方法都在`CffuFactory`类，且有相同的方法签名与功能

# 🔌 API Docs

- 当前版本的`Java API`文档： https://foldright.io/api-docs/cffu/
- 当前版本的`Kotlin API`文档： https://foldright.io/api-docs/cffu-kotlin/

代码示例：

# 🍪依赖

> 可以在 [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions) 查看最新版本与可用版本列表。

- `cffu`库（包含[`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)的增强`CompletableFutureUtils`）:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu</artifactId>
      <version>1.0.0-Alpha22</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu:1.0.0-Alpha22")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu:1.0.0-Alpha22'
    ```
- `cffu Kotlin`支持库:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-kotlin</artifactId>
      <version>1.0.0-Alpha22</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu-kotlin:1.0.0-Alpha22")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu-kotlin:1.0.0-Alpha22'
    ```
- `cffu bom`:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-bom</artifactId>
      <version>1.0.0-Alpha22</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation(platform("io.foldright:cffu-bom:1.0.0-Alpha22"))
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation platform('io.foldright:cffu-bom:1.0.0-Alpha22')
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local)的[`cffu executor wrapper SPI`实现](cffu-ttl-executor-wrapper)：
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-ttl-executor-wrapper</artifactId>
      <version>1.0.0-Alpha22</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:1.0.0-Alpha22")
    ```
    ```groovy
    // Gradle Groovy DSL
    runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:1.0.0-Alpha22'
    ```

# 📚 更多资料

- 官方资料
  - [`CompletionStage` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletionStage.html)
  - [`CompletableFuture` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
- [`CompletableFuture` Guide](docs/completable-future-guide.md)
  - 完备说明`CompletableFuture`的使用方式
  - 给出 最佳实践建议 与 使用陷阱注意
  - 在业务中，更有效安全地使用`CompletableFuture`

# 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，想到了《功夫熊猫》里可爱的小浣熊师傅吧～ 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
