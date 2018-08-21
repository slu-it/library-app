package library.integration.slack.services.slack

import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*
import library.integration.slack.services.error.handling.ErrorHandler
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.HttpStatus.*
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.IntegrationTest

@ExtendWith(SpringExtension::class)
@SpringBootTest
@IntegrationTest
@AutoConfigureWireMock(port = 9999)
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

    val slackMessage = "some msg"

    @BeforeEach
    fun setUp() {
        slackSettings.baseUrl = "http://localhost:9999"
    }

    @RecordLoggers(SlackMessageAccessor::class)
    @Test
    fun `posting correct message to slack channel`(log: LogRecord) {
        WireMock.stubFor(post(urlPathEqualTo(slackSettings.channelWebhook))
                .withHeader("Content-type", equalTo("application/json"))
                .withRequestBody(equalToJson("""{"text": "some msg"}"""))
                .willReturn(aResponse().withStatus(OK.value())))

        slackMessageAccessor.postMessage(slackMessage)

        assertThat(log.messages).containsOnly("Message with body [$slackMessage] has been send successful.")
    }

    @RecordLoggers(ErrorHandler::class)
    @Test
    fun `posting correct message to archived slack channel`(log: LogRecord) {
        WireMock.stubFor(post(urlPathEqualTo(slackSettings.channelWebhook))
                .withHeader("Content-type", equalTo("application/json"))
                .withRequestBody(equalToJson("""{"text": "some msg"}"""))
                .willReturn(aResponse().withStatus(GONE.value()).withBody("channel_is_archived")))

        slackMessageAccessor.postMessage(slackMessage)

        assertThat(log.messages).containsOnly(
                "Error with statusCode [${GONE.value()}] and reason [channel_is_archived] " +
                        "when trying to post message with body [$slackMessage]."
        )
    }
}