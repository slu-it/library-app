package library.service.api.books

import com.fasterxml.jackson.databind.ObjectMapper
import library.service.api.books.BookResource.BorrowedState
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.UnitTest

@UnitTest
internal class BookResourceTest {

    val objectMapper = ObjectMapper()

    @Nested inner class `can be (de)serialized from and to JSON` {

        @Test fun `empty instance`() {
            val cut = BookResource()
            assertJsonSerializable(cut)
        }

        @Test fun `full instance`() {
            val cut = BookResource(
                    isbn = "0123456789",
                    title = "Hello World",
                    borrowed = BorrowedState(
                            by = "Someone",
                            on = "2017-08-20T12:34:56.789Z"
                    )
            )
            assertJsonSerializable(cut)
        }

        private fun assertJsonSerializable(cut: BookResource) {
            val serialized = objectMapper.writeValueAsString(cut)
            val deSerialized = objectMapper.readValue(serialized, BookResource::class.java)
            assertThat(deSerialized).isEqualTo(cut)
        }

    }

}