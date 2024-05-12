package io.foldright.cffu.generics;

import io.foldright.cffu.CompletableFutureUtils;
import io.foldright.test_utils.TestThreadPoolManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static io.foldright.cffu.CompletableFutureUtils.orTimeout;
import static io.foldright.cffu.CompletableFutureUtils.resultNow;
import static java.util.concurrent.CompletableFuture.completedFuture;


/**
 * Checks type parameter declaration, Variance(covariance/contravariance)
 */
@SuppressWarnings({"UnnecessaryLocalVariable", "unused", "RedundantThrows"})
public class TypeParameterDeclarationTest {

    private static final CompletableFuture<Integer> f = completedFuture(42);
    private static final CompletableFuture<? extends Integer> fe = f;
    private static final CompletableFuture<? super Integer> fs = f;
    private static final CompletableFuture<?> fq = f;

    @Test
    void checkTypeParameterDeclaration_peek_completeAsync() throws Exception {
        final BiConsumer<? super Integer, Throwable> c = (v, ex) -> {
        };
        CompletableFutureUtils.peek(fe, c).get();
        CompletableFutureUtils.peekAsync(fe, c).get();
        CompletableFutureUtils.peekAsync(fe, c, executorService).get();

        final Supplier<? extends Integer> s = () -> 0;
        fs.complete(0);
        CompletableFutureUtils.completeAsync(fs, s).complete(1);
        CompletableFutureUtils.completeAsync(fs, s, executorService).complete(1);

        CompletableFuture<? extends Integer> ffe = orTimeout(fe, 1, TimeUnit.MILLISECONDS);
        CompletableFuture<? super Integer> ffs = orTimeout(fs, 1, TimeUnit.MILLISECONDS);
        CompletableFuture<?> ffq = orTimeout(fq, 1, TimeUnit.MILLISECONDS);
    }

    @Test
    void checkTypeParameterDeclaration_resultNow() throws Exception {
        Integer ii = resultNow(f);
        Integer iie = resultNow(fe);
        Object iis = resultNow(fs);
        Object iiq = resultNow(fq);
    }

    @Test
    void checkTypeParameterDeclaration_get() throws Exception {
        Integer i = f.get();
        Integer ie = fe.get();
        Object is = fs.get();
        Object iq = fq.get();
    }

    private static ExecutorService executorService;

    @BeforeAll
    static void beforeAll() {
        executorService = TestThreadPoolManager.createThreadPool("TypeParameterDeclarationTest");
    }

    @AfterAll
    static void afterAll() {
        TestThreadPoolManager.shutdownExecutorService(executorService);
    }
}
