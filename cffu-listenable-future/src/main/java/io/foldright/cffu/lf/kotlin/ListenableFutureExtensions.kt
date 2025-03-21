package io.foldright.cffu.lf.kotlin

import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.lf.ListenableFutureUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor


/*
  This file contains the Extension methods related with ListenableFuture.

  ---------------------
  Implementation Note:
  ---------------------
  The methods of this file MUST NOT be defined in `CompletableFutureExtensions` or `CffuExtensions`;
  Otherwise `NoClassDefFoundError` when loading CompletableFutureExtensions/CffuExtensions
  if `ListenableFuture` class(`ClassNotFoundException` aka. `Guava` dependency) is absent.
*/

/**
 * Converts input [ListenableFuture] to [CompletableFuture].
 *
 * Callback from ListenableFuture is executed using the given executor,
 * use {[MoreExecutors.directExecutor]} if you need skip executor switch.
 */
fun <T> ListenableFuture<T>.toCompletableFuture(
    executor: Executor, interruptLfWhenCancellationException: Boolean
): CompletableFuture<T> =
    ListenableFutureUtils.toCompletableFuture(this, executor, interruptLfWhenCancellationException)

/**
 * Converts input [ListenableFuture] to [Cffu].
 *
 * Callback from ListenableFuture is executed using cffuFactory's default executor.
 */
fun <T> ListenableFuture<T>.toCffu(cffuFactory: CffuFactory, interruptLfWhenCancellationException: Boolean): Cffu<T> =
    ListenableFutureUtils.toCffu(this, cffuFactory, interruptLfWhenCancellationException)

/**
 * Converts input [CompletableFuture] to [ListenableFuture].
 */
fun <T> CompletableFuture<T>.toListenableFuture(): ListenableFuture<T> =
    ListenableFutureUtils.toListenableFuture(this)

/**
 * Converts input [Cffu] to [ListenableFuture].
 */
fun <T> Cffu<T>.toListenableFuture(): ListenableFuture<T> =
    ListenableFutureUtils.toListenableFuture(this)
