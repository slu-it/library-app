package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.*
import library.enrichment.core.BookAddedEvent
import library.enrichment.core.BookEventHandler
import library.enrichment.correlation.CorrelationIdHolder
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers


internal class BookAddedMessageListenerTest {

    val correlationIdHolder: CorrelationIdHolder = mock()
    val objectMapper: ObjectMapper = ObjectMapper().apply { findAndRegisterModules() }
    val handler: BookEventHandler = mock()

    val cut = BookAddedMessageListener(correlationIdHolder, objectMapper, handler)

    val event = BookAddedEvent(
            id = "event-id",
            bookId = "book-id",
            isbn = "1234567890123"
    )

    @Test fun `event messages are processed by delegating them to the handler`() {
        val message = toMessage(event)
        cut.onMessage(message)
        verify(handler).handle(event)
    }

    @Nested inner class `correlation ids` {

        @Test fun `if the message has no correlation id one will be generated`() {
            val message = toMessage(event, null)

            cut.onMessage(message)

            verify(correlationIdHolder).set(check {
                assertThat(it).isNotBlank()
            })
        }

        @Test fun `if the message has a correlation id it will be used`() {
            val message = toMessage(event, "correlation-id")
            cut.onMessage(message)
            verify(correlationIdHolder).set("correlation-id")
        }

    }

    @RecordLoggers(BookAddedMessageListener::class)
    @Test fun `any exception during message handling is logged`(log: LogRecord) {
        val message = toMessage(event, "correlation-id")
        given { handler.handle(event) } willThrow { RuntimeException() }
        cut.onMessage(message)
        assertThat(log.messages)
                .containsOnly("could not process message [correlation-id] because of an exception")
    }

    private fun toMessage(event: BookAddedEvent, correlationId: String? = null): Message {
        val body = objectMapper.writeValueAsBytes(event)
        val properties = MessageProperties()
        properties.correlationId = correlationId
        return Message(body, properties)
    }

}