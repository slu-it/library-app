package library.service.business.books.domain.events

import library.service.business.books.domain.types.BookId
import java.time.OffsetDateTime
import java.util.*

/** Base class for domain events related to books. */
sealed class BookEvent(
        val type: String,
        id: UUID,
        bookId: BookId,
        timestamp: OffsetDateTime
) {

    /** ID to uniquely identify the event. */
    val id = id.toString()
    /** The ID of the book this event relates to. */
    val bookId = bookId.toString()
    /** The exact time the event occurred. */
    val timestamp = timestamp.toString()

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
