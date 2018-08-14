package library.integration.slack.services.slack

import library.integration.slack.core.Slack
import library.integration.slack.services.error.handling.*
import mu.KotlinLogging.logger
import org.springframework.stereotype.Service

@Service
class SlackMessageAccessor(
        private val slackMessageClient: SlackMessageClient,
        private val errorHandler: ErrorHandler)
    : Slack {

    private val log = logger {}

    /**
     * Posts message to Slack. In case of error, it forwards the error to the Error Handler.
     */
    override fun postMessage(slackMessage: String) {
        try {
            slackMessageClient.postMessage(SlackMessage(slackMessage))
            log.debug { "Message with body [$slackMessage] has been send successful." }
        } catch (e: Exception) {
            errorHandler.handleSlackServiceErrors(e, slackMessage)
        }
    }
}