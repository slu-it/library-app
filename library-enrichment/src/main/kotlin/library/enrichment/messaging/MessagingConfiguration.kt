package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
internal class MessagingConfiguration(
        private val connectionFactory: ConnectionFactory,
        private val exchange: BookEventExchange,
        private val queue: BookAddedEventQueue,
        private val bookAddedMessageListener: BookAddedMessageListener
) {

    @Bean fun messageConverter(objectMapper: ObjectMapper): MessageConverter
            = Jackson2JsonMessageConverter(objectMapper)

    /**
     * Binds the [BookAddedEventQueue] to the [BookEventsExchange] in order to
     * receive messages on that queue.
     */
    @Bean fun bookAddedEventBinding(): Binding
            = BindingBuilder.bind(queue).to(exchange).with("book-added")

    @Bean fun bookAddedEventMessageContainer() = SimpleMessageListenerContainer(connectionFactory).apply {
        setQueueNames(queue.name)
        setMessageListener(bookAddedMessageListener)
    }

    @Component
    internal class BookEventExchange : TopicExchange("book-events")

    @Component
    internal class BookAddedEventQueue : Queue("library-enrichment.book-events.book-added", true)

}