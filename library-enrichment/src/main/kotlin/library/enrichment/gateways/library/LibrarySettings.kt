package library.enrichment.gateways.library

import feign.Logger.Level
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("library")
class LibrarySettings {
    /** The base URL of the main library service. */
    lateinit var url: String
    /** The log [Level] to use when logging requests and responses. */
    lateinit var logLevel: Level
    /** The username to use when communication with the main library service. */
    lateinit var username: String
    /** The password to use when communication with the main library service. */
    lateinit var password: String
}