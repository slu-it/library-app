package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.classification.UnitTest
import javax.validation.Validation
import javax.validation.Validator

@UnitTest
internal class CreateBookRequestBodyTest {

    val objectMapper = ObjectMapper().apply { findAndRegisterModules() }

    @Test fun `can be de-serialized from JSON`() {
        val json = """ { "isbn": "0123456789", "title": "Hello World" } """
        val cut = objectMapper.readValue(json, CreateBookRequestBody::class.java)
        assertThat(cut.isbn).isEqualTo("0123456789")
        assertThat(cut.title).isEqualTo("Hello World")
    }

    @Nested inner class `bean validation` {

        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        @Nested inner class `for isbn` {

            @Test fun `null is not allowed`() {
                val cut = CreateBookRequestBody(isbn = null, title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("must not be blank")
            }

            @Test fun `empty is not allowed`() {
                val cut = CreateBookRequestBody(isbn = "", title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 10 and 13",
                        "must not be blank"
                )
            }

            @Test fun `blank is not allowed`() {
                val cut = CreateBookRequestBody(isbn = " ", title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 10 and 13",
                        "must not be blank"
                )
            }

            @Test fun `values between 10 and 13 characters are valid`() {
                (10..13)
                        .map { "".padEnd(it, '1') }
                        .map { CreateBookRequestBody(isbn = it, title = "Hello World") }
                        .map { validator.validate(it).toList() }
                        .forEach { assertThat(it).isEmpty() }
            }

            @Test fun `values with less 10 characters are invalid`() {
                val value = "".padEnd(9, '1')
                val cut = CreateBookRequestBody(isbn = value, title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 10 and 13")
            }

            @Test fun `values with more than 13 characters are invalid`() {
                val value = "".padEnd(14, '1')
                val cut = CreateBookRequestBody(isbn = value, title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 10 and 13")
            }

        }

        @Nested inner class `for title` {

            @Test fun `null is not allowed`() {
                val cut = CreateBookRequestBody(isbn = "0123456789", title = null)
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("must not be blank")
            }

            @Test fun `empty is not allowed`() {
                val cut = CreateBookRequestBody(isbn = "0123456789", title = "")
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 1 and 256",
                        "must not be blank"
                )
            }

            @Test fun `blank is not allowed`() {
                val cut = CreateBookRequestBody(isbn = "0123456789", title = " ")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("must not be blank")
            }

            @Test fun `values between 1 and 256 characters are valid`() {
                (1..256)
                        .map { "".padEnd(it, 'a') }
                        .map { CreateBookRequestBody(isbn = "0123456789", title = it) }
                        .map { validator.validate(it).toList() }
                        .forEach { assertThat(it).isEmpty() }
            }

            @Test fun `values with more than 256 characters are invalid`() {
                val value = "".padEnd(257, 'a')
                val cut = CreateBookRequestBody(isbn = "0123456789", title = value)
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 1 and 256")
            }

        }

    }

}