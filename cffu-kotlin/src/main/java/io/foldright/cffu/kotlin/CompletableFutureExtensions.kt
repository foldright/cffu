@file:Suppress("EXTENSION_SHADOWED_BY_MEMBER")

package io.foldright.cffu.kotlin

import com.google.common.util.concurrent.Futures
import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuState
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import java.util.concurrent.*
import java.util.function.*
import java.util.function.Function


/*
  This file contains the Extension methods for CompletableFuture
  input and output(return type) is CompletableFuture
*/

////////////////////////////////////////////////////////////////////////////////
// region# CF Factory Methods
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////
// region## Multi-Actions(M*) Methods(create by actions)
//
//    - Supplier<T>[] -> CompletableFuture<List<T>>
//    - Runnable[]    -> CompletableFuture<Void>
////////////////////////////////////////////////////////////

/**
 * The default executor of [CompletableFuture], **NOT** including the customized subclasses of `CompletableFuture`.
 */
private val ASYNC_POOL: Executor = CompletableFutureUtils.defaultExecutor(CompletableFuture.completedFuture(null))

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see allResultsFailFastOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Collection<Supplier<out T>>.mSupplyFailFastAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyFailFastAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see allResultsFailFastOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Array<out Supplier<out T>>.mSupplyFailFastAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyFailFastAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the successfully values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * If any of the provided suppliers fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the supplier having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not failed
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see allSuccessResultsOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Collection<Supplier<out T>>.mSupplyAllSuccessAsyncCompletableFuture(
    valueIfFailed: T, executor: Executor = ASYNC_POOL
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyAllSuccessAsync(valueIfFailed, executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the successfully values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * If any of the provided suppliers fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the supplier having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not failed
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see allSuccessResultsOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Array<out Supplier<out T>>.mSupplyAllSuccessAsyncCompletableFuture(
    valueIfFailed: T, executor: Executor = ASYNC_POOL
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyAllSuccessAsync(valueIfFailed, executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the most values obtained by calling the given Suppliers
 * in the given time(`timeout`, aka as many results as possible in the given time)
 * in the **same order** of the given Suppliers arguments.
 *
 * If any of the provided suppliers does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`
 * (which is indistinguishable from the supplier having a successful value of `valueIfNotSuccess`).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @param executor the executor to use for asynchronous execution
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see mostSuccessResultsOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Collection<Supplier<out T>>.mSupplyMostSuccessAsyncCompletableFuture(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit, executor: Executor = ASYNC_POOL
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyMostSuccessAsync(valueIfNotSuccess, executor, timeout, unit, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the most values obtained by calling the given Suppliers
 * in the given time(`timeout`, aka as many results as possible in the given time)
 * in the **same order** of the given Suppliers arguments.
 *
 * If any of the provided suppliers does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`
 * (which is indistinguishable from the supplier having a successful value of `valueIfNotSuccess`).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @param executor the executor to use for asynchronous execution
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see mostSuccessResultsOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Array<out Supplier<out T>>.mSupplyMostSuccessAsyncCompletableFuture(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit, executor: Executor = ASYNC_POOL
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyMostSuccessAsync(valueIfNotSuccess, executor, timeout, unit, *this)

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see allResultsOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Collection<Supplier<out T>>.mSupplyAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor with the values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see allResultsOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Array<out Supplier<out T>>.mSupplyAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<List<T>> =
    CompletableFutureUtils.mSupplyAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously successful
 * when any of tasks running in the given executor by calling the given Suppliers success, with the same result.
 * Otherwise, all the given tasks complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause.
 * If no suppliers are provided, returns a new CompletableFuture that is already completed exceptionally
 * with a CompletionException holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see anySuccessOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Collection<Supplier<out T>>.mSupplyAnySuccessAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<T> =
    CompletableFutureUtils.mSupplyAnySuccessAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously successful
 * when any of tasks running in the given executor by calling the given Suppliers success, with the same result.
 * Otherwise, all the given tasks complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause.
 * If no suppliers are provided, returns a new CompletableFuture that is already completed exceptionally
 * with a CompletionException holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see anySuccessOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Array<out Supplier<out T>>.mSupplyAnySuccessAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<T> =
    CompletableFutureUtils.mSupplyAnySuccessAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is completed
 * when any of tasks running in the given Executor by calling the given Suppliers complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no suppliers are provided, returns an incomplete CompletableFuture.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see anyOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Collection<Supplier<out T>>.mSupplyAnyAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<T> =
    CompletableFutureUtils.mSupplyAnyAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is completed
 * when any of tasks running in the given Executor by calling the given Suppliers complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no suppliers are provided, returns an incomplete CompletableFuture.
 *
 * @param executor the executor to use for asynchronous execution
 * @param <T> the suppliers' return type
 * @return the new CompletableFuture
 * @see anyOfCompletableFuture
 * @see CompletableFuture.supplyAsync
 */
fun <T> Array<out Supplier<out T>>.mSupplyAnyAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<T> =
    CompletableFutureUtils.mSupplyAnyAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor after runs the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see allFailFastOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Collection<Runnable>.mRunFailFastAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunFailFastAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor after runs the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see allFailFastOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Array<out Runnable>.mRunFailFastAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunFailFastAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor after runs the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see allOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Collection<Runnable>.mRunAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * by tasks running in the given Executor after runs the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see allOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Array<out Runnable>.mRunAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously successful
 * when any tasks running in the given executor success.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see anySuccessOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Collection<Runnable>.mRunAnySuccessAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunAnySuccessAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously successful
 * when any tasks running in the given executor success.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see anySuccessOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Array<out Runnable>.mRunAnySuccessAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunAnySuccessAsync(executor, *this)

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * when any tasks running in the given executor complete.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see anyOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Collection<Runnable>.mRunAnyAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunAnyAsync(executor, *toTypedArray())

/**
 * Returns a new CompletableFuture that is asynchronously completed
 * when any tasks running in the given executor complete.
 *
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see anyOfCompletableFuture
 * @see CompletableFuture.runAsync
 */
fun Array<out Runnable>.mRunAnyAsyncCompletableFuture(executor: Executor = ASYNC_POOL): CompletableFuture<Void> =
    CompletableFutureUtils.mRunAnyAsync(executor, *this)

// endregion
////////////////////////////////////////////////////////////
// region## allOf* Methods for Collection/Array(including mostSuccessResultsOf)
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletableFuture with the results in the **same order** of all the given CompletableFutures arguments,
 * the new CompletableFuture success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
 *
 * This method is the same as [allFailFastOfCompletableFuture],
 * except the returned CompletableFuture contains the results of input CompletableFutures.
 * This method is the same as [CompletableFutureUtils.allResultsFailFastOf],
 * providing this method is convenient for method chaining.
 *
 * @see allFailFastOfCompletableFuture
 */
fun <T> Collection<CompletionStage<out T>>.allResultsFailFastOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allResultsFailFastOf(*toTypedArray())

/**
 * Returns a new CompletableFuture with the results in the **same order** of all the given CompletableFutures arguments,
 * the new CompletableFuture success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
 *
 * This method is the same as [allFailFastOfCompletableFuture],
 * except the returned CompletableFuture contains the results of input CompletableFutures.
 * This method is the same as [CompletableFutureUtils.allResultsFailFastOf],
 * providing this method is convenient for method chaining.
 *
 * @see allFailFastOfCompletableFuture
 */
fun <T> Array<out CompletionStage<out T>>.allResultsFailFastOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allResultsFailFastOf(*this)

/**
 * Returns a new CompletableFuture that is successful with the results in the **same order**
 * of the given stages arguments when all the given stages completed;
 * If no stages are provided, returns a CompletableFuture completed with the value empty list.
 *
 * If any of the provided stages fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the stage having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @throws NullPointerException if the cfs param or any of its elements are `null`
 * @see getSuccessNow
 * @see Futures.successfulAsList
 */
fun <T> Collection<CompletionStage<out T>>.allSuccessResultsOfCompletableFuture(valueIfFailed: T): CompletableFuture<List<T>> =
    CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, *toTypedArray())

/**
 * Returns a new CompletableFuture that is successful with the results in the **same order**
 * of the given stages arguments when all the given stages completed;
 * If no stages are provided, returns a CompletableFuture completed with the value empty list.
 *
 * If any of the provided stages fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the stage having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @throws NullPointerException if the cfs param or any of its elements are `null`
 * @see getSuccessNow
 * @see Futures.successfulAsList
 */
fun <T> Array<out CompletionStage<out T>>.allSuccessResultsOfCompletableFuture(valueIfFailed: T): CompletableFuture<List<T>> =
    CompletableFutureUtils.allSuccessResultsOf(valueIfFailed, *this)

/**
 * Returns a new CompletableFuture with the most results in the **same order** of the given stages arguments
 * in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see getSuccessNow
 */
fun <T> Collection<CompletionStage<out T>>.mostSuccessResultsOfCompletableFuture(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, *toTypedArray())

/**
 * Returns a new CompletableFuture with the most results in the **same order** of the given stages arguments
 * in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see getSuccessNow
 */
fun <T> Array<out CompletionStage<out T>>.mostSuccessResultsOfCompletableFuture(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, *this)

/**
 * Returns a new CompletableFuture with the most results in the **same order** of the given stages arguments
 * in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param executorWhenTimeout the async executor when triggered by timeout
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see getSuccessNow
 */
fun <T> Collection<CompletionStage<out T>>.mostSuccessResultsOfCompletableFuture(
    valueIfNotSuccess: T, executorWhenTimeout: Executor, timeout: Long, unit: TimeUnit
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mostSuccessResultsOf(valueIfNotSuccess, executorWhenTimeout, timeout, unit, *toTypedArray())

/**
 * Returns a new CompletableFuture with the most results in the **same order** of the given stages arguments
 * in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param executorWhenTimeout the async executor when triggered by timeout
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see getSuccessNow
 */
fun <T> Array<out CompletionStage<out T>>.mostSuccessResultsOfCompletableFuture(
    valueIfNotSuccess: T, executorWhenTimeout: Executor, timeout: Long, unit: TimeUnit
): CompletableFuture<List<T>> =
    CompletableFutureUtils.mostSuccessResultsOf(valueIfNotSuccess, executorWhenTimeout, timeout, unit, *this)

/**
 * Returns a new CompletableFuture with the results in the **same order** of all the given CompletableFutures arguments,
 * the returned new CompletableFuture is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
 *
 * This method is the same as [allOfCompletableFuture],
 * except the returned CompletableFuture contains the results of input CompletableFutures.
 * This method is the same as [CompletableFutureUtils.allResultsOf],
 * providing this method is convenient for method chaining.
 *
 * @see allOfCompletableFuture
 */
fun <T> Collection<CompletionStage<out T>>.allResultsOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allResultsOf(*toTypedArray())

/**
 * Returns a new CompletableFuture with the results in the **same order** of all the given CompletableFutures arguments,
 * the returned new CompletableFuture is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value empty list.
 *
 * This method is the same as [allOfCompletableFuture],
 * except the returned CompletableFuture contains the results of input CompletableFutures.
 * This method is the same as [CompletableFutureUtils.allResultsOf],
 * providing this method is convenient for method chaining.
 *
 * @see allOfCompletableFuture
 */
fun <T> Array<out CompletionStage<out T>>.allResultsOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allResultsOf(*this)

/**
 * Returns a new CompletableFuture that is successful when all the given CompletableFutures success,
 * the results(`CompletableFuture<Void>`) of the given CompletableFutures are not reflected
 * in the returned CompletableFuture, but may be obtained by inspecting them individually.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value `null`.
 *
 * If you need the results of given stages, prefer methods [allResultsFailFastOfCompletableFuture].
 *
 * This method is the same as [CompletableFutureUtils.allFailFastOf],
 * providing this method is convenient for method chaining.
 *
 * @see allResultsFailFastOfCompletableFuture
 */
fun Collection<CompletionStage<*>>.allFailFastOfCompletableFuture(): CompletableFuture<Void> =
    CompletableFutureUtils.allFailFastOf(*toTypedArray())

/**
 * Returns a new CompletableFuture that is successful when all the given CompletableFutures success,
 * the results(`CompletableFuture<Void>`) of the given CompletableFutures are not reflected
 * in the returned CompletableFuture, but may be obtained by inspecting them individually.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value `null`.
 *
 * If you need the results of given stages, prefer methods [allResultsFailFastOfCompletableFuture]
 *
 * This method is the same as [CompletableFutureUtils.allFailFastOf],
 * providing this method is convenient for method chaining.
 *
 * @see allResultsFailFastOfCompletableFuture
 */
fun Array<out CompletionStage<*>>.allFailFastOfCompletableFuture(): CompletableFuture<Void> =
    CompletableFutureUtils.allFailFastOf(*this)

/**
 * Returns a new CompletableFuture that is completed when all the given stages complete.
 * If any of the given stages complete exceptionally, then the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given stages are not reflected
 * in the returned CompletableFuture, but may be obtained by inspecting them individually.
 * If no stages are provided, returns a CompletableFuture completed with the value `null`.
 *
 * If you need the results of given stages, prefer methods [allResultsOfCompletableFuture]
 *
 * This method is the same as [CompletableFutureUtils.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCompletableFuture
 */
fun Collection<CompletionStage<*>>.allOfCompletableFuture(): CompletableFuture<Void> =
    CompletableFutureUtils.allOf(*toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when all the given stages complete.
 * If any of the given stages complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given stages are not reflected
 * in the returned CompletableFuture, but may be obtained by inspecting them individually.
 * If no stages are provided, returns a CompletableFuture completed with the value `null`.
 *
 * If you need the results of given stages, prefer methods [allResultsOfCompletableFuture].
 *
 * This method is the same as [CompletableFutureUtils.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCompletableFuture
 */
fun Array<out CompletionStage<*>>.allOfCompletableFuture(): CompletableFuture<Void> =
    CompletableFutureUtils.allOf(*this)

// endregion
////////////////////////////////////////////////////////////
// region## anyOf* methods for Collection/Array
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new CompletableFuture that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CompletableFutureUtils.anySuccessOf],
 * providing this method is convenient for method chaining.
 *
 * @see anyOfCompletableFuture
 */
fun <T> Collection<CompletionStage<out T>>.anySuccessOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anySuccessOf(*toTypedArray())

/**
 * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new CompletableFuture that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CompletableFutureUtils.anySuccessOf],
 * providing this method is convenient for method chaining.
 *
 * @see anyOfCompletableFuture
 */
fun <T> Array<out CompletionStage<out T>>.anySuccessOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anySuccessOf(*this)

/**
 * Returns a new CompletableFuture that is completed when any of the given CompletableFutures complete,
 * with the same result. Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
 *
 * This method is the same as [CompletableFutureUtils.anyOf], providing this method is convenient for method chaining.
 *
 * @see anySuccessOfCompletableFuture
 */
fun <T> Collection<CompletionStage<out T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOf(*toTypedArray())

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
 *
 * This method is the same as [CompletableFutureUtils.anyOf], providing this method is convenient for method chaining.
 *
 * @see anySuccessOfCompletableFuture
 */
fun <T> Array<out CompletionStage<out T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOf(*this)

// endregion
// endregion
////////////////////////////////////////////////////////////////////////////////
// region# CF Instance Methods(including new enhanced + backport methods)
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////
// region## Then-Multi-Actions(thenM*) Methods
//
//    - thenMApply* (Function[]: T -> U)       -> CompletableFuture<List<U>>
//    - thenMAccept*(Consumer[]: T -> Void)    -> CompletableFuture<Void>
//    - thenMRun*   (Runnable[]: Void -> Void) -> CompletableFuture<Void>
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the **same order** of the given Functions arguments.
 *
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyFailFastAsync(vararg fns: Function<in T, out U>): CompletableFuture<List<U>> =
    CompletableFutureUtils.thenMApplyFailFastAsync(this, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the **same order** of the given Functions arguments.
 *
 * @param executor the executor to use for asynchronous execution
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyFailFastAsync(
    executor: Executor, vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> = CompletableFutureUtils.thenMApplyFailFastAsync(this, executor, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed in the default executor of parameter this
 * with the successful values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the **same order** of the given Functions arguments.
 *
 * If any of the provided functions fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the function having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAllSuccessAsync(
    valueIfFailed: U, vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> = CompletableFutureUtils.thenMApplyAllSuccessAsync(this, valueIfFailed, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed in the given Executor with the successful values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the **same order** of the given Functions arguments.
 *
 * If any of the provided functions fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the function having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAllSuccessAsync(
    valueIfFailed: U, executor: Executor, vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> = CompletableFutureUtils.thenMApplyAllSuccessAsync(this, valueIfFailed, executor, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the most values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the given time(`timeout`, aka as many results as possible in the given time)
 * in the **same order** of the given Functions arguments.
 *
 * If any of the provided functions does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`
 * (which is indistinguishable from the function having a successful value of `valueIfNotSuccess`).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyMostSuccessAsync(
    valueIfNotSuccess: U, timeout: Long, unit: TimeUnit, vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> =
    CompletableFutureUtils.thenMApplyMostSuccessAsync(this, valueIfNotSuccess, timeout, unit, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the most values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the given time(`timeout`, aka as many results as possible in the given time)
 * in the **same order** of the given Functions arguments.
 *
 * If any of the provided functions does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`
 * (which is indistinguishable from the function having a successful value of `valueIfNotSuccess`).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @param executor the executor to use for asynchronous execution
 * @param timeout how long to wait in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyMostSuccessAsync(
    valueIfNotSuccess: U, executor: Executor, timeout: Long, unit: TimeUnit, vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> =
    CompletableFutureUtils.thenMApplyMostSuccessAsync(this, valueIfNotSuccess, executor, timeout, unit, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the **same order** of the given Functions arguments.
 *
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAsync(
    vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> = CompletableFutureUtils.thenMApplyAsync(this, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the values obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions)
 * in the **same order** of the given Functions arguments.
 *
 * @param executor the executor to use for asynchronous execution
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAsync(
    executor: Executor, vararg fns: Function<in T, out U>
): CompletableFuture<List<U>> = CompletableFutureUtils.thenMApplyAsync(this, executor, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with any successful value obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions).
 *
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAnySuccessAsync(vararg fns: Function<in T, out U>): CompletableFuture<U> =
    CompletableFutureUtils.thenMApplyAnySuccessAsync(this, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with any successful value obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions).
 *
 * @param executor the executor to use for asynchronous execution
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAnySuccessAsync(
    executor: Executor, vararg fns: Function<in T, out U>
): CompletableFuture<U> = CompletableFutureUtils.thenMApplyAnySuccessAsync(this, executor, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with any completed result obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions).
 *
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAnyAsync(vararg fns: Function<in T, out U>): CompletableFuture<U> =
    CompletableFutureUtils.thenMApplyAnyAsync(this, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with any completed result obtained by calling the given Functions
 * (with the given stage's result as the argument to the given functions).
 *
 * @param executor the executor to use for asynchronous execution
 * @param fns the functions to use to compute the values of the returned CompletableFuture
 * @param <U> the functions' return type
 * @return the new CompletableFuture
 */
fun <T, U> CompletableFuture<out T>.thenMApplyAnyAsync(
    executor: Executor, vararg fns: Function<in T, out U>
): CompletableFuture<U> = CompletableFutureUtils.thenMApplyAnyAsync(this, executor, *fns)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the given stage's result as the argument to the given actions.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptFailFastAsync(vararg actions: Consumer<in T>): CompletableFuture<Void> =
    CompletableFutureUtils.thenMAcceptFailFastAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the given stage's result as the argument to the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptFailFastAsync(
    executor: Executor, vararg actions: Consumer<in T>
): CompletableFuture<Void> = CompletableFutureUtils.thenMAcceptFailFastAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the given stage's result as the argument to the given actions.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptAsync(vararg actions: Consumer<in T>): CompletableFuture<Void> =
    CompletableFutureUtils.thenMAcceptAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the given stage's result as the argument to the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptAsync(
    executor: Executor, vararg actions: Consumer<in T>
): CompletableFuture<Void> = CompletableFutureUtils.thenMAcceptAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the given stage's result as the argument to the given actions.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptAnySuccessAsync(vararg actions: Consumer<in T>): CompletableFuture<Void> =
    CompletableFutureUtils.thenMAcceptAnySuccessAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the given stage's result as the argument to the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptAnySuccessAsync(
    executor: Executor, vararg actions: Consumer<in T>
): CompletableFuture<Void> = CompletableFutureUtils.thenMAcceptAnySuccessAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the default executor of parameter this,
 * with the given stage's result as the argument to the given actions.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptAnyAsync(vararg actions: Consumer<in T>): CompletableFuture<Void> =
    CompletableFutureUtils.thenMAcceptAnyAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * is executed using the given Executor, with the given stage's result as the argument to the given actions.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<out T>.thenMAcceptAnyAsync(
    executor: Executor, vararg actions: Consumer<in T>
): CompletableFuture<Void> = CompletableFutureUtils.thenMAcceptAnyAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the default executor of parameter this.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see allFailFastOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunFailFastAsync(vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunFailFastAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the given Executor.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see allFailFastOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunFailFastAsync(executor: Executor, vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunFailFastAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the default executor of parameter this.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see allOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunAsync(vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the given Executor.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see allOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunAsync(executor: Executor, vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the default executor of parameter this.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see anySuccessOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunAnySuccessAsync(vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunAnySuccessAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the given Executor.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see anySuccessOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunAnySuccessAsync(
    executor: Executor, vararg actions: Runnable
): CompletableFuture<Void> = CompletableFutureUtils.thenMRunAnySuccessAsync(this, executor, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the default executor of parameter this.
 *
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see anyOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunAnyAsync(vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunAnyAsync(this, *actions)

/**
 * Returns a new CompletableFuture that, when the given stage completes normally,
 * executes the given actions using the given Executor.
 *
 * @param executor the executor to use for asynchronous execution
 * @param actions the actions to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletableFuture.thenRunAsync
 * @see anyOfCompletableFuture
 */
fun CompletableFuture<*>.thenMRunAnyAsync(executor: Executor, vararg actions: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.thenMRunAnyAsync(this, executor, *actions)

// endregion
////////////////////////////////////////////////////////////
// region## Then-Tuple-Multi-Actions(thenTupleM*) Methods
////////////////////////////////////////////////////////////

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> = CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, fn1, fn2)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> = CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, executor, fn1, fn2)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> = CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> =
    CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, executor, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    executor: Executor, fn1: Function<in T, out U1>,
    fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, executor, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, fn1, fn2, fn3, fn4, fn5)

/**
 * Tuple variance of [thenMApplyFailFastAsync].
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyFailFastAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyFailFastAsync(this, executor, fn1, fn2, fn3, fn4, fn5)


/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> = CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, fn1, fn2)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> = CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, executor, fn1, fn2)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> = CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> =
    CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, executor, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, executor, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, fn1, fn2, fn3, fn4, fn5)

/**
 * Tuple variance of [thenMApplyAllSuccessAsync] with `null` valueIfFailed.
 *
 * If any of the provided functions fails, its corresponding position will contain `null`
 * (which is indistinguishable from the function having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyAllSuccessAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyAllSuccessAsync(this, executor, fn1, fn2, fn3, fn4, fn5)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, timeout, unit, fn1, fn2)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    executor: Executor, timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, executor, timeout, unit, fn1, fn2)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, timeout, unit, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    executor: Executor, timeout: Long, unit: TimeUnit,
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, executor, timeout, unit, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>,
    fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, timeout, unit, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    executor: Executor, timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>,
    fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, executor, timeout, unit, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync]
 * with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, timeout, unit, fn1, fn2, fn3, fn4, fn5)

/**
 * Tuple variance of [thenMApplyMostSuccessAsync] with `null` valueIfNotSuccess.
 *
 * If any of the provided suppliers does not success, its corresponding position will contain `null`
 * (which is indistinguishable from the supplier having a successful value of `null`).
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyMostSuccessAsync(
    executor: Executor, timeout: Long, unit: TimeUnit, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyMostSuccessAsync(this, executor, timeout, unit, fn1, fn2, fn3, fn4, fn5)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> = CompletableFutureUtils.thenTupleMApplyAsync(this, fn1, fn2)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2> CompletableFuture<out T>.thenTupleMApplyAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>
): CompletableFuture<Tuple2<U1, U2>> = CompletableFutureUtils.thenTupleMApplyAsync(this, executor, fn1, fn2)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> = CompletableFutureUtils.thenTupleMApplyAsync(this, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2, U3> CompletableFuture<out T>.thenTupleMApplyAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>
): CompletableFuture<Tuple3<U1, U2, U3>> = CompletableFutureUtils.thenTupleMApplyAsync(this, executor, fn1, fn2, fn3)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> = CompletableFutureUtils.thenTupleMApplyAsync(this, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2, U3, U4> CompletableFuture<out T>.thenTupleMApplyAsync(
    executor: Executor,
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>, fn3: Function<in T, out U3>, fn4: Function<in T, out U4>
): CompletableFuture<Tuple4<U1, U2, U3, U4>> =
    CompletableFutureUtils.thenTupleMApplyAsync(this, executor, fn1, fn2, fn3, fn4)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyAsync(
    fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyAsync(this, fn1, fn2, fn3, fn4, fn5)

/**
 * Tuple variance of [thenMApplyAsync].
 */
fun <T, U1, U2, U3, U4, U5> CompletableFuture<out T>.thenTupleMApplyAsync(
    executor: Executor, fn1: Function<in T, out U1>, fn2: Function<in T, out U2>,
    fn3: Function<in T, out U3>, fn4: Function<in T, out U4>, fn5: Function<in T, out U5>
): CompletableFuture<Tuple5<U1, U2, U3, U4, U5>> =
    CompletableFutureUtils.thenTupleMApplyAsync(this, executor, fn1, fn2, fn3, fn4, fn5)

// endregion
////////////////////////////////////////////////////////////
// region## thenBoth* Methods(binary input) with fast-fail support
//
//    - thenCombineFailFast*   (BiFunction: (T, U) -> V)    -> CompletableFuture<U>
//    - thenAcceptBothFailFast*(BiConsumer: (T, U) -> Void) -> CompletableFuture<Void>
//    - runAfterBothFailFast*  (Runnable:   Void -> Void)   -> CompletableFuture<Void>
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletableFuture that, when tow given stage both complete normally,
 * is executed with the two results as arguments to the supplied function.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * @param fn the function to use to compute the value of the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.thenCombine
 */
fun <T, U, V> CompletableFuture<out T>.thenCombineFailFast(
    other: CompletionStage<out U>, fn: BiFunction<in T, in U, out V>
): CompletableFuture<V> = CompletableFutureUtils.thenCombineFailFast(this, other, fn)

/**
 * Returns a new CompletableFuture that, when tow given stage both complete normally,
 * is executed using CompletableFuture's default asynchronous execution facility,
 * with the two results as arguments to the supplied function.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.thenCombineAsync] except for the fast-fail behavior.
 *
 * @param fn the function to use to compute the value of the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.thenCombineAsync
 */
fun <T, U, V> CompletableFuture<out T>.thenCombineFailFastAsync(
    other: CompletionStage<out U>, fn: BiFunction<in T, in U, out V>
): CompletableFuture<V> = CompletableFutureUtils.thenCombineFailFastAsync(this, other, fn)

/**
 * Returns a new CompletableFuture that, when tow given stage both complete normally,
 * is executed using the supplied executor,
 * with the two results as arguments to the supplied function.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.thenCombineAsync] except for the fast-fail behavior.
 *
 * @param fn the function to use to compute the value of the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.thenCombineAsync
 */
fun <T, U, V> CompletableFuture<out T>.thenCombineFailFastAsync(
    other: CompletionStage<out U>, fn: BiFunction<in T, in U, out V>, executor: Executor
): CompletableFuture<V> = CompletableFutureUtils.thenCombineFailFastAsync(this, other, fn, executor)

/**
 * Returns a new CompletableFuture that, when tow given stage both complete normally,
 * is executed with the two results as arguments to the supplied action.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.thenAcceptBoth] except for the fast-fail behavior.
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.thenAcceptBoth
 */
fun <T, U> CompletableFuture<out T>.thenAcceptBothFailFast(
    other: CompletionStage<out U>, action: BiConsumer<in T, in U>
): CompletableFuture<Void> = CompletableFutureUtils.thenAcceptBothFailFast(this, other, action)

/**
 * Returns a new CompletableFuture that, when tow given stage both complete normally,
 * is executed using CompletableFuture's default asynchronous execution facility,
 * with the two results as arguments to the supplied action.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.thenAcceptBothAsync] except for the fast-fail behavior.
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.thenAcceptBothAsync
 */
fun <T, U> CompletableFuture<out T>.thenAcceptBothFailFastAsync(
    other: CompletionStage<out U>, action: BiConsumer<in T, in U>
): CompletableFuture<Void> = CompletableFutureUtils.thenAcceptBothFailFastAsync(this, other, action)

/**
 * Returns a new CompletableFuture that, when tow given stage both complete normally,
 * is executed using the supplied executor,
 * with the two results as arguments to the supplied action.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.thenAcceptBothAsync] except for the fast-fail behavior.
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.thenAcceptBothAsync
 */
fun <T, U> CompletableFuture<out T>.thenAcceptBothFailFastAsync(
    other: CompletionStage<out U>, action: BiConsumer<in T, in U>, executor: Executor
): CompletableFuture<Void> = CompletableFutureUtils.thenAcceptBothFailFastAsync(this, other, action, executor)

/**
 * Returns a new CompletableFuture that, when two given stages both complete normally, executes the given action.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so **without** waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.runAfterBoth] except for the fast-fail behavior.
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.runAfterBoth
 */
fun CompletableFuture<*>.runAfterBothFailFast(other: CompletionStage<*>, action: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.runAfterBothFailFast(this, other, action)

/**
 * Returns a new CompletableFuture that, when two given stages both complete normally,
 * executes the given action using CompletableFuture's default asynchronous execution facility.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so **without** waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.runAfterBothAsync] except for the fast-fail behavior.
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.runAfterBothAsync
 */
fun CompletableFuture<*>.runAfterBothFailFastAsync(
    other: CompletionStage<*>,
    action: Runnable
): CompletableFuture<Void> = CompletableFutureUtils.runAfterBothFailFastAsync(this, other, action)

/**
 * Returns a new CompletableFuture that, when two given stages both complete normally,
 * executes the given action using the supplied executor.
 * if any of the given stage complete exceptionally, then the returned CompletableFuture
 * also does so **without** waiting other incomplete given CompletionStage,
 * with a CompletionException holding this exception as its cause.
 *
 * This method is the same as [CompletableFuture.runAfterBothAsync] except for the fast-fail behavior.
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.runAfterBothAsync
 */
fun CompletableFuture<*>.runAfterBothFailFastAsync(
    other: CompletionStage<*>, action: Runnable, executor: Executor
): CompletableFuture<Void> = CompletableFutureUtils.runAfterBothFailFastAsync(this, other, action, executor)

// endregion
////////////////////////////////////////////////////////////
// region## thenEither* Methods(binary input) with either(any)-success support
//
//    - applyToEitherSuccess* (Function: (T) -> U)     -> CompletableFuture<U>
//    - acceptEitherSuccess*  (Consumer: (T) -> Void)  -> CompletableFuture<Void>
//    - runAfterEitherSuccess*(Runnable: Void -> Void) -> CompletableFuture<Void>
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletionStage that, when either given stage success,
 * is executed with the corresponding result as argument to the supplied function.
 *
 * This method is the same as [CompletableFuture.applyToEither]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param fn the function to use to compute the value of the returned CompletableFuture
 * @param <U> the function's return type
 * @return the new CompletableFuture
 * @see CompletionStage.applyToEither
 */
fun <T, U> CompletableFuture<out T>.applyToEitherSuccess(
    other: CompletionStage<out T>, fn: Function<in T, out U>
): CompletableFuture<U> = CompletableFutureUtils.applyToEitherSuccess(this, other, fn)

/**
 * Returns a new CompletionStage that, when either given stage success,
 * is executed using this stage's default asynchronous execution facility,
 * with the corresponding result as argument to the supplied function.
 *
 * This method is the same as [CompletableFuture.applyToEitherAsync]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param fn the function to use to compute the value of the returned CompletableFuture
 * @param <U> the function's return type
 * @return the new CompletableFuture
 * @see CompletionStage.applyToEitherAsync
 */
fun <T, U> CompletableFuture<out T>.applyToEitherSuccessAsync(
    other: CompletionStage<out T>, fn: Function<in T, out U>
): CompletableFuture<U> = CompletableFutureUtils.applyToEitherSuccessAsync(this, other, fn)

/**
 * Returns a new CompletionStage that, when either given stage success,
 * is executed using the supplied executor, with the corresponding result as argument to the supplied function.
 *
 * This method is the same as [CompletableFuture.applyToEitherAsync]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param fn the function to use to compute the value of the returned CompletableFuture
 * @param executor the executor to use for asynchronous execution
 * @param <U> the function's return type
 * @return the new CompletableFuture
 * @see CompletionStage.applyToEitherAsync
 */
fun <T, U> CompletableFuture<out T>.applyToEitherSuccessAsync(
    other: CompletionStage<out T>, fn: Function<in T, out U>, executor: Executor
): CompletableFuture<U> = CompletableFutureUtils.applyToEitherSuccessAsync(this, other, fn, executor)

/**
 * Returns a new CompletableFuture that, when either given stage success,
 * is executed with the corresponding result as argument to the supplied action.
 *
 * This method is the same as [CompletableFuture.acceptEither]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.acceptEither
 */
fun <T> CompletableFuture<out T>.acceptEitherSuccess(
    other: CompletionStage<out T>, action: Consumer<in T>
): CompletableFuture<Void> = CompletableFutureUtils.acceptEitherSuccess(this, other, action)

/**
 * Returns a new CompletionStage that, when either given stage success,
 * is executed using this stage's default asynchronous execution facility,
 * with the corresponding result as argument to the supplied action.
 *
 * This method is the same as [CompletableFuture.acceptEitherAsync]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.acceptEitherAsync
 */
fun <T> CompletableFuture<out T>.acceptEitherSuccessAsync(
    other: CompletionStage<out T>, action: Consumer<in T>
): CompletableFuture<Void> = CompletableFutureUtils.acceptEitherSuccessAsync(this, other, action)

/**
 * Returns a new CompletionStage that, when either given stage success,
 * is executed using the supplied executor, with the corresponding result as argument to the supplied action.
 *
 * This method is the same as [CompletableFuture.acceptEitherAsync]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletableFuture
 * @see CompletionStage.acceptEitherAsync
 */
fun <T> CompletableFuture<out T>.acceptEitherSuccessAsync(
    other: CompletionStage<out T>, action: Consumer<in T>, executor: Executor
): CompletableFuture<Void> = CompletableFutureUtils.acceptEitherSuccessAsync(this, other, action, executor)

/**
 * Returns a new CompletableFuture that, when either given stage success, executes the given action.
 * Otherwise, all two given CompletionStage complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletionStage as its cause.
 *
 * This method is the same as [CompletableFuture.runAfterEither]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.runAfterEither
 */
fun CompletableFuture<*>.runAfterEitherSuccess(other: CompletionStage<*>, action: Runnable): CompletableFuture<Void> =
    CompletableFutureUtils.runAfterEitherSuccess(this, other, action)

/**
 * Returns a new CompletableFuture that, when either given stage success, executes the given action
 * using CompletableFuture's default asynchronous execution facility.
 * Otherwise, all two given CompletionStage complete exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding an exception from any of the given CompletionStage as its cause.
 *
 * This method is the same as [CompletableFuture.runAfterEitherAsync]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.runAfterEitherAsync
 */
fun CompletableFuture<*>.runAfterEitherSuccessAsync(
    other: CompletionStage<*>, action: Runnable
): CompletableFuture<Void> = CompletableFutureUtils.runAfterEitherSuccessAsync(this, other, action)

/**
 * Returns a new CompletableFuture that, when either given stage success, executes the given action
 * using the supplied executor. Otherwise, all two given CompletionStage complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletionStage as its cause.
 *
 * This method is the same as [CompletableFuture.runAfterEitherAsync]
 * except for the either-**success** behavior(not either-**complete**).
 *
 * @param action the action to perform before completing the returned CompletableFuture
 * @return the new CompletableFuture
 * @see CompletionStage.runAfterEitherAsync
 */
fun CompletableFuture<*>.runAfterEitherSuccessAsync(
    other: CompletionStage<*>, action: Runnable, executor: Executor
): CompletableFuture<Void> = CompletableFutureUtils.runAfterEitherSuccessAsync(this, other, action, executor)

// endregion
////////////////////////////////////////////////////////////
// region## Error Handling Methods of CompletionStage
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletionStage that, when this stage completes exceptionally with the given exceptionType,
 * is executed with this stage's exception as the argument to the supplied function.
 * Otherwise, the returned stage contains same result as input CompletionStage.
 *
 * @param exceptionType the exception type that triggers use of `fallback`. The exception type is matched
 * against the input's exception. To avoid hiding bugs and other unrecoverable errors,
 * callers should prefer more specific types, avoiding `Throwable.class` in particular.
 * @param fallback the Function to be called if `input` fails with the expected exception type.
 * The function's argument is the input's exception.
 */
fun <T, X : Throwable, C : CompletionStage<in T>> C.catching(
    exceptionType: Class<X>, fallback: Function<in X, out T>
): C = CompletableFutureUtils.catching(this, exceptionType, fallback)

/**
 * Returns a new CompletionStage that, when this stage completes exceptionally with the given exceptionType,
 * is executed with this stage's exception as the argument to the supplied function,
 * using this stage's default asynchronous execution facility.
 * Otherwise, the returned stage contains same result as input CompletionStage.
 *
 * @param exceptionType the exception type that triggers use of `fallback`. The exception type is matched
 * against the input's exception. To avoid hiding bugs and other unrecoverable errors,
 * callers should prefer more specific types, avoiding `Throwable.class` in particular.
 * @param fallback the Function to be called if `input` fails with the expected exception type.
 * The function's argument is the input's exception.
 */
fun <T, X : Throwable, C : CompletionStage<in T>> C.catchingAsync(
    exceptionType: Class<X>, fallback: Function<in X, out T>
): C = CompletableFutureUtils.catchingAsync(this, exceptionType, fallback)

/**
 * Returns a new CompletionStage that, when this stage completes exceptionally with the given exceptionType,
 * is executed with this stage's exception as the argument to the supplied function, using the supplied Executor.
 * Otherwise, the returned stage contains same result as input CompletionStage.
 *
 * @param exceptionType the exception type that triggers use of `fallback`. The exception type is matched
 * against the input's exception. To avoid hiding bugs and other unrecoverable errors,
 * callers should prefer more specific types, avoiding `Throwable.class` in particular.
 * @param fallback the Function to be called if `input` fails with the expected exception type.
 * The function's argument is the input's exception.
 * @param executor the executor to use for asynchronous execution
 */
fun <T, X : Throwable, C : CompletionStage<in T>> C.catchingAsync(
    exceptionType: Class<X>, fallback: Function<in X, out T>, executor: Executor
): C = CompletableFutureUtils.catchingAsync(this, exceptionType, fallback, executor)

/**
 * Returns a new CompletionStage that, when this stage completes exceptionally, is executed with this
 * stage's exception as the argument to the supplied function, using this stage's
 * default asynchronous execution facility. Otherwise, if this stage completes normally,
 * then the returned stage also completes normally with the same value.
 *
 * @param fn the function to use to compute the value of the returned CompletionStage
 * if this CompletionStage completed exceptionally
 * @return the new CompletionStage
 */
fun <T, C : CompletionStage<in T>> C.exceptionallyAsync(fn: Function<Throwable, out T>): C =
    CompletableFutureUtils.exceptionallyAsync(this, fn)

/**
 * Returns a new CompletionStage that, when this stage completes exceptionally, is executed with this
 * stage's exception as the argument to the supplied function, using the supplied Executor. Otherwise,
 * if this stage completes normally, then the returned stage also completes normally with the same value.
 *
 * @param fn the function to use to compute the value of the returned CompletionStage
 * if this CompletionStage completed exceptionally
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletionStage
 */
fun <T, C : CompletionStage<in T>> C.exceptionallyAsync(fn: Function<Throwable, out T>, executor: Executor): C =
    CompletableFutureUtils.exceptionallyAsync(this, fn, executor)

// endregion
////////////////////////////////////////////////////////////
// region## Timeout Control Methods of CompletableFuture
////////////////////////////////////////////////////////////

/**
 * Exceptionally completes this CompletableFuture with a TimeoutException
 * if not otherwise completed before the given timeout.
 *
 * Uses CompletableFuture's default asynchronous execution facility as `executorWhenTimeout`.
 *
 * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return the new CompletableFuture
 */
fun <C : CompletableFuture<*>> C.cffuOrTimeout(timeout: Long, unit: TimeUnit): C =
    CompletableFutureUtils.cffuOrTimeout(this, timeout, unit)

/**
 * Exceptionally completes this CompletableFuture with a TimeoutException
 * if not otherwise completed before the given timeout.
 *
 * @param executorWhenTimeout the async executor when triggered by timeout
 * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return the new CompletableFuture
 */
fun <C : CompletableFuture<*>> C.cffuOrTimeout(executorWhenTimeout: Executor, timeout: Long, unit: TimeUnit): C =
    CompletableFutureUtils.cffuOrTimeout(this, executorWhenTimeout, timeout, unit)

/**
 * Exceptionally completes this CompletableFuture with a [TimeoutException]
 * if not otherwise completed before the given timeout.
 *
 * **CAUTION:** This method and [CompletableFuture.orTimeout] is **UNSAFE**!
 *
 * When triggered by timeout, the subsequent non-async actions of the dependent CompletableFutures
 * are performed in the **SINGLE thread builtin executor** of CompletableFuture for delay execution (including
 * timeout function). So the long-running subsequent non-async actions lead to the CompletableFuture dysfunction
 * (including delay execution and timeout).
 *
 * **Strong recommend** using the safe method [cffuOrTimeout] instead of this method and [CompletableFuture.orTimeout].
 * <br/>Unless all subsequent actions of dependent CompletableFutures is ensured executing async
 * (aka. the dependent CompletableFutures is created by async methods), using this method and [CompletableFuture.orTimeout]
 * is one less thread switch of task execution when triggered by timeout.
 *
 * Note: Before Java 21(Java 20-), [CompletableFuture.orTimeout] leaks if the future completes exceptionally,
 * more info see [issue JDK-8303742](https://bugs.openjdk.org/browse/JDK-8303742),
 * [PR review openjdk/jdk/13059](https://github.com/openjdk/jdk/pull/13059)
 * and [JDK bugfix commit](https://github.com/openjdk/jdk/commit/ded6a8131970ac2f7ae59716769e6f6bae3b809a).
 * The cffu backport logic(for Java 20-) has merged the fix of this JDK bug.
 *
 * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return this CompletableFuture
 * @see cffuOrTimeout
 */
fun <C : CompletableFuture<*>> C.orTimeout(timeout: Long, unit: TimeUnit): C =
    CompletableFutureUtils.orTimeout(this, timeout, unit)

/**
 * Completes this CompletableFuture with the given value if not otherwise completed before the given timeout.
 *
 * Uses CompletableFuture's default asynchronous execution facility as `executorWhenTimeout`.
 *
 * **CAUTION:** This method returns a new CompletableFuture and this behavior is DIFFERENT from the original CF method
 * [CompletableFuture.completeOnTimeout] and its backport method [completeOnTimeout], because the returned new CF
 * instance avoids the subsequent usage of the delay thread. More info see the javadoc of [completeOnTimeout] and the demo
 * [DelayDysfunctionDemo](https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java).
 *
 * @param value the value to use upon timeout
 * @param timeout how long to wait before completing normally with the given value, in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return the new CompletableFuture
 */
fun <T, C : CompletableFuture<in T>> C.cffuCompleteOnTimeout(
    value: T, timeout: Long, unit: TimeUnit
): C = CompletableFutureUtils.cffuCompleteOnTimeout(this, value, timeout, unit)

/**
 * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
 *
 * **CAUTION:** This method returns a new CompletableFuture and this behavior is DIFFERENT from the original CF method
 * [CompletableFuture.completeOnTimeout] and its backport method [completeOnTimeout], because the returned new CF
 * instance avoids the subsequent usage of the delay thread. More info see the javadoc of [completeOnTimeout] and the demo
 * [DelayDysfunctionDemo](https://github.com/foldright/cffu/blob/main/cffu-core/src/test/java/io/foldright/demo/CfDelayDysfunctionDemo.java).
 *
 * @param value the value to use upon timeout
 * @param executorWhenTimeout the async executor when triggered by timeout
 * @param timeout how long to wait before completing normally with the given value, in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return the new CompletableFuture
 */
fun <T, C : CompletableFuture<in T>> C.cffuCompleteOnTimeout(
    value: T, executorWhenTimeout: Executor, timeout: Long, unit: TimeUnit
): C = CompletableFutureUtils.cffuCompleteOnTimeout(this, value, executorWhenTimeout, timeout, unit)

/**
 * Completes this CompletableFuture with the given value if not otherwise completed before the given timeout.
 *
 * **CAUTION:** This method and [CompletableFuture.completeOnTimeout] is **UNSAFE**!
 *
 * When triggered by timeout, the subsequent non-async actions of the dependent CompletableFutures
 * are performed in the **SINGLE thread builtin executor** of CompletableFuture for delay execution
 * (including timeout function). So the long-running subsequent non-async actions lead to
 * the CompletableFuture dysfunction (including delay execution and timeout).
 *
 * **Strong recommend** using the safe method [cffuCompleteOnTimeout]
 * instead of this method and [CompletableFuture.completeOnTimeout].<br/>
 * Unless all subsequent actions of dependent CompletableFutures is ensured executing async(aka. the dependent
 * CompletableFutures is created by async methods), using this method and [CompletableFuture.completeOnTimeout]
 * is one less thread switch of task execution when triggered by timeout.
 *
 * @param value the value to use upon timeout
 * @param timeout how long to wait before completing normally with the given value, in units of `unit`
 * @param unit a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return this CompletableFuture
 * @see cffuCompleteOnTimeout
 */
fun <T, C : CompletableFuture<in T>> C.completeOnTimeout(value: T, timeout: Long, unit: TimeUnit): C =
    CompletableFutureUtils.completeOnTimeout(this, value, timeout, unit)

// endregion
////////////////////////////////////////////////////////////
// region## Advanced Methods of CompletionStage(compose* and handle-like methods)
//
// NOTE about advanced meaning:
//   - `compose` methods, input function argument return CompletionStage
//   - handle successful and failed result together(handle*/whenComplete*/peek*)
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
 * is composed using the results of the supplied function applied to given stage's exception.
 *
 * @param exceptionType the exception type that triggers use of `fallback`. The exception type is matched
 * against the input's exception. To avoid hiding bugs and other unrecoverable errors,
 * callers should prefer more specific types, avoiding `Throwable.class` in particular.
 * @param fallback the Function to be called if `input` fails with the expected exception type.
 * The function's argument is the input's exception.
 */
fun <T, X : Throwable, C : CompletionStage<in T>> C.catchingCompose(
    exceptionType: Class<X>, fallback: Function<in X, out CompletionStage<T>>
): C = CompletableFutureUtils.catchingCompose(this, exceptionType, fallback)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
 * is composed using the results of the supplied function applied to given stage's exception,
 * using given stage's default asynchronous execution facility.
 *
 * @param exceptionType the exception type that triggers use of `fallback`. The exception type is matched
 * against the input's exception. To avoid hiding bugs and other unrecoverable errors,
 * callers should prefer more specific types, avoiding `Throwable.class` in particular.
 * @param fallback the Function to be called if `input` fails with the expected exception type.
 * The function's argument is the input's exception.
 */
fun <T, X : Throwable, C : CompletionStage<in T>> C.catchingComposeAsync(
    exceptionType: Class<X>, fallback: Function<in X, out CompletionStage<T>>
): C = CompletableFutureUtils.catchingComposeAsync(this, exceptionType, fallback)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally with the given exceptionType,
 * is composed using the results of the supplied function applied to given's exception,
 * using the supplied Executor.
 *
 * @param exceptionType the exception type that triggers use of `fallback`. The exception type is matched
 * against the input's exception. To avoid hiding bugs and other unrecoverable errors,
 * callers should prefer more specific types, avoiding `Throwable.class` in particular.
 * @param fallback the Function to be called if `input` fails with the expected exception type.
 * The function's argument is the input's exception.
 * @param executor the executor to use for asynchronous execution
 */
fun <T, X : Throwable, C : CompletionStage<in T>> C.catchingComposeAsync(
    exceptionType: Class<X>, fallback: Function<in X, out CompletionStage<T>>,
    executor: Executor
): C = CompletableFutureUtils.catchingComposeAsync(this, exceptionType, fallback, executor)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally,
 * is composed using the results of the supplied function applied to given stage's exception.
 *
 * @param fn the function to use to compute the returned
 *           CompletionStage if given CompletionStage completed exceptionally
 * @return the new CompletionStage
 */
fun <T, C : CompletionStage<in T>> C.exceptionallyCompose(fn: Function<Throwable, out CompletionStage<T>>): C =
    CompletableFutureUtils.exceptionallyCompose(this, fn)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally,
 * is composed using the results of the supplied function applied to given stage's exception,
 * using given stage's default asynchronous execution facility.
 *
 * @param fn the function to use to compute the returned
 *           CompletionStage if given CompletionStage completed exceptionally
 * @return the new CompletionStage
 */
fun <T, C : CompletionStage<in T>> C.exceptionallyComposeAsync(fn: Function<Throwable, out CompletionStage<T>>): C =
    CompletableFutureUtils.exceptionallyComposeAsync(this, fn)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally, is composed using
 * the results of the supplied function applied to given stage's exception, using the supplied Executor.
 *
 * @param fn the function to use to compute the returned CompletionStage
 *                 if given CompletionStage completed exceptionally
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletionStage
 */
fun <T, C : CompletionStage<in T>> C.exceptionallyComposeAsync(
    fn: Function<Throwable, out CompletionStage<T>>, executor: Executor
): C = CompletableFutureUtils.exceptionallyComposeAsync(this, fn, executor)

/**
 * Peeks the result by executing the given action when this stage completes, returns this stage.
 *
 * When this stage is complete, the given action is invoked with the result(or `null` if none)
 * and the exception (or `null` if none) of this stage as arguments.
 *
 * **CAUTION:** The return stage of method [CompletionStage.whenComplete]
 * will contain **DIFFERENT** result to the input stage when the input stage is successful
 * but the supplied action throws an exception. This behavior of method `whenComplete` is subtle,
 * and common misused if you just want to **peek** the input stage without affecting the result(e.g.
 * logging the cf result).<br/>For this `peek` method, whether the supplied action throws an exception or not,
 * the result of return stage(aka. this stage) is **NOT** affected.
 *
 * Unlike method [CompletionStage.handle] and like method [CompletionStage.whenComplete],
 * this method is not designed to translate completion outcomes.
 *
 * @param action the action to perform
 * @return this stage
 * @see CompletionStage.whenComplete
 * @see java.util.stream.Stream.peek
 */
fun <T, C : CompletionStage<out T>> C.peek(action: BiConsumer<in T, in Throwable>): C =
    CompletableFutureUtils.peek(this, action)

/**
 * Peeks the result by executing the given action using the default executor of this stage
 * when this stage completes, returns this stage.
 *
 * When this stage is complete, the given action is invoked with the result(or `null` if none)
 * and the exception (or `null` if none) of this as arguments.
 *
 * **CAUTION:** The return stage of method [CompletionStage.whenCompleteAsync]
 * will contain **DIFFERENT** result to the input stage when the input stage is successful
 * but the supplied action throws an exception. This behavior of method `whenComplete` is subtle,
 * and common misused if you just want to **peek** the input stage without affecting the result(e.g.
 * logging the cf result).<br/>For this `peek` method, whether the supplied action throws an exception or not,
 * the result of return stage(aka. this stage) is **NOT** affected.
 *
 * Unlike method [CompletionStage.handleAsync] and like method [CompletionStage.whenCompleteAsync],
 * this method is not designed to translate completion outcomes.
 *
 * @param action the action to perform
 * @return this stage
 * @see CompletionStage.whenCompleteAsync
 * @see java.util.stream.Stream.peek
 */
fun <T, C : CompletionStage<out T>> C.peekAsync(action: BiConsumer<in T, in Throwable>): C =
    CompletableFutureUtils.peekAsync(this, action)

/**
 * Peeks the result by executing the given action using the supplied executor
 * when this stage completes, returns this stage.
 *
 * When this stage is complete, the given action is invoked with the result(or `null` if none)
 * and the exception (or `null` if none) of this stage as arguments.
 *
 * **CAUTION:** The return stage of method [CompletionStage.whenCompleteAsync]
 * will contain **DIFFERENT** result to the input stage when the input stage is successful
 * but the supplied action throws an exception. This behavior of method `whenComplete` is subtle,
 * and common misused if you just want to **peek** the input stage without affecting the result(e.g.
 * logging the cf result).<br/>For this `peek` method, whether the supplied action throws an exception or not,
 * the result of return stage(aka. this stage) is **NOT** affected.
 *
 * Unlike method [CompletionStage.handleAsync] and like method [CompletionStage.whenCompleteAsync],
 * this method is not designed to translate completion outcomes.
 *
 * @param action the action to perform
 * @return this stage
 * @see CompletionStage.whenCompleteAsync
 * @see java.util.stream.Stream.peek
 */
fun <T, C : CompletionStage<out T>> C.peekAsync(action: BiConsumer<in T, in Throwable>, executor: Executor): C =
    CompletableFutureUtils.peekAsync(this, action, executor)

// endregion
////////////////////////////////////////////////////////////
// region## Read(explicitly) Methods of CompletableFuture(including Future)
////////////////////////////////////////////////////////////

/**
 * Waits if necessary for at most the given time for the computation to complete,
 * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
 *
 * **NOTE:** Calling this method
 *
 * `val result = cf.join(timeout, unit);`
 *
 * is the same as:
 *
 * ```
 * val result = cf.copy() // defensive copy to avoid writing this cf unexpectedly
 *     .orTimeout(timeout, unit)
 *     .join();
 * ```
 *
 * **CAUTION:** If the wait timed out, this method throws an (unchecked) CompletionException
 * with the TimeoutException as its cause; NOT throws a (checked) TimeoutException like [CompletableFuture.get].
 *
 * @param timeout the maximum time to wait
 * @param unit the time unit of the timeout argument
 * @return the result value
 * @see CompletableFuture.join
 */
@Suppress("UNCHECKED_CAST")
fun <T> CompletableFuture<out T>.join(timeout: Long, unit: TimeUnit): T =
    CompletableFutureUtils.join(this, timeout, unit) as T

/**
 * Returns the result value if completed successfully, else returns the given valueIfNotSuccess.
 *
 * This method will not throw exceptions
 * (CancellationException/CompletionException/ExecutionException/IllegalStateException/...).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @return the result value, if completed successfully, else the given valueIfNotSuccess
 */
@Suppress("UNCHECKED_CAST")
fun <T> CompletableFuture<out T>.getSuccessNow(valueIfNotSuccess: T): T =
    CompletableFutureUtils.getSuccessNow(this, valueIfNotSuccess) as T

/**
 * Returns the computed result, without waiting.
 *
 * This method is for cases where the caller knows that the task has already completed successfully,
 * for example when filtering Future objects for the successful tasks
 * and using a mapping operation to obtain results.
 *
 * ```
 * val results = futures
 *     .filter { it.state() == Future.State.SUCCESS }
 *     .map { it.resultNow() }
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T> Future<out T>.resultNow(): T =
    CompletableFutureUtils.resultNow(this) as T

/**
 * Returns the exception thrown by the task, without waiting.
 *
 * This method is for cases where the caller knows that the task has already completed with an exception.
 *
 * @return the exception thrown by the task
 * @throws IllegalStateException if the task has not completed, the task completed normally,
 *                               or the task was cancelled
 */
fun Future<*>.exceptionNow(): Throwable =
    CompletableFutureUtils.exceptionNow(this)

/**
 * Returns the computation state([CffuState]), this method is equivalent to [CompletableFuture.state]
 * with java version compatibility logic, so you can invoke in old `java 18-`.
 *
 * @return the computation state
 * @see java.util.concurrent.Future.state
 * @see CompletableFuture.state
 */
fun Future<*>.cffuState(): CffuState =
    CompletableFutureUtils.state(this)

// endregion
////////////////////////////////////////////////////////////
// region## Write Methods of CompletableFuture
////////////////////////////////////////////////////////////

/**
 * Completes given CompletableFuture with the result of the given Supplier function invoked
 * from an asynchronous task using the default executor.
 *
 * @param supplier a function returning the value to be used to complete given CompletableFuture
 * @return given CompletableFuture
 */
fun <T, C : CompletableFuture<in T>> C.completeAsync(supplier: Supplier<out T>): C =
    CompletableFutureUtils.completeAsync(this, supplier)

/**
 * Completes given CompletableFuture with the result of the given Supplier function invoked
 * from an asynchronous task using the given executor.
 *
 * @param supplier a function returning the value to be used to complete given CompletableFuture
 * @param executor the executor to use for asynchronous execution
 * @return given CompletableFuture
 */
fun <T, C : CompletableFuture<in T>> C.completeAsync(supplier: Supplier<out T>, executor: Executor): C =
    CompletableFutureUtils.completeAsync(this, supplier, executor)

/**
 * If not already completed, completes given CompletableFuture with the exception result
 * of the given Supplier function invoked from an asynchronous task using the default executor.
 *
 * @param supplier a function returning the value to be used to complete given CompletableFuture
 * @return the given CompletableFuture
 * @see CompletableFuture.completeExceptionally
 */
fun <C : CompletableFuture<*>> C.completeExceptionallyAsync(supplier: Supplier<out Throwable>): C =
    CompletableFutureUtils.completeExceptionallyAsync(this, supplier)

/**
 * If not already completed, completes given CompletableFuture with the exception result
 * of the given Supplier function invoked from an asynchronous task using the given executor.
 *
 * @param supplier a function returning the value to be used to complete given CompletableFuture
 * @param executor the executor to use for asynchronous execution
 * @return the given CompletableFuture
 * @see CompletableFuture.completeExceptionally
 */
fun <C : CompletableFuture<*>> C.completeExceptionallyAsync(supplier: Supplier<out Throwable>, executor: Executor): C =
    CompletableFutureUtils.completeExceptionallyAsync(this, supplier, executor)

// endregion
////////////////////////////////////////////////////////////
// region## Re-Config Methods of CompletableFuture
////////////////////////////////////////////////////////////

/**
 * Returns a new CompletionStage that is completed normally with the same value as given CompletableFuture
 * when it completes normally, and cannot be independently completed or otherwise used in ways
 * not defined by the methods of interface [CompletionStage].
 * If given CompletableFuture completes exceptionally, then the returned CompletionStage completes exceptionally
 * with a CompletionException with given exception as cause.
 *
 * ***CAUTION:*** if run on old Java 8(not support *minimal* CompletionStage),
 * just return a ***normal*** CompletableFuture which is NOT with a ***minimal*** CompletionStage.
 *
 * @return the new CompletionStage
 */
fun <T> CompletableFuture<T>.minimalCompletionStage(): CompletionStage<T> =
    CompletableFutureUtils.minimalCompletionStage(this)

/**
 * Returns a new CompletableFuture that is completed normally with the same value as this CompletableFuture when
 * it completes normally. If this CompletableFuture completes exceptionally, then the returned CompletableFuture
 * completes exceptionally with a CompletionException with this exception as cause. The behavior is equivalent
 * to `thenApply(x -> x)`. This method may be useful as a form of "defensive copying", to prevent clients
 * from completing, while still being able to arrange dependent actions.
 *
 * @return the new CompletableFuture
 */
fun <T> CompletableFuture<T>.copy(): CompletableFuture<T> =
    CompletableFutureUtils.copy(this)

/**
 * Returns the default Executor of parameter cfThis used for async methods that do not specify an Executor.
 *
 * The default executor of CompletableFuture(**NOT** including the customized subclasses of CompletableFuture)
 * uses the [ForkJoinPool.commonPool] if it supports more than one parallel thread, or else an Executor using
 * one thread per async task. **CAUTION:** This executor may be not suitable for common biz use(io intensive).
 *
 * @see CompletableFuture.defaultExecutor
 */
fun CompletionStage<*>.defaultExecutor(): Executor = CompletableFutureUtils.defaultExecutor(this)

/**
 * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
 *
 * @param <T> the type of the value
 * @return a new CompletableFuture
 */
fun <U> CompletableFuture<*>.newIncompleteFuture(): CompletableFuture<U> =
    CompletableFutureUtils.newIncompleteFuture(this)

// endregion
// endregion
////////////////////////////////////////////////////////////////////////////////
// region# toCompletableFuture Conversion Methods for Collection/Array
////////////////////////////////////////////////////////////////////////////////

/**
 * Convert [CompletionStage] (including [Cffu]) collection elements to [CompletableFuture].
 *
 * This method is the same as [CompletableFutureUtils.toCompletableFutureArray],
 * providing this method is convenient for method chaining.
 */
fun <T> Collection<CompletionStage<T>>.toCompletableFuture(): List<CompletableFuture<T>> =
    map { it.toCompletableFuture() }

/**
 * Convert [CompletionStage] (including [Cffu]) array elements to [CompletableFuture].
 *
 * This method is the same as [CompletableFutureUtils.toCompletableFutureArray],
 * providing this method is convenient for method chaining.
 */
fun <T> Array<out CompletionStage<T>>.toCompletableFuture(): Array<CompletableFuture<T>> =
    CompletableFutureUtils.toCompletableFutureArray(*this)
