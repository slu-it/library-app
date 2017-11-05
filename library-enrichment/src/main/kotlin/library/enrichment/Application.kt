package library.enrichment

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.time.Clock

@SpringBootApplication
class Application {

    @Bean fun utcClock(): Clock = Clock.systemUTC()

}

fun main(args: Array<String>) {
    SpringApplication.run(Application::class.java, *args)
}
