package library.enrichment.gateways.openlibrary

import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import library.enrichment.gateways.DynamicUrlTarget
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OpenLibrarySettings::class)
class OpenLibraryConfiguration {

    @Bean fun openLibraryClient(settings: OpenLibrarySettings): OpenLibraryClient {
        val target = DynamicUrlTarget("openlibrary", OpenLibraryClient::class) { settings.url }
        return Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .logger(Slf4jLogger("utils.feign.openlibrary"))
                .logLevel(settings.logLevel)
                .requestInterceptor {
                    it.header("User-Agent", "Mozilla/5.0")
                }
                .target(target)
    }

}