package library.integration.slack.messaging

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.model.PactSpecVersion
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import library.integration.slack.core.BookAddedEventHandler
import library.integration.slack.core.Slack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.ContextConfiguration
import utils.classification.ContractTest
import utils.extensions.EnableSpringExtension
import utils.messaging.BookEventsMessageProvider.Companion.bookAddedEvent
import utils.messaging.toMessageConverter
import utils.objectMapper

@SpringBootTest
@ContractTest
@EnableSpringExtension
@ContextConfiguration(classes = [BookAddedMessageContractTest.TestConfiguration::class])
class BookAddedMessageContractTest {

    @ComponentScan(basePackageClasses = [BookAddedMessageConsumer::class, BookAddedEventHandler::class])
    class TestConfiguration {
        @Bean
        fun objectMapperCustom() = objectMapper()

        @Bean
        fun slack(): Slack = mock()

        @Bean
        fun connectionFactory(): ConnectionFactory = mock()
    }

    companion object {
        private const val CONSUMER = "library-integration-slack"

        private const val PROVIDER = "library-service"

        private const val PACT_DIR = "build/pacts"
    }

    @MockBean
    lateinit var bookAddedEventHandler: BookAddedEventHandler

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var bookAddedMessageConsumer: BookAddedMessageConsumer


    @Test
    fun `book added event contract test`() {

        val pact = MessagePactBuilder
                .consumer(CONSUMER)
                .hasPactWith(PROVIDER)
                .given("new book has been added to the library")
                .expectsToReceive("event for book-added is received")
                .withContent(
                        PactDslJsonBody()
                                .stringType("isbn", bookAddedEvent.isbn)
                                .stringType("title", bookAddedEvent.title))
                .toPact()

        pact.messages.forEach {
            val message = toMessageConverter(bookAddedEvent, objectMapper);
            bookAddedMessageConsumer.onMessage(message)
        }

        verify(bookAddedEventHandler).handleBookAdded(check {
            assertThat(it.isbn).isEqualTo(bookAddedEvent.isbn)
            assertThat(it.title).isEqualTo(bookAddedEvent.title)
        })

        pact.write(PACT_DIR, PactSpecVersion.V3)
    }
}