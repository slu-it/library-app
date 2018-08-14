package library.integration.slack.services.slack

import feign.Headers
import feign.RequestLine

/**
 * Handles integration with Slack API with regards to message posting on a channel.
 */
interface SlackMessageClient {

    @Headers("Content-type: application/json")
    @RequestLine("POST")
    fun postMessage(slackMessage: SlackMessage)
}
