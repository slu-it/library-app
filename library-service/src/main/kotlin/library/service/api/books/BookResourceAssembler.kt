package library.service.api.books

import library.service.api.books.BookResource.Borrowed
import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.states.BookState
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component

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

        if (bookState is BookState.Borrowed) {
            val by = bookState.by.value
            val on = bookState.on.toString()
            resource.borrowed = Borrowed(by, on)
            resource.add(linkTo(booksController).slash(bookId).slash("return").withRel("return"))
        } else {
            resource.add(linkTo(booksController).slash(bookId).slash("borrow").withRel("borrow"))
        }

        return resource
    }

}