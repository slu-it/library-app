package pact

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.PactRunner
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit.target.AmqpTarget
import au.com.dius.pact.provider.junit.target.Target
import au.com.dius.pact.provider.junit.target.TestTarget
import com.fasterxml.jackson.databind.ObjectMapper
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.types.BookId
import library.service.messaging.MessagingConfiguration
import org.junit.runner.RunWith
import org.springframework.amqp.core.MessageProperties
import utils.Books
import utils.classification.ContractTest
import java.time.OffsetDateTime
import java.util.*

@ContractTest
@RunWith(PactRunner::class)
@Provider("library-service")
@PactFolder("src/test/pacts/message")
@VerificationReports("console")
class MessageContractTest {

    val configuration = MessagingConfiguration()
    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }
    val messageConverter = configuration.messageConverter(objectMapper)

    @JvmField
    @TestTarget
    var target: Target = AmqpTarget(listOf(javaClass.name + ".*"))

    @PactVerifyProvider("'The Martian' was added event")
    fun verifyTheMartianWasAddedEvent(): String {
        val event = BookAdded(
                id = UUID.randomUUID(),
                bookId = BookId.generate(),
                isbn = Books.THE_MARTIAN.isbn,
                timestamp = OffsetDateTime.now()
        )
        val message = messageConverter.toMessage(event, MessageProperties())
        return String(message.body)
    }

}