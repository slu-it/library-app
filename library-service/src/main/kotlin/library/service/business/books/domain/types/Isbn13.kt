package library.service.business.books.domain.types

import library.service.business.books.domain.types.Isbn13.NotAnIsbnNumberException
import library.service.business.exceptions.MalformedValueException

/**
 * The unique 13 digit identification number of a book.
 *
 * This type cannot be instantiated with anything other than 13 digit numbers.
 * Any attempt to do so will result in a [NotAnIsbnNumberException].
 */
data class Isbn13(
        private val value: String
) {

    companion object {

        const val VALID_PARSE_PATTERN = """(\d{3}-?)?\d{10}"""
        private val VALID_PARSE_REGEX = Regex(VALID_PARSE_PATTERN)
        private val VALID_VALUE_REGEX = Regex("""\d{13}""")

        /**
         * Creates a new [Isbn13] from the given value.
         *
         * The value can be in one of the following formats:
         * - 10 digit number e.g. `0575081244`
         * - 13 digit number e.g. `9780575081244`
         * - 3 digit / 10 digit with separator e.g. `978-0575081244`
         *
         * Any other format will throw an exception. The value will always
         * be stored as a 13 digit number! For values with a 10 digit format
         * the official `978` prefix is added to make it 13 digits.
         *
         * @param value the value to use
         * @return the created [Isbn13]
         * @throws NotAnIsbnNumberException in case the given value is not an
         *          ISBN number
         */
        fun parse(value: String): Isbn13 {
            if (!value.matches(VALID_PARSE_REGEX)) {
                throw NotAnIsbnNumberException(value)
            }

            val cleanValue = value.replace("-", "")

            val isbnValue = when {
                cleanValue.length == 10 -> "978$cleanValue"
                cleanValue.length == 13 -> cleanValue
                else -> error("invalid state")
            }
            return Isbn13(isbnValue)
        }

    }

    init {
        if (!value.matches(VALID_VALUE_REGEX)) throw NotAnIsbnNumberException(value)
    }

    override fun toString(): String = value

    class NotAnIsbnNumberException(value: String)
        : MalformedValueException("This is not a valid ISBN-13 number: $value")

}