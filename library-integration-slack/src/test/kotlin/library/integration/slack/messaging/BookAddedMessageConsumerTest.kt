package library.integration.slack.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.*
import library.integration.slack.core.BookAddedEvent
import library.integration.slack.core.BookAddedEventHandler
import utils.classification.UnitTest
import utils.objectMapper
import org.junit.jupiter.api.Test
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.messaging.toMessageConverter
import org.assertj.core.api.Assertions.assertThat
import java.io.IOException

@UnitTest
class BookAddedMessageConsumerTest {
    val bookAddedEventHandler: BookAddedEventHandler = mock()

    val objectMapper: ObjectMapper = spy(objectMapper())

    val cut = BookAddedMessageConsumer(objectMapper, bookAddedEventHandler)

    val bookAddedEvent = BookAddedEvent(
            bookId = "book-id-test",
            id = "event-id-test",
            isbn = "9780132350884"
    )

    private val expectedLog: String = "Message could not be processed because of an exception";

    @Test
    fun `when a message for book-added is received, then the message is processed by book added handler`() {
        val message = toMessageConverter(bookAddedEvent, objectMapper);

        cut.onMessage(message)

        verify(bookAddedEventHandler, times(1)).handleBookAdded(bookAddedEvent)
    }

    @RecordLoggers(BookAddedMessageConsumer::class)
    @Test
    fun `when an exception during handleBookAdded occurs, then corresponding log will be provided`(log: LogRecord) {
        val message = toMessageConverter(bookAddedEvent, objectMapper);

        whenever(bookAddedEventHandler.handleBookAdded(bookAddedEvent)).thenThrow(RuntimeException())

        cut.onMessage(message)

        assertThat(log.messages).containsOnly(expectedLog)
    }

    @RecordLoggers(BookAddedMessageConsumer::class)
    @Test
    fun `when an exception during readValue occurs, then corresponding log will be provided`(log: LogRecord) {
        val message = toMessageConverter(bookAddedEvent, objectMapper);

        whenever(objectMapper.readValue(message.body, BookAddedEvent::class.java)).thenThrow(IOException())

        cut.onMessage(message)

        assertThat(log.messages).containsOnly(expectedLog)
        verify(bookAddedEventHandler, times(0)).handleBookAdded(bookAddedEvent)
    }
}