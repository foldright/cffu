package io.foldright.cffu;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import org.jetbrains.annotations.Contract;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.Future;

import static java.util.Objects.requireNonNull;


/**
 * Same as {@link Future.State}, existed for java version compatibility,
 * {@link Cffu#cffuState()} to {@link Cffu#state()}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Future.State
 */
@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
public enum CffuState {
    /**
     * The task has not completed.
     */
    RUNNING {
        @Override
        public Future.State toFutureState() {
            return Future.State.RUNNING;
        }
    },
    /**
     * The task completed with a result.
     *
     * @see Cffu#resultNow()
     * @see Future#resultNow()
     */
    SUCCESS {
        @Override
        public Future.State toFutureState() {
            return Future.State.SUCCESS;
        }
    },
    /**
     * The task completed with an exception.
     *
     * @see Cffu#exceptionNow()
     * @see Future#exceptionNow()
     */
    FAILED {
        @Override
        public Future.State toFutureState() {
            return Future.State.FAILED;
        }
    },
    /**
     * The task was cancelled.
     *
     * @see Cffu#cancel(boolean)
     * @see Future#cancel(boolean)
     */
    CANCELLED {
        @Override
        public Future.State toFutureState() {
            return Future.State.CANCELLED;
        }
    },

    ;

    /**
     * Convert {@link CffuState} to {@link Future.State}.
     *
     * @see #toCffuState(Future.State)
     */
    @Contract(pure = true)
    public abstract Future.State toFutureState();

    /**
     * Convert {@link Future.State} to {@link CffuState}.
     *
     * @see #toFutureState()
     */
    @Contract(pure = true)
    public static CffuState toCffuState(Future.State state) {
        switch (requireNonNull(state, "state argument is null")) {
            case RUNNING:
                return CffuState.RUNNING;
            case SUCCESS:
                return CffuState.SUCCESS;
            case FAILED:
                return CffuState.FAILED;
            case CANCELLED:
                return CffuState.CANCELLED;
            default:
                throw new IllegalStateException("unknown Future.State: " + state);
        }
    }
}
