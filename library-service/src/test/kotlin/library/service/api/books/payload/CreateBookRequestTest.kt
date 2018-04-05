package library.service.api.books.payload

import library.service.business.books.domain.types.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.UnitTest
import javax.validation.Validation
import javax.validation.Validator

@UnitTest
internal class CreateBookRequestTest : AbstractPayloadTest<CreateBookRequest>() {

    override val payloadType = CreateBookRequest::class

    override val jsonExample: String = """ { "isbn": "0123456789", "title": "Hello World" } """
    override val deserializedExample = CreateBookRequest("0123456789", "Hello World")

    @Nested inner class `bean validation` {

        val validator: Validator = Validation.buildDefaultValidatorFactory().validator

        @Nested inner class `for isbn` {

            @Test fun `null is not allowed`() {
                val cut = CreateBookRequest(isbn = null, title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("must not be blank")
            }

            @Test fun `empty is not allowed`() {
                val cut = CreateBookRequest(isbn = "", title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 10 and 13",
                        "must not be blank"
                )
            }

            @Test fun `blank is not allowed`() {
                val cut = CreateBookRequest(isbn = " ", title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result.map { it.message }).containsOnly(
                        "size must be between 10 and 13",
                        "must not be blank"
                )
            }

            @Test fun `values between 10 and 13 characters are valid`() {
                (10..13)
                        .map { "".padEnd(it, '1') }
                        .map { CreateBookRequest(isbn = it, title = "Hello World") }
                        .map { validator.validate(it).toList() }
                        .forEach { assertThat(it).isEmpty() }
            }

            @Test fun `values with less 10 characters are invalid`() {
                val value = "".padEnd(9, '1')
                val cut = CreateBookRequest(isbn = value, title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 10 and 13")
            }

            @Test fun `values with more than 13 characters are invalid`() {
                val value = "".padEnd(14, '1')
                val cut = CreateBookRequest(isbn = value, title = "Hello World")
                val result = validator.validate(cut).toList()
                assertThat(result[0].message).isEqualTo("size must be between 10 and 13")
            }

        }

    }

    @Nested inner class `title property validation` {

        @Test fun `any values between 1 and 256 characters are valid`() = (1..256)
                .map { CreateBookRequest("0123456789", titleOfLength(it)) }
                .forEach { assertThat(validate(it)).isEmpty() }

        @ValueSource(strings = [
            "abc", "ABC", "The Martian", "The Dark Tower I: The Gunslinger",
            "Loer Saguzaz-Vocle", "Lülöla", "Ètien",
            """"_ !"#$%&'()*+,-./:;<=>?@`\~[]^|{} _""", "1234567890"
        ])
        @ParameterizedTest fun `valid value examples`(title: String) {
            val payload = CreateBookRequest("0123456789", title)
            assertThat(validate(payload)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val blankError = "must not be blank"
            private val sizeError = "size must be between 1 and 256"
            private val patternError = """must match "${Title.VALID_TITLE_PATTERN}""""

            @Test fun `null`() {
                val cut = CreateBookRequest("0123456789", null)
                assertThat(validate(cut)).containsOnly(blankError)
            }

            @Test fun `empty string`() {
                val cut = CreateBookRequest("0123456789", "")
                assertThat(validate(cut)).containsOnly(sizeError, blankError, patternError)
            }

            @Test fun `blank string`() {
                val cut = CreateBookRequest("0123456789", " ")
                assertThat(validate(cut)).containsOnly(blankError)
            }

            @Test fun `more than 256 characters string`() {
                val cut = CreateBookRequest("0123456789", titleOfLength(257))
                assertThat(validate(cut)).containsOnly(sizeError)
            }

        }

    }

    private fun titleOfLength(length: Int) = "".padEnd(length, 'a')

}