package library.enrichment

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Clock

@SpringBootApplication(exclude = [
    SecurityAutoConfiguration::class,
    ErrorMvcAutoConfiguration::class
])
class Application {

    @Bean fun utcClock(): Clock = Clock.systemUTC()
    @Bean fun objectMapper(): ObjectMapper = ObjectMapper().apply { findAndRegisterModules() }

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
