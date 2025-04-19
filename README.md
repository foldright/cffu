# <div align="center"><a href="#dummy"><img src="https://github.com/foldright/cffu/assets/1063891/124658cd-025f-471e-8da1-7eea0e482915" alt="🦝 CompletableFuture-Fu(CF-Fu)"></a></div>

<p align="center">
<a href="https://github.com/foldright/cffu/actions/workflows/fast_ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/fast_ci.yaml?branch=main&logo=github&logoColor=white&label=fast%20ci" alt="Fast Build CI"></a>
<a href="https://github.com/foldright/cffu/actions/workflows/ci.yaml"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/ci.yaml?branch=main&logo=github&logoColor=white&label=strong%20ci" alt="Strong Build CI"></a>
<a href="https://app.codecov.io/gh/foldright/cffu/tree/main"><img src="https://img.shields.io/codecov/c/github/foldright/cffu/main?logo=codecov&logoColor=white" alt="Codecov"></a>
<a href="https://qodana.cloud/projects/A61Yy"><img src="https://img.shields.io/github/actions/workflow/status/foldright/cffu/qodana_code_quality.yml?branch=main&logo=jetbrains&logoColor=white&label=qodana" alt="Qodana Code Inspections"></a>
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

👉 `cffu`（`CompletableFuture-Fu` 🦝）是一个小小的[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)辅助增强库，提升`CF`使用体验并减少误用，在业务中更方便高效安全地使用`CF`。😋🚀🦺

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
  - [1. `cffu`的使用方式](#1-cffu%E7%9A%84%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F)
    - [1.1 推荐`Cffu`类的使用方式 🌟](#11-%E6%8E%A8%E8%8D%90cffu%E7%B1%BB%E7%9A%84%E4%BD%BF%E7%94%A8%E6%96%B9%E5%BC%8F-)
    - [1.2 迁移使用`CompletableFuture`类的代码到使用`Cffu`类](#12-%E8%BF%81%E7%A7%BB%E4%BD%BF%E7%94%A8completablefuture%E7%B1%BB%E7%9A%84%E4%BB%A3%E7%A0%81%E5%88%B0%E4%BD%BF%E7%94%A8cffu%E7%B1%BB)
    - [1.3 库依赖（包含`CompletableFutureUtils`工具类）](#13-%E5%BA%93%E4%BE%9D%E8%B5%96%E5%8C%85%E5%90%ABcompletablefutureutils%E5%B7%A5%E5%85%B7%E7%B1%BB)
  - [2. `cffu`功能介绍](#2-cffu%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)
    - [2.1 支持返回多个输入`CF`的整体运行结果](#21-%E6%94%AF%E6%8C%81%E8%BF%94%E5%9B%9E%E5%A4%9A%E4%B8%AA%E8%BE%93%E5%85%A5cf%E7%9A%84%E6%95%B4%E4%BD%93%E8%BF%90%E8%A1%8C%E7%BB%93%E6%9E%9C)
    - [2.2 支持设置缺省的业务线程池并封装携带](#22-%E6%94%AF%E6%8C%81%E8%AE%BE%E7%BD%AE%E7%BC%BA%E7%9C%81%E7%9A%84%E4%B8%9A%E5%8A%A1%E7%BA%BF%E7%A8%8B%E6%B1%A0%E5%B9%B6%E5%B0%81%E8%A3%85%E6%90%BA%E5%B8%A6)
    - [2.3 高效灵活的并发执行策略（`AllFailFast` / `AnySuccess` / `AllSuccess` / `MostSuccess`）](#23-%E9%AB%98%E6%95%88%E7%81%B5%E6%B4%BB%E7%9A%84%E5%B9%B6%E5%8F%91%E6%89%A7%E8%A1%8C%E7%AD%96%E7%95%A5allfailfast--anysuccess--allsuccess--mostsuccess)
    - [2.4 支持直接运行多个`Action`，而不是要先包装成`CompletableFuture`](#24-%E6%94%AF%E6%8C%81%E7%9B%B4%E6%8E%A5%E8%BF%90%E8%A1%8C%E5%A4%9A%E4%B8%AAaction%E8%80%8C%E4%B8%8D%E6%98%AF%E8%A6%81%E5%85%88%E5%8C%85%E8%A3%85%E6%88%90completablefuture)
    - [2.5 支持处理指定异常类型，而不是处理所有异常`Throwable`](#25-%E6%94%AF%E6%8C%81%E5%A4%84%E7%90%86%E6%8C%87%E5%AE%9A%E5%BC%82%E5%B8%B8%E7%B1%BB%E5%9E%8B%E8%80%8C%E4%B8%8D%E6%98%AF%E5%A4%84%E7%90%86%E6%89%80%E6%9C%89%E5%BC%82%E5%B8%B8throwable)
    - [2.6 `Backport`支持`Java 8`](#26-backport%E6%94%AF%E6%8C%81java-8)
    - [2.7 超时执行安全的`orTimeout` / `completeOnTimeout`新实现](#27-%E8%B6%85%E6%97%B6%E6%89%A7%E8%A1%8C%E5%AE%89%E5%85%A8%E7%9A%84ortimeout--completeontimeout%E6%96%B0%E5%AE%9E%E7%8E%B0)
    - [2.8 支持超时的`join`方法](#28-%E6%94%AF%E6%8C%81%E8%B6%85%E6%97%B6%E7%9A%84join%E6%96%B9%E6%B3%95)
    - [2.9 返回具体类型的`anyOf`方法](#29-%E8%BF%94%E5%9B%9E%E5%85%B7%E4%BD%93%E7%B1%BB%E5%9E%8B%E7%9A%84anyof%E6%96%B9%E6%B3%95)
    - [2.10 输入宽泛类型的`allOf/anyOf`方法](#210-%E8%BE%93%E5%85%A5%E5%AE%BD%E6%B3%9B%E7%B1%BB%E5%9E%8B%E7%9A%84allofanyof%E6%96%B9%E6%B3%95)
    - [更多功能说明](#%E6%9B%B4%E5%A4%9A%E5%8A%9F%E8%83%BD%E8%AF%B4%E6%98%8E)
- [🔌 API Docs](#-api-docs)
- [🍪依赖](#%E4%BE%9D%E8%B5%96)
- [📚 更多资料](#-%E6%9B%B4%E5%A4%9A%E8%B5%84%E6%96%99)
- [👋 关于库名](#-%E5%85%B3%E4%BA%8E%E5%BA%93%E5%90%8D)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

# 🔧 功能

☘️ **补全业务使用中缺失的功能**

- 🏪 更方便的功能，如
  - 支持返回多个输入`CF`的运行结果，而不是返回没有包含输入`CF`的结果（`CompletableFuture#allOf`）  
    如方法`allResultsFailFastOf` / `allResultsOf` / `mSupplyFailFastAsync` / `thenMApplyFailFastAsync`
  - 支持返回多个不同类型的输入`CF`的结果，而不是同一类型  
    如方法`allTupleFailFastOf` / `allTupleOf` / `mSupplyTupleFailFastAsync` / `thenMApplyTupleFailFastAsync`
  - 支持直接运行多个`Action`，而不是要先包装成`CompletableFuture`  
    如方法`mSupplyAsync` / `mRunAsync` / `mSupplyFailFastAsync` / `thenMApplyMostSuccessTupleAsync`
  - 支持设置缺省的业务线程池并封装携带，`CffuFactory#builder(executor)`方法，而不是在异步执行时反复传入业务线程池参数
  - 支持处理指定异常类型的`catching`方法，而不是处理所有异常`Throwable`（`CompletableFuture#exceptionally`）
- 🚦 更高效灵活的并发执行策略，如
  - `AllFailFast`策略：当输入的多个`CF`有失败时快速失败返回，而不再于事无补地等待所有`CF`运行完成（`CompletableFuture#allOf`）
  - `AnySuccess`策略：返回首个成功的`CF`结果，而不是首个完成但可能失败的`CF`（`CompletableFuture#anyOf`）
  - `AllSuccess`策略：返回多个`CF`中成功的结果，对于失败的`CF`返回指定的缺省值
  - `MostSuccess`策略：指定时间内返回多个`CF`中成功的结果，对于失败或超时的`CF`返回指定的缺省值
  - `All(Complete)` / `Any(Complete)`策略：这2个是`CompletableFuture`已有支持的策略
- 🦺 更安全的使用方式，如
  - 超时执行安全的`orTimeout` / `completeOnTimeout`方法新实现  
    `CF#orTimeout` / `CF#completeOnTimeout`方法会导致`CF`的超时与延迟执行基础功能失效❗️
  - 一定不会修改`CF`结果的`peek`处理方法  
    `whenComplete`方法可能会修改`CF`的结果，返回`CF`的结果与输入并不一定一致
  - 支持超时的`join(timeout, unit)`方法
  - 支持禁止强制篡改，`CffuFactoryBuilder#forbidObtrudeMethods`方法
  - 在类方法附加完善的代码质量注解，在编码时`IDE`能尽早提示出问题  
    如`@NonNull`、`@Nullable`、`@CheckReturnValue`、`@Contract`等
- 🧩 缺失的基本功能，除了上面面向安全而新实现的方法，还有
  - 异步异常完成，`completeExceptionallyAsync`方法
  - 非阻塞地获取成功结果，对于失败的或还在运行中的`CF`则返回指定的缺省值，`getSuccessNow`方法
  - 解包装`CF`异常成业务异常，`unwrapCfException`方法

⏳ **`Backport`支持`Java 8`**，`Java 9+`高版本的所有`CF`新功能方法在`Java 8`低版本直接可用，如

- 超时控制：`orTimeout` / `completeOnTimeout`
- 延迟执行：`delayedExecutor`
- 工厂方法：`failedFuture` / `completedStage` / `failedStage`
- 处理操作：`completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`
- 非阻塞读：`resultNow` / `exceptionNow` / `state`

💪 **已有功能的增强**，如

- `anyOf`方法：返回具体类型`T`（类型安全），而不是返回`Object`（`CompletableFuture#anyOf`）
- `allOf` / `anyOf`方法：输入更宽泛的`CompletionStage`参数类型，而不是`CompletableFuture`类（`CompletableFuture#allOf/anyOf`）

更多`cffu`功能及其使用方式的说明参见 [`cffu`功能介绍](#2-cffu%E5%8A%9F%E8%83%BD%E4%BB%8B%E7%BB%8D)。

## 关于`CompletableFuture`

如何管理并发执行是个复杂易错的问题，业界有大量的工具、框架可以采用。

> 并发工具、框架的广度了解，可以看看如《[七周七并发模型](https://book.douban.com/subject/26337939/)》、《[Java虚拟机并发编程](https://book.douban.com/subject/24533312/)》、《[Scala并发编程（第2版）](https://book.douban.com/subject/35448965/)》；更多关于并发主题的书籍参见[书单](https://www.douban.com/doulist/41916951/)。

其中[`CompletableFuture(CF)`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)有其优点：

- **广为人知广泛使用，有一流的群众基础**
  - `CompletableFuture`在2014年发布的`Java 8`提供，有10年了
  - `CompletableFuture`的父接口[`Future`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Future.html)早在2004年发布的`Java 5`中提供，有20年了。虽然`Future`接口不支持运行结果的异步获取与并发执行逻辑的编排，但也让广大`Java`开发者熟悉了`Future`这个典型的概念与工具
- **功能强大、但不会非常庞大复杂**
  - 足以应对日常业务的异步并发需求
  - 其它大型并发框架（比如[`Akka`](https://akka.io/)、[`RxJava`](https://github.com/ReactiveX/RxJava)）在使用上需要理解的内容要多很多。当然基本的并发关注方面及其复杂性，与具体使用哪个工具无关，都是要理解与注意的
- **高层抽象**
  - 或说 以业务流程的形式表达技术的并发流程
  - 可以避免或减少使用繁琐易错的并发协调基础工具：[同步器`Synchronizers`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/package-summary.html#synchronizers-heading)（如[`CountDownLatch`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CountDownLatch.html)、[`CyclicBarrier`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CyclicBarrier.html)、[`Phaser`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/Phaser.html)）、[锁`Locks`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/locks/package-summary.html)和[原子类`atomic`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/atomic/package-summary.html)
- **`Java`标准库内置**
  - 无需额外依赖，几乎总是可用
  - 相信有极高的实现质量

与其它并发工具、框架一样，`CompletableFuture`用于

- 并发执行业务逻辑，或说编排并发处理流程或异步任务
- 多核并行处理，充分利用资源
- 缩短请求响应时间，提升业务响应性

值得更深入地了解和应用。 💕

# 👥 User Guide

## 1. `cffu`的使用方式

- 🦝 **使用`Cffu`类**
- 🔧 **使用`CompletableFutureUtils`工具类**

### 1.1 推荐`Cffu`类的使用方式 🌟

相比调用`CompletableFutureUtils`工具类的静态方法，

- 使用`Cffu`类就像使用`CompletableFuture`一样，新功能作为`Cffu`类的实例方法，可以自然方便地调用
  - `Cffu`类之于工具类`CompletableFutureUtils`，就像`Guava`的`FluentFuture`之于`ListenableFuture`的工具类`Futures`
- `Java`语言不支持在已有类（`CompletableFuture`）上扩展方法，所以需要一个新的包装类（`Cffu`）

如果你不想在项目中引入新类（`Cffu`类）、觉得这样增加了复杂性的话，完全可以将`cffu`库作为一个工具类来用：

- 优化`CompletableFuture`使用的工具方法在业务项目中很常见
- `CompletableFutureUtils`提供了一系列实用高效安全可靠的工具方法
- 这种使用方式有些`cffu`功能没有提供（也没有想到好的实现方案）  
  如支持设置缺省的业务线程池、禁止强制篡改

### 1.2 迁移使用`CompletableFuture`类的代码到使用`Cffu`类

为了方便自然地使用`cffu`库的增强功能与方法，可以迁移使用`CompletableFuture`类的已有代码到`Cffu`类。

1\) 如果可以修改使用`CompletableFuture`的代码

迁移到`Cffu`类，包含2步简单的修改：

- 在类型声明地方，将`CompletableFuture`类改成`Cffu`类
- 在`CompletableFuture`静态方法调用的地方，将类名`CompletableFuture`改成`cffuFactory`实例

> 之所以可以这样迁移，是因为：
>
> - `CompletableFuture`类的所有实例方法都在`Cffu`类中有实现，且有相同的方法签名与功能
> - `CompletableFuture`类的所有静态方法都在`CffuFactory`类中有实现，且有相同的方法签名与功能

2\) 如果不能修改使用`CompletableFuture`的代码（如在外部库中返回的`CF`）

使用[`CffuFactory.toCffu(CompletionStage)`方法](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/CffuFactory.html#toCffu(java.util.concurrent.CompletionStage))，将`CompletableFuture`或`CompletionStage`转换成`Cffu`类型。

### 1.3 库依赖（包含`CompletableFutureUtils`工具类）

- For `Maven` projects:

  ```xml
  <dependency>
    <groupId>io.foldright</groupId>
    <artifactId>cffu</artifactId>
    <version>1.1.8</version>
  </dependency>
  ```
- For `Gradle` projects:

  Gradle Kotlin DSL
  ```groovy
  implementation("io.foldright:cffu:1.1.8")
  ```
  Gradle Groovy DSL
  ```groovy
  implementation 'io.foldright:cffu:1.1.8'
  ```

> `cffu`也支持`Kotlin`扩展方法的使用方式，参见[`cffu-kotlin/README.md`](cffu-kotlin/README.md)；使用方式的对比示例参见[`docs/usage-mode-demo.md`](docs/usage-mode-demo.md)。

## 2. `cffu`功能介绍

### 2.1 支持返回多个输入`CF`的整体运行结果

`CompletableFuture`的`allOf`方法没有返回输入`CF`的运行结果（方法的返回类型是`CF<Void>`）。

由于不能方便地获取输入`CF`的运行结果，需要：

- 在`allOf`方法之后再通过入参`CF`的读操作（如`join` / `get`）来获取结果
  - 操作繁琐 🔧🤯
  - 像`join` / `get`读方法是阻塞的，增加了业务逻辑的死锁风险❗️  
    更多说明可以看看[CompletableFuture原理与实践 - 4.2.2 线程池循环引用会导致死锁](https://juejin.cn/post/7098727514725416967#heading-24)
- 或是在传入的`CompletableFuture Action`中设置外部的变量
  - 需要注意多线程读写的线程安全问题 ⚠️🔀  
    多线程读写涉及多线程数据传递的复杂性，遗漏并发逻辑的数据读写的正确处理是业务代码中的常见问题❗️
  - 并发深坑勿入，并发逻辑复杂易出Bug 🐞  
    如果涉及超时则会更复杂，`JDK CompletableFuture`自身在`Java 21`中也有这方面的[Bug修复](https://github.com/foldright/cffu/releases/tag/v1.0.0-Alpha20) ⏰

`cffu`的`allResultsFailFastOf` / `allResultsOf` / `mostSuccessResultsOf`等方法提供了返回输入`CF`结果的功能。使用这些方法获取输入`CF`的整体运行结果：

- 方便直接
- 因为返回的是有整体结果的`CF`，可以继续串接非阻塞的操作，所以自然减少了阻塞读方法（如`join` / `get`）的使用，尽量降低业务逻辑的死锁风险
- 规避了在业务逻辑中直接实现多线程读写逻辑的复杂线程安全问题与逻辑错误
- 使用「可靠实现与测试的」库所提供的并发功能而不是去直接实现 是 最佳实践 🏆✅

示例代码如下：

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

    Cffu<List<Integer>> allResults = cffuFactory.allResultsOf(cffu1, cffu2);
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

> \# 完整可运行的Demo代码参见[`AllResultsOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllResultsOfDemo.java)。

上面是多个相同结果类型的`CF`，`cffu`还提供了返回多个不同类型的输入`CF`结果的`allTupleFailFastOf` / `allTupleOf` / `mSupplyTupleFailFastAsync`等方法。

示例代码如下：

```java
public class AllTupleOfDemo {
  private static final ExecutorService myBizExecutor = Executors.newCachedThreadPool();
  private static final CffuFactory cffuFactory = CffuFactory.builder(myBizExecutor).build();

  public static void main(String[] args) throws Exception {
    //////////////////////////////////////////////////
    // allTupleFailFastOf / allTupleOf
    //////////////////////////////////////////////////
    Cffu<String> cffu1 = cffuFactory.completedFuture("foo");
    Cffu<Integer> cffu2 = cffuFactory.completedFuture(42);

    Cffu<Tuple2<String, Integer>> allTuple = cffuFactory.allTupleFailFastOf(cffu1, cffu2);
    System.out.println(allTuple.get());
    // output: Tuple2(foo, 42)

    //////////////////////////////////////////////////
    // or CompletableFutureUtils.allTupleFailFastOf / allTupleOf
    //////////////////////////////////////////////////
    CompletableFuture<String> cf1 = CompletableFuture.completedFuture("foo");
    CompletableFuture<Integer> cf2 = CompletableFuture.completedFuture(42);

    CompletableFuture<Tuple2<String, Integer>> allTuple2 = allTupleFailFastOf(cf1, cf2);
    System.out.println(allTuple2.get());
    // output: Tuple2(foo, 42)
  }
}
```

> \# 完整可运行的Demo代码参见[`AllTupleOfDemo.java`](cffu-core/src/test/java/io/foldright/demo/AllTupleOfDemo.java)。

### 2.2 支持设置缺省的业务线程池并封装携带

`CompletableFuture`异步执行（即`*Async`方法）使用的缺省线程池是`ForkJoinPool.commonPool()`；业务中使用这个缺省线程池是很危险的❗

- `ForkJoinPool.commonPool()`差不多是`CPU`个线程，合适执行`CPU`密集的任务；对于业务逻辑，往往有很多等待操作（如网络`IO`、阻塞等待）并不是`CPU`密集的，导致业务处理能力低下 🐌
- `ForkJoinPool`使用的是无界队列；当大流量时任务会堆积，导致内存耗尽服务崩溃 🚨  
   关于这个问题及原因的更多说明可以看看[这篇文章](https://juejin.cn/post/7476755577193824295)

结果就是，在业务逻辑中，调用`CompletableFuture`的`*Async`方法时，几乎每次都要反复传入指定的业务线程池；这让`CompletableFuture`的使用很繁琐易错 🤯❌

另外，当在底层逻辑底层操作回调业务时（如`RPC`回调），不合适或方便为业务提供线程池；使用`Cffu`封装携带的上层业务指定的线程池既方便又合理安全。  
这个使用场景的更多说明可以看看[CompletableFuture原理与实践 - 4.2.3 异步RPC调用注意不要阻塞IO线程池](https://juejin.cn/post/7098727514725416967#heading-25)

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

`Cffu`类支持设置缺省的业务线程池，规避上面的繁琐与危险。示例代码如下：

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

> \# 完整可运行的Demo代码参见[`DefaultExecutorSettingForCffu.java`](cffu-core/src/test/java/io/foldright/demo/DefaultExecutorSettingForCffu.java)。

### 2.3 高效灵活的并发执行策略（`AllFailFast` / `AnySuccess` / `AllSuccess` / `MostSuccess`）

- `CompletableFuture`的`allOf`方法会等待所有输入`CF`运行完成；即使有`CF`失败了也要等待后续`CF`都运行完成，再返回一个失败的`CF`。
  - 对于业务逻辑来说，这样失败且继续等待的策略（`AllComplete`），减慢了业务响应性；会希望当有输入`CF`失败了，则快速失败不再做于事无补的等待
  - `cffu`提供了相应的`allResultsFailFastOf`等方法，支持`AllFailFast`并发执行策略
  - `allOf` / `allResultsFailFastOf`两者都是，只有当所有的输入`CF`都成功时，才返回成功结果
- `CompletableFuture`的`anyOf`方法返回首个完成的`CF`，不会等待后续没有完成的`CF`赛马模式；即使首个完成的`CF`是失败的，也会返回这个失败的`CF`结果。
  - 对于业务逻辑来说，想要的往往不是首个完成但失败的`CF`结果（`AnyComplete`），会希望赛马模式返回首个成功的`CF`结果
  - `cffu`提供了相应的`anySuccessOf`等方法，支持`AnySuccess`并发执行策略
  - `anySuccessOf`只有当所有的输入`CF`都失败时，才返回失败结果
- 返回多个`CF`中成功的结果，对于失败的`CF`返回指定的缺省值
  - 业务逻辑包含容错时，当某些`CF`处理出错时可以使用成功的那部分结果，而不是整体失败
  - `cffu`提供了相应的`allSuccessOf`等方法，支持`AllSuccess`并发执行策略
- 返回指定时间内多个`CF`中成功的结果，对于失败或超时的`CF`返回指定的缺省值
  - 业务是最终一致性时，尽量返回有的结果；对于没能及时返回还在运行中处理的`CF`，结果会写到分布式缓存中下次业务请求就有了，以避免重复计算
  - 这是个常见业务使用模式，`cffu`提供了相应的`mostSuccessResultsOf`等方法，支持`MostSuccess`并发执行策略

> 📔 关于多个`CF`的并发执行策略，可以看看`JavaScript`规范[`Promise Concurrency`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise#promise_concurrency)；在`JavaScript`中，`Promise`即对应`CompletableFuture`。
>
> `JavaScript Promise`提供了4个并发执行方法：
>
> - [`Promise.all()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/all)：等待所有`Promise`运行成功，只要有一个失败就立即返回失败（`AllFailFast`）
> - [`Promise.allSettled()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/allSettled)：等待所有`Promise`运行完成，不管成功失败（`AllComplete`）
> - [`Promise.any()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/any)：赛马模式，立即返回首个成功的`Promise`（`AnySuccess`）
> - [`Promise.race()`](https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/race)：赛马模式，立即返回首个完成的`Promise`（`AnyComplete`）
>
> PS：`JavaScript Promise`的方法命名考究～ 👍
>
> `cffu`的新方法支持了`JavaScript Promise`规范的并发执行方式～

示例代码如下：

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

    Cffu<List<Integer>> failFast = cffuFactory.allResultsFailFastOf(success, successAfterLongTime, failed);
    // fail fast without waiting successAfterLongTime
    System.out.println(failFast.exceptionNow());
    // output: java.lang.RuntimeException: Bang!

    Cffu<Integer> anySuccess = cffuFactory.anySuccessOf(success, successAfterLongTime, failed);
    System.out.println(anySuccess.get());
    // output: 42

    Cffu<List<Integer>> mostSuccess = cffuFactory.mostSuccessResultsOf(
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

> \# 完整可运行的Demo代码参见[`ConcurrencyStrategyDemo.java`](cffu-core/src/test/java/io/foldright/demo/ConcurrencyStrategyDemo.java)。

### 2.4 支持直接运行多个`Action`，而不是要先包装成`CompletableFuture`

`CompletableFuture`的`allOf/anyOf`方法输入的是`CompletableFuture`；当业务直接有要编排业务逻辑方法时，仍然需要先包装成`CompletableFuture`再运行：

- 繁琐
- 模糊了业务流程
- 简单包装多个`Action`成`CF`提交给`allOf/anyOf`的做法（业务代码往/往是这样实现的），**会呑异常**
  - 当输入`Action`的运行抛出多个异常时，这些异常至多只能有一个能通过返回`CF`反馈给业务，其它的异常则被默默地呑掉，影响业务问题的排查

`cffu`提供了直接运行多个`Action`的方法，解决上述问题：

- 方便直接明了地表达与编排业务流程
- 不呑异常，方便排查业务问题
  - 当多个输入`Action`的运行抛出多个异常时，会打印日志报告出没有在返回`CF`中反馈给业务的异常

示例代码如下：

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

这些多`Action`方法也配套实现了「不同的并发执行策略」与「返回多个输入`CF`结果」的支持。

示例代码如下：

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

    CfTupleUtils.thenMApplyTupleFailFastAsync(
        completedFuture(42),
        v -> "string" + v,
        v -> v + 1,
        v -> v + 2.1
    ).thenAccept(System.out::println);
    // output: Tuple3(string42, 43, 44.1)
    cffuFactory.completedFuture(42).thenMApplyAllSuccessTupleAsync(
        v -> "string" + v,
        v -> v + 1,
        v -> v + 2.1
    ).thenAccept(System.out::println);
    // output: Tuple3(string42, 43, 44.1)
  }
}
```

> \# 完整可运行的Demo代码参见[`MultipleActionsDemo.java`](cffu-core/src/test/java/io/foldright/demo/MultipleActionsDemo.java)。

### 2.5 支持处理指定异常类型，而不是处理所有异常`Throwable`

在业务处理的`try-catch`语句中，`catch`所有异常（`Throwable`）往往是不好的实践。类似的，`CompletableFuture#exceptionally`方法，也是处理了所有异常（`Throwable`）。

应该只处理当前业务自己清楚明确能恢复的具体异常，由外层处理其它的异常；避免掩盖Bug或是错误地处理了自己不能恢复的异常。

`cffu`提供了相应的[`catching*`方法](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/CompletableFutureUtils.html#catching(F,java.lang.Class,java.util.function.Function))，支持指定要处理异常类型；相比`CF#exceptionally`方法新加了一个异常类型参数，使用方式类似，不附代码示例。

### 2.6 `Backport`支持`Java 8`

`Java 9+`高版本的所有`CF`新功能方法在`Java 8`低版本直接可用。

其中重要的`backport`功能有：

- 超时控制：`orTimeout` / `completeOnTimeout`
- 延迟执行：`delayedExecutor`
- 工厂方法：`failedFuture` / `completedStage` / `failedStage`
- 处理操作：`completeAsync` / `exceptionallyAsync` / `exceptionallyCompose` / `copy`
- 非阻塞读：`resultNow` / `exceptionNow` / `state`

这些`backport`方法是`CompletableFuture`的已有功能，不附代码示例。

### 2.7 超时执行安全的`orTimeout` / `completeOnTimeout`新实现

`CF#orTimeout()` / `CF#completeOnTimeout()`方法当超时时使用`CF`内部的单线程`ScheduledThreadPoolExecutor`来触发业务逻辑执行，会导致`CF`的超时与延迟执行基础功能失效❗️

因为超时与延迟执行是基础功能，一旦失效会导致：

- 业务功能的正确性问题，设置超时的触发不准延后
- 系统稳定性问题，如线程中等待操作不能返回、其它有依赖的`CF`不能完成、线程池耗尽与内存泄露

`cffu`库提供了超时执行安全的新实现方法：

- [`Cffu#orTimeout()`](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/Cffu.html#orTimeout(long,java.util.concurrent.TimeUnit))
  / [`Cffu#completeOnTimeoutTimeout()`](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/Cffu.html#completeOnTimeout(java.lang.Object,long,java.util.concurrent.TimeUnit))
- [`CFU#cffuOrTimeout()`](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/CompletableFutureUtils.html#cffuOrTimeout(F,long,java.util.concurrent.TimeUnit))
  / [`CFU#cffuCompleteOnTimeout()`](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/CompletableFutureUtils.html#cffuCompleteOnTimeout(F,T,long,java.util.concurrent.TimeUnit))

保证业务逻辑不会在`CF`的单线程`ScheduledThreadPoolExecutor`中执行。

更多说明参见：

- 演示问题的[`DelayDysfunctionDemo.java`](https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java)
- `cffu backport`方法的`JavaDoc`： [`CFU#orTimeout()`](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/CompletableFutureUtils.html#orTimeout(F,long,java.util.concurrent.TimeUnit))
  / [`CFU#completeOnTimeout()`](https://foldright.io/api-docs/cffu/1.1.8/io/foldright/cffu/CompletableFutureUtils.html#completeOnTimeout(F,T,long,java.util.concurrent.TimeUnit))
- 文章[`CompletableFuture`超时功能使用不当直接生产事故](https://juejin.cn/post/7411686792342274089)

### 2.8 支持超时的`join`方法

`cf.join()`方法「没有超时会永远等待」，在业务中很危险❗️当意外出现长时间等待时，会导致：

- 主业务逻辑阻塞，没有机会做相应的处理，以及时响应用户
- 会费掉一个线程，线程是很有限的资源（一般几百个），耗尽线程意味着服务瘫痪故障

`join(timeout, unit)`方法即支持超时的`join`方法；就像`cf.get(timeout, unit)`之于`cf.get()`。

这个新方法使用简单类似，不附代码示例。

### 2.9 返回具体类型的`anyOf`方法

`CompletableFuture`的`anyOf()`方法返回类型是`Object`，丢失具体类型，使用返回值时需要转型操作不方便，也不类型安全。

`cffu`提供的`anySuccessOf()` / `anyOf()`方法，返回具体类型`T`，而不是返回`Object`。

这个方法使用简单类似，不附代码示例。

### 2.10 输入宽泛类型的`allOf/anyOf`方法

`CompletableFuture`的`allOf()` / `anyOf()`方法输入参数类型是`CompletableFuture`，而不是更宽泛的`CompletionStage`类型；对于`CompletionStage`类型的输入，则需要调用`CompletionStage#toCompletableFuture`方法做转换。

`cffu`提供的`allOf()` / `anyOf()`方法输入更宽泛的`CompletionStage`参数类型，使用更方便。

方法使用简单类似，不附代码示例。

### 更多功能说明

可以参见：

- [`Java API`文档](https://foldright.io/api-docs/cffu/)
- 实现源码
  - [`Cffu.java`](cffu-core/src/main/java/io/foldright/cffu/Cffu.java)、[`CffuFactory.java`](cffu-core/src/main/java/io/foldright/cffu/CffuFactory.java)
  - [`CompletableFutureUtils.java`](cffu-core/src/main/java/io/foldright/cffu/CompletableFutureUtils.java)

# 🔌 API Docs

- 当前版本的`Java API`文档： https://foldright.io/api-docs/cffu/

代码示例：

# 🍪依赖

> 可以在 [central.sonatype.com](https://central.sonatype.com/artifact/io.foldright/cffu/1.0.0/versions) 查看最新版本与可用版本列表。

- `cffu`库（包含[`Java CompletableFuture`](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)的增强`CompletableFutureUtils`）:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu</artifactId>
      <version>1.1.8</version>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation("io.foldright:cffu:1.1.8")
    ```
    Gradle Groovy DSL
    ```groovy
    implementation 'io.foldright:cffu:1.1.8'
    ```
- [📌 `TransmittableThreadLocal(TTL)`](https://github.com/alibaba/transmittable-thread-local)的[`cffu executor wrapper SPI`实现](cffu-ttl-executor-wrapper)：
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-ttl-executor-wrapper</artifactId>
      <version>1.1.8</version>
      <scope>runtime</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    runtimeOnly("io.foldright:cffu-ttl-executor-wrapper:1.1.8")
    ```
    Gradle Groovy DSL
    ```groovy
    runtimeOnly 'io.foldright:cffu-ttl-executor-wrapper:1.1.8'
    ```
- `cffu bom`:
  - For `Maven` projects:

    ```xml
    <dependency>
      <groupId>io.foldright</groupId>
      <artifactId>cffu-bom</artifactId>
      <version>1.1.8</version>
      <type>pom</type>
      <scope>import</scope>
    </dependency>
    ```
  - For `Gradle` projects:

    Gradle Kotlin DSL
    ```groovy
    implementation(platform("io.foldright:cffu-bom:1.1.8"))
    ```
    Gradle Groovy DSL
    ```groovy
    implementation platform('io.foldright:cffu-bom:1.1.8')
    ```

# 📚 更多资料

- 官方资料
  - [`CompletionStage` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletionStage.html)
  - [`CompletableFuture` JavaDoc](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html)
- `cffu`开发者[`@linzee1`](https://github.com/linzee1)的[`CF/cffu`掘金专栏](https://juejin.cn/column/7413672189316038719)
- [`CompletableFuture` Guide](docs/completable-future-guide.md)
  - 完备说明`CompletableFuture`的使用方式
  - 给出 最佳实践建议 与 使用陷阱注意
  - 在业务中，更有效安全地使用`CompletableFuture`

# 👋 关于库名

`cffu` 是 `CompletableFuture-Fu`的缩写；读作`C Fu`，谐音`Shifu/师傅`。

嗯嗯，想到了《功夫熊猫》里可爱的小浣熊师傅吧～ 🦝

<a href="#dummy"><img src="https://user-images.githubusercontent.com/1063891/230850403-87ff74de-1acb-4aff-b9b4-632e4e51e225.png" width="40%" alt="shifu" /></a>
