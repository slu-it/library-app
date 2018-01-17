package library.enrichment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import java.time.Clock

@SpringBootApplication
class Application {

    @Bean fun utcClock(): Clock = Clock.systemUTC()

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
