package architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaAnnotation
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.AcceptanceTest
import utils.classification.ContractTest
import utils.classification.IntegrationTest
import utils.classification.UnitTest

@UnitTest
internal class ArchitectureTest {

    val classes = ClassFileImporter().importClasspath()!!

    @ValueSource(strings = [
        "library.service.api",
        "library.service.database",
        "library.service.messaging"
    ])
    @ParameterizedTest fun `business module classes are not allowed to access technical modules`(packageName: String) {
        noClasses().that()
                .resideInAPackage("library.service.business..")
                .should().accessClassesThat()
                .resideInAPackage("$packageName..")
    }

    @Test fun `test classes must be classified`() = checkThat {
        classes().that()
                .haveNameMatching(".*Test(s)?")
                .and()
                .areAssignableTo(Any::class.java)
                .should()
                .beAnnotatedWith(AnyTestClassification)
    }

    fun checkThat(ruleSupplier: () -> ArchRule) {
        ruleSupplier().check(classes)
    }

    object AnyTestClassification : DescribedPredicate<JavaAnnotation>("Test Classification") {

        override fun apply(input: JavaAnnotation): Boolean {
            val javaClass = input.rawType
            return when {
                javaClass.isEquivalentTo(UnitTest::class.java) -> true
                javaClass.isEquivalentTo(IntegrationTest::class.java) -> true
                javaClass.isEquivalentTo(AcceptanceTest::class.java) -> true
                javaClass.isEquivalentTo(ContractTest::class.java) -> true
                else -> false
            }
        }

    }

}