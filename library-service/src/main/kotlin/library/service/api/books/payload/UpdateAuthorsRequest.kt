package library.service.api.books.payload

import javax.validation.constraints.NotEmpty

/** Request body used when updating a book's title. */
data class UpdateAuthorsRequest(
        @get:NotEmpty
        val authors: List<String>?
)