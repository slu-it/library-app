package library.service.messaging

import library.service.business.books.domain.events.BookEvent
import library.service.business.events.EventDispatcher
import library.service.correlation.CorrelationIdMessagePostProcessor
import library.service.logging.logger
import library.service.messaging.MessagingConfiguration.BookEventsExchange
import library.service.metrics.DomainEventSendCounter
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

@Component
class MessagingBookEventDispatcher(
        private val rabbitTemplate: RabbitTemplate,
        private val exchange: BookEventsExchange,
        private val postProcessor: CorrelationIdMessagePostProcessor,
        private val eventCounter: DomainEventSendCounter
) : EventDispatcher<BookEvent> {

    private val log = MessagingBookEventDispatcher::class.logger

    override fun dispatch(event: BookEvent) {
        log.debug("dispatching event [{}] to exchange [{}]", event, exchange.name)
        rabbitTemplate.convertAndSend(exchange.name, event.type, event, postProcessor)
        eventCounter.increment(event)
    }

}