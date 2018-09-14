package library.integration.slack.services.error.handling

import feign.FeignException
import feign.Response
import feign.codec.ErrorDecoder
import org.springframework.http.HttpStatus.*

/**
 * Custom implementation of Feign Error Decoder.
 */
class SlackErrorDecoder : ErrorDecoder {

    override fun decode(methodKey: String, response: Response): Exception {
        val statusCode: Int = response.status()

        return when {
            statusCode == BAD_REQUEST.value() -> SlackInvalidPayloadException(response.status())
            statusCode == FORBIDDEN.value() -> SlackChannelProhibitedException(response.status())
            statusCode == NOT_FOUND.value() -> SlackChannelNotFoundException(response.status())
            statusCode == GONE.value() -> SlackChannelArchivedException(response.status())
            statusCode in INTERNAL_SERVER_ERROR.value()..599 -> SlackServerException(response.status())
            else -> FeignException.errorStatus(methodKey, response)
        }
    }
}

data class SlackInvalidPayloadException(val status: Int, val reason: String = "invalid_payload") : RuntimeException()

data class SlackChannelProhibitedException(val status: Int, val reason: String = "action_prohibited") :
    RuntimeException()

data class SlackChannelNotFoundException(val status: Int, val reason: String = "channel_not_found") : RuntimeException()

data class SlackChannelArchivedException(val status: Int, val reason: String = "channel_is_archived") :
    RuntimeException()

data class SlackServerException(val status: Int, val reason: String = "rollup_error") : RuntimeException()

