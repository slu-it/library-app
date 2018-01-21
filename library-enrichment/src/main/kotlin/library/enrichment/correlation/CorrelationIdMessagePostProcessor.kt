package library.enrichment.correlation

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.stereotype.Component

@Component
class CorrelationIdMessagePostProcessor(
        private val correlationIdHolder: CorrelationIdHolder
) : MessagePostProcessor {

    override fun postProcessMessage(message: Message): Message {
        val correlationId = message.messageProperties.correlationId
                ?: CorrelationId.generate()
        correlationIdHolder.set(correlationId)
        return message
    }

}