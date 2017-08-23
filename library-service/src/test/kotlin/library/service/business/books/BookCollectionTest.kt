package library.service.business.books

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.verify
import library.service.business.books.domain.BookEntity
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
import java.time.Clock
import java.time.OffsetDateTime

@UnitTest
internal class BookCollectionTest {

    val clock: Clock = Clock.systemDefaultZone()
    val dataStore: BookDataStore = mock()
    val cut = BookCollection(clock, dataStore)

    @Nested inner class `adding a book` {

        @Test fun `delegates directly to data store`() {
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(BookId.generate(), book)
            given { dataStore.create(book) }.willReturn(bookEntity)

            val addedBook = cut.addBook(book)

            assertThat(addedBook).isEqualTo(bookEntity)
        }

    }

    @Nested inner class `getting a book` {

        @Test fun `returns it if it was found in data store`() {
            val id = BookId.generate()
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(id, book)
            given { dataStore.findById(id) }.willReturn(bookEntity)

            val gotBook = cut.getBook(id)

            assertThat(gotBook).isEqualTo(bookEntity)
        }

        @Test fun `throws exception if it was not found in data store`() {
            val id = BookId.generate()
            given { dataStore.findById(id) }.willReturn(null)

            assertThrows(BookNotFoundException::class.java, {
                cut.getBook(id)
            })
        }

    }

    @Nested inner class `getting all books` {

        @Test fun `delegates directly to data store`() {
            val bookEntity1 = BookEntity(BookId.generate(), Book(Isbn13("0123456789012"), Title("Hello World #1")))
            val bookEntity2 = BookEntity(BookId.generate(), Book(Isbn13("1234567890123"), Title("Hello World #2")))
            given { dataStore.findAll() }.willReturn(listOf(bookEntity1, bookEntity2))

            val allBooks = cut.getAllBooks()

            assertThat(allBooks).containsExactly(bookEntity1, bookEntity2)
        }

    }

    @Nested inner class `removing a book` {

        @Test fun `deletes it from the data store if found`() {
            val id = BookId.generate()
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(id, book)
            given { dataStore.findById(id) }.willReturn(bookEntity)

            cut.removeBook(id)

            verify(dataStore).delete(bookEntity)
        }

        @Test fun `throws exception if it was not found in data store`() {
            val id = BookId.generate()
            given { dataStore.findById(id) }.willReturn(null)

            assertThrows(BookNotFoundException::class.java, {
                cut.removeBook(id)
            })
        }

    }

    @Nested inner class `borrowing a book` {

        @Test fun `changes its state and updates it in the data store`() {
            val id = BookId.generate()
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(id, book)
            given { dataStore.findById(id) }.willReturn(bookEntity)
            given { dataStore.update(bookEntity) }.willReturn(bookEntity)

            val borrowedBook = cut.borrowBook(id, Borrower("Someone"))

            assertThat(borrowedBook.state).isInstanceOf(Borrowed::class.java)
            assertThat(borrowedBook).isSameAs(bookEntity)
        }

        @Test fun `throws exception if it was not found in data store`() {
            val id = BookId.generate()
            given { dataStore.findById(id) }.willReturn(null)

            assertThrows(BookNotFoundException::class.java, {
                cut.borrowBook(id, Borrower("Someone"))
            })
        }

        @Test fun `throws exception if it is already 'borrowed'`() {
            val id = BookId.generate()
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(id, book)
            bookEntity.borrow(Borrower("Someone"), OffsetDateTime.now())
            given { dataStore.findById(id) }.willReturn(bookEntity)

            assertThrows(BookAlreadyBorrowedException::class.java, {
                cut.borrowBook(id, Borrower("Someone Else"))
            })
        }

    }

    @Nested inner class `returning a book` {

        @Test fun `changes its state and updates it in the data store`() {
            val id = BookId.generate()
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(id, book)
            bookEntity.borrow(Borrower("Someone"), OffsetDateTime.now())
            given { dataStore.findById(id) }.willReturn(bookEntity)
            given { dataStore.update(bookEntity) }.willReturn(bookEntity)

            val borrowedBook = cut.returnBook(id)
            assertThat(borrowedBook.state).isEqualTo(Available)
            assertThat(borrowedBook).isSameAs(bookEntity)
        }

        @Test fun `throws exception if it was not found in data store`() {
            val id = BookId.generate()
            given { dataStore.findById(id) }.willReturn(null)

            assertThrows(BookNotFoundException::class.java, {
                cut.returnBook(id)
            })
        }

        @Test fun `throws exception if it is already 'returning'`() {
            val id = BookId.generate()
            val book = Book(Isbn13("0123456789012"), Title("Hello World"))
            val bookEntity = BookEntity(id, book)
            given { dataStore.findById(id) }.willReturn(bookEntity)

            assertThrows(BookAlreadyReturnedException::class.java, {
                cut.returnBook(id)
            })
        }

    }

}