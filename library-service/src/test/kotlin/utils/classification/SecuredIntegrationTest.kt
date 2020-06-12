package utils.classification

import org.junit.jupiter.api.Tag
import org.springframework.context.annotation.Import
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import utils.testconfiguration.BaseTestConfiguration
import utils.testconfiguration.IntegrationTestConfiguration

/**
 * This qualifier works exactly like [IntegrationTest], except that it activates
 * the `secured-integration-test` profile, enabling the default security configuration.
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("integration-test")
@DirtiesContext
@Import(BaseTestConfiguration::class, IntegrationTestConfiguration::class)
@ActiveProfiles("test", "secured-integration-test")
annotation class SecuredIntegrationTest