package library.enrichment.core

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import utils.objectMapper


@UnitTest
internal class BookAddedEventTest {

    val id = "8669efc6-cde3-401b-98f6-6a32f621ea9c"
    val bookId = "9e8f2a6a-1dc9-4965-8525-cf42efc1d767"

    val objectMapper = objectMapper()

    @Test fun `can be de-serialized from JSON`() {
        val json = """
            {
              "id": "$id",
              "bookId": "$bookId",
              "isbn": "1234567890123"
            }
            """
        val event = objectMapper.readValue(json, BookAddedEvent::class.java)
        with(event) {
            assertThat(id).isEqualTo(id)
            assertThat(bookId).isEqualTo(bookId)
            assertThat(isbn).isEqualTo("1234567890123")
        }
    }

    @Test fun `unknown properties are ignored`() {
        val json = """
            {
              "id": "$id",
              "bookId": "$bookId",
              "isbn": "1234567890123",
              "unknown": "property"
            }
            """
        objectMapper.readValue(json, BookAddedEvent::class.java)
    }

}