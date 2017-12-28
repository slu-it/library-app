package library.service.messaging

import library.service.business.books.domain.events.BookEvent
import library.service.business.events.EventDispatcher
import library.service.common.logging.logger
import library.service.messaging.MessagingConfiguration.BookEventsExchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class MessagingBookEventDispatcher(
        private val rabbitTemplate: RabbitTemplate,
        private val exchange: BookEventsExchange
) : EventDispatcher<BookEvent> {

    private val log = MessagingBookEventDispatcher::class.logger()

    override fun dispatch(event: BookEvent) {
        log.debug("dispatching event [{}] to exchange [{}]", event, exchange.name)
        rabbitTemplate.convertAndSend(exchange.name, event.type, event)
    }

}