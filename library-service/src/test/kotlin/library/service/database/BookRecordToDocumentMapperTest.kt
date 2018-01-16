package library.service.database

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.states.Available
import library.service.business.books.domain.states.Borrowed
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Books
import utils.classification.UnitTest
import java.time.OffsetDateTime


@UnitTest
internal class BookRecordToDocumentMapperTest {

    val cut = BookRecordToDocumentMapper()

    @Test fun `mapping uses the record's id as a UUID`() {
        val bookId = BookId.generate()
        val bookRecord = BookRecord(bookId, Books.THE_LORD_OF_THE_RINGS_1)
        with(cut.map(bookRecord)) {
            assertThat(id).isEqualTo(bookId.toUuid())
        }
    }

    @Test fun `mapping uses string representations of the record's book data`() {
        val bookId = BookId.generate()
        val book = Books.THE_LORD_OF_THE_RINGS_2
        with(cut.map(BookRecord(bookId, book))) {
            assertThat(isbn).isEqualTo("9780261102361")
            assertThat(title).isEqualTo("The Lord of the Rings 2. The Two Towers")
            assertThat(authors).containsExactly("J.R.R. Tolkien")
        }
    }

    @Nested inner class `handling of 'borrowed' state` {

        @Test fun `mapping sets borrowed to null if record has 'available' state`() {
            val bookId = BookId.generate()
            val book = Books.THE_LORD_OF_THE_RINGS_3
            with(cut.map(BookRecord(bookId, book, Available))) {
                assertThat(borrowed).isNull()
            }
        }

        @Test fun `mapping sets borrowed if record has 'borrowed' state`() {
            val bookId = BookId.generate()
            val book = Books.THE_LORD_OF_THE_RINGS_3
            val state = Borrowed(Borrower("Frodo"), OffsetDateTime.parse("2017-12-16T12:34:56.789Z"))
            with(cut.map(BookRecord(bookId, book, state))) {
                assertThat(borrowed).isNotNull()
                assertThat(borrowed?.by).isEqualTo("Frodo")
                assertThat(borrowed?.on).isEqualTo("2017-12-16T12:34:56.789Z")
            }
        }

        @Test fun `mapping converts borrowed timestamp to UTC timezone`() {
            val bookId = BookId.generate()
            val book = Books.THE_LORD_OF_THE_RINGS_3
            val state = Borrowed(Borrower("Frodo"), OffsetDateTime.parse("2017-12-16T12:34:56.789+01:00"))
            with(cut.map(BookRecord(bookId, book, state))) {
                assertThat(borrowed?.on).isEqualTo("2017-12-16T11:34:56.789Z")
            }
        }

    }

}