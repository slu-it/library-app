package library.enrichment.gateways.library

import feign.Logger.Level
import feign.Logger.Level.BASIC
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("library")
class LibrarySettings {
    /** The base URL of the main library service. */
    var url: String = "localhost"
    /** The log [Level] to use when logging requests and responses. */
    var logLevel: Level = BASIC
    /** The username to use when communication with the main library service. */
    var username: String = "user"
    /** The password to use when communication with the main library service. */
    var password: String = "password"
}