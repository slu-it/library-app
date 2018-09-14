package library.integration.slack.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.integration.slack.core.BookAddedEvent
import library.integration.slack.core.BookAddedEventHandler
import mu.KotlinLogging.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.stereotype.Component

/**
 * The Consumer class for processing messages with routing key "book-added". Logs the error in case processing is not
 * possible. Does not implement a retry strategy for further error handling.
 */
@Component
class BookAddedMessageConsumer(
    private val objectMapper: ObjectMapper,
    private val bookAddedEventHandler: BookAddedEventHandler
) : MessageListener {
    private val log = logger {}

    override fun onMessage(message: Message) {
        try {
            val bookAddedEvent = objectMapper.readValue(message.body, BookAddedEvent::class.java)
            bookAddedEventHandler.handleBookAdded(bookAddedEvent)
        } catch (e: Exception) {
            log.error(e) {
                "Message could not be processed because of an exception"
            }
        }
    }
}


