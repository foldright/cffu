# <div align="center"><a href="#dummy"><img src="docs/logo.png" alt="🦝 CompletableFuture Fu (CF-Fu)"></a></div>

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=main&logo=github&logoColor=white" alt="Github Workflow Build Status"></a>
<a href="https://openjdk.java.net/"><img src="https://img.shields.io/badge/Java-8+-green?logo=openjdk&logoColor=white" alt="Java support"></a>
<a href="https://www.apache.org/licenses/LICENSE-2.0.html"><img src="https://img.shields.io/github/license/foldright/cffu?color=4D7A97&logo=apache" alt="License"></a>
<a href="https://github.com/foldright/cffu/stargazers"><img src="https://img.shields.io/github/stars/foldright/cffu" alt="GitHub Stars"></a>
<a href="https://github.com/foldright/cffu/fork"><img src="https://img.shields.io/github/forks/foldright/cffu" alt="GitHub Forks"></a>
<a href="https://github.com/foldright/cffu/issues"><img src="https://img.shields.io/github/issues/foldright/cffu" alt="GitHub issues"></a>
<a href="https://github.com/foldright/cffu/graphs/contributors"><img src="https://img.shields.io/github/contributors/foldright/cffu" alt="GitHub Contributors"></a>
<a href="https://gitpod.io/#https://github.com/foldright/cffu"><img src="https://img.shields.io/badge/Gitpod-ready--to--code-green?label=gitpod&logo=gitpod&logoColor=white" alt="gitpod: Ready to Code"></a>
<a href="https://github.com/foldright/cffu"><img src="https://img.shields.io/github/repo-size/foldright/cffu" alt="GitHub repo size"></a>
</p>

<a href="#dummy"><img src="docs/shifu1.png" width="15%" alt="shifu" align="right" /></a>

如何管理并发执行 是一个复杂易错的问题，业界有大量的工具、框架可以采用。

其中[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- `Java`标准库内置
  - 无需额外依赖，几乎总是可用
  - 广为人知广泛使用，有一流的群众基础
    - `CompletableFuture`的父接口[`Future`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Future.html)早在`Java 5`就有了（`Java 5`在2004年发布，有～20年了）
    - 虽然`Java 5`的`Future`接口不支持 执行结果异步获取与异步执行逻辑编排，但也让广大`Java`开发者熟悉了`Future`这个典型的工具与概念
  - 相信有极高质量的实现
- 功能强大、但不会非常庞大复杂
  - 足以应对日常的业务需求开发
  - 像大型并发框架（如`AKKA`）使用上需要理解的内容要多很多
  - 当然基本的并发关注方面的复杂性，与具体使用哪个工具无关，都是要理解与注意的

`CompletableFuture`用于

- 编排业务处理流程（或说串接业务处理任务）
- 以并发的方式执行业务逻辑，从而提升业务响应性

值得更深入了解和应用。 💕

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [🎯 目标](#-%E7%9B%AE%E6%A0%87)
- [📚 `CompletableFuture` Guide](#-completablefuture-guide)
  - [0. `CompletableFuture`并发执行的关注方面](#0-completablefuture%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%9A%84%E5%85%B3%E6%B3%A8%E6%96%B9%E9%9D%A2)
    - [0.1 输入输出的数据类型](#01-%E8%BE%93%E5%85%A5%E8%BE%93%E5%87%BA%E7%9A%84%E6%95%B0%E6%8D%AE%E7%B1%BB%E5%9E%8B)
  - [1. `CompletableFuture`的创建](#1-completablefuture%E7%9A%84%E5%88%9B%E5%BB%BA)
  - [2. 设置与读取方法](#2-%E8%AE%BE%E7%BD%AE%E4%B8%8E%E8%AF%BB%E5%8F%96%E6%96%B9%E6%B3%95)
  - [3. 流程编排](#3-%E6%B5%81%E7%A8%8B%E7%BC%96%E6%8E%92)
- [📦 库功能](#-%E5%BA%93%E5%8A%9F%E8%83%BD)
- [👋 关于库名](#-%E5%85%B3%E4%BA%8E%E5%BA%93%E5%90%8D)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

## 🎯 目标

- 作为文档库：
  - 完备说明`CompletableFuture`的使用方式
  - 给出 最佳实践建议 与 使用陷阱注意
  - 期望在业务中，有效地使用`CompletableFuture`
  - 这部分是主要目标
- 作为代码库：
  - 补齐在业务使用中`CompletableFuture`所缺失的功能
  - 期望在业务中，更方便自然地使用`CompletableFuture`
  - 这部分只是甜点目标

## 📚 `CompletableFuture` Guide

**_WIP..._**

### 0. `CompletableFuture`并发执行的关注方面

`CompletableFuture`任务执行/流程编排，即执行提交的代码逻辑/计算/任务，涉及下面几个方面：

- 输入输出的数据类型，aka. 参数类型与结果类型
- 指定`executor`（用于执行提交的任务）
- 错误处理/报错反馈
- 超时处理
  - 处理超时是并发的基础关注方面之一
  - 在实现上可以看成`CF`的使用方式
  - `Java 9`通过新增的`completeOnTimeout(...)/orTimeout(...)/delayedExecutor(...)`方法提供了内置支持

<img src="docs/cf-graph.png" width="200" alt="cf-graph" />

#### 0.1 输入输出的数据类型

### 1. `CompletableFuture`的创建

通过静态工厂方法（🅵actory）或构造函数（🅒onstructor）来创建`CompletableFuture`。这些方法是`CompletableFuture`链的起始。

| Method Name                                                                                          | 🅒/🅵 | 结果类型   | `Executor`       |                                            |
|------------------------------------------------------------------------------------------------------|-------|--------|------------------|--------------------------------------------|
| `CompletableFuture<T>()`<sup><b><i>〚1〛</i></b></sup>                                                 | 🅒    | `T`    | 无需               | 显式通过`CF`对象的写方法完成，无需`Executor`来运行           |
| `CompletableFuture<U> newIncompleteFuture()`<sup><b><i>J9</i></b></sup><sup><b><i>〚1〛</i></b></sup>  | 🅵    | `T`    | 无需               | 同上，只是形式上是静态工厂方式                            |
| `CompletionStage<T> minimalCompletionStage()`<sup><b><i>J9</i></b></sup><sup><b><i>〚1〛</i></b></sup> | 🅵    | `T`    | 无需               | 同上，只是返回的类型是`CompletionStage`而不`CF`         |
|                                                                                                      |       |        |                  |                                            |
| `completedFuture(U value)`                                                                           | 🅵    | `U`    | 无需               | 用入参`value`直接创建一个已完成的`CF`，无需`Executor`来运行   |
| `completedStage(U value)`<sup><b><i>J9</i></b></sup>                                                 | 🅵    | `U`    | 无需               | 与上一方法一样，只是返回的类型是`CompletionStage`而不`CF`    |
| `failedFuture(Throwable ex)`<sup><b><i>J9</i></b></sup>                                              | 🅵    | `U`    | 无需               | 用入参`ex`直接创建一个已完成的`CF`，无需`Executor`来运行      |
| `failedStage(Throwable ex)`<sup><b><i>J9</i></b></sup>                                               | 🅵    | `U`    | 无需               | 与上一方法一样，只是返回的类型是`CompletionStage<U>`而不`CF` |
|                                                                                                      |       |        |                  |                                            |
| `supplyAsync(Supplier<U> supplier)`                                                                  | 🅵    | `U`    | `CF`缺省`Executor` |                                            |
| `supplyAsync(Supplier<U> supplier, Executor executor)`                                               | 🅵    | `U`    | `executor`入参     |                                            |
| `runAsync(Runnable runnable)`                                                                        | 🅵    | `Void` | `CF`缺省`Executor` |                                            |
| `runAsync(Runnable runnable, Executor executor)`                                                     | 🅵    | `Void` | `executor`入参     |                                            |
|                                                                                                      |       |        |                  |                                            |
| `CompletableFuture<T> copy()`<sup><b><i>J9</i></b></sup><sup>                                        | 🅵    | `T`    | 无需               |                                            |
|                                                                                                      |       |        |                  |                                            |
| `CompletableFuture<T> copy()`<sup><b><i>J9</i></b></sup><sup>                                        | 🅵    | `T`    | 无需               | 同上，只是形式是静态工厂方式                             |
|                                                                                                      |       |        |                  |                                            |
| `allOf(CompletableFuture<?>... cfs)`<sup><b><i>〚2〛</i></b></sup>                                     | 🅵    | `Void` | 无需               | 组合输入的多个`CF`，本身无执行逻辑，所以无需`Executor`         |
| `anyOf(CompletableFuture<?>... cfs)`<sup><b><i>〚2〛</i></b></sup>                                     | 🅵    | `Void` | 无需               | 同上                                         |

注：

- 〚1〛：在日常的业务开发中使用`CF`来编排业务流程，几乎一定不应该使用 这3个构造类方法。
  - 构造函数创建的`CF`的使用场景：
  - 在已有异步处理线程，即不与`CF`关联的`Executor`，比如在网络`IO`框架的回调（线程）完成处理后，显式调用`CF`对象的写方法设置其它结果；
  - 往往是在中间件中会有必要这样使用。
- 〚2〛：`allOf`/`anyOf`这个2个方法虽然是静态工厂方法；但不是`CF`链的起点，而是输入多个`CF`，用于编排多路的流程。
  - 在功能与使用的上，应该和下面【3. 流程编排】一节的方法归类在一起。
  - 这2个方法也列在上面的表格，只是为了体现出是静态工厂方法这个特点。

### 2. 显式的读写方法

读方法：

| Method Name                                            | 所属父接口    | Blocking?                                 |                                                                         |
|--------------------------------------------------------|----------|-------------------------------------------|-------------------------------------------------------------------------|
| `boolean isDone()`                                     | `Future` | nonblocking                               |                                                                         |
|                                                        |          |                                           |                                                                         |
| `T join()`                                             | -        | **BLOCKING!**                             |                                                                         |
| `T get()`                                              | `Future` | **BLOCKING!**                             |                                                                         |
| `T get(long timeout, TimeUnit unit)`                   | `Future` | **BLOCKING!**<sup><b><i>〚1〛</i></b></sup> |                                                                         |
| `T getNow(T valueIfAbsent)`                            | -        | nonblocking                               |                                                                         |
| `T resultNow()`<sup><b><i>J19</i></b></sup>            | `Future` | nonblocking                               | 返回已正常完成`CF`的正常结果；如果`CF`不是正常完成（未完成/被取消/异常完成）则抛出`IllegalStateException`异常 |
|                                                        |          |                                           |                                                                         |
| `boolean isCompletedExceptionally()`                   | -        | nonblocking                               |                                                                         |
| `Throwable exceptionNow()`<sup><b><i>J19</i></b></sup> | `Future` | nonblocking                               | 返回已异常完成`CF`的出错异常；如果`CF`不是异常完成（未完成/被取消/正常完成）则抛出`IllegalStateException`异常 |
|                                                        |          |                                           |                                                                         |
| `boolean isCancelled()`                                | -        | nonblocking                               |                                                                         |
|                                                        |          |                                           |                                                                         |
| `State state()`<sup><b><i>J19</i></b></sup>            | `Future` | nonblocking                               |                                                                         |
|                                                        |          |                                           |                                                                         |
| `int getNumberOfDependents()`                          | -        | nonblocking                               |                                                                         |

注：

- 〚1〛：`T get(long timeout, TimeUnit unit)`如果设置的超时是`0`，不会BLOCKING；但这个情况下应该调用`T getNow(T valueIfAbsent)`。

写方法：

| Method Name                                                                                   | 所属父接口    | Blocking?   |                   |
|-----------------------------------------------------------------------------------------------|----------|-------------|-------------------|
| `boolean complete(T value)`                                                                   | -        | nonblocking |                   |
| `completeAsync(Supplier<? extends T> supplier)`<sup><b><i>J9</i></b></sup>                    | -        | nonblocking | 方法返回`this`，方便链式调用 |
| `completeAsync(Supplier<? extends T> supplier, Executor executor)`<sup><b><i>J9</i></b></sup> | -        | nonblocking | 同上                |
|                                                                                               |          |             |                   |
| `boolean completeExceptionally(Throwable ex)`                                                 | -        | nonblocking |                   |
| `exceptionallyAsync(Function<Throwable, ? extends T> fn)`                                     | -        | nonblocking |                   |
|                                                                                               |          |             |                   |
| `boolean cancel(boolean mayInterruptIfRunning)`                                               | `Future` | nonblocking |                   |
| `void obtrudeValue(T value)`                                                                  | -        | nonblocking |                   |
| `void obtrudeException(Throwable ex)`                                                         | -        | nonblocking |                   |
|                                                                                               |          |             |                   |

### 3. 流程编排

| Method Name                                                                                | 所属父接口 | Blocking?   |     |
|--------------------------------------------------------------------------------------------|-------|-------------|-----|
| `exceptionallyAsync(Function<Throwable, ? extends T> fn)`                                  | -     | nonblocking |     |
|                                                                                            |       |             |     |
|                                                                                            |       |             |     |
| `completeOnTimeout(T value, long timeout, TimeUnit unit)`<sup><b><i>J9</i></b></sup>       | -     | nonblocking |     |
| `orTimeout(long timeout, TimeUnit unit)`<sup><b><i>J9</i></b></sup>                        | -     | nonblocking |     |
| `Executor defaultExecutor()`<sup><b><i>J9</i></b></sup>                                    | -     | nonblocking |     |
| `delayedExecutor(long delay, TimeUnit unit, Executor executor)`<sup><b><i>J9</i></b></sup> | -     | nonblocking |     |

## 📦 库功能

**_WIP..._**

[`Cffu.java`](src/main/java/io/foldright/cffu/Cffu.java)：

- 支持运行多个`CompletableFuture`并返回结果的`allOf`方法：
  - `resultAllOf`方法，运行多个**相同**结果类型的`CompletableFuture`
  - `resultOf`方法，运行多个**不同**结果类型的`CompletableFuture`

## 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，想到了《功夫熊猫》里可爱的浣熊师傅～ 🦝

<a href="#dummy"><img src="docs/shifu1.png" width="250" alt="shifu" /></a>
