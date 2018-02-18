package library.enrichment.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Immutable representation of a `book-added` event from the `library-service`.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BookAddedEvent(
        val id: String,
        val bookId: String,
        val isbn: String
)