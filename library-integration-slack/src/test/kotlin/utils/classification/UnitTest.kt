package utils.classification

import org.junit.jupiter.api.Tag

/**
 * Custom annotation for filtering unit tests on the class level.
 * - [tagged][Tag] as `unit-test`
 */

@Target(AnnotationTarget.CLASS)
@Retention
@Tag("unit-test")
annotation class UnitTest