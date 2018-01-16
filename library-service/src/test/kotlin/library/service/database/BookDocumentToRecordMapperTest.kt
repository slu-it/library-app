package library.service.database

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
import java.util.*


@UnitTest
internal class BookDocumentToRecordMapperTest {

    val availableBookDocument = BookDocument(
            id = UUID.randomUUID(),
            isbn = "9780091956141",
            title = "The Martian",
            authors = listOf("Andy Weir"),
            numberOfPages = 384,
            borrowed = null
    )
    val borrowedBookDocument = BookDocument(
            id = UUID.randomUUID(),
            isbn = "9780091956141",
            title = "The Martian",
            authors = listOf("Andy Weir"),
            numberOfPages = 384,
            borrowed = BorrowedState(
                    by = "Mark Watney",
                    on = "2035-12-16T12:34:56.789Z"
            )
    )

    val cut = BookDocumentToRecordMapper()

    @Test fun `mapping converts representations of the document's data to value types`() {
        with(cut.map(availableBookDocument)) {
            assertThat(id).isEqualTo(BookId(availableBookDocument.id))
            assertThat(book).isEqualTo(Books.THE_MARTIAN)
        }
    }

    @Nested inner class `handling of book states` {

        @Test fun `mapping sets 'available' state if borrowed value is null`() {
            with(cut.map(availableBookDocument)) {
                assertThat(state).isEqualTo(Available)
            }
        }

        @Test fun `mapping sets 'borrowed' state if borrowed value is set`() {
            with(cut.map(borrowedBookDocument)) {
                with(state as Borrowed) {
                    assertThat(by).isEqualTo(Borrower("Mark Watney"))
                    assertThat(on).isEqualTo(OffsetDateTime.parse("2035-12-16T12:34:56.789Z"))
                }
            }
        }

    }

}