package library.service.business.books

import com.nhaarman.mockito_kotlin.*
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.events.BookAdded
import library.service.business.books.domain.events.BookBorrowed
import library.service.business.books.domain.events.BookRemoved
import library.service.business.books.domain.events.BookReturned
import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.*
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import library.service.business.books.exceptions.BookNotFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.UnitTest
import utils.junit.assertThrows
import java.time.Clock
import java.time.OffsetDateTime
import java.time.ZoneId

@UnitTest
internal class BookCollectionTest {

    val fixedTimestamp = OffsetDateTime.parse("2017-09-23T12:34:56.789Z")

    val clock: Clock = Clock.fixed(fixedTimestamp.toInstant(), ZoneId.of("UTC"))
    val dataStore: BookDataStore = mock()
    val eventDispatcher: BookEventDispatcher = mock()

    val cut = BookCollection(clock, dataStore, eventDispatcher)

    @Nested inner class `adding a book` {

        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val createdBook = BookRecord(id, book)

        @Test fun `creates a new record in the data store`() {
            given { dataStore.create(book) } willReturn { createdBook }
            val addedBook = cut.addBook(book)
            assertThat(addedBook).isEqualTo(createdBook)
        }

        @Test fun `dispatches a BookAdded event`() {
            given { dataStore.create(book) } willReturn { createdBook }
            cut.addBook(book)
            verify(eventDispatcher).dispatch(check<BookAdded> {
                assertThat(it.bookId).isEqualTo(id)
                assertThat(it.timestamp).isEqualTo(fixedTimestamp)
            })
        }

        @Test fun `does not dispatch any events in case of an exception`() {
            given { dataStore.create(book) } willThrow { RuntimeException() }
            assertThrows(RuntimeException::class) {
                cut.addBook(book)
            }
            verifyZeroInteractions(eventDispatcher)
        }

    }

    @Nested inner class `getting a book` {

        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookRecord = BookRecord(id, book)

        @Test fun `returns it if it was found in data store`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            val gotBook = cut.getBook(id)
            assertThat(gotBook).isEqualTo(bookRecord)
        }

        @Test fun `throws exception if it was not found in data store`() {
            given { dataStore.findById(id) } willReturn { null }
            assertThrows(BookNotFoundException::class) {
                cut.getBook(id)
            }
        }

    }

    @Nested inner class `getting all books` {

        @Test fun `delegates directly to data store`() {
            val bookRecord1 = BookRecord(BookId.generate(), Book(Isbn13("0123456789012"), Title("Hello World #1")))
            val bookRecord2 = BookRecord(BookId.generate(), Book(Isbn13("1234567890123"), Title("Hello World #2")))
            given { dataStore.findAll() } willReturn { listOf(bookRecord1, bookRecord2) }
            val allBooks = cut.getAllBooks()
            assertThat(allBooks).containsExactly(bookRecord1, bookRecord2)
        }

    }

    @Nested inner class `removing a book` {

        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookRecord = BookRecord(id, book)

        @Test fun `deletes it from the data store if found`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            cut.removeBook(id)
            verify(dataStore).delete(bookRecord)
        }

        @Test fun `dispatches a BookRemoved event`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            cut.removeBook(id)
            verify(eventDispatcher).dispatch(check<BookRemoved> {
                assertThat(it.bookId).isEqualTo(id)
                assertThat(it.timestamp).isEqualTo(fixedTimestamp)
            })
        }

        @Test fun `throws exception if it was not found in data store`() {
            given { dataStore.findById(id) } willReturn { null }
            assertThrows(BookNotFoundException::class) {
                cut.removeBook(id)
            }
        }

        @Test fun `does not dispatch any events in case of an exception`() {
            given { dataStore.findById(id) } willThrow { RuntimeException() }
            assertThrows(RuntimeException::class) {
                cut.removeBook(id)
            }
            verifyZeroInteractions(eventDispatcher)
        }

    }

    @Nested inner class `borrowing a book` {

        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookRecord = BookRecord(id, book)

        @Test fun `changes its state and updates it in the data store`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            given { dataStore.update(bookRecord) } willReturn { bookRecord }
            val borrowedBook = cut.borrowBook(id, Borrower("Someone"))
            assertThat(borrowedBook.state).isInstanceOf(Borrowed::class.java)
            assertThat(borrowedBook).isSameAs(bookRecord)
        }

        @Test fun `dispatches a BookBorrowed event`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            given { dataStore.update(bookRecord) } willReturn { bookRecord }
            cut.borrowBook(id, Borrower("Someone"))
            verify(eventDispatcher).dispatch(check<BookBorrowed> {
                assertThat(it.bookId).isEqualTo(id)
                assertThat(it.timestamp).isEqualTo(fixedTimestamp)
            })
        }

        @Test fun `throws exception if it was not found in data store`() {
            given { dataStore.findById(id) } willReturn { null }
            assertThrows(BookNotFoundException::class) {
                cut.borrowBook(id, Borrower("Someone"))
            }
        }

        @Test fun `throws exception if it is already 'borrowed'`() {
            bookRecord.borrow(Borrower("Someone"), OffsetDateTime.now())
            given { dataStore.findById(id) } willReturn { bookRecord }
            assertThrows(BookAlreadyBorrowedException::class) {
                cut.borrowBook(id, Borrower("Someone Else"))
            }
        }

        @Test fun `does not dispatch any events in case of an exception`() {
            given { dataStore.findById(id) } willThrow { RuntimeException() }
            assertThrows(RuntimeException::class) {
                cut.borrowBook(id, Borrower("Someone Else"))
            }
            verifyZeroInteractions(eventDispatcher)
        }

    }

    @Nested inner class `returning a book` {

        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookRecord = BookRecord(id, book).apply {
            borrow(Borrower("Someone"), OffsetDateTime.now())
        }

        @Test fun `changes its state and updates it in the data store`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            given { dataStore.update(bookRecord) } willReturn { bookRecord }
            val returnedBook = cut.returnBook(id)
            assertThat(returnedBook.state).isEqualTo(Available)
            assertThat(returnedBook).isSameAs(bookRecord)
        }

        @Test fun `dispatches a BookReturned event`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            given { dataStore.update(bookRecord) } willReturn { bookRecord }
            cut.returnBook(id)
            verify(eventDispatcher).dispatch(check<BookReturned> {
                assertThat(it.bookId).isEqualTo(id)
                assertThat(it.timestamp).isEqualTo(fixedTimestamp)
            })
        }

        @Test fun `throws exception if it was not found in data store`() {
            given { dataStore.findById(id) } willReturn { null }
            assertThrows(BookNotFoundException::class) {
                cut.returnBook(id)
            }
        }

        @Test fun `throws exception if it is already 'returned'`() {
            given { dataStore.findById(id) } willReturn { bookRecord }
            bookRecord.`return`()
            assertThrows(BookAlreadyReturnedException::class.java, {
                cut.returnBook(id)
            })
        }

        @Test fun `does not dispatch any events in case of an exception`() {
            given { dataStore.findById(id) } willThrow { RuntimeException() }
            assertThrows(RuntimeException::class) {
                cut.returnBook(id)
            }
            verifyZeroInteractions(eventDispatcher)
        }

    }

}