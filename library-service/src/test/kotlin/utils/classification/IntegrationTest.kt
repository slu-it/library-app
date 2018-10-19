package utils.classification

import org.junit.jupiter.api.Tag
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import utils.testconfiguration.BaseTestConfiguration
import utils.testconfiguration.IntegrationTestConfiguration

/**
 * Qualifier for Integration Tests:
 *
 * - [tagged][Tag] as `integration-test`
 * - if annotated class is a Spring Boot test:
 * -- imports [BaseTestConfiguration]
 * -- imports [IntegrationTestConfiguration]
 * -- activates profiles: `test` and `integration-test`
 * -- enables [DirtiesContext]
 *
 * An integration test is a test integrating one or more components of a
 * system. A component can be a module, a set of classes, a framework or
 * something like a database. It is allowed to fake (mock, stub etc.) parts
 * of the application in order to isolate the part you want to test.
 *
 * For this application integration tests are generally used for the outer
 * boundaries like HTTP (API), the database (persistence) and the correct
 * usage of framework features like caching, aspects, error handlers etc.
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("integration-test")
@DirtiesContext
@Import(BaseTestConfiguration::class, IntegrationTestConfiguration::class)
@ActiveProfiles("test", "integration-test")
annotation class IntegrationTest