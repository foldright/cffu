# 🔧 `CF`的功能介绍 | 💪 `CF`方法分类说明

--------------------------------------------------------------------------------

<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->

- [1. `CF`的创建](#1-cf%E7%9A%84%E5%88%9B%E5%BB%BA)
  - [直接创建已完成的`CompletableFuture`的工厂方法 (x4)](#%E7%9B%B4%E6%8E%A5%E5%88%9B%E5%BB%BA%E5%B7%B2%E5%AE%8C%E6%88%90%E7%9A%84completablefuture%E7%9A%84%E5%B7%A5%E5%8E%82%E6%96%B9%E6%B3%95-x4)
  - [创建异步完成的`CompletableFuture`的工厂方法 (x4)](#%E5%88%9B%E5%BB%BA%E5%BC%82%E6%AD%A5%E5%AE%8C%E6%88%90%E7%9A%84completablefuture%E7%9A%84%E5%B7%A5%E5%8E%82%E6%96%B9%E6%B3%95-x4)
  - [`allOf`/`anyOf`静态工厂方法 (x2)](#allofanyof%E9%9D%99%E6%80%81%E5%B7%A5%E5%8E%82%E6%96%B9%E6%B3%95-x2)
  - [构造函数`CompletableFuture<T>()` (x1)](#%E6%9E%84%E9%80%A0%E5%87%BD%E6%95%B0completablefuturet-x1)
- [2. `CF`的显式读写方法](#2-cf%E7%9A%84%E6%98%BE%E5%BC%8F%E8%AF%BB%E5%86%99%E6%96%B9%E6%B3%95)
  - [显式结果读取方法 (x5)](#%E6%98%BE%E5%BC%8F%E7%BB%93%E6%9E%9C%E8%AF%BB%E5%8F%96%E6%96%B9%E6%B3%95-x5)
  - [获取任务状态的方法 (x4)](#%E8%8E%B7%E5%8F%96%E4%BB%BB%E5%8A%A1%E7%8A%B6%E6%80%81%E7%9A%84%E6%96%B9%E6%B3%95-x4)
  - [显式结果写入方法 (x5)](#%E6%98%BE%E5%BC%8F%E7%BB%93%E6%9E%9C%E5%86%99%E5%85%A5%E6%96%B9%E6%B3%95-x5)
- [3. `CF`的流程编排](#3-cf%E7%9A%84%E6%B5%81%E7%A8%8B%E7%BC%96%E6%8E%92)
  - [简单`then`方法 (x9)](#%E7%AE%80%E5%8D%95then%E6%96%B9%E6%B3%95-x9)
  - [两个都完成 - Both (x9)](#%E4%B8%A4%E4%B8%AA%E9%83%BD%E5%AE%8C%E6%88%90---both-x9)
  - [两个任一完成 -Either (x9)](#%E4%B8%A4%E4%B8%AA%E4%BB%BB%E4%B8%80%E5%AE%8C%E6%88%90--either-x9)
  - [错误处理 (x6)](#%E9%94%99%E8%AF%AF%E5%A4%84%E7%90%86-x6)
  - [超时控制 (x2)](#%E8%B6%85%E6%97%B6%E6%8E%A7%E5%88%B6-x2)
  - [高阶方法 (x12)](#%E9%AB%98%E9%98%B6%E6%96%B9%E6%B3%95-x12)
- [4. 其它辅助类方法](#4-%E5%85%B6%E5%AE%83%E8%BE%85%E5%8A%A9%E7%B1%BB%E6%96%B9%E6%B3%95)
  - [转换方法](#%E8%BD%AC%E6%8D%A2%E6%96%B9%E6%B3%95)
  - [属性查看/子类扩展方法](#%E5%B1%9E%E6%80%A7%E6%9F%A5%E7%9C%8B%E5%AD%90%E7%B1%BB%E6%89%A9%E5%B1%95%E6%96%B9%E6%B3%95)
  - [强制改写完成结果的后门方法](#%E5%BC%BA%E5%88%B6%E6%94%B9%E5%86%99%E5%AE%8C%E6%88%90%E7%BB%93%E6%9E%9C%E7%9A%84%E5%90%8E%E9%97%A8%E6%96%B9%E6%B3%95)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

--------------------------------------------------------------------------------

## 1. `CF`的创建

通过静态工厂方法（`Factory`）或构造函数（`Constructor`）来创建`CompletableFuture`。这些方法是`CompletableFuture`链的起始。

### 直接创建已完成的`CompletableFuture`的工厂方法 (x4)

1. `completedFuture(T value)`：返回`CF<T>`
2. `completedStage(T value)`<sup><b><i>J9</i></b></sup>：返回`CompletionStage<T>`
3. `failedFuture(Throwable ex)`<sup><b><i>J9</i></b></sup>：返回`CF<T>`
4. `failedStage(Throwable ex)`<sup><b><i>J9</i></b></sup>：返回`CompletionStage<T>`

说明：

- 因为通过正常完成结果（`value`）或异常完成的异常（`ex`）创建已完成的`CompletableFuture`，能很快完成创建，所以并不需要用于异步执行线程池（`executor`）。
- 对于`completedStage`/`failedStage`方法返回的类型是`CompletionStage`接口，限止了调用`CompletionStage`接口之外的其它方法，通过抛`UnsupportedOperationException`异常表示不支持。
- 对于通过异常完成的异常（`ex`）的`CF<T>`或`CompletionStage<T>`，结果类型`T`可以是任意类型。

### 创建异步完成的`CompletableFuture`的工厂方法 (x4)

1. `runAsync(Runnable runnable)`：返回`CF<Void>`
2. `runAsync(Runnable runnable, Executor executor)`：返回`CF<Void>`
3. `supplyAsync(Supplier<T> supplier)`：返回`CF<T>`
4. `supplyAsync(Supplier<T> supplier, Executor executor)`：返回`CF<T>`

说明：

- 因为要异步运行输入的任务（`Runnable`/`Supplier`），所以需要异步执行的线程池。
- 如果不指定`executor`参数，缺省是`ForkJoinPool.commonPool()`。

> 注：严格的说，`CompletableFuture`的缺省`executor`所使用的选择逻辑是：
>
> Java 8–24：当`ForkJoinPool.getCommonPoolParallelism() > 1`时，即`Runtime.getRuntime().availableProcessors() > 2`时，使用`ForkJoinPool.commonPool()`
> （现在机器的处理器个数一般都不止2个，无论线上服务器还是个人电脑）；
> 否则使用`ThreadPerTaskExecutor`，即为每个任务新建一个线程来执行 🤯
>
> Java 25+：始终使用`ForkJoinPool.commonPool()`（`ForkJoinPool.asyncCommonPool()`；公共池并行度为 0 时会被改成 2），不再退回`ThreadPerTaskExecutor`。
>
> 具体确定的缺省逻辑，还是去查看`CompletableFuture`与`ForkJoinPool`类的源码实现。

### `allOf`/`anyOf`静态工厂方法 (x2)

- `allOf(CompletableFuture<?>... cfs)`：返回`CF<Void>`
  - 返回的`CF`，当多个输入`CF`全部成功完成时，才成功完成；
  - 如果输入`CF`有一个失败的，则返回的`CF`立即失败，不再需要依赖其它`CF`完成的状态
- `anyOf(CompletableFuture<?>... cfs)`：返回`CF<Object>`
  - 返回的`CF`，当多个输入`CF`有任一个完成（无论成功完成还是失败完成），返回这个完成的输入`CF`的结果，不会关注后续输入`CF`的完成情况
  - 赛马模式

说明：

- 虽然这2个方法是静态工厂方法，但并不是`CF`链的起点，而是输入多个`CF`，用于编排多路的流程。
  - 在功能与使用的上，应该与下面【3. 流程编排】一节的方法归类在一起。
  - 这里列上，只是为了体现出是静态工厂方法这个特点。
- 这2个方法是在组合输入的多个`CF`的结果，本身复杂业务执行逻辑，逻辑简单无阻塞，所以无需`Executor`。
- 这2个方法所返回的`CF`，在结果获取上，有不方便的地方： 😔
  - 对于`allOf`方法，返回`CF`结果是`Void`即无内容，并没有持有多个输入`CF`的结果
    - [`allOf`方法的文档](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/CompletableFuture.html#allOf(java.util.concurrent.CompletableFuture...))给的解决方法是，再通过调用各个输入`CF`的结果读取方法（如`join()`）来获得：
    - > the results of the given CompletableFutures are not reflected in the returned CompletableFuture, but may be obtained by inspecting them individually.
  - 对于`anyOf`方法，返回`CF`结果类型是`Object`，要使用这个结果一定要做强制类型转换
  - > 这些不方便的地方，在`cffu`库中，提供了对应的加强解决 💗

----------------------------------------

> `anyOf`方法的赛马模式，任一个失败完成的输入`CF`也会导致返回`CF`失败完成，即使后续有成功完成的输入`CF`，这样的效果可能不是业务希望的 😔
>
> 业务会希望有这样的赛马模式：
>
> - 当多个输入`CF`有任一个成功完成，返回这个完成的输入`CF`的结果
> - 否则当所有的输入`CF`都失败时，返回失败
> - 在`cffu`库中，可以考虑是否要提供这种赛马模式的支持 💗

### 构造函数`CompletableFuture<T>()` (x1)

返回一个没有完成的`CompletableFuture`；后续可以通过显式的写方法来写入结果以完成，如`complete(T value)`、`completeExceptionally(Throwable ex)`。  
可以后续完成体现出命名`CompletableFuture`（可完成的`Future`）。

在日常的业务开发中，更推荐使用`CF`来编排业务流程，几乎一定不应该使用这个构造方法。

- 构造函数创建的`CF`的使用场景：
  - 在用户自己的业务逻辑线程中异步处理，并通过显式调用`CF`对象的写方法设置完成的结果；
  - 无需由`CF`关联的`Executor`来执行用户的业务逻辑。
- 往往是在中间件中会有必要这样使用，比如
  - 在网络`IO`框架的回调（线程）中完成处理后设置`CF`结果。
- 显式给`CompletableFuture`写入结果的方式，体现出极强灵活性与复杂性。
  - `CompletableFuture`编排的使用方式下层也是通过「显式写入结果的方式」来实现的。

## 2. `CF`的显式读写方法

### 显式结果读取方法 (x5)

- `T get()` **阻塞❗**，属于`Future`接口
  - 返回成功完成的结果；对于执行失败的情况，抛出`ExecutionException`异常，cause 是失败异常
- `T get(long timeout, TimeUnit unit)` **阻塞❗**<sup><b><i>〚1〛</i></b></sup>，属于`Future`接口
  - 同上
  - 如果等待超时，则抛出`TimeoutException`异常
- `T join()` **阻塞❗️**
  - 功能与`T get()`一样，区别是抛的不是受检异常
  - 对于执行失败的情况，抛出`CompletionException`异常，cause 是失败异常
- `T getNow(T valueIfAbsent)`
  - 返回已正常完成`CF`的正常结果；如果`CF`不是正常完成（未完成/被取消/异常完成）则抛出`IllegalStateException`异常
- `T resultNow()`<sup><b><i>J19</i></b></sup>，属于`Future`接口
  - 返回已异常完成`CF`的出错异常；如果`CF`不是异常完成（未完成/被取消/正常完成）则抛出`IllegalStateException`异常
- `Throwable exceptionNow()`<sup><b><i>J19</i></b></sup>，属于`Future`接口

注：

- 〚1〛：`T get(long timeout, TimeUnit unit)`如果设置的超时是`0`，不会BLOCKING；这个情况下往往应该调用`T getNow(T valueIfAbsent)`。

### 获取任务状态的方法 (x4)

- `boolean isDone()`，属于`Future`接口
  - 是否 完成状态
  - 注意：对于「取消」，这个方法也是返回`true`的；即不是运行中，则是完成的。
- `boolean isCompletedExceptionally()`
  - 是否是 异常完成状态
  - 注意：对于「取消」，这个方法也是返回`true`的。即不是运行中或完成完成，则是异常完成
- `boolean isCancelled()`，属于`Future`接口
  - 是否是 取消状态
- `State state()`<sup><b><i>J19</i></b></sup>，属于`Future`接口
  - 获取完成状态
  - 对应4个枚举值：`RUNNING`、`SUCCESS`、`FAILED`、`CANCELLED`

### 显式结果写入方法 (x5)

- 显式写入 成功结果 (x3)
  - `boolean complete(T value)`
  - `completeAsync(Supplier<T> supplier)`<sup><b><i>J9</i></b></sup>
    - 在缺省线程池中计算结果（`Supplier`）
  - `completeAsync(Supplier<T> supplier, Executor executor)`<sup><b><i>J9</i></b></sup>
    - 在指定的线程池中计算结果（`Supplier`）
- 显式写入 失败结果/异常 (x1)
  - `boolean completeExceptionally(Throwable ex)`
- 取消任务 (x1)
  - `boolean cancel(boolean mayInterruptIfRunning)`，属于`Future`接口

## 3. `CF`的流程编排

**_WIP..._**

### 简单`then`方法 (x9)

- `thenRun(Runnable action)`
  - `thenRunAsync(Runnable action)`
  - `thenRunAsync(Runnable action, Executor executor)`
- `thenAccept(Consumer<? super T> action)`
  - `thenAcceptAsync(Consumer<? super T> action)`
  - `thenAcceptAsync(Consumer<? super T> action, Executor executor)`
- `thenApply(Function<? super T, ? extends U> fn)`
  - `thenApplyAsync(Function<? super T, ? extends U> fn)`
  - `thenApplyAsync(Function<? super T, ? extends U> fn, Executor executor)`

### 两个都完成 - Both (x9)

- `runAfterBoth(CompletionStage<?> other, Runnable action)`
  - `runAfterBothAsync(CompletionStage<?> other, Runnable action)`
  - `runAfterBothAsync(CompletionStage<?> other, Runnable action, Executor executor)`
- `thenAcceptBoth(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action)`
  - `thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action)`
  - `thenAcceptBothAsync(CompletionStage<? extends U> other, BiConsumer<? super T, ? super U> action, Executor executor)`
- `thenCombine(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn)`
  - `thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn)`
  - `thenCombineAsync(CompletionStage<? extends U> other, BiFunction<? super T, ? super U, ? extends V> fn, Executor executor)`

### 两个任一完成 -Either (x9)

- `runAfterEither*`
- `acceptEither*`
- `applyToEither*`

### 错误处理 (x6)

- `exceptionally(Function<Throwable, ? extends T> fn)`
  - `exceptionallyAsync(Function<Throwable, ? extends T> fn)`
  - `exceptionallyAsync(Function<Throwable, ? extends T> fn, Executor executor)`

### 超时控制 (x2)

- `completeOnTimeout(T value, long timeout, TimeUnit unit)`<sup><b><i>J9</i></b></sup>
  - 如果超时了，返回的`CF`会成功，结果是指定的值
- `orTimeout(long timeout, TimeUnit unit)`<sup><b><i>J9</i></b></sup>
  - 如果超时了，返回的`CF`会失败，失败异常是`TimeoutException`

延时执行与超时控制强相关，放在这一节里。
（实现超时控制 使用了延时执行功能）

- `delayedExecutor(long delay, TimeUnit unit, Executor executor)`<sup><b><i>J9</i></b></sup>
  - 返回延时执行的包装`Executor`

### 高阶方法 (x12)

- `thenCompose*`
- `exceptionallyCompose(Function<Throwable, ? extends CompletionStage<T>> fn)`
  - `exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn)`
  - `exceptionallyComposeAsync(Function<Throwable, ? extends CompletionStage<T>> fn, Executor executor)`
- `whenComplete*`
- `handle*`

## 4. 其它辅助类方法

从`CF`的功能使用上，这些方法不是必须的。

但通过这些`CF`的非功能方法可以

- 提升实现的安全性
  - 如防御式拷贝防止被使用方意外写结果
- 获取额外信息
  - 如用于监控
- ……

### 转换方法

- `toCompletableFuture()`，属于`CompletionStage`接口
  - 转换一个普通的`CF`，比如
    - 不再是`MinimalCompletionStage`，可以做显式的写操作
  - 如果对象已经是普通的`CF`，则会返回`this`
- `CompletionStage<T> minimalCompletionStage()`<sup><b><i>J9</i></b></sup><sup><b><i>〚1〛</i></b></sup>
  - 转换一个`MinimalCompletionStage`，限制`CompletionStage`接口之外的方法，不能做显式写操作
- `CompletableFuture<T> copy()`
  - 生成一个（防御性）拷贝
  - 对返回的`CF`做写操作，不会影响原来的`CF`

### 属性查看/子类扩展方法

- `int getNumberOfDependents()`
  - 返回依赖这个`CF`的`CF`个数，可以用于监控
- `Executor defaultExecutor()`<sup><b><i>J9</i></b></sup>
  - 返回缺省的`Executor`
  - 主要是用于 `CompletableFuture`子类的模板方法，扩展用
- `CompletableFuture<U> newIncompleteFuture()`<sup><b><i>J9</i></b></sup><sup><b><i>〚1〛</i></b></sup>
  - 主要是用于 `CompletableFuture`子类的模板方法，扩展用
  - 业务使用中，不需要使用这个方法；如果要用，推荐使用`new CompletableFuture()`

注：

- 〚1〛：`CompletableFuture<U> newIncompleteFuture()`功能与`CompletableFuture<T>()`是一样，实际上代码实现就只是调用构造函数。
  - 相比构造函数，工厂方法形式的一个好处是可以无需指定泛型参数；在很多库的`API`中都可以看到这样的设计方式。

### 强制改写完成结果的后门方法

- `void obtrudeValue(T value)`
  - 强制设置成功结果为`value`，可以多次改写
- `void obtrudeException(Throwable ex)`
  - 强制设置失败异常为`ex`，可以多次改写
