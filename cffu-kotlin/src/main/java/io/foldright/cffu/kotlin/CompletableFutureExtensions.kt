package io.foldright.cffu.kotlin

import io.foldright.cffu.CffuState
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit


////////////////////////////////////////////////////////////////////////////////
//# Extension methods for CompletableFuture
//  input and output(return type) is CompletableFuture
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
//# allOf* methods
//
//    - allOfCompletableFuture
//    - allOfCompletableFutureVoid
//    - allOfFastFailCompletableFuture
//    - allOfFastFailCompletableFutureVoid
////////////////////////////////////////

/**
 * Returns a new CompletableFuture with the results of all the given CompletableFutures,
 * the returned new CompletableFuture is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCompletableFutureVoid],
 * but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CompletableFutureUtils.allOfWithResult], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see allOfCompletableFutureVoid
 */
fun <T> Collection<CompletableFuture<T>>.allOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfWithResult(*this.toTypedArray())

/**
 * Returns a new CompletableFuture with the results of all the given CompletableFutures,
 * the returned new CompletableFuture is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCompletableFutureVoid],
 * but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CompletableFutureUtils.allOfWithResult], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see allOfCompletableFutureVoid
 */
fun <T> Array<CompletableFuture<T>>.allOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfWithResult(*this)

/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given CompletableFutures are not reflected in the returned
 * CompletableFuture, but may be obtained by inspecting them individually.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value `null`.
 *
 * Among the applications of this method is to await completion of a set of independent CompletableFutures
 * before continuing a program, as in: `CompletableFuture.allOf(c1, c2, c3).join();`.
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see allOfCffuVoid
 * @see allOfCompletableFuture
 * @see CompletableFuture.allOf
 */
fun Collection<CompletableFuture<*>>.allOfCompletableFutureVoid(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given CompletableFutures are not reflected in the returned
 * CompletableFuture, but may be obtained by inspecting them individually.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value `null`.
 *
 * Among the applications of this method is to await completion of a set of independent CompletableFutures
 * before continuing a program, as in: `CompletableFuture.allOf(c1, c2, c3).join();`.
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see allOfCffuVoid
 * @see allOfCompletableFuture
 * @see CompletableFuture.allOf
 */
fun Array<CompletableFuture<*>>.allOfCompletableFutureVoid(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this)

/**
 * Returns a new CompletableFuture with the results of all the given CompletableFutures,
 * the new CompletableFuture success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfFastFailCompletableFutureVoid],
 * but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CompletableFutureUtils.allOfFastFailWithResult], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see allOfFastFailCompletableFutureVoid
 */
fun <T> Collection<CompletableFuture<T>>.allOfFastFailCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfFastFailWithResult(*this.toTypedArray())

/**
 * Returns a new CompletableFuture with the results of all the given CompletableFutures,
 * the new CompletableFuture success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfFastFailCompletableFutureVoid],
 * but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CompletableFutureUtils.allOfFastFailWithResult], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see allOfFastFailCompletableFutureVoid
 */
fun <T> Array<CompletableFuture<T>>.allOfFastFailCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfFastFailWithResult(*this)

/**
 * Returns a new CompletableFuture that is successful when all the given CompletableFutures success,
 * the results(`CompletableFuture<Void>`) of the given CompletableFutures are not reflected in the returned CompletableFuture,
 * but may be obtained by inspecting them individually.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value `null`.
 *
 * Same as [CompletableFutureUtils.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see allOfFastFailCffuVoid
 * @see allOfFastFailCompletableFuture
 * @see CompletableFutureUtils.allOfFastFail
 */
fun Collection<CompletableFuture<*>>.allOfFastFailCompletableFutureVoid(): CompletableFuture<Void> =
    CompletableFutureUtils.allOfFastFail(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is successful when all the given CompletableFutures success,
 * the results(`CompletableFuture<Void>`) of the given CompletableFutures are not reflected in the returned CompletableFuture,
 * but may be obtained by inspecting them individually.
 * If any of the given CompletableFutures complete exceptionally, then the returned CompletableFuture
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a CompletableFuture completed with the value `null`.
 *
 * Same as [CompletableFutureUtils.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see allOfFastFailCffuVoid
 * @see allOfFastFailCompletableFuture
 * @see CompletableFutureUtils.allOfFastFail
 */
fun Array<CompletableFuture<*>>.allOfFastFailCompletableFutureVoid(): CompletableFuture<Void> =
    CompletableFutureUtils.allOfFastFail(*this)


////////////////////////////////////////
//# anyOf* methods
//
//    - anyOfCompletableFuture
//    - anyOfCompletableFutureAny
//    - anyOfSuccessCompletableFuture
//    - anyOfSuccessCompletableFutureAny
////////////////////////////////////////

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
 *
 * Same as [anyOfCompletableFutureAny], but return result type is specified type instead of type `Any`.
 * Same as [CompletableFutureUtils.anyOfWithType], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see anyOfCompletableFutureAny
 */
fun <T> Collection<CompletableFuture<T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfWithType(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
 *
 * Same as [anyOfCompletableFutureAny], but return result type is specified type instead of type `Any`.
 * Same as [CompletableFutureUtils.anyOfWithType], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see anyOfCompletableFutureAny
 */
fun <T> Array<CompletableFuture<T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfWithType(*this)

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
 *
 * Same as [CompletableFuture.anyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see anyOfCffuAny
 * @see anyOfCompletableFuture
 * @see CompletableFuture.anyOf
 */
fun Collection<CompletableFuture<*>>.anyOfCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFuture.anyOf(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned CompletableFuture also does so,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns an incomplete CompletableFuture.
 *
 * Same as [CompletableFuture.anyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see anyOfCffuAny
 * @see anyOfCompletableFuture
 * @see CompletableFuture.anyOf
 */
fun Array<CompletableFuture<*>>.anyOfCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFuture.anyOf(*this)

/**
 * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new CompletableFuture that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [anyOfSuccessCompletableFutureAny], but return result type is specified type instead of type `Any`.
 * Same as [CompletableFutureUtils.anyOfSuccessWithType], providing this method is convenient for method chaining.
 *
 * @see anyOfCompletableFuture
 * @see CompletableFutureUtils.anyOfSuccessWithType
 */
fun <T> Collection<CompletableFuture<T>>.anyOfSuccessCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfSuccessWithType(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new CompletableFuture that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [anyOfSuccessCompletableFutureAny], but return result type is specified type instead of type `Any`.
 * Same as [CompletableFutureUtils.anyOfSuccessWithType], providing this method is convenient for method chaining.
 *
 * @see anyOfCompletableFuture
 * @see CompletableFutureUtils.anyOfSuccessWithType
 */
fun <T> Array<CompletableFuture<T>>.anyOfSuccessCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfSuccessWithType(*this)

/**
 * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new CompletableFuture that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CompletableFutureUtils.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see anyOfCompletableFuture
 * @see CompletableFutureUtils.anyOfSuccess
 */
fun Collection<CompletableFuture<*>>.anyOfSuccessCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFutureUtils.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception CompletableFuture any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CompletableFutureUtils.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see anyOfCompletableFuture
 * @see CompletableFutureUtils.anyOfSuccess
 */
fun Array<CompletableFuture<*>>.anyOfSuccessCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFutureUtils.anyOfSuccess(*this)

////////////////////////////////////////
//# combine methods
////////////////////////////////////////

/**
 * Returns a new CompletableFuture that is completed when the given two CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned
 * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
 *
 * @return a new CompletableFuture that is completed when the given 2 CompletableFutures complete
 * @throws NullPointerException if any input CompletableFutures are `null`
 * @see allOfCompletableFuture
 * @see CompletableFuture.allOf
 */
fun <T1, T2> CompletableFuture<T1>.combine(cf2: CompletableFuture<T2>): CompletableFuture<Tuple2<T1, T2>> =
    CompletableFutureUtils.combine(this, cf2)

/**
 * Returns a new CompletableFuture that is completed when the given three CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned
 * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
 *
 * @return a new CompletableFuture that is completed when the given 3 CompletableFutures complete
 * @throws NullPointerException if any input CompletableFutures are `null`
 * @see allOfCompletableFuture
 * @see CompletableFuture.allOf
 */
fun <T1, T2, T3> CompletableFuture<T1>.combine(
    cf2: CompletableFuture<T2>, cf3: CompletableFuture<T3>
): CompletableFuture<Tuple3<T1, T2, T3>> =
    CompletableFutureUtils.combine(this, cf2, cf3)

/**
 * Returns a new CompletableFuture that is completed when the given 4 CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned
 * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
 *
 * @return a new CompletableFuture that is completed when the given 4 CompletableFutures complete
 * @throws NullPointerException if any input CompletableFutures are `null`
 * @see allOfCompletableFuture
 * @see CompletableFuture.allOf
 */
fun <T1, T2, T3, T4> CompletableFuture<T1>.combine(
    cf2: CompletableFuture<T2>, cf3: CompletableFuture<T3>, cf4: CompletableFuture<T4>
): CompletableFuture<Tuple4<T1, T2, T3, T4>> =
    CompletableFutureUtils.combine(this, cf2, cf3, cf4)

/**
 * Returns a new CompletableFuture that is completed when the given 5 CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned
 * CompletableFuture also does so, with a CompletionException holding this exception as its cause.
 *
 * @return a new CompletableFuture that is completed when the given 5 CompletableFutures complete
 * @throws NullPointerException if any input CompletableFutures are `null`
 * @see allOfCompletableFuture
 * @see CompletableFuture.allOf
 */
fun <T1, T2, T3, T4, T5> CompletableFuture<T1>.combine(
    cf2: CompletableFuture<T2>, cf3: CompletableFuture<T3>,
    cf4: CompletableFuture<T4>, cf5: CompletableFuture<T5>
): CompletableFuture<Tuple5<T1, T2, T3, T4, T5>> =
    CompletableFutureUtils.combine(this, cf2, cf3, cf4, cf5)

////////////////////////////////////////////////////////////////////////////////
//# Backport CF instance methods
//  compatibility for low Java version
//
//  all methods name prefix with `cffu`
////////////////////////////////////////////////////////////////////////////////

//# Error Handling methods of CompletionStage

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
 * stage's exception as the argument to the supplied function, using given stage's
 * default asynchronous execution facility. Otherwise, if given stage completes normally,
 * then the returned stage also completes normally with the same value.
 *
 * @param fn the function to use to compute the value of the returned CompletionStage
 * if given CompletionStage completed exceptionally
 * @return the new CompletionStage
 */
fun <T> CompletableFuture<T>.cffuExceptionallyAsync(fn: (Throwable) -> T): CompletableFuture<T> =
    CompletableFutureUtils.exceptionallyAsync(this, fn)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally, is executed with given
 * stage's exception as the argument to the supplied function, using the supplied Executor. Otherwise,
 * if given stage completes normally, then the returned stage also completes normally with the same value.
 *
 * @param fn       the function to use to compute the value of the returned CompletionStage
 * if given CompletionStage completed exceptionally
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletionStage
 */
fun <T> CompletableFuture<T>.cffuExceptionallyAsync(fn: (Throwable) -> T, executor: Executor): CompletableFuture<T> =
    CompletableFutureUtils.exceptionallyAsync(this, fn, executor)

//# Timeout Control methods

/**
 * Exceptionally completes this CompletableFuture with a [TimeoutException][java.util.concurrent.TimeoutException]
 * if not otherwise completed before the given timeout.
 *
 * @param timeout how long to wait before completing exceptionally with a TimeoutException, in units of `unit`
 * @param unit    a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return this CompletableFuture
 */
fun <T> CompletableFuture<T>.cffuOrTimeout(timeout: Long, unit: TimeUnit): CompletableFuture<T> =
    CompletableFutureUtils.orTimeout(this, timeout, unit)

/**
 * Completes given CompletableFuture with the given value if not otherwise completed before the given timeout.
 *
 * @param value   the value to use upon timeout
 * @param timeout how long to wait before completing normally with the given value, in units of `unit`
 * @param unit    a `TimeUnit` determining how to interpret the `timeout` parameter
 * @return given CompletableFuture
 */
fun <T> CompletableFuture<T>.cffuCompleteOnTimeout(value: T, timeout: Long, unit: TimeUnit): CompletableFuture<T> =
    CompletableFutureUtils.completeOnTimeout(this, value, timeout, unit)

//# Advanced methods of CompletionStage

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally,
 * is composed using the results of the supplied function applied to given stage's exception.
 *
 * @param fn the function to use to compute the returned
 *           CompletionStage if given CompletionStage completed exceptionally
 * @return the new CompletionStage
 */
fun <T> CompletableFuture<T>.cffuExceptionallyCompose(fn: (Throwable) -> CompletionStage<T>): CompletableFuture<T> =
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
fun <T> CompletableFuture<T>.cffuExceptionallyComposeAsync(
    fn: (Throwable) -> CompletionStage<T>
): CompletableFuture<T> =
    CompletableFutureUtils.exceptionallyComposeAsync(this, fn)

/**
 * Returns a new CompletionStage that, when given stage completes exceptionally, is composed using
 * the results of the supplied function applied to given stage's exception, using the supplied Executor.
 *
 * @param fn       the function to use to compute the returned CompletionStage
 *                 if given CompletionStage completed exceptionally
 * @param executor the executor to use for asynchronous execution
 * @return the new CompletionStage
 */
fun <T> CompletableFuture<T>.cffuExceptionallyComposeAsync(
    fn: (Throwable) -> CompletionStage<T>, executor: Executor
): CompletableFuture<T> =
    CompletableFutureUtils.exceptionallyComposeAsync(this, fn, executor)

//# Read(explicitly) methods of CompletableFuture

/**
 * Waits if necessary for at most the given time for the computation to complete,
 * and then retrieves its result value when complete, or throws an (unchecked) exception if completed exceptionally.
 * <p>
 * <b><i>NOTE:<br></i></b>
 * call this method
 *
 * `result = CompletableFutureUtils.cffuJoin(cf, timeout, unit);`
 *
 * is same as:
 *
 * ```
 * result = cf.copy() // defensive copy to avoid writing this cf unexpectedly
 *     .orTimeout(timeout, unit)
 *     .join();
 * }
 * ```
 *
 * <b><i>CAUTION:<br></i></b>
 * if the wait timed out, this method throws an (unchecked) [CompletionException][java.util.concurrent.CompletionException]
 * with the [TimeoutException][java.util.concurrent.TimeoutException] as its cause;
 * NOT throws a (checked) [TimeoutException][java.util.concurrent.TimeoutException] like [CompletableFuture.get].
 *
 * @param timeout the maximum time to wait
 * @param unit    the time unit of the timeout argument
 * @return the result value
 * @see CompletableFuture.join
 */
@Suppress("UNCHECKED_CAST")
fun <T> CompletableFuture<T>.cffuJoin(timeout: Long, unit: TimeUnit): T =
    CompletableFutureUtils.cffuJoin(this, timeout, unit) as T

/**
 * Returns the computed result, without waiting.
 * <p>
 * This method is for cases where the caller knows that the task has already completed successfully,
 * for example when filtering a stream of Future objects for the successful tasks
 * and using a mapping operation to obtain a stream of results.
 *
 * ```
 * results = futures.stream()
 *     .filter(f -> f.state() == Future.State.SUCCESS)
 *     .map(Future::resultNow)
 *     .toList();
 * }
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <T> CompletableFuture<T>.cffuResultNow(): T =
    CompletableFutureUtils.resultNow(this) as T

/**
 * Returns the exception thrown by the task, without waiting.
 * <p>
 * This method is for cases where the caller knows that the task has already completed with an exception.
 *
 * @return the exception thrown by the task
 * @throws IllegalStateException if the task has not completed, the task completed normally,
 *                               or the task was cancelled
 * @see CompletableFuture#resultNow()
 */
fun <T> CompletableFuture<T>.cffuExceptionNow(): Throwable =
    CompletableFutureUtils.exceptionNow(this)

/**
 * Returns the computation state([CffuState]), this method has java version compatibility logic,
 * so you can invoke in old `java 18-`.
 *
 * @return the computation state
 * @see java.util.concurrent.Future.state
 * @see CompletableFuture.state
 */
fun <T> CompletableFuture<T>.cffuState(): CffuState =
    CompletableFutureUtils.cffuState(this)

//# Write methods of CompletableFuture

/**
 * Completes given CompletableFuture with the result of the given Supplier function invoked
 * from an asynchronous task using the default executor.
 *
 * @param supplier a function returning the value to be used to complete given CompletableFuture
 * @return given CompletableFuture
 */
fun <T> CompletableFuture<T>.cffuCompleteAsync(supplier: () -> T): CompletableFuture<T> =
    CompletableFutureUtils.completeAsync(this, supplier)

/**
 * Completes given CompletableFuture with the result of the given Supplier function invoked
 * from an asynchronous task using the given executor.
 *
 * @param supplier a function returning the value to be used to complete given CompletableFuture
 * @param executor the executor to use for asynchronous execution
 * @return given CompletableFuture
 */
fun <T> CompletableFuture<T>.cffuCompleteAsync(supplier: () -> T, executor: Executor): CompletableFuture<T> =
    CompletableFutureUtils.completeAsync(this, supplier, executor)

//# Re-Config methods

/**
 * Returns a new CompletionStage that is completed normally with the same value as given CompletableFuture
 * when it completes normally, and cannot be independently completed or otherwise used in ways
 * not defined by the methods of interface [CompletionStage].
 * If given CompletableFuture completes exceptionally, then the returned CompletionStage completes exceptionally
 * with a CompletionException with given exception as cause.
 *
 * ***CAUTION:*** if run on old Java 8, just return a ***normal*** CompletableFuture
 * which is NOT with a ***minimal*** CompletionStage.
 *
 * @return the new CompletionStage
 */
fun <T> CompletableFuture<T>.cffuMinimalCompletionStage(): CompletionStage<T> =
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
fun <T> CompletableFuture<T>.cffuCopy(): CompletableFuture<T> =
    CompletableFutureUtils.copy(this)

/**
 * Returns a new incomplete CompletableFuture of the type to be returned by a CompletionStage method.
 *
 * @param <T> the type of the value
 * @return a new CompletableFuture
 */
fun <T, U> CompletableFuture<T>.cffuNewIncompleteFuture(): CompletableFuture<U> =
    CompletableFutureUtils.newIncompleteFuture(this)
