package library.service.business.books.domain

import library.service.business.books.domain.states.BookState
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.Borrower
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import java.time.OffsetDateTime
import java.util.*

class BookEntity(
        val id: UUID,
        val book: Book
) {

    var state: BookState = Available
        private set

    fun borrow(by: Borrower, on: OffsetDateTime): BookEntity {
        if (state is Borrowed) {
            throw BookAlreadyBorrowedException(id)
        }
        state = Borrowed(by, on)
        return this
    }

    fun `return`(): BookEntity {
        if (state is Available) {
            throw BookAlreadyReturnedException(id)
        }
        state = Available
        return this
    }

}