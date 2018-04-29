package library.service.api.books.payload

import library.service.business.books.domain.types.Borrower
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import utils.classification.UnitTest

@UnitTest
internal class BorrowBookRequestTest : AbstractPayloadTest<BorrowBookRequest>() {

    override val payloadType = BorrowBookRequest::class

    override val jsonExample: String = """ { "borrower": "Someone"} """
    override val deserializedExample = BorrowBookRequest("Someone")

    @Nested inner class `borrower property validation` {

        @Test fun `any values between 1 and 50 characters are valid`() = (1..50)
                .forEach { assertThat(validate(borrowerOfLength(it))).isEmpty() }

        @ValueSource(strings = ["abc", "ABC", "Loer Saguzaz", "Loer Saguzaz-Vocle", "Lülöla", "Ètien"])
        @ParameterizedTest fun `valid value examples`(borrower: String) {
            assertThat(validate(borrower)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val nullError = "must not be null"
            private val sizeError = "size must be between 1 and 50"
            private val patternError = """must match "${Borrower.VALID_BORROWER_PATTERN}""""

            @Test fun `null`() {
                assertThat(validate(null)).containsOnly(nullError)
            }

            @Test fun `empty string`() {
                assertThat(validate("")).containsOnly(sizeError, patternError)
            }

            @Test fun `blank string`() {
                assertThat(validate(" ")).containsOnly(patternError)
            }

            @Test fun `more than 50 character string`() {
                assertThat(validate(borrowerOfLength(51))).containsOnly(sizeError)
            }

            @ValueSource(strings = [
                ".", ",", ";", ":", "=", "*", "+", "[", "]", "(", ")", "!", "?", "<", ">", "$", "&"
            ])
            @ParameterizedTest fun `special characters`(borrower: String) {
                assertThat(validate(borrower)).containsOnly(patternError)
            }

        }

        private fun validate(borrower: String?) = validate(BorrowBookRequest(borrower))

    }

    private fun borrowerOfLength(length: Int) = "".padEnd(length, 'a')

}