package library.service.business.books.domain.types

import library.service.business.exceptions.MalformedValueException

data class Isbn13(val value: String) {

    init {
        if (!value.matches(Regex("[0-9]{13}")))
            throw NotAnIsbnNumberException(value)
    }

    companion object {

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