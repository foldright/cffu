package io.foldright.cffu.kotlin

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.ListenableFutureUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor


/*
  This file contains the Extension methods related with ListenableFuture.

  ---------------------
  Implementation Note:
  ---------------------
  the methods of this file MUST NOT be defined in CompletableFutureExtensions or CffuExtensions;
  Otherwise `NoClassDefFoundError` when loading CompletableFutureExtensions/CffuExtensions
  if ListenableFuture class(`ClassNotFoundException` aka. guava dependency) absent.
*/

/**
 * A convenient util method for converting input [ListenableFuture] to [CompletableFuture].
 *
 * Callback from ListenableFuture is executed using CompletableFuture's default asynchronous execution facility.
 */
fun <T> ListenableFuture<T>.toCompletableFuture(): CompletableFuture<T> =
    ListenableFutureUtils.toCompletableFuture(this)

/**
 * A convenient util method for converting input [ListenableFuture] to [CompletableFuture].
 *
 * Callback from ListenableFuture is executed using the given executor,
 * use {[MoreExecutors.directExecutor]} if you need skip executor switch.
 */
fun <T> ListenableFuture<T>.toCompletableFuture(executor: Executor): CompletableFuture<T> =
    ListenableFutureUtils.toCompletableFuture(this, executor)

/**
 * A convenient util method for converting input [ListenableFuture] to [Cffu].
 *
 * Callback from ListenableFuture is executed using cffuFactory's default executor.
 */
fun <T> ListenableFuture<T>.toCffu(cffuFactory: CffuFactory): Cffu<T> {
    return ListenableFutureUtils.toCffu(this, cffuFactory)
}

/**
 * A convenient util method for converting input [CompletableFuture] to [ListenableFuture].
 */
fun <T> CompletableFuture<T>.toListenableFuture(): ListenableFuture<T> =
    ListenableFutureUtils.toListenableFuture(this)

/**
 * A convenient util method for converting input [Cffu] to [ListenableFuture].
 */
fun <T> Cffu<T>.toListenableFuture(): ListenableFuture<T> =
    ListenableFutureUtils.toListenableFuture(this)
