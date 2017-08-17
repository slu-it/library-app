package library.service.business.books

import library.service.business.books.domain.Book
import library.service.business.books.domain.PersistedBook
import library.service.business.books.domain.types.Borrower
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import library.service.business.books.exceptions.BookNotFoundException
import library.service.common.logging.LogMethodEntryAndExit
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

@Service
@LogMethodEntryAndExit
class BookService(
        private val clock: Clock,
        private val persistenceService: BookPersistenceService
) {

    fun createBook(book: Book): PersistedBook {
        return persistenceService.create(book)
    }

    @Throws(BookNotFoundException::class)
    fun getBook(id: UUID): PersistedBook {
        return persistenceService.findById(id) ?: throw BookNotFoundException(id)
    }

    fun getBooks(): List<PersistedBook> {
        return persistenceService.findAll()
    }

    @Throws(BookNotFoundException::class)
    fun deleteBook(id: UUID) {
        val book = getBook(id)
        persistenceService.delete(book)
    }

    @Throws(BookNotFoundException::class, BookAlreadyBorrowedException::class)
    fun borrowBook(id: UUID, borrower: Borrower): PersistedBook {
        val book = getBook(id)
        book.borrow(borrower, OffsetDateTime.now(clock))
        return persistenceService.update(book)
    }

    @Throws(BookNotFoundException::class, BookAlreadyReturnedException::class)
    fun returnBook(id: UUID): PersistedBook {
        val book = getBook(id)
        book.`return`()
        return persistenceService.update(book)
    }

}