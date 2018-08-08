package library.integration.slack.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("users")
class UserSettings {
    var admin = UserCredentials()

    class UserCredentials {
        lateinit var username: String
        lateinit var password: String
    }
}