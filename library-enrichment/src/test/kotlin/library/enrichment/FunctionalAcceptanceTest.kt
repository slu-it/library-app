package library.enrichment

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import library.enrichment.core.BookAddedEvent
import library.enrichment.gateways.library.LibraryClient
import library.enrichment.gateways.library.UpdateAuthors
import library.enrichment.gateways.library.UpdateNumberOfPages
import library.enrichment.gateways.openlibrary.OpenLibraryClient
import library.enrichment.messaging.ProcessedMessagesCounter
import org.awaitility.Awaitility.await
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import utils.classification.AcceptanceTest
import utils.extensions.RabbitMqExtension
import utils.readFile
import java.util.concurrent.TimeUnit.SECONDS


@AcceptanceTest
@ExtendWith(RabbitMqExtension::class, SpringExtension::class)
@SpringBootTest(
        webEnvironment = NONE,
        properties = ["spring.rabbitmq.port=\${RABBITMQ_PORT}"]
)
@ActiveProfiles("test", "unsecured")
internal class FunctionalAcceptanceTest {

    companion object {
        const val routingKey = "book-added"
        const val id = "dda05852-f6fe-4ba6-9ce2-6f3a73c664a9"
        const val bookId = "175c5a7e-dd91-4d42-8c0d-6a97d8755231"
    }

    @Autowired lateinit var exchange: TopicExchange
    @Autowired lateinit var connectionFactory: ConnectionFactory
    @Autowired lateinit var objectMapper: ObjectMapper
    @Autowired lateinit var messagesCounter: ProcessedMessagesCounter

    @MockBean lateinit var openLibraryClient: OpenLibraryClient
    @MockBean lateinit var libraryClient: LibraryClient

    @Test fun `book added events are processed correctly`() {
        given { openLibraryClient.searchBooks("9780261102354") } willReturn {
            readFile("openlibrary/responses/200_isbn_9780261102354.json").toJson()
        }

        send(asMessage(BookAddedEvent(
                id = id,
                bookId = bookId,
                isbn = "9780261102354"
        )))

        await("message being processed")
                .atMost(30, SECONDS)
                .until { messagesCounter.total > 0 }

        verify(libraryClient).updateAuthors(bookId, UpdateAuthors(listOf("J. R. R. Tolkien")))
        verify(libraryClient).updateNumberOfPages(bookId, UpdateNumberOfPages(576))
    }

    fun String.toJson(): JsonNode = objectMapper.readTree(this)
    fun asMessage(event: BookAddedEvent) = Message(objectMapper.writeValueAsBytes(event), MessageProperties())
    fun send(message: Message) = RabbitTemplate(connectionFactory).send(exchange.name, routingKey, message)

}