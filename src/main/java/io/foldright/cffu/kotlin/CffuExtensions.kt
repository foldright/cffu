package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CompletableFutureUtils
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
 * the new CompletableFuture is completed when all the given CompletableFutures complete.
 *
 * Same as [allOfCompletableFutureVoid],
 * but the returned CompletableFuture contains the results of input CompletableFutures.
 *
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun <T> Collection<CompletableFuture<T>>.allOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfWithResult(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [allOfCompletableFutureVoid],
 * but the returned CompletableFuture contains the results of input CompletableFutures.
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun <T> Array<CompletableFuture<T>>.allOfCompletableFuture(): CompletableFuture<List<T>> =
    CompletableFutureUtils.allOfWithResult(*this)

/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun Collection<CompletableFuture<*>>.allOfCompletableFutureVoid(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun Array<CompletableFuture<*>>.allOfCompletableFutureVoid(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this)

////////////////////////////////////////
// - anyOfCompletableFuture
// - anyOfCompletableFutureAny
////////////////////////////////////////

/**
 * Returns a new CompletableFuture that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCompletableFutureAny], but return result type is specified type instead of {@code Object}.
 *
 * @see [anyOfCffu]
 */
fun <T> Collection<CompletableFuture<T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfWithType(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCompletableFutureAny], but return result type is specified type instead of {@code Object}.
 *
 * @see [anyOfCffu]
 */
fun <T> Array<CompletableFuture<T>>.anyOfCompletableFuture(): CompletableFuture<T> =
    CompletableFutureUtils.anyOfWithType(*this)

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CompletableFuture.anyOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.anyOf
 * @see CffuFactory.anyOf
 */
fun Collection<CompletableFuture<*>>.anyOfCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFuture.anyOf(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CompletableFuture.anyOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.anyOf
 * @see CffuFactory.anyOf
 */
fun Array<CompletableFuture<*>>.anyOfCompletableFutureAny(): CompletableFuture<Any> =
    CompletableFuture.anyOf(*this)


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
 * Wrap input [CompletableFuture]/[CompletionStage] collection element to [Cffu] by [CffuFactory.asCffu].
 *
 * Same as [CffuFactory.asCffu], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.asCffu
 */
fun <T> Collection<CompletionStage<T>>.asCffu(cffuFactory: CffuFactory): List<Cffu<T>> =
    map { it.asCffu(cffuFactory) }

/**
 * Wrap input [CompletableFuture]/[CompletionStage] array element to [Cffu] by [CffuFactory.asCffu].
 *
 * Same as [CffuFactory.asCffu], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.asCffu
 * @see CffuFactory.asCffuArray
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
 *
 * Same as [allOfCffuVoid], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
fun <T> Collection<Cffu<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [allOfCffuVoid], but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */

fun <T> Array<Cffu<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this)

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [allOfCffuVoid], but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
@JvmName("allOfCffuCf")
fun <T> Collection<CompletableFuture<T>>.allOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [allOfCffuVoid], but the returned CompletableFuture contains the results of input CompletableFutures.
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
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
 * @see CffuFactory.allOf
 */
fun Array<CompletableFuture<*>>.allOfCffuVoid(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allOf(*this)

////////////////////////////////////////
// - allOfCffu
// - allOfCffuAny
////////////////////////////////////////

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of {@code Object}.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Collection<Cffu<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of {@code Object}.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<Cffu<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of {@code Object}.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
@JvmName("anyOfCffuCf")
fun <T> Collection<CompletableFuture<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [anyOfCffuAny], but return result type is specified type instead of {@code Object}.
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<CompletableFuture<T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
fun Collection<Cffu<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
fun Array<Cffu<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
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
 * @see CffuFactory.anyOf
 */
fun Array<CompletableFuture<*>>.anyOfCffuAny(cffuFactory: CffuFactory): Cffu<Any> =
    cffuFactory.anyOf(*this)

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
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuArrayUnwrap
 */
fun <T> Collection<Cffu<T>>.cffuUnwrap(): List<CompletableFuture<T>> =
    map { it.cffuUnwrap() }

/**
 * Unwrap input [Cffu] array elements by [Cffu.cffuUnwrap].
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuArrayUnwrap
 */
fun <T> Array<Cffu<T>>.cffuUnwrap(): Array<CompletableFuture<T>> =
    CffuFactory.cffuArrayUnwrap(*this)
