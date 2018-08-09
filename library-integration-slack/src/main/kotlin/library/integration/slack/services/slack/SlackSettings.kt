package library.integration.slack.services.slack

import feign.Logger
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("slack")
class SlackSettings {
    lateinit var baseUrl: String
    lateinit var channelWebhook: String
    lateinit var logLevel: Logger.Level
}