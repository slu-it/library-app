package library.integration.slack.core

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Represents the model of a consumer BookAdded event.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class BookAddedEvent(
        val bookId: String,
        val id: String,
        val isbn: String
) : BookEvent