package library.service.business.books.domain

import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.BookState
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

    @Nested inner class `certain book properties can be changed` {

        val book = Book(
                isbn = Isbn13("0123456789123"),
                title = Title("Original Book"),
                authors = listOf(Author("Original Author")),
                numberOfPages = 128
        )
        val bookRecord = BookRecord(id = BookId.generate(), book = book)

        @Test fun `title can be changed`(): Unit = with(bookRecord) {
            changeTitle(Title("New Title"))
            assertThat(book.title).isEqualTo(Title("New Title"))
        }

        @Test fun `authors can be changed`(): Unit = with(bookRecord) {
            changeAuthors(listOf(Author("New Author")))
            assertThat(book.authors).containsExactly(Author("New Author"))
        }

        @Test fun `authors can be removed`(): Unit = with(bookRecord) {
            changeAuthors(emptyList())
            assertThat(book.authors).isEmpty()
        }

        @Test fun `number of pages can be changed`(): Unit = with(bookRecord) {
            changeNumberOfPages(256)
            assertThat(book.numberOfPages).isEqualTo(256)
        }

        @Test fun `number of pages can be removed`(): Unit = with(bookRecord) {
            changeNumberOfPages(null)
            assertThat(book.numberOfPages).isNull()
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