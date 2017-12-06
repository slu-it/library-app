package library.service.business.books.domain.types

import contracts.ValueTypeContract
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import utils.assertThrows
import utils.classification.UnitTest
import java.util.*

@UnitTest
internal class BookIdTest : ValueTypeContract<BookId, String>() {

    override fun getValueExample() = "d19eafd1-c77c-45a3-bcc1-96cd288910b2"
    override fun getAnotherValueExample() = "40335985-97bc-42d2-9fdb-e48ae3c94ea6"
    override fun createNewInstance(value: String) = BookId.from(value)

    val uuid = UUID.fromString("4b13ce6b-4546-4b89-9356-dd7bbf67c40d")!!

    @Test fun `toString() returns BookId's value as a String`() {
        val bookId = BookId(uuid)
        assertThat(bookId.toString()).isEqualTo("4b13ce6b-4546-4b89-9356-dd7bbf67c40d")
    }

    @Test fun `toUuid() returns BookId's value as a UUID`() {
        val bookId = BookId(uuid)
        assertThat(bookId.toUuid()).isEqualTo(uuid)
    }

    @Test fun `when generating IDs each generated ID is unique`() {
        val bookId1 = BookId.generate()
        val bookId2 = BookId.generate()

        assertThat(bookId1).isEqualTo(bookId1)
        assertThat(bookId2).isEqualTo(bookId2)

        assertThat(bookId1).isNotEqualTo(bookId2)
        assertThat(bookId1.hashCode()).isNotEqualTo(bookId2.hashCode())
        assertThat(bookId1.toString()).isNotEqualTo(bookId2.toString())
    }

    @Nested inner class `when creating IDs from String` {

        @Test fun `UUID conform Strings are accepted`() {
            BookId.from("4b13ce6b-4546-4b89-9356-dd7bbf67c40d")
        }

        @Test fun `non UUID conform Strings will throw an exception`() {
            assertThrows(BookId.NotABookIdException::class) {
                BookId.from("not a uuid")
            }
        }

    }

}