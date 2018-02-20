package pact

import com.fasterxml.jackson.databind.ObjectMapper
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.types.BookId
import library.service.messaging.MessagingConfiguration
import org.junit.jupiter.api.TestFactory
import org.springframework.amqp.core.MessageProperties
import org.testit.pact.provider.junit.PactFileLoader
import org.testit.pact.provider.junit.message.ComparableMessage
import org.testit.pact.provider.junit.message.MessagePactTestFactory
import org.testit.pact.provider.junit.message.MessageProducer
import utils.Books
import utils.classification.ContractTest
import java.time.OffsetDateTime
import java.util.*

@ContractTest
class MessageContractTest {

    val configuration = MessagingConfiguration()
    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }
    val messageConverter = configuration.messageConverter(objectMapper)

    val testFactory = MessagePactTestFactory(PactFileLoader("src/test/pacts/message"), "library-service")

    @TestFactory fun `library-enrichment consumer contract tests`() =
            testFactory.createTests("library-enrichment", this)

    @MessageProducer("'The Martian' was added event")
    fun verifyTheMartianWasAddedEvent(): ComparableMessage {
        val event = BookAdded(
                id = UUID.randomUUID(),
                bookId = BookId.generate(),
                isbn = Books.THE_MARTIAN.isbn,
                timestamp = OffsetDateTime.now()
        )
        val message = messageConverter.toMessage(event, MessageProperties())
        return ComparableMessage(message.body)
    }

}