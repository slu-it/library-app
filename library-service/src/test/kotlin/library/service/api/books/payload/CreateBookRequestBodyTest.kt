package library.service.api.books.payload

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.UnitTest
import javax.validation.Validation


@UnitTest
internal class CreateBookRequestBodyTest {

    @Test fun `can be de-serialized from JSON`() {
        val json = """ { "isbn": "0123456789", "title": "Hello World" } """
        val cut = ObjectMapper().readValue(json, CreateBookRequestBody::class.java)
        assertThat(cut.isbn).isEqualTo("0123456789")
        assertThat(cut.title).isEqualTo("Hello World")
    }

    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    @Nested inner class `bean validation` {

        val validator = Validation.buildDefaultValidatorFactory().validator
        val validInstance = CreateBookRequestBody().apply {
            isbn = "01234567890"
            title = "Hello World"
        }

        @Nested inner class `for isbn` {

            @Test fun `null is not allowed`() {
                val cut = validInstance.apply { isbn = null }
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("may not be empty")
            }

            @Test fun `empty is not allowed`() {
                val cut = validInstance.apply { isbn = "" }
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 10 and 13",
                        "may not be empty"
                )
            }

            @Test fun `blank is not allowed`() {
                val cut = validInstance.apply { isbn = " " }
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 10 and 13",
                        "may not be empty"
                )
            }

            @Test fun `values between 10 and 13 characters are valid`() {
                (10..13)
                        .map { "".padEnd(it, '1') }
                        .map { validInstance.apply { isbn = it } }
                        .map { validator.validate(it).toList() }
                        .forEach { assertThat(it).isEmpty() }
            }

            @Test fun `values with less 10 characters are invalid`() {
                val value = "".padEnd(9, '1')
                val cut = validInstance.apply { isbn = value }
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 10 and 13")
            }

            @Test fun `values with more than 13 characters are invalid`() {
                val value = "".padEnd(14, '1')
                val cut = validInstance.apply { isbn = value }
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 10 and 13")
            }

        }

        @Nested inner class `for title` {

            @Test fun `null is not allowed`() {
                val cut = validInstance.apply { title = null }
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("may not be empty")
            }

            @Test fun `empty is not allowed`() {
                val cut = validInstance.apply { title = "" }
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 1 and 256",
                        "may not be empty"
                )
            }

            @Test fun `blank is not allowed`() {
                val cut = validInstance.apply { title = " " }
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("may not be empty")
            }

            @Test fun `values between 1 and 256 characters are valid`() {
                (1..256)
                        .map { "".padEnd(it, 'a') }
                        .map { validInstance.apply { title = it } }
                        .map { validator.validate(it).toList() }
                        .forEach { assertThat(it).isEmpty() }
            }

            @Test fun `values with more than 256 characters are invalid`() {
                val value = "".padEnd(257, 'a')
                val cut = validInstance.apply { title = value }
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 1 and 256")
            }

        }

    }

}