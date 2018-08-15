package library.service.api.books

import library.service.api.books.payload.*
import library.service.business.books.BookCollection
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.*
import library.service.logging.LogMethodEntryAndExit
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
        val allBookRecords = collection.getAllBooks()
        val selfLink = linkTo(methodOn(javaClass).getBooks()).withSelfRel()
        val bookResources = assembler.toResources(allBookRecords)
        return Resources(bookResources, selfLink)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun postBook(@Valid @RequestBody body: CreateBookRequest): BookResource {
        val book = Book(
                isbn = Isbn13.parse(body.isbn!!),
                title = Title(body.title!!),
                authors = emptyList(),
                numberOfPages = null
        )
        val bookRecord = collection.addBook(book)
        return assembler.toResource(bookRecord)
    }

    @PutMapping("/{id}/title")
    @ResponseStatus(HttpStatus.OK)
    fun putBookTitle(@PathVariable id: UUID, @Valid @RequestBody body: UpdateTitleRequest): BookResource {
        val bookRecord = collection.updateBook(BookId(id)) {
            it.changeTitle(Title(body.title!!))
        }
        return assembler.toResource(bookRecord)
    }

    @PutMapping("/{id}/authors")
    @ResponseStatus(HttpStatus.OK)
    fun putBookAuthors(@PathVariable id: UUID, @Valid @RequestBody body: UpdateAuthorsRequest): BookResource {
        val bookRecord = collection.updateBook(BookId(id)) {
            it.changeAuthors(body.authors!!.map { Author(it) })
        }
        return assembler.toResource(bookRecord)
    }

    @DeleteMapping("/{id}/authors")
    @ResponseStatus(HttpStatus.OK)
    fun deleteBookAuthors(@PathVariable id: UUID): BookResource {
        val bookRecord = collection.updateBook(BookId(id)) {
            it.changeAuthors(emptyList())
        }
        return assembler.toResource(bookRecord)
    }

    @PutMapping("/{id}/numberOfPages")
    @ResponseStatus(HttpStatus.OK)
    fun putBookNumberOfPages(@PathVariable id: UUID, @Valid @RequestBody body: UpdateNumberOfPagesRequest): BookResource {
        val bookRecord = collection.updateBook(BookId(id)) {
            it.changeNumberOfPages(body.numberOfPages)
        }
        return assembler.toResource(bookRecord)
    }

    @DeleteMapping("/{id}/numberOfPages")
    @ResponseStatus(HttpStatus.OK)
    fun deleteBookNumberOfPages(@PathVariable id: UUID): BookResource {
        val bookRecord = collection.updateBook(BookId(id)) {
            it.changeNumberOfPages(null)
        }
        return assembler.toResource(bookRecord)
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getBook(@PathVariable id: UUID): BookResource {
        val bookRecord = collection.getBook(BookId(id))
        return assembler.toResource(bookRecord)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteBook(@PathVariable id: UUID) {
        collection.removeBook(BookId(id))
    }

    @PostMapping("/{id}/borrow")
    @ResponseStatus(HttpStatus.OK)
    fun postBorrowBook(@PathVariable id: UUID, @Valid @RequestBody body: BorrowBookRequest): BookResource {
        val bookRecord = collection.borrowBook(BookId(id), Borrower(body.borrower!!))
        return assembler.toResource(bookRecord)
    }

    @PostMapping("/{id}/return")
    @ResponseStatus(HttpStatus.OK)
    fun postReturnBook(@PathVariable id: UUID): BookResource {
        val bookRecord = collection.returnBook(BookId(id))
        return assembler.toResource(bookRecord)
    }

}