package utils.services.error.handling

import library.integration.slack.services.error.handling.*

class ErrorHandlerDataProvider {

    companion object {
        val slackChannelNotFoundException = SlackChannelNotFoundException(404)

        val slackChannelProhibitedException = SlackChannelProhibitedException(403)

        val slackInvalidPayloadException = SlackInvalidPayloadException(400)

        val slackChannelArchivedException = SlackChannelArchivedException(410)

        val slackServerException = SlackServerException(500)
    }
}