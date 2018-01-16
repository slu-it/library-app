package library.service.api.books

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.security.UserContext
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component

/**
 * Component responsible for converting a [BookRecord] into a [BookResource].
 *
 * This includes transforming the data from one class to another and adding the
 * correct links depending on the [BookRecord] state.
 */
@Component
class BookResourceAssembler(
        private val currentUser: UserContext
) : ResourceAssemblerSupport<BookRecord, BookResource>(BooksController::class.java, BookResource::class.java) {

    private val booksController = BooksController::class.java

    override fun toResource(bookRecord: BookRecord): BookResource = createResourceWithId(bookRecord.id, bookRecord).apply {
        add(when (bookRecord.state) {
            is Available -> linkTo(booksController).slash(bookRecord.id).slash("borrow").withRel("borrow")
            is Borrowed -> linkTo(booksController).slash(bookRecord.id).slash("return").withRel("return")
        })
        if (currentUser.isCurator()) {
            add(linkTo(booksController).slash(bookRecord.id).withRel("delete"))
        }
    }

    override fun instantiateResource(bookRecord: BookRecord): BookResource {
        val bookState = bookRecord.state
        return BookResource(
                isbn = bookRecord.book.isbn.toString(),
                title = bookRecord.book.title.toString(),
                authors = bookRecord.book.authors.map { it.toString() },
                numberOfPages = bookRecord.book.numberOfPages,
                borrowed = when (bookState) {
                    is Available -> null
                    is Borrowed -> Borrowed(by = "${bookState.by}", on = "${bookState.on}")
                }
        )
    }

}