package library.service.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("cors")
class CorsSettings {
    var origins = ArrayList<String>()
    var methods = ArrayList<String>()

}