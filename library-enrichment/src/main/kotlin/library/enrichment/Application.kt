package library.enrichment

import library.enrichment.datasources.isbndb.IsbnDbConfiguration
import library.enrichment.datasources.openlibrary.OpenLibraryConfiguration
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.time.Clock

@SpringBootApplication
class Application {

    @Bean fun utcClock(): Clock = Clock.systemUTC()

}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
