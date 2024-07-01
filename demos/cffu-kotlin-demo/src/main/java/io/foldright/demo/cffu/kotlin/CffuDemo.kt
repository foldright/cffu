package io.foldright.demo.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.kotlin.anySuccessOfCffu
import java.lang.Thread.sleep
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


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

    val anySuccessOf: Cffu<Int> = listOf(longTaskC, longFailedTask).anySuccessOfCffu()
    println("anySuccessOf result: ${anySuccessOf.get()}")

    ////////////////////////////////////////
    // cleanup
    ////////////////////////////////////////
    myBizThreadPool.shutdown()
}

