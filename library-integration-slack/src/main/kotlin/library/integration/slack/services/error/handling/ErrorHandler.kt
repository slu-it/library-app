package library.integration.slack.services.error.handling

import mu.KotlinLogging
import org.springframework.stereotype.Component

@Component
class ErrorHandler {

    private val log = KotlinLogging.logger {}

    /**
     * Handles all errors related to Slack Service integration by logging detailed reason and status for the error.
     */
    fun handleSlackServiceErrors(e: Exception, slackMessage: String) {
        when (e) {
            is SlackInvalidPayloadException -> log.error(e) { setLogMessage(e.status, e.reason, slackMessage) }
            is SlackChannelProhibitedException -> log.error(e) { setLogMessage(e.status, e.reason, slackMessage) }
            is SlackChannelNotFoundException -> log.error(e) { setLogMessage(e.status, e.reason, slackMessage) }
            is SlackChannelArchivedException -> log.error(e) { setLogMessage(e.status, e.reason, slackMessage) }
            is SlackServerException -> log.error(e) { setLogMessage(e.status, e.reason, slackMessage) }
            else -> log.error(e) { "Unexpected error occurred  when trying to post message with body [$slackMessage]." }
        }
    }

    private fun setLogMessage(status: Int, reason: String, slackMessage: String): String {
        return "Error with statusCode [${status}] and reason [${reason}] " +
                "when trying to post message with body [$slackMessage]."

    }
}