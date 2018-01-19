package library.enrichment.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class BookAddedEvent(
        val id: String,
        val bookId: String,
        val isbn: String
)