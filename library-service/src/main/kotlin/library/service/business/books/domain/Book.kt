package library.service.business.books.domain

import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title

data class Book(
        val isbn: Isbn13,
        val title: Title
)