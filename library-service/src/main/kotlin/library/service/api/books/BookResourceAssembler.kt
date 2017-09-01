package library.service.api.books

import library.service.api.books.BookResource.BorrowedState
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.BookId
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
class BookResourceAssembler
    : ResourceAssemblerSupport<BookRecord, BookResource>(BooksController::class.java, BookResource::class.java) {

    private val booksController = BooksController::class.java

    override fun toResource(bookRecord: BookRecord): BookResource {
        val book = bookRecord.book

        val isbn = book.isbn.toString()
        val title = book.title.toString()
        val resource = BookResource(isbn, title)

        val bookId = bookRecord.id
        val bookState = bookRecord.state
        when (bookState) {
            is Borrowed -> handleBorrowedState(resource, bookId, bookState)
            is Available -> handleAvailableState(resource, bookId)
        }
        resource.add(linkTo(booksController).slash(bookId).withSelfRel())
        resource.add(linkTo(booksController).slash(bookId).withRel("delete"))

        return resource
    }

    private fun handleBorrowedState(resource: BookResource, bookId: BookId, bookState: Borrowed) {
        val borrowedBy = bookState.by.toString()
        val borrowedOn = bookState.on.toString()
        resource.borrowed = BorrowedState(borrowedBy, borrowedOn)
        resource.add(linkTo(booksController).slash(bookId).slash("return").withRel("return"))
    }

    private fun handleAvailableState(resource: BookResource, bookId: BookId) {
        resource.add(linkTo(booksController).slash(bookId).slash("borrow").withRel("borrow"))
    }

}