package io.foldright.cffu;

import org.jetbrains.annotations.NonBlocking;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static io.foldright.cffu.CompletableFutureUtils.*;
import static java.util.Objects.requireNonNull;


public class Results<T> {
    private final CompletionStage<? extends T> cfs;

    Results(CompletionStage<? extends T> cfs) {
        this.cfs = cfs;
    }


    public static <T> ResultsBuilder<T> build(CompletionStage<? extends T>[] cfs) {
        return new ResultsBuilder<>(cfs);
    }

    public static class ResultsBuilder<T> {
        private final CompletionStage<? extends T>[] css;
        long timeout;
        TimeUnit unit;

        public ResultsBuilder(CompletionStage<? extends T>[] cfs) {
            this.css = requireCfsAndEleNonNull(cfs);
        }

        ResultsBuilder timeout(long timeout, TimeUnit unit) {
            this.timeout = timeout;
            this.unit = requireNonNull(unit, "unit is null");
            return this;
        }

        CompletableFuture<List<CompletableFuture<T>>> build() {
            final int size = css.length;
            if (size == 0) return CompletableFuture.completedFuture(arrayList());
            if (size == 1) {
                CompletableFuture<T> cf = copy(toCf(css[0]));
                return orTimeout(cf, timeout, unit).handle((unused, ex) -> arrayList(cf));
            }

            final CompletableFuture<T>[] cfArray = f_toCfArray(css);
            return orTimeout(CompletableFuture.allOf(cfArray), timeout, unit)
                    .handle((unused, throwable) -> arrayList(cfArray));
        }
    }

    @NonBlocking
    public static <T> List<T> results(CompletionStage<? extends T>... cfs) {
        final Function<CompletableFuture<T>, T> converter = cf -> {
            if (cf.state() == Future.State.SUCCESS) return cf.join();
            else return null;
        };
        return results(converter, cfs);
    }

    @SuppressWarnings("unchecked")
    public static <T, R> List<R> results(
            Function<CompletableFuture<T>, ? extends R> converter, CompletionStage<? extends T>... cfs) {
        final CompletableFuture<T>[] cfArray = f_toCfArray(cfs);
        Object[] results = new Object[cfArray.length];
        for (int i = 0; i < cfArray.length; i++) {
            results[i] = converter.apply(cfArray[i]);
        }
        return (List<R>) arrayList(results);
    }
}
