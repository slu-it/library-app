package utils.classification

import org.junit.jupiter.api.Tag
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import utils.testconfiguration.BaseTestConfiguration
import utils.testconfiguration.ContractTestConfiguration

/**
 * Qualifier for Contract Tests:
 *
 * - [tagged][Tag] as `contract-test`
 * - if annotated class is a Spring Boot test:
 * -- imports [BaseTestConfiguration]
 * -- imports [ContractTestConfiguration]
 * -- activates profiles: `test` and `contract-test`
 * -- enables [DirtiesContext]
 *
 * A contract test is a test veryfing the contracts (e.g. PACT) provided by
 * consumers of this service (e.g. HTTP, Messages).
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("contract-test")
@DirtiesContext
@Import(BaseTestConfiguration::class, ContractTestConfiguration::class)
@ActiveProfiles("test", "contract-test")
annotation class ContractTest