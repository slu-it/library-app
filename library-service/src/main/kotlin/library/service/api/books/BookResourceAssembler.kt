package library.service.api.books

import library.service.api.books.BookResource.Borrowed
import library.service.business.books.domain.PersistedBook
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ResourceAssemblerSupport
import org.springframework.stereotype.Component

@Component
class BookResourceAssembler
    : ResourceAssemblerSupport<PersistedBook, BookResource>(BooksController::class.java, BookResource::class.java) {

    private val booksController = BooksController::class.java

    override fun toResource(persistedBook: PersistedBook): BookResource {
        val id = persistedBook.id
        val book = persistedBook.book
        val borrowedState = persistedBook.borrowed

        val resource = BookResource(isbn = book.isbn.value, title = book.title.value)
        resource.add(linkTo(booksController).slash(id).withSelfRel())

        if (borrowedState != null) {
            val by = borrowedState.by.value
            val on = borrowedState.on.toString()
            resource.borrowed = Borrowed(by, on)
            resource.add(linkTo(booksController).slash(id).slash("return").withRel("return"))
        } else {
            resource.add(linkTo(booksController).slash(id).slash("borrow").withRel("borrow"))
        }

        return resource
    }

}