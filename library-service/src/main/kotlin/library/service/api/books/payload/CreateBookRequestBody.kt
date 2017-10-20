package library.service.api.books.payload

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

/** Request body used when creating a book. */
class CreateBookRequestBody {

    @NotBlank
    @Size(min = 10, max = 13)
    var isbn: String? = null

    @NotBlank
    @Size(min = 1, max = 256)
    var title: String? = null

}