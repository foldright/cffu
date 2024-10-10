/**
 * A tiny sidekick library to make CompletableFuture usage more convenient, more efficient and safer
 * in your application. 😋🚀🦺
 * <p> The core classes are {@link io.foldright.cffu.Cffu}/{@link io.foldright.cffu.CffuFactory}. And the core
 * util class {@link io.foldright.cffu.CompletableFutureUtils} of {@link java.util.concurrent.CompletableFuture}
 * contains the enhanced and backport methods for {@link java.util.concurrent.CompletableFuture}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
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
