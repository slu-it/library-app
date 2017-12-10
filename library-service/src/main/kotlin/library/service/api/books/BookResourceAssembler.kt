package library.service.api.books

import library.service.api.books.BookResource.BorrowedState
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

    override fun toResource(bookRecord: BookRecord): BookResource {
        val bookResource = createResource(bookRecord)
        addLinks(bookResource, bookRecord)
        return bookResource
    }

    private fun createResource(bookRecord: BookRecord): BookResource {
        val bookState = bookRecord.state
        return BookResource(
                isbn = bookRecord.book.isbn.toString(),
                title = bookRecord.book.title.toString(),
                borrowed = when (bookState) {
                    is Available -> null
                    is Borrowed -> toBorrowedState(bookState)
                }
        )
    }

    private fun toBorrowedState(bookState: Borrowed) =
            BorrowedState(by = bookState.by.toString(), on = bookState.on.toString())

    private fun addLinks(resource: BookResource, bookRecord: BookRecord) {
        val bookId = bookRecord.id
        val bookState = bookRecord.state

        resource.add(linkTo(booksController).slash(bookId).withSelfRel())

        if (currentUser.isCurator()) {
            resource.add(linkTo(booksController).slash(bookId).withRel("delete"))
        }

        if (bookState is Available) {
            resource.add(linkTo(booksController).slash(bookId).slash("borrow").withRel("borrow"))
        } else {
            resource.add(linkTo(booksController).slash(bookId).slash("return").withRel("return"))
        }
    }

}