package library.integration.slack.messaging

import com.fasterxml.jackson.databind.ObjectMapper
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

@Configuration
class MessageConfiguration(
        private val connectionFactory: ConnectionFactory
) {
    companion object {
        const val BOOK_EVENTS_TOPIC = "book-events"
        const val BOOK_ADDED_QUEUE = "library-integration-slack.book-events.book-added"
        const val ROUTING_KEY = "book-added"
    }

    @Bean
    fun messageConvertor(objectMapper: ObjectMapper): MessageConverter {
        return Jackson2JsonMessageConverter(objectMapper).also {
            it.classMapper = DefaultJackson2JavaTypeMapper();
        }

    }

    @Bean
    fun bookEventsTopicExchange() = TopicExchange(BOOK_EVENTS_TOPIC)

    @Bean
    fun bookAddedQueue() = Queue(BOOK_ADDED_QUEUE, true);

    @Bean
    fun bookAddedEventBinding(bookAddedQueue: Queue, bookEventsTopicExchange: TopicExchange) = BindingBuilder
            .bind(bookAddedQueue)
            .to(bookEventsTopicExchange)
            .with(ROUTING_KEY)

    @Bean
    fun messageListenerContainer(consumer: BookAddedMessageConsumer): MessageListenerContainer =
            SimpleMessageListenerContainer(connectionFactory)
                    .apply {
                        setQueueNames(BOOK_ADDED_QUEUE)
                        setMessageListener(consumer)
                    }

    @Bean
    fun rabbitOperations(messageConverter: MessageConverter): RabbitOperations =
            RabbitTemplate(connectionFactory)
                    .also { it.messageConverter = messageConverter }
}

