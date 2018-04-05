package library.service.api.books.payload

import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

/** Request body used when borrowing a book. */
data class BorrowBookRequest(
        @field:NotNull
        @field:Pattern(regexp = """(?U)[\w][\w -]{0,49}""")
        val borrower: String?
)