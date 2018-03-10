package library.enrichment.correlation

import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.verify
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import utils.classification.UnitTest


@UnitTest
internal class CorrelationIdMessageReceivedPostProcessorTest {

    val correlationId = spy(CorrelationId())
    val cut = CorrelationIdMessageReceivedPostProcessor(correlationId)

    @Test fun `if the message has no correlation id one will be generated`() {
        val message = message(null)
        cut.postProcessMessage(message)
        verify(correlationId).setOrGenerate(null)
    }

    @Test fun `if the message has a correlation id it will be used`() {
        val message = message("correlation-id")
        cut.postProcessMessage(message)
        verify(correlationId).setOrGenerate("correlation-id")
    }

    private fun message(correlationId: String?): Message {
        val properties = MessageProperties()
        properties.correlationId = correlationId
        return Message("".toByteArray(), properties)
    }

}