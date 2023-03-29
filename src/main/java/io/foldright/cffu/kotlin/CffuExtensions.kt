package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage


/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun Collection<CompletableFuture<*>>.allCompletableFuture(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun Array<CompletableFuture<*>>.allCompletableFuture(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this)

/**
 * Returns a new CompletableFuture that is completed
 * when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CompletableFuture.anyOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.anyOf
 * @see CffuFactory.anyOf
 */
fun Collection<CompletableFuture<*>>.anyCompletableFuture(): CompletableFuture<Any> =
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
fun Array<CompletableFuture<*>>.anyCompletableFuture(): CompletableFuture<Any> =
    CompletableFuture.anyOf(*this)

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

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
fun <T> Collection<Cffu<T>>.allCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [CffuFactory.cffuAllOf],  providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */

fun <T> Array<Cffu<T>>.allCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this)

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
@JvmName("cffuAllOfCf")
fun <T> Collection<CompletableFuture<T>>.allCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
fun <T> Array<CompletableFuture<T>>.allCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Collection<Cffu<T>>.anyCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<Cffu<T>>.anyCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
@JvmName("cffuAnyOfCf")
fun <T> Collection<CompletableFuture<T>>.anyCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<CompletableFuture<T>>.anyCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

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
