package contractbase

import library.service.Application
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.events.BookEvent
import library.service.business.books.domain.types.BookId
import library.service.business.events.EventDispatcher
import library.service.messaging.MessagingConfiguration.BookEventsExchange
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.amqp.core.*
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier
import org.springframework.context.annotation.*
import utils.Books
import utils.extensions.MongoDbExtension
import utils.extensions.RabbitMqExtension
import java.time.OffsetDateTime
import java.util.UUID

@AutoConfigureMessageVerifier
@ExtendWith(RabbitMqExtension::class, MongoDbExtension::class)
@SpringBootTest(
    webEnvironment = NONE,
    classes = [Application::class, ContractVerifierConfiguration::class],
    properties = [
        "spring.rabbitmq.port=\${RABBITMQ_PORT}",
        "spring.data.mongodb.port=\${MONGODB_PORT}",
        "stubrunner.amqp.mockConnection=true",
        "stubrunner.amqp.enabled=true"
    ]
)
abstract class AmqpBase {

    @Autowired
    lateinit var dispatcher: EventDispatcher<BookEvent>

    fun publishBookCreatedEvent() {
        dispatcher.dispatch(
            BookAdded(
                id = UUID.fromString("88b88f1a-a76c-4d9b-93a1-d256773fed88"),
                timestamp = OffsetDateTime.parse("2021-07-02T12:34:56Z"),
                bookRecord = BookRecord(
                    id = BookId.from("ebd332fe-86c9-443e-ae5e-3202d4e9af73"),
                    book = Books.CLEAN_CODE
                )
            )
        )
    }
}


@Configuration
//@Import(SpringAmqpStubMessages::class)
//@ImportAutoConfiguration(ContractVerifierAmqpAutoConfiguration::class)
class ContractVerifierConfiguration {

//    @Bean
//    fun contractVerifier(verifier: SpringAmqpStubMessages): ContractVerifierMessaging<Message> =
//        object : ContractVerifierMessaging<Message>(verifier) {
//
//            override fun convert(receive: Message?): ContractVerifierMessage {
//                return ContractVerifierMessage(receive?.body, receive?.messageProperties?.headers)
//            }
//
//        }

    @Primary
    @Bean
    fun jacksonMessageConverter(): MessageConverter = object : Jackson2JsonMessageConverter() {
        override fun fromMessage(message: Message): Any {
            return String(message.body)
        }
    }

    @Bean
    fun bookEventsQueue() = Queue("bookEventsQueue")

    @Bean
    fun binding(exchange: BookEventsExchange): Binding =
        BindingBuilder.bind(bookEventsQueue())
            .to(exchange)
            .with("book-added")
}