package library.service.api.books.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/** Request body used when updating a book's title. */
data class UpdateTitleRequest(
        @get:NotBlank
        @get:Size(min = 1, max = 256)
        val title: String?
)