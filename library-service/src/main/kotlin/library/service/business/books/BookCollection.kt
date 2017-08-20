package library.service.business.books

import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.types.Book
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
class BookCollection(
        private val clock: Clock,
        private val dataStore: BookDataStore
) {

    fun addBook(book: Book): BookEntity {
        return dataStore.create(book)
    }

    @Throws(BookNotFoundException::class)
    fun getBook(id: UUID): BookEntity {
        return dataStore.findById(id) ?: throw BookNotFoundException(id)
    }

    fun getAllBooks(): List<BookEntity> {
        return dataStore.findAll()
    }

    @Throws(BookNotFoundException::class)
    fun removeBook(id: UUID) {
        val book = getBook(id)
        dataStore.delete(book)
    }

    @Throws(BookNotFoundException::class, BookAlreadyBorrowedException::class)
    fun borrowBook(id: UUID, borrower: Borrower): BookEntity {
        val book = getBook(id)
        book.borrow(borrower, OffsetDateTime.now(clock))
        return dataStore.update(book)
    }

    @Throws(BookNotFoundException::class, BookAlreadyReturnedException::class)
    fun returnBook(id: UUID): BookEntity {
        val book = getBook(id)
        book.`return`()
        return dataStore.update(book)
    }

}