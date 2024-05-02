package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CompletableFutureUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


////////////////////////////////////////////////////////////////////////////////
//# Extension methods for Cffu
//  output(return type) is Cffu
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// toCffu methods
////////////////////////////////////////

/**
 * Wrap an existed [CompletableFuture]/[CompletionStage] to [Cffu].
 *
 * This method is the same as [CffuFactory.toCffu], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCffu
 * @see CompletionStage.toCompletableFuture
 */
fun <T> CompletionStage<T>.toCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.toCffu(this)

/**
 * Wrap input [CompletableFuture]/[CompletionStage] collection elements to [Cffu] by [CffuFactory.toCffu].
 *
 * This method is the same as [CffuFactory.toCffuArray], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCffuArray
 * @see CffuFactory.toCffu
 */
fun <T> Collection<CompletionStage<T>>.toCffu(cffuFactory: CffuFactory): List<Cffu<T>> =
    map { it.toCffu(cffuFactory) }

/**
 * Wrap input [CompletableFuture]/[CompletionStage] array elements to [Cffu] by [CffuFactory.toCffu].
 *
 * This method is the same as [CffuFactory.toCffuArray], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCffuArray
 * @see CffuFactory.toCffu
 */
fun <T> Array<out CompletionStage<T>>.toCffu(cffuFactory: CffuFactory): Array<Cffu<T>> =
    cffuFactory.toCffuArray(*this)

////////////////////////////////////////
// allOf* methods
//
//   - allResultsOfCffu
//   - allOfCffu
//   - allResultsOfFastFailCffu
//   - allOfFastFailCffu
////////////////////////////////////////

/**
 * Placeholder for optional [CffuFactory] argument.
 */
private val ABSENT: CffuFactory = CffuFactory.builder { }.build()
private const val ERROR_MSG_FOR_COLL = "no cffuFactory argument provided when this collection is empty"
private const val ERROR_MSG_FOR_ARRAY = "no cffuFactory argument provided when this array is empty"

/**
 * Returns a new Cffu with the results in the **same order** of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
fun <T> Collection<Cffu<out T>>.allResultsOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allResultsOf(*this.toTypedArray())
}

/**
 * Returns a new Cffu with the results in the **same order** of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
fun <T> Array<out Cffu<out T>>.allResultsOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.allResultsOf(*this)
}

/**
 * Returns a new Cffu with the results in the **same order** of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input CompletableFutures.
 *
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
@JvmName("allResultsOfCffuCf")
fun <T> Collection<CompletableFuture<out T>>.allResultsOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the results in the **same order** of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input CompletableFutures.
 *
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
fun <T> Array<out CompletableFuture<out T>>.allResultsOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOf(*this)

/**
 * Returns a new Cffu that is completed when all the given Cffus complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given Cffus are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */
fun Collection<Cffu<*>>.allOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allOf(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is completed when all the given Cffus complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given Cffus are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */

fun Array<out Cffu<*>>.allOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.allOf(*this)
}

/**
 * Returns a new Cffu that is completed when all the given CompletableFutures complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given CompletableFutures are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no CompletableFutures are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */
@JvmName("allOfCffuCf")
fun Collection<CompletableFuture<*>>.allOfCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when all the given CompletableFutures complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given CompletableFutures are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no CompletableFutures are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */
fun Array<out CompletableFuture<*>>.allOfCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this)

/**
 * Returns a new Cffu with the results in the **same order** of all the given Cffus,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfFastFailCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOfFastFail], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allResultsOfFastFail
 */
fun <T> Collection<Cffu<out T>>.allResultsOfFastFailCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allResultsOfFastFail(*this.toTypedArray())
}

/**
 * Returns a new Cffu with the results in the **same order** of all the given Cffus,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfFastFailCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOfFastFail], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allResultsOfFastFail
 */
fun <T> Array<out Cffu<out T>>.allResultsOfFastFailCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.allResultsOfFastFail(*this)
}

/**
 * Returns a new Cffu with the results in the **same order** of all the given CompletableFutures,
 * the new Cffu success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfFastFailCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allResultsOfFastFail
 */
@JvmName("allResultsOfFastFailCffuCf")
fun <T> Collection<CompletableFuture<out T>>.allResultsOfFastFailCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOfFastFail(*this.toTypedArray())

/**
 * Returns a new Cffu with the results in the **same order** of all the given CompletableFutures,
 * the new Cffu success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfFastFailCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allResultsOfFastFail
 */
fun <T> Array<out CompletableFuture<out T>>.allResultsOfFastFailCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOfFastFail(*this)

/**
 * Returns a new Cffu that is successful when all the given Cffus success,
 * the results(`Cffu<Void>`) of the given Cffus are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allResultsOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Collection<Cffu<*>>.allOfFastFailCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allOfFastFail(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is successful when all the given Cffus success,
 * the results(`Cffu<Void>`) of the given Cffus are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allResultsOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Array<out Cffu<*>>.allOfFastFailCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.allOfFastFail(*this)
}

/**
 * Returns a new Cffu that is successful when all the given CompletableFutures success,
 * the results(`Cffu<Void>`) of the given CompletableFutures are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allResultsOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
@JvmName("allOfFastFailCffuCf")
fun Collection<CompletableFuture<*>>.allOfFastFailCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOfFastFail(*this.toTypedArray())

/**
 * Returns a new Cffu that is successful when all the given CompletableFutures success,
 * the results(`Cffu<Void>`) of the given CompletableFutures are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allResultsOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Array<out CompletableFuture<*>>.allOfFastFailCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOfFastFail(*this)

////////////////////////////////////////
// anyOf* methods
//
//   - anyOfCffu
//   - anyOfSuccessCffu
////////////////////////////////////////

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see CffuFactory.anyOf
 */
fun <T> Collection<Cffu<out T>>.anyOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.anyOf(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see CffuFactory.anyOf
 */
fun <T> Array<out Cffu<out T>>.anyOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.anyOf(*this)
}

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
@JvmName("anyOfCffuCf")
fun <T> Collection<CompletableFuture<out T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
fun <T> Array<out CompletableFuture<out T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOf(*this)

/**
 * Returns a new Cffu that is successful when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOfSuccess
 */
fun <T> Collection<Cffu<out T>>.anyOfSuccessCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.anyOfSuccess(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is successful when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOfSuccess
 */
fun <T> Array<out Cffu<out T>>.anyOfSuccessCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.anyOfSuccess(*this)
}

/**
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOfSuccess
 */
@JvmName("anyOfSuccessCffuCf")
fun <T> Collection<CompletableFuture<out T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOfSuccess
 */
fun <T> Array<out CompletableFuture<out T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOfSuccess(*this)

////////////////////////////////////////
// cffuUnwrap methods
////////////////////////////////////////

/**
 * Unwrap input [Cffu] collection elements by [Cffu.cffuUnwrap].
 *
 * This method is the same as [CffuFactory.cffuArrayUnwrap], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuArrayUnwrap
 */
fun <T> Collection<Cffu<T>>.cffuUnwrap(): List<CompletableFuture<T>> =
    map { it.cffuUnwrap() }

/**
 * Unwrap input [Cffu] array elements by [Cffu.cffuUnwrap].
 *
 * This method is the same as [CffuFactory.cffuArrayUnwrap], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuArrayUnwrap
 */
fun <T> Array<out Cffu<T>>.cffuUnwrap(): Array<CompletableFuture<T>> =
    CffuFactory.cffuArrayUnwrap(*this)
