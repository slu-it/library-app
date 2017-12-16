package library.service.persistence.books

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.Books
import utils.classification.UnitTest


@UnitTest
internal class BookToDocumentMapperTest {

    val cut = BookToDocumentMapper()

    @Test fun `mapping generates a new ID`() {
        with(cut.map(Books.THE_LORD_OF_THE_RINGS_1)) {
            assertThat(id).isNotNull()
        }
    }

    @Test fun `mapping uses string representations of the book's value types`() {
        val book = Books.THE_LORD_OF_THE_RINGS_2
        with(cut.map(book)) {
            assertThat(isbn).isEqualTo(book.isbn.toString())
            assertThat(title).isEqualTo(book.title.toString())
        }
    }

    @Test fun `mapping sets borrowed to null`() {
        with(cut.map(Books.THE_LORD_OF_THE_RINGS_3)) {
            assertThat(borrowed).isNull()
        }
    }

}