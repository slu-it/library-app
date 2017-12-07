package library.service.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("users")
class UserSettings {

    var admin = UserCredentials()
    var curator = UserCredentials()
    var user = UserCredentials()

    class UserCredentials {
        lateinit var username: String
        lateinit var password: String
    }

}