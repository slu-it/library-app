package library.enrichment.core

data class BookAddedEvent(
        val id: String,
        val bookId: String,
        val isbn: String
)