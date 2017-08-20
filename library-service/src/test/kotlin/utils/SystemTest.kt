package utils

import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Qualifier for Integration Tests with Spring Boot:
 *
 * - [tagged][Tag] as `system-test`
 * - [test instance][TestInstance] generation: `PER_METHOD`
 * - [spring boot test features][SpringBootTest] activated with random port
 */
@Retention
@Target(AnnotationTarget.CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Tag("system-test")
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@ExtendWith(SpringExtension::class)
annotation class SystemTest