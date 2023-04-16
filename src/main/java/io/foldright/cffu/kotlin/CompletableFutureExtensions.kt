package io.foldright.cffu.kotlin

import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import java.util.concurrent.CompletableFuture


////////////////////////////////////////////////////////////////////////////////
//# Extension methods for CompletableFuture
//  input and output(return type) is CompletableFuture
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// allOf* methods
//
//   - allOfCompletableFuture
//   - allOfCompletableFutureVoid
//   - allOfFastFailCompletableFuture
//   - allOfFastFailCompletableFutureVoid
////////////////////////////////////////

/**
 * Returns a new CompletableFuture with the result of all the given CompletableFutures,
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
 * Returns a new CompletableFuture with the result of all the given CompletableFutures,
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
 * Returns a new CompletableFuture that success when all the given CompletableFutures success,
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
 * Returns a new CompletableFuture that success when all the given CompletableFutures success,
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
// anyOf* methods
//
//   - anyOfCompletableFuture
//   - anyOfCompletableFutureAny
//   - anyOfSuccessCompletableFuture
//   - anyOfSuccessCompletableFutureAny
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
 * Returns a new CompletableFuture that success when any of the given CompletableFutures success,
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
 * Returns a new CompletableFuture that success when any of the given CompletableFutures success,
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
 * Returns a new CompletableFuture that success when any of the given CompletableFutures success,
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
 * Returns a new CompletableFuture that success when any of the given CompletableFutures success,
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
// combine methods
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
