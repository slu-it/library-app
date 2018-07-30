package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.enrichment.correlation.CorrelationIdMessageReceivedPostProcessor
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitOperations
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This [Configuration] is responsible for setting up all the things necessary
 * for receiving book domain events as messages via AMQP / RabbitMQ.
 *
 * It is creating an exclusive, durable queue for domain events of type `book-added`
 * and binding it to the general `book-events` topic on the message broker. In order
 * to process these event messages it also creates a [MessageListenerContainer] which
 * uses a [BookAddedEventMessageListener] to handle each incoming message.
 *
 * @see Binding
 * @see TopicExchange
 * @see Queue
 * @see MessageListenerContainer
 * @see BookAddedEventMessageListener
 */
@Configuration
internal class MessagingConfiguration(
        private val connectionFactory: ConnectionFactory,
        private val correlationIdPostProcessor: CorrelationIdMessageReceivedPostProcessor
) {

    companion object {
        const val BOOK_EVENTS_TOPIC = "book-events"
        const val BOOK_ADDED_QUEUE = "library-enrichment.book-events.book-added"
        const val ROUTING_KEY = "book-added"
    }

    @Bean fun messageConverter(objectMapper: ObjectMapper): MessageConverter {
        return Jackson2JsonMessageConverter(objectMapper).also {
            it.classMapper = DefaultJackson2JavaTypeMapper()
        }
    }

    @Bean fun bookEventsExchange() = TopicExchange(BOOK_EVENTS_TOPIC)
    @Bean fun bookAddedEventQueue() = Queue(BOOK_ADDED_QUEUE, true)

    @Bean fun bookAddedEventBinding(): Binding = BindingBuilder
            .bind(bookAddedEventQueue())
            .to(bookEventsExchange())
            .with(ROUTING_KEY)

    @Bean fun messageListenerContainer(listener: BookAddedEventMessageListener): MessageListenerContainer =
            SimpleMessageListenerContainer(connectionFactory)
                    .apply {
                        setQueueNames(BOOK_ADDED_QUEUE)
                        setMessageListener(listener)
                        setAfterReceivePostProcessors(correlationIdPostProcessor)
                    }

    @Bean fun rabbitOperations(messageConverter: MessageConverter): RabbitOperations =
            RabbitTemplate(connectionFactory).also {
                it.messageConverter = messageConverter
            }

}

