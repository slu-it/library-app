package library.enrichment.gateways.library

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("library")
class LibrarySettings {
    lateinit var url: String
    lateinit var logLevel: Logger.Level
    lateinit var username: String
    lateinit var password: String
}