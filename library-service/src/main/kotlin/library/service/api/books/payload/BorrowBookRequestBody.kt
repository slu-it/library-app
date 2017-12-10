package library.service.api.books.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/** Request body used when borrowing a book. */
data class BorrowBookRequestBody(
        @get:NotBlank
        @get:Size(min = 1, max = 50)
        val borrower: String?
)