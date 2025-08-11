@file:JvmName("CffuTestHelper")

package io.foldright.cffu2

import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.types.shouldBeSameInstanceAs
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.ForkJoinPool


fun Cffu<*>.getOriginalExecutor(): Executor = cffuFactory().getOriginalExecutor()
fun Cffu<*>.getScreenedExecutor(): Executor = cffuFactory().getScreenedExecutor()

fun CffuFactory.getOriginalExecutor(): Executor = defaultExecutor.original
fun CffuFactory.getScreenedExecutor(): Executor = defaultExecutor.screened


class FooCs<T>(cf: CompletableFuture<T>) : CompletionStage<T> by cf

fun useCommonPool() = ForkJoinPool.getCommonPoolParallelism() > 1

fun assertIsCfDefaultExecutor(executor: Executor) {
    if (useCommonPool()) {
        executor shouldBeSameInstanceAs ForkJoinPool.commonPool()
    } else {
        executor.javaClass.name shouldEndWith "\$ThreadPerTaskExecutor"
    }
}
