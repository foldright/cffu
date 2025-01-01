/**
 * Exception handle from <strong>MULTIPLE</strong> {@link java.util.concurrent.CompletionStage CompletionStage}s
 * (including {@link java.util.concurrent.CompletableFuture CompletableFuture}s and {@link io.foldright.cffu.Cffu Cffu}s).
 * <p>
 * More info see the doc of the core class
 * {@link io.foldright.cffu.eh.ExHandleOfMultiplyCfsUtils ExHandleOfMultiplyCfsUtils} of this package.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see io.foldright.cffu.eh.ExHandleOfMultiplyCfsUtils
 * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 */
@DefaultAnnotation(NonNull.class)
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
package io.foldright.cffu.eh;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
