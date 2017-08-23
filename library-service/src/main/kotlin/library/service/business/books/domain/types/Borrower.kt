package library.service.business.books.domain.types

import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.states.BookState.Borrowed

/** Person who [Borrowed] a [BookEntity]. */
data class Borrower(
        private val value: String
) {

    override fun toString(): String = value

}