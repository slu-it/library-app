package library.service.business.books.domain

import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.*
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.UnitTest
import java.time.OffsetDateTime

@UnitTest
internal class BookRecordTest {

    val book = Book(Isbn13("9780007507672"), Title("A Knight of the Seven Kingdoms"))
    val bookId = BookId.generate()

    val borrowed = Borrowed(Borrower("Duncan the Tall"), OffsetDateTime.now())

    @Test fun `books are initialized as 'available'`() {
        val minimalBook = BookRecord(bookId, book)
        assertThat(minimalBook.state).isEqualTo(Available)
    }

    @Nested inner class `given an 'available' book` {

        val availableBook = BookRecord(bookId, book, Available)

        @Test fun `it can be 'borrowed'`() {
            availableBook.borrow(borrowed.by, borrowed.on)
            assertThat(availableBook.state).isEqualTo(borrowed)
        }

        @Test fun `trying to return it will throw an exception`() {
            assertThrows(BookAlreadyReturnedException::class.java, {
                availableBook.`return`()
            })
        }

    }

    @Nested inner class `given a 'borrowed' book` {

        val borrowedBook = BookRecord(bookId, book, borrowed)

        @Test fun `it can be returned in order to make it 'available' again`() {
            borrowedBook.`return`()
            assertThat(borrowedBook.state).isEqualTo(Available)
        }

        @Test fun `trying to borrow it will throw an exception`() {
            assertThrows(BookAlreadyBorrowedException::class.java, {
                borrowedBook.borrow(borrowed.by, borrowed.on)
            })
        }

    }

}