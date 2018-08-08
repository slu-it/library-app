package library.integration.slack

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication(exclude = [SecurityAutoConfiguration::class])
class SlackIntegrationApplication {
    @Bean
    fun objectMapper(): ObjectMapper = ObjectMapper().apply {
        findAndRegisterModules()
    }
}

fun main(args: Array<String>) {
    runApplication<SlackIntegrationApplication>(*args)
}
