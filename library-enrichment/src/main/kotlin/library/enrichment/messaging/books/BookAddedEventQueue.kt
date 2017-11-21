package library.enrichment.messaging.books

import org.springframework.amqp.core.Queue
import org.springframework.stereotype.Component

@Component
class BookAddedEventQueue : Queue("library-enrichment.book-events.book-added", true)