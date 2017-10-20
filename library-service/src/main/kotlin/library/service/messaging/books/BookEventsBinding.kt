package library.service.messaging.books

import library.service.messaging.Channels
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class BookEventsConfiguration(
        private val queue: BookEventsQueue,
        private val exchange: BookEventsExchange
) {

    @Bean fun bookEventsBinding(): Binding = BindingBuilder
            .bind(queue)
            .to(exchange)
            .with(Channels.BOOK_EVENTS)

}

@Component
class BookEventsQueue : Queue(Channels.BOOK_EVENTS, true)

@Component
class BookEventsExchange : TopicExchange("${Channels.BOOK_EVENTS}-exchange")