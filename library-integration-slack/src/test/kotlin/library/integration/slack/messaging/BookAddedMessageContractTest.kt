package library.integration.slack.messaging

import au.com.dius.pact.consumer.MessagePactBuilder
import au.com.dius.pact.consumer.Pact
import au.com.dius.pact.consumer.dsl.PactDslJsonBody
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt
import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.consumer.junit5.ProviderType
import au.com.dius.pact.model.v3.messaging.Message
import au.com.dius.pact.model.v3.messaging.MessagePact
import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.check
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import library.integration.slack.core.BookAddedEventHandler
import library.integration.slack.core.Slack
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.core.MessageProperties
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
import utils.objectMapper

@SpringBootTest
@ContractTest
@EnableSpringExtension
@ExtendWith(PactConsumerTestExt::class)
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


    @Pact(provider = PROVIDER, consumer = CONSUMER)
    fun createPact(builder: MessagePactBuilder): MessagePact {
        return builder
            .given("new book has been added to the library")
            .expectsToReceive("event for book-added is received")
            .withContent(
                PactDslJsonBody()
                    .stringType("isbn", bookAddedEvent.isbn)
                    .stringType("title", bookAddedEvent.title)
            )
            .toPact()
    }

    @Test
    @PactTestFor(pactMethod = "createPact", providerType = ProviderType.ASYNCH)
    fun `book added event contract test`(messages: List<Message>) {
        //gets the message from the pact mock server
        val messageConverted =
            org.springframework.amqp.core.Message(messages.get(0).contentsAsBytes(), MessageProperties())

        bookAddedMessageConsumer.onMessage(messageConverted) // message is sent on the consumer

        verify(bookAddedEventHandler).handleBookAdded(
            check {
                assertThat(it.isbn).isEqualTo(bookAddedEvent.isbn)
                assertThat(it.title).isEqualTo(bookAddedEvent.title)
            }
        )
    }
}