package library.service.api.books.payload

import library.service.business.books.domain.types.Title
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

/** Request body used when creating a book. */
data class CreateBookRequest(
        @get:NotBlank
        @get:Size(min = 10, max = 13)
        val isbn: String?,
        @field:NotBlank
        @field:Size(min = 1, max = 256)
        @field:Pattern(regexp = Title.VALID_TITLE_PATTERN)
        val title: String?
)