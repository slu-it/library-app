package library.service.business.books.domain.events

import library.service.business.books.domain.BookRecord
import library.service.business.events.DomainEvent
import java.time.OffsetDateTime
import java.util.*

/** Base class for domain events related to books. */
sealed class BookEvent(
        type: String,
        id: UUID,
        timestamp: OffsetDateTime,
        bookRecord: BookRecord
) : DomainEvent {

    override val type: String = type
    override val id: String = id.toString()
    override val timestamp: String = timestamp.toString()

    /**
     * The ID of the book this event relates to. Can be used by the receiving
     * bounded contexts to make callbacks or otherwise reference the book.
     */
    val bookId: String = bookRecord.id.toString()
    val isbn: String = bookRecord.book.isbn.toString()
    val title: String = bookRecord.book.title.toString()

    override fun toString() =
            "BookEvent(type='$type', id='$id', timestamp='$timestamp', bookId='$bookId', isbn='$isbn', title='$title')"

}

/** A new book was added to the library. */
class BookAdded(
        id: UUID = UUID.randomUUID(),
        timestamp: OffsetDateTime,
        bookRecord: BookRecord
) : BookEvent("book-added", id, timestamp, bookRecord)

/** A book was updated in the library. */
class BookUpdated(
        id: UUID = UUID.randomUUID(),
        timestamp: OffsetDateTime,
        bookRecord: BookRecord
) : BookEvent("book-updated", id, timestamp, bookRecord)

/** A book was permanently removed from the library. */
class BookRemoved(
        id: UUID = UUID.randomUUID(),
        timestamp: OffsetDateTime,
        bookRecord: BookRecord
) : BookEvent("book-removed", id, timestamp, bookRecord)

/** A book was borrowed from the library. */
class BookBorrowed(
        id: UUID = UUID.randomUUID(),
        timestamp: OffsetDateTime,
        bookRecord: BookRecord
) : BookEvent("book-borrowed", id, timestamp, bookRecord)

/** A book was returned to the library. */
class BookReturned(
        id: UUID = UUID.randomUUID(),
        timestamp: OffsetDateTime,
        bookRecord: BookRecord
) : BookEvent("book-returned", id, timestamp, bookRecord)
