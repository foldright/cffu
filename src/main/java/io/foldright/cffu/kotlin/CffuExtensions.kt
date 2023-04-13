package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CompletableFutureUtils
import io.foldright.cffu.tuple.Tuple2
import io.foldright.cffu.tuple.Tuple3
import io.foldright.cffu.tuple.Tuple4
import io.foldright.cffu.tuple.Tuple5
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


////////////////////////////////////////////////////////////////////////////////
//# Extension methods for CompletableFuture
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// - allOfCompletableFuture
// - allOfCompletableFutureVoid
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
 *
 * @see allOfCffu
 * @see allOfCompletableFutureVoid
 */
fun <T> Array<CompletableFuture<T>>.allOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfWithResult(*this)

/**
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

////////////////////////////////////////
// - anyOfCompletableFuture
// - anyOfCompletableFutureAny
////////////////////////////////////////

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCompletableFutureAny], but return result type is specified type instead of type `Any`.
 *
 * @see anyOfCffu
 * @see anyOfCompletableFutureAny
 */
fun <T> Collection<CompletableFuture<T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfWithType(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCompletableFutureAny], but return result type is specified type instead of type `Any`.
 *
 * @see anyOfCffu
 * @see anyOfCompletableFutureAny
 */
fun <T> Array<CompletableFuture<T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfWithType(*this)

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
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
 * returns a new CompletableFuture that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
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
 * returns a new CompletableFuture that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CompletableFutureUtils.anyOfSuccess], providing this method is convenient for method chaining.
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
 * returns a new CompletableFuture that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CompletableFutureUtils.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see anyOfCompletableFuture
 * @see CompletableFutureUtils.anyOfSuccessWithType
 */
fun Collection<CompletableFuture<*>>.anyOfSuccessCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFutureUtils.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned CompletableFuture also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new CompletableFuture that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CompletableFutureUtils.anyOfSuccessWithType], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see anyOfCompletableFuture
 * @see CompletableFutureUtils.anyOfSuccessWithType
 */
fun Array<CompletableFuture<*>>.anyOfSuccessCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFutureUtils.anyOfSuccess(*this)

////////////////////////////////////////
// combine
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
//# Extension methods for Cffu
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// asCffu
////////////////////////////////////////

/**
 * Wrap an existed [CompletableFuture]/[CompletionStage] to [Cffu].
 *
 * Same as [CffuFactory.asCffu], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.asCffu
 */
fun <T> CompletionStage<T>.asCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.asCffu(this)

/**
 * Wrap input [CompletableFuture]/[CompletionStage] collection elements to [Cffu] by [CffuFactory.asCffu].
 *
 * Same as [CffuFactory.asCffuArray], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.asCffu
 */
fun <T> Collection<CompletionStage<T>>.asCffu(cffuFactory: CffuFactory): List<Cffu<T>> =
    map { it.asCffu(cffuFactory) }

/**
 * Wrap input [CompletableFuture]/[CompletionStage] array elements to [Cffu] by [CffuFactory.asCffu].
 *
 * Same as [CffuFactory.asCffuArray], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.asCffuArray
 * @see CffuFactory.asCffu
 */
fun <T, CS : CompletionStage<T>> Array<CS>.asCffu(cffuFactory: CffuFactory): Array<Cffu<T>> =
    cffuFactory.asCffuArray(*this)

////////////////////////////////////////
// - allOfCffu
// - allOfCffuVoid
////////////////////////////////////////

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffuVoid
 * @see CffuFactory.cffuAllOf
 */
fun <T> Collection<Cffu<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffuVoid
 * @see CffuFactory.cffuAllOf
 */

fun <T> Array<Cffu<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this)

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCffuVoid], but the returned Cffu contains the results of input CompletableFutures.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffuVoid
 * @see CffuFactory.cffuAllOf
 */
@JvmName("allOfCffuCf")
fun <T> Collection<CompletableFuture<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCffuVoid], but the returned Cffu contains the results of input CompletableFutures.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffuVoid
 * @see CffuFactory.cffuAllOf
 */
fun <T> Array<CompletableFuture<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this)

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */
fun Collection<Cffu<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */

fun Array<Cffu<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this)

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */
@JvmName("allOfCffuVoidCf")
fun Collection<CompletableFuture<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */
fun Array<CompletableFuture<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this)

////////////////////////////////////////
// - anyOfCffu
// - anyOfCffuAny
////////////////////////////////////////

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of type `Any`.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffuAny
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Collection<Cffu<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of type `Any`.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffuAny
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<Cffu<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of type `Any`.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffuAny
 * @see CffuFactory.cffuAnyOf
 */
@JvmName("anyOfCffuCf")
fun <T> Collection<CompletableFuture<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of type `Any`.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffuAny
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<CompletableFuture<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOf
 */
fun Collection<Cffu<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOf
 */
fun Array<Cffu<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOf
 */
@JvmName("anyOfCffuAnyCf")
fun Collection<CompletableFuture<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOf
 */
fun Array<CompletableFuture<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this)

/**
 * Returns a new Cffu that success when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
fun <T> Collection<Cffu<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that success when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
fun <T> Array<Cffu<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOfSuccess(*this)

/**
 * Returns a new Cffu that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
@JvmName("anyOfSuccessCffuCf")
fun <T> Collection<CompletableFuture<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
fun <T> Array<CompletableFuture<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOfSuccess(*this)

/**
 * Returns a new Cffu that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
fun Collection<Cffu<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
fun Array<Cffu<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOfSuccess(*this)

/**
 * Returns a new Cffu that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
@JvmName("anyOfSuccessCffuAnyCf")
fun Collection<CompletableFuture<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that success when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a {@link NoCfsProvidedException} exception as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
fun Array<CompletableFuture<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOfSuccess(*this)

////////////////////////////////////////
// toCompletableFuture
////////////////////////////////////////

/**
 * Convert [Cffu] collection elements to [CompletableFuture] by [Cffu.toCompletableFuture].
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCompletableFutureArray
 */
fun <T> Collection<CompletionStage<T>>.toCompletableFuture(): List<CompletableFuture<T>> =
    map { it.toCompletableFuture() }

/**
 * Convert [Cffu] array elements to [CompletableFuture] by [Cffu.toCompletableFuture].
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCompletableFutureArray
 */
fun <T, CS : CompletionStage<T>> Array<CS>.toCompletableFuture(): Array<CompletableFuture<T>> =
    CffuFactory.toCompletableFutureArray(*this)

////////////////////////////////////////
// cffuUnwrap
////////////////////////////////////////

/**
 * Unwrap input [Cffu] collection elements by [Cffu.cffuUnwrap].
 *
 * Same as [CffuFactory.cffuArrayUnwrap], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuArrayUnwrap
 */
fun <T> Collection<Cffu<T>>.cffuUnwrap(): List<CompletableFuture<T>> =
    map { it.cffuUnwrap() }

/**
 * Unwrap input [Cffu] array elements by [Cffu.cffuUnwrap].
 *
 * Same as [CffuFactory.cffuArrayUnwrap], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuArrayUnwrap
 */
fun <T> Array<Cffu<T>>.cffuUnwrap(): Array<CompletableFuture<T>> =
    CffuFactory.cffuArrayUnwrap(*this)
