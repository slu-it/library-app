package library.service.business.books

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.*
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
        private val dataStore: BookDataStore,
        private val eventDispatcher: BookEventDispatcher
) {

    /**
     * Adds the given [Book] to the collection.
     *
     * The [Book] is stored in the collection's [BookDataStore] for future
     * usage.
     *
     * Produces a [BookAdded] domain event.
     *
     * @param book the book to add to the collection
     * @return the [BookRecord] containing the unique ID of the stored book
     */
    fun addBook(book: Book): BookRecord {
        val bookRecord = dataStore.create(book)

        bookAddedEvent(bookRecord).dispatch()
        return bookRecord
    }

    private fun bookAddedEvent(bookRecord: BookRecord) =
            BookAdded(timestamp = now(), bookId = bookRecord.id)

    /**
     * Gets a [BookRecord] from the collection by its unique ID.
     *
     * The [BookRecord] is looked up in the collection's [BookDataStore]. If
     * the [BookDataStore] does not contain a book for that ID, an exception
     * is thrown.
     *
     * @param id the unique ID to look up
     * @return the [BookRecord] for the given ID
     * @throws BookNotFoundException in case there is no book for the given ID
     */
    fun getBook(id: BookId): BookRecord {
        return dataStore.findById(id) ?: throw BookNotFoundException(id)
    }

    /**
     * Gets a list of all [BookRecord] currently part of this collection.
     *
     * The books are looked up in the collection's [BookDataStore]. If there
     * are no books in the data store, an empty list is returned.
     *
     * @return a list of all [BookRecord]
     */
    fun getAllBooks(): List<BookRecord> {
        return dataStore.findAll()
    }

    /**
     * Removes a [BookRecord] from the collection by its unique ID.
     *
     * The book needs to exist in the collection's [BookDataStore] in order to
     * remove it. If there is no book for the given ID an exception is thrown.
     *
     * Produces a [BookRemoved] domain event.
     *
     * @param id the unique ID of the book to delete
     * @throws BookNotFoundException in case there is no book for the given ID
     */
    fun removeBook(id: BookId) {
        val bookRecord = getBook(id)
        dataStore.delete(bookRecord)

        bookRemovedEvent(bookRecord).dispatch()
    }

    private fun bookRemovedEvent(bookRecord: BookRecord) =
            BookRemoved(timestamp = now(), bookId = bookRecord.id)

    /**
     * Tries to borrow a [BookRecord] with the given unique ID for the given
     * [Borrower].
     *
     * The book needs to exist in the collection's [BookDataStore] in order to
     * borrow it. It also needs to be _available_ for borrowing. If either of
     * those conditions is not met, an exception is thrown.
     *
     * Produces a [BookBorrowed] domain event.
     *
     * @param id the unique ID of the book to borrow
     * @param borrower the [Borrower] who is trying to borrow the book
     * @return the borrowed and updated [BookRecord] instance
     * @throws BookNotFoundException in case there is no book for the given ID
     * @throws BookAlreadyBorrowedException in case the book is already borrowed
     */
    fun borrowBook(id: BookId, borrower: Borrower): BookRecord {
        val bookRecord = getBook(id)
        bookRecord.borrow(borrower, now())
        val updatedBookRecord = dataStore.update(bookRecord)

        bookBorrowedEvent(bookRecord).dispatch()
        return updatedBookRecord
    }

    private fun bookBorrowedEvent(bookRecord: BookRecord) =
            BookBorrowed(timestamp = now(), bookId = bookRecord.id)

    /**
     * Tries to return a [BookRecord] with the given unique ID.
     *
     * The book needs to exist in the collection's [BookDataStore] in order to
     * return it. It also needs to be currently _borrowed_. If either of those
     * conditions is not met, an exception is thrown.
     *
     * Produces a [BookReturned] domain event.
     *
     * @param id the unique ID of the book to borrow
     * @return the returned and updated [BookRecord] instance
     * @throws BookNotFoundException in case there is no book for the given ID
     * @throws BookAlreadyReturnedException in case the book is already returned
     */
    fun returnBook(id: BookId): BookRecord {
        val bookRecord = getBook(id)
        bookRecord.`return`()
        val updatedBookRecord = dataStore.update(bookRecord)

        bookReturnedEvent(bookRecord).dispatch()
        return updatedBookRecord
    }

    private fun bookReturnedEvent(bookRecord: BookRecord) =
            BookReturned(timestamp = now(), bookId = bookRecord.id)

    private fun BookEvent.dispatch() = eventDispatcher.dispatch(this)
    private fun now() = OffsetDateTime.now(clock)

}