package io.foldright.cffu.archunit.rules

import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import io.foldright.cffu.testee.TestSample
import java.util.concurrent.CompletableFuture


@AnalyzeClasses(packagesOf = [TestSample::class, CompletableFuture::class])
@Suppress("unused")
private class CompletableFutureRulesTest {
    @ArchTest
    private val xx = CompletableFutureRules.ASYNC_CF_METHODS_WITHOUT_SHOULD_NOT_BE_CALLED;
}
