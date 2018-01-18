package library.service.messaging

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import library.service.business.books.domain.events.*
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Isbn13
import library.service.correlation.CorrelationIdMessagePostProcessor
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
    val postProcessor = mock<CorrelationIdMessagePostProcessor>()
    val cut = MessagingBookEventDispatcher(rabbitTemplate, exchange, postProcessor)

    val uuid = UUID.randomUUID()!!
    val bookId = BookId.generate()
    val timestamp = OffsetDateTime.now()!!
    val isbn = Isbn13("0123456789123")

    val allBookEventTypes = listOf(
            BookAdded(uuid, bookId, timestamp, isbn),
            BookUpdated(uuid, bookId, timestamp),
            BookRemoved(uuid, bookId, timestamp),
            BookBorrowed(uuid, bookId, timestamp),
            BookReturned(uuid, bookId, timestamp)
    )

    @TestFactory fun `events are send to exchange with their type as the routing key`() = allBookEventTypes.map {
        dynamicTest(it.javaClass.simpleName) {
            cut.dispatch(it)
            verify(rabbitTemplate).convertAndSend(exchange.name, it.type, it, postProcessor)
        }
    }

}