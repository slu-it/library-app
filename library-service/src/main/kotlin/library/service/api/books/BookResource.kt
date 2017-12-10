package library.service.api.books

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import library.service.business.books.domain.BookRecord
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.Relation

/** Representation of a [BookRecord] as a REST resource. */
@JsonInclude(NON_EMPTY)
@Relation(value = "book", collectionRelation = "books")
data class BookResource(
        val isbn: String,
        val title: String,
        val borrowed: BorrowedState?
) : ResourceSupport() {

    data class BorrowedState(
            val by: String,
            val on: String
    )

}