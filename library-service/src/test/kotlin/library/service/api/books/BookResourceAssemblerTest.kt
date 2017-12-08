package library.service.api.books

import com.nhaarman.mockito_kotlin.given
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.willReturn
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.composites.Book
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.business.books.domain.types.Isbn13
import library.service.business.books.domain.types.Title
import library.service.security.UserContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.classification.UnitTest
import java.time.OffsetDateTime

@UnitTest
internal class BookResourceAssemblerTest {

    val currentUser: UserContext = mock()
    val cut = BookResourceAssembler(currentUser)

    val id = BookId.generate()
    val book = Book(Isbn13("0123456789012"), Title("Hello World"))
    val bookRecord = BookRecord(id, book)

    @Test fun `book with 'available' state is assembled correctly`() {
        val resource = cut.toResource(bookRecord)

        assertThat(resource.isbn).isEqualTo("0123456789012")
        assertThat(resource.title).isEqualTo("Hello World")
        assertThat(resource.borrowed).isNull()

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNotNull()
        assertThat(resource.getLink("return")).isNull()
    }

    @Test fun `book with 'borrowed' state is assembled correctly`() {
        val borrowedBy = Borrower("Someone")
        val borrowedOn = OffsetDateTime.now()
        bookRecord.borrow(borrowedBy, borrowedOn)

        val resource = cut.toResource(bookRecord)

        assertThat(resource.isbn).isEqualTo("0123456789012")
        assertThat(resource.title).isEqualTo("Hello World")
        assertThat(resource.borrowed).isNotNull()
        assertThat(resource.borrowed!!.by).isEqualTo("Someone")
        assertThat(resource.borrowed!!.on).isEqualTo(borrowedOn.toString())

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNull()
        assertThat(resource.getLink("return")).isNotNull()
    }

    @Nested inner class `delete link` {

        @Test fun `is generate for curators`() {
            given { currentUser.isCurator() } willReturn { true }
            val resource = cut.toResource(bookRecord)
            assertThat(resource.getLink("delete")).isNotNull()
        }

        @Test fun `is not generated for users`() {
            given { currentUser.isCurator() } willReturn { false }
            val resource = cut.toResource(bookRecord)
            assertThat(resource.getLink("delete")).isNull()
        }

    }

}