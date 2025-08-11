/**
 * Exception handle from <strong>MULTIPLE</strong> {@link java.util.concurrent.CompletionStage CompletionStage}s
 * (including {@link java.util.concurrent.CompletableFuture CompletableFuture}s and {@link io.foldright.cffu2.Cffu Cffu}s).
 * <p>
 * See the core class {@link io.foldright.cffu2.eh.SwallowedExceptionHandleUtils SwallowedExceptionHandleUtils}
 * documentation for more info.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see io.foldright.cffu2.eh.SwallowedExceptionHandleUtils
 * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 */
@DefaultAnnotation(NonNull.class)
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
package io.foldright.cffu2.eh;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
