# 🦝 CompletableFuture Fu (CF-Fu)

<img src="docs/shifu1.png" width="20%" alt="shifu" align="right" />

如何管理并发执行 是一个复杂易错的问题，业界有大量的工具、框架可以采用。

其中[`CompletableFuture`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- `Java`标准库内置
  - 无需额外依赖，几乎总是可用
  - 广为人知广泛使用，有一流的群众基础
    - `CompletableFuture`的父接口[`Future`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Future.html)早在`Java 5`就有了
    - 虽然`Future`不支持异步 获取结果与编排异步执行逻辑，但也让大众熟悉了`Future`的概念
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
- [🐶 `CompletableFuture` Guide](#-completablefuture-guide)
- [🔧 库功能](#-%E5%BA%93%E5%8A%9F%E8%83%BD)
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

## 🐶 `CompletableFuture` Guide

### 0. `CompletableFuture`的创建

通过静态工厂方法（🏭）或构造函数（🅒）来创建`CompletableFuture`。这些方法

| 方法名                                                    |     | 结果类型    | `Executor`        | 备注                                         |
|--------------------------------------------------------|-----|---------|-------------------|--------------------------------------------|
| `CompletableFuture<T>()`                               | 🅒  | 类型参数`T` | 无需                | 显式通过`CF`对象的写方法完成，无需`Executor`来运行           |
|                                                        |     |         |                   |                                            |
| `completedFuture(U value)`                             | 🏭  | 类型参数`U` | 无需                | 用入参`value`构建一个已完成的`CF`，无需`Executor`来运行     |
| `completedStage(U value)`                              | 🏭  | 类型参数`U` | 无需                | 与上一方法一样，只是返回的类型是`CompletionStage<U>`而不`CF` |
| `failedFuture(Throwable ex)`                           | 🏭  | 类型参数`U` | 无需                | 用入参`ex`构建一个已完成的`CF`，无需`Executor`来运行        |
| `failedStage(Throwable ex)`                            | 🏭  | 类型参数`U` | 无需                | 与上一方法一样，只是返回的类型是`CompletionStage<U>`而不`CF` |
|                                                        |     |         |                   |                                            |
| `supplyAsync(Supplier<U> supplier)`                    | 🏭  | 类型参数`U` | `CF`的缺省`Executor` |                                            |
| `supplyAsync(Supplier<U> supplier, Executor executor)` | 🏭  | 类型参数`U` | `executor`参数      |                                            |
| `runAsync(Runnable runnable)`                          | 🏭  | `Void`  | `CF`的缺省`Executor` |                                            |
| `runAsync(Runnable runnable, Executor executor)`       | 🏭  | `Void`  | `executor`参数      |                                            |

### 1. 流程编排

`CompletableFuture`的处理流程编排涉及下面几个方面：

- 输入输出的数据，aka. 参数类型与结果类型
- 错误处理/报错反馈
- `executor`指定

## 🔧 库功能

**_WIP..._**

[`Cffu.java`](src/main/java/io/foldright/cffu/Cffu.java)：

- 支持运行多个`CompletableFuture`并返回结果的`allOf`方法：
  - `resultAllOf`方法，运行多个**相同**结果类型的`CompletableFuture`
  - `resultOf`方法，运行多个**不同**结果类型的`CompletableFuture`

## 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，《功夫熊猫》里可爱的浣熊师傅～ 🦝

<img src="docs/shifu1.png" width="300" alt="shifu" />
