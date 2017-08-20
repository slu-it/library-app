package utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Qualifier for Integration Tests:
 *
 * - [tagged][Tag] as `integration-test`
 * - [test instance][TestInstance] generation: `PER_METHOD`
 * - [Mongo DB][DataMongoTest] test support
 */
@Retention
@Target(AnnotationTarget.CLASS)
@Tag("integration-test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DataMongoTest
@ExtendWith(SpringExtension::class)
annotation class DatabaseIntegrationTest