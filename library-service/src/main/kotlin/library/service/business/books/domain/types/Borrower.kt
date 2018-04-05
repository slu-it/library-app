package library.service.business.books.domain.types

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Borrowed

/** Person who [Borrowed] a [BookRecord]. */
data class Borrower(
        private val value: String
) {

    companion object {
        const val VALID_BORROWER_PATTERN = """(?U)[\w][\w -]*"""
    }

    override fun toString(): String = value

}