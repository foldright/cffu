@file:JvmName("CffuTestHelper")

package io.foldright.cffu

import io.foldright.cffu.CffuFactoryBuilder.CffuMadeExecutor
import org.junit.jupiter.api.Assertions
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool


fun Cffu<*>.unwrapMadeExecutor(): Executor = defaultExecutor().unwrapMadeExecutor()

fun CffuFactory.unwrapMadeExecutor(): Executor = defaultExecutor().unwrapMadeExecutor()

fun Executor.unwrapMadeExecutor(): Executor = (this as CffuMadeExecutor).unwrap()

class FooCs<T>(cf: CompletableFuture<T>) : CompletionStage<T> by cf

fun assertIsCfDefaultExecutor(executor: Executor) {
    val useCommonPool = ForkJoinPool.getCommonPoolParallelism() > 1
    if (useCommonPool) {
        Assertions.assertSame(ForkJoinPool.commonPool(), executor)
    } else {
        val executorClassName = executor.javaClass.name
        Assertions.assertTrue(executorClassName.endsWith("\$ThreadPerTaskExecutor"))
    }
}
