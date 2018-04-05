package library.service.api.books.payload

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
                .map { BorrowBookRequest(borrowerOfLength(it)) }
                .forEach { assertThat(validate(it)).isEmpty() }

        @ValueSource(strings = ["abc", "ABC", "Loer Saguzaz", "Loer Saguzaz-Vocle", "Lülöla", "Ètien"])
        @ParameterizedTest fun `valid value examples`(borrower: String) {
            val payload = BorrowBookRequest(borrower)
            assertThat(validate(payload)).isEmpty()
        }

        @Nested inner class `invalid value examples` {

            private val nullError = "must not be null"
            private val patternError = """must match "(?U)[\w][\w -]{0,49}""""

            @Test fun `null`() {
                val payload = BorrowBookRequest(null)
                assertThat(validate(payload)).containsOnly(nullError)
            }

            @Test fun `empty string`() {
                val payload = BorrowBookRequest("")
                assertThat(validate(payload)).containsOnly(patternError)
            }

            @Test fun `blank string`() {
                val payload = BorrowBookRequest(" ")
                assertThat(validate(payload)).containsOnly(patternError)
            }

            @Test fun `more than 50 character string`() {
                val payload = BorrowBookRequest(borrowerOfLength(51))
                assertThat(validate(payload)).containsOnly(patternError)
            }

            @ValueSource(strings = [
                ".", ",", ";", ":", "=", "*", "+", "[", "]", "(", ")", "!", "?", "<", ">", "$", "&"
            ])
            @ParameterizedTest fun `special characters`(borrower: String) {
                val payload = BorrowBookRequest(borrower)
                assertThat(validate(payload)).containsOnly(patternError)
            }

        }

    }

    private fun borrowerOfLength(length: Int) = "".padEnd(length, 'a')

}