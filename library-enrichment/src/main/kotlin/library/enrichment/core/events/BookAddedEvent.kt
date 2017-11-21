package library.enrichment.core.events

data class BookAddedEvent(
        val id: String,
        val bookId: String
)