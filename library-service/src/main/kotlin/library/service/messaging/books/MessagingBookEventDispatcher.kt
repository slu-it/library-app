package library.service.messaging.books

import library.service.business.books.BookEventDispatcher
import library.service.business.books.domain.events.BookEvent
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessagingBookEventDispatcher(
        private val rabbitTemplate: RabbitTemplate,
        private val exchange: BookEventsExchange
) : BookEventDispatcher {

    private val log: Logger = LoggerFactory.getLogger(javaClass)

    override fun dispatch(event: BookEvent) {
        log.debug("dispatching event [{}] to exchange [{}]", event, exchange.name)
        rabbitTemplate.convertAndSend(exchange.name, event.type, event)
    }

}