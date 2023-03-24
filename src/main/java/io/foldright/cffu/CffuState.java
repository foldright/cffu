package io.foldright.cffu;

import java.util.concurrent.Future;


/**
 * Same as {@link Future.State}, existed for java version compatibility,
 * {@link Cffu#cffuState()} to {@link Cffu#state()}.
 *
 * @author Jerry Lee (oldratlee at gmail dot com)
 * @see Future.State
 */
public enum CffuState {
    /**
     * The task has not completed.
     */
    RUNNING,
    /**
     * The task completed with a result.
     *
     * @see Cffu#resultNow()
     * @see Future#resultNow()
     */
    SUCCESS,
    /**
     * The task completed with an exception.
     *
     * @see Cffu#exceptionNow()
     * @see Future#exceptionNow()
     */
    FAILED,
    /**
     * The task was cancelled.
     *
     * @see Cffu#cancel(boolean)
     * @see Future#cancel(boolean)
     */
    CANCELLED,

    ;

    /**
     * Convert {@link Future.State} to {@link CffuState}.
     *
     * @see #toFutureState(CffuState)
     */
    public static CffuState toCffuState(Future.State state) {
        switch (state) {
            case RUNNING:
                return CffuState.RUNNING;
            case SUCCESS:
                return CffuState.SUCCESS;
            case FAILED:
                return CffuState.FAILED;
            case CANCELLED:
                return CffuState.CANCELLED;
            default:
                throw new IllegalStateException();
        }
    }

    /**
     * Convert {@link CffuState} to {@link Future.State}.
     *
     * @see #toCffuState(Future.State)
     */
    public static Future.State toFutureState(CffuState state) {
        switch (state) {
            case RUNNING:
                return Future.State.RUNNING;
            case SUCCESS:
                return Future.State.SUCCESS;
            case FAILED:
                return Future.State.FAILED;
            case CANCELLED:
                return Future.State.CANCELLED;
            default:
                throw new IllegalStateException();
        }
    }
}
