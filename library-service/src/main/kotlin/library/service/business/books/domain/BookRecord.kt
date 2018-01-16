package library.service.business.books.domain

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.BookState
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.Author
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Title
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import java.time.OffsetDateTime
import java.util.*

/**
 * Aggregate of a [Book], its unique reference ID ([UUID]) and [state][BookState].
 */
class BookRecord(id: BookId, book: Book, state: BookState = Available) {

    val id: BookId = id
    var book: Book = book
        private set
    var state: BookState = state
        private set

    /**
     * Changes the [Title] of this [BookRecord's][BookRecord] [Book]
     *
     * @param title the new [Title]
     */
    fun changeTitle(title: Title) {
        book = book.copy(title = title)
    }

    /**
     * Changes the list of [Authors][Author] of this [BookRecord's][BookRecord]
     * [Book]. Might be empty in order to remove the existing authors.
     *
     * @param authors the new list of [Authors][Author]
     */
    fun changeAuthors(authors: List<Author>) {
        book = book.copy(authors = authors.toList())
    }

    /**
     * Changes the number of pages of this [BookRecord's][BookRecord] [Book].
     * Might be null in order to remove the existing number of pages.
     *
     * @param numberOfPages the new number of pages
     */
    fun changeNumberOfPages(numberOfPages: Int?) {
        book = book.copy(numberOfPages = numberOfPages)
    }

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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BookRecord

        if (id != other.id) return false
        if (book != other.book) return false
        if (state != other.state) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + book.hashCode()
        result = 31 * result + state.hashCode()
        return result
    }

    override fun toString() = "BookRecord(id=$id, book=$book, state=$state)"

}