package library.service.messaging.books

import com.fasterxml.jackson.databind.ObjectMapper
import library.service.business.books.BookEventDispatcher
import library.service.business.books.domain.events.BookEvent
import library.service.messaging.Channels
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessagingBookEventDispatcher(
        private val rabbitTemplate: RabbitTemplate,
        private val objectMapper: ObjectMapper
) : BookEventDispatcher {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun dispatch(event: BookEvent) {
        val json = objectMapper.writeValueAsString(event)
        log.debug("dispatching {} as JSON message [{}]", event, json)
        rabbitTemplate.convertAndSend(Channels.BOOK_EVENTS, json)
    }

}