package library.enrichment.messaging

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.model.PactSpecVersion
import au.com.dius.pact.model.v3.messaging.Message
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import library.enrichment.core.BookAddedEvent
import library.enrichment.core.BookAddedEventHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.MessageProperties
import utils.classification.UnitTest
import utils.objectMapper


@UnitTest
internal class MessagingContractTest {

    companion object {
        const val pactContractFolder = "../library-service/src/test/pacts/message"
        const val eventId = "aa1dc09f-7b64-4e7e-a6f6-7eb50dcd6e9d"
        const val bookId = "9bf258be-19d4-4338-b172-60a1b7ef076b"
    }

    val configuration = MessagingConfiguration(mock(), mock())
    val objectMapper = objectMapper()
    val messageConverter = configuration.messageConverter(objectMapper)
    val handler: BookAddedEventHandler = mock()
    val cut = BookAddedEventMessageListener(objectMapper, handler)

    @Test fun `book-added contract`() {
        val pact = MessagePactBuilder
                .consumer("library-enrichment")
                .hasPactWith("library-service")
                .expectsToReceive("'The Martian' was added event")
                .withContent(PactDslJsonBody()
                        .stringType("id", eventId)
                        .stringType("bookId", bookId)
                        .stringValue("isbn", "9780091956141")
                )
                .toPact()

        pact.messages.forEach {
            val message = toMessage(readEvent(it), it.contentType)
            cut.onMessage(message)
        }

        verify(handler).handle(check {
            assertThat(it.id).isEqualTo(eventId)
            assertThat(it.bookId).isEqualTo(bookId)
            assertThat(it.isbn).isEqualTo("9780091956141")
        })

        pact.write(pactContractFolder, PactSpecVersion.V3)
    }

    private fun toMessage(event: BookAddedEvent, contentType: String): org.springframework.amqp.core.Message {
        val properties = MessageProperties()
        properties.contentType = contentType
        properties.consumerQueue = MessagingConfiguration.BOOK_ADDED_QUEUE
        return messageConverter.toMessage(event, properties)
    }

    private fun readEvent(it: Message) =
            objectMapper.readValue(it.contentsAsBytes(), BookAddedEvent::class.java)

}