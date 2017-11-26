package library.enrichment.library

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("library")
class LibrarySettings {
    lateinit var url: String
    lateinit var logLevel: Logger.Level
}