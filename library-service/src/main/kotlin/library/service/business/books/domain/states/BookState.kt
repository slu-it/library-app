package library.service.business.books.domain.states

import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.Borrower
import java.time.OffsetDateTime

/**
 * The state of a [BookEntity]. Can be one of:
 *
 * - [Available]
 * - [Borrowed]
 */
sealed class BookState {

    /**
     * The [BookEntity] is available to be [Borrowed].
     */
    object Available : BookState()

    /**
     * The [BookEntity] is _borrowed_ by someone. Contains information on who
     * borrowed the book and when.
     */
    data class Borrowed(val by: Borrower, val on: OffsetDateTime) : BookState()

}