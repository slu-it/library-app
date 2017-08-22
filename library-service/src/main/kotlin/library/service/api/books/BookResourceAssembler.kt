package library.service.api.books

import library.service.api.books.BookResource.BorrowedState
import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component
import java.util.*

/**
 * Component responsible for converting a [BookEntity] into a [BookResource].
 *
 * This includes transforming the data from one class to another and adding the
 * correct links depending on the [BookEntity] state.
 */
@Component
class BookResourceAssembler
    : ResourceAssemblerSupport<BookEntity, BookResource>(BooksController::class.java, BookResource::class.java) {

    private val booksController = BooksController::class.java

    override fun toResource(bookEntity: BookEntity): BookResource {
        val book = bookEntity.book
        val bookId = bookEntity.id
        val bookState = bookEntity.state

        val resource = BookResource(isbn = book.isbn.value, title = book.title.value)
        resource.add(linkTo(booksController).slash(bookId).withSelfRel())

        when (bookState) {
            is Borrowed -> handleBorrowedState(resource, bookId, bookState)
            is Available -> handleAvailableState(resource, bookId)
        }

        return resource
    }

    private fun handleBorrowedState(resource: BookResource, bookId: UUID, bookState: Borrowed) {
        resource.borrowed = BorrowedState(by = bookState.by.value, on = bookState.on.toString())
        resource.add(linkTo(booksController).slash(bookId).slash("return").withRel("return"))
    }

    private fun handleAvailableState(resource: BookResource, bookId: UUID) {
        resource.add(linkTo(booksController).slash(bookId).slash("borrow").withRel("borrow"))
    }

}