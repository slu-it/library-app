package library.service.api.books

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest

@UnitTest
internal class BookResourceTest {

    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Nested inner class `can be serialized to JSON` {

        @Test fun `min instance`() {
            val cut = BookResource(
                    isbn = "0123456789",
                    title = "Hello World",
                    authors = null,
                    numberOfPages = null,
                    borrowed = null
            )
            assertJsonSerializable(cut)
        }

        @Test fun `empty author instance`() {
            val cut = BookResource(
                    isbn = "0123456789",
                    title = "Hello World",
                    authors = emptyList(),
                    numberOfPages = 256,
                    borrowed = null
            )
            assertJsonSerializable(cut)
        }

        @Test fun `max instance`() {
            val cut = BookResource(
                    isbn = "0123456789",
                    title = "Hello World",
                    authors = listOf("Author #1", "Author #2"),
                    numberOfPages = 128,
                    borrowed = Borrowed(
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