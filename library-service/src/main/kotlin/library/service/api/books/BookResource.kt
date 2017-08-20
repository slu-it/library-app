package library.service.api.books

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import org.springframework.hateoas.ResourceSupport

@JsonInclude(NON_EMPTY)
data class BookResource(
        var isbn: String? = null,
        var title: String? = null,
        var borrowed: BorrowedState? = null
) : ResourceSupport() {

    data class BorrowedState(
            var by: String? = null,
            var on: String? = null
    )

}