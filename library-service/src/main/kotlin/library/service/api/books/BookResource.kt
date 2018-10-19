package library.service.api.books

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import library.service.business.books.domain.BookRecord
import org.springframework.hateoas.ResourceSupport
import org.springframework.hateoas.core.Relation

/** Representation of a [BookRecord] as a REST resource. */
@JsonInclude(NON_NULL)
@Relation(value = "book", collectionRelation = "books")
data class BookResource(
        val isbn: String,
        val title: String,
        val authors: List<String>?,
        val numberOfPages: Int?,
        val borrowed: Borrowed?
) : ResourceSupport()

data class Borrowed(
        val by: String,
        val on: String
)