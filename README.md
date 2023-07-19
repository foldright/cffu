# <div align="center"><a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230851256-ac495db4-e2cd-4dbe-b881-91a143c4b551.png" alt="🦝 CompletableFuture Fu (CF-Fu)"></a></div>

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

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="23%" align="right" alt="shifu" /></a>

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🔧 功能](#-%E5%8A%9F%E8%83%BD)
  - [关于`CompletableFuture`](#%E5%85%B3%E4%BA%8Ecompletablefuture)
- [👥 User Guide](#-user-guide)
  - [新增功能](#%E6%96%B0%E5%A2%9E%E5%8A%9F%E8%83%BD)
  - [Backport支持`Java 8`](#backport%E6%94%AF%E6%8C%81java-8)
  - [业务使用中`CompletableFuture`所缺失的功能介绍](#%E4%B8%9A%E5%8A%A1%E4%BD%BF%E7%94%A8%E4%B8%ADcompletablefuture%E6%89%80%E7%BC%BA%E5%A4%B1%E7%9A%84%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)
- [🎪 使用示例](#-%E4%BD%BF%E7%94%A8%E7%A4%BA%E4%BE%8B)
  - [`Java`](#java)
  - [`Kotlin`](#kotlin)
- [🔌 API Docs](#-api-docs)
- [🍪依赖](#%E4%BE%9D%E8%B5%96)
- [📚 更多资料](#-%E6%9B%B4%E5%A4%9A%E8%B5%84%E6%96%99)
- [👋 关于库名](#-%E5%85%B3%E4%BA%8E%E5%BA%93%E5%90%8D)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

## 🔧 功能

👉 `cffu`（`CompletableFuture Fu`）是一个小小的[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/CompletableFuture.html)辅助增强库，提升`CF`的使用体验并减少误用，期望在业务中更方便高效安全地使用`CF`。

提供的功能有：

- ☘️ **补全业务使用中缺失的功能**
  - 更方便的功能，如
    - `cffuAllOf/allOfWithResult`方法：返回包含多个`CF`的结果，而不是返回`Void`（`allOf`）
  - 更高效灵活的并发执行策略，如
    - `cffuAllOfFastFail/allOfFastFail`方法：有`CF`失败时快速返回，而不是等待所有`CF`运行完成（`allOf`）
    - `cffuAnyOfSuccess/anyOfSuccess`方法：返回首个成功的`CF`结果，而不是首个完成的`CF`（`anyOf`）
  - 更安全的使用方式，如
    - 支持设置缺省的业务线程池（`CffuFactoryBuilder#newCffuFactoryBuilder(executor)`方法）
    - `cffuJoin(timeout, unit)`方法：支持超时的`join`的方法
    - `cffuCombine`方法：多个不同类型的`CF`同时运行，返回保留多个不同类型的结果
    - 支持禁止强制篡改（`CffuFactoryBuilder#forbidObtrudeMethods`方法）
- 💪 **已有功能的增强**，如
  - 类型安全的`allOf`、`anyOf`方法
- ⏳ **`Backport`支持`Java 8`**，`Java 9+`高版本的所有`CF`新功能在`Java 8`等低`Java`版本直接可用，如
  - 超时控制：`orTimeout`/`completeOnTimeout`方法
  - 延迟执行：`delayedExecutor`方法
  - 工厂方法：`failedFuture`/`completedStage`/`failedStage`
- 🍩 **一等公民支持`Kotlin`**

更多`cffu`的使用方式与功能说明详见 [User Guide](#-user-guide)。

### 关于`CompletableFuture`

如何管理并发执行是个复杂易错的问题，业界有大量的工具、框架可以采用。

> 并发工具、框架的广度了解，可以看看如《[七周七并发模型](https://book.douban.com/subject/26337939/)》、《[Java虚拟机并发编程](https://book.douban.com/subject/24533312/)》、《[Scala并发编程（第2版）](https://book.douban.com/subject/35448965/)》；更多关于并发主题的书籍参见[书单](https://www.douban.com/doulist/41916951/)。

其中[`CompletableFuture (CF)`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- **`Java`标准库内置**
  - 无需额外依赖，几乎总是可用
  - 相信有极高的实现质量
- **广为人知广泛使用，有一流的群众基础**
  - `CompletableFuture`在2014年发布的`Java 8`提供，有～10年了
  - `CompletableFuture`的父接口[`Future`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/Future.html)早在2004年发布的`Java 5`中提供，有～20年了
  - 虽然`Future`接口不支持 执行结果的异步获取与并发执行逻辑的编排，但也让广大`Java`开发者熟悉了`Future`这个典型的概念与工具
- **功能强大、但不会非常庞大复杂**
  - 足以应对日常的业务需求开发
  - 其它的大型并发框架（比如[`Akka`](https://akka.io/)、[`RxJava`](https://github.com/ReactiveX/RxJava)）在使用上需要理解的内容要多很多
  - 当然基本的并发关注方面及其复杂性，与具体使用哪个工具无关，都是要理解与注意的
- **高层抽象**
  - 或说 以业务流程的形式表达技术的并发流程
  - 可以不使用繁琐易错的基础并发协调工具，如[`CountDownLatch`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/CountDownLatch.html)、锁（[`Lock`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/locks/package-summary.html)）、信号量（[`Semaphore`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/Semaphore.html)）

和其它并发工具、框架一样，`CompletableFuture` 用于

- 并发执行业务逻辑，或说编排并发的处理流程/处理任务
- 利用多核并行处理
- 提升业务响应性

值得更深入了解和应用。 💕

## 👥 User Guide

### 新增功能

- 支持设置缺省的业务线程池
  - `CompletableFuture`的缺省线程池是`ForkJoinPool.commonPool()`，这个线程池差不多`CPU`个线程，合适执行`CPU`密集的任务。
  - 对于业务逻辑往往有很多等待操作（如网络`IO`、阻塞等待），并不是`CPU`密集的；使用这个缺省线程池`ForkJoinPool.commonPool()`很危险❗️  
    所以每次调用`CompletableFuture`的`*async`方法时，都传入业务线程池，很繁琐易错 🤯
  - `Cffu`支持设置缺省的业务线程池，规避上面的繁琐与危险
- 一等公民支持`Kotlin` 🍩
- `cffuAllOf`方法
  - 运行多个`CompletableFuture`并返回结果的`allOf`方法
- `cffuAnyOf`方法
  - 返回具体类型的`anyOf`方法
- `cffuCombine(...)`方法
  - 运行多个(2 ~ 5个)不同类型的`CompletableFuture`，返回结果元组
- `cffuJoin(timeout, unit)`方法
  - 支持超时的`join`的方法；就像`cf.get(timeout, unit)` 之于 `cf.get()`
  - `CompletableFuture`缺少这个功能，`cf.join()`会「不超时永远等待」很危险❗️

### Backport支持`Java 8`

Backport`Java 9+`高版本的所有`CompletableFuture`新功能，在`Java 8`可以直接使用。

其中重要的Backport功能有：

- 超时控制：`orTimeout(...)`/`completeOnTimeout(...)`方法
- 延迟执行：`delayedExecutor(...)`方法
- 工厂方法：`failedFuture(...)`/`completedStage(...)`/`failedStage(...)`

### 业务使用中`CompletableFuture`所缺失的功能介绍

- 运行多个`CompletableFuture`并返回结果的`allOf`方法：
  - `resultAllOf`方法，运行多个**相同**结果类型的`CompletableFuture`
    - `CompletableFuture<List<T>> resultAllOf(CompletableFuture<T>... cfs)`
    - `CompletableFuture<List<T>> resultAllOf(List<? extends CompletableFuture<T>> cfs)`
  - `resultOf`方法，运行多个**不同**结果类型的`CompletableFuture`
    - `CompletableFuture<Pair<T1, T2>> resultOf(CompletableFuture<T1> cf1, CompletableFuture<T2> cf2)`
    - `CompletableFuture<Triple<T1, T2, T3>> resultOf(CompletableFuture<T1> cf1, CompletableFuture<T2> cf2, CompletableFuture<T3> cf3)`
- 具体类型的`anyOf`方法：
  - 提供的方法：
    - `CompletableFuture<T> anyOf(CompletableFuture<T>... cfs)`
    - `CompletableFuture<T> anyOf(List<? extends CompletableFuture<T>> cfs)`
  - `CF`返回的类型是`Object`，丢失具体类型：
    - `CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs)`

实现所在的类：

- [`Cffu.java`](cffu-core/src/main/java/io/foldright/cffu/Cffu.java)
- [`CffuFactory.java`](cffu-core/src/main/java/io/foldright/cffu/CffuFactory.java)

## 🎪 使用示例

### `Java`

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
      sleep(1_000);
      throw new RuntimeException("Bang!");
    });

    final Cffu<Integer> combined = longTaskA.thenCombine(longTaskB, Integer::sum)
        .orTimeout(1500, TimeUnit.MILLISECONDS);
    System.out.println("combined result: " + combined.get());
    final Cffu<Integer> anyOfSuccess = cffuFactory.cffuAnyOfSuccess(longTaskC, longFailedTask);
    System.out.println("anyOfSuccess result: " + anyOfSuccess.get());

    ////////////////////////////////////////
    // cleanup
    ////////////////////////////////////////
    myBizThreadPool.shutdown();
  }
}
```

\# 完整可运行的Demo代码参见[`CffuDemo.java`](demos/cffu-demo/src/main/java/io/foldright/demo/cffu/CffuDemo.java)。

### `Kotlin`

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

  ////////////////////////////////////////
  // cleanup
  ////////////////////////////////////////
  myBizThreadPool.shutdown()
}
```

\# 完整可运行的Demo代码参见[`CffuDemo.kt`](demos/cffu-kotlin-demo/src/main/java/io/foldright/demo/cffu/kotlin/CffuDemo.kt)。

## 🔌 API Docs

- 当前版本的`Java API`文档： https://foldright.io/cffu/apidocs/
- 当前版本的`Kotlin API`文档： https://foldright.io/cffu/dokka/

## 🍪依赖

> 可以在 [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions) 查看最新版本与可用版本列表。

- `cffu`库（包含[`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/CompletableFuture.html)的增强`CompletableFutureUtils`）:
  - For `Maven` projects:

    ```xml

    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu</artifactId>
      <version>0.9.7</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu:0.9.7")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu:0.9.7'
    ```
- `cffu Kotlin`支持库:
  - For `Maven` projects:

    ```xml

    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-kotlin</artifactId>
      <version>0.9.7</version>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation("io.foldright:cffu-kotlin:0.9.7")
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation 'io.foldright:cffu-kotlin:0.9.7'
    ```
- `cffu bom`:
  - For `Maven` projects:

    ```xml

    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-bom</artifactId>
      <version>0.9.7</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    implementation(platform("io.foldright:cffu-bom:0.9.7"))
    ```
    ```groovy
    // Gradle Groovy DSL
    implementation platform('io.foldright:cffu-bom:0.9.7')
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local)的[`cffu executor wrapper SPI`实现](cffu-ttl-executor-wrapper)：
  - For `Maven` projects:

    ```xml

    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-ttl-executor-wrapper</artifactId>
      <version>0.9.7</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    ```groovy
    // Gradle Kotlin DSL
    runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:0.9.7")
    ```
    ```groovy
    // Gradle Groovy DSL
    runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:0.9.7'
    ```

## 📚 更多资料

- [`CompletableFuture` Guide](docs/completable-future-guide.md)
  - 完备说明`CompletableFuture`的使用方式
  - 给出 最佳实践建议 与 使用陷阱注意
  - 期望在业务中，更有效安全地使用`CompletableFuture`

## 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，想到了《功夫熊猫》里可爱的小浣熊师傅吧～ 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
