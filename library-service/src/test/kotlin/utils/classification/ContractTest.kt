package utils.classification

import org.junit.jupiter.api.Tag

/**
 * Qualifier for Acceptance Tests:
 *
 * - [tagged][Tag] as `contract-test`
 *
 * A contract test is a test veryfing the contracts (e.g. PACT) provided by
 * consumers of this service (e.g. HTTP, Messages).
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("contract-test")
annotation class ContractTest