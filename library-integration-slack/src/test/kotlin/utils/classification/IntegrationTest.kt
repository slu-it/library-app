package utils.classification

import org.junit.jupiter.api.Tag

/**
 * Custom annotation for filtering integration tests on the class level.
 * - [tagged][Tag] as `integration-test`
 */

@Target(AnnotationTarget.CLASS)
@Retention
@Tag("integration-test")
annotation class IntegrationTest