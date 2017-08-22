package library.service.api.books.payload

import org.hibernate.validator.constraints.NotBlank
import javax.validation.constraints.Size

/** Request body used when borrowing a book. */
class BorrowBookRequestBody {

    @NotBlank
    @Size(min = 1, max = 50)
    var borrower: String? = null

}