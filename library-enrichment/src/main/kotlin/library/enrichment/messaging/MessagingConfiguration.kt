package library.enrichment.messaging

import library.enrichment.correlation.CorrelationIdMessageReceivedPostProcessor
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.MessageListenerContainer
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * This [Configuration] is responsible for setting up all the things necessary
 * for receiving book domain events as messages via AMQP / RabbitMQ.
 *
 * It is creating an exclusive, durable queue for domain events of type `book-added`
 * and binding it to the general `book-events` topic on the message broker. In order
 * to process these event messages it also creates a [MessageListenerContainer] which
 * uses a [BookAddedEventMessageListener] in order to handle each incoming message.
 *
 * @see Binding
 * @see TopicExchange
 * @see Queue
 * @see MessageListenerContainer
 * @see BookAddedEventMessageListener
 */
@Configuration
internal class MessagingConfiguration(
        private val correlationIdMessageReceivedPostProcessor: CorrelationIdMessageReceivedPostProcessor,
        private val bookAddedEventMessageListener: BookAddedEventMessageListener
) {

    private companion object {
        const val EXCHANGE_NAME = "book-events"
        const val ROUTING_KEY = "book-added"
        const val QUEUE_NAME = "library-enrichment.book-events.book-added"
    }

    @Bean fun bookAddedEventBinding(): Binding = BindingBuilder
            .bind(bookAddedEventQueue())
            .to(bookEventsExchange())
            .with(ROUTING_KEY)

    @Bean fun bookAddedEventQueue() = Queue(QUEUE_NAME, true)
    @Bean fun bookEventsExchange() = TopicExchange(EXCHANGE_NAME)

    @Bean fun bookAddedEventMessageContainer(connectionFactory: ConnectionFactory): MessageListenerContainer = SimpleMessageListenerContainer(connectionFactory)
            .apply {
                setQueueNames(QUEUE_NAME)
                setAfterReceivePostProcessors(correlationIdMessageReceivedPostProcessor)
                setMessageListener(bookAddedEventMessageListener)
            }

}

