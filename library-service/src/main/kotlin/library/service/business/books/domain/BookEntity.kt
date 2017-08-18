package library.service.business.books.domain

import library.service.business.books.domain.types.BorrowedState
import library.service.business.books.domain.types.Borrower
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import java.time.OffsetDateTime
import java.util.*

class BookEntity(
        val id: UUID,
        val book: Book
) {

    var borrowed: BorrowedState? = null
        private set

    fun borrow(by: Borrower, on: OffsetDateTime) {
        if (borrowed != null) {
            throw BookAlreadyBorrowedException(id)
        }
        borrowed = BorrowedState(by, on)
    }

    fun `return`() {
        if (borrowed == null) {
            throw BookAlreadyReturnedException(id)
        }
        borrowed = null
    }

}