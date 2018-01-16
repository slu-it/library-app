package library.service.api.books.payload

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

/** Request body used when updating a book's number of pages. */
data class UpdateNumberOfPagesRequest(
        @get:NotNull
        @get:Min(1)
        val numberOfPages: Int?
)