package io.foldright.cffu.generics;

import io.foldright.cffu.CompletableFutureUtils;
import io.foldright.cffu.tuple.Tuple3;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.orTimeout;
import static io.foldright.cffu.CompletableFutureUtils.resultNow;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * Checks type parameter declaration, Variance(covariance/contravariance)
 *
 * @see io.foldright.study.generics.GenericsStudyTest
 */
@SuppressWarnings({"unused", "RedundantThrows"})
public class TypeParameterDeclarationTest {
    private static final CompletableFuture<?> fq = completedFuture(0);

    private static final CompletableFuture<Integer> fi = completedFuture(42);
    private static final CompletableFuture<? extends Integer> fie = fi;
    private static final CompletableFuture<? super Integer> fis = fi;

    private static final CompletableFuture<Boolean> fb = completedFuture(false);
    private static final CompletableFuture<? extends Boolean> fbe = fb;
    private static final CompletableFuture<? super Boolean> fbs = fb;

    private static final CompletableFuture<String> fs = completedFuture("hello");
    private static final CompletableFuture<? extends String> fse = fs;
    private static final CompletableFuture<? super String> fss = fs;

    @Test
    void checkTypeParameterDeclaration_peek_completeAsync() throws Exception {
        final BiConsumer<? super Integer, Throwable> c = (v, ex) -> {
        };
        CompletableFutureUtils.peek(fie, c).get();
        CompletableFutureUtils.peekAsync(fie, c).get();
        CompletableFutureUtils.peekAsync(fie, c, executor).get();

        final Supplier<? extends Integer> s = () -> 0;
        fis.complete(0);
        CompletableFutureUtils.completeAsync(fis, s).complete(1);
        CompletableFutureUtils.completeAsync(fis, s, executor).complete(1);

        CompletableFuture<? extends Integer> ffe = orTimeout(fie, 1, TimeUnit.MILLISECONDS);
        CompletableFuture<? super Integer> ffs = orTimeout(fis, 1, TimeUnit.MILLISECONDS);
        CompletableFuture<?> ffq = orTimeout(fq, 1, TimeUnit.MILLISECONDS);
    }

    @Test
    void checkTypeParameterDeclaration_resultNow() throws Exception {
        Integer ii = resultNow(fi);
        Integer iie = resultNow(fie);
        Object iis = resultNow(fis);
        Object iiq = resultNow(fq);
    }

    @Test
    void checkTypeParameterDeclaration_get() throws Exception {
        Integer i = fi.get();
        Integer ie = fie.get();
        Object is = fis.get();
        Object iq = fq.get();
    }

    @Test
    void test_singleUsedTypeParameter() {
        CompletableFuture<Tuple3<Integer, Boolean, String>> f1 = CompletableFutureUtils.allTupleOf(fi, fb, fs);
        CompletableFuture<? extends Tuple3<? extends Integer, ? extends Boolean, ? extends String>> f2 = CompletableFutureUtils.allTupleOf(fie, fbe, fse);
        CompletableFuture<? extends Tuple3<?, ?, ?>> f3 = CompletableFutureUtils.allTupleOf(fq, fq, fq);

        CompletableFuture<? extends Tuple3<Integer, ?, ? extends String>> f4 = CompletableFutureUtils.allTupleOf(fi, fq, fse);
    }

    private final Executor executor = Runnable::run;

}
