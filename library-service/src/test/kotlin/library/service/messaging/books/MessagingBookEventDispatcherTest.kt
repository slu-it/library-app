package library.service.messaging.books

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.events.BookBorrowed
import library.service.business.books.domain.events.BookRemoved
import library.service.business.books.domain.events.BookReturned
import library.service.business.books.domain.types.BookId
import library.service.messaging.MessagingBookEventDispatcher
import library.service.messaging.MessagingConfiguration
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import utils.classification.UnitTest
import java.time.OffsetDateTime
import java.util.*

@UnitTest
internal class MessagingBookEventDispatcherTest {

    val rabbitTemplate = mock<RabbitTemplate>()
    val exchange = MessagingConfiguration.BookEventsExchange()

    val cut = MessagingBookEventDispatcher(rabbitTemplate, exchange)

    val uuid = UUID.randomUUID()
    val bookId = BookId.generate()
    val timestamp = OffsetDateTime.now()

    @TestFactory fun `events are send as JSONs`(): List<DynamicTest> {
        val map = mapOf(
                BookAdded(uuid, bookId, timestamp) to "book-added",
                BookRemoved(uuid, bookId, timestamp) to "book-removed",
                BookBorrowed(uuid, bookId, timestamp) to "book-borrowed",
                BookReturned(uuid, bookId, timestamp) to "book-returned"
        )
        return map.map { (event, type) ->
            dynamicTest(event.javaClass.simpleName) {
                cut.dispatch(event)
                verify(rabbitTemplate).convertAndSend(exchange.name, type, event)
            }
        }
    }

}