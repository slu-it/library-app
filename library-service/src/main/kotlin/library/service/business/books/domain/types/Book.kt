package library.service.business.books.domain.types

/** Aggregation of different book attributes into one immutable instance. */
data class Book(
        val isbn: Isbn13,
        val title: Title
)