/**
 * A tiny sidekick library for CompletableFuture to improve user experience and reduce misuse.
 * <p>
 * The core class is {@link io.foldright.cffu.Cffu}. And utils for {@link java.util.concurrent.CompletableFuture},
 * the key user class is {@link io.foldright.cffu.CompletableFutureUtils}
 * which contains the enhanced and backport methods for {@link java.util.concurrent.CompletableFuture}.
 *
 * @see io.foldright.cffu.Cffu
 * @see io.foldright.cffu.CffuFactory
 * @see io.foldright.cffu.CffuFactoryBuilder
 * @see io.foldright.cffu.CompletableFutureUtils
 */
@DefaultAnnotation(NonNull.class)
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
