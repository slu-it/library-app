package library.service.business.books.domain

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.BookState
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
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
            assertThrows(BookAlreadyReturnedException::class) {
                availableBook.`return`()
            }
        }

    }

    @Nested inner class `given a 'borrowed' book` {

        val borrowedBook = BookRecord(bookId, book, borrowed)

        @Test fun `it can be returned in order to make it 'available' again`() {
            borrowedBook.`return`()
            assertThat(borrowedBook.state).isEqualTo(Available)
        }

        @Test fun `trying to borrow it will throw an exception`() {
            assertThrows(BookAlreadyBorrowedException::class) {
                borrowedBook.borrow(borrowed.by, borrowed.on)
            }
        }

    }

    @Nested inner class `book records can be checked for equality` {

        @Test fun `two records with the same data are equal`() {
            val id = BookId.generate()
            val bookRecord1 = BookRecord(id, Books.THE_LORD_OF_THE_RINGS_1, Available)
            val bookRecord2 = BookRecord(id, Books.THE_LORD_OF_THE_RINGS_1, Available)
            assertThat(bookRecord1).isEqualTo(bookRecord2)
        }

        @Nested inner class `two records with different data are unequal` {

            val id = BookId.generate()
            val anotherId = BookId.generate()

            @Test fun `id`() {
                assertThat(bookRecord(id = id))
                        .isNotEqualTo(bookRecord(id = anotherId))
            }

            @Test fun `book`() {
                assertThat(bookRecord(book = Books.THE_LORD_OF_THE_RINGS_1))
                        .isNotEqualTo(bookRecord(book = Books.THE_LORD_OF_THE_RINGS_2))
            }

            @Test fun `state`() {
                assertThat(bookRecord(state = Available))
                        .isNotEqualTo(bookRecord(state = Borrowed(Borrower("Frodo"), OffsetDateTime.now())))
            }

            fun bookRecord(
                    id: BookId = this.id,
                    book: Book = Books.THE_LORD_OF_THE_RINGS_1,
                    state: BookState = Available
            ) = BookRecord(id, book, state)

        }

    }

}