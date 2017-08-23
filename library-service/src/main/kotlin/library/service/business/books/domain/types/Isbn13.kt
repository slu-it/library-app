package library.service.business.books.domain.types

import library.service.business.books.domain.types.Isbn13.NotAnIsbnNumberException
import library.service.business.exceptions.MalformedValueException

/**
 * The unique 13 digit identification number of a [Book].
 *
 * This type cannot be instantiated with anything other than 13 digit numbers.
 * Any attempt to do so will result in a [NotAnIsbnNumberException].
 */
data class Isbn13(
        private val value: String
) {

    init {
        if (!value.matches(Regex("[0-9]{13}")))
            throw NotAnIsbnNumberException(value)
    }

    override fun toString(): String = value

    companion object {

        /**
         * Creates a new [Isbn13] from the given value.
         *
         * The value can either be a 10 or 13 digit ISBN number. Anything
         * else will throw an exception. If the value is a 10 digit ISBN
         * number, it will be prefixed with `978` to make it a valid 13
         * digit ISBN.
         *
         * @param value the value to use
         * @return the created [Isbn13]
         * @throws NotAnIsbnNumberException in case the given value is not an
         * ISBN number
         */
        fun parse(value: String): Isbn13 {
            val isbnValue = when {
                value.length == 10 -> "978$value"
                value.length == 13 -> value
                else -> throw NotAnIsbnNumberException(value)
            }
            return Isbn13(isbnValue)
        }

    }

    class NotAnIsbnNumberException(value: String)
        : MalformedValueException("This is not a valid ISBN-13 number: $value")

}