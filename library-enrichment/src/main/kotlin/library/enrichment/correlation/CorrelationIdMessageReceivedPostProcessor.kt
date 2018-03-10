package library.enrichment.correlation

import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.stereotype.Component

@Component
class CorrelationIdMessageReceivedPostProcessor(
        private val correlationId: CorrelationId
) : MessagePostProcessor {

    override fun postProcessMessage(message: Message): Message {
        val correlationId = message.messageProperties.correlationId
        this.correlationId.setOrGenerate(correlationId)
        return message
    }

}