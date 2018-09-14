package library.integration.slack.services.error.handling

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.UnitTest
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelArchivedException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelNotFoundException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelProhibitedException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackInvalidPayloadException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackServerException
import java.io.IOException
import java.util.stream.Stream

@UnitTest
class ErrorHandlerTest {

    private val slackMessage = "some msg"

    private val cut = ErrorHandler()

    @RecordLoggers(ErrorHandler::class)
    @ParameterizedTest
    @MethodSource("createSlackServiceExceptionTestData")
    fun `given SlackService Exception and slackMessage = 'some msg', when handleSlackServiceErrors() is executed, then corresponding log will be provided`(
        e: Exception,
        status: Int,
        reason: String,
        log: LogRecord
    ) {
        val expectedLog = "Error with statusCode [$status] and reason [$reason] " +
                "when trying to post message with body [some msg]."

        cut.handleSlackServiceErrors(e, slackMessage)

        assertThat(log.messages).containsOnly(expectedLog)
    }

    @RecordLoggers(ErrorHandler::class)
    @Test
    fun `given Unexpected Exception and slackMessage = 'some msg', when handleSlackServiceErrors() is executed, then corresponding log will be provided`(
        log: LogRecord
    ) {
        val expectedLog = "Unexpected error occurred  when trying to post message with body [some msg]."
        val ioException: IOException = IOException()

        cut.handleSlackServiceErrors(ioException, slackMessage)

        assertThat(log.messages).containsOnly(expectedLog)
    }

    companion object {

        @JvmStatic
        fun createSlackServiceExceptionTestData(): Stream<Arguments> =
            Stream.of(
                Arguments.of(
                    slackChannelNotFoundException,
                    slackChannelNotFoundException.status,
                    slackChannelNotFoundException.reason
                ),
                Arguments.of(
                    slackChannelProhibitedException,
                    slackChannelProhibitedException.status,
                    slackChannelProhibitedException.reason
                ),
                Arguments.of(
                    slackInvalidPayloadException,
                    slackInvalidPayloadException.status,
                    slackInvalidPayloadException.reason
                ),
                Arguments.of(
                    slackChannelArchivedException,
                    slackChannelArchivedException.status,
                    slackChannelArchivedException.reason
                ),
                Arguments.of(slackServerException, slackServerException.status, slackServerException.reason)
            )
    }

}