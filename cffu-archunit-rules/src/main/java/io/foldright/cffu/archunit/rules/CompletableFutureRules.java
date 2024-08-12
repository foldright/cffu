package io.foldright.cffu.archunit.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchRule;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;


public class CompletableFutureRules {
    public static final ArchRule ASYNC_CF_METHODS_WITHOUT_SHOULD_NOT_BE_CALLED = methods()
            .that()
            .areDeclaredIn(CompletableFuture.class)
            .and().arePublic()
            .and().haveNameEndingWith("async")
            .and().haveRawParameterTypes(new DescribedPredicate<List<JavaClass>>("without Executor type") {
                @Override
                public boolean test(List<JavaClass> javaClasses) {
                    return javaClasses.stream().noneMatch(javaClass -> javaClass.isEquivalentTo(Executor.class));
                }
            })
            .should()
            .onlyBeCalled()
            .byClassesThat(DescribedPredicate.alwaysFalse());


    public static final ArchRule xxx = classes()
            .should()
            .
}
