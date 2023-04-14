package io.foldright.demo

import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import io.foldright.cffu.kotlin.allOfCffu
import java.lang.Thread.sleep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


private val myBizThreadPool: ExecutorService = Executors.newFixedThreadPool(42)

// Create a CffuFactory with configuration of the customized thread pool
private val cffuFactory: CffuFactory = newCffuFactoryBuilder(myBizThreadPool).build()

fun main() {
    val cf42 = cffuFactory
        .supplyAsync { 21 }     // Run in myBizThreadPool
        .thenApply { it * 2 }

    listOf(
        // Run in myBizThreadPool
        cf42.thenApplyAsync { n: Int ->
            sleep(1001)
            n / 2
        },
        // Run in myBizThreadPool
        cf42.thenApplyAsync { n: Int ->
            sleep(1002)
            n / 2
        },
    ).allOfCffu(cffuFactory).thenApply(List<Int>::sum).orTimeout(2, TimeUnit.SECONDS).get().let(::println)

    ////////////////////////////////////////
    // cleanup
    ////////////////////////////////////////
    myBizThreadPool.shutdown()
}

