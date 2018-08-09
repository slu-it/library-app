package library.integration.slack.services.slack

import feign.Headers
import feign.RequestLine
import library.integration.slack.services.slack.SlackMessage
import org.springframework.stereotype.Component

/**
 * Handles integration with Slack API with regards to message posting on a channel.
 */
@Headers("Content-type: application/json")
@Component
interface SlackMessageClient {

    @RequestLine("POST")
    fun postMessage(slackMessage: SlackMessage)
}
