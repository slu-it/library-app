package library.enrichment.external.isbndb

import feign.Feign
import feign.Logger
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import library.enrichment.common.feign.DynamicUrlTarget
import library.enrichment.external.openlibrary.OpenLibraryClient
import library.enrichment.external.openlibrary.OpenLibrarySettings
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(IsbnDbSettings::class)
class IsbnDbConfiguration {

    @Bean fun isbnDbClient(settings: IsbnDbSettings): IsbnDbClient {
        val target = DynamicUrlTarget(IsbnDbClient::class) { settings.url }
        return Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .logger(Slf4jLogger("utils.feign.isbndb"))
                .logLevel(settings.logLevel)
                .target(target)
    }

}