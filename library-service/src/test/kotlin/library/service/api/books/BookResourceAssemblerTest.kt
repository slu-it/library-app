package library.service.api.books

import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import test.classification.UnitTest
import java.time.OffsetDateTime

@UnitTest
internal class BookResourceAssemblerTest {

    val cut = BookResourceAssembler()

    @Test fun `book with 'available' state is assembled correctly`() {
        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookRecord(id, book)

        val resource = cut.toResource(bookEntity)

        assertThat(resource.isbn).isEqualTo("0123456789012")
        assertThat(resource.title).isEqualTo("Hello World")
        assertThat(resource.borrowed).isNull()

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNotNull()
        assertThat(resource.getLink("return")).isNull()
    }

    @Test fun `book with 'borrowed' state is assembled correctly`() {
        val id = BookId.generate()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookRecord(id, book)

        val borrowedBy = Borrower("Someone")
        val borrowedOn = OffsetDateTime.now()
        bookEntity.borrow(borrowedBy, borrowedOn)

        val resource = cut.toResource(bookEntity)

        assertThat(resource.isbn).isEqualTo("0123456789012")
        assertThat(resource.title).isEqualTo("Hello World")
        assertThat(resource.borrowed).isNotNull()
        assertThat(resource.borrowed!!.by).isEqualTo("Someone")
        assertThat(resource.borrowed!!.on).isEqualTo(borrowedOn.toString())

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNull()
        assertThat(resource.getLink("return")).isNotNull()
    }

}