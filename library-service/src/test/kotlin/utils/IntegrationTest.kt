package utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD

/**
 * Qualifier for Integration Tests:
 *
 * - [tagged][Tag] as `integration-test`
 * - [test instance][TestInstance] generation: `PER_METHOD`
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("integration-test")
@TestInstance(PER_METHOD)
annotation class IntegrationTest