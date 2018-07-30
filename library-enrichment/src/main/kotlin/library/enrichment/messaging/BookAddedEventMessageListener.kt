package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.enrichment.core.BookAddedEvent
import library.enrichment.core.BookAddedEventHandler
import mu.KotlinLogging.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.stereotype.Component

/**
 * This [MessageListener] is responsible for handling `book-added` messages.
 *
 * It tries to read the incoming [Message] as a [BookAddedEvent] and invokes
 * the corresponding [BookAddedEventHandler]. If any exceptions occurs during
 * processing of either the [Message] or the [BookAddedEvent], that exception
 * is logged and otherwise ignored. There is no retry or other specialized
 * error handling in place!
 *
 * @see MessageListener
 * @see Message
 * @see BookAddedEvent
 * @see BookAddedEventHandler
 */
@Component
internal class BookAddedEventMessageListener(
        private val objectMapper: ObjectMapper,
        private val handler: BookAddedEventHandler,
        private val messagesCounter: ProcessedMessagesCounter
) : MessageListener {

    private val log = logger {}

    override fun onMessage(message: Message) = try {
        readEventFrom(message) {
            handler.handle(it)
        }
    } catch (e: Exception) {
        val correlationId = message.messageProperties.correlationId
        log.error(e) { "could not process message [$correlationId] because of an exception" }
    } finally {
        messagesCounter.increment()
    }

    private fun readEventFrom(message: Message, eventProcessor: (BookAddedEvent) -> Unit) {
        eventProcessor(objectMapper.readValue(message.body, BookAddedEvent::class.java))
    }

}