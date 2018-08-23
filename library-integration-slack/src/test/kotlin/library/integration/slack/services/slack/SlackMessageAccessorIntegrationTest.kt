package library.integration.slack.services.slack

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import library.integration.slack.services.error.handling.ErrorHandler
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpStatus.*
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.context.ContextConfiguration
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.IntegrationTest
import utils.extensions.EnableSpringExtension
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelArchivedException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelNotFoundException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackChannelProhibitedException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackInvalidPayloadException
import utils.services.error.handling.ErrorHandlerDataProvider.Companion.slackServerException
import java.util.stream.Stream


@SpringBootTest
@IntegrationTest
@EnableSpringExtension
@AutoConfigureWireMock(port = 0)
@ContextConfiguration(classes = [TestConfiguration::class])
class SlackMessageAccessorIntegrationTest {

    @ComponentScan(basePackageClasses = [SlackMessageAccessor::class, ErrorHandler::class])
    class TestConfiguration

    @Autowired
    lateinit var slackSettings: SlackSettings

    @Autowired
    lateinit var slackMessageAccessor: SlackMessageAccessor

    @Autowired
    lateinit var slackMessageClient: SlackMessageClient

    @Autowired
    lateinit var errorHandler: ErrorHandler

    @Autowired
    lateinit var wireMockServer: WireMockServer

    val slackMessage = "some msg"

    @BeforeEach
    fun setUp() {
        slackSettings.baseUrl = "http://localhost:${wireMockServer.port()}"
    }

    @RecordLoggers(SlackMessageAccessor::class)
    @Test
    fun `posting correct message to slack channel`(log: LogRecord) {
        wireMockServer.givenThat(post(urlPathEqualTo(slackSettings.channelWebhook))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""{"text": "some msg"}"""))
                .willReturn(aResponse().withStatus(OK.value())))

        slackMessageAccessor.postMessage(slackMessage)

        assertThat(log.messages).containsOnly("Message with body [$slackMessage] has been send successful.")
    }

    @RecordLoggers(ErrorHandler::class)
    @ParameterizedTest
    @MethodSource("createSlackMessageAccessorTestData")
    fun `posting correct message to slack channel and getting an error`(status: Int, reason: String, log: LogRecord) {
        wireMockServer.givenThat(post(urlPathEqualTo(slackSettings.channelWebhook))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""{"text": "some msg"}"""))
                .willReturn(aResponse().withStatus(status)))

        slackMessageAccessor.postMessage(slackMessage)

        assertThat(log.messages).containsOnly(
                "Error with statusCode [$status] and reason [$reason] " +
                        "when trying to post message with body [$slackMessage]."
        )
    }

    @RecordLoggers(ErrorHandler::class)
    @Test
    fun `posting correct message to slack channel and getting unexpected error`(log: LogRecord) {
        wireMockServer.givenThat(post(urlPathEqualTo(slackSettings.channelWebhook))
                .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
                .withRequestBody(equalToJson("""{"text": "some msg"}"""))
                .willReturn(aResponse().withStatus(401)))

        slackMessageAccessor.postMessage(slackMessage)

        assertThat(log.messages).containsOnly(
                "Unexpected error occurred  when trying to post message with body [$slackMessage]."
        )
    }

    companion object {
        @JvmStatic
        fun createSlackMessageAccessorTestData(): Stream<Arguments> =
                Stream.of(
                        Arguments.of(slackChannelNotFoundException.status, slackChannelNotFoundException.reason),
                        Arguments.of(slackChannelProhibitedException.status, slackChannelProhibitedException.reason),
                        Arguments.of(slackInvalidPayloadException.status, slackInvalidPayloadException.reason),
                        Arguments.of(slackChannelArchivedException.status, slackChannelArchivedException.reason),
                        Arguments.of(slackServerException.status, slackServerException.reason)
                )
    }
}