package library.service.correlation

import library.service.logging.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessagePostProcessor
import org.springframework.stereotype.Component

@Component
class CorrelationIdMessagePostProcessor(
        private val corrleationIdHolder: CorrelationIdHolder
) : MessagePostProcessor {

    private val log = CorrelationIdMessagePostProcessor::class.logger

    override fun postProcessMessage(message: Message) = message.apply {
        val correlationId = corrleationIdHolder.get() ?: CorrelationId.generate()
        log.debug("setting message correlation ID to [{}]", correlationId)
        messageProperties.correlationId = correlationId
    }

}