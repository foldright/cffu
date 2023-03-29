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
fun Collection<CompletableFuture<*>>.allOf(): CompletableFuture<Void> =
    CompletableFuture.allOf(*this.toTypedArray())

/**
 * Returns a new CompletableFuture that is completed when all the given CompletableFutures complete.
 *
 * Same as [CompletableFuture.allOf], providing this method is convenient for method chaining.
 *
 * @see CompletableFuture.allOf
 * @see CffuFactory.allOf
 */
fun Array<CompletableFuture<*>>.allOf(): CompletableFuture<Void> =
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
fun Collection<CompletableFuture<*>>.anyOf(): CompletableFuture<Any> =
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
fun Array<CompletableFuture<*>>.anyOf(): CompletableFuture<Any> =
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
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
fun <T> Collection<Cffu<T>>.cffuAllOf(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given Cffus,
 * the new Cffu is completed when all the given Cffus complete.
 *
 * Same as [CffuFactory.cffuAllOf],  providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */

fun <T> Array<Cffu<T>>.cffuAllOf(cffuFactory: CffuFactory): Cffu<List<T>> =
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
fun <T> Collection<CompletableFuture<T>>.cffuAllOf(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this.toTypedArray())

/**
 * Returns a new Cffu with the result of all the given CompletableFutures,
 * the new Cffu is completed when all the given CompletableFutures complete.
 *
 * Same as [CffuFactory.cffuAllOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAllOf
 */
fun <T> Array<CompletableFuture<T>>.cffuAllOf(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.cffuAllOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Collection<Cffu<T>>.cffuAnyOf(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<Cffu<T>>.cffuAnyOf(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
@JvmName("cffuAnyOfCf")
fun <T> Collection<CompletableFuture<T>>.cffuAnyOf(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this.toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given CompletableFutures complete, with the same result.
 *
 * Same as [CffuFactory.cffuAnyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.cffuAnyOf
 */
fun <T> Array<CompletableFuture<T>>.cffuAnyOf(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.cffuAnyOf(*this)
