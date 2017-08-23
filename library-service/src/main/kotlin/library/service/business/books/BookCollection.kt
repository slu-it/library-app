package library.service.business.books

import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import library.service.business.books.exceptions.BookNotFoundException
import library.service.common.logging.LogMethodEntryAndExit
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.OffsetDateTime

/**
 * This represents the book collection of this library application instance.
 *
 * It offers functions for common actions taken with a collection of books:
 *
 * - adding books
 * - finding books
 * - deleting books
 * - borrowing & returning books
 */
@Service
@LogMethodEntryAndExit
class BookCollection(
        private val clock: Clock,
        private val dataStore: BookDataStore
) {

    /**
     * Adds the given [Book] to the collection.
     *
     * The [Book] is stored in the collection's [BookDataStore] for future
     * usage.
     *
     * @param book the book to add to the collection
     * @return the [BookEntity] containing the unique ID of the stored book
     */
    fun addBook(book: Book): BookEntity {
        return dataStore.create(book)
    }

    /**
     * Gets a [BookEntity] from the collection by its unique ID.
     *
     * The [BookEntity] is looked up in the collection's [BookDataStore]. If
     * the [BookDataStore] does not contain a book for that ID, an exception
     * is thrown.
     *
     * @param id the unique ID to look up
     * @return the [BookEntity] for the given ID
     * @throws BookNotFoundException in case there is no book for the given ID
     */
    fun getBook(id: BookId): BookEntity {
        return dataStore.findById(id) ?: throw BookNotFoundException(id)
    }

    /**
     * Gets a list of all [BookEntity] currently part of this collection.
     *
     * The books are looked up in the collection's [BookDataStore]. If there
     * are no books in the data store, an empty list is returned.
     *
     * @return a list of all [BookEntity]
     */
    fun getAllBooks(): List<BookEntity> {
        return dataStore.findAll()
    }

    /**
     * Removes a [BookEntity] from the collection by its unique ID.
     *
     * The book needs to exist in the collection's [BookDataStore] in order to
     * remove it. If there is no book for the given ID an exception is thrown.
     *
     * @param id the unique ID of the book to delete
     * @throws BookNotFoundException in case there is no book for the given ID
     */
    fun removeBook(id: BookId) {
        val book = getBook(id)
        dataStore.delete(book)
    }

    /**
     * Tries to borrow a [BookEntity] with the given unique ID for the given
     * [Borrower].
     *
     * The book needs to exist in the collection's [BookDataStore] in order to
     * borrow it. It also needs to be _available_ for borrowing. If either of
     * those conditions is not met, an exception is thrown.
     *
     * @param id the unique ID of the book to borrow
     * @param borrower the [Borrower] who is trying to borrow the book
     * @return the borrowed and updated [BookEntity] instance
     * @throws BookNotFoundException in case there is no book for the given ID
     * @throws BookAlreadyBorrowedException in case the book is already borrowed
     */
    fun borrowBook(id: BookId, borrower: Borrower): BookEntity {
        val book = getBook(id)
        book.borrow(borrower, OffsetDateTime.now(clock))
        return dataStore.update(book)
    }

    /**
     * Tries to return a [BookEntity] with the given unique ID.
     *
     * The book needs to exist in the collection's [BookDataStore] in order to
     * return it. It also needs to be currently _borrowed_. If either of those
     * conditions is not met, an exception is thrown.
     *
     * @param id the unique ID of the book to borrow
     * @return the returned and updated [BookEntity] instance
     * @throws BookNotFoundException in case there is no book for the given ID
     * @throws BookAlreadyReturnedException in case the book is already returned
     */
    fun returnBook(id: BookId): BookEntity {
        val book = getBook(id)
        book.`return`()
        return dataStore.update(book)
    }

}