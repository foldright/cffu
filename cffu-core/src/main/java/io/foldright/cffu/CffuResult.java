package io.foldright.cffu;

import io.foldright.cffu.tuple.Tuple3;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static io.foldright.cffu.CompletableFutureUtils.arrayList;


public class CffuResult<T> {
    private final CompletableFuture<? extends T> cf;

    /**
     * (State, Exception, Result)
     */
    private volatile Tuple3<CffuState, Throwable, T> ser;

    public CffuResult(CompletableFuture<? extends T> cf) {
        this.cf = cf;
    }

    public CffuState state() {
        Tuple3<CffuState, Throwable, T> ret = ser();
        if (ret != null) return ret._1;
        return CffuState.RUNNING;
    }

    public Throwable exception() {
        Tuple3<CffuState, Throwable, T> ret = ser();
        if (ret != null) return ret._2;
        return null;
    }

    public T result() {
        Tuple3<CffuState, Throwable, T> ret = ser();
        if (ret != null) return ret._3;
        return null;
    }

    private Tuple3<CffuState, Throwable, T> ser() {
        if (ser != null) return ser;

        if (!cf.isDone()) return null;
        if (cf.isCancelled()) {
            return ser = Tuple3.of(CffuState.CANCELLED, CompletableFutureUtils.exceptionNow(cf), null);
        }

        boolean interrupted = false;
        try {
            while (true) {
                try {
                    T r = cf.get();// may throw InterruptedException when done
                    return Tuple3.of(CffuState.SUCCESS, null, r);
                } catch (InterruptedException e) {
                    interrupted = true;
                } catch (ExecutionException e) {
                    return ser = Tuple3.of(CffuState.FAILED, e.getCause(), null);
                }
            }
        } finally {
            if (interrupted) Thread.currentThread().interrupt();
        }
    }


    static <T> @NotNull List<CffuResult<T>> getCffuResults(CompletableFuture<T>[] cfArray) {
        @SuppressWarnings("unchecked")
        final CffuResult<T>[] results = new CffuResult[cfArray.length];
        for (int i = 0; i < cfArray.length; i++) {
            results[i] = new CffuResult<>(cfArray[i]);
        }
        return arrayList(results);
    }
}
