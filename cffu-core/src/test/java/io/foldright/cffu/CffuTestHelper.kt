@file:JvmName("CffuTestHelper")

package io.foldright.cffu

import io.foldright.cffu.CffuFactoryBuilder.CffuMadeExecutor
import java.util.concurrent.Executor


fun CffuFactory.unwrapMadeExecutor(): Executor {
    val executor = defaultExecutor() as CffuMadeExecutor
    return executor.unwrap()
}

fun Cffu<*>.unwrapMadeExecutor(): Executor {
    val executor = defaultExecutor() as CffuMadeExecutor
    return executor.unwrap()
}
