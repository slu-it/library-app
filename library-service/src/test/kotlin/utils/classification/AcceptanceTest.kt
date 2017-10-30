package utils.classification

import org.junit.jupiter.api.Tag
import org.springframework.test.context.ActiveProfiles

/**
 * Qualifier for Acceptance Tests:
 *
 * - [tagged][Tag] as `acceptance-test`
 * - active spring profiles: `test` and `acceptance-test`
 *
 * An acceptance test is a test against a running instance of the application
 * without faking (mocking / stubbing etc.) _any_ part of it. In addition
 * any interaction with the application should be done through official
 * interfaces like the API.
 *
 * For this application this means starting the Spring Boot application
 * context within the same JVM as the tests and using a dynamically created
 * (embedded) MongoDB instance. Using this version of MongoDB does not violate
 * the above constraints, because it is an actual MongoDB set up fresh for
 * each test run.
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("acceptance-test")
@ActiveProfiles("test", "acceptance-test")
annotation class AcceptanceTest