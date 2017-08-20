package library.service.api.books

import library.service.business.books.domain.BookEntity
import library.service.business.books.domain.types.Book
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import utils.UnitTest
import java.time.OffsetDateTime
import java.util.*

@UnitTest
internal class BookResourceAssemblerTest {

    val cut = BookResourceAssembler()

    @Test fun `book with 'available' state is assembled correctly`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

        val resource = cut.toResource(bookEntity)

        assertThat(resource.isbn).isEqualTo("0123456789012")
        assertThat(resource.title).isEqualTo("Hello World")
        assertThat(resource.borrowed).isNull()

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNotNull()
        assertThat(resource.getLink("return")).isNull()
    }

    @Test fun `book with 'borrowed' state is assembled correctly`() {
        val id = UUID.randomUUID()
        val book = Book(Isbn13("0123456789012"), Title("Hello World"))
        val bookEntity = BookEntity(id, book)

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