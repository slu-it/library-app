package library.service.logging

import io.mockk.every
import io.mockk.mockk
import library.service.correlation.CorrelationIdHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.MutableClock
import utils.ResetMocksAfterEachTest
import utils.classification.IntegrationTest
import utils.testapi.TestController
import utils.testapi.TestService

@IntegrationTest
@ResetMocksAfterEachTest
@WebMvcTest(TestController::class, secure = false)
internal class RequestLoggingFilterIntTest(
    @Autowired val clock: MutableClock,
    @Autowired val mockMvc: MockMvc
) {

    @TestConfiguration
    @ComponentScan("utils.testapi")
    class AdditionalBeans {
        @Bean fun correlationIdHolder() = CorrelationIdHolder()
        @Bean fun testService(): TestService = mockk(relaxed = true)
    }

    @BeforeEach fun setTime() {
        clock.setFixedTime("2017-09-01T12:34:56.789Z")
    }

    @RecordLoggers(RequestLoggingFilter::class)
    @Test fun `processing a request generates 2 log entries`(log: LogRecord) = aRequestWillProduceLog(log) { messages ->
        assertThat(messages).hasSize(2)
    }

    @RecordLoggers(RequestLoggingFilter::class)
    @Test fun `log entries are formatted correctly`(log: LogRecord) = aRequestWillProduceLog(log) { messages ->
        assertThat(messages[0]).matches("""Received Request \[(.+?)\]""")
        assertThat(messages[1]).matches("""Processed Request \[(.+?)\]""")
    }

    @RecordLoggers(RequestLoggingFilter::class)
    @Test fun `uri is logged with query strings`(log: LogRecord) = aRequestWillProduceLog(log) { messages ->
        assertThat(messages[0]).contains("uri=/test?foo=bar;")
    }

    @RecordLoggers(RequestLoggingFilter::class)
    @Test fun `available client information is logged`(log: LogRecord) = aRequestWillProduceLog(log) { messages ->
        assertThat(messages[0]).contains("client=127.0.0.1;")
    }

    @RecordLoggers(RequestLoggingFilter::class)
    @Test fun `request headers are logged`(log: LogRecord) = aRequestWillProduceLog(log) { messages ->
        assertThat(messages[0]).contains("headers=[]")
    }

    fun aRequestWillProduceLog(log: LogRecord, body: (List<String>) -> Unit) {
        mockMvc.perform(post("/test?foo=bar"))
        body(log.messages)
    }

}