package library.enrichment.gateways.library

import feign.Feign
import feign.auth.BasicAuthRequestInterceptor
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import library.enrichment.correlation.CorrelationIdRequestInterceptor
import library.enrichment.gateways.DynamicUrlTarget
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(LibrarySettings::class)
class LibraryConfiguration {

    @Bean fun libraryClient(
            settings: LibrarySettings,
            correlationInterceptor: CorrelationIdRequestInterceptor
    ): LibraryClient {
        val target = DynamicUrlTarget("library", LibraryClient::class) { settings.url }
        return Feign.builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .logger(Slf4jLogger(LibraryClient::class.java))
                .logLevel(settings.logLevel)
                .requestInterceptor(correlationInterceptor)
                .requestInterceptor(BasicAuthRequestInterceptor(settings.username, settings.password))
                .target(target)
    }

}