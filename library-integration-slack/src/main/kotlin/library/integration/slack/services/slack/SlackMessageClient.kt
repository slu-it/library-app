package library.integration.slack.services.slack

import feign.Headers
import feign.RequestLine
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE

/**
 * Handles integration with Slack API with regards to message posting on a channel.
 */
interface SlackMessageClient {

    @Headers("$CONTENT_TYPE: $APPLICATION_JSON_VALUE")
    @RequestLine("POST")
    fun postMessage(slackMessage: SlackMessage)
}
