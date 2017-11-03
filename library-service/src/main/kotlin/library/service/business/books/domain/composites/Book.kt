package library.service.business.books.domain.composites

import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title

/** Composition of different book attributes into one immutable instance. */
data class Book(
        val isbn: Isbn13,
        val title: Title
)