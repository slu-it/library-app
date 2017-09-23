package library.service.business.books.domain.events

import library.service.business.books.domain.types.BookId
import java.time.OffsetDateTime
import java.util.*

/** Base class for domain events related to books. */
sealed class BookEvent {

    /** ID to uniquely identify the event. */
    abstract val id: UUID

    /** The ID of the book this event relates to. */
    abstract val bookId: BookId

    /** The exact time the event occurred. */
    abstract val timestamp: OffsetDateTime

}

/** A new book was added to the library. */
data class BookAdded(
        override val id: UUID = UUID.randomUUID(),
        override val bookId: BookId,
        override val timestamp: OffsetDateTime
) : BookEvent()

/** A book was permanently removed from the library. */
data class BookRemoved(
        override val id: UUID = UUID.randomUUID(),
        override val bookId: BookId,
        override val timestamp: OffsetDateTime
) : BookEvent()

/** A book was borrowed from the library. */
data class BookBorrowed(
        override val id: UUID = UUID.randomUUID(),
        override val bookId: BookId,
        override val timestamp: OffsetDateTime
) : BookEvent()

/** A book was returned to the library. */
data class BookReturned(
        override val id: UUID = UUID.randomUUID(),
        override val bookId: BookId,
        override val timestamp: OffsetDateTime
) : BookEvent()
