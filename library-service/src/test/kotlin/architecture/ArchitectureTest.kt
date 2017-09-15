package architecture

import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.ArchRule
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test


internal class ArchitectureTest {

    val classes = ClassFileImporter().importClasspath()

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

    }

    fun checkThat(ruleSupplier: () -> ArchRule) {
        ruleSupplier().check(classes)
    }

}