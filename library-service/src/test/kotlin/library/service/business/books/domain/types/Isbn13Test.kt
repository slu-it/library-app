package library.service.business.books.domain.types

import contracts.ValueTypeContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import test.classification.UnitTest

@UnitTest
internal class Isbn13Test : ValueTypeContract<Isbn13>() {

    override fun instanceExampleOne() = Isbn13("0123456789012")
    override fun instanceExampleTwo() = Isbn13("1234567890123")

    @Test fun `toString() returns ISBN's value as a String`() {
        val isbn = Isbn13("1234567890123")
        assertThat(isbn.toString()).isEqualTo("1234567890123")
    }

    @Nested inner class `during construction` {

        @ValueSource(strings = arrayOf(
                "0123456789012",
                "1234567890123",
                "2345678901234",
                "3456789012345",
                "4567890123456",
                "5678901234567",
                "6789012345678",
                "7890123456789",
                "8901234567890",
                "9012345678901"
        ))
        @ParameterizedTest fun `13 digit numbers are considered valid`(isbnCandidate: String) {
            Isbn13(isbnCandidate) // no error means it's OK
        }

        @ValueSource(strings = arrayOf(
                "120000000000",
                "14000000000000"
        ))
        @ParameterizedTest fun `any other number of digits is considered invalid`(isbnCandidate: String) {
            assertThrows(Isbn13.NotAnIsbnNumberException::class.java, {
                Isbn13(isbnCandidate)
            })
        }

        @ValueSource(strings = arrayOf(
                "a000000000000",
                "A000000000000",
                "z000000000000",
                "Z000000000000",
                "$000000000000",
                "_000000000000"
        ))
        @ParameterizedTest fun `any non number characters are considered invalid`(isbnCandidate: String) {
            assertThrows(Isbn13.NotAnIsbnNumberException::class.java, {
                Isbn13(isbnCandidate)
            })
        }

    }

    @Nested inner class `when parsing from a String` {

        @Test fun `10 digit ISBNs are converted to 13 digit ISBNs`() {
            val isbn = Isbn13.parse("0000000000")
            assertThat(isbn.toString()).isEqualTo("9780000000000")
        }

        @Test fun `13 digit ISBNs are used as they are`() {
            val isbn = Isbn13.parse("0000000000111")
            assertThat(isbn.toString()).isEqualTo("0000000000111")
        }

        @ValueSource(strings = arrayOf(
                "120000000000",
                "14000000000000"
        ))
        @ParameterizedTest fun `any other number of digits is considered invalid`(isbnCandidate: String) {
            assertThrows(Isbn13.NotAnIsbnNumberException::class.java, {
                Isbn13.parse(isbnCandidate)
            })
        }

    }

}