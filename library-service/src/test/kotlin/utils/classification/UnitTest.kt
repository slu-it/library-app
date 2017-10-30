package utils.classification

import org.junit.jupiter.api.Tag

/**
 * Qualifier for Unit Tests:
 *
 * - [tagged][Tag] as `unit-test`
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("unit-test")
annotation class UnitTest
