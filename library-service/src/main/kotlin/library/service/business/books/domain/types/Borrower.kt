package library.service.business.books.domain.types

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Borrowed

/** Person who [Borrowed] a [BookRecord]. */
data class Borrower(
        private val value: String
) {

    override fun toString(): String = value

}