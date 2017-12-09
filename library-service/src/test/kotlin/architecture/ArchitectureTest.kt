package architecture

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaAnnotation
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.AcceptanceTest
import utils.classification.IntegrationTest
import utils.classification.UnitTest

@UnitTest
internal class ArchitectureTest {

    val classes = ClassFileImporter().importClasspath()!!

    @Nested inner class `business module` {

        @Test fun `must have no knowledge about the existence of the API module`() = checkThat {
            noClasses().that()
                    .resideInAPackage("library.service.business..")
                    .should().accessClassesThat()
                    .resideInAPackage("library.service.api..")
        }

        @Test fun `must have no knowledge about the existence of the persistence module`() = checkThat {
            noClasses().that()
                    .resideInAPackage("library.service.business..")
                    .should().accessClassesThat()
                    .resideInAPackage("library.service.persistence..")
        }

        @Test fun `must have no knowledge about the existence of the messaging module`() = checkThat {
            noClasses().that()
                    .resideInAPackage("library.service.business..")
                    .should().accessClassesThat()
                    .resideInAPackage("library.service.messaging..")
        }

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
            val javaClass = input.type
            return when {
                javaClass.isEquivalentTo(UnitTest::class.java) -> true
                javaClass.isEquivalentTo(IntegrationTest::class.java) -> true
                javaClass.isEquivalentTo(AcceptanceTest::class.java) -> true
                else -> false
            }
        }

    }

}