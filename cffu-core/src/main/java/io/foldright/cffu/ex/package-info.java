/**
 * Exception handle for {@link java.util.concurrent.CompletableFuture CompletableFuture}s
 * and {@link io.foldright.cffu.Cffu Cffu}s.
 *
 * @see <a href="https://peps.python.org/pep-0020/">Errors should never pass silently. Unless explicitly silenced.</a>
 */
@DefaultAnnotation(NonNull.class)
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
package io.foldright.cffu.ex;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;
