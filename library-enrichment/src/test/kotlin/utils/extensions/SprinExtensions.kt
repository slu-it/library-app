package utils.extensions

import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension

@Retention
@Target(AnnotationTarget.CLASS)
@ExtendWith(SpringExtension::class)
annotation class EnableSpringExtension