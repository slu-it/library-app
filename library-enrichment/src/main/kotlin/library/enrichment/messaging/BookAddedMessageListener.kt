package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.enrichment.core.BookAddedEvent
import library.enrichment.core.BookEventHandler
import library.enrichment.correlation.CorrelationId
import library.enrichment.correlation.CorrelationIdHolder
import library.enrichment.logging.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import org.springframework.stereotype.Component

@Component
class BookAddedMessageListener(
        private val correlationIdHolder: CorrelationIdHolder,
        private val objectMapper: ObjectMapper,
        private val handler: BookEventHandler
) : MessageListener {

    private val log = BookAddedMessageListener::class.logger

    override fun onMessage(message: Message) = try {
        tryOnMessage(message)
    } catch (e: Exception) {
        val correlationId = message.messageProperties.correlationId
        log.warn("could not process message [$correlationId] because of an exception", e)
    }

    private fun tryOnMessage(message: Message) {
        setCorrelationIdBasedOn(message)
        readEventFrom(message) {
            handler.handle(it)
        }
    }

    private fun setCorrelationIdBasedOn(message: Message) {
        val correlationId = message.messageProperties.correlationId
                ?: CorrelationId.generate()
        correlationIdHolder.set(correlationId)
    }

    private fun readEventFrom(message: Message, eventProcessor: (BookAddedEvent) -> Unit) {
        val event = objectMapper.readValue(message.body, BookAddedEvent::class.java)
        eventProcessor(event)
    }

}