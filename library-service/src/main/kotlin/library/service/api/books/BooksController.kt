package library.service.api.books

import library.service.api.books.payload.BorrowBookRequestBody
import library.service.api.books.payload.CreateBookRequestBody
import library.service.business.books.BookService
import library.service.business.books.domain.Book
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.common.logging.LogMethodEntryAndExit
import org.springframework.hateoas.Resources
import org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo
import org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.validation.Valid

@Validated
@RestController
@RequestMapping("/api/books")
@LogMethodEntryAndExit
class BooksController(
        private val service: BookService,
        private val assembler: BookResourceAssembler
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getBooks(): Resources<BookResource> {
        val allBooks = service.getBooks()
        val selfLink = linkTo(methodOn(javaClass).getBooks()).withSelfRel()
        return Resources(assembler.toResources(allBooks), selfLink)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postBook(@Valid @RequestBody body: CreateBookRequestBody): BookResource {
        val book = Book(Isbn13.parse(body.isbn!!), Title(body.title!!))
        val persistedBook = service.createBook(book)
        return assembler.toResource(persistedBook)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getBook(@PathVariable id: UUID): BookResource {
        val book = service.getBook(id)
        return assembler.toResource(book)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable id: UUID) {
        service.deleteBook(id)
    }

    @PostMapping("/{id}/borrow")
    @ResponseStatus(HttpStatus.OK)
    fun borrowBook(@PathVariable id: UUID, @Valid @RequestBody body: BorrowBookRequestBody): BookResource {
        val book = service.borrowBook(id, Borrower(body.borrower!!))
        return assembler.toResource(book)
    }

    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    fun returnBook(@PathVariable id: UUID): BookResource {
        val book = service.returnBook(id)
        return assembler.toResource(book)
    }

}