package library.enrichment.messaging

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import com.nhaarman.mockito_kotlin.willThrow
import library.enrichment.core.BookAddedEvent
import library.enrichment.core.BookAddedEventHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.classification.UnitTest
import utils.testObjectMapper


@UnitTest
internal class BookAddedEventMessageListenerTest {

    val objectMapper = testObjectMapper()
    val handler: BookAddedEventHandler = mock()

    val cut = BookAddedEventMessageListener(objectMapper, handler)

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

    @RecordLoggers(BookAddedEventMessageListener::class)
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