package library.service.business.books.domain

import library.service.business.books.domain.states.BookState.Available
import library.service.business.books.domain.states.BookState.Borrowed
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.business.books.exceptions.BookAlreadyBorrowedException
import library.service.business.books.exceptions.BookAlreadyReturnedException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import utils.UnitTest
import java.time.OffsetDateTime
import java.util.*

@UnitTest
internal class BookEntityTest {

    @Test fun `book state is initialized as 'available'`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

        assertThat(bookEntity.state).isEqualTo(Available)
    }

    @Test fun `book state can be changed to 'borrowed' if 'available'`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

        val by = Borrower("Someone")
        val on = OffsetDateTime.now()
        bookEntity.borrow(by, on)

        assertThat(bookEntity.state).isEqualTo(Borrowed(by, on))
    }

    @Test fun `book state cannot be changed to 'borrowed' if already 'borrowed'`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

        val by = Borrower("Someone")
        val on = OffsetDateTime.now()
        bookEntity.borrow(by, on)

        assertThrows(BookAlreadyBorrowedException::class.java, {
            bookEntity.borrow(by, on)
        })
    }

    @Test fun `book state can be changed to 'available' if 'borrowed'`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

        bookEntity.borrow(Borrower("Someone"), OffsetDateTime.now())
        bookEntity.`return`()

        assertThat(bookEntity.state).isEqualTo(Available)
    }

    @Test fun `book state cannot be changed to 'available' if already 'available'`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

        assertThrows(BookAlreadyReturnedException::class.java, {
            bookEntity.`return`()
        })
    }

}