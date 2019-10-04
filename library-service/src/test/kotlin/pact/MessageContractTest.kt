package pact

import au.com.dius.pact.provider.PactVerifyProvider
import au.com.dius.pact.provider.junit.Provider
import au.com.dius.pact.provider.junit.VerificationReports
import au.com.dius.pact.provider.junit.loader.PactFolder
import au.com.dius.pact.provider.junit5.AmpqTestTarget
import au.com.dius.pact.provider.junit5.PactVerificationContext
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider
import com.fasterxml.jackson.databind.ObjectMapper
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.types.BookId
import library.service.messaging.MessagingConfiguration
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestTemplate
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.core.MessageProperties
import utils.Books
import utils.classification.ContractTest
import java.time.OffsetDateTime
import java.util.*

@ContractTest
@Provider("library-service")
@PactFolder("src/test/pacts/message")
@VerificationReports("console")
@TestInstance(PER_CLASS) // PACT needs this ... for some reason ...
class MessageContractTest {

    val configuration = MessagingConfiguration()
    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }
    val messageConverter = configuration.messageConverter(objectMapper)

    @BeforeEach
    fun setTarget(context: PactVerificationContext) {
        context.target = AmpqTestTarget(listOf("pact"))
    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider::class)
    fun pactVerificationTestTemplate(context: PactVerificationContext) {
        context.verifyInteraction()
    }

    @PactVerifyProvider("'The Martian' was added event")
    fun `verify 'The Martian' was added event`(): String {
        val event = BookAdded(
                id = UUID.randomUUID(),
                timestamp = OffsetDateTime.now(),
                bookRecord = BookRecord(BookId.generate(), Books.THE_MARTIAN)
        )
        val message = messageConverter.toMessage(event, MessageProperties())
        return String(message.body)
    }

}