package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CffuFactoryBuilder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CompletionStage
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit
import java.util.function.Supplier


/*
  This file contains the Extension methods for Cffu
  output(return type) is Cffu
*/

////////////////////////////////////////////////////////////////////////////////
// region# toCffu Conversion Methods
////////////////////////////////////////////////////////////////////////////////

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

// endregion
////////////////////////////////////////////////////////////
// region# Multi-Actions(M*) Methods(create by actions)
//
//    - Supplier<T>[] -> Cffu<List<T>>
//    - Runnable[]    -> Cffu<Void>
////////////////////////////////////////////////////////////

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the values obtained by calling the given Suppliers in the **same order** of the given Suppliers arguments.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see allResultsFastFailOfCffu
 */
fun <T> Collection<Supplier<out T>>.mSupplyFastFailAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyFastFailAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the values obtained by calling the given Suppliers in the **same order** of the given Suppliers arguments.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see allResultsFastFailOfCffu
 */
fun <T> Array<out Supplier<out T>>.mSupplyFastFailAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyFastFailAsync(executor, *this)

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the successful values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * If any of the provided suppliers fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the supplier having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not failed
 * @param <T>           the suppliers' return type
 * @return the new Cffu
 * @see allSuccessResultsOfCffu
 */
fun <T> Collection<Supplier<out T>>.mSupplyAllSuccessAsyncCffu(
    cffuFactory: CffuFactory, valueIfFailed: T, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyAllSuccessAsync(valueIfFailed, executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the successful values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * If any of the provided suppliers fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the supplier having a successful value of `valueIfFailed`).
 *
 * @param valueIfFailed the value to return if not failed
 * @param <T>           the suppliers' return type
 * @return the new Cffu
 * @see allSuccessResultsOfCffu
 */
fun <T> Array<out Supplier<out T>>.mSupplyAllSuccessAsyncCffu(
    cffuFactory: CffuFactory, valueIfFailed: T, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyAllSuccessAsync(valueIfFailed, executor, *this)

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the most values obtained by calling the given Suppliers
 * in the given time(`timeout`, aka as many results as possible in the given time)
 * in the **same order** of the given Suppliers arguments.
 *
 *
 * If any of the provided suppliers does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`
 * (which is indistinguishable from the supplier having a successful value of `valueIfNotSuccess`).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @param timeout           how long to wait in units of `unit`
 * @param unit              a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param <T>               the suppliers' return type
 * @return the new Cffu
 * @see mostSuccessResultsOfCffu
 */
fun <T> Collection<Supplier<out T>>.mSupplyMostSuccessAsyncCffu(
    cffuFactory: CffuFactory, valueIfNotSuccess: T,
    timeout: Long, unit: TimeUnit, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyMostSuccessAsync(valueIfNotSuccess, executor, timeout, unit, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the most values obtained by calling the given Suppliers
 * in the given time(`timeout`, aka as many results as possible in the given time)
 * in the **same order** of the given Suppliers arguments.
 *
 *
 * If any of the provided suppliers does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`
 * (which is indistinguishable from the supplier having a successful value of `valueIfNotSuccess`).
 *
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @param timeout           how long to wait in units of `unit`
 * @param unit              a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param <T>               the suppliers' return type
 * @return the new Cffu
 * @see mostSuccessResultsOfCffu
 */
fun <T> Array<out Supplier<out T>>.mSupplyMostSuccessAsyncCffu(
    cffuFactory: CffuFactory, valueIfNotSuccess: T,
    timeout: Long, unit: TimeUnit, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyMostSuccessAsync(valueIfNotSuccess, executor, timeout, unit, *this)

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see allResultsOfCffu
 */
fun <T> Collection<Supplier<out T>>.mSupplyAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * with the values obtained by calling the given Suppliers
 * in the **same order** of the given Suppliers arguments.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see allResultsOfCffu
 */
fun <T> Array<out Supplier<out T>>.mSupplyAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<List<T>> = cffuFactory.mSupplyAsync(executor, *this)

/**
 * Returns a new Cffu that is asynchronously successful
 * when any of tasks running in the Cffu's default asynchronous execution facility
 * by calling the given Suppliers success, with the same result.
 * Otherwise, all the given tasks complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause.
 * If no suppliers are provided, returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see anySuccessOfCffu
 */
fun <T> Collection<Supplier<out T>>.mSupplyAnySuccessAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<T> = cffuFactory.mSupplyAnySuccessAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously successful
 * when any of tasks running in the Cffu's default asynchronous execution facility
 * by calling the given Suppliers success, with the same result.
 * Otherwise, all the given tasks complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause.
 * If no suppliers are provided, returns a new Cffu that is already completed exceptionally
 * with a CompletionException holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see anySuccessOfCffu
 */
fun <T> Array<out Supplier<out T>>.mSupplyAnySuccessAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<T> = cffuFactory.mSupplyAnySuccessAsync(executor, *this)

/**
 * Returns a new Cffu that is completed
 * when any of tasks running in the Cffu's default asynchronous execution facility
 * by calling the given Suppliers complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned Cffu also does so,
 * with a CompletionException holding this exception as its cause.
 * If no suppliers are provided, returns an incomplete Cffu.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see anyOfCffu
 */
fun <T> Collection<Supplier<out T>>.mSupplyAnyAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<T> = cffuFactory.mSupplyAnyAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is completed
 * when any of tasks running in the Cffu's default asynchronous execution facility
 * by calling the given Suppliers complete, with the same result.
 * Otherwise, if it completed exceptionally, the returned Cffu also does so,
 * with a CompletionException holding this exception as its cause.
 * If no suppliers are provided, returns an incomplete Cffu.
 *
 * @param <T>       the suppliers' return type
 * @return the new Cffu
 * @see anyOfCffu
 */
fun <T> Array<out Supplier<out T>>.mSupplyAnyAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<T> = cffuFactory.mSupplyAnyAsync(executor, *this)

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * after runs the given actions.
 *
 * @return the new Cffu
 * @see allFastFailOfCffu
 */
fun Collection<Runnable>.mRunFastFailAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunFastFailAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * after runs the given actions.
 *
 * @return the new Cffu
 * @see allFastFailOfCffu
 */
fun Array<out Runnable>.mRunFastFailAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunFastFailAsync(executor, *this)

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * after runs the given actions.
 *
 * @return the new Cffu
 * @see allOfCffu
 */
fun Collection<Runnable>.mRunAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * by tasks running in the Cffu's default asynchronous execution facility
 * after runs the given actions.
 *
 * @return the new Cffu
 * @see allOfCffu
 */
fun Array<out Runnable>.mRunAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunAsync(executor, *this)

/**
 * Returns a new Cffu that is asynchronously successful
 * when any tasks running in the Cffu's default asynchronous execution facility success.
 *
 * @return the new Cffu
 * @see anySuccessOfCffu
 */
fun Collection<Runnable>.mRunAnySuccessAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunAnySuccessAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously successful
 * when any tasks running in the Cffu's default asynchronous execution facility success.
 *
 * @return the new Cffu
 * @see anySuccessOfCffu
 */
fun Array<out Runnable>.mRunAnySuccessAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunAnySuccessAsync(executor, *this)

/**
 * Returns a new Cffu that is asynchronously completed
 * when any tasks running in the Cffu's default asynchronous execution facility.
 *
 * @return the new Cffu
 * @see anyOfCffu
 */
fun Collection<Runnable>.mRunAnyAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunAnyAsync(executor, *toTypedArray())

/**
 * Returns a new Cffu that is asynchronously completed
 * when any tasks running in the Cffu's default asynchronous execution facility.
 *
 * @return the new Cffu
 * @see anyOfCffu
 */
fun Array<out Runnable>.mRunAnyAsyncCffu(
    cffuFactory: CffuFactory, executor: Executor = cffuFactory.defaultExecutor()
): Cffu<Void> = cffuFactory.mRunAnyAsync(executor, *this)

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# allOf* Methods for Collection/Array(including mostSuccessResultsOf)
////////////////////////////////////////////////////////////////////////////////

/**
 * Placeholder for optional [CffuFactory] argument.
 */
private val ABSENT: CffuFactory = CffuFactoryBuilder.POISON_FACTORY
private const val ERROR_MSG_FOR_COLL = "no cffuFactory argument provided when this collection is empty"
private const val ERROR_MSG_FOR_ARRAY = "no cffuFactory argument provided when this array is empty"

/**
 * Returns a new Cffu with the results in the **same order** of the given Cffus arguments,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [allFastFailOfCffu], except the returned Cffu contains the results of input Cffus.
 * This method is the same as [CffuFactory.allResultsFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allFastFailOfCffu
 * @see CffuFactory.allResultsFastFailOf
 */
fun <T> Collection<Cffu<out T>>.allResultsFastFailOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allResultsFastFailOf(*toTypedArray())
}

/**
 * Returns a new Cffu with the results in the **same order** of the given Cffus arguments,
 * the new Cffu success when all the given Cffus success.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [allFastFailOfCffu], except the returned Cffu contains the results of input Cffus.
 * This method is the same as [CffuFactory.allResultsFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allFastFailOfCffu
 * @see CffuFactory.allResultsFastFailOf
 */
fun <T> Array<out Cffu<out T>>.allResultsFastFailOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.allResultsFastFailOf(*this)
}

/**
 * Returns a new Cffu with the results in the **same order** of the given stages arguments,
 * the new Cffu success when all the given stages success.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allFastFailOfCffu], except the returned Cffu contains the results of input Cffus.
 * This method is the same as [CffuFactory.allResultsFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allFastFailOfCffu
 * @see CffuFactory.allResultsFastFailOf
 */
@JvmName("allResultsFastFailOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.allResultsFastFailOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsFastFailOf(*toTypedArray())

/**
 * Returns a new Cffu with the results in the **same order** of the given stages arguments,
 * the new Cffu success when all the given stages success.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allFastFailOfCffu], except the returned Cffu contains the results of input Cffus.
 * This method is the same as [CffuFactory.allResultsFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allFastFailOfCffu
 * @see CffuFactory.allResultsFastFailOf
 */
fun <T> Array<out CompletionStage<out T>>.allResultsFastFailOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsFastFailOf(*this)

/**
 * Returns a new Cffu that is successful with the results in the **same order**
 * of the given stages arguments when all the given stages completed;
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * If any of the provided stages fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the stage having a successful value of `valueIfFailed`).
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.allSuccessResultsOf], providing this method is convenient for method chaining.
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @throws NullPointerException if the cfs param or any of its elements are `null`
 * @see com.google.common.util.concurrent.Futures.successfulAsList
 */
fun <T> Collection<Cffu<out T>>.allSuccessResultsOfCffu(
    valueIfFailed: T, cffuFactory: CffuFactory = ABSENT
): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allSuccessResultsOf(valueIfFailed, *toTypedArray())
}

/**
 * Returns a new Cffu that is successful with the results in the **same order**
 * of the given stages arguments when all the given stages completed;
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * If any of the provided stages fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the stage having a successful value of `valueIfFailed`).
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.allSuccessResultsOf], providing this method is convenient for method chaining.
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @throws NullPointerException if the cfs param or any of its elements are `null`
 * @see com.google.common.util.concurrent.Futures.successfulAsList
 */
fun <T> Array<out Cffu<out T>>.allSuccessResultsOfCffu(
    valueIfFailed: T, cffuFactory: CffuFactory = ABSENT
): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allSuccessResultsOf(valueIfFailed, *this)
}

/**
 * Returns a new Cffu that is successful with the results in the **same order**
 * of the given stages arguments when all the given stages completed;
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * If any of the provided stages fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the stage having a successful value of `valueIfFailed`).
 *
 * This method is the same as [CffuFactory.allSuccessResultsOf], providing this method is convenient for method chaining.
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @throws NullPointerException if the cfs param or any of its elements are `null`
 * @see com.google.common.util.concurrent.Futures.successfulAsList
 */
@JvmName("allSuccessResultsOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.allSuccessResultsOfCffu(
    valueIfFailed: T, cffuFactory: CffuFactory
): Cffu<List<T>> = cffuFactory.allSuccessResultsOf(valueIfFailed, *toTypedArray())

/**
 * Returns a new Cffu that is successful with the results in the **same order**
 * of the given stages arguments when all the given stages completed;
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * If any of the provided stages fails, its corresponding position will contain `valueIfFailed`
 * (which is indistinguishable from the stage having a successful value of `valueIfFailed`).
 *
 * This method is the same as [CffuFactory.allSuccessResultsOf], providing this method is convenient for method chaining.
 *
 * @param valueIfFailed the value to return if not completed successfully
 * @throws NullPointerException if the cfs param or any of its elements are `null`
 * @see com.google.common.util.concurrent.Futures.successfulAsList
 */
@JvmName("allSuccessResultsOfCffuCs")
fun <T> Array<out CompletionStage<out T>>.allSuccessResultsOfCffu(
    valueIfFailed: T, cffuFactory: CffuFactory
): Cffu<List<T>> = cffuFactory.allSuccessResultsOf(valueIfFailed, *this)

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given Cffus arguments in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see CffuFactory.mostSuccessResultsOf
 * @see CffuFactory.mostSuccessTupleOf
 * @see Cffu.getSuccessNow
 */
fun <T> Collection<Cffu<out T>>.mostSuccessResultsOfCffu(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit, cffuFactory: CffuFactory = ABSENT
): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, *toTypedArray())
}

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given Cffus arguments in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see CffuFactory.mostSuccessResultsOf
 * @see CffuFactory.mostSuccessTupleOf
 * @see Cffu.getSuccessNow
 */
fun <T> Array<out Cffu<out T>>.mostSuccessResultsOfCffu(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit, cffuFactory: CffuFactory = ABSENT
): Cffu<List<T>> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, *this)
}

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given stages arguments in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see CffuFactory.mostSuccessResultsOf
 * @see CffuFactory.mostSuccessTupleOf
 * @see Cffu.getSuccessNow
 */
@JvmName("mostSuccessResultsOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.mostSuccessResultsOfCffu(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit, cffuFactory: CffuFactory
): Cffu<List<T>> =
    cffuFactory.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, *toTypedArray())

/**
 * Returns a new Cffu with the most results in the **same order** of
 * the given stages arguments in the given time(`timeout`, aka as many results as possible in the given time).
 *
 * If any of the provided stages does not success(fails or incomplete) in given time,
 * its corresponding position will contain `valueIfNotSuccess`.
 *
 * @param timeout       how long to wait in units of `unit`
 * @param unit          a `TimeUnit` determining how to interpret the `timeout` parameter
 * @param valueIfNotSuccess the value to return if not completed successfully
 * @see CffuFactory.mostSuccessResultsOf
 * @see CffuFactory.mostSuccessTupleOf
 * @see Cffu.getSuccessNow
 */
fun <T> Array<out CompletionStage<out T>>.mostSuccessResultsOfCffu(
    valueIfNotSuccess: T, timeout: Long, unit: TimeUnit, cffuFactory: CffuFactory
): Cffu<List<T>> =
    cffuFactory.mostSuccessResultsOf(valueIfNotSuccess, timeout, unit, *this)

/**
 * Returns a new Cffu with the results in the **same order** of the given Cffus arguments,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input Cffus.
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
    return factory.allResultsOf(*toTypedArray())
}

/**
 * Returns a new Cffu with the results in the **same order** of the given Cffus arguments,
 * the new Cffu is completed when all the given Cffus complete.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value empty list.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input Cffus.
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
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
 * Returns a new Cffu with the results in the **same order** of the given stages arguments,
 * the new Cffu is completed when all the given stages complete.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input stages.
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
@JvmName("allResultsOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.allResultsOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOf(*toTypedArray())

/**
 * Returns a new Cffu with the results in the **same order** of the given stages arguments,
 * the new Cffu is completed when all the given stages complete.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so, with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value empty list.
 *
 * This method is the same as [allOfCffu], except the returned Cffu contains the results of input stages.
 * This method is the same as [CffuFactory.allResultsOf], providing this method is convenient for method chaining.
 *
 * @see allOfCffu
 * @see CffuFactory.allResultsOf
 */
fun <T> Array<out CompletionStage<out T>>.allResultsOfCffu(cffuFactory: CffuFactory): Cffu<List<T>> =
    cffuFactory.allResultsOf(*this)

/**
 * Returns a new Cffu that is successful when all the given Cffus success,
 * the results(`Cffu<Void>`) of the given Cffus are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given Cffus complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given Cffus,
 * with a CompletionException holding this exception as its cause.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.allFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allResultsFastFailOfCffu
 * @see CffuFactory.allFastFailOf
 */
fun Collection<Cffu<*>>.allFastFailOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allFastFailOf(*toTypedArray())
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
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.allFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allResultsFastFailOfCffu
 * @see CffuFactory.allFastFailOf
 */
fun Array<out Cffu<*>>.allFastFailOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.allFastFailOf(*this)
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
 * This method is the same as [CffuFactory.allFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allResultsFastFailOfCffu
 * @see CffuFactory.allFastFailOf
 */
@JvmName("allFastFailOfCffuCs")
fun Collection<CompletionStage<*>>.allFastFailOfCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allFastFailOf(*toTypedArray())

/**
 * Returns a new Cffu that is successful when all the given stages success,
 * the results(`Cffu<Void>`) of the given stages are not reflected in the returned Cffu,
 * but may be obtained by inspecting them individually.
 * If any of the given stages complete exceptionally, then the returned Cffu
 * also does so *without* waiting other incomplete given stages,
 * with a CompletionException holding this exception as its cause.
 * If no stages are provided, returns a Cffu completed with the value `null`.
 *
 * This method is the same as [CffuFactory.allFastFailOf], providing this method is convenient for method chaining.
 *
 * @see allResultsFastFailOfCffu
 * @see CffuFactory.allFastFailOf
 */
fun Array<out CompletionStage<*>>.allFastFailOfCffu(cffuFactory: CffuFactory): Cffu<Void> =
    cffuFactory.allFastFailOf(*this)

/**
 * Returns a new Cffu that is completed when all the given Cffus complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given Cffus are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
 *
 * @see allResultsOfCffu
 * @see CffuFactory.allOf
 */
fun Collection<Cffu<*>>.allOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<Void> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.allOf(*toTypedArray())
}

/**
 * Returns a new Cffu that is completed when all the given Cffus complete.
 * If any of the given Cffu complete exceptionally, then the returned
 * Cffu also does so, with a CompletionException holding this exception as its cause.
 * Otherwise, the results, if any, of the given Cffus are not reflected in
 * the returned Cffu(`Cffu<Void>`), but may be obtained by inspecting them individually.
 * If no Cffus are provided, returns a Cffu completed with the value `null`.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.allOf], providing this method is convenient for method chaining.
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
    cffuFactory.allOf(*toTypedArray())

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

// endregion
////////////////////////////////////////////////////////////////////////////////
// region# anyOf* methods for Collection/Array
////////////////////////////////////////////////////////////////////////////////

/**
 * Returns a new Cffu that is successful when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.anySuccessOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anySuccessOf
 */
fun <T> Collection<Cffu<out T>>.anySuccessOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.anySuccessOf(*toTypedArray())
}

/**
 * Returns a new Cffu that is successful when any of the given Cffus success,
 * with the same result. Otherwise, all the given Cffus complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given Cffus as its cause. If no Cffus are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.anySuccessOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anySuccessOf
 */
fun <T> Array<out Cffu<out T>>.anySuccessOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_ARRAY)
    return factory.anySuccessOf(*this)
}

/**
 * Returns a new Cffu that is successful when any of the given stages success,
 * with the same result. Otherwise, all the given stages complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause. If no stages are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anySuccessOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anySuccessOf
 */
@JvmName("anySuccessOfCffuCs")
fun <T> Collection<CompletionStage<out T>>.anySuccessOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anySuccessOf(*toTypedArray())

/**
 * Returns a new Cffu that is successful when any of the given stages success,
 * with the same result. Otherwise, all the given stages complete exceptionally,
 * the returned Cffu also does so, with a CompletionException holding
 * an exception from any of the given stages as its cause. If no stages are provided,
 * returns a new Cffu that is already completed exceptionally with a CompletionException
 * holding a [NoCfsProvidedException][io.foldright.cffu.NoCfsProvidedException] as its cause.
 *
 * This method is the same as [CffuFactory.anySuccessOf], providing this method is convenient for method chaining.
 *
 * @see anyOfCffu
 * @see CffuFactory.anySuccessOf
 */
fun <T> Array<out CompletionStage<out T>>.anySuccessOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anySuccessOf(*this)

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * If this collection is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this collection is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
fun <T> Collection<Cffu<out T>>.anyOfCffu(cffuFactory: CffuFactory = ABSENT): Cffu<T> {
    val factory: CffuFactory = if (cffuFactory !== ABSENT) cffuFactory
    else firstOrNull()?.cffuFactory() ?: throw IllegalArgumentException(ERROR_MSG_FOR_COLL)
    return factory.anyOf(*toTypedArray())
}

/**
 * Returns a new Cffu that is completed when any of the given Cffus complete, with the same result.
 *
 * If this array is not empty, `cffuFactory` argument is optional, use the `cffuFactory` of the first cffu element.
 * If this array is empty and no`cffuFactory` provided, throw [IllegalArgumentException].
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
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
    cffuFactory.anyOf(*toTypedArray())

/**
 * Returns a new Cffu that is completed when any of the given stages complete, with the same result.
 *
 * This method is the same as [CffuFactory.anyOf], providing this method is convenient for method chaining.
 *
 * @see CffuFactory.anyOf
 */
fun <T> Array<out CompletionStage<out T>>.anyOfCffu(cffuFactory: CffuFactory): Cffu<T> =
    cffuFactory.anyOf(*this)
