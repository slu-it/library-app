package library.enrichment.messaging.books

import com.fasterxml.jackson.databind.ObjectMapper
import library.enrichment.common.logging.logger
import library.enrichment.core.BookEventProcessor
import library.enrichment.core.events.BookAddedEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.stereotype.Component

@Component
class BookAddedEventMessageListener(
        private val objectMapper: ObjectMapper,
        private val eventProcessor: BookEventProcessor
) : MessageListener {

    private val bookAddedEventName = BookAddedEvent::class.simpleName
    private val log = BookAddedEventMessageListener::class.logger()

    override fun onMessage(message: Message) {
        tryToReadEvent(message)?.let {
            eventProcessor.bookWasAdded(it)
        }
    }

    private fun tryToReadEvent(message: Message): BookAddedEvent? {
        try {
            return objectMapper.readValue(message.body, BookAddedEvent::class.java)
        } catch (e: Exception) {
            log.warn("received malformed {} message: {}", bookAddedEventName, message)
        }
        return null
    }

}