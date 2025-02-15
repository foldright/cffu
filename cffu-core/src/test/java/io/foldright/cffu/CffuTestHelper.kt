@file:JvmName("CffuTestHelper")

package io.foldright.cffu

import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool


fun Cffu<*>.getOriginalExecutor(): Executor = cffuFactory().getOriginalExecutor()
fun Cffu<*>.getScreenedExecutor(): Executor = cffuFactory().getScreenedExecutor()

fun CffuFactory.getOriginalExecutor(): Executor = defaultExecutor.original
fun CffuFactory.getScreenedExecutor(): Executor = defaultExecutor.screened


class FooCs<T>(cf: CompletableFuture<T>) : CompletionStage<T> by cf

fun assertIsCfDefaultExecutor(executor: Executor) {
    val useCommonPool = ForkJoinPool.getCommonPoolParallelism() > 1
    if (useCommonPool) {
        assertSame(ForkJoinPool.commonPool(), executor)
    } else {
        val executorClassName = executor.javaClass.name
        assertTrue(executorClassName.endsWith("\$ThreadPerTaskExecutor"))
    }
}
