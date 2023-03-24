package io.foldright.cffu.kotlin

import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import java.util.concurrent.CompletionStage

/**
 * Wrap this [CompletionStage] to [Cffu].
 *
 * reimplement [CffuFactory.asCffu] method as extension of [CompletionStage], it's convenient for method chaining.
 *
 * @see CffuFactory.asCffu
 */
fun <T> CompletionStage<T>.asCffu(cffuFactory: CffuFactory): Cffu<T> = cffuFactory.asCffu(this)
