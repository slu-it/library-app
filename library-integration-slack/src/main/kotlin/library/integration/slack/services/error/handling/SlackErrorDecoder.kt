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
            statusCode == BAD_REQUEST.value() -> SlackInvalidPayloadException(response.status(), response.reason())
            statusCode == FORBIDDEN.value() -> SlackChannelProhibitedException(response.status(), response.reason())
            statusCode == NOT_FOUND.value() -> SlackChannelNotFoundException(response.status(), response.reason())
            statusCode == GONE.value() -> SlackChannelArchivedException(response.status(), response.reason())
            statusCode in INTERNAL_SERVER_ERROR.value()..599 -> SlackServerException(response.status(), response.reason())
            else -> FeignException.errorStatus(methodKey, response)
        }
    }
}

data class SlackInvalidPayloadException(val status: Int, val reason: String) : RuntimeException()

data class SlackChannelProhibitedException(val status: Int, val reason: String) : RuntimeException()

data class SlackChannelNotFoundException(val status: Int, val reason: String) : RuntimeException()

data class SlackChannelArchivedException(val status: Int, val reason: String) : RuntimeException()

data class SlackServerException(val status: Int, val reason: String) : RuntimeException()

