package library.integration.slack.services.slack

import feign.Feign
import feign.jackson.JacksonDecoder
import feign.jackson.JacksonEncoder
import feign.slf4j.Slf4jLogger
import library.integration.slack.services.error.handling.SlackErrorDecoder
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SlackSettings::class)
class SlackMessageClientConfiguration {

    @Bean
    fun slackMessageClient(slackSettings: SlackSettings): SlackMessageClient {
        val slackMsgPostUrl = slackSettings.baseUrl + slackSettings.channelWebhook

        return Feign
                .builder()
                .encoder(JacksonEncoder())
                .decoder(JacksonDecoder())
                .errorDecoder(SlackErrorDecoder())
                .logger(Slf4jLogger())
                .logLevel(slackSettings.logLevel)
                .target(SlackMessageClient::class.java, slackMsgPostUrl)
    }
}