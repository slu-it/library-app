package library.service.api.books.payload

import org.hibernate.validator.constraints.NotBlank
import org.hibernate.validator.constraints.NotEmpty
import javax.validation.constraints.Size

class CreateBookRequestBody {

    @NotBlank
    @Size(min = 10, max = 13)
    var isbn: String? = null

    @NotEmpty
    @Size(min = 1, max = 256)
    var title: String? = null

}