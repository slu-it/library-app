package utils.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import library.integration.slack.core.BookEvent
import org.springframework.amqp.core.Message
import org.springframework.amqp.core.MessageProperties

/**
 * Converts a message of type BookEvent into AMQP Message
 */
fun toMessageConverter(inputMsg: BookEvent, objectMapper: ObjectMapper): Message {
    val body = objectMapper.writeValueAsBytes(inputMsg)
    val msgProperties = MessageProperties()
    return Message(body, msgProperties)
}
