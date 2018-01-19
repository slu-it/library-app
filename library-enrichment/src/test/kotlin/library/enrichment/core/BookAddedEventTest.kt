package library.enrichment.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.testObjectMapper
import java.util.*


internal class BookAddedEventTest {

    val objectMapper = testObjectMapper()

    @Test fun `can be de-serialized from JSON`() {
        val id = UUID.randomUUID()
        val bookId = UUID.randomUUID()
        val json = """
            {
              "id": "$id",
              "bookId": "$bookId",
              "isbn": "1234567890123"
            }
            """
        val event = objectMapper.readValue(json, BookAddedEvent::class.java)
        assertThat(event.id).isEqualTo(id.toString())
        assertThat(event.bookId).isEqualTo(bookId.toString())
        assertThat(event.isbn).isEqualTo("1234567890123")
    }

    @Test fun `unknown properties are ignored`() {
        val json = """
            {
              "id": "${UUID.randomUUID()}",
              "bookId": "${UUID.randomUUID()}",
              "isbn": "1234567890123",
              "foo": "bar"
            }
            """
        objectMapper.readValue(json, BookAddedEvent::class.java)
    }

}