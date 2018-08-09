package library.integration.slack.core

/**
 * [Slack] offers methods for interacting with the Slack API.
 *
 * @see postMessage
 */
interface Slack {
    /**
     * Posts a message to a predefined slack channel and webhook url. Max length of the message:
     * Empty messages are not allowed and an error will be thrown.
     */
    fun postMessage(slackMessage: String)
}