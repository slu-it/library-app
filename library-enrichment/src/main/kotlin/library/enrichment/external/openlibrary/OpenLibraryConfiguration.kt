package library.enrichment.external.openlibrary

import feign.Feign
import feign.Logger
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import library.enrichment.common.feign.DynamicUrlTarget
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(OpenLibrarySettings::class)
internal class OpenLibraryConfiguration {

    @Bean fun openLibraryClient(settings: OpenLibrarySettings): OpenLibraryClient {
        val target = DynamicUrlTarget(OpenLibraryClient::class) { settings.url }
        return Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .logger(Slf4jLogger("utils.feign"))
                .logLevel(Logger.Level.FULL)
                .target(target)
    }

}