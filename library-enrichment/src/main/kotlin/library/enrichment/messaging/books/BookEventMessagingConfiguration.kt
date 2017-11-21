package library.enrichment.messaging.books

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class BookEventMessagingConfiguration(
        private val queue: BookAddedEventQueue,
        private val exchange: BookEventsExchange
) {

    /**
     * Binds the [BookAddedEventQueue] to the [BookEventsExchange] in order to
     * receive messages on that queue.
     */
    @Bean fun bookAddedEventBinding(): Binding = BindingBuilder
            .bind(queue)
            .to(exchange)
            .with("book-added")

    @Bean
    fun bookAddedEventMessageContainer(
            connectionFactory: ConnectionFactory,
            listener: BookAddedEventMessageListener
    ): SimpleMessageListenerContainer {

        val container = SimpleMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.setQueueNames(queue.name)
        container.setMessageListener(listener)
        return container
    }

}