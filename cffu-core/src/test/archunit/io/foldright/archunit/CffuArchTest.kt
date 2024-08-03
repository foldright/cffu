package io.foldright.archunit

import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.junit.AnalyzeClasses
import com.tngtech.archunit.junit.ArchTest
import com.tngtech.archunit.lang.ArchCondition
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.ConditionEvents
import com.tngtech.archunit.lang.SimpleConditionEvent
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import io.foldright.cffu.Cffu
import io.foldright.cffu.CffuFactory
import io.foldright.cffu.CffuFactoryBuilder

/**
 * [ArchUnit User Guide - 3.3. Using JUnit 4 or JUnit 5](https://www.archunit.org/userguide/html/000_Index.html#_using_junit_4_or_junit_5)
 */
@AnalyzeClasses(packagesOf = [Cffu::class], importOptions = [ImportOption.DoNotIncludeTests::class])
@Suppress("unused")
internal object CffuArchTest {
    @ArchTest
    private val constructorOfCffuFactory: ArchRule = run {
        val description = "only contain the constructors that only accessed by class CffuFactoryBuilder," +
                " aka. accessing CffuFactory constructor is only allowed by class CffuFactoryBuilder"

        val condition = object : ArchCondition<JavaClass>(description) {
            val cffuFactoryBuilderClass = CffuFactoryBuilder::class.java

            override fun check(clazz: JavaClass, events: ConditionEvents) {
                val codeUnitAccesses = clazz.constructorCallsToSelf + clazz.constructorReferencesToSelf

                codeUnitAccesses.filterNot { it.originOwner.isEquivalentTo(cffuFactoryBuilderClass) }.forEach {
                    val msg = "Accessing constructor `${it.target.fullName}` is not allowed" +
                            " by class ${it.originOwner.name}${it.sourceCodeLocation}"
                    events.add(SimpleConditionEvent.violated(it, msg))
                }
            }
        }

        ArchRuleDefinition.theClass(CffuFactory::class.java).should(condition)
    }
}
