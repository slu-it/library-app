package library.enrichment.external.isbndb

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("isbndb")
class IsbnDbSettings {
    lateinit var url: String
    lateinit var apiKey: String
    lateinit var logLevel: Logger.Level
}