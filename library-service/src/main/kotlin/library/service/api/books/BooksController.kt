package library.service.api.books

import library.service.api.books.payload.BorrowBookRequestBody
import library.service.api.books.payload.CreateBookRequestBody
import library.service.business.books.BookCollection
import library.service.business.books.domain.types.*
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
@CrossOrigin
@RequestMapping("/api/books")
@LogMethodEntryAndExit
class BooksController(
        private val collection: BookCollection,
        private val assembler: BookResourceAssembler
) {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun getBooks(): Resources<BookResource> {
        val allBooks = collection.getAllBooks()
        val selfLink = linkTo(methodOn(javaClass).getBooks()).withSelfRel()
        val bookResources = assembler.toResources(allBooks)
        return Resources(bookResources, selfLink)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postBook(@Valid @RequestBody body: CreateBookRequestBody): BookResource {
        val book = Book(Isbn13.parse(body.isbn!!), Title(body.title!!))
        val persistedBook = collection.addBook(book)
        return assembler.toResource(persistedBook)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getBook(@PathVariable id: UUID): BookResource {
        val book = collection.getBook(BookId(id))
        return assembler.toResource(book)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable id: UUID) {
        collection.removeBook(BookId(id))
    }

    @PostMapping("/{id}/borrow")
    @ResponseStatus(HttpStatus.OK)
    fun postBorrowBook(@PathVariable id: UUID, @Valid @RequestBody body: BorrowBookRequestBody): BookResource {
        val book = collection.borrowBook(BookId(id), Borrower(body.borrower!!))
        return assembler.toResource(book)
    }

    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    fun postReturnBook(@PathVariable id: UUID): BookResource {
        val book = collection.returnBook(BookId(id))
        return assembler.toResource(book)
    }

}