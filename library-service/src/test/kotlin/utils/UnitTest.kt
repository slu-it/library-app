package utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_METHOD

/**
 * Qualifier for Unit Tests:
 *
 * - [tagged][Tag] as `unit-test`
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("unit-test")
annotation class UnitTest
