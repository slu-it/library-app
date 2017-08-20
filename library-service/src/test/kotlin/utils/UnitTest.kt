package utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD

/**
 * Qualifier for Unit Tests:
 *
 * - [tagged][Tag] as `unit-test`
 * - [test instance][TestInstance] generation: `PER_METHOD`
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("unit-test")
@TestInstance(PER_METHOD)
annotation class UnitTest
