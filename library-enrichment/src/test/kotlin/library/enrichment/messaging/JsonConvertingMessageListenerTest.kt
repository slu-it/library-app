package library.enrichment.messaging

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.amqp.core.Message
import org.testit.testutils.logrecorder.api.LogLevel.WARN
import org.testit.testutils.logrecorder.api.LogRecord
import org.testit.testutils.logrecorder.junit5.RecordLoggers
import utils.extensions.firstEntry


internal class JsonConvertingMessageListenerTest {

    val objectMapper = spy(ObjectMapper().findAndRegisterModules())
    val payloadConsumer: (TestObject) -> Unit = mock()

    val cut = JsonConvertingMessageListener(objectMapper, TestObject::class, payloadConsumer)

    @Test fun `messages are de-serialized from JSON into target type`() {
        cut.onMessage(message("""{"foo": "value"}"""))
        verify(payloadConsumer).invoke(check {
            assertThat(it.foo).isEqualTo("value")
        })
    }

    @RecordLoggers(JsonConvertingMessageListener::class)
    @Test fun `any exception is logged and otherwise ignored`(log: LogRecord) {
        cut.onMessage(message("""{"unknown": "value"}"""))
        with(log.firstEntry()) {
            assertThat(level).isSameAs(WARN)
            assertThat(message).startsWith("received malformed TestObject message: [message]")
        }
        verifyZeroInteractions(payloadConsumer)
    }

    fun message(json: String): Message = mock {
        on { body } doReturn json.toByteArray()
        on { toString() } doReturn "[message]"
    }

    data class TestObject(val foo: String)

}