package library.enrichment.messaging

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.model.PactSpecVersion
import com.nhaarman.mockito_kotlin.check
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import library.enrichment.core.BookAddedEventHandler
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import utils.classification.UnitTest
import utils.testObjectMapper


@UnitTest
internal class BookAddedMessageContractTest {

    val pactContractFolder = "../library-service/src/test/pacts/message"

    val eventId = "aa1dc09f-7b64-4e7e-a6f6-7eb50dcd6e9d"
    val bookId = "9bf258be-19d4-4338-b172-60a1b7ef076b"

    val objectMapper = testObjectMapper()
    val handler: BookAddedEventHandler = mock()
    val cut = BookAddedMessageListener(objectMapper, handler)

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
            cut.onMessage(Message(it.contentsAsBytes(), MessageProperties().apply {
                contentType = it.contentType
            }))
        }

        verify(handler).handle(check {
            assertThat(it.id).isEqualTo(eventId)
            assertThat(it.bookId).isEqualTo(bookId)
            assertThat(it.isbn).isEqualTo("9780091956141")
        })

        pact.write(pactContractFolder, PactSpecVersion.V3)
    }

}