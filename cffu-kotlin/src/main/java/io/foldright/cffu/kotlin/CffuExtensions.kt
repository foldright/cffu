package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CompletableFutureUtils
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.TimeUnit


////////////////////////////////////////////////////////////////////////////////
//# Extension methods for Cffu
//  output(return type) is Cffu
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////
// toCffu methods
////////////////////////////////////////

/**
 * Wrap an existed [CompletionStage] (including [CompletableFuture]) to [Cffu].
 *
 * This method is the same as [CffuFactory.toCffu], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCffu
 * @see CompletionStage.toCompletableFuture
 */
fun <T> CompletionStage<T>.toCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.toCffu(this)

/**
 * Wrap input [CompletionStage] (including [CompletableFuture]) collection elements to [Cffu] by [CffuFactory.toCffu].
 *
 * This method is the same as [CffuFactory.toCffuArray], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCffuArray
 * @see CffuFactory.toCffu
 */
fun <T> Collection<CompletionStage<T>>.toCffu(cffuFactory: CffuFactory): List<Cffu<T>> =
    map { it.toCffu(cffuFactory) }

/**
 * Wrap input [CompletionStage] (including [CompletableFuture]) array elements to [Cffu] by [CffuFactory.toCffu].
 *
 * This method is the same as [CffuFactory.toCffuArray], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.toCffuArray
 * @see CffuFactory.toCffu
 */
fun <T> Array<out CompletionStage<T>>.toCffu(cffuFactory: CffuFactory): Array<Cffu<T>> =
    cffuFactory.toCffuArray(*this)

////////////////////////////////////////
//# allOf*/mostResultsOfSuccess* methods for Array/Collection
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
 * Returns a new Cffu with the results in the **same order** of all the given stages,
 * the new Cffu is completed when all the given stages complete.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input stages.
 *
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
@JvmName("allResultsOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.allResultsOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the results in the **same order** of all the given stages,
 * the new Cffu is completed when all the given stages complete.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input stages.
 *
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
fun <T> Array<out CompletionStage<out T>>.allResultsOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
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
 * Returns a new Cffu that is completed when all the given stages complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given stages are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no stages are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */
@JvmName("allOfCffuCs")
fun Collection<CompletionStage<*>>.allOfCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when all the given stages complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given stages are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no stages are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */
fun Array<out CompletionStage<*>>.allOfCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this)

/**
 * Returns a new Cffu with the results in the **same order** of all the given Cffus,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
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
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
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
 * Returns a new Cffu with the results in the **same order** of all the given stages,
 * the new Cffu success when all the given stages success.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfFastFailCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allResultsOfFastFail
 */
@JvmName("allResultsOfFastFailCffuCs")
fun <T> Collection<CompletionStage<out T>>.allResultsOfFastFailCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOfFastFail(*this.toTypedArray())

/**
 * Returns a new Cffu with the results in the **same order** of all the given stages,
 * the new Cffu success when all the given stages success.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfFastFailCffu], except the returned Cffu contains the results of input Cffus.
 *
 * This method is the same as [CffuFactory.allResultsOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allOfFastFailCffu
 * @see CffuFactory.allResultsOfFastFail
 */
fun <T> Array<out CompletionStage<out T>>.allResultsOfFastFailCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
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
 * Returns a new Cffu that is successful when all the given stages success,
 * the results(`Cffu<Void>`) of the given stages are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allResultsOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
@JvmName("allOfFastFailCffuCs")
fun Collection<CompletionStage<*>>.allOfFastFailCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOfFastFail(*this.toTypedArray())

/**
 * Returns a new Cffu that is successful when all the given stages success,
 * the results(`Cffu<Void>`) of the given stages are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allOfFastFail], providing this method is convenient for method chaining.
 *
 * @see allResultsOfFastFailCffu
 * @see CffuFactory.allOfFastFail
 */
fun Array<out CompletionStage<*>>.allOfFastFailCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOfFastFail(*this)

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given Cffus in the given time(`timeout`), aka as many results as possible in the given time.
 *
 * If the given Cffu is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
 * (aka the result extraction logic is [Cffu.getSuccessNow]).
 *
 * The result extraction logic can be customized using another overloaded [mostResultsOfSuccessCffu] method.
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see Cffu.getSuccessNow
 */
// * @see CompletableFutureUtils.MGetSuccessNow
fun <T> Collection<Cffu<out T>>.mostResultsOfSuccessCffu(
    timeout: Long, unit: TimeUnit, valueIfNotSuccess: T, cffuFactory: CffuFactory = ABSENT
): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.mostResultsOfSuccess(timeout, unit, valueIfNotSuccess, *this.toTypedArray())
}

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given Cffus in the given time(`timeout`), aka as many results as possible in the given time.
 *
 * If the given Cffu is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
 * (aka the result extraction logic is [Cffu.getSuccessNow]).
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see Cffu.getSuccessNow
 */
// * @see CompletableFutureUtils.MGetSuccessNow
fun <T> Array<out Cffu<out T>>.mostResultsOfSuccessCffu(
    timeout: Long, unit: TimeUnit, valueIfNotSuccess: T, cffuFactory: CffuFactory = ABSENT
): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.mostResultsOfSuccess(timeout, unit, valueIfNotSuccess, *this)
}

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given stages in the given time(`timeout`), aka as many results as possible in the given time.
 *
 * If the given stage is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
 * (aka the result extraction logic is [Cffu.getSuccessNow]).
 *
 * The result extraction logic can be customized using another overloaded [mostResultsOfSuccessCffu] method.
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see Cffu.getSuccessNow
 */
// * @see CompletableFutureUtils.MGetSuccessNow
@JvmName("mostResultsOfSuccessCffuCs")
fun <T> Collection<CompletionStage<out T>>.mostResultsOfSuccessCffu(
    timeout: Long, unit: TimeUnit, valueIfNotSuccess: T, cffuFactory: CffuFactory
): Cffu<List<T>> =
    cffuFactory.mostResultsOfSuccess(timeout, unit, valueIfNotSuccess, *this.toTypedArray())

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given stages in the given time(`timeout`), aka as many results as possible in the given time.
 *
 * If the given stage is successful, its result is the completed value; Otherwise the given valueIfNotSuccess.
 * (aka the result extraction logic is [Cffu.getSuccessNow]).
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see Cffu.getSuccessNow
 */
// * @see CompletableFutureUtils.MGetSuccessNow
fun <T> Array<out CompletionStage<out T>>.mostResultsOfSuccessCffu(
    timeout: Long, unit: TimeUnit, valueIfNotSuccess: T, cffuFactory: CffuFactory
): Cffu<List<T>> =
    cffuFactory.mostResultsOfSuccess(timeout, unit, valueIfNotSuccess, *this)

////////////////////////////////////////
//# anyOf* methods for Array/Collection
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
 * Returns a new Cffu that is completed when any of the given stages complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
@JvmName("anyOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given stages complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
fun <T> Array<out CompletionStage<out T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
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
 * Returns a new Cffu that is successful when any of the given stages success,
 * with the same result. Otherwise, all the given stages complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause. If no stages are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOfSuccess
 */
@JvmName("anyOfSuccessCffuCs")
fun <T> Collection<CompletionStage<out T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOfSuccess(*this.toTypedArray())

/**
 * Returns a new Cffu that is successful when any of the given stages success,
 * with the same result. Otherwise, all the given stages complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause. If no stages are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anyOfSuccess], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anyOfSuccess
 */
fun <T> Array<out CompletionStage<out T>>.anyOfSuccessCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOfSuccess(*this)

////////////////////////////////////////
// cffuUnwrap methods for Array/Collection
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
