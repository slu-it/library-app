package library.service.business.books.domain.events

import library.service.business.books.domain.types.BookId
import java.time.OffsetDateTime
import java.util.*

/** Base class for domain events related to books. */
sealed class BookEvent(
        type: String,
        id: UUID,
        bookId: BookId,
        timestamp: OffsetDateTime
) {

    /**
     * The type of the event. Can be used by the receiving bounded contexts to
     * differentiate between the different event types.
     */
    val type: String = type

    /**
     * ID to uniquely identify this event among all other events. Is formatted
     * as a UUID.
     */
    val id: String = id.toString()

    /**
     * The ID of the book this event relates to. Can be used by the receiving
     * bounded contexts to make callbacks or otherwise reference the book.
     */
    val bookId: String = bookId.toString()

    /** The exact time the event occurred formatted as an ISO-8601 string. */
    val timestamp: String = timestamp.toString()

    override fun toString() = "BookEvent(type='$type', id='$id', bookId='$bookId', timestamp='$timestamp')"

}

/** A new book was added to the library. */
class BookAdded(
        id: UUID = UUID.randomUUID(),
        bookId: BookId,
        timestamp: OffsetDateTime
) : BookEvent("book-added", id, bookId, timestamp)

/** A book was permanently removed from the library. */
class BookRemoved(
        id: UUID = UUID.randomUUID(),
        bookId: BookId,
        timestamp: OffsetDateTime
) : BookEvent("book-removed", id, bookId, timestamp)

/** A book was borrowed from the library. */
class BookBorrowed(
        id: UUID = UUID.randomUUID(),
        bookId: BookId,
        timestamp: OffsetDateTime
) : BookEvent("book-borrowed", id, bookId, timestamp)

/** A book was returned to the library. */
class BookReturned(
        id: UUID = UUID.randomUUID(),
        bookId: BookId,
        timestamp: OffsetDateTime
) : BookEvent("book-returned", id, bookId, timestamp)
