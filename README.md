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
<a href="https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions"><img src="https://img.shields.io/maven-central/v/io.foldright/cffu?logo=apache-maven&logoColor=white" alt="Maven Central"></a>
<a href="https://github.com/foldright/cffu/releases"><img src="https://img.shields.io/github/release/foldright/cffu.svg" alt="GitHub Releases"></a>
<a href="https://github.com/foldright/cffu/stargazers"><img src="https://img.shields.io/github/stars/foldright/cffu" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/cffu/fork"><img src="https://img.shields.io/github/forks/foldright/cffu" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/cffu/issues"><img src="https://img.shields.io/github/issues/foldright/cffu" alt="GitHub Issues"></a>
<a href="https://github.com/foldright/cffu/graphs/contributors"><img src="https://img.shields.io/github/contributors/foldright/cffu" alt="GitHub Contributors"></a>
<a href="https://github.com/foldright/cffu"><img src="https://img.shields.io/github/repo-size/foldright/cffu" alt="GitHub repo size"></a>
<a href="https://gitpod.io/#https://github.com/foldright/cffu"><img src="https://img.shields.io/badge/Gitpod-ready to code-339933?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
</p>

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="20%" align="right" alt="shifu" /></a>

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

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🎯 〇、目标](#-%E3%80%87%E7%9B%AE%E6%A0%87)
- [🦮 一、`CompletableFuture` Guide](#-%E4%B8%80completablefuture-guide)
  - [🔠 `CF`并发执行的描述及其用语](#-cf%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%9A%84%E6%8F%8F%E8%BF%B0%E5%8F%8A%E5%85%B6%E7%94%A8%E8%AF%AD)
  - [🕹️ `CF`并发执行的关注方面](#-cf%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%9A%84%E5%85%B3%E6%B3%A8%E6%96%B9%E9%9D%A2)
    - [1. 输入输出](#1-%E8%BE%93%E5%85%A5%E8%BE%93%E5%87%BA)
    - [2. 调度](#2-%E8%B0%83%E5%BA%A6)
    - [3. 错误处理](#3-%E9%94%99%E8%AF%AF%E5%A4%84%E7%90%86)
    - [4. 任务执行的超时控制](#4-%E4%BB%BB%E5%8A%A1%E6%89%A7%E8%A1%8C%E7%9A%84%E8%B6%85%E6%97%B6%E6%8E%A7%E5%88%B6)
  - [🔧 `CF`的功能介绍 | 💪 `CF`方法分类说明](#-cf%E7%9A%84%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D---cf%E6%96%B9%E6%B3%95%E5%88%86%E7%B1%BB%E8%AF%B4%E6%98%8E)
  - [📐 `CF`的设计模式 | 🐻 最佳实践与使用陷阱](#-cf%E7%9A%84%E8%AE%BE%E8%AE%A1%E6%A8%A1%E5%BC%8F---%E6%9C%80%E4%BD%B3%E5%AE%9E%E8%B7%B5%E4%B8%8E%E4%BD%BF%E7%94%A8%E9%99%B7%E9%98%B1)
- [📦 二、`cffu`库](#-%E4%BA%8Ccffu%E5%BA%93)
  - [🔧 功能](#-%E5%8A%9F%E8%83%BD)
    - [新功能](#%E6%96%B0%E5%8A%9F%E8%83%BD)
    - [Backport支持`Java 8`](#backport%E6%94%AF%E6%8C%81java-8)
  - [🌿 业务使用中`CompletableFuture`所缺失的功能介绍](#-%E4%B8%9A%E5%8A%A1%E4%BD%BF%E7%94%A8%E4%B8%ADcompletablefuture%E6%89%80%E7%BC%BA%E5%A4%B1%E7%9A%84%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)
  - [🎪 使用示例](#-%E4%BD%BF%E7%94%A8%E7%A4%BA%E4%BE%8B)
    - [`Java`](#java)
    - [`Kotlin`](#kotlin)
  - [🔌 Java API Docs](#-java-api-docs)
  - [🍪依赖](#%E4%BE%9D%E8%B5%96)
- [👋 ∞、关于库名](#-%E2%88%9E%E5%85%B3%E4%BA%8E%E5%BA%93%E5%90%8D)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

# 🎯 〇、目标

- 作为文档库（即`CompletableFuture` Guide）：
  - 完备说明`CompletableFuture`的使用方式
  - 给出 最佳实践建议 与 使用陷阱注意
  - 期望在业务中，更有效安全地使用`CompletableFuture`
- 作为代码库（即`cffu`库）：
  - 补齐在业务使用中`CompletableFuture`所缺失的功能
  - 期望在业务中，更方便自然地使用`CompletableFuture`

# 🦮 一、`CompletableFuture` Guide

> 为了阅读的简洁方便，后文`CompletableFuture`会简写成`CF`。

## 🔠 `CF`并发执行的描述及其用语

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230757830-397055e9-b701-4074-bbb9-dc227fd0f8f2.png" width="22%" align="right" alt="cf-graph" /></a>

基本概念与术语：

- 任务（`Task`）/ 计算（`Computation`）
  - 任务逻辑（`Task Logic`）/ 业务逻辑（`Biz Logic`）
  - 执行（`Execute`）任务
- 状态（`State`）
  - 运行中（`Running`）<sup><b><i>〚1〛</i></b></sup>
  - 取消（`Cancelled`）<sup><b><i>〚2〛</i></b></sup>
  - 完成（`Completed` / `Done`）
    - 成功（`Success` / `Successful`）/ 正常完成（`Completed Normally`）/ 成功完成（`Completed Successfully`）
    - 失败（`Failed` / `Fail`）/ 异常完成（`Completed Exceptionally`）
- 状态转变（`Transition`）
  - 事件（`Event`）、触发（`Trigger`）
- 业务流程（`Biz Flow`）、`CF`链（`Chain`）
  - 流程图（`Flow Graph`）、有向无环图 / `DAG`
    - 为什么构建的`CF`链一定是`DAG`？
  - 流程编排（`Flow Choreography`）
- 前驱（`Predecessor`）/ 后继（`Successor`）
  - 上游任务 / 前驱任务 / `Dependency Task`（我依赖的任务）
  - 下游任务 / 后继任务 / `Dependent Task`（依赖我的任务）

> 注：上面用`/`隔开的多个词是，在表述`CF`同一个概念时，会碰到的多个术语；在不影响理解的情况下，后文会尽量统一用第一个词来表达。

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230757836-ea49a8cb-9e68-40c8-9efa-c3e880eecde4.png" width="30%" align="right"  alt="task stauts transition"></a>

更多说明：

- 〚1〛 任务状态有且有只有 运行中（`Running`）、取消（`Cancelled`）、完成（`Completed`）这3种状态。
  - 对于「完成」状态，进一步可以分成 成功（`Success`）、失败（`Failed`）2种状态。
- 所以也可以说，任务状态有且只有 运行中、取消、成功、失败 这4种状态。
  - 右图是任务的状态及其转变图。
  - 在概念上`CF`的状态转变只能是单次单向的，这很简单可靠、也容易理解并和使用直觉一致。
  - > 注：虽然下文提到的`obtrudeValue()`/`obtrudeException`方法可以突破`CF`概念上的约定，但这2个后门方法在正常设计实现中不应该会用到，尤其在业务使用应该完全忽略；带来的问题也由使用者自己了解清楚并注意。
- 〚2〛 关于「取消」状态：
  - 对于`CompletableFuture`，取消的实现方式是设置[`CancellationException`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/concurrent/CancellationException.html)异常。
- 对于「取消」状态，或说设置了「`CancellationException`」失败异常的`CompletableFuture cf`，相比其它异常失败 / 设置了其它失败异常 的情况，不一样的地方：
  - 调用`cf.get()` / `cf.get(timeout, unit)`方法
    - 会抛出`CancellationException`异常
    - 其它异常失败时，这2个方法抛出的是包了一层的`ExecutionException`，`cause`是实际的失败异常
  - 调用`cf.join()` / `cf.getNow(valueIfAbsent)`方法
    - 会抛出`CancellationException`异常
    - 其它异常失败时，这2个方法抛出的是包了一层的`CompletionException`，`cause`是实际的失败异常
  - 调用`cf.exceptionNow()`方法
    - 会抛出`IllegalStateException`，而**不是返回**`cf`所设置的`CancellationException`异常
    - 其它异常失败时，`exceptionNow()`返回设置的异常
  - 调用`cf.isCancelled()`方法
    - 返回`true`
    - 其它异常失败时，`isCancelled()`返回`false`
- 其它地方，`CancellationException`异常与其它异常是一样处理的。比如：
  - 调用`cf.resultNow()`方法  
    都是抛出`IllegalStateException`异常
  - 调用`cf.isDone()`、`cf.isCompletedExceptionally()`  
    都是返回`true`
  - `CompletionStage`接口方法对异常的处理，如  
    `cf.exceptionally()`的方法参数`Function<Throwable, T>`所处理的都是直接设置的异常对象没有包装过

## 🕹️ `CF`并发执行的关注方面

`CF`任务执行/流程编排，即执行提交的代码逻辑/计算/任务，涉及下面4个方面：

- **任务的输入输出**
  - 即`CF`所关联任务的输入参数/返回结果（及其数据类型）
- **任务的调度，即在哪个线程来执行任务**。可以是
  - 在触发的线程中就地连续执行任务
  - 在指定`Executor`（的线程）中执行任务
- **任务的错误处理**（任务运行出错）
- **任务的超时控制**
  - 超时控制是并发的基础关注方面之一
  - 到了`Java 9`提供了内置支持，新增了`completeOnTimeout(...)`/`orTimeout(...)`方法

> 本节「并发关注方面」，会举例上一些`CF`方法名，以说明`CF`方法的命名模式；  
> 可以先不用关心方法的具体功能，在「`CF`的功能介绍」中会分类展开说明`CF`方法及其功能。

### 1. 输入输出

对应下面4种情况：

- **无输入无返回（00）**
  - 对应`Runnable`接口（包含单个`run`方法）
- **无输入有返回（01）**
  - 对应[`Supplier<O>`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/Supplier.html)接口（包含单个`supply`方法）
- **有输入无返回（10）**
  - 对应[`Consumer<I>`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/Consumer.html)接口（包含单个`accept`方法）
- **有输入有返回（11）**
  - 对应[`Function<I, O>`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/Function.html)接口（包含单个`apply`方法）

注：

- 对于有输入或返回的接口（即除了`Runnable`接口）
  - 都是泛型的，所以可以支持不同的具体数据类型
  - 都是处理单个输入数据
  - 如果要处理两个输入数据，即有两个上游`CF`的返回，会涉及下面的变体接口
- 对于有输入接口，有两个输入参数的变体接口：
  - `Consumer`接口的两参数变体接口：[`BiConsumer<I1, I2>`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/BiConsumer.html)
  - `Function`接口的两参数变体接口：[`BiFunction<I1, I2, O>`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/function/BiFunction.html)

----------------------------------------

`CF`通过其方法名中包含的用词来体现：

- **`run`：无输入无返回（00）**
  - 即是`Runnable`接口包含的`run`方法名
  - 相应的`CF`方法名的一些例子：
    - `runAsync(Runnable runnable)`
    - `thenRun(Runnable action)`
    - `runAfterBoth(CompletionStage<?> other, Runnable action)`
    - `runAfterEitherAsync(CompletionStage<?> other, Runnable action)`
- **`supply`：无输入有返回（01）**
  - 即是`Supplier`接口包含的`supply`方法名
  - 相应的`CF`方法名的一些例子：
    - `supplyAsync(Supplier<U> supplier)`
    - `supplyAsync(Supplier<U> supplier, Executor executor)`
- **`accept`：有输入无返回（10）**
  - 即是`Consumer`接口包含的`accept`方法名
  - 相应的`CF`方法名的一些例子：
    - `thenAccept(Consumer<T> action)`
    - `thenAcceptAsync(Consumer<T> action)`
    - `thenAcceptBoth(CompletionStage<U> other, BiConsumer<T, U> action)`
    - `acceptEitherAsync(CompletionStage<T> other, Consumer<T> action)`
- **`apply`：有输入有返回（11）**
  - 即是`Function`接口包含的`apply`方法名。`CF`的方法如
  - 相应的`CF`方法名的一些例子：
    - `thenApply(Function<T, U> fn)`
    - `thenApplyAsync(Function<T, U> fn)`
    - `applyToEither(CompletionStage<T> other, Function<T, U> fn)`

### 2. 调度

任务调度是指，任务在哪个线程执行。有2种方式：

- 在触发的线程中就地连续执行任务
- 在指定`Executor`（的线程）中执行任务

`CF`通过方法名后缀`Async`来体现调度方式：

- **有方法名后缀`Async`**：
  - 在触发`CF`后，任务在指定`Executor`执行
    - 如果不指定`executor`参数，缺省是`ForkJoinPool.commonPool()`
  - 相应的`CF`方法名的一些例子：
    - `runAsync(Runnable runnable)`
    - `thenAcceptAsync(Consumer<T> action, Executor executor)`
    - `runAfterBothAsync(CompletionStage<?> other, Runnable action)`
- **无方法名后缀`Async`**：
  - 任务在触发线程就地连续执行
  - 相应的`CF`方法名的一些例子：
    - `thenAccept(Consumer<T> action)`
    - `thenApply(Function<T, U> fn)`
    - `applyToEither(CompletionStage<T> other, Function<T, U> fn)`

### 3. 错误处理

提交给`CF`的任务可以运行出错（抛出异常），即状态是失败（`Failed`）或取消（`Cancelled`）。

对于直接读取结果的方法：

- 读取 成功结果的方法，如
  `cf.get()`、`cf.join()`会抛出异常（包装的异常）来反馈
- 读取 失败结果的方法，如
  `cf.exceptionNow()`会返回结果异常或是抛出异常来反馈

对于`CompletionStage`接口中编排执行的方法，会根据方法的功能 是只处理成功结果或失败结果一者，或是同时处理成功失败结果二者。如

- `exceptionally(...)`只处理 失败结果
- `whenComplete(...)`/`handle(...)`同时处理 成功与失败结果；
  - 这2个方法的参数`lamdba`（`BiConsumer`/`BiFunction`）同时输入成功失败结果2个参数：`value`与`exception`
- 其它多数的方法只处理 成功结果
- 对于不处理的结果，效果上就好像  
  没有调用这个`CompletionStage`方法一样，即短路`bypass`了 👏

### 4. 任务执行的超时控制

超时控制是并发的基础关注方面之一。

到了`Java 9`提供了内置支持，新增了`completeOnTimeout(...)`/`orTimeout(...)`方法。

> `CF`的超时控制，在实现上其实可以看成是`CF`的使用方式，并不是`CF`要实现基础能力；即可以通过其它已有的`CF`功能，在`CF`外围实现。

## 🔧 `CF`的功能介绍 | 💪 `CF`方法分类说明

见子文档页  [`cf-functions-intro.md`](docs/cf-functions-intro.md)

`CF`的方法个数比较多，所以介绍内容有些多，内容继续完善中… 💪 💕

## 📐 `CF`的设计模式 | 🐻 最佳实践与使用陷阱

见子文档页 [`cf-design-patterns.md`](docs/cf-design-patterns.md)

还没有什么内容，收集思考展开中… 💪 💕

# 📦 二、`cffu`库

## 🔧 功能

### 新功能

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
- 延迟执行：`defaultExecutor(...)`方法
- 工厂方法：`failedFuture(...)`/`completedStage(...)`/`failedStage(...)`

## 🌿 业务使用中`CompletableFuture`所缺失的功能介绍

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
import io.foldright.cffu.Cffu;
import io.foldright.cffu.CffuFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder;


public class Demo {
    private static final ExecutorService myBizThreadPool = Executors.newFixedThreadPool(42);

    // Create a CffuFactory with configuration of the customized thread pool
    private static final CffuFactory cffuFactory = newCffuFactoryBuilder(myBizThreadPool).build();

    public static void main(String[] args) throws Exception {
        Cffu<Integer> cf42 = cffuFactory
                .supplyAsync(() -> 21) // Run in myBizThreadPool
                .thenApply(n -> n * 2);

        // Run in myBizThreadPool
        Cffu<Integer> longTaskA = cf42.thenApplyAsync(n -> {
            sleep(1001);
            return n / 2;
        });

        // Run in myBizThreadPool
        Cffu<Integer> longTaskB = cf42.thenApplyAsync(n -> {
            sleep(1002);
            return n / 2;
        });

        Cffu<Integer> finalCf = longTaskA.thenCombine(longTaskB, Integer::sum)
                .orTimeout(2, TimeUnit.SECONDS);

        Integer result = finalCf.get();
        System.out.println(result);

        ////////////////////////////////////////
        // cleanup
        ////////////////////////////////////////
        myBizThreadPool.shutdown();
    }

    static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
```

\# 完整可运行的Demo代码参见[`Demo.java`](cffu-core/src/test/java/io/foldright/demo/Demo.java)。

### `Kotlin`

```kt
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.foldright.cffu.kotlin.allOfCffu
import java.lang.Thread.sleep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


private val myBizThreadPool: ExecutorService = Executors.newFixedThreadPool(42)

// Create a CffuFactory with configuration of the customized thread pool
private val cffuFactory: CffuFactory = newCffuFactoryBuilder(myBizThreadPool).build()

fun main() {
    val cf42 = cffuFactory
        .supplyAsync { 21 }     // Run in myBizThreadPool
        .thenApply { it * 2 }

    listOf(
        // Run in myBizThreadPool
        cf42.thenApplyAsync { n: Int ->
            sleep(1001)
            n / 2
        },
        // Run in myBizThreadPool
        cf42.thenApplyAsync { n: Int ->
            sleep(1002)
            n / 2
        },
    ).allOfCffu(cffuFactory).thenApply(List<Int>::sum).orTimeout(2, TimeUnit.SECONDS).get().let(::println)

    ////////////////////////////////////////
    // cleanup
    ////////////////////////////////////////
    myBizThreadPool.shutdown()
}
```

\# 完整可运行的Demo代码参见[`Demo.kt`](cffu-core/src/test/java/io/foldright/demo/Demo.kt)。

## 🔌 Java API Docs

当前版本的Java API文档地址： <https://foldright.io/cffu/apidocs/>

## 🍪依赖

For `Maven` projects:

```xml

<dependency>
  <groupId>io.foldright</groupId>
  <artifactId>cffu</artifactId>
  <version>0.9.5</version>
</dependency>
```

For `Gradle` projects:

```groovy
// Gradle Kotlin DSL
implementation("io.foldright:cffu:0.9.5")
```

```groovy
// Gradle Groovy DSL
implementation 'io.foldright:cffu:0.9.5'
```

可以在 [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/0.9.0/versions) 查看最新版本与可用版本列表。

# 👋 ∞、关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，想到了《功夫熊猫》里可爱的小浣熊师傅吧～ 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="35%" alt="shifu" /></a>
