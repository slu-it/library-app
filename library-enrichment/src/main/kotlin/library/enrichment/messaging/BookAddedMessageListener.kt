package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.enrichment.core.BookAddedEvent
import library.enrichment.core.BookEventProcessor
import library.enrichment.correlation.CorrelationId
import library.enrichment.correlation.CorrelationIdHolder
import library.enrichment.logging.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.stereotype.Component

@Component
class BookAddedMessageListener(
        private val objectMapper: ObjectMapper,
        private val correlationIdHolder: CorrelationIdHolder,
        private val processor: BookEventProcessor
) : MessageListener {

    private val log = BookAddedMessageListener::class.logger

    override fun onMessage(message: Message) {
        setCorrelationId(message)
        tryToReadEvent(message)?.let {
            processor.bookWasAdded(it)
        }
    }

    private fun setCorrelationId(message: Message) {
        val correlationId = message.messageProperties.correlationId ?: CorrelationId.generate()
        correlationIdHolder.set(correlationId)
    }

    private fun tryToReadEvent(message: Message): BookAddedEvent? {
        try {
            return objectMapper.readValue(message.body, BookAddedEvent::class.java)
        } catch (e: Exception) {
            log.warn("received malformed 'book-added' message: {}", message, e)
        }
        return null
    }

}