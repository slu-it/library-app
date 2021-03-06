package library.service.api.books.payload

import library.service.business.books.domain.types.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.UnitTest

@UnitTest
internal class UpdateTitleRequestTest : AbstractPayloadTest<UpdateTitleRequest>() {

    override val payloadType = UpdateTitleRequest::class

    override val jsonExample: String = """ { "title": "Hello World" } """
    override val deserializedExample = UpdateTitleRequest("Hello World")

    @Nested inner class `title property validation` {

        @Test fun `any values between 1 and 256 characters are valid`() = (1..256)
                .map { UpdateTitleRequest(titleOfLength(it)) }
                .forEach { assertThat(validate(it)).isEmpty() }

        @ValueSource(strings = [
            "abc", "ABC", "The Martian", "The Dark Tower I: The Gunslinger",
            "Loer Saguzaz-Vocle", "Lülöla", "Ètien",
            """"_ !"#$%&'()*+,-./:;<=>?@`\~[]^|{} _""", "1234567890"
        ])
        @ParameterizedTest fun `valid value examples`(title: String) {
            val payload = UpdateTitleRequest(title)
            assertThat(validate(payload)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val blankError = "must not be blank"
            private val sizeError = "size must be between 1 and 256"
            private val patternError = """must match "${Title.VALID_TITLE_PATTERN}""""

            @Test fun `null`() {
                val cut = UpdateTitleRequest(null)
                assertThat(validate(cut)).containsOnly(blankError)
            }

            @Test fun `empty string`() {
                val cut = UpdateTitleRequest("")
                assertThat(validate(cut)).containsOnly(sizeError, blankError, patternError)
            }

            @Test fun `blank string`() {
                val cut = UpdateTitleRequest(" ")
                assertThat(validate(cut)).containsOnly(blankError)
            }

            @Test fun `more than 256 characters string`() {
                val cut = UpdateTitleRequest(titleOfLength(257))
                assertThat(validate(cut)).containsOnly(sizeError)
            }

        }

    }

    private fun titleOfLength(length: Int) = "".padEnd(length, 'a')

}