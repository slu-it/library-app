package library.integration.slack.services.slack

import library.integration.slack.core.Slack
import library.integration.slack.services.error.handling.*
import mu.KotlinLogging.logger
import org.springframework.stereotype.Component

@Component
class SlackMessageAccessor(
        private val slackMessageClient: SlackMessageClient,
        private val errorHandler: ErrorHandler)
    : Slack {

    private val log = logger {}

    override fun postMessage(slackMessage: String) {
        try {
            slackMessageClient.postMessage(SlackMessage(slackMessage))
            log.debug { "Message with body [$slackMessage] has been send successful." }
        } catch (e: Exception) {
            errorHandler.handleSlackServiceErrors(e, slackMessage)
        }
    }
}