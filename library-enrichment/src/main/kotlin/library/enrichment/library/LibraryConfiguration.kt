package library.enrichment.library

import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import library.enrichment.common.feign.DynamicUrlTarget
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(LibrarySettings::class)
class LibraryConfiguration {

    @Bean fun libraryClient(settings: LibrarySettings): LibraryClient {
        val target = DynamicUrlTarget("library", LibraryClient::class) { settings.url }
        return Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .logger(Slf4jLogger("utils.feign.library"))
                .logLevel(settings.logLevel)
                .target(target)
    }

}