package library.service.business.books.domain.events

import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Isbn13
import library.service.business.events.DomainEvent
import java.time.OffsetDateTime
import java.util.*

/** Base class for domain events related to books. */
sealed class BookEvent(
        type: String,
        id: UUID,
        bookId: BookId,
        timestamp: OffsetDateTime
) : DomainEvent {

    override val type: String = type
    override val id: String = id.toString()
    override val timestamp: String = timestamp.toString()

    /**
     * The ID of the book this event relates to. Can be used by the receiving
     * bounded contexts to make callbacks or otherwise reference the book.
     */
    val bookId: String = bookId.toString()

    override fun toString() = "BookEvent(type='$type', id='$id', bookId='$bookId', timestamp='$timestamp')"

}

/** A new book was added to the library. */
class BookAdded(
        id: UUID = UUID.randomUUID(),
        bookId: BookId,
        timestamp: OffsetDateTime,
        isbn:Isbn13
) : BookEvent("book-added", id, bookId, timestamp){
    val isbn:String = isbn.toString()
}

/** A book was updated in the library. */
class BookUpdated(
        id: UUID = UUID.randomUUID(),
        bookId: BookId,
        timestamp: OffsetDateTime
) : BookEvent("book-updated", id, bookId, timestamp)

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
