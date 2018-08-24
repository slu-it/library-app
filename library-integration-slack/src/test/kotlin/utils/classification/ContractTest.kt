package utils.classification

import org.junit.jupiter.api.Tag


/**
 * Custom annotation for filtering contract tests on the class level.
 * - [tagged][Tag] as `contract-test`
 */

@Target(AnnotationTarget.CLASS)
@Retention
@Tag("contract-test")
annotation class ContractTest