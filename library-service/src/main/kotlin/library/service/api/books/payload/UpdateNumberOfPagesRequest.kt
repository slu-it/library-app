package library.service.api.books.payload

import javax.validation.constraints.Min
import javax.validation.constraints.NotNull

/** Request body used when updating a book's number of pages. */
data class UpdateNumberOfPagesRequest(
        @field:NotNull
        @field:Min(1)
        val numberOfPages: Int?
)