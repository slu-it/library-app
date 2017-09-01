package library.service.business.books.domain

import library.service.business.books.domain.states.BookState
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import java.time.OffsetDateTime
import java.util.*

/**
 * Aggregate of a [Book], its unique reference ID ([UUID]) and [state][BookState].
 */
class BookRecord(
        val id: BookId,
        val book: Book,
        initialState: BookState = Available
) {

    var state: BookState = initialState
        private set

    /**
     * Tries to borrow this [BookRecord].
     *
     * Borrowing is a book's transition from the [Available] to the [Borrowed]
     * state. And can only be executed if the book is in the [Available] state.
     * Attempting to borrow an already [Borrowed] book will result in an
     * exception.
     *
     * In order to borrow a book, a [Borrower] and an [OffsetDateTime] for
     * the time of borrowing must be provided.
     *
     * @param by who is borrowing the book
     * @param on when was the book borrowed
     * @return the same [BookRecord] in it's [Borrowed] state
     * @throws BookAlreadyBorrowedException in case the book is already
     * [Borrowed] by someone
     */
    fun borrow(by: Borrower, on: OffsetDateTime): BookRecord {
        if (state is Borrowed) {
            throw BookAlreadyBorrowedException(id)
        }
        state = Borrowed(by, on)
        return this
    }

    /**
     * Tries to return this [BookRecord].
     *
     * Returning is a book's transition from the [Borrowed] to the [Available]
     * state. And can only be executed if the book is in the [Borrowed] state.
     * Attempting to return an already [Available] book will result in an
     * exception.
     *
     * @return the same [BookRecord] in it's [Available] state
     * @throws BookAlreadyReturnedException in case the book is already
     * [Available]
     */
    fun `return`(): BookRecord {
        if (state is Available) {
            throw BookAlreadyReturnedException(id)
        }
        state = Available
        return this
    }

}