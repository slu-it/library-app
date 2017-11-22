package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.enrichment.common.logging.logger
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageListener
import kotlin.reflect.KClass

class JsonConvertingMessageListener<T : Any>(
        private val objectMapper: ObjectMapper,
        private val payloadType: KClass<T>,
        private val payloadConsumer: (T) -> Unit
) : MessageListener {

    private val log = JsonConvertingMessageListener::class.logger()

    override fun onMessage(message: Message) {
        tryToReadEvent(message)?.let {
            payloadConsumer(it)
        }
    }

    private fun tryToReadEvent(message: Message): T? {
        try {
            return objectMapper.readValue(message.body, payloadType.java)
        } catch (e: Exception) {
            log.warn("received malformed {} message: {}", payloadType.simpleName, message, e)
        }
        return null
    }

}