package library.service.api.books.payload

import library.service.business.books.domain.types.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.UnitTest

@UnitTest
internal class CreateBookRequestTest : AbstractPayloadTest<CreateBookRequest>() {

    override val payloadType = CreateBookRequest::class

    override val jsonExample: String = """ { "isbn": "0123456789", "title": "Hello World" } """
    override val deserializedExample = CreateBookRequest("0123456789", "Hello World")

    @Nested inner class `bean validation` {

        @Nested inner class `for isbn` {

            @ValueSource(strings = ["0575081244", "978-0575081244", "9780575081244"])
            @ParameterizedTest fun `valid value examples`(isbn: String) {
                assertThat(validate(isbn)).isEmpty()
            }

            @Nested inner class `invalid value examples` {

                private val blankError = "must not be blank"
                private val patternError = """must match "(\d{3}-?)?\d{10}""""

                @Test fun `null`() {
                    assertThat(validate(null)).containsOnly(blankError)
                }

                @ValueSource(strings = ["", " ", "\t", "\n"])
                @ParameterizedTest fun `blank strings`(isbn: String) {
                    assertThat(validate(isbn)).containsOnly(patternError, blankError)
                }

                @ValueSource(strings = ["123456789", "12345678901", "123456789012", "12345678901234"])
                @ParameterizedTest fun `wrong number of digits`(isbn: String) {
                    assertThat(validate(isbn)).containsOnly(patternError)
                }

                @ValueSource(strings = ["a1b2c3d4e5", "1a2-1234567890", "1a21234567890"])
                @ParameterizedTest fun `alpha numeric characters`(isbn: String) {
                    assertThat(validate(isbn)).containsOnly(patternError)
                }

            }

        }

        private fun validate(isbn: String?) = validate(CreateBookRequest(isbn = isbn, title = "Hello World"))

    }

    @Nested inner class `title property validation` {

        @Test fun `any values between 1 and 256 characters are valid`() = (1..256)
                .forEach { assertThat(validate(titleOfLength(it))).isEmpty() }

        @ValueSource(strings = [
            "abc", "ABC", "The Martian", "The Dark Tower I: The Gunslinger",
            "Loer Saguzaz-Vocle", "Lülöla", "Ètien",
            """"_ !"#$%&'()*+,-./:;<=>?@`\~[]^|{} _""", "1234567890"
        ])
        @ParameterizedTest fun `valid value examples`(title: String) {
            assertThat(validate(title)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val blankError = "must not be blank"
            private val sizeError = "size must be between 1 and 256"
            private val patternError = """must match "${Title.VALID_TITLE_PATTERN}""""

            @Test fun `null`() {
                assertThat(validate(null)).containsOnly(blankError)
            }

            @Test fun `empty string`() {
                assertThat(validate("")).containsOnly(sizeError, blankError, patternError)
            }

            @Test fun `blank string`() {
                assertThat(validate(" ")).containsOnly(blankError)
            }

            @Test fun `more than 256 characters string`() {
                assertThat(validate(titleOfLength(257))).containsOnly(sizeError)
            }

        }

        private fun titleOfLength(length: Int) = "".padEnd(length, 'a')

        private fun validate(title: String?) = validate(CreateBookRequest(isbn = "0123456789", title = title))

    }

}