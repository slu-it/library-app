package library.service.messaging.books

import org.springframework.amqp.core.TopicExchange
import org.springframework.stereotype.Component

@Component
class BookEventsExchange : TopicExchange("book-events")