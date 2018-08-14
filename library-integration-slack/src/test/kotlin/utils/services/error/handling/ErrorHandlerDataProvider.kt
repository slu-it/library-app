package utils.services.error.handling

import library.integration.slack.services.error.handling.*

class ErrorHandlerDataProvider {

    companion object {
        val slackChannelNotFoundException = SlackChannelNotFoundException(404, "channel_not_found")

        val slackChannelProhibitedException = SlackChannelProhibitedException(403, "action_prohibited")

        val slackInvalidPayloadException = SlackInvalidPayloadException(400, "invalid_payload")

        val slackChannelArchivedException = SlackChannelArchivedException(410, "channel_is_archived")

        val slackServerException = SlackServerException(500, "rollup_error")
    }
}