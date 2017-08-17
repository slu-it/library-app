package library.service.api.books.payload

import org.hibernate.validator.constraints.NotBlank
import javax.validation.constraints.Size

class BorrowBookRequestBody {

    @NotBlank
    @Size(min = 1, max = 50)
    var borrower: String? = null

}