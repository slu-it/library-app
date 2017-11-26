package library.enrichment.datasources.openlibrary

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("datasources.openlibrary")
class OpenLibrarySettings {
    lateinit var url: String
    lateinit var logLevel: Logger.Level
}