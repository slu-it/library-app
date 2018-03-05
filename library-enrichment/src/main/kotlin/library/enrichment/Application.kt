package library.enrichment

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

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
