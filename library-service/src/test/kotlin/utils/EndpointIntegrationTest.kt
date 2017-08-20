package utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Qualifier for Integration Tests:
 *
 * - [tagged][Tag] as `integration-test`
 * - [test instance][TestInstance] generation: `PER_METHOD`
 * - [WebMVC][WebMvcTest] test support
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@WebMvcTest
@ExtendWith(SpringExtension::class)
annotation class EndpointIntegrationTest