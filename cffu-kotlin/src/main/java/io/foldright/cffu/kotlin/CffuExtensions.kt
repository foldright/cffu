package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CffuFactoryBuilder.newCffuFactoryBuilder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


////////////////////////////////////////////////////////////////////////////////
//# Extension methods for Cffu
//  output(return type) is Cffu
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// asCffu methods
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
fun <T> Array<out CompletionStage<T>>.asCffu(cffuFactory: CffuFactory): Array<Cffu<T>> =
    cffuFactory.asCffuArray(*this)

////////////////////////////////////////
// allOf* methods
//
//   - allOfCffu
//   - allOfCffuVoid
//   - allOfFastFailCffu
//   - allOfFastFailCffuVoid
////////////////////////////////////////

/**
 * Placeholder for optional [CffuFactory] argument.
 */
private val ABSENT: CffuFactory = newCffuFactoryBuilder { }.build()
private const val ERROR_MSG_FOR_COLL = "no cffuFactory argument provided when this collection is empty"
private const val ERROR_MSG_FOR_ARRAY = "no cffuFactory argument provided when this array is empty"

/**
 * Returns a new Cffu with the results of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfCffuVoid
 * @see CffuFactory.cffuAllOf
 */
fun <T> Collection<Cffu<T>>.allOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.cffuAllOf(*this.toTypedArray())
}

/**
 * Returns a new Cffu with the results of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfCffuVoid
 * @see CffuFactory.cffuAllOf
 */
fun <T> Array<Cffu<T>>.allOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.cffuAllOf(*this)
}

/**
 * Returns a new Cffu with the results of all the given CompletableFutures,
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
 * Returns a new Cffu with the results of all the given CompletableFutures,
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
 * Returns a new Cffu that is completed when all the given Cffus complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given Cffus are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */
fun Collection<Cffu<*>>.allOfCffuVoid(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
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
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */

fun Array<out Cffu<*>>.allOfCffuVoid(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
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
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */
@JvmName("allOfCffuVoidCf")
fun Collection<CompletableFuture<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when all the given CompletableFutures complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given CompletableFutures are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no CompletableFutures are provided, returns a Cffu completed with the value `null`.
 *
 * Same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allOf
 */
fun Array<out CompletableFuture<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this)

/**
 * Returns a new Cffu with the results of all the given Cffus,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfFastFailCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOfFastFail], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfFastFailCffuVoid
 * @see CffuFactory.cffuAllOfFastFail
 */
fun <T> Collection<Cffu<T>>.allOfFastFailCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.cffuAllOfFastFail(*this.toTypedArray())
}

/**
 * Returns a new Cffu with the results of all the given Cffus,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfFastFailCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOfFastFail], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfFastFailCffuVoid
 * @see CffuFactory.cffuAllOfFastFail
 */
fun <T> Array<Cffu<T>>.allOfFastFailCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.cffuAllOfFastFail(*this)
}

/**
 * Returns a new Cffu with the results of all the given CompletableFutures,
 * the new Cffu success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfFastFailCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffuVoid
 * @see CffuFactory.cffuAllOfFastFail
 */
@JvmName("allOfFastFailCffuCf")
fun <T> Collection<CompletableFuture<T>>.allOfFastFailCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOfFastFail(*this.toTypedArray())

/**
 * Returns a new Cffu with the results of all the given CompletableFutures,
 * the new Cffu success when all the given CompletableFutures success.
 * If any of the given CompletableFutures complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given CompletableFutures,
 * with a CompletionException holding this exception as its cause.
 * If no CompletableFutures are provided, returns a Cffu completed
 * with the value [emptyList][java.util.Collections.emptyList].
 *
 * Same as [allOfFastFailCffuVoid], but the returned Cffu contains the results of input Cffus.
 * Same as [CffuFactory.cffuAllOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffuVoid
 * @see CffuFactory.cffuAllOfFastFail
 */
fun <T> Array<out CompletableFuture<T>>.allOfFastFailCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOfFastFail(*this)

/**
 * Returns a new Cffu that is successful when all the given Cffus success,
 * the results(`Cffu<Void>`) of the given Cffus are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * Same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Collection<Cffu<*>>.allOfFastFailCffuVoid(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
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
 * Same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Array<out Cffu<*>>.allOfFastFailCffuVoid(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
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
 * Same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
@JvmName("allOfFastFailCffuVoidCf")
fun Collection<CompletableFuture<*>>.allOfFastFailCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
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
 * Same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Array<out CompletableFuture<*>>.allOfFastFailCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOfFastFail(*this)

////////////////////////////////////////
// anyOf* methods
//
//   - anyOfCffu
//   - anyOfCffuAny
//   - anyOfSuccessCffu
//   - anyOfSuccessCffuAny
////////////////////////////////////////

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of type `Any`.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffuAny
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Collection<Cffu<T>>.anyOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.cffuAnyOf(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of type `Any`.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffuAny
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<Cffu<T>>.anyOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.cffuAnyOf(*this)
}

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
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOf
 */
fun Collection<Cffu<*>>.anyOfCffuAny(cffuFactory: CffuFactory = ABSENT): Cffu<Any> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.anyOf(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOf
 */
fun Array<out Cffu<*>>.anyOfCffuAny(cffuFactory: CffuFactory = ABSENT): Cffu<Any> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.anyOf(*this)
}

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
fun Array<out CompletableFuture<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this)

/**
 * Returns a new Cffu that is successful when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
fun <T> Collection<Cffu<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.cffuAnyOfSuccess(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is successful when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
fun <T> Array<Cffu<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.cffuAnyOfSuccess(*this)
}

/**
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
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
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CffuFactory.cffuAnyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.cffuAnyOfSuccess
 */
fun <T> Array<CompletableFuture<T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOfSuccess(*this)

/**
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
fun Collection<Cffu<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory = ABSENT): Cffu<Any> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.anyOfSuccess(*this.toTypedArray())
}

/**
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
fun Array<out Cffu<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory = ABSENT): Cffu<Any> {
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
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
@JvmName("anyOfSuccessCffuAnyCf")
fun Collection<CompletableFuture<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that is successful when any of the given CompletableFutures success,
 * with the same result. Otherwise, all the given CompletableFutures complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given CompletableFutures as its cause. If no CompletableFutures are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * Same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfSuccessCffu
 * @see CffuFactory.anyOfSuccess
 */
fun Array<out CompletableFuture<*>>.anyOfSuccessCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOfSuccess(*this)

////////////////////////////////////////
// toCompletableFuture methods
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
fun <T> Array<out CompletionStage<T>>.toCompletableFuture(): Array<CompletableFuture<T>> =
    CffuFactory.toCompletableFutureArray(*this)

////////////////////////////////////////
// cffuUnwrap methods
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
