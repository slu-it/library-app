package library.service.business.books.domain

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.*
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Books
import utils.assertThrows
import utils.classification.UnitTest
import java.time.OffsetDateTime

@UnitTest
internal class BookRecordTest {

    val book = Books.A_KNIGHT_OF_THE_SEVEN_KINGDOMS
    val bookId = BookId.generate()

    @Test fun `books are initialized as 'available'`() {
        val minimalBook = BookRecord(bookId, book)
        assertThat(minimalBook.state).isEqualTo(Available)
    }

    @Nested inner class `given an 'available' book` {

        val availableBook = BookRecord(bookId, book, Available)

        @Test fun `it can be 'borrowed'`() {
            val borrowed = Borrowed(Borrower("Duncan the Tall"), OffsetDateTime.now())
            val borrowedBook = availableBook.borrow(borrowed.by, borrowed.on)
            assertThat(borrowedBook.state).isEqualTo(borrowed)
        }

        @Test fun `trying to return it will throw an exception`() {
            assertThrows(BookAlreadyReturnedException::class) {
                availableBook.`return`()
            }
        }

    }

    @Nested inner class `given a 'borrowed' book` {

        val borrowed = Borrowed(Borrower("Duncan the Tall"), OffsetDateTime.now())
        val borrowedBook = BookRecord(bookId, book, borrowed)

        @Test fun `it can be returned in order to make it 'available' again`() {
            val returnedBook = borrowedBook.`return`()
            assertThat(returnedBook.state).isEqualTo(Available)
        }

        @Test fun `trying to borrow it will throw an exception`() {
            assertThrows(BookAlreadyBorrowedException::class) {
                borrowedBook.borrow(borrowed.by, borrowed.on)
            }
        }

    }

    @Nested inner class `certain book properties can be changed` {

        val book = Book(
                isbn = Isbn13("0123456789123"),
                title = Title("Original Book"),
                authors = listOf(Author("Original Author")),
                numberOfPages = 128
        )
        val bookRecord = BookRecord(id = BookId.generate(), book = book)

        @Test fun `title can be changed`() {
            val changedBook = bookRecord.changeTitle(Title("New Title"))
            assertThat(changedBook.book.title).isEqualTo(Title("New Title"))
        }

        @Test fun `authors can be changed`() {
            val changedBook = bookRecord.changeAuthors(listOf(Author("New Author")))
            assertThat(changedBook.book.authors).containsExactly(Author("New Author"))
        }

        @Test fun `authors can be removed`() {
            val changedBook = bookRecord.changeAuthors(emptyList())
            assertThat(changedBook.book.authors).isEmpty()
        }

        @Test fun `number of pages can be changed`() {
            val changedBook = bookRecord.changeNumberOfPages(256)
            assertThat(changedBook.book.numberOfPages).isEqualTo(256)
        }

        @Test fun `number of pages can be removed`() {
            val changedBook = bookRecord.changeNumberOfPages(null)
            assertThat(changedBook.book.numberOfPages).isNull()
        }

    }

}