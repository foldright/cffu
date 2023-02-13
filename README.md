# 🦝 CompletableFuture-Fu (CF-Fu)

并发是一个复杂问题，业界有大量的工具、框架可以采用。

其中[`CompletableFuture`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- `Java`标准库内置
  - 无需额外依赖，几乎总是可用
  - 广为人知广泛使用，有一流的群众基础
  - 相信有极高质量的实现
- 功能强大、但不会非常庞大复杂
  - 像大型并发框架（如`AKKA`）使用上需要理解的内容要多很多
  - 当然基本的并发关注方面的复杂性，与具体使用哪个工具无关，都是要理解与注意的

值得更深入了解和应用。 💕

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

## 🔧 库功能

**_`WIP...`_**

[`Cffu.java`](src/main/java/io/foldright/cffu/Cffu.java)：

- 支持运行多个`CompletableFuture`并返回结果的`allOf`方法：
  - `resultAllOf`方法，运行多个**相同**结果类型的`CompletableFuture`
  - `resultOf`方法，运行多个**不同**结果类型的`CompletableFuture`

## 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，《功夫熊猫》里可爱的浣熊师傅～ 🦝

<img src="docs/shifu1.png" width="300" alt="shifu" />
