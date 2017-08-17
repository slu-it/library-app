package library.service.business.books.domain

import library.service.business.books.domain.types.Isbn
import library.service.business.books.domain.types.Title

data class Book(
        val isbn: Isbn,
        val title: Title
)