@file:JvmName("CffuTestHelper")

package io.foldright.cffu

import io.foldright.cffu.CffuFactoryBuilder.CffuMadeExecutor
import java.util.concurrent.Executor


fun CffuFactory.unwrapMadeExecutor(): Executor = (defaultExecutor() as CffuMadeExecutor).unwrap()

fun Cffu<*>.unwrapMadeExecutor(): Executor = (defaultExecutor() as CffuMadeExecutor).unwrap()

fun Executor.unwrapMadeExecutor(): Executor = (this as CffuMadeExecutor).unwrap()
