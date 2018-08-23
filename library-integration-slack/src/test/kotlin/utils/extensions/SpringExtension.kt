package utils.extensions

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

/**
 * Custom annotation for enabling Spring Junit5 extension on the class level.
 */
@Target(AnnotationTarget.CLASS)
@Retention
@ExtendWith(SpringExtension::class)
annotation class EnableSpringExtension
