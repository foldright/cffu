# `cffu`的三种使用方式的示例代码

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

