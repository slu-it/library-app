package library.service.api.books.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/** Request body used when creating a book. */
data class CreateBookRequestBody(
        @get:NotBlank
        @get:Size(min = 10, max = 13)
        val isbn: String?,
        @get:NotBlank
        @get:Size(min = 1, max = 256)
        val title: String?
)