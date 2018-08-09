package library.integration.slack.services.error.handling

import feign.FeignException
import feign.Response
import feign.codec.ErrorDecoder

/**
 * Custom implementation of Feign Error Decoder.
 */
class SlackErrorDecoder : ErrorDecoder {

    override fun decode(methodKey: String, response: Response): Exception {
        val statusCode: Int = response.status()

        return when {
            statusCode == 400 -> SlackChannelNotFoundException(response.status(), response.reason())
            statusCode == 403 -> SlackChannelProhibitedException(response.status(), response.reason())
            statusCode == 404 -> SlackInvalidPayloadException(response.status(), response.reason())
            statusCode == 410 -> SlackChannelArchivedException(response.status(), response.reason())
            statusCode in 500..599 -> SlackServerException(response.status(), response.reason())
            else -> FeignException.errorStatus(methodKey, response)
        }
    }
}


data class SlackChannelNotFoundException(val status: Int, val reason: String) : RuntimeException()

data class SlackChannelProhibitedException(val status: Int, val reason: String) : RuntimeException()

data class SlackInvalidPayloadException(val status: Int, val reason: String) : RuntimeException()

data class SlackChannelArchivedException(val status: Int, val reason: String) : RuntimeException()

data class SlackServerException(val status: Int, val reason: String) : RuntimeException()

