package library.service.api.books

import io.mockk.every
import io.mockk.mockk
import library.service.business.books.domain.BookRecord
import library.service.business.books.domain.types.BookId
import library.service.business.books.domain.types.Borrower
import library.service.security.UserContext
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.Books
import utils.classification.UnitTest
import java.time.OffsetDateTime

@UnitTest
internal class BookResourceAssemblerTest {

    val currentUser: UserContext = mockk()
    val cut = BookResourceAssembler(currentUser)

    val id = BookId.generate()
    val book = Books.THE_MARTIAN
    val bookRecord = BookRecord(id, book)

    @BeforeEach fun setMockDefaults(){
        every { currentUser.isCurator() } returns false
    }

    @Test fun `book with 'available' state is assembled correctly`() {
        val resource = cut.toResource(bookRecord)

        assertThat(resource.isbn).isEqualTo(book.isbn.toString())
        assertThat(resource.title).isEqualTo(book.title.toString())
        assertThat(resource.authors).isEqualTo(book.authors.map { it.toString() })
        assertThat(resource.borrowed).isNull()

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNotNull()
        assertThat(resource.getLink("return")).isNull()
    }

    @Test fun `book with 'borrowed' state is assembled correctly`() {
        val borrowedBy = Borrower("Someone")
        val borrowedOn = OffsetDateTime.now()
        val borrowedBookRecord = bookRecord.borrow(borrowedBy, borrowedOn)

        val resource = cut.toResource(borrowedBookRecord)

        assertThat(resource.isbn).isEqualTo(book.isbn.toString())
        assertThat(resource.title).isEqualTo(book.title.toString())
        assertThat(resource.authors).isEqualTo(book.authors.map { it.toString() })
        assertThat(resource.borrowed).isNotNull()
        assertThat(resource.borrowed!!.by).isEqualTo("Someone")
        assertThat(resource.borrowed!!.on).isEqualTo(borrowedOn.toString())

        assertThat(resource.getLink("self")).isNotNull()
        assertThat(resource.getLink("borrow")).isNull()
        assertThat(resource.getLink("return")).isNotNull()
    }

    @Nested inner class `delete link` {

        @Test fun `is generate for curators`() {
            every { currentUser.isCurator() } returns true
            val resource = cut.toResource(bookRecord)
            assertThat(resource.getLink("delete")).isNotNull()
        }

        @Test fun `is not generated for users`() {
            every { currentUser.isCurator() } returns false
            val resource = cut.toResource(bookRecord)
            assertThat(resource.getLink("delete")).isNull()
        }

    }

}