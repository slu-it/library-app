package library.enrichment

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.given
import com.nhaarman.mockitokotlin2.timeout
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.willReturn
import library.enrichment.core.BookAddedEvent
import library.enrichment.gateways.library.grpc.LibraryClient
import library.enrichment.gateways.openlibrary.OpenLibraryClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.core.TopicExchange
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

@AcceptanceTest
@ExtendWith(RabbitMqExtension::class, SpringExtension::class)
@SpringBootTest(
    webEnvironment = NONE,
    properties = ["spring.rabbitmq.port=\${RABBITMQ_PORT}"]
)
@ActiveProfiles("test", "unsecured")
internal class FunctionalAcceptanceTest {

    companion object {
        const val TIMEOUT = 5000L
        const val routingKey = "book-added"
        const val id = "dda05852-f6fe-4ba6-9ce2-6f3a73c664a9"
        const val bookId = "175c5a7e-dd91-4d42-8c0d-6a97d8755231"
    }

    @Autowired
    lateinit var exchange: TopicExchange

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var rabbitTemplate: RabbitTemplate

    @MockBean
    lateinit var openLibraryClient: OpenLibraryClient

    @MockBean
    lateinit var libraryClient: LibraryClient

    @Test
    fun `book added events are processed correctly`() {
        given { openLibraryClient.searchBooks("9780261102354") } willReturn {
            readFile("openlibrary/responses/200_isbn_9780261102354.json").toJson()
        }

        send(
            BookAddedEvent(
                id = id,
                bookId = bookId,
                isbn = "9780261102354"
            )
        )

        verify(libraryClient, timeout(TIMEOUT))
            .updateAuthors(bookId, listOf("J. R. R. Tolkien"))
        verify(libraryClient, timeout(TIMEOUT))
            .updateNumberOfPages(bookId, 576)
    }

    fun String.toJson(): JsonNode = objectMapper.readTree(this)
    fun send(event: BookAddedEvent) = rabbitTemplate.convertAndSend(exchange.name, routingKey, event)

}