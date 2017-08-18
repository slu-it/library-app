package library.service.business.books.domain.types

data class Book(
        val isbn: Isbn13,
        val title: Title
)