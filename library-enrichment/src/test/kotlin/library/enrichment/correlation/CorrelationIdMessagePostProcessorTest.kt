package library.enrichment.correlation

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import utils.classification.UnitTest


@UnitTest
internal class CorrelationIdMessagePostProcessorTest {

    val correlationIdHolder: CorrelationIdHolder = mock()
    val cut = CorrelationIdMessagePostProcessor(correlationIdHolder)

    @Test fun `if the message has no correlation id one will be generated`() {
        val message = message(null)
        cut.postProcessMessage(message)
        verify(correlationIdHolder).set(com.nhaarman.mockito_kotlin.check {
            assertThat(it).isNotBlank()
        })
    }

    @Test fun `if the message has a correlation id it will be used`() {
        val message = message("correlation-id")
        cut.postProcessMessage(message)
        verify(correlationIdHolder).set("correlation-id")
    }

    private fun message(correlationId: String?): Message {
        val properties = MessageProperties()
        properties.correlationId = correlationId
        return Message("".toByteArray(), properties)
    }

}