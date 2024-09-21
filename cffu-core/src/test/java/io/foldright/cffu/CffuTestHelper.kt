@file:JvmName("CffuTestHelper")

package io.foldright.cffu

import io.foldright.cffu.CffuFactoryBuilder.CffuMadeExecutor
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor


fun Cffu<*>.unwrapMadeExecutor(): Executor = defaultExecutor().unwrapMadeExecutor()

fun CffuFactory.unwrapMadeExecutor(): Executor = defaultExecutor().unwrapMadeExecutor()

fun Executor.unwrapMadeExecutor(): Executor = (this as CffuMadeExecutor).unwrap()

class FooCs<T>(cf: CompletableFuture<T>) : CompletionStage<T> by cf
