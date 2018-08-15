package pact

import com.fasterxml.jackson.databind.ObjectMapper
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.types.BookId
import library.service.messaging.MessagingConfiguration
import org.junit.jupiter.api.TestFactory
import org.springframework.amqp.core.MessageProperties
import org.testit.pact.provider.junit.PactTestFactory
import org.testit.pact.provider.message.ActualMessage
import org.testit.pact.provider.message.MessagePacts
import org.testit.pact.provider.message.MessageProducer
import org.testit.pact.provider.sources.LocalFiles
import utils.Books
import utils.classification.ContractTest
import java.time.OffsetDateTime
import java.util.*

@ContractTest
class MessageContractTest {

    val configuration = MessagingConfiguration()
    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }
    val messageConverter = configuration.messageConverter(objectMapper)

    val pacts = MessagePacts(LocalFiles("src/test/pacts/message"), "library-service")

    @TestFactory fun `library-enrichment consumer contract tests`() =
            PactTestFactory.createTests(pacts, "library-enrichment", this)

    @MessageProducer("'The Martian' was added event")
    fun `verify The Martian was added event`(): ActualMessage {
        val event = BookAdded(
                id = UUID.randomUUID(),
                timestamp = OffsetDateTime.now(),
                bookRecord = BookRecord(BookId.generate(), Books.THE_MARTIAN)
        )
        val message = messageConverter.toMessage(event, MessageProperties())
        return ActualMessage(message.body)
    }

}