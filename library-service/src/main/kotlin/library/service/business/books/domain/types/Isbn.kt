package library.service.business.books.domain.types

import library.service.business.exceptions.MalformedValueException

data class Isbn(private var _value: String) {

    val value: String
        get() = _value

    init {
        if (is10DigitIsbn(value)) {
            _value = "978$value"
        } else if (is13DigitIsbn(value)) {
            _value = value
        } else {
            throw NotAnIsbnNumberException(value)
        }
    }

    private fun is10DigitIsbn(value: String) = value.matches(Regex("[0-9]{10}"))
    private fun is13DigitIsbn(value: String) = value.matches(Regex("[0-9]{13}"))

    class NotAnIsbnNumberException(value: String)
        : MalformedValueException("This is not a valid ISBN number: $value")

}